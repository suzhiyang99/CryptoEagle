package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyIvSourceType[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasIvSourceTypeEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to ivSourceType property */
  def ivSourceType: Iterator[String] =
    traversal.map(_.ivSourceType)

  /** Traverse to nodes where the ivSourceType matches the regular expression `value`
    */
  def ivSourceType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      ivSourceTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.ivSourceType).matches }
    }

  /** Traverse to nodes where the ivSourceType matches at least one of the regular expressions in `values`
    */
  def ivSourceType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.ivSourceType).matches } }
  }

  /** Traverse to nodes where ivSourceType matches `value` exactly.
    */
  def ivSourceTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 36, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.ivSourceType == value }
  }

  /** Traverse to nodes where ivSourceType matches one of the elements in `values` exactly.
    */
  def ivSourceTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return ivSourceTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 36, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.ivSourceType) }
    }
  }

  /** Traverse to nodes where ivSourceType does not match the regular expression `value`.
    */
  def ivSourceTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.ivSourceType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.ivSourceType).matches }
    }
  }

  /** Traverse to nodes where ivSourceType does not match any of the regular expressions in `values`.
    */
  def ivSourceTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.ivSourceType).matches }.isEmpty }
  }

}
