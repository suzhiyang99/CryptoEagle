package org.apache.openejb.log.logger;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class AbstractDelegatingLogger extends Logger {
   protected AbstractDelegatingLogger(String name, String resourceBundleName) {
      super(name, resourceBundleName);
   }

   public void log(LogRecord record) {
      if (this.isLoggable(record.getLevel())) {
         this.doLog(record);
      }

   }

   public void log(Level level, String msg) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         this.doLog(lr);
      }

   }

   public void log(Level level, String msg, Object param1) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         Object[] params = new Object[]{param1};
         lr.setParameters(params);
         this.doLog(lr);
      }

   }

   public void log(Level level, String msg, Object[] params) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setParameters(params);
         this.doLog(lr);
      }

   }

   public void log(Level level, String msg, Throwable thrown) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setThrown(thrown);
         this.doLog(lr);
      }

   }

   public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         this.doLog(lr);
      }

   }

   public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         Object[] params = new Object[]{param1};
         lr.setParameters(params);
         this.doLog(lr);
      }

   }

   public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         lr.setParameters(params);
         this.doLog(lr);
      }

   }

   public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         lr.setThrown(thrown);
         this.doLog(lr);
      }

   }

   public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         this.doLog(lr, bundleName);
      }

   }

   public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         Object[] params = new Object[]{param1};
         lr.setParameters(params);
         this.doLog(lr, bundleName);
      }

   }

   public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         lr.setParameters(params);
         this.doLog(lr, bundleName);
      }

   }

   public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
      if (this.isLoggable(level)) {
         LogRecord lr = new LogRecord(level, msg);
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         lr.setThrown(thrown);
         this.doLog(lr, bundleName);
      }

   }

   public void entering(String sourceClass, String sourceMethod) {
      if (this.isLoggable(Level.FINER)) {
         this.logp(Level.FINER, sourceClass, sourceMethod, "ENTRY");
      }

   }

   public void entering(String sourceClass, String sourceMethod, Object param1) {
      if (this.isLoggable(Level.FINER)) {
         Object[] params = new Object[]{param1};
         this.logp(Level.FINER, sourceClass, sourceMethod, "ENTRY {0}", params);
      }

   }

   public void entering(String sourceClass, String sourceMethod, Object[] params) {
      if (this.isLoggable(Level.FINER)) {
         String msg = "ENTRY";
         if (params == null) {
            this.logp(Level.FINER, sourceClass, sourceMethod, "ENTRY");
            return;
         }

         StringBuilder builder = new StringBuilder("ENTRY");

         for(int i = 0; i < params.length; ++i) {
            builder.append(" {");
            builder.append(Integer.toString(i));
            builder.append("}");
         }

         this.logp(Level.FINER, sourceClass, sourceMethod, builder.toString(), params);
      }

   }

   public void exiting(String sourceClass, String sourceMethod) {
      if (this.isLoggable(Level.FINER)) {
         this.logp(Level.FINER, sourceClass, sourceMethod, "RETURN");
      }

   }

   public void exiting(String sourceClass, String sourceMethod, Object result) {
      if (this.isLoggable(Level.FINER)) {
         Object[] params = new Object[]{result};
         this.logp(Level.FINER, sourceClass, sourceMethod, "RETURN {0}", params);
      }

   }

   public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
      if (this.isLoggable(Level.FINER)) {
         LogRecord lr = new LogRecord(Level.FINER, "THROW");
         lr.setSourceClassName(sourceClass);
         lr.setSourceMethodName(sourceMethod);
         lr.setThrown(thrown);
         this.doLog(lr);
      }

   }

   public void severe(String msg) {
      if (this.isLoggable(Level.SEVERE)) {
         LogRecord lr = new LogRecord(Level.SEVERE, msg);
         this.doLog(lr);
      }

   }

   public void warning(String msg) {
      if (this.isLoggable(Level.WARNING)) {
         LogRecord lr = new LogRecord(Level.WARNING, msg);
         this.doLog(lr);
      }

   }

   public void info(String msg) {
      if (this.isLoggable(Level.INFO)) {
         LogRecord lr = new LogRecord(Level.INFO, msg);
         this.doLog(lr);
      }

   }

   public void config(String msg) {
      if (this.isLoggable(Level.CONFIG)) {
         LogRecord lr = new LogRecord(Level.CONFIG, msg);
         this.doLog(lr);
      }

   }

   public void fine(String msg) {
      if (this.isLoggable(Level.FINE)) {
         LogRecord lr = new LogRecord(Level.FINE, msg);
         this.doLog(lr);
      }

   }

   public void finer(String msg) {
      if (this.isLoggable(Level.FINER)) {
         LogRecord lr = new LogRecord(Level.FINER, msg);
         this.doLog(lr);
      }

   }

   public void finest(String msg) {
      if (this.isLoggable(Level.FINEST)) {
         LogRecord lr = new LogRecord(Level.FINEST, msg);
         this.doLog(lr);
      }

   }

   public void setLevel(Level newLevel) throws SecurityException {
      throw new UnsupportedOperationException();
   }

   public abstract Level getLevel();

   public boolean isLoggable(Level level) {
      Level l = this.getLevel();
      return level.intValue() >= l.intValue() && l != Level.OFF;
   }

   protected boolean supportsHandlers() {
      return false;
   }

   public synchronized void addHandler(Handler handler) throws SecurityException {
      if (this.supportsHandlers()) {
         super.addHandler(handler);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public synchronized void removeHandler(Handler handler) throws SecurityException {
      if (this.supportsHandlers()) {
         super.removeHandler(handler);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public synchronized Handler[] getHandlers() {
      if (this.supportsHandlers()) {
         return super.getHandlers();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public synchronized void setUseParentHandlers(boolean useParentHandlers) {
      if (this.supportsHandlers()) {
         super.setUseParentHandlers(useParentHandlers);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public synchronized boolean getUseParentHandlers() {
      if (this.supportsHandlers()) {
         return super.getUseParentHandlers();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public Logger getParent() {
      return null;
   }

   public void setParent(Logger parent) {
      throw new UnsupportedOperationException();
   }

   protected void doLog(LogRecord lr) {
      lr.setLoggerName(this.getName());
      String rbname = this.getResourceBundleName();
      if (rbname != null) {
         lr.setResourceBundleName(rbname);
         lr.setResourceBundle(this.getResourceBundle());
      }

      this.internalLog(lr);
   }

   protected void doLog(LogRecord lr, String rbname) {
      lr.setLoggerName(this.getName());
      if (rbname != null) {
         lr.setResourceBundleName(rbname);
         lr.setResourceBundle(loadResourceBundle(rbname));
      }

      this.internalLog(lr);
   }

   protected void internalLog(LogRecord record) {
      Filter filter = this.getFilter();
      if (filter == null || filter.isLoggable(record)) {
         String msg = this.formatMessage(record);
         this.internalLogFormatted(msg, record);
      }
   }

   protected abstract void internalLogFormatted(String var1, LogRecord var2);

   protected String formatMessage(LogRecord record) {
      String format = record.getMessage();
      ResourceBundle catalog = record.getResourceBundle();
      if (catalog != null) {
         try {
            format = catalog.getString(record.getMessage());
         } catch (MissingResourceException var6) {
            format = record.getMessage();
         }
      }

      try {
         Object[] parameters = record.getParameters();
         if (parameters != null && parameters.length != 0) {
            return format.indexOf("{0") < 0 && format.indexOf("{1") < 0 && format.indexOf("{2") < 0 && format.indexOf("{3") < 0 ? format : MessageFormat.format(format, parameters);
         } else {
            return format;
         }
      } catch (Exception var5) {
         return format;
      }
   }

   static ResourceBundle loadResourceBundle(String resourceBundleName) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (null != cl) {
         try {
            return ResourceBundle.getBundle(resourceBundleName, Locale.getDefault(), cl);
         } catch (MissingResourceException var4) {
         }
      }

      cl = ClassLoader.getSystemClassLoader();
      if (null != cl) {
         try {
            return ResourceBundle.getBundle(resourceBundleName, Locale.getDefault(), cl);
         } catch (MissingResourceException var3) {
         }
      }

      return null;
   }
}
