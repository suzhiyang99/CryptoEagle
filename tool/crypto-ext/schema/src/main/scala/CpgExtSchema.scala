import io.shiftleft.codepropertygraph.schema._
import flatgraph.schema.SchemaBuilder
import flatgraph.schema.Property.ValueType

class CpgExtSchema(builder: SchemaBuilder, cpgSchema: CpgSchema) {
  // Core ontology attributes. Existing properties keep their defaults so that the
  // legacy passes remain source-compatible while they are migrated phase by phase.
  val opType       = builder.addProperty(name = "OP_TYPE", valueType = ValueType.String).mandatory("")
  val operation    = builder.addProperty(name = "OPERATION", valueType = ValueType.String).mandatory("")
  val algorithm    = builder.addProperty(name = "ALGORITHM", valueType = ValueType.String).mandatory("")
  val mode         = builder.addProperty(name = "MODE", valueType = ValueType.String).mandatory("")
  val padding      = builder.addProperty(name = "PADDING", valueType = ValueType.String).mandatory("")
  val keyLength    = builder.addProperty(name = "KEY_LENGTH", valueType = ValueType.Int).mandatory(-1)
  val keySize      = builder.addProperty(name = "KEY_SIZE", valueType = ValueType.Int).mandatory(-1)
  val outputLength = builder.addProperty(name = "OUTPUT_LENGTH", valueType = ValueType.Int).mandatory(-1)
  val iterations   = builder.addProperty(name = "ITERATIONS", valueType = ValueType.Int).mandatory(-1)
  val source       = builder.addProperty(name = "SOURCE", valueType = ValueType.String).mandatory("UNKNOWN")

  // Optional booleans preserve the distinction between false and not resolved.
  val saltUsed     = builder.addProperty(name = "SALT_USED", valueType = ValueType.Boolean)
  val verified     = builder.addProperty(name = "VERIFIED", valueType = ValueType.Boolean)
  val usedForCrypto= builder.addProperty(name = "USED_FOR_CRYPTO", valueType = ValueType.Boolean)

  // Security properties derived from the ontology.
  val isDeprecated          = builder.addProperty(name = "IS_DEPRECATED", valueType = ValueType.Boolean).mandatory(false)
  val providesIntegrity     = builder.addProperty(name = "PROVIDES_INTEGRITY", valueType = ValueType.Boolean).mandatory(false)
  val requiresUniqueNonce   = builder.addProperty(name = "REQUIRES_UNIQUE_NONCE", valueType = ValueType.Boolean).mandatory(false)
  val requiresCsprngSource  = builder.addProperty(name = "REQUIRES_CSPRNG_SOURCE", valueType = ValueType.Boolean).mandatory(false)

  // Legacy KEY_SOURCE is retained until all callers use the parameter-specific fields.
  val keySource       = builder.addProperty(name = "KEY_SOURCE", valueType = ValueType.String).mandatory("")
  val keySourceType   = builder.addProperty(name = "KEY_SOURCE_TYPE", valueType = ValueType.String).mandatory("UNKNOWN")
  val ivSourceType    = builder.addProperty(name = "IV_SOURCE_TYPE", valueType = ValueType.String).mandatory("UNKNOWN")
  val saltSourceType  = builder.addProperty(name = "SALT_SOURCE_TYPE", valueType = ValueType.String).mandatory("UNKNOWN")
  val nonceSourceType = builder.addProperty(name = "NONCE_SOURCE_TYPE", valueType = ValueType.String).mandatory("UNKNOWN")
  val resolution      = builder.addProperty(name = "RESOLUTION", valueType = ValueType.String).mandatory("UNKNOWN")

  val lineNo  = builder.addProperty(name = "LINE_NO", valueType = ValueType.Int).mandatory(-1)
  val srcFile = builder.addProperty(name = "SRC_FILE", valueType = ValueType.String).mandatory("")

  val semanticKind = builder.addProperty("SEMANTIC_KIND", ValueType.String).mandatory("")
  val semanticDetail = builder.addProperty("SEMANTIC_DETAIL", ValueType.String).mandatory("")
  val verificationKind = builder.addProperty("VERIFICATION_KIND", ValueType.String).mandatory("")
  val verificationEnabled = builder.addProperty("VERIFICATION_ENABLED", ValueType.Boolean)
  val transportScheme = builder.addProperty("TRANSPORT_SCHEME", ValueType.String).mandatory("")
  val protocolVersion = builder.addProperty("PROTOCOL_VERSION", ValueType.String).mandatory("")
  val tagLength = builder.addProperty("TAG_LENGTH", ValueType.Int).mandatory(-1)

