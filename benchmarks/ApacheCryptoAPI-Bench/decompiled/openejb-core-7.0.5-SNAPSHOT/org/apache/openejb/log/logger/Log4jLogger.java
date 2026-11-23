package org.apache.openejb.log.logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jLogger extends AbstractDelegatingLogger {
   private static final Map<Level, org.apache.log4j.Level> TO_LOG4J = new HashMap();
   private static final org.apache.log4j.Level TRACE;
   private final Logger log;

   public Log4jLogger(String name, String resourceBundleName) {
      super(name, resourceBundleName);
      this.log = LogManager.getLogger(name);
   }

   public Level getLevel() {
      org.apache.log4j.Level l = this.log.getEffectiveLevel();
      return l != null ? this.fromL4J(l) : null;
   }

   public void setLevel(Level newLevel) throws SecurityException {
      this.log.setLevel((org.apache.log4j.Level)TO_LOG4J.get(newLevel));
   }

   public synchronized void addHandler(Handler handler) throws SecurityException {
      this.log.addAppender(new HandlerWrapper(handler));
   }

   public synchronized void removeHandler(Handler handler) throws SecurityException {
      this.log.removeAppender("HandlerWrapper-" + handler.hashCode());
   }

   public synchronized Handler[] getHandlers() {
      List<Handler> ret = new ArrayList();
      Enumeration<?> en = this.log.getAllAppenders();

      while(en.hasMoreElements()) {
         Appender ap = (Appender)en.nextElement();
         if (ap instanceof HandlerWrapper) {
            ret.add(((HandlerWrapper)ap).getHandler());
         }
      }

      return (Handler[])ret.toArray(new Handler[ret.size()]);
   }

   protected void internalLogFormatted(String msg, LogRecord record) {
      this.log.log(AbstractDelegatingLogger.class.getName(), (Priority)TO_LOG4J.get(record.getLevel()), msg, record.getThrown());
   }

   private Level fromL4J(org.apache.log4j.Level l) {
      Level l2 = null;
      switch (l.toInt()) {
         case Integer.MIN_VALUE:
            l2 = Level.ALL;
            break;
         case 10000:
            l2 = Level.FINE;
            break;
         case 20000:
            l2 = Level.INFO;
            break;
         case 30000:
            l2 = Level.WARNING;
            break;
         case 40000:
            l2 = Level.SEVERE;
            break;
         case 50000:
            l2 = Level.SEVERE;
            break;
         case Integer.MAX_VALUE:
            l2 = Level.OFF;
            break;
         default:
            if (l.toInt() == TRACE.toInt()) {
               l2 = Level.FINEST;
            }
      }

      return l2;
   }

   static {
      org.apache.log4j.Level t = org.apache.log4j.Level.DEBUG;

      try {
         Field f = org.apache.log4j.Level.class.getField("TRACE");
         t = (org.apache.log4j.Level)f.get((Object)null);
      } catch (Throwable var2) {
      }

      TRACE = t;
      TO_LOG4J.put(Level.ALL, org.apache.log4j.Level.ALL);
      TO_LOG4J.put(Level.SEVERE, org.apache.log4j.Level.ERROR);
      TO_LOG4J.put(Level.WARNING, org.apache.log4j.Level.WARN);
      TO_LOG4J.put(Level.INFO, org.apache.log4j.Level.INFO);
      TO_LOG4J.put(Level.CONFIG, org.apache.log4j.Level.DEBUG);
      TO_LOG4J.put(Level.FINE, org.apache.log4j.Level.DEBUG);
      TO_LOG4J.put(Level.FINER, TRACE);
      TO_LOG4J.put(Level.FINEST, TRACE);
      TO_LOG4J.put(Level.OFF, org.apache.log4j.Level.OFF);
   }

   private class HandlerWrapper extends AppenderSkeleton {
      Handler handler;

      public HandlerWrapper(Handler h) {
         this.handler = h;
         this.name = "HandlerWrapper-" + h.hashCode();
      }

      public Handler getHandler() {
         return this.handler;
      }

      protected void append(LoggingEvent event) {
         LogRecord lr = new LogRecord(Log4jLogger.this.fromL4J(event.getLevel()), event.getMessage().toString());
         lr.setLoggerName(event.getLoggerName());
         if (event.getThrowableInformation() != null) {
            lr.setThrown(event.getThrowableInformation().getThrowable());
         }

         String rbname = Log4jLogger.this.getResourceBundleName();
         if (rbname != null) {
            lr.setResourceBundleName(rbname);
            lr.setResourceBundle(Log4jLogger.this.getResourceBundle());
         }

         this.handler.publish(lr);
      }

      public void close() {
         this.handler.close();
         this.closed = true;
      }

      public boolean requiresLayout() {
         return false;
      }

      public Priority getThreshold() {
         return (Priority)Log4jLogger.TO_LOG4J.get(this.handler.getLevel());
      }

      public boolean isAsSevereAsThreshold(Priority priority) {
         Priority p = this.getThreshold();
         return p == null || priority.isGreaterOrEqual(p);
      }
   }
}
