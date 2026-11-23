package org.codeminers.standalone.enrichment

import io.joern.dataflowengineoss.language.*
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, CfgNode, Expression, Identifier, Literal, StoredNode}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.*

import scala.collection.mutable
import scala.util.Try

/** Resolves adapter attributes whose call-site argument is an identifier, field, parameter, constructed string, or
  * returned value.
  *
  * A value is accepted only when all bounded data-flow/AST candidates normalize to one semantic value. Ambiguous
  * paths therefore remain explicit instead of choosing a convenient constant and creating path-sensitive false
  * positives.
  */
private[enrichment] final class AttributeValueResolver(cpg: Cpg, sharedIndex: Option[JavaValueIndex] = None)(implicit engineContext: EngineContext) {
  private val index = sharedIndex.getOrElse(new JavaValueIndex(cpg))
  private val constructedStrings = new StringValueEvaluator(cpg, Some(index))
  private val javaSummaries = new JavaValueSummaries(cpg, sharedIndex = Some(index))

  private lazy val literalsByFile: Map[String, Vector[CfgNode]] = index.literalsByFile

  private lazy val callsByFile: Map[String, Vector[CfgNode]] = index.callsByFile
  private lazy val assignmentsByFile = index.writesByFile
  private lazy val callsByName = index.callsByName
  private lazy val callsByFullName = index.callsByFullName

  /** `manualValues` is a pure function of its arguments over a CPG that is read-only while the pass resolves, and its
    * recursion strictly increases `depth`, so caching it cannot change any result.  Without the cache, a file that
    * reuses one local name across many methods (typical of decompiled code: `var1`, `i`) re-walks the same symbol
    * chains once per path, which is exponential in the depth bound. */
  private val manualValuesCache = mutable.HashMap.empty[(Long, AttributeExtraction, Int), Vector[String]]
  private val candidateCache = mutable.HashMap.empty[(AttributeExtraction, String), Option[String]]
  private val expressionCache = mutable.HashMap.empty[(Long, AttributeExtraction), Option[String]]
  private val flowCandidateCache = mutable.HashMap.empty[(String, AttributeExtraction), Vector[CfgNode]]

  def resolve(call: Call, matched: AdapterMatch): AdapterMatch = {
    val recovered = matched.rule.extract.flatMap { spec =>
      expression(call, spec).filter(AnalysisAblation.evaluable).flatMap { value =>
        if (matched.rule.id == "python-cryptography-dsa-generate" && spec.target == "keySize" &&
          value.ast.isCall.nameExact("<operator>.indexAccess").nonEmpty)
          pythonMappingKeySize(value, spec)
        else resolveExpression(value, spec)
      }.map(spec.target -> _)
    }.toMap
    val attributes = matched.attributes ++ recovered
    // In the benchmark's default JCE provider, the one-argument transformation "AES" uses ECB.
    // Require the entire argument to resolve to AES; an unknown or partly resolved transformation
    // must not inherit ECB from an unrelated literal. An explicit provider can choose its own default.
    val bareDefaultAes = matched.rule.id == "java-cipher-get-instance" &&
      call.argument.argumentIndex(2).isEmpty &&
      call.argument.argumentIndex(1).headOption.flatMap(constructedStrings.evaluate)
        .exists(_.trim.equalsIgnoreCase("AES")) &&
      attributes.get("mode").forall(value => value.isEmpty || value == AttributeExtractor.Unknown)
    val bareRsa = matched.rule.id == "java-cipher-get-instance" &&
      attributes.get("algorithm").exists(_.equalsIgnoreCase("RSA")) &&
      attributes.get("padding").forall(value => value.isEmpty || value == AttributeExtractor.Unknown)
    val normalized = if (bareDefaultAes) attributes.updated("mode", "ECB") else attributes
    // An omitted transformation component selects a provider default; it does not prove NoPadding.
    matched.copy(attributes = if (bareRsa) normalized.updated("padding", "PROVIDER_DEFAULT") else normalized)
  }

  /** Recover an exact local dictionary entry, including a reachable weak default. Never merge
    * similarly named entries in other methods or recover a value before a later unconditional write.
    * Unknown writes and aliases remain unresolved rather than inheriting a convenient constant.
    */
  private def pythonMappingKeySize(value: Expression, spec: AttributeExtraction): Option[String] = {
    def compact(text: String): String = text.replaceAll("\\s+", "").replace('\'', '"')
    val selector = compact(value.code).stripPrefix("int(").stripSuffix(")")
    if (!selector.matches("[A-Za-z_][A-Za-z0-9_]*\\[\"[^\"]+\"\\]")) return None
    val base = selector.takeWhile(_ != '[')
    val writes = assignmentsByFile.getOrElse(fileName(value), Vector.empty)
      .filter(a => a.method.id == value.method.id && a.lineNumber.exists(_ < value.lineNumber.getOrElse(0)))
      .sortBy(_.lineNumber.getOrElse(0))
    // Rebinding the whole mapping or handing it to another alias invalidates this bounded proof.
    if (writes.exists(a => a.argument.argumentIndex(1).code.headOption.exists(c => compact(c) == base) ||
      a.argument.argumentIndex(2).code.headOption.exists(c => compact(c) == base))) return None
    if (cpg.call.filter(c => c.method.id == value.method.id &&
      c.lineNumber.exists(_ < value.lineNumber.getOrElse(0)) && !c.name.startsWith("<operator>.") &&
      (compact(c.code).startsWith(base + ".") || c.argument.codeExact(base).nonEmpty)).nonEmpty) return None
    val relevant = writes.filter(_.argument.argumentIndex(1).code.headOption.exists(c => compact(c) == selector))
    val lastUnconditional = relevant.lastIndexWhere(_.inAst.isControlStructure.isEmpty)
    val reaching = relevant.drop(math.max(lastUnconditional, 0))
    val candidates = reaching.map(_.argument.argumentIndex(2).headOption.flatMap(v => candidateValue(spec, v.code)))
    if (candidates.isEmpty || candidates.exists(_.isEmpty)) None
    else candidates.flatten.distinct match {
      case Vector(single) => Some(single)
      case _ => None
    }
  }

  private def expression(call: Call, spec: AttributeExtraction): Option[Expression] = spec.source match {
    case ExtractionSource.Argument => spec.keyword.flatMap(name => call.argument.filter(_.argumentName.contains(name)).headOption)
      .orElse(spec.argumentIndex.flatMap(index => call.argument.argumentIndex(index).filter(_.argumentName.isEmpty).headOption))
    case ExtractionSource.Keyword  => spec.keyword.flatMap(name => call.argument.filter(_.argumentName.contains(name)).headOption)
    case _                         => None
  }

  private def resolveExpression(expression: Expression, spec: AttributeExtraction): Option[String] = {
    expressionCache.getOrElseUpdate((expression.id, spec), computeExpression(expression, spec))
  }

  private def computeExpression(expression: Expression, spec: AttributeExtraction): Option[String] = {
    // A transformation assembled by `+`, `String.format`, or a builder chain holds its separators and its values in
    // different nodes, so the per-node patterns below can never see `AES/CBC/PKCS5Padding`.  Rebuilding the string
    // first, and only accepting it when every part resolved, recovers `mode` and `padding` for those call sites while
    // leaving every other shape to the existing candidate search.
    val summaryValues = Option.when(fileName(expression).endsWith(".java"))(javaSummaries.resolve(expression)).flatten
      .toVector.flatMap(_.sources).collect { case literal: Literal => literal.code.trim.stripPrefix("\"").stripSuffix("\"") }
      .distinct
    val summarized = Option.when(summaryValues.size == 1)(summaryValues.head).flatMap(candidateValue(spec, _))
    if (summarized.nonEmpty) return summarized
    val constructed = constructedStrings.evaluate(expression).flatMap(value => candidateValue(spec, value))
    if (constructed.nonEmpty) return constructed

    val file = fileName(expression)
    val directNodes = (Vector(expression: StoredNode) ++ expression.ast.l.collect { case node: StoredNode => node })
      .distinctBy(_.id)
      .filterNot(node => node.id == expression.id && expression.isInstanceOf[Identifier])
    val directValues = directNodes.flatMap(node => candidateValue(spec, code(node))).distinct
    if (directValues.size == 1) return directValues.headOption
    val exactSymbolValues = manualValues(expression, spec, depth = 0)
    if (exactSymbolValues.size == 1) return exactSymbolValues.headOption
    // Iteration counts may have several reachable call paths, including an unsafe public default.
    // Keep the weakest proven count rather than dropping every path as ambiguous.
    if (spec.target == "iterations" && file.endsWith(".py") && exactSymbolValues.nonEmpty &&
      exactSymbolValues.forall(_.replace("_", "").toIntOption.exists(_ > 0)))
      return Some(exactSymbolValues.map(_.replace("_", "").toInt).min.toString)

    val flowCandidates = flowCandidateCache.getOrElseUpdate((file, spec),
      (literalsByFile.getOrElse(file, Vector.empty) ++ callsByFile.getOrElse(file, Vector.empty))
        .filter(node => candidateValue(spec, code(node)).nonEmpty))
    val flowSources = Try {
      if (flowCandidates.isEmpty) Vector.empty
      else
        Iterator.single(expression)
          .reachableByFlows(flowCandidates.iterator)
          .filter(_.elements.size <= 64)
          .filter(path => path.elements.forall(AnalysisAblation.reachable(_, expression.method)))
          .flatMap(_.elements.headOption)
          .collect { case node: StoredNode => node }
          .toVector
    }.getOrElse(Vector.empty)

    val values = flowSources.flatMap(node => candidateValue(spec, code(node))).distinct
    values match {
      case Vector(single) => Some(single)
      case _              => None
    }
  }

  /** Joern's supported frontends do not always connect static fields or calls split across benchmark source files.
    * Resolve only exact symbol assignments and exact parameter positions, retaining a result only when it is unique. */
  private def manualValues(expression: Expression, spec: AttributeExtraction, depth: Int): Vector[String] = {
    val key = (expression.id, spec, depth)
    manualValuesCache.get(key) match {
      case Some(values) => values
      case None =>
        val values = computeManualValues(expression, spec, depth)
        manualValuesCache.update(key, values)
        values
    }
  }

  private def computeManualValues(expression: Expression, spec: AttributeExtraction, depth: Int): Vector[String] = {
    // Static benchmark and real-world configuration values are frequently copied through a constant, a mutable
    // class field, a normalized lower-case field, and finally a value-preserving conversion.  Six bounded exact
    // symbol steps cover that shape while the unique-value requirement below still rejects ambiguous branches.
    if (depth > 6) Vector.empty
    else {
      // A static field read is a `<operator>.fieldAccess` whose AST children are an IDENTIFIER carrying the
      // *type* name and a FIELD_IDENTIFIER carrying the field name.  Collecting identifiers alone therefore
      // yields the class, not the field, so a field-to-field copy (`crypto = CRYPTO`) ended the backward walk.
      val symbols = (expression.ast.isIdentifier.name.l ++
        expression.ast.isFieldIdentifier.canonicalName.l ++
        Option.when(expression.code.trim.matches("[A-Za-z_$][A-Za-z0-9_$]*"))(expression.code.trim).toVector ++
        valuePreservingSymbols(expression.code)).distinct
      val assigned = symbols.flatMap { symbol =>
        assignmentsByFile
          .getOrElse(fileName(expression), Vector.empty)
          .filter(assignment => {
            val lhs = assignment.argument.l.find(_.argumentIndex == 1).map(_.code.trim).getOrElse("")
            if (AnalysisAblation.full) lhs == symbol || lhs.endsWith(s".$symbol")
            else lhs == symbol && assignment.method.id == expression.method.id
          })
          .flatMap(_.argument.l.find(_.argumentIndex == 2))
          .flatMap(value => directAndManual(value, spec, depth + 1))
      }
      val parameters = symbols.filter(_ => AnalysisAblation.full).flatMap { symbol =>
        expression.method.parameter.nameExact(symbol).l.flatMap { parameter =>
          val exact = callsByFullName.getOrElse(expression.method.fullName, Vector.empty)
          val callSites =
            if (exact.nonEmpty) exact
            else if (!expression.method.name.startsWith("<")) callsByName.getOrElse(expression.method.name, Vector.empty)
            else Vector.empty
          val defaults = Option.when(fileName(expression).endsWith(".py"))(
            PythonDefaults.value(expression.method, symbol).flatMap(candidateValue(spec, _))).flatten.toVector
          defaults ++ callSites
            .filterNot(_.method.id == expression.method.id)
            .take(64)
            .flatMap(_.argument.l.filter(a => a.argumentName.contains(parameter.name) ||
              (a.argumentName.isEmpty && a.argumentIndex == parameter.index)))
            .flatMap(value => directAndManual(value, spec, depth + 1))
        }
      }
      val memberInitializers = symbols.filter(_ => AnalysisAblation.full).flatMap { symbol =>
        cpg.member
          .nameExact(symbol)
          .filter(member => fileName(member) == fileName(expression))
          .l
          .flatMap { member =>
            val literalValues = member.ast.isLiteral.code.l.flatMap(value => candidateValue(spec, value))
            val initializer = member.code.split("=", 2).lift(1).toVector
              .map(_.stripSuffix(";").trim)
              .flatMap(value => candidateValue(spec, value))
            literalValues ++ initializer
          }
      }
      (assigned ++ parameters ++ memberInitializers).distinct.toVector
    }
  }

  private def directAndManual(expression: Expression, spec: AttributeExtraction, depth: Int): Vector[String] = {
    val direct = (Vector(expression: StoredNode) ++ expression.ast.l.collect { case node: StoredNode => node })
      .flatMap(node => candidateValue(spec, code(node)))
      .distinct
    if (direct.nonEmpty) direct else manualValues(expression, spec, depth)
  }

  private def valuePreservingSymbols(code: String): Vector[String] = {
    val compact = code.replaceAll("\\s+", "")
    val receiver = "([A-Za-z_$][A-Za-z0-9_$]*)\\.(?:toCharArray|toString)\\(\\)".r
      .findAllMatchIn(compact)
      .map(_.group(1))
    val argument = "(?:String\\.)?valueOf\\(([A-Za-z_$][A-Za-z0-9_$]*)\\)".r
      .findAllMatchIn(compact)
      .map(_.group(1))
    (receiver ++ argument).toVector.distinct
  }

  /** Unpatterned adapter fields need a semantic guard: otherwise unrelated payload literals that share a Joern call
    * join with an algorithm/iteration argument make the result ambiguous. */
  private val AlgorithmPattern = java.util.regex.Pattern.compile(
    "(?:AES|ARIA|BLOWFISH|CAMELLIA|CAST5|CHACHA20|DES|DES3|DESEDE|IDEA|RC2|RC4|ARC2|ARC4|RSA|DSA|EC|ECDSA|ED25519|MD2|MD4|MD5|SHA|SHA-?1|SHA-?224|SHA-?256|SHA-?384|SHA-?512|PBKDF2(?:WITHHMAC[A-Z0-9-]+)?|PBEWITH[A-Z0-9]+AND[A-Z0-9_-]+)")
  private val ModePattern = java.util.regex.Pattern.compile("(?:CBC|CCM|CFB|CTR|ECB|GCM|OFB|SIV|XTS)")
  private val PaddingPattern = java.util.regex.Pattern.compile("(?:NONE|NOPADDING|OAEP|PKCS1|PKCS5|PKCS7|PSS|[A-Z0-9-]*PADDING)")

  private def candidateValue(spec: AttributeExtraction, raw: String): Option[String] = {
    // Bound retained source strings on large projects. Eviction changes only recomputation, never results.
    if (candidateCache.size >= 16384) candidateCache.clear()
    candidateCache.getOrElseUpdate((spec, raw), AttributeExtractor.extractValue(spec, raw).filter { value =>
      spec.target match {
        case "iterations" | "keyLength" | "keySize" | "outputLength" =>
          value.replace("_", "").toIntOption.nonEmpty
        case "algorithm" =>
          AlgorithmPattern.matcher(value.toUpperCase).matches()
        case "mode" =>
          ModePattern.matcher(value.toUpperCase).matches()
        case "padding" =>
          PaddingPattern.matcher(value.toUpperCase).matches()
        case _ => true
      }
    })
  }

  private def code(node: StoredNode): String = node match {
    case ast: AstNode => ast.code
    case _            => ""
  }

  private def fileName(node: StoredNode): String = node match {
    case ast: AstNode => ast.file.name.headOption.getOrElse("")
    case _            => ""
  }
}
