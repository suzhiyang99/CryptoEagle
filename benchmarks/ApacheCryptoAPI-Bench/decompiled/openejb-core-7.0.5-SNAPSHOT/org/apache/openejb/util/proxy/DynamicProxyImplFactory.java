package org.apache.openejb.util.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Injection;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.api.Proxy;
import org.apache.xbean.finder.Annotated;
import org.apache.xbean.finder.MetaAnnotatedClass;

public class DynamicProxyImplFactory {
   public static boolean isKnownDynamicallyImplemented(Class<?> clazz) {
      Annotated<Class<?>> metaClass = new MetaAnnotatedClass(clazz);
      return isKnownDynamicallyImplemented(metaClass, clazz);
   }

   public static boolean isKnownDynamicallyImplemented(Annotated<?> metaClass, Class<?> clazz) {
      return clazz.isInterface() && (metaClass.getAnnotation(PersistenceContext.class) != null || metaClass.getAnnotation(Proxy.class) != null);
   }

   public static Object newProxy(BeanContext context, InvocationHandler invocationHandler) {
      if (QueryProxy.class.isInstance(invocationHandler)) {
         EntityManager em = null;

         for(Injection injection : context.getInjections()) {
            if (QueryProxy.class.equals(injection.getTarget())) {
               try {
                  em = (EntityManager)context.getJndiEnc().lookup(injection.getJndiName());
               } catch (NamingException e) {
                  throw new OpenEJBRuntimeException("a dynamic bean should reference at least one correct PersistenceContext", e);
               }
            }
         }

         if (em == null) {
            throw new OpenEJBRuntimeException("can't find the entity manager to use for the dynamic bean " + context.getEjbName());
         }

         ((QueryProxy)QueryProxy.class.cast(invocationHandler)).setEntityManager(em);
      }

      return newProxy(context.getBeanClass(), invocationHandler);
   }

   public static Object newProxy(Class<?> type, InvocationHandler invocationHandler) {
      try {
         return ProxyManager.newProxyInstance((Class)type, new Handler(invocationHandler));
      } catch (IllegalAccessException e) {
         throw new OpenEJBRuntimeException("illegal access", e);
      }
   }

   public static Object realHandler(Object proxy) {
      if (proxy != null && java.lang.reflect.Proxy.isProxyClass(proxy.getClass())) {
         Object handler = java.lang.reflect.Proxy.getInvocationHandler(proxy);
         if (handler instanceof Handler) {
            return ((Handler)handler).realHandler();
         }
      }

      return null;
   }

   private static final class Handler implements InvocationHandler {
      private final InvocationHandler handler;

      private Handler(InvocationHandler handler) {
         this.handler = handler;
      }

      public InvocationHandler realHandler() {
         return this.handler;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         return this.handler.invoke(proxy, method, args);
      }
   }
}
