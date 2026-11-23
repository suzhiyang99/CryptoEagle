package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{
  AstNode,
  Call,
  ControlStructure,
  Expression,
  Identifier,
  Literal,
  Method
}
import io.shiftleft.semanticcpg.language.*

import scala.collection.mutable

/** Evaluates only Java branches whose value is uniquely derivable from literals, exact local assignments, or exact
  * call-site arguments. Unknown and conflicting values deliberately remain unknown.
  */
private[standalone] final class JavaConstantPathEvaluator(cpg: Cpg, strictCallSites: Boolean = false,
  sharedIndex: Option[JavaValueIndex] = None) {
  import JavaConstantPathEvaluator.*

  private lazy val strictPaths = if (strictCallSites) this
    else new JavaConstantPathEvaluator(cpg, strictCallSites = true, sharedIndex = sharedIndex)

  // Graph structure is immutable during value evaluation. Cache structure, not context-dependent value results.
  private val methodCache = mutable.LongMap.empty[Method]
  private val argumentCache = mutable.LongMap.empty[List[Expression]]
  private val positionCache = mutable.LongMap.empty[(Int, Int, Long)]
  private val branchNodes = mutable.LongMap.empty[(Set[Long], Set[Long])]
  private val literalCache = mutable.HashMap.empty[String, Option[Constant]]
  private type CallerInput = (Call, Option[Expression])
  private val callerInputs = mutable.HashMap.empty[
    (Boolean, String, Int), (Vector[CallerInput], Vector[CallerInput])]
  private lazy val indexedCalls = sharedIndex.map(_.calls).getOrElse(cpg.call.l.toVector)
  private def enclosingMethod(call: Call): Method = methodCache.getOrElseUpdate(call.id, call.method)
  private def arguments(call: Call): List[Expression] =
    sharedIndex.map(_.argumentsOf(call)).getOrElse(argumentCache.getOrElseUpdate(call.id, call.argument.l))

  private lazy val assignmentsByMethod: Map[Long, Vector[Call]] = indexedCalls
    .filter(_.name == "<operator>.assignment")
    .groupBy(enclosingMethod(_).id)
    .view
    .mapValues(_.toVector.sortBy(order))
    .toMap
  private lazy val assignmentsByMethodAndName: Map[(Long, String), Vector[Call]] =
    assignmentsByMethod.toVector.flatMap { (methodId, writes) =>
      writes.groupBy(lhs).map { (name, assignments) => (methodId -> name) -> assignments }
    }.toMap
  private lazy val callsByFullName: Map[String, Vector[Call]] =
    sharedIndex.map(_.callsByFullName).getOrElse(indexedCalls.groupBy(_.methodFullName).view.mapValues(_.toVector).toMap)
  private lazy val callsByName: Map[String, Vector[Call]] =
    sharedIndex.map(_.callsByName).getOrElse(indexedCalls.groupBy(_.name).view.mapValues(_.toVector).toMap)

  /** The textual value `symbol` provably holds at `use`, honouring overwrites whose branch is proven taken.
    * None whenever any step is unknown or ambiguous, so callers keep their own conservative behaviour.
    */
  def provenText(symbol: String, context: Method, use: AstNode): Option[String] =
    if (!AnalysisAblation.full) None
    else symbolValue(symbol, context, use, depth = 0).collect { case Text(value) => value }

  def provenInteger(expression: Expression): Option[Int] =
    if (!AnalysisAblation.full && !AnalysisAblation.literalOnly(expression)) None
    else value(expression, expression.method, depth = 0).collect {
      case Number(number) if number.isWhole && number.isValidInt => number.toInt
    }

  /** Read-only branch proof for clients that preserve feasible control-flow edges. */
  def provenCondition(control: ControlStructure): Option[Boolean] =
    if (!AnalysisAblation.full) None else condition(control)

  /** `condition` restarts the depth budget, so a branch whose condition depends (through an assignment or a call-site
    * parameter) on code guarded by that same branch used to recurse until the stack overflowed.  `execution` is a
    * deterministic function of `call`, so re-entering it for a call that is still being evaluated can only happen on
    * such a non-terminating cycle; that inner evaluation is reported as unknown, which every caller already treats
    * conservatively.  A computation that terminated before never meets a cycle, so caching results changes nothing
    * for it and only removes repeated evaluation. */
  private val executionInProgress = mutable.HashSet.empty[Long]
  private val executionCache      = mutable.HashMap.empty[Long, Option[Boolean]]

  /** The same reasoning covers `symbolValue` and `parameterValue`: both are deterministic in their arguments, and an
    * entry computed while a cycle was being cut belongs to a computation that never terminated before.
    * `parameterValue` fans out to every call site of a method name, so without these caches a common name such as
    * `get` multiplies the work at every level of the depth budget. */
  private val symbolCache    = mutable.HashMap.empty[(String, Long, Long, Int), Option[Constant]]
  private val parameterCache = mutable.HashMap.empty[(String, Long, Int), Option[Constant]]

  /** Some(true) means every enclosing branch is proven taken, Some(false) means one is proven unreachable. */
  def execution(call: Call): Option[Boolean] =
    // Without branch proofs a statement is known to run only when no branch or loop encloses it.
    if (!AnalysisAblation.full) Option.when(call.inAst.isControlStructure.isEmpty)(true)
    else executionCache.get(call.id) match {
      case Some(cached)                         => cached
      case None if executionInProgress(call.id) => None
      case None =>
        executionInProgress += call.id
        val result =
          try evaluateExecution(call)
          finally executionInProgress -= call.id
        executionCache.update(call.id, result)
        result
    }

  private def evaluateExecution(call: Call): Option[Boolean] = {
    val enclosing = call.method.controlStructure.isIf.l.filter(control => branch(control, call).nonEmpty)
    enclosing.foldLeft(Option(true)) { (state, control) =>
      (state, branch(control, call), condition(control)) match {
        case (Some(false), _, _)                                           => Some(false)
        case (_, Some(isTrueBranch), Some(value)) if value != isTrueBranch => Some(false)
        case (Some(true), Some(_), Some(_))                                => Some(true)
        case (None, Some(_), Some(_))                                      => None
        case (_, Some(_), None)                                            => None
        case (current, None, _)                                            => current
      }
    }
  }

  private def branch(control: ControlStructure, node: AstNode): Option[Boolean] = {
    val (trueIds, falseIds) = branchNodes.getOrElseUpdate(control.id,
      (descendantIds(control.whenTrue.l), descendantIds(control.whenFalse.l)))
    if (trueIds.contains(node.id)) Some(true)
    else if (falseIds.contains(node.id)) Some(false)
    else None
  }

  private def descendantIds(nodes: Iterable[AstNode]): Set[Long] =
    nodes.iterator.flatMap(node => Iterator.single(node.id) ++ node.ast.map(_.id)).toSet

  private def condition(control: ControlStructure): Option[Boolean] =
    control.condition.headOption.flatMap(value(_, control.method, depth = 0)).collect { case Truth(result) => result }

  private def value(expression: Expression, context: Method, depth: Int): Option[Constant] = {
    if (depth > 5) None
    else
      expression match {
        case literal: Literal       => literalValue(literal.code)
        case identifier: Identifier => symbolValue(identifier.name, context, identifier, depth + 1)
        case call: Call             => operatorValue(call, context, depth + 1)
        case _                      => expression.ast.isCall.headOption.flatMap(operatorValue(_, context, depth + 1))
      }
  }

  private def operatorValue(call: Call, context: Method, depth: Int): Option[Constant] = {
    lazy val operands = arguments(call).filter(_.argumentIndex > 0).sortBy(_.argumentIndex)
    def unary(f: Constant => Option[Constant]): Option[Constant] =
      operands.headOption.flatMap(value(_, context, depth)).flatMap(f)
    def binary(f: (Constant, Constant) => Option[Constant]): Option[Constant] = for {
      left   <- operands.headOption.flatMap(value(_, context, depth))
      right  <- operands.drop(1).headOption.flatMap(value(_, context, depth))
      result <- f(left, right)
    } yield result

    def nullComparison(negated: Boolean): Option[Constant] = for {
      other <- operands.find(_.code.trim != "null").orElse(operands.headOption)
      resolved <- strictPaths.value(other, context, depth)
    } yield Truth((resolved == NullValue) != negated)

    call.name match {
      case "<operator>.greaterThan"       => binary(compare(_ > 0))
      case "<operator>.greaterEqualsThan" => binary(compare(_ >= 0))
      case "<operator>.lessThan"          => binary(compare(_ < 0))
      case "<operator>.lessEqualsThan"    => binary(compare(_ <= 0))
      case "<operator>.equals" if operands.exists(_.code.trim == "null") => nullComparison(negated = false)
      case "<operator>.notEquals" if operands.exists(_.code.trim == "null") => nullComparison(negated = true)
      case "<operator>.equals"            => binary(compareEquality(negated = false))
      case "<operator>.notEquals"         => binary(compareEquality(negated = true))
      case "<operator>.logicalNot"        => unary { case Truth(v) => Some(Truth(!v)); case _ => None }
      case "<operator>.logicalAnd"        => binary(boolean(_ && _))
      case "<operator>.logicalOr"         => binary(boolean(_ || _))
      case "isEmpty" if call.methodFullName.startsWith("java.lang.String.") =>
        arguments(call).find(_.argumentIndex == 0).flatMap(strictPaths.value(_, context, depth))
          .collect { case Text(text) => Truth(text.isEmpty) }
      // A successful invocation of String.getBytes returns a non-null byte array.
      case "getBytes" if call.methodFullName.startsWith("java.lang.String.") => Some(NonNullValue)
      case _                              => None
    }
  }

  private def compareEquality(negated: Boolean)(left: Constant, right: Constant): Option[Constant] =
    Option.when(left != NonNullValue && right != NonNullValue)(Truth((left == right) != negated))

  private def compare(predicate: Int => Boolean)(left: Constant, right: Constant): Option[Constant] =
    (left, right) match {
      case (Number(a), Number(b)) => Some(Truth(predicate(a.compare(b))))
      case _                      => None
    }

  private def boolean(f: (Boolean, Boolean) => Boolean)(left: Constant, right: Constant): Option[Constant] =
    (left, right) match {
      case (Truth(a), Truth(b)) => Some(Truth(f(a, b)))
      case _                    => None
    }

  private def symbolValue(name: String, context: Method, use: AstNode, depth: Int): Option[Constant] = {
    val key = (name, context.id, use.id, depth)
    symbolCache.get(key) match {
      case Some(cached) => cached
      case None =>
        val result = computeSymbolValue(name, context, use, depth)
        symbolCache.update(key, result)
        result
    }
  }

  private def computeSymbolValue(name: String, context: Method, use: AstNode, depth: Int): Option[Constant] = {
    val possibleAssignments = assignmentsByMethodAndName
      .getOrElse(context.id -> name, Vector.empty)
      .filter(assignment => summon[Ordering[(Int, Int, Long)]].lt(order(assignment), order(use)))
      .filterNot(assignment => execution(assignment).contains(false))
    possibleAssignments.lastOption match {
      case Some(assignment) if execution(assignment).contains(true) => rhs(assignment).flatMap(value(_, context, depth))
      case Some(_)                                                  => None
      case None                                                     => parameterValue(name, context, depth)
    }
  }

  private def parameterValue(name: String, context: Method, depth: Int): Option[Constant] = {
    val key = (name, context.id, depth)
    parameterCache.get(key) match {
      case Some(cached) => cached
      case None =>
        val result = computeParameterValue(name, context, depth)
        parameterCache.update(key, result)
        result
    }
  }

  private def computeParameterValue(name: String, context: Method, depth: Int): Option[Constant] =
    context.parameter.nameExact(name).headOption.flatMap { parameter =>
      val exact = callsByFullName.getOrElse(context.fullName, Vector.empty)
      val sites = if (exact.nonEmpty || strictCallSites) exact else callsByName.getOrElse(context.name, Vector.empty)
      val lookup = (exact.nonEmpty || strictCallSites,
        if (exact.nonEmpty || strictCallSites) context.fullName else context.name, parameter.index)
      val (all, supplied) = callerInputs.getOrElseUpdate(lookup, {
        val all = sites.map(site => site -> arguments(site).find(_.argumentIndex == parameter.index))
        (all, all.filter(_._2.nonEmpty))
      })
      // Non-strict mode has always ignored missing arguments; index that subset once instead of rescanning it
      // for every same-named callee and depth. Strict mode retains missing arguments as unknown inputs.
      val relevant = if (strictCallSites) all else supplied
      agreeingCallerValue(relevant.iterator.filterNot(entry => enclosingMethod(entry._1).id == context.id), depth)
    }

  /** Once two known caller values disagree, no additional caller can make the value unique.
    * Strict mode likewise cannot recover from an unknown input. Depth and caller selection are unchanged.
    */
  private def agreeingCallerValue(sites: Iterator[CallerInput], depth: Int): Option[Constant] = {
    var agreed: Option[Constant] = None
    while (sites.hasNext) {
      val (site, expression) = sites.next()
      val input = expression.flatMap(value(_, enclosingMethod(site), depth + 1))
      input match {
        case None if strictCallSites => return None
        case Some(current) =>
          if (agreed.exists(_ != current)) return None
          agreed = Some(current)
        case _ => ()
      }
    }
    agreed
  }

  private def literalValue(code: String): Option[Constant] =
    literalCache.getOrElseUpdate(code, parseLiteral(code))

  private val NumericLiteral = "[-+]?\\d+(?:\\.\\d+)?".r
  private def parseLiteral(code: String): Option[Constant] = {
    val raw = code.trim
    val normalized = raw.replace("_", "").stripSuffix("L").stripSuffix("l")
    raw match {
      case "true"                                           => Some(Truth(true))
      case "false"                                          => Some(Truth(false))
      case "null"                                           => Some(NullValue)
      case _ if NumericLiteral.matches(normalized) => Some(Number(BigDecimal(normalized)))
      case _
          if raw.length >= 2 && Set('"', '\'').contains(raw.head) && raw.last == raw.head =>
        Some(Text(raw.substring(1, raw.length - 1)))
      case _ => None
    }
  }

  private def lhs(assignment: Call): String =
    arguments(assignment).find(_.argumentIndex == 1).map(_.code.trim).getOrElse("")

  private def rhs(assignment: Call): Option[Expression] =
    arguments(assignment).find(_.argumentIndex == 2)

  private def unique(values: Iterable[Constant]): Option[Constant] = values.toVector.distinct match {
    case Vector(single) => Some(single)
    case _              => None
  }

  private def order(node: AstNode): (Int, Int, Long) =
    positionCache.getOrElseUpdate(node.id,
      (node.lineNumber.getOrElse(Int.MaxValue), node.columnNumber.getOrElse(Int.MaxValue), node.id))
}

private object JavaConstantPathEvaluator {
  sealed trait Constant
  final case class Number(value: BigDecimal) extends Constant
  final case class Text(value: String) extends Constant
  final case class Truth(value: Boolean) extends Constant
  case object NullValue extends Constant
  case object NonNullValue extends Constant
}
