package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyVerificationEnabled[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasVerificationEnabledEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to verificationEnabled property */
  def verificationEnabled: Iterator[Boolean] =
    traversal.flatMap(_.verificationEnabled)

  /** Traverse to nodes where the verificationEnabled equals the given `value`
    */
  def verificationEnabled(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.verificationEnabled.isDefined && node.verificationEnabled.get == value }

}
