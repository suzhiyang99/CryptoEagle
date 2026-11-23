package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Block, Call, CryptoOperation, Expression, Identifier, Literal, Method}
import io.shiftleft.semanticcpg.language.*

/** Proves a transformation of the actual KEY, independently of password-KDF strength.
  * Unknown/raw alternatives invalidate the proof. Names, file allowlists and a hash
  * elsewhere in the method do not establish that a key was transformed.
  */
final class PythonKeyTransformationResolver(cpg: Cpg) {
  private case class Bound(value: Expression, env: Map[String, Bound])
  private case class Value(hashes: Vector[Call], neutral: Boolean = false)
  private val empty = Value(Vector.empty, neutral = true)
  private lazy val methods = cpg.method.l.filterNot(_.isExternal).groupBy(_.name)
  private lazy val writes = cpg.call.nameExact("<operator>.assignment", "<operator>.assignmentPlus").l
    .groupBy(a => a.method.id -> lhs(a))
  private lazy val calls = cpg.call.l.groupBy(_.method.id)
  private lazy val importCalls = cpg.call.nameExact("import").l.groupBy(file)
  private val resultCache = collection.mutable.HashMap.empty[Long, Vector[Call]]

  def keyTransformations(operation: CryptoOperation): Vector[Call] = resultCache.getOrElseUpdate(operation.id, {
    if (!operation.srcFile.endsWith(".py") || !Set("SYMMETRIC_ENCRYPTION", "ASYMMETRIC_ENCRYPTION", "MAC_SIGNATURE").contains(operation.opType)) Vector.empty
    else {
      val keys = operation.realizingCall.l.flatMap { c =>
        if (Set("new", "ARC4Cipher", "encryptor_create", "decryptor_create").contains(c.name)) argument(c, 1, "key").toVector
        else if (c.name == "Cipher") argument(c, 1, "algorithm").toVector.flatMap {
          case algorithm: Call if algorithm.argument.l.count(_.argumentIndex > 0) == 1 =>
            argument(algorithm, 1, "key").toVector
          case _ => Vector.empty
        }
        else Vector.empty
      }
      merge(keys.map(v => follow(v, Map.empty, 0, Set.empty, None))).filter(_.hashes.nonEmpty)
        .map(_.hashes.distinctBy(_.id)).getOrElse(Vector.empty)
    }
  })

  /** A rehash of a proven digest is not a new direct-password hashing instance.
    * Preserve the graph's password origin for whole-chain analysis; only refine
    * the input role of this particular hash API. Raw/unknown alternatives keep C6.
    */
  def hashInputAlreadyTransformed(operation: CryptoOperation): Boolean = {
    if (!operation.srcFile.endsWith(".py") || operation.opType != "HASH_FUNCTION") false
    else {
      val sites = operation.realizingCall.l
      sites.nonEmpty && sites.forall(c => argument(c, 1).exists(input =>
        follow(input, Map.empty, 0, Set.empty, None).exists(value => value.hashes.nonEmpty &&
          value.hashes.forall(hash => constructorAlgorithm(hash, Map.empty, 0).nonEmpty))))
    }
  }

  /** Model a proven hash-object update at its actual API position. This is needed
    * for legacy derivation functions whose digest constructor is a callback.
    */
  private case class HashUpdate(algorithm: String, passwords: Vector[Expression])
  private val updateCache = collection.mutable.HashMap.empty[Long, Option[HashUpdate]]
  def hashUpdateAlgorithm(update: Call): Option[String] = hashUpdate(update).map(_.algorithm)
  def hashUpdatePasswordSources(update: Call): Vector[Expression] =
    hashUpdate(update).map(_.passwords).getOrElse(Vector.empty)

