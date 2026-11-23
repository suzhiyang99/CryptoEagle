package org.apache.meecrowave.logging.tomcat;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.juli.logging.Log;

public class JULLog implements Log {
   private final Logger logger;
   private static final String SIMPLE_FMT = "java.util.logging.SimpleFormatter";
   private static final String SIMPLE_CFG = "org.apache.juli.JdkLoggerConfig";
   private static final String FORMATTER = "org.apache.juli.formatter";

   public JULLog(String name) {
      this.logger = Logger.getLogger(name);
   }

   public final boolean isErrorEnabled() {
      return this.logger.isLoggable(Level.SEVERE);
   }

   public final boolean isWarnEnabled() {
      return this.logger.isLoggable(Level.WARNING);
   }

   public final boolean isInfoEnabled() {
      return this.logger.isLoggable(Level.INFO);
   }

   public final boolean isDebugEnabled() {
      return this.logger.isLoggable(Level.FINE);
   }

   public final boolean isFatalEnabled() {
      return this.logger.isLoggable(Level.SEVERE);
   }

   public final boolean isTraceEnabled() {
      return this.logger.isLoggable(Level.FINER);
   }

   public final void debug(Object message) {
      this.log(Level.FINE, String.valueOf(message), (Throwable)null);
   }

   public final void debug(Object message, Throwable t) {
      this.log(Level.FINE, String.valueOf(message), t);
   }

   public final void trace(Object message) {
      this.log(Level.FINER, String.valueOf(message), (Throwable)null);
   }

   public final void trace(Object message, Throwable t) {
      this.log(Level.FINER, String.valueOf(message), t);
   }

   public final void info(Object message) {
      this.log(Level.INFO, String.valueOf(message), (Throwable)null);
   }

   public final void info(Object message, Throwable t) {
      this.log(Level.INFO, String.valueOf(message), t);
   }

   public final void warn(Object message) {
      this.log(Level.WARNING, String.valueOf(message), (Throwable)null);
   }

   public final void warn(Object message, Throwable t) {
      this.log(Level.WARNING, String.valueOf(message), t);
   }

   public final void error(Object message) {
      this.log(Level.SEVERE, String.valueOf(message), (Throwable)null);
   }

   public final void error(Object message, Throwable t) {
      this.log(Level.SEVERE, String.valueOf(message), t);
   }

   public final void fatal(Object message) {
      this.log(Level.SEVERE, String.valueOf(message), (Throwable)null);
   }

   public final void fatal(Object message, Throwable t) {
      this.log(Level.SEVERE, String.valueOf(message), t);
   }

   private void log(Level level, String msg, Throwable ex) {
      if (this.logger.isLoggable(level)) {
         Throwable dummyException = new Throwable();
         StackTraceElement[] locations = dummyException.getStackTrace();
         String cname = "unknown";
         String method = "unknown";
         if (locations != null && locations.length > 2) {
            StackTraceElement caller = locations[2];
            cname = caller.getClassName();
            method = caller.getMethodName();
         }

         if (ex == null) {
            this.logger.logp(level, cname, method, msg);
         } else {
            this.logger.logp(level, cname, method, msg, ex);
         }
      }

   }

   static {
      if (System.getProperty("java.util.logging.config.class") == null && System.getProperty("java.util.logging.config.file") == null) {
         try {
            Class.forName("org.apache.juli.JdkLoggerConfig").newInstance();
         } catch (Throwable var6) {
         }

         try {
            Formatter fmt = (Formatter)Class.forName(System.getProperty("org.apache.juli.formatter", "java.util.logging.SimpleFormatter")).newInstance();
            Logger root = Logger.getLogger("");

            for(Handler handler : root.getHandlers()) {
               if (handler instanceof ConsoleHandler) {
                  handler.setFormatter(fmt);
               }
            }
         } catch (Throwable var7) {
         }
      }

   }
}
