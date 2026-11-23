package org.codeminers.standalone

import io.joern.c2cpg.{C2Cpg, Config as CConfig}
import io.joern.dataflowengineoss.layers.dataflows.{OssDataFlow, OssDataFlowOptions}
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.joern.pysrc2cpg.{Py2CpgOnFileSystem, Py2CpgOnFileSystemConfig}
import io.joern.x2cpg.X2Cpg.applyDefaultOverlays
import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.semanticcpg.language.*
import io.shiftleft.semanticcpg.layers.LayerCreatorContext
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.enrichment.{AnalysisAblation, CryptoEnrichmentPass, ValueTracking}
import org.codeminers.standalone.report.{FindingCli, PerformanceReporter, PerformanceSnapshot}
import org.codeminers.standalone.rules.{CryptoQLRules, StringMatchBaseline}

import java.nio.file.{Files, Path}
import scala.jdk.StreamConverters.*
import scala.util.{Failure, Success, Try}

/** Reproducible benchmark harness for phase-level timing, graph-size measurement, and machine-readable findings. */
object EvaluationMain {
  def main(args: Array[String]): Unit = {
    require(args.length >= 2, usage)
    val language = parseLanguage(args(0)).fold(error => throw new IllegalArgumentException(error), identity)
    val input    = Path.of(args(1)).toAbsolutePath.normalize
    require(Files.exists(input), s"Input does not exist: $input")
    val metricsOutput = args
      .collectFirst {
        case argument if argument.startsWith("--metrics-output=") =>
          Path.of(argument.stripPrefix("--metrics-output=")).toAbsolutePath.normalize
      }
      .getOrElse(throw new IllegalArgumentException("--metrics-output=<path> is required"))
    // Ablation variants: full (default); no-provenance (same-method definitions only); no-value-tracking (diagnostic:
    // only values written at the call); string-match (standard CPG, no enrichment, string patterns).
    val ablation = args.collectFirst { case a if a.startsWith("--ablation=") => a.stripPrefix("--ablation=") }.getOrElse("full")
    val reportLegacyHmacSha1 = args.contains("--flag-legacy-hmac-sha1")
    require(Set("full", "no-provenance", "no-value-tracking", "string-match").contains(ablation), s"Unknown ablation '$ablation'")
    require(ablation == "full" || language == AdapterLanguage.Java, "Ablation variants are implemented for Java only")
    require(!reportLegacyHmacSha1 || language == AdapterLanguage.Java, "--flag-legacy-hmac-sha1 is supported for Java only")
    AnalysisAblation.select(ablation match {
      case "no-provenance"     => ValueTracking.Local
      case "no-value-tracking" => ValueTracking.None
      case _                   => ValueTracking.Full
    })

    val totalStarted = System.nanoTime()
    PerformanceReporter.resetPeakHeap()
    val (sourceFiles, sourceLines, sourceBytes) = sourceStats(input)
    val (configsEither, adapterConfigNanos)     = PerformanceReporter.timed(CryptoEnrichmentPass.defaultConfigs)
    val configs    = configsEither.fold(errors => throw new IllegalArgumentException(errors.mkString("\n")), identity)
    val outputPath = Files.createTempFile("crypto-aware-evaluation-", ".bin")
    Files.deleteIfExists(outputPath)

    var snapshot: Option[PerformanceSnapshot] = None
    try {
      val (created, frontendNanos) = PerformanceReporter.timed(createCpg(language, input, outputPath))
      created match {
        case Failure(error) => throw error
        case Success(cpg) =>
          try {
            val (_, overlaysNanos) = PerformanceReporter.timed(applyDefaultOverlays(cpg))
            val (_, dataflowNanos) = PerformanceReporter.timed {
              new OssDataFlow(new OssDataFlowOptions()).run(new LayerCreatorContext(cpg))
            }
            val standardNodes                         = cpg.graph.nodeCount
            val standardEdges                         = cpg.graph.edgeCount
            implicit val engineContext: EngineContext = EngineContext()
            val pass                                  = new CryptoEnrichmentPass(cpg, language, configs,
              Option.when(language == AdapterLanguage.Java)(input))
            val (_, enrichmentWallNanos)              = PerformanceReporter.timed(
              if (ablation != "string-match") pass.createAndApply(reportLegacyHmacSha1))
            val cryptoAwareNodes                      = cpg.graph.nodeCount
            val cryptoAwareEdges                      = cpg.graph.edgeCount
            val (core, coreRuleNanos)                 = PerformanceReporter.timed(
              if (ablation == "string-match") StringMatchBaseline.run(cpg, reportLegacyHmacSha1)
              else CryptoQLRules.run(cpg, Option.when(language == AdapterLanguage.Java)(input)))
            val findings                              = core
            val operations                            = cpg.cryptoOperation.l
            val selectionOperations = operations.filter(operation =>
              operation.opType != "RANDOMNESS" || operation.usedForCrypto.contains(true)
            )
            FindingCli.emit(args, findings)
            snapshot = Some(
              PerformanceSnapshot(
                language.storedValue,
                input.toString,
                sourceFiles,
                sourceLines,
                sourceBytes,
                standardNodes,
                standardEdges,
                cryptoAwareNodes,
                cryptoAwareEdges,
                serializedBytes = 0L,
                peakHeapBytes = 0L,
                core.size,
                0,
                Vector(
                  "adapterConfigLoad" -> adapterConfigNanos,
                  "cpgFrontend"       -> frontendNanos,
                  "overlays"          -> overlaysNanos,
                  "dataflow"          -> dataflowNanos,
                  "enrichmentWall"    -> enrichmentWallNanos,
                  "coreRules"         -> coreRuleNanos,
                  "extendedRules"     -> 0L
                ),
                pass.metrics,
                operations.groupBy(_.opType).toVector.map((key, values) => key -> values.size).sortBy(_._1),
                selectionOperations.groupBy(_.opType).toVector.map((key, values) => key -> values.size).sortBy(_._1),
                selectionOperations
                  .groupBy(_.resolution)
                  .toVector
                  .map((key, values) => key -> values.size)
                  .sortBy(_._1),
                findings.groupBy(_.ruleId).toVector.map((key, values) => key -> values.size).sortBy(_._1)
              )
            )
          } finally cpg.close()
      }
      val serializedBytes = Option.when(Files.exists(outputPath))(Files.size(outputPath)).getOrElse(0L)
      val initial         = snapshot.getOrElse(throw new IllegalStateException("Evaluation did not produce metrics"))
      val completed = initial.copy(
        serializedBytes = serializedBytes,
        peakHeapBytes = PerformanceReporter.peakHeapBytes,
        timingsNanos = initial.timingsNanos :+ ("totalWall" -> (System.nanoTime() - totalStarted))
      )
      PerformanceReporter.write(completed, metricsOutput)
      println(s"metricsReport=$metricsOutput")
      println(s"enrichmentOverheadPercent=${overhead(completed)}")
    } finally Files.deleteIfExists(outputPath)
  }

