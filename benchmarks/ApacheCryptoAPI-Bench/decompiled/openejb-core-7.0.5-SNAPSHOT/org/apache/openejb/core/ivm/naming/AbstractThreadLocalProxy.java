package org.apache.openejb.core.ivm.naming;

public class AbstractThreadLocalProxy<T> {
   private final ThreadLocal<T> infos = new ThreadLocal();

   protected AbstractThreadLocalProxy() {
   }

   public T get() {
      return (T)this.infos.get();
   }

   public void remove() {
      this.infos.remove();
   }

   public void set(T value) {
      this.infos.set(value);
   }
}
