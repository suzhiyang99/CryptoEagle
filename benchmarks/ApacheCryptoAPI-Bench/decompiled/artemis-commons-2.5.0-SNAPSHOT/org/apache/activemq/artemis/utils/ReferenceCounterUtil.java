package org.apache.activemq.artemis.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceCounterUtil implements ReferenceCounter {
   private final Runnable runnable;
   private final Executor executor;
   private final AtomicInteger uses;

   public ReferenceCounterUtil(Runnable runnable) {
      this(runnable, (Executor)null);
   }

   public ReferenceCounterUtil(Runnable runnable, Executor executor) {
      this.uses = new AtomicInteger(0);
      this.runnable = runnable;
      this.executor = executor;
   }

   public int increment() {
      return this.uses.incrementAndGet();
   }

   public int decrement() {
      int value = this.uses.decrementAndGet();
      if (value == 0) {
         if (this.executor != null) {
            this.executor.execute(this.runnable);
         } else {
            this.runnable.run();
         }
      }

      return value;
   }
}
