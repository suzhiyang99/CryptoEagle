package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, CryptoOperation, Identifier, Literal, MethodParameterIn}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.JavaFrontend
import org.codeminers.standalone.ontology.{CryptoPolicy, CryptoResolution, CryptoSourceType}
import org.codeminers.standalone.enrichment.SourceClassifier

/** The unified C1-C18 registry. Predicates operate only on the CA-CPG ontology and semantic edges. */
object CryptoQLRules {
  val SymmetricEncryption  = "SYMMETRIC_ENCRYPTION"
  val AsymmetricEncryption = "ASYMMETRIC_ENCRYPTION"
  val HashFunction         = "HASH_FUNCTION"
  val KeyDerivation        = "KEY_DERIVATION"
  val MacSignature         = "MAC_SIGNATURE"
  val Randomness           = "RANDOMNESS"

  private val Hardcoded   = CryptoSourceType.Hardcoded.storedValue
  private val WeakRandom  = CryptoSourceType.WeakRandom.storedValue
  private val Kdf         = CryptoSourceType.Kdf.storedValue
  private val Predictable = CryptoSourceType.Predictable.storedValue
  private val Complete    = CryptoResolution.Complete.storedValue

  val all: Vector[CryptoQLRule] = Vector(
    rule(
      "C1",
      "ECB mode used",
      "SymEnc",
      327,
      FindingSeverity.High,
      "ECB mode leaks plaintext patterns.",
      "Use an authenticated mode such as GCM."
    ) { (_, operation) =>
      operation.opType == SymmetricEncryption && operation.algorithm != "RSA" && operation.mode == "ECB" &&
      !operation.protocolPrimitive && !ProtocolSemantics.suppressesPrimitiveMode(operation)
    },
    rule(
      "C2",
      "Hardcoded or predictable key",
      "SymEnc",
      798,
      FindingSeverity.High,
      "A key or key-derivation password originates from a source-code literal, bundled plaintext resource, or predictable system property.",
      "Load keys from a protected secret store or derive them from a strong secret with a KDF.",
      Set("KEY", "KEY_DEFAULT", "KEY_PATH", "KEY_RESOURCE", "KEY_RESOURCE_BRANCH")
    ) { (context, operation) =>
      ((Set(SymmetricEncryption, AsymmetricEncryption).contains(operation.opType) &&
        ((operation.keySourceType == Hardcoded && hasSecretLiteralProof(operation, Set("KEY"))) ||
          operation.evidence.exists(item => item.kind == "KEY_DEFAULT" && isSecretLiteral(item)) ||
          (operation.keySourceType == Predictable && hasPredictablePropertyProof(operation)) ||
          (operation.keySourceType == Kdf && derivedFromConstantPassword(context, operation)) ||
          (operation.opType == SymmetricEncryption && operation.evidence.exists(e =>
            e.kind == "KEY_RESOURCE" && e.nodeLabel == "VERIFIED_CLASSPATH_RESOURCE")))) ||
        (operation.opType == KeyDerivation && operation.operation == "PBKDF2" &&
          hasPredictablePropertyProof(operation) && context.operations.exists(candidate =>
            candidate.opType == SymmetricEncryption &&
            candidate.scopes.intersect(operation.scopes).nonEmpty &&
            Set(Kdf, Predictable).contains(candidate.keySourceType)
          )) ||
        // A password KDF whose every password source is a secret-looking literal derives a fixed key, whether or
        // not this program shows the derived key being used.
        (operation.opType == KeyDerivation && hasSecretLiteralProof(operation, Set("KEY")))) &&
      !ProtocolSemantics.suppressesHardcodedKeyFinding(operation)
    },
    rule(
      "C3",
      "Static or predictable IV/nonce",
      "SymEnc",
      329,
      FindingSeverity.High,
      "The IV or nonce is static or predictably generated.",
      "Generate a fresh IV or nonce with a CSPRNG for every encryption.",
      Set("IV", "NONCE", "IV_DEFAULT", "IV_CONSUMER")
    ) { (_, operation) =>
      (isCompleteEncryption(operation) || (operation.opType == SymmetricEncryption && operation.defaultIvEncryptionProven)) &&
      !ProtocolSemantics.suppressesIvFinding(operation) &&
      (hasConcreteSourceProof(operation, Set("IV"), operation.ivSourceType) ||
        hasConcreteSourceProof(operation, Set("IV"), Hardcoded) ||
        hasConcreteSourceProof(operation, Set("IV"), WeakRandom) ||
        hasConcreteSourceProof(operation, Set("NONCE"), operation.nonceSourceType) ||
        hasConcreteSourceProof(operation, Set("NONCE"), Hardcoded) ||
        hasConcreteSourceProof(operation, Set("NONCE"), WeakRandom) ||
        ProtocolSemantics.isDeterministicIvDerivation(operation) || operation.defaultIvEncryptionProven)
    },
    rule(
      "C4",
      "Encryption without authentication",
      "Protocol",
      353,
      FindingSeverity.High,
      "Encryption has no associated ciphertext authentication operation in its resolved scope.",
      "Use an AEAD mode or authenticate the ciphertext with a MAC."
    ) { (context, operation) =>
      (operation.encryptionConsumer match {
        case Some(proven) => proven && operation.opType == SymmetricEncryption && operation.operation == "ENCRYPT"
        case None => isCompleteEncryption(operation)
      }) && !operation.protocolPrimitive && !context.hasAuthentication(operation) &&
      ((operation.mode == "CBC" && !ProtocolSemantics.isAuthenticatedWrapper(operation)) ||
        !ProtocolSemantics.suppressesPrimitiveMode(operation))
    },
    rule(
      "C5",
      "Weak hash",
      "Hash",
      327,
      FindingSeverity.High,
      "A deprecated hash algorithm is used in a security-sensitive context.",
      "Use SHA-256 or a stronger approved hash for security-sensitive hashing."
    ) { (_, operation) =>
      operation.opType == HashFunction && operation.isDeprecated && !operation.nonSecurityUse && !operation.publicQueryDecoding && securityRelevantHash(operation)
    },
    rule(
      "C6",
      "Password hashed without KDF",
      "Hash",
      916,
      FindingSeverity.High,
      "Password-like input is passed directly to a hash operation.",
      "Use a password KDF such as PBKDF2, scrypt, Argon2, or bcrypt.",
      Set("KEY")
    ) { (_, operation) =>
      operation.opType == HashFunction && operation.passwordSource && operation.keySourceType != Kdf &&
      !operation.hashInputTransformed && !operation.publicQueryDecoding
    },
    rule(
      "C7",
      "Insufficient PBKDF2 iterations",
      "KDF",
      None,
      FindingSeverity.Medium,
      s"PBKDF2 uses fewer than ${CryptoPolicy.MinimumPbkdf2Iterations} iterations.",
      s"Use at least ${CryptoPolicy.MinimumPbkdf2Iterations} iterations and tune upward for the deployment environment.",
      Set.empty
    ) { (_, operation) =>
      operation.opType == KeyDerivation &&
      (Set(operation.algorithm, operation.operation).contains("PBKDF2") ||
        operation.operation == "KDF_PARAMETERS") &&
      operation.iterations >= 0 && operation.iterations < CryptoPolicy.MinimumPbkdf2Iterations
    },
    rule(
      "C8",
      "Missing or insecure salt",
      "KDF",
      759,
      FindingSeverity.High,
      "The KDF salt is absent, static, or predictably generated.",
      "Use a fresh CSPRNG-generated salt for every password.",
      Set("SALT")
    ) { (_, operation) =>
      operation.opType == KeyDerivation && operation.resolution == Complete &&
      // A Java SecretKeyFactory selects an implementation; it does not execute a KDF.
      // Salt belongs to PBEKeySpec/PBEParameterSpec or Cipher.init, not getInstance.
      !(operation.srcFile.endsWith(".java") && operation.operation == "CONFIGURE") &&
      (operation.saltUsed.contains(false) || operation.saltUsed.isEmpty ||
        Set(Hardcoded, WeakRandom, Predictable).contains(operation.saltSourceType) ||
        hasConcreteSourceProof(operation, Set("SALT"), Hardcoded) ||
        hasConcreteSourceProof(operation, Set("SALT"), WeakRandom))
    },
    rule(
      "C9",
      "Insecure randomness for crypto",
      "Random",
      330,
      FindingSeverity.High,
      "Cryptographic material originates from a non-cryptographic random source.",
      "Use the platform cryptographic random generator."
    ) { (_, operation) =>
      !operation.nonSecurityUse && ((operation.opType == Randomness && operation.usedForCrypto.contains(true) && operation.source == WeakRandom) ||
      (operation.opType == Randomness && operation.operation == "SEED" &&
        (Set(Hardcoded, WeakRandom).contains(operation.keySourceType) ||
          hasConcreteSourceProof(operation, Set("KEY"), Hardcoded) ||
          hasConcreteSourceProof(operation, Set("KEY"), WeakRandom))) ||
      (operation.opType != Randomness && operation.sourceTypes.contains(WeakRandom)))
    },
    rule(
      "C10",
      "Hardcoded HMAC key",
      "MAC",
      798,
      FindingSeverity.High,
      "A MAC key originates from a source-code literal.",
      "Load the MAC key from a protected secret store.",
      Set("KEY")
    ) { (context, operation) =>
      operation.opType == MacSignature && operation.operation == "MAC" &&
      operation.keySourceType == Hardcoded && hasSecretLiteralProof(operation, Set("KEY")) &&
      !ProtocolSemantics.isProtocolDerivedMacKey(operation) && !context.isHkdfExtractExpand(operation)
    },
    rule(
      "C11",
      "RSA without secure padding",
      "Signature",
      780,
      FindingSeverity.High,
      "An RSA operation uses no padding, or RSA encryption uses PKCS#1 v1.5 padding.",
      "Use OAEP for encryption or PSS for signatures."
    ) { (_, operation) =>
      (((operation.opType == AsymmetricEncryption && operation.algorithm == "RSA") ||
        (operation.opType == MacSignature && Set("SIGNATURE", "SIGN", "VERIFY").contains(operation.operation) &&
          operation.algorithm.contains("RSA"))) &&
      operation.operation != "KEY_GENERATION" && operation.resolution == Complete &&
      (operation.padding == "NONE" ||
        (Set("SIGNATURE", "SIGN", "VERIFY").contains(operation.operation) && operation.algorithm.startsWith("NONEWITHRSA")))) ||
      // RSA encryption with PKCS#1 v1.5 (Bleichenbacher padding-oracle class). Signatures with v1.5 are not affected, so
      // the cipher must be initialized for encryption/decryption, and private-key encryption / public-key decryption
      // (signature direction) is excluded. A bare JCA "RSA" transformation (PROVIDER_DEFAULT) selects PKCS1Padding.
      (operation.opType == AsymmetricEncryption && operation.algorithm == "RSA" &&
        ((operation.padding.startsWith("PKCS1") && !operation.padding.contains("OAEP")) ||
          (operation.srcFile.endsWith(".java") && operation.padding == "PROVIDER_DEFAULT")) &&
        ((operation.operation == "ENCRYPT" && !rsaKeyIs(operation, "Private")) ||
          (operation.operation == "DECRYPT" && !rsaKeyIs(operation, "Public"))))
    },
    rule(
      "C12",
      "Raw password used as key",
      "KDF",
      261,
      FindingSeverity.High,
      "Password-like input is used directly as cryptographic key material.",
      "Derive a key with a salted password KDF before use.",
      Set("KEY")
    ) { (_, operation) =>
      Set(SymmetricEncryption, AsymmetricEncryption, MacSignature).contains(operation.opType) &&
      operation.passwordSource && !operation.keyTransformed && operation.keySourceType != CryptoSourceType.Kdf.storedValue &&
      !ProtocolSemantics.isPasswordKdfInternal(operation)
    },
    rule(
      "C13",
      "Insufficient asymmetric parameter strength",
      "AsymEnc",
      326,
      FindingSeverity.High,
      "The RSA, DSA or DH parameters are below the configured minimum strength.",
      "Use RSA keys of at least 2048 bits or a modern signature algorithm with adequate strength."
    ) { (_, operation) =>
      operation.keySize >= 0 && (
        (Set(AsymmetricEncryption, SymmetricEncryption).contains(operation.opType) && operation.algorithm == "RSA" &&
          operation.keySize < CryptoPolicy.MinimumRsaKeySize) ||
        (operation.opType == MacSignature && operation.algorithm == "DSA" && operation.operation == "KEY_GENERATION" &&
          operation.keySize < CryptoPolicy.MinimumDsaKeySize)
      )
    },
    semanticRule("C14", "Weak symmetric cipher", "SymEnc", 327,
      "A deprecated symmetric cipher algorithm is used.", "Use an authenticated modern cipher such as AES-GCM."),
    semanticRule("C15", "Weak MAC algorithm", "MAC", 327,
      "A MAC uses a deprecated digest algorithm or a truncated tag shorter than 96 bits.",
      "Use an appropriate MAC with a modern digest and a tag of at least 96 bits."),
    semanticRule("C16", "Weak signature digest", "Signature", 327,
      "A digital signature uses SHA-1 or MD5.", "Use an appropriate signature algorithm with a modern digest."),
    semanticRule("C17", "Verification disabled", "Verification", 295,
      "Certificate, hostname or token-signature verification is disabled or implemented as an unconditional success.",
      "Use the platform trust manager and hostname verifier, and verify token signatures with a restricted key/algorithm set."),
    semanticRule("C18", "Insecure transport", "Transport", 319,
      "Data is sent over cleartext HTTP or a deprecated SSL/TLS protocol version is enabled.",
      "Use HTTPS and require TLS 1.2 or newer.")
  )

