package org.codeminers.standalone

import io.joern.dataflowengineoss.layers.dataflows.{OssDataFlow, OssDataFlowOptions}
import io.joern.dataflowengineoss.queryengine.{EngineConfig, EngineContext}
import io.joern.x2cpg.X2Cpg.applyDefaultOverlays
import io.shiftleft.semanticcpg.language.*
import io.shiftleft.semanticcpg.layers.LayerCreatorContext
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.enrichment.CryptoEnrichmentPass
import org.codeminers.standalone.report.{FindingCli, PerformanceReporter}
import org.codeminers.standalone.rules.CryptoQLRules

import java.nio.file.{Files, Path}
import scala.util.{Failure, Success}

/** Diagnostic entry point for exercising the paper-aligned enrichment pipeline against real Java projects without
  * invoking any legacy detection pass.
  */
object JavaEnrichmentMain {
  def main(args: Array[String]): Unit = {
    val directory = args
      .find(!_.startsWith("--"))
      .getOrElse(throw new IllegalArgumentException("Usage: JavaEnrichmentMain <source-directory> [options]"))
    val summaryOnly = args.contains("--summary")
    val printCalls  = args.contains("--calls")
    val disableDelombok = args.contains("--no-delombok")
    val reportLegacyHmacSha1 = args.contains("--flag-legacy-hmac-sha1")
    val flowCallDepth = args.find(_.startsWith("--max-flow-call-depth="))
      .map(_.stripPrefix("--max-flow-call-depth=").toInt).getOrElse(2)
    require(flowCallDepth >= 0, "max-flow-call-depth must be nonnegative")
    val configs = CryptoEnrichmentPass.defaultConfigs.fold(
      errors => throw new IllegalArgumentException(errors.mkString("\n")),
      identity
    )
    val outputPath = Files.createTempFile("crypto-aware-java-", ".bin")
    Files.deleteIfExists(outputPath)
    PerformanceReporter.resetPeakHeap()
    val runStarted = System.nanoTime()

    try {
      JavaFrontend.createCpg(Path.of(directory), outputPath, disableDelombok) match {
        case Failure(error) => throw error
        case Success(cpg) =>
          try {
            applyDefaultOverlays(cpg)
            new OssDataFlow(new OssDataFlowOptions()).run(new LayerCreatorContext(cpg))
            if (printCalls) {
              cpg.call
                .filter(call =>
                  Set("getInstance", "init", "update", "doFinal", "encryptor", "decryptor", "finalize")
                    .contains(call.name) ||
                    call.methodFullName.matches(".*(?:SecretKeySpec|IvParameterSpec|GCMParameterSpec|SecureRandom).*")
                )
                .foreach { call =>
                  val arguments = call.argument.l
                    .sortBy(_.argumentIndex)
                    .map(argument => s"${argument.argumentIndex}:${argument.label}:${argument.code}")
                    .mkString("[", ", ", "]")
                  val enclosingCalls = call.inAst.isCall.l
                    .filterNot(_.id == call.id)
                    .map(parent => s"${parent.name}:${parent.code}")
                    .mkString("[", ", ", "]")
                  println(
                    s"call id=${call.id} method=${call.method.name} line=${call.lineNumber.getOrElse(-1)} " +
                      s"name=${call.name} fullName=${call.methodFullName} code=${call.code} " +
                      s"arguments=$arguments enclosingCalls=$enclosingCalls"
                  )
                }
            }
            implicit val engineContext: EngineContext = EngineContext(config = EngineConfig(maxCallDepth = flowCallDepth))
            println(s"maxFlowCallDepth=$flowCallDepth")
            val enrichment = new CryptoEnrichmentPass(cpg, AdapterLanguage.Java, configs, Some(Path.of(directory)))
            enrichment.createAndApply(reportLegacyHmacSha1)
            val metrics = enrichment.metrics
            println(s"enrichmentProvenanceMillis=${metrics.provenanceNanos / 1000000L}")
            println(s"enrichmentLifecycleMillis=${metrics.lifecycleNanos / 1000000L}")
            println(s"enrichmentMillis=${metrics.totalNanos / 1000000L}")

            val operations = cpg.cryptoOperation.l
            println(s"calls=${cpg.call.size}")
            println(s"parsedJavaFiles=${cpg.file.name.l.count(_.endsWith(".java"))}")
            println(s"cryptoOperations=${operations.size}")
            println(s"realizesCryptoEdges=${operations.map(_.realizingCall.size).sum}")
            println(s"usesKeyFromEdges=${operations.map(_.keySourceNode.size).sum}")
            println(s"usesIvFromEdges=${operations.map(_.ivSourceNode.size).sum}")
            println(s"usesSaltFromEdges=${operations.map(_.saltSourceNode.size).sum}")
            println(s"usesNonceFromEdges=${operations.map(_.nonceSourceNode.size).sum}")
            println(s"unknownKeyOrigins=${operations.count(operation => operation.keySourceNode.nonEmpty && operation.keySourceType == "UNKNOWN")}")
            println(s"unknownIvOrigins=${operations.count(operation => operation.ivSourceNode.nonEmpty && operation.ivSourceType == "UNKNOWN")}")
            operations.groupBy(_.opType).toSeq.sortBy(_._1).foreach { case (operationType, matches) =>
              println(s"operationType[$operationType]=${matches.size}")
            }
            val coreFindings     = CryptoQLRules.run(cpg, Some(Path.of(directory)))
            val findings         = coreFindings
            println(s"cryptoFindings=${coreFindings.size}")
            findings.groupBy(_.ruleId).toSeq.sortBy(_._1).foreach { case (ruleId, matches) =>
              println(s"findingRule[$ruleId]=${matches.size}")
            }
            FindingCli.emit(args, findings)
            println(s"analysisMillis=${(System.nanoTime() - runStarted) / 1000000L}")
            println(s"peakHeapBytes=${PerformanceReporter.peakHeapBytes}")
            if (!summaryOnly) operations.take(100).foreach { operation =>
              def sourceCodes(nodes: Iterator[io.shiftleft.codepropertygraph.generated.nodes.StoredNode]): String =
                nodes
                  .map {
                    case node: io.shiftleft.codepropertygraph.generated.nodes.AstNode => node.code
                    case node                                                         => node.label
                  }
                  .mkString("[", ", ", "]")
              println(
                s"op file=${operation.srcFile} line=${operation.lineNo} type=${operation.opType} " +
                  s"operation=${operation.operation} algorithm=${operation.algorithm} mode=${operation.mode} " +
                  s"resolution=${operation.resolution} deprecated=${operation.isDeprecated} integrity=${operation.providesIntegrity} " +
                  s"uniqueNonce=${operation.requiresUniqueNonce} csprngRequired=${operation.requiresCsprngSource} " +
                  s"keySource=${operation.keySourceType} ivSource=${operation.ivSourceType} " +
                  s"saltSource=${operation.saltSourceType} nonceSource=${operation.nonceSourceType} " +
                  s"keyNodes=${sourceCodes(operation.keySourceNode)} " +
                  s"ivNodes=${sourceCodes(operation.ivSourceNode)} " +
                  s"saltNodes=${sourceCodes(operation.saltSourceNode)} " +
                  s"nonceNodes=${sourceCodes(operation.nonceSourceNode)}"
              )
            }
          } finally cpg.close()
      }
    } finally Files.deleteIfExists(outputPath)
  }
}
