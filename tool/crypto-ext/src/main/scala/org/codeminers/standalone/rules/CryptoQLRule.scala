package org.codeminers.standalone.rules

final case class CryptoQLRule(
  id: String,
  title: String,
  category: String,
  cwe: Option[Int],
  severity: FindingSeverity,
  message: String,
  remediation: String,
  predicate: Option[(CryptoQLContext, CryptoOperationFact) => Boolean],
  evidenceKinds: Set[String] = Set.empty
)

final case class CryptoOperationFact(
  operationId: Long,
  opType: String,
  operation: String,
  algorithm: String,
  mode: String,
  padding: String,
  keySize: Int,
  iterations: Int,
  saltUsed: Option[Boolean],
  usedForCrypto: Option[Boolean],
  source: String,
  isDeprecated: Boolean,
  providesIntegrity: Boolean,
  keySourceType: String,
  ivSourceType: String,
  saltSourceType: String,
  nonceSourceType: String,
  resolution: String,
  srcFile: String,
  lineNo: Int,
  scopes: Set[String],
  passwordSource: Boolean,
  evidence: Vector[FindingEvidence],
  /** Method-local names and calls used only for structural protocol recognition.  They are deliberately kept
    * separate from finding evidence so reports remain compact and source provenance is not widened. */
  semanticContext: Set[String] = Set.empty,
  /** Actual key has a proven hash transformation; this does not certify KDF strength. */
  keyTransformed: Boolean = false,
  protocolPrimitive: Boolean = false,
  ciphertextAuthentication: Option[Boolean] = None,
  hashInputTransformed: Boolean = false,
  publicQueryDecoding: Boolean = false,
  /** A default key-slice IV has a proven inherited encryption consumer, even if the broader lifecycle is partial. */
  defaultIvEncryptionProven: Boolean = false,
  /** A bounded Java consumer proof identifies caching, protocol formatting or a test substitute. */
  nonSecurityUse: Boolean = false,
  /** Java C4 has a separate consumer proof; init-only facts cannot establish message encryption. */
  encryptionConsumer: Option[Boolean] = None,
  semanticKind: String = "",
  verificationKind: String = "",
  verificationEnabled: Option[Boolean] = None,
  transportScheme: String = "",
  protocolVersion: String = "",
  tagLength: Int = -1,
  semanticDetail: String = ""
)

final class CryptoQLContext private[rules] (val operations: Vector[CryptoOperationFact]) {
  private val authenticatingScopes = operations.iterator
    .filter(operation =>
      operation.resolution == "COMPLETE" &&
        (operation.providesIntegrity || operation.opType == CryptoQLRules.MacSignature)
    )
    .flatMap(_.scopes)
    .toSet

  def hasAuthentication(operation: CryptoOperationFact): Boolean =
    operation.providesIntegrity || operation.ciphertextAuthentication.getOrElse(operation.scopes.exists(authenticatingScopes.contains))

  /** HKDF-Extract/Expand can be implemented directly as two adjacent HMACs:
    * the extract step uses an all-zero default salt and the expand step uses
    * the pseudorandom key produced by the first HMAC. Treat the zero salt as a
    * protocol parameter only when the complete two-HMAC composition is visible
    * in the same method scope.
    */
  def isHkdfExtractExpand(operation: CryptoOperationFact): Boolean = {
    val sameScopeMacs = operations.filter { candidate =>
      candidate.opType == CryptoQLRules.MacSignature && candidate.operation == "MAC" &&
      candidate.scopes.intersect(operation.scopes).nonEmpty
    }
    val hasZeroSalt = sameScopeMacs.exists(_.evidence.exists { evidence =>
      evidence.kind == "KEY" && evidence.nodeLabel == "LITERAL" &&
      evidence.code.toLowerCase.replaceAll("\\s+", "").matches("(?s).*(?:\\\\x00|0x00).*")
    })
    val callCode = sameScopeMacs.flatMap(_.evidence.filter(_.kind == "CALL").map(_.code.toLowerCase))
    // Some Python frontends do not materialize the digest constructor as the
    // operation's algorithm attribute; the exact realizing-call argument is an
    // equivalent proof.
    val sha256Composition = sameScopeMacs.count(_.algorithm == "SHA256") >= 2 ||
      callCode.count(_.contains("sha256")) >= 2
    val hasExpandCounter = callCode.exists(code =>
      code.replaceAll("\\s+", "").matches("(?s).*(?:\\\\x01|0x01|b['\"]\\x01['\"]).*")
    )
    val adjacentPair = sameScopeMacs.combinations(2).exists { pair =>
      math.abs(pair.head.lineNo - pair.last.lineNo) <= 2
    }
    sameScopeMacs.size >= 2 && hasZeroSalt && sha256Composition && hasExpandCounter && adjacentPair
  }
}
