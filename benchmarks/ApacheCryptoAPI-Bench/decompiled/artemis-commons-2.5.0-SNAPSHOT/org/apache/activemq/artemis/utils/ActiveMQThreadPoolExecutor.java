package org.apache.activemq.artemis.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ActiveMQThreadPoolExecutor extends ThreadPoolExecutor {
   private static final RejectedExecutionHandler QUEUE_EXECUTION_HANDLER = (r, e) -> {
      if (!e.isShutdown()) {
         e.getQueue().add(r);
      }

   };

   public ActiveMQThreadPoolExecutor(int coreSize, int maxSize, long keep, TimeUnit keepUnits, ThreadFactory factory) {
      super(coreSize, maxSize, keep, keepUnits, new ThreadPoolQueue(), factory, QUEUE_EXECUTION_HANDLER);
   }

   private static class ThreadPoolQueue extends LinkedBlockingQueue<Runnable> {
      private ThreadPoolQueue() {
      }

      public boolean offer(Runnable runnable) {
         return false;
      }

      public boolean add(Runnable runnable) {
         return super.offer(runnable);
      }
   }
}
