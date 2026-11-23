package org.spark_project.jetty.security;

import javax.servlet.ServletContext;
import org.spark_project.jetty.security.authentication.BasicAuthenticator;
import org.spark_project.jetty.security.authentication.ClientCertAuthenticator;
import org.spark_project.jetty.security.authentication.DigestAuthenticator;
import org.spark_project.jetty.security.authentication.FormAuthenticator;
import org.spark_project.jetty.security.authentication.SpnegoAuthenticator;
import org.spark_project.jetty.server.Server;

public class DefaultAuthenticatorFactory implements Authenticator.Factory {
   LoginService _loginService;

   public Authenticator getAuthenticator(Server server, ServletContext context, Authenticator.AuthConfiguration configuration, IdentityService identityService, LoginService loginService) {
      String auth = configuration.getAuthMethod();
      Authenticator authenticator = null;
      if (auth != null && !"BASIC".equalsIgnoreCase(auth)) {
         if ("DIGEST".equalsIgnoreCase(auth)) {
            authenticator = new DigestAuthenticator();
         } else if ("FORM".equalsIgnoreCase(auth)) {
            authenticator = new FormAuthenticator();
         } else if ("SPNEGO".equalsIgnoreCase(auth)) {
            authenticator = new SpnegoAuthenticator();
         } else if ("NEGOTIATE".equalsIgnoreCase(auth)) {
            authenticator = new SpnegoAuthenticator("NEGOTIATE");
         }
      } else {
         authenticator = new BasicAuthenticator();
      }

      if ("CLIENT_CERT".equalsIgnoreCase(auth) || "CLIENT-CERT".equalsIgnoreCase(auth)) {
         authenticator = new ClientCertAuthenticator();
      }

      return authenticator;
   }

   public LoginService getLoginService() {
      return this._loginService;
   }

   public void setLoginService(LoginService loginService) {
      this._loginService = loginService;
   }
}
