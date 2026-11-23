package org.codeminers.standalone.enrichment

import flatgraph.DiffGraphBuilder
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.codepropertygraph.generated.{Cpg, EdgeTypes}
import io.shiftleft.codepropertygraph.generated.nodes.{Call, NewCryptoOperation, StoredNode}
import io.shiftleft.passes.CpgPass
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.*
import org.codeminers.standalone.ontology.{CryptoResolution, CryptoSourceType, CryptoOperationType}
import java.nio.file.Path

/** Builds primitive operations, then commits library-specific operation contexts before any rules run. */
final class CryptoEnrichmentPass(cpg: Cpg, language: AdapterLanguage, configs: Seq[AdapterConfig],
  originalJavaInput: Option[Path] = None)(implicit
  engineContext: EngineContext
) {
  private var recordedMetrics = CryptoEnrichmentMetrics.Empty
  def metrics: CryptoEnrichmentMetrics = recordedMetrics
  def createAndApply(): Unit = createAndApply(reportLegacyHmacSha1 = false)

  def createAndApply(reportLegacyHmacSha1: Boolean): Unit = {
    val start = System.nanoTime()
    val javaIndex = Option.when(language == AdapterLanguage.Java)(new JavaValueIndex(cpg, originalJavaInput))
    val primitives = new PrimitiveEnrichmentPass(cpg, language, configs, javaIndex)
    primitives.createAndApply()
    val contexts = new SecurityOperationPass(cpg, language, javaIndex, reportLegacyHmacSha1)
    val contextStart = System.nanoTime()
    contexts.createAndApply()
    val contextNanos = System.nanoTime() - contextStart
    recordedMetrics = primitives.metrics.copy(
      callsMatched = cpg.call.count(_.realizedCryptoOperation.nonEmpty),
      operationGroups = primitives.metrics.operationGroups + contexts.addedOperations,
      contextAnalysisNanos = contexts.analysisNanos,
      materializationNanos = primitives.metrics.materializationNanos + math.max(0L, contextNanos - contexts.analysisNanos),
      totalNanos = System.nanoTime() - start)
  }
}

/** Paper-aligned semantic enrichment entry point shared by Java, Python, and C/C++.
  *
  * The legacy passes remain available only for regression comparison. This pass is idempotent with respect to calls
  * that already have a REALIZES_CRYPTO edge and C++ local-type facts already materialized at the same location.
  */
