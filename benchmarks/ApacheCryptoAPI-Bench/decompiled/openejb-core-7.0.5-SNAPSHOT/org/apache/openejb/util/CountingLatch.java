package org.apache.openejb.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class CountingLatch {
   private final Sync sync;

   public CountingLatch() {
      this(0);
   }

   public CountingLatch(int count) {
      this.sync = new Sync(count);
   }

   public void await() throws InterruptedException {
      this.sync.acquireSharedInterruptibly(1);
   }

   public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return this.sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
   }

   public void countDown() {
      this.sync.releaseShared(-1);
   }

   public void countUp() {
      this.sync.releaseShared(1);
   }

   public long getCount() {
      return (long)this.sync.getCount();
   }

   private static final class Sync extends AbstractQueuedSynchronizer {
      private Sync(int count) {
         this.setState(count);
      }

      public boolean tryReleaseShared(int releases) {
         int count;
         int next;
         do {
            count = this.getState();
            next = count + releases;
            if (next < 0) {
               return false;
            }
         } while(!this.compareAndSetState(count, next));

         return next == 0;
      }

      public int tryAcquireShared(int acquires) {
         return this.getState() == 0 ? 1 : -1;
      }

      int getCount() {
         return this.getState();
      }
   }
}
