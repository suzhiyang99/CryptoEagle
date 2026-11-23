package org.apache.openejb.core.ivm.naming;

import java.util.concurrent.Callable;

public class LazyObjectReference<T> extends Reference {
   private final Callable<T> creator;
   private volatile T instance;

   public LazyObjectReference(Callable<T> creator) {
      this.creator = creator;
   }

   public Object getObject() throws javax.naming.NamingException {
      if (this.instance == null) {
         synchronized(this) {
            if (this.instance == null) {
               try {
                  this.instance = (T)this.creator.call();
               } catch (Exception e) {
                  throw new LazyNamingException(e.getMessage());
               }
            }
         }
      }

      return this.instance;
   }

   public boolean isInitialized() {
      return this.instance != null;
   }

   public static final class LazyNamingException extends javax.naming.NamingException {
      private LazyNamingException(String message) {
         super(message);
      }
   }
}
