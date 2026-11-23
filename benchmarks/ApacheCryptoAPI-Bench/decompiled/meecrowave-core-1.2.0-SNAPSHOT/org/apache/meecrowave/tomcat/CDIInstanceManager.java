package org.apache.meecrowave.tomcat;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.NamingException;
import org.apache.tomcat.InstanceManager;
import org.apache.webbeans.servlet.WebBeansConfigurationListener;

public class CDIInstanceManager implements InstanceManager {
   private final Map<Object, Runnable> destroyables = new ConcurrentHashMap();

   public Object newInstance(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException {
      Object newInstance = clazz.newInstance();
      this.newInstance(newInstance);
      return newInstance;
   }

   public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException {
      return this.newInstance(className, Thread.currentThread().getContextClassLoader());
   }

   public Object newInstance(String fqcn, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException {
      return this.newInstance(classLoader.loadClass(fqcn));
   }

   public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
      if (!WebBeansConfigurationListener.class.isInstance(o) && !o.getClass().getName().startsWith("org.apache.catalina.servlets.")) {
         BeanManager bm = CDI.current().getBeanManager();
         AnnotatedType<?> annotatedType = bm.createAnnotatedType(o.getClass());
         InjectionTarget injectionTarget = bm.createInjectionTarget(annotatedType);
         CreationalContext<Object> creationalContext = bm.createCreationalContext((Contextual)null);
         injectionTarget.inject(o, creationalContext);

         try {
            injectionTarget.postConstruct(o);
         } catch (RuntimeException e) {
            creationalContext.release();
            throw e;
         }

         this.destroyables.put(o, (Runnable)() -> {
            try {
               injectionTarget.preDestroy(o);
            } finally {
               creationalContext.release();
            }

         });
      }
   }

   public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
      Optional.ofNullable(this.destroyables.remove(o)).ifPresent(Runnable::run);
   }
}
