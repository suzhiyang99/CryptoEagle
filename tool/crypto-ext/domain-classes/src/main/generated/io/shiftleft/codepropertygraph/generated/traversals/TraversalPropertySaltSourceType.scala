package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertySaltSourceType[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasSaltSourceTypeEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to saltSourceType property */
  def saltSourceType: Iterator[String] =
    traversal.map(_.saltSourceType)

  /** Traverse to nodes where the saltSourceType matches the regular expression `value`
    */
  def saltSourceType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      saltSourceTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.saltSourceType).matches }
    }

  /** Traverse to nodes where the saltSourceType matches at least one of the regular expressions in `values`
    */
  def saltSourceType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.saltSourceType).matches } }
  }

  /** Traverse to nodes where saltSourceType matches `value` exactly.
    */
  def saltSourceTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 70, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.saltSourceType == value }
  }

  /** Traverse to nodes where saltSourceType matches one of the elements in `values` exactly.
    */
  def saltSourceTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return saltSourceTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 70, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.saltSourceType) }
    }
  }

  /** Traverse to nodes where saltSourceType does not match the regular expression `value`.
    */
  def saltSourceTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.saltSourceType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.saltSourceType).matches }
    }
  }

  /** Traverse to nodes where saltSourceType does not match any of the regular expressions in `values`.
    */
  def saltSourceTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.saltSourceType).matches }.isEmpty }
  }

}
