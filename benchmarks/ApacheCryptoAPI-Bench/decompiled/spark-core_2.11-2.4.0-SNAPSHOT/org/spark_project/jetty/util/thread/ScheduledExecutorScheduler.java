package org.spark_project.jetty.util.thread;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;

public class ScheduledExecutorScheduler extends AbstractLifeCycle implements Scheduler, Dumpable {
   private final String name;
   private final boolean daemon;
   private final ClassLoader classloader;
   private final ThreadGroup threadGroup;
   private volatile ScheduledThreadPoolExecutor scheduler;
   private volatile Thread thread;

   public ScheduledExecutorScheduler() {
      this((String)null, false);
   }

   public ScheduledExecutorScheduler(String name, boolean daemon) {
      this(name, daemon, Thread.currentThread().getContextClassLoader());
   }

   public ScheduledExecutorScheduler(String name, boolean daemon, ClassLoader threadFactoryClassLoader) {
      this(name, daemon, threadFactoryClassLoader, (ThreadGroup)null);
   }

   public ScheduledExecutorScheduler(String name, boolean daemon, ClassLoader threadFactoryClassLoader, ThreadGroup threadGroup) {
      this.name = name == null ? "Scheduler-" + this.hashCode() : name;
      this.daemon = daemon;
      this.classloader = threadFactoryClassLoader == null ? Thread.currentThread().getContextClassLoader() : threadFactoryClassLoader;
      this.threadGroup = threadGroup;
   }

   protected void doStart() throws Exception {
      this.scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
         public Thread newThread(Runnable r) {
            Thread thread = ScheduledExecutorScheduler.this.thread = new Thread(ScheduledExecutorScheduler.this.threadGroup, r, ScheduledExecutorScheduler.this.name);
            thread.setDaemon(ScheduledExecutorScheduler.this.daemon);
            thread.setContextClassLoader(ScheduledExecutorScheduler.this.classloader);
            return thread;
         }
      });
      this.scheduler.setRemoveOnCancelPolicy(true);
      super.doStart();
   }

   protected void doStop() throws Exception {
      this.scheduler.shutdownNow();
      super.doStop();
      this.scheduler = null;
   }

   public Scheduler.Task schedule(Runnable task, long delay, TimeUnit unit) {
      ScheduledThreadPoolExecutor s = this.scheduler;
      if (s == null) {
         return new Scheduler.Task() {
            public boolean cancel() {
               return false;
            }
         };
      } else {
         ScheduledFuture<?> result = s.schedule(task, delay, unit);
         return new ScheduledFutureTask(result);
      }
   }

   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      ContainerLifeCycle.dumpObject(out, this);
      Thread thread = this.thread;
      if (thread != null) {
         List<StackTraceElement> frames = Arrays.asList(thread.getStackTrace());
         ContainerLifeCycle.dump(out, indent, frames);
      }

   }

   private static class ScheduledFutureTask implements Scheduler.Task {
      private final ScheduledFuture<?> scheduledFuture;

      ScheduledFutureTask(ScheduledFuture<?> scheduledFuture) {
         this.scheduledFuture = scheduledFuture;
      }

      public boolean cancel() {
         return this.scheduledFuture.cancel(false);
      }
   }
}
