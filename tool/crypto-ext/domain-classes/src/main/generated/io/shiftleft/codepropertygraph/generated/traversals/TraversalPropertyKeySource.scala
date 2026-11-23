package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyKeySource[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasKeySourceEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to keySource property */
  def keySource: Iterator[String] =
    traversal.map(_.keySource)

  /** Traverse to nodes where the keySource matches the regular expression `value`
    */
  def keySource(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      keySourceExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.keySource).matches }
    }

  /** Traverse to nodes where the keySource matches at least one of the regular expressions in `values`
    */
  def keySource(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.keySource).matches } }
  }

  /** Traverse to nodes where keySource matches `value` exactly.
    */
  def keySourceExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 40, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.keySource == value }
  }

  /** Traverse to nodes where keySource matches one of the elements in `values` exactly.
    */
  def keySourceExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return keySourceExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 40, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.keySource) }
    }
  }

  /** Traverse to nodes where keySource does not match the regular expression `value`.
    */
  def keySourceNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.keySource != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.keySource).matches }
    }
  }

  /** Traverse to nodes where keySource does not match any of the regular expressions in `values`.
    */
  def keySourceNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.keySource).matches }.isEmpty }
  }

}
