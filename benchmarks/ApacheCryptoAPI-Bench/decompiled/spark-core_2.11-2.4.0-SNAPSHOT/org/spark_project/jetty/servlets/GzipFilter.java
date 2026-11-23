package org.spark_project.jetty.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

/** @deprecated */
@Deprecated
public class GzipFilter implements Filter {
   private static final Logger LOG = Log.getLogger(GzipFilter.class);

   public void init(FilterConfig filterConfig) throws ServletException {
      LOG.warn("GzipFilter is deprecated. Use GzipHandler");
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      chain.doFilter(request, response);
   }

   public void destroy() {
   }
}
