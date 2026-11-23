package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyNonceSourceType[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasNonceSourceTypeEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to nonceSourceType property */
  def nonceSourceType: Iterator[String] =
    traversal.map(_.nonceSourceType)

  /** Traverse to nodes where the nonceSourceType matches the regular expression `value`
    */
  def nonceSourceType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      nonceSourceTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.nonceSourceType).matches }
    }

  /** Traverse to nodes where the nonceSourceType matches at least one of the regular expressions in `values`
    */
  def nonceSourceType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.nonceSourceType).matches } }
  }

  /** Traverse to nodes where nonceSourceType matches `value` exactly.
    */
  def nonceSourceTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 52, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.nonceSourceType == value }
  }

  /** Traverse to nodes where nonceSourceType matches one of the elements in `values` exactly.
    */
  def nonceSourceTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return nonceSourceTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 52, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.nonceSourceType) }
    }
  }

  /** Traverse to nodes where nonceSourceType does not match the regular expression `value`.
    */
  def nonceSourceTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.nonceSourceType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.nonceSourceType).matches }
    }
  }

  /** Traverse to nodes where nonceSourceType does not match any of the regular expressions in `values`.
    */
  def nonceSourceTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.nonceSourceType).matches }.isEmpty }
  }

}
