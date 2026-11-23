package org.apache.openejb.util;

import org.apache.log4j.Level;

public class Log4jLogStream implements LogStream {
   protected org.apache.log4j.Logger logger;

   public Log4jLogStream(LogCategory logCategory) {
      this.logger = org.apache.log4j.Logger.getLogger(logCategory.getName());
   }

   public boolean isFatalEnabled() {
      return this.logger.isEnabledFor(Level.FATAL);
   }

   public void fatal(String message) {
      this.logger.fatal(message);
   }

   public void fatal(String message, Throwable t) {
      this.logger.fatal(message, t);
   }

   public boolean isErrorEnabled() {
      return this.logger.isEnabledFor(Level.ERROR);
   }

   public void error(String message) {
      this.logger.error(message);
   }

   public void error(String message, Throwable t) {
      this.logger.error(message, t);
   }

   public boolean isWarnEnabled() {
      return this.logger.isEnabledFor(Level.WARN);
   }

   public void warn(String message) {
      this.logger.warn(message);
   }

   public void warn(String message, Throwable t) {
      this.logger.warn(message, t);
   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public void info(String message) {
      this.logger.info(message);
   }

   public void info(String message, Throwable t) {
      this.logger.info(message, t);
   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public void debug(String message) {
      this.logger.debug(message);
   }

   public void debug(String message, Throwable t) {
      this.logger.debug(message, t);
   }
}
