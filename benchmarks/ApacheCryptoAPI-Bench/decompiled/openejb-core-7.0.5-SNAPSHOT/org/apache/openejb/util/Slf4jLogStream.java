package org.apache.openejb.util;

import org.slf4j.LoggerFactory;

public class Slf4jLogStream implements LogStream {
   private final org.slf4j.Logger log;

   public Slf4jLogStream(LogCategory logCategory) {
      this.log = LoggerFactory.getLogger(logCategory.getName());
   }

   public boolean isFatalEnabled() {
      return this.log.isErrorEnabled();
   }

   public void fatal(String message) {
      this.log.error(message);
   }

   public void fatal(String message, Throwable t) {
      this.log.error(message, t);
   }

   public boolean isErrorEnabled() {
      return this.log.isErrorEnabled();
   }

   public void error(String message) {
      this.log.error(message);
   }

   public void error(String message, Throwable t) {
      this.log.error(message, t);
   }

   public boolean isWarnEnabled() {
      return this.log.isWarnEnabled();
   }

   public void warn(String message) {
      this.log.warn(message);
   }

   public void warn(String message, Throwable t) {
      this.log.warn(message, t);
   }

   public boolean isInfoEnabled() {
      return this.log.isInfoEnabled();
   }

   public void info(String message) {
      this.log.info(message);
   }

   public void info(String message, Throwable t) {
      this.log.info(message, t);
   }

   public boolean isDebugEnabled() {
      return this.log.isDebugEnabled();
   }

   public void debug(String message) {
      this.log.debug(message);
   }

   public void debug(String message, Throwable t) {
      this.log.debug(message, t);
   }
}
