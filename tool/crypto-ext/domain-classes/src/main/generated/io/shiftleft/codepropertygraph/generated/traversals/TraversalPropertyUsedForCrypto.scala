package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyUsedForCrypto[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasUsedForCryptoEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to usedForCrypto property */
  def usedForCrypto: Iterator[Boolean] =
    traversal.flatMap(_.usedForCrypto)

  /** Traverse to nodes where the usedForCrypto equals the given `value`
    */
  def usedForCrypto(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.usedForCrypto.isDefined && node.usedForCrypto.get == value }

}
