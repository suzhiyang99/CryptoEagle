package org.apache.openejb.core;

import java.security.Principal;

public final class UnauthenticatedPrincipal implements Principal {
   public static final UnauthenticatedPrincipal INSTANCE = new UnauthenticatedPrincipal();

   private UnauthenticatedPrincipal() {
   }

   public String getName() {
      return "Unauthenticated";
   }
}
