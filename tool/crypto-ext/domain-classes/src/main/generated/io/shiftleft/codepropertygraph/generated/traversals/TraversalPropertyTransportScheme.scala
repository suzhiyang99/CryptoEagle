package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyTransportScheme[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasTransportSchemeEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to transportScheme property */
  def transportScheme: Iterator[String] =
    traversal.map(_.transportScheme)

  /** Traverse to nodes where the transportScheme matches the regular expression `value`
    */
  def transportScheme(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      transportSchemeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.transportScheme).matches }
    }

  /** Traverse to nodes where the transportScheme matches at least one of the regular expressions in `values`
    */
  def transportScheme(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.transportScheme).matches } }
  }

  /** Traverse to nodes where transportScheme matches `value` exactly.
    */
  def transportSchemeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 79, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.transportScheme == value }
  }

  /** Traverse to nodes where transportScheme matches one of the elements in `values` exactly.
    */
  def transportSchemeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return transportSchemeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 79, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.transportScheme) }
    }
  }

  /** Traverse to nodes where transportScheme does not match the regular expression `value`.
    */
  def transportSchemeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.transportScheme != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.transportScheme).matches }
    }
  }

  /** Traverse to nodes where transportScheme does not match any of the regular expressions in `values`.
    */
  def transportSchemeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.transportScheme).matches }.isEmpty }
  }

}
