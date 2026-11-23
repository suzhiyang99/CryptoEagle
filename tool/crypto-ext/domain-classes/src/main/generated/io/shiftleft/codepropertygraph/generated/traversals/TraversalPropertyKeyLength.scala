package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyKeyLength[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasKeyLengthEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to keyLength property */
  def keyLength: Iterator[Int] =
    traversal.map(_.keyLength)

  /** Traverse to nodes where the keyLength equals the given `value`
    */
  def keyLength(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength == value }

  /** Traverse to nodes where the keyLength equals at least one of the given `values`
    */
  def keyLength(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.keyLength) }
  }

  /** Traverse to nodes where the keyLength is not equal to the given `value`
    */
  def keyLengthNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength != value }

  /** Traverse to nodes where the keyLength is not equal to any of the given `values`
    */
  def keyLengthNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.keyLength) }
  }

  /** Traverse to nodes where the keyLength is greater than the given `value`
    */
  def keyLengthGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength > value }

  /** Traverse to nodes where the keyLength is greater than or equal the given `value`
    */
  def keyLengthGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength >= value }

  /** Traverse to nodes where the keyLength is less than the given `value`
    */
  def keyLengthLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength < value }

  /** Traverse to nodes where the keyLength is less than or equal the given `value`
    */
  def keyLengthLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength <= value }

}
