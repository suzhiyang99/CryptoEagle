package io.shiftleft.codepropertygraph.generated.nodes

import io.shiftleft.codepropertygraph.generated.language.*
import scala.collection.immutable.{IndexedSeq, ArraySeq}
import scala.collection.mutable

/** Node base type for compiletime-only checks to improve type safety. EMT stands for: "erased marker trait", i.e. it is
  * erased at runtime
  */
trait CryptoOperationEMT
    extends AnyRef
    with HasAlgorithmEMT
    with HasIsDeprecatedEMT
    with HasIterationsEMT
    with HasIvSourceTypeEMT
    with HasKeyLengthEMT
    with HasKeySizeEMT
    with HasKeySourceEMT
    with HasKeySourceTypeEMT
    with HasLineNoEMT
    with HasModeEMT
    with HasNonceSourceTypeEMT
    with HasOperationEMT
    with HasOpTypeEMT
    with HasOutputLengthEMT
    with HasPaddingEMT
    with HasProtocolVersionEMT
    with HasProvidesIntegrityEMT
    with HasRequiresCsprngSourceEMT
    with HasRequiresUniqueNonceEMT
    with HasResolutionEMT
    with HasSaltSourceTypeEMT
    with HasSaltUsedEMT
    with HasSemanticDetailEMT
    with HasSemanticKindEMT
    with HasSourceEMT
    with HasSrcFileEMT
    with HasTagLengthEMT
    with HasTransportSchemeEMT
    with HasUsedForCryptoEMT
    with HasVerificationEnabledEMT
    with HasVerificationKindEMT
    with HasVerifiedEMT

trait CryptoOperationBase extends AbstractNode with StaticType[CryptoOperationEMT] {

  override def propertiesMap: java.util.Map[String, Any] = {
    import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*
    val res = new java.util.HashMap[String, Any]()
    if (("": String) != this.algorithm) res.put("ALGORITHM", this.algorithm)
    if ((false: Boolean) != this.isDeprecated) res.put("IS_DEPRECATED", this.isDeprecated)
    if ((-1: Int) != this.iterations) res.put("ITERATIONS", this.iterations)
    if (("UNKNOWN": String) != this.ivSourceType) res.put("IV_SOURCE_TYPE", this.ivSourceType)
    if ((-1: Int) != this.keyLength) res.put("KEY_LENGTH", this.keyLength)
    if ((-1: Int) != this.keySize) res.put("KEY_SIZE", this.keySize)
    if (("": String) != this.keySource) res.put("KEY_SOURCE", this.keySource)
    if (("UNKNOWN": String) != this.keySourceType) res.put("KEY_SOURCE_TYPE", this.keySourceType)
    if ((-1: Int) != this.lineNo) res.put("LINE_NO", this.lineNo)
    if (("": String) != this.mode) res.put("MODE", this.mode)
    if (("UNKNOWN": String) != this.nonceSourceType) res.put("NONCE_SOURCE_TYPE", this.nonceSourceType)
    if (("": String) != this.opType) res.put("OP_TYPE", this.opType)
    if (("": String) != this.operation) res.put("OPERATION", this.operation)
    if ((-1: Int) != this.outputLength) res.put("OUTPUT_LENGTH", this.outputLength)
    if (("": String) != this.padding) res.put("PADDING", this.padding)
    if (("": String) != this.protocolVersion) res.put("PROTOCOL_VERSION", this.protocolVersion)
    if ((false: Boolean) != this.providesIntegrity) res.put("PROVIDES_INTEGRITY", this.providesIntegrity)
    if ((false: Boolean) != this.requiresCsprngSource) res.put("REQUIRES_CSPRNG_SOURCE", this.requiresCsprngSource)
    if ((false: Boolean) != this.requiresUniqueNonce) res.put("REQUIRES_UNIQUE_NONCE", this.requiresUniqueNonce)
    if (("UNKNOWN": String) != this.resolution) res.put("RESOLUTION", this.resolution)
    if (("UNKNOWN": String) != this.saltSourceType) res.put("SALT_SOURCE_TYPE", this.saltSourceType)
    this.saltUsed.foreach { p => res.put("SALT_USED", p) }
    if (("": String) != this.semanticDetail) res.put("SEMANTIC_DETAIL", this.semanticDetail)
    if (("": String) != this.semanticKind) res.put("SEMANTIC_KIND", this.semanticKind)
    if (("UNKNOWN": String) != this.source) res.put("SOURCE", this.source)
    if (("": String) != this.srcFile) res.put("SRC_FILE", this.srcFile)
    if ((-1: Int) != this.tagLength) res.put("TAG_LENGTH", this.tagLength)
    if (("": String) != this.transportScheme) res.put("TRANSPORT_SCHEME", this.transportScheme)
    this.usedForCrypto.foreach { p => res.put("USED_FOR_CRYPTO", p) }
    this.verificationEnabled.foreach { p => res.put("VERIFICATION_ENABLED", p) }
    if (("": String) != this.verificationKind) res.put("VERIFICATION_KIND", this.verificationKind)
    this.verified.foreach { p => res.put("VERIFIED", p) }
    res
  }
}

