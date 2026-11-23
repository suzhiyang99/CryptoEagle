package org.apache.openejb.util;

import org.apache.logging.log4j.LogManager;

public class Log4j2LogStreamFactory implements LogStreamFactory {
   public Log4j2LogStreamFactory() {
      JavaSecurityManagers.setSystemProperty("openwebbeans.logging.factory", "org.apache.openejb.cdi.logging.Log4j2LoggerFactory");
   }

   public LogStream createLogStream(LogCategory logCategory) {
      return new Log4j2Stream(logCategory.getName());
   }

   private class Log4j2Stream implements LogStream {
      private final org.apache.logging.log4j.Logger delegate;

      public Log4j2Stream(String name) {
         this.delegate = LogManager.getLogger(name);
      }

      public boolean isFatalEnabled() {
         return this.delegate.isFatalEnabled();
      }

      public void fatal(String message) {
         this.delegate.fatal(message);
      }

      public void fatal(String message, Throwable t) {
         this.delegate.fatal(message, t);
      }

      public boolean isErrorEnabled() {
         return this.delegate.isErrorEnabled();
      }

      public void error(String message) {
         this.delegate.error(message);
      }

      public void error(String message, Throwable t) {
         this.delegate.error(message, t);
      }

      public boolean isWarnEnabled() {
         return this.delegate.isWarnEnabled();
      }

      public void warn(String message) {
         this.delegate.warn(message);
      }

      public void warn(String message, Throwable t) {
         this.delegate.warn(message, t);
      }

      public boolean isInfoEnabled() {
         return this.delegate.isInfoEnabled();
      }

      public void info(String message) {
         this.delegate.info(message);
      }

      public void info(String message, Throwable t) {
         this.delegate.info(message, t);
      }

      public boolean isDebugEnabled() {
         return this.delegate.isDebugEnabled();
      }

      public void debug(String message) {
         this.delegate.debug(message);
      }

      public void debug(String message, Throwable t) {
         this.delegate.debug(message, t);
      }
   }
}
