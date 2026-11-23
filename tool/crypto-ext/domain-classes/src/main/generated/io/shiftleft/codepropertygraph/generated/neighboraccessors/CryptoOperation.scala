package io.shiftleft.codepropertygraph.generated.neighboraccessors

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.language.*

final class AccessNeighborsForCryptoOperation(val node: nodes.CryptoOperation) extends AnyVal {

  /** A source call that realizes this cryptographic operation Traverse to CALL via REALIZES_CRYPTO IN edge.
    */
  @deprecated("please use realizingCall instead")
  def _callViaRealizesCryptoIn: Iterator[nodes.Call] = realizingCall

  /** A source call that realizes this cryptographic operation Traverse to CALL via REALIZES_CRYPTO IN edge.
    */
  def realizingCall: Iterator[nodes.Call] = realizesCryptoIn.collectAll[nodes.Call]

  /** Source node supporting this operation context Traverse to AnyNode via HAS_SECURITY_EVIDENCE OUT edge.
    */
  @deprecated("please use securityEvidenceNode instead")
  def _anynodeViaHasSecurityEvidenceOut: Iterator[nodes.StoredNode] = securityEvidenceNode

  /** Source node supporting this operation context Traverse to AnyNode via HAS_SECURITY_EVIDENCE OUT edge.
    */
  def securityEvidenceNode: Iterator[nodes.StoredNode] = hasSecurityEvidenceOut.collectAll[nodes.StoredNode]

  /** The terminal CPG source of initialization-vector material Traverse to AnyNode via USES_IV_FROM OUT edge.
    */
  @deprecated("please use ivSourceNode instead")
  def _anynodeViaUsesIvFromOut: Iterator[nodes.StoredNode] = ivSourceNode

  /** The terminal CPG source of initialization-vector material Traverse to AnyNode via USES_IV_FROM OUT edge.
    */
  def ivSourceNode: Iterator[nodes.StoredNode] = usesIvFromOut.collectAll[nodes.StoredNode]

  /** The terminal CPG source of key material Traverse to AnyNode via USES_KEY_FROM OUT edge.
    */
  @deprecated("please use keySourceNode instead")
  def _anynodeViaUsesKeyFromOut: Iterator[nodes.StoredNode] = keySourceNode

  /** The terminal CPG source of key material Traverse to AnyNode via USES_KEY_FROM OUT edge.
    */
  def keySourceNode: Iterator[nodes.StoredNode] = usesKeyFromOut.collectAll[nodes.StoredNode]

  /** The terminal CPG source of nonce material Traverse to AnyNode via USES_NONCE_FROM OUT edge.
    */
  @deprecated("please use nonceSourceNode instead")
  def _anynodeViaUsesNonceFromOut: Iterator[nodes.StoredNode] = nonceSourceNode

  /** The terminal CPG source of nonce material Traverse to AnyNode via USES_NONCE_FROM OUT edge.
    */
  def nonceSourceNode: Iterator[nodes.StoredNode] = usesNonceFromOut.collectAll[nodes.StoredNode]

  /** The terminal CPG source of salt material Traverse to AnyNode via USES_SALT_FROM OUT edge.
    */
  @deprecated("please use saltSourceNode instead")
  def _anynodeViaUsesSaltFromOut: Iterator[nodes.StoredNode] = saltSourceNode

  /** The terminal CPG source of salt material Traverse to AnyNode via USES_SALT_FROM OUT edge.
    */
  def saltSourceNode: Iterator[nodes.StoredNode] = usesSaltFromOut.collectAll[nodes.StoredNode]

  def hasSecurityEvidenceOut: Iterator[nodes.StoredNode] = node._hasSecurityEvidenceOut.cast[nodes.StoredNode]

  def realizesCryptoIn: Iterator[nodes.Call] = node._realizesCryptoIn.cast[nodes.Call]

  def usesIvFromOut: Iterator[nodes.StoredNode] = node._usesIvFromOut.cast[nodes.StoredNode]

  def usesKeyFromOut: Iterator[nodes.StoredNode] = node._usesKeyFromOut.cast[nodes.StoredNode]

