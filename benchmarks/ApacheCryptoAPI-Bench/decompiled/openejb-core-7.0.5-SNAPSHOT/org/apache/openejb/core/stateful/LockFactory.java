package org.apache.openejb.core.stateful;

import java.util.concurrent.TimeUnit;

public interface LockFactory {
   StatefulLock newLock(String var1);

   void setContainer(StatefulContainer var1);

   public interface StatefulLock {
      void lock();

      void unlock();

      boolean isHeldByCurrentThread();

      boolean tryLock();

      boolean tryLock(long var1, TimeUnit var3) throws InterruptedException;
   }
}
