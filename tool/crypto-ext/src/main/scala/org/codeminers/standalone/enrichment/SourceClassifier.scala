package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.nodes.{Call, Identifier, Literal, MethodParameterIn, StoredNode}
import io.shiftleft.codepropertygraph.generated.language.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.ontology.CryptoSourceType

object SourceClassifier {
  /** Lookup selectors and file paths identify a secret; they are not its bytes. */
  def isJavaSecretLookup(call: Call): Boolean =
    (Set("getKey", "getEntry")(call.name) && call.methodFullName.startsWith("java.security.KeyStore.")) ||
      (call.name == "getSecretKey" && call.methodFullName.startsWith("java.security.KeyStore$SecretKeyEntry."))

  def isJavaFileRead(call: Call): Boolean =
    (Set("read", "readAllBytes", "newInputStream")(call.name) &&
      (call.methodFullName.startsWith("java.nio.file.Files.") ||
        call.methodFullName.startsWith("org.springframework.core.io.buffer.DataBufferUtils."))) ||
      (call.name == "readAllBytes" && call.methodFullName.startsWith("java.io.InputStream."))
  private val CsprngMarkers = Set(
    "SECURERANDOM",
    "GETINSTANCESTRONG",
    "NEXTBYTES",
    "GENERATESEED",
    "KEYGENERATOR",
    "KEYPAIRGENERATOR",
    "GENERATEKEY",
    "GENERATEKEYPAIR",
    "SECRETS.",
    "OS.URANDOM",
    "TOKEN_BYTES",
    "TOKEN_HEX",
    "TOKEN_URLSAFE",
    "GET_RANDOM_BYTES"
  )
  private val WeakRandomMarkers = Set(
    "JAVA.UTIL.RANDOM",
    "JAVA.UTIL.CONCURRENT.THREADLOCALRANDOM",
    "MATH.RANDOM",
    "RANDOM.RANDOM",
    "RANDOM.RANDINT",
    "RANDOM.RANDRANGE",
    "RANDOM.CHOICE",
    "RANDOM.SAMPLE",
    "RANDOM.GETRANDBITS",
    "LIBC-RAND"
  )
  private val KdfMarkers = Set("PBKDF2", "SCRYPT", "ARGON2", "BCRYPT", "SECRETKEYFACTORY", "HKDF")

  def classify(node: StoredNode): CryptoSourceType = node match {
    case literal: Literal if isConcreteLiteral(literal) => CryptoSourceType.Hardcoded
    case _: Literal           => CryptoSourceType.Unknown
    case call: Call           => classifyCall(call)
    case _: MethodParameterIn => CryptoSourceType.ExternalInput
    case _: Identifier        => CryptoSourceType.ExternalInput
    case _                    => CryptoSourceType.Unknown
  }

  def combine(nodes: Iterable[StoredNode]): CryptoSourceType = {
    val classifications = nodes.iterator.map(classify).toSet
    val concrete = classifications - CryptoSourceType.Unknown
    if (concrete.isEmpty) CryptoSourceType.Unknown
    else if (concrete == Set(CryptoSourceType.Hardcoded)) CryptoSourceType.Hardcoded
    else if (concrete.contains(CryptoSourceType.Kdf)) CryptoSourceType.Kdf
    else if (concrete.contains(CryptoSourceType.WeakRandom)) CryptoSourceType.WeakRandom
    else if (concrete.contains(CryptoSourceType.Csprng)) CryptoSourceType.Csprng
    else if (concrete.contains(CryptoSourceType.Predictable)) CryptoSourceType.Predictable
    else if (concrete.contains(CryptoSourceType.ExternalInput)) CryptoSourceType.ExternalInput
    else CryptoSourceType.Unknown
  }

  // Zero allocation is only accepted by the exact resolver after checking for intervening array mutations.
  // It must not enter generic flow-source searches, where a later nextBytes/fill could have replaced its content.
  def isKnownProducer(call: Call): Boolean = !isImplicitZeroArray(call) && classifyCall(call) != CryptoSourceType.Unknown

  def isImplicitZeroArrayCode(code: String): Boolean =
    code.trim.matches("new\\s+byte\\s*\\[\\s*[1-9][0-9]*\\s*\\]")

  def isImplicitZeroArray(call: Call): Boolean =
    call.name.startsWith("<operator>.") && isImplicitZeroArrayCode(call.code)