  private[rules] lazy val legacyHmacSha1Rule: CryptoQLRule = all.find(_.id == "C15").get.copy(
    title = "Legacy HMAC-SHA1", severity = FindingSeverity.Medium,
    message = "HMAC-SHA1 is used for message authentication.",
    remediation = "Review the protocol requirements and migrate to HMAC-SHA256 where supported."
  )

  def run(cpg: Cpg, originalJavaInput: Option[java.nio.file.Path] = None): Vector[CryptoFinding] = {
    val summaries = new org.codeminers.standalone.enrichment.JavaValueSummaries(cpg)
    val resourceKeys = originalJavaInput.map(new JavaResourceKeyEvidence(cpg, _))
    val pythonKeyTransforms = new org.codeminers.standalone.enrichment.PythonKeyTransformationResolver(cpg)
    val pythonProtocol = new PythonProtocolEvidence(cpg)
    val pythonHashUse = new PythonHashUseEvidence(cpg, pythonKeyTransforms)
    val defaultIvUses = if (cpg.cryptoOperation.exists(o => o.srcFile.endsWith(".py") &&
      o.opType == SymmetricEncryption && o.mode == "CBC")) new PythonCbcUseEvidence(cpg).defaultIvEncryption
      else Map.empty[Long, Vector[FindingEvidence]]
    val javaUse = new JavaUsageEvidence(cpg)
    val facts = cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.map { operation =>
      val original = toFact(operation)
      val ivUseEvidence = operation.realizingCall.l.flatMap(c => defaultIvUses.getOrElse(c.id, Vector.empty)).distinct.toVector
      val fact = original.copy(keyTransformed = original.passwordSource && pythonKeyTransforms.keyTransformations(operation).nonEmpty,
        hashInputTransformed = original.passwordSource && pythonKeyTransforms.hashInputAlreadyTransformed(operation),
        publicQueryDecoding = (original.isDeprecated || original.passwordSource) && pythonHashUse.publicQueryDecoding(operation),
        defaultIvEncryptionProven = ivUseEvidence.nonEmpty,
        evidence = original.evidence ++ ivUseEvidence,
        protocolPrimitive = pythonProtocol.isProtocolPrimitive(operation),
        ciphertextAuthentication = Option.when(operation.srcFile.endsWith(".py") && operation.opType == SymmetricEncryption)(
          pythonProtocol.ciphertextAuthenticated(operation)))
      val extra = operation.realizingCall.l.filter(_.file.name.headOption.exists(_.endsWith(".java"))).flatMap { call =>
        val valueArguments = if ((call.name == "getInstance" && call.methodFullName.startsWith("javax.crypto.Cipher.")) ||
          (call.name == "getCipher" && call.methodFullName.startsWith("hudson.util.Secret.")))
          call.argument.l.filter(_.argumentIndex == 1) else Nil
        val materialArguments = if (call.name == "<init>" && call.methodFullName.startsWith("javax.crypto.spec.SecretKeySpec."))
          call.argument.l.filter(_.argumentIndex == 1) else if (call.name == "init" && call.methodFullName.startsWith("javax.crypto.Cipher."))
          call.argument.l.filter(_.argumentIndex == 2) else if (operation.algorithm == "XOR")
          call.argument.l.filter(_.argumentIndex == 2) else if (operation.operation == "KEY_GENERATION" &&
          call.method.name.matches("generate(?:Random)?Key")) call.argument.l.filter(_.argumentIndex == 0) else Nil
        val keyTrace = materialArguments.flatMap(summaries.resolve(_, material = true)).filter(_.sources.exists {
          case source: Call => SourceClassifier.classify(source) == CryptoSourceType.WeakRandom ||
            (source.name == "generateSecret" && source.methodFullName.startsWith("javax.crypto.SecretKeyFactory.")) ||
            (source.name == "mac" && source.methodFullName.startsWith("jenkins.security.HMACConfidentialKey.")) ||
            (source.name == "getJnlpMac" && source.methodFullName.startsWith("hudson.slaves.SlaveComputer."))
          case _ => false
        }).flatMap(_.trace).map(step => FindingEvidence("KEY_PATH", step.label,
          s"${step.file.name.headOption.getOrElse("UNKNOWN")}:${step.lineNumber.getOrElse(-1)} ${step.code}"))
        keyTrace ++ (if (operation.opType == SymmetricEncryption)
          materialArguments.flatMap(a => resourceKeys.toVector.flatMap(_.evidence(a))) else Nil) ++
          valueArguments.flatMap(summaries.resolve(_)).flatMap { summary =>
          val kind = if (summary.defaultPath) "CONFIG_DEFAULT" else "VALUE_PATH"
          summary.trace.map(step => FindingEvidence(kind, step.label,
            s"${step.file.name.headOption.getOrElse("UNKNOWN")}:${step.lineNumber.getOrElse(-1)} ${step.code}"))
        }
      }
      // Preserve repeated array elements: sixteen zero nodes prove a whole zero IV.
      javaUse.refine(operation, fact.copy(evidence = fact.evidence ++ extra.distinct.filterNot(fact.evidence.contains)))
    }
    val language = if (cpg.file.name.exists(_.endsWith(".java"))) org.codeminers.standalone.adapter.AdapterLanguage.Java
      else if (cpg.file.name.exists(_.endsWith(".py"))) org.codeminers.standalone.adapter.AdapterLanguage.Python
      else org.codeminers.standalone.adapter.AdapterLanguage.Cpp
    val c4Facts = if (language == org.codeminers.standalone.adapter.AdapterLanguage.Java)
      Some(new JavaCipherUseEvidence(cpg).forC4(facts.toVector)) else None
    val semanticFacts = cpg.cryptoOperation.filter(_.semanticKind.nonEmpty).l.map(toFact).toVector
    mergeFindings(evaluate(facts.toVector ++ semanticFacts, c4Facts))
  }

