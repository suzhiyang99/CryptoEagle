package org.apache.openejb.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import org.apache.openejb.util.JavaSecurityManagers;

public class SingleLineFormatter extends Formatter {
   private static final String SEP = JavaSecurityManagers.getSystemProperty("line.separator", "\n");

   public synchronized String format(LogRecord record) {
      boolean exception = record.getThrown() != null;
      StringBuilder sbuf = new StringBuilder();
      sbuf.append(record.getLevel().getLocalizedName());
      sbuf.append(" - ");
      sbuf.append(this.formatMessage(record));
      sbuf.append(SEP);
      if (exception) {
         try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sbuf.append(sw.toString());
         } catch (Exception var6) {
         }
      }

      return sbuf.toString();
   }
}
