package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{
  AstNode,
  Call,
  Expression,
  FieldIdentifier,
  Identifier,
  Literal,
  StoredNode
}
import io.shiftleft.semanticcpg.language.*

/** Folds string construction into the single value an argument provably carries.
  *
  * The regex-based attribute extraction can only read a transformation such as `"AES/CBC/PKCS5Padding"` when the
  * separators and the values share one text node. Real code frequently assembles that string instead — through `+`,
  * `String.format`, or a `StringBuilder` chain — which leaves the separators in one node (`"%s/%s/%s"`) and the values
  * in others (`"AES"`, `"CBC"`). Nothing reassembled them, so `mode` and `padding` stayed UNKNOWN. This evaluator
  * performs that reassembly before extraction runs, and returns None whenever any part is unknown or ambiguous so the
  * caller keeps its existing conservative behaviour.
  */
private[enrichment] final class StringValueEvaluator(cpg: Cpg, sharedIndex: Option[JavaValueIndex] = None) {
  private val MaxDepth            = 10
  private val PrintfPlaceholder   = "%[sSd%]".r
  private val BracePlaceholder    = "\\{\\d*\\}".r
  private val BuilderConstruction = "^new\\s+String(?:Builder|Buffer)\\s*\\(".r
  private val BuilderInitializer  = "^java\\.lang\\.StringBu(?:ilder|ffer)\\.<init>.*".r

  private lazy val assignmentsByFile: Map[String, Vector[Call]] = sharedIndex.map(_.writesByFile).getOrElse(cpg.call
    .nameExact("<operator>.assignment")
    .l
    .groupBy(fileName)
    .view
    .mapValues(_.toVector)
    .toMap)

  private lazy val mutationsByMethod: Map[Long, Vector[Call]] = cpg.call
    .filter(call => call.name == "append" || call.name == "<operator>.assignmentPlus")
    .l
    .groupBy(_.method.id)
    .view
    .mapValues(_.toVector)
    .toMap

  private lazy val buildersByMethod: Map[Long, Vector[Call]] = cpg.call
    .nameExact("<init>")
    .filter(call => BuilderInitializer.findFirstIn(call.methodFullName).nonEmpty)
    .l
    .groupBy(_.method.id)
    .view
    .mapValues(_.toVector)
    .toMap

  private lazy val callsByFullName: Map[String, Vector[Call]] =
    sharedIndex.map(_.callsByFullName).getOrElse(cpg.call.l.groupBy(_.methodFullName).view.mapValues(_.toVector).toMap)
  private lazy val callsByName: Map[String, Vector[Call]] =
    sharedIndex.map(_.callsByName).getOrElse(cpg.call.l.groupBy(_.name).view.mapValues(_.toVector).toMap)

  /** The complete string `expression` evaluates to, or None when any part is unknown or ambiguous. */
  def evaluate(expression: Expression): Option[String] =
    if (AnalysisAblation.evaluable(expression)) value(expression, depth = 0, seen = Set.empty) else None

  private def value(expression: Expression, depth: Int, seen: Set[Long]): Option[String] =
    if (depth > MaxDepth || seen.contains(expression.id)) None
    else
      expression match {
        case literal: Literal       => quotedText(literal.code)
        case call: Call             => callValue(call, depth, seen + call.id)
        case identifier: Identifier => symbolValue(identifier.name, identifier, depth, seen + identifier.id)
        case _                      => None
      }

  private def callValue(call: Call, depth: Int, seen: Set[Long]): Option[String] = call.name match {
    case "<operator>.addition" =>
      for {
        left  <- argumentValue(call, 1, depth, seen)
        right <- argumentValue(call, 2, depth, seen)
      } yield left + right

    case "<operator>.fieldAccess" if AnalysisAblation.full =>
      fieldName(call).flatMap(name => symbolValue(name, call, depth, seen))

    case "<operator>.conditional" => conditionalValue(call, depth, seen)

    // Python `"%s/%s" % (algorithm, mode)`.
    case "<operator>.modulo" =>
      argumentValue(call, 1, depth, seen)
        .filter(hasPlaceholder)
        .flatMap(format => substitute(format, flattened(call, 2).map(part => value(part, depth + 1, seen))))

    case "format" => formatValue(call, depth, seen)
    case "join"   => joinValue(call, depth, seen)

    case "append" | "concat" =>
      for {
        receiver <- receiverValue(call, depth, seen).orElse(constructedBuilder(call, depth, seen))
        part     <- argumentValue(call, 1, depth, seen)
      } yield receiver + part

    case "toString"              => receiverValue(call, depth, seen)
    case "trim" | "strip"        => receiverValue(call, depth, seen).map(_.trim)
    case "toUpperCase" | "upper" => receiverValue(call, depth, seen).map(_.toUpperCase)
    case "toLowerCase" | "lower" => receiverValue(call, depth, seen).map(_.toLowerCase)
    case "valueOf"               => argumentValue(call, 1, depth, seen)
    case _                       => None
  }

  /** Both branches agreeing is a proof. Beyond that only the null-coalescing default idiom
    * (`name == null ? "AES" : name`) is resolved: the literal is the configuration that applies whenever the caller
    * supplies nothing, so it is a reachable configuration rather than a guess. Genuinely divergent branches such as
    * `strong ? "AES" : "DES"` stay unknown.
    */
  private def conditionalValue(call: Call, depth: Int, seen: Set[Long]): Option[String] = {
    val branches = Vector(2, 3).map(index => argumentValue(call, index, depth, seen))
    if (branches.forall(_.nonEmpty) && branches.flatten.distinct.size == 1) branches.head
    else nullDefault(call, depth, seen)
  }

  private def nullDefault(call: Call, depth: Int, seen: Set[Long]): Option[String] = {
    val whenTrue  = call.argument.l.find(_.argumentIndex == 2)
    val whenFalse = call.argument.l.find(_.argumentIndex == 3)
    call.argument.l
      .find(_.argumentIndex == 1)
      .collect {
        case comparison: Call
            if comparison.name == "<operator>.equals" || comparison.name == "<operator>.notEquals" =>
          comparison
      }
      .flatMap { comparison =>
        val operands = comparison.argument.l.filter(_.argumentIndex > 0).map(_.code.trim)
        val (default, passthrough) =
          if (comparison.name == "<operator>.equals") (whenTrue, whenFalse) else (whenFalse, whenTrue)
        for {
          _           <- Option.when(operands.contains("null"))(())
          symbol      <- operands.find(_ != "null")
          carried     <- passthrough
          if carried.code.trim == symbol
          branch      <- default
          resolved    <- value(branch, depth + 1, seen)
        } yield resolved
      }
  }

  /** `String.format(fmt, a, b)`, `StringUtils.format(fmt, a, b)`, and Python's `fmt.format(a, b)`. The format string is
    * located by its placeholders rather than by position, so a leading `Locale` or receiver argument is skipped.
    */
  private def formatValue(call: Call, depth: Int, seen: Set[Long]): Option[String] = {
    val resolved = flattened(call, 0).map(part => value(part, depth + 1, seen))
    val index    = resolved.indexWhere(_.exists(hasPlaceholder))
    if (index < 0) None else substitute(resolved(index).get, resolved.drop(index + 1))
  }

  private def joinValue(call: Call, depth: Int, seen: Set[Long]): Option[String] = {
    val parts = flattened(call, 2).map(part => value(part, depth + 1, seen))
    for {
      separator <- argumentValue(call, 1, depth, seen)
      if parts.nonEmpty && parts.forall(_.nonEmpty)
    } yield parts.flatten.mkString(separator)
  }

  /** Java varargs reach the CPG wrapped in an `<operator>.arrayInitializer`, so the substitution arguments are one
    * level below the call. */
  private def flattened(call: Call, fromIndex: Int): Vector[Expression] =
    call.argument.l
      .filter(_.argumentIndex >= fromIndex)
      .sortBy(_.argumentIndex)
      .flatMap {
        case inner: Call if inner.name == "<operator>.arrayInitializer" =>
          inner.argument.l.filter(_.argumentIndex >= 1).sortBy(_.argumentIndex)
        case other => List(other)
      }
      .toVector

  private def hasPlaceholder(format: String): Boolean =
    PrintfPlaceholder.findFirstIn(format).nonEmpty || BracePlaceholder.findFirstIn(format).nonEmpty

  private def substitute(format: String, arguments: Vector[Option[String]]): Option[String] =
    if (PrintfPlaceholder.findFirstIn(format).nonEmpty) substitutePrintf(format, arguments)
    else if (BracePlaceholder.findFirstIn(format).nonEmpty) substituteBrace(format, arguments)
    else None

  private def substitutePrintf(format: String, arguments: Vector[Option[String]]): Option[String] = {
    val text     = new StringBuilder
    var position = 0
    var next     = 0
    while (position < format.length) {
      val character = format.charAt(position)
      if (character != '%') { text.append(character); position += 1 }
      else if (position + 1 >= format.length) return None
      else
        format.charAt(position + 1) match {
          case '%' => text.append('%'); position += 2
          case conversion if "sSd".contains(conversion) =>
            arguments.lift(next).flatten match {
              case Some(argument) =>
                text.append(if (conversion == 'S') argument.toUpperCase else argument)
                next += 1
                position += 2
              case None => return None
            }
          // Width, precision, and argument-index flags are not modelled; staying unknown beats guessing.
          case _ => return None
        }
    }
    Some(text.toString)
  }

  private def substituteBrace(format: String, arguments: Vector[Option[String]]): Option[String] = {
    val text     = new StringBuilder
    var position = 0
    var next     = 0
    while (position < format.length) {
      if (format.startsWith("{{", position)) { text.append('{'); position += 2 }
      else if (format.startsWith("}}", position)) { text.append('}'); position += 2 }
      else if (format.charAt(position) == '{') {
        val end = format.indexOf('}', position)
        if (end < 0) return None
        val specifier = format.substring(position + 1, end)
        val index     = if (specifier.isEmpty) { next += 1; next - 1 } else specifier.toIntOption.getOrElse(-1)
        if (index < 0) return None
        arguments.lift(index).flatten match {
          case Some(argument) => text.append(argument); position = end + 1
          case None           => return None
        }
      } else { text.append(format.charAt(position)); position += 1 }
    }
    Some(text.toString)
  }

  /** The value a symbol carries at `use`: one unambiguous base assignment, then every `append` or `+=` that provably
    * runs before `use`, applied in source order. */
  private def symbolValue(name: String, use: Expression, depth: Int, seen: Set[Long]): Option[String] = {
    val assigned = assignedExpressions(name, use)
    // An assignment that exists but does not resolve must not fall through to the parameter or field initializer:
    // the assignment overwrote whatever those carried, so the symbol is unknown rather than equal to the old value.
    val base =
      if (assigned.isEmpty) Option.when(AnalysisAblation.full)(
        parameterValue(name, use, depth, seen).orElse(memberValue(name, use))).flatten
      else
        assigned.map(expression => baseValue(expression, name, use, depth, seen)).distinct match {
          case Vector(single) => single
          case _              => None
        }
    val mutations = mutationsFor(name, use)
    if (mutations.isEmpty) base
    else
      mutations.foldLeft(base) { (accumulated, mutation) =>
        for {
          prefix <- accumulated
          part   <- mutationPart(mutation, depth, seen)
        } yield prefix + part
      }
  }

  private def baseValue(
    assigned: Expression,
    name: String,
    use: Expression,
    depth: Int,
    seen: Set[Long]
  ): Option[String] =
    if (isBuilderConstruction(assigned)) builderBase(name, use, depth, seen)
    else value(assigned, depth + 1, seen)

  /** Matching the node kind rather than the code prefix keeps `new StringBuilder().append(..).toString()` — an
    * expression that merely starts with a construction — out of the named-builder path. */
  private def isBuilderConstruction(expression: Expression): Boolean = expression match {
    case call: Call =>
      (call.name == "<operator>.alloc" || call.name == "<init>") &&
      BuilderConstruction.findFirstIn(call.code.trim).nonEmpty
    case _ => false
  }

  /** `new StringBuilder()` reaches the CPG as an `<operator>.alloc` that carries no value, so the initial content is
    * read from the matching constructor call instead. A capacity argument contributes no text. */
  private def builderBase(name: String, use: Expression, depth: Int, seen: Set[Long]): Option[String] =
    buildersByMethod
      .getOrElse(use.method.id, Vector.empty)
      .filter(init => receiverSymbol(init).contains(name))
      .filter(init => precedes(init, use))
      .sortBy(order)
      .lastOption match {
      case None       => Some("")
      case Some(init) => initialText(init, depth, seen)
    }

  /** `new StringBuilder().append(..)` keeps the construction inside the receiver BLOCK rather than under a name, so the
    * initial content is read from the constructor call that block carries. */
  private def constructedBuilder(call: Call, depth: Int, seen: Set[Long]): Option[String] =
    call.argument.l
      .find(_.argumentIndex == 0)
      .flatMap(_.ast.isCall.find(inner => BuilderInitializer.findFirstIn(inner.methodFullName).nonEmpty))
      .flatMap(init => initialText(init, depth, seen))

  private def initialText(init: Call, depth: Int, seen: Set[Long]): Option[String] =
    init.argument.l.find(_.argumentIndex == 1) match {
      case None                                                 => Some("")
      case Some(capacity) if capacity.code.trim.matches("\\d+") => Some("")
      case Some(initial)                                        => value(initial, depth + 1, seen)
    }

  private def assignedExpressions(name: String, use: Expression): Vector[Expression] =
    assignmentsByFile
      .getOrElse(fileName(use), Vector.empty)
      .filter { assignment =>
        val target = assignment.argument.l.find(_.argumentIndex == 1).map(_.code.trim).getOrElse("")
        if (AnalysisAblation.full) target == name || target.endsWith(s".$name")
        else target == name && assignment.method.id == use.method.id
      }
      .flatMap(_.argument.l.find(_.argumentIndex == 2))

  private def mutationsFor(name: String, use: Expression): Vector[Call] = {
    val inMethod = mutationsByMethod.getOrElse(use.method.id, Vector.empty)
    // `sb.append(a).append(b)` is one statement whose inner call is only the receiver of the outer one, so the chain
    // contributes once, through its outermost call.
    val chained = inMethod.flatMap(_.argument.l.find(_.argumentIndex == 0)).map(_.id).toSet
    inMethod
      .filterNot(mutation => chained.contains(mutation.id))
      .filter { mutation =>
        if (mutation.name == "append") rootSymbol(mutation).contains(name)
        else
          mutation.argument.l.find(_.argumentIndex == 1).map(_.code.trim).exists { target =>
            target == name || target.endsWith(s".$name")
          }
      }
      .filter(mutation => precedes(mutation, use))
      .sortBy(order)
  }

  private def mutationPart(mutation: Call, depth: Int, seen: Set[Long]): Option[String] =
    if (mutation.name == "append") chainedParts(mutation, depth, seen)
    else argumentValue(mutation, 2, depth, seen)

  /** Every argument an append chain contributes, innermost first, without the receiver's own value. */
  private def chainedParts(call: Call, depth: Int, seen: Set[Long]): Option[String] = {
    val inherited = call.argument.l.find(_.argumentIndex == 0) match {
      case Some(inner: Call) if inner.name == "append" => chainedParts(inner, depth, seen)
      case _                                           => Some("")
    }
    for {
      prefix <- inherited
      part   <- argumentValue(call, 1, depth, seen)
    } yield prefix + part
  }

  private def rootSymbol(call: Call): Option[String] =
    call.argument.l.find(_.argumentIndex == 0) match {
      case Some(inner: Call) if inner.name == "append" => rootSymbol(inner)
      case _                                           => receiverSymbol(call)
    }

  private def parameterValue(name: String, use: Expression, depth: Int, seen: Set[Long]): Option[String] =
    use.method.parameter.nameExact(name).headOption.flatMap { parameter =>
      val exact = callsByFullName.getOrElse(use.method.fullName, Vector.empty)
      val sites =
        if (exact.nonEmpty) exact
        else if (!use.method.name.startsWith("<")) callsByName.getOrElse(use.method.name, Vector.empty)
        else Vector.empty
      val values = sites
        .filterNot(_.method.id == use.method.id)
        .take(64)
        .flatMap(_.argument.l.filter(_.argumentIndex == parameter.index))
        .flatMap(argument => value(argument, depth + 1, seen))
        .distinct
      values match {
        case Vector(single) => Some(single)
        case _              => None
      }
    }

  private def memberValue(name: String, use: Expression): Option[String] = {
    val initializers: Vector[String] = cpg.member
      .nameExact(name)
      .filter(member => fileName(member) == fileName(use))
      .l
      .flatMap(member => member.ast.isLiteral.code.l)
      .flatMap(quotedText)
      .distinct
      .toVector
    initializers match {
      case Vector(single) => Some(single)
      case _              => None
    }
  }

  private def argumentValue(call: Call, index: Int, depth: Int, seen: Set[Long]): Option[String] =
    call.argument.l.find(_.argumentIndex == index).flatMap(argument => value(argument, depth + 1, seen))

  private def receiverValue(call: Call, depth: Int, seen: Set[Long]): Option[String] =
    argumentValue(call, 0, depth, seen)

  private def receiverSymbol(call: Call): Option[String] =
    call.argument.l.find(_.argumentIndex == 0).flatMap {
      case identifier: Identifier                                 => Some(identifier.name)
      case access: Call if access.name == "<operator>.fieldAccess" => fieldName(access)
      case _                                                       => None
    }

  private def fieldName(call: Call): Option[String] =
    call.argument.l.find(_.argumentIndex == 2).collect { case field: FieldIdentifier => field.canonicalName }

  /** Only quoted literals carry text. Numeric literals must stay unknown so that `128 + 128` is never folded into the
    * string "128128". */
  private def quotedText(code: String): Option[String] = {
    val raw = code.trim
    // Python's b/u/r prefixes leave the text intact; an f-string still holds unsubstituted placeholders and is
    // therefore not a value.
    val prefix  = raw.takeWhile(character => character.isLetter)
    val trimmed = raw.drop(prefix.length)
    if (prefix.exists(character => character == 'f' || character == 'F')) return None
    Seq("\"\"\"", "'''", "\"", "'")
      .find(quote => trimmed.length >= 2 * quote.length && trimmed.startsWith(quote) && trimmed.endsWith(quote))
      .map(quote => trimmed.substring(quote.length, trimmed.length - quote.length))
  }

  private def precedes(node: Call, use: Expression): Boolean =
    summon[Ordering[(Int, Int, Long)]].lt(order(node), order(use)) && !node.ast.exists(_.id == use.id)

  private def order(node: AstNode): (Int, Int, Long) =
    (node.lineNumber.getOrElse(Int.MaxValue), node.columnNumber.getOrElse(Int.MaxValue), node.id)

  private def fileName(node: StoredNode): String = node match {
    case ast: AstNode => ast.file.name.headOption.getOrElse("")
    case _            => ""
  }
}