  def evaluate(operations: Seq[CryptoOperationFact],
    c4Operations: Option[Seq[CryptoOperationFact]] = None): Vector[CryptoFinding] = {
    val context = new CryptoQLContext(operations.filter(_.semanticKind.isEmpty).toVector)
    val c4Context = c4Operations.map(ops => new CryptoQLContext(ops.toVector)).getOrElse(context)
    val primary = all.iterator
      .flatMap { rule =>
        val selected = if (rule.id == "C4") c4Context else context
        val candidates = if (rule.id == "C4") c4Operations.getOrElse(operations.filter(_.semanticKind.isEmpty)) else operations
        candidates.iterator.filter(op => rule.predicate.exists(_(selected, op))).map(toFinding(rule, _))
      }
      .toVector
    (primary ++ decryptionFindings(primary)).distinctBy(findingIdentity)
      .sortBy(finding => (finding.srcFile, finding.lineNo, finding.ruleId))
  }

  /** Materialize only readers of an independently confirmed C4 writer. Keep the write location as evidence so
    * independent API locations can be evaluated without treating the file format as two independent root causes.
    */
  private def decryptionFindings(findings: Vector[CryptoFinding]): Vector[CryptoFinding] = {
    val readers = for {
      writer <- findings if writer.ruleId == "C4" && writer.operation == "ENCRYPT"
      evidence <- writer.evidence if evidence.kind == "DECRYPTION_CONSUMER"
      site <- evidence.location
    } yield ((site, writer.algorithm, writer.mode), (writer, evidence))
    readers.groupMap(_._1)(_._2).iterator.map { case ((site, algorithm, mode), linked) =>
      val ordered = linked.sortBy((writer, read) => (writer.srcFile, writer.lineNo, read.code))
      val (writer, read) = ordered.head
      val origins = ordered.map { (producer, _) =>
        val call = producer.evidence.find(_.kind == "ENCRYPTION_CONSUMER")
          .map(_.code).getOrElse(producer.title)
        FindingEvidence("RELATED_ENCRYPTION", "CALL", call, Some(FindingLocation(producer.srcFile, producer.lineNo)))
      }.distinct
      writer.copy(
        fingerprint = Vector("C4", "DECRYPT", site.srcFile, site.lineNo.toString, algorithm, mode).mkString("|").hashCode.toHexString,
        title = "Decryption without ciphertext authentication",
        message = "This path releases decrypted data without verifying the ciphertext it reads.",
        remediation = "Verify ciphertext authentication before releasing plaintext.",
        srcFile = site.srcFile,
        lineNo = site.lineNo,
        operation = "DECRYPT",
        evidence = FindingEvidence("CALL", read.nodeLabel, read.code) +: origins
      )
    }.toVector
  }