object CryptoOperation {
  val Label = "CRYPTO_OPERATION"
  object PropertyNames {

    val Algorithm = "ALGORITHM"

    val IsDeprecated = "IS_DEPRECATED"

    val Iterations = "ITERATIONS"

    val IvSourceType = "IV_SOURCE_TYPE"

    val KeyLength = "KEY_LENGTH"

    val KeySize = "KEY_SIZE"

    val KeySource = "KEY_SOURCE"

    val KeySourceType = "KEY_SOURCE_TYPE"

    val LineNo = "LINE_NO"

    val Mode = "MODE"

    val NonceSourceType = "NONCE_SOURCE_TYPE"

    val OpType = "OP_TYPE"

    val Operation = "OPERATION"

    val OutputLength = "OUTPUT_LENGTH"

    val Padding = "PADDING"

    val ProtocolVersion = "PROTOCOL_VERSION"

    val ProvidesIntegrity = "PROVIDES_INTEGRITY"

    val RequiresCsprngSource = "REQUIRES_CSPRNG_SOURCE"

    val RequiresUniqueNonce = "REQUIRES_UNIQUE_NONCE"

    val Resolution = "RESOLUTION"

    val SaltSourceType = "SALT_SOURCE_TYPE"

    val SaltUsed = "SALT_USED"

    val SemanticDetail = "SEMANTIC_DETAIL"

    val SemanticKind = "SEMANTIC_KIND"

    val Source = "SOURCE"

    val SrcFile = "SRC_FILE"

    val TagLength = "TAG_LENGTH"

    val TransportScheme = "TRANSPORT_SCHEME"

    val UsedForCrypto = "USED_FOR_CRYPTO"

    val VerificationEnabled = "VERIFICATION_ENABLED"

    val VerificationKind = "VERIFICATION_KIND"

