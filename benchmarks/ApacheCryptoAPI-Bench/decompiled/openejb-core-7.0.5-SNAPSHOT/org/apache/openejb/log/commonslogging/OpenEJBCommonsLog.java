package org.apache.openejb.log.commonslogging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class OpenEJBCommonsLog implements Log, Serializable {
   private transient Logger logger;
   private final String category;

   public OpenEJBCommonsLog(String category) {
      this.category = category;
      this.logger = Logger.getInstance(LogCategory.OPENEJB, category);
   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public boolean isErrorEnabled() {
      return this.logger.isErrorEnabled();
   }

   public boolean isFatalEnabled() {
      return this.logger.isFatalEnabled();
   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public boolean isTraceEnabled() {
      return this.logger.isDebugEnabled();
   }

   public boolean isWarnEnabled() {
      return this.logger.isWarningEnabled();
   }

   public void trace(Object message) {
      this.logger.debug(String.valueOf(message));
   }

   public void trace(Object message, Throwable t) {
      this.logger.debug(String.valueOf(message), t);
   }

   public void debug(Object message) {
      this.logger.debug(String.valueOf(message));
   }

   public void debug(Object message, Throwable t) {
      this.logger.debug(String.valueOf(message), t);
   }

   public void info(Object message) {
      this.logger.info(String.valueOf(message));
   }

   public void info(Object message, Throwable t) {
      this.logger.info(String.valueOf(message), t);
   }

   public void warn(Object message) {
      this.logger.warning(String.valueOf(message));
   }

   public void warn(Object message, Throwable t) {
      this.logger.warning(String.valueOf(message), t);
   }

   public void error(Object message) {
      this.logger.error(String.valueOf(message));
   }

   public void error(Object message, Throwable t) {
      this.logger.error(String.valueOf(message), t);
   }

   public void fatal(Object message) {
      this.logger.fatal(String.valueOf(message));
   }

   public void fatal(Object message, Throwable t) {
      this.logger.fatal(String.valueOf(message), t);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeUTF(this.category);
   }

   private void readObject(ObjectInputStream in) throws IOException {
      this.logger = Logger.getInstance(LogCategory.OPENEJB, in.readUTF());
   }
}
