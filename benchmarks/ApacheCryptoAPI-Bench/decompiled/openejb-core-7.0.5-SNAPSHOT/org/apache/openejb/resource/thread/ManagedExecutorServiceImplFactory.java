package org.apache.openejb.resource.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import javax.enterprise.concurrent.ManagedThreadFactory;
import org.apache.openejb.threads.impl.ManagedExecutorServiceImpl;
import org.apache.openejb.threads.impl.ManagedThreadFactoryImpl;
import org.apache.openejb.threads.reject.CURejectHandler;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ManagedExecutorServiceImplFactory {
   private int core = 5;
   private int max = 25;
   private Duration keepAlive = new Duration("5 second");
   private int queue = 15;
   private String threadFactory;

   public ManagedExecutorServiceImpl create() {
      return new ManagedExecutorServiceImpl(this.createExecutorService());
   }

   private ExecutorService createExecutorService() {
      BlockingQueue<Runnable> blockingQueue;
      if (this.queue <= 0) {
         blockingQueue = new LinkedBlockingQueue();
      } else {
         blockingQueue = new ArrayBlockingQueue(this.queue);
      }

      ManagedThreadFactory managedThreadFactory;
      try {
         managedThreadFactory = (ManagedThreadFactory)("org.apache.openejb.threads.impl.ManagedThreadFactoryImpl".equals(this.threadFactory) ? new ManagedThreadFactoryImpl() : ThreadFactories.findThreadFactory(this.threadFactory));
      } catch (Exception e) {
         Logger.getInstance(LogCategory.OPENEJB, ManagedExecutorServiceImplFactory.class).warning("Can't create configured thread factory: " + this.threadFactory, (Throwable)e);
         managedThreadFactory = new ManagedThreadFactoryImpl();
      }

      return new ThreadPoolExecutor(this.core, this.max, this.keepAlive.getTime(), this.keepAlive.getUnit(), blockingQueue, managedThreadFactory, CURejectHandler.INSTANCE);
   }

   public void setCore(int core) {
      this.core = core;
   }

   public void setMax(int max) {
      this.max = max;
   }

   public void setKeepAlive(Duration keepAlive) {
      this.keepAlive = keepAlive;
   }

   public void setQueue(int queue) {
      this.queue = queue;
   }

   public void setThreadFactory(String threadFactory) {
      this.threadFactory = threadFactory;
   }
}
