package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyLineNo[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasLineNoEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to lineNo property */
  def lineNo: Iterator[Int] =
    traversal.map(_.lineNo)

  /** Traverse to nodes where the lineNo equals the given `value`
    */
  def lineNo(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo == value }

  /** Traverse to nodes where the lineNo equals at least one of the given `values`
    */
  def lineNo(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.lineNo) }
  }

  /** Traverse to nodes where the lineNo is not equal to the given `value`
    */
  def lineNoNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo != value }

  /** Traverse to nodes where the lineNo is not equal to any of the given `values`
    */
  def lineNoNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.lineNo) }
  }

  /** Traverse to nodes where the lineNo is greater than the given `value`
    */
  def lineNoGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo > value }

  /** Traverse to nodes where the lineNo is greater than or equal the given `value`
    */
  def lineNoGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo >= value }

  /** Traverse to nodes where the lineNo is less than the given `value`
    */
  def lineNoLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo < value }

  /** Traverse to nodes where the lineNo is less than or equal the given `value`
    */
  def lineNoLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo <= value }

}
