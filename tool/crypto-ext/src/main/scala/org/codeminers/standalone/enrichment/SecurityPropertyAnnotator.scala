package org.codeminers.standalone.enrichment

import org.codeminers.standalone.ontology.{CryptoOperationType, CryptoPolicy}

private[enrichment] final case class NormalizedCryptoProperties(
  algorithm: String,
  mode: String,
  padding: String,
  isDeprecated: Boolean,
  providesIntegrity: Boolean,
  requiresUniqueNonce: Boolean,
  requiresCsprngSource: Boolean
)

/** Central policy-derived annotation used by every language adapter. */
private[enrichment] object SecurityPropertyAnnotator {
  def evaluate(
    operationType: String,
    operation: String,
    algorithm: String,
    mode: String,
    padding: String
  ): NormalizedCryptoProperties = {
    val normalizedAlgorithm = normalizeAlgorithm(algorithm)
    val normalizedMode      = normalizeToken(mode)
    val normalizedPadding   = normalizePadding(padding)
    val operationKind       = CryptoOperationType.fromStoredValue(operationType)
    val deprecatedTokens = Set(normalizedAlgorithm, normalizeToken(algorithm))
      .flatMap(_.split("[/_-]").filter(_.nonEmpty)) + normalizedAlgorithm ++ pbeCipherTokens(normalizedAlgorithm)
    val integrity = operationKind.contains(CryptoOperationType.MacSignature) ||
      CryptoPolicy.IntegrityProvidingModes.contains(normalizedMode) ||
      normalizedAlgorithm == "FERNET" || Set("SIGN", "SIGNATURE", "VERIFY", "MAC").contains(operation)

    NormalizedCryptoProperties(
      algorithm = normalizedAlgorithm,
      mode = normalizedMode,
      padding = normalizedPadding,
      isDeprecated = deprecatedTokens.exists(CryptoPolicy.DeprecatedAlgorithms.contains) ||
        normalizedAlgorithm.startsWith("NONEWITH"),
      providesIntegrity = integrity,
      requiresUniqueNonce = CryptoPolicy.UniqueNonceModes.contains(normalizedMode),
      requiresCsprngSource = operationKind.exists {
        case CryptoOperationType.SymmetricEncryption | CryptoOperationType.AsymmetricEncryption |
             CryptoOperationType.KeyDerivation | CryptoOperationType.MacSignature => true
        case _ => false
      }
    )
  }

  private def normalizeAlgorithm(value: String): String = normalizeToken(value) match {
    case "TRIPLEDES" | "DESEDE" | "DES3" => "3DES"
    case "DES_EDE3"                      => "3DES"
    case "BF"                            => "BLOWFISH"
    case "ARCFOUR" | "ARC4"              => "RC4"
    case "SHA-1"                         => "SHA1"
    // PyCryptodome's legacy Crypto.Hash.SHA module implements SHA-1.
    case "SHA"                           => "SHA1"
    case other                           => other
  }

  /** PBE algorithm names embed the cipher after `AND`: `PBEWithMD5AndDES`, `PBEWithSHA1AndDESede`,
    * `PBEWithSHA1AndRC2_40`, `PBEWithHmacSHA256AndAES_256`, `PBEWithSHAAnd3-KeyTripleDES-CBC`.  Only that part decides
    * whether the symmetric cipher is deprecated; the digest before `AND` (MD5, SHA1) is judged by the hash rules.
    */
  private def pbeCipherTokens(normalizedAlgorithm: String): Set[String] =
    "^PBEWITH.+?AND(.+)$".r.findFirstMatchIn(normalizedAlgorithm).toSet.flatMap { matched =>
      matched.group(1).split("[/_-]").filter(_.nonEmpty).map(normalizeAlgorithm).flatMap { token =>
        // `3-KeyTripleDES` splits into `3` and `KEYTRIPLEDES`; any DES variant is the deprecated cipher.
        if (token.endsWith("DES")) Set(token, "DES") else Set(token)
      }
    }

  private def normalizePadding(value: String): String = normalizeToken(value) match {
    case "PKCS5PADDING" => "PKCS5"
    case "PKCS7PADDING" => "PKCS7"
    case "NOPADDING"    => "NONE"
    case other          => other
  }

  private def normalizeToken(value: String): String =
    Option(value).getOrElse("").trim.replace("\"", "").replace("'", "").toUpperCase
}
