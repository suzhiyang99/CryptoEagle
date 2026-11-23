package org.apache.meecrowave.logging.jul;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.cxf.common.logging.AbstractDelegatingLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.status.StatusLogger;

public class Log4j2Logger extends AbstractDelegatingLogger {
   private static final Map<Level, org.apache.logging.log4j.Level> TO_LOG4J = new HashMap();
   private final Logger log;

   public Log4j2Logger(String name, String resourceBundleName) {
      super(name, resourceBundleName);
      this.log = LogManager.getLogger(name);
   }

   public void setLevel(Level newLevel) throws SecurityException {
      if (org.apache.logging.log4j.core.Logger.class.isInstance(this.log)) {
         ((org.apache.logging.log4j.core.Logger)org.apache.logging.log4j.core.Logger.class.cast(this.log)).setLevel((org.apache.logging.log4j.Level)TO_LOG4J.get(newLevel));
      } else if (StatusLogger.class.isInstance(this.log)) {
         ((StatusLogger)StatusLogger.class.cast(this.log)).setLevel((org.apache.logging.log4j.Level)TO_LOG4J.get(newLevel));
      } else if (SimpleLogger.class.isInstance(this.log)) {
         ((SimpleLogger)SimpleLogger.class.cast(this.log)).setLevel((org.apache.logging.log4j.Level)TO_LOG4J.get(newLevel));
      }

   }

   public Level getLevel() {
      org.apache.logging.log4j.Level l = this.log.getLevel();
      return l != null ? this.fromL4J(l) : null;
   }

   protected void internalLogFormatted(String msg, LogRecord record) {
      this.log.log((org.apache.logging.log4j.Level)TO_LOG4J.get(record.getLevel()), msg, record.getThrown());
   }

   private Level fromL4J(org.apache.logging.log4j.Level l) {
      Level l2 = null;
      switch (l.getStandardLevel()) {
         case ALL:
            l2 = Level.ALL;
            break;
         case FATAL:
            l2 = Level.SEVERE;
            break;
         case ERROR:
            l2 = Level.SEVERE;
            break;
         case WARN:
            l2 = Level.WARNING;
            break;
         case INFO:
            l2 = Level.INFO;
            break;
         case DEBUG:
            l2 = Level.FINE;
            break;
         case OFF:
            l2 = Level.OFF;
            break;
         case TRACE:
            l2 = Level.FINEST;
            break;
         default:
            l2 = Level.FINE;
      }

      return l2;
   }

   static {
      org.apache.logging.log4j.Level t = org.apache.logging.log4j.Level.DEBUG;
      TO_LOG4J.put(Level.ALL, org.apache.logging.log4j.Level.ALL);
      TO_LOG4J.put(Level.SEVERE, org.apache.logging.log4j.Level.ERROR);
      TO_LOG4J.put(Level.WARNING, org.apache.logging.log4j.Level.WARN);
      TO_LOG4J.put(Level.INFO, org.apache.logging.log4j.Level.INFO);
      TO_LOG4J.put(Level.CONFIG, org.apache.logging.log4j.Level.DEBUG);
      TO_LOG4J.put(Level.FINE, org.apache.logging.log4j.Level.DEBUG);
      TO_LOG4J.put(Level.FINER, org.apache.logging.log4j.Level.TRACE);
      TO_LOG4J.put(Level.FINEST, org.apache.logging.log4j.Level.TRACE);
      TO_LOG4J.put(Level.OFF, org.apache.logging.log4j.Level.OFF);
   }
}
