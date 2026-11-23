package org.apache.openejb.rest;

import java.io.Serializable;
import org.apache.openejb.loader.SystemInstance;

public class AbstractRestThreadLocalProxy<T> implements Serializable {
   private final ThreadLocal<T> infos = new ThreadLocal();
   private final Class<T> clazz;

   protected AbstractRestThreadLocalProxy(Class<T> clazz) {
      this.clazz = clazz;
   }

   public T get() {
      T t = (T)this.infos.get();
      if (t == null) {
         t = (T)this.find();
      }

      return t;
   }

   public T find() {
      RESTResourceFinder finder = (RESTResourceFinder)SystemInstance.get().getComponent(RESTResourceFinder.class);
      return (T)(finder != null ? finder.find(this.clazz) : null);
   }

   public void remove() {
      this.infos.remove();
   }

   public void set(T value) {
      this.infos.set(value);
   }
}
