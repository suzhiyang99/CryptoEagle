package org.codeminers.standalone.report

import org.codeminers.standalone.rules.{CryptoFinding, CryptoQLRules, FindingEvidence}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

enum FindingFormat {
  case Text, Json, Csv
}

object FindingFormat {
  def parse(value: String): Option[FindingFormat] = value.trim.toLowerCase match {
    case "text" => Some(FindingFormat.Text)
    case "json" => Some(FindingFormat.Json)
    case "csv"  => Some(FindingFormat.Csv)
    case _      => None
  }
}

object FindingReporter {
  def render(findings: Seq[CryptoFinding], format: FindingFormat): String = format match {
    case FindingFormat.Text => renderText(findings)
    case FindingFormat.Json => renderJson(findings)
    case FindingFormat.Csv  => renderCsv(findings)
  }

  def write(findings: Seq[CryptoFinding], format: FindingFormat, output: Path): Unit = {
    Option(output.getParent).foreach(Files.createDirectories(_))
    // Excel reads a CSV without a byte-order mark in the local code page, which garbles non-ASCII text.
    val bom = if (format == FindingFormat.Csv) "\uFEFF" else ""
    Files.writeString(output, bom + render(findings, format) + System.lineSeparator(), StandardCharsets.UTF_8)
  }

  private def renderText(findings: Seq[CryptoFinding]): String = findings
    .map { finding =>
      val cwe = finding.cwe.fold("CWE-N/A")(value => s"CWE-$value")
      s"${finding.ruleId} ${finding.severity.storedValue} $cwe ${finding.srcFile}:${finding.lineNo} " +
        s"${finding.title} [${finding.algorithm}/${finding.mode}] - ${finding.message}" +
        relatedLocations(finding).map(location => "\n  " + location).mkString
    }
    .mkString("\n")

  private def renderJson(findings: Seq[CryptoFinding]): String =
    findings.map(jsonFinding).mkString("[\n  ", ",\n  ", "\n]")

  private def jsonFinding(finding: CryptoFinding): String = {
    val evidence = finding.evidence
      .map { item =>
        val location = item.location.fold("")(site => s",\"srcFile\":${quote(site.srcFile)},\"lineNo\":${site.lineNo}")
        s"{\"kind\":${quote(item.kind)},\"nodeLabel\":${quote(item.nodeLabel)},\"code\":${quote(item.code)}$location}"
      }
      .mkString("[", ",", "]")
    s"{" +
      s"\"fingerprint\":${quote(finding.fingerprint)}," +
      s"\"ruleId\":${quote(finding.ruleId)}," +
      s"\"ruleSet\":\"C1-C18\"," +
      s"\"title\":${quote(finding.title)}," +
      s"\"category\":${quote(finding.category)}," +
      s"\"cwe\":${finding.cwe.fold("null")(_.toString)}," +
      s"\"severity\":${quote(finding.severity.storedValue)}," +
      s"\"message\":${quote(finding.message)}," +
      s"\"remediation\":${quote(finding.remediation)}," +
      s"\"srcFile\":${quote(finding.srcFile)}," +
      s"\"lineNo\":${finding.lineNo}," +
      s"\"operationType\":${quote(finding.operationType)}," +
      s"\"operation\":${quote(finding.operation)}," +
      s"\"algorithm\":${quote(finding.algorithm)}," +
      s"\"mode\":${quote(finding.mode)}," +
      s"\"evidence\":$evidence}"
  }

  /** A table for people to read; JSON is the complete record.  One row per finding, ordered by file, line and rule so
    * that the findings on one statement sit together.  Rule metadata that repeats on every row (category, CWE, severity,
    * message) and machine identifiers are left to JSON, and the evidence is cut down to what the rule looked at.
    */
  private def renderCsv(findings: Seq[CryptoFinding]): String = {
    val header = "file,line,rule,issue,crypto,evidence,fix,path"
    val rows = findings.sortBy(finding => (finding.srcFile, finding.lineNo, ruleOrder(finding.ruleId))).map { finding =>
      val separator = finding.srcFile.lastIndexWhere(character => character == '/' || character == '\\')
      Seq(
        finding.srcFile.substring(separator + 1),
        finding.lineNo.toString,
        finding.ruleId,
        finding.title,
        cryptoSummary(finding),
        (evidenceSummary(finding) +: relatedLocations(finding)).filter(_.nonEmpty).mkString(" | "),
        finding.remediation,
        if (separator < 0) "" else finding.srcFile.substring(0, separator)
      ).map(csv).mkString(",")
    }
    (header +: rows).mkString("\n")
  }