  /** A factory may legitimately realize both encryption and decryption. Rules
    * about the primitive configuration or its key source should still produce
    * one finding at that factory location, not one copy per direction.
    */
  private def findingIdentity(finding: CryptoFinding): String =
    if (Set("C1", "C2", "C12").contains(finding.ruleId))
      Vector(
        finding.ruleId,
        finding.srcFile,
        finding.lineNo.toString,
        finding.operationType,
        finding.algorithm,
        finding.mode
      ).mkString("|")
    else s"${finding.ruleId}|${finding.fingerprint}"

  private def rule(
    id: String,
    title: String,
    category: String,
    cwe: Int,
    severity: FindingSeverity,
    message: String,
    remediation: String,
    evidenceKinds: Set[String] = Set.empty
  )(predicate: (CryptoQLContext, CryptoOperationFact) => Boolean): CryptoQLRule =
    rule(id, title, category, Some(cwe), severity, message, remediation, evidenceKinds)(predicate)

  private def rule(
    id: String,
    title: String,
    category: String,
    cwe: Option[Int],
    severity: FindingSeverity,
    message: String,
    remediation: String,
    evidenceKinds: Set[String]
  )(predicate: (CryptoQLContext, CryptoOperationFact) => Boolean): CryptoQLRule =
    CryptoQLRule(id, title, category, cwe, severity, message, remediation,
      Some((context, operation) => if (operation.semanticKind.nonEmpty) semanticPredicate(id, operation)
        else predicate(context, operation)), evidenceKinds)

