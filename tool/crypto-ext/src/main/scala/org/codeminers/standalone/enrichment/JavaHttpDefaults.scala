package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.JavaFrontend

import scala.collection.mutable

/** Proves a possible cleartext request on a newly constructed instance with missing configuration.
  * A configurable default is considered only if it survives to a JDK URL connection on that instance.
  */
private[enrichment] final class JavaHttpDefaults(cpg: Cpg, index: JavaValueIndex, isCleartext: String => Boolean) {
  private enum Missing {
    case Null, Empty
  }
  import Missing.*

  private case class Field(owner: String, name: String)
  private val paths = index.paths
  private val defaultWrites = mutable.HashMap.empty[Field, Option[Call]]
  private lazy val privateStrings = cpg.member.filter(_.typeFullName == "java.lang.String").l.flatMap { member =>
    member.astParent match {
      case owner: TypeDecl if hasModifier(member, "PRIVATE") => Some(Field(owner.fullName, member.name))
      case _ => None
    }
  }.toSet

  private def hasModifier(node: AstNode, kind: String): Boolean =
    node.astChildren.exists { case m: Modifier => m.modifierType == kind; case _ => false }
  private def owner(method: Method): String = method.fullName.takeWhile(_ != ':').stripSuffix("." + method.name)
  private def arg(call: Call, position: Int): Option[Expression] = index.arg(call, position)
  private def order(node: AstNode): (Int, Int, Long) =
    (node.lineNumber.getOrElse(Int.MaxValue), node.columnNumber.getOrElse(Int.MaxValue), node.id)
  private def before(left: AstNode, right: AstNode): Boolean =
    summon[Ordering[(Int, Int, Long)]].lt(order(left), order(right))
  private def feasible(call: Call): Boolean = !paths.execution(call).contains(false)

  private def self(expression: Expression): Boolean = expression match {
    case id: Identifier => id.name == "this"
    case _ => false
  }
  private def field(expression: Expression): Option[Field] = expression match {
    case call: Call if call.name == "<operator>.fieldAccess" && arg(call, 1).exists(self) =>
      call.ast.isFieldIdentifier.canonicalName.headOption.map(Field(owner(call.method), _))
    case _ => None
  }
  private def writes(call: Call, target: Field): Boolean =
    call.name.startsWith("<operator>.assignment") && arg(call, 1).flatMap(field).contains(target)
  private def calls(method: Method): Vector[Call] = index.callsByMethod.getOrElse(method.id, Vector.empty)
  private def earlier(use: Expression): Vector[Call] =
    calls(use.method).filter(c => before(c, use) && !c.ast.exists(_.id == use.id) && feasible(c))

  // Expand same-instance helpers so a settings loader or setter called before the use counts as a field write.
  private def effects(sites: Vector[Call], seen: Set[Long], depth: Int = 0): Option[Vector[Call]] = {
    if (depth > 4) return None
    val expanded = Vector.newBuilder[Call]
    val ordered = sites.sortBy(order).iterator
    while (ordered.hasNext) {
      val call = ordered.next()
      expanded += call
      val delegatesConstructor = call.methodFullName.startsWith(owner(call.method) + ".<init>:")
      if ((!call.name.startsWith("<") || delegatesConstructor) && arg(call, 0).exists(self)) {
        val targets = index.methodsByName.getOrElse(call.name, Vector.empty)
          .filter(m => m.fullName == call.methodFullName && !m.isExternal && owner(m) == owner(call.method))
        if (targets.size != 1 || seen(targets.head.id)) return None
        val target = targets.head
        effects(calls(target).filter(feasible), seen + target.id, depth + 1) match {
          case Some(nested) => expanded ++= nested
          case None => return None
        }
      }
    }
    Some(expanded.result())
  }

  private def absence(condition: Expression, target: Field): Set[Missing] = condition match {
    case call: Call if call.name == "<operator>.logicalOr" =>
      val left = arg(call, 1).fold(Set.empty[Missing])(absence(_, target))
      val right = arg(call, 2).fold(Set.empty[Missing])(absence(_, target))
      // A length or isEmpty check on the left would throw before a null check on the right.
      if (left.nonEmpty && right.nonEmpty) left ++ right.filter(_ == Empty) else Set.empty
    case call: Call if call.name == "<operator>.equals" =>
      val operands = Vector(1, 2).flatMap(arg(call, _))
      if (operands.exists(_.code == "null") && operands.exists(e => field(e).contains(target))) Set(Null)
      else if (operands.exists(_.code == "0") && operands.exists {
        case length: Call => length.methodFullName == "java.lang.String.length:int()" &&
          arg(length, 0).flatMap(field).contains(target)
        case _ => false
      }) Set(Empty)
      else Set.empty
    case call: Call if call.methodFullName == "java.lang.String.isEmpty:boolean()" &&
      arg(call, 0).flatMap(field).contains(target) => Set(Empty)
    case _ => Set.empty
  }

  private def externalConfiguration(source: Expression, prefix: Vector[Call], depth: Int = 0): Boolean = {
    if (depth > 5) return false
    source match {
      case input: Call =>
        input.name == "<operator>.alloc" && Set("java.io.FileInputStream", "java.io.FileReader")(input.typeFullName)
      case id: Identifier =>
        localWrite(id) match {
          case Some(write) => paths.execution(write).contains(true) &&
            arg(write, 2).exists(externalConfiguration(_, prefix, depth + 1))
          case None => id.refOut.collect { case parameter: MethodParameterIn if parameter.index > 0 => parameter }.exists { p =>
            val method = p.method
            (method.name == "<init>" && hasModifier(method, "PUBLIC")) ||
              prefix.filter(_.methodFullName == method.fullName)
                .exists(c => arg(c, p.index).exists(externalConfiguration(_, prefix, depth + 1)))
          }
        }
      case _ => false
    }
  }

  private def emptyValues(value: Expression, prefix: Vector[Call]): Set[Missing] = value match {
    case literal: Literal if literal.code == "null" => Set(Null)
    case literal: Literal if literal.code == "\"\"" => Set(Empty)
    case read: Call if read.methodFullName == "java.util.Properties.getProperty:java.lang.String(java.lang.String)" &&
      arg(read, 1).exists(_.isInstanceOf[Literal]) =>
      val beforeRead = prefix.takeWhile(_.id != read.id)
      val optional = arg(read, 0).flatMap(field).exists { settings =>
        val definitions = beforeRead.filter(writes(_, settings))
        val noDefaults = definitions.sizeIs == 1 && beforeRead.exists { ctor =>
          ctor.methodFullName == "java.util.Properties.<init>:void()" && ctor.method.id == definitions.head.method.id &&
            ctor.lineNumber == definitions.head.lineNumber && arg(ctor, 0).flatMap(field).contains(settings)
        }
        val noSuppliedValue = beforeRead.filter(c => !c.name.startsWith("<operator>") &&
          index.argumentsOf(c).exists(e => field(e).contains(settings))).forall { use =>
          arg(use, 0).flatMap(field).contains(settings) && use.methodFullName.startsWith("java.util.Properties.") &&
            (Set("<init>", "getProperty").contains(use.name) ||
              (use.name == "load" && arg(use, 1).exists(externalConfiguration(_, beforeRead))))
        }
        val aliased = beforeRead.exists(c => c.name == "<operator>.assignment" && arg(c, 2).flatMap(field).contains(settings))
        noDefaults && noSuppliedValue && !aliased
      }
      if (optional) Set(Null, Empty) else Set.empty
    case _ => Set.empty
  }

  private def defaultWrite(target: Field): Option[Call] = defaultWrites.getOrElseUpdate(target, {
    val initializers = index.methodsByName.iterator.collect {
      case (name, methods) if name.startsWith(JavaFrontend.InstanceInitializerPrefix) =>
        methods.filter(owner(_) == target.owner)
    }.flatten.toVector
    val initializerChangesField = effects(initializers.flatMap(calls), initializers.map(_.id).toSet)
      .forall(_.exists(writes(_, target)))
    val constructors = index.methodsByName.getOrElse("<init>", Vector.empty)
      .filter(m => !initializerChangesField && owner(m) == target.owner && hasModifier(m, "PUBLIC"))
    constructors.iterator.flatMap { ctor =>
      calls(ctor).iterator.filter(c => c.name == "<operator>.assignment" && writes(c, target) && feasible(c) &&
        arg(c, 2).exists { case literal: Literal => isCleartext(literal.code); case _ => false }).filter { write =>
        val guards = write.inAst.isControlStructure.l
        val missing = guards match {
          case guard :: Nil if guard.controlStructureType == "IF" && guard.whenTrue.ast.exists(_.id == write.id) =>
            guard.condition.headOption.fold(Set.empty[Missing])(absence(_, target))
          case _ => Set.empty[Missing]
        }
        missing.nonEmpty && effects(earlier(write), Set(ctor.id)).exists { prefix =>
          val assigned = prefix.filter(writes(_, target))
          val absent: Set[Missing] = if (assigned.isEmpty) Set(Null) else assigned.foldLeft(missing) { (values, assignment) =>
            values.intersect(arg(assignment, 2).fold(Set.empty[Missing])(emptyValues(_, prefix)))
          }
          absent.intersect(missing).nonEmpty &&
            effects(calls(ctor).filter(c => before(write, c) && feasible(c)), Set(ctor.id))
              .exists(!_.exists(writes(_, target)))
        }
      }
    }.nextOption()
  })

  private def fromFreshInstance(use: Expression, target: Field, depth: Int): Boolean =
    depth <= 4 && effects(earlier(use), Set(use.method.id)).exists(!_.exists(writes(_, target))) &&
      (hasModifier(use.method, "PUBLIC") || index.callsByFullName.getOrElse(use.method.fullName, Vector.empty)
        .exists(site => owner(site.method) == target.owner && site.method.id != use.method.id &&
          arg(site, 0).exists(self) && paths.execution(site).contains(true) && fromFreshInstance(site, target, depth + 1)))

  private def origin(value: Expression, seen: Set[Long], depth: Int): Option[Call] = {
    if (depth > 8 || seen(value.id)) return None
    def follow(next: Expression): Option[Call] = origin(next, seen + value.id, depth + 1)
    value match {
      // A suffix cannot change the scheme. A URL in the suffix is not destination evidence.
      case call: Call if call.name == "<operator>.addition" && call.typeFullName == "java.lang.String" =>
        arg(call, 1).flatMap(follow)
      case call: Call =>
        field(call).filter(privateStrings.contains).filter(fromFreshInstance(call, _, 0)).flatMap(defaultWrite)
      case id: Identifier =>
        localWrite(id) match {
          case Some(write) => Option.when(paths.execution(write).contains(true))(arg(write, 2).flatMap(follow)).flatten
          case None =>
            val sites = index.callsByFullName.getOrElse(id.method.fullName, Vector.empty)
              .filter(site => site.method.id != id.method.id && paths.execution(site).contains(true))
            id.refOut.collect { case parameter: MethodParameterIn if parameter.index > 0 => parameter }
              .headOption.filter(_ => sites.size <= 32)
              .flatMap(parameter => sites.iterator.flatMap(site => arg(site, parameter.index).flatMap(follow)).nextOption())
        }
      case _ => None
    }
  }

  private def localWrite(use: Identifier): Option[Call] = {
    val declaration = use.refOut.headOption
    declaration.flatMap { symbol =>
      earlier(use).filter(c => c.name == "<operator>.assignment" && arg(c, 1).exists {
        case id: Identifier => id.refOut.headOption.exists(_.id == symbol.id)
        case _ => false
      }).maxByOption(order)
    }
  }

  def at(sink: Call): Option[Call] = {
    if (!Set("openConnection", "openStream")(sink.name) || !sink.methodFullName.startsWith("java.net.URL.") ||
      !paths.execution(sink).contains(true)) return None
    for {
      receiver <- arg(sink, 0).collect { case id: Identifier => id }
      write <- localWrite(receiver).filter(w => paths.execution(w).contains(true) && arg(w, 2).exists {
        case alloc: Call => alloc.name == "<operator>.alloc" && alloc.typeFullName == "java.net.URL"
        case _ => false
      })
      ctor <- calls(sink.method).filter(c => c.methodFullName == "java.net.URL.<init>:void(java.lang.String)" &&
        c.lineNumber == write.lineNumber && before(c, sink) && paths.execution(c).contains(true) &&
        arg(c, 0).exists(_.code == receiver.name)).maxByOption(order)
      destination <- arg(ctor, 1)
      fallback <- origin(destination, Set.empty, 0)
    } yield fallback
  }
}
