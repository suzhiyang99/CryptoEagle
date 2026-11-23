package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyMode[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasModeEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to mode property */
  def mode: Iterator[String] =
    traversal.map(_.mode)

  /** Traverse to nodes where the mode matches the regular expression `value`
    */
  def mode(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      modeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.mode).matches }
    }

  /** Traverse to nodes where the mode matches at least one of the regular expressions in `values`
    */
  def mode(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.mode).matches } }
  }

  /** Traverse to nodes where mode matches `value` exactly.
    */
  def modeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 48, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.mode == value }
  }

  /** Traverse to nodes where mode matches one of the elements in `values` exactly.
    */
  def modeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return modeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 48, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.mode) }
    }
  }

  /** Traverse to nodes where mode does not match the regular expression `value`.
    */
  def modeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.mode != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.mode).matches }
    }
  }

  /** Traverse to nodes where mode does not match any of the regular expressions in `values`.
    */
  def modeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.mode).matches }.isEmpty }
  }

}
