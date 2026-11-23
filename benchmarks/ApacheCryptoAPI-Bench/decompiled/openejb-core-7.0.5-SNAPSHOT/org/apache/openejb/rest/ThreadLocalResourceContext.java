package org.apache.openejb.rest;

import javax.ws.rs.container.ResourceContext;

public class ThreadLocalResourceContext extends AbstractRestThreadLocalProxy<ResourceContext> implements ResourceContext {
   protected ThreadLocalResourceContext() {
      super(ResourceContext.class);
   }

   public <T> T getResource(Class<T> tClass) {
      return (T)((ResourceContext)this.get()).getResource(tClass);
   }

   public <T> T initResource(T t) {
      return (T)((ResourceContext)this.get()).initResource(t);
   }
}
