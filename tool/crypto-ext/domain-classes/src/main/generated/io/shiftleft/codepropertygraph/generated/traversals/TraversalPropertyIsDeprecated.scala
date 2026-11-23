package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyIsDeprecated[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasIsDeprecatedEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to isDeprecated property */
  def isDeprecated: Iterator[Boolean] =
    traversal.map(_.isDeprecated)

  /** Traverse to nodes where the isDeprecated equals the given `value`
    */
  def isDeprecated(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.isDeprecated == value }

}
