package org.codeminers.standalone.rules

enum FindingSeverity(val storedValue: String) {
  case Low      extends FindingSeverity("LOW")
  case Medium   extends FindingSeverity("MEDIUM")
  case High     extends FindingSeverity("HIGH")
  case Critical extends FindingSeverity("CRITICAL")
}

final case class FindingLocation(srcFile: String, lineNo: Int)

final case class FindingEvidence(kind: String, nodeLabel: String, code: String, location: Option[FindingLocation] = None) {
  private[rules] def identityCode: String = location.fold(code)(site => s"${site.srcFile}:${site.lineNo} ${code}")
}

/** Language-independent result emitted by a CryptoQL rule. */
final case class CryptoFinding(
  fingerprint: String,
  ruleId: String,
  title: String,
  category: String,
  cwe: Option[Int],
  severity: FindingSeverity,
  message: String,
  remediation: String,
  srcFile: String,
  lineNo: Int,
  operationType: String,
  operation: String,
  algorithm: String,
  mode: String,
  evidence: Vector[FindingEvidence]
)
