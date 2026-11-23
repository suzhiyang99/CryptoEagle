package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Block, Call, CryptoOperation, Expression, Identifier, Literal, Method}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.enrichment.PythonKeyTransformationResolver

/** Evidence for a public web-player query decoder, not a project allowlist.
  * Require the actual hash-derived decryption key, URL-query ciphertext, a key
  * extracted from embedded JavaScript on that same page, and query parsing of
  * the plaintext. Missing callers, mixed uses and unknown values keep C5/C6.
  */
private[rules] final class PythonHashUseEvidence(cpg: Cpg, transforms: PythonKeyTransformationResolver) {
  private val calls = cpg.call.l.toVector
  private val methods = cpg.method.l.filterNot(_.isExternal).toVector
  private val byName = methods.groupBy(_.name)
  private val imports = calls.filter(_.name == "import").groupBy(file)
  private val assignments = calls.filter(_.name == "<operator>.assignment")
  private val writes = assignments.groupBy(a => a.method.id -> lhs(a))
  private val operations = cpg.cryptoOperation.l.toVector
  private val cache = collection.mutable.HashMap.empty[Long, Boolean]

  def publicQueryDecoding(o: CryptoOperation): Boolean = cache.getOrElseUpdate(o.id, {
    o.srcFile.endsWith(".py") && o.opType == "HASH_FUNCTION" && o.realizingCall.l.nonEmpty &&
      o.realizingCall.l.forall { hash =>
        val sites = callers(hash.method)
        sites.nonEmpty && sites.forall { derive =>
          val parent = derive.method
          val consumers = operations.filter(x => x.opType == "SYMMETRIC_ENCRYPTION" &&
            x.realizingCall.method.l.exists(_.id == parent.id))
          consumers.exists(x => x.operation == "DECRYPT" && x.algorithm == "AES" && x.mode == "CBC" &&
            transforms.keyTransformations(x).exists(_.id == hash.id)) &&
          !consumers.exists(_.operation == "ENCRYPT") &&
          callers(parent).nonEmpty && callers(parent).forall(publicDecoderSite) && !escapes(parent) && !escapes(hash.method)
        }
      }
  })

  private case class QuerySource(url: String, line: Int)
  private def publicDecoderSite(site: Call): Boolean = {
    val cipher = arg(site, 1).flatMap(queryCipher(_, Set.empty, 0))
    val page = arg(site, 2).flatMap(pagePassword(_, Set.empty, 0))
    if (cipher.isEmpty || page.isEmpty || cipher.get.url != page.get) return false
    val parsers = site.method.ast.isCall.l.filter(c => definitions(c).exists(queryParser))
    val queryRead = cipher.get.line
    val changedUrl = assignments.exists(a => a.method.id == site.method.id && lhs(a) == cipher.get.url &&
      a.lineNumber.exists(n => n > queryRead && n <= site.lineNumber.getOrElse(Int.MaxValue)))
    val directReturn = site.method.ast.isReturn.astChildren.collect { case e: Expression => e }
      .exists(depends(_, site.id, Set.empty, 0))
    val parsed = parsers.exists(p => arg(p, 1).exists(depends(_, site.id, Set.empty, 0)))
    // A second visible use as a credential/key input invalidates a decoder-only attribution.
    val cryptoUse = operations.filter(x => x.realizingCall.method.l.exists(_.id == site.method.id))
      .exists(x => x.realizingCall.l.exists(c => c.id != site.id && c.argument.l.exists(depends(_, site.id, Set.empty, 0))))
    parsed && !changedUrl && !directReturn && !cryptoUse
  }

  /** The parsed value must be the helper's actual first argument, directly or
    * through a resolved lambda/parser callback. An unrelated parse elsewhere
    * in a helper is insufficient.
    */
  private def queryParser(m: Method): Boolean = {
    val input = m.parameter.filter(_.index == 1).name.headOption
    def parameter(e: Expression, name: String): Boolean =
      origin(e, Set.empty, 0).orElse(Option.when(e.isInstanceOf[Identifier])(e)).exists(v => v.code.trim == name &&
        writes.getOrElse(v.method.id -> name, Vector.empty).isEmpty)
    def parses(method: Method, name: String): Boolean = method.ast.isCall.l.exists(p =>
      importedMember(p, "urllib.parse", "parse_qsl") && arg(p, 1).exists(parameter(_, name)))
    def rawReturn(method: Method, name: String): Boolean = method.ast.isReturn.astChildren
      .collect { case e: Expression => e }.exists(parameter(_, name))
    input.exists { name =>
      !rawReturn(m, name) && (parses(m, name) || m.ast.isCall.l.exists { wrapper =>
        val callback = arg(wrapper, 1).toVector.flatMap(_.ast.isMethodRef.methodFullName.l)
          .flatMap(n => methods.filter(_.fullName == n))
        val wrapperDefs = definitions(wrapper)
        arg(wrapper, 2).exists(parameter(_, name)) && callback.size == 1 && wrapperDefs.size == 1 &&
          callback.head.parameter.filter(_.index == 1).name.headOption.exists(n => parses(callback.head, n) && !rawReturn(callback.head, n)) && {
            val method = wrapperDefs.head
            val parser = method.parameter.filter(_.index == 1).name.headOption
            val data = method.parameter.filter(_.index == 2).name.headOption
            parser.nonEmpty && data.nonEmpty && !rawReturn(method, data.get) && method.ast.isCall.l.exists(c =>
              c.name == parser.get && arg(c, 1).exists(parameter(_, data.get)))
          }
      })
    }
  }

  private def queryCipher(e: Expression, seen: Set[Long], depth: Int): Option[QuerySource] = {
    if (depth > 14 || seen(e.id)) return None
    val next = seen + e.id
    def go(v: Expression) = queryCipher(v, next, depth + 1)
    e match {
      case b: Block => last(b).flatMap(go)
      case i: Identifier => latest(i).flatMap(go)
      case c: Call if importedMember(c, "base64", "b64decode") => arg(c, 1).flatMap(go)
      case c: Call if c.name == "<operator>.fieldAccess" && c.ast.isFieldIdentifier.canonicalName.l.contains("query") =>
        arg(c, 1).flatMap(origin(_, Set.empty, 0)).collect {
          case p: Call if importedMember(p, "urllib.parse", "urlparse") => arg(p, 1).map(v => QuerySource(v.code.trim, p.lineNumber.getOrElse(0)))
        }.flatten
      case _ => None
    }
  }

  private def pagePassword(e: Expression, seen: Set[Long], depth: Int): Option[String] = {
    if (depth > 14 || seen(e.id)) return None
    val next = seen + e.id
    def go(v: Expression) = pagePassword(v, next, depth + 1)
    e match {
      case b: Block => last(b).flatMap(go)
      case i: Identifier => latest(i).flatMap(go)
      case c: Call if Set("encode", "decode").contains(c.name) => arg(c, 0).flatMap(go)
      case c: Call if c.name == "<operator>.logicalAnd" => arg(c, 2).flatMap(go)
      case c: Call if c.name == "group" =>
        val group = arg(c, 1).collect { case l: Literal => unquote(l.code) }
        val matcher = arg(c, 0).flatMap(origin(_, Set.empty, 0)).collect { case s: Call if s.name == "search" => s }
        for {
          g <- group
          m <- matcher
          regex <- arg(m, 0).flatMap(fieldOrigin(_, Set.empty, 0)).collect {
            case r: Call if importedMember(r, "re", "compile") => r
          }
          pattern <- arg(regex, 1).collect { case l: Literal => l.code }
          if pattern.contains("CryptoJS\\.AES\\.decrypt") && pattern.contains(s"(?P<$g>") && pattern.contains(".*?,")
          page <- arg(m, 1).flatMap(responsePage)
        } yield page
      case c: Call =>
        val defs = definitions(c)
        if (defs.size != 1 || escapes(defs.head)) None
        else {
          val returns = defs.head.ast.isReturn.astChildren.collect { case v: Expression => v }.toVector
          val values = returns.map(go)
          if (values.nonEmpty && values.forall(_.nonEmpty) && values.flatten.distinct.size == 1) values.head else None
        }
      case _ => None
    }
  }

  private def responsePage(e: Expression): Option[String] = e match {
    case c: Call if c.name == "<operator>.fieldAccess" && c.ast.isFieldIdentifier.canonicalName.l.contains("text") =>
      arg(c, 1).flatMap(origin(_, Set.empty, 0)).collect { case g: Call if g.name == "get" => g }
        .filter(g => arg(g, 0).flatMap(fieldName(_, Set.empty, 0)).exists(_.endsWith(".session.http")) &&
          definitions(g).isEmpty).flatMap(arg(_, 1)).map(_.code.trim)
    case _ => None
  }

  private def origin(e: Expression, seen: Set[Long], depth: Int): Option[Expression] = {
    if (depth > 14 || seen(e.id)) None
    else e match {
      case b: Block => last(b).flatMap(v => origin(v, seen + e.id, depth + 1))
      case i: Identifier => latest(i).flatMap(v => origin(v, seen + e.id, depth + 1))
      case c: Call if c.name == "<operator>.assignment" => arg(c, 2).flatMap(v => origin(v, seen + e.id, depth + 1))
      case _ => Some(e)
    }
  }
  private def fieldName(e: Expression, seen: Set[Long], depth: Int): Option[String] =
    if (depth > 10 || seen(e.id)) None
    else if (e.code.trim.startsWith("self.")) Some(e.code.trim)
    else latest(e).flatMap(v => fieldName(v, seen + e.id, depth + 1))
  private def fieldOrigin(e: Expression, seen: Set[Long], depth: Int): Option[Expression] = {
    if (depth > 14 || seen(e.id)) return None
    val local = origin(e, Set.empty, 0)
    local.flatMap { value =>
      if (!value.code.trim.startsWith("self.")) Some(value)
      else {
        val member = value.code.trim.stripPrefix("self.")
        val values = assignments.filter(a => file(a) == file(value) && owner(a.method) == owner(value.method) &&
          Set(member, "self." + member).contains(lhs(a))).flatMap(arg(_, 2))
        if (values.size != 1) None else fieldOrigin(values.head, seen + e.id, depth + 1)
      }
    }
  }
  private def depends(e: Expression, target: Long, seen: Set[Long], depth: Int): Boolean = {
    if (e.id == target) return true
    if (depth > 16 || seen(e.id)) return false
    val next = seen + e.id
    e match {
      case b: Block => last(b).exists(depends(_, target, next, depth + 1))
      case i: Identifier => latest(i).exists(depends(_, target, next, depth + 1))
      case c: Call if c.name.startsWith("<operator>.") || Set("encode", "decode").contains(c.name) =>
        c.argument.l.exists(depends(_, target, next, depth + 1))
      case _ => false
    }
  }

  private def definitions(c: Call): Vector[Method] = {
    val prefix = c.code.takeWhile(_ != '(').trim
    val parts = prefix.split("\\.").toVector
    val base = parts.headOption.getOrElse("")
    if (base != "self" && (c.method.parameter.nameExact(base).nonEmpty || assignments.exists(a =>
      file(a) == file(c) && (a.method.id == c.method.id || a.method.name == "<module>") && lhs(a) == base &&
        !arg(a, 2).exists(v => v.label == "METHOD_REF" || (v match { case i: Call => i.name == "import"; case _ => false }))))) return Vector.empty
    val local = byName.getOrElse(c.name, Vector.empty).filter(m => file(m) == file(c) &&
      (if (base == "self") owner(m) == owner(c.method) else if (parts.size == 1) owner(m) == "<module>" else owner(m) == base))
    if (local.nonEmpty) local
    else imports.getOrElse(file(c), Vector.empty).flatMap { i => imported(i).flatMap { case (module, member, alias) =>
      val qualified = module.isEmpty && prefix == s"$alias.${c.name}"
      if (prefix != alias && !qualified) Vector.empty
      else byName.getOrElse(if (qualified) c.name else member, Vector.empty).filter(m =>
        file(m).endsWith((if (qualified) member else module).replace('.', '/') + ".py") && owner(m) == "<module>")
    }}.distinctBy(_.id)
  }
  private def importedMember(c: Call, module: String, member: String): Boolean = {
    val prefix = c.code.takeWhile(_ != '(').trim
    val base = prefix.takeWhile(_ != '.')
    val matched = imports.getOrElse(file(c), Vector.empty).exists { i =>
      imported(i).exists { case (m, name, alias) =>
        (m == module && name == member && prefix == alias) || (m.isEmpty && name == module && prefix == s"$alias.$member")
      }
    }
    matched && c.method.parameter.nameExact(base).isEmpty && !assignments.exists(a => file(a) == file(c) &&
      (a.method.id == c.method.id || a.method.name == "<module>") && Set(base, prefix).contains(lhs(a)) &&
      !arg(a, 2).exists { case i: Call => i.name == "import"; case _ => false })
  }
  private def imported(c: Call): Vector[(String, String, String)] =
    "import\\(([^,)]*),\\s*([^,)]+)(?:,\\s*([^,)]+))?\\)".r.findFirstMatchIn(c.code)
      .map(m => (m.group(1).trim, m.group(2).trim, Option(m.group(3)).getOrElse(m.group(2)).trim)).toVector
  private val callerCache = collection.mutable.HashMap.empty[Long, Vector[Call]]
  private def callers(m: Method): Vector[Call] = callerCache.getOrElseUpdate(m.id, {
    val aliases = imports.valuesIterator.flatten.flatMap(imported).collect {
      case (module, member, alias) if member == m.name && file(m).endsWith(module.replace('.', '/') + ".py") => alias
    }.toSet
    calls.filter(c => (c.name == m.name || aliases(c.code.takeWhile(_ != '(').trim)) &&
      c.method.id != m.id && definitions(c).exists(_.id == m.id))
  })
  private def escapes(m: Method): Boolean = assignments.exists { a =>
    val aliases = imports.getOrElse(file(a), Vector.empty).flatMap(imported).collect {
      case (module, member, alias) if member == m.name && file(m).endsWith(module.replace('.', '/') + ".py") => alias
      case (module, member, alias) if module.isEmpty && file(m).endsWith(member.replace('.', '/') + ".py") => s"$alias.${m.name}"
    }.toSet ++ Option.when(file(a) == file(m))(m.name)
    lhs(a) != m.name && arg(a, 2).exists(v =>
      v.ast.isMethodRef.methodFullName.l.contains(m.fullName) || aliases(v.code.trim))
  }
  private def latest(e: Expression): Option[Expression] = writes.getOrElse(e.method.id -> e.code.trim, Vector.empty)
    .filter(a => a.lineNumber.getOrElse(Int.MaxValue) <= e.lineNumber.getOrElse(Int.MaxValue) && !a.ast.id(e.id).nonEmpty)
    .sortBy(a => (a.lineNumber.getOrElse(0), a.columnNumber.getOrElse(0))).lastOption
    .filter(a => a.inAst.isControlStructure.id.l.forall(e.inAst.isControlStructure.id.l.toSet.contains)).flatMap(arg(_, 2))
  private def last(b: Block): Option[Expression] = b.astChildren.collect { case e: Expression => e }.toVector.lastOption
  private def owner(m: Method): String = m.fullName.stripSuffix("." + m.name).split("\\.").last.split(":").last
  private def unquote(s: String): String = s.stripPrefix("r").stripPrefix("b").drop(1).dropRight(1)
  private def arg(c: Call, n: Int): Option[Expression] = c.argument.l.find(v => v.argumentIndex == n && v.argumentName.isEmpty)
  private def lhs(c: Call): String = arg(c, 1).map(_.code.trim).getOrElse("")
  private def file(n: AstNode): String = n.file.name.headOption.getOrElse("")
}
