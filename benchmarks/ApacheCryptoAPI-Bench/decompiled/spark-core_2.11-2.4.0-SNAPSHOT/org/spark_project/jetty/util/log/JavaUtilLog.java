package org.spark_project.jetty.util.log;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import org.spark_project.jetty.util.Loader;

public class JavaUtilLog extends AbstractLogger {
   private static final String THIS_CLASS = JavaUtilLog.class.getName();
   private static final boolean __source;
   private static boolean _initialized;
   private Level configuredLevel;
   private java.util.logging.Logger _logger;

   public JavaUtilLog() {
      this("org.spark_project.jetty.util.log.javautil");
   }

   public JavaUtilLog(String name) {
      synchronized(JavaUtilLog.class) {
         if (!_initialized) {
            _initialized = true;
            final String properties = Log.__props.getProperty("org.spark_project.jetty.util.log.javautil.PROPERTIES", (String)null);
            if (properties != null) {
               AccessController.doPrivileged(new PrivilegedAction<Object>() {
                  public Object run() {
                     try {
                        URL props = Loader.getResource(JavaUtilLog.class, properties);
                        if (props != null) {
                           LogManager.getLogManager().readConfiguration(props.openStream());
                        }
                     } catch (Throwable e) {
                        System.err.println("[WARN] Error loading logging config: " + properties);
                        e.printStackTrace(System.err);
                     }

                     return null;
                  }
               });
            }
         }
      }

      this._logger = java.util.logging.Logger.getLogger(name);
      switch (lookupLoggingLevel(Log.__props, name)) {
         case -1:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         default:
            break;
         case 0:
            this._logger.setLevel(Level.ALL);
            break;
         case 1:
            this._logger.setLevel(Level.FINE);
            break;
         case 2:
            this._logger.setLevel(Level.INFO);
            break;
         case 3:
            this._logger.setLevel(Level.WARNING);
            break;
         case 10:
            this._logger.setLevel(Level.OFF);
      }

      this.configuredLevel = this._logger.getLevel();
   }

   public String getName() {
      return this._logger.getName();
   }

   protected void log(Level level, String msg, Throwable thrown) {
      LogRecord record = new LogRecord(level, msg);
      if (thrown != null) {
         record.setThrown(thrown);
      }

      record.setLoggerName(this._logger.getName());
      if (__source) {
         StackTraceElement[] stack = (new Throwable()).getStackTrace();

         for(int i = 0; i < stack.length; ++i) {
            StackTraceElement e = stack[i];
            if (!e.getClassName().equals(THIS_CLASS)) {
               record.setSourceClassName(e.getClassName());
               record.setSourceMethodName(e.getMethodName());
               break;
            }
         }
      }

      this._logger.log(record);
   }

   public void warn(String msg, Object... args) {
      if (this._logger.isLoggable(Level.WARNING)) {
         this.log(Level.WARNING, this.format(msg, args), (Throwable)null);
      }

   }

   public void warn(Throwable thrown) {
      if (this._logger.isLoggable(Level.WARNING)) {
         this.log(Level.WARNING, "", thrown);
      }

   }

   public void warn(String msg, Throwable thrown) {
      if (this._logger.isLoggable(Level.WARNING)) {
         this.log(Level.WARNING, msg, thrown);
      }

   }

   public void info(String msg, Object... args) {
      if (this._logger.isLoggable(Level.INFO)) {
         this.log(Level.INFO, this.format(msg, args), (Throwable)null);
      }

   }

   public void info(Throwable thrown) {
      if (this._logger.isLoggable(Level.INFO)) {
         this.log(Level.INFO, "", thrown);
      }

   }

   public void info(String msg, Throwable thrown) {
      if (this._logger.isLoggable(Level.INFO)) {
         this.log(Level.INFO, msg, thrown);
      }

   }

   public boolean isDebugEnabled() {
      return this._logger.isLoggable(Level.FINE);
   }

   public void setDebugEnabled(boolean enabled) {
      if (enabled) {
         this.configuredLevel = this._logger.getLevel();
         this._logger.setLevel(Level.FINE);
      } else {
         this._logger.setLevel(this.configuredLevel);
      }

   }

   public void debug(String msg, Object... args) {
      if (this._logger.isLoggable(Level.FINE)) {
         this.log(Level.FINE, this.format(msg, args), (Throwable)null);
      }

   }

   public void debug(String msg, long arg) {
      if (this._logger.isLoggable(Level.FINE)) {
         this.log(Level.FINE, this.format(msg, arg), (Throwable)null);
      }

   }

   public void debug(Throwable thrown) {
      if (this._logger.isLoggable(Level.FINE)) {
         this.log(Level.FINE, "", thrown);
      }

   }

   public void debug(String msg, Throwable thrown) {
      if (this._logger.isLoggable(Level.FINE)) {
         this.log(Level.FINE, msg, thrown);
      }

   }

   protected Logger newLogger(String fullname) {
      return new JavaUtilLog(fullname);
   }

   public void ignore(Throwable ignored) {
      if (this._logger.isLoggable(Level.FINEST)) {
         this.log(Level.FINEST, "IGNORED EXCEPTION ", ignored);
      }

   }

   private String format(String msg, Object... args) {
      msg = String.valueOf(msg);
      String braces = "{}";
      StringBuilder builder = new StringBuilder();
      int start = 0;

      for(Object arg : args) {
         int bracesIndex = msg.indexOf(braces, start);
         if (bracesIndex < 0) {
            builder.append(msg.substring(start));
            builder.append(" ");
            builder.append(arg);
            start = msg.length();
         } else {
            builder.append(msg.substring(start, bracesIndex));
            builder.append(String.valueOf(arg));
            start = bracesIndex + braces.length();
         }
      }

      builder.append(msg.substring(start));
      return builder.toString();
   }

   static {
      __source = Boolean.parseBoolean(Log.__props.getProperty("org.spark_project.jetty.util.log.SOURCE", Log.__props.getProperty("org.spark_project.jetty.util.log.javautil.SOURCE", "true")));
      _initialized = false;
   }
}
