package org.codeminers.standalone.enrichment

/** Internal timings from one CryptoEnrichmentPass execution.
  *
  * Values are wall-clock nanoseconds. Adapter and provenance time are measured around the corresponding work only;
  * lifecycle and materialization are disjoint phases. `totalNanos` covers the pass `run` method, while callers may
  * additionally measure diff-graph commit time around `createAndApply`.
  */
final case class CryptoEnrichmentMetrics(
  callsScanned: Int,
  callsMatched: Int,
  localOperationsMatched: Int,
  operationGroups: Int,
  sensitiveBindings: Int,
  resolvedSourceNodes: Int,
  adapterNanos: Long,
  provenanceNanos: Long,
  lifecycleNanos: Long,
  materializationNanos: Long,
  totalNanos: Long,
  contextAnalysisNanos: Long = 0L,
  provenanceFlowQueries: Int = 0,
  provenanceMultiSinkQueries: Int = 0
)

object CryptoEnrichmentMetrics {
  val Empty: CryptoEnrichmentMetrics = CryptoEnrichmentMetrics(0, 0, 0, 0, 0, 0, 0L, 0L, 0L, 0L, 0L)
}
