package org.spark_project.jetty.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.Request;

public class CloseableDoSFilter extends DoSFilter {
   protected void onRequestTimeout(HttpServletRequest request, HttpServletResponse response, Thread handlingThread) {
      Request base_request = Request.getBaseRequest(request);
      base_request.getHttpChannel().getEndPoint().close();
   }
}
