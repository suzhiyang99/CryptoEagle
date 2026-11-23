package org.apache.deltaspike.core.impl.throttling;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.api.throttling.Throttled;
import org.apache.deltaspike.core.api.throttling.Throttling;
import org.apache.deltaspike.core.impl.util.AnnotatedMethods;

@ApplicationScoped
@Typed({InvokerStorage.class})
public class InvokerStorage implements Throttling.SemaphoreFactory {
   private final ConcurrentMap<String, Semaphore> semaphores = new ConcurrentHashMap();
   private final ConcurrentMap<Method, Invoker> providers = new ConcurrentHashMap();
   @Inject
   private BeanManager beanManager;

   Invoker getOrCreateInvoker(InvocationContext ic) {
      Method method = ic.getMethod();
      Invoker i = (Invoker)this.providers.get(method);
      if (i == null) {
         Class declaringClass = method.getDeclaringClass();
         AnnotatedType<Object> annotatedType = this.beanManager.createAnnotatedType(declaringClass);
         AnnotatedMethod<?> annotatedMethod = AnnotatedMethods.findMethod(annotatedType, method);
         Throttled config = (Throttled)annotatedMethod.getAnnotation(Throttled.class);
         if (config == null) {
            config = (Throttled)annotatedType.getAnnotation(Throttled.class);
         }

         Throttling sharedConfig = (Throttling)annotatedMethod.getAnnotation(Throttling.class);
         if (sharedConfig == null) {
            sharedConfig = (Throttling)annotatedType.getAnnotation(Throttling.class);
         }

         Throttling.SemaphoreFactory factory = (Throttling.SemaphoreFactory)(sharedConfig != null && sharedConfig.factory() != Throttling.SemaphoreFactory.class ? (Throttling.SemaphoreFactory)Throttling.SemaphoreFactory.class.cast(this.beanManager.getReference(this.beanManager.resolve(this.beanManager.getBeans(sharedConfig.factory(), new Annotation[0])), Throttling.SemaphoreFactory.class, (CreationalContext)null)) : this);
         Semaphore semaphore = factory.newSemaphore(annotatedMethod, sharedConfig != null && !sharedConfig.name().isEmpty() ? sharedConfig.name() : declaringClass.getName(), sharedConfig != null && sharedConfig.fair(), sharedConfig != null ? sharedConfig.permits() : 1);
         long timeout = config.timeoutUnit().toMillis(config.timeout());
         int weigth = config.weight();
         i = new Invoker(semaphore, weigth, timeout);
         Invoker existing = (Invoker)this.providers.putIfAbsent(ic.getMethod(), i);
         if (existing != null) {
            i = existing;
         }
      }

      return i;
   }

   public Semaphore newSemaphore(AnnotatedMethod<?> method, String name, boolean fair, int permits) {
      Semaphore semaphore = (Semaphore)this.semaphores.get(name);
      if (semaphore == null) {
         semaphore = new Semaphore(permits, fair);
         Semaphore existing = (Semaphore)this.semaphores.putIfAbsent(name, semaphore);
         if (existing != null) {
            semaphore = existing;
         }
      }

      return semaphore;
   }
}
