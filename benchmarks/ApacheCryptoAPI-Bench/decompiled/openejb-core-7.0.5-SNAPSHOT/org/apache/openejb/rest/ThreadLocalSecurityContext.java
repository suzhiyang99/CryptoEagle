package org.apache.openejb.rest;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import org.apache.openejb.core.security.AbstractSecurityService;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;

public class ThreadLocalSecurityContext extends AbstractRestThreadLocalProxy<SecurityContext> implements SecurityContext {
   private final String defaultUser;

   protected ThreadLocalSecurityContext() {
      super(SecurityContext.class);
      SecurityService securityService = service();
      this.defaultUser = AbstractSecurityService.class.isInstance(securityService) ? ((AbstractSecurityService)AbstractSecurityService.class.cast(securityService)).getDefaultUser() : null;
   }

   private static SecurityService service() {
      return (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
   }

   public String getAuthenticationScheme() {
      return ((SecurityContext)this.get()).getAuthenticationScheme();
   }

   public Principal getUserPrincipal() {
      Principal callerPrincipal = service().getCallerPrincipal();
      if (callerPrincipal == null) {
         SecurityContext securityContext = (SecurityContext)this.get();
         if (securityContext != null) {
            return securityContext.getUserPrincipal();
         }
      }

      return callerPrincipal != null && !callerPrincipal.getName().equals(this.defaultUser) ? callerPrincipal : null;
   }

   public boolean isSecure() {
      return ((SecurityContext)this.get()).isSecure();
   }

   public boolean isUserInRole(String role) {
      if (service().isCallerInRole(role)) {
         return true;
      } else {
         SecurityContext sc = (SecurityContext)this.get();
         return sc != null && sc.isUserInRole(role);
      }
   }
}
