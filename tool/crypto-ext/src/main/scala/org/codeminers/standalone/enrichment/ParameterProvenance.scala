package org.codeminers.standalone.enrichment

import io.joern.dataflowengineoss.language.*
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{
  AstNode,
  Call,
  CfgNode,
  Expression,
  Identifier,
  Literal,
  MethodParameterIn,
  StoredNode,
  TypeRef
}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.JavaFrontend
import org.codeminers.standalone.adapter.{ParameterBinding, SensitiveParameter}

import scala.collection.mutable
import scala.util.Try
import scala.util.control.ControlThrowable

private[standalone] final case class ProvenanceRequest(call: Call, binding: ParameterBinding, seed: Boolean = false)

/** Resolves terminal source nodes for a security-sensitive call argument.
  *
  * The first implementation deliberately stays within Joern's existing DFG and
  * falls back only to the direct expression. Interprocedural lifecycle linking
  * is handled separately, so an unresolved value remains explicit instead of
  * being guessed from similarly named variables elsewhere in the file.
  */
final case class ProvenanceLimits(
  maxPathLength: Int = 64,
  maxSourcesPerArgument: Int = 8,
  maxParameterHops: Int = 2,
  maxCallSitesPerParameter: Int = 32
) {
  require(maxPathLength > 0, "maxPathLength must be positive")
  require(maxSourcesPerArgument > 0, "maxSourcesPerArgument must be positive")
  require(maxParameterHops >= 0, "maxParameterHops must not be negative")
  require(maxCallSitesPerParameter > 0, "maxCallSitesPerParameter must be positive")
}

