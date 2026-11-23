package org.spark_project.jetty.security.authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.security.ServerAuthException;
import org.spark_project.jetty.security.UserAuthentication;
import org.spark_project.jetty.server.Authentication;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.B64Code;

public class BasicAuthenticator extends LoginAuthenticator {
   public String getAuthMethod() {
      return "BASIC";
   }

   public Authentication validateRequest(ServletRequest req, ServletResponse res, boolean mandatory) throws ServerAuthException {
      HttpServletRequest request = (HttpServletRequest)req;
      HttpServletResponse response = (HttpServletResponse)res;
      String credentials = request.getHeader(HttpHeader.AUTHORIZATION.asString());

      try {
         if (!mandatory) {
            return new DeferredAuthentication(this);
         } else {
            if (credentials != null) {
               int space = credentials.indexOf(32);
               if (space > 0) {
                  String method = credentials.substring(0, space);
                  if ("basic".equalsIgnoreCase(method)) {
                     credentials = credentials.substring(space + 1);
                     credentials = B64Code.decode(credentials, StandardCharsets.ISO_8859_1);
                     int i = credentials.indexOf(58);
                     if (i > 0) {
                        String username = credentials.substring(0, i);
                        String password = credentials.substring(i + 1);
                        UserIdentity user = this.login(username, password, request);
                        if (user != null) {
                           return new UserAuthentication(this.getAuthMethod(), user);
                        }
                     }
                  }
               }
            }

            if (DeferredAuthentication.isDeferred(response)) {
               return Authentication.UNAUTHENTICATED;
            } else {
               response.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), "basic realm=\"" + this._loginService.getName() + '"');
               response.sendError(401);
               return Authentication.SEND_CONTINUE;
            }
         }
      } catch (IOException e) {
         throw new ServerAuthException(e);
      }
   }

   public boolean secureResponse(ServletRequest req, ServletResponse res, boolean mandatory, Authentication.User validatedUser) throws ServerAuthException {
      return true;
   }
}
