package org.apache.openejb.core.ivm;

public class ClientSecurity {
   private static Object clientIdentity;

   public static Object getIdentity() {
      return clientIdentity;
   }

   public static void setIdentity(Object clientIdentity) {
      ClientSecurity.clientIdentity = clientIdentity;
   }
}
