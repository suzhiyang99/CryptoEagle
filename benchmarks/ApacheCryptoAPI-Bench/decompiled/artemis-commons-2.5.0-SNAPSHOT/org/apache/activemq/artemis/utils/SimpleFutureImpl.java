package org.apache.activemq.artemis.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleFutureImpl<V> implements SimpleFuture<V> {
   V value;
   Throwable exception;
   private final CountDownLatch latch = new CountDownLatch(1);
   boolean canceled = false;

   public boolean cancel(boolean mayInterruptIfRunning) {
      this.canceled = true;
      this.latch.countDown();
      return true;
   }

   public boolean isCancelled() {
      return this.canceled;
   }

   public boolean isDone() {
      return this.latch.getCount() <= 0L;
   }

   public void fail(Throwable e) {
      this.exception = e;
      this.latch.countDown();
   }

   public V get() throws InterruptedException, ExecutionException {
      this.latch.await();
      if (this.exception != null) {
         throw new ExecutionException(this.exception);
      } else {
         return this.value;
      }
   }

   public void set(V v) {
      this.value = v;
      this.latch.countDown();
   }

   public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      this.latch.await(timeout, unit);
      return this.value;
   }
}
