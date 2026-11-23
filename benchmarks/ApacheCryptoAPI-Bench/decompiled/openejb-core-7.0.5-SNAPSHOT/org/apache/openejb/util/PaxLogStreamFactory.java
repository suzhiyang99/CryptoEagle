package org.apache.openejb.util;

public class PaxLogStreamFactory implements LogStreamFactory {
   public LogStream createLogStream(LogCategory logCategory) {
      return new Log4jLogStream(logCategory);
   }
}
