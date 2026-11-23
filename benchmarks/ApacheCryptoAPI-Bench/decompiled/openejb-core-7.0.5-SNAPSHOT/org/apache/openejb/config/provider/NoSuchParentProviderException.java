package org.apache.openejb.config.provider;

import org.apache.openejb.config.sys.ServiceProvider;

public class NoSuchParentProviderException extends IllegalStateException {
   private final ServiceProvider provider;
   private final ID id;

   public NoSuchParentProviderException(ServiceProvider provider, ID id) {
      super(String.format("ServiceProvider{id='%s', parent='%s:%s'}", provider.getId(), id.getNamespace(), id.getName()));
      this.provider = provider;
      this.id = id;
   }

   public ServiceProvider getProvider() {
      return this.provider;
   }

   public ID getId() {
      return this.id;
   }
}
