package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertySemanticKind[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasSemanticKindEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to semanticKind property */
  def semanticKind: Iterator[String] =
    traversal.map(_.semanticKind)

  /** Traverse to nodes where the semanticKind matches the regular expression `value`
    */
  def semanticKind(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      semanticKindExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.semanticKind).matches }
    }

  /** Traverse to nodes where the semanticKind matches at least one of the regular expressions in `values`
    */
  def semanticKind(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.semanticKind).matches } }
  }

  /** Traverse to nodes where semanticKind matches `value` exactly.
    */
  def semanticKindExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 73, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.semanticKind == value }
  }

  /** Traverse to nodes where semanticKind matches one of the elements in `values` exactly.
    */
  def semanticKindExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return semanticKindExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 73, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.semanticKind) }
    }
  }

  /** Traverse to nodes where semanticKind does not match the regular expression `value`.
    */
  def semanticKindNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.semanticKind != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.semanticKind).matches }
    }
  }

  /** Traverse to nodes where semanticKind does not match any of the regular expressions in `values`.
    */
  def semanticKindNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.semanticKind).matches }.isEmpty }
  }

}