  private def hashUpdate(update: Call): Option[HashUpdate] = updateCache.getOrElseUpdate(update.id, {
    if (update.name != "update" || !file(update).endsWith(".py")) None
    else {
      val constructors = localWrites(receiverOf(update), update, Set.empty).flatMap(rhs).collect { case c: Call => c }
      val ctor = constructors.headOption.filter(_ => constructors.size == 1)
        .filter(c => c.method.parameter.nameExact(c.name).nonEmpty)
      val material = argument(update, 1).flatMap(e => transparentParameters(e, Map.empty, 0, Set.empty))
      val sites = calls.valuesIterator.flatten.filter(c => c.name == update.method.name &&
        c.method.id != update.method.id && definitions(c).exists(_.id == update.method.id)).toVector
      val passwords = material.toVector.flatMap(_.toVector).flatMap { name =>
        update.method.parameter.nameExact(name).l.flatMap(p => sites.flatMap(site => argument(site, p.index, p.name)))
      }
      val rawPasswords = passwords.filter(e => transparentParameters(e, Map.empty, 0, Set.empty)
        .exists(_.exists(_.matches("(?i).*(?:password|passwd|passphrase|pwd).*"))))
      val algorithms = ctor.toVector.flatMap(c => sites.map(s => constructorAlgorithm(c, bindings(update.method, s, Map.empty), 0)))
      if (sites.nonEmpty && passwords.nonEmpty && rawPasswords.size == passwords.size &&
        algorithms.nonEmpty && algorithms.forall(_.nonEmpty) && algorithms.flatten.distinct.size == 1)
        Some(HashUpdate(algorithms.flatten.head, rawPasswords.distinctBy(_.id)))
      else None
    }
  })

  /** Only identity/encoding wrappers preserve raw password provenance. A hash,
    * KDF, arbitrary callback, reassignment or mixed return invalidates this proof.
    */
  private def transparentParameters(e: Expression, env: Map[String, Bound], depth: Int,
    seen: Set[Long]): Option[Set[String]] = {
    if (depth > 12 || seen(e.id)) return None
    val next = seen + e.id
    def go(v: Expression): Option[Set[String]] = transparentParameters(v, env, depth + 1, next)
    e match {
      case i: Identifier => env.get(i.name) match {
        case Some(b) if localWrites(i.name, i, Set.empty).isEmpty => transparentParameters(b.value, b.env, depth + 1, next)
        case None if i.method.parameter.nameExact(i.name).nonEmpty && localWrites(i.name, i, Set.empty).isEmpty => Some(Set(i.name))
        case _ => None
      }
      case c: Call if Set("encode", "decode").contains(c.name) => c.argument.l.find(_.argumentIndex == 0).flatMap(go)
      case c: Call if !c.name.startsWith("<operator>.") =>
        val defs = definitions(c)
        if (defs.size != 1) None else {
          val method = defs.head
          val bound = bindings(method, c, env)
          val values = method.ast.isReturn.l.flatMap(_.astChildren.collect { case v: Expression => v })
            .map(v => transparentParameters(v, bound, depth + 1, next))
          if (values.nonEmpty && values.forall(_.nonEmpty) && values.flatten.distinct.size == 1) values.head else None
        }
      case _ => None
    }
  }

  private def merge(values: Iterable[Option[Value]]): Option[Value] = {
    val paths = values.toVector
    if (paths.isEmpty || paths.exists(_.isEmpty)) None
    else Some(Value(paths.flatten.flatMap(_.hashes).distinctBy(_.id), paths.flatten.forall(_.neutral)))
  }

