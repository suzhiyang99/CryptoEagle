package org.apache.openejb.cdi;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class Proxys {
   public static <T> T threadLocalProxy(Class<T> type, ThreadLocal<? extends T> threadLocal, T defaultValue) {
      return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{type, Serializable.class}, new ThreadLocalHandler(threadLocal, defaultValue));
   }

   public static HttpSession threadLocalRequestSessionProxy(ThreadLocal<? extends HttpServletRequest> threadLocal, HttpSession defaultValue) {
      return (HttpSession)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{HttpSession.class, Serializable.class}, new ThreadLocalSessionFromRequestHandler(threadLocal, defaultValue));
   }

   public static <T> T handlerProxy(InvocationHandler raw, Class<T> main, Class<?>... type) {
      Collection<Class<?>> types = new ArrayList(type.length + 2);
      types.add(main);
      types.addAll(Arrays.asList(type));
      types.add(Serializable.class);
      return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), (Class[])types.toArray(new Class[types.size()]), new EnsureExceptionIsUnwrapped(raw));
   }

   private Proxys() {
   }

   private static final class ThreadLocalSessionFromRequestHandler implements InvocationHandler {
      private final ThreadLocal<? extends HttpServletRequest> holder;
      private final HttpSession defaultValue;

      public ThreadLocalSessionFromRequestHandler(ThreadLocal<? extends HttpServletRequest> threadLocal, HttpSession defaultValue) {
         this.holder = threadLocal;
         this.defaultValue = defaultValue;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            HttpServletRequest request = (HttpServletRequest)this.holder.get();
            return request == null ? method.invoke(this.defaultValue, args) : method.invoke(request.getSession(), args);
         } catch (InvocationTargetException ite) {
            throw ite.getCause();
         }
      }
   }

   private static final class ThreadLocalHandler<T> implements InvocationHandler {
      private final ThreadLocal<? extends T> holder;
      private final T defaultValue;

      public ThreadLocalHandler(ThreadLocal<? extends T> threadLocal, T defaultValue) {
         this.holder = threadLocal;
         this.defaultValue = defaultValue;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            T obj = (T)this.holder.get();
            if (obj == null) {
               obj = this.defaultValue;
            }

            return method.invoke(obj, args);
         } catch (InvocationTargetException ite) {
            throw ite.getCause();
         }
      }
   }

   private static final class EnsureExceptionIsUnwrapped implements InvocationHandler {
      private final InvocationHandler delegate;

      public EnsureExceptionIsUnwrapped(InvocationHandler raw) {
         this.delegate = raw;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            return this.delegate.invoke(proxy, method, args);
         } catch (InvocationTargetException ite) {
            throw ite.getCause();
         }
      }
   }
}
