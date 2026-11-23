package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyOperation[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasOperationEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to operation property */
  def operation: Iterator[String] =
    traversal.map(_.operation)

  /** Traverse to nodes where the operation matches the regular expression `value`
    */
  def operation(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      operationExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.operation).matches }
    }

  /** Traverse to nodes where the operation matches at least one of the regular expressions in `values`
    */
  def operation(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.operation).matches } }
  }

  /** Traverse to nodes where operation matches `value` exactly.
    */
  def operationExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 55, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.operation == value }
  }

  /** Traverse to nodes where operation matches one of the elements in `values` exactly.
    */
  def operationExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return operationExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 55, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.operation) }
    }
  }

  /** Traverse to nodes where operation does not match the regular expression `value`.
    */
  def operationNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.operation != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.operation).matches }
    }
  }

  /** Traverse to nodes where operation does not match any of the regular expressions in `values`.
    */
  def operationNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.operation).matches }.isEmpty }
  }

}
