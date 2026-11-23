package org.apache.openejb.util.classloader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ClassLoaderAwareHandler implements InvocationHandler {
   private final Object delegate;
   private final ClassLoader loader;
   private final String toString;

   public ClassLoaderAwareHandler(String toString, Object delegate, ClassLoader loader) {
      this.delegate = delegate;
      this.loader = loader;
      this.toString = toString;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (this.toString != null && method.getName().equals("toString")) {
         return this.toString;
      } else {
         Thread thread = Thread.currentThread();
         ClassLoader old = thread.getContextClassLoader();
         thread.setContextClassLoader(this.loader);

         Object var6;
         try {
            var6 = method.invoke(this.delegate, args);
         } catch (InvocationTargetException e) {
            throw e.getCause();
         } finally {
            thread.setContextClassLoader(old);
         }

         return var6;
      }
   }
}