  private def follow(e: Expression, env: Map[String, Bound], depth: Int,
    seen: Set[(Long, String)], selected: Option[Int]): Option[Value] = {
    val text = e.code.trim
    val mark = e.id -> env.keys.toVector.sorted.mkString(",")
    if (depth > 24 || seen(mark)) return None
    val next = seen + mark
    def go(v: Expression, selection: Option[Int] = selected): Option[Value] = follow(v, env, depth + 1, next, selection)
    def symbol(name: String): Option[Value] = env.get(name) match {
      case Some(b) =>
        val assignments = localWrites(name, e, e.inAst.isCall.l.map(_.id).toSet).flatMap(rhs)
        if (assignments.nonEmpty) merge(assignments.map(go(_)))
        else follow(b.value, b.env, depth + 1, next, selected)
      case None =>
        val assignments = localWrites(name, e, e.inAst.isCall.l.map(_.id).toSet).flatMap(rhs)
        val fields = if (name.startsWith("self.")) writes.valuesIterator.flatten.filter(a => lhs(a) == name &&
          a.method.fullName.stripSuffix("." + a.method.name) == e.method.fullName.stripSuffix("." + e.method.name)).flatMap(rhs).toVector else Vector.empty
        if (assignments.nonEmpty) merge(assignments.map(go(_)))
        else if (fields.nonEmpty) merge(fields.map(v => follow(v, Map.empty, depth + 1, next, selected)))
        else {
          val param = e.method.parameter.nameExact(name).headOption
          val incoming = param.toVector.flatMap(p => calls.valuesIterator.flatten.filter(c => c.name == e.method.name &&
            c.method.id != e.method.id && definitions(c).exists(_.id == e.method.id)).flatMap(c => argument(c, p.index, p.name)))
          merge(incoming.map(v => follow(v, Map.empty, depth + 1, next, selected)))
        }
    }
    e match {
      case l: Literal if l.code == "None" => Some(empty) // invalid/uninitialized key, never raw password bytes
      case l: Literal if l.code.matches("(?i)[br]*['\"]{2}") => Some(empty)
      case b: Block => b.astChildren.collect { case v: Expression => v }.toVector.lastOption.flatMap(go(_))
      case c: Call if c.name == "<operator>.assignment" => argument(c, 2).flatMap(go(_))
      case c: Call if c.name == "<operator>.slice" => argument(c, 1).flatMap(v => go(v, None))
      case c: Call if c.name == "<operator>.indexAccess" =>
        val index = argument(c, 2).flatMap(v => v.code.toIntOption)
        argument(c, 1).flatMap(v => go(v, index.orElse(selected)))
      case c: Call if c.name == "<operator>.addition" || c.name == "<operator>.assignmentPlus" =>
        merge(c.argument.l.filter(_.argumentIndex > 0).map(go(_, None)))
      case c: Call if text.startsWith("(") || c.name == "<operator>.tuple" =>
        val parts = c.argument.l.filter(_.argumentIndex > 0).sortBy(_.argumentIndex)
        selected match {
          case Some(index) => parts.lift(index).flatMap(v => go(v, None))
          case None => merge(parts.map(go(_, None)))
        }
      case c: Call if Set("digest", "hexdigest").contains(c.name) =>
        val recv = receiverOf(c)
        val constructors = localWrites(recv, c, Set.empty).flatMap(rhs)
        val safeObject = !calls.getOrElse(c.method.id, Nil).exists(other =>
          other.lineNumber.exists(_ <= c.lineNumber.getOrElse(Int.MaxValue)) &&
          !other.name.startsWith("<operator>.") && !Set("update", "digest", "hexdigest").contains(other.name) &&
          other.argument.codeExact(recv).nonEmpty) &&
          !writes.keys.exists { case (methodId, name) => methodId == c.method.id && name.startsWith(recv + ".") }
        val direct = if (constructors.nonEmpty && safeObject)
          merge(constructors.map {
            case ctor: Call => constructorAlgorithm(ctor, env, 0).map(_ => Value(Vector(ctor)))
            case _ => None
          }) else None
        direct.orElse(c.argument.l.find(_.argumentIndex == 0).flatMap(go(_, None)))
      case c: Call if Set("encode", "decode").contains(c.name) =>
        c.argument.l.find(_.argumentIndex == 0).flatMap(go(_, None)).orElse(symbol(receiverOf(c)))
      case c: Call if Set("bytes", "bytearray").contains(c.name) && c.argument.l.forall(_.argumentIndex <= 0) =>
        if (env.contains(c.name) || c.method.parameter.nameExact(c.name).nonEmpty ||
          methods.getOrElse(c.name, Nil).exists(m => file(m) == file(c)) ||
          writes.exists { case ((_, name), assignments) => name == c.name && assignments.exists(a => file(a) == file(c) && !rhs(a).exists(_.code == s"__builtins__.${c.name}")) }) None
        else Some(empty)
      // A bytewise XOR with a public literal-range counter preserves prior key
      // derivation. It does not establish KDF strength or make a raw key derived.
      case c: Call if c.name == "bytes" && builtin(c, "bytes") && builtin(c, "bytearray") && builtin(c, "range") &&
        c.ast.isCall.nameExact("<operator>.genExp").nonEmpty && c.ast.isCall.nameExact("bytearray").nonEmpty =>
        val iterators = c.ast.isCall.nameExact("__iter__").l
        val xors = c.ast.isCall.nameExact("<operator>.xor").l
        val counter = xors.headOption.flatMap(argument(_, 2)).map(_.code.trim)
        val publicLoop = counter.exists(n => writes.getOrElse(c.method.id -> n, Nil).flatMap(rhs).nonEmpty &&
          writes.getOrElse(c.method.id -> n, Nil).flatMap(rhs).forall(publicRangeCounter(_, 0, Set.empty)))
        if (iterators.size == 1 && xors.size == 1 && publicLoop) symbol(receiverOf(iterators.head)) else None
      case c: Call if Set("encrypt", "decrypt", "update", "finalize").contains(c.name) &&
        c.realizedCryptoOperation.l.exists(o => o.opType == "SYMMETRIC_ENCRYPTION" &&
          Set("ENCRYPT", "DECRYPT").contains(o.operation) && o.resolution == "COMPLETE") => Some(Value(Vector(c)))
      case c: Call if c.code.startsWith("secrets.token_bytes(") &&
        importCalls.getOrElse(file(c), Nil).exists(_.code == "import(, secrets)") &&
        c.method.parameter.nameExact("secrets").isEmpty && !writes.exists { case ((_, name), assignments) =>
          name.startsWith("secrets") && assignments.exists(a => file(a) == file(c) && !rhs(a).exists {
            case i: Call => i.name == "import"; case _ => false })
        } => Some(Value(Vector(c)))
      case c: Call if !c.name.startsWith("<operator>.") =>
        val defs = definitions(c)
        if (defs.size != 1) None
        else {
          val method = defs.head
          val bound = bindings(method, c, env)
          val returns = method.ast.isReturn.l.flatMap(_.astChildren.collect { case v: Expression => v })
          merge(returns.map(v => follow(v, bound, depth + 1, next, selected)))
        }
      case _ => symbol(text.replaceAll("\\s+", ""))
    }
  }

