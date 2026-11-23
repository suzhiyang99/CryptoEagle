package org.spark_project.jetty.server.handler;

import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.RequestLog;

public class RequestLogHandler extends HandlerWrapper {
   private RequestLog _requestLog;

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (baseRequest.getDispatcherType() == DispatcherType.REQUEST) {
         baseRequest.getHttpChannel().addRequestLog(this._requestLog);
      }

      if (this._handler != null) {
         this._handler.handle(target, baseRequest, request, response);
      }

   }

   public void setRequestLog(RequestLog requestLog) {
      this.updateBean(this._requestLog, requestLog);
      this._requestLog = requestLog;
   }

   public RequestLog getRequestLog() {
      return this._requestLog;
   }
}
