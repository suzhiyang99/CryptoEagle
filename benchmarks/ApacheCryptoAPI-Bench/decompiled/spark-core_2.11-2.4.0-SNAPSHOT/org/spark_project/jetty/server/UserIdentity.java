package org.spark_project.jetty.server;

import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;

public interface UserIdentity {
   UserIdentity UNAUTHENTICATED_IDENTITY = new UnauthenticatedUserIdentity() {
      public Subject getSubject() {
         return null;
      }

      public Principal getUserPrincipal() {
         return null;
      }

      public boolean isUserInRole(String role, Scope scope) {
         return false;
      }

      public String toString() {
         return "UNAUTHENTICATED";
      }
   };

   Subject getSubject();

   Principal getUserPrincipal();

   boolean isUserInRole(String var1, Scope var2);

   public interface Scope {
      String getContextPath();

      String getName();

      Map<String, String> getRoleRefMap();
   }

   public interface UnauthenticatedUserIdentity extends UserIdentity {
   }
}
