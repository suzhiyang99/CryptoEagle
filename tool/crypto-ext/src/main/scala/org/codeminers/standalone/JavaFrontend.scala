package org.codeminers.standalone

import com.github.javaparser.{JavaParser, ParserConfiguration}
import com.github.javaparser.ast.body.InitializerDeclaration
import io.joern.javasrc2cpg.{Config, JavaSrc2Cpg}
import io.shiftleft.codepropertygraph.generated.Cpg

import java.nio.charset.StandardCharsets.ISO_8859_1
import java.nio.file.{Files, Path}
import java.util.Comparator
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*
import scala.jdk.StreamConverters.*
import scala.util.Try

/** javasrc2cpg drops initializer blocks: `AstForTypeDeclsCreator` maps every `InitializerDeclaration` to `Nil`
  * ("Handled with field initialisers"), yet only field declarations are collected into `<clinit>` and the
  * constructors (4.0.250 and upstream master alike).  A key, IV or password assigned in `static { ... }` or an
  * instance `{ ... }` block was therefore absent from the CPG, and decompiled code is full of such blocks.
  *
  * Before the frontend runs, each initializer block is given a method header in a copy of the sources, so its
  * statements become an ordinary method: `static {` reads `static void $cryptoeagle$staticInit0() {`.  Only the
  * header is inserted, on the block's own line, so file names and line numbers stay those of the original sources.
  */
object JavaFrontend {
  val StaticInitializerPrefix   = "$cryptoeagle$staticInit"
  val InstanceInitializerPrefix = "$cryptoeagle$instanceInit"

  /** A method that runs as part of object or class initialization, including the rewritten initializer blocks. */
  def isInitializer(methodName: String): Boolean =
    methodName == "<init>" || methodName == "<clinit>" ||
      methodName.startsWith(StaticInitializerPrefix) || methodName.startsWith(InstanceInitializerPrefix)

  def createCpg(input: Path, output: Path, disableDelombok: Boolean = false): Try[Cpg] =
    Try(prepareSources(input)).flatMap { case (analysisInput, temporaryRoot) =>
      val config = Config().withInputPath(analysisInput.toString).withOutputPath(output.toString)
      val effectiveConfig = if (disableDelombok) config.withDelombokMode("no-delombok") else config
      try JavaSrc2Cpg().createCpg(effectiveConfig)
      finally temporaryRoot.foreach(deleteRecursively)
    }

  /** The input to hand to the frontend, and the temporary copy to delete afterwards.  Sources without initializer
    * blocks are analysed in place.
    */
  def prepareSources(input: Path): (Path, Option[Path]) = {
    val root = if (Files.isRegularFile(input)) input.getParent else input
    val sources =
      if (Files.isRegularFile(input)) Vector(input)
      else {
        val stream = Files.walk(input)
        try stream.toScala(Vector).filter(path => Files.isRegularFile(path) && path.toString.endsWith(".java"))
        finally stream.close()
      }
    // ISO-8859-1 maps every byte to one char and back, so files in any encoding are copied byte for byte apart from
    // the inserted ASCII headers.
    // A file the rewrite cannot handle is analysed unchanged, exactly as before.
    val rewritten = sources.flatMap(path =>
      Try(rewrite(new String(Files.readAllBytes(path), ISO_8859_1))).toOption.flatten.map(path -> _)
    )
    if (rewritten.isEmpty) (input, None)
    else {
      val temporaryRoot = Files.createTempDirectory("crypto-aware-java-src-")
      val replacements  = rewritten.toMap
      sources.foreach { path =>
        val target = temporaryRoot.resolve(root.relativize(path).toString)
        Files.createDirectories(target.getParent)
        replacements.get(path) match {
          case Some(content) => Files.write(target, content.getBytes(ISO_8859_1))
          case None          => Files.copy(path, target)
        }
      }
      (temporaryRoot.resolve(root.relativize(input).toString), Some(temporaryRoot))
    }
  }

  /** Cheap superset of the shapes an initializer block's opening brace takes (`static {`, a brace opening a line,
    * `{{`, a brace after `;` or `}`); files matching none of them are not parsed.
    */
  private val InitializerCandidate = """(?m)static\s*\{|^\s*\{|\{\s*\{|[;}]\s*\{""".r

  /** The source with a method header before every initializer block, or None when it has none. */
  def rewrite(source: String): Option[String] =
    if (InitializerCandidate.findFirstIn(source).isEmpty) None
    else {
      val parser = new JavaParser(
        new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE)
      )
      val lineStarts = lineStartOffsets(source)
      val headers = parser
        .parse(source)
        .getResult
        .toScala
        .toVector
        .flatMap(_.findAll(classOf[InitializerDeclaration]).asScala)
        .flatMap(block =>
          block.getBody.getBegin.toScala.map(begin => (lineStarts(begin.line - 1) + begin.column - 1, block.isStatic))
        )
        .filter((offset, _) => offset < source.length && source.charAt(offset) == '{')
        .sortBy(_._1)
      if (headers.isEmpty) None
      else {
        val builder = new StringBuilder(source)
        // Insert back to front so earlier offsets stay valid; number blocks in source order.
        headers.zipWithIndex.reverse.foreach { case ((offset, isStatic), index) =>
          val prefix = if (isStatic) StaticInitializerPrefix else InstanceInitializerPrefix
          builder.insert(offset, s"void $prefix$index() ")
        }
        Some(builder.toString)
      }
    }

  /** Offsets at which each line starts, counting `\r\n`, `\r` and `\n` as terminators as JavaParser does. */
  private def lineStartOffsets(source: String): Vector[Int] = {
    val starts = Vector.newBuilder[Int]
    starts += 0
    var index = 0
    while (index < source.length) {
      val char = source.charAt(index)
      if (char == '\n' || (char == '\r' && !(index + 1 < source.length && source.charAt(index + 1) == '\n')))
        starts += index + 1
      index += 1
    }
    starts.result()
  }

  private def deleteRecursively(root: Path): Unit = {
    val stream = Files.walk(root)
    try stream.sorted(Comparator.reverseOrder()).forEach(path => Files.deleteIfExists(path))
    finally stream.close()
  }
}
