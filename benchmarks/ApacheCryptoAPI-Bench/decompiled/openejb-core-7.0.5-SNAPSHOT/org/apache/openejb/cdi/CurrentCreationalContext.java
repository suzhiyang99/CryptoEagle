package org.apache.openejb.cdi;

import javax.enterprise.context.spi.CreationalContext;

public final class CurrentCreationalContext<T> {
   private final ThreadLocal<CreationalContext<T>> creationalContext = new ThreadLocal();

   public CreationalContext<T> get() {
      return (CreationalContext)this.creationalContext.get();
   }

   public void set(CreationalContext<T> value) {
      this.creationalContext.set(value);
   }

   public void remove() {
      this.creationalContext.remove();
   }
}
