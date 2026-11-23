package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.nodes.{Expression, Method, StoredNode}
import io.shiftleft.semanticcpg.language.*

/** Value tracking used by the analysis; the evaluation harness selects it with `EvaluationMain --ablation=...`. */
enum ValueTracking {
  /** The full CA-CPG provenance: across methods, fields, parameters, return values and proven branches. */
  case Full

  /** Only definitions inside the method that uses the value, which the standard CPG's reaching definitions already
    * provide. Nothing crosses a method boundary (no caller arguments, fields, return values or constants declared
    * elsewhere) and no branch is proven taken. */
  case Local

  /** Diagnostic: a value is known only when it is written out at the call (literals and operations on literals). */
  case None
}

/** Ablation switch for the evaluation harness; the default is the full analysis. CryptoOperation nodes, their
  * lifecycle grouping, the ontology's security properties and the API checks stay the same in every mode. The setting
  * is per thread (inherited by threads it starts), so test suites running in parallel cannot see another's mode.
  */
object AnalysisAblation {
  private val current = new InheritableThreadLocal[ValueTracking] {
    override def initialValue(): ValueTracking = ValueTracking.Full
  }

  def valueTracking: ValueTracking = current.get

  /** Selects the mode for the rest of this thread's work (the evaluation harness analyses one input per process). */
  def select(mode: ValueTracking): Unit = current.set(mode)

  /** Runs `body` under `mode` and restores the previous mode afterwards. */
  def withValueTracking[T](mode: ValueTracking)(body: => T): T = {
    val previous = current.get
    current.set(mode)
    try body finally current.set(previous)
  }

  def full: Boolean = valueTracking == ValueTracking.Full

  /** True when `expression` may be evaluated at all: with any tracking, or when it refers to no variable. */
  def evaluable(expression: Expression): Boolean = valueTracking != ValueTracking.None || literalOnly(expression)

  /** The expression refers to no variable, field or parameter: its value is written out where it is used. */
  def literalOnly(expression: Expression): Boolean =
    expression.ast.isIdentifier.isEmpty && expression.ast.isFieldIdentifier.isEmpty

  /** True when `node` may serve as the value of something used in `use`'s method under the current mode. */
  def reachable(node: StoredNode, use: Method): Boolean = full || (valueTracking == ValueTracking.Local && (node match {
    case expression: Expression => expression.method.id == use.id
    case _                      => false
  }))
}
