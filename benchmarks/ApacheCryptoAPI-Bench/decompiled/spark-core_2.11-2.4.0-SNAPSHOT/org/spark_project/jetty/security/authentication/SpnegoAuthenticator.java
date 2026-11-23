package org.spark_project.jetty.security.authentication;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.security.ServerAuthException;
import org.spark_project.jetty.security.UserAuthentication;
import org.spark_project.jetty.server.Authentication;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class SpnegoAuthenticator extends LoginAuthenticator {
   private static final Logger LOG = Log.getLogger(SpnegoAuthenticator.class);
   private String _authMethod = "SPNEGO";

   public SpnegoAuthenticator() {
   }

   public SpnegoAuthenticator(String authMethod) {
      this._authMethod = authMethod;
   }

   public String getAuthMethod() {
      return this._authMethod;
   }

   public Authentication validateRequest(ServletRequest request, ServletResponse response, boolean mandatory) throws ServerAuthException {
      HttpServletRequest req = (HttpServletRequest)request;
      HttpServletResponse res = (HttpServletResponse)response;
      String header = req.getHeader(HttpHeader.AUTHORIZATION.asString());
      if (!mandatory) {
         return new DeferredAuthentication(this);
      } else if (header == null) {
         try {
            if (DeferredAuthentication.isDeferred(res)) {
               return Authentication.UNAUTHENTICATED;
            } else {
               LOG.debug("SpengoAuthenticator: sending challenge");
               res.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), HttpHeader.NEGOTIATE.asString());
               res.sendError(401);
               return Authentication.SEND_CONTINUE;
            }
         } catch (IOException ioe) {
            throw new ServerAuthException(ioe);
         }
      } else {
         if (header != null && header.startsWith(HttpHeader.NEGOTIATE.asString())) {
            String spnegoToken = header.substring(10);
            UserIdentity user = this.login((String)null, spnegoToken, request);
            if (user != null) {
               return new UserAuthentication(this.getAuthMethod(), user);
            }
         }

         return Authentication.UNAUTHENTICATED;
      }
   }

   public boolean secureResponse(ServletRequest request, ServletResponse response, boolean mandatory, Authentication.User validatedUser) throws ServerAuthException {
      return true;
   }
}
