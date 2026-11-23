package org.apache.openejb.openjpa;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.openejb.log.LoggerCreator;
import org.apache.openejb.util.JuliLogStream;
import org.apache.openjpa.lib.log.Log;

public class JULOpenJPALog implements Log {
   private final LoggerCreator logger;
   private final AtomicBoolean debug = new AtomicBoolean(false);
   private final AtomicBoolean info = new AtomicBoolean(true);

   public JULOpenJPALog(LoggerCreator delegate) {
      this.logger = delegate;
   }

   private Logger logger() {
      return LoggerCreator.Get.exec(this.logger, this.debug, this.info);
   }

   public boolean isTraceEnabled() {
      this.logger();
      return this.debug.get();
   }

   public boolean isInfoEnabled() {
      this.logger();
      return this.info.get();
   }

   public boolean isWarnEnabled() {
      return this.logger().isLoggable(Level.WARNING);
   }

   public boolean isErrorEnabled() {
      return this.logger().isLoggable(Level.SEVERE);
   }

   public boolean isFatalEnabled() {
      return this.logger().isLoggable(Level.SEVERE);
   }

   public void trace(Object o) {
      if (this.isTraceEnabled()) {
         this.logger().log(this.record(o, Level.FINEST));
      }

   }

   public void trace(Object o, Throwable t) {
      if (this.isTraceEnabled()) {
         this.logger().log(this.record(o, t, Level.FINEST));
      }

   }

   public void info(Object o) {
      this.logger().log(this.record(o, Level.INFO));
   }

   public void info(Object o, Throwable t) {
      this.logger().log(this.record(o, t, Level.INFO));
   }

   public void warn(Object o) {
      this.logger().log(this.record(o, Level.WARNING));
   }

   public void warn(Object o, Throwable t) {
      this.logger().log(this.record(o, t, Level.WARNING));
   }

   public void error(Object o) {
      this.logger().log(this.record(o.toString(), Level.SEVERE));
   }

   public void error(Object o, Throwable t) {
      this.logger().log(this.record(o, t, Level.SEVERE));
   }

   public void fatal(Object o) {
      this.logger().log(this.record(o, Level.SEVERE));
   }

   public void fatal(Object o, Throwable t) {
      this.logger().log(this.record(o, t, Level.SEVERE));
   }

   private LogRecord record(Object o, Throwable t, Level level) {
      LogRecord record = this.record(o, level);
      record.setThrown(t);
      return record;
   }

   private LogRecord record(Object o, Level level) {
      LogRecord record = new JuliLogStream.OpenEJBLogRecord(level, o.toString());
      record.setSourceMethodName(this.logger().getName());
      return record;
   }
}
