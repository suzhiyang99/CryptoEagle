package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyAlgorithm[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasAlgorithmEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to algorithm property */
  def algorithm: Iterator[String] =
    traversal.map(_.algorithm)

  /** Traverse to nodes where the algorithm matches the regular expression `value`
    */
  def algorithm(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      algorithmExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.algorithm).matches }
    }

  /** Traverse to nodes where the algorithm matches at least one of the regular expressions in `values`
    */
  def algorithm(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.algorithm).matches } }
  }

  /** Traverse to nodes where algorithm matches `value` exactly.
    */
  def algorithmExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 0, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.algorithm == value }
  }

  /** Traverse to nodes where algorithm matches one of the elements in `values` exactly.
    */
  def algorithmExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return algorithmExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 0, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.algorithm) }
    }
  }

  /** Traverse to nodes where algorithm does not match the regular expression `value`.
    */
  def algorithmNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.algorithm != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.algorithm).matches }
    }
  }

  /** Traverse to nodes where algorithm does not match any of the regular expressions in `values`.
    */
  def algorithmNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.algorithm).matches }.isEmpty }
  }

}
