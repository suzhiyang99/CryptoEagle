package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyOpType[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasOpTypeEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to opType property */
  def opType: Iterator[String] =
    traversal.map(_.opType)

  /** Traverse to nodes where the opType matches the regular expression `value`
    */
  def opType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      opTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.opType).matches }
    }

  /** Traverse to nodes where the opType matches at least one of the regular expressions in `values`
    */
  def opType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.opType).matches } }
  }

  /** Traverse to nodes where opType matches `value` exactly.
    */
  def opTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 56, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.opType == value }
  }

  /** Traverse to nodes where opType matches one of the elements in `values` exactly.
    */
  def opTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return opTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 56, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.opType) }
    }
  }

  /** Traverse to nodes where opType does not match the regular expression `value`.
    */
  def opTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.opType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.opType).matches }
    }
  }

  /** Traverse to nodes where opType does not match any of the regular expressions in `values`.
    */
  def opTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.opType).matches }.isEmpty }
  }

}
