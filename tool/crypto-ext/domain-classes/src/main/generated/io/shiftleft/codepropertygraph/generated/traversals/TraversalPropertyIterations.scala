package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyIterations[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasIterationsEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to iterations property */
  def iterations: Iterator[Int] =
    traversal.map(_.iterations)

  /** Traverse to nodes where the iterations equals the given `value`
    */
  def iterations(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations == value }

  /** Traverse to nodes where the iterations equals at least one of the given `values`
    */
  def iterations(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.iterations) }
  }

  /** Traverse to nodes where the iterations is not equal to the given `value`
    */
  def iterationsNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations != value }

  /** Traverse to nodes where the iterations is not equal to any of the given `values`
    */
  def iterationsNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.iterations) }
  }

  /** Traverse to nodes where the iterations is greater than the given `value`
    */
  def iterationsGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations > value }

  /** Traverse to nodes where the iterations is greater than or equal the given `value`
    */
  def iterationsGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations >= value }

  /** Traverse to nodes where the iterations is less than the given `value`
    */
  def iterationsLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations < value }

  /** Traverse to nodes where the iterations is less than or equal the given `value`
    */
  def iterationsLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations <= value }

}