    val Verified = "VERIFIED"
  }
  object Properties {
    val Algorithm     = flatgraph.SinglePropertyKey[String](kind = 0, name = "ALGORITHM", default = "")
    val IsDeprecated  = flatgraph.SinglePropertyKey[Boolean](kind = 30, name = "IS_DEPRECATED", default = false)
    val Iterations    = flatgraph.SinglePropertyKey[Int](kind = 35, name = "ITERATIONS", default = -1: Int)
    val IvSourceType  = flatgraph.SinglePropertyKey[String](kind = 36, name = "IV_SOURCE_TYPE", default = "UNKNOWN")
    val KeyLength     = flatgraph.SinglePropertyKey[Int](kind = 38, name = "KEY_LENGTH", default = -1: Int)
    val KeySize       = flatgraph.SinglePropertyKey[Int](kind = 39, name = "KEY_SIZE", default = -1: Int)
    val KeySource     = flatgraph.SinglePropertyKey[String](kind = 40, name = "KEY_SOURCE", default = "")
    val KeySourceType = flatgraph.SinglePropertyKey[String](kind = 41, name = "KEY_SOURCE_TYPE", default = "UNKNOWN")
    val LineNo        = flatgraph.SinglePropertyKey[Int](kind = 43, name = "LINE_NO", default = -1: Int)
    val Mode          = flatgraph.SinglePropertyKey[String](kind = 48, name = "MODE", default = "")
    val NonceSourceType =
      flatgraph.SinglePropertyKey[String](kind = 52, name = "NONCE_SOURCE_TYPE", default = "UNKNOWN")
    val OpType          = flatgraph.SinglePropertyKey[String](kind = 56, name = "OP_TYPE", default = "")
    val Operation       = flatgraph.SinglePropertyKey[String](kind = 55, name = "OPERATION", default = "")
    val OutputLength    = flatgraph.SinglePropertyKey[Int](kind = 58, name = "OUTPUT_LENGTH", default = -1: Int)
    val Padding         = flatgraph.SinglePropertyKey[String](kind = 61, name = "PADDING", default = "")
    val ProtocolVersion = flatgraph.SinglePropertyKey[String](kind = 64, name = "PROTOCOL_VERSION", default = "")
    val ProvidesIntegrity =
      flatgraph.SinglePropertyKey[Boolean](kind = 65, name = "PROVIDES_INTEGRITY", default = false)
    val RequiresCsprngSource =
      flatgraph.SinglePropertyKey[Boolean](kind = 66, name = "REQUIRES_CSPRNG_SOURCE", default = false)
    val RequiresUniqueNonce =
      flatgraph.SinglePropertyKey[Boolean](kind = 67, name = "REQUIRES_UNIQUE_NONCE", default = false)
    val Resolution      = flatgraph.SinglePropertyKey[String](kind = 68, name = "RESOLUTION", default = "UNKNOWN")
    val SaltSourceType  = flatgraph.SinglePropertyKey[String](kind = 70, name = "SALT_SOURCE_TYPE", default = "UNKNOWN")
    val SaltUsed        = flatgraph.OptionalPropertyKey[Boolean](kind = 71, name = "SALT_USED")
    val SemanticDetail  = flatgraph.SinglePropertyKey[String](kind = 72, name = "SEMANTIC_DETAIL", default = "")
    val SemanticKind    = flatgraph.SinglePropertyKey[String](kind = 73, name = "SEMANTIC_KIND", default = "")
    val Source          = flatgraph.SinglePropertyKey[String](kind = 75, name = "SOURCE", default = "UNKNOWN")
    val SrcFile         = flatgraph.SinglePropertyKey[String](kind = 76, name = "SRC_FILE", default = "")
    val TagLength       = flatgraph.SinglePropertyKey[Int](kind = 78, name = "TAG_LENGTH", default = -1: Int)
    val TransportScheme = flatgraph.SinglePropertyKey[String](kind = 79, name = "TRANSPORT_SCHEME", default = "")
    val UsedForCrypto   = flatgraph.OptionalPropertyKey[Boolean](kind = 82, name = "USED_FOR_CRYPTO")
    val VerificationEnabled = flatgraph.OptionalPropertyKey[Boolean](kind = 84, name = "VERIFICATION_ENABLED")
    val VerificationKind    = flatgraph.SinglePropertyKey[String](kind = 85, name = "VERIFICATION_KIND", default = "")
    val Verified            = flatgraph.OptionalPropertyKey[Boolean](kind = 86, name = "VERIFIED")
  }
  object PropertyDefaults {
    val Algorithm            = ""
    val IsDeprecated         = false
    val Iterations           = -1: Int
    val IvSourceType         = "UNKNOWN"
    val KeyLength            = -1: Int
    val KeySize              = -1: Int
    val KeySource            = ""
    val KeySourceType        = "UNKNOWN"
    val LineNo               = -1: Int
    val Mode                 = ""
    val NonceSourceType      = "UNKNOWN"
    val OpType               = ""
    val Operation            = ""
    val OutputLength         = -1: Int
    val Padding              = ""
    val ProtocolVersion      = ""
    val ProvidesIntegrity    = false
    val RequiresCsprngSource = false
    val RequiresUniqueNonce  = false
    val Resolution           = "UNKNOWN"
    val SaltSourceType       = "UNKNOWN"
    val SemanticDetail       = ""
    val SemanticKind         = ""
    val Source               = "UNKNOWN"
    val SrcFile              = ""
    val TagLength            = -1: Int
    val TransportScheme      = ""
    val VerificationKind     = ""
  }
}