  private def publicRangeCounter(e: Expression, depth: Int, seen: Set[Long]): Boolean = {
    if (depth > 8 || seen(e.id)) return false
    val next = seen + e.id
    e match {
      case b: Block => b.astChildren.collect { case v: Expression => v }.toVector.lastOption.exists(publicRangeCounter(_, depth + 1, next))
      case c: Call if c.name == "<operator>.assignment" => argument(c, 2).exists(publicRangeCounter(_, depth + 1, next))
      case c: Call if c.name == "range" && builtin(c, "range") =>
        val values = c.argument.l.filter(_.argumentIndex > 0)
        values.nonEmpty && values.forall(_.code.toIntOption.nonEmpty)
      case c: Call if Set("__iter__", "__next__").contains(c.name) =>
        val values = writes.getOrElse(c.method.id -> receiverOf(c), Nil).flatMap(rhs)
        values.nonEmpty && values.forall(publicRangeCounter(_, depth + 1, next))
      case i: Identifier =>
        val values = writes.getOrElse(i.method.id -> i.name, Nil).flatMap(rhs)
        values.nonEmpty && values.forall(publicRangeCounter(_, depth + 1, next))
      case _ => false
    }
  }

  private def builtin(c: Call, name: String): Boolean = c.method.parameter.nameExact(name).isEmpty &&
    !methods.getOrElse(name, Nil).exists(m => file(m) == file(c)) &&
    !writes.exists { case ((_, symbol), assignments) => symbol == name && assignments.exists(a => file(a) == file(c) && !rhs(a).exists(_.code == s"__builtins__.$name")) }

