package org.codeminers.standalone.report

import org.codeminers.standalone.enrichment.CryptoEnrichmentMetrics

import java.lang.management.ManagementFactory
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.*
import scala.util.Try

final case class PerformanceSnapshot(
  language: String,
  input: String,
  sourceFiles: Long,
  sourceLines: Long,
  sourceBytes: Long,
  standardNodes: Int,
  standardEdges: Int,
  cryptoAwareNodes: Int,
  cryptoAwareEdges: Int,
  serializedBytes: Long,
  peakHeapBytes: Long,
  coreFindings: Int,
  extendedFindings: Int,
  timingsNanos: Vector[(String, Long)],
  enrichment: CryptoEnrichmentMetrics,
  operationTypes: Vector[(String, Int)],
  selectionOperationTypes: Vector[(String, Int)],
  selectionOperationResolutions: Vector[(String, Int)],
  findingRules: Vector[(String, Int)]
)

object PerformanceReporter {
  def timed[A](block: => A): (A, Long) = {
    val started = System.nanoTime()
    val result  = block
    result -> (System.nanoTime() - started)
  }

  def resetPeakHeap(): Unit = ManagementFactory.getMemoryPoolMXBeans.asScala.foreach(bean => Try(bean.resetPeakUsage()))

  def peakHeapBytes: Long = ManagementFactory.getMemoryPoolMXBeans.asScala.iterator
    .filter(_.getType == java.lang.management.MemoryType.HEAP)
    .flatMap(bean => Option(bean.getPeakUsage))
    .map(_.getUsed)
    .sum

  def write(snapshot: PerformanceSnapshot, output: Path): Unit = {
    Option(output.getParent).foreach(Files.createDirectories(_))
    Files.writeString(output, render(snapshot) + System.lineSeparator(), StandardCharsets.UTF_8)
  }

  def render(snapshot: PerformanceSnapshot): String = {
    val timings = snapshot.timingsNanos
      .map { case (name, nanos) => s"${quote(name)}:${decimalMillis(nanos)}" }
      .mkString("{", ",", "}")
    val enrichment = Vector(
      "callsScanned"           -> snapshot.enrichment.callsScanned.toString,
      "callsMatched"           -> snapshot.enrichment.callsMatched.toString,
      "localOperationsMatched" -> snapshot.enrichment.localOperationsMatched.toString,
      "operationGroups"        -> snapshot.enrichment.operationGroups.toString,
      "sensitiveBindings"      -> snapshot.enrichment.sensitiveBindings.toString,
      "resolvedSourceNodes"    -> snapshot.enrichment.resolvedSourceNodes.toString,
      "adapterMillis"          -> decimalMillis(snapshot.enrichment.adapterNanos),
      "provenanceMillis"       -> decimalMillis(snapshot.enrichment.provenanceNanos),
      "provenanceFlowQueries"  -> snapshot.enrichment.provenanceFlowQueries.toString,
      "provenanceMultiSinkQueries" -> snapshot.enrichment.provenanceMultiSinkQueries.toString,
      "lifecycleMillis"        -> decimalMillis(snapshot.enrichment.lifecycleNanos),
      "contextAnalysisMillis"  -> decimalMillis(snapshot.enrichment.contextAnalysisNanos),
      "materializationMillis"  -> decimalMillis(snapshot.enrichment.materializationNanos),
      "runMillis"              -> decimalMillis(snapshot.enrichment.totalNanos)
    ).map((key, value) => s"${quote(key)}:$value").mkString("{", ",", "}")
    val operationTypes              = integerObject(snapshot.operationTypes)
    val selectionOperationTypes     = integerObject(snapshot.selectionOperationTypes)
    val selectionOperationResolutions = integerObject(snapshot.selectionOperationResolutions)
    val findingRules                = integerObject(snapshot.findingRules)
    val standardNanos = snapshot.timingsNanos.collect {
      case (name, nanos) if Set("cpgFrontend", "overlays", "dataflow").contains(name) => nanos
    }.sum
    val enrichmentWall = snapshot.timingsNanos.find(_._1 == "enrichmentWall").map(_._2).getOrElse(0L)
    val overhead       = if (standardNanos == 0L) 0.0 else enrichmentWall.toDouble * 100.0 / standardNanos.toDouble

    s"""{
       |  "schemaVersion":1,
       |  "language":${quote(snapshot.language)},
       |  "input":${quote(snapshot.input)},
       |  "source":{"files":${snapshot.sourceFiles},"lines":${snapshot.sourceLines},"bytes":${snapshot.sourceBytes}},
       |  "graph":{"standardNodes":${snapshot.standardNodes},"standardEdges":${snapshot.standardEdges},"cryptoAwareNodes":${snapshot.cryptoAwareNodes},"cryptoAwareEdges":${snapshot.cryptoAwareEdges},"addedNodes":${snapshot.cryptoAwareNodes - snapshot.standardNodes},"addedEdges":${snapshot.cryptoAwareEdges - snapshot.standardEdges},"serializedBytes":${snapshot.serializedBytes}},
       |  "memory":{"peakHeapBytes":${snapshot.peakHeapBytes}},
       |  "findings":{"core":${snapshot.coreFindings},"extended":${snapshot.extendedFindings},"byRule":$findingRules},
       |  "operations":$operationTypes,
       |  "selectionOperations":$selectionOperationTypes,
       |  "selectionOperationResolutions":$selectionOperationResolutions,
       |  "timingsMillis":$timings,
       |  "enrichment":$enrichment,
       |  "enrichmentOverheadPercent":${f"$overhead%.3f"}
       |}""".stripMargin
  }

  private def integerObject(entries: Vector[(String, Int)]): String =
    entries.map((key, value) => s"${quote(key)}:$value").mkString("{", ",", "}")

  private def decimalMillis(nanos: Long): String = f"${nanos.toDouble / 1000000.0}%.3f"

  private def quote(value: String): String = {
    val escaped = value.flatMap {
      case '"'       => "\\\""
      case '\\'      => "\\\\"
      case '\n'      => "\\n"
      case '\r'      => "\\r"
      case '\t'      => "\\t"
      case character => character.toString
    }
    s"\"$escaped\""
  }
}
