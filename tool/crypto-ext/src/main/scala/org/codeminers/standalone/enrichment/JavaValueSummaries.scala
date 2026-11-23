package org.codeminers.standalone.enrichment

import com.github.javaparser.{JavaParser, ParserConfiguration}
import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.JavaFrontend
import java.nio.file.{Files, Path}
import scala.collection.mutable
import scala.util.Try

/** Bounded, typed value summaries. Caller arguments substitute only the exact callee's parameters.
  * Defaults are possible configurations, not assertions about an observed deployment. */
private[standalone] final case class JavaValueSummary(
  sources: Vector[StoredNode], trace: Vector[Call] = Vector.empty, defaultPath: Boolean = false, arrayOutput: Boolean = false,
  sourceTraces: Map[Long, Vector[Call]] = Map.empty
) {
  def traceFor(source: StoredNode): Vector[Call] = sourceTraces.getOrElse(source.id, trace)
  def through(call: Call): JavaValueSummary = copy(trace = (trace :+ call).distinctBy(_.id),
    sourceTraces = sourceTraces.map { case (id, path) => id -> (path :+ call).distinctBy(_.id) })
}

/** Shared graph structure for the differently configured value analyses of one enrichment run.
  * Value summaries remain separate: runtime writes and salt transformations have different semantics.
  */
private[standalone] final class JavaValueIndex(cpg: Cpg, val originalInput: Option[Path] = None) {
  lazy val paths = new JavaConstantPathEvaluator(cpg, sharedIndex = Some(this))
  lazy val calls = cpg.call.l.toVector
  lazy val callsByMethod = calls.groupBy(_.method.id)
  lazy val writes = calls.filter(_.name == "<operator>.assignment")
  lazy val writesByMethod = writes.groupBy(_.method.id)
  lazy val methodsByName = cpg.method.l.toVector.groupBy(_.name)
  lazy val callsByName = calls.groupBy(_.name)
  lazy val callsByFullName = calls.groupBy(_.methodFullName)
  lazy val callsByFile = calls.groupBy(fileName)
  lazy val writesByFile = writes.groupBy(fileName)
  lazy val literalsByFile = cpg.literal.l.toVector.groupBy(fileName)
  private val files = mutable.LongMap.empty[String]
  def fileName(node: StoredNode): String = files.getOrElseUpdate(node.id, node match {
    case ast: AstNode => ast.file.name.headOption.getOrElse("")
    case _ => ""
  })
  private val arguments = mutable.LongMap.empty[List[Expression]]
  def argumentsOf(call: Call): List[Expression] = arguments.getOrElseUpdate(call.id, call.argument.l)
  def arg(call: Call, index: Int): Option[Expression] =
    argumentsOf(call).find(_.argumentIndex == index)
}

private[standalone] object JavaValueSummaries {
  def isDecimalByteParse(call: Call): Boolean =
    call.methodFullName == "java.lang.Byte.parseByte:byte(java.lang.String)"
}

