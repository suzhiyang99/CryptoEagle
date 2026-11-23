package org.codeminers.standalone

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.enrichment.JavaConstantPathEvaluator
import scala.collection.mutable

/** Source projection proofs for the exporter only; no detector verdicts or benchmark names. */
private[standalone] final class SliceLineEvidence(cpg: Cpg) {
  val paths = new JavaConstantPathEvaluator(cpg, strictCallSites = true)
  private val allCalls = cpg.call.l.toVector
  private val assignments = allCalls.filter(_.name == "<operator>.assignment")
  private val controls = cpg.controlStructure.l.filter(_.controlStructureType == "IF")
  private def descendants(ns: Iterable[AstNode]): Set[Long] = ns.iterator.flatMap(n => Iterator(n.id) ++ n.ast.map(_.id)).toSet
  private val decisions = controls.flatMap(c => for {
    test <- c.condition.headOption
    truth <- paths.provenCondition(c)
  } yield test.id -> (truth, descendants(c.whenTrue.l))).toMap
  private val inactive = controls.flatMap(c => paths.provenCondition(c).toVector.flatMap { truth =>
    descendants(if (truth) c.whenFalse.l else c.whenTrue.l)
  }).toSet
  def reachable(c: Call): Boolean = !inactive(c.id) && !paths.execution(c).contains(false)
  def arg(c: Call, i: Int): Option[Expression] = c.argument.l.find(_.argumentIndex == i)
  def symbol(e: Expression): Option[Long] = e match {
    case i: Identifier => i.refOut.headOption.map(_.id)
    case _ => None
  }
  def order(n: AstNode): (Int, Int, Long) = (n.lineNumber.getOrElse(Int.MaxValue), n.columnNumber.getOrElse(Int.MaxValue), n.id)
  def before(a: AstNode, b: AstNode): Boolean = summon[Ordering[(Int, Int, Long)]].lt(order(a), order(b))
  private def next(n: CfgNode): Vector[CfgNode] = {
    val edges = n.cfgNext.l.toVector.filterNot(x => inactive(x.id))
    decisions.get(n.id) match {
      case Some((truth, trueIds)) => edges.filter(x => trueIds(x.id) == truth)
      case None => edges
    }
  }
  /** All feasible CFG paths from `start` to `use` must pass `via`. Unknown branches remain both ways.
    * Exhausting the bound means no proof. This also preserves zero-iteration loop paths.
    */
  def mustPass(start: Call, via: Call, use: Call): Boolean = {
    if (start.method.id != via.method.id || use.method.id != via.method.id || !reachable(via) ||
      !before(start, via) || (!before(via, use) && via.id != use.id)) return false
    if (via.id == use.id) return true
    def reaches(blocked: Option[Long]): Option[Boolean] = {
      val seen = mutable.HashSet.empty[Long]
      val todo = mutable.Stack.empty[CfgNode]
      next(start).foreach(todo.push(_))
      var steps = 0
      while (todo.nonEmpty && steps < 20000) {
        val n = todo.pop(); steps += 1
        if (!seen(n.id) && !blocked.contains(n.id)) {
          if (n.id == use.id) return Some(true)
          seen += n.id
          if (n.method.id == use.method.id) next(n).foreach(todo.push(_))
        }
      }
      if (todo.nonEmpty) None else Some(false)
    }
    reaches(None).contains(true) && reaches(Some(via.id)).contains(false)
  }
  def inactiveLines(file: String): Set[Int] = allCalls.filter(_.file.name.headOption.contains(file))
    .flatMap(c => c.lineNumber.map(_ -> c)).groupBy(_._1).collect {
      case (line, calls) if calls.forall((_, c) => !reachable(c)) => line
    }.toSet
  def killedLines(use: Call): Set[Int] = {
    val local = assignments.filter(a => a.method.id == use.method.id && reachable(a) && before(a, use))
    local.flatMap { older =>
      val overwritten = arg(older, 1).flatMap(symbol).exists { id =>
        local.exists(later => later.id != older.id && arg(later, 1).flatMap(symbol).contains(id) && mustPass(older, later, use))
      }
      // A line slice cannot delete just one of several statements/conditions on that line.
      val ownNodes = older.ast.map(_.id).toSet + older.id
      val sharedLine = allCalls.exists(c => c.method.id == older.method.id && c.lineNumber == older.lineNumber && !ownNodes(c.id))
      if (overwritten && !sharedLine) older.lineNumber else None
    }.toSet
  }
  case class Overwrite(buffer: String, definitionLine: Int, writerLine: Int, replacement: String)
  case class Declaration(line: Int, name: String, typeFullName: String, replacement: String)
  def declarations(calls: Iterable[Call], killed: Set[Int]): Vector[Declaration] = {
    calls.toVector.flatMap(c => c.argument.l.flatMap(e =>
      Iterator.single(e).collect { case i: Identifier => i }.toVector ++ e.ast.isIdentifier.l))
      .flatMap(_.refOut.collect { case l: Local => l }).distinct.flatMap { local =>
        local.lineNumber.filter(killed).filter(_ => !Set("", "ANY", "<unknown>")(local.typeFullName)).map { line =>
          val declaredType = local.typeFullName.split('.').last
          Declaration(line, local.name, local.typeFullName, s"$declaredType ${local.name};")
        }
      }
  }
  private def identifiers(call: Call): Set[Long] = call.argument.l.flatMap(e =>
    Iterator.single(e).collect { case i: Identifier => i }.toVector ++ e.ast.isIdentifier.l
  ).flatMap(symbol).toSet
  private def assignedBy(c: Call): Option[Long] = c.inAst.isCall.find(a => a.name == "<operator>.assignment" &&
    arg(a, 2).exists(e => e.id == c.id || e.ast.exists(_.id == c.id))).flatMap(a => arg(a, 1).flatMap(symbol))
  def outputSymbols(c: Call): Set[Long] = {
    val buffer = if (c.name == "nextBytes") arg(c, 1).flatMap(symbol).toSet else Set.empty[Long]
    val state = if (Set("setSeed", "<init>")(c.name)) arg(c, 0).flatMap(symbol).toSet else Set.empty[Long]
    buffer ++ state ++ assignedBy(c).toSet
  }
  def dependsOn(producer: Call, consumer: Call): Boolean = producer.method.id == consumer.method.id &&
    before(producer, consumer) && reachable(producer) && reachable(consumer) &&
    outputSymbols(producer).intersect(identifiers(consumer)).nonEmpty

  /** A full random fill and its actual consumer share one buffer version. This permits adding consumer
    * context to the producer slice, without removing old values or changing the ANY voting policy.
    */
  def fullRandomFillReaches(writer: Call, use: Call): Boolean = {
    if (writer.name != "nextBytes" || !writer.methodFullName.startsWith("java.security.SecureRandom.nextBytes:") ||
      !dependsOn(writer, use)) return false
    val buffer = arg(writer, 1).flatMap(symbol)
    if (buffer.isEmpty) return false
    val definitions = assignments.filter(a => a.method.id == writer.method.id && before(a, writer) &&
      reachable(a) && arg(a, 1).flatMap(symbol) == buffer).sortBy(order)
    val intervening = allCalls.filter(c => c.method.id == writer.method.id && before(writer, c) && before(c, use) && reachable(c))
    val changed = intervening.exists { c =>
      (c.name == "<operator>.assignment" && (arg(c, 1).flatMap(symbol) == buffer ||
        arg(c, 1).exists(_.ast.isIdentifier.l.exists(i => symbol(i) == buffer)) || arg(c, 2).flatMap(symbol) == buffer)) ||
        (!c.name.startsWith("<operator>.") && c.argument.l.exists(e =>
          e.ast.isIdentifier.l.exists(i => symbol(i) == buffer) || (e match { case i: Identifier => symbol(i) == buffer; case _ => false })))
    }
    !changed && definitions.lastOption.exists(d => mustPass(d, writer, use))
  }

  /** Only a typed SecureRandom full-buffer overwrite can justify an array-value projection. */
  def overwrites(use: Call, selected: Set[Int]): Vector[Overwrite] = {
    allCalls.filter(w => w.method.id == use.method.id && w.methodFullName.startsWith("java.security.SecureRandom.nextBytes:") &&
      reachable(w) && (before(w, use) || w.id == use.id)).flatMap { writer =>
      arg(writer, 1).collect { case i: Identifier => i }.toVector.flatMap { buffer =>
        val defs = assignments.filter(a => a.method.id == writer.method.id && before(a, writer) &&
          arg(a, 1).flatMap(symbol) == symbol(buffer) && symbol(buffer).nonEmpty && reachable(a))
        defs.sortBy(order).lastOption.toVector.flatMap { definition =>
          val initializers = arg(definition, 2).toVector.flatMap(e =>
            e match {
              case c: Call if c.name == "<operator>.arrayInitializer" => Vector(c)
              case _ => e.ast.isCall.nameExact("<operator>.arrayInitializer").l.toVector
            })
          val earlierReads = allCalls.exists(c => c.method.id == writer.method.id && before(definition, c) && before(c, writer) &&
            c.lineNumber.exists(selected) && identifiers(c).contains(symbol(buffer).get) && c.id != definition.id)
          if (!mustPass(definition, writer, use) || initializers.size != 1 || earlierReads) Vector.empty
          else {
            val size = initializers.head.argument.l.count(_.argumentIndex > 0)
            val declaration = buffer.refOut.headOption.collect { case l: Local => l }.exists(_.lineNumber == definition.lineNumber)
            for { dl <- definition.lineNumber.toVector; wl <- writer.lineNumber.toVector } yield
              Overwrite(buffer.name, dl, wl, s"${if (declaration) "byte[] " else ""}${buffer.name} = new byte[$size];")
          }
        }
      }
    }
  }
  def randomContext(use: Call): Vector[Call] = allCalls.filter(w => w.method.id == use.method.id && reachable(w) &&
    w.methodFullName.startsWith("java.security.SecureRandom.nextBytes:") && (dependsOn(w, use) || w.id == use.id))
}