  def usesNonceFromOut: Iterator[nodes.StoredNode] = node._usesNonceFromOut.cast[nodes.StoredNode]

  def usesSaltFromOut: Iterator[nodes.StoredNode] = node._usesSaltFromOut.cast[nodes.StoredNode]
}

final class AccessNeighborsForCryptoOperationTraversal(val traversal: Iterator[nodes.CryptoOperation]) extends AnyVal {

  /** A source call that realizes this cryptographic operation Traverse to CALL via REALIZES_CRYPTO IN edge.
    */
  def realizingCall: Iterator[nodes.Call] = traversal.flatMap(_.realizingCall)

  /** A source call that realizes this cryptographic operation Traverse to CALL via REALIZES_CRYPTO IN edge.
    */
  @deprecated("please use realizingCall instead")
  def _callViaRealizesCryptoIn: Iterator[nodes.Call] = traversal.flatMap(_._callViaRealizesCryptoIn)

  /** Source node supporting this operation context Traverse to AnyNode via HAS_SECURITY_EVIDENCE OUT edge.
    */
  def securityEvidenceNode: Iterator[nodes.StoredNode] = traversal.flatMap(_.securityEvidenceNode)

  /** Source node supporting this operation context Traverse to AnyNode via HAS_SECURITY_EVIDENCE OUT edge.
    */
  @deprecated("please use securityEvidenceNode instead")
  def _anynodeViaHasSecurityEvidenceOut: Iterator[nodes.StoredNode] =
    traversal.flatMap(_._anynodeViaHasSecurityEvidenceOut)

  /** The terminal CPG source of initialization-vector material Traverse to AnyNode via USES_IV_FROM OUT edge.
    */
  def ivSourceNode: Iterator[nodes.StoredNode] = traversal.flatMap(_.ivSourceNode)

  /** The terminal CPG source of initialization-vector material Traverse to AnyNode via USES_IV_FROM OUT edge.
    */
  @deprecated("please use ivSourceNode instead")
  def _anynodeViaUsesIvFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_._anynodeViaUsesIvFromOut)

  /** The terminal CPG source of key material Traverse to AnyNode via USES_KEY_FROM OUT edge.
    */
  def keySourceNode: Iterator[nodes.StoredNode] = traversal.flatMap(_.keySourceNode)

  /** The terminal CPG source of key material Traverse to AnyNode via USES_KEY_FROM OUT edge.
    */
  @deprecated("please use keySourceNode instead")
  def _anynodeViaUsesKeyFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_._anynodeViaUsesKeyFromOut)

  /** The terminal CPG source of nonce material Traverse to AnyNode via USES_NONCE_FROM OUT edge.
    */
  def nonceSourceNode: Iterator[nodes.StoredNode] = traversal.flatMap(_.nonceSourceNode)

  /** The terminal CPG source of nonce material Traverse to AnyNode via USES_NONCE_FROM OUT edge.
    */
  @deprecated("please use nonceSourceNode instead")
  def _anynodeViaUsesNonceFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_._anynodeViaUsesNonceFromOut)

  /** The terminal CPG source of salt material Traverse to AnyNode via USES_SALT_FROM OUT edge.
    */
  def saltSourceNode: Iterator[nodes.StoredNode] = traversal.flatMap(_.saltSourceNode)

  /** The terminal CPG source of salt material Traverse to AnyNode via USES_SALT_FROM OUT edge.
    */
  @deprecated("please use saltSourceNode instead")
  def _anynodeViaUsesSaltFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_._anynodeViaUsesSaltFromOut)

  def hasSecurityEvidenceOut: Iterator[nodes.StoredNode] = traversal.flatMap(_.hasSecurityEvidenceOut)

  def realizesCryptoIn: Iterator[nodes.Call] = traversal.flatMap(_.realizesCryptoIn)

  def usesIvFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_.usesIvFromOut)

  def usesKeyFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_.usesKeyFromOut)

  def usesNonceFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_.usesNonceFromOut)

  def usesSaltFromOut: Iterator[nodes.StoredNode] = traversal.flatMap(_.usesSaltFromOut)
}
