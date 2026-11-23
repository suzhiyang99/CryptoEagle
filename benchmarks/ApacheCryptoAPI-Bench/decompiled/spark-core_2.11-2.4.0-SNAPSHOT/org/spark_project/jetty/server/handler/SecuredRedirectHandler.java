package org.spark_project.jetty.server.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.HttpChannel;
import org.spark_project.jetty.server.HttpConfiguration;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.util.URIUtil;

public class SecuredRedirectHandler extends AbstractHandler {
   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      HttpChannel channel = baseRequest.getHttpChannel();
      if (!baseRequest.isSecure() && channel != null) {
         HttpConfiguration httpConfig = channel.getHttpConfiguration();
         if (httpConfig == null) {
            response.sendError(403, "No http configuration available");
         } else {
            if (httpConfig.getSecurePort() > 0) {
               String scheme = httpConfig.getSecureScheme();
               int port = httpConfig.getSecurePort();
               String url = URIUtil.newURI(scheme, baseRequest.getServerName(), port, baseRequest.getRequestURI(), baseRequest.getQueryString());
               response.setContentLength(0);
               response.sendRedirect(url);
            } else {
               response.sendError(403, "Not Secure");
            }

            baseRequest.setHandled(true);
         }
      }
   }
}
