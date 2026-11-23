package org.apache.openejb.log;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class RemoveLogMessage implements Filter {
   private final Level level;
   private final String message;
   private final Filter wrapped;

   public RemoveLogMessage(Filter filter, Level lvl, String msg) {
      this.wrapped = filter;
      this.level = lvl;
      this.message = msg;
   }

   public boolean isLoggable(LogRecord record) {
      if (this.wrapped != null && !this.wrapped.isLoggable(record)) {
         return false;
      } else {
         return !this.level.equals(record.getLevel()) || !this.message.equals(record.getMessage());
      }
   }
}
