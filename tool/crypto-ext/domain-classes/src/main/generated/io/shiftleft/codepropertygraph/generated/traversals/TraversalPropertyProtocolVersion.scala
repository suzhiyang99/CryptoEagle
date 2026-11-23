package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyProtocolVersion[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasProtocolVersionEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to protocolVersion property */
  def protocolVersion: Iterator[String] =
    traversal.map(_.protocolVersion)

  /** Traverse to nodes where the protocolVersion matches the regular expression `value`
    */
  def protocolVersion(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      protocolVersionExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.protocolVersion).matches }
    }

  /** Traverse to nodes where the protocolVersion matches at least one of the regular expressions in `values`
    */
  def protocolVersion(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.protocolVersion).matches } }
  }

  /** Traverse to nodes where protocolVersion matches `value` exactly.
    */
  def protocolVersionExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 64, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.protocolVersion == value }
  }

  /** Traverse to nodes where protocolVersion matches one of the elements in `values` exactly.
    */
  def protocolVersionExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return protocolVersionExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 64, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.protocolVersion) }
    }
  }

  /** Traverse to nodes where protocolVersion does not match the regular expression `value`.
    */
  def protocolVersionNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.protocolVersion != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.protocolVersion).matches }
    }
  }

  /** Traverse to nodes where protocolVersion does not match any of the regular expressions in `values`.
    */
  def protocolVersionNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.protocolVersion).matches }.isEmpty }
  }

}
