package org.apache.activemq.artemis.utils.actors;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.artemis.utils.ExecutorFactory;

public final class OrderedExecutorFactory implements ExecutorFactory {
   final Executor parent;

   public static boolean flushExecutor(Executor executor) {
      return flushExecutor(executor, 30L, TimeUnit.SECONDS);
   }

   public static boolean flushExecutor(Executor executor, long timeout, TimeUnit unit) {
      CountDownLatch latch = new CountDownLatch(1);
      Objects.requireNonNull(latch);
      executor.execute(latch::countDown);

      try {
         return latch.await(timeout, unit);
      } catch (Exception var6) {
         return false;
      }
   }

   public OrderedExecutorFactory(Executor parent) {
      this.parent = parent;
   }

   public ArtemisExecutor getExecutor() {
      return new OrderedExecutor(this.parent);
   }

   public Executor getParent() {
      return this.parent;
   }
}