  val cryptoOperation = builder.addNodeType("CRYPTO_OPERATION")
    .addProperty(opType).addProperty(operation).addProperty(algorithm).addProperty(mode).addProperty(padding)
    .addProperty(keyLength).addProperty(keySize).addProperty(outputLength).addProperty(iterations)
    .addProperty(source)
    .addProperty(saltUsed).addProperty(verified).addProperty(usedForCrypto)
    .addProperty(isDeprecated).addProperty(providesIntegrity)
    .addProperty(requiresUniqueNonce).addProperty(requiresCsprngSource)
    .addProperty(keySource).addProperty(keySourceType).addProperty(ivSourceType)
    .addProperty(saltSourceType).addProperty(nonceSourceType).addProperty(resolution)
    .addProperty(lineNo).addProperty(srcFile)
    .addProperty(semanticKind).addProperty(semanticDetail)
    .addProperty(verificationKind).addProperty(verificationEnabled)
    .addProperty(transportScheme).addProperty(protocolVersion).addProperty(tagLength)

  val hasSecurityEvidence = builder.addEdgeType("HAS_SECURITY_EVIDENCE")
  cryptoOperation.addOutEdge(hasSecurityEvidence, builder.anyNode,
    stepNameOut = "securityEvidenceNode", stepNameOutDoc = "Source node supporting this operation context",
    stepNameIn = "supportedSecurityOperation", stepNameInDoc = "Operation context supported by this source node")

  val realizesCrypto = builder.addEdgeType("REALIZES_CRYPTO")
  val usesKeyFrom     = builder.addEdgeType("USES_KEY_FROM")
  val usesIvFrom      = builder.addEdgeType("USES_IV_FROM")
  val usesSaltFrom    = builder.addEdgeType("USES_SALT_FROM")
  val usesNonceFrom   = builder.addEdgeType("USES_NONCE_FROM")

  cpgSchema.ast.callNode.addOutEdge(
    realizesCrypto,
    cryptoOperation,
    stepNameOut = "realizedCryptoOperation",
    stepNameOutDoc = "The language-independent cryptographic operation realized by this call",
    stepNameIn = "realizingCall",
    stepNameInDoc = "A source call that realizes this cryptographic operation"
  )

  cryptoOperation
    .addOutEdge(
      usesKeyFrom,
      builder.anyNode,
      stepNameOut = "keySourceNode",
      stepNameOutDoc = "The terminal CPG source of key material",
      stepNameIn = "keyUsingCryptoOperation",
      stepNameInDoc = "A cryptographic operation that uses this node as key material"
    )
    .addOutEdge(
      usesIvFrom,
      builder.anyNode,
      stepNameOut = "ivSourceNode",
      stepNameOutDoc = "The terminal CPG source of initialization-vector material",
      stepNameIn = "ivUsingCryptoOperation",
      stepNameInDoc = "A cryptographic operation that uses this node as initialization-vector material"
    )
    .addOutEdge(
      usesSaltFrom,
      builder.anyNode,
      stepNameOut = "saltSourceNode",
      stepNameOutDoc = "The terminal CPG source of salt material",
      stepNameIn = "saltUsingCryptoOperation",
      stepNameInDoc = "A cryptographic operation that uses this node as salt material"
    )
    .addOutEdge(
      usesNonceFrom,
      builder.anyNode,
      stepNameOut = "nonceSourceNode",
      stepNameOutDoc = "The terminal CPG source of nonce material",
      stepNameIn = "nonceUsingCryptoOperation",
      stepNameInDoc = "A cryptographic operation that uses this node as nonce material"
    )
}

object CpgExtSchema {
  val builder   = new SchemaBuilder(domainShortName = "Cpg", basePackage = "io.shiftleft.codepropertygraph.generated")
  val cpgSchema = new CpgSchema(builder)
  val cpgExtSchema = new CpgExtSchema(builder, cpgSchema)
  val instance     = builder.build
}