  private def createCpg(language: AdapterLanguage, input: Path, output: Path): Try[Cpg] = language match {
    case AdapterLanguage.Java =>
      JavaFrontend.createCpg(input, output)
    case AdapterLanguage.Python =>
      new Py2CpgOnFileSystem().createCpg(
        Py2CpgOnFileSystemConfig().withInputPath(input.toString).withOutputPath(output.toString).withDisableFileContent(false)
      )
    case AdapterLanguage.Cpp =>
      new C2Cpg().createCpg(CConfig().withInputPath(input.toString).withOutputPath(output.toString))
  }

  private def parseLanguage(value: String): Either[String, AdapterLanguage] = value.trim.toLowerCase match {
    case "java"              => Right(AdapterLanguage.Java)
    case "python" | "py"     => Right(AdapterLanguage.Python)
    case "c" | "cpp" | "c++" => Right(AdapterLanguage.Cpp)
    case other               => Left(s"Unsupported language '$other'. Expected java, python, or cpp.")
  }

  private def sourceStats(input: Path): (Long, Long, Long) = {
    val extensions = Set(".java", ".py", ".c", ".cc", ".cpp", ".cxx", ".h", ".hpp")
    val paths =
      if (Files.isRegularFile(input)) Vector(input)
      else {
        val stream = Files.walk(input)
        try stream.toScala(Vector).filter(Files.isRegularFile(_))
        finally stream.close()
      }
    val sources = paths.filter(path => extensions.exists(path.getFileName.toString.toLowerCase.endsWith))
    // Source corpora can contain valid Python 2 files whose declared encoding is
    // not UTF-8. Line counting is encoding-independent, so count newline bytes
    // instead of decoding the file with the platform's default UTF-8 decoder.
    val lines = sources.iterator.map(countLines).sum
    (sources.size.toLong, lines, sources.iterator.map(Files.size(_)).sum)
  }

  private def countLines(path: Path): Long = {
    val stream = Files.newInputStream(path)
    try {
      val buffer = new Array[Byte](8192)
      var lines  = 0L
      var total  = 0L
      var last   = -1
      var read   = stream.read(buffer)
      while (read >= 0) {
        var index = 0
        while (index < read) {
          if (buffer(index) == '\n'.toByte) lines += 1
          last = buffer(index) & 0xff
          index += 1
        }
        total += read
        read = stream.read(buffer)
      }
      if (total > 0 && last != '\n') lines + 1 else lines
    } finally stream.close()
  }

  private def overhead(snapshot: PerformanceSnapshot): String = {
    val base = snapshot.timingsNanos.collect {
      case (name, nanos) if Set("cpgFrontend", "overlays", "dataflow").contains(name) => nanos
    }.sum
    val enrichment = snapshot.timingsNanos.find(_._1 == "enrichmentWall").map(_._2).getOrElse(0L)
    if (base == 0L) "0.000" else f"${enrichment.toDouble * 100.0 / base.toDouble}%.3f"
  }

  private val usage =
    "Usage: EvaluationMain <java|python|cpp> <input> --metrics-output=<json> [--report-output=<json|csv|txt>] " +
      "[--ablation=full|no-provenance|no-value-tracking|string-match] [--flag-legacy-hmac-sha1]"
}
