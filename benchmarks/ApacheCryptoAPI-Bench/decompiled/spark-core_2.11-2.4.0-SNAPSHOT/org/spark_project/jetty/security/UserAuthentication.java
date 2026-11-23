package org.spark_project.jetty.security;

import org.spark_project.jetty.server.UserIdentity;

public class UserAuthentication extends AbstractUserAuthentication {
   public UserAuthentication(String method, UserIdentity userIdentity) {
      super(method, userIdentity);
   }

   public String toString() {
      return "{User," + this.getAuthMethod() + "," + this._userIdentity + "}";
   }

   public void logout() {
      SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
      if (security != null) {
         security.logout(this);
      }

   }
}