private[enrichment] final class PrimitiveEnrichmentPass(cpg: Cpg, language: AdapterLanguage, configs: Seq[AdapterConfig],
  sharedIndex: Option[JavaValueIndex] = None)(implicit
  engineContext: EngineContext
) extends CpgPass(cpg) {

  require(configs.exists(_.language == language), s"No cryptographic adapter configured for ${language.storedValue}")

  private val matcher         = new CryptoCallMatcher(configs)
  private val javaIndex       = sharedIndex.orElse(Option.when(language == AdapterLanguage.Java)(new JavaValueIndex(cpg)))
  private val provenance      = new ParameterProvenance(cpg, sharedIndex = javaIndex)
  private val attributeValues = new AttributeValueResolver(cpg, javaIndex)
  private val rsaKeySizes = new JavaRsaKeySizeResolver
  private val customJava = Option.when(language == AdapterLanguage.Java)(new JavaCustomCryptoModel(cpg, javaIndex))
  private val pythonFunctionValues =
    Option.when(language == AdapterLanguage.Python)(new PythonFunctionValueResolver(cpg))
  private val pythonApis = Option.when(language == AdapterLanguage.Python)(new PythonCryptoApiModel(cpg))

  private lazy val pythonKeyTransforms = new PythonKeyTransformationResolver(cpg)

  @volatile private var recordedMetrics = CryptoEnrichmentMetrics.Empty

  def metrics: CryptoEnrichmentMetrics = recordedMetrics

  override def run(builder: DiffGraphBuilder): Unit = {
    val totalStarted        = System.nanoTime()
    var adapterNanos        = 0L
    var provenanceNanos     = 0L
    var sensitiveBindings   = 0
    var resolvedSourceNodes = 0
    val calls               = javaIndex.map(_.calls).getOrElse(cpg.call.l.toVector)
    val matchedCandidates = calls.flatMap { call =>
      if (call.realizedCryptoOperation.nonEmpty) None
      else {
        val adapterStarted = System.nanoTime()
        val matchedCalls   = matchCall(call)
        adapterNanos += System.nanoTime() - adapterStarted
        matchedCalls.map(matched => call -> matched)
      }
    }.toVector

    val provenanceStarted = System.nanoTime()
    val requests = matchedCandidates.flatMap { (call, matched) =>
      if (matched.rule.id == "python-proven-hash-update") Vector.empty
      else matched.rule.sensitiveParameters.map(binding => ProvenanceRequest(call, binding, matched.rule.operation == "SEED"))
    }
    val resolvedRequests = provenance.resolveAll(requests).iterator
    val candidates = matchedCandidates.map { (call, matched) =>
      val sources = matched.rule.sensitiveParameters.map { binding =>
        sensitiveBindings += 1
        val resolved = if (matched.rule.id == "python-proven-hash-update") pythonKeyTransforms.hashUpdatePasswordSources(call)
          else resolvedRequests.next()
        resolvedSourceNodes += resolved.size
        binding.parameter -> resolved
      }.toMap
      EnrichmentCandidate(call, matched, sources)
    }
    provenanceNanos = System.nanoTime() - provenanceStarted

    val lifecycleStarted = System.nanoTime()
    val groups           = new OperationLifecycleResolver(cpg, language).resolve(candidates)
    val lifecycleNanos   = System.nanoTime() - lifecycleStarted
    val usedSourceIds = groups.iterator
      .filterNot(_.candidates.exists(_.matched.rule.operationType.storedValue == "RANDOMNESS"))
      .flatMap(_.candidates.iterator.flatMap(_.sources.valuesIterator.flatten.map(_.id)))
      .toSet
    val cryptoInputSymbols = groups.iterator
      .filterNot(_.candidates.exists(_.matched.rule.operationType.storedValue == "RANDOMNESS"))
      .flatMap(_.candidates)
      .flatMap { candidate =>
        candidate.matched.rule.sensitiveParameters.iterator.flatMap { binding =>
          provenance
            .argument(candidate.call, binding)
            .iterator
            .map(_.code.trim)
            .filter(_.matches("[A-Za-z_$][A-Za-z0-9_$]*"))
            .map(candidate.call.method.id -> _)
        }
      }
      .toSet

    val materializationStarted = System.nanoTime()
    groups.foreach { group =>
      enrich(group, usedSourceIds, cryptoInputSymbols, builder)
    }
    val cppMetrics = if (language == AdapterLanguage.Cpp) enrichCppLocals(builder) else CppLocalMetrics.Empty
    adapterNanos += cppMetrics.adapterNanos
    val materializationNanos = System.nanoTime() - materializationStarted - cppMetrics.adapterNanos
    recordedMetrics = CryptoEnrichmentMetrics(
      callsScanned = calls.size,
      callsMatched = candidates.map(_.call.id).distinct.size,
      localOperationsMatched = cppMetrics.matches,
      operationGroups = groups.size + cppMetrics.matches,
      sensitiveBindings = sensitiveBindings,
      resolvedSourceNodes = resolvedSourceNodes,
      adapterNanos = adapterNanos,
      provenanceNanos = provenanceNanos,
      provenanceFlowQueries = provenance.flowSearchCount,
      provenanceMultiSinkQueries = provenance.multiSinkSearchCount,
      lifecycleNanos = lifecycleNanos,
      materializationNanos = math.max(0L, materializationNanos),
      totalNanos = System.nanoTime() - totalStarted
    )
  }

  private def enrichCppLocals(builder: DiffGraphBuilder): CppLocalMetrics = {
    var matches      = 0
    var adapterNanos = 0L
    cpg.local.foreach { local =>
      val adapterStarted = System.nanoTime()
      val localMatches = matcher.matchAndExtract(LocalDescriptor(language, local.name, local.typeFullName, local.code))
      adapterNanos += System.nanoTime() - adapterStarted
      localMatches
        .foreach { matched =>
          matches += 1
          val attributes = matched.attributes.filter(_._2 != AttributeExtractor.Unknown)
          val security = SecurityPropertyAnnotator.evaluate(
            matched.rule.operationType.storedValue,
            matched.rule.operation,
            attributes.getOrElse("algorithm", ""),
            attributes.getOrElse("mode", ""),
            attributes.getOrElse("padding", "")
          )
          val srcFile = local.file.name.headOption.getOrElse("UNKNOWN")
          val lineNo  = local.lineNumber.map(_.toInt).getOrElse(-1)
          val duplicate = cpg.cryptoOperation.exists(operation =>
            operation.srcFile == srcFile && operation.lineNo == lineNo &&
              operation.opType == matched.rule.operationType.storedValue && operation.operation == matched.rule.operation
          )
          if (!duplicate) {
            val operation = NewCryptoOperation()
              .opType(matched.rule.operationType.storedValue)
              .operation(matched.rule.operation)
              .algorithm(security.algorithm)
              .mode(security.mode)
              .padding(security.padding)
              .keyLength(integer(attributes, "keyLength"))
              .keySize(integer(attributes, "keySize"))
              .outputLength(integer(attributes, "outputLength"))
              .iterations(integer(attributes, "iterations"))
              .source(attributes.getOrElse("source", CryptoSourceType.Unknown.storedValue))
              .keySourceType(CryptoSourceType.Unknown.storedValue)
              .ivSourceType(CryptoSourceType.Unknown.storedValue)
              .saltSourceType(CryptoSourceType.Unknown.storedValue)
              .nonceSourceType(CryptoSourceType.Unknown.storedValue)
              .resolution(CryptoResolution.Complete.storedValue)
              .isDeprecated(security.isDeprecated)
              .providesIntegrity(security.providesIntegrity)
              .requiresUniqueNonce(security.requiresUniqueNonce)
              .requiresCsprngSource(security.requiresCsprngSource)
              .lineNo(lineNo)
              .srcFile(srcFile)
            if (matched.rule.operationType.storedValue == "RANDOMNESS") operation.usedForCrypto(false)
            builder.addNode(operation)
          }
        }
    }
    CppLocalMetrics(matches, adapterNanos)
  }

  private def matchCall(call: Call): Vector[AdapterMatch] = {
    lazy val descriptor = describe(call)
    val raw = if (matcher.mayMatch(language, call.name, call.methodFullName)) matcher.matchAndExtract(descriptor)
      else Vector.empty
    val direct = raw.filter(m => pythonApis.forall(_.accepts(call, m))).map(attributeValues.resolve(call, _))
      .map(m => pythonApis.map(_.refine(call, m)).getOrElse(m)).map { matched =>
      if (language == AdapterLanguage.Java && matched.rule.id == "java-cipher-init") {
        // Cipher.init(mode, key[, random]) has no explicit IV parameter. Random is not an IV.
        val third = call.argument.argumentIndex(3).headOption
        val isRandom = third.exists {
          case c: Call => c.typeFullName == "java.security.SecureRandom"
          case i: io.shiftleft.codepropertygraph.generated.nodes.Identifier => i.typeFullName == "java.security.SecureRandom"
          case _ => false
        } ||
          call.methodFullName.matches(".*\\(int,java\\.security\\.Key,java\\.security\\.SecureRandom\\)$")
        if (third.isEmpty || isRandom) matched.copy(rule = matched.rule.copy(
          sensitiveParameters = matched.rule.sensitiveParameters.filterNot(_.parameter == SensitiveParameter.Iv)))
        else matched
      } else matched
    }
    if (direct.nonEmpty) direct
    else {
      val custom = customJava.flatMap(_.matchCall(call)).toVector
      if (custom.nonEmpty) return custom
      val python = pythonApis.toVector.flatMap(_.matchCall(call))
      if (python.nonEmpty) return python
      pythonFunctionValues
        .flatMap(_.syntheticCall(call))
        .toVector
        .flatMap { synthetic =>
          matcher
            .matchAndExtract(descriptor.copy(name = synthetic.name, code = synthetic.code))
            .map(attributeValues.resolve(call, _))
        }
    }
  }

  private def enrich(
    group: OperationGroup,
    usedSourceIds: Set[Long],
    cryptoInputSymbols: Set[(Long, String)],
    builder: DiffGraphBuilder
  ): Unit = {
    val sourcesByParameter = mergeSources(group.candidates)
    val operation          = buildOperation(group, sourcesByParameter, usedSourceIds, cryptoInputSymbols)
    builder.addNode(operation)
    group.realizingCalls.foreach(call => builder.addEdge(call, operation, EdgeTypes.REALIZES_CRYPTO))

    sourcesByParameter.foreach { case (parameter, sources) =>
      sources.foreach { source =>
        builder.addEdge(operation, source, edgeFor(parameter))
      }
    }
  }

  private def buildOperation(
    group: OperationGroup,
    sources: Map[SensitiveParameter, Vector[StoredNode]],
    usedSourceIds: Set[Long],
    cryptoInputSymbols: Set[(Long, String)]
  ): NewCryptoOperation = {
    val canonical = group.candidates
      .find(_.matched.rule.lifecycleRole == LifecycleRole.Factory)
      .getOrElse(group.candidates.head)
    val call    = canonical.call
    val matched = canonical.matched
    val attributes = group.candidates.foldLeft(Map.empty[String, String]) { (combined, candidate) =>
      combined ++ candidate.matched.attributes.filter((key, value) => value.nonEmpty && !combined.contains(key))
    }
    val keySources = sources.getOrElse(SensitiveParameter.Key, Vector.empty) ++
      sources.getOrElse(SensitiveParameter.Password, Vector.empty)
    val bindings = group.candidates.flatMap(_.matched.rule.sensitiveParameters)
    val ordinaryBindingsResolved = bindings
      .filterNot(binding => Set(SensitiveParameter.Iv, SensitiveParameter.Nonce).contains(binding.parameter))
      .forall(binding => bindingSources(binding.parameter, sources).nonEmpty)
    val ivOrNonceBindings = bindings.filter(binding =>
      Set(SensitiveParameter.Iv, SensitiveParameter.Nonce).contains(binding.parameter)
    )
    // Some APIs use the same physical parameter as an IV in block modes and
    // a nonce in AEAD modes. Treat those declarations as alternatives.
    val ivOrNonceResolved = ivOrNonceBindings.isEmpty ||
      sources.getOrElse(SensitiveParameter.Iv, Vector.empty).nonEmpty ||
      sources.getOrElse(SensitiveParameter.Nonce, Vector.empty).nonEmpty
    val allBindingsResolved = ordinaryBindingsResolved && ivOrNonceResolved
    val javaCipher = language == AdapterLanguage.Java && matched.rule.id.startsWith("java-cipher-")
    val unknownSymmetricMode = javaCipher && !attributes.get("algorithm").exists(_.equalsIgnoreCase("RSA")) &&
      attributes.get("mode").forall(value => value.isEmpty || value == AttributeExtractor.Unknown)
    val nullCipherKey = javaCipher && group.candidates.exists(candidate =>
      candidate.matched.rule.lifecycleRole == LifecycleRole.Initialize &&
      candidate.call.argument.argumentIndex(2).ast.isLiteral.code.l.contains("null"))
    val resolutionComplete = group.lifecycleComplete.forall(identity) && allBindingsResolved &&
      !unknownSymmetricMode && !nullCipherKey
    val operationName      = lifecycleOperation(group).getOrElse(matched.rule.operation)
    val operationType = if (language == AdapterLanguage.Java &&
      matched.rule.operationType == CryptoOperationType.SymmetricEncryption &&
      attributes.get("algorithm").exists(_.equalsIgnoreCase("RSA")))
      CryptoOperationType.AsymmetricEncryption.storedValue else matched.rule.operationType.storedValue
    val security = SecurityPropertyAnnotator.evaluate(
      operationType,
      operationName,
      attributes.getOrElse("algorithm", ""),
      attributes.getOrElse("mode", ""),
      attributes.getOrElse("padding", "")
    )

    val node = NewCryptoOperation()
      .opType(operationType)
      .operation(operationName)
      .algorithm(security.algorithm)
      .mode(security.mode)
      .padding(security.padding)
      .keyLength(integer(attributes, "keyLength"))
      .keySize(if (language == AdapterLanguage.Java && security.algorithm == "RSA" && integer(attributes, "keySize") < 0)
        rsaKeySizes.resolve(keySources).getOrElse(-1) else integer(attributes, "keySize"))
      .outputLength(integer(attributes, "outputLength"))
      .iterations(integer(attributes, "iterations"))
      .source(attributes.getOrElse("source", "UNKNOWN"))
      .keySourceType(SourceClassifier.combine(classifiableSources(keySources)).storedValue)
      .ivSourceType(sourceType(sources, SensitiveParameter.Iv).storedValue)
      .saltSourceType(sourceType(sources, SensitiveParameter.Salt).storedValue)
      .nonceSourceType(sourceType(sources, SensitiveParameter.Nonce).storedValue)
      .resolution(
        if (resolutionComplete) CryptoResolution.Complete.storedValue else CryptoResolution.Partial.storedValue
      )
      .isDeprecated(security.isDeprecated)
      .providesIntegrity(security.providesIntegrity)
      .requiresUniqueNonce(security.requiresUniqueNonce)
      .requiresCsprngSource(security.requiresCsprngSource)
      .lineNo(call.lineNumber.map(_.toInt).getOrElse(-1))
      .srcFile(call.file.name.headOption.getOrElse("UNKNOWN"))

    if (matched.rule.operationType.storedValue == "KEY_DERIVATION")
      node.saltUsed(sources.getOrElse(SensitiveParameter.Salt, Vector.empty).nonEmpty)
    else attributes.get("saltUsed").flatMap(boolean).foreach(node.saltUsed)
    attributes.get("verified").flatMap(boolean).foreach(node.verified)
    if (matched.rule.operationType.storedValue == "RANDOMNESS") {
      node.usedForCrypto(
        group.realizingCalls.exists(call =>
          usedSourceIds.contains(call.id) || call.ast.exists(source => usedSourceIds.contains(source.id)) ||
            call.argument.l.exists(argument => cryptoInputSymbols.contains(call.method.id -> argument.code.trim))
        )
      )
    } else attributes.get("usedForCrypto").flatMap(boolean).foreach(node.usedForCrypto)
    node
  }

  private def mergeSources(candidates: Vector[EnrichmentCandidate]): Map[SensitiveParameter, Vector[StoredNode]] =
    candidates.iterator
      .flatMap(_.sources)
      .toVector
      .groupMap(_._1)(_._2)
      .view
      .mapValues(_.flatten.distinctBy(_.id))
      .toMap

  private def bindingSources(
    parameter: SensitiveParameter,
    sources: Map[SensitiveParameter, Vector[StoredNode]]
  ): Vector[StoredNode] = parameter match {
    case SensitiveParameter.Key =>
      sources.getOrElse(SensitiveParameter.Key, Vector.empty) ++ sources.getOrElse(
        SensitiveParameter.Password,
        Vector.empty
      )
    case SensitiveParameter.Iv =>
      sources.getOrElse(SensitiveParameter.Iv, Vector.empty) ++ sources.getOrElse(
        SensitiveParameter.Nonce,
        Vector.empty
      )
    case SensitiveParameter.Password =>
      sources.getOrElse(SensitiveParameter.Password, Vector.empty) ++ sources.getOrElse(
        SensitiveParameter.Key,
        Vector.empty
      )
    case SensitiveParameter.Nonce =>
      sources.getOrElse(SensitiveParameter.Nonce, Vector.empty) ++ sources.getOrElse(
        SensitiveParameter.Iv,
        Vector.empty
      )
    case other => sources.getOrElse(other, Vector.empty)
  }

  private def lifecycleOperation(group: OperationGroup): Option[String] =
    group.candidates
      .find(candidate => Set("ENCRYPT", "DECRYPT").contains(candidate.matched.rule.operation))
      .map(_.matched.rule.operation)
      .orElse {
        group.realizingCalls.iterator
          .map(_.name.toLowerCase)
          .collectFirst {
            case "encrypt" | "encrypt_and_digest" => "ENCRYPT"
            case "decrypt" | "decrypt_and_verify" => "DECRYPT"
          }
      }
      .orElse {
        Option
          .when(language == AdapterLanguage.Cpp)(group.candidates)
          .toVector
          .flatten
          .find(_.matched.rule.id == "c-openssl-cipher-init-generic")
          .flatMap(_.call.argument.l.find(_.argumentIndex == 6))
          .flatMap(argument => argument.code.trim.toIntOption)
          .flatMap {
            case 1 => Some("ENCRYPT")
            case 0 => Some("DECRYPT")
            case _ => None
          }
      }
      .orElse {
        group.candidates
          .find(_.matched.rule.lifecycleRole == LifecycleRole.Initialize)
          .flatMap(_.call.argument.l.find(_.argumentIndex == 1))
          .flatMap { mode =>
            val code = mode.code.toUpperCase
            if (code.contains("ENCRYPT_MODE") || code.contains("WRAP_MODE")) Some("ENCRYPT")
            else if (code.contains("DECRYPT_MODE") || code.contains("UNWRAP_MODE")) Some("DECRYPT")
            else None
          }
      }

  private def describe(call: Call): CallDescriptor = {
    val inputs = javaIndex.map(_.argumentsOf(call)).getOrElse(call.argument.l)
    val arguments = inputs.map(argument => argument.argumentIndex -> argument.code).toMap
    val keywords  = inputs.flatMap(argument => argument.argumentName.map(_ -> argument.code)).toMap
    CallDescriptor(language, call.name, call.methodFullName, call.code, arguments, keywords)
  }

  private def edgeFor(parameter: SensitiveParameter): String = parameter match {
    case SensitiveParameter.Key | SensitiveParameter.Password => EdgeTypes.USES_KEY_FROM
    case SensitiveParameter.Iv                                => EdgeTypes.USES_IV_FROM
    case SensitiveParameter.Salt                              => EdgeTypes.USES_SALT_FROM
    case SensitiveParameter.Nonce                             => EdgeTypes.USES_NONCE_FROM
  }

  private def sourceType(
    sources: Map[SensitiveParameter, Vector[StoredNode]],
    parameter: SensitiveParameter
  ): CryptoSourceType = SourceClassifier.combine(classifiableSources(sources.getOrElse(parameter, Vector.empty)))

  /** C/C++ represents null pointers as literal `0`, `NULL`, or `nullptr`. They make an argument syntactically resolved
    * but are not hardcoded key/IV material.
    */
  private def classifiableSources(sources: Iterable[StoredNode]): Iterable[StoredNode] =
    if (language != AdapterLanguage.Cpp) sources
    else
      sources.filter {
        case ast: io.shiftleft.codepropertygraph.generated.nodes.AstNode =>
          !Set("0", "NULL", "nullptr").contains(ast.code.trim)
        case _ => true
      }

  private def integer(attributes: Map[String, String], key: String): Int =
    attributes.get(key).flatMap(_.replace("_", "").toIntOption).getOrElse(-1)

  private def boolean(value: String): Option[Boolean] = value.trim.toLowerCase match {
    case "true"  => Some(true)
    case "false" => Some(false)
    case _       => None
  }
}

private final case class CppLocalMetrics(matches: Int, adapterNanos: Long)

private object CppLocalMetrics {
  val Empty: CppLocalMetrics = CppLocalMetrics(0, 0L)
}

object CryptoEnrichmentPass {
  def defaultConfigs: Either[Vector[String], Vector[AdapterConfig]] = for {
    javaConfig   <- AdapterLoader.loadResource("adapters/java.yaml")
    pythonConfig <- AdapterLoader.loadResource("adapters/python.yaml")
  } yield Vector(javaConfig, pythonConfig)
}
