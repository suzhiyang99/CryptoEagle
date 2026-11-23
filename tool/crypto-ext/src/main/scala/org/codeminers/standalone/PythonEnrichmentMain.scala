package org.codeminers.standalone

import io.joern.dataflowengineoss.layers.dataflows.{OssDataFlow, OssDataFlowOptions}
import io.joern.dataflowengineoss.language.*
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.joern.pysrc2cpg.{Py2CpgOnFileSystem, Py2CpgOnFileSystemConfig}
import io.joern.x2cpg.X2Cpg.applyDefaultOverlays
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, StoredNode}
import io.shiftleft.semanticcpg.language.*
import io.shiftleft.semanticcpg.layers.LayerCreatorContext
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.enrichment.CryptoEnrichmentPass
import org.codeminers.standalone.report.FindingCli
import org.codeminers.standalone.rules.CryptoQLRules

import java.nio.file.Files
import scala.util.{Failure, Success}

/** Runs only the paper-aligned enrichment pipeline against a Python source tree. */
object PythonEnrichmentMain {
  def main(args: Array[String]): Unit = {
    val directory = args
      .find(!_.startsWith("--"))
      .getOrElse(throw new IllegalArgumentException("Usage: PythonEnrichmentMain <source-directory> [options]"))
    val summaryOnly = args.contains("--summary")
    val showCalls   = args.contains("--calls")
    val configs = CryptoEnrichmentPass.defaultConfigs.fold(
      errors => throw new IllegalArgumentException(errors.mkString("\n")),
      identity
    )
    val outputPath = Files.createTempFile("crypto-aware-python-", ".bin")
    Files.deleteIfExists(outputPath)

    try {
      new Py2CpgOnFileSystem().createCpg(
        Py2CpgOnFileSystemConfig().withInputPath(directory).withOutputPath(outputPath.toString).withDisableFileContent(false)
      ) match {
        case Failure(error) => throw error
        case Success(cpg) =>
          try {
            applyDefaultOverlays(cpg)
            new OssDataFlow(new OssDataFlowOptions()).run(new LayerCreatorContext(cpg))
            implicit val engineContext: EngineContext = EngineContext()

            if (showCalls)
              cpg.call
                .filter(call =>
                  call.code.contains("AES.new") || call.code.contains("Cipher(") || call.code.contains("pbkdf2_hmac") ||
                    call.code
                      .contains("MD5.new") || Set("argument", "x", "encryptor", "decryptor", "update", "finalize")
                      .contains(call.name)
                )
                .foreach { call =>
                  val arguments = call.argument.l
                    .map { argument =>
                      s"${argument.argumentIndex}:${argument.argumentName.getOrElse("-")}:${argument.label}:${argument.code}"
                    }
                    .mkString(" | ")
                  val enclosingCalls = call.inAst.isCall.l
                    .filterNot(_.id == call.id)
                    .map(parent => s"${parent.name}:${parent.code}")
                    .mkString("[", ", ", "]")
                  println(
                    s"call method=${call.method.name} line=${call.lineNumber.getOrElse(-1)} name=${call.name} " +
                      s"code=${call.code} args=[$arguments] enclosingCalls=$enclosingCalls"
                  )
                  val functionSources = cpg.call
                    .filter(source =>
                      source.code.matches("(?i).*(?:MD2|MD4|MD5|SHA|SHA1|SHA224|SHA256|SHA384|SHA512)\\.new.*")
                    )
                  val flows = Iterator
                    .single(call)
                    .reachableByFlows(functionSources)
                    .l
                    .map(_.elements.map(_.code).mkString(" -> "))
                  if (flows.nonEmpty) println(s"functionValueFlows=${flows.mkString("[", ", ", "]")}")
                }

            new CryptoEnrichmentPass(cpg, AdapterLanguage.Python, configs).createAndApply()

            val operations = cpg.cryptoOperation.l
            println(s"calls=${cpg.call.size}")
            println(s"cryptoOperations=${operations.size}")
            println(s"realizesCryptoEdges=${operations.map(_.realizingCall.size).sum}")
            println(s"usesKeyFromEdges=${operations.map(_.keySourceNode.size).sum}")
            println(s"usesIvFromEdges=${operations.map(_.ivSourceNode.size).sum}")
            println(s"usesSaltFromEdges=${operations.map(_.saltSourceNode.size).sum}")
            println(s"usesNonceFromEdges=${operations.map(_.nonceSourceNode.size).sum}")
            operations.groupBy(_.opType).toSeq.sortBy(_._1).foreach { case (operationType, matches) =>
              println(s"operationType[$operationType]=${matches.size}")
            }
            val coreFindings     = CryptoQLRules.run(cpg)
            val findings         = coreFindings
            println(s"cryptoFindings=${coreFindings.size}")
            findings.groupBy(_.ruleId).toSeq.sortBy(_._1).foreach { case (ruleId, matches) =>
              println(s"findingRule[$ruleId]=${matches.size}")
            }
            FindingCli.emit(args, findings)
            val rulePattern = "(?i).*rule[_-]([0-9]+).*".r
            operations
              .flatMap(operation =>
                operation.srcFile match {
                  case rulePattern(rule) => Some(rule -> operation)
                  case _                 => None
                }
              )
              .groupBy(_._1)
              .toSeq
              .sortBy(_._1.toInt)
              .foreach { case (rule, matches) =>
                println(s"benchmarkRule[$rule]=${matches.size}")
              }
            if (!summaryOnly) operations.take(100).foreach { operation =>
              def sourceCodes(nodes: Iterator[StoredNode]): String = nodes
                .map {
                  case node: AstNode => s"${node.label}:${node.code}"
                  case node          => node.label
                }
                .mkString("[", ", ", "]")
              println(
                s"op file=${operation.srcFile} line=${operation.lineNo} type=${operation.opType} " +
                  s"operation=${operation.operation} algorithm=${operation.algorithm} mode=${operation.mode} source=${operation.source} " +
                  s"resolution=${operation.resolution} deprecated=${operation.isDeprecated} integrity=${operation.providesIntegrity} " +
                  s"uniqueNonce=${operation.requiresUniqueNonce} csprngRequired=${operation.requiresCsprngSource} " +
                  s"keySource=${operation.keySourceType} ivSource=${operation.ivSourceType} " +
                  s"saltSource=${operation.saltSourceType} nonceSource=${operation.nonceSourceType} " +
                  s"keyNodes=${sourceCodes(operation.keySourceNode)} " +
                  s"ivNodes=${sourceCodes(operation.ivSourceNode)} " +
                  s"saltNodes=${sourceCodes(operation.saltSourceNode)}"
              )
            }
          } finally cpg.close()
      }
    } finally Files.deleteIfExists(outputPath)
  }
}
