package org.apache.openejb.testing.rest;

import java.util.HashMap;
import java.util.Map;
import org.apache.openejb.rest.RESTResourceFinder;

public class ContextProvider implements RESTResourceFinder {
   private final Map<Class<?>, Object> instances;
   private final RESTResourceFinder defaults;

   public ContextProvider() {
      this((RESTResourceFinder)null);
   }

   public ContextProvider(RESTResourceFinder fallback) {
      this.instances = new HashMap();
      this.defaults = fallback;
   }

   public <T> T find(Class<T> clazz) {
      Object obj = this.instances.get(clazz);
      if (obj == null) {
         return (T)(this.defaults != null ? this.defaults.find(clazz) : null);
      } else {
         return (T)clazz.cast(obj);
      }
   }

   public ContextProvider deregister(Class<?> contextType) {
      this.instances.remove(contextType);
      return this;
   }

   public <T> ContextProvider register(Class<T> contextType, T instance) {
      this.instances.put(contextType, instance);
      return this;
   }
}
