package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyRequiresCsprngSource[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasRequiresCsprngSourceEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to requiresCsprngSource property */
  def requiresCsprngSource: Iterator[Boolean] =
    traversal.map(_.requiresCsprngSource)

  /** Traverse to nodes where the requiresCsprngSource equals the given `value`
    */
  def requiresCsprngSource(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.requiresCsprngSource == value }

}
