package org.apache.tika.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleThreadPoolExecutor extends ThreadPoolExecutor implements ConfigurableThreadPoolExecutor {
   public SimpleThreadPoolExecutor() {
      super(1, 2, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory() {
         public Thread newThread(Runnable r) {
            return new Thread(r, "Tika Executor Thread");
         }
      });
   }
}