  private def localWrites(name: String, use: Expression, excluded: Set[Long]): Vector[Call] = {
    val context = use.method
    writes.getOrElse(context.id -> name, Nil).filter(a => !excluded(a.id) &&
      a.lineNumber.getOrElse(Int.MaxValue) <= use.lineNumber.getOrElse(Int.MaxValue)).toVector
  }

  private def constructorAlgorithm(c: Call, env: Map[String, Bound], depth: Int): Option[String] = {
    if (depth > 10) None
    else hashReference(c.code.takeWhile(_ != '(').trim, c, env, depth + 1)
  }

  private def hashReference(name: String, context: Expression, env: Map[String, Bound], depth: Int): Option[String] = {
    if (depth > 10) return None
    val assigned = localWrites(name, context, Set.empty).flatMap(rhs).filterNot { case i: Call => i.name == "import"; case _ => false }
    if (assigned.nonEmpty) {
      val values = assigned.map(v => hashExpression(v, env, depth + 1, Set(context.id)))
      return Option.when(values.forall(_.nonEmpty))(values.flatten.distinct.sorted.mkString("/"))
    }
    env.get(name) match {
      case Some(b) => return hashReference(b.value.code.trim, b.value, b.env, depth + 1)
      case _ =>
    }
    val base = name.takeWhile(_ != '.')
    val rebound = context.method.parameter.nameExact(base).nonEmpty ||
      writes.exists { case ((methodId, symbol), assignments) =>
        (methodId == context.method.id || assignments.exists(a => file(a) == file(context) && a.method.name == "<module>")) &&
          (symbol == base || symbol == name) && assignments.exists(a => !rhs(a).exists {
            case call: Call => call.name == "import"
            case _ => false
          })
      }
    if (rebound) return None
    val imported = importCalls.getOrElse(file(context), Nil).map(_.code)
    val moduleImports = "import\\(,\\s*hashlib(?:,\\s*([^,)]+))?\\)".r
    val aliases = imported.flatMap(s => moduleImports.findFirstMatchIn(s).map(m => Option(m.group(1)).getOrElse("hashlib"))).toSet
    val digest = "(?i)(md5|sha1|sha224|sha256|sha384|sha512)".r
    val parts = name.split("\\.")
    if (parts.length == 2 && aliases(parts(0)) && digest.pattern.matcher(parts(1)).matches()) Some(parts(1).toUpperCase)
    else {
      val directImport = "import\\(hashlib,\\s*(md5|sha1|sha224|sha256|sha384|sha512)(?:,\\s*([^,)]+))?\\)".r
      imported.flatMap(s => directImport.findFirstMatchIn(s).filter(m => Option(m.group(2)).getOrElse(m.group(1)) == name)
        .map(_.group(1).toUpperCase)).distinct match {
        case Seq(one) => Some(one)
        case _ => None
      }
    }
  }

  private def hashExpression(e: Expression, env: Map[String, Bound], depth: Int, seen: Set[Long]): Option[String] = {
    if (depth > 12 || seen(e.id)) return None
    val next = seen + e.id
    def go(v: Expression): Option[String] = hashExpression(v, env, depth + 1, next)
    e match {
      case b: Block => b.astChildren.collect { case v: Expression => v }.toVector.lastOption.flatMap(go)
      case c: Call if c.name == "<operator>.indexAccess" => argument(c, 1).flatMap(go)
      case c: Call if Set("<operator>.tuple", "<operator>.tupleLiteral", "tuple").contains(c.name) =>
        val values = c.argument.l.filter(_.argumentIndex > 0).map(go)
        Option.when(values.nonEmpty && values.forall(_.nonEmpty))(values.flatten.distinct.sorted.mkString("/"))
      case i if localWrites(i.code.trim, i, Set.empty).nonEmpty =>
        val values = localWrites(i.code.trim, i, Set.empty).flatMap(rhs).map(go)
        Option.when(values.nonEmpty && values.forall(_.nonEmpty))(values.flatten.distinct.sorted.mkString("/"))
      case _ => hashReference(e.code.trim, e, env, depth + 1)
    }
  }

