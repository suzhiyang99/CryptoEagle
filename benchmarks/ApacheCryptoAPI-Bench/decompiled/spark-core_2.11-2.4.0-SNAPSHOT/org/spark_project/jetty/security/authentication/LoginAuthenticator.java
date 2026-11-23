package org.spark_project.jetty.security.authentication;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.spark_project.jetty.security.Authenticator;
import org.spark_project.jetty.security.IdentityService;
import org.spark_project.jetty.security.LoginService;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.Response;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.server.session.AbstractSession;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class LoginAuthenticator implements Authenticator {
   private static final Logger LOG = Log.getLogger(LoginAuthenticator.class);
   protected LoginService _loginService;
   protected IdentityService _identityService;
   private boolean _renewSession;

   protected LoginAuthenticator() {
   }

   public void prepareRequest(ServletRequest request) {
   }

   public UserIdentity login(String username, Object password, ServletRequest request) {
      UserIdentity user = this._loginService.login(username, password, request);
      if (user != null) {
         this.renewSession((HttpServletRequest)request, request instanceof Request ? ((Request)request).getResponse() : null);
         return user;
      } else {
         return null;
      }
   }

   public void setConfiguration(Authenticator.AuthConfiguration configuration) {
      this._loginService = configuration.getLoginService();
      if (this._loginService == null) {
         throw new IllegalStateException("No LoginService for " + this + " in " + configuration);
      } else {
         this._identityService = configuration.getIdentityService();
         if (this._identityService == null) {
            throw new IllegalStateException("No IdentityService for " + this + " in " + configuration);
         } else {
            this._renewSession = configuration.isSessionRenewedOnAuthentication();
         }
      }
   }

   public LoginService getLoginService() {
      return this._loginService;
   }

   protected HttpSession renewSession(HttpServletRequest request, HttpServletResponse response) {
      HttpSession httpSession = request.getSession(false);
      if (this._renewSession && httpSession != null) {
         synchronized(httpSession) {
            if (httpSession.getAttribute("org.spark_project.jetty.security.sessionCreatedSecure") != Boolean.TRUE) {
               if (httpSession instanceof AbstractSession) {
                  AbstractSession abstractSession = (AbstractSession)httpSession;
                  String oldId = abstractSession.getId();
                  abstractSession.renewId(request);
                  abstractSession.setAttribute("org.spark_project.jetty.security.sessionCreatedSecure", Boolean.TRUE);
                  if (abstractSession.isIdChanged() && response != null && response instanceof Response) {
                     ((Response)response).addCookie(abstractSession.getSessionManager().getSessionCookie(abstractSession, request.getContextPath(), request.isSecure()));
                  }

                  LOG.debug("renew {}->{}", oldId, abstractSession.getId());
               } else {
                  LOG.warn("Unable to renew session " + httpSession);
               }

               return httpSession;
            }
         }
      }

      return httpSession;
   }
}
