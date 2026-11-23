package org.spark_project.jetty.util.log;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class Slf4jLog extends AbstractLogger {
   private final org.slf4j.Logger _logger;

   public Slf4jLog() throws Exception {
      this("org.spark_project.jetty.util.log");
   }

   public Slf4jLog(String name) {
      org.slf4j.Logger logger = LoggerFactory.getLogger(name);
      if (logger instanceof LocationAwareLogger) {
         this._logger = new JettyAwareLogger((LocationAwareLogger)logger);
      } else {
         this._logger = logger;
      }

   }

   public String getName() {
      return this._logger.getName();
   }

   public void warn(String msg, Object... args) {
      this._logger.warn(msg, args);
   }

   public void warn(Throwable thrown) {
      this.warn("", thrown);
   }

   public void warn(String msg, Throwable thrown) {
      this._logger.warn(msg, thrown);
   }

   public void info(String msg, Object... args) {
      this._logger.info(msg, args);
   }

   public void info(Throwable thrown) {
      this.info("", thrown);
   }

   public void info(String msg, Throwable thrown) {
      this._logger.info(msg, thrown);
   }

   public void debug(String msg, Object... args) {
      this._logger.debug(msg, args);
   }

   public void debug(String msg, long arg) {
      if (this.isDebugEnabled()) {
         this._logger.debug(msg, new Object[]{new Long(arg)});
      }

   }

   public void debug(Throwable thrown) {
      this.debug("", thrown);
   }

   public void debug(String msg, Throwable thrown) {
      this._logger.debug(msg, thrown);
   }

   public boolean isDebugEnabled() {
      return this._logger.isDebugEnabled();
   }

   public void setDebugEnabled(boolean enabled) {
      this.warn("setDebugEnabled not implemented", null, null);
   }

   protected Logger newLogger(String fullname) {
      return new Slf4jLog(fullname);
   }

   public void ignore(Throwable ignored) {
      if (Log.isIgnored()) {
         this.debug("IGNORED EXCEPTION ", ignored);
      }

   }

   public String toString() {
      return this._logger.toString();
   }
}
