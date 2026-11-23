package org.codeminers.standalone.report

import org.codeminers.standalone.rules.CryptoFinding

import java.nio.file.Path

object FindingCli {
  def emit(args: Array[String], findings: Seq[CryptoFinding]): Unit = {
    val requestedFormat = args.collectFirst {
      case argument if argument.startsWith("--report=") =>
        val value = argument.stripPrefix("--report=")
        FindingFormat.parse(value).getOrElse(throw new IllegalArgumentException(s"Unsupported report format: $value"))
    }
    val output = args.collectFirst {
      case argument if argument.startsWith("--report-output=") =>
        Path.of(argument.stripPrefix("--report-output="))
    }

    (requestedFormat, output) match {
      case (None, None) => ()
      case (format, Some(path)) =>
        val resolvedFormat = format.getOrElse(formatFromPath(path))
        FindingReporter.write(findings, resolvedFormat, path)
        println(s"findingReport=${path.toAbsolutePath} format=$resolvedFormat")
      case (Some(format), None) => println(FindingReporter.render(findings, format))
    }
  }

  private def formatFromPath(path: Path): FindingFormat = {
    val fileName = path.getFileName.toString.toLowerCase
    if (fileName.endsWith(".json")) FindingFormat.Json
    else if (fileName.endsWith(".csv")) FindingFormat.Csv
    else FindingFormat.Text
  }
}
