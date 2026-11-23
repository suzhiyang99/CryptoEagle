package org.spark_project.jetty.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class WelcomeFilter implements Filter {
   private String welcome;

   public void init(FilterConfig filterConfig) {
      this.welcome = filterConfig.getInitParameter("welcome");
      if (this.welcome == null) {
         this.welcome = "index.html";
      }

   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      String path = ((HttpServletRequest)request).getServletPath();
      if (this.welcome != null && path.endsWith("/")) {
         request.getRequestDispatcher(path + this.welcome).forward(request, response);
      } else {
         chain.doFilter(request, response);
      }

   }

   public void destroy() {
   }
}