  /** These stable process properties are guessable passwords, not secret configuration values. */
  def isPredictableSystemProperty(call: Call): Boolean = {
    val systemCall = call.name == "getProperty" &&
      (call.methodFullName.startsWith("java.lang.System.getProperty") ||
        call.code.matches("(?s)\\s*(?:java\\.lang\\.)?System\\.getProperty\\s*\\(.*"))
    val property = call.argument.l.find(_.argumentIndex == 1).collect {
      case literal: Literal => literal.code.trim.stripPrefix("\"").stripSuffix("\"")
    }
    systemCall && property.exists(Set("user.home", "user.name", "user.dir", "java.io.tmpdir"))
  }

  /** A local host identity is public/stable metadata, not per-operation random salt. */
  def isPredictableHostIdentity(call: Call): Boolean =
    call.name == "getLocalHost" && call.methodFullName.startsWith("java.net.InetAddress.getLocalHost:")

  /** Python import lowering creates pseudo-literals such as `AES` and
    * `Crypto.Cipher`. They are symbols, not hardcoded cryptographic material. */
  def isConcreteLiteral(literal: Literal): Boolean = {
    val code = literal.code.trim
    code.matches("(?s)(?i:[rubf]*)(?:\".*\"|'.*')") ||
    code.matches("[-+]?(?:[0-9]+(?:_[0-9]+)*)(?:\\.[0-9]+)?[fFdDlL]?") ||
    // Hex, binary, and octal literals are just as hardcoded as decimal ones; a byte array written as
    // `{(byte) 0xa2}` is a constant salt, not an unknown value.
    code.matches("(?i)[-+]?0x[0-9a-f_]+[lL]?") ||
    code.matches("(?i)[-+]?0b[01_]+[lL]?") ||
    code.matches("[-+]?0[0-7_]+[lL]?") ||
    Set("true", "false", "null", "none").contains(code.toLowerCase)
  }

  /** A file/resource name selects bytes; it is not those bytes. Resolve only typed JDK reads or
    * an exact source wrapper that obtains a classpath stream and returns its bytes. */
  def isJavaResourceRead(call: Call): Boolean = call.typeFullName == "byte[]" &&
    ((call.name == "readAllBytes" && (call.methodFullName.startsWith("java.nio.file.Files.") ||
      call.methodFullName.startsWith("java.io.InputStream."))) || NoResolve.getCalledMethods(call).exists { method =>
      val calls = method.ast.isCall.l
      calls.exists(c => c.name == "getResourceAsStream" && c.methodFullName.startsWith("java.lang.Class.")) &&
        method.ast.isReturn.astChildren.collect { case c: Call => c }.exists(c =>
          c.name == "readAllBytes" && c.methodFullName.startsWith("java.io.InputStream."))
    })

  /** MT19937-64 output: require a resolved implementation and its characteristic tempering masks.
    * The SecureRandom seed does not turn this generator's output into a CSPRNG. */
  def isJavaMersenneTwisterOutput(call: Call): Boolean = call.name == "nextLong" &&
    call.methodFullName.takeWhile(_ != ':').endsWith(".MersenneTwister64.nextLong") &&
    NoResolve.getCalledMethods(call).exists { method =>
      val constants = method.ast.isLiteral.code.l.map(_.toUpperCase.replace("_", "")).toSet
      Set("0X71D67FFFEDA60000L", "0XFFF7EEE000000000L", "0X5555555555555555L").subsetOf(constants) &&
        method.ast.isCall.nameExact("<operator>.assignmentXor", "<operators>.assignmentXor").nonEmpty
    }

  private def classifyCall(call: Call): CryptoSourceType = {
    val text = s"${call.methodFullName} ${call.code}".toUpperCase
    if (isJavaSecretLookup(call) || isJavaFileRead(call) || isJavaResourceRead(call)) CryptoSourceType.ExternalInput
    else if (isJavaMersenneTwisterOutput(call)) CryptoSourceType.WeakRandom
    else if (isPredictableSystemProperty(call) || isPredictableHostIdentity(call)) CryptoSourceType.Predictable
    else if (isImplicitZeroArray(call)) CryptoSourceType.Hardcoded
    else if (call.methodFullName.startsWith("java.util.UUID.randomUUID:")) CryptoSourceType.Csprng
    else if (CsprngMarkers.exists(marker => text.contains(marker))) CryptoSourceType.Csprng
    else if (WeakRandomMarkers.exists(marker => text.contains(marker))) CryptoSourceType.WeakRandom
    else if (KdfMarkers.exists(marker => text.contains(marker)) ||
      (Set("hash_secret", "hash_secret_raw").contains(call.name) &&
        call.file.method.ast.isCall.nameExact("import").code.l.exists(_.contains("argon2.low_level")) &&
        !call.file.method.nameExact(call.name).exists(m => !m.isExternal))) CryptoSourceType.Kdf
    else CryptoSourceType.Unknown
  }
}
