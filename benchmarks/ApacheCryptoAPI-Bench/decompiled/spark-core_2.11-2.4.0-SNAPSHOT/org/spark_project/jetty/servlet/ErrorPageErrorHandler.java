package org.spark_project.jetty.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.ErrorHandler;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ErrorPageErrorHandler extends ErrorHandler implements ErrorHandler.ErrorPageMapper {
   public static final String GLOBAL_ERROR_PAGE = "org.spark_project.jetty.server.error_page.global";
   private static final Logger LOG = Log.getLogger(ErrorPageErrorHandler.class);
   protected ServletContext _servletContext;
   private final Map<String, String> _errorPages = new HashMap();
   private final List<ErrorCodeRange> _errorPageList = new ArrayList();

   public String getErrorPage(HttpServletRequest request) {
      String error_page = null;
      PageLookupTechnique pageSource = null;
      Class<?> matchedThrowable = null;

      Throwable th;
      for(th = (Throwable)request.getAttribute("javax.servlet.error.exception"); error_page == null && th != null; th = th instanceof ServletException ? ((ServletException)th).getRootCause() : null) {
         pageSource = ErrorPageErrorHandler.PageLookupTechnique.THROWABLE;
         Class<?> exClass = th.getClass();

         for(error_page = (String)this._errorPages.get(exClass.getName()); error_page == null; error_page = (String)this._errorPages.get(exClass.getName())) {
            exClass = exClass.getSuperclass();
            if (exClass == null) {
               break;
            }
         }

         if (error_page != null) {
            matchedThrowable = exClass;
         }
      }

      Integer errorStatusCode = null;
      if (error_page == null) {
         pageSource = ErrorPageErrorHandler.PageLookupTechnique.STATUS_CODE;
         errorStatusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
         if (errorStatusCode != null) {
            error_page = (String)this._errorPages.get(Integer.toString(errorStatusCode));
            if (error_page == null && this._errorPageList != null) {
               for(int i = 0; i < this._errorPageList.size(); ++i) {
                  ErrorCodeRange errCode = (ErrorCodeRange)this._errorPageList.get(i);
                  if (errCode.isInRange(errorStatusCode)) {
                     error_page = errCode.getUri();
                     break;
                  }
               }
            }
         }
      }

      if (error_page == null) {
         pageSource = ErrorPageErrorHandler.PageLookupTechnique.GLOBAL;
         error_page = (String)this._errorPages.get("org.spark_project.jetty.server.error_page.global");
      }

      if (LOG.isDebugEnabled()) {
         StringBuilder dbg = new StringBuilder();
         dbg.append("getErrorPage(");
         dbg.append(request.getMethod()).append(' ');
         dbg.append(request.getRequestURI());
         dbg.append(") => error_page=").append(error_page);
         switch (pageSource) {
            case THROWABLE:
               dbg.append(" (using matched Throwable ");
               dbg.append(matchedThrowable.getName());
               dbg.append(" / actually thrown as ");
               Throwable originalThrowable = (Throwable)request.getAttribute("javax.servlet.error.exception");
               dbg.append(originalThrowable.getClass().getName());
               dbg.append(')');
               LOG.debug(dbg.toString(), th);
               break;
            case STATUS_CODE:
               dbg.append(" (from status code ");
               dbg.append(errorStatusCode);
               dbg.append(')');
               LOG.debug(dbg.toString());
               break;
            case GLOBAL:
               dbg.append(" (from global default)");
               LOG.debug(dbg.toString());
         }
      }

      return error_page;
   }

   public Map<String, String> getErrorPages() {
      return this._errorPages;
   }

   public void setErrorPages(Map<String, String> errorPages) {
      this._errorPages.clear();
      if (errorPages != null) {
         this._errorPages.putAll(errorPages);
      }

   }

   public void addErrorPage(Class<? extends Throwable> exception, String uri) {
      this._errorPages.put(exception.getName(), uri);
   }

   public void addErrorPage(String exceptionClassName, String uri) {
      this._errorPages.put(exceptionClassName, uri);
   }

   public void addErrorPage(int code, String uri) {
      this._errorPages.put(Integer.toString(code), uri);
   }

   public void addErrorPage(int from, int to, String uri) {
      this._errorPageList.add(new ErrorCodeRange(from, to, uri));
   }

   protected void doStart() throws Exception {
      super.doStart();
      this._servletContext = ContextHandler.getCurrentContext();
   }

   static enum PageLookupTechnique {
      THROWABLE,
      STATUS_CODE,
      GLOBAL;
   }

   private class ErrorCodeRange {
      private int _from;
      private int _to;
      private String _uri;

      ErrorCodeRange(int from, int to, String uri) throws IllegalArgumentException {
         if (from > to) {
            throw new IllegalArgumentException("from>to");
         } else {
            this._from = from;
            this._to = to;
            this._uri = uri;
         }
      }

      boolean isInRange(int value) {
         return value >= this._from && value <= this._to;
      }

      String getUri() {
         return this._uri;
      }

      public String toString() {
         return "from: " + this._from + ",to: " + this._to + ",uri: " + this._uri;
      }
   }
}
