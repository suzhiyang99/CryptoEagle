package org.apache.openejb.rest;

import javax.ws.rs.ext.ContextResolver;

public class ThreadLocalContextResolver extends AbstractRestThreadLocalProxy<ContextResolver> implements ContextResolver {
   protected ThreadLocalContextResolver() {
      super(ContextResolver.class);
   }

   public Object getContext(Class type) {
      return this.get() != null ? ((ContextResolver)this.get()).getContext(type) : null;
   }
}
