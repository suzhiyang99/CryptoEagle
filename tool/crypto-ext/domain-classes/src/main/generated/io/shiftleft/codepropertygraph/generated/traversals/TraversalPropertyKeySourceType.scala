package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyKeySourceType[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasKeySourceTypeEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to keySourceType property */
  def keySourceType: Iterator[String] =
    traversal.map(_.keySourceType)

  /** Traverse to nodes where the keySourceType matches the regular expression `value`
    */
  def keySourceType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      keySourceTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.keySourceType).matches }
    }

  /** Traverse to nodes where the keySourceType matches at least one of the regular expressions in `values`
    */
  def keySourceType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.keySourceType).matches } }
  }

  /** Traverse to nodes where keySourceType matches `value` exactly.
    */
  def keySourceTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 41, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.keySourceType == value }
  }

  /** Traverse to nodes where keySourceType matches one of the elements in `values` exactly.
    */
  def keySourceTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return keySourceTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 41, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.keySourceType) }
    }
  }

  /** Traverse to nodes where keySourceType does not match the regular expression `value`.
    */
  def keySourceTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.keySourceType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.keySourceType).matches }
    }
  }

  /** Traverse to nodes where keySourceType does not match any of the regular expressions in `values`.
    */
  def keySourceTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.keySourceType).matches }.isEmpty }
  }

}
