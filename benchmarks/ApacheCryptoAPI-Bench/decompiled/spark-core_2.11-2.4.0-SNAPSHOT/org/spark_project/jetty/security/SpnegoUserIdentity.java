package org.spark_project.jetty.security;

import java.security.Principal;
import java.util.List;
import javax.security.auth.Subject;
import org.spark_project.jetty.server.UserIdentity;

public class SpnegoUserIdentity implements UserIdentity {
   private Subject _subject;
   private Principal _principal;
   private List<String> _roles;

   public SpnegoUserIdentity(Subject subject, Principal principal, List<String> roles) {
      this._subject = subject;
      this._principal = principal;
      this._roles = roles;
   }

   public Subject getSubject() {
      return this._subject;
   }

   public Principal getUserPrincipal() {
      return this._principal;
   }

   public boolean isUserInRole(String role, UserIdentity.Scope scope) {
      return this._roles.contains(role);
   }
}