  /** C2 < C10 < X1: the letter, then the number. */
  private def ruleOrder(ruleId: String): (String, Int, String) =
    (ruleId.takeWhile(_.isLetter), ruleId.dropWhile(_.isLetter).toIntOption.getOrElse(0), ruleId)

  /** `DES/CBC (configure)`; an unknown mode is left out, and an operation without an algorithm stands alone. */
  private def cryptoSummary(finding: CryptoFinding): String = {
    val operation = finding.operation.toLowerCase.replace('_', ' ')
    val mode      = Option(finding.mode).filter(value => value.nonEmpty && value != "UNKNOWN")
    val primitive = Option(finding.algorithm).filter(_.nonEmpty).map(algorithm => (algorithm +: mode.toSeq).mkString("/"))
    primitive.fold(operation)(value => if (operation.isEmpty) value else s"$value ($operation)")
  }

  private val declaredEvidenceKinds: Map[String, Set[String]] =
    CryptoQLRules.all.map(rule => rule.id -> rule.evidenceKinds).toMap

  private def relatedLocations(finding: CryptoFinding): Vector[String] =
    finding.evidence.flatMap(item => item.location.map(site => s"${item.kind} ${site.srcFile}:${site.lineNo}")).distinct

  /** A rule that names its evidence kinds (C2 the key, C3 the IV, C8 the salt, ...) is shown those sources, as
    * `IV = 0 ×8`.  A rule that names none carries every source of the operation, which says nothing about why it
    * fired; there the call that realizes the operation is shown.  An extended rule's evidence is its API node, shown
    * only when it is code rather than the operation's own label.
    */
  private def evidenceSummary(finding: CryptoFinding): String = {
    val declared = declaredEvidenceKinds.getOrElse(finding.ruleId, Set.empty)
    if (declared.nonEmpty) {
      finding.evidence
        .filter(item => declared.contains(item.kind))
        .groupBy(_.kind)
        .toVector
        .sortBy((kind, _) => finding.evidence.indexWhere(_.kind == kind))
        .map { (kind, items) =>
          val counts = items.map(evidenceValue).foldLeft(Vector.empty[(String, Int)]) { (seen, value) =>
            seen.indexWhere(_._1 == value) match {
              case -1    => seen :+ (value -> 1)
              case index => seen.updated(index, value -> (seen(index)._2 + 1))
            }
          }
          val shown = counts.take(3).map((value, count) => if (count > 1) s"$value ×$count" else value)
          val more  = if (counts.size > 3) s" … (+${counts.size - 3})" else ""
          s"$kind = ${shown.mkString(", ")}$more"
        }
        .mkString("; ")
    } else {
      val calls = finding.evidence.filter(_.kind == "CALL")
      calls.headOption match {
        case Some(first) => shorten(first.code) + (if (calls.size > 1) s" (+${calls.size - 1} calls)" else "")
        case None =>
          finding.evidence
            .find(item => item.kind == "API" && item.nodeLabel != "CRYPTO_OPERATION")
            .fold("")(item => shorten(item.code))
      }
    }
  }

  private def evidenceValue(item: FindingEvidence): String =
    if (item.nodeLabel == "METHOD_PARAMETER_IN") s"parameter ${shorten(item.code)}" else shorten(item.code)

  private def shorten(code: String): String = {
    val flat = code.replaceAll("\\s+", " ").trim
    if (flat.length <= 80) flat else flat.take(79) + "…"
  }

  private def quote(value: String): String = {
    val escaped = value.flatMap {
      case '"'                              => "\\\""
      case '\\'                             => "\\\\"
      case '\b'                             => "\\b"
      case '\f'                             => "\\f"
      case '\n'                             => "\\n"
      case '\r'                             => "\\r"
      case '\t'                             => "\\t"
      case character if character.isControl => f"\\u${character.toInt}%04x"
      case character                        => character.toString
    }
    s"\"$escaped\""
  }

  private def csv(value: String): String = s"\"${value.replace("\"", "\"\"")}\""
}
