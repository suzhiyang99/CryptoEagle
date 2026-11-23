package org.apache.openejb.util;

import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;

public class OptionsLog implements Options.Log {
   private final Logger logger;

   public OptionsLog() {
      this(Logger.getInstance(LogCategory.OPENEJB.createChild("options"), OptionsLog.class));
   }

   public OptionsLog(Logger logger) {
      this.logger = logger;
   }

   public static void install() {
      SystemInstance.get().getOptions().setLogger(new OptionsLog());
   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public boolean isWarningEnabled() {
      return this.logger.isWarningEnabled();
   }

   public void debug(String message) {
      this.logger.debug(message);
   }

   public void debug(String message, Throwable t) {
      this.logger.debug(message, t);
   }

   public void info(String message) {
      this.logger.info(message);
   }

   public void info(String message, Throwable t) {
      this.logger.info(message, t);
   }

   public void warning(String message) {
      this.logger.warning(message);
   }

   public void warning(String message, Throwable t) {
      this.logger.warning(message, t);
   }
}
