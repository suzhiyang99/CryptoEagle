package org.spark_project.jetty.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.HandlerWrapper;
import org.spark_project.jetty.util.ArrayUtil;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class Invoker extends HttpServlet {
   private static final Logger LOG = Log.getLogger(Invoker.class);
   private ContextHandler _contextHandler;
   private ServletHandler _servletHandler;
   private Map.Entry<String, ServletHolder> _invokerEntry;
   private Map<String, String> _parameters;
   private boolean _nonContextServlets;
   private boolean _verbose;

   public void init() {
      ServletContext config = this.getServletContext();
      this._contextHandler = ((ContextHandler.Context)config).getContextHandler();

      Handler handler;
      for(handler = this._contextHandler.getHandler(); handler != null && !(handler instanceof ServletHandler) && handler instanceof HandlerWrapper; handler = ((HandlerWrapper)handler).getHandler()) {
      }

      this._servletHandler = (ServletHandler)handler;
      Enumeration<String> e = this.getInitParameterNames();

      while(e.hasMoreElements()) {
         String param = (String)e.nextElement();
         String value = this.getInitParameter(param);
         String lvalue = value.toLowerCase(Locale.ENGLISH);
         if ("nonContextServlets".equals(param)) {
            this._nonContextServlets = value.length() > 0 && lvalue.startsWith("t");
         }

         if (!"verbose".equals(param)) {
            if (this._parameters == null) {
               this._parameters = new HashMap();
            }

            this._parameters.put(param, value);
         } else {
            this._verbose = value.length() > 0 && lvalue.startsWith("t");
         }
      }

   }

   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      boolean included = false;
      String servlet_path = (String)request.getAttribute("javax.servlet.include.servlet_path");
      if (servlet_path == null) {
         servlet_path = request.getServletPath();
      } else {
         included = true;
      }

      String path_info = (String)request.getAttribute("javax.servlet.include.path_info");
      if (path_info == null) {
         path_info = request.getPathInfo();
      }

      if (path_info != null && path_info.length() > 1) {
         int i0 = path_info.charAt(0) == '/' ? 1 : 0;
         int i1 = path_info.indexOf(47, i0);
         String servlet = i1 < 0 ? path_info.substring(i0) : path_info.substring(i0, i1);
         ServletHolder[] holders = this._servletHandler.getServlets();
         ServletHolder holder = this.getHolder(holders, servlet);
         if (holder != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Adding servlet mapping for named servlet:" + servlet + ":" + URIUtil.addPaths(servlet_path, servlet) + "/*");
            }

            ServletMapping mapping = new ServletMapping();
            mapping.setServletName(servlet);
            mapping.setPathSpec(URIUtil.addPaths(servlet_path, servlet) + "/*");
            this._servletHandler.setServletMappings((ServletMapping[])ArrayUtil.addToArray(this._servletHandler.getServletMappings(), mapping, ServletMapping.class));
         } else {
            if (servlet.endsWith(".class")) {
               servlet = servlet.substring(0, servlet.length() - 6);
            }

            if (servlet == null || servlet.length() == 0) {
               response.sendError(404);
               return;
            }

            synchronized(this._servletHandler) {
               this._invokerEntry = this._servletHandler.getHolderEntry(servlet_path);
               String path = URIUtil.addPaths(servlet_path, servlet);
               Map.Entry<String, ServletHolder> entry = this._servletHandler.getHolderEntry(path);
               if (entry != null && !entry.equals(this._invokerEntry)) {
                  holder = (ServletHolder)entry.getValue();
               } else {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Making new servlet=" + servlet + " with path=" + path + "/*");
                  }

                  holder = this._servletHandler.addServletWithMapping(servlet, path + "/*");
                  if (this._parameters != null) {
                     holder.setInitParameters(this._parameters);
                  }

                  try {
                     holder.start();
                  } catch (Exception e) {
                     LOG.debug(e);
                     throw new UnavailableException(e.toString());
                  }

                  if (!this._nonContextServlets) {
                     Object s = holder.getServlet();
                     if (this._contextHandler.getClassLoader() != s.getClass().getClassLoader()) {
                        try {
                           holder.stop();
                        } catch (Exception e) {
                           LOG.ignore(e);
                        }

                        LOG.warn("Dynamic servlet " + s + " not loaded from context " + request.getContextPath());
                        throw new UnavailableException("Not in context");
                     }
                  }

                  if (this._verbose && LOG.isDebugEnabled()) {
                     LOG.debug("Dynamic load '" + servlet + "' at " + path);
                  }
               }
            }
         }

         if (holder != null) {
            Request baseRequest = Request.getBaseRequest(request);
            holder.handle(baseRequest, new InvokedRequest(request, included, servlet, servlet_path, path_info), response);
         } else {
            LOG.info("Can't find holder for servlet: " + servlet);
            response.sendError(404);
         }

      } else {
         response.sendError(404);
      }
   }

   private ServletHolder getHolder(ServletHolder[] holders, String servlet) {
      if (holders == null) {
         return null;
      } else {
         ServletHolder holder = null;

         for(int i = 0; holder == null && i < holders.length; ++i) {
            if (holders[i].getName().equals(servlet)) {
               holder = holders[i];
            }
         }

         return holder;
      }
   }

   class InvokedRequest extends HttpServletRequestWrapper {
      String _servletPath;
      String _pathInfo;
      boolean _included;

      InvokedRequest(HttpServletRequest request, boolean included, String name, String servletPath, String pathInfo) {
         super(request);
         this._included = included;
         this._servletPath = URIUtil.addPaths(servletPath, name);
         this._pathInfo = pathInfo.substring(name.length() + 1);
         if (this._pathInfo.length() == 0) {
            this._pathInfo = null;
         }

      }

      public String getServletPath() {
         return this._included ? super.getServletPath() : this._servletPath;
      }

      public String getPathInfo() {
         return this._included ? super.getPathInfo() : this._pathInfo;
      }

      public Object getAttribute(String name) {
         if (this._included) {
            if (name.equals("javax.servlet.include.request_uri")) {
               return URIUtil.addPaths(URIUtil.addPaths(this.getContextPath(), this._servletPath), this._pathInfo);
            }

            if (name.equals("javax.servlet.include.path_info")) {
               return this._pathInfo;
            }

            if (name.equals("javax.servlet.include.servlet_path")) {
               return this._servletPath;
            }
         }

         return super.getAttribute(name);
      }
   }
}
