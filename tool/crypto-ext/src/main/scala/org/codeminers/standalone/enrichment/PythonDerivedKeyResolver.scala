package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{Block, Call, Expression, Method, StoredNode}
import io.shiftleft.semanticcpg.language.*

/** Proves that material has already passed through a password KDF. Every possible
  * definition must preserve that state; a raw-password alternative cancels the proof.
  * Only the material argument of HMAC/hash and representation transforms is followed.
  */
private[enrichment] final class PythonDerivedKeyResolver(cpg: Cpg) {
  private lazy val assignments = cpg.call.nameExact("<operator>.assignment").l.toVector
  private lazy val writesBySymbol = assignments.groupBy(a => file(a) -> lhs(a))
  private lazy val methods = cpg.method.l.groupBy(_.name)
  private lazy val imports = cpg.call.nameExact("import").l.groupBy(file).view
    .mapValues(_.map(_.code).mkString("\n")).toMap
  private val cache = collection.mutable.HashMap.empty[Long, Vector[StoredNode]]

  def sources(expression: Expression): Vector[StoredNode] = cache.getOrElseUpdate(expression.id,
    follow(expression, expression.method, Map.empty, 0, Set.empty))

  private def all(paths: Iterable[Vector[StoredNode]]): Vector[StoredNode] = {
    val values = paths.toVector
    if (values.isEmpty || values.exists(_.isEmpty)) Vector.empty
    else values.flatten.distinctBy(_.id)
  }

  private def follow(e: Expression, context: Method, env: Map[String, Expression], depth: Int,
    seen: Set[(Long, String)]): Vector[StoredNode] = {
    val text = e.code.trim
    val marker = context.id -> s"${e.id}:$text"
    if (depth > 20 || seen(marker)) return Vector.empty
    val next = seen + marker
    def recurse(v: Expression): Vector[StoredNode] = follow(v, context, env, depth + 1, next)
    def arg(c: Call, index: Int, keyword: String = ""): Option[Expression] =
      c.argument.l.find(a => keyword.nonEmpty && a.argumentName.contains(keyword))
        .orElse(c.argument.l.find(a => a.argumentName.isEmpty && a.argumentIndex == index))
    def symbol(value: String): Vector[StoredNode] = {
      env.get(value) match {
        case Some(actual) => follow(actual, actual.method, Map.empty, depth + 1, next)
        case None =>
          val classOwner = context.fullName.stripSuffix(s".${context.name}")
          val ownWrites = e.inAst.isCall.nameExact("<operator>.assignment").l.filter(a => lhs(a) == value).map(_.id).toSet
          val writes = writesBySymbol.getOrElse(file(context) -> value, Vector.empty).filter(a => !ownWrites(a.id) &&
            (if (value.startsWith("self.")) a.method.fullName.stripSuffix(s".${a.method.name}") == classOwner
             else a.method.id == context.id && a.lineNumber.getOrElse(Int.MaxValue) <= e.lineNumber.getOrElse(Int.MaxValue)))
          all(writes.flatMap(rhs).map(v => follow(v, v.method, env, depth + 1, next)))
      }
    }
    e match {
      case block: Block => block.astChildren.collect { case v: Expression => v }.toVector.lastOption.toVector.flatMap(recurse)
      case c: Call =>
        val imported = imports.getOrElse(file(c), "")
        val genuineArgon = Set("hash_secret", "hash_secret_raw").contains(c.name) &&
          imported.contains("argon2.low_level") && !methods.getOrElse(c.name, Nil).exists(m => file(m) == file(c) && !m.isExternal)
        val genuinePbkdf = (c.code.startsWith("hashlib.pbkdf2_hmac(") && imported.contains("hashlib")) ||
          (c.name == "PBKDF2" && Set("Crypto.Protocol.KDF", "Cryptodome.Protocol.KDF").exists(imported.contains))
        if (genuineArgon || genuinePbkdf) Vector(c)
        else if (c.name == "derive" && c.code.contains(".derive(")) {
          val receiver = c.code.takeWhile(_ != '(').stripSuffix(".derive")
          val constructors = assignments.filter(a => a.method.id == context.id && lhs(a) == receiver).flatMap(rhs)
          if (constructors.nonEmpty && constructors.forall(v => v.code.startsWith("PBKDF2HMAC(") || v.code.startsWith("Scrypt(")) &&
            imported.contains("cryptography.hazmat.primitives.kdf")) constructors.map(v => v: StoredNode)
          else Vector.empty
        } else if (Set("new", "HMAC").contains(c.name) && c.code.matches("(?s)hmac\\.(?:new|HMAC)\\(.*") && imported.contains("hmac"))
          arg(c, 1, "key").toVector.flatMap(recurse)
        else if (c.name == "new" && c.code.startsWith("hashlib.new(")) arg(c, 2, "data").toVector.flatMap(recurse)
        else if (Set("sha256", "sha512", "sha1", "md5").contains(c.name) && c.code.startsWith("hashlib."))
          arg(c, 1, "data").toVector.flatMap(recurse)
        else if (Set("digest", "hexdigest", "encode", "decode", "split", "rsplit").contains(c.name)) {
          val receiver = c.code.takeWhile(_ != '(').stripSuffix(s".${c.name}")
          val direct = symbol(receiver)
          if (direct.nonEmpty) direct else c.argument.l.find(_.argumentIndex == 0).toVector.flatMap(recurse)
        } else if (Set("bytes", "bytearray", "b64decode", "b64encode").contains(c.name)) arg(c, 1).toVector.flatMap(recurse)
        else if (Set("<operator>.indexAccess", "<operator>.fieldAccess").contains(c.name)) {
          val direct = symbol(text.replaceAll("\\s+", ""))
          if (direct.nonEmpty) direct else arg(c, 1).toVector.flatMap(recurse)
        } else if (!c.name.startsWith("<operator>.")) {
          val local = methods.getOrElse(c.name, Nil).filter(m => file(m) == file(c) && !m.isExternal)
          val definitions = if (local.size == 1) local else methods.getOrElse(c.name, Nil)
            .filter(m => !m.isExternal && imported.contains(m.name))
          if (definitions.size != 1) Vector.empty
          else {
            val method = definitions.head
            val bindings = method.parameter.l.flatMap(p => arg(c, p.index, p.name).map(p.name -> _)).toMap
            val returned = method.ast.isReturn.l.flatMap(_.astChildren.collect { case v: Expression => v })
            all(returned.map(v => follow(v, method, bindings, depth + 1, next)))
          }
        } else Vector.empty
      case _ => symbol(text)
    }
  }

  private def lhs(c: Call): String = c.argument.l.find(_.argumentIndex == 1).map(_.code.replaceAll("\\s+", "")).getOrElse("")
  private def rhs(c: Call): Option[Expression] = c.argument.l.find(_.argumentIndex == 2)
  private def file(n: io.shiftleft.codepropertygraph.generated.nodes.AstNode): String = n.file.name.headOption.getOrElse("")
}
