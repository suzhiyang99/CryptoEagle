package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Block, Call, Expression, Method}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.*
import org.codeminers.standalone.ontology.CryptoOperationType

/** Library operation models. These add API entrances to the existing ontology, not new misuse rules.
  * Imports, exact symbols and returned values constrain every model; project/file names are never consulted.
  */
private[enrichment] final class PythonCryptoApiModel(cpg: Cpg) {
  private lazy val keyTransforms = new PythonKeyTransformationResolver(cpg)
  private lazy val assignments = cpg.call.nameExact("<operator>.assignment").l.toVector
  private lazy val methods = cpg.method.l.toVector
  private lazy val methodsByName = methods.groupBy(_.name)
  private val modeledCalls = scala.collection.mutable.HashMap.empty[Long, Vector[AdapterMatch]]
  private lazy val imports = cpg.call.nameExact("import").l.groupBy(file).view.mapValues(_.map(_.code).mkString("\n")).toMap

  def accepts(call: Call, matched: AdapterMatch): Boolean =
    matched.rule.id != "python-pycryptodome-pbkdf2" ||
      (Set("Crypto.Protocol.KDF", "Cryptodome.Protocol.KDF").exists(imports.getOrElse(file(call), "").contains) &&
        !methodsByName.getOrElse("PBKDF2", Vector.empty).exists(m => file(m) == file(call) && !m.isExternal))

  def refine(call: Call, matched: AdapterMatch): AdapterMatch = {
    if (matched.rule.id != "python-hmac-new") return matched
    val digest = call.argument.l.find(a => a.argumentName.contains("digestmod") || (a.argumentName.isEmpty && a.argumentIndex == 3))
    val values = digest.toVector.flatMap(e => digestValues(e, call.method, 0, Set.empty)).distinct.sorted
    if (values.nonEmpty) matched.copy(attributes = matched.attributes.updated("algorithm", values.mkString("/"))) else matched
  }

  /** Follow hash callables through a selected dictionary, a returned tuple and exact object fields.
    * Multiple configured digests remain an explicit set: a warning means a weak selectable branch,
    * not that every connection negotiates that digest.
    */
  private def digestValues(e: Expression, context: Method, depth: Int, seen: Set[(Long, String)]): Vector[String] = {
    val text = e.code.trim
    val mark = context.id -> text
    if (depth > 16 || seen(mark)) return Vector.empty
    val next = seen + mark
    val direct = "(?i)^(?:hashlib\\.)?(md5|sha1|sha256|sha384|sha512)(?:\\(\\))?$".r
      .findFirstMatchIn(text).map(_.group(1).toUpperCase).toVector
    if (direct.nonEmpty) return direct
    e match {
      case b: Block =>
        val last = b.astChildren.collect { case v: Expression => v }.toVector.lastOption
        val dictionaryWrites = last.toVector.flatMap { terminal =>
          b.ast.isCall.nameExact("<operator>.assignment").l.filter(a => lhs(a).startsWith(terminal.code.trim + "["))
            .flatMap(rhs)
        }
        (if (dictionaryWrites.nonEmpty) dictionaryWrites else last.toVector)
          .flatMap(v => digestValues(v, context, depth + 1, next))
      case c: Call if c.name == "<operator>.indexAccess" =>
        c.argument.l.find(_.argumentIndex == 1).toVector.flatMap(v => digestValues(v, context, depth + 1, next))
      case c: Call if text.startsWith("{") =>
        // Dictionary keys name algorithms; only values select the digest callable.
        c.ast.isIdentifier.l.flatMap(i => digestValues(i, context, depth + 1, next)).toVector
      case c: Call if text.startsWith("(") || text.matches("[A-Za-z_][A-Za-z0-9_]*\\(\\(.*") =>
        c.argument.l.filter(_.argumentIndex > 0).flatMap(v => digestValues(v, context, depth + 1, next)).toVector
      case c: Call if !c.name.startsWith("<operator>.") =>
        val sameOwner = owner(context)
        val defs = methodsByName.getOrElse(c.name, Vector.empty).filter(m => file(m) == file(context) && owner(m) == sameOwner)
        if (defs.size == 1) defs.flatMap(m => m.ast.isReturn.astChildren.collect { case v: Expression => v }
          .flatMap(v => digestValues(v, m, depth + 1, next))) else Vector.empty
      case _ =>
        val classOwner = owner(context)
        val symbol = if (text.startsWith("self.")) text.stripPrefix("self.") else text
        val local = assignments.filter(a => a.method.id == context.id && lhs(a) == text)
        val field = assignments.filter(a => file(a) == file(context) &&
          (lhs(a) == text || (lhs(a) == symbol && a.method.name == "<body>")) &&
          (owner(a.method) == classOwner || a.method.fullName == classOwner))
        val writes = if (local.nonEmpty) local else field
        writes.flatMap(rhs).flatMap(v => digestValues(v, v.method, depth + 1, next)).distinct
    }
  }

  private def owner(m: Method): String = m.fullName.stripSuffix(s".${m.name}")

  def matchCall(call: Call): Vector[AdapterMatch] = modeledCalls.getOrElseUpdate(call.id, computeMatch(call))

  private def computeMatch(call: Call): Vector[AdapterMatch] = {
    val code = call.code
    val imported = imports.getOrElse(file(call), "")
    val callbackDigest = Option.when(call.name == "update")(keyTransforms.hashUpdateAlgorithm(call)).flatten
    if (callbackDigest.nonEmpty)
      Vector(model("python-proven-hash-update", CryptoOperationType.HashFunction, "HASH", callbackDigest.get,
        bindings = Vector(ParameterBinding(SensitiveParameter.Password, Some(1)))))
    else if (call.name == "as_pem" && imported.contains("M2Crypto") &&
      call.argument.l.exists(a => a.argumentName.contains("cipher") && a.code.matches("(?i).*des_ede3?_cbc.*")))
      Vector(model("python-private-key-pem", CryptoOperationType.SymmetricEncryption, "KEY_EXPORT", "3DES", "CBC"))
    else if (call.name == "rsa_write_key" && imported.contains("M2Crypto") &&
      call.argument.l.exists(_.code.matches(".*\\b(?:m2|M2Crypto\\.m2)\\.des_ede3?_cbc\\(\\).*")))
      Vector(model("python-private-key-export", CryptoOperationType.SymmetricEncryption, "KEY_EXPORT", "3DES", "CBC"))
    else if (Set("encryptor_create", "decryptor_create").contains(call.name) &&
      imported.contains("py_aes_cfb") && code.matches("aes\\.(?:en|de)cryptor_create\\(.*"))
      Vector(model("python-aes-cfb-native", CryptoOperationType.SymmetricEncryption,
        if (call.name == "encryptor_create") "ENCRYPT" else "DECRYPT", "AES", "CFB",
        Vector(ParameterBinding(SensitiveParameter.Key, Some(1)), ParameterBinding(SensitiveParameter.Iv, Some(3)))))
    else if (Set("sign", "verify").contains(call.name) && imported.contains("cryptography.hazmat.primitives.asymmetric") &&
      call.argument.l.exists(_.code.matches(".*hashes\\.(?:SHA1|MD5)\\(\\).*")) &&
      receiverHasPrivateKey(call))
      Vector(model("python-cryptography-signature", CryptoOperationType.MacSignature,
        if (call.name == "sign") "SIGN" else "VERIFY", call.argument.l.find(_.code.matches(".*hashes\\.(?:SHA1|MD5)\\(\\).*"))
          .map(a => if (a.code.contains("MD5")) "MD5" else "SHA1").getOrElse("")))
    else if (call.name == "hash" && imported.contains("passlib")) passwordHashSchemes(call)
    else if (!call.name.startsWith("<operator>.") && call.name != "import") {
      val wrapper = hashWrapper(call, 0, Set.empty)
      wrapper.toVector.map { algorithm =>
        model("python-proven-hash-wrapper", CryptoOperationType.HashFunction, "HASH", algorithm,
          bindings = Vector(ParameterBinding(SensitiveParameter.Password, Some(1))))
      }
    } else Vector.empty
  }

  private def model(id: String, kind: CryptoOperationType, operation: String, algorithm: String,
    mode: String = "", bindings: Vector[ParameterBinding] = Vector.empty): AdapterMatch =
    AdapterMatch(AdapterRule(id, AdapterLanguage.Python, kind, operation,
      if (kind == CryptoOperationType.HashFunction) LifecycleRole.Hash else if (operation == "CONFIGURE") LifecycleRole.Factory else LifecycleRole.Initialize,
      ApiPattern(), sensitiveParameters = bindings), Map("algorithm" -> algorithm, "mode" -> mode), Map.empty)

  private def receiverHasPrivateKey(call: Call): Boolean = {
    val receiver = call.code.takeWhile(_ != '(').stripSuffix(s".${call.name}")
    assignments.exists(a => a.method.id == call.method.id && lhs(a) == receiver &&
      rhs(a).exists(v => (v.code.contains(".private_key(") || v.code.contains(".public_key(")) &&
        v.ast.isCall.code.l.exists(_.matches("(?s)(?:dsa|rsa|ec)\\..*(?:Private|Public)Numbers\\(.*"))))
  }

  private def passwordHashSchemes(call: Call): Vector[AdapterMatch] = {
    val receiver = call.code.takeWhile(_ != '(').stripSuffix(".hash")
    val constructors = assignments.filter(a => a.method.id == call.method.id && lhs(a) == receiver)
      .flatMap(rhs).flatMap(_.ast.isCall.l).filter(c => c.name == "CryptContext" &&
        (c.code.startsWith("passlib.context.") || assignments.exists(a => a.method.id == c.method.id &&
          lhs(a) == c.code.takeWhile(_ != '(').stripSuffix(".CryptContext") && rhs(a).exists(_.code.trim == "passlib.context"))))
    if (constructors.size != 1) return Vector.empty
    val selected = call.argument.l.find(_.argumentName.contains("scheme"))
    val schemes = if (selected.nonEmpty) selected.toVector.flatMap(_.ast.isLiteral.code.l)
    else {
      // **kwargs must actually select from this context's scheme collection. A list of supported
      // schemes alone is not evidence that the hash call may select a weak alternative.
      val lists = constructors.head.argument.l.filter(_.argumentName.contains("schemes"))
        .map(_.code.trim).toSet
      // The frontend drops **kwargs at a call. Read only this exact call's source
      // line to recover the mapping name, then prove its scheme value in the CPG.
      val original = call.file.content.headOption.toVector.flatMap(_.linesIterator.drop(call.lineNumber.getOrElse(0) - 1).take(1)).mkString
      val sourceKwargs = (java.util.regex.Pattern.quote(receiver) + "\\.hash\\(\\s*\\*\\*([A-Za-z_][A-Za-z0-9_]*)\\s*\\)").r
        .findFirstMatchIn(original).map(_.group(1)).toSet
      val kwargs = call.argument.l.map(_.code.trim.stripPrefix("**")).toSet ++ sourceKwargs
      val selections = assignments.filter(a => a.method.id == call.method.id && kwargs.contains(lhs(a)))
        .flatMap(rhs).filter(v => v.ast.isLiteral.code.l.exists(s => s == "'scheme'" || s == "\"scheme\"") &&
          v.ast.isIdentifier.name.l.exists(lists.contains))
      if (selections.isEmpty) Vector.empty
      else assignments.filter(a => a.method.id == call.method.id && lists.contains(lhs(a)))
        .flatMap(rhs).flatMap(_.ast.isLiteral.code.l)
    }
    schemes.map(_.replace("'", "").replace("\"", "")).distinct.collect {
      case "md5_crypt" => model("python-passlib-weak-scheme", CryptoOperationType.HashFunction, "HASH", "MD5_CRYPT")
      case "des_crypt" => model("python-passlib-weak-scheme", CryptoOperationType.HashFunction, "HASH", "DES_CRYPT")
    }
  }

  /** A wrapper is a hash only if its returned value traces to a supported hash constructor.
    * Algorithm parameters are substituted at the wrapper call, not selected from unrelated literals.
    */
  private def hashWrapper(call: Call, depth: Int, seen: Set[Long]): Option[String] = {
    if (depth > 6 || seen(call.id)) return None
    val candidates = methodsByName.getOrElse(call.name, Vector.empty).filter(m => file(m) == file(call)) match {
      case Vector(single) => Vector(single)
      case _ =>
        // Cross-file wrappers require an explicit import of this symbol/module, and a unique definition.
        val imported = imports.getOrElse(file(call), "")
        methodsByName.getOrElse(call.name, Vector.empty).filter(m => imported.contains(m.name)) match {
          case Vector(single) => Vector(single)
          case _ => Vector.empty
        }
    }
    candidates.headOption.flatMap { method =>
      val bindings = method.parameter.l.flatMap(p =>
        call.argument.l.find(a => a.argumentName.contains(p.name) || (a.argumentName.isEmpty && a.argumentIndex == p.index))
          .map(p.name -> _)).toMap
      val returned = method.ast.isReturn.l.flatMap(_.astChildren.collect { case e: Expression => e })
      val algorithms = returned.flatMap(e => hashResult(e, method, bindings, depth + 1, seen + call.id)).distinct
      if (algorithms.size == 1) algorithms.headOption else None
    }
  }

  private def hashResult(e: Expression, context: Method, bindings: Map[String, Expression], depth: Int,
    seen: Set[Long]): Vector[String] = {
    if (depth > 8) return Vector.empty
    e match {
      case block: Block => block.astChildren.collect { case v: Expression => v }.toVector.lastOption.toVector
        .flatMap(v => hashResult(v, context, bindings, depth + 1, seen))
      case c: Call if c.code.matches("(?s)hashlib\\.(?:md5|sha1|sha256|sha512)\\(.*") =>
        "hashlib\\.([a-z0-9]+)\\(".r.findFirstMatchIn(c.code).map(_.group(1).toUpperCase).toVector
      case c: Call if c.code.startsWith("hashlib.new(") =>
        c.argument.l.find(_.argumentIndex == 1).toVector.flatMap { a =>
          val value = bindings.getOrElse(a.code.trim, a).code.trim.replace("'", "").replace("\"", "").toUpperCase
          Option.when(Set("MD5", "SHA1", "SHA256", "SHA512").contains(value))(value)
        }
      case c: Call if Set("digest", "hexdigest").contains(c.name) && c.code.takeWhile(_ != '(').contains('.') =>
        val receiver = c.code.takeWhile(_ != '(').stripSuffix(s".${c.name}")
        val writes = assignments.filter(a => a.method.id == context.id && lhs(a) == receiver)
        if (writes.size == 1) writes.flatMap(rhs).flatMap(v => hashResult(v, context, bindings, depth + 1, seen))
        else c.ast.isCall.l.filterNot(_.id == c.id).flatMap(v => hashResult(v, context, bindings, depth + 1, seen)).toVector
      case c: Call if Set("bytearray", "bytes").contains(c.name) =>
        c.argument.l.filter(_.argumentIndex == 1).flatMap(v => hashResult(v, context, bindings, depth + 1, seen)).toVector
      case c: Call if !c.name.startsWith("<operator>.") => hashWrapper(c, depth + 1, seen).toVector
      case _ =>
        val symbol = e.code.trim
        val writes = assignments.filter(a => a.method.id == context.id && lhs(a) == symbol)
        if (writes.size == 1) writes.flatMap(rhs).flatMap(v => hashResult(v, context, bindings, depth + 1, seen))
        else Vector.empty
    }
  }

  private def lhs(c: Call): String = c.argument.l.find(_.argumentIndex == 1).map(_.code.trim).getOrElse("")
  private def rhs(c: Call): Option[Expression] = c.argument.l.find(_.argumentIndex == 2)
  private def file(n: AstNode): String = n.file.name.headOption.getOrElse("")
}
