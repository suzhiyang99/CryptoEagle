package org.spark_project.jetty.util.log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class JettyLogHandler extends Handler {
   public static void config() {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      URL url = cl.getResource("logging.properties");
      if (url != null) {
         System.err.printf("Initializing java.util.logging from %s%n", url);

         try {
            InputStream in = url.openStream();
            Throwable var3 = null;

            try {
               LogManager.getLogManager().readConfiguration(in);
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (in != null) {
                  if (var3 != null) {
                     try {
                        in.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     in.close();
                  }
               }

            }
         } catch (IOException e) {
            e.printStackTrace(System.err);
         }
      } else {
         System.err.printf("WARNING: java.util.logging failed to initialize: logging.properties not found%n");
      }

      System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
   }

   public JettyLogHandler() {
      if (Boolean.parseBoolean(Log.__props.getProperty("org.spark_project.jetty.util.log.DEBUG", "false"))) {
         this.setLevel(Level.FINEST);
      }

      if (Boolean.parseBoolean(Log.__props.getProperty("org.spark_project.jetty.util.log.IGNORED", "false"))) {
         this.setLevel(Level.ALL);
      }

      System.err.printf("%s Initialized at level [%s]%n", this.getClass().getName(), this.getLevel().getName());
   }

   private synchronized String formatMessage(LogRecord record) {
      String msg = this.getMessage(record);

      try {
         Object[] params = record.getParameters();
         if (params != null && params.length != 0) {
            return Pattern.compile("\\{\\d+\\}").matcher(msg).find() ? MessageFormat.format(msg, params) : msg;
         } else {
            return msg;
         }
      } catch (Exception var4) {
         return msg;
      }
   }

   private String getMessage(LogRecord record) {
      ResourceBundle bundle = record.getResourceBundle();
      if (bundle != null) {
         try {
            return bundle.getString(record.getMessage());
         } catch (MissingResourceException var4) {
         }
      }

      return record.getMessage();
   }

   public void publish(LogRecord record) {
      Logger JLOG = this.getJettyLogger(record.getLoggerName());
      int level = record.getLevel().intValue();
      if (level < Level.OFF.intValue()) {
         Throwable cause = record.getThrown();
         String msg = this.formatMessage(record);
         if (level >= Level.WARNING.intValue()) {
            if (cause != null) {
               JLOG.warn(msg, cause);
            } else {
               JLOG.warn(msg);
            }

         } else if (level >= Level.INFO.intValue()) {
            if (cause != null) {
               JLOG.info(msg, cause);
            } else {
               JLOG.info(msg);
            }

         } else if (level >= Level.FINEST.intValue()) {
            if (cause != null) {
               JLOG.debug(msg, cause);
            } else {
               JLOG.debug(msg);
            }

         } else if (level >= Level.ALL.intValue()) {
            JLOG.ignore(cause);
         }
      }
   }

   private Logger getJettyLogger(String loggerName) {
      return Log.getLogger(loggerName);
   }

   public void flush() {
   }

   public void close() throws SecurityException {
   }
}
