package org.spark_project.jetty.servlet;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.resource.Resource;

public class JspPropertyGroupServlet extends GenericServlet {
   private static final long serialVersionUID = 3681783214726776945L;
   public static final String NAME = "__org.eclipse.jetty.servlet.JspPropertyGroupServlet__";
   private final ServletHandler _servletHandler;
   private final ContextHandler _contextHandler;
   private ServletHolder _dftServlet;
   private ServletHolder _jspServlet;
   private boolean _starJspMapped;

   public JspPropertyGroupServlet(ContextHandler context, ServletHandler servletHandler) {
      this._contextHandler = context;
      this._servletHandler = servletHandler;
   }

   public void init() throws ServletException {
      String jsp_name = "jsp";
      ServletMapping servlet_mapping = this._servletHandler.getServletMapping("*.jsp");
      if (servlet_mapping != null) {
         this._starJspMapped = true;
         ServletMapping[] mappings = this._servletHandler.getServletMappings();

         for(ServletMapping m : mappings) {
            String[] paths = m.getPathSpecs();
            if (paths != null) {
               for(String path : paths) {
                  if ("*.jsp".equals(path) && !"__org.eclipse.jetty.servlet.JspPropertyGroupServlet__".equals(m.getServletName())) {
                     servlet_mapping = m;
                  }
               }
            }
         }

         jsp_name = servlet_mapping.getServletName();
      }

      this._jspServlet = this._servletHandler.getServlet(jsp_name);
      String dft_name = "default";
      ServletMapping default_mapping = this._servletHandler.getServletMapping("/");
      if (default_mapping != null) {
         dft_name = default_mapping.getServletName();
      }

      this._dftServlet = this._servletHandler.getServlet(dft_name);
   }

   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
      HttpServletRequest request = null;
      if (req instanceof HttpServletRequest) {
         request = (HttpServletRequest)req;
         Object var4 = null;
         Object var5 = null;
         String var9;
         String var10;
         if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            var9 = (String)request.getAttribute("javax.servlet.include.servlet_path");
            var10 = (String)request.getAttribute("javax.servlet.include.path_info");
            if (var9 == null) {
               var9 = request.getServletPath();
               var10 = request.getPathInfo();
            }
         } else {
            var9 = request.getServletPath();
            var10 = request.getPathInfo();
         }

         String pathInContext = URIUtil.addPaths(var9, var10);
         if (pathInContext.endsWith("/")) {
            this._dftServlet.getServlet().service(req, res);
         } else if (this._starJspMapped && pathInContext.toLowerCase(Locale.ENGLISH).endsWith(".jsp")) {
            this._jspServlet.getServlet().service(req, res);
         } else {
            Resource resource = this._contextHandler.getResource(pathInContext);
            if (resource != null && resource.isDirectory()) {
               this._dftServlet.getServlet().service(req, res);
            } else {
               this._jspServlet.getServlet().service(req, res);
            }
         }

      } else {
         throw new ServletException("Request not HttpServletRequest");
      }
   }
}
