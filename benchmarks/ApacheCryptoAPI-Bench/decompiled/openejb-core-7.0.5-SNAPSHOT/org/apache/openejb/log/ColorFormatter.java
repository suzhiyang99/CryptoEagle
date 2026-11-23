package org.apache.openejb.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.openejb.loader.SystemInstance;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

public class ColorFormatter extends SingleLineFormatter {
   public static final String OPENEJB_LOG_COLOR_PREFIX = "openejb.log.color.";
   private final Map<Level, Ansi.Color> colors = new HashMap();

   public ColorFormatter() {
      this.colors.put(Level.FINEST, this.color("finest", Color.DEFAULT.name()));
      this.colors.put(Level.FINER, this.color("finer", Color.DEFAULT.name()));
      this.colors.put(Level.FINE, this.color("fine", Color.DEFAULT.name()));
      this.colors.put(Level.INFO, this.color("info", Color.WHITE.name()));
      this.colors.put(Level.WARNING, this.color("warning", Color.YELLOW.name()));
      this.colors.put(Level.SEVERE, this.color("severe", Color.RED.name()));
   }

   public synchronized String format(LogRecord record) {
      boolean exception = record.getThrown() != null;
      Ansi sbuf = this.prefix(record);
      sbuf.a(record.getLevel().getLocalizedName());
      sbuf.a(" - ");
      sbuf.a(this.formatMessage(record));
      if (!exception) {
         this.suffix(sbuf, record);
      }

      sbuf.newline();
      if (exception) {
         try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sbuf.a(sw.toString());
         } catch (Exception var9) {
         } finally {
            this.suffix(sbuf, record);
         }
      }

      return sbuf.toString();
   }

   private Ansi.Color color(String lvl, String aDefault) {
      try {
         return Color.valueOf(SystemInstance.get().getProperty("openejb.log.color." + lvl, aDefault));
      } catch (IllegalArgumentException var4) {
         return Color.valueOf(aDefault);
      }
   }

   private Ansi color(Level key) {
      return this.colors.containsKey(key) ? Ansi.ansi().fg((Ansi.Color)this.colors.get(key)) : Ansi.ansi().fg(Color.DEFAULT);
   }

   protected Ansi prefix(LogRecord record) {
      return this.color(record.getLevel());
   }

   protected Ansi suffix(Ansi ansi, LogRecord record) {
      return ansi.reset();
   }
}
