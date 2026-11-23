package org.apache.activemq.artemis.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface SimpleFuture<V> extends Future<V> {
   SimpleFuture dumb = new SimpleFuture() {
      public void fail(Throwable e) {
      }

      public void set(Object o) {
      }

      public boolean cancel(boolean mayInterruptIfRunning) {
         return false;
      }

      public boolean isCancelled() {
         return false;
      }

      public boolean isDone() {
         return false;
      }

      public Object get() throws InterruptedException, ExecutionException {
         return null;
      }

      public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
         return null;
      }
   };

   static SimpleFuture dumb() {
      return dumb;
   }

   void fail(Throwable var1);

   void set(V var1);
}
