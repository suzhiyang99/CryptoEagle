package org.apache.openejb.core.interceptor;

import java.lang.reflect.Method;

public class Interceptor {
   private final Object instance;
   private final Method method;

   public Interceptor(Object instance, Method method) {
      if (instance == null) {
         throw new NullPointerException("instance is null");
      } else if (method == null) {
         throw new NullPointerException("method is null");
      } else {
         this.instance = instance;
         this.method = method;
      }
   }

   public Object getInstance() {
      return this.instance;
   }

   public Method getMethod() {
      return this.method;
   }
}
