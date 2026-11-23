package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, Expression, Method, StoredNode}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.ontology.CryptoPolicy

/** Ablation variant "Base" (`EvaluationMain --ablation=string-match`): string matching on the standard CPG, Java only.
  *
  * No enrichment pass runs, so there are no CryptoOperation nodes, no ontology properties, no lifecycle grouping and no
  * value tracking. A rule fires when the source text of one API call (its receiver, arguments and identifier names)
  * matches a pattern. The rule constants are the full tool's (algorithm lists, the 10,000-iteration and 2,048-bit
  * thresholds and the optional legacy HMAC-SHA1 setting), so the variants use the same policy. C4, C6 and C12 need operation
  * facts and have no string pattern here.
  */
private[standalone] object StringMatchBaseline {
  private case class BaselineMatch(rule: CryptoQLRule, node: StoredNode,
    operationType: String, operation: String, algorithm: String = "", mode: String = "", detail: String = "")

  private def rule(id: String): CryptoQLRule = CryptoQLRules.all.find(_.id == id).get

  private val Quoted            = "\"([^\"]*)\""
  private val WeakCipher        = "(?i)(?:^|[^a-z0-9])(DES|DESede|TripleDES|3DES|RC2|RC4|ARCFOUR|Blowfish|IDEA)(?:$|[^a-z0-9])".r
  private val WeakDigest        = "(?i)^(MD2|MD4|MD5|SHA|SHA-?1)$".r
  private val WeakMac           = "(?i)^Hmac(MD2|MD4|MD5)$".r
  private val WeakSignature     = "(?i)^(MD2|MD5|SHA1)with".r
  private val DeprecatedTls     = "(?i)^(SSL|SSLv2|SSLv3|TLSv1|TLSv1\\.1)$".r
  private val MinimumIterations = 10000
  private val MinimumKeySize    = 2048

  def run(cpg: Cpg, reportLegacyHmacSha1: Boolean = false): Vector[CryptoFinding] = {
    val calls = cpg.call.l.filterNot(_.name.startsWith("<operator>")).toVector
    val matches = calls.flatMap(call => callMatches(call, reportLegacyHmacSha1)) ++ trustCallbacks(cpg) ++ httpLiterals(cpg)
    matches.map(toFinding).distinctBy(f => (f.ruleId, f.srcFile, f.lineNo))
      .sortBy(f => (f.srcFile, f.lineNo, f.ruleId))
  }

  private def callMatches(call: Call, reportLegacyHmacSha1: Boolean): Vector[BaselineMatch] = {
    def hit(id: String, detail: String = call.code) = BaselineMatch(rule(id), call, "STRING_MATCH", call.name, detail = detail)
    val text      = call.code
    val receiver  = text.takeWhile(_ != '(')
    val literals  = Quoted.r.findAllMatchIn(text).map(_.group(1)).toVector
    val arguments = call.argument.l.filter(_.argumentIndex > 0).sortBy(_.argumentIndex).toVector
    def argument(index: Int): Option[Expression] = arguments.find(_.argumentIndex == index)
    def written(index: Int): Boolean = argument(index).exists(a => a.code.contains("\"") || a.code.contains("{"))
    def integer(index: Int): Option[Int] = argument(index).flatMap(_.code.trim.toIntOption)
    val constructed = if (call.name == "<init>") call.methodFullName.takeWhile(_ != ':').stripSuffix(".<init>")
      .split('.').last else ""

    call.name match {
      case "getInstance" =>
        val owner = receiver.split('.').dropRight(1).lastOption.getOrElse("")
        Vector(
          Option.when(owner.endsWith("Cipher") && (text.toUpperCase.contains("ECB") || literals.contains("AES")))(hit("C1")),
          Option.when(Set("Cipher", "KeyGenerator", "SecretKeyFactory").exists(owner.endsWith) &&
            literals.exists(l => WeakCipher.findFirstIn(l).nonEmpty))(hit("C14")),
          Option.when(owner.endsWith("Cipher") && literals.exists(l => l.equalsIgnoreCase("RSA") ||
            l.toUpperCase.matches("RSA/.*/(NOPADDING|PKCS1PADDING)")))(hit("C11")),
          Option.when(owner.endsWith("MessageDigest") && literals.exists(l => WeakDigest.matches(l)))(hit("C5")),
          Option.when(owner.endsWith("Mac") && literals.exists(l => WeakMac.matches(l)))(hit("C15")),
          Option.when(owner.endsWith("Mac") && reportLegacyHmacSha1 && literals.exists(CryptoPolicy.isHmacSha1))(
            hit("C15").copy(rule = CryptoQLRules.legacyHmacSha1Rule)),
          Option.when(owner.endsWith("Signature") && literals.exists(l => WeakSignature.findFirstIn(l).nonEmpty))(hit("C16")),
          Option.when(owner.endsWith("SSLContext") && literals.exists(l => DeprecatedTls.matches(l)))(hit("C18"))
        ).flatten
      case "<init>" => constructed match {
        case "SecretKeySpec" if written(1) =>
          Vector(hit(if (literals.exists(_.toLowerCase.startsWith("hmac"))) "C10" else "C2"))
        case "IvParameterSpec" | "GCMParameterSpec" if written(if (constructed == "IvParameterSpec") 1 else 2) =>
          Vector(hit("C3"))
        case "PBEParameterSpec" =>
          Vector(Option.when(written(1))(hit("C8")),
            Option.when(integer(2).exists(_ < MinimumIterations))(hit("C7"))).flatten
        case "PBEKeySpec" =>
          Vector(Option.when(written(1))(hit("C2")), Option.when(written(2))(hit("C8")),
            Option.when(integer(3).exists(_ < MinimumIterations))(hit("C7"))).flatten
        case "Random" => Vector(hit("C9"))
        case "SecureRandom" if written(1) => Vector(hit("C9"))
        case _ => Vector.empty
      }
      case "setSeed" if argument(1).exists(a => a.code.trim.matches("-?\\d+L?") || a.code.contains("\"") ||
        a.code.contains("{")) => Vector(hit("C9"))
      case "initialize" if integer(1).exists(_ < MinimumKeySize) => Vector(hit("C13"))
      case "createSocket" if arguments.exists(_.code.contains("\"")) => Vector(hit("C17"))
      case _ => Vector.empty
    }
  }

  /** Trust callbacks that never throw and hostname verifiers that `return true`, recognised by name and body text. */
  private def trustCallbacks(cpg: Cpg): Vector[BaselineMatch] = cpg.method.l.filterNot(_.isExternal).toVector.flatMap { m =>
    val body = m.ast.isCall.name.l
    val trust = Set("checkServerTrusted", "checkClientTrusted").contains(m.name) && !body.contains("<operator>.throw")
    val verifier = m.name == "verify" && m.ast.isReturn.code.l.nonEmpty && m.ast.isReturn.code.l.forall(_.contains("true"))
    Option.when(trust || verifier)(BaselineMatch(rule("C17"), m, "STRING_MATCH", m.name, detail = m.fullName))
  }

  /** Every `http://` string literal, wherever it occurs. */
  private def httpLiterals(cpg: Cpg): Vector[BaselineMatch] =
    cpg.literal.l.toVector.filter(_.code.matches("(?s)\"http://.*")).map(literal =>
      BaselineMatch(rule("C18"), literal, "STRING_MATCH", "LITERAL", detail = literal.code))
  private def toFinding(result: BaselineMatch): CryptoFinding = {
    val file         = sourceFile(result.node)
    val line         = sourceLine(result.node)
    val evidenceCode = if (result.detail.nonEmpty) result.detail else nodeCode(result.node)
    val fingerprint = Vector(
      result.rule.id,
      file,
      line.toString,
      result.operationType,
      result.operation,
      result.algorithm,
      result.mode,
      evidenceCode
    ).mkString("|").hashCode.toHexString
    CryptoFinding(
      fingerprint,
      result.rule.id,
      result.rule.title,
      result.rule.category,
      result.rule.cwe,
      result.rule.severity,
      result.rule.message,
      result.rule.remediation,
      file,
      line,
      result.operationType,
      result.operation,
      result.algorithm,
      result.mode,
      Vector(FindingEvidence("API", result.node.label, evidenceCode))
    )
  }

  private def sourceFile(node: StoredNode): String = node match {
    case operation: io.shiftleft.codepropertygraph.generated.nodes.CryptoOperation => operation.srcFile
    case method: Method => method.file.name.headOption.getOrElse("UNKNOWN")
    case ast: AstNode   => ast.file.name.headOption.getOrElse("UNKNOWN")
    case _              => "UNKNOWN"
  }

  private def sourceLine(node: StoredNode): Int = node match {
    case operation: io.shiftleft.codepropertygraph.generated.nodes.CryptoOperation => operation.lineNo
    case method: Method => method.lineNumber.map(_.toInt).getOrElse(-1)
    case ast: AstNode   => ast.lineNumber.map(_.toInt).getOrElse(-1)
    case _              => -1
  }

  private def nodeCode(node: StoredNode): String = node match {
    case method: Method => method.name
    case ast: AstNode   => ast.code
    case operation: io.shiftleft.codepropertygraph.generated.nodes.CryptoOperation =>
      s"${operation.operation}:${operation.algorithm}:${operation.mode}"
    case _ => node.label
  }

}
