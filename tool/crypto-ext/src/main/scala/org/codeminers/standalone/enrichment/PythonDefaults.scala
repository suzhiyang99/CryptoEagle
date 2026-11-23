package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.nodes.Method
import io.shiftleft.semanticcpg.language.*

/** pysrc2cpg 4.0.250 discards parameter defaults. Recover only numeric/string literals
  * from this exact method's source header, never from its docstring or another function.
  * Source content is stored by the Python frontend; no code is executed.
  */
private[enrichment] object PythonDefaults {
  def value(method: Method, parameter: String): Option[String] = {
    val lines = method.file.content.headOption.getOrElse("").linesIterator.toVector
    val start = method.lineNumber.getOrElse(0) - 1
    if (start < 0 || start >= lines.size) return None
    val header = lines.slice(start, start + 32).mkString("\n")
    val prefix = s"(?s)^\\s*(?:async\\s+)?def\\s+${java.util.regex.Pattern.quote(method.name)}\\s*\\(".r
      .findFirstMatchIn(header)
    prefix.flatMap { matched =>
      val text = header.drop(matched.end)
      val parts = scala.collection.mutable.ArrayBuffer.empty[String]
      var depth = 0
      var quote = ' '
      var escaped = false
      var end = false
      val buffer = new StringBuilder
      text.iterator.takeWhile(_ => !end).foreach { ch =>
        if (quote != ' ') {
          buffer.append(ch)
          if (escaped) escaped = false
          else if (ch == '\\') escaped = true
          else if (ch == quote) quote = ' '
        } else if (ch == '\'' || ch == '"') { quote = ch; buffer.append(ch) }
        else if (ch == ')' && depth == 0) { parts += buffer.toString; end = true }
        else if (ch == ',' && depth == 0) { parts += buffer.toString; buffer.clear() }
        else { if ("([{ ".trim.contains(ch)) depth += 1; if (")] }".replace(" ", "").contains(ch)) depth -= 1; buffer.append(ch) }
      }
      if (!end) None
      else parts.find(_.trim.matches(s"(?s)${java.util.regex.Pattern.quote(parameter)}(?:\\s*:[^=]+)?\\s*=.*"))
        .flatMap(_.split("=", 2).lift(1)).map(_.trim)
        .filter(_.matches("(?s)(?:[0-9][0-9_]*|\"[^\"\\\\]*\"|'[^'\\\\]*')"))
    }
  }
}
