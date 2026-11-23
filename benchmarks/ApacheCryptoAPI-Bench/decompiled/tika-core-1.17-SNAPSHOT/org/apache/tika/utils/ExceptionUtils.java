package org.apache.tika.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.exception.TikaException;

public class ExceptionUtils {
   private static final Pattern MSG_PATTERN = Pattern.compile(":[^\r\n]+");

   public static String getFilteredStackTrace(Throwable t) {
      Throwable cause = t;
      if (t instanceof TikaException && t.getCause() != null) {
         cause = t.getCause();
      }

      return getStackTrace(cause);
   }

   public static String getStackTrace(Throwable t) {
      Writer result = new StringWriter();
      PrintWriter writer = new PrintWriter(result);
      t.printStackTrace(writer);

      try {
         writer.flush();
         result.flush();
         writer.close();
         result.close();
      } catch (IOException var4) {
      }

      return result.toString();
   }

   public static String trimMessage(String trace) {
      Matcher msgMatcher = MSG_PATTERN.matcher(trace);
      return msgMatcher.find() ? msgMatcher.replaceFirst("") : trace;
   }
}
