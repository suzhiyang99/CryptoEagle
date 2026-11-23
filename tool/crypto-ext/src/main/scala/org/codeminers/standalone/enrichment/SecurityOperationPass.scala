package org.codeminers.standalone.enrichment

import flatgraph.DiffGraphBuilder
import io.shiftleft.codepropertygraph.generated.{Cpg, EdgeTypes}
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, CryptoOperation, NewCryptoOperation, StoredNode}
import io.shiftleft.passes.CpgPass
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.ontology.{CryptoOperationType, CryptoPolicy}

/** Semantic roles, independent of detection rule IDs. A role records what was resolved, not a verdict. */
private[enrichment] enum OperationObservationKind {
  case KeyStoreAccess, CredentialUse, KeyOrigin, CipherAlgorithm, RandomUse, AsymmetricParameters,
    MacAlgorithm, SignatureDigest, AuthenticationTag, RsaPadding, Verification, VerificationUse, Transport
}

private[enrichment] final case class OperationObservation(
  kind: OperationObservationKind,
  node: StoredNode,
  operationType: String,
  operation: String,
  algorithm: String = "",
  mode: String = "",
  detail: String = "",
  verificationKind: String = "",
  verificationEnabled: Option[Boolean] = None,
  transportScheme: String = "",
  protocolVersion: String = "",
  tagLength: Int = -1,
  keySize: Int = -1,
  evidenceNodes: Vector[StoredNode] = Vector.empty
)

/** Materializes resolved operation contexts before rule evaluation, including explicit secure settings.
  * Context nodes retain their original source node (which can be a callback method, not only a call).
  * No finding, rule ID, severity or remediation is stored in the graph.
  */
private[enrichment] final class SecurityOperationPass(cpg: Cpg, language: AdapterLanguage,
  sharedIndex: Option[JavaValueIndex], reportLegacyHmacSha1: Boolean) extends CpgPass(cpg) {
  import OperationObservationKind.*
  private var contextNanos = 0L
  def analysisNanos: Long = contextNanos
  private var operationCount = 0
  def addedOperations: Int = operationCount

  override def run(builder: DiffGraphBuilder): Unit = {
    // The primitive graph is already committed. Contexts must never recursively become extraction inputs.
    val analysisStart = System.nanoTime()
    val observations = SupplementalOperationModel.observations(cpg, language, sharedIndex, reportLegacyHmacSha1)
      .distinctBy(o => (o.kind, o.node.id, o.operation, o.algorithm, o.mode, o.verificationKind, o.tagLength))
    contextNanos = System.nanoTime() - analysisStart
    val existing = cpg.cryptoOperation.l.filter(_.semanticKind.nonEmpty).flatMap { o =>
      o.securityEvidenceNode.l.map(n => (o.semanticKind, n.id, o.operation, o.algorithm, o.mode, o.verificationKind, o.tagLength))
    }.toSet
    observations.foreach { observed =>
      val opType = if (observed.operationType == "KEY_STORE") "KEY_STORAGE" else observed.operationType
      require(CryptoOperationType.fromStoredValue(opType).nonEmpty, s"Unknown ontology operation $opType")
      val key = (observed.kind.toString, observed.node.id, observed.operation, observed.algorithm,
        observed.mode, observed.verificationKind, observed.tagLength)
      if (!existing(key)) {
        val deprecated = observed.kind match {
          case CipherAlgorithm => observed.node match {
            case o: CryptoOperation => o.isDeprecated
            case _ => SecurityPropertyAnnotator.evaluate(opType, observed.operation,
              observed.algorithm, observed.mode, "").isDeprecated
          }
          case MacAlgorithm | SignatureDigest =>
            val inherited = observed.node match {
              case o: CryptoOperation => o.isDeprecated
              case _ => false
            }
            val algorithm = observed.algorithm.toUpperCase
            inherited || algorithm.matches(".*(?:MD5|SHA-?1).*") ||
              CryptoPolicy.DeprecatedAlgorithms.contains(algorithm)
          case Transport => observed.protocolVersion.nonEmpty &&
            !Set("TLS1.2", "TLS1.3").contains(observed.protocolVersion)
          case _ => false
        }
        val security = SecurityPropertyAnnotator.evaluate(opType, observed.operation, observed.algorithm, observed.mode, "")
        val operation = NewCryptoOperation().opType(opType).operation(observed.operation)
          .algorithm(observed.algorithm).mode(observed.mode)
          .semanticKind(observed.kind.toString).semanticDetail(observed.detail)
          .verificationKind(observed.verificationKind)
          .transportScheme(observed.transportScheme).protocolVersion(observed.protocolVersion)
          .tagLength(observed.tagLength).isDeprecated(deprecated)
          .providesIntegrity(security.providesIntegrity)
          .requiresUniqueNonce(security.requiresUniqueNonce)
          .requiresCsprngSource(security.requiresCsprngSource)
          .resolution("COMPLETE")
        observed.verificationEnabled.foreach(operation.verificationEnabled)
        observed.node match {
          case o: CryptoOperation => operation.srcFile(o.srcFile).lineNo(o.lineNo)
          case ast: AstNode => operation.srcFile(ast.file.name.headOption.getOrElse("UNKNOWN"))
            .lineNo(ast.lineNumber.getOrElse(-1))
          case _ => operation.srcFile("UNKNOWN").lineNo(-1)
        }
        observed.kind match {
          case KeyStoreAccess | CredentialUse | KeyOrigin => operation.keySourceType("HARDCODED")
          case RandomUse => operation.source("WEAK_RANDOM").usedForCrypto(true)
          case RsaPadding => operation.padding("PKCS1")
          case AsymmetricParameters => operation.keySize(observed.keySize)
          case _ => ()
        }
        builder.addNode(operation)
        builder.addEdge(operation, observed.node, EdgeTypes.HAS_SECURITY_EVIDENCE)
        observed.evidenceNodes.filterNot(_.id == observed.node.id).foreach(n =>
          builder.addEdge(operation, n, EdgeTypes.HAS_SECURITY_EVIDENCE))
        val calls = observed.node match {
          case c: Call => Vector(c)
          case o: CryptoOperation => o.realizingCall.l.toVector
          case _ => Vector.empty
        }
        calls.foreach(c => builder.addEdge(c, operation, EdgeTypes.REALIZES_CRYPTO))
        operationCount += 1
      }
    }
  }
}
