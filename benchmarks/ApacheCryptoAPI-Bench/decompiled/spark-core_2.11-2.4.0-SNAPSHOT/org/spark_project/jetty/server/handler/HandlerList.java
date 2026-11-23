package org.spark_project.jetty.server.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.Request;

public class HandlerList extends HandlerCollection {
   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      Handler[] handlers = this.getHandlers();
      if (handlers != null && this.isStarted()) {
         for(int i = 0; i < handlers.length; ++i) {
            handlers[i].handle(target, baseRequest, request, response);
            if (baseRequest.isHandled()) {
               return;
            }
         }
      }

   }
}