class CryptoOperation(graph_4762: flatgraph.Graph, seq_4762: Int)
    extends StoredNode(graph_4762, 12.toShort, seq_4762)
    with CryptoOperationBase
    with StaticType[CryptoOperationEMT] {

  override def productElementName(n: Int): String =
    n match {
      case 0  => "algorithm"
      case 1  => "isDeprecated"
      case 2  => "iterations"
      case 3  => "ivSourceType"
      case 4  => "keyLength"
      case 5  => "keySize"
      case 6  => "keySource"
      case 7  => "keySourceType"
      case 8  => "lineNo"
      case 9  => "mode"
      case 10 => "nonceSourceType"
      case 11 => "opType"
      case 12 => "operation"
      case 13 => "outputLength"
      case 14 => "padding"
      case 15 => "protocolVersion"
      case 16 => "providesIntegrity"
      case 17 => "requiresCsprngSource"
      case 18 => "requiresUniqueNonce"
      case 19 => "resolution"
      case 20 => "saltSourceType"
      case 21 => "saltUsed"
      case 22 => "semanticDetail"
      case 23 => "semanticKind"
      case 24 => "source"
      case 25 => "srcFile"
      case 26 => "tagLength"
      case 27 => "transportScheme"
      case 28 => "usedForCrypto"
      case 29 => "verificationEnabled"
      case 30 => "verificationKind"
      case 31 => "verified"
      case _  => ""
    }

  override def productElement(n: Int): Any =
    n match {
      case 0  => this.algorithm
      case 1  => this.isDeprecated
      case 2  => this.iterations
      case 3  => this.ivSourceType
      case 4  => this.keyLength
      case 5  => this.keySize
      case 6  => this.keySource
      case 7  => this.keySourceType
      case 8  => this.lineNo
      case 9  => this.mode
      case 10 => this.nonceSourceType
      case 11 => this.opType
      case 12 => this.operation
      case 13 => this.outputLength
      case 14 => this.padding
      case 15 => this.protocolVersion
      case 16 => this.providesIntegrity
      case 17 => this.requiresCsprngSource
      case 18 => this.requiresUniqueNonce
      case 19 => this.resolution
      case 20 => this.saltSourceType
      case 21 => this.saltUsed
      case 22 => this.semanticDetail
      case 23 => this.semanticKind
      case 24 => this.source
      case 25 => this.srcFile
      case 26 => this.tagLength
      case 27 => this.transportScheme
      case 28 => this.usedForCrypto
      case 29 => this.verificationEnabled
      case 30 => this.verificationKind
      case 31 => this.verified
      case _  => null
    }

  override def productPrefix = "CryptoOperation"
  override def productArity  = 32

  override def canEqual(that: Any): Boolean = that != null && that.isInstanceOf[CryptoOperation]
}

object NewCryptoOperation {
  def apply(): NewCryptoOperation = new NewCryptoOperation
  private val outNeighbors: Map[String, Set[String]] = Map(
    "HAS_SECURITY_EVIDENCE" -> Set("AnyNode"),
    "USES_IV_FROM"          -> Set("AnyNode"),
    "USES_KEY_FROM"         -> Set("AnyNode"),
    "USES_NONCE_FROM"       -> Set("AnyNode"),
    "USES_SALT_FROM"        -> Set("AnyNode")
  )
  private val inNeighbors: Map[String, Set[String]] = Map("REALIZES_CRYPTO" -> Set("CALL"))

