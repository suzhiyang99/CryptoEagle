package org.apache.meecrowave.logging.tomcat;

import org.apache.juli.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4j2Log implements Log {
   private final Logger delegate;

   public Log4j2Log(String name) {
      this.delegate = LogManager.getLogger(name);
   }

   public boolean isDebugEnabled() {
      return this.delegate.isDebugEnabled();
   }

   public boolean isErrorEnabled() {
      return this.delegate.isErrorEnabled();
   }

   public boolean isFatalEnabled() {
      return this.delegate.isFatalEnabled();
   }

   public boolean isInfoEnabled() {
      return this.delegate.isInfoEnabled();
   }

   public boolean isTraceEnabled() {
      return this.delegate.isTraceEnabled();
   }

   public boolean isWarnEnabled() {
      return this.delegate.isWarnEnabled();
   }

   public void trace(Object message) {
      this.delegate.trace(message);
   }

   public void trace(Object message, Throwable t) {
      this.delegate.trace(message, t);
   }

   public void debug(Object message) {
      this.delegate.debug(message);
   }

   public void debug(Object message, Throwable t) {
      this.delegate.debug(message, t);
   }

   public void info(Object message) {
      this.delegate.info(message);
   }

   public void info(Object message, Throwable t) {
      this.delegate.info(message, t);
   }

   public void warn(Object message) {
      this.delegate.warn(message);
   }

   public void warn(Object message, Throwable t) {
      this.delegate.warn(message, t);
   }

   public void error(Object message) {
      this.delegate.error(message);
   }

   public void error(Object message, Throwable t) {
      this.delegate.error(message, t);
   }

   public void fatal(Object message) {
      this.delegate.fatal(message);
   }

   public void fatal(Object message, Throwable t) {
      this.delegate.fatal(message, t);
   }
}
