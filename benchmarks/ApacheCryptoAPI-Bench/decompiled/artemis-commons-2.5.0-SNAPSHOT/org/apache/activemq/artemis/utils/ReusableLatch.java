package org.apache.activemq.artemis.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class ReusableLatch {
   private final CountSync control;

   public ReusableLatch() {
      this(0);
   }

   public ReusableLatch(int count) {
      this.control = new CountSync(count);
   }

   public int getCount() {
      return this.control.getCount();
   }

   public void setCount(int count) {
      this.control.setCount(count);
   }

   public void countUp() {
      this.control.add();
   }

   public void countDown() {
      this.control.releaseShared(1);
   }

   public void countDown(int count) {
      this.control.releaseShared(count);
   }

   public void await() throws InterruptedException {
      this.control.acquireSharedInterruptibly(1);
   }

   public boolean await(long milliseconds) throws InterruptedException {
      return this.control.tryAcquireSharedNanos(1, TimeUnit.MILLISECONDS.toNanos(milliseconds));
   }

   public boolean await(long timeWait, TimeUnit timeUnit) throws InterruptedException {
      return this.control.tryAcquireSharedNanos(1, timeUnit.toNanos(timeWait));
   }

   private static class CountSync extends AbstractQueuedSynchronizer {
      private CountSync(int count) {
         this.setState(count);
      }

      public int getCount() {
         return this.getState();
      }

      public void setCount(int count) {
         this.setState(count);
      }

      public int tryAcquireShared(int numberOfAqcquires) {
         return this.getState() == 0 ? 1 : -1;
      }

      public void add() {
         int actualState;
         int newState;
         do {
            actualState = this.getState();
            newState = actualState + 1;
         } while(!this.compareAndSetState(actualState, newState));

      }

      public boolean tryReleaseShared(int numberOfReleases) {
         int actualState;
         int newState;
         do {
            actualState = this.getState();
            if (actualState == 0) {
               return true;
            }

            newState = actualState - numberOfReleases;
            if (newState < 0) {
               newState = 0;
            }
         } while(!this.compareAndSetState(actualState, newState));

         return newState == 0;
      }
   }
}
