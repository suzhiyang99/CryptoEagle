package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertySemanticDetail[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasSemanticDetailEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to semanticDetail property */
  def semanticDetail: Iterator[String] =
    traversal.map(_.semanticDetail)

  /** Traverse to nodes where the semanticDetail matches the regular expression `value`
    */
  def semanticDetail(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      semanticDetailExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.semanticDetail).matches }
    }

  /** Traverse to nodes where the semanticDetail matches at least one of the regular expressions in `values`
    */
  def semanticDetail(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.semanticDetail).matches } }
  }

  /** Traverse to nodes where semanticDetail matches `value` exactly.
    */
  def semanticDetailExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 72, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.semanticDetail == value }
  }

  /** Traverse to nodes where semanticDetail matches one of the elements in `values` exactly.
    */
  def semanticDetailExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return semanticDetailExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 72, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.semanticDetail) }
    }
  }

  /** Traverse to nodes where semanticDetail does not match the regular expression `value`.
    */
  def semanticDetailNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.semanticDetail != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.semanticDetail).matches }
    }
  }

  /** Traverse to nodes where semanticDetail does not match any of the regular expressions in `values`.
    */
  def semanticDetailNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.semanticDetail).matches }.isEmpty }
  }

}
