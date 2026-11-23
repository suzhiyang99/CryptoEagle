package org.codeminers.standalone

import io.joern.dataflowengineoss.language.*
import io.joern.dataflowengineoss.layers.dataflows.{OssDataFlow, OssDataFlowOptions}
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.joern.x2cpg.X2Cpg.applyDefaultOverlays
import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, CfgNode, StoredNode}
import io.shiftleft.semanticcpg.language.*
import io.shiftleft.semanticcpg.layers.LayerCreatorContext
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.enrichment.CryptoEnrichmentPass

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import scala.util.{Failure, Success}

/** Exports, for every CryptoOperation, the source lines that its realizing calls and their data-flow provenance
  * occupy.
  *
  * This is the slicing counterpart to the detection rules: `ParameterProvenance` already computes bounded flows for
  * sensitive arguments but keeps only each path's terminal node, discarding the intermediate steps. A slice needs the
  * whole path, so the flows are recomputed here over the enriched CPG and every element's line is retained.
  *
  * Sources are restricted to the operation's own file, matching the provenance pass: the Java frontend's DFG does not
  * cross files, so a candidate from elsewhere would only add noise.
  *
  * Usage:
  *   SliceExportMain <source-directory> [--out=<path>]
  */
object SliceExportMain {

  /** Same bound ParameterProvenance uses, so exported paths match the ones detection reasons over. */
  private val MaxPathLength = 64