final class ParameterProvenance(
  cpg: Cpg,
  limits: ProvenanceLimits = ProvenanceLimits(),
  sharedIndex: Option[JavaValueIndex] = None
)(implicit engineContext: EngineContext) {

  // Operators whose result is a truth value: nothing that passes through them can be key material.
  private val PredicateOperators = Set(
    "<operator>.lessThan",
    "<operator>.greaterThan",
    "<operator>.lessEqualsThan",
    "<operator>.greaterEqualsThan",
    "<operator>.equals",
    "<operator>.notEquals",
    "<operator>.logicalAnd",
    "<operator>.logicalOr",
    "<operator>.logicalNot"
  )
  // Arithmetic whose constant or int-typed operands compute a size, index, bound or mask.  The same operators also
  // transform byte content (`key[i] ^ pad[i]`), so a byte/char operand is still content.  Addition is excluded
  // because it also concatenates strings.
  private val ArithmeticOperators = Set(
    "<operator>.subtraction",
    "<operator>.multiplication",
    "<operator>.division",
    "<operator>.modulo",
    "<operator>.and",
    "<operator>.or",
    "<operator>.xor",
    "<operator>.shiftLeft",
    "<operator>.arithmeticShiftRight",
    "<operator>.logicalShiftRight",
    "<operator>.assignmentMinus",
    "<operator>.assignmentMultiplication",
    "<operator>.assignmentDivision",
    "<operator>.assignmentModulo"
  )
  // Static types of a length, offset, count, mode or index.  Byte and char are deliberately absent: they carry content.
  private val ScalarTypes = Set("int", "long", "short", "java.lang.Integer", "java.lang.Long", "java.lang.Short")
  // Factories whose arguments name an algorithm, transformation or provider.
  private val AlgorithmFactories = Set("getInstance")
  // Lookups whose first argument names the entry to read (`System.getProperty("user.home")`, `params.get("key")`):
  // the result is runtime configuration, not the name.  A default in the second argument is still content.
  private val LookupCalls = Set(
    "get",
    "getOrDefault",
    "getProperty",
    "getenv",
    "getString",
    "getParameter",
    "getInitParameter",
    "getAttribute",
    "getHeader",
    "getValue",
    "lookup"
  )
  // Constructors whose first argument is the value the constructed object holds.
  private val ValueHolderConstructors = Vector("javax.crypto.spec.", "java.lang.String.<init>")
  // Calls that turn their argument or receiver into the same value in another representation.
  private val ValuePreservingConversions = Set(
    "getBytes",
    "toCharArray",
    "toString",
    "toHexString",
    "toBinaryString",
    "toOctalString",
    "valueOf",
    "trim",
    "intern",
    "toUpperCase",
    "toLowerCase"
  )

  // DFG does not cross source files in the supported Java/Python frontends.
  // Indexing candidates once by file avoids rebuilding a whole-program source
  // vector for every sensitive argument and prevents unrelated-file evidence.
  private lazy val concreteLiteralsByFile: Map[String, Vector[CfgNode]] = javaValueIndex.literalsByFile
    .view.mapValues(_.filter(SourceClassifier.isConcreteLiteral)).toMap
  private lazy val producersByFile: Map[String, Vector[CfgNode]] = javaValueIndex.calls
    .filter(SourceClassifier.isKnownProducer)
    .groupBy(fileName)
    .view
    .mapValues(_.map(node => node: CfgNode).toVector)
    .toMap
  private lazy val parametersByFile: Map[String, Vector[CfgNode]] = cpg.method.parameter
    .filter(_.index > 0)
    .l
    .groupBy(fileName)
    .view
    .mapValues(_.map(node => node: CfgNode).toVector)
    .toMap
  // Call-site indexes are whole-program on purpose: the caller that supplies a literal frequently lives in a
  // different source file from the cryptographic call, which is exactly the flow the DFG cannot follow.
  private lazy val callsByFullName: Map[String, Vector[Call]] =
    javaValueIndex.callsByFullName
  private lazy val pythonMethodsByName = cpg.method.l.groupBy(_.name)
  private lazy val pythonImportsByFile = cpg.call.nameExact("import").l.groupBy(fileName).view.mapValues(_.map(_.code).mkString("\n")).toMap
  private lazy val callsByName: Map[String, Vector[Call]] =
    javaValueIndex.callsByName
  private lazy val fieldAssignmentsByName: Map[String, Vector[Call]] = cpg.call
    .nameExact("<operator>.assignment")
    .l
    .flatMap(assignment => assignedField(assignment).map(_._2 -> assignment))
    .groupMap(_._1)(_._2)
    .view
    .mapValues(_.toVector)
    .toMap

  private lazy val javaValueIndex = sharedIndex.getOrElse(new JavaValueIndex(cpg))
  private lazy val javaSaltValues = new JavaValueSummaries(cpg, traceSaltTransforms = true, sharedIndex = Some(javaValueIndex))

  /** Resolve a frontier of independent arguments together. A miss suspends only that argument; completed AST/value
    * resolutions remain cached. Each frontier batches identical source sets, then replays unfinished arguments.
    * This retains the existing lazy source precedence and bounded caller expansion without speculative searches.
    */
  private[standalone] def resolveAll(requests: Vector[ProvenanceRequest], batchQueries: Boolean = true): Vector[Vector[StoredNode]] = {
    def resolveRequest(request: ProvenanceRequest): Vector[StoredNode] =
      if (request.seed) resolveSeed(request.call, request.binding) else resolve(request.call, request.binding)
    if (!batchQueries || requests.sizeIs <= 1 || java.lang.Boolean.getBoolean("crypto.provenance.disableBatch"))
      return requests.map(resolveRequest)
    require(!collectingFlows, "provenance frontiers must not be nested")
    val results = Array.fill[Option[Vector[StoredNode]]](requests.size)(None)
    var unfinished = requests.indices.toVector
    collectingFlows = true
    try {
      while (unfinished.nonEmpty) {
        unfinished = unfinished.filter { index =>
          try {
            results(index) = Some(resolveRequest(requests(index)))
            false
          } catch { case AwaitFlow => true }
        }
        if (unfinished.nonEmpty) {
          require(pendingFlows.nonEmpty, "a suspended provenance argument must have a pending flow query")
          executePendingFlows()
        }
      }
      if (java.lang.Boolean.getBoolean("crypto.profile"))
        System.err.println(s"crypto.profile provenance.searches total=$flowSearchCount multiSink=$multiSinkSearchCount")
      results.iterator.map(_.get).toVector
    } finally {
      collectingFlows = false
      pendingFlows.clear()
    }
  }

  def resolve(call: Call, binding: ParameterBinding): Vector[StoredNode] =
    argument(call, binding).map { expression => scoped(call, expression, {
      val java = fileName(call).endsWith(".java")
      Option.when(java)(parsedByteSources(expression).orElse(runtimeFieldConstants(expression))).flatten
        .orElse {
          Option.when(binding.parameter == SensitiveParameter.Salt && java)(
            javaSaltValues.resolve(expression, material = true).map(_.sources.distinctBy(_.id).take(limits.maxSourcesPerArgument))
          ).flatten
        }
        .getOrElse(resolveExpression(call, expression, preferLiterals = false, depth = 0))
    })}.getOrElse(Vector.empty)

  // An unknown parse is retained as an opaque origin so generic data flow cannot attach nearby literals to it.
  private def parsedByteSources(expression: Expression): Option[Vector[StoredNode]] =
    javaRuntimeValues.resolve(expression, material = true).filter(_.trace.exists(JavaValueSummaries.isDecimalByteParse))
      .map(_.sources.distinctBy(_.id).take(limits.maxSourcesPerArgument))

  /** A field with no initializer default that a setup method writes before the reading method runs (both called in
    * order from the same method, e.g. go2(); go3(); go()) takes its value from that write. When the exact chain of such
    * writes ends only in literals, those literals are the value; any other outcome keeps the regular resolution. */
  private val runtimeFieldCache = mutable.LongMap.empty[Option[Vector[StoredNode]]]
  private def runtimeFieldConstants(expression: Expression): Option[Vector[StoredNode]] =
    runtimeFieldCache.getOrElseUpdate(expression.id, javaRuntimeValues.resolve(expression, material = true).filter { value =>
      value.trace.exists(write => write.name == "<operator>.assignment" &&
        !JavaFrontend.isInitializer(write.method.name) && assignedField(write).nonEmpty) &&
      value.sources.nonEmpty && value.sources.forall {
        case literal: Literal => SourceClassifier.isConcreteLiteral(literal)
        case _                => false
      }
    }.map(_.sources.distinctBy(_.id).take(limits.maxSourcesPerArgument)))

  /** Seed APIs are the inverse of generator APIs: the argument configures the generator, so a receiver-side
    * SecureRandom producer must not hide a literal seed that reaches the argument. */
  def resolveSeed(call: Call, binding: ParameterBinding): Vector[StoredNode] =
    argument(call, binding).map(expression =>
      scoped(call, expression, resolveExpression(call, expression, preferLiterals = true, depth = 0))).getOrElse(Vector.empty)

  /** Applies the ablation mode: a local resolution is kept only when every source lies in the calling method. */
  private def scoped(call: Call, expression: Expression, resolved: => Vector[StoredNode]): Vector[StoredNode] =
    AnalysisAblation.valueTracking match {
      case ValueTracking.Full => resolved
      case ValueTracking.Local =>
        val sources = resolved
        if (sources.nonEmpty && sources.forall(AnalysisAblation.reachable(_, call.method))) sources else ownSources(expression)
      case ValueTracking.None => ownSources(expression)
    }

  /** Without value tracking a parameter's source is what is written at the call: its literals, or the expression. */
  private def ownSources(expression: Expression): Vector[StoredNode] = {
    val literals = expression.ast.isLiteral.l.toVector
    if (literals.nonEmpty && AnalysisAblation.evaluable(expression)) literals else Vector(expression)
  }

  def argument(call: Call, binding: ParameterBinding): Option[Expression] =
    binding.keyword.flatMap { keyword =>
      call.argument.filter(_.argumentName.contains(keyword)).headOption
    }.orElse {
      binding.argumentIndex.flatMap { index =>
        call.argument
          .argumentIndex(index)
          // A named argument at the same physical position belongs to a
          // different API parameter (for example mac_len=16 is not an IV).
          .filter(argument => argument.argumentName.isEmpty || binding.keyword.isEmpty)
          .headOption
      }
    }

  /** `resolveExpression` is a pure function of its arguments over the CPG, which the enrichment pass only reads while
    * it resolves.  Parameter expansion reaches the same caller arguments from many crypto operations, and each
    * resolution runs a data-flow query, so the results are cached. */
  private val resolvedCache = mutable.HashMap.empty[(Long, Long, Boolean, Int), Vector[StoredNode]]
  private val definitionsByMethod = mutable.HashMap.empty[Long, Map[String, Set[String]]]
  private lazy val javaPaths = javaValueIndex.paths
  private lazy val javaSummaries = new JavaValueSummaries(cpg, sharedIndex = Some(javaValueIndex))
  private lazy val javaRuntimeValues = new JavaValueSummaries(cpg, includeRuntimeFieldWrites = true, sharedIndex = Some(javaValueIndex))
  private lazy val pythonDerivedKeys = new PythonDerivedKeyResolver(cpg)
  private lazy val assignmentsByMethod = cpg.call.nameExact("<operator>.assignment").l.groupBy(_.method.id)
  private lazy val constructorsByMethod = cpg.call.nameExact("<init>").l.groupBy(_.method.id)

  private def resolveExpression(
    owner: Call,
    expression: Expression,
    preferLiterals: Boolean,
    depth: Int
  ): Vector[StoredNode] = {
    val key = (owner.id, expression.id, preferLiterals, depth)
    resolvedCache.get(key) match {
      case Some(sources) => sources
      case None =>
        val sources = computeSources(owner, expression, preferLiterals, depth)
        resolvedCache.update(key, sources)
        sources
    }
  }

  private def computeSources(
    owner: Call,
    expression: Expression,
    preferLiterals: Boolean,
    depth: Int
  ): Vector[StoredNode] = {
    val sourceFile = fileName(expression)
    val direct = expression match {
      case node: Literal           => Vector(node)
      case node: MethodParameterIn => Vector(node)
      case node: Call if SourceClassifier.isKnownProducer(node) => Vector(node)
      case _                       => Vector.empty
    }

    // Python's frontend can join unrelated values in a busy function. Prefer
    // exact assignment/parameter chains there; otherwise a URL, mode name, or
    // version string may be mistaken for the source of a key or IV.
    val exactPythonSources = Option
      .when(sourceFile.toLowerCase.endsWith(".py"))(resolvePythonExpression(owner, expression, 0, Set.empty))
      .getOrElse(Vector.empty)
    if (sourceFile.toLowerCase.endsWith(".py")) {
      val derived = pythonDerivedKeys.sources(expression)
      if (derived.nonEmpty) return derived
      val resolved = distinctById(direct ++ exactPythonSources).take(limits.maxSourcesPerArgument)
      return if (resolved.nonEmpty) resolved else Vector(expression)
    }

    if (sourceFile.toLowerCase.endsWith(".java")) {
      val parsed = parsedByteSources(expression)
      if (parsed.nonEmpty) return parsed.get
      // Deterministic KDFs retain their password origin as well as the KDF marker. Exact
      // returned-value substitution avoids the broad DFG join with algorithm/salt siblings.
      val summary = javaSummaries.resolve(expression, material = true)
      if (summary.exists(v => v.arrayOutput || v.sources.exists {
        case call: Call => SourceClassifier.isJavaSecretLookup(call) || SourceClassifier.isJavaFileRead(call) || SourceClassifier.isJavaResourceRead(call) || Set(org.codeminers.standalone.ontology.CryptoSourceType.WeakRandom,
          org.codeminers.standalone.ontology.CryptoSourceType.Csprng).contains(SourceClassifier.classify(call)) ||
          (call.name == "generateSecret" && call.methodFullName.startsWith("javax.crypto.SecretKeyFactory.")) ||
          (call.name == "mac" && call.methodFullName.startsWith("jenkins.security.HMACConfidentialKey.")) ||
          (call.name == "getJnlpMac" && call.methodFullName.startsWith("hudson.slaves.SlaveComputer."))
        case _ => false
      })) return summary.get.sources.distinctBy(_.id).take(limits.maxSourcesPerArgument)
    }

    if (sourceFile.toLowerCase.endsWith(".java")) {
      val runtime = runtimeFieldConstants(expression)
      if (runtime.nonEmpty) return runtime.get
    }

    // A direct read of a field with a closed initializer has a real default path even when a setter may later
    // replace it. Joern joins those paths and unrelated calls, so use the exact initializer for this expression.
    val fieldDefault = if (isDirectFieldValue(expression)) fieldConstantSources(expression) else Vector.empty
    if (fieldDefault.nonEmpty) return distinctById(fieldDefault).take(limits.maxSourcesPerArgument)
    // Joern's generic heap flow merges a field's owner with every use of that object. Without an exact initializer
    // this is not evidence about the field value (and enumerates huge numbers of unrelated object paths).
    // Retain the explicit unknown field instead of promoting its receiver/callers to cryptographic material.
    if (sourceFile.toLowerCase.endsWith(".java") && isDirectFieldValue(expression)) return Vector(expression)

    // Exact AST chains need no whole-file path enumeration. Keep the DFG fallback for computations or ambiguous
    // branches; this fast path resolves only actual operands, last proven local writes, and exact caller arguments.
    if (sourceFile.toLowerCase.endsWith(".java")) {
      val exact = exactJavaValue(owner, expression, preferLiterals, depth, 0, Set.empty)
      if (exact.exists(_.nonEmpty)) return distinctById(exact.get).take(limits.maxSourcesPerArgument)
    }

    // reachableBy returns every node participating in a flow. For provenance
    // edges we only retain the first node of each bounded path: the terminal
    // origin, never intermediate literals/calls from the transformation chain.
    // Query only the source classes selected by the existing precedence below. A known producer is a
    // trust boundary, so its successful query makes the literal and parameter queries unnecessary.
    lazy val literalSources = terminalFlowSources(
      expression,
      concreteLiteralsByFile.getOrElse(sourceFile, Vector.empty).iterator
    )
    lazy val producerSources = terminalFlowSources(
      expression,
      producersByFile.getOrElse(sourceFile, Vector.empty).iterator
    )
    lazy val referencedIdentifiers = localDefinitionNames(expression)
    lazy val parameterSources = terminalFlowSources(
      expression,
      parametersByFile.getOrElse(sourceFile, Vector.empty).iterator
        .collect { case node: MethodParameterIn if referencedIdentifiers.contains(node.name) => node }
    )
      .collect {
        case node: MethodParameterIn if node.index > 0 && referencedIdentifiers.contains(node.name) => node
      }

    // Joern models a call as a data-flow join and may consequently report a
    // sibling argument (for example the "AES" algorithm literal) as a source
    // of the key argument. Such nodes are structurally impossible provenance
    // and must be removed before classification.
    val siblingArgumentIds = owner.argument
      .filterNot(_.id == expression.id)
      .flatMap(_.ast)
      .map(_.id)
      .toSet
    // A recognized generator/KDF is a semantic trust boundary. Its own
    // configuration literals (for example "AES") describe how a value was
    // generated, but are not themselves the generated key's source.
    val tracedSources =
      if (preferLiterals && literalSources.nonEmpty) literalSources ++ parameterSources
      else if (producerSources.nonEmpty) producerSources
      else literalSources ++ parameterSources
    val resolved = distinctById(
      expandParameters(distinctById(direct ++ tracedSources), preferLiterals, depth).flatMap(wholeArrayConstant)
    )
      .filterNot(node => siblingArgumentIds.contains(node.id))
      .take(limits.maxSourcesPerArgument)
    if (resolved.nonEmpty) resolved
    else {
      val fieldConstants = distinctById(fieldConstantSources(expression)).take(limits.maxSourcesPerArgument)
      if (fieldConstants.nonEmpty) fieldConstants
      else expression match {
        case node: Identifier => Vector(node)
        case node: StoredNode => Vector(node)
      }
    }
  }

  private def exactJavaValue(
    owner: Call,
    expression: Expression,
    preferLiterals: Boolean,
    depth: Int,
    steps: Int,
    seen: Set[Long]
  ): Option[Vector[StoredNode]] = {
    if (steps > 8 || seen(expression.id)) return None
    val visited = seen + expression.id
    def follow(value: Expression): Option[Vector[StoredNode]] =
      exactJavaValue(owner, value, preferLiterals, depth, steps + 1, visited)
    expression match {
      case literal: Literal => Some(Vector(literal))
      case parameter: MethodParameterIn if parameter.index > 0 =>
        Some(expandParameters(Vector(parameter), preferLiterals, depth))
      case _: MethodParameterIn => None
      case call: Call if SourceClassifier.isImplicitZeroArray(call) => Some(Vector(call))
      case call: Call if SourceClassifier.isKnownProducer(call) => Some(Vector(call))
      case call: Call if isDirectFieldValue(call) =>
        Option(fieldConstantSources(call)).filter(_.nonEmpty).orElse {
          Option.when(AnalysisAblation.full)(javaSummaries.resolve(call, material = true)).flatten
            .filter(summary => !summary.defaultPath && summary.sources.nonEmpty && summary.sources.forall {
              case literal: Literal => SourceClassifier.isConcreteLiteral(literal)
              case _ => false
            }).map(_.sources)
        }
      // `char[] password = {'s'}`: a constant array is its elements. Without this the generic DFG fallback can attach
      // an unrelated value of the same method (e.g. the salt's SecureRandom) as the password's source.
      case call: Call if call.name == "<operator>.arrayInitializer" =>
        Option.when(isClosedConstant(call))(contentLiterals(call)).filter(_.nonEmpty).orElse {
          // A freshly wrapped seed can still be constant when an element comes from a caller's scalar argument.
          // Require every element to resolve to a concrete literal before overriding generic data flow.
          Option.when(preferLiterals && call.typeFullName == "byte[]")(javaSummaries.resolve(call, material = true)).flatten
            .filter(summary => !summary.defaultPath && summary.sources.nonEmpty && summary.sources.forall {
              case literal: Literal => SourceClassifier.isConcreteLiteral(literal)
              case _                => false
            }).map(_.sources)
        }
      // A cast changes representation, not the origin. Continue at the value operand, never its type reference.
      case call: Call if call.name == "<operator>.cast" =>
        call.argument.l.filter(_.argumentIndex > 0).sortBy(_.argumentIndex).lastOption.flatMap(follow)
      case call: Call if ValuePreservingConversions(call.name) =>
        call.argument.l.find(_.argumentIndex == (if (call.name == "valueOf") 1 else 0)).flatMap(follow)
      case call: Call if call.name == "<init>" && ValueHolderConstructors.exists(call.methodFullName.startsWith) =>
        call.argument.l.find(_.argumentIndex == 1).flatMap(follow)
      case identifier: Identifier =>
        val writes = assignmentsByMethod.getOrElse(identifier.method.id, Nil)
          .filter(_.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == identifier.name))
          .filter(assignment => summon[Ordering[(Int, Int, Long)]].lt(sourceOrder(assignment), sourceOrder(identifier)))
          .filterNot(assignment => javaPaths.execution(assignment).contains(false))
          .sortBy(sourceOrder)
        writes.lastOption match {
          case Some(write) if javaPaths.execution(write).contains(true) =>
            write.argument.l.find(_.argumentIndex == 2).flatMap {
              case allocation: Call if SourceClassifier.isImplicitZeroArray(allocation) =>
                val mutations = identifier.method.ast.isCall.l.filter { call =>
                  summon[Ordering[(Int, Int, Long)]].gt(sourceOrder(call), sourceOrder(write)) &&
                    summon[Ordering[(Int, Int, Long)]].lt(sourceOrder(call), sourceOrder(identifier)) &&
                    !call.ast.exists(_.id == identifier.id) &&
                    (call.argument.l.exists(_.ast.isIdentifier.nameExact(identifier.name).nonEmpty) ||
                      (call.name == "<operator>.assignment" && call.argument.l.find(_.argumentIndex == 1)
                        .exists(_.code.startsWith(identifier.name + "[")))) &&
                    !Set("<operator>.fieldAccess", "<operator>.indexAccess").contains(call.name) &&
                    !(call.name == "<init>" && ValueHolderConstructors.exists(call.methodFullName.startsWith))
                }.sortBy(sourceOrder)
                mutations.lastOption match {
                  case None => Some(Vector(allocation))
                  case Some(mutation) if mutation.name == "nextBytes" &&
                    mutation.methodFullName.startsWith("java.security.SecureRandom.") &&
                    javaPaths.execution(mutation).contains(true) => Some(Vector(mutation))
                  case Some(_) => Some(Vector(identifier))
                }
              case allocation: Call if allocation.name == "<operator>.alloc" =>
                constructorsByMethod.getOrElse(identifier.method.id, Nil)
                  .filter(call => ValueHolderConstructors.exists(call.methodFullName.startsWith))
                  .filter(call => call.argument.l.find(_.argumentIndex == 0).exists(_.code.trim == identifier.name))
                  .filter(call => call.lineNumber == allocation.lineNumber)
                  .flatMap(_.argument.l.find(_.argumentIndex == 1)).headOption.flatMap(follow)
              case value => follow(value)
            }
          case Some(_) => None
          case None => identifier.method.parameter.nameExact(identifier.name).filter(_.index > 0).headOption.map(parameter =>
            expandParameters(Vector(parameter), preferLiterals, depth))
        }
      case _ => None
    }
  }

  private def sourceOrder(node: AstNode): (Int, Int, Long) =
    (node.lineNumber.getOrElse(Int.MaxValue), node.columnNumber.getOrElse(Int.MaxValue), node.id)

  /** The DFG reaches an array constant through whichever element it happens to walk first, so `{0, 0, 0, 0}` arrived
    * as a single `0`, which is indistinguishable from a null pointer or an offset.  The value is the whole
    * initializer, so report all of its elements.
    */
  private def wholeArrayConstant(node: StoredNode): Vector[StoredNode] = node match {
    case literal: Literal =>
      Try(literal.astParent).toOption.collect {
        case initializer: Call if initializer.name == "<operator>.arrayInitializer" =>
          initializer.argument.isLiteral.filter(SourceClassifier.isConcreteLiteral).l.toVector
      }.filter(_.nonEmpty).getOrElse(Vector(literal))
    case other => Vector(other)
  }

  /** Names whose value reaches `expression` through assignments in its own method.  A parameter is accepted as a
    * source only when the argument refers to it, and `keyBytes = key.getBytes(); new SecretKeySpec(keyBytes, ..)`
    * refers to `key` only through the local copy; without following that chain the parameter was dropped and the
    * caller's literal key was never reached.
    */
  private def localDefinitionNames(expression: Expression): Set[String] = {
    val definitions = Try(expression.method).toOption.map { method =>
      definitionsByMethod.getOrElseUpdate(
        method.id,
        method.ast.isCall
          .nameExact("<operator>.assignment")
          .l
          .flatMap { assignment =>
            for {
              target <- assignment.argument.l.find(_.argumentIndex == 1).collect { case id: Identifier => id.name }
              value  <- assignment.argument.l.find(_.argumentIndex == 2)
            } yield target -> value.ast.isIdentifier.name.toSet
          }
          .groupMapReduce(_._1)(_._2)(_ ++ _)
      )
    }.getOrElse(Map.empty)
    @annotation.tailrec
    def close(names: Set[String]): Set[String] = {
      val expanded = names ++ names.flatMap(name => definitions.getOrElse(name, Set.empty))
      if (expanded == names) names else close(expanded)
    }
    close(expression.ast.isIdentifier.name.toSet)
  }

  /** Joern's DFG does not connect a field read to the initializer that runs in `<init>`/`<clinit>` (or a rewritten
    * initializer block, see [[org.codeminers.standalone.JavaFrontend]]), so a
    * `private static final byte[] iv = {0, ...}` or a default `private byte[] key = "secret".getBytes()` reached the
    * classifier as an opaque field access.  Use the field's initialization when every assignment made there is a
    * closed constant; assignments made later by setters are ignored because the constant stays the reachable
    * default.  A constructor that copies a parameter or any other runtime value leaves the field unresolved.
    */
  private def fieldConstantSources(expression: Expression, depth: Int = 0): Vector[StoredNode] =
    if (depth > 3) Vector.empty
    else
      expression.ast.isCall
        .nameExact("<operator>.fieldAccess")
        .l
        .toVector
        .flatMap { access =>
          assignedFieldOf(access).toVector.flatMap { case target @ (declaringType, field) =>
            val initializers = fieldAssignmentsByName
              .getOrElse(field, Vector.empty)
              .filter(assignment => JavaFrontend.isInitializer(assignment.method.name))
              .filter(assignment =>
                assignedField(assignment).exists(_._1 == declaringType) ||
                  assignment.method.fullName.startsWith(s"$declaringType.")
              )
            val active = initializers.filterNot(initializer => javaPaths.execution(initializer).contains(false))
            val values = active.map(initializer => initializer -> initializedValue(initializer, target, depth))
            // A conditional configuration overwrite leaves the declared default reachable. An unconditional
            // runtime overwrite does not; never recover the earlier literal in that case.
            if (values.exists { case (initializer, value) => value.isEmpty && javaPaths.execution(initializer).contains(true) })
              Vector.empty
            else values.flatMap(_._2.toVector.flatten)
          }
        }

  /** The constant an initializer stores, or None when it is not provably constant.  Joern lowers
    * `KEY = new SecretKeySpec(DATA, "DESede")` into `KEY = alloc` plus a separate `KEY.<init>(DATA, "DESede")`; for the
    * value-holding spec and string classes the stored value is that constructor's first argument.
    */
  private def initializedValue(
    initializer: Call,
    target: (String, String),
    depth: Int
  ): Option[Vector[StoredNode]] =
    initializer.argument.l.find(_.argumentIndex == 2).flatMap {
      case value if isClosedConstant(value) => Some(contentLiterals(value))
      case call: Call if SourceClassifier.isPredictableSystemProperty(call) => Some(Vector(call))
      case value if isDirectFieldValue(value) =>
        Some(fieldConstantSources(value, depth + 1)).filter(_.nonEmpty)
      case allocation: Call if allocation.name == "<operator>.alloc" && allocation.argument.isEmpty =>
        initializer.method.ast.isCall
          .nameExact("<init>")
          .filter(call =>
            ValueHolderConstructors.exists(call.methodFullName.startsWith) &&
              call.argument.l.find(_.argumentIndex == 0).exists {
                case receiver: Call if receiver.name == "<operator>.fieldAccess" =>
                  assignedFieldOf(receiver).contains(target)
                case _ => false
              }
          )
          .argument
          .filter(_.argumentIndex == 1)
          .headOption
          .flatMap {
            case content if isClosedConstant(content) => Some(contentLiterals(content))
            case content =>
              Some(fieldConstantSources(content, depth + 1)).filter(_.nonEmpty)
          }
      case _ => None
    }

  /** (declaring type, field name) written by an assignment whose target is a field access. */
  private def assignedField(assignment: Call): Option[(String, String)] =
    assignment.argument.l.find(_.argumentIndex == 1).collect {
      case access: Call if access.name == "<operator>.fieldAccess" => access
    }.flatMap(assignedFieldOf)

  private def assignedFieldOf(access: Call): Option[(String, String)] = {
    val declaringType = access.argument.l.find(_.argumentIndex == 1).collect {
      case receiver: Identifier => receiver.typeFullName
      case receiver: TypeRef    => receiver.typeFullName
    }
    for {
      owner <- declaringType.map(_.takeWhile(_ != '<')).filter(_.nonEmpty)
      field <- access.ast.isFieldIdentifier.canonicalName.headOption
    } yield owner -> field
  }

  /** An initializer is a closed constant when it is built only from literals, operators and conversions that keep
    * their receiver's value; any variable, field read or other call makes its value unknown here. */
  private def isClosedConstant(value: Expression): Boolean =
    value match {
      // Representation arguments (charset, error policy) do not supply the key's material.
      case call: Call if ValuePreservingConversions(call.name) =>
        call.argument.l.find(_.argumentIndex == (if (call.name == "valueOf") 1 else 0)).exists(isClosedConstant)
      case _ => value.ast.isLiteral.nonEmpty && value.ast.forall {
      case _: Literal => true
      case call: Call =>
        (call.name.startsWith("<operator>") && call.name != "<operator>.fieldAccess") ||
        ValuePreservingConversions.contains(call.name)
      case _: Identifier => false
      case _             => true
      }
    }

  private def isDirectFieldValue(expression: Expression): Boolean = {
    val calls = expression.ast.isCall.l
    calls.exists(_.name == "<operator>.fieldAccess") &&
      calls.forall(call => call.name == "<operator>.fieldAccess" ||
        call.name == "<operator>.cast" || ValuePreservingConversions.contains(call.name))
  }

  private def contentLiterals(value: Expression): Vector[StoredNode] =
    value match {
      case call: Call if ValuePreservingConversions(call.name) =>
        call.argument.l.find(_.argumentIndex == (if (call.name == "valueOf") 1 else 0))
          .toVector.flatMap(contentLiterals)
      case _ => value.ast.isLiteral
      .filter(SourceClassifier.isConcreteLiteral)
      .filterNot(literal => literalShapesValue(literal, value))
      .l
      .toVector
    }

  /** The literal sits in a shape position somewhere between itself and `root` (for example `new byte[16]`). */
  private def literalShapesValue(literal: Literal, root: Expression): Boolean =
    Iterator
      .iterate(Option[AstNode](literal))(_.flatMap(node => Try(node.astParent).toOption))
      .takeWhile(_.nonEmpty)
      .flatten
      .takeWhile(_.id != root.id)
      .exists(node => isShapePosition(node))

  /** Joern's data flow ends at a method parameter, and the supported Java/Python frontends do not join flows
    * across source files.  A literal handed in from another class therefore reaches classification as a bare
    * parameter and reads as external input.  Walk the call graph in bounded steps and substitute the values the
    * callers actually pass; a parameter with no resolvable call site stays as it was.
    */
  private def expandParameters(
    nodes: Vector[StoredNode],
    preferLiterals: Boolean,
    depth: Int
  ): Vector[StoredNode] =
    if (depth >= limits.maxParameterHops) nodes
    else
      nodes.flatMap {
        case parameter: MethodParameterIn if parameter.index > 0 =>
          val method = parameter.method
          val exact  = callsByFullName.getOrElse(method.fullName, Vector.empty)
          val owner = method.fullName.takeWhile(_ != ':').stripSuffix(s".${method.name}")
          val fallback = callsByName.getOrElse(method.name, Vector.empty)
            .filter(_.methodFullName.startsWith(s"$owner.${method.name}:"))
            .filter(site => site.argument.count(_.argumentIndex > 0) == method.parameter.count(_.index > 0))
          val sites = (if (exact.nonEmpty) exact else fallback)
            .filterNot(_.method.id == method.id)
            .take(limits.maxCallSitesPerParameter)
          val callerSources = sites.flatMap { site =>
            site.argument.l
              .filter(_.argumentIndex == parameter.index)
              .flatMap(argument => resolveExpression(site, argument, preferLiterals, depth + 1))
          }
          // Keep the parameter when the callers add nothing; replacing it with itself would loop.
          val informative = callerSources.filterNot(_.id == parameter.id)
          if (informative.isEmpty) Vector(parameter) else informative
        // The receiver is an object identity, not a key/IV value. Expanding `this` walks every call of the object
        // (e.g. all cache operations) and adds unrelated literals as secret evidence.
        case _: MethodParameterIn => Vector.empty
        case other => Vector(other)
      }

  /** Resolve Python value provenance through exact symbols and assignments.
    *
    * This intentionally does not use a same-file literal pool. For compound
    * expressions every value argument is retained, so a literal prefix mixed
    * with a runtime parameter is classified as external rather than hardcoded.
    */
  private def resolvePythonExpression(
    owner: Call,
    expression: Expression,
    depth: Int,
    seen: Set[(Long, String)]
  ): Vector[StoredNode] = {
    if (depth > 8) Vector(expression)
    else
      expression match {
        case node: Literal           => Vector(node)
        case node: MethodParameterIn => Vector(node)
        case node: Call if SourceClassifier.isKnownProducer(node) => Vector(node)
        case node: Identifier => resolvePythonSymbol(owner, node.name, node, depth, seen)
        case call: Call =>
          if (isReceiverValueTransform(call)) {
            receiverSymbol(call.code)
              .map(symbol => resolvePythonSymbol(owner, symbol, call, depth, seen))
              .orElse(receiverLiteral(call).map(node => Vector(node: StoredNode)))
              .filter(_.nonEmpty)
              .getOrElse {
                call.receiver.headOption
                  .map(receiver => resolvePythonExpression(owner, receiver, depth + 1, seen))
                  .getOrElse(Vector(call))
              }
          } else if (isFirstArgumentValueTransform(call)) {
            call.argument
              .filter(_.argumentIndex == 1)
              .headOption
              .map(argument => resolvePythonExpression(owner, argument, depth + 1, seen))
              .getOrElse(Vector(call))
          } else if (isPythonSelectorAccess(call)) {
            // Mapping keys and member names select a value; they are not the
            // value itself. Treat doc['secret'] as flowing from doc, never
            // from the selector literal "secret".
            simplePythonSymbol(call.code)
              .map(symbol => resolvePythonSymbol(owner, symbol, call, depth, seen))
              .filter(_.nonEmpty)
              .getOrElse {
                call.argument
                  .filter(_.argumentIndex == 1)
                  .headOption
                  .map(argument => resolvePythonExpression(owner, argument, depth + 1, seen))
                  .getOrElse(Vector(call))
              }
          } else if (isOpaqueLookup(call)) {
            // A retrieval API's return value cannot be inferred from its
            // configuration key, e.g. config.get("api_key").
            Vector(call)
          } else simplePythonSymbol(call.code) match {
            case Some(symbol) => resolvePythonSymbol(owner, symbol, call, depth, seen)
            case None =>
              val valueArguments = call.argument.l.filter(_.argumentIndex > 0)
              val argumentSources = valueArguments.flatMap(argument =>
                resolvePythonExpression(owner, argument, depth + 1, seen)
              ).toVector
              if (argumentSources.nonEmpty) argumentSources
              else {
                val receiver = receiverSymbol(call.code)
                receiver
                  .map(symbol => resolvePythonSymbol(owner, symbol, call, depth, seen))
                  .filter(_.nonEmpty)
                  .getOrElse {
                    val literals = call.ast.isLiteral.filter(SourceClassifier.isConcreteLiteral).l
                    if (literals.nonEmpty) literals.toVector else Vector(call)
                  }
              }
          }
        case node: StoredNode => Vector(node)
      }
  }

  private def resolvePythonSymbol(
    owner: Call,
    rawSymbol: String,
    fallback: StoredNode,
    depth: Int,
    seen: Set[(Long, String)]
  ): Vector[StoredNode] = {
    val symbol = rawSymbol.replaceAll("\\s+", "")
    val tail   = symbol.split("\\.").lastOption.getOrElse(symbol)
    val method = owner.method
    val methodId = method.id
    val marker   = methodId -> symbol
    if (symbol.isEmpty || seen(marker)) Vector(fallback)
    else {
      val parameters = method.parameter.nameExact(tail).l.map(node => node: StoredNode)
      // Preserve the external parameter and add proven caller password roles. Do not turn
      // an arbitrary parameter named `secret` into a password or a source-code constant.
      val callerPasswordRoles = if (depth < 6 && parameters.nonEmpty) {
        val definitions = pythonMethodsByName.getOrElse(method.name, Nil)
        val sites = callsByName.getOrElse(method.name, Vector.empty).filter(site =>
          site.method.id != method.id && (fileName(site) == fileName(owner) ||
            (definitions.size == 1 && pythonImportsByFile.getOrElse(fileName(site), "").contains(method.name))))
        parameters.collect { case p: MethodParameterIn => p }.flatMap { p =>
          sites.take(limits.maxCallSitesPerParameter).flatMap(site =>
            site.argument.l.filter(a => a.argumentName.contains(p.name) || (a.argumentName.isEmpty && a.argumentIndex == p.index))
              .flatMap(a => resolvePythonExpression(site, a, depth + 1, seen + marker)))
        }.filter {
          case p: MethodParameterIn => p.name.matches("(?i).*(?:password|passwd|passphrase|pwd).*")
          case i: Identifier => i.name.matches("(?i).*(?:password|passwd|passphrase|pwd).*")
          case _ => false
        }
      } else Vector.empty
      val useLine    = owner.lineNumber.map(_.toInt).getOrElse(Int.MaxValue)
      val candidateAssignments = cpg.call
        .nameExact("<operator>.assignment")
        .filter(assignment => fileName(assignment) == fileName(owner))
        .filter(_.lineNumber.map(_.toInt).getOrElse(Int.MinValue) <= useLine)
        .filter { assignment =>
          val sameMethod = assignment.method.id == methodId
          val lhs = assignment.argument.l.find(_.argumentIndex == 1).map(_.code.replaceAll("\\s+", ""))
          val exact = lhs.contains(symbol)
          val moduleGlobal = assignment.method.name == "<module>" && exact
          val classConstant = tail.matches("[A-Z_][A-Z0-9_]+") && lhs.exists(_ == tail)
          (sameMethod && exact) || moduleGlobal || classConstant
        }
        .l
      val localAssignments = candidateAssignments.filter(_.method.id == methodId)
      val exactAssignments = if (localAssignments.nonEmpty) localAssignments else candidateAssignments
      val rhsSources = exactAssignments
        .flatMap(_.argument.l.find(_.argumentIndex == 2))
        .flatMap { rhs =>
          val terminals = resolvePythonExpression(owner, rhs, depth + 1, seen + marker)
          val derivationMarker = Option.when(
            tail.toLowerCase.split("[^a-z0-9]+").exists(Set("iv", "nonce")) &&
              isKeyDerivedIvExpression(rhs.code)
          )(rhs: StoredNode)
          derivationMarker.toVector ++ terminals
        }
        .toVector
      val fieldSources = resolvePythonField(owner, symbol, depth, seen + marker)
      val resolved = distinctById(parameters ++ callerPasswordRoles ++ rhsSources ++ fieldSources)
      if (resolved.nonEmpty) resolved else Vector(fallback)
    }
  }

  /** Resolve `object.field` only through the exact constructor argument assigned to that field. */
  private def resolvePythonField(
    owner: Call,
    symbol: String,
    depth: Int,
    seen: Set[(Long, String)]
  ): Vector[StoredNode] = {
    val parts = symbol.split("\\.").toVector
    if (parts.length != 2 || depth > 8) Vector.empty
    else {
      val objectName = parts.head
      val fieldName  = parts.last
      val file       = fileName(owner)
      // Python class fields are assignments in __init__, rather than MEMBER initializers.
      // Accept only a closed constant assigned by this exact class, with no other writes.
      if (objectName == "self") {
        val className = owner.method.fullName.stripSuffix(s".${owner.method.name}")
        val writes = cpg.call.nameExact("<operator>.assignment").l.filter(a =>
          fileName(a) == file && a.method.fullName.stripSuffix(s".${a.method.name}") == className &&
            a.argument.l.find(_.argumentIndex == 1).exists(_.code.replaceAll("\\s+", "") == symbol))
        val initializers = writes.filter(_.method.name == "__init__")
        if (initializers.nonEmpty && writes.size == initializers.size) {
          val values = initializers.flatMap(_.argument.l.find(_.argumentIndex == 2))
          if (values.forall(v => v.ast.isIdentifier.isEmpty && v.ast.isCall.filterNot(_.name.startsWith("<operator>.")).isEmpty))
            return values.flatMap(v => resolvePythonExpression(owner, v, depth + 1, seen)).toVector
        }
      }
      val constructors = cpg.call
        .nameExact("<operator>.assignment")
        .filter(assignment => fileName(assignment) == file)
        .filter(_.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == objectName))
        .argument
        .filter(_.argumentIndex == 2)
        .ast
        .isCall
        .filterNot(_.name.startsWith("<operator>"))
        .l
      constructors.flatMap { constructor =>
        cpg.method
          .nameExact("__init__")
          .filter(_.fullName.contains(s".${constructor.name}.__init__"))
          .l
          .flatMap { init =>
            cpg.call
              .nameExact("<operator>.assignment")
              .filter(_.method.id == init.id)
              .filter(_.argument.l.find(_.argumentIndex == 1).exists(_.code.trim.endsWith(s".$fieldName")))
              .argument
              .filter(_.argumentIndex == 2)
              .isIdentifier
              .l
              .flatMap { rhs =>
                init.parameter.nameExact(rhs.name).l.flatMap { parameter =>
                  constructor.argument.l
                    .filter(_.argumentIndex == parameter.index)
                    .flatMap(value => resolvePythonExpression(owner, value, depth + 1, seen))
                }
              }
          }
      }.toVector
    }
  }

  private def simplePythonSymbol(code: String): Option[String] = {
    val compact = code.trim.replaceAll("\\s+", "")
    Option.when(compact.matches("[A-Za-z_$][A-Za-z0-9_$]*(?:\\.[A-Za-z_$][A-Za-z0-9_$]*)*"))(compact)
  }

  private def receiverSymbol(code: String): Option[String] = {
    val compact = code.trim.replaceAll("\\s+", "")
    "^([A-Za-z_$][A-Za-z0-9_$]*(?:\\.[A-Za-z_$][A-Za-z0-9_$]*)*)\\.[A-Za-z_$][A-Za-z0-9_$]*\\(.*".r
      .findFirstMatchIn(compact)
      .map(_.group(1))
  }

  private def receiverLiteral(call: Call): Option[Literal] = {
    val code = call.code.trim
    val receiver = "^((?i:[rubf]*)(?:\"[^\"]*\"|'[^']*'))\\.[A-Za-z_$][A-Za-z0-9_$]*\\(.*".r
      .findFirstMatchIn(code)
      .map(_.group(1))
    receiver.flatMap(value => call.ast.isLiteral.find(_.code.trim == value))
  }

  /** Calls such as key.encode("utf-8") preserve the receiver value. Their
    * configuration arguments describe representation and must never become
    * evidence that the cryptographic key itself is hardcoded.
    */
  private def isReceiverValueTransform(call: Call): Boolean =
    Set("encode", "decode", "digest", "hexdigest", "copy", "split", "rsplit").contains(call.name.toLowerCase)

  /** Python conversion helpers preserve their first value argument; later
    * arguments are encodings, error policies, or formatting options.
    */
  private def isFirstArgumentValueTransform(call: Call): Boolean =
    Set("bytes", "bytearray", "memoryview", "str", "unicode", "text_type").contains(call.name.toLowerCase)

  private def isPythonSelectorAccess(call: Call): Boolean =
    Set("<operator>.indexaccess", "<operator>.fieldaccess").contains(call.name.toLowerCase)

  private def isOpaqueLookup(call: Call): Boolean = {
    val name = call.name.toLowerCase
    Set("get", "getenv", "find", "find_one", "find_and_modify", "get_password", "option").contains(name) ||
    name.matches(".*(?:get|load|read|fetch)_secret(?:_key)?")
  }

  private def isKeyDerivedIvExpression(code: String): Boolean = {
    val compact = code.toLowerCase.replaceAll("\\s+", "")
    compact.matches("(?s).*(?:key|secret)(?:\\[[^]]+]|\\.__getitem__\\().*")
  }

  private val flowCache = mutable.HashMap.empty[(Long, Vector[Long]), Vector[StoredNode]]
  private val shapeLiteralCache = mutable.LongMap.empty[Boolean]
  private case object AwaitFlow extends ControlThrowable
  private final case class PendingFlow(expression: Expression, candidates: Vector[CfgNode])
  private var collectingFlows = false
  private val pendingFlows = mutable.LinkedHashMap.empty[(Long, Vector[Long]), PendingFlow]
  private[standalone] var flowSearchCount = 0
  private[standalone] var multiSinkSearchCount = 0

  private def executePendingFlows(): Unit = {
    val groups = mutable.LinkedHashMap.empty[Vector[Long], Vector[PendingFlow]]
    pendingFlows.foreach { case ((_, ids), request) =>
      groups.update(ids, groups.getOrElse(ids, Vector.empty) :+ request)
    }
    pendingFlows.clear()
    groups.foreach { (ids, requests) =>
      // Chunking bounds engine working memory; every endpoint is processed, with the same evidence/path limits.
      requests.grouped(16).foreach { chunk =>
        val values = if (chunk.sizeIs == 1) Map(chunk.head.expression.id ->
          singleFlowSources(chunk.head.expression, chunk.head.candidates))
        else batchFlowSources(chunk)
        chunk.foreach(request => flowCache.update((request.expression.id, ids), values(request.expression.id)))
      }
    }
  }

  private def batchFlowSources(requests: Vector[PendingFlow]): Map[Long, Vector[StoredNode]] = {
    val candidates = requests.head.candidates
    val sinks = requests.map(_.expression)
    val ids = sinks.map(_.id).toSet
    flowSearchCount += 1
    multiSinkSearchCount += 1
    val attempted = OperationModelTiming.measure(s"provenance.batch sinks=${sinks.size} sources=${candidates.size}")(Try {
      val results = mutable.HashMap.from(sinks.map(sink => sink.id -> Vector.newBuilder[StoredNode]))
      sinks.iterator.reachableByFlows(candidates.iterator).foreach { path =>
        val target = path.elements.lastOption.map(_.id).getOrElse(
          throw new IllegalStateException("empty batch provenance path"))
        require(ids(target), "a batch path must end at one of its requested sinks")
        if (path.elements.size <= limits.maxPathLength && !carriesOnlyShape(path.elements))
          path.elements.headOption.collect { case node: StoredNode => results(target) += node }
      }
      results.iterator.map((id, builder) => id -> builder.result()).toMap
    })
    // A failed group must not hide a successful independent endpoint.
    val resolved = attempted.getOrElse(requests.map(r => r.expression.id -> singleFlowSources(r.expression, r.candidates)).toMap)
    if (java.lang.Boolean.getBoolean("crypto.provenance.verifyBatch")) requests.foreach { request =>
      val expected = singleFlowSources(request.expression, request.candidates).map(_.id).toSet
      require(resolved(request.expression.id).map(_.id).toSet == expected,
        s"batch provenance differs from independent query at ${fileName(request.expression)}:${request.expression.lineNumber}")
    }
    resolved
  }

  private def singleFlowSources(expression: Expression, candidates: Vector[CfgNode]): Vector[StoredNode] = {
    flowSearchCount += 1
    OperationModelTiming.measure(
      s"provenance.flow ${fileName(expression)}:${expression.lineNumber.getOrElse(-1)} sources=${candidates.size}")(Try {
      Iterator.single(expression)
        .reachableByFlows(candidates.iterator)
        .filter(_.elements.size <= limits.maxPathLength)
        .filterNot(path => carriesOnlyShape(path.elements))
        .flatMap(_.elements.headOption)
        .collect { case node: StoredNode => node }
        .toVector
    }.getOrElse(Vector.empty))
  }

  /** These positions are rejected by carriesOnlyShape regardless of the next path element. Filtering them
    * before search avoids constructing paths that can never survive the existing provenance validation.
    * Lookups and scalar library arguments are deliberately excluded: they can enter a callee parameter.
    */
  private def unconditionalShapeLiteral(literal: Literal): Boolean = shapeLiteralCache.getOrElseUpdate(literal.id,
    Try(literal.astParent).toOption.exists {
      case call: Call if Set("<operator>.alloc", "<operator>.indexAccess", "<operator>.conditional").contains(call.name) ||
        PredicateOperators(call.name) || ArithmeticOperators(call.name) || AlgorithmFactories(call.name) =>
          isShapePosition(literal)
      case _ => false
    })

  private def terminalFlowSources(expression: Expression, sources: Iterator[CfgNode]): Vector[StoredNode] = {
    val candidates = sources.filterNot {
      case literal: Literal if literal.id != expression.id => unconditionalShapeLiteral(literal)
      case _ => false
    }.toVector
    if (candidates.isEmpty) return Vector.empty
    if (sys.props.get("crypto.provenance.trace").contains("true"))
      println(s"provenanceFlow file=${fileName(expression)} method=${expression.method.fullName} " +
        s"line=${expression.lineNumber.getOrElse(-1)} expression=${expression.code} sources=${candidates.size}")
    val key = (expression.id, candidates.map(_.id))
    flowCache.get(key) match {
      case Some(sources) => sources
      case None if collectingFlows =>
        pendingFlows.getOrElseUpdate(key, PendingFlow(expression, candidates))
        throw AwaitFlow
      case None =>
        val sources = singleFlowSources(expression, candidates)
        flowCache.update(key, sources)
        sources
    }
  }

  /** A literal is key/IV/salt *material* only when it reaches the argument through value-carrying positions.  The
    * same code also sizes the buffer (`new byte[24]`), indexes it (`key[i / 8]`), bounds loops, passes lengths and
    * offsets (`Arrays.copyOf(hash, 16)`, `System.arraycopy(.., 0, .., 0, 8)`), selects a cipher direction
    * (`cipher.init(1, ..)`) or names an algorithm (`getInstance("AES/CTS/NoPadding")`), and Joern's default call
    * semantics propagate all of these into the result.  Such a literal describes how a runtime value was built, so
    * its path is not provenance; otherwise a key derived from a parameter is reported as hardcoded.
    */
  private def carriesOnlyShape(elements: Iterable[AstNode]): Boolean = elements.headOption match {
    case Some(_: Literal) =>
      // The last element is the resolved argument itself; its position inside the crypto API call is the sink,
      // not part of the value's construction.
      val path = elements.toVector
      path.indices.dropRight(1).exists(index => isShapePosition(path(index), path.lift(index + 1)))
    case _ => false
  }

  /** Whether the value at `node` is consumed as shape by the expression that contains it.  Positions are judged by
    * the static type of the value, not of the originating literal: `{20, 10, ..}` travels as a `byte[]` through
    * `Arrays.copyOf(keyBytes, 16)` and stays content, while the `int` 16 beside it is a length.  `next` is the
    * following path element: a value handed to a method the analysis enters continues into that method's parameter
    * and is judged there, so only a value absorbed by the call itself (library code) is shape.
    */
  private def isShapePosition(node: AstNode, next: Option[AstNode] = None): Boolean = node match {
    case expression: Expression =>
      Try(expression.astParent).toOption.exists {
        case call: Call =>
          val index = expression.argumentIndex
          if (SourceClassifier.isJavaSecretLookup(call) || SourceClassifier.isJavaFileRead(call)) index > 0
          else call.name match {
            // Array dimensions; an initializer's elements (`new byte[]{..}`) are content wherever it is nested.
            case "<operator>.alloc" =>
              !(expression match {
                case nested: Call => nested.name == "<operator>.arrayInitializer"
                case _            => false
              })
            case "<operator>.indexAccess"                   => index == 2
            case "<operator>.conditional"                   => index == 1
            case name if PredicateOperators.contains(name)  => true
            case name if ArithmeticOperators.contains(name) => isScalar(expression)
            case name if name.startsWith("<operator>")      => false
            case name if AlgorithmFactories.contains(name)  => index >= 1
            case name if LookupCalls.contains(name) =>
              index == 1 && !next.exists(_.isInstanceOf[MethodParameterIn])
            case _ if call.methodFullName.startsWith("java.util.Objects.requireNonNull:") ||
              call.methodFullName.startsWith("io.netty.util.internal.ObjectUtil.checkNotNull:") =>
              index > 1 && !next.exists(_.isInstanceOf[MethodParameterIn])
            case _ =>
              index >= 1 && isScalar(expression) && !ValuePreservingConversions.contains(call.name) &&
              !next.exists(_.isInstanceOf[MethodParameterIn])
          }
        case _ => false
      }
    case _ => false
  }

  private def isScalar(expression: Expression): Boolean = {
    val staticType = expression match {
      case literal: Literal       => Some(literal.typeFullName)
      case identifier: Identifier => Some(identifier.typeFullName)
      case call: Call             => Some(call.typeFullName)
      case _                      => None
    }
    staticType.exists(ScalarTypes.contains)
  }

  private def distinctById(nodes: Iterable[StoredNode]): Vector[StoredNode] =
    nodes.iterator.foldLeft(Vector.empty[StoredNode]) { (result, node) =>
      if (result.exists(_.id == node.id)) result else result :+ node
    }

  private def fileName(node: StoredNode): String = node match {
    case astNode: io.shiftleft.codepropertygraph.generated.nodes.AstNode =>
      astNode.file.name.headOption.getOrElse("")
    case _ => ""
  }
}
