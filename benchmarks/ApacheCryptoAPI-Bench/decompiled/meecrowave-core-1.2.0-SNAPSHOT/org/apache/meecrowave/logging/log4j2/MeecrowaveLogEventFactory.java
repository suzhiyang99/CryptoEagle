package org.apache.meecrowave.logging.log4j2;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;

public class MeecrowaveLogEventFactory extends DefaultLogEventFactory {
   public LogEvent createEvent(String loggerName, Marker marker, String fqcn, Level level, Message data, List<Property> properties, Throwable t) {
      return new MeecrowaveLog4jLogEvent(loggerName, marker, fqcn, level, data, properties, t);
   }

   public static class MeecrowaveLog4jLogEvent extends Log4jLogEvent {
      private StackTraceElement source;

      public MeecrowaveLog4jLogEvent(String loggerName, Marker marker, String fqcn, Level level, Message data, List<Property> properties, Throwable t) {
         super(loggerName, marker, fqcn, level, data, properties, t);
      }

      public StackTraceElement getSource() {
         if (this.source != null) {
            return this.source;
         } else if (this.getLoggerFqcn() != null && this.isIncludeLocation()) {
            StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();

            for(int i = stackTrace.length - 1; i > 0; --i) {
               String className = stackTrace[i].getClassName();
               if (this.getLoggerFqcn().equals(className) && stackTrace.length > i + 1) {
                  this.source = stackTrace[i + 1];
                  if (i + 3 < stackTrace.length && stackTrace[i + 2].getClassName().equals("org.apache.meecrowave.logging.tomcat.LogFacade")) {
                     this.source = stackTrace[i + 3];
                  }
                  break;
               }
            }

            return this.source;
         } else {
            return null;
         }
      }
   }
}