  def main(args: Array[String]): Unit = {
    val directory = args
      .find(!_.startsWith("--"))
      .getOrElse(throw new IllegalArgumentException("Usage: SliceExportMain <source-directory> [--out=<path>]"))
    val output = args
      .collectFirst { case argument if argument.startsWith("--out=") => argument.drop("--out=".length) }
      .map(Paths.get(_))
      .getOrElse(Paths.get("crypto-slices.json"))

    val randomContext = !args.contains("--random-context=false")
    val verificationContext = !args.contains("--verification-context=false")
    val dependencyContext = !args.contains("--dependency-context=false")
    val effectiveOverwriteContext = !args.contains("--effective-overwrite-context=false")
    // Optional case membership limits cross-file provenance to the files of one benchmark case.
    // Without this option the existing per-file exporter is unchanged.
    val caseGroups = args.collectFirst { case a if a.startsWith("--case-groups=") => a.drop("--case-groups=".length) }
      .map { name =>
        import scala.jdk.CollectionConverters.*
        Files.readAllLines(Paths.get(name)).asScala.filter(_.nonEmpty).map { line =>
          val fields = line.split("\t", -1)
          require(fields.length == 2, s"Expected case-id TAB filename: $line")
          fields(1) -> fields(0)
        }.toMap
      }.getOrElse(Map.empty[String, String])
    def basename(file: String): String = file.replace('\\', '/').split('/').lastOption.getOrElse(file)
    val configs = CryptoEnrichmentPass.defaultConfigs.fold(
      errors => throw new IllegalArgumentException(errors.mkString("\n")),
      identity
    )
    val cpgPath = Files.createTempFile("crypto-slice-export-", ".bin")
    Files.deleteIfExists(cpgPath)

    try {
      JavaFrontend.createCpg(Path.of(directory), cpgPath) match {
        case Failure(error) => throw error
        case Success(cpg) =>
          try {
            applyDefaultOverlays(cpg)
            new OssDataFlow(new OssDataFlowOptions()).run(new LayerCreatorContext(cpg))
            implicit val engineContext: EngineContext = EngineContext()
            new CryptoEnrichmentPass(cpg, AdapterLanguage.Java, configs).createAndApply()

            // Flow candidates, indexed per file once instead of per operation.
            val literalsByFile = cpg.literal.l.groupBy(fileOf).view.mapValues(_.map(n => n: CfgNode)).toMap
            val parametersByFile =
              cpg.method.parameter.l.groupBy(fileOf).view.mapValues(_.map(n => n: CfgNode)).toMap
            val callsByFile = cpg.call.l.groupBy(fileOf).view.mapValues(_.map(n => n: CfgNode)).toMap

            val context = new SliceLineEvidence(cpg)
            val verification = Option.when(verificationContext)(new VerificationSliceContext(cpg))
            val dependencies = Option.when(dependencyContext)(new SliceDependencyContext(cpg, context))
            val operations = cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l
            val primitiveEntries = operations.map { operation =>
              val calls        = operation.realizingCall.l.filter(context.reachable)
              val file         = calls.flatMap(call => Option(fileOf(call))).headOption.getOrElse(operation.srcFile)
              val verificationContexts = verification.toVector.flatMap(_.forCalls(calls, file))
              val verificationLines = verificationContexts.flatMap(_.lines).distinct.sorted
              val callLines    = calls.flatMap(lineOf).distinct.sorted
              val methodLines  = calls.flatMap(call => lineOf(call.method)).distinct.sorted
              val declaredNodes = operation.keySourceNode.l ++ operation.ivSourceNode.l ++
                operation.saltSourceNode.l ++ operation.nonceSourceNode.l
              val declaredLines = declaredNodes.filter(n => caseGroups.isEmpty || fileOf(n) == file)
                .flatMap(lineOf).distinct.sorted

              // The real provenance: every element of every bounded flow that reaches an argument of a realizing
              // call. This is what the detection pass throws away when it keeps only path heads.
              val sinks = calls.flatMap(_.argument.l)
              val caseId = caseGroups.get(basename(file))
              val allowedFiles = if (caseId.isEmpty) Set(file) else
                (literalsByFile.keySet ++ parametersByFile.keySet ++ callsByFile.keySet)
                  .filter(f => caseGroups.get(basename(f)) == caseId).toSet
              val sources = allowedFiles.toList.sorted.flatMap(f => literalsByFile.getOrElse(f, Nil) ++
                parametersByFile.getOrElse(f, Nil) ++ callsByFile.getOrElse(f, Nil))
              val dependencyLines = dependencies.map(_.lines(calls, allowedFiles)).getOrElse(Map.empty[String, Vector[Int]])
              val flows = if (sinks.isEmpty || sources.isEmpty) Nil
              else
                sinks.iterator
                  .reachableByFlows(sources.iterator)
                  .l
                  .filter(_.elements.size <= MaxPathLength)
                  .filter(p => caseGroups.isEmpty || p.elements.collect { case n: AstNode => fileOf(n) }
                    .filter(_.nonEmpty).forall(allowedFiles.contains))
              val rawProvenanceLines = flows
                .flatMap(_.elements)
                .collect { case node: StoredNode => node }
                .filter(n => caseGroups.isEmpty || fileOf(n) == file)
                .flatMap(lineOf)
                .distinct
                .sorted
              val anchors = calls.groupBy(_.method.id).values.map(_.maxBy(context.order)).toVector
              val killed = anchors.flatMap(context.killedLines).toSet
              val inactive = context.inactiveLines(file)
              val excluded = killed ++ inactive
              val provenanceLines = rawProvenanceLines.filterNot(excluded.contains)
              val randomCalls = if (randomContext) anchors.flatMap(context.randomContext).distinct else Vector.empty
              val randomLines = randomCalls.flatMap(lineOf)
              val referenced = calls.flatMap(_.argument.l.flatMap(_.ast.isIdentifier.l))
              val declarationLines = referenced.flatMap(_.refOut.collect { case l: io.shiftleft.codepropertygraph.generated.nodes.Local => l })
                .flatMap(lineOf).filterNot(excluded.contains)
              val apiTypes = (cpg.call.filter(c => fileOf(c) == file &&
                (c.methodFullName.startsWith("java.security.") || c.methodFullName.startsWith("javax.crypto.")))
                .methodFullName.l.map(_.takeWhile(_ != ':').split('.').dropRight(1).mkString(".")) ++
                verificationContexts.flatMap(_.apiTypes)).distinct.sorted

              // The realizing calls and the enclosing signature are structural and always kept; a killed definition
              // is dropped wherever it entered from, provenance or declared sensitive-parameter source.
              val allLines = (callLines ++ methodLines ++
                (declaredLines ++ provenanceLines ++ randomLines ++ declarationLines).filterNot(excluded.contains) ++
                verificationLines ++ dependencyLines.getOrElse(file, Vector.empty)).distinct.sorted
              val overwrites = if (randomContext) anchors.flatMap(a => context.overwrites(a, allLines.toSet)).distinct else Vector.empty
              val foreignNodes = if (caseId.isEmpty) Nil else (flows.flatMap(_.elements) ++ declaredNodes)
                .collect { case n: AstNode if fileOf(n) != file && allowedFiles.contains(fileOf(n)) => n }
              val rawForeignLines = foreignNodes.groupBy(fileOf).toSeq.sortBy(_._1).map { case (f, nodes) =>
                val signatures = nodes.collect { case n: CfgNode => n }.flatMap(n => lineOf(n.method))
                f -> (nodes.flatMap(lineOf) ++ signatures).distinct.sorted
              }
              val foreignLines = (rawForeignLines ++ dependencyLines.toSeq.filter(_._1 != file))
                .groupBy(_._1).toSeq.sortBy(_._1).map((f, groups) => f -> groups.flatMap(_._2).distinct.sorted)
              Entry(
                operationId = operation.id,
                srcFile = file,
                lineNo = operation.lineNo,
                opType = operation.opType,
                operation = operation.operation,
                algorithm = operation.algorithm,
                mode = operation.mode,
                resolution = operation.resolution,
                semanticKind = "",
                verificationContextLines = verificationLines,
                dependencyContextLines = dependencyLines.getOrElse(file, Vector.empty),
                callLines = callLines,
                methodLines = methodLines,
                declaredSourceLines = declaredLines.filterNot(excluded.contains),
                provenanceLines = provenanceLines,
                pathCount = flows.size,
                killedLines = killed.toSeq.sorted,
                allLines = if (calls.nonEmpty) allLines else Seq.empty,
                apiTypes = apiTypes,
                declarations = context.declarations(calls, killed),
                overwrites = overwrites,
                supportOf = Seq.empty,
                sourceFiles = if (caseId.isDefined) (Seq(file -> allLines) ++ foreignLines) else Seq.empty
              )
            }

            // A callback can have no primitive operation at all (e.g. an empty trust callback).
            // Keep its source as an independent slice; safe callbacks use exactly the same selection rule.
            val coveredMethods = operations.flatMap(_.realizingCall.method.id.l).toSet
            val verificationEntries = verification.toVector.flatMap(_.contexts)
              .filterNot(c => c.methods.nonEmpty && c.methods.forall(m => coveredMethods(m.id)))
              .map { c =>
                Entry(operationId = c.id, srcFile = c.file, lineNo = c.line, opType = "VERIFICATION",
                  operation = "VERIFY_CONTEXT", algorithm = "", mode = "", resolution = "COMPLETE",
                  semanticKind = "VerificationContext", verificationContextLines = c.lines,
                  dependencyContextLines = Seq.empty,
                  callLines = c.calls.flatMap(lineOf).distinct.sorted,
                  methodLines = c.methods.flatMap(lineOf).distinct.sorted,
                  declaredSourceLines = Seq.empty, provenanceLines = Seq.empty, pathCount = 0,
                  killedLines = Seq.empty, allLines = c.lines, apiTypes = c.apiTypes,
                  declarations = Seq.empty, overwrites = Seq.empty, supportOf = Seq.empty,
                  sourceFiles = if (caseGroups.nonEmpty) Seq(c.file -> c.lines) else Seq.empty)
              }
            val entries = primitiveEntries ++ verificationEntries

            val linked = entries.map { entry =>
              val source = operations.find(_.id == entry.operationId).toVector.flatMap(_.realizingCall.l).filter(context.reachable)
              val consumers = if (entry.opType != "RANDOMNESS") Seq.empty else entries.filter { target =>
                target.operationId != entry.operationId && target.srcFile == entry.srcFile &&
                  operations.find(_.id == target.operationId).exists(op => source.exists(p => op.realizingCall.l.exists(c =>
                    context.dependsOn(p, c) && lineOf(p).exists(target.allLines.contains))))
              }.map(_.operationId)
              val effectiveConsumers = if (!effectiveOverwriteContext) Seq.empty else entries.filter { target =>
                consumers.contains(target.operationId) && operations.find(_.id == target.operationId).exists(op =>
                  source.exists(w => op.realizingCall.l.exists(c => context.fullRandomFillReaches(w, c))))
              }
              val sections = (entry.sourceFiles ++ effectiveConsumers.flatMap(_.sourceFiles))
                .groupBy(_._1).toSeq.sortBy(_._1).map((f, groups) => f -> groups.flatMap(_._2).distinct.sorted)
              entry.copy(supportOf = consumers,
                allLines = (entry.allLines ++ effectiveConsumers.flatMap(_.allLines)).distinct.sorted,
                dependencyContextLines = (entry.dependencyContextLines ++ effectiveConsumers.flatMap(_.dependencyContextLines)).distinct.sorted,
                sourceFiles = sections,
                declarations = (entry.declarations ++ effectiveConsumers.flatMap(_.declarations)).distinct,
                overwrites = (entry.overwrites ++ effectiveConsumers.flatMap(_.overwrites)).distinct,
                effectiveConsumerContext = effectiveConsumers.map(_.operationId))
            }
            Option(output.getParent).foreach(Files.createDirectories(_))
            Files.writeString(output, renderJson(linked), StandardCharsets.UTF_8)

            println(s"cryptoOperations=${operations.size}")
            println(s"verificationContextEntries=${entries.count(_.verificationContextLines.nonEmpty)}")
            println(s"filesWithOperation=${entries.map(_.srcFile).distinct.size}")
            println(s"flowPaths=${entries.map(_.pathCount).sum}")
            println(s"provenanceLines=${entries.map(_.provenanceLines.size).sum}")
            println(s"killedLinesRemoved=${entries.map(_.killedLines.size).sum}")
            entries.groupBy(_.resolution).toSeq.sortBy(_._1).foreach { case (value, group) =>
              println(s"resolution[$value]=${group.size}")
            }
            println(s"-> $output")
          } finally cpg.close()
      }
    } finally Files.deleteIfExists(cpgPath)
  }

