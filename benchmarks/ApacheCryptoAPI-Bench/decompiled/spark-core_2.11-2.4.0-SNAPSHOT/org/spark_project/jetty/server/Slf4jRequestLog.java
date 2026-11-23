package org.spark_project.jetty.server;

import java.io.IOException;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.log.Slf4jLog;

@ManagedObject("NCSA standard format request log to slf4j bridge")
public class Slf4jRequestLog extends AbstractNCSARequestLog {
   private Slf4jLog logger;
   private String loggerName = "org.spark_project.jetty.server.RequestLog";

   public void setLoggerName(String loggerName) {
      this.loggerName = loggerName;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   protected boolean isEnabled() {
      return this.logger != null;
   }

   public void write(String requestEntry) throws IOException {
      this.logger.info(requestEntry);
   }

   protected synchronized void doStart() throws Exception {
      this.logger = new Slf4jLog(this.loggerName);
      super.doStart();
   }
}
