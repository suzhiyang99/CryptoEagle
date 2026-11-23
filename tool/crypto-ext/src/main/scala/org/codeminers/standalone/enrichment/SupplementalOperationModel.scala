package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, Expression, Literal, Method, StoredNode}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.JavaFrontend
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.enrichment.JavaConstantPathEvaluator
import org.codeminers.standalone.ontology.{CryptoPolicy, CryptoSourceType}

import org.codeminers.standalone.rules.{JavaUsageEvidence, PythonCbcUseEvidence, PythonProtocolEvidence}
import OperationObservationKind.*

/** Library-specific semantic extraction. This model never constructs findings or selects a rule. */
private[enrichment] object SupplementalOperationModel {
  private val HardcodedKeyStorePassword = KeyStoreAccess
  private val CipherContext = CipherAlgorithm
  private val PredictableRandomSeed = RandomUse
  private val WeakDhParameters = AsymmetricParameters
  private val MacContext = MacAlgorithm
  private val SignatureContext = SignatureDigest

  def observations(cpg: Cpg, language: AdapterLanguage, sharedIndex: Option[JavaValueIndex],
    reportLegacyHmacSha1: Boolean): Vector[OperationObservation] = {
    def stage(name: String)(body: => Vector[OperationObservation]): Vector[OperationObservation] =
      OperationModelTiming.measure(name)(body)
    stage("keyStore")(keyStore(cpg)) ++ stage("cipher")(deprecatedCipher(cpg)) ++
      stage("random")(predictableRandom(cpg, language)) ++ stage("DH")(weakDh(cpg)) ++
      stage("MAC")(deprecatedMac(cpg, reportLegacyHmacSha1)) ++
      stage("javaEntryPoints")(javaSecurityEntryPoints(cpg, language, sharedIndex)) ++
      stage("pythonSignature")(pythonSignatureEntryPoints(cpg, language)) ++
      stage("tags")(shortTags(cpg, language, sharedIndex)) ++ stage("pythonRSA")(pythonRsaPkcs1(cpg, language)) ++
      stage("credentials")(hardcodedCredentials(cpg, language)) ++
      stage("numericKeys")(numericKeyOrigins(cpg, language, sharedIndex)) ++
      stage("transport")(TransportOperationModel.observations(cpg, language, sharedIndex)) ++
      stage("verificationUse")(VerificationUseModel.observations(cpg, language, sharedIndex)) ++
      stage("secureContexts")(securePrimitiveContexts(cpg))
  }

  private def numericKeyOrigins(cpg: Cpg, language: AdapterLanguage,
    sharedIndex: Option[JavaValueIndex]): Vector[OperationObservation] = {
    if (language != AdapterLanguage.Java || !AnalysisAblation.full) return Vector.empty
    val values = new JavaValueSummaries(cpg, sharedIndex = sharedIndex, traceNumericKeyEncoding = true)
    cpg.cryptoOperation.filter(o => o.semanticKind.isEmpty && o.algorithm == "XOR").l
      .flatMap(_.realizingCall.l).distinctBy(_.id).flatMap { use =>
        use.argument.argumentIndex(2).headOption.flatMap(values.resolve(_, material = true))
          .filter(_.arrayOutput).toVector.flatMap { summary =>
            summary.sources.collect { case l: Literal => l }.flatMap { literal =>
              val path = summary.traceFor(literal)
              path.find(a => a.name == "<operator>.assignment" && a.method.name == "<clinit>" &&
                a.argument.argumentIndex(1).isCall.nameExact("<operator>.fieldAccess").nonEmpty &&
                a.argument.argumentIndex(2).ast.exists(_.id == literal.id)).map { origin =>
                OperationObservation(KeyOrigin, origin, "SYMMETRIC_ENCRYPTION", "TRANSFORM", "XOR", "REPEATING_KEY",
                  evidenceNodes = (Vector(use) ++ path).distinctBy(_.id))
              }
            }
          }
      }.toVector
  }

  /** Preserve resolved modern algorithm settings as first-class contexts, alongside legacy ones. */
  private def securePrimitiveContexts(cpg: Cpg): Vector[OperationObservation] =
    cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.filter(o =>
      o.opType == "MAC_SIGNATURE" && !o.isDeprecated &&
      !o.algorithm.toUpperCase.matches(".*(?:MD5|SHA-?1).*"))
      .flatMap { o =>
        val role = if (o.operation == "MAC") Some(MacAlgorithm)
          else if (Set("SIGNATURE", "SIGN", "VERIFY").contains(o.operation)) Some(SignatureDigest)
          else None
        role.map(kind => OperationObservation(kind, o, o.opType, o.operation, o.algorithm))
      }.toVector

  private def pythonSignatureEntryPoints(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] =
    if (language != AdapterLanguage.Python) Vector.empty
    else cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.filter(o => o.opType == "MAC_SIGNATURE" && Set("SIGNATURE", "SIGN", "VERIFY").contains(o.operation) &&
      Set("MD5", "SHA1").contains(o.algorithm)).map(o =>
        OperationObservation(SignatureContext, o, o.opType, o.operation, o.algorithm)).toVector

  private def keyStore(cpg: Cpg): Vector[OperationObservation] = {
    val values = new org.codeminers.standalone.enrichment.JavaValueSummaries(cpg, includeRuntimeFieldWrites = true)
    cpg.call.l.collect {
      case call if Set("load", "getKey", "setKeyEntry", "store").contains(call.name) &&
        call.methodFullName.startsWith("java.security.KeyStore.") &&
        !(call.name == "load" && call.argument.argumentIndex(1).code.headOption.contains("null")) &&
        call.argument.argumentIndex(if (call.name == "setKeyEntry") 3 else 2).headOption.exists { argument =>
          // Resolve the password value, never literals buried in lookup/constructor arguments.
          // Typed field/constructor substitution preserves real default passwords across classes.
          values.resolve(argument, material = true).exists(summary => summary.sources.nonEmpty &&
            summary.sources.forall {
              case literal: io.shiftleft.codepropertygraph.generated.nodes.Literal => isConcreteSecret(literal.code)
              case _ => false
            })
        } => OperationObservation(HardcodedKeyStorePassword, call, "KEY_STORE", "ACCESS", detail = call.code)
    }.toVector
  }

  private def deprecatedCipher(cpg: Cpg): Vector[OperationObservation] = cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.collect {
    case operation if (operation.opType == "SYMMETRIC_ENCRYPTION" ||
      (operation.opType == "KEY_DERIVATION" && operation.algorithm.startsWith("PBEWITH") &&
        operation.algorithm.contains("AND") && JavaUsageEvidence.pbeCipherConsumer(operation))) =>
      OperationObservation(
        CipherContext,
        operation,
        operation.opType,
        operation.operation,
        operation.algorithm,
        operation.mode
      )
  }.toVector

  private def predictableRandom(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] = {
    val prefixes = if (language == AdapterLanguage.Python) new PythonCbcUseEvidence(cpg).evidence else Map.empty[Long, String]
    val saltUses = if (language == AdapterLanguage.Java) {
      cpg.cryptoOperation.filter(o => o.semanticKind.isEmpty && o.opType == "KEY_DERIVATION" &&
        o.saltSourceType == CryptoSourceType.WeakRandom.storedValue).l
        .flatMap(o => o.saltSourceNode.l.map(_.id -> s"${o.srcFile}:${o.lineNo} KDF salt"))
        .groupMap(_._1)(_._2)
    } else Map.empty[Long, List[String]]
    cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.collect {
      case operation
          if operation.opType == "RANDOMNESS" && operation.source == CryptoSourceType.WeakRandom.storedValue &&
            (operation.realizingCall.l.exists(c => saltUses.contains(c.id) || prefixes.contains(c.id)) ||
              weakRandomFeedsCrypto(cpg, operation) || weakRandomDefinesSecurityAsset(cpg, operation)) =>
        OperationObservation(PredictableRandomSeed, operation, operation.opType, operation.operation, operation.algorithm,
          detail = operation.realizingCall.l.flatMap(c => prefixes.get(c.id).toList ++
            saltUses.getOrElse(c.id, Nil)).distinct.mkString("; "))
    }.toVector
  }

  /** Recover a frontend-missed flow only when the random call is assigned to an exact symbol which is attached to a
    * sensitive source edge of a crypto operation in the same method. This retains C/C++ cast/assignment flows without
    * reverting to the former same-method heuristic that flagged unrelated counters and delays.
    */
  private def weakRandomFeedsCrypto(
    cpg: Cpg,
    randomOperation: io.shiftleft.codepropertygraph.generated.nodes.CryptoOperation
  ): Boolean = {
    val randomCalls = randomOperation.realizingCall.l
    randomCalls.exists { randomCall =>
      val assignedSymbols = cpg.call
        .nameExact("<operator>.assignment")
        .filter(_.method.id == randomCall.method.id)
        .filter(_.ast.isCall.id(randomCall.id).nonEmpty)
        .l
        .flatMap(_.argument.l.find(_.argumentIndex == 1).map(_.code.trim))
        .filter(_.matches("[A-Za-z_$][A-Za-z0-9_$]*"))
        .toSet
      assignedSymbols.nonEmpty && cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l
        .filterNot(_.opType == "RANDOMNESS")
        .filter(_.realizingCall.method.id.l.contains(randomCall.method.id))
        .exists { consumer =>
          val sensitiveCodes = (consumer.keySourceNode.l ++ consumer.ivSourceNode.l ++ consumer.saltSourceNode.l ++
            consumer.nonceSourceNode.l).collect { case ast: AstNode => ast.code.trim }.toSet
          assignedSymbols.exists(sensitiveCodes.contains)
        }
    }
  }

  /** Recognize security assets constructed across several statements. A method name or a nearby hash is not enough:
    * the random expression must be nested in a crypto call, assigned to an asset that is returned/consumed, or flow
    * through an exact symbol into a cryptographic source/container. This removes padding lengths, shard IDs, device
    * IDs, synthetic resource suffixes, and public RSA exponents.
    */
  private def weakRandomDefinesSecurityAsset(
    cpg: Cpg,
    randomOperation: io.shiftleft.codepropertygraph.generated.nodes.CryptoOperation
  ): Boolean = randomOperation.realizingCall.l.exists { randomCall =>
    val methodName = randomCall.method.name.toLowerCase
    val methodHasAssetRole = securityAssetTokens(methodName).nonEmpty
    val randomLine = randomCall.lineNumber.map(_.toInt).getOrElse(Int.MinValue)
    val explicitlyNonSecretUse = {
      val scope = randomCall.method.fullName.toLowerCase
      val nonSecretScope = scope.matches(
        "(?s).*(?:boundary|scan[_-]?id|randomized[_-]?iface|interface[_-]?id|populate[_-]?(?:db|data)|fixture|fake[_-]?data).*"
      )
      val tlsRoutingOnly = randomCall.inAst.isCall.code.l.exists(code => compactLower(code).contains("server_hostname="))
      val enclosingCalls = randomCall.method.ast.isCall
        .filter(call => call.id != randomCall.id && call.ast.id(randomCall.id).nonEmpty)
        .code
        .l
        .map(compactLower)
      val csprngSizingOnly = enclosingCalls.exists(code =>
        code.matches("(?s).*(?:os\\.urandom|secrets\\.(?:token_bytes|token_hex|token_urlsafe))\\(.*")
      )
      // Treat random values inside URL or HTTP expressions as routing values unless a cryptographic call encloses them.
      val urlRoutingOnly = enclosingCalls.exists(code =>
        code.matches("(?s).*(?:https?://|url|uri|request|get[_-]?page|fetch|download|\\.format\\().*")
      ) && !enclosingCalls.exists(code =>
        code.matches("(?s).*(?:hmac|hashlib|md5|sha1|sha256|encrypt|decrypt|sign|derive|kdf)\\s*\\(.*")
      )
      nonSecretScope || tlsRoutingOnly || urlRoutingOnly || csprngSizingOnly
    }

    if (explicitlyNonSecretUse) false
    else {

      val enclosingAssignments = randomCall.method.ast.isCall
        .filter(call => call.name.startsWith("<operator>.assignment") && call.ast.id(randomCall.id).nonEmpty)
        .l
      val assignedSymbols = enclosingAssignments
        .flatMap(_.argument.l.find(_.argumentIndex == 1).map(_.code))
        .map(_.replaceAll("\\s+", ""))
        .toSet
      val assignedAssetSymbols = assignedSymbols.filter(code => isDirectSecretAsset(code))

      val nestedCryptoUse = cpg.cryptoOperation.filter(_.semanticKind.isEmpty)
        .filterNot(_.opType == "RANDOMNESS")
        .realizingCall
        .filter(call => call.method.id == randomCall.method.id && call.lineNumber.map(_.toInt).getOrElse(Int.MaxValue) >= randomLine)
        .exists(_.ast.id(randomCall.id).nonEmpty)
      val sameMethodCrypto = cpg.cryptoOperation.filter(_.semanticKind.isEmpty)
        .filterNot(_.opType == "RANDOMNESS")
        .filter(operation =>
          operation.realizingCall.method.id.l.contains(randomCall.method.id) && operation.lineNo >= randomLine
        )
        .nonEmpty
      val exactSameMethodCryptoUse = assignedSymbols.nonEmpty && cpg.cryptoOperation.filter(_.semanticKind.isEmpty)
        .filterNot(_.opType == "RANDOMNESS")
        .realizingCall
        .filter(call => call.method.id == randomCall.method.id && call.lineNumber.map(_.toInt).getOrElse(Int.MaxValue) >= randomLine)
        .exists(call => assignedSymbols.exists(symbol => referencesSymbol(call.code, symbol)))
      val crossScopeCryptoUse = assignedSymbols.nonEmpty && cpg.cryptoOperation.filter(_.semanticKind.isEmpty)
        .filterNot(_.opType == "RANDOMNESS")
        .filter(operation => operation.srcFile == randomOperation.srcFile && operation.lineNo >= randomLine)
        .exists { consumer =>
          val sensitiveCodes = (consumer.keySourceNode.l ++ consumer.ivSourceNode.l ++ consumer.saltSourceNode.l ++
            consumer.nonceSourceNode.l).collect { case ast: AstNode => ast.code.replaceAll("\\s+", "") }.toSet
          assignedSymbols.exists(symbol => sensitiveCodes.contains(symbol))
        }

      // A generator/join return can hide the choice call inside frontend temporary blocks.
      // Require a security charset/alphabet at the exact call, a returned enclosing
      // assignment, and an explicit ordinary random provider (SystemRandom stays excluded).
      val returnedSecretSelection = randomCall.name == "choice" && randomCall.code.startsWith("random.choice(") &&
        randomCall.argument.l.filter(_.argumentIndex > 0).exists(a => isStrongSecretAsset(a.code)) &&
        (randomCall.method.ast.isReturn.exists(_.ast.id(randomCall.id).nonEmpty) ||
          assignedSymbols.exists(symbol => randomCall.method.ast.isReturn.code.l.exists(code => referencesSymbol(code, symbol))))
      val returnedAsset = returnedSecretSelection || assignedAssetSymbols.exists { symbol =>
        (isStrongSecretAsset(symbol) || methodHasAssetRole) &&
        randomCall.method.ast.isReturn.code.l.exists(code => referencesSymbol(code, symbol))
      }
      val forwardedToAsset = assignedSymbols.exists { symbol =>
        !isNonSecretRandomRole(symbol) && randomCall.method.ast.isCall.nameExact("<operator>.assignment").l.exists { assignment =>
          val lhs = assignment.argument.l.find(_.argumentIndex == 1).map(_.code.replaceAll("\\s+", "")).getOrElse("")
          val rhs = assignment.argument.l.find(_.argumentIndex == 2).map(_.code).getOrElse("")
          isDirectSecretAsset(lhs) && referencesSymbol(rhs, symbol) &&
          assignment.lineNumber.map(_.toInt).getOrElse(Int.MaxValue) >= randomLine
        }
      }
      val assetContainerFlow = assignedSymbols.exists { symbol =>
        randomCall.method.ast.isCall.l.exists { call =>
          val code = call.code.replaceAll("\\s+", "")
          referencesSymbol(code, symbol) && securityAssetTokens(code.takeWhile(_ != '(')).nonEmpty &&
          Set("append", "extend", "add", "update").contains(call.name.toLowerCase) &&
          call.lineNumber.map(_.toInt).getOrElse(Int.MaxValue) >= randomLine
        }
      }

      // Random character selection often contributes to a StringBuilder through an integer index, not through
      // the key/nonce argument itself. Require that contribution to reach the returned builder in a security-asset
      // generator; random buffer lengths and unrelated builder mutations are not sufficient.
      val returnedBuilderAsset = methodHasAssetRole && {
        val dependent = collection.mutable.Set.from(assignedSymbols)
        val assignments = randomCall.method.ast.isCall.nameExact("<operator>.assignment").l
        (1 to 4).foreach { _ =>
          assignments.foreach { assignment =>
            val rhs = assignment.argument.l.find(_.argumentIndex == 2).map(_.code).getOrElse("")
            if (dependent.exists(symbol => referencesSymbol(rhs, symbol)))
              assignment.argument.l.find(_.argumentIndex == 1).map(_.code.trim)
                .filter(_.matches("[A-Za-z_$][A-Za-z0-9_$]*")).foreach(dependent += _)
          }
        }
        randomCall.method.ast.isCall.nameExact("append").l.exists { append =>
          val builder = append.argument.l.find(_.argumentIndex == 0).collect {
            case id: io.shiftleft.codepropertygraph.generated.nodes.Identifier
                if Set("java.lang.StringBuilder", "java.lang.StringBuffer").contains(id.typeFullName) => id.name
          }
          val dependsOnRandom = append.argument.l.filter(_.argumentIndex > 0)
            .exists(argument => dependent.exists(symbol => referencesSymbol(argument.code, symbol)))
          builder.exists { name =>
            dependsOnRandom && randomCall.method.ast.isReturn.l.exists { returned =>
              returned.ast.isCall.nameExact("toString").l.exists(call =>
                call.argument.l.find(_.argumentIndex == 0).exists(_.code.trim == name)) &&
                !assignments.exists(assignment =>
                  assignment.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == name) &&
                    assignment.lineNumber.getOrElse(0) > append.lineNumber.getOrElse(Int.MaxValue) &&
                    assignment.lineNumber.getOrElse(Int.MaxValue) < returned.lineNumber.getOrElse(0))
            }
          }
        }
      }

      nestedCryptoUse || exactSameMethodCryptoUse || crossScopeCryptoUse ||
      assignedAssetSymbols.exists(isStrongSecretAsset) || forwardedToAsset || returnedAsset || assetContainerFlow ||
      returnedBuilderAsset || (methodHasAssetRole && sameMethodCrypto)
    }
  }

  private def isDirectSecretAsset(value: String): Boolean = {
    !isNonSecretRandomRole(value) && securityAssetTokens(value).nonEmpty
  }

  private def isNonSecretRandomRole(value: String): Boolean = {
    val normalized = value.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase
    normalized.matches(
      "(?s).*(?:padding|pad[_-]?len|padding[_-]?length|data[_-]?len|length|size|shard|arn|device|" +
        "public[_-]?exponent|bip32.*index|derivation[_-]?index).*"
    ) || Set("e", "id", "id_string", "random").contains(normalized.split("\\.").lastOption.getOrElse(normalized))
  }

  private def isStrongSecretAsset(value: String): Boolean = {
    val normalized = value.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase
    normalized.matches(
      "(?s).*(?:private[_-]?key|session[_-]?key|api[_-]?key|socket[_-]?key|secret|nonce|cnonce|salt|token|challenge|(?:^|[._])iv(?:$|[._])).*"
    )
  }

  private def referencesSymbol(code: String, rawSymbol: String): Boolean = {
    val symbol = rawSymbol.split("\\.").lastOption.getOrElse(rawSymbol).replaceAll("[^A-Za-z0-9_$]", "")
    symbol.nonEmpty && ("(?s).*\\b" + java.util.regex.Pattern.quote(symbol) + "\\b.*").r.matches(code)
  }

  private def securityAssetTokens(value: String): Set[String] = {
    val normalized = value.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase
    val tokens     = normalized.split("[^a-z0-9]+").filter(_.nonEmpty).toSet
    val roles      = Set("key", "secret", "iv", "nonce", "cnonce", "salt", "token", "challenge")
    tokens.intersect(roles) ++ roles.filter(role => normalized.endsWith(role))
  }

  private val Whitespace = java.util.regex.Pattern.compile("\\s+")
  private def compactLower(value: String): String = Whitespace.matcher(value).replaceAll("").toLowerCase

  private def weakDh(cpg: Cpg): Vector[OperationObservation] = cpg.call.l.collect {
    case call if Set("DH_generate_parameters", "DH_generate_parameters_ex").contains(call.name) && {
          call.argument.argumentIndex(2).code.headOption.flatMap(integer).exists(bits => bits >= 0 && bits < 2048)
        } =>
      OperationObservation(WeakDhParameters, call, "ASYMMETRIC_ENCRYPTION", "KEY_GENERATION", "DH", detail = call.code,
        keySize = call.argument.argumentIndex(2).code.headOption.flatMap(integer).getOrElse(-1))
  }.toVector

  /** Framework entry points require an API owner as well as an algorithm operand: documentation, arbitrary
    * methods named `sign`/`init`, and unrelated constants must not become findings. */
  private def javaSecurityEntryPoints(cpg: Cpg, language: AdapterLanguage, sharedIndex: Option[JavaValueIndex]): Vector[OperationObservation] = {
    if (language != AdapterLanguage.Java) return Vector.empty
    val paths = sharedIndex.map(_.paths).getOrElse(new JavaConstantPathEvaluator(cpg))
    val calls = sharedIndex.map(_.calls).getOrElse(cpg.call.l.toVector)
    val factoriesByMethod = calls.filter(call => call.name == "getInstance" &&
      (call.methodFullName.startsWith("java.security.AlgorithmParameterGenerator.") ||
        call.methodFullName.startsWith("java.security.KeyPairGenerator."))).groupBy(_.method.id)

    // Resolve operands only after the API owner is known. Ordinary calls can have deep value graphs.
    val signatures = calls.filter { call =>
      (call.name == "getInstance" && call.methodFullName.startsWith("java.security.Signature.")) ||
        (call.name == "<init>" && call.methodFullName.startsWith("cn.hutool.crypto.asymmetric.Sign."))
    }.flatMap { call =>
      val operand = call.argument.l.find(_.argumentIndex == 1)
      val weak = operand.flatMap { expression =>
        // A literal prefix `SHA1with` is sufficient even when the asymmetric algorithm comes from runtime input.
        val code = expression.code.trim
        val direct = "(?i)^\"(MD5|SHA-?1)with[A-Za-z0-9]*\"(?:\\s*\\+.*)?$".r
          .findFirstMatchIn(code).map(_.group(1))
        val enumValue = Option.when(expression.ast.isFieldIdentifier.nonEmpty &&
          expression.ast.isIdentifier.typeFullName.l.exists(_.endsWith(".SignAlgorithm")))(code)
          .flatMap(value => "(?i).*\\.(MD5|SHA1)with(?:RSA|DSA|ECDSA)$".r.findFirstMatchIn(value).map(_.group(1)))
        direct.orElse(enumValue).orElse(
          paths.provenText(code, call.method, call).flatMap(value =>
            "(?i)^(MD5|SHA-?1)with.*$".r.findFirstMatchIn(value).map(_.group(1)))
        )
      }
      Option.when(weak.nonEmpty && !JavaUsageEvidence.historicalSignatureVerification(cpg, call))(OperationObservation(SignatureContext, call,
        "MAC_SIGNATURE", "SIGNATURE", weak.get.toUpperCase, detail = call.code)).toVector
    }
    val enumSignatureDefaults = calls.filter(call => call.name == "createSignature" &&
      call.methodFullName.startsWith("org.keycloak.saml.SignatureAlgorithm.createSignature:")).flatMap { call =>
      val fields = call.argument.l.find(_.argumentIndex == 0).toVector
        .flatMap(_.ast.isCall.nameExact("<operator>.fieldAccess").l.flatMap(javaFieldIdentity))
      val initializers = calls.filter(assignment => assignment.name == "<operator>.assignment" &&
        JavaFrontend.isInitializer(assignment.method.name) &&
        assignment.file.name.headOption == call.file.name.headOption &&
        assignment.argument.l.find(_.argumentIndex == 1).exists(lhs =>
          lhs.ast.isCall.nameExact("<operator>.fieldAccess").l.flatMap(javaFieldIdentity).exists(fields.contains)))
      initializers.flatMap { assignment =>
        val value = assignment.argument.l.find(_.argumentIndex == 2)
        val weak = value.flatMap(expression =>
          "(?i).*\\.(?:RSA|DSA|ECDSA)_(SHA1|MD5)$".r.findFirstMatchIn(expression.code.trim).map(_.group(1)))
        Option.when(weak.nonEmpty)(OperationObservation(SignatureContext, assignment, "MAC_SIGNATURE", "SIGNATURE",
          weak.get.toUpperCase, detail = s"${assignment.code}; used by ${call.code}")).toVector
      }
    }
    val dh = calls.filter { call =>
      (call.name == "init" && call.methodFullName.startsWith("java.security.AlgorithmParameterGenerator.")) ||
        (call.name == "initialize" && call.methodFullName.startsWith("java.security.KeyPairGenerator."))
    }.flatMap { call =>
      val receiver = call.argument.l.find(_.argumentIndex == 0).map(_.code.trim)
      val factories = factoriesByMethod.getOrElse(call.method.id, Vector.empty).filter { factory =>
        factory.lineNumber.getOrElse(Int.MaxValue) <= call.lineNumber.getOrElse(Int.MinValue) &&
          factory.inAst.isCall.nameExact("<operator>.assignment").l.exists(assignment =>
            assignment.argument.l.find(_.argumentIndex == 1).map(_.code.trim) == receiver)
      }
      val factory = factories.sortBy(_.lineNumber.getOrElse(0)).lastOption
      val isDh = factory.exists(_.argument.code.l.exists(code => Set("\"DH\"", "\"DiffieHellman\"").contains(code)))
      val bits = if (isDh) call.argument.l.find(_.argumentIndex == 1).flatMap(paths.provenInteger) else None
      Option.when(bits.exists(size => size > 0 && size < 2048))(
        OperationObservation(WeakDhParameters, call, "ASYMMETRIC_ENCRYPTION", "KEY_GENERATION", "DH",
          detail = s"${call.code}; resolved keySize=${bits.get}", keySize = bits.get)
      ).toVector
    }
    signatures ++ enumSignatureDefaults ++ dh
  }

  private def javaFieldIdentity(access: Call): Option[(String, String)] = for {
    owner <- access.argument.l.find(_.argumentIndex == 1).collect {
      case id: io.shiftleft.codepropertygraph.generated.nodes.Identifier => id.typeFullName
      case ref: io.shiftleft.codepropertygraph.generated.nodes.TypeRef => ref.typeFullName
    }
    field <- access.ast.isFieldIdentifier.canonicalName.headOption
  } yield owner -> field

  private def deprecatedMac(cpg: Cpg, reportLegacyHmacSha1: Boolean): Vector[OperationObservation] = {
    val protocol = new PythonProtocolEvidence(cpg)
    val javaUse = new JavaUsageEvidence(cpg)
    cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l.filter(operation =>
      operation.opType == "MAC_SIGNATURE" && operation.operation == "MAC").flatMap { operation =>
      val javaSha1 = operation.srcFile.endsWith(".java") && operation.algorithm.toUpperCase.replace("-", "").contains("SHA1") &&
        operation.realizingCall.l.exists(_.methodFullName.startsWith("javax.crypto.Mac."))
      val deprecatedSuite = if (javaSha1) javaUse.deprecatedKerberosChecksum(operation) else None
      if (deprecatedSuite.nonEmpty) Vector(OperationObservation(MacContext, operation,
        operation.opType, operation.operation, operation.algorithm,
        detail = s"RFC 8429: ${deprecatedSuite.get}; typed checksumType selector and resolved checksum caller to ${nodeCode(operation)}"))
      else if (reportLegacyHmacSha1 && javaSha1 && CryptoPolicy.isHmacSha1(operation.algorithm))
        Vector(OperationObservation(MacContext, operation, operation.opType, operation.operation,
          operation.algorithm, detail = CryptoPolicy.LegacyJavaHmacSha1Evidence))
      else if (
          (operation.isDeprecated || Set("MD5", "SHA1", "SHA-1").exists(operation.algorithm.toUpperCase.contains)) &&
          !javaSha1 && !protocol.isTotpMac(operation))
        Vector(OperationObservation(MacContext, operation, operation.opType, operation.operation, operation.algorithm))
      else Vector.empty
    }.toVector
  }

  /** C15: MAC/AEAD tags truncated below 96 bits (the GCM minimum in NIST SP 800-38D): `GCMParameterSpec(64, iv)`,
    * `Arrays.copyOf(mac.doFinal(..), 8)`, `mac.digest()[:10]`, pycryptodome `mac_len=8`, cryptography `min_tag_length=8`. */
  private def shortTags(cpg: Cpg, language: AdapterLanguage, sharedIndex: Option[JavaValueIndex]): Vector[OperationObservation] = {
    def tag(node: StoredNode, how: String, bits: Int, detail: String) =
      OperationObservation(AuthenticationTag, node, "MAC_SIGNATURE", how, s"TAG_${bits}_BITS", tagLength = bits, detail = detail)
    language match {
      case AdapterLanguage.Java =>
        val paths = sharedIndex.map(_.paths).getOrElse(new JavaConstantPathEvaluator(cpg))
        sharedIndex.map(_.calls).getOrElse(cpg.call.l.toVector).flatMap { call =>
          def arg(i: Int) = call.argument.l.find(_.argumentIndex == i)
          if (call.name == "<init>" && call.methodFullName.startsWith("javax.crypto.spec.GCMParameterSpec."))
            arg(1).flatMap(paths.provenInteger).filter(_ > 0).map(bits => tag(call, "GCM_TAG", bits, call.code)).toVector
          else if (call.name == "copyOf" && call.methodFullName.startsWith("java.util.Arrays.") &&
            arg(1).exists(_.ast.isCall.exists(_.methodFullName.startsWith("javax.crypto.Mac.doFinal"))))
            arg(2).flatMap(paths.provenInteger).map(_ * 8).filter(_ > 0).map(bits => tag(call, "TRUNCATED_MAC", bits, call.code)).toVector
          else Vector.empty
        }
      case AdapterLanguage.Python =>
        val sliced = """(?s)^([A-Za-z_][A-Za-z0-9_.]*|hmac\.new\(.*\)|hmac\.HMAC\(.*\))\.(hex)?digest\(\)\s*\[\s*:\s*(\d+)\s*\]$""".r
        val slices = cpg.call.l.toVector.flatMap { call =>
          sliced.findFirstMatchIn(call.code.trim).flatMap { m =>
            val receiver = m.group(1)
            val bits = m.group(3).toInt * (if (m.group(2) == null) 8 else 4)
            // The receiver must be an HMAC object built in the same function (or the constructor call itself).
            val hmac = receiver.startsWith("hmac.") || call.method.ast.isCall.nameExact("<operator>.assignment").exists(a =>
              a.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == receiver) &&
                a.argument.l.find(_.argumentIndex == 2).exists(v => "(?s)^(?:hmac\\.)?(?:new|HMAC)\\(.*".r.matches(v.code.trim)))
            Option.when(hmac && bits > 0)(tag(call, "TRUNCATED_MAC", bits, call.code))
          }
        }.distinctBy(m => (sourceFile(m.node), sourceLine(m.node)))
        val tagLengths = cpg.call.l.toVector.flatMap { call =>
          call.argument.l.filter(a => a.argumentName.exists(Set("mac_len", "min_tag_length"))).flatMap { a =>
            integer(a.code).map(n => n * 8).filter(_ > 0).map(bits => tag(call, "AEAD_TAG", bits, call.code))
          }
        }
        slices ++ tagLengths
      case _ => Vector.empty
    }
  }

  /** C11 for Python RSA encryption with PKCS#1 v1.5: cryptography `encrypt/decrypt(..., padding.PKCS1v15())` and
    * pycryptodome/pycrypto `PKCS1_v1_5.new(..)` imported from `Crypto.Cipher` (the Signature module is not affected). */
  private def pythonRsaPkcs1(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] =
    if (language != AdapterLanguage.Python) Vector.empty
    else cpg.call.l.toVector.flatMap { call =>
      val cryptography = Set("encrypt", "decrypt")(call.name) && call.argument.l.exists(a => a.argumentIndex > 0 &&
        a.code.replaceAll("\\s+", "").matches("(?:[A-Za-z_][A-Za-z0-9_]*\\.)*PKCS1v15\\(\\)"))
      val cipherModule = call.name == "new" && call.code.trim.matches("(?s)^(?:Crypto(?:dome)?\\.Cipher\\.)?PKCS1_v1_5\\.new\\(.*") &&
        call.file.ast.isCall.code.l.exists(code => code.matches("(?s).*(?:from|import)\\s+Crypto(?:dome)?\\.Cipher\\b.*PKCS1_v1_5.*") ||
          code.matches("(?s).*Crypto(?:dome)?\\.Cipher\\.PKCS1_v1_5.*"))
      Option.when(cryptography || cipherModule)(OperationObservation(RsaPadding, call, "ASYMMETRIC_ENCRYPTION", "ENCRYPT",
        "RSA", "", detail = call.code)).toVector
    }

  /** C2 for hard-coded passwords passed to authentication APIs (database, HTTP basic/SMTP/FTP login, SSH). Only a
    * direct non-empty literal (or a local variable assigned one) counts. */
  private def hardcodedCredentials(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] = {
    def literal(e: io.shiftleft.codepropertygraph.generated.nodes.Expression): Boolean = {
      val value = e.code.trim.stripSuffix(".toCharArray()")
      isConcreteSecret(value) || (e match {
        // `"secret".toCharArray()`: javasrc2cpg renders the receiver as `this`, so read it from the AST.
        case c: Call if Set("toCharArray", "getBytes")(c.name) =>
          c.argument.l.find(_.argumentIndex == 0).exists(r => r.isInstanceOf[io.shiftleft.codepropertygraph.generated.nodes.Literal] &&
            isConcreteSecret(r.code))
        case i: io.shiftleft.codepropertygraph.generated.nodes.Identifier =>
          val writes = i.method.ast.isCall.nameExact("<operator>.assignment").l
            .filter(a => a.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == i.name))
          writes.size == 1 && writes.head.argument.l.find(_.argumentIndex == 2).exists(v => isConcreteSecret(v.code.trim.stripSuffix(".toCharArray()")))
        case _ => false
      })
    }
    def credential(call: Call, password: Option[io.shiftleft.codepropertygraph.generated.nodes.Expression]) =
      password.filter(literal).map(_ => OperationObservation(CredentialUse, call, "AUTHENTICATION", "CREDENTIAL", detail = call.code))
    cpg.call.l.toVector.flatMap { call =>
      def arg(i: Int) = call.argument.l.find(_.argumentIndex == i)
      def named(n: String) = call.argument.l.find(_.argumentName.contains(n))
      val api = call.methodFullName
      language match {
        case AdapterLanguage.Java =>
          if (call.name == "getConnection" && api.startsWith("java.sql.DriverManager.") && call.argument.count(_.argumentIndex > 0) == 3)
            credential(call, arg(3)).toVector
          else if (call.name == "<init>" && api.startsWith("java.net.PasswordAuthentication.")) credential(call, arg(2)).toVector
          else Vector.empty
        case AdapterLanguage.Python =>
          val code = call.code.replaceAll("\\s+", "")
          if (Set("HTTPBasicAuth", "HTTPDigestAuth")(call.name)) credential(call, arg(2)).toVector
          else if (call.name == "login" && call.argument.count(_.argumentIndex > 0) >= 2 &&
            call.file.ast.isCall.code.l.exists(c => c.contains("smtplib") || c.contains("ftplib") || c.contains("imaplib")))
            credential(call, arg(2)).toVector
          else if (call.name == "connect" && (code.contains("password=") || code.contains("passwd=")) &&
            (code.startsWith("paramiko") || code.contains("pymysql") || code.contains("psycopg2") ||
              code.contains("mysql.connector") || code.contains("ssh.connect") || code.contains("client.connect")))
            credential(call, named("password").orElse(named("passwd"))).toVector
          else Vector.empty
        case _ => Vector.empty
      }
    }
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

  private def isEmptyString(value: String): Boolean = Set("\"\"", "''").contains(value.trim)
  private def isConcreteSecret(value: String): Boolean = {
    val code = value.trim
    (code.startsWith("\"") && code.endsWith("\"") && code.length > 2) ||
    (code.startsWith("'") && code.endsWith("'") && code.length > 2)
  }
  private def integer(value: String): Option[Int] = value.trim.replace("_", "").toIntOption
}
