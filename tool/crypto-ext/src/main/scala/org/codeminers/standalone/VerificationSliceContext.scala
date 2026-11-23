package org.codeminers.standalone

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*

/** Source-only verification context for model input. No detector verdict, score or label is exported.
  * Complete enclosing methods retain guards, failure exits, checked values and later uses together.
  */
private[standalone] final class VerificationSliceContext(cpg: Cpg) {
  case class Context(id: Long, file: String, line: Int, kind: String, methods: Vector[Method],
    calls: Vector[Call], lines: Vector[Int], apiTypes: Vector[String])

  private def file(n: AstNode): String = n.file.name.headOption.getOrElse("")
  private def methodLines(m: Method): Vector[Int] = {
    val positions = (Vector(m) ++ m.ast.l).flatMap(_.lineNumber).filter(_ > 0)
    if (positions.isEmpty) Vector.empty
    else (positions.min to math.max(positions.max, m.lineNumberEnd.getOrElse(positions.max))).toVector
  }
  private def context(id: Long, anchor: AstNode, kind: String, nodes: Vector[AstNode]): Option[Context] = {
    val ownFile = file(anchor)
    val methods = nodes.flatMap {
      case m: Method => Vector(m)
      case n: CfgNode => Vector(n.method)
      case _ => Vector.empty
    }.filter(m => !m.isExternal && file(m) == ownFile).distinctBy(_.id).sortBy(_.lineNumber.getOrElse(0))
    val lines = (nodes.filter(n => file(n) == ownFile).flatMap(_.lineNumber) ++ methods.flatMap(methodLines))
      .filter(_ > 0).distinct.sorted
    val calls = methods.flatMap(_.ast.isCall.l).distinctBy(_.id)
    val apiTypes = (calls.map(_.methodFullName.takeWhile(_ != ':').split('.').dropRight(1).mkString(".")) ++
      methods.flatMap(_.parameter.typeFullName.l) ++ methods.flatMap(_.typeDecl.inheritsFromTypeFullName.l))
      .filter(t => t.startsWith("java.security.") || t.startsWith("javax.crypto.") || t.startsWith("javax.net.ssl."))
      .map(_.stripSuffix("[]")).distinct.sorted
    Option.when(ownFile.nonEmpty && lines.nonEmpty)(Context(id, ownFile, anchor.lineNumber.getOrElse(lines.head),
      kind, methods, calls, lines, apiTypes))
  }

  lazy val contexts: Vector[Context] = {
    val semantic = cpg.cryptoOperation.filter(_.opType == "VERIFICATION").l.toVector.flatMap { op =>
      val evidence = op.securityEvidenceNode.l.collect { case n: AstNode => n }.toVector
      val anchor = op.realizingCall.headOption.orElse(evidence.headOption)
      anchor.flatMap(n => context(op.id, n, op.verificationKind, evidence :+ n))
    }
    // Recognize both accepting and rejecting callbacks. Selection does not depend on C17 firing.
    val callbacks = cpg.method.filterNot(_.isExternal).l.toVector.filter { m =>
      (m.name == "verify" && m.signature.contains("javax.net.ssl.SSLSession")) ||
        (Set("checkServerTrusted", "checkClientTrusted")(m.name) &&
          m.signature.contains("java.security.cert.X509Certificate"))
    }.flatMap { m =>
      context(m.id, m, if (m.name == "verify") "HOSTNAME" else "CERTIFICATE", Vector(m))
    }
    (semantic ++ callbacks).groupBy(c => (c.file, c.kind, c.methods.map(_.id))).values
      .map(_.minBy(_.id)).toVector.sortBy(c => (c.file, c.line, c.kind))
  }

  def forCalls(calls: Iterable[Call], file: String): Vector[Context] = {
    val methods = calls.iterator.map(_.method.id).toSet
    contexts.filter(c => c.file == file && c.methods.exists(m => methods(m.id)))
  }
}
