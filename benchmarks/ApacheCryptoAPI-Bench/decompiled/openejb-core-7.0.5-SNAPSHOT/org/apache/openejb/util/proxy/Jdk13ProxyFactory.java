package org.apache.openejb.util.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.openejb.OpenEJBException;

public class Jdk13ProxyFactory implements ProxyFactory {
   public void init(Properties props) throws OpenEJBException {
   }

   public InvocationHandler getInvocationHandler(Object proxy) throws IllegalArgumentException {
      return Proxy.getInvocationHandler(proxy);
   }

   public Class getProxyClass(Class interfce) throws IllegalArgumentException {
      return Proxy.getProxyClass(interfce.getClassLoader(), interfce);
   }

   public Class getProxyClass(Class[] interfaces) throws IllegalArgumentException {
      if (interfaces.length < 1) {
         throw new IllegalArgumentException("It's boring to implement 0 interfaces!");
      } else {
         return Proxy.getProxyClass(interfaces[0].getClassLoader(), interfaces);
      }
   }

   public boolean isProxyClass(Class cl) {
      return Proxy.isProxyClass(cl);
   }

   public Object newProxyInstance(Class interfce, InvocationHandler h) throws IllegalArgumentException {
      try {
         return Proxy.newProxyInstance(interfce.getClassLoader(), new Class[]{interfce}, h);
      } catch (IllegalArgumentException iae) {
         ClassLoader reconciliatedCl = reconciliate(interfce);

         try {
            reconciliatedCl.loadClass(interfce.getName());
            return Proxy.newProxyInstance(reconciliatedCl, new Class[]{interfce}, h);
         } catch (ClassNotFoundException var6) {
            throw iae;
         }
      }
   }

   public Object newProxyInstance(Class[] interfaces, InvocationHandler handler) throws IllegalArgumentException {
      if (interfaces.length < 1) {
         throw new IllegalArgumentException("It's boring to implement 0 interfaces!");
      } else {
         try {
            return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, handler);
         } catch (IllegalArgumentException e) {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();

            try {
               Class tcclHomeClass = tccl.loadClass(interfaces[0].getName());
               if (tcclHomeClass == interfaces[0]) {
                  return Proxy.newProxyInstance(tccl, interfaces, handler);
               }
            } catch (ClassNotFoundException var10) {
               ClassLoader reconciliatedCl = reconciliate(interfaces);

               try {
                  Class homeClass = reconciliatedCl.loadClass(interfaces[0].getName());
                  if (homeClass == interfaces[0]) {
                     return Proxy.newProxyInstance(reconciliatedCl, interfaces, handler);
                  }
               } catch (ClassNotFoundException var9) {
                  throw e;
               }
            }

            throw e;
         }
      }
   }

   private static ClassLoader reconciliate(Class<?>... interfaces) {
      Set<ClassLoader> classloaders = new LinkedHashSet();

      for(Class<?> clazz : interfaces) {
         classloaders.add(clazz.getClassLoader());
      }

      return new MultipleClassLoadersClassLoader((ClassLoader[])classloaders.toArray(new ClassLoader[classloaders.size()]));
   }

   private static class MultipleClassLoadersClassLoader extends ClassLoader {
      private final ClassLoader[] delegatingClassloaders;

      public MultipleClassLoadersClassLoader(ClassLoader[] classLoaders) {
         super(classLoaders[0]);
         this.delegatingClassloaders = classLoaders;
      }

      public Class<?> loadClass(String name) throws ClassNotFoundException {
         ClassNotFoundException ex = null;

         for(ClassLoader cl : this.delegatingClassloaders) {
            try {
               return cl.loadClass(name);
            } catch (ClassNotFoundException cnfe) {
               if (ex == null) {
                  ex = cnfe;
               }
            }
         }

         throw ex;
      }
   }
}
