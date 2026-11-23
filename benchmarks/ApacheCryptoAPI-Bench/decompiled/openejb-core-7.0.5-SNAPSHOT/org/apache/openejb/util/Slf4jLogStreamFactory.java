package org.apache.openejb.util;

public class Slf4jLogStreamFactory implements LogStreamFactory {
   public LogStream createLogStream(LogCategory logCategory) {
      return new Slf4jLogStream(logCategory);
   }

   public Slf4jLogStreamFactory() {
      JavaSecurityManagers.setSystemProperty("openjpa.Log", "slf4j");
      JavaSecurityManagers.setSystemProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Slf4jLogger");
      JavaSecurityManagers.setSystemProperty("openwebbeans.logging.factory", "org.apache.openejb.cdi.logging.Slf4jLoggerFactory");
   }
}
