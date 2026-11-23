package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Block, Call, Expression, Identifier, Literal, Method}
import io.shiftleft.semanticcpg.language.*

/** Prove a key-derived default IV reaches a real CBC encryption consumer through an inherited wrapper.
  * Also recover a returned random first block, not arbitrary random data that happens to be encrypted.
  * Require a uniquely resolved CBC wrapper, a key-derived default IV, an actual encryption method,
  * and a 16-character random prefix before the remaining plaintext. Unknown calls stop the proof.
  * All lookups are bounded/indexed; no project or function names are allowlisted.
  */
private[standalone] final class PythonCbcUseEvidence(cpg: Cpg) {
  private case class CbcUse(factory: Call, constructor: Call, defaultIv: Expression, sink: Call,
                            store: Call, producer: Call, update: Call)
  private case class Bound(e: Expression, env: Map[String, Bound])
  private case class Value(sources: Vector[Call] = Vector.empty, width: Option[Int] = None,
                           prefix: Boolean = false, trace: Vector[Call] = Vector.empty)
  private val empty = Value()
  private val calls = cpg.call.l.toVector
  private val methods = cpg.method.l.filterNot(_.isExternal).toVector
  private val byName = methods.groupBy(_.name)
  private val writes = calls.filter(_.name == "<operator>.assignment").groupBy(c => c.method.id -> lhs(c))
  private val byMethod = calls.groupBy(_.method.id)
  private val assignmentsByFile = calls.filter(_.name == "<operator>.assignment").groupBy(file)
  private val imports = calls.filter(_.name == "import").groupBy(file)
  private val contents = cpg.file.l.map(f => f.name -> f.content).toMap
  private val operations = cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.toVector
  private val cbcFactories = operations.filter(o => o.srcFile.endsWith(".py") &&
    o.opType == "SYMMETRIC_ENCRYPTION" && o.algorithm == "AES" && o.mode == "CBC")
  private val cipherMethods = operations.filter(o => o.opType == "SYMMETRIC_ENCRYPTION" && o.operation == "ENCRYPT")
    .flatMap(_.realizingCall.method.l).map(_.id).toSet
  private val weakCalls = operations.filter(o => o.opType == "RANDOMNESS" && o.source == "WEAK_RANDOM")
    .flatMap(_.realizingCall.l).map(c => c.id -> c).toMap

  private lazy val cbcUses = if (cbcFactories.isEmpty) Vector.empty else calls
    .filter(c => c.name == "encrypt" && file(c).endsWith(".py"))
    .flatMap(provenCbcWrapper)

  /** Preserve the partial general lifecycle: this proof establishes only the default-IV encryption use,
    * not caller authentication or unrelated key properties. Its factory location is shared across consumers.
    */
  lazy val defaultIvEncryption: Map[Long, Vector[FindingEvidence]] = cbcUses.groupBy(_.factory.id).map { case (id, uses) =>
    val evidence = uses.flatMap { use =>
      Vector(FindingEvidence("IV_DEFAULT", use.defaultIv.label,
        s"${file(use.defaultIv)}:${use.defaultIv.lineNumber.getOrElse(-1)} ${use.defaultIv.code}; IV omitted or None at ${file(use.constructor)}:${use.constructor.lineNumber.getOrElse(-1)} ${use.constructor.code}"),
        FindingEvidence("IV_CONSUMER", "CALL", Vector(use.factory, use.store, use.producer, use.update, use.sink)
          .map(c => s"${file(c)}:${c.lineNumber.getOrElse(-1)} ${c.code}").mkString(" -> ")))
    }.distinct
    id -> evidence
  }

  lazy val evidence: Map[Long, String] = {
    if (weakCalls.isEmpty) Map.empty
    else cbcUses.flatMap { use =>
      val sink = use.sink
      arg(sink, 1).toVector.flatMap { input =>
        val value = follow(input, Map.empty, 0, Set.empty)
        if (!value.prefix) Vector.empty
        else value.sources.map { source =>
          val chain = (value.trace :+ sink).distinctBy(_.id).map(c => s"${file(c)}:${c.lineNumber.getOrElse(-1)} ${c.code}")
          source.id -> (s"${source.code}; random first CBC block: " + chain.mkString(" -> "))
        }
      }
    }.groupMap(_._1)(_._2).view.mapValues(_.distinct.mkString("; ")).toMap
  }

  private def follow(e: Expression, env: Map[String, Bound], depth: Int, seen: Set[Long]): Value = {
    if (depth > 24 || seen(e.id)) return empty
    val next = seen + e.id
    def recurse(v: Expression) = follow(v, env, depth + 1, next)
    e match {
      case c: Call if weakCalls.contains(c.id) && c.code.startsWith("random.sample(") && genuine(c.method, "random") =>
        val n = arg(c, 2).flatMap(v => integer(v, env, 0, Set.empty))
        if (arg(c, 1).exists(v => asciiAlphabet(v, Set.empty, 0))) Value(Vector(c), n, trace = Vector(c)) else empty
      case i: Identifier =>
        latest(i).map(recurse).orElse(env.get(i.name).map(b => follow(b.e, b.env, depth + 1, next))).getOrElse(empty)
      case l: Literal if l.code.matches("(?s)b?[\"'].*[\"']") =>
        Value(width = Some(l.code.stripPrefix("b").drop(1).dropRight(1).length))
      case b: Block => b.astChildren.collect { case v: Expression => v }.toVector.lastOption.map(recurse).getOrElse(empty)
      case c: Call if c.name == "<operator>.assignment" => arg(c, 2).map(recurse).getOrElse(empty)
      case c: Call if c.name == "<operator>.addition" =>
        val left = arg(c, 1).map(recurse).getOrElse(empty)
        val right = arg(c, 2).map(recurse).getOrElse(empty)
        left.copy(width = for (a <- left.width; b <- right.width) yield a + b,
          prefix = left.prefix || (left.width.contains(16) && left.sources.nonEmpty), trace = left.trace :+ c)
      case c: Call if c.name == "join" && emptySeparator(c) =>
        val joined = arg(c, 1).map(v => container(v, env, depth + 1, next)).getOrElse(empty)
        joined.copy(trace = joined.trace :+ c)
      case c: Call if Set("encode", "decode").contains(c.name) && arg(c, 0).nonEmpty && definitions(c).isEmpty =>
        // Python's string/bytes conversions preserve the ASCII prefix. A same-name local method is resolved below.
        arg(c, 0).map(recurse).getOrElse(empty)
      case c: Call if c.name == "<operator>.conditional" =>
        combine(c.argument.l.filter(_.argumentIndex > 1).map(recurse).toVector)
      case c: Call if !c.name.startsWith("<operator>.") =>
        val defs = definitions(c)
        if (defs.size != 1) empty
        else {
          val m = defs.head
          val bound = m.parameter.l.flatMap(p => arg(c, p.index, p.name).map(v => p.name -> Bound(v, env))).toMap
          val result = combine(m.ast.isReturn.astChildren.collect { case v: Expression =>
            follow(v, bound, depth + 1, next)
          }.toVector)
          result.copy(trace = result.trace :+ c)
        }
      case _ => empty
    }
  }

  private def container(e: Expression, env: Map[String, Bound], depth: Int, seen: Set[Long]): Value = {
    if (depth > 24) return empty
    val name = e.code.trim
    val initial = latestAssignment(e)
    if (initial.isEmpty || !arg(initial.get, 2).exists(_.code.trim == "[]"))
      return follow(e, env, depth + 1, seen)
    val start = initial.get.lineNumber.getOrElse(0)
    val end = e.lineNumber.getOrElse(Int.MaxValue)
    val mutations = byMethod.getOrElse(e.method.id, Vector.empty).filter(c =>
      c.lineNumber.exists(n => n > start && n < end) && receiver(c).contains(name))
    if (mutations.exists(c => c.name != "append" || c.inAst.isControlStructure.nonEmpty)) return empty
    val appended = mutations.filter(_.name == "append").sortBy(c => (c.lineNumber.getOrElse(0), c.columnNumber.getOrElse(0)))
    if (appended.size < 2) return empty
    val first = arg(appended.head, 1).map(v => follow(v, env, depth + 1, seen)).getOrElse(empty)
    if (first.width.contains(16) && first.sources.nonEmpty)
      first.copy(width = None, prefix = true, trace = first.trace :+ appended.head)
    else empty
  }

  private def combine(values: Vector[Value]): Value = {
    val carrying = values.filter(_.sources.nonEmpty)
    if (carrying.isEmpty) empty
    else Value(carrying.flatMap(_.sources).distinctBy(_.id),
      Option.when(carrying.forall(_.width == carrying.head.width))(carrying.head.width).flatten,
      prefix = carrying.exists(_.prefix), trace = carrying.flatMap(_.trace).distinctBy(_.id))
  }

  private def integer(e: Expression, env: Map[String, Bound], depth: Int, seen: Set[Long]): Option[Int] = {
    if (depth > 10 || seen(e.id)) None
    else e.code.replace("_", "").toIntOption.orElse(e match {
      case i: Identifier => latest(i).flatMap(v => integer(v, env, depth + 1, seen + e.id))
        .orElse(env.get(i.name).flatMap(b => integer(b.e, b.env, depth + 1, seen + e.id)))
      case _ => None
    })
  }

  private def asciiAlphabet(e: Expression, seen: Set[Long], depth: Int): Boolean = {
    if (depth > 10 || seen(e.id)) false
    else if (Set("string.ascii_letters", "string.ascii_lowercase", "string.ascii_uppercase", "string.digits", "string.hexdigits")
      .contains(e.code.trim)) genuine(e.method, "string")
    else e match {
      case i: Identifier => latest(i).exists(v => asciiAlphabet(v, seen + e.id, depth + 1))
      case c: Call if c.name == "<operator>.addition" =>
        c.argument.l.filter(_.argumentIndex > 0).forall(v => asciiAlphabet(v, seen + e.id, depth + 1))
      case l: Literal => l.code.matches("[\"'][A-Za-z0-9]+[\"']")
      case _ => false
    }
  }

  private def provenCbcWrapper(c: Call): Option[CbcUse] = {
    val receiverField = arg(c, 0).flatMap(fieldReference(_, Set.empty, 0))
    if (receiverField.exists(name => assignmentsByFile.getOrElse(file(c), Vector.empty).exists(a =>
      lhs(a) == name && owner(a.method) == owner(c.method) && a.method.name != "__init__"))) return None
    val constructors = receiverField.toVector.flatMap { name =>
      byName.getOrElse("__init__", Vector.empty).filter(m => file(m) == file(c) && owner(m) == owner(c.method))
        .flatMap(m => writes.getOrElse(m.id -> name, Vector.empty)).flatMap(arg(_, 2)).collect { case x: Call => x }
    }
    if (constructors.size != 1) return None
    val className = constructors.head.name
    if (constructors.head.method.parameter.nameExact(className).nonEmpty ||
      assignmentsByFile.getOrElse(file(c), Vector.empty).exists(a => lhs(a) == className &&
        (a.method.id == constructors.head.method.id || a.method.name == "<module>") && !arg(a, 2).exists {
          case ref: io.shiftleft.codepropertygraph.generated.nodes.TypeRef => ref.typeFullName.stripSuffix("<meta>").endsWith("." + className)
          case n => n.label == "METHOD_REF"
        })) return None
    if (sourceCallArguments(constructors.head).forall(_.matches("(?s).*(?:^|,)\\s*\\*.*"))) return None
    val init = classMethod(file(c), className, "__init__", Set.empty)
    val encryption = classMethod(file(c), className, "encrypt", Set.empty)
    if (init.size != 1 || encryption.size != 1) return None
    val method = encryption.head
    if (!cipherMethods(method.id)) return None
    val keyName = init.head.parameter.index(1).name.headOption.getOrElse("")
    val ivName = init.head.parameter.index(2).name.headOption.getOrElse("")
    // Omitted IV and literal None take the fallback. A caller-supplied IV is not folded into this default branch.
    val suppliedIv = arg(constructors.head, 2, ivName)
    if (suppliedIv.exists(_.code.trim != "None") || constructors.head.argument.l.exists(_.argumentIndex > 2)) return None
    val ivDefault = ivName.nonEmpty && contents.getOrElse(file(c), "").linesIterator.toVector
      .lift(init.head.lineNumber.getOrElse(0) - 1).exists(_.matches(
        ".*\\b" + java.util.regex.Pattern.quote(ivName) + "\\s*=\\s*None.*"))
    val cbc = cbcFactories.filter(o =>
      o.realizingCall.method.l.exists(_.id == init.head.id) && o.realizingCall.l.exists(factory =>
        factory.ast.isCall.nameExact("AES").exists(a => arg(a, 1).exists(_.code.trim == keyName)) &&
          factory.ast.isCall.nameExact("CBC").exists(a => arg(a, 1).exists(v => v.code.trim == ivName &&
            latest(v).exists(_.code.replaceAll("\\s+", "") == ivName + "or" + keyName + "[:16]")))))
    // The factory must be handed to the base constructor that stores the cipher used by encrypt().
    if (!ivDefault) return None
    cbc.flatMap(_.realizingCall.l).flatMap { factory =>
      val defaultIv = factory.ast.isCall.nameExact("CBC").l.flatMap(a => arg(a, 1).flatMap(latest)).headOption
      val forwarded = factory.inAst.isCall.nameExact("__init__").l.exists { parent =>
      arg(parent, 0).exists(v => origin(v, Set.empty, 0).exists {
        case s: Call => s.name == "super" && init.head.parameter.nameExact("super").isEmpty &&
          !assignmentsByFile.getOrElse(file(c), Vector.empty).exists(a => lhs(a) == "super" &&
            !arg(a, 2).exists(_.code.trim == "__builtins__.super"))
        case _ => false
      }) && arg(parent, 1).exists(_.ast.id(factory.id).nonEmpty)
      }
      if (!forwarded || defaultIv.isEmpty) Vector.empty
      else method.ast.isCall.nameExact("encryptor").l.flatMap { producer =>
          val field = arg(producer, 0).flatMap(fieldReference(_, Set.empty, 0))
          // super() invokes the immediate parent's constructor, which can override the stored cipher.
          // Looking up the encrypt method's owner constructor would skip that override.
          val bases = parents(file(c), className).flatMap(p => classMethod(file(c), p, "__init__", Set.empty))
          val stored = Option.when(bases.size == 1)(bases.head).toVector.flatMap { base =>
            field.toVector.flatMap { f =>
              val latestStore = writes.getOrElse(base.id -> f, Vector.empty)
                .sortBy(a => (a.lineNumber.getOrElse(0), a.columnNumber.getOrElse(0))).lastOption
                .filterNot(_.inAst.isControlStructure.nonEmpty)
              val modifiedElsewhere = assignmentsByFile.getOrElse(file(c), Vector.empty).exists(a => lhs(a) == f &&
                Set(owner(method), owner(base)).contains(owner(a.method)) && a.method.name != "__init__")
              if (modifiedElsewhere) Vector.empty
              else latestStore.filter(a => arg(a, 2).exists(v => base.parameter.index(1).exists(_.name == v.code.trim))).toVector
            }
          }.headOption
          val workerNames = byMethod.getOrElse(method.id, Vector.empty).filter(a => a.name == "<operator>.assignment" &&
            arg(a, 2).exists(_.ast.id(producer.id).nonEmpty)).map(lhs).toSet
          val updates = method.ast.isCall.nameExact("update").l.filter(u => receiver(u).exists(workerNames.contains) &&
            arg(u, 0).flatMap(origin(_, Set.empty, 0)).exists(_.id == producer.id) &&
            arg(u, 1).exists(v => method.parameter.index(1).exists(_.name == v.code.trim)))
          stored.toVector.flatMap(s => updates.map(u => CbcUse(factory, constructors.head, defaultIv.get, c, s, producer, u)))
      }.toVector
    }.headOption
  }

  private def origin(e: Expression, seen: Set[Long], depth: Int): Option[Expression] =
    if (depth > 8 || seen(e.id)) None
    else e match {
      case _: Identifier => latest(e).flatMap(v => origin(v, seen + e.id, depth + 1))
      case b: Block => b.astChildren.collect { case v: Expression => v }.toVector.lastOption
        .flatMap(v => origin(v, seen + e.id, depth + 1))
      case _ => Some(e)
    }

  /** The frontend discards expanded positional/keyword arguments. Check the exact balanced source call. */
  private def sourceCallArguments(c: Call): Option[String] = {
    val lines = contents.getOrElse(file(c), "").linesIterator.toVector
    val text = lines.slice(c.lineNumber.getOrElse(0) - 1, c.lineNumber.getOrElse(0) + 31).mkString("\n")
    val start = ("\\b" + java.util.regex.Pattern.quote(c.name) + "\\s*\\(").r.findFirstMatchIn(text)
    start.flatMap { matched =>
      val buf = new StringBuilder
      var depth = 0
      var quote = ' '
      var escaped = false
      var done = false
      text.drop(matched.end).iterator.takeWhile(_ => !done).foreach { ch =>
        if (quote != ' ') {
          buf.append(ch)
          if (escaped) escaped = false
          else if (ch == '\\') escaped = true
          else if (ch == quote) quote = ' '
        } else if (ch == '\'' || ch == '"') { quote = ch; buf.append(ch) }
        else if (ch == ')' && depth == 0) done = true
        else { if ("([{ ".trim.contains(ch)) depth += 1; if (")] }".replace(" ", "").contains(ch)) depth -= 1; buf.append(ch) }
      }
      Option.when(done)(buf.toString)
    }
  }

  private def fieldReference(e: Expression, seen: Set[Long], depth: Int): Option[String] =
    if (depth > 8 || seen(e.id)) None
    else if (e.code.trim.matches("self\\.[A-Za-z_][A-Za-z0-9_]*")) Some(e.code.trim)
    else latest(e).flatMap(v => fieldReference(v, seen + e.id, depth + 1))

  private def classMethod(f: String, cls: String, name: String, seen: Set[String]): Vector[Method] = {
    if (seen(cls) || seen.size > 4) return Vector.empty
    val direct = byName.getOrElse(name, Vector.empty).filter(m => file(m) == f && owner(m) == cls)
    if (direct.nonEmpty) direct
    else {
      parents(f, cls).flatMap(p => classMethod(f, p, name, seen + cls))
    }
  }

  private def parents(f: String, cls: String): Vector[String] = {
    val pattern = ("(?m)^class\\s+" + java.util.regex.Pattern.quote(cls) + "\\s*\\(([^)]*)\\)\\s*:").r
    val found = pattern.findFirstMatchIn(contents.getOrElse(f, "")).toVector.flatMap(_.group(1).split(",").map(_.trim))
    if (found.size == 1 && found.head.matches("[A-Za-z_][A-Za-z0-9_]*")) found else Vector.empty
  }

  private def definitions(c: Call): Vector[Method] = {
    val prefix = c.code.takeWhile(_ != '(').trim
    val parts = prefix.split("\\.").toVector
    val base = parts.headOption.getOrElse("")
    if (base != "self" && (c.method.parameter.nameExact(base).nonEmpty ||
      assignmentsByFile.getOrElse(file(c), Vector.empty).exists(a => lhs(a) == base &&
        (a.method.id == c.method.id || a.method.name == "<module>") && !arg(a, 2).exists {
          case i: Call => i.name == "import"
          case ref: io.shiftleft.codepropertygraph.generated.nodes.TypeRef => ref.typeFullName.stripSuffix("<meta>").endsWith("." + base)
          case n => n.label == "METHOD_REF"
        }))) return Vector.empty
    val local = byName.getOrElse(c.name, Vector.empty).filter(m => file(m) == file(c) &&
      (if (parts.headOption.contains("self")) owner(m) == owner(c.method)
       else if (parts.size > 1) owner(m) == base else owner(m) == "<module>"))
    if (local.nonEmpty) local
    else imports.getOrElse(file(c), Vector.empty).flatMap { i =>
      "import\\(([^,)]+),\\s*([^,)]+)(?:,\\s*([^,)]+))?\\)".r.findFirstMatchIn(i.code).toVector.flatMap { matched =>
        val module = matched.group(1).trim
        val member = matched.group(2).trim
        val alias = Option(matched.group(3)).getOrElse(member).trim
        if (base != alias) Vector.empty
        else byName.getOrElse(if (parts.size == 1) member else c.name, Vector.empty).filter(m =>
          file(m).endsWith(module.replace('.', '/') + ".py") &&
            (if (parts.size == 1) owner(m) == "<module>" else owner(m) == member))
      }
    }.distinctBy(_.id)
  }

  private def genuine(m: Method, module: String): Boolean =
    imports.getOrElse(file(m), Vector.empty).exists(_.code == s"import(, $module)") &&
      m.parameter.nameExact(module).isEmpty && !assignmentsByFile.getOrElse(file(m), Vector.empty).exists(a =>
        (lhs(a) == module || lhs(a).startsWith(module + ".")) &&
        (a.method.id == m.id || a.method.name == "<module>") && !arg(a, 2).exists { case c: Call => c.name == "import"; case _ => false })
  private def emptySeparator(c: Call): Boolean =
    arg(c, 0).exists(v => Set("\"\"", "''", "b\"\"", "b''").contains(v.code.trim) ||
      latest(v).exists(x => Set("\"\"", "''", "b\"\"", "b''").contains(x.code.trim)))
  private def latestAssignment(e: Expression): Option[Call] = {
    val candidates = writes.getOrElse(e.method.id -> e.code.trim, Vector.empty).filter(a =>
      a.lineNumber.getOrElse(Int.MaxValue) <= e.lineNumber.getOrElse(Int.MaxValue) && !a.ast.id(e.id).nonEmpty)
    candidates.sortBy(a => (a.lineNumber.getOrElse(0), a.columnNumber.getOrElse(0))).lastOption
      .filterNot(_.inAst.isControlStructure.nonEmpty)
  }
  private def latest(e: Expression): Option[Expression] = latestAssignment(e).flatMap(arg(_, 2))
  private def receiver(c: Call): Option[String] = {
    val prefix = c.code.takeWhile(_ != '(').trim
    Option.when(prefix.endsWith("." + c.name))(prefix.stripSuffix("." + c.name))
  }
  private def owner(m: Method): String = m.fullName.stripSuffix("." + m.name).split("\\.").last.split(":").last
  private def lhs(c: Call): String = arg(c, 1).map(_.code.trim).getOrElse("")
  private def arg(c: Call, n: Int, keyword: String = ""): Option[Expression] = c.argument.l
    .find(v => keyword.nonEmpty && v.argumentName.contains(keyword))
    .orElse(c.argument.l.find(v => v.argumentIndex == n && v.argumentName.isEmpty))
  private def file(n: AstNode): String = n.file.name.headOption.getOrElse("")
}
