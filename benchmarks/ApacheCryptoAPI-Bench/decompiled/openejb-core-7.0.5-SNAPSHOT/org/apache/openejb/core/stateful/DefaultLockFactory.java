package org.apache.openejb.core.stateful;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultLockFactory implements LockFactory {
   public LockFactory.StatefulLock newLock(String beanId) {
      return new DefaultLock();
   }

   public void setContainer(StatefulContainer statefulContainer) {
   }

   public static class DefaultLock implements LockFactory.StatefulLock {
      private final ReentrantLock lock = new ReentrantLock();

      public void lock() {
         this.lock.lock();
      }

      public void unlock() {
         this.lock.unlock();
      }

      public boolean isHeldByCurrentThread() {
         return this.lock.isHeldByCurrentThread();
      }

      public boolean tryLock() {
         return this.lock.tryLock();
      }

      public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
         return this.lock.tryLock(time, unit);
      }
   }
}
