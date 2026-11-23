package io.shiftleft.codepropertygraph.generated.traversals

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.codepropertygraph.generated.accessors.languagebootstrap.*

final class TraversalCryptooperationBase[NodeType <: nodes.CryptoOperationBase](val traversal: Iterator[NodeType])
    extends AnyVal {

  /** Traverse to algorithm property */
  def algorithm: Iterator[String] =
    traversal.map(_.algorithm)

  /** Traverse to nodes where the algorithm matches the regular expression `value`
    */
  def algorithm(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      algorithmExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.algorithm).matches }
    }

  /** Traverse to nodes where the algorithm matches at least one of the regular expressions in `values`
    */
  def algorithm(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.algorithm).matches } }
  }

  /** Traverse to nodes where algorithm matches `value` exactly.
    */
  def algorithmExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 0, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.algorithm == value }
  }

  /** Traverse to nodes where algorithm matches one of the elements in `values` exactly.
    */
  def algorithmExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return algorithmExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 0, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.algorithm) }
    }
  }

  /** Traverse to nodes where algorithm does not match the regular expression `value`.
    */
  def algorithmNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.algorithm != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.algorithm).matches }
    }
  }

  /** Traverse to nodes where algorithm does not match any of the regular expressions in `values`.
    */
  def algorithmNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.algorithm).matches }.isEmpty }
  }

  /** Traverse to isDeprecated property */
  def isDeprecated: Iterator[Boolean] =
    traversal.map(_.isDeprecated)

  /** Traverse to nodes where the isDeprecated equals the given `value`
    */
  def isDeprecated(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.isDeprecated == value }

  /** Traverse to iterations property */
  def iterations: Iterator[Int] =
    traversal.map(_.iterations)

  /** Traverse to nodes where the iterations equals the given `value`
    */
  def iterations(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations == value }

  /** Traverse to nodes where the iterations equals at least one of the given `values`
    */
  def iterations(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.iterations) }
  }

  /** Traverse to nodes where the iterations is not equal to the given `value`
    */
  def iterationsNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations != value }

  /** Traverse to nodes where the iterations is not equal to any of the given `values`
    */
  def iterationsNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.iterations) }
  }

  /** Traverse to nodes where the iterations is greater than the given `value`
    */
  def iterationsGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations > value }

  /** Traverse to nodes where the iterations is greater than or equal the given `value`
    */
  def iterationsGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations >= value }

  /** Traverse to nodes where the iterations is less than the given `value`
    */
  def iterationsLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations < value }

  /** Traverse to nodes where the iterations is less than or equal the given `value`
    */
  def iterationsLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.iterations <= value }

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

  /** Traverse to keyLength property */
  def keyLength: Iterator[Int] =
    traversal.map(_.keyLength)

  /** Traverse to nodes where the keyLength equals the given `value`
    */
  def keyLength(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength == value }

  /** Traverse to nodes where the keyLength equals at least one of the given `values`
    */
  def keyLength(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.keyLength) }
  }

  /** Traverse to nodes where the keyLength is not equal to the given `value`
    */
  def keyLengthNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength != value }

  /** Traverse to nodes where the keyLength is not equal to any of the given `values`
    */
  def keyLengthNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.keyLength) }
  }

  /** Traverse to nodes where the keyLength is greater than the given `value`
    */
  def keyLengthGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength > value }

  /** Traverse to nodes where the keyLength is greater than or equal the given `value`
    */
  def keyLengthGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength >= value }

  /** Traverse to nodes where the keyLength is less than the given `value`
    */
  def keyLengthLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength < value }

  /** Traverse to nodes where the keyLength is less than or equal the given `value`
    */
  def keyLengthLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keyLength <= value }

  /** Traverse to keySize property */
  def keySize: Iterator[Int] =
    traversal.map(_.keySize)

  /** Traverse to nodes where the keySize equals the given `value`
    */
  def keySize(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize == value }

  /** Traverse to nodes where the keySize equals at least one of the given `values`
    */
  def keySize(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.keySize) }
  }

  /** Traverse to nodes where the keySize is not equal to the given `value`
    */
  def keySizeNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize != value }

  /** Traverse to nodes where the keySize is not equal to any of the given `values`
    */
  def keySizeNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.keySize) }
  }

  /** Traverse to nodes where the keySize is greater than the given `value`
    */
  def keySizeGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize > value }

  /** Traverse to nodes where the keySize is greater than or equal the given `value`
    */
  def keySizeGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize >= value }

  /** Traverse to nodes where the keySize is less than the given `value`
    */
  def keySizeLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize < value }

  /** Traverse to nodes where the keySize is less than or equal the given `value`
    */
  def keySizeLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.keySize <= value }

  /** Traverse to keySource property */
  def keySource: Iterator[String] =
    traversal.map(_.keySource)

  /** Traverse to nodes where the keySource matches the regular expression `value`
    */
  def keySource(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      keySourceExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.keySource).matches }
    }

  /** Traverse to nodes where the keySource matches at least one of the regular expressions in `values`
    */
  def keySource(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.keySource).matches } }
  }

  /** Traverse to nodes where keySource matches `value` exactly.
    */
  def keySourceExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 40, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.keySource == value }
  }

  /** Traverse to nodes where keySource matches one of the elements in `values` exactly.
    */
  def keySourceExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return keySourceExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 40, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.keySource) }
    }
  }

  /** Traverse to nodes where keySource does not match the regular expression `value`.
    */
  def keySourceNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.keySource != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.keySource).matches }
    }
  }

  /** Traverse to nodes where keySource does not match any of the regular expressions in `values`.
    */
  def keySourceNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.keySource).matches }.isEmpty }
  }

  /** Traverse to keySourceType property */
  def keySourceType: Iterator[String] =
    traversal.map(_.keySourceType)

  /** Traverse to nodes where the keySourceType matches the regular expression `value`
    */
  def keySourceType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      keySourceTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.keySourceType).matches }
    }

  /** Traverse to nodes where the keySourceType matches at least one of the regular expressions in `values`
    */
  def keySourceType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.keySourceType).matches } }
  }

  /** Traverse to nodes where keySourceType matches `value` exactly.
    */
  def keySourceTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 41, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.keySourceType == value }
  }

  /** Traverse to nodes where keySourceType matches one of the elements in `values` exactly.
    */
  def keySourceTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return keySourceTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 41, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.keySourceType) }
    }
  }

  /** Traverse to nodes where keySourceType does not match the regular expression `value`.
    */
  def keySourceTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.keySourceType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.keySourceType).matches }
    }
  }

  /** Traverse to nodes where keySourceType does not match any of the regular expressions in `values`.
    */
  def keySourceTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.keySourceType).matches }.isEmpty }
  }

  /** Traverse to lineNo property */
  def lineNo: Iterator[Int] =
    traversal.map(_.lineNo)

  /** Traverse to nodes where the lineNo equals the given `value`
    */
  def lineNo(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo == value }

  /** Traverse to nodes where the lineNo equals at least one of the given `values`
    */
  def lineNo(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.lineNo) }
  }

  /** Traverse to nodes where the lineNo is not equal to the given `value`
    */
  def lineNoNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo != value }

  /** Traverse to nodes where the lineNo is not equal to any of the given `values`
    */
  def lineNoNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.lineNo) }
  }

  /** Traverse to nodes where the lineNo is greater than the given `value`
    */
  def lineNoGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo > value }

  /** Traverse to nodes where the lineNo is greater than or equal the given `value`
    */
  def lineNoGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo >= value }

  /** Traverse to nodes where the lineNo is less than the given `value`
    */
  def lineNoLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo < value }

  /** Traverse to nodes where the lineNo is less than or equal the given `value`
    */
  def lineNoLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.lineNo <= value }

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

  /** Traverse to nonceSourceType property */
  def nonceSourceType: Iterator[String] =
    traversal.map(_.nonceSourceType)

  /** Traverse to nodes where the nonceSourceType matches the regular expression `value`
    */
  def nonceSourceType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      nonceSourceTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.nonceSourceType).matches }
    }

  /** Traverse to nodes where the nonceSourceType matches at least one of the regular expressions in `values`
    */
  def nonceSourceType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.nonceSourceType).matches } }
  }

  /** Traverse to nodes where nonceSourceType matches `value` exactly.
    */
  def nonceSourceTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 52, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.nonceSourceType == value }
  }

  /** Traverse to nodes where nonceSourceType matches one of the elements in `values` exactly.
    */
  def nonceSourceTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return nonceSourceTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 52, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.nonceSourceType) }
    }
  }

  /** Traverse to nodes where nonceSourceType does not match the regular expression `value`.
    */
  def nonceSourceTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.nonceSourceType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.nonceSourceType).matches }
    }
  }

  /** Traverse to nodes where nonceSourceType does not match any of the regular expressions in `values`.
    */
  def nonceSourceTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.nonceSourceType).matches }.isEmpty }
  }

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

  /** Traverse to outputLength property */
  def outputLength: Iterator[Int] =
    traversal.map(_.outputLength)

  /** Traverse to nodes where the outputLength equals the given `value`
    */
  def outputLength(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength == value }

  /** Traverse to nodes where the outputLength equals at least one of the given `values`
    */
  def outputLength(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.outputLength) }
  }

  /** Traverse to nodes where the outputLength is not equal to the given `value`
    */
  def outputLengthNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength != value }

  /** Traverse to nodes where the outputLength is not equal to any of the given `values`
    */
  def outputLengthNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.outputLength) }
  }

  /** Traverse to nodes where the outputLength is greater than the given `value`
    */
  def outputLengthGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength > value }

  /** Traverse to nodes where the outputLength is greater than or equal the given `value`
    */
  def outputLengthGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength >= value }

  /** Traverse to nodes where the outputLength is less than the given `value`
    */
  def outputLengthLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength < value }

  /** Traverse to nodes where the outputLength is less than or equal the given `value`
    */
  def outputLengthLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.outputLength <= value }

  /** Traverse to padding property */
  def padding: Iterator[String] =
    traversal.map(_.padding)

  /** Traverse to nodes where the padding matches the regular expression `value`
    */
  def padding(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      paddingExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.padding).matches }
    }

  /** Traverse to nodes where the padding matches at least one of the regular expressions in `values`
    */
  def padding(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.padding).matches } }
  }

  /** Traverse to nodes where padding matches `value` exactly.
    */
  def paddingExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 61, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.padding == value }
  }

  /** Traverse to nodes where padding matches one of the elements in `values` exactly.
    */
  def paddingExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return paddingExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 61, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.padding) }
    }
  }

  /** Traverse to nodes where padding does not match the regular expression `value`.
    */
  def paddingNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.padding != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.padding).matches }
    }
  }

  /** Traverse to nodes where padding does not match any of the regular expressions in `values`.
    */
  def paddingNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.padding).matches }.isEmpty }
  }

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

  /** Traverse to providesIntegrity property */
  def providesIntegrity: Iterator[Boolean] =
    traversal.map(_.providesIntegrity)

  /** Traverse to nodes where the providesIntegrity equals the given `value`
    */
  def providesIntegrity(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.providesIntegrity == value }

  /** Traverse to requiresCsprngSource property */
  def requiresCsprngSource: Iterator[Boolean] =
    traversal.map(_.requiresCsprngSource)

  /** Traverse to nodes where the requiresCsprngSource equals the given `value`
    */
  def requiresCsprngSource(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.requiresCsprngSource == value }

  /** Traverse to requiresUniqueNonce property */
  def requiresUniqueNonce: Iterator[Boolean] =
    traversal.map(_.requiresUniqueNonce)

  /** Traverse to nodes where the requiresUniqueNonce equals the given `value`
    */
  def requiresUniqueNonce(value: Boolean): Iterator[NodeType] =
    traversal.filter { _.requiresUniqueNonce == value }

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

  /** Traverse to saltSourceType property */
  def saltSourceType: Iterator[String] =
    traversal.map(_.saltSourceType)

  /** Traverse to nodes where the saltSourceType matches the regular expression `value`
    */
  def saltSourceType(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      saltSourceTypeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.saltSourceType).matches }
    }

  /** Traverse to nodes where the saltSourceType matches at least one of the regular expressions in `values`
    */
  def saltSourceType(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.saltSourceType).matches } }
  }

  /** Traverse to nodes where saltSourceType matches `value` exactly.
    */
  def saltSourceTypeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 70, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.saltSourceType == value }
  }

  /** Traverse to nodes where saltSourceType matches one of the elements in `values` exactly.
    */
  def saltSourceTypeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return saltSourceTypeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 70, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.saltSourceType) }
    }
  }

  /** Traverse to nodes where saltSourceType does not match the regular expression `value`.
    */
  def saltSourceTypeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.saltSourceType != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.saltSourceType).matches }
    }
  }

  /** Traverse to nodes where saltSourceType does not match any of the regular expressions in `values`.
    */
  def saltSourceTypeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.saltSourceType).matches }.isEmpty }
  }

  /** Traverse to saltUsed property */
  def saltUsed: Iterator[Boolean] =
    traversal.flatMap(_.saltUsed)

  /** Traverse to nodes where the saltUsed equals the given `value`
    */
  def saltUsed(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.saltUsed.isDefined && node.saltUsed.get == value }

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

  /** Traverse to source property */
  def source: Iterator[String] =
    traversal.map(_.source)

  /** Traverse to nodes where the source matches the regular expression `value`
    */
  def source(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      sourceExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.source).matches }
    }

  /** Traverse to nodes where the source matches at least one of the regular expressions in `values`
    */
  def source(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.source).matches } }
  }

  /** Traverse to nodes where source matches `value` exactly.
    */
  def sourceExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 75, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.source == value }
  }

  /** Traverse to nodes where source matches one of the elements in `values` exactly.
    */
  def sourceExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return sourceExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 75, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.source) }
    }
  }

  /** Traverse to nodes where source does not match the regular expression `value`.
    */
  def sourceNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.source != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.source).matches }
    }
  }

  /** Traverse to nodes where source does not match any of the regular expressions in `values`.
    */
  def sourceNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.source).matches }.isEmpty }
  }

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

  /** Traverse to tagLength property */
  def tagLength: Iterator[Int] =
    traversal.map(_.tagLength)

  /** Traverse to nodes where the tagLength equals the given `value`
    */
  def tagLength(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength == value }

  /** Traverse to nodes where the tagLength equals at least one of the given `values`
    */
  def tagLength(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => vset.contains(node.tagLength) }
  }

  /** Traverse to nodes where the tagLength is not equal to the given `value`
    */
  def tagLengthNot(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength != value }

  /** Traverse to nodes where the tagLength is not equal to any of the given `values`
    */
  def tagLengthNot(values: Int*): Iterator[NodeType] = {
    val vset = values.toSet
    traversal.filter { node => !vset.contains(node.tagLength) }
  }

  /** Traverse to nodes where the tagLength is greater than the given `value`
    */
  def tagLengthGt(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength > value }

  /** Traverse to nodes where the tagLength is greater than or equal the given `value`
    */
  def tagLengthGte(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength >= value }

  /** Traverse to nodes where the tagLength is less than the given `value`
    */
  def tagLengthLt(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength < value }

  /** Traverse to nodes where the tagLength is less than or equal the given `value`
    */
  def tagLengthLte(value: Int): Iterator[NodeType] =
    traversal.filter { _.tagLength <= value }

  /** Traverse to transportScheme property */
  def transportScheme: Iterator[String] =
    traversal.map(_.transportScheme)

  /** Traverse to nodes where the transportScheme matches the regular expression `value`
    */
  def transportScheme(pattern: String): Iterator[NodeType] =
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      transportSchemeExact(pattern)
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filter { item => matcher.reset(item.transportScheme).matches }
    }

  /** Traverse to nodes where the transportScheme matches at least one of the regular expressions in `values`
    */
  def transportScheme(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.exists { _.reset(item.transportScheme).matches } }
  }

  /** Traverse to nodes where transportScheme matches `value` exactly.
    */
  def transportSchemeExact(value: String): Iterator[NodeType] = traversal match {
    case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
      val someNode = init.next
      flatgraph.Accessors
        .getWithInverseIndex(someNode.graph, someNode.nodeKind, 79, value)
        .asInstanceOf[Iterator[NodeType]]
    case _ => traversal.filter { _.transportScheme == value }
  }

  /** Traverse to nodes where transportScheme matches one of the elements in `values` exactly.
    */
  def transportSchemeExact(values: String*): Iterator[NodeType] = {
    if (values.length == 1) return transportSchemeExact(values.head)
    traversal match {
      case init: flatgraph.misc.InitNodeIterator[flatgraph.GNode @unchecked] if init.isVirgin && init.hasNext =>
        val someNode = init.next
        values.iterator.flatMap { value =>
          flatgraph.Accessors
            .getWithInverseIndex(someNode.graph, someNode.nodeKind, 79, value)
            .asInstanceOf[Iterator[NodeType]]
        }
      case _ =>
        val valueSet = values.toSet
        traversal.filter { item => valueSet.contains(item.transportScheme) }
    }
  }

  /** Traverse to nodes where transportScheme does not match the regular expression `value`.
    */
  def transportSchemeNot(pattern: String): Iterator[NodeType] = {
    if (!flatgraph.misc.Regex.isRegex(pattern)) {
      traversal.filter { node => node.transportScheme != pattern }
    } else {
      val matcher = flatgraph.misc.Regex.multilineMatcher(pattern)
      traversal.filterNot { item => matcher.reset(item.transportScheme).matches }
    }
  }

  /** Traverse to nodes where transportScheme does not match any of the regular expressions in `values`.
    */
  def transportSchemeNot(patterns: String*): Iterator[NodeType] = {
    val matchers = patterns.map(flatgraph.misc.Regex.multilineMatcher)
    traversal.filter { item => matchers.find { _.reset(item.transportScheme).matches }.isEmpty }
  }

  /** Traverse to usedForCrypto property */
  def usedForCrypto: Iterator[Boolean] =
    traversal.flatMap(_.usedForCrypto)

  /** Traverse to nodes where the usedForCrypto equals the given `value`
    */
  def usedForCrypto(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.usedForCrypto.isDefined && node.usedForCrypto.get == value }

  /** Traverse to verificationEnabled property */
  def verificationEnabled: Iterator[Boolean] =
    traversal.flatMap(_.verificationEnabled)

  /** Traverse to nodes where the verificationEnabled equals the given `value`
    */
  def verificationEnabled(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.verificationEnabled.isDefined && node.verificationEnabled.get == value }

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

  /** Traverse to verified property */
  def verified: Iterator[Boolean] =
    traversal.flatMap(_.verified)

  /** Traverse to nodes where the verified equals the given `value`
    */
  def verified(value: Boolean): Iterator[NodeType] =
    traversal.filter { node => node.verified.isDefined && node.verified.get == value }

}
