package org.apache.openejb.web;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import javax.naming.Context;
import javax.naming.LinkRef;
import javax.naming.NameNotFoundException;
import org.apache.openejb.core.ivm.naming.Reference;

public class WebInitialContext implements InvocationHandler {
   private static final Class<?>[] INTERFACES = new Class[]{Context.class};
   private final Map<String, Object> bindings;
   private final Context delegate;

   public WebInitialContext(Map<String, Object> bindings, Context ctx) {
      this.bindings = bindings;
      this.delegate = ctx;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("lookup".equals(method.getName()) && method.getParameterTypes().length == 1 && String.class.equals(method.getParameterTypes()[0])) {
         Object lookedUp = this.bindings.get(normalize((String)args[0]));
         if (lookedUp != null) {
            if (lookedUp instanceof Reference) {
               return ((Reference)lookedUp).getObject();
            }

            if (lookedUp instanceof LinkRef) {
               return ((Context)proxy).lookup(((LinkRef)lookedUp).getLinkName());
            }

            try {
               return method.invoke(this.delegate, args);
            } catch (InvocationTargetException nnfe) {
               if (NameNotFoundException.class.isInstance(nnfe.getTargetException())) {
                  return lookedUp;
               }

               throw nnfe.getTargetException();
            }
         }
      }

      try {
         return method.invoke(this.delegate, args);
      } catch (InvocationTargetException nnfe) {
         throw nnfe.getTargetException();
      }
   }

   private static String normalize(String arg) {
      return arg.startsWith("java:") ? arg.substring("java:".length()) : arg;
   }

   public static Context create(Map<String, Object> bindings, Context fallback) {
      return (Context)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), INTERFACES, new WebInitialContext(bindings, fallback));
   }
}
