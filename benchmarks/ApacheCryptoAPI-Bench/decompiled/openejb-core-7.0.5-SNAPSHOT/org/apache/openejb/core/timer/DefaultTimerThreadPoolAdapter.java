package org.apache.openejb.core.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.quartz.SchedulerConfigException;
import org.apache.openejb.quartz.spi.ThreadPool;
import org.apache.openejb.util.ExecutorBuilder;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class DefaultTimerThreadPoolAdapter implements ThreadPool {
   private static final Logger logger;
   public static final String OPENEJB_TIMER_POOL_SIZE = "openejb.timer.pool.size";
   public static final String OPENEJB_EJB_TIMER_POOL_AWAIT_SECONDS = "openejb.ejb-timer-pool.shutdown.timeout";
   private Executor executor;
   private String instanceId;
   private String instanceName;
   private int threadCount = Integer.parseInt(SystemInstance.get().getProperty("openejb.timer.pool.size", "3"));
   private int threadPriority = 5;
   private final Object threadAvailableLock = new Object();
   private boolean threadPoolExecutorUsed;

   public int blockForAvailableThreads() {
      if (this.threadPoolExecutorUsed) {
         ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)this.executor;
         synchronized(this.threadAvailableLock) {
            while(threadPoolExecutor.getMaximumPoolSize() - threadPoolExecutor.getActiveCount() < 1 && !threadPoolExecutor.isShutdown()) {
               try {
                  this.threadAvailableLock.wait(500L);
               } catch (InterruptedException var5) {
               }
            }

            return threadPoolExecutor.getMaximumPoolSize() - threadPoolExecutor.getActiveCount();
         }
      } else {
         return 1;
      }
   }

   public void setInstanceId(String instanceId) {
      this.instanceId = instanceId;
   }

   public void setInstanceName(String instanceName) {
      this.instanceName = instanceName;
   }

   public String getInstanceId() {
      return this.instanceId;
   }

   public String getInstanceName() {
      return this.instanceName;
   }

   public int getPoolSize() {
      return this.threadPoolExecutorUsed ? ((ThreadPoolExecutor)this.executor).getPoolSize() : 1;
   }

   public synchronized void initialize() throws SchedulerConfigException {
      TimerExecutor timerExecutor = (TimerExecutor)SystemInstance.get().getComponent(TimerExecutor.class);
      if (timerExecutor != null) {
         this.executor = timerExecutor.incr().executor;
      } else {
         this.executor = (new ExecutorBuilder()).size(this.threadCount).prefix("EjbTimerPool").build(SystemInstance.get().getOptions());
         TimerExecutor value = (new TimerExecutor(this.executor)).incr();
         SystemInstance.get().setComponent(TimerExecutor.class, value);
      }

      this.threadPoolExecutorUsed = this.executor instanceof ThreadPoolExecutor;
      if (!this.threadPoolExecutorUsed) {
         logger.warning("Unrecognized ThreadPool implementation [" + this.executor.getClass().getName() + "] is used, EJB Timer service may not work correctly");
      }

   }

   public boolean runInThread(Runnable runnable) {
      try {
         this.executor.execute(runnable);
         return true;
      } catch (RejectedExecutionException e) {
         logger.error("Failed to execute timer task", (Throwable)e);
         return false;
      }
   }

   public synchronized void shutdown(boolean waitForJobsToComplete) {
      if (this.threadPoolExecutorUsed) {
         SystemInstance systemInstance = SystemInstance.get();
         TimerExecutor te = (TimerExecutor)systemInstance.getComponent(TimerExecutor.class);
         if (te != null) {
            if (te.executor == this.executor) {
               if (te.decr()) {
                  this.doShutdownExecutor(waitForJobsToComplete);
                  systemInstance.removeComponent(TimerExecutor.class);
               } else {
                  ThreadPoolExecutor tpe = (ThreadPoolExecutor)ThreadPoolExecutor.class.cast(this.executor);
                  if (waitForJobsToComplete) {
                     Collection<Runnable> jobs = new ArrayList();
                     tpe.getQueue().drainTo(jobs);

                     for(Runnable r : jobs) {
                        try {
                           r.run();
                        } catch (Exception e) {
                           logger.warning(e.getMessage(), (Throwable)e);
                        }
                     }
                  }
               }
            } else {
               this.doShutdownExecutor(waitForJobsToComplete);
            }
         } else {
            this.doShutdownExecutor(waitForJobsToComplete);
         }
      }

   }

   private void doShutdownExecutor(boolean waitJobs) {
      ThreadPoolExecutor tpe = (ThreadPoolExecutor)this.executor;
      tpe.shutdown();
      if (waitJobs) {
         int timeout = SystemInstance.get().getOptions().get("openejb.ejb-timer-pool.shutdown.timeout", 5);

         try {
            tpe.awaitTermination((long)timeout, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
            logger.error(e.getMessage(), (Throwable)e);
         }
      }

   }

   public int getThreadCount() {
      return this.threadCount;
   }

   public void setThreadCount(int threadCount) {
      this.threadCount = threadCount;
   }

   public int getThreadPriority() {
      return this.threadPriority;
   }

   public void setThreadPriority(int threadPriority) {
      this.threadPriority = threadPriority;
   }

   static {
      logger = Logger.getInstance(LogCategory.TIMER, "org.apache.openejb.util.resources");
   }

   public static final class TimerExecutor {
      private final Executor executor;
      private final AtomicInteger references;

      private TimerExecutor(Executor executor) {
         this.references = new AtomicInteger(0);
         if (executor == null) {
            throw new IllegalArgumentException("executor cannot be null");
         } else {
            this.executor = executor;
         }
      }

      public TimerExecutor incr() {
         this.references.incrementAndGet();
         return this;
      }

      public boolean decr() {
         return this.references.decrementAndGet() == 0;
      }
   }
}