private[standalone] final class JavaValueSummaries(cpg: Cpg, includeResourceDefaults: Boolean = false,
  includeRuntimeFieldWrites: Boolean = false, traceSaltTransforms: Boolean = false,
  sharedIndex: Option[JavaValueIndex] = None, transportText: Boolean = false, traceNumericKeyEncoding: Boolean = false) {
  private val index = sharedIndex.getOrElse(new JavaValueIndex(cpg))
  private val paths = index.paths
  private lazy val byteText = new JavaValueSummaries(cpg, includeRuntimeFieldWrites = true,
    sharedIndex = Some(index), transportText = true)
  private lazy val writes = index.writes
  private lazy val writesByMethod = index.writesByMethod
  private lazy val writesByField = writes.flatMap(a => arg(a, 1).collect {
    case c: Call if c.name == "<operator>.fieldAccess" => c
  }.flatMap(field).map(_ -> a)).groupMap(_._1)(_._2)
  private lazy val methodsByName = index.methodsByName
  private lazy val callsByName = index.callsByName
  private def effectKey(e: Expression): Option[String] = e match {
    case c: Call if c.name == "<operator>.fieldAccess" && c.typeFullName == "byte[]" => field(c).map((t, n) => s"field:$t:$n")
    case i: Identifier if i.typeFullName == "byte[]" => Some(s"local:${i.method.id}:${i.name}")
    case _ => None
  }
  private lazy val arraySites = callsByName.valuesIterator.flatten.filterNot(_.name.startsWith("<")).flatMap { call =>
    call.argument.l.filter(_.argumentIndex > 0).flatMap(a => effectKey(a).map(key => key -> (call, a.argumentIndex)))
  }.toVector.groupMap(_._1)(_._2)
  private val cache = mutable.HashMap.empty[(Long, Boolean), Option[JavaValueSummary]]
  private type Env = Map[Long, Expression]
  private def arg(c: Call, position: Int) = index.arg(c, position)
  private def order(n: AstNode) = (n.lineNumber.getOrElse(Int.MaxValue), n.columnNumber.getOrElse(Int.MaxValue), n.id)
  private def before(a: AstNode, b: AstNode) = summon[Ordering[(Int, Int, Long)]].lt(order(a), order(b))
  private def owner(full: String, name: String) = full.takeWhile(_ != ':').stripSuffix("." + name)
  private def arity(c: Call) = c.argument.count(_.argumentIndex > 0)
  private def matches(c: Call, m: Method): Boolean = c.methodFullName == m.fullName ||
    (c.methodFullName.contains("<unresolvedSignature>") &&
      owner(c.methodFullName, c.name) == owner(m.fullName, m.name) && arity(c) == m.parameter.count(_.index > 0))
  private def charArrayToString(c: Call): Boolean =
    Set("<init>", "valueOf", "copyValueOf")(c.name) && c.methodFullName.startsWith("java.lang.String.") &&
      arity(c) == 1 && arg(c, 1).exists {
        case identifier: Identifier => identifier.typeFullName == "char[]"
        case call: Call => call.typeFullName == "char[]"
        case _ => false
      }
  private def parsedByte(c: Call): JavaValueSummary = {
    val text = arg(c, 1).flatMap(byteText.resolve(_)).filter { summary =>
      // Text provenance retains origins across trim, so it does not establish the exact operand of a numeric parse.
      !summary.defaultPath && summary.trace.forall(_.methodFullName != "java.lang.String.trim:java.lang.String()") &&
      summary.sources.nonEmpty && summary.sources.forall {
        case literal: Literal if literal.typeFullName == "java.lang.String" =>
          val code = literal.code
          code.startsWith("\"") && code.endsWith("\"") && !code.contains('\\') &&
            Try(java.lang.Byte.parseByte(code.drop(1).dropRight(1))).isSuccess
        case _ => false
      }
    }
    text.getOrElse(JavaValueSummary(Vector(c))).through(c)
  }
  private def integerBox(c: Call): Boolean =
    Set("java.lang.Integer.<init>:void(int)", "java.lang.Integer.valueOf:java.lang.Integer(int)")(c.methodFullName)
  private def javaMapCall(call: Call): Boolean =
    call.methodFullName.startsWith("java.util.Map.") || call.methodFullName.startsWith("java.util.HashMap.")

  /** Resolve a local HashMap entry by its literal String key. Require a fresh allocation, exclude escapes and
    * unknown mutations, and use the latest feasible matching write. */
  private def localMapPut(get: Call): Option[Call] = {
    def selector(call: Call): Option[String] = arg(call, 1).collect {
      case literal: Literal if literal.typeFullName == "java.lang.String" && !literal.code.contains('\\') => literal.code
    }
    def definitelyRuns(call: Call): Boolean = paths.execution(call).contains(true) &&
      call.inAst.isControlStructure.forall(_.controlStructureType == "IF")
    for {
      receiver <- arg(get, 0).collect { case id: Identifier => id }
      key <- selector(get)
      reset <- writesByMethod.getOrElse(get.method.id, Vector.empty)
        .filter(write => before(write, get) && !paths.execution(write).contains(false) &&
          arg(write, 1).exists(_.code.trim == receiver.name)).maxByOption(order)
      if definitelyRuns(reset)
      if arg(reset, 2).exists {
        case allocation: Call =>
          allocation.name == "<operator>.alloc" && allocation.typeFullName.takeWhile(_ != '<') == "java.util.HashMap"
        case _ => false
      }
      uses = get.method.ast.isCall.l.filter { use =>
        before(reset, use) && before(use, get) && !use.ast.exists(_.id == get.id) &&
          !paths.execution(use).contains(false) && index.argumentsOf(use).exists {
            case id: Identifier => id.name == receiver.name
            case _ => false
          }
      }
      if uses.forall { use =>
        val localReceiver = arg(use, 0).exists(_.code.trim == receiver.name)
        localReceiver && ((javaMapCall(use) &&
          (Set("get", "size", "containsKey")(use.name) || (use.name == "put" && selector(use).nonEmpty))) ||
          (use.name == "<init>" && use.methodFullName.startsWith("java.util.HashMap.") && arity(use) == 0))
      }
      put <- uses.filter(use => use.name == "put" && selector(use).contains(key)).maxByOption(order)
      if definitelyRuns(put)
    } yield put
  }
  private def field(c: Call): Option[(String, String)] = for {
    typ <- arg(c, 1).collect { case i: Identifier => i.typeFullName; case t: TypeRef => t.typeFullName }
    name <- c.ast.isFieldIdentifier.canonicalName.headOption
  } yield typ.takeWhile(_ != '<') -> name

  // A byte encoder preserves a long's origin when its entire result is a permutation of that long's bytes.
  private lazy val longEncoders = methodsByName.valuesIterator.flatten.filter(m =>
    !m.isExternal && m.fullName.endsWith(":byte[](long)") && m.controlStructure.isEmpty
  ).filter { m =>
    val parameter = m.parameter.filter(_.index == 1).name.headOption
    val returns = m.ast.isReturn.l
    val result = Option.when(returns.size == 1)(returns.head).flatMap(_.astChildren.isIdentifier.headOption)
    val writes = writesByMethod.getOrElse(m.id, Vector.empty).sortBy(order)
    val calls = index.callsByMethod.getOrElse(m.id, Vector.empty)
    val allowed = Set("<operator>.assignment", "<operator>.alloc", "<operator>.indexAccess",
      "<operator>.cast", "<operator>.arithmeticShiftRight", "<operator>.logicalShiftRight")
    parameter.exists { input =>
      result.exists { output =>
        val encoded = writes.drop(1).flatMap { write =>
          for {
            access <- arg(write, 1).collect { case c: Call if c.name == "<operator>.indexAccess" => c }
            if arg(access, 1).exists(_.code == output.name)
            offset <- arg(access, 2).collect { case n: Literal => n.code }.flatMap(_.toIntOption)
            value <- arg(write, 2).collect { case c: Call if c.name == "<operator>.cast" && c.typeFullName == "byte" => c }
            shifted <- arg(value, 2)
            shift <- shifted match {
              case i: Identifier if i.name == input => Some(0)
              case c: Call if Set("<operator>.arithmeticShiftRight", "<operator>.logicalShiftRight")(c.name) &&
                arg(c, 1).exists(_.code == input) =>
                arg(c, 2).collect { case n: Literal => n.code }.flatMap(_.toIntOption)
              case _ => None
            }
          } yield offset -> shift
        }
        writes.size == 9 && writes.forall(before(_, output)) && calls.forall(c => allowed(c.name)) &&
          writes.headOption.exists(a => arg(a, 1).exists(_.code == output.name) &&
            arg(a, 2).exists(_.code.replaceAll("\\s+", "") == "newbyte[8]")) &&
          encoded.map(_._1).toSet == (0 until 8).toSet && encoded.map(_._2).toSet == (0 until 64 by 8).toSet
      }
    }
  }.toVector.groupBy(_.name)

  private lazy val lombokGetterImports = cpg.imports.l.filter(n =>
    n.code.replaceAll("\\s+", "").stripSuffix(";") match {
      case "importlombok.Getter" | "importlombok.*" => true
      case _ => false
    }).map(index.fileName).toSet
  private val lombokSourceImports = mutable.Map.empty[String, Boolean]
  private def importsLombokGetter(file: String): Boolean =
    lombokGetterImports(file) || lombokSourceImports.getOrElseUpdate(file, index.originalInput.exists { input =>
      val absolute = input.toAbsolutePath.normalize()
      val root = if (Files.isDirectory(absolute)) absolute else absolute.getParent
      val source = root.resolve(file).normalize()
      source.startsWith(root) && Files.isRegularFile(source) && Try {
        val parser = new JavaParser(new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE))
        val unit = parser.parse(source).getResult.orElseThrow()
        unit.getImports.stream().anyMatch(i => !i.isStatic &&
          (i.getNameAsString == "lombok.Getter" || i.getNameAsString == "lombok" && i.isAsterisk))
      }.getOrElse(false)
    })
  private lazy val lombokLongGetters = cpg.member.l.filter(_.typeFullName == "long").flatMap { member =>
    member.astParent match {
      case typ: TypeDecl if !member.modifier.exists(_.modifierType == "STATIC") &&
        member.ast.isAnnotation.exists(a => a.code == "@lombok.Getter" ||
          (a.code == "@Getter" && (a.fullName == "lombok.Getter" || importsLombokGetter(index.fileName(member))))) =>
        val name = member.name
        Some((typ.fullName + ".get" + name.head.toUpper + name.tail) -> (typ.fullName -> name))
      case _ => None
    }
  }.groupMap(_._1)(_._2)

  def resolve(expression: Expression, material: Boolean = false): Option[JavaValueSummary] =
    if (AnalysisAblation.valueTracking == ValueTracking.None) None
    else if (!AnalysisAblation.full) resolveFull(expression, material).filter(summary =>
      (summary.sources ++ summary.trace).forall(AnalysisAblation.reachable(_, expression.method)))
    else resolveFull(expression, material)

  private def resolveFull(expression: Expression, material: Boolean): Option[JavaValueSummary] =
    cache.getOrElseUpdate((expression.id, material), {
      var states = 0
      def walk(e: Expression, env: Env, depth: Int, seen: Set[(Long, Vector[(Long, Long)])]): Option[JavaValueSummary] = {
        states += 1
        val mark = e.id -> env.toVector.map((k, v) => k -> v.id).sortBy(_._1)
        if (depth > 24 || states > 512 || seen(mark)) return None
        val visited = seen + mark
        def follow(x: Expression, bindings: Env = env) = walk(x, bindings, depth + 1, visited)
        def combine(values: Vector[Option[JavaValueSummary]], requireAll: Boolean = true): Option[JavaValueSummary] = {
          if (values.isEmpty || (requireAll && values.exists(_.isEmpty))) None
          else {
            val resolved = values.flatten
            val sourceTraces = if (!traceNumericKeyEncoding) Map.empty[Long, Vector[Call]]
              else resolved.flatMap(v => v.sources.map(s => s.id -> v.traceFor(s)))
                .groupMapReduce(_._1)(_._2)((a, b) => (a ++ b).distinctBy(_.id))
            Option.when(resolved.nonEmpty)(JavaValueSummary(resolved.flatMap(_.sources).distinctBy(_.id),
              resolved.flatMap(_.trace).distinctBy(_.id), resolved.exists(_.defaultPath), resolved.exists(_.arrayOutput),
              sourceTraces))
          }
        }
        def parameter(p: MethodParameterIn): Option[JavaValueSummary] = env.get(p.id) match {
          case Some(value) if value.id != p.id => follow(value, env - p.id)
          case _ =>
            val sites = callsByName.getOrElse(p.method.name, Vector.empty).filter(matches(_, p.method))
              .filterNot(_.method.id == p.method.id)
            if (sites.size > 32) None
            else {
              val alternatives = sites.flatMap(site => arg(site, p.index).map(a => follow(a).map(_.through(site))))
              combine(alternatives).orElse {
                if (material) Some(JavaValueSummary(alternatives.flatten.flatMap(_.sources) :+ p,
                  alternatives.flatten.flatMap(_.trace), alternatives.flatten.exists(_.defaultPath)))
                else None
              }
            }
        }
        def construction(value: Expression, bindings: Env, hops: Int = 0): Option[(Call, Env)] = {
          if (hops > 8) return None
          value match {
            case i: Identifier =>
              val local = writesByMethod.getOrElse(i.method.id, Vector.empty).filter(a =>
                arg(a, 1).exists(_.code.trim == i.name) && before(a, i) && paths.execution(a).contains(true)).sortBy(order)
              local.lastOption.flatMap(arg(_, 2)).flatMap(construction(_, bindings, hops + 1))
                .orElse(i.method.parameter.nameExact(i.name).headOption.flatMap(p => bindings.get(p.id))
                  .filterNot(_.id == i.id).flatMap(construction(_, bindings, hops + 1)))
            case c: Call if c.name == "<operator>.alloc" =>
              val ctors = constructorsAt(c)
              Option.when(ctors.size == 1)(ctors.head -> bindings)
            case c: Call if !c.name.startsWith("<") =>
              val ms = methodsByName.getOrElse(c.name, Vector.empty).filter(matches(c, _)).filterNot(_.isExternal)
              if (ms.size != 1) None else {
                val m = ms.head
                val next = bindings ++ m.parameter.l.flatMap(p => arg(c, p.index).map(p.id -> _))
                val values = m.ast.isReturn.l.flatMap(_.astChildren.collect { case x: Expression => x })
                if (values.size == 1) construction(values.head, next, hops + 1) else None
              }
            case block: Block =>
              val allocations = block.ast.isCall.nameExact("<operator>.alloc").l
              if (allocations.size == 1) construction(allocations.head, bindings, hops + 1) else None
            case _ => None
          }
        }
        def arrayEffects(value: Expression): Option[JavaValueSummary] = {
          if (!material) return None
          val lastAssignment = value match {
            case i: Identifier => writesByMethod.getOrElse(i.method.id, Vector.empty).filter(a =>
              arg(a, 1).exists(_.code == i.name) && before(a, i) && paths.execution(a).contains(true)).sortBy(order).lastOption
            case _ => None
          }
          val sites = effectKey(value).toVector.flatMap(key => arraySites.getOrElse(key, Vector.empty))
            .filter((c, _) => !paths.execution(c).contains(false) && !c.ast.exists(_.id == value.id) &&
              (c.method.id != value.method.id || (before(c, value) && lastAssignment.forall(before(_, c)))))
          if (sites.size > 32) return None
          val effects = sites.sortBy(pair => order(pair._1)).flatMap { (site, index) =>
            val callees = methodsByName.getOrElse(site.name, Vector.empty).filter(matches(site, _)).filterNot(_.isExternal)
            if (site.name == "nextBytes" && site.methodFullName.startsWith("java.security.SecureRandom."))
              Vector(JavaValueSummary(Vector(site), Vector(site), arrayOutput = true))
            else if (callees.size != 1) {
              // An opaque writer after a known fill invalidates that fill. Parameter specs only read bytes.
              if (site.name == "<init>" || site.methodFullName.startsWith("java.util.Arrays.copy")) Vector.empty
              else Vector(JavaValueSummary(Vector(site), Vector(site), arrayOutput = true))
            } else {
              val method = callees.head
              val param = method.parameter.l.find(_.index == index).filter(_.typeFullName == "byte[]")
              val indexedWrites = param.toVector.flatMap { p =>
                writesByMethod.getOrElse(method.id, Vector.empty).filterNot(a => paths.execution(a).contains(false)).filter(a => arg(a, 1).exists {
                  case access: Call if access.name == "<operator>.indexAccess" => arg(access, 1).exists(_.code == p.name)
                  case _ => false
                })
              }
              val bindings = method.parameter.l.flatMap(p => arg(site, p.index).map(p.id -> _)).toMap
              if (indexedWrites.isEmpty) Vector.empty else {
                val values = indexedWrites.map(a => arg(a, 2).flatMap(x => follow(x, env ++ bindings)).map(_.through(a)))
                combine(values).map(v => v.copy(arrayOutput = true).through(site)).toVector
              }
            }
          }
          // Salt byte copies carry the source array's entropy, not the loop index or allocation length.
          // Combine indexed writes with later fills/opaque mutations in execution order. An unknown
          // writer remains a boundary, rather than being mistaken for a zero-filled allocation.
          if (traceSaltTransforms) value match {
            case i: Identifier if i.typeFullName == "byte[]" =>
              val indexed = writesByMethod.getOrElse(i.method.id, Vector.empty).filter(a =>
                before(a, i) && lastAssignment.forall(before(_, a)) && !paths.execution(a).contains(false) &&
                  arg(a, 1).exists {
                    case access: Call if access.name == "<operator>.indexAccess" => arg(access, 1).exists(_.code == i.name)
                    case _ => false
                  })
              if (indexed.nonEmpty && indexed.size <= 32) {
                val copied = indexed.map(a => arg(a, 2).flatMap(follow(_))
                  .getOrElse(JavaValueSummary(arg(a, 2).toVector)).copy(arrayOutput = true).through(a))
                val later = effects.filter(v => v.trace.exists(c => indexed.forall(before(_, c))))
                if (later.nonEmpty) return later.lastOption
                return combine((effects ++ copied).map(Some(_)), requireAll = false)
                  .map(_.copy(arrayOutput = true))
              }
            case _ =>
          }
          value match {
            case _: Identifier => effects.lastOption
            case _ => combine(effects.map(Some(_)), requireAll = false)
          }
        }
        def fieldValue(c: Call, target: (String, String), receiver: Option[Expression]): Option[JavaValueSummary] = {
          val effects = arrayEffects(c)
          val assignments = writesByField.getOrElse(target, Vector.empty)
          if (traceNumericKeyEncoding && assignments.exists(a => !JavaFrontend.isInitializer(a.method.name))) return None
          if (includeRuntimeFieldWrites && (!transportText || arg(c, 1).exists(_.isInstanceOf[TypeRef]))) {
            // Only writes that precede this read on an exact local/caller sequence count.
            // Same-named fields in other classes and arbitrary global setters are excluded.
            val readSites = callsByName.getOrElse(c.method.name, Vector.empty).filter(matches(_, c.method))
            val reaching = assignments.filterNot(a => JavaFrontend.isInitializer(a.method.name)).flatMap { a =>
              if (a.method.id == c.method.id && before(a, c) && paths.execution(a).contains(true)) Vector(a -> a)
              else callsByName.getOrElse(a.method.name, Vector.empty).filter(matches(_, a.method)).filter { writeSite =>
                paths.execution(writeSite).contains(true) && paths.execution(a).contains(true) &&
                  ((writeSite.method.id == c.method.id && before(writeSite, c)) ||
                    readSites.exists(readSite => readSite.method.id == writeSite.method.id && before(writeSite, readSite) &&
                      paths.execution(readSite).contains(true)))
              }.map(a -> _)
            }.sortBy((a, site) => (order(site), order(a)))
            if (reaching.nonEmpty) return arg(reaching.last._1, 2).flatMap(follow(_)).map(_.through(reaching.last._1))
          }
          // Resource loading often happens in an explicit startup method. Retain that possible
          // static-field origin for resource auditing, without changing normal array provenance.
          val resourceWrites = if (includeResourceDefaults) assignments.filter { a =>
            !JavaFrontend.isInitializer(a.method.name) && !paths.execution(a).contains(false) &&
              arg(a, 1).exists(_.ast.isTypeRef.nonEmpty) &&
              arg(a, 2).exists { case read: Call => SourceClassifier.isJavaResourceRead(read); case _ => false }
          } else Vector.empty
          val feasible = (assignments.filter(a => JavaFrontend.isInitializer(a.method.name)) ++ resourceWrites)
            .filterNot(a => paths.execution(a).contains(false))
          val initial = if (!traceNumericKeyEncoding) feasible else feasible.groupBy(_.method.id).valuesIterator.flatMap { writes =>
            val ordered = writes.sortBy(order)
            ordered.drop(ordered.lastIndexWhere(a => paths.execution(a).contains(true)).max(0))
          }.toVector
          if (effects.nonEmpty) {
            if (!includeResourceDefaults) return effects.map(v => v.copy(defaultPath = true))
            val writers = effects.toVector.flatMap(_.trace).filter(site =>
              paths.execution(site).contains(true) && arraySites.getOrElse(effectKey(c).getOrElse(""), Vector.empty)
                .exists(_._1.id == site.id))
            val ctors = methodsByName.getOrElse("<init>", Vector.empty).filter(m =>
              owner(m.fullName, m.name) == target._1 && !m.isExternal)
            val overwritesDefault = writers.exists(site => site.method.id == c.method.id && before(site, c)) ||
              (ctors.nonEmpty && ctors.forall(m => writers.exists(_.method.id == m.id)))
            if (overwritesDefault) return effects
            val defaults = initial.flatMap(a => arg(a, 2).flatMap(follow(_)).map(_.through(a)))
              .filter(_.sources.exists { case read: Call => SourceClassifier.isJavaResourceRead(read); case _ => false })
            return combine((effects.toVector ++ defaults).map(Some(_)), requireAll = false)
              .map(v => v.copy(defaultPath = true))
          }
          // A concrete receiver allows its constructor arguments to replace class-wide possibilities.
          val actualReceiver = receiver.flatMap {
            case i: Identifier if i.name == "this" => i.method.parameter.filter(_.index == 0).headOption
              .flatMap(p => env.get(p.id)).orElse(Some(i))
            case e => Some(e)
          }
          val receiverConstruction = actualReceiver.flatMap(construction(_, env))
          val receiverBindings = receiverConstruction.map { case (ctor, context) =>
            context ++ methodsByName.getOrElse("<init>", Vector.empty).filter(matches(ctor, _)).flatMap { m =>
              m.parameter.l.filter(_.index > 0).flatMap(p => arg(ctor, p.index).map(p.id -> _))
            }
          }.getOrElse(Map.empty)
          val receiverWrites = actualReceiver.toVector.collect { case i: Identifier if i.name != "this" => i }
            .flatMap { instance =>
              assignments.filterNot(a => JavaFrontend.isInitializer(a.method.name)).flatMap { write =>
                callsByName.getOrElse(write.method.name, Vector.empty).filter(matches(_, write.method))
                  .filter(site => site.method.id == instance.method.id && before(site, instance) &&
                    arg(site, 0).exists(_.code.trim == instance.name)).map(site => write -> site)
              }
            }.sortBy(pair => order(pair._2))
          if (receiverWrites.nonEmpty) {
            val (write, site) = receiverWrites.last
            val bindings = write.method.parameter.l.flatMap(p => arg(site, p.index).map(p.id -> _)).toMap
            return if (paths.execution(site).contains(true) && paths.execution(write).contains(true))
              arg(write, 2).flatMap(x => follow(x, env ++ bindings)).map(_.through(site)) else None
          }
          val results = initial.map(a => arg(a, 2).flatMap(x => follow(x, env ++ receiverBindings)).map(_.through(a)))
          val definiteUnknown = initial.zip(results).exists((a, result) => result.isEmpty && paths.execution(a).contains(true))
          if (definiteUnknown) None
          else combine(results, requireAll = false).map { result =>
            result.copy(defaultPath = result.defaultPath || assignments.exists(a => !JavaFrontend.isInitializer(a.method.name)))
          }
        }
        e match {
          case l: Literal if SourceClassifier.isConcreteLiteral(l) && !Set("null", "true", "false").contains(l.code) =>
            Some(JavaValueSummary(Vector(l)))
          case p: MethodParameterIn if p.index > 0 => parameter(p)
          case i: Identifier =>
            val effects = arrayEffects(i)
            if (effects.nonEmpty) return effects
            // In `x = f(x)` the right-hand `x` reads the previous value, not the assignment that encloses it.
            val local = writesByMethod.getOrElse(i.method.id, Vector.empty)
              .filter(a => arg(a, 1).exists(_.code.trim == i.name) && before(a, i) && !a.ast.exists(_.id == i.id))
              .filterNot(a => paths.execution(a).contains(false)).sortBy(order)
            val tryAssignments = if (traceSaltTransforms) local.filter(a =>
              a.method.controlStructure.l.exists(control => control.controlStructureType == "TRY" &&
                control.ast.exists(_.id == a.id))) else Vector.empty
            if (tryAssignments.lastOption.exists(a => local.lastOption.exists(_.id == a.id)))
              return combine(local.map(a => arg(a, 2).flatMap(follow(_)).map(_.through(a))))
                .map(v => v.copy(defaultPath = true))
            local.lastOption match {
              case Some(a) if paths.execution(a).contains(true) =>
                val collection = arg(a, 2).exists {
                  case x: Call => x.typeFullName.contains("List") || x.typeFullName.endsWith("[]")
                  case x: Identifier => x.typeFullName.contains("List") || x.typeFullName.endsWith("[]")
                  case _ => false
                }
                val mutation = !material && collection && i.method.ast.isCall.l.exists { call =>
                  before(a, call) && before(call, i) && !call.ast.exists(_.id == i.id) &&
                    call.argument.l.exists(_.ast.isIdentifier.nameExact(i.name).nonEmpty) &&
                    !Set("<operator>.fieldAccess", "<operator>.indexAccess", "<operator>.equals", "<operator>.notEquals",
                      "<operator>.logicalAnd", "<operator>.logicalOr", "<operator>.logicalNot", "toArray",
                      "isEmpty", "size", "contains", "get").contains(call.name)
                }
                if (mutation) None else arg(a, 2).flatMap(follow(_)).map(_.through(a))
              case Some(_) if material =>
                // Alternative local assignments retain every origin. Unknown branches never become constants.
                val branches = local.map(a => arg(a, 2).flatMap(follow(_)).map(_.through(a)))
                combine(branches)
              case Some(_) => None
              case None => i.method.parameter.nameExact(i.name).headOption.flatMap(parameter)
            }
          case c: Call if material && (SourceClassifier.isJavaSecretLookup(c) || SourceClassifier.isJavaFileRead(c) || SourceClassifier.isJavaResourceRead(c) || Set(org.codeminers.standalone.ontology.CryptoSourceType.WeakRandom,
            org.codeminers.standalone.ontology.CryptoSourceType.Csprng,
            org.codeminers.standalone.ontology.CryptoSourceType.Predictable).contains(SourceClassifier.classify(c))) =>
            Some(JavaValueSummary(Vector(c), Vector(c)))
          case c: Call if traceSaltTransforms && c.name == "<operator>.indexAccess" =>
            arg(c, 1).flatMap(follow(_)).map(_.through(c))
          case c: Call if traceSaltTransforms && c.name == "digest" &&
            c.methodFullName.startsWith("java.security.MessageDigest.") =>
            val directInput = arg(c, 1).flatMap(follow(_))
            val updated = arg(c, 0).collect { case i: Identifier => i }.flatMap { receiver =>
              val reset = writesByMethod.getOrElse(c.method.id, Vector.empty).filter(a =>
                arg(a, 1).exists(_.code == receiver.name) && before(a, c)).sortBy(order).lastOption
              val updates = callsByName.getOrElse("update", Vector.empty).filter(u =>
                u.method.id == c.method.id && u.methodFullName.startsWith("java.security.MessageDigest.") &&
                  arg(u, 0).exists(_.code == receiver.name) && before(u, c) && reset.forall(before(_, u)) &&
                  !paths.execution(u).contains(false))
              if (updates.size > 32) None else combine(updates.map(u => arg(u, 1).flatMap(follow(_)).map(_.through(u))))
            }
            combine((updated.toVector ++ directInput.toVector).map(Some(_))).map(_.through(c))
          case c: Call if c.name == "<operator>.fieldAccess" => field(c).flatMap(target => fieldValue(c, target, arg(c, 1)))
          case c: Call if !transportText && c.name == "get" && javaMapCall(c) =>
            localMapPut(c).flatMap(put => arg(put, 2).flatMap(follow(_)).map(_.through(put).through(c)))
          case c: Call if !transportText && integerBox(c) =>
            arg(c, 1).flatMap(follow(_)).map(_.through(c))
          case c: Call if !transportText &&
            Set("java.lang.Integer.intValue:int()", "java.lang.Integer.byteValue:byte()")(c.methodFullName) =>
            arg(c, 0).flatMap(follow(_)).map(_.through(c))
          case c: Call if material && JavaValueSummaries.isDecimalByteParse(c) =>
            Some(parsedByte(c))
          case c: Call if traceNumericKeyEncoding && c.methodFullName == "java.lang.Long.parseUnsignedLong:long(java.lang.String)" =>
            arg(c, 1).collect { case l: Literal if l.code.matches("\"[0-9]+\"") &&
              Try(java.lang.Long.parseUnsignedLong(l.code.drop(1).dropRight(1))).isSuccess =>
              JavaValueSummary(Vector(l), Vector(c))
            }
          case c: Call if traceNumericKeyEncoding && longEncoders.getOrElse(c.name, Vector.empty).exists(matches(c, _)) =>
            arg(c, 1).flatMap(follow(_)).map(v => v.copy(arrayOutput = true).through(c))
          case c: Call if material && c.methodFullName.startsWith("java.util.UUID.<init>:") =>
            combine(Vector(1, 2).map(index => arg(c, index).flatMap(follow(_)))).map(_.through(c))
          case c: Call if material && Set("substring", "replace", "replaceAll")(c.name) &&
            c.methodFullName.startsWith("java.lang.String.") =>
            // Bounds, replacement text and UUID punctuation are not entropy sources.
            arg(c, 0).flatMap(follow(_)).map(_.through(c))
          case c: Call if transportText && charArrayToString(c) =>
            arg(c, 1).flatMap(follow(_)).map(_.through(c))
          case c: Call if transportText && Set("toCharArray", "trim", "toString")(c.name) &&
            c.methodFullName.startsWith("java.lang.String.") =>
            arg(c, 0).flatMap(follow(_)).map(_.through(c))
          case c: Call if !transportText && Set("getBytes", "toCharArray", "getEncoded", "trim", "toString")(c.name) =>
            arg(c, 0).flatMap(follow(_)).map(_.through(c))
          case c: Call if material && Set("<operator>.arithmeticShiftRight", "<operator>.logicalShiftRight",
            "<operator>.shiftLeft")(c.name) => arg(c, 1).flatMap(follow(_)).map(_.through(c))
          case c: Call if c.name == "<operator>.cast" => arg(c, arity(c)).flatMap(follow(_))
          case c: Call if c.name == "<operator>.arrayInitializer" =>
            val values = c.argument.l.filter(_.argumentIndex > 0).map(follow(_)).toVector
            val unprovedParses = values.flatten.filter(_.sources.exists {
              case parse: Call => JavaValueSummaries.isDecimalByteParse(parse)
              case _ => false
            })
            // Other elements cannot prove constant material if the array's parse has not been shown to succeed.
            if (unprovedParses.nonEmpty) combine(unprovedParses.map(Some(_))) else combine(values)
          case c: Call if c.name == "<operator>.conditional" =>
            if (material) {
              val alternatives = Vector(2, 3).flatMap(index => arg(c, index).map(e =>
                follow(e).getOrElse(JavaValueSummary(Vector(e)))))
              return combine(alternatives.map(Some(_))).map(v => v.copy(defaultPath = true).through(c))
            }
            // Constant lists can differ by capabilities; retain their intersection, not their union.
            val branches = Vector(2, 3).map(index => arg(c, index).flatMap(follow(_)))
            if (branches.forall(_.nonEmpty)) {
              val common = branches.head.get.sources.filter(a => branches.last.get.sources.exists(b => code(a) == code(b)))
              Option.when(common.nonEmpty)(JavaValueSummary(common, branches.flatten.flatMap(_.trace), true))
            } else None
          case c: Call if (c.name == "asList" && c.methodFullName.startsWith("java.util.Arrays.")) ||
            (c.name == "of" && c.methodFullName.startsWith("java.util.List.")) =>
            combine(c.argument.l.filter(_.argumentIndex > 0).map(follow(_)).toVector).map(_.through(c))
          case c: Call if Set("unmodifiableList", "copyOf")(c.name) && c.methodFullName.startsWith("java.util.") =>
            arg(c, 1).flatMap(follow(_)).map(_.through(c))
          case c: Call if c.name == "toArray" => arg(c, 0).flatMap(follow(_)).map(_.through(c))
          case c: Call if material && ((c.name == "mac" &&
            c.methodFullName.startsWith("jenkins.security.HMACConfidentialKey.")) ||
            (c.name == "getJnlpMac" && c.methodFullName.startsWith("hudson.slaves.SlaveComputer."))) =>
            // MAC-derived bytes are not literals, random generators, or passwords.
            Some(JavaValueSummary(Vector(c), Vector(c)))
          case c: Call if material && c.name == "fromHexString" && c.methodFullName.startsWith("hudson.Util.") =>
            arg(c, 1).flatMap(follow(_)).map(_.through(c))
          case c: Call if material && c.methodFullName.startsWith("javax.crypto.Mac.doFinal:") =>
            Some(JavaValueSummary(Vector(c), Vector(c)))
          case c: Call if material && c.name == "generateSecret" && c.methodFullName.startsWith("javax.crypto.SecretKeyFactory.") =>
            // Preserve the derivation boundary even when the spec's origin is unresolved.
            // Resolved literal passwords remain alongside the marker for C2.
            val password = arg(c, 1).flatMap(follow(_)).getOrElse(JavaValueSummary(Vector.empty))
            Some(password.copy(sources = c +: password.sources).through(c))
          case c: Call if material && c.name == "<init>" &&
            Vector("javax.crypto.spec.PBEKeySpec.", "javax.crypto.spec.SecretKeySpec.").exists(c.methodFullName.startsWith) =>
            arg(c, 1).flatMap(follow(_)).map(_.through(c))
          case c: Call if c.name == "<operator>.alloc" =>
            val ctors = constructorsAt(c)
            if (ctors.size == 1) follow(ctors.head) else None
          case c: Call if c.name == "get" && c.methodFullName.startsWith("org.elasticsearch.common.settings.Setting.") =>
            settingValue(c, follow(_)).map(_.through(c))
          case c: Call if c.name == "resolveListSetting" &&
            c.methodFullName.startsWith("org.elasticsearch.common.ssl.SslConfigurationLoader.") &&
            arg(c, 2).exists(_.code.contains("Function.identity()")) =>
            arg(c, 3).flatMap(follow(_)).map(v => v.copy(defaultPath = true).through(c))
          case c: Call if !c.name.startsWith("<") =>
            val callees = methodsByName.getOrElse(c.name, Vector.empty).filter(matches(c, _)).filterNot(_.isExternal)
            if (callees.isEmpty && traceNumericKeyEncoding && c.name.startsWith("get") && arity(c) == 0) {
              lombokLongGetters.get(c.methodFullName.takeWhile(_ != ':')).collect { case Seq(target) => target }
                .flatMap { target =>
                  val receiver = arg(c, 0).collect { case i: Identifier if i.typeFullName == target._1 => i }
                  receiver.flatMap(i => fieldValue(c, target, Some(i))).map(_.through(c))
                }
            } else if (callees.size != 1) None
            else {
              val m = callees.head
              val bindings = m.parameter.l.flatMap(p => arg(c, p.index).map(p.id -> _)).toMap
              val returns = m.ast.isReturn.l.flatMap(_.astChildren.collect { case x: Expression => x }).toVector
              combine(returns.map(x => follow(x, env ++ bindings))).map(_.through(c))
            }
          case block: Block if transportText =>
            val ctors = block.ast.isCall.nameExact("<init>").filter(charArrayToString).l
            if (ctors.size == 1) follow(ctors.head) else None
          case block: Block if block.ast.isCall.nameExact("<init>").exists(integerBox) =>
            val constructors = block.ast.isCall.nameExact("<init>").l
            if (constructors.size == 1) follow(constructors.head) else None
          case block: Block if material =>
            val ctors = block.ast.isCall.nameExact("<init>").l.filter(c =>
              Vector("javax.crypto.spec.PBEKeySpec.", "javax.crypto.spec.SecretKeySpec.", "java.util.UUID.").exists(c.methodFullName.startsWith))
            if (ctors.size == 1) follow(ctors.head) else None
          case _ => None
        }
      }
      walk(expression, Map.empty, 0, Set.empty)
    })

  private def constructorsAt(allocation: Call): Vector[Call] = callsByName.getOrElse("<init>", Vector.empty)
    .filter(c => c.method.id == allocation.method.id && c.lineNumber == allocation.lineNumber &&
      owner(c.methodFullName, c.name) == allocation.typeFullName.takeWhile(_ != '<'))
  private def constructorsFor(i: Identifier): Vector[Call] = callsByName.getOrElse("<init>", Vector.empty)
    .filter(c => c.method.id == i.method.id && before(c, i) && arg(c, 0).exists(_.code.trim == i.name))

  private def settingValue(c: Call, follow: Expression => Option[JavaValueSummary]): Option[JavaValueSummary] = {
    val ctors = arg(c, 0).toVector.flatMap {
      case access: Call if access.name == "<operator>.fieldAccess" => field(access).toVector.flatMap(target =>
        writesByField.getOrElse(target, Vector.empty).flatMap(a => arg(a, 2).toVector.collect {
          case allocation: Call if allocation.name == "<operator>.alloc" => allocation
        }.flatMap(constructorsAt)))
      case i: Identifier => constructorsFor(i)
      case _ => Vector.empty
    }.filter(_.methodFullName.startsWith("org.elasticsearch.common.settings.Setting.<init>"))
    if (ctors.size != 1) return None
    val ctor = ctors.head
    // Only the identity parser preserves the declared default string.
    val identityParser = arg(ctor, 3).collect { case ref: MethodRef => ref }.exists { ref =>
      methodsByName.valuesIterator.flatten.find(_.fullName == ref.methodFullName).exists { method =>
        val returned = method.ast.isReturn.ast.isIdentifier.name.l
        returned.size == 1 && method.parameter.filter(_.index > 0).name.l.contains(returned.head)
      }
    }
    if (!identityParser) return None
    val key = arg(ctor, 1).flatMap(follow).filter(_.sources.size == 1).map(v => code(v.sources.head).stripPrefix("\"").stripSuffix("\""))
    def configured(e: Expression, depth: Int): Option[Option[Expression]] = {
      if (depth > 8) return None
      e match {
        case i: Identifier =>
          writesByMethod.getOrElse(i.method.id, Vector.empty).filter(a =>
            arg(a, 1).exists(_.code.trim == i.name) && before(a, i)).sortBy(order).lastOption
            .filter(a => paths.execution(a).contains(true)).flatMap(arg(_, 2)).flatMap(configured(_, depth + 1))
        case call: Call if call.name == "build" && call.methodFullName.startsWith("org.elasticsearch.common.settings.") =>
          arg(call, 0).flatMap(configured(_, depth + 1))
        case call: Call if call.name == "put" && call.methodFullName.startsWith("org.elasticsearch.common.settings.") =>
          val suppliedKey = arg(call, 1).flatMap(follow).filter(_.sources.size == 1)
            .map(v => code(v.sources.head).stripPrefix("\"").stripSuffix("\""))
          if (suppliedKey.nonEmpty && key.nonEmpty) {
            if (suppliedKey == key) Some(arg(call, 2)) else arg(call, 0).flatMap(configured(_, depth + 1))
          } else None
        case call: Call if call.name == "builder" && call.methodFullName.startsWith("org.elasticsearch.common.settings.Settings.") => Some(None)
        case _ => None
      }
    }
    val overrideValue = arg(c, 1).flatMap(configured(_, 0))
    if (overrideValue.exists(_.nonEmpty)) return overrideValue.flatten.flatMap(follow).map(_.through(ctor))
    if (overrideValue.isEmpty && arg(c, 1).exists(_.code.contains(".put("))) return None
    arg(ctor, 2).flatMap {
      case ref: MethodRef =>
        val methods = methodsByName.valuesIterator.flatten.filter(_.fullName == ref.methodFullName).toVector
        val values = methods.flatMap(_.ast.isReturn.astChildren.collect { case e: Expression => e }).flatMap(follow)
        if (values.size == 1) Some(values.head.copy(defaultPath = true).through(ctor)) else None
      case e => follow(e).map(v => v.copy(defaultPath = true).through(ctor))
    }
  }

  private def code(n: StoredNode): String = n match { case a: AstNode => a.code; case _ => "" }
}
