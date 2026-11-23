package org.apache.openejb.config.provider;

import org.apache.openejb.config.sys.ServiceProvider;

public class InvalidProviderDeclarationException extends IllegalArgumentException {
   private final ID id;
   private final ServiceProvider provider;

   public InvalidProviderDeclarationException(String s, ID id, ServiceProvider provider) {
      super(String.format("%s - %s", id, s));
      this.id = id;
      this.provider = provider;
   }

   public ServiceProvider getProvider() {
      return this.provider;
   }

   public ID getId() {
      return this.id;
   }
}