  object InsertionHelpers {
    object NewNodeInserter_CryptoOperation_algorithm extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.algorithm
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_isDeprecated extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.isDeprecated
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_iterations extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Int]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.iterations
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_ivSourceType extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.ivSourceType
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_keyLength extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Int]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.keyLength
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_keySize extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Int]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.keySize
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_keySource extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.keySource
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_keySourceType extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.keySourceType
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_lineNo extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Int]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.lineNo
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_mode extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.mode
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_nonceSourceType extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.nonceSourceType
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_opType extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.opType
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_operation extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.operation
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_outputLength extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Int]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.outputLength
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_padding extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.padding
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_protocolVersion extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.protocolVersion
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_providesIntegrity extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.providesIntegrity
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_requiresCsprngSource extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.requiresCsprngSource
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_requiresUniqueNonce extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.requiresUniqueNonce
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_resolution extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.resolution
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_saltSourceType extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.saltSourceType
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_saltUsed extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              generated.saltUsed match {
                case Some(item) =>
                  dstCast(offset) = item
                  offset += 1
                case _ =>
              }
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_semanticDetail extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.semanticDetail
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_semanticKind extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.semanticKind
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_source extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.source
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_srcFile extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.srcFile
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_tagLength extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Int]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.tagLength
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_transportScheme extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.transportScheme
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_usedForCrypto extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              generated.usedForCrypto match {
                case Some(item) =>
                  dstCast(offset) = item
                  offset += 1
                case _ =>
              }
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_verificationEnabled extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              generated.verificationEnabled match {
                case Some(item) =>
                  dstCast(offset) = item
                  offset += 1
                case _ =>
              }
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_verificationKind extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[String]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              dstCast(offset) = generated.verificationKind
              offset += 1
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
    object NewNodeInserter_CryptoOperation_verified extends flatgraph.NewNodePropertyInsertionHelper {
      override def insertNewNodeProperties(
        newNodes: mutable.ArrayBuffer[flatgraph.DNode],
        dst: AnyRef,
        offsets: Array[Int]
      ): Unit = {
        if (newNodes.isEmpty) return
        val dstCast = dst.asInstanceOf[Array[Boolean]]
        val seq     = newNodes.head.storedRef.get.seq()
        var offset  = offsets(seq)
        var idx     = 0
        while (idx < newNodes.length) {
          val nn = newNodes(idx)
          nn match {
            case generated: NewCryptoOperation =>
              generated.verified match {
                case Some(item) =>
                  dstCast(offset) = item
                  offset += 1
                case _ =>
              }
            case _ =>
          }
          assert(seq + idx == nn.storedRef.get.seq(), "internal consistency check")
          idx += 1
          offsets(idx + seq) = offset
        }
      }
    }
  }
}

class NewCryptoOperation extends NewNode(12.toShort) with CryptoOperationBase {
  override type StoredNodeType = CryptoOperation
  override def label: String = "CRYPTO_OPERATION"

  override def isValidOutNeighbor(edgeLabel: String, n: NewNode): Boolean = {
    NewCryptoOperation.outNeighbors.getOrElse(edgeLabel, Set.empty).contains(n.label)
  }
  override def isValidInNeighbor(edgeLabel: String, n: NewNode): Boolean = {
    NewCryptoOperation.inNeighbors.getOrElse(edgeLabel, Set.empty).contains(n.label)
  }

