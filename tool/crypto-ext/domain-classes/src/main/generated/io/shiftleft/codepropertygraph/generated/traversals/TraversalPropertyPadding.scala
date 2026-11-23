package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyPadding[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasPaddingEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to padding property */
  def padding: Iterator[String] =
    traversal.map(_.padding)

  /** Traverse to nodes where the padding matches the regular expression `value`
    */
  def padding(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      paddingExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.padding).matches }
    }

  /** Traverse to nodes where the padding matches at least one of the regular expressions in `values`
    */
  def padding(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.padding).matches } }
  }

  /** Traverse to nodes where padding matches `value` exactly.
    */
  def paddingExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 61, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.padding == value }
  }

  /** Traverse to nodes where padding matches one of the elements in `values` exactly.
    */
  def paddingExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return paddingExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 61, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.padding) }
    }
  }

  /** Traverse to nodes where padding does not match the regular expression `value`.
    */
  def paddingNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.padding != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.padding).matches }
    }
  }

  /** Traverse to nodes where padding does not match any of the regular expressions in `values`.
    */
  def paddingNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.padding).matches }.isEmpty }
  }

}
