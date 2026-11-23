package org.apache.deltaspike.core.impl.throttling;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.util.ExceptionUtils;

class Invoker {
   private final int weight;
   private final Semaphore semaphore;
   private final long timeout;

   Invoker(Semaphore semaphore, int weight, long timeout) {
      this.semaphore = semaphore;
      this.weight = weight;
      this.timeout = timeout;
   }

   public Object invoke(InvocationContext context) throws Exception {
      if (this.timeout > 0L) {
         try {
            if (!this.semaphore.tryAcquire(this.weight, this.timeout, TimeUnit.MILLISECONDS)) {
               throw new IllegalStateException("Can't acquire " + this.weight + " permits for " + context.getMethod() + " in " + this.timeout + "ms");
            }
         } catch (InterruptedException e) {
            return onInterruption(e);
         }
      } else {
         try {
            this.semaphore.acquire(this.weight);
         } catch (InterruptedException e) {
            return onInterruption(e);
         }
      }

      Object e;
      try {
         e = context.proceed();
      } finally {
         this.semaphore.release(this.weight);
      }

      return e;
   }

   private static Semaphore onInterruption(InterruptedException e) {
      Thread.interrupted();
      throw ExceptionUtils.throwAsRuntimeException(e);
   }
}
