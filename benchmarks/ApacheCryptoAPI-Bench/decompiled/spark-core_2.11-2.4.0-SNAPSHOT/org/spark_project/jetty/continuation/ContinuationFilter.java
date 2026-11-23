package org.spark_project.jetty.continuation;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ContinuationFilter implements Filter {
   static boolean _initialized;
   static boolean __debug;
   private boolean _faux;
   private boolean _filtered;
   ServletContext _context;
   private boolean _debug;

   public void init(FilterConfig filterConfig) throws ServletException {
      boolean jetty_7_or_greater = "org.spark_project.jetty.servlet".equals(filterConfig.getClass().getPackage().getName());
      this._context = filterConfig.getServletContext();
      String param = filterConfig.getInitParameter("debug");
      this._debug = param != null && Boolean.parseBoolean(param);
      if (this._debug) {
         __debug = true;
      }

      param = filterConfig.getInitParameter("partial");
      param = filterConfig.getInitParameter("faux");
      if (param != null) {
         this._faux = Boolean.parseBoolean(param);
      } else {
         this._faux = !jetty_7_or_greater && this._context.getMajorVersion() < 3;
      }

      this._filtered = this._faux;
      if (this._debug) {
         this._context.log("ContinuationFilter  jetty=" + jetty_7_or_greater + " faux=" + this._faux + " filtered=" + this._filtered + " servlet3=" + ContinuationSupport.__servlet3);
      }

      _initialized = true;
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      if (this._filtered) {
         Continuation c = (Continuation)request.getAttribute("org.spark_project.jetty.continuation");
         FilteredContinuation fc;
         if (!this._faux || c != null && c instanceof FauxContinuation) {
            fc = (FilteredContinuation)c;
         } else {
            fc = new FauxContinuation(request);
            request.setAttribute("org.spark_project.jetty.continuation", fc);
         }

         boolean complete = false;

         while(!complete) {
            try {
               if (fc == null || fc.enter(response)) {
                  chain.doFilter(request, response);
               }
            } catch (ContinuationThrowable e) {
               this.debug("faux", e);
            } finally {
               if (fc == null) {
                  fc = (FilteredContinuation)request.getAttribute("org.spark_project.jetty.continuation");
               }

               complete = fc == null || fc.exit();
            }
         }
      } else {
         try {
            chain.doFilter(request, response);
         } catch (ContinuationThrowable e) {
            this.debug("caught", e);
         }
      }

   }

   private void debug(String string) {
      if (this._debug) {
         this._context.log(string);
      }

   }

   private void debug(String string, Throwable th) {
      if (this._debug) {
         if (th instanceof ContinuationThrowable) {
            this._context.log(string + ":" + th);
         } else {
            this._context.log(string, th);
         }
      }

   }

   public void destroy() {
   }

   public interface FilteredContinuation extends Continuation {
      boolean enter(ServletResponse var1);

      boolean exit();
   }
}
