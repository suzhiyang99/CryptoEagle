package org.spark_project.jetty.util.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.component.LifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ExecutorThreadPool extends AbstractLifeCycle implements ThreadPool, LifeCycle {
   private static final Logger LOG = Log.getLogger(ExecutorThreadPool.class);
   private final ExecutorService _executor;

   public ExecutorThreadPool(ExecutorService executor) {
      this._executor = executor;
   }

   public ExecutorThreadPool() {
      this(new ThreadPoolExecutor(256, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue()));
   }

   public ExecutorThreadPool(int queueSize) {
      this(queueSize < 0 ? new ThreadPoolExecutor(256, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue()) : (queueSize == 0 ? new ThreadPoolExecutor(32, 256, 60L, TimeUnit.SECONDS, new SynchronousQueue()) : new ThreadPoolExecutor(32, 256, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(queueSize))));
   }

   public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
      this(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS);
   }

   public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
      this(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue());
   }

   public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
      this(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue));
   }

   public void execute(Runnable job) {
      this._executor.execute(job);
   }

   public boolean dispatch(Runnable job) {
      try {
         this._executor.execute(job);
         return true;
      } catch (RejectedExecutionException e) {
         LOG.warn(e);
         return false;
      }
   }

   public int getIdleThreads() {
      if (this._executor instanceof ThreadPoolExecutor) {
         ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
         return tpe.getPoolSize() - tpe.getActiveCount();
      } else {
         return -1;
      }
   }

   public int getThreads() {
      if (this._executor instanceof ThreadPoolExecutor) {
         ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
         return tpe.getPoolSize();
      } else {
         return -1;
      }
   }

   public boolean isLowOnThreads() {
      if (!(this._executor instanceof ThreadPoolExecutor)) {
         return false;
      } else {
         ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
         return tpe.getPoolSize() == tpe.getMaximumPoolSize() && tpe.getQueue().size() >= tpe.getPoolSize() - tpe.getActiveCount();
      }
   }

   public void join() throws InterruptedException {
      this._executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
   }

   protected void doStop() throws Exception {
      super.doStop();
      this._executor.shutdownNow();
   }
}
