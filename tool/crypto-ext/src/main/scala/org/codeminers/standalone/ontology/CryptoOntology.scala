package org.codeminers.standalone.ontology

/** Language-independent operation categories defined by the CryptoEagle paper. */
enum CryptoOperationType(val storedValue: String) {
  case SymmetricEncryption  extends CryptoOperationType("SYMMETRIC_ENCRYPTION")
  case AsymmetricEncryption extends CryptoOperationType("ASYMMETRIC_ENCRYPTION")
  case HashFunction         extends CryptoOperationType("HASH_FUNCTION")
  case KeyDerivation        extends CryptoOperationType("KEY_DERIVATION")
  case MacSignature         extends CryptoOperationType("MAC_SIGNATURE")
  case Randomness           extends CryptoOperationType("RANDOMNESS")
  case Verification         extends CryptoOperationType("VERIFICATION")
  case Transport            extends CryptoOperationType("TRANSPORT")
  case KeyStorage           extends CryptoOperationType("KEY_STORAGE")
  case Authentication       extends CryptoOperationType("AUTHENTICATION")
}

object CryptoOperationType {
  def fromStoredValue(value: String): Option[CryptoOperationType] =
    CryptoOperationType.values.find(_.storedValue == value.trim.toUpperCase)
}

/** Classification of the terminal source reached for a sensitive parameter. */
enum CryptoSourceType(val storedValue: String) {
  case Hardcoded     extends CryptoSourceType("HARDCODED")
  case Predictable   extends CryptoSourceType("PREDICTABLE")
  case Csprng        extends CryptoSourceType("CSPRNG")
  case WeakRandom    extends CryptoSourceType("WEAK_RANDOM")
  case Kdf           extends CryptoSourceType("KDF")
  case ExternalInput extends CryptoSourceType("EXTERNAL_INPUT")
  case Unknown       extends CryptoSourceType("UNKNOWN")
}

object CryptoSourceType {
  def fromStoredValue(value: String): CryptoSourceType =
    CryptoSourceType.values
      .find(_.storedValue == value.trim.toUpperCase)
      .getOrElse(CryptoSourceType.Unknown)
}

/** Confidence of call-to-operation lifecycle resolution. */
enum CryptoResolution(val storedValue: String) {
  case Complete extends CryptoResolution("COMPLETE")
  case Partial  extends CryptoResolution("PARTIAL")
  case Unknown  extends CryptoResolution("UNKNOWN")
}

object CryptoPolicy {
  val MinimumPbkdf2Iterations: Int = 10000
  val MinimumAesKeyLength: Int     = 128
  val MinimumRsaKeySize: Int       = 2048
  val MinimumDsaKeySize: Int       = 2048
  val LegacyJavaHmacSha1Evidence   = "Java HMAC-SHA1 selected by the legacy-algorithm policy"

  def isHmacSha1(algorithm: String): Boolean = algorithm.replace("-", "").equalsIgnoreCase("HmacSHA1")

  val DeprecatedAlgorithms: Set[String] =
    Set(
      "DES",
      "3DES",
      "DESEDE",
      "BLOWFISH",
      "IDEA",
      "RC2",
      "RC4",
      "ARCFOUR",
      "MD2",
      "MD4",
      "MD5",
      "SHA1",
      "SHA-1"
    )

  val IntegrityProvidingModes: Set[String] = Set("GCM", "CCM", "EAX", "OCB")
  val UniqueNonceModes: Set[String]         = Set("GCM", "CCM", "CTR", "EAX", "OCB", "CHACHA20")
}
