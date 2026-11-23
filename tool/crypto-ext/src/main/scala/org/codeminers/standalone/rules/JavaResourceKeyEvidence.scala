package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{Call, Expression, Identifier, Literal}
import io.shiftleft.semanticcpg.language.*
import io.shiftleft.codepropertygraph.generated.language.*
import org.codeminers.standalone.enrichment.{JavaValueSummaries, SourceClassifier}
import java.nio.file.{Files, Path}
import java.security.MessageDigest
import scala.util.Try

/** Read-only proof for plaintext key bytes distributed in a source tree's classpath resources.
  * A path literal alone is never key evidence. Dynamic paths, missing files and external filesystem
  * reads remain unknown. The original input is explicit because the Java frontend rewrites into a
  * temporary source-only tree that is deleted before rules run.
  */
private[rules] final class JavaResourceKeyEvidence(cpg: Cpg, input: Path) {
  private val summaries = new JavaValueSummaries(cpg, includeResourceDefaults = true)
  private val normalized = input.toAbsolutePath.normalize()
  private val roots = {
    val ancestors = Iterator.iterate(normalized)(_.getParent).takeWhile(_ != null).take(10).toVector
    val conventional = ancestors.collect {
      case p if Option(p.getFileName).exists(_.toString == "java") &&
        Option(p.getParent).flatMap(x => Option(x.getFileName)).exists(_.toString == "main") &&
        Option(p.getParent).flatMap(x => Option(x.getParent)).flatMap(x => Option(x.getFileName))
          .exists(_.toString == "src") => p.getParent.resolve("resources")
    }
    (Vector(normalized.resolve("src/main/resources"), normalized.resolve("resources")) ++ conventional)
      .filter(Files.isDirectory(_)).distinct
  }
  private def arg(c: Call, index: Int) = c.argument.l.find(_.argumentIndex == index)
  private def location(c: Call) = s"${c.file.name.headOption.getOrElse("UNKNOWN")}:${c.lineNumber.getOrElse(-1)} ${c.code}"

  private def classpathName(read: Call): Option[String] = {
    // Resolve only exact byte-returning wrappers whose stream is selected by the same parameter.
    val methods = NoResolve.getCalledMethods(read).filterNot(_.isExternal).toVector
    if (!SourceClassifier.isJavaResourceRead(read) || methods.size != 1) return None
    val method = methods.head
    val streams = method.ast.isCall.filter(c => c.name == "getResourceAsStream" &&
      c.methodFullName.startsWith("java.lang.Class.")).l
    if (streams.size != 1) return None
    val stream = streams.head
    val streamNames = method.ast.isCall.nameExact("<operator>.assignment").l.filter(a =>
      arg(a, 2).exists(_.id == stream.id)).flatMap(a => arg(a, 1).collect { case i: Identifier => i.name })
    if (streamNames.size != 1 || method.ast.isCall.nameExact("<operator>.assignment").l.count(a =>
      arg(a, 1).exists { case i: Identifier => streamNames.contains(i.name); case _ => false }) != 1) return None
    val reads = method.ast.isReturn.astChildren.collect { case c: Call => c }.filter(c =>
      c.name == "readAllBytes" && c.methodFullName.startsWith("java.io.InputStream.")).toVector
    if (reads.isEmpty || !reads.forall(c => arg(c, 0).exists {
      case i: Identifier => streamNames.contains(i.name)
      case x: Call => x.id == stream.id
      case _ => false
    })) return None
    val path = arg(stream, 1).flatMap {
      case i: Identifier => method.parameter.nameExact(i.name).headOption.flatMap(p => arg(read, p.index))
      case l: Literal => Some(l)
      case _ => None
    }
    path.flatMap(summaries.resolve(_)).filter(_.sources.size == 1).flatMap(_.sources.head match {
      case l: Literal if l.code.startsWith("\"") && l.code.endsWith("\"") =>
        val raw = l.code.drop(1).dropRight(1)
        // Escapes and traversal need a fuller Java string/classpath interpreter; leave them unknown.
        if (raw.isEmpty || raw.contains("\\") || raw.split('/').contains("..")) None
        else Option.when(raw.startsWith("/"))(raw.drop(1)) // Relative names require proof of the receiver Class.
      case _ => None
    })
  }

  private def proof(read: Call): Vector[FindingEvidence] = classpathName(read).toVector.flatMap { name =>
    val candidates = roots.map(root => root -> root.resolve(name).normalize()).filter { (root, file) =>
      file.startsWith(root) && Files.isRegularFile(file) &&
        Try(file.toRealPath().startsWith(root.toRealPath())).getOrElse(false)
    }
    // Ambiguous classpath roots cannot establish which bytes are loaded.
    if (candidates.size != 1) Vector.empty else Try {
      val (root, file) = candidates.head
      val size = Files.size(file)
      if (size <= 0 || size > 1024 * 1024) Vector.empty else {
        val bytes = Files.readAllBytes(file)
        val hash = MessageDigest.getInstance("SHA-256").digest(bytes).map(b => f"${b & 0xff}%02x").mkString
        Vector(FindingEvidence("KEY_RESOURCE", "VERIFIED_CLASSPATH_RESOURCE",
          s"classpath=/$name bytes=${bytes.length} sha256=$hash file=$file read=${location(read)}"))
      }
    }.getOrElse(Vector.empty)
  }

  def evidence(expression: Expression): Vector[FindingEvidence] = summaries.resolve(expression, material = true)
    .toVector.flatMap { summary =>
      val verified = summary.sources.collect { case read: Call => read }.flatMap(proof)
      if (verified.isEmpty) Vector.empty else verified ++
        summary.trace.map(c => FindingEvidence("KEY_PATH", c.label, location(c))) ++
        Option.when(summary.defaultPath)(FindingEvidence("KEY_RESOURCE_BRANCH", "POSSIBLE_CONFIGURATION",
          "Bundled resource key is a possible initialization/configuration branch; this does not assert the runtime branch.")).toVector
    }.distinct
}
