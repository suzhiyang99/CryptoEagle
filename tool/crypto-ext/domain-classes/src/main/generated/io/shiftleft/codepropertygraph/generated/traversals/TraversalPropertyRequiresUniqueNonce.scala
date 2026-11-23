package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyRequiresUniqueNonce[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasRequiresUniqueNonceEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to requiresUniqueNonce property */
  def requiresUniqueNonce: Iterator[Boolean] =
    traversal.map(_.requiresUniqueNonce)

  /** Traverse to nodes where the requiresUniqueNonce equals the given `value`
    */
  def requiresUniqueNonce(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.requiresUniqueNonce == value }

}
