package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertyVerificationKind[
  NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasVerificationKindEMT]
](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to verificationKind property */
  def verificationKind: Iterator[String] =
    traversal.map(_.verificationKind)

  /** Traverse to nodes where the verificationKind matches the regular expression `value`
    */
  def verificationKind(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      verificationKindExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.verificationKind).matches }
    }

  /** Traverse to nodes where the verificationKind matches at least one of the regular expressions in `values`
    */
  def verificationKind(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.verificationKind).matches } }
  }

  /** Traverse to nodes where verificationKind matches `value` exactly.
    */
  def verificationKindExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 85, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.verificationKind == value }
  }

  /** Traverse to nodes where verificationKind matches one of the elements in `values` exactly.
    */
  def verificationKindExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return verificationKindExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 85, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.verificationKind) }
    }
  }

  /** Traverse to nodes where verificationKind does not match the regular expression `value`.
    */
  def verificationKindNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.verificationKind != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.verificationKind).matches }
    }
  }

  /** Traverse to nodes where verificationKind does not match any of the regular expressions in `values`.
    */
  def verificationKindNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.verificationKind).matches }.isEmpty }
  }

}
