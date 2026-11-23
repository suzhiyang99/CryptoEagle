package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyProvidesIntegrity[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasProvidesIntegrityEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to providesIntegrity property */
  def providesIntegrity: Iterator[Boolean] =
    traversal.map(_.providesIntegrity)

  /** Traverse to nodes where the providesIntegrity equals the given `value`
    */
  def providesIntegrity(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.providesIntegrity == value }

}
