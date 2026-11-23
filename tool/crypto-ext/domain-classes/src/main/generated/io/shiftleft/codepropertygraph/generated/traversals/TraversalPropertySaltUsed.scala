package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertySaltUsed[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasSaltUsedEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to saltUsed property */
  def saltUsed: Iterator[Boolean] =
    traversal.flatMap(_.saltUsed)

  /** Traverse to nodes where the saltUsed equals the given `value`
    */
  def saltUsed(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.saltUsed.isDefined && node.saltUsed.get == value }

}