  private def semanticRule(id: String, title: String, category: String, cwe: Int,
    message: String, remediation: String): CryptoQLRule =
    rule(id, title, category, Some(cwe), FindingSeverity.High, message, remediation, Set.empty)(
      (_, _) => false)

  /** Detection policy over persisted, language-independent operation properties. No source/API scanning here. */
  private def semanticPredicate(id: String, o: CryptoOperationFact): Boolean = id match {
    case "C2" => o.keySourceType == Hardcoded &&
      ((Set("KEY_STORAGE", "AUTHENTICATION").contains(o.opType) &&
        Set("KeyStoreAccess", "CredentialUse").contains(o.semanticKind)) ||
        (o.opType == SymmetricEncryption && o.semanticKind == "KeyOrigin"))
    case "C9" => o.semanticKind == "RandomUse" && o.opType == Randomness &&
      o.source == WeakRandom && o.usedForCrypto.contains(true)
    case "C11" => o.semanticKind == "RsaPadding" && o.opType == AsymmetricEncryption &&
      o.algorithm == "RSA" && o.padding == "PKCS1"
    case "C13" => o.semanticKind == "AsymmetricParameters" && o.opType == AsymmetricEncryption &&
      o.algorithm == "DH" && o.keySize >= 0 && o.keySize < 2048
    case "C14" => o.semanticKind == "CipherAlgorithm" && o.isDeprecated
    case "C15" => (o.semanticKind == "MacAlgorithm" && o.opType == MacSignature && o.isDeprecated) ||
      (o.semanticKind == "AuthenticationTag" && o.opType == MacSignature && o.tagLength > 0 && o.tagLength < 96)
    case "C16" => o.semanticKind == "SignatureDigest" && o.opType == MacSignature && o.isDeprecated
    case "C17" => o.opType == "VERIFICATION" && o.verificationEnabled.contains(false)
    case "C18" => o.opType == "TRANSPORT" &&
      (o.transportScheme == "HTTP" || (o.protocolVersion.nonEmpty && o.isDeprecated))
    case _ => false
  }

  /** Preserve all evidence when ontology and API analyses identify the same operation. */
  private def mergeFindings(findings: Vector[CryptoFinding]): Vector[CryptoFinding] =
    findings.groupBy(f =>
      if (Set("C2", "C9", "C13", "C14").contains(f.ruleId))
        Vector(f.ruleId, f.srcFile, f.lineNo.toString, f.operationType, f.algorithm, f.mode).mkString("|")
      else findingIdentity(f)
    ).values.map { group =>
      val first = group.head
      val evidence = group.flatMap(_.evidence).distinct
      first.copy(evidence = evidence, fingerprint =
        (first.ruleId + "|" + first.srcFile + "|" + first.lineNo + "|" + first.operationType + "|" +
          first.operation + "|" + first.algorithm + "|" + first.mode + "|" +
          evidence.map(e => s"${e.kind}:${e.nodeLabel}:${e.identityCode}").sorted.mkString("|")).hashCode.toHexString)
    }.toVector.sortBy(f => (f.srcFile, f.lineNo, f.ruleId))

  private def isCompleteEncryption(operation: CryptoOperationFact): Boolean =
    operation.opType == SymmetricEncryption && Set("ENCRYPT", "CONFIGURE", "INITIALIZE").contains(
      operation.operation
    ) &&
      operation.resolution == Complete