  var algorithm: String                                      = "": String
  var isDeprecated: Boolean                                  = false: Boolean
  var iterations: Int                                        = -1: Int
  var ivSourceType: String                                   = "UNKNOWN": String
  var keyLength: Int                                         = -1: Int
  var keySize: Int                                           = -1: Int
  var keySource: String                                      = "": String
  var keySourceType: String                                  = "UNKNOWN": String
  var lineNo: Int                                            = -1: Int
  var mode: String                                           = "": String
  var nonceSourceType: String                                = "UNKNOWN": String
  var opType: String                                         = "": String
  var operation: String                                      = "": String
  var outputLength: Int                                      = -1: Int
  var padding: String                                        = "": String
  var protocolVersion: String                                = "": String
  var providesIntegrity: Boolean                             = false: Boolean
  var requiresCsprngSource: Boolean                          = false: Boolean
  var requiresUniqueNonce: Boolean                           = false: Boolean
  var resolution: String                                     = "UNKNOWN": String
  var saltSourceType: String                                 = "UNKNOWN": String
  var saltUsed: Option[Boolean]                              = None
  var semanticDetail: String                                 = "": String
  var semanticKind: String                                   = "": String
  var source: String                                         = "UNKNOWN": String
  var srcFile: String                                        = "": String
  var tagLength: Int                                         = -1: Int
  var transportScheme: String                                = "": String
  var usedForCrypto: Option[Boolean]                         = None
  var verificationEnabled: Option[Boolean]                   = None
  var verificationKind: String                               = "": String
  var verified: Option[Boolean]                              = None
  def algorithm(value: String): this.type                    = { this.algorithm = value; this }
  def isDeprecated(value: Boolean): this.type                = { this.isDeprecated = value; this }
  def iterations(value: Int): this.type                      = { this.iterations = value; this }
  def ivSourceType(value: String): this.type                 = { this.ivSourceType = value; this }
  def keyLength(value: Int): this.type                       = { this.keyLength = value; this }
  def keySize(value: Int): this.type                         = { this.keySize = value; this }
  def keySource(value: String): this.type                    = { this.keySource = value; this }
  def keySourceType(value: String): this.type                = { this.keySourceType = value; this }
  def lineNo(value: Int): this.type                          = { this.lineNo = value; this }
  def mode(value: String): this.type                         = { this.mode = value; this }
  def nonceSourceType(value: String): this.type              = { this.nonceSourceType = value; this }
  def opType(value: String): this.type                       = { this.opType = value; this }
  def operation(value: String): this.type                    = { this.operation = value; this }
  def outputLength(value: Int): this.type                    = { this.outputLength = value; this }
  def padding(value: String): this.type                      = { this.padding = value; this }
  def protocolVersion(value: String): this.type              = { this.protocolVersion = value; this }
  def providesIntegrity(value: Boolean): this.type           = { this.providesIntegrity = value; this }
  def requiresCsprngSource(value: Boolean): this.type        = { this.requiresCsprngSource = value; this }
  def requiresUniqueNonce(value: Boolean): this.type         = { this.requiresUniqueNonce = value; this }
  def resolution(value: String): this.type                   = { this.resolution = value; this }
  def saltSourceType(value: String): this.type               = { this.saltSourceType = value; this }
  def saltUsed(value: Boolean): this.type                    = { this.saltUsed = Option(value); this }
  def saltUsed(value: Option[Boolean]): this.type            = { this.saltUsed = value; this }
  def semanticDetail(value: String): this.type               = { this.semanticDetail = value; this }
  def semanticKind(value: String): this.type                 = { this.semanticKind = value; this }
  def source(value: String): this.type                       = { this.source = value; this }
  def srcFile(value: String): this.type                      = { this.srcFile = value; this }
  def tagLength(value: Int): this.type                       = { this.tagLength = value; this }
  def transportScheme(value: String): this.type              = { this.transportScheme = value; this }
  def usedForCrypto(value: Boolean): this.type               = { this.usedForCrypto = Option(value); this }
  def usedForCrypto(value: Option[Boolean]): this.type       = { this.usedForCrypto = value; this }
  def verificationEnabled(value: Boolean): this.type         = { this.verificationEnabled = Option(value); this }
  def verificationEnabled(value: Option[Boolean]): this.type = { this.verificationEnabled = value; this }
  def verificationKind(value: String): this.type             = { this.verificationKind = value; this }
  def verified(value: Boolean): this.type                    = { this.verified = Option(value); this }
  def verified(value: Option[Boolean]): this.type            = { this.verified = value; this }
  override def countAndVisitProperties(interface: flatgraph.BatchedUpdateInterface): Unit = {
    interface.countProperty(this, 0, 1)
    interface.countProperty(this, 30, 1)
    interface.countProperty(this, 35, 1)
    interface.countProperty(this, 36, 1)
    interface.countProperty(this, 38, 1)
    interface.countProperty(this, 39, 1)
    interface.countProperty(this, 40, 1)
    interface.countProperty(this, 41, 1)
    interface.countProperty(this, 43, 1)
    interface.countProperty(this, 48, 1)
    interface.countProperty(this, 52, 1)
    interface.countProperty(this, 56, 1)
    interface.countProperty(this, 55, 1)
    interface.countProperty(this, 58, 1)
    interface.countProperty(this, 61, 1)
    interface.countProperty(this, 64, 1)
    interface.countProperty(this, 65, 1)
    interface.countProperty(this, 66, 1)
    interface.countProperty(this, 67, 1)
    interface.countProperty(this, 68, 1)
    interface.countProperty(this, 70, 1)
    interface.countProperty(this, 71, saltUsed.size)
    interface.countProperty(this, 72, 1)
    interface.countProperty(this, 73, 1)
    interface.countProperty(this, 75, 1)
    interface.countProperty(this, 76, 1)
    interface.countProperty(this, 78, 1)
    interface.countProperty(this, 79, 1)
    interface.countProperty(this, 82, usedForCrypto.size)
    interface.countProperty(this, 84, verificationEnabled.size)
    interface.countProperty(this, 85, 1)
    interface.countProperty(this, 86, verified.size)
  }

