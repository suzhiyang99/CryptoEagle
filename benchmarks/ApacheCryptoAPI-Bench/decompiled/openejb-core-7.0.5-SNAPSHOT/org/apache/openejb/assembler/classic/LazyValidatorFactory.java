package org.apache.openejb.assembler.classic;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;
import javax.validation.ValidatorFactory;

public class LazyValidatorFactory implements InvocationHandler, Serializable {
   private final transient ReentrantLock lock = new ReentrantLock();
   private final transient ClassLoader loader;
   private final ValidationInfo info;
   private volatile ValidatorFactory factory;

   public LazyValidatorFactory(ClassLoader classLoader, ValidationInfo validationInfo) {
      this.loader = classLoader;
      this.info = validationInfo;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      this.ensureDelegate();

      try {
         return method.invoke(this.factory, args);
      } catch (InvocationTargetException ite) {
         throw ite.getCause();
      }
   }

   private void ensureDelegate() {
      if (this.factory == null) {
         ReentrantLock l = this.lock;
         l.lock();

         try {
            if (this.factory == null) {
               this.factory = ValidatorBuilder.buildFactory(this.loader == null ? Thread.currentThread().getContextClassLoader() : this.loader, this.info == null ? new ValidationInfo() : this.info);
            }
         } finally {
            l.unlock();
         }
      }

   }

   public ValidatorFactory getFactory() {
      this.ensureDelegate();
      return this.factory;
   }
}
