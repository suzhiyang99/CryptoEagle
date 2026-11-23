package org.spark_project.jetty.security.authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.security.IdentityService;
import org.spark_project.jetty.security.LoginService;
import org.spark_project.jetty.security.ServerAuthException;
import org.spark_project.jetty.security.UserAuthentication;
import org.spark_project.jetty.server.Authentication;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class DeferredAuthentication implements Authentication.Deferred {
   private static final Logger LOG = Log.getLogger(DeferredAuthentication.class);
   protected final LoginAuthenticator _authenticator;
   private Object _previousAssociation;
   static final HttpServletResponse __deferredResponse = new HttpServletResponse() {
      public void addCookie(Cookie cookie) {
      }

      public void addDateHeader(String name, long date) {
      }

      public void addHeader(String name, String value) {
      }

      public void addIntHeader(String name, int value) {
      }

      public boolean containsHeader(String name) {
         return false;
      }

      public String encodeRedirectURL(String url) {
         return null;
      }

      public String encodeRedirectUrl(String url) {
         return null;
      }

      public String encodeURL(String url) {
         return null;
      }

      public String encodeUrl(String url) {
         return null;
      }

      public void sendError(int sc) throws IOException {
      }

      public void sendError(int sc, String msg) throws IOException {
      }

      public void sendRedirect(String location) throws IOException {
      }

      public void setDateHeader(String name, long date) {
      }

      public void setHeader(String name, String value) {
      }

      public void setIntHeader(String name, int value) {
      }

      public void setStatus(int sc) {
      }

      public void setStatus(int sc, String sm) {
      }

      public void flushBuffer() throws IOException {
      }

      public int getBufferSize() {
         return 1024;
      }

      public String getCharacterEncoding() {
         return null;
      }

      public String getContentType() {
         return null;
      }

      public Locale getLocale() {
         return null;
      }

      public ServletOutputStream getOutputStream() throws IOException {
         return DeferredAuthentication.__nullOut;
      }

      public PrintWriter getWriter() throws IOException {
         return IO.getNullPrintWriter();
      }

      public boolean isCommitted() {
         return true;
      }

      public void reset() {
      }

      public void resetBuffer() {
      }

      public void setBufferSize(int size) {
      }

      public void setCharacterEncoding(String charset) {
      }

      public void setContentLength(int len) {
      }

      public void setContentLengthLong(long len) {
      }

      public void setContentType(String type) {
      }

      public void setLocale(Locale loc) {
      }

      public Collection<String> getHeaderNames() {
         return Collections.emptyList();
      }

      public String getHeader(String arg0) {
         return null;
      }

      public Collection<String> getHeaders(String arg0) {
         return Collections.emptyList();
      }

      public int getStatus() {
         return 0;
      }
   };
   private static ServletOutputStream __nullOut = new ServletOutputStream() {
      public void write(int b) throws IOException {
      }

      public void print(String s) throws IOException {
      }

      public void println(String s) throws IOException {
      }

      public void setWriteListener(WriteListener writeListener) {
      }

      public boolean isReady() {
         return false;
      }
   };

   public DeferredAuthentication(LoginAuthenticator authenticator) {
      if (authenticator == null) {
         throw new NullPointerException("No Authenticator");
      } else {
         this._authenticator = authenticator;
      }
   }

   public Authentication authenticate(ServletRequest request) {
      try {
         Authentication authentication = this._authenticator.validateRequest(request, __deferredResponse, true);
         if (authentication != null && authentication instanceof Authentication.User && !(authentication instanceof Authentication.ResponseSent)) {
            LoginService login_service = this._authenticator.getLoginService();
            IdentityService identity_service = login_service.getIdentityService();
            if (identity_service != null) {
               this._previousAssociation = identity_service.associate(((Authentication.User)authentication).getUserIdentity());
            }

            return authentication;
         }
      } catch (ServerAuthException e) {
         LOG.debug(e);
      }

      return this;
   }

   public Authentication authenticate(ServletRequest request, ServletResponse response) {
      try {
         LoginService login_service = this._authenticator.getLoginService();
         IdentityService identity_service = login_service.getIdentityService();
         Authentication authentication = this._authenticator.validateRequest(request, response, true);
         if (authentication instanceof Authentication.User && identity_service != null) {
            this._previousAssociation = identity_service.associate(((Authentication.User)authentication).getUserIdentity());
         }

         return authentication;
      } catch (ServerAuthException e) {
         LOG.debug(e);
         return this;
      }
   }

   public Authentication login(String username, Object password, ServletRequest request) {
      if (username == null) {
         return null;
      } else {
         UserIdentity identity = this._authenticator.login(username, password, request);
         if (identity != null) {
            IdentityService identity_service = this._authenticator.getLoginService().getIdentityService();
            UserAuthentication authentication = new UserAuthentication("API", identity);
            if (identity_service != null) {
               this._previousAssociation = identity_service.associate(identity);
            }

            return authentication;
         } else {
            return null;
         }
      }
   }

   public Object getPreviousAssociation() {
      return this._previousAssociation;
   }

   public static boolean isDeferred(HttpServletResponse response) {
      return response == __deferredResponse;
   }
}
