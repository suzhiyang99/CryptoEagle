package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyOutputLength[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasOutputLengthEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to outputLength property */
  def outputLength: Iterator[Int] =
    traversal.map(_.outputLength)

  /** Traverse to nodes where the outputLength equals the given `value`
    */
  def outputLength(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength == value }

  /** Traverse to nodes where the outputLength equals at least one of the given `values`
    */
  def outputLength(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.outputLength) }
  }

  /** Traverse to nodes where the outputLength is not equal to the given `value`
    */
  def outputLengthNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength != value }

  /** Traverse to nodes where the outputLength is not equal to any of the given `values`
    */
  def outputLengthNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.outputLength) }
  }

  /** Traverse to nodes where the outputLength is greater than the given `value`
    */
  def outputLengthGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength > value }

  /** Traverse to nodes where the outputLength is greater than or equal the given `value`
    */
  def outputLengthGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength >= value }

  /** Traverse to nodes where the outputLength is less than the given `value`
    */
  def outputLengthLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength < value }

  /** Traverse to nodes where the outputLength is less than or equal the given `value`
    */
  def outputLengthLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength <= value }

}
