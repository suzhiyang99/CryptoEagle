package org.apache.deltaspike.core.impl.lock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.api.lock.Locked;
import org.apache.deltaspike.core.api.lock.Locked.Operation;
import org.apache.deltaspike.core.impl.util.AnnotatedMethods;

@ApplicationScoped
@Typed({LockSupplierStorage.class})
public class LockSupplierStorage implements Locked.LockFactory {
   private final ConcurrentMap<String, ReadWriteLock> locks = new ConcurrentHashMap();
   private final ConcurrentMap<Method, LockSupplier> lockSuppliers = new ConcurrentHashMap();
   @Inject
   private BeanManager beanManager;

   protected LockSupplier getLockSupplier(InvocationContext ic) {
      final Method key = ic.getMethod();
      LockSupplier operation = (LockSupplier)this.lockSuppliers.get(key);
      if (operation == null) {
         Class declaringClass = key.getDeclaringClass();
         AnnotatedType<Object> annotatedType = this.beanManager.createAnnotatedType(declaringClass);
         AnnotatedMethod<?> annotatedMethod = AnnotatedMethods.findMethod(annotatedType, key);
         Locked config = (Locked)annotatedMethod.getAnnotation(Locked.class);
         if (config == null) {
            config = (Locked)annotatedType.getAnnotation(Locked.class);
         }

         Locked.LockFactory factory = (Locked.LockFactory)(config.factory() != Locked.LockFactory.class ? (Locked.LockFactory)Locked.LockFactory.class.cast(this.beanManager.getReference(this.beanManager.resolve(this.beanManager.getBeans(config.factory(), new Annotation[0])), Locked.LockFactory.class, (CreationalContext)null)) : this);
         ReadWriteLock writeLock = factory.newLock(annotatedMethod, config.fair());
         final long timeout = config.timeoutUnit().toMillis(config.timeout());
         final Lock lock = config.operation() == Operation.READ ? writeLock.readLock() : writeLock.writeLock();
         if (timeout > 0L) {
            operation = new LockSupplier() {
               public Lock get() {
                  try {
                     if (!lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                        throw new IllegalStateException("Can't lock for " + key + " in " + timeout + "ms");
                     }
                  } catch (InterruptedException e) {
                     Thread.interrupted();
                     throw new IllegalStateException("Locking interrupted", e);
                  }

                  return lock;
               }
            };
         } else {
            operation = new LockSupplier() {
               public Lock get() {
                  lock.lock();
                  return lock;
               }
            };
         }

         LockSupplier existing = (LockSupplier)this.lockSuppliers.putIfAbsent(key, operation);
         if (existing != null) {
            operation = existing;
         }
      }

      return operation;
   }

   public ReadWriteLock newLock(AnnotatedMethod<?> method, boolean fair) {
      String name = method.getJavaMember().getDeclaringClass().getName();
      ReadWriteLock lock = (ReadWriteLock)this.locks.get(name);
      if (lock == null) {
         lock = new ReentrantReadWriteLock(fair);
         ReadWriteLock existing = (ReadWriteLock)this.locks.putIfAbsent(name, lock);
         if (existing != null) {
            lock = existing;
         }
      }

      return lock;
   }
}
