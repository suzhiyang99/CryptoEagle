package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, CryptoOperation, Expression, Identifier, Literal, Method}
import io.shiftleft.semanticcpg.language.*

/** Bounded evidence for protocol primitives and ciphertext authentication. Unknown
  * callers/branches never establish a suppression; source paths are not allowlists.
  */
private[standalone] final class PythonProtocolEvidence(cpg: Cpg) {
  private lazy val securityFlow = new org.codeminers.standalone.enrichment.SecurityFlowContext(cpg)
  private lazy val methods = cpg.method.l.filterNot(_.isExternal).groupBy(_.name)
  private lazy val calls = cpg.call.l
  private lazy val imports = calls.filter(_.name == "import").groupBy(file)
  private lazy val writes = calls.filter(_.name == "<operator>.assignment").groupBy(c => c.method.id -> arg(c, 1).map(_.code.trim).getOrElse(""))
  private case class Bound(e: Expression, env: Map[String, Bound])

  def isTotpMac(o: CryptoOperation): Boolean = o.srcFile.endsWith(".py") && o.algorithm == "SHA1" &&
    o.realizingCall.l.exists { c =>
      val m = c.method
      val text = body(m)
      val input = arg(c, 2, "msg").map(e => expanded(e, Set.empty, 0)).getOrElse("")
      genuine(m, "hmac") && genuine(m, "hashlib") && genuine(m, "struct") && genuine(m, "time") &&
        input.contains("struct.pack(") && (input.contains("\">Q\"") || input.contains("'>Q'")) &&
        (input.contains("time.time()//30") || input.contains("int(time.time())//30")) &&
        text.contains("&0x7fffffff") && text.contains("%1000000") &&
        m.ast.isCall.l.exists(n => compact(n.code).matches("(?s).*(?:15&.*\\[19\\]|\\[19\\].*&15).*" ) &&
          depends(n, o.realizingCall.l.map(_.id).toSet, Map.empty, 0, Set.empty)) &&
        m.ast.isReturn.astChildren.collect { case e: Expression => e }.exists(e =>
          depends(e, o.realizingCall.l.map(_.id).toSet, Map.empty, 0, Set.empty))
    }

  def isProtocolPrimitive(o: CryptoOperation): Boolean = {
    if (!o.srcFile.endsWith(".py") || o.algorithm != "AES" || !Set("ECB", "CBC").contains(o.mode)) return false
    o.realizingCall.method.l.distinctBy(_.id).exists { m =>
      val sites = callers(m)
      sites.nonEmpty && sites.forall { site =>
        if (o.mode == "ECB") permissionEncrypt(site) || permissionDecrypt(site)
        else pdfHashRound(site) || pdfKeyEnvelope(site)
      }
    }
  }

  private def permissionEncrypt(c: Call): Boolean = {
    val data = arg(c, 2).map(e => expanded(e, Set.empty, 0)).getOrElse("")
    byteWidth(arg(c, 2), 0, Set.empty).contains(16) && genuine(c.method, "struct") && genuine(c.method, "secrets") &&
      data.contains("struct.pack(") && data.contains("<I") && data.contains("adb") && data.contains("\\xff") &&
      data.contains("secrets.token_bytes(4)") && data.contains("T") && data.contains("F") &&
      returns(c.method).exists(e => depends(e, Set(c.id), Map.empty, 0, Set.empty))
  }
  private def permissionDecrypt(c: Call): Boolean = {
    genuine(c.method, "struct") && c.method.ast.isCall.l.exists { comparison =>
      comparison.name == "<operator>.equals" && comparison.argument.l.exists(e =>
        byteWidth(Some(e), 0, Set.empty).contains(12) && expanded(e, Set.empty, 0).contains("adb")) &&
        comparison.argument.l.exists(e => compact(e.code).endsWith("[:12]") &&
          depends(e, Set(c.id), Map.empty, 0, Set.empty)) &&
        returns(c.method).exists(e => depends(e, Set(comparison.id), Map.empty, 0, Set.empty))
    }
  }
  private def pdfHashRound(c: Call): Boolean = {
    val text = body(c.method)
    val result = c.inAst.isCall.nameExact("<operator>.assignment").l.headOption.flatMap(arg(_, 1)).map(_.code.trim).getOrElse("")
    result.nonEmpty && arg(c, 1).exists(e => compact(e.code).endsWith("[:16]")) &&
      arg(c, 2).exists(e => compact(e.code).endsWith("[16:32]")) &&
      arg(c, 3).exists(e => compact(e.code).endsWith("*64")) && genuine(c.method, "hashlib") &&
      text.contains("hashlib.sha256") && text.contains("hashlib.sha384") && text.contains("hashlib.sha512") &&
      text.contains(s"sum($result[:16])%3") && text.contains("count>=64") && text.contains(s"$result[-1]<=count-32") &&
      returns(c.method).exists(e => compact(e.code).endsWith("[:32]"))
  }
  private def pdfKeyEnvelope(c: Call): Boolean = {
    val text = body(c.method)
    val data = arg(c, 3)
    val constructors = c.method.ast.isCall.nameExact("calculate_hash").l
    byteWidth(data, 0, Set.empty).contains(32) && byteWidth(arg(c, 2), 0, Set.empty).contains(16) &&
      genuine(c.method, "secrets") && text.contains("secrets.token_bytes(16)") && text.contains("password[:127]") &&
      constructors.nonEmpty && constructors.forall(h => definitions(h).exists(m => m.ast.isCall.l.exists(pdfHashRound))) &&
      returns(c.method).exists(e => depends(e, Set(c.id), Map.empty, 0, Set.empty) && expanded(e, Set.empty, 0).contains("calculate_hash("))
  }

  def ciphertextAuthenticated(o: CryptoOperation): Boolean = {
    if (o.providesIntegrity) return true
    val output = o.realizingCall.l.filter(c => Set("encrypt", "update", "finalize").contains(c.name)).map(_.id).toSet
    if (output.isEmpty) return false
    val m = o.realizingCall.method.headOption
    m.exists(method => protectedResult(method, output, Map.empty) || {
      val sites = callers(method)
      sites.nonEmpty && sites.forall(site => protectedResult(site.method, Set(site.id), Map.empty))
    })
  }
  private def protectedResult(m: Method, ciphertext: Set[Long], env: Map[String, Bound]): Boolean = {
    val results = returns(m).filterNot(_.code.trim == "None")
    val macs = cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.filter(o => o.opType == "MAC_SIGNATURE" && o.operation == "MAC" &&
      o.resolution == "COMPLETE" && o.realizingCall.method.l.exists(_.id == m.id))
    val tags = macs.filter(o => macCoversCiphertext(o, m, ciphertext, env)).flatMap(_.realizingCall.l.map(_.id)).toSet
    val direct = results.nonEmpty && results.forall(e => depends(e, ciphertext, env, 0, Set.empty) &&
      depends(e, tags, env, 0, Set.empty))
    val wrappers = m.ast.isCall.l.filter(c => !c.name.startsWith("<operator>."))
      .filter(c => definitions(c).size == 1).filter { site =>
        val method = definitions(site).head
        val bindings = method.parameter.l.flatMap(p => arg(site, p.index, p.name).map(e => p.name -> Bound(e, env))).toMap
        val sinks = cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.filter(o => o.opType == "MAC_SIGNATURE" && o.operation == "MAC" &&
          o.realizingCall.method.l.exists(_.id == method.id))
        val matched = sinks.filter(o => macCoversCiphertext(o, method, ciphertext, bindings)).flatMap(_.realizingCall.l.map(_.id)).toSet
        val outputs = returns(method).filterNot(_.code.trim == "None")
        matched.nonEmpty && outputs.nonEmpty && outputs.forall(e => depends(e, matched, bindings, 0, Set.empty))
      }.map(_.id).toSet
    direct || (wrappers.nonEmpty && results.nonEmpty && results.forall(e =>
      depends(e, ciphertext, env, 0, Set.empty) && depends(e, wrappers, env, 0, Set.empty)))
  }

  private def macCoversCiphertext(o: CryptoOperation, m: Method, ciphertext: Set[Long], env: Map[String, Bound]): Boolean = {
    val constructors = o.realizingCall.l.filter(_.name == "new")
    val ids = constructors.map(_.id).toSet
    def owned(c: Call): Boolean = c.argument.l.find(_.argumentIndex == 0)
      .exists(e => depends(e, ids, env, 0, Set.empty))
    val updates = m.ast.isCall.nameExact("update").l.filter(owned)
    val digests = m.ast.isCall.nameExact("digest", "hexdigest").l.filter(owned)
    val input = constructors.flatMap(arg(_, 2, "msg")) ++ updates.filter(u =>
      digests.nonEmpty && digests.forall(d => u.lineNumber.exists(n => d.lineNumber.exists(_ >= n)) && securityFlow.dominates(u, d)))
      .flatMap(arg(_, 1, "msg"))
    ids.nonEmpty && ciphertext.nonEmpty && ciphertext.forall(t => input.exists(e =>
      depends(e, Set(t), env, 0, Set.empty, whole = true)))
  }

  private def depends(e: Expression, targets: Set[Long], env: Map[String, Bound], depth: Int, seen: Set[Long], whole: Boolean = false): Boolean = {
    if (targets(e.id)) return true
    if (depth > 14 || seen(e.id) || targets.isEmpty) return false
    val next = seen + e.id
    e match {
      case i: Identifier =>
        val assigned = definitionsOf(i)
        if (assigned.nonEmpty) assigned.forall(v => depends(v, targets, env, depth + 1, next, whole))
        else env.get(i.name).exists(b => depends(b.e, targets, b.env, depth + 1, next, whole))
      case c: Call if whole && Set("<operator>.slice", "<operator>.indexAccess").contains(c.name) => false
      case c: Call if c.name == "<operator>.conditional" =>
        val branches = c.argument.l.filter(_.argumentIndex > 1)
        branches.nonEmpty && branches.forall(v => depends(v, targets, env, depth + 1, next, whole))
      case c: Call if c.name.startsWith("<operator>.") || Set("digest", "hexdigest", "encode", "decode", "format", "b64encode").contains(c.name) =>
        c.argument.l.exists(v => depends(v, targets, env, depth + 1, next, whole))
      case c: Call if c.code.startsWith("struct.unpack(") && genuine(c.method, "struct") =>
        arg(c, 2).exists(v => depends(v, targets, env, depth + 1, next, whole))
      case b: io.shiftleft.codepropertygraph.generated.nodes.Block =>
        b.astChildren.collect { case v: Expression => v }.toVector.lastOption.exists(v => depends(v, targets, env, depth + 1, next, whole))
      case _ => false
    }
  }
  private def byteWidth(value: Option[Expression], depth: Int, seen: Set[Long]): Option[Int] = value.flatMap { e =>
    if (depth > 12 || seen(e.id)) None else {
      val next = seen + e.id
      def width(v: Expression) = byteWidth(Some(v), depth + 1, next)
      e match {
        case l: Literal if l.code.matches("(?s)b[\"'].*[\"']") =>
          val text = l.code.drop(2).dropRight(1)
          Some("\\\\x[0-9a-fA-F]{2}|\\\\.|[^\\\\]".r.findAllIn(text).size)
        case i: Identifier =>
          val local = definitionsOf(i)
          val incoming = if (local.nonEmpty) local else i.method.parameter.nameExact(i.name).l.flatMap(p => callers(i.method).flatMap(c => arg(c, p.index, p.name)))
          val sizes = incoming.map(width)
          if (sizes.nonEmpty && sizes.forall(_.nonEmpty) && sizes.flatten.distinct.size == 1) sizes.head else None
        case c: Call if c.name == "<operator>.conditional" =>
          val widths = c.argument.l.filter(_.argumentIndex > 1).map(width)
          if (widths.nonEmpty && widths.forall(_.nonEmpty) && widths.flatten.distinct.size == 1) widths.head else None
        case c: Call if c.name == "<operator>.addition" =>
          val values = c.argument.l.filter(_.argumentIndex > 0).map(width)
          if (values.nonEmpty && values.forall(_.nonEmpty)) Some(values.flatten.sum) else None
        case c: Call if c.name == "<operator>.multiplication" =>
          for (left <- arg(c, 1).flatMap(width); right <- arg(c, 2).flatMap(_.code.toIntOption)) yield left * right
        case c: Call if c.code.startsWith("struct.pack(") && genuine(c.method, "struct") &&
          arg(c, 1).exists(e => Set("\"<I\"", "'<I'").contains(e.code)) => Some(4)
        case c: Call if c.code.startsWith("secrets.token_bytes(") && genuine(c.method, "secrets") => arg(c, 1).flatMap(_.code.toIntOption)
        case c: Call if compact(c.code).matches("bytes\\(0for[^)]*range\\(16\\).*" ) => Some(16)
        case b: io.shiftleft.codepropertygraph.generated.nodes.Block => b.astChildren.collect { case v: Expression => v }.toVector.lastOption.flatMap(width)
        case _ => None
      }
    }
  }
  private def definitionsOf(e: Expression): Vector[Expression] = writes.getOrElse(e.method.id -> e.code.trim, Nil)
    .filter(a => a.lineNumber.getOrElse(Int.MaxValue) <= e.lineNumber.getOrElse(Int.MaxValue))
    .flatMap(arg(_, 2)).toVector
  private def expanded(e: Expression, seen: Set[Long], depth: Int): String = {
    if (depth > 12 || seen(e.id)) return ""
    val next = seen + e.id
    compact(e.code) + e.ast.isIdentifier.l.flatMap(i => definitionsOf(i).map(v => expanded(v, next, depth + 1))).mkString
  }
  private def genuine(m: Method, module: String): Boolean =
    imports.getOrElse(file(m), Nil).exists(_.code == s"import(, $module)") &&
      m.parameter.nameExact(module).isEmpty && !writes.exists { case ((id, name), values) =>
        (name == module || name.startsWith(module + ".")) && values.exists(a => file(a) == file(m) && (id == m.id || a.method.name == "<module>") &&
          !arg(a, 2).exists { case c: Call => c.name == "import"; case _ => false })
      }
  private def definitions(c: Call): Vector[Method] = {
    val prefix = c.code.takeWhile(_ != '(').trim
    val candidates = methods.getOrElse(c.name, Nil).toVector
    val base = prefix.takeWhile(_ != '.')
    if (c.method.parameter.nameExact(base).nonEmpty || writes.exists { case ((id, name), values) =>
      (name == base || name == prefix) && values.exists(a => file(a) == file(c) &&
        (id == c.method.id || a.method.name == "<module>") && arg(a, 2).exists {
          case imported: Call => imported.name != "import"
          case ref: io.shiftleft.codepropertygraph.generated.nodes.TypeRef => !ref.typeFullName.stripSuffix("<meta>").endsWith("." + base)
          case ref => ref.label != "METHOD_REF"
        })
    }) return Vector.empty
    val local = candidates.filter(m => file(m) == file(c) &&
      (!prefix.contains('.') || m.fullName.endsWith("." + prefix)))
    if (local.nonEmpty) local
    else imports.getOrElse(file(c), Nil).flatMap(i => imported(i.code)).filter { case (_, member, alias) =>
      member == c.name && alias == prefix
    }.flatMap { case (module, member, _) => exports(module, member, Set.empty) }.distinctBy(_.id).toVector
  }
  private def exports(module: String, symbol: String, seen: Set[String]): Vector[Method] = {
    if (seen(module) || seen.size > 4) return Vector.empty
    val suffix = module.replace('.', '/')
    val files = cpg.file.name.l.filter(f => f.endsWith(suffix + ".py") || f.endsWith(suffix + "/__init__.py"))
    files.flatMap { f =>
      methods.getOrElse(symbol, Nil).filter(m => file(m) == f) ++
        imports.getOrElse(f, Nil).flatMap(i => imported(i.code)).filter(_._3 == symbol)
          .flatMap { case (parent, member, _) => exports(parent, member, seen + module) }
    }.distinctBy(_.id).toVector
  }
  private def imported(code: String): Vector[(String, String, String)] =
    "import\\(([^,)]+),\\s*([^,)]+)(?:,\\s*([^,)]+))?\\)".r.findFirstMatchIn(code)
      .map(m => (m.group(1).trim, m.group(2).trim, Option(m.group(3)).getOrElse(m.group(2)).trim)).toVector
  private def callers(m: Method): Vector[Call] = calls.filter(c => c.name == m.name && c.method.id != m.id && definitions(c).exists(_.id == m.id)).toVector
  private def returns(m: Method): Vector[Expression] = m.ast.isReturn.astChildren.collect { case e: Expression => e }.toVector
  private def body(m: Method): String = compact(m.ast.isCall.code.l.mkString(" "))
  private def compact(s: String): String = s.replaceAll("\\s+", "")
  private def arg(c: Call, n: Int, keyword: String = ""): Option[Expression] = c.argument.l.find(a => keyword.nonEmpty && a.argumentName.contains(keyword))
    .orElse(c.argument.l.find(a => a.argumentIndex == n && a.argumentName.isEmpty))
  private def file(n: AstNode): String = n.file.name.headOption.getOrElse("")
}
