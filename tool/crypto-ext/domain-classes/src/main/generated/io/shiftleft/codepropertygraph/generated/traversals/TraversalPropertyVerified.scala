package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyVerified[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasVerifiedEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to verified property */
  def verified: Iterator[Boolean] =
    traversal.flatMap(_.verified)

  /** Traverse to nodes where the verified equals the given `value`
    */
  def verified(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.verified.isDefined && node.verified.get == value }

}
