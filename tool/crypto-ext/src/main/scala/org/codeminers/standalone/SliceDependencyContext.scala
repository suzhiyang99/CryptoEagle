package org.codeminers.standalone

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.enrichment.JavaValueIndex
import scala.collection.mutable

/** Bounded source evidence for constructor fields and ordered static initialization.
  * Keeps the original writes, call arguments and guards; never substitutes a class-wide constant for an object.
  * An ambiguous construction or unproven runtime setter cannot establish an effective field definition.
  */
private[standalone] final class SliceDependencyContext(cpg: Cpg, evidence: SliceLineEvidence) {
  private val index = new JavaValueIndex(cpg)
  private val methods = cpg.method.filterNot(_.isExternal).l.toVector.groupBy(_.name)
  private def arg(c: Call, n: Int) = index.arg(c, n)
  private def file(n: AstNode) = n.file.name.headOption.getOrElse("")
  private def owner(m: Method) = m.fullName.takeWhile(_ != ':').stripSuffix("." + m.name)
  private def matches(c: Call, m: Method): Boolean = c.methodFullName == m.fullName ||
    (c.methodFullName.contains("<unresolvedSignature>") &&
      c.methodFullName.takeWhile(_ != ':').stripSuffix("." + c.name) == owner(m) &&
      c.argument.count(_.argumentIndex > 0) == m.parameter.count(_.index > 0))
  private def sites(m: Method, allowed: Set[String]): Vector[Call] =
    index.callsByName.getOrElse(m.name, Vector.empty).filter(c => allowed(file(c)) &&
      c.method.id != m.id && evidence.reachable(c) && matches(c, m))
  private def field(c: Call): Option[(String, String)] = for {
    base <- arg(c, 1)
    typ <- base match {
      case i: Identifier => Some(i.typeFullName)
      case t: TypeRef => Some(t.typeFullName)
      case _ => None
    }
    name <- c.ast.isFieldIdentifier.canonicalName.headOption
  } yield typ.takeWhile(_ != '<') -> name
  private lazy val fieldWrites = index.writes.flatMap(a => arg(a, 1).collect {
    case access: Call if access.name == "<operator>.fieldAccess" => access
  }.flatMap(field).map(_ -> a)).groupMap(_._1)(_._2)
  private def before(a: AstNode, b: AstNode) = summon[Ordering[(Int, Int, Long)]].lt(evidence.order(a), evidence.order(b))

  /** Runtime writes count only on the same method sequence, or before the read's invocation in one caller. */
  private def precedes(write: Call, read: Call, allowed: Set[String]): Boolean = {
    if (write.method.id == read.method.id) before(write, read) && index.paths.execution(write).contains(true)
    else sites(write.method, allowed).exists { w =>
      index.paths.execution(w).contains(true) && index.paths.execution(write).contains(true) &&
        ((w.method.id == read.method.id && before(w, read)) || sites(read.method, allowed).exists(r =>
          w.method.id == r.method.id && before(w, r) && index.paths.execution(r).contains(true)))
    }
  }

  def lines(calls: Iterable[Call], allowed: Set[String]): Map[String, Vector[Int]] = {
    val nodes = mutable.LinkedHashMap.empty[Long, AstNode]
    val seen = mutable.HashSet.empty[Long]
    var fieldReached = false
    var exhausted = false
    def add(n: AstNode): Unit = if (allowed(file(n))) {
      if (nodes.size >= 256) exhausted = true else nodes.getOrElseUpdate(n.id, n)
    }
    def guards(n: AstNode): Unit = n.inAst.isControlStructure.foreach { control =>
      add(control)
      control.condition.foreach(c => walk(c, 1))
    }
    def retain(n: AstNode): Unit = {
      add(n)
      n match { case c: CfgNode => add(c.method); case _ => () }
      guards(n)
    }
    def write(a: Call, depth: Int): Unit = {
      retain(a)
      arg(a, 2).foreach(walk(_, depth + 1))
    }
    def walk(e: Expression, depth: Int): Unit = {
      if (depth > 16 || seen.size >= 512) { exhausted = true; return }
      if (!allowed(file(e)) || !seen.add(e.id)) return
      e match {
        case i: Identifier if i.name != "this" =>
          val symbol = i.refOut.headOption.map(_.id)
          val writes = index.writesByMethod.getOrElse(i.method.id, Vector.empty).filter(a =>
            before(a, i) && !a.ast.exists(_.id == i.id) && evidence.reachable(a) && arg(a, 1).exists {
              case lhs: Identifier => symbol.nonEmpty && lhs.refOut.headOption.map(_.id) == symbol
              case _ => false
            }).sortBy(evidence.order)
          // A proven later assignment kills prior values; unknown branch writes retain their alternatives.
          val lastDefinite = writes.lastIndexWhere(a => index.paths.execution(a).contains(true))
          if (writes.nonEmpty) writes.drop(math.max(lastDefinite, 0)).foreach(write(_, depth))
          else i.method.parameter.nameExact(i.name).headOption.foreach { p =>
            val callers = sites(p.method, allowed)
            if (callers.size <= 16) callers.foreach { caller =>
              retain(caller)
              arg(caller, p.index).foreach(walk(_, depth + 1))
            }
          }
        case c: Call if c.name == "<operator>.fieldAccess" =>
          field(c).foreach { key =>
            val definitions = fieldWrites.getOrElse(key, Vector.empty)
              .filter(a => allowed(file(a)) && evidence.reachable(a))
            val static = arg(c, 1).exists(_.isInstanceOf[TypeRef]) ||
              definitions.exists(a => a.method.name == "<clinit>")
            val constructors = methods.getOrElse("<init>", Vector.empty).filter(m =>
              owner(m) == key._1 && allowed(file(m))).flatMap(sites(_, allowed))
            // Do not mix constructor arguments of different object instances.
            val unambiguousObject = static || constructors.size == 1
            val runtime = definitions.filterNot(a => JavaFrontend.isInitializer(a.method.name))
            val eligible = definitions.filter(a => JavaFrontend.isInitializer(a.method.name) || precedes(a, c, allowed))
            if (eligible.nonEmpty && unambiguousObject && !runtime.exists(a => !precedes(a, c, allowed))) {
              fieldReached = true
              retain(c)
              eligible.foreach { a =>
                write(a, depth)
                sites(a.method, allowed).foreach { site => retain(site) }
              }
            }
          }
        // Array.toString returns an identity string, not its contents. In particular a following
        // numeric parser cannot consume the array's constant characters as a numeric seed.
        case c: Call if c.name == "toString" && arg(c, 0).exists(_.evalType.l.exists(_.endsWith("[]"))) => ()
        case c: Call if c.name == "<operator>.assignment" => write(c, depth)
        case c: Call =>
          val children = c.argument.l.filter(_.argumentIndex >= 0)
          children.foreach(walk(_, depth + 1))
          val targets = methods.getOrElse(c.name, Vector.empty).filter(m => allowed(file(m)) && matches(c, m))
          if (targets.size == 1 && c.name != "<init>") {
            targets.head.ast.isReturn.astChildren.collect { case x: Expression => x }.foreach(walk(_, depth + 1))
          }
        case b: Block => b.astChildren.collect { case x: Expression => x }.foreach(walk(_, depth + 1))
        case _ => retain(e)
      }
    }
    calls.foreach(c => c.argument.l.foreach(walk(_, 0)))
    if (!fieldReached || exhausted) Map.empty
    else nodes.values.toVector.groupBy(file).view.mapValues(ns => ns.flatMap(_.lineNumber).filter(_ > 0).distinct.sorted).toMap
  }
}