  /** A classified source is reportable only when the evidence has the expected terminal node kind. This prevents a
    * nearby constant in a busy expression from overriding a parameter, field, KDF result, or protocol state.
    */
  private def hasConcreteSourceProof(
    operation: CryptoOperationFact,
    evidenceKinds: Set[String],
    sourceType: String
  ): Boolean = {
    val evidence = operation.evidence.filter(item => evidenceKinds.contains(item.kind))
    sourceType match {
      case Hardcoded =>
        val zeroAllocation = evidence.nonEmpty && evidence.forall(item =>
          item.nodeLabel == "CALL" && SourceClassifier.isImplicitZeroArrayCode(item.code))
        // A lone `0`/`null` is a null pointer or an omitted argument, but several zero literals are the elements
        // of an all-zero array constant such as `static final byte[] iv = {0, 0, ...}`.
        val zeroElements = evidence.count(item => item.nodeLabel == "LITERAL" && item.code.trim == "0")
        zeroAllocation || (evidence.nonEmpty && (zeroElements >= 2 || evidence.exists(item =>
          item.nodeLabel == "LITERAL" && !Set("0", "null", "none").contains(item.code.trim.toLowerCase)
        )) &&
        evidence.forall(item => Set("LITERAL", "METHOD_PARAMETER_IN", "IDENTIFIER").contains(item.nodeLabel)))
      case WeakRandom =>
        evidence.nonEmpty && evidence.exists(item =>
          item.nodeLabel == "CALL" && item.code.toLowerCase.matches(
            "(?s).*(?:random\\.(?:random|randint|randrange|getrandbits|choice|shuffle|sample|uniform)|math\\.random|libc-rand).*"
          )
        )
      case _ => false
    }
  }

  /** A literal proves an embedded secret only when it is plausible material,
    * not syntax/configuration consumed by a wrapper around the real value.
    * This rejects provenance artifacts such as utf-8, ascii, md5, rc4, '-'
    * and '_' while preserving arbitrary byte/string/numeric keys.
    */
  private def hasSecretLiteralProof(operation: CryptoOperationFact, evidenceKinds: Set[String]): Boolean = {
    val evidence = operation.evidence.filter(item => evidenceKinds.contains(item.kind))
    evidence.nonEmpty && evidence.forall(_.nodeLabel == "LITERAL") && evidence.exists(isSecretLiteral)
  }

  private def isSecretLiteral(item: FindingEvidence): Boolean = {
    val value = item.code.trim
      .replaceFirst("(?i)^[rubf]+", "")
      .stripPrefix("\"").stripSuffix("\"")
      .stripPrefix("'").stripSuffix("'")
      .trim.toLowerCase.replaceAll("[-_]", "")
    item.nodeLabel == "LITERAL" && value.nonEmpty && !Set(
      "none", "null", "0", "true", "false",
      "utf8", "utf16", "utf32", "ascii", "latin1", "unicode", "strict", "ignore", "replace",
      "aes", "des", "des3", "tripledes", "blowfish", "rc2", "rc4", "arc2", "arc4",
      "md2", "md4", "md5", "sha", "sha1", "sha224", "sha256", "sha384", "sha512"
    ).contains(value)
  }

  /** A password KDF is deterministic: a key derived from a literal or predictable password (with a fixed or
    * absent salt) is predictable too. The KDF is linked by method scope because the key operation records the
    * KDF call as its source.
    */
  private def derivedFromConstantPassword(context: CryptoQLContext, operation: CryptoOperationFact): Boolean =
    context.operations.exists { kdf =>
      kdf.opType == KeyDerivation && kdf.scopes.intersect(operation.scopes).nonEmpty &&
      kdf.evidence.exists(item => item.kind == "KEY" &&
        (isSecretLiteral(item) || isPredictableProperty(item)))
    }

  /** The RSA key reaching the cipher is typed or obtained as a `Private`/`Public` key (`PrivateKey key`, `getPrivate()`). */
  /** The key is a `kind` (Public/Private) key: by its provenance, or by the key argument of the `init` call. */
  private def rsaKeyIs(operation: CryptoOperationFact, kind: String): Boolean =
    operation.evidence.exists(item =>
      (item.kind == "KEY" && (item.code.contains(s"${kind}Key") || item.code.contains(s"get$kind("))) ||
      (item.kind == "CALL" && initKeyArgument(item.code).exists(_.toLowerCase.contains(kind.toLowerCase))))

  /** `cipher.init(Cipher.ENCRYPT_MODE, privateKey, ...)` -> `privateKey, ...`. */
  private def initKeyArgument(code: String): Option[String] =
    """(?s).*\.init\(\s*[^,]*,(.*)\)\s*""".r.findFirstMatchIn(code).map(_.group(1))

  private def hasPredictablePropertyProof(operation: CryptoOperationFact): Boolean =
    operation.evidence.exists(item => item.kind == "KEY" && isPredictableProperty(item))

  private def isPredictableProperty(item: FindingEvidence): Boolean =
    item.nodeLabel == "CALL" &&
      item.code.matches("(?s).*\\bSystem\\.getProperty\\s*\\(\\s*\"(?:user\\.home|user\\.name|user\\.dir|java\\.io\\.tmpdir)\"\\s*\\).*")

