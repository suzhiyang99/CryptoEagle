package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import scala.collection.mutable

/** Local, versioned value identities and bounded CFG proofs. No inference from variable or application names. */
private[standalone] final class SecurityFlowContext(cpg: Cpg, sharedIndex: Option[JavaValueIndex] = None) {
  private val index = sharedIndex.getOrElse(new JavaValueIndex(cpg))
  val paths = index.paths
  lazy val calls = index.calls
  lazy val callsByMethod = calls.groupBy(_.method.id)
  private lazy val writes = index.writesByMethod
  private val fieldOrigins = mutable.HashMap.empty[(Long, String, String, Long), Long]
  def arg(call: Call, position: Int): Option[Expression] = index.arg(call, position)
  def receiver(call: Call): Option[Expression] = arg(call, 0).flatMap {
    case access: Call if access.name == "<operator>.fieldAccess" &&
      access.ast.isFieldIdentifier.canonicalName.headOption.contains(call.name) => arg(access, 1)
    case e => Some(e)
  }
  def order(n: AstNode): (Int, Int, Long) =
    (n.lineNumber.getOrElse(Int.MaxValue), n.columnNumber.getOrElse(Int.MaxValue), n.id)
  def before(a: AstNode, b: AstNode): Boolean = summon[Ordering[(Int, Int, Long)]].lt(order(a), order(b))
  def active(c: Call): Boolean = !paths.execution(c).contains(false)

  private def definitions(i: Identifier): Vector[Call] = writes.getOrElse(i.method.id, Vector.empty)
    .filter(a => before(a, i) && active(a) && arg(a, 1).exists(_.code.trim == i.name)).sortBy(order)

  private def definition(i: Identifier): Option[Option[Expression]] = definitions(i).lastOption match {
    case None => Some(None)
    case Some(a) if paths.execution(a).contains(true) => Some(arg(a, 2))
    case _ => None
  }

  def origin(e: Expression, depth: Int = 0, seen: Set[Long] = Set.empty): Option[Long] = {
    if (depth > 16 || seen(e.id)) return None
    def follow(x: Expression) = origin(x, depth + 1, seen + e.id)
    e match {
      case i: Identifier => definition(i).flatMap {
        case Some(value) => follow(value)
        case None => i.refOut.headOption.map(_.id).orElse(
          i.method.parameter.nameExact(i.name).headOption.map(_.id))
      }
      case c: Call if c.name == "<operator>.cast" => c.argument.l.lastOption.flatMap(follow)
      case b: Block => b.astChildren.collect { case x: Expression => x }.lastOption.flatMap(follow)
      case c: Call if Set("getBytes", "encode", "decode")(c.name) => receiver(c).flatMap(follow)
      case c: Call if c.name == "<operator>.fieldAccess" =>
        def key(access: Call): Option[(String, String)] = for {
          base <- arg(access, 1)
          owner <- base match {
            case t: TypeRef => Some("type:" + t.typeFullName)
            case _ => follow(base).map(id => "value:" + id)
          }
          field <- access.ast.isFieldIdentifier.canonicalName.headOption
        } yield owner -> field
        key(c).flatMap { case (owner, field) =>
          val local = writes.getOrElse(c.method.id, Vector.empty).filter(a => before(a, c) && active(a) &&
            arg(a, 1).collect { case access: Call if access.name == "<operator>.fieldAccess" => access }
              .filter(_.ast.isFieldIdentifier.canonicalName.headOption.contains(field))
              .flatMap(key).contains(owner -> field)).sortBy(order)
          local.lastOption match {
            case Some(write) if paths.execution(write).contains(true) => arg(write, 2).flatMap(follow)
            case Some(_) => None
            case None => Some(fieldOrigins.getOrElseUpdate((c.method.id, owner, field, 0L), c.id))
          }
        }
      case _ => Some(e.id)
    }
  }
  def same(a: Expression, b: Expression): Boolean = origin(a).exists(id => origin(b).contains(id))

  /** Three-argument updates cover the complete array only with a proven zero offset and its own full length. */
  def wholeInput(call: Call): Option[Expression] = {
    val args = call.argument.l.filter(_.argumentIndex > 0)
    if (args.size == 1) args.headOption
    else if (args.size == 3 && arg(call, 2).exists(_.code.trim == "0")) arg(call, 3).collect {
      case length: Call if length.name == "<operator>.fieldAccess" &&
        length.ast.isFieldIdentifier.canonicalName.headOption.contains("length") &&
        arg(length, 1).exists(a => arg(call, 1).exists(same(a, _))) => args.head
    }
    else None
  }

  /** JCA doFinal/reset/init all end the preceding MAC accumulation. Receiver aliases retain their value version. */
  def javaMacInputs(tag: Call): Vector[Expression] = {
    val receiver = arg(tag, 0)
    def owned(c: Call) = receiver.exists(r => arg(c, 0).exists(same(r, _)))
    val local = callsByMethod.getOrElse(tag.method.id, Vector.empty)
      .filter(c => c.methodFullName.startsWith("javax.crypto.Mac.") && before(c, tag) && active(c) && owned(c))
    val resets = local.filter(c => Set("init", "reset", "doFinal")(c.name) && mayReach(c, tag).getOrElse(true))
    val updates = local.filter(c => c.name == "update" &&
      resets.forall(before(_, c)) && dominates(c, tag))
    (if (tag.argument.count(_.argumentIndex > 0) == 1) arg(tag, 1).toVector else Vector.empty) ++
      updates.flatMap(wholeInput)
  }

  /** Full-value dependencies: lengths, indices, predicates and partial slices cannot authenticate the whole input. */
  def depends(e: Expression, targets: Set[Long], depth: Int = 0, seen: Set[Long] = Set.empty): Boolean = {
    if (targets(e.id) || origin(e).exists(targets)) return true
    if (depth > 16 || seen(e.id)) return false
    def follow(x: Expression) = depends(x, targets, depth + 1, seen + e.id)
    e match {
      case i: Identifier => definition(i).flatten.exists(follow)
      case c: Call if c.name == "<operator>.conditional" =>
        val branches = c.argument.l.filter(_.argumentIndex > 1)
        arg(c, 1).flatMap(boolValue(_, c, false)) match {
          case Some(truth) => arg(c, if (truth) 2 else 3).exists(follow)
          case None => branches.nonEmpty && branches.forall(follow)
        }
      case c: Call if Set("<operator>.cast", "<operator>.addition", "<operator>.arrayInitializer",
        "<operator>.assignment", "getBytes", "encode", "decode", "encodeToString", "b64encode", "toString")(c.name) =>
        c.argument.l.exists(follow)
      case b: Block => b.astChildren.collect { case x: Expression => x }.lastOption.exists(follow)
      case _ => false
    }
  }

  def boolValue(e: Expression, result: Call, assumed: Boolean, depth: Int = 0, seen: Set[Long] = Set.empty,
    bindings: Map[Long, Option[Boolean]] = Map.empty, failedChecks: Set[Long] = Set.empty): Option[Boolean] = {
    if (e.id == result.id) return Some(assumed)
    if (failedChecks(e.id)) return Some(false)
    if (depth > 16 || seen(e.id)) return None
    def follow(x: Expression) = boolValue(x, result, assumed, depth + 1, seen + e.id, bindings, failedChecks)
    e match {
      case l: Literal if Set("true", "false")(l.code.toLowerCase) => Some(l.code.equalsIgnoreCase("true"))
      case i: Identifier => i.refOut.headOption.map(_.id).filter(bindings.contains) match {
        case Some(id) => bindings(id)
        case None => definition(i).flatten.flatMap(follow)
      }
      case c: Call =>
        val values = c.argument.l.filter(_.argumentIndex > 0).map(follow)
        c.name match {
          case "<operator>.logicalNot" => values.headOption.flatten.map(!_)
          case "<operator>.logicalAnd" =>
            if (values.contains(Some(false))) Some(false) else Option.when(values.nonEmpty && values.forall(_.contains(true)))(true)
          case "<operator>.logicalOr" =>
            if (values.contains(Some(true))) Some(true) else Option.when(values.nonEmpty && values.forall(_.contains(false)))(false)
          case "<operator>.equals" | "<operator>.notEquals" if values.size == 2 =>
            for (a <- values.head; b <- values.last) yield if (c.name == "<operator>.equals") a == b else a != b
          case "<operator>.assignment" => values.lastOption.flatten
          case _ => None
        }
      case _ => None
    }
  }

  private val dominationCache = mutable.HashMap.empty[(Long, Long), Boolean]
  private def throws(c: CfgNode): Boolean = c match {
    case call: Call => Set("<operator>.throw", "<operator>.raise", "throw", "raise")(call.name) ||
      (call.name.startsWith("<operator>.") && call.code.trim.startsWith("throw "))
    case control: ControlStructure => Set("THROW", "RAISE")(control.controlStructureType)
    case _ => false
  }
  def mayReach(start: CfgNode, use: CfgNode): Option[Boolean] = {
    val seen = mutable.HashSet.empty[Long]
    val todo = mutable.Stack[CfgNode](start)
    var incomplete = false
    while (todo.nonEmpty && seen.size < 20000) {
      val n = todo.pop()
      if (n.id == use.id) return Some(true)
      if (seen.add(n.id) && n.method.id == use.method.id) {
        if (throws(n)) incomplete ||= n.inAst.isControlStructure.exists(_.controlStructureType == "TRY")
        else n.cfgNext.foreach(todo.push(_))
      }
    }
    if (todo.nonEmpty || incomplete) None else Some(false)
  }
  def dominates(via: CfgNode, use: CfgNode): Boolean = dominationCache.getOrElseUpdate((via.id, use.id), {
    val seen = mutable.HashSet.empty[Long]
    val todo = mutable.Stack[CfgNode](use)
    var found = false
    while (todo.nonEmpty && seen.size < 20000 && !found) {
      val node = todo.pop()
      if (node.id == via.id) found = true
      else if (seen.add(node.id)) node.dominatedBy.foreach(todo.push(_))
    }
    found
  })

  /** Reachability after forcing this Boolean verification to fail. Boolean branch constraints are retained;
    * unsupported predicates and exhaustion remain unknown, never proof of enforcement or a violating path.
    */
  def failureReaches(check: Call, use: CfgNode, failedChecks: Set[Long] = Set.empty): Option[Boolean] = {
    if (check.method.id != use.method.id) return None
    val decisions = check.method.controlStructure.l.filter(_.controlStructureType == "IF").flatMap { control =>
      control.condition.headOption.map(condition => condition.id -> (condition,
        control.whenTrue.l.flatMap(n => Iterator(n.id) ++ n.ast.map(_.id)).toSet))
    }.toMap
    val unsupported = check.method.controlStructure.l.filterNot(_.controlStructureType == "IF")
      .flatMap(_.condition).map(_.id).toSet
    type State = Map[Long, Option[Boolean]]
    def assume(e: Expression, truth: Boolean, state: State, depth: Int = 0): Option[State] = {
      if (depth > 16) return None
      boolValue(e, check, false, bindings = state, failedChecks = failedChecks) match {
        case Some(value) => Option.when(value == truth)(state)
        case None => e match {
          case i: Identifier => i.refOut.headOption.map(n => state.updated(n.id, Some(truth)))
          case c: Call if c.name == "<operator>.logicalNot" => arg(c, 1).flatMap(assume(_, !truth, state, depth + 1))
          case c: Call if Set("<operator>.logicalAnd", "<operator>.logicalOr")(c.name) =>
            val operands = c.argument.l.filter(_.argumentIndex > 0)
            val all = (c.name == "<operator>.logicalAnd") == truth
            if (all) operands.foldLeft(Option(state))((s, operand) => s.flatMap(assume(operand, truth, _, depth + 1)))
            else {
              val unresolved = operands.filter(e => boolValue(e, check, false, bindings = state, failedChecks = failedChecks).isEmpty)
              if (unresolved.size == 1) assume(unresolved.head, truth, state, depth + 1) else None
            }
          case c: Call if Set("<operator>.equals", "<operator>.notEquals")(c.name) =>
            val operands = c.argument.l.filter(_.argumentIndex > 0)
            val known = operands.flatMap(e => boolValue(e, check, false, bindings = state, failedChecks = failedChecks).map(e -> _))
            val unknown = operands.filter(e => boolValue(e, check, false, bindings = state, failedChecks = failedChecks).isEmpty)
            if (known.size == 1 && unknown.size == 1)
              assume(unknown.head, if ((c.name == "<operator>.equals") == truth) known.head._2 else !known.head._2, state, depth + 1)
            else None
          case _ => None
        }
      }
    }
    var initial: Option[State] = Some(Map.empty)
    check.inAst.isControlStructure.filter(_.controlStructureType == "IF").foreach { control =>
      val trueBranch = control.whenTrue.l.exists(n => n.id == check.id || n.ast.exists(_.id == check.id))
      val falseBranch = control.whenFalse.l.exists(n => n.id == check.id || n.ast.exists(_.id == check.id))
      if (trueBranch || falseBranch) initial = initial.flatMap(s => control.condition.headOption.flatMap(assume(_, trueBranch, s)))
    }
    check.inAst.isCall.filter(c => Set("<operator>.logicalAnd", "<operator>.logicalOr")(c.name)).foreach { parent =>
      val operands = parent.argument.l.filter(_.argumentIndex > 0).sortBy(_.argumentIndex)
      val preceding = operands.takeWhile(e => e.id != check.id && !e.ast.exists(_.id == check.id))
      preceding.foreach(e => initial = initial.flatMap(assume(e, parent.name == "<operator>.logicalAnd", _)))
    }
    if (initial.isEmpty) return None
    val seen = mutable.HashSet.empty[(Long, Vector[(Long, Option[Boolean])])]
    val todo = mutable.Stack.empty[(CfgNode, State)]
    todo.push(check -> initial.get)
    var incomplete = false
    while (todo.nonEmpty && seen.size < 20000) {
      val (node, state) = todo.pop()
      if (node.id == use.id) return Some(true)
      if (seen.add(node.id -> state.toVector.sortBy(_._1)) && node.method.id == check.method.id) {
        val after = node match {
          case assignment: Call if assignment.name == "<operator>.assignment" =>
            val lhs = arg(assignment, 1).collect { case i: Identifier => i }.flatMap(_.refOut.headOption).map(_.id)
            val value = arg(assignment, 2).flatMap(e => boolValue(e, check, false, bindings = state, failedChecks = failedChecks))
            lhs.filter(id => value.nonEmpty || state.contains(id)).map(id => state.updated(id, value)).getOrElse(state)
          case _ => state
        }
        val throwing = throws(node)
        // Joern's source CFG can give throw a lexical fallthrough edge. A local handler requires exception
        // dispatch knowledge; retain unknown rather than certifying that the failure escapes the method.
        if (throwing && node.inAst.isControlStructure.exists(c => c.controlStructureType == "TRY")) incomplete = true
        if (unsupported(node.id)) incomplete = true
        val next = if (throwing || unsupported(node.id)) Nil else node.cfgNext.l
        val selected = decisions.get(node.id) match {
          case Some((condition, trueIds)) => next.flatMap { n =>
            val branch = trueIds(n.id)
            boolValue(condition, check, assumed = false, bindings = after, failedChecks = failedChecks) match {
              case Some(truth) => Option.when(truth == branch)(n -> after)
              case None => assume(condition, branch, after) match {
                case Some(refined) => Some(n -> refined)
                case None => incomplete = true; None
              }
            }
          }
          case None => next.map(n => n -> after)
        }
        if (java.lang.Boolean.getBoolean("crypto.context.trace"))
          System.err.println(s"context.cfg ${node.id} ${node.label} ${node.code.take(100)} decision=${decisions.get(node.id).map(x => boolValue(x._1, check, false, bindings = after))} next=" +
            selected.map((n, _) => s"${n.id}:${n.code.take(50)}").mkString(" | "))
        selected.foreach(todo.push(_))
      }
    }
    if (todo.nonEmpty || incomplete) None else Some(false)
  }
}
