package org.apache.openejb.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.openejb.log.LoggerCreator;

public class JuliLogStream implements LogStream {
   protected final LoggerCreator logger;
   protected final AtomicBoolean debug = new AtomicBoolean(false);
   protected final AtomicBoolean info = new AtomicBoolean(false);

   public JuliLogStream(LogCategory logCategory) {
      this.logger = new LoggerCreator(logCategory.getName());
   }

   public boolean isFatalEnabled() {
      return LoggerCreator.Get.exec(this.logger, this.debug, this.info).isLoggable(Level.SEVERE);
   }

   public void fatal(String message) {
      this.log(Level.SEVERE, message, (Throwable)null);
   }

   public void fatal(String message, Throwable t) {
      this.log(Level.SEVERE, message, t);
   }

   public boolean isErrorEnabled() {
      return LoggerCreator.Get.exec(this.logger, this.debug, this.info).isLoggable(Level.SEVERE);
   }

   public void error(String message) {
      this.log(Level.SEVERE, message, (Throwable)null);
   }

   public void error(String message, Throwable t) {
      this.log(Level.SEVERE, message, t);
   }

   public boolean isWarnEnabled() {
      return LoggerCreator.Get.exec(this.logger, this.debug, this.info).isLoggable(Level.WARNING);
   }

   public void warn(String message) {
      this.log(Level.WARNING, message, (Throwable)null);
   }

   public void warn(String message, Throwable t) {
      this.log(Level.WARNING, message, t);
   }

   public boolean isInfoEnabled() {
      LoggerCreator.Get.exec(this.logger, this.debug, this.info);
      return this.info.get();
   }

   public void info(String message) {
      this.log(Level.INFO, message, (Throwable)null);
   }

   public void info(String message, Throwable t) {
      this.log(Level.INFO, message, t);
   }

   public boolean isDebugEnabled() {
      LoggerCreator.Get.levels(this.logger, this.debug, this.info);
      return this.debug.get();
   }

   public void debug(String message) {
      this.log(Level.FINE, message, (Throwable)null);
   }

   public void debug(String message, Throwable t) {
      this.log(Level.FINE, message, t);
   }

   private void log(Level level, String message, Throwable t) {
      java.util.logging.Logger log = LoggerCreator.Get.exec(this.logger, this.debug, this.info);
      if (log.isLoggable(level)) {
         LogRecord logRecord = new OpenEJBLogRecord(level, message);
         if (t != null) {
            logRecord.setThrown(t);
         }

         logRecord.setLoggerName(log.getName());
         log.log(logRecord);
      }

   }

   public static class OpenEJBLogRecord extends LogRecord {
      private static final String LOGGER = Logger.class.getName();
      private static final String ASYNC_LOG_NAME = "org.apache.openejb.util.LogStreamAsync";
      private static final String ASYNC_LOG_THREAD = "org.apache.openejb.util.LogStreamAsync$Consumer";
      private String sourceClassName;
      private String sourceMethodName;
      private transient boolean sourceInited = false;

      public OpenEJBLogRecord(Level level, String message) {
         super(level, message);
      }

      public String getSourceClassName() {
         this.initSource();
         return this.sourceClassName;
      }

      public void setSourceClassName(String sourceClassName) {
         this.sourceInited = true;
         this.sourceClassName = sourceClassName;
      }

      public String getSourceMethodName() {
         this.initSource();
         return this.sourceMethodName;
      }

      public void setSourceMethodName(String sourceMethodName) {
         this.sourceInited = true;
         this.sourceMethodName = sourceMethodName;
      }

      private void initSource() {
         if (!this.sourceInited) {
            StackTraceElement[] elements = (new Throwable()).getStackTrace();
            int i = 0;

            String current;
            for(current = null; i < elements.length; ++i) {
               current = elements[i].getClassName();
               if (current.equals(LOGGER)) {
                  break;
               }

               if (current.equals("org.apache.openejb.util.LogStreamAsync$Consumer")) {
                  this.sourceClassName = "org.apache.openejb.util.LogStreamAsync";
                  this.sourceMethodName = elements[i].getMethodName();
                  this.sourceInited = true;
                  return;
               }
            }

            do {
               ++i;
            } while(i < elements.length && elements[i].getClassName().equals(current));

            if (i < elements.length) {
               this.sourceClassName = elements[i].getClassName();
               this.sourceMethodName = elements[i].getMethodName();
            } else if (current != null && current.endsWith("LoggerThread")) {
               this.sourceClassName = "org.apache.openejb.util.LogStreamAsync";
               this.sourceMethodName = "run";
            }

            this.sourceInited = true;
         }

      }
   }
}