  private def definitions(c: Call): List[Method] = {
    val base = c.code.takeWhile(_ != '(').takeWhile(_ != '.').trim
    if (c.method.parameter.nameExact(base).nonEmpty || writes.exists { case ((methodId, name), assignments) =>
      name == base && assignments.exists(a => file(a) == file(c) &&
        (methodId == c.method.id || a.method.name == "<module>") && rhs(a).exists { value =>
          value.label != "METHOD_REF" && (value match {
            case ref: io.shiftleft.codepropertygraph.generated.nodes.TypeRef if ref.typeFullName.stripSuffix("<meta>").endsWith("." + base) => false
            case imported: Call => imported.name != "import"
            case _ => true
          })
        })
    }) return Nil
    val sameFile = methods.getOrElse(c.name, Nil).filter(m => file(m) == file(c))
    if (sameFile.nonEmpty && (!c.code.takeWhile(_ != '(').contains('.') || sameFile.exists(_.fullName.endsWith("." + c.code.takeWhile(_ != '(').trim))))
      sameFile.filter(m => !c.code.takeWhile(_ != '(').contains('.') || m.fullName.endsWith("." + c.code.takeWhile(_ != '(').trim))
    else {
      val prefix = c.code.takeWhile(_ != '(').trim
      val parts = prefix.split("\\.")
      val imports = importCalls.getOrElse(file(c), Nil).map(_.code)
      val modules = imports.flatMap { code =>
        val pattern = "import\\(([^,)]+),\\s*([^,)]+)(?:,\\s*([^,)]+))?\\)".r
        pattern.findFirstMatchIn(code).toVector.flatMap { m =>
          val module = m.group(1).trim; val member = m.group(2).trim
          val alias = Option(m.group(3)).getOrElse(member).trim
          if (parts.length == 1 && alias == c.name) Vector(module)
          else if (parts.length == 2 && alias == parts(0)) Vector(module + "." + member)
          else Vector.empty
        }
      }.distinct
      modules.flatMap(mod => exportedDefinitions(mod, c.name, Set.empty)).distinctBy(_.id).toList
    }
  }

  private def exportedDefinitions(module: String, symbol: String, seen: Set[String]): Vector[Method] = {
    if (seen(module) || seen.size > 4) return Vector.empty
    val suffix = module.replace('.', '/')
    val files = cpg.file.name.l.filter(f => f.endsWith(suffix + ".py") || f.endsWith(suffix + "/__init__.py"))
    files.flatMap(f => methods.getOrElse(symbol, Nil).filter(m => file(m) == f) ++
      importCalls.getOrElse(f, Nil).flatMap { i =>
        "import\\(([^,)]+),\\s*([^,)]+)(?:,\\s*([^,)]+))?\\)".r.findFirstMatchIn(i.code)
          .filter(m => Option(m.group(3)).getOrElse(m.group(2)).trim == symbol)
          .toVector.flatMap(m => exportedDefinitions(m.group(1).trim, m.group(2).trim, seen + module))
      }).distinctBy(_.id).toVector
  }

  private def bindings(method: Method, call: Call, env: Map[String, Bound]): Map[String, Bound] =
    method.parameter.l.flatMap(p => argument(call, p.index, p.name).map(e => p.name -> Bound(e, env))).toMap
  private def argument(c: Call, index: Int, keyword: String = ""): Option[Expression] =
    c.argument.l.find(a => keyword.nonEmpty && a.argumentName.contains(keyword))
      .orElse(c.argument.l.find(a => a.argumentIndex == index && a.argumentName.isEmpty))
  private def receiverOf(c: Call): String = c.code.takeWhile(_ != '(').stripSuffix("." + c.name).trim
  private def lhs(c: Call): String = argument(c, 1).map(_.code.replaceAll("\\s+", "")).getOrElse("")
  private def rhs(c: Call): Option[Expression] = argument(c, 2)
  private def file(n: AstNode): String = n.file.name.headOption.getOrElse("")
}