  override def copy: this.type = {
    val newInstance = new NewCryptoOperation
    newInstance.algorithm = this.algorithm
    newInstance.isDeprecated = this.isDeprecated
    newInstance.iterations = this.iterations
    newInstance.ivSourceType = this.ivSourceType
    newInstance.keyLength = this.keyLength
    newInstance.keySize = this.keySize
    newInstance.keySource = this.keySource
    newInstance.keySourceType = this.keySourceType
    newInstance.lineNo = this.lineNo
    newInstance.mode = this.mode
    newInstance.nonceSourceType = this.nonceSourceType
    newInstance.opType = this.opType
    newInstance.operation = this.operation
    newInstance.outputLength = this.outputLength
    newInstance.padding = this.padding
    newInstance.protocolVersion = this.protocolVersion
    newInstance.providesIntegrity = this.providesIntegrity
    newInstance.requiresCsprngSource = this.requiresCsprngSource
    newInstance.requiresUniqueNonce = this.requiresUniqueNonce
    newInstance.resolution = this.resolution
    newInstance.saltSourceType = this.saltSourceType
    newInstance.saltUsed = this.saltUsed
    newInstance.semanticDetail = this.semanticDetail
    newInstance.semanticKind = this.semanticKind
    newInstance.source = this.source
    newInstance.srcFile = this.srcFile
    newInstance.tagLength = this.tagLength
    newInstance.transportScheme = this.transportScheme
    newInstance.usedForCrypto = this.usedForCrypto
    newInstance.verificationEnabled = this.verificationEnabled
    newInstance.verificationKind = this.verificationKind
    newInstance.verified = this.verified
    newInstance.asInstanceOf[this.type]
  }

  override def productElementName(n: Int): String =
    n match {
      case 0  => "algorithm"
      case 1  => "isDeprecated"
      case 2  => "iterations"
      case 3  => "ivSourceType"
      case 4  => "keyLength"
      case 5  => "keySize"
      case 6  => "keySource"
      case 7  => "keySourceType"
      case 8  => "lineNo"
      case 9  => "mode"
      case 10 => "nonceSourceType"
      case 11 => "opType"
      case 12 => "operation"
      case 13 => "outputLength"
      case 14 => "padding"
      case 15 => "protocolVersion"
      case 16 => "providesIntegrity"
      case 17 => "requiresCsprngSource"
      case 18 => "requiresUniqueNonce"
      case 19 => "resolution"
      case 20 => "saltSourceType"
      case 21 => "saltUsed"
      case 22 => "semanticDetail"
      case 23 => "semanticKind"
      case 24 => "source"
      case 25 => "srcFile"
      case 26 => "tagLength"
      case 27 => "transportScheme"
      case 28 => "usedForCrypto"
      case 29 => "verificationEnabled"
      case 30 => "verificationKind"
      case 31 => "verified"
      case _  => ""
    }

  override def productElement(n: Int): Any =
    n match {
      case 0  => this.algorithm
      case 1  => this.isDeprecated
      case 2  => this.iterations
      case 3  => this.ivSourceType
      case 4  => this.keyLength
      case 5  => this.keySize
      case 6  => this.keySource
      case 7  => this.keySourceType
      case 8  => this.lineNo
      case 9  => this.mode
      case 10 => this.nonceSourceType
      case 11 => this.opType
      case 12 => this.operation
      case 13 => this.outputLength
      case 14 => this.padding
      case 15 => this.protocolVersion
      case 16 => this.providesIntegrity
      case 17 => this.requiresCsprngSource
      case 18 => this.requiresUniqueNonce
      case 19 => this.resolution
      case 20 => this.saltSourceType
      case 21 => this.saltUsed
      case 22 => this.semanticDetail
      case 23 => this.semanticKind
      case 24 => this.source
      case 25 => this.srcFile
      case 26 => this.tagLength
      case 27 => this.transportScheme
      case 28 => this.usedForCrypto
      case 29 => this.verificationEnabled
      case 30 => this.verificationKind
      case 31 => this.verified
      case _  => null
    }

  override def productPrefix                = "NewCryptoOperation"
  override def productArity                 = 32
  override def canEqual(that: Any): Boolean = that != null && that.isInstanceOf[NewCryptoOperation]
}
