package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertySource[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasSourceEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to source property */
  def source: Iterator[String] =
    traversal.map(_.source)

  /** Traverse to nodes where the source matches the regular expression `value`
    */
  def source(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      sourceExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.source).matches }
    }

  /** Traverse to nodes where the source matches at least one of the regular expressions in `values`
    */
  def source(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.source).matches } }
  }

  /** Traverse to nodes where source matches `value` exactly.
    */
  def sourceExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 75, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.source == value }
  }

  /** Traverse to nodes where source matches one of the elements in `values` exactly.
    */
  def sourceExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return sourceExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 75, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.source) }
    }
  }

  /** Traverse to nodes where source does not match the regular expression `value`.
    */
  def sourceNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.source != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.source).matches }
    }
  }

  /** Traverse to nodes where source does not match any of the regular expressions in `values`.
    */
  def sourceNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.source).matches }.isEmpty }
  }

}
