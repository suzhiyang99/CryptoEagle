package org.spark_project.jetty.util.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Locker {
   private final ReentrantLock _lock = new ReentrantLock();
   private final Lock _unlock = new Lock();

   public Lock lock() {
      if (this._lock.isHeldByCurrentThread()) {
         throw new IllegalStateException("Locker is not reentrant");
      } else {
         this._lock.lock();
         return this._unlock;
      }
   }

   public boolean isLocked() {
      return this._lock.isLocked();
   }

   public Condition newCondition() {
      return this._lock.newCondition();
   }

   public class Lock implements AutoCloseable {
      public void close() {
         Locker.this._lock.unlock();
      }
   }
}