  /** MD5/SHA-1 are still widely used as non-security checksums and cache keys. C5 is a crypto-misuse rule, so it
    * requires evidence that the hash protects a password, credential, signature, token, key, or other security value.
    */
  private def securityRelevantHash(operation: CryptoOperationFact): Boolean = {
    val contextText = (operation.scopes.toVector ++ operation.evidence.map(_.code) :+ operation.srcFile).mkString(" ")
    // Joern includes the return and parameter types in Java method full names. MessageDigest's
    // package name also appears at fully qualified calls, regardless of what the digest protects.
    val context = (if (operation.srcFile.endsWith(".java"))
      contextText.replace("java.security.MessageDigest", "MessageDigest") else contextText).toLowerCase
    val explicitlyNonSecurity =
      ("(?:usedforsecurity\\s*=\\s*false|check[_-]?sum|cache|etag|content[_-]?hash|file[_-]?hash|dedup|" +
        "fingerprint[_-]?file|build[_-]?id|multipart|boundary|fake[_-]?data|test[_-]?data)").r
        .findFirstIn(context)
        .nonEmpty
    val securityContext =
      "(?:password|passwd|passphrase|credential|secret|auth|token|signature|encrypt|decrypt|crypto|security|integrity|broken[_-]?hash|weak[_-]?hash|(?:^|[^a-z0-9])(?:pwd|sign|certificate|cert|mac|kdf)(?:[^a-z0-9]|$))".r
        .findFirstIn(context)
        .nonEmpty
    // A Crypto.Hash/cryptography hash constructor is accepted by the adapter only after callable identity has been
    // proven.  Unlike a direct hashlib call, that proof remains meaningful even when the constructor is forwarded
    // through a field, global, return value, or parameter and the local call text becomes `argument()` or `x()`.
    // Keep the non-security and protocol suppressions below so checksum and compatibility uses remain excluded.
    //
    // The proof only substitutes for the security-relevance requirement while the call text really has lost the
    // digest name.  `from hashlib import md5` produces a bare `md5(...)` call whose text contains no "hashlib.",
    // so testing for that string alone waived the requirement for every such Python file and reported SSH/SASL
    // protocol digests, cache keys, and content checksums as misuses.
    // Two shapes still name the digest at the call site:
    //   * a bare call, which is what `from hashlib import md5` produces: `md5(data)`, `SHA1(data)`
    //   * a lowercase module constructor from the removed Python 2 stdlib: `md5.new(...)`, `sha.new(...)`
    // An upper-case `MD5.new(...)` / `SHA.new(...)` is a pycryptodome constructor matched by its own adapter rule,
    // so it keeps the waiver; case is the only signal separating the two library families here.
    val bareDigestCall  = """(?s).*(?:^|[^A-Za-z0-9_.])(?i:md[245]|sha|sha1|ripemd(?:160)?)\s*\(.*"""
    val legacyModuleNew = """(?s).*(?:^|[^A-Za-z0-9_.])(?:md[245]|sha|ripemd(?:160)?)\s*\.\s*new\s*\(.*"""
    val namesDigestDirectly = operation.evidence.exists { item =>
      item.code.matches(bareDigestCall) || item.code.matches(legacyModuleNew)
    }
    val provenPythonCryptoHash =
      operation.srcFile.toLowerCase.endsWith(".py") && !context.contains("hashlib.") && !namesDigestDirectly
    !ProtocolSemantics.isWeakHashCompatibility(operation) &&
    !explicitlyNonSecurity &&
    (operation.passwordSource || securityContext || provenPythonCryptoHash ||
      operation.evidence.exists(_.kind == "DIGEST_CONSUMER"))
  }

  private def toFinding(rule: CryptoQLRule, operation: CryptoOperationFact): CryptoFinding = {
    val policy = if (rule.id == "C16" && operation.semanticKind == "SignatureDigest" && operation.operation == "VERIFY")
      rule.copy(title = "Legacy signature verification requires review", severity = FindingSeverity.Medium,
        message = "Signature verification accepts SHA-1 or MD5. Review live protocol acceptance separately from historical-signature compatibility.",
        remediation = "Disable legacy algorithms in new protocol negotiations; review historical verification requirements before migration.")
    else if (rule.id == "C17" && operation.semanticKind == "VerificationUse")
      rule.copy(title = "Verification result not enforced",
        cwe = Some(if (operation.verificationKind == "SIGNATURE_RESULT") 347 else 354),
        message = "The checked data is used on a path where Boolean verification returns false.",
        remediation = "Reject the checked data when verification fails, before returning or using it.")
    else if (rule.id == "C15" && operation.semanticDetail.startsWith("RFC 8429:"))
      rule.copy(message = "A Kerberos checksum suite deprecated by RFC 8429 is used.",
        remediation = "Use a supported modern Kerberos checksum and encryption suite.")
    else if (rule.id == "C15" && operation.semanticDetail == CryptoPolicy.LegacyJavaHmacSha1Evidence)
      legacyHmacSha1Rule
    else rule
    val fingerprintParts = Vector(
      rule.id,
      operation.srcFile,
      operation.lineNo.toString,
      operation.opType,
      operation.operation,
      operation.algorithm,
      operation.mode
    ) ++ operation.evidence.map(evidence => s"${evidence.kind}:${evidence.identityCode}").sorted // evidence order varies per run
    CryptoFinding(
      fingerprint = fingerprintParts.mkString("|").hashCode.toHexString,
      ruleId = rule.id,
      title = policy.title,
      category = policy.category,
      cwe = policy.cwe,
      severity = policy.severity,
      message = policy.message,
      remediation = policy.remediation,
      srcFile = operation.srcFile,
      lineNo = operation.lineNo,
      operationType = operation.opType,
      operation = operation.operation,
      algorithm = operation.algorithm,
      mode = operation.mode,
      evidence = operation.evidence.filter(evidence => operation.semanticKind.nonEmpty || rule.evidenceKinds.isEmpty || rule.evidenceKinds(evidence.kind)) ++
        Option.when(rule.id == "C13" && operation.keySize >= 0)(
          FindingEvidence("KEY_SIZE", "CRYPTO_OPERATION", s"${operation.keySize} bits")
        ).toVector
    )
  }

