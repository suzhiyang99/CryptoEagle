package org.apache.activemq.artemis.utils.actors;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface ArtemisExecutor extends Executor {
   static ArtemisExecutor delegate(final Executor executor) {
      return new ArtemisExecutor() {
         public void execute(Runnable command) {
            executor.execute(command);
         }
      };
   }

   default int shutdownNow(Consumer<? super Runnable> onPendingTask) {
      return 0;
   }

   default int shutdownNow() {
      return this.shutdownNow((t) -> {
      });
   }

   default void shutdown() {
   }

   default boolean isFlushed() {
      final CountDownLatch latch = new CountDownLatch(1);
      Runnable runnable = new Runnable() {
         public void run() {
            latch.countDown();
         }
      };
      this.execute(runnable);

      try {
         return latch.await(100L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var4) {
         return false;
      }
   }
}
