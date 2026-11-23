package org.apache.activemq.artemis.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class ConcurrentUtil {
   public static boolean await(Condition condition, long timeout) throws InterruptedException {
      boolean awaited = false;
      long timeoutRemaining = timeout;

      for(long awaitStarted = System.currentTimeMillis(); !awaited && timeoutRemaining > 0L; timeoutRemaining -= System.currentTimeMillis() - awaitStarted) {
         awaited = condition.await(timeoutRemaining, TimeUnit.MILLISECONDS);
      }

      return awaited;
   }
}
