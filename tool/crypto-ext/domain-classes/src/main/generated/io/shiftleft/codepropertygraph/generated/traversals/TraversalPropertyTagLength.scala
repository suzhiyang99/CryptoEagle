package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyTagLength[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasTagLengthEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to tagLength property */
  def tagLength: Iterator[Int] =
    traversal.map(_.tagLength)

  /** Traverse to nodes where the tagLength equals the given `value`
    */
  def tagLength(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength == value }

  /** Traverse to nodes where the tagLength equals at least one of the given `values`
    */
  def tagLength(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.tagLength) }
  }

  /** Traverse to nodes where the tagLength is not equal to the given `value`
    */
  def tagLengthNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength != value }

  /** Traverse to nodes where the tagLength is not equal to any of the given `values`
    */
  def tagLengthNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.tagLength) }
  }

  /** Traverse to nodes where the tagLength is greater than the given `value`
    */
  def tagLengthGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength > value }

  /** Traverse to nodes where the tagLength is greater than or equal the given `value`
    */
  def tagLengthGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength >= value }

  /** Traverse to nodes where the tagLength is less than the given `value`
    */
  def tagLengthLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength < value }

  /** Traverse to nodes where the tagLength is less than or equal the given `value`
    */
  def tagLengthLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength <= value }

}
