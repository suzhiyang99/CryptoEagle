package org.apache.wicket.util;

import java.util.function.Supplier;
import org.apache.wicket.util.io.IClusterable;

public abstract class LazyInitializer<T> implements Supplier<T>, IClusterable {
   private static final long serialVersionUID = 1L;
   private transient volatile T instance = null;

   public T get() {
      if (this.instance == null) {
         synchronized(this) {
            if (this.instance == null) {
               this.instance = (T)this.createInstance();
            }
         }
      }

      return this.instance;
   }

   protected abstract T createInstance();
}
