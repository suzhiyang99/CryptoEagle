package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalPropertySrcFile[NodeType <: nodes.StoredNode & nodes.StaticType[nodes.HasSrcFileEMT]](
  val traversal: Iterator[NodeType]
) extends AnyVal {

  /** Traverse to srcFile property */
  def srcFile: Iterator[String] =
    traversal.map(_.srcFile)

  /** Traverse to nodes where the srcFile matches the regular expression `value`
    */
  def srcFile(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      srcFileExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.srcFile).matches }
    }

  /** Traverse to nodes where the srcFile matches at least one of the regular expressions in `values`
    */
  def srcFile(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.srcFile).matches } }
  }

  /** Traverse to nodes where srcFile matches `value` exactly.
    */
  def srcFileExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 76, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.srcFile == value }
  }

  /** Traverse to nodes where srcFile matches one of the elements in `values` exactly.
    */
  def srcFileExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return srcFileExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 76, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.srcFile) }
    }
  }

  /** Traverse to nodes where srcFile does not match the regular expression `value`.
    */
  def srcFileNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.srcFile != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.srcFile).matches }
    }
  }

  /** Traverse to nodes where srcFile does not match any of the regular expressions in `values`.
    */
  def srcFileNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.srcFile).matches }.isEmpty }
  }

}
