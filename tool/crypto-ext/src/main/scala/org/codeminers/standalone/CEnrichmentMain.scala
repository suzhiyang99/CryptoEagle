package org.codeminers.standalone

import io.joern.c2cpg.{C2Cpg, Config}
import io.joern.dataflowengineoss.layers.dataflows.{OssDataFlow, OssDataFlowOptions}
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.joern.x2cpg.X2Cpg.applyDefaultOverlays
import io.shiftleft.semanticcpg.language.*
import io.shiftleft.semanticcpg.layers.LayerCreatorContext
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.enrichment.CryptoEnrichmentPass
import org.codeminers.standalone.report.FindingCli
import org.codeminers.standalone.rules.CryptoQLRules

import java.nio.file.Files
import scala.util.{Failure, Success}

/** Runs the shared ontology/enrichment/rule pipeline for both C and C++ inputs. */
object CEnrichmentMain {
  def main(args: Array[String]): Unit = {
    val directory = args
      .find(!_.startsWith("--"))
      .getOrElse(throw new IllegalArgumentException("Usage: CEnrichmentMain <source-directory> [options]"))
    val summaryOnly = args.contains("--summary")
    val showCalls   = args.contains("--calls")
    val configs = CryptoEnrichmentPass.defaultConfigs.fold(
      errors => throw new IllegalArgumentException(errors.mkString("\n")),
      identity
    )
    val outputPath = Files.createTempFile("crypto-aware-c-cpp-", ".bin")
    Files.deleteIfExists(outputPath)

    try {
      new C2Cpg().createCpg(Config().withInputPath(directory).withOutputPath(outputPath.toString)) match {
        case Failure(error) => throw error
        case Success(cpg) =>
          try {
            applyDefaultOverlays(cpg)
            new OssDataFlow(new OssDataFlowOptions()).run(new LayerCreatorContext(cpg))
            implicit val engineContext: EngineContext = EngineContext()
            if (showCalls) {
              cpg.call.foreach { call =>
                val arguments = call.argument.l
                  .sortBy(_.argumentIndex)
                  .map(argument => s"${argument.argumentIndex}:${argument.code}")
                  .mkString("[", ", ", "]")
                println(
                  s"call line=${call.lineNumber.getOrElse(-1)} name=${call.name} fullName=${call.methodFullName} " +
                    s"code=${call.code} arguments=$arguments"
                )
              }
              cpg.local.foreach(local =>
                println(
                  s"local line=${local.lineNumber.getOrElse(-1)} name=${local.name} " +
                    s"type=${local.typeFullName} code=${local.code}"
                )
              )
            }
            new CryptoEnrichmentPass(cpg, AdapterLanguage.Cpp, configs).createAndApply()

            val operations = cpg.cryptoOperation.l
            val core       = CryptoQLRules.run(cpg)
            val findings   = core
            println(s"calls=${cpg.call.size}")
            println(s"cryptoOperations=${operations.size}")
            println(s"realizesCryptoEdges=${operations.map(_.realizingCall.size).sum}")
            println(s"cryptoFindings=${core.size}")
            operations.groupBy(_.opType).toSeq.sortBy(_._1).foreach { case (operationType, matches) =>
              println(s"operationType[$operationType]=${matches.size}")
            }
            findings.groupBy(_.ruleId).toSeq.sortBy(_._1).foreach { case (ruleId, matches) =>
              println(s"findingRule[$ruleId]=${matches.size}")
            }
            FindingCli.emit(args, findings)

            if (!summaryOnly) operations.take(100).foreach { operation =>
              println(
                s"op file=${operation.srcFile} line=${operation.lineNo} type=${operation.opType} " +
                  s"operation=${operation.operation} algorithm=${operation.algorithm} mode=${operation.mode} " +
                  s"resolution=${operation.resolution} keySource=${operation.keySourceType} " +
                  s"ivSource=${operation.ivSourceType} deprecated=${operation.isDeprecated}"
              )
            }
          } finally cpg.close()
      }
    } finally Files.deleteIfExists(outputPath)
  }
}