  private final case class Entry(
    operationId: Long,
    srcFile: String,
    lineNo: Int,
    opType: String,
    operation: String,
    algorithm: String,
    mode: String,
    resolution: String,
    semanticKind: String,
    verificationContextLines: Seq[Int],
    dependencyContextLines: Seq[Int],
    callLines: Seq[Int],
    methodLines: Seq[Int],
    declaredSourceLines: Seq[Int],
    provenanceLines: Seq[Int],
    pathCount: Int,
    killedLines: Seq[Int],
    allLines: Seq[Int],
    apiTypes: Seq[String],
    declarations: Seq[SliceLineEvidence#Declaration],
    overwrites: Seq[SliceLineEvidence#Overwrite],
    supportOf: Seq[Long],
    sourceFiles: Seq[(String, Seq[Int])],
    effectiveConsumerContext: Seq[Long] = Seq.empty
  )

  private def fileOf(node: StoredNode): String = node match {
    case astNode: AstNode => astNode.file.name.headOption.getOrElse("")
    case _                => ""
  }

  private def lineOf(node: StoredNode): Option[Int] = node match {
    case astNode: AstNode => astNode.lineNumber.map(_.intValue)
    case _                => None
  }

  private def renderJson(entries: Seq[Entry]): String =
    entries
      .map { entry =>
        s"""{"operationId":${entry.operationId},"srcFile":${quote(entry.srcFile)},"lineNo":${entry.lineNo},""" +
          s""""opType":${quote(entry.opType)},"operation":${quote(entry.operation)},""" +
          s""""algorithm":${quote(entry.algorithm)},"mode":${quote(entry.mode)},""" +
          s""""resolution":${quote(entry.resolution)},""" +
          s""""semanticKind":${quote(entry.semanticKind)},"verificationContextLines":${array(entry.verificationContextLines)},""" +
          s""""dependencyContextLines":${array(entry.dependencyContextLines)},"effectiveConsumerContext":${entry.effectiveConsumerContext.mkString("[", ",", "]")},""" +
          s""""callLines":${array(entry.callLines)},"methodLines":${array(entry.methodLines)},""" +
          s""""declaredSourceLines":${array(entry.declaredSourceLines)},""" +
          s""""provenanceLines":${array(entry.provenanceLines)},""" +
          s""""pathCount":${entry.pathCount},"killedLines":${array(entry.killedLines)},""" +
          s""""allLines":${array(entry.allLines)},"apiTypes":${entry.apiTypes.map(quote).mkString("[", ",", "]")},""" +
          s""""declarationProjections":${entry.declarations.map(d =>
            s"""{"line":${d.line},"name":${quote(d.name)},"typeFullName":${quote(d.typeFullName)},"replacement":${quote(d.replacement)}}""").mkString("[", ",", "]")},""" +
          s""""sourceFiles":${entry.sourceFiles.map { case (f, lines) => s"""{"file":${quote(f)},"lines":${array(lines)}}""" }.mkString("[", ",", "]")},""" +
          s""""supportOf":${entry.supportOf.mkString("[", ",", "]")},"randomOverwrites":${entry.overwrites.map(w =>
            s"""{"buffer":${quote(w.buffer)},"definitionLine":${w.definitionLine},"writerLine":${w.writerLine},"replacement":${quote(w.replacement)}}""").mkString("[", ",", "]")}}"""
      }
      .mkString("[\n  ", ",\n  ", "\n]\n")

  private def array(values: Seq[Int]): String = values.mkString("[", ",", "]")

  private def quote(value: String): String = {
    val escaped = value.flatMap {
      case '"'                       => "\\\""
      case '\\'                      => "\\\\"
      case '\n'                      => "\\n"
      case '\r'                      => "\\r"
      case '\t'                      => "\\t"
      case c if c.toInt < 0x20       => f"\\u${c.toInt}%04x"
      case c                         => c.toString
    }
    s""""$escaped""""
  }
}
