package org.apache.meecrowave.tomcat;

import java.io.CharArrayWriter;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.meecrowave.logging.tomcat.LogFacade;

public class LoggingAccessLogPattern extends AbstractAccessLogValve {
   private final LogFacade logger = new LogFacade(LoggingAccessLogPattern.class.getName());

   public LoggingAccessLogPattern(String pattern) {
      this.setAsyncSupported(true);
      this.setPattern(pattern);
   }

   protected void log(CharArrayWriter message) {
      this.logger.info(message);
   }
}
