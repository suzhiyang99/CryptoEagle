package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyKeySize[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasKeySizeEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to keySize property */
  def keySize: Iterator[Int] =
    traversal.map(_.keySize)

  /** Traverse to nodes where the keySize equals the given `value`
    */
  def keySize(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize == value }

  /** Traverse to nodes where the keySize equals at least one of the given `values`
    */
  def keySize(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.keySize) }
  }

  /** Traverse to nodes where the keySize is not equal to the given `value`
    */
  def keySizeNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize != value }

  /** Traverse to nodes where the keySize is not equal to any of the given `values`
    */
  def keySizeNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.keySize) }
  }

  /** Traverse to nodes where the keySize is greater than the given `value`
    */
  def keySizeGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize > value }

  /** Traverse to nodes where the keySize is greater than or equal the given `value`
    */
  def keySizeGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize >= value }

  /** Traverse to nodes where the keySize is less than the given `value`
    */
  def keySizeLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize < value }

  /** Traverse to nodes where the keySize is less than or equal the given `value`
    */
  def keySizeLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize <= value }

}
