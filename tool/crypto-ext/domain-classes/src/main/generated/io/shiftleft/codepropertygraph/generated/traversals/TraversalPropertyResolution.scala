package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyResolution[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasResolutionEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to resolution property */
  def resolution: Iterator[String] =
    traversal.map(_.resolution)

  /** Traverse to nodes where the resolution matches the regular expression `value`
    */
  def resolution(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      resolutionExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.resolution).matches }
    }

  /** Traverse to nodes where the resolution matches at least one of the regular expressions in `values`
    */
  def resolution(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.resolution).matches } }
  }

  /** Traverse to nodes where resolution matches `value` exactly.
    */
  def resolutionExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 68, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.resolution == value }
  }

  /** Traverse to nodes where resolution matches one of the elements in `values` exactly.
    */
  def resolutionExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return resolutionExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 68, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.resolution) }
    }
  }

  /** Traverse to nodes where resolution does not match the regular expression `value`.
    */
  def resolutionNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.resolution != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.resolution).matches }
    }
  }

  /** Traverse to nodes where resolution does not match any of the regular expressions in `values`.
    */
  def resolutionNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.resolution).matches }.isEmpty }
  }

}