  private def toFact(operation: CryptoOperation): CryptoOperationFact = {
    val realizingCallEvidence = operation.realizingCall.l.map(call => FindingEvidence("CALL", call.label, call.code))
    val sourceEvidence = Vector(
      "KEY"   -> operation.keySourceNode.l,
      "IV"    -> operation.ivSourceNode.l,
      "SALT"  -> operation.saltSourceNode.l,
      "NONCE" -> operation.nonceSourceNode.l
    ).flatMap { case (kind, nodes) =>
      // One entry per source node, not per distinct text: the eight elements of `{0, 0, 0, 0, 0, 0, 0, 0}` are eight
      // pieces of evidence, which is what separates an all-zero array constant from a lone null/offset `0`.
      nodes.distinctBy(_.id).flatMap { node =>
        val code = node match {
          case ast: AstNode => ast.code
          case other        => other.label
        }
        val ordinary = FindingEvidence(kind, node.label, code)
        val default = node match {
          case literal: Literal if kind == "KEY" && isFieldInitializerLiteral(literal) =>
            Vector(FindingEvidence("KEY_DEFAULT", node.label, code))
          case _ => Vector.empty
        }
        ordinary +: default
      }
    }
    val evidence = if (operation.semanticKind == "KeyOrigin") operation.securityEvidenceNode.l.collect { case call: Call =>
      val kind = if (operation.realizingCall.exists(_.id == call.id)) "KEY" else "KEY_PATH"
      val location = call.lineNumber.map(line => FindingLocation(call.file.name.headOption.getOrElse(operation.srcFile), line))
      FindingEvidence(kind, call.label, call.code, location)
    }.toVector else if (operation.semanticKind == "VerificationUse") operation.securityEvidenceNode.l.map { node =>
      val code = node match {
        case ast: AstNode => s"${ast.file.name.headOption.getOrElse(operation.srcFile)}:${ast.lineNumber.getOrElse(-1)} ${ast.code}"
        case _ => node.label
      }
      FindingEvidence(if (operation.realizingCall.exists(_.id == node.id)) "CHECK" else "CONTEXT", node.label, code)
    }.toVector else if (operation.semanticKind.nonEmpty) operation.securityEvidenceNode.l.map { node =>
      val code = if (operation.semanticDetail.nonEmpty) operation.semanticDetail else node match {
        case ast: AstNode => ast.code
        case o: CryptoOperation => s"${o.operation}:${o.algorithm}:${o.mode}"
        case _ => node.label
      }
      FindingEvidence("API", node.label, code)
    }.toVector else (realizingCallEvidence.distinct ++ sourceEvidence).toVector
    val sourceNames = operation.keySourceNode.l.flatMap {
      case identifier: Identifier       => Some(identifier.name)
      case parameter: MethodParameterIn => Some(parameter.name)
      // A field read names the field; its qualifier is a class (`StaticDESPasswordCipher.KEY` is not a password).
      case access: Call if access.name == "<operator>.fieldAccess" =>
        access.ast.isFieldIdentifier.canonicalName.headOption
      case ast: AstNode => Some(ast.code)
      case _            => None
    }
    val scopes = operation.realizingCall.method.fullName.l.toSet
    val semanticContext = operation.realizingCall.method.l.flatMap { method =>
      // A primitive constructor alone (for example hashlib.sha1()) does not expose whether the digest is an
      // OAuth body hash, an OAEP parameter, a transfer checksum, or an application-selected password hash.  Keep
      // the enclosing method's call/name vocabulary as non-reporting context so protocol models can require the
      // complete local construction without relying on source-file allowlists.
      Vector(method.name, method.fullName) ++
        method.ast.isCall.code.l ++
        method.ast.isIdentifier.name.l
    }.filter(_.nonEmpty).toSet
    CryptoOperationFact(
      operationId = operation.id,
      opType = operation.opType,
      operation = operation.operation.toUpperCase,
      algorithm = operation.algorithm.toUpperCase,
      mode = operation.mode.toUpperCase,
      padding = operation.padding.toUpperCase,
      keySize = operation.keySize,
      iterations = operation.iterations,
      saltUsed = operation.saltUsed,
      usedForCrypto = operation.usedForCrypto,
      source = operation.source,
      isDeprecated = operation.isDeprecated,
      providesIntegrity = operation.providesIntegrity,
      keySourceType = operation.keySourceType,
      ivSourceType = operation.ivSourceType,
      saltSourceType = operation.saltSourceType,
      nonceSourceType = operation.nonceSourceType,
      resolution = operation.resolution,
      srcFile = operation.srcFile,
      lineNo = operation.lineNo,
      scopes = if (scopes.nonEmpty) scopes else Set(s"${operation.srcFile}:<unknown>"),
      passwordSource = sourceNames.exists(_.matches("(?i).*(?:password|passwd|passphrase|pwd).*")),
      evidence = evidence,
      semanticContext = semanticContext,
      semanticKind = operation.semanticKind,
      verificationKind = operation.verificationKind,
      verificationEnabled = operation.verificationEnabled,
      transportScheme = operation.transportScheme,
      protocolVersion = operation.protocolVersion,
      tagLength = operation.tagLength,
      semanticDetail = operation.semanticDetail
    )
  }

  /** A literal already linked to the key is a reachable default when its enclosing assignment initializes a
    * field. Runtime writes may override it on other paths; they do not erase this default path. */
  private def isFieldInitializerLiteral(literal: Literal): Boolean =
    literal.inAst.isCall.nameExact("<operator>.assignment").l.exists { assignment =>
      JavaFrontend.isInitializer(assignment.method.name) &&
      assignment.argument.l.find(_.argumentIndex == 1).exists {
        case access: Call => access.name == "<operator>.fieldAccess"
        case _ => false
      }
    }

  extension (operation: CryptoOperationFact) {
    private def sourceTypes: Set[String] =
      Set(operation.keySourceType, operation.ivSourceType, operation.saltSourceType, operation.nonceSourceType)
  }
}
