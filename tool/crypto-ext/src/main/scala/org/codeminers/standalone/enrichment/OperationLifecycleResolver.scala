package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{Call, StoredNode}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.*

private[enrichment] final case class EnrichmentCandidate(
  call: Call,
  matched: AdapterMatch,
  sources: Map[SensitiveParameter, Vector[StoredNode]]
)

private[enrichment] final case class OperationGroup(
  candidates: Vector[EnrichmentCandidate],
  realizingCalls: Vector[Call],
  lifecycleComplete: Option[Boolean]
)

/** Conservatively combines calls that provably operate on the same object.
  *
  * Exact assignment/receiver names are required. Python additionally supports
  * one bounded interprocedural case: a helper that directly returns a cipher
  * factory and whose result is immediately used by a caller. Aliases with
  * multiple possible definitions remain separate and therefore PARTIAL.
  */
private[enrichment] final class OperationLifecycleResolver(cpg: Cpg, language: AdapterLanguage) {
  private val javaPaths = Option.when(language == AdapterLanguage.Java)(new JavaConstantPathEvaluator(cpg))

  def resolve(input: Vector[EnrichmentCandidate]): Vector[OperationGroup] = {
    val reachable  = input.filterNot(candidate => javaPaths.flatMap(_.execution(candidate.call)).contains(false))
    val candidates = refineProducerSources(reachable)
    language match {
      case AdapterLanguage.Java   => resolveJavaLifecycles(candidates)
      case AdapterLanguage.Python => resolvePythonCipher(candidates)
      case AdapterLanguage.Cpp    => resolveCppLifecycles(candidates)
    }
  }

  private final case class FamilyResult(groups: Vector[OperationGroup], consumed: Set[Long])

  private def resolveCppLifecycles(candidates: Vector[EnrichmentCandidate]): Vector[OperationGroup] = {
    val initializers = candidates.filter(candidate => candidate.matched.rule.id.startsWith("c-openssl-cipher-init-"))
    val factories    = candidates.filter(_.matched.rule.id == "c-openssl-evp-cipher")
    val consumed     = collection.mutable.Set.empty[Long]
    val groups = initializers.map { initializer =>
      val nestedFactories = initializer.call.ast.isCall.l
        .filterNot(_.id == initializer.call.id)
        .flatMap(call => factories.find(_.call.id == call.id))
        .distinctBy(_.call.id)
        .toVector
      val attached        = if (nestedFactories.size == 1) nestedFactories else Vector.empty
      val groupCandidates = (attached :+ initializer).sortBy(sourceOrder)
      groupCandidates.foreach(candidate => consumed += candidate.call.id)
      OperationGroup(groupCandidates, groupCandidates.map(_.call), Some(attached.nonEmpty))
    }
    groups ++ candidates.filterNot(candidate => consumed.contains(candidate.call.id)).map { candidate =>
      val status = Option.when(candidate.matched.rule.id == "c-openssl-evp-cipher")(false)
      OperationGroup(Vector(candidate), Vector(candidate.call), status)
    }
  }

  private def resolveJavaLifecycles(candidates: Vector[EnrichmentCandidate]): Vector[OperationGroup] = {
    val families = Vector(
      resolveJavaFamily(
        candidates,
        "java-cipher-",
        members => members.exists(_.matched.rule.lifecycleRole == LifecycleRole.Initialize)
      ),
      resolveJavaFamily(
        candidates,
        "java-message-digest",
        members => members.exists(_.matched.rule.lifecycleRole == LifecycleRole.Finalize)
      ),
      resolveJavaFamily(
        candidates,
        "java-mac-",
        members => members.exists(_.matched.rule.lifecycleRole == LifecycleRole.Initialize)
      ),
      resolveJavaFamily(
        candidates,
        "java-key-pair-",
        members => members.exists(_.matched.rule.lifecycleRole == LifecycleRole.Initialize)
      )
    )
    val consumed       = families.flatMap(_.consumed).toSet
    val familyPrefixes = Vector("java-cipher-", "java-message-digest", "java-mac-", "java-key-pair-")

    families.flatMap(_.groups) ++ candidates.filterNot(candidate => consumed.contains(candidate.call.id)).map {
      candidate =>
        val isUnattachedFactory = familyPrefixes.exists(candidate.matched.rule.id.startsWith) &&
          candidate.matched.rule.lifecycleRole == LifecycleRole.Factory
        OperationGroup(Vector(candidate), Vector(candidate.call), if (isUnattachedFactory) Some(false) else None)
    }
  }

  private def resolveJavaFamily(
    candidates: Vector[EnrichmentCandidate],
    rulePrefix: String,
    isComplete: Vector[EnrichmentCandidate] => Boolean
  ): FamilyResult = {
    val lifecycle      = candidates.filter(candidate => candidate.matched.rule.id.startsWith(rulePrefix))
    val factories      = lifecycle.filter(_.matched.rule.lifecycleRole == LifecycleRole.Factory)
    val runtime        = lifecycle.filterNot(_.matched.rule.lifecycleRole == LifecycleRole.Factory)
    val groupedRuntime = runtime.groupBy(candidate => receiverKey(candidate.call))
    val consumed       = collection.mutable.Set.empty[Long]

    val groups = groupedRuntime.toVector.collect { case (Some(key), members) =>
      val firstRuntime = members.map(_.call).minBy(callOrder)
      val matchingFactories = factories
        .filter(factory => assignmentKey(factory.call).contains(key))
        .filter(factory => summon[Ordering[(Int, Long)]].lt(callOrder(factory.call), callOrder(firstRuntime)))
        .sortBy(sourceOrder)
      val lastPossible = matchingFactories.lastOption
      val attachedFactory = lastPossible
        .filter(factory => matchingFactories.size == 1 || javaPaths.flatMap(_.execution(factory.call)).contains(true))
        .toVector
      if (attachedFactory.nonEmpty) matchingFactories.foreach(factory => consumed += factory.call.id)
      val groupCandidates = (attachedFactory ++ members).sortBy(sourceOrder)
      groupCandidates.foreach(candidate => consumed += candidate.call.id)
      OperationGroup(
        groupCandidates,
        groupCandidates.map(_.call).distinctBy(_.id),
        Some(attachedFactory.nonEmpty && isComplete(members))
      )
    }

    FamilyResult(groups, consumed.toSet)
  }

  private def resolvePythonCipher(candidates: Vector[EnrichmentCandidate]): Vector[OperationGroup] = {
    val factories = candidates.filter(_.matched.rule.id == "python-cryptography-cipher")
    val pyCryptoFactories = candidates.filter(c => Set("python-pycryptodome-cipher-new", "python-pycryptodome-arc4-constructor").contains(c.matched.rule.id))
    val executors = candidates.filter(candidate =>
      Set("python-cryptography-encryptor", "python-cryptography-decryptor").contains(candidate.matched.rule.id)
    )
    val consumed = collection.mutable.Set.empty[Long]

    val groups = executors.map { executor =>
      val matchingFactories = receiverKey(executor.call).toVector.flatMap { key =>
        factories.filter(factory => assignmentKey(factory.call).contains(key))
      }
      val attachedFactory   = if (matchingFactories.size == 1) matchingFactories else Vector.empty
      val candidatesInGroup = (attachedFactory :+ executor).sortBy(sourceOrder)
      val executorVariable  = assignmentVariable(executor.call)
      val relatedCalls = executorVariable.toVector.flatMap { variable =>
        cpg.call
          .filter(call => call.method.id == executor.call.method.id)
          .filter(call => Set("update", "finalize", "finalize_with_tag").contains(call.name))
          .filter(call => receiverVariable(call).contains(variable))
          .l
      }
      // A discarded encryptor inside an UnsupportedAlgorithm probe never handles
      // plaintext. Escaping/assigned encryptors remain ordinary partial lifecycles.
      val capabilityProbe = executorVariable.isEmpty && relatedCalls.isEmpty &&
        !executor.call.inAst.isReturn.nonEmpty &&
        executor.call.inAst.isControlStructure.filter(_.controlStructureType == "TRY").exists { attempt =>
          val lines = executor.call.file.content.headOption.getOrElse("").linesIterator.toVector
          // pysrc2cpg drops the exception type and gives CATCH the TRY's line.
          // Recover only a handler at this exact try statement's indentation.
          attempt.lineNumber.exists { line =>
            val header = lines.lift(line - 1).getOrElse("")
            val indent = header.takeWhile(_.isWhitespace).length
            header.trim == "try:" && lines.slice(line, line + 64)
              .takeWhile(s => s.trim.isEmpty || s.takeWhile(_.isWhitespace).length >= indent)
              .filter(s => s.takeWhile(_.isWhitespace).length == indent)
              .takeWhile(s => s.trim.isEmpty || s.trim.startsWith("except") || s.trim == "else:" || s.trim == "finally:")
              .exists(_.trim.matches("except\\s+(?:[A-Za-z_][A-Za-z0-9_]*\\.)?UnsupportedAlgorithm(?:\\s+as\\s+\\w+)?\\s*:.*"))
          }
        }
      candidatesInGroup.foreach(candidate => consumed += candidate.call.id)
      OperationGroup(
        candidatesInGroup,
        (candidatesInGroup.map(_.call) ++ relatedCalls).distinctBy(_.id).sortBy(callOrder),
        Some(attachedFactory.nonEmpty && !capabilityProbe)
      )
    }

    val pyCryptoGroups = pyCryptoFactories.flatMap { factory =>
      val executorNames = Set("encrypt", "encrypt_and_digest", "decrypt", "decrypt_and_verify")
      val factoryLine   = factory.call.lineNumber.map(_.toInt)
      val assignedExecutors = assignmentVariable(factory.call).toVector.flatMap { variable =>
        cpg.call
          .filter(_.method.id == factory.call.method.id)
          .filter(call => executorNames.contains(call.name.toLowerCase))
          .filter(call => receiverVariable(call).contains(variable))
          .filter(call => summon[Ordering[(Int, Long)]].gt(callOrder(call), callOrder(factory.call)))
          .l
      }
      // The Python frontend may insert field-access nodes between nested
      // AES.new(...).decrypt(...). A containing executor on the same source
      // line is nevertheless an exact structural use of this factory call.
      val inlineExecutors = cpg.call
        .filter(_.method.id == factory.call.method.id)
        .filter(call => executorNames.contains(call.name.toLowerCase))
        .filter(call => factoryLine.nonEmpty && call.lineNumber.map(_.toInt) == factoryLine)
        .filter(call => call.code.contains(factory.call.code))
        .l

      // A small but common Python wrapper returns a configured cipher from one
      // function and immediately invokes encrypt/decrypt in a caller. Link the
      // factory only when it is structurally below a return and the executor AST
      // contains a call to that exact helper in the same source file. This is an
      // interprocedural proof, not a filename or project allowlist.
      val returnedExecutors = {
        val factoryMethod = factory.call.method
        val returned = factoryMethod.ast.isReturn.ast.isCall.l.exists(_.id == factory.call.id)
        val sourceFile = factory.call.file.name.headOption
        if (!returned || factoryMethod.name == "<module>" || sourceFile.isEmpty) Vector.empty
        else
          cpg.call
            .filter(call => executorNames.contains(call.name.toLowerCase))
            .l
            .filter(call => call.method.id != factoryMethod.id)
            .filter(call => call.file.name.headOption == sourceFile)
            .filter(call => returnedHelperFeedsExecutor(call, factoryMethod.name))
            .toVector
      }
      val related = (assignedExecutors ++ inlineExecutors ++ returnedExecutors).distinctBy(_.id).sortBy(callOrder)
      consumed += factory.call.id
      if (related.isEmpty)
        Vector(OperationGroup(Vector(factory), Vector(factory.call), Some(false)))
      else
        related
          .groupBy(call => executorOperation(call.name))
          .toVector
          .sortBy(_._1)
          .map { case (_, directionCalls) =>
            OperationGroup(
              Vector(factory),
              (Vector(factory.call) ++ directionCalls).distinctBy(_.id).sortBy(callOrder),
              Some(true)
            )
          }
    }

    groups ++ pyCryptoGroups ++ candidates.filterNot(candidate => consumed.contains(candidate.call.id)).map { candidate =>
      val lifecycleStatus =
        if (candidate.matched.rule.id == "python-cryptography-cipher") Some(false) else None
      OperationGroup(Vector(candidate), Vector(candidate.call), lifecycleStatus)
    }
  }

  /** Prefer the source already established for an exactly assigned KeySpec, IVSpec, GCMParameterSpec, KDF, or random
    * producer. This prevents broad DFG fallback paths in a busy method from mixing unrelated sibling objects.
    */
  private def refineProducerSources(input: Vector[EnrichmentCandidate]): Vector[EnrichmentCandidate] = {
    val byCallId = input.map(candidate => candidate.call.id -> candidate).toMap

    input.map { consumer =>
      val mutationRefined = seedMutationSource(consumer, input).fold(consumer.sources) { source =>
        consumer.sources + (SensitiveParameter.Key -> Vector(source))
      }
      val refined = consumer.matched.rule.sensitiveParameters.foldLeft(mutationRefined) { (current, binding) =>
        val producer = producerFor(consumer.call, binding, byCallId)
        producer match {
          case Some(candidate) =>
            val compatible = compatibleSources(binding.parameter, candidate.sources)
            val existingDerivations = current.getOrElse(binding.parameter, Vector.empty).collect {
              case c: Call if c.name == "generateSecret" && c.methodFullName.startsWith("javax.crypto.SecretKeyFactory.") => c
            }
            // A derived key retains both its password origin and the actual derivation boundary.
            // Substituting only the password erases generateSecret and creates a false C12.
            val material = if (language == AdapterLanguage.Java &&
              Set(SensitiveParameter.Key, SensitiveParameter.Password).contains(binding.parameter) &&
              (existingDerivations.nonEmpty || (candidate.call.name == "generateSecret" &&
              candidate.call.methodFullName.startsWith("javax.crypto.SecretKeyFactory."))))
              compatible.updated(SensitiveParameter.Key,
                (compatible.getOrElse(SensitiveParameter.Key, Vector.empty) ++ existingDerivations ++
                  Option.when(candidate.call.name == "generateSecret")(candidate.call)).distinctBy(_.id))
            else compatible
            if (material.nonEmpty) current - binding.parameter ++ material else current
          case None => current
        }
      }
      consumer.copy(sources = refined)
    }
  }

  /** Java byte arrays are mutable. If a seed buffer is provably overwritten by SecureRandom.nextBytes on the active
    * constant path before it is consumed, the generator call—not the array initializer—is its effective source.
    */
  private def seedMutationSource(
    consumer: EnrichmentCandidate,
    input: Vector[EnrichmentCandidate]
  ): Option[StoredNode] = {
    if (language != AdapterLanguage.Java || consumer.matched.rule.operation != "SEED") None
    else {
      val seedSymbols = consumer.matched.rule.sensitiveParameters.flatMap { binding =>
        consumer.call.argument.l
          .filter(argument => binding.argumentIndex.contains(argument.argumentIndex))
          .map(_.code.trim)
          .filter(isSimpleName)
      }.toSet
      input
        .filter(candidate => candidate.matched.rule.id == "java-secure-random")
        .filter(candidate => candidate.call.name == "nextBytes")
        .filter(candidate => candidate.call.method.id == consumer.call.method.id)
        .filter(candidate => summon[Ordering[(Int, Long)]].lt(callOrder(candidate.call), callOrder(consumer.call)))
        .filter(candidate =>
          candidate.call.argument.l.exists(argument =>
            argument.argumentIndex > 0 && seedSymbols.contains(argument.code.trim)
          )
        )
        .filter(candidate => javaPaths.flatMap(_.execution(candidate.call)).contains(true))
        .sortBy(sourceOrder)
        .lastOption
        .map(_.call: StoredNode)
    }
  }

  private def producerFor(
    consumer: Call,
    binding: ParameterBinding,
    byCallId: Map[Long, EnrichmentCandidate]
  ): Option[EnrichmentCandidate] = {
    val argument = consumer.argument.l.find(argument =>
      binding.argumentIndex.contains(argument.argumentIndex) ||
        binding.keyword.exists(name => argument.argumentName.contains(name))
    )

    val direct = argument.toVector
      .flatMap(_.ast.isCall.l)
      .flatMap(call => byCallId.get(call.id))
      .filter(candidate => compatibleSources(binding.parameter, candidate.sources).nonEmpty)

    val assigned = argument.toVector.flatMap { node =>
      val variable = node.code.trim
      if (!variable.matches("[A-Za-z_$][A-Za-z0-9_$]*")) Vector.empty
      else {
        val constructorReceivers = inputCandidates(byCallId)
          .filter(_.call.method.id == consumer.method.id)
          .filter(candidate =>
            candidate.call.lineNumber.getOrElse(Int.MinValue) <= consumer.lineNumber.getOrElse(Int.MaxValue)
          )
          .filter(candidate => receiverVariable(candidate.call).contains(variable))
          .filter(candidate => compatibleSources(binding.parameter, candidate.sources).nonEmpty)

        val assignmentProducers = cpg.call
          .nameExact("<operator>.assignment")
          .filter(_.method.id == consumer.method.id)
          .filter(assignment =>
            assignment.lineNumber.getOrElse(Int.MinValue) <= consumer.lineNumber.getOrElse(Int.MaxValue)
          )
          .filter(assignment => assignment.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == variable))
          .l
          .sortBy(callOrder)
          .reverse
          .flatMap(_.ast.isCall.l.flatMap(call => byCallId.get(call.id)))
          .filter(candidate => compatibleSources(binding.parameter, candidate.sources).nonEmpty)

        (constructorReceivers ++ assignmentProducers).sortBy(sourceOrder).reverse
      }
    }

    (direct ++ assigned).distinctBy(_.call.id) match {
      case Vector(single)                    => Some(single)
      case candidates if candidates.nonEmpty => Some(candidates.head)
      case _                                 => None
    }
  }

  private def inputCandidates(byCallId: Map[Long, EnrichmentCandidate]): Vector[EnrichmentCandidate] =
    byCallId.valuesIterator.toVector

  private def compatibleSources(
    parameter: SensitiveParameter,
    sources: Map[SensitiveParameter, Vector[StoredNode]]
  ): Map[SensitiveParameter, Vector[StoredNode]] = parameter match {
    case SensitiveParameter.Key =>
      sources.filter((kind, _) => kind == SensitiveParameter.Key || kind == SensitiveParameter.Password)
    case SensitiveParameter.Iv =>
      sources.filter((kind, _) => kind == SensitiveParameter.Iv || kind == SensitiveParameter.Nonce)
    case SensitiveParameter.Salt => sources.filter(_._1 == SensitiveParameter.Salt)
    case SensitiveParameter.Nonce =>
      sources.filter((kind, _) => kind == SensitiveParameter.Nonce || kind == SensitiveParameter.Iv)
    case SensitiveParameter.Password =>
      sources.filter((kind, _) => kind == SensitiveParameter.Password || kind == SensitiveParameter.Key)
  }

  private def receiverKey(call: Call): Option[(Long, String)] =
    receiverVariable(call).map(variable => call.method.id -> variable)

  private def assignmentKey(call: Call): Option[(Long, String)] =
    assignmentVariable(call).map(variable => call.method.id -> variable)

  private def receiverVariable(call: Call): Option[String] =
    call.argument.l.find(_.argumentIndex == 0).map(_.code.trim).filter(isSimpleName).map { variable =>
      if (language != AdapterLanguage.Python) variable
      else {
        val aliases = cpg.call.nameExact("<operator>.assignment").l.filter(a =>
          a.method.id == call.method.id && a.lineNumber.getOrElse(Int.MaxValue) <= call.lineNumber.getOrElse(Int.MaxValue) &&
            a.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == variable))
          .flatMap(_.argument.l.find(_.argumentIndex == 2)).map(_.code.trim).distinct
        if (aliases.size == 1 && aliases.head.matches("self\\.[A-Za-z_][A-Za-z0-9_]*")) aliases.head else variable
      }
    }

  private def assignmentVariable(call: Call): Option[String] =
    call.inAst.isCall
      .filter(_.name == "<operator>.assignment")
      .l
      .headOption
      .flatMap(_.argument.l.find(_.argumentIndex == 1))
      .map(_.code.trim)
      .filter(isSimpleName)

  private def isSimpleName(value: String): Boolean = value.matches("[A-Za-z_$][A-Za-z0-9_$]*") ||
    (language == AdapterLanguage.Python && value.matches("self\\.[A-Za-z_][A-Za-z0-9_]*"))

  private def executorOperation(name: String): String = name.toLowerCase match {
    case "encrypt" | "encrypt_and_digest" => "ENCRYPT"
    case "decrypt" | "decrypt_and_verify" => "DECRYPT"
    case _                                  => "CONFIGURE"
  }

  /** pysrc2cpg lowers `make_cipher(...).encrypt(...)` to a temporary assignment
    * followed by `tmp.encrypt(...)`. Require that the executor's exact receiver
    * is assigned from the returned helper in the same method before linking it.
    */
  private def returnedHelperFeedsExecutor(executor: Call, helperName: String): Boolean =
    receiverVariable(executor).exists { receiver =>
      cpg.call
        .nameExact("<operator>.assignment")
        .filter(_.method.id == executor.method.id)
        .filter(assignment => summon[Ordering[(Int, Long)]].lt(callOrder(assignment), callOrder(executor)))
        .filter(assignment =>
          assignment.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == receiver)
        )
        .filter(assignment => assignment.ast.isCall.nameExact(helperName).nonEmpty)
        .nonEmpty
    }

  private def sourceOrder(candidate: EnrichmentCandidate): (Int, Long) = callOrder(candidate.call)
  private def callOrder(call: Call): (Int, Long) = (call.lineNumber.getOrElse(Int.MaxValue), call.id)
}
