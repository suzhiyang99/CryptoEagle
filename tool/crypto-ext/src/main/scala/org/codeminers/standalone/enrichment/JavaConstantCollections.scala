package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.joern.dataflowengineoss.language.*
import io.shiftleft.codepropertygraph.generated.language.*
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, Expression, Identifier, Literal, TypeRef}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.JavaFrontend

/** Exact constant protocol arrays/lists. No union of unrelated field names or runtime configuration values. */
private[standalone] final class JavaConstantCollections(cpg: Cpg, sharedIndex: Option[JavaValueIndex] = None) {
  private val index = sharedIndex.getOrElse(new JavaValueIndex(cpg))
  private val paths = index.paths
  private val summaries = new JavaValueSummaries(cpg, sharedIndex = Some(index))
  private lazy val writes = index.writes
  private def order(node: AstNode) = (node.lineNumber.getOrElse(Int.MaxValue), node.columnNumber.getOrElse(Int.MaxValue), node.id)

  def summary(expression: Expression): Option[JavaValueSummary] = summaries.resolve(expression)
  def values(expression: Expression): Option[Vector[String]] =
    if (!AnalysisAblation.full && !AnalysisAblation.literalOnly(expression))
      summary(expression).filter(_.sources.forall(_.isInstanceOf[Literal])).map(_.sources.collect {
        case literal: Literal => literal.code.trim.stripPrefix("\"").stripSuffix("\"")
      })
    else resolve(expression, 0, Set.empty).orElse {
    summary(expression).filter(_.sources.forall(_.isInstanceOf[Literal])).map(_.sources.collect {
      case literal: Literal => literal.code.trim.stripPrefix("\"").stripSuffix("\"")
    })
  }

  private def field(call: Call): Option[(String, String)] = for {
    owner <- call.argument.l.find(_.argumentIndex == 1).collect {
      case id: Identifier => id.typeFullName
      case ref: TypeRef => ref.typeFullName
    }
    name <- call.ast.isFieldIdentifier.canonicalName.headOption
  } yield owner -> name

  private def resolve(expression: Expression, depth: Int, seen: Set[Long]): Option[Vector[String]] = {
    if (depth > 8 || seen(expression.id)) return None
    val visited = seen + expression.id
    def follow(value: Expression) = resolve(value, depth + 1, visited)
    def elements(elements: List[Expression]): Option[Vector[String]] = {
      val parts = elements.map(follow)
      if (parts.forall(_.nonEmpty)) Some(parts.flatten.flatten.toVector) else None
    }
    expression match {
      case literal: Literal if literal.code.startsWith("\"") && literal.code.endsWith("\"") =>
        Some(Vector(literal.code.substring(1, literal.code.length - 1)))
      case call: Call if call.name == "<operator>.arrayInitializer" =>
        elements(call.argument.l.filter(_.argumentIndex > 0))
      case call: Call if call.name == "asList" && call.methodFullName.startsWith("java.util.Arrays.") ||
        call.name == "of" && call.methodFullName.startsWith("java.util.List.") =>
        elements(call.argument.l.filter(_.argumentIndex > 0))
      case call: Call if call.name == "<operator>.alloc" =>
        call.ast.isCall.nameExact("<operator>.arrayInitializer").headOption.flatMap(follow)
      case call: Call if call.name == "toArray" => call.argument.l.find(_.argumentIndex == 0).flatMap(follow)
      case call: Call if call.name == "<operator>.fieldAccess" =>
        field(call).flatMap { target =>
          val assignments = writes.filter(_.argument.l.find(_.argumentIndex == 1).exists {
            case lhs: Call => field(lhs).contains(target)
            case _ => false
          })
          // Mutable fields with setter/runtime writes cannot be treated as their declared value.
          if (assignments.isEmpty || assignments.exists(a => !JavaFrontend.isInitializer(a.method.name))) None
          else {
            val options = assignments.map(_.argument.l.find(_.argumentIndex == 2).flatMap(follow))
            if (options.forall(_.nonEmpty) && options.flatten.distinct.size == 1) options.head else None
          }
        }
      case identifier: Identifier =>
        val assignments = writes.filter(a => a.method.id == identifier.method.id &&
          a.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == identifier.name) &&
          summon[Ordering[(Int, Int, Long)]].lt(order(a), order(identifier)))
          .filterNot(a => paths.execution(a).contains(false)).sortBy(order)
        assignments.lastOption.filter(a => paths.execution(a).contains(true)).flatMap { write =>
          val changed = identifier.method.ast.isCall.l.exists(c =>
            summon[Ordering[(Int, Int, Long)]].gt(order(c), order(write)) &&
              summon[Ordering[(Int, Int, Long)]].lt(order(c), order(identifier)) &&
              !c.ast.exists(_.id == identifier.id) &&
              c.argument.l.exists(_.ast.isIdentifier.nameExact(identifier.name).nonEmpty) &&
              !Set("<operator>.fieldAccess", "<operator>.indexAccess", "toArray").contains(c.name))
          if (changed) None else write.argument.l.find(_.argumentIndex == 2).flatMap(follow)
        }
      case _ => None
    }
  }
}
