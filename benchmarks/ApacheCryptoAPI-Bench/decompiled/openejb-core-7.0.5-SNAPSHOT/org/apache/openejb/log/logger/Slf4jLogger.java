package org.apache.openejb.log.logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

public class Slf4jLogger extends AbstractDelegatingLogger {
   private static final String FQCN = AbstractDelegatingLogger.class.getName();
   private final Logger logger;
   private LocationAwareLogger locationAwareLogger;

   public Slf4jLogger(String name, String resourceBundleName) {
      super(name, resourceBundleName);
      this.logger = LoggerFactory.getLogger(name);
      if (this.logger instanceof LocationAwareLogger) {
         this.locationAwareLogger = (LocationAwareLogger)this.logger;
      }

   }

   protected boolean supportsHandlers() {
      return true;
   }

   public Level getLevel() {
      Level level;
      if (this.logger.isTraceEnabled()) {
         level = Level.FINEST;
      } else if (this.logger.isDebugEnabled()) {
         level = Level.FINER;
      } else if (this.logger.isInfoEnabled()) {
         level = Level.INFO;
      } else if (this.logger.isWarnEnabled()) {
         level = Level.WARNING;
      } else if (this.logger.isErrorEnabled()) {
         level = Level.SEVERE;
      } else {
         level = Level.OFF;
      }

      return level;
   }

   public boolean isLoggable(Level level) {
      int i = level.intValue();
      if (i == Level.OFF.intValue()) {
         return false;
      } else if (i >= Level.SEVERE.intValue()) {
         return this.logger.isErrorEnabled();
      } else if (i >= Level.WARNING.intValue()) {
         return this.logger.isWarnEnabled();
      } else if (i >= Level.INFO.intValue()) {
         return this.logger.isInfoEnabled();
      } else {
         return i >= Level.FINER.intValue() ? this.logger.isDebugEnabled() : this.logger.isTraceEnabled();
      }
   }

   protected void internalLogFormatted(String msg, LogRecord record) {
      Level level = record.getLevel();
      Throwable t = record.getThrown();
      Handler[] targets = this.getHandlers();
      if (targets != null) {
         for(Handler h : targets) {
            h.publish(record);
         }
      }

      if (this.getUseParentHandlers()) {
         if (Level.FINE.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.debug(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 10, msg, (Object[])null, t);
            }
         } else if (Level.INFO.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.info(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 20, msg, (Object[])null, t);
            }
         } else if (Level.WARNING.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.warn(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 30, msg, (Object[])null, t);
            }
         } else if (Level.FINER.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.trace(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 10, msg, (Object[])null, t);
            }
         } else if (Level.FINEST.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.trace(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 0, msg, (Object[])null, t);
            }
         } else if (Level.ALL.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.error(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 40, msg, (Object[])null, t);
            }
         } else if (Level.SEVERE.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.error(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 40, msg, (Object[])null, t);
            }
         } else if (Level.CONFIG.equals(level)) {
            if (this.locationAwareLogger == null) {
               this.logger.debug(msg, t);
            } else {
               this.locationAwareLogger.log((Marker)null, FQCN, 10, msg, (Object[])null, t);
            }
         }

      }
   }
}
