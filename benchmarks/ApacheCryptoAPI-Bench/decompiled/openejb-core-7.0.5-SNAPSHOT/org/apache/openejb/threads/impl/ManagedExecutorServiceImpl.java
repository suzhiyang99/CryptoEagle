package org.apache.openejb.threads.impl;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.enterprise.concurrent.ManagedExecutorService;
import org.apache.openejb.api.resource.DestroyableResource;
import org.apache.openejb.threads.future.CUFuture;
import org.apache.openejb.threads.task.CUCallable;
import org.apache.openejb.threads.task.CURunnable;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ManagedExecutorServiceImpl extends AbstractExecutorService implements ManagedExecutorService, DestroyableResource {
   private static final Logger LOGGER;
   private final ExecutorService delegate;

   public ManagedExecutorServiceImpl(ExecutorService delegate) {
      this.delegate = delegate;
   }

   public void shutdown() {
      throw new IllegalStateException("You can't call shutdown");
   }

   public List<Runnable> shutdownNow() {
      throw new IllegalStateException("You can't call shutdownNow");
   }

   public boolean isShutdown() {
      return this.delegate.isShutdown();
   }

   public boolean isTerminated() {
      return this.delegate.isTerminated();
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate.awaitTermination(timeout, unit);
   }

   public <T> Future<T> submit(Callable<T> task) {
      CUCallable<T> wrapper = new CUCallable<T>(task);
      Future<T> future = this.delegate.submit(wrapper);
      wrapper.taskSubmitted(future, this, task);
      return new CUFuture(future, wrapper);
   }

   public <T> Future<T> submit(Runnable task, T result) {
      CURunnable wrapper = new CURunnable(task);
      Future<T> future = this.delegate.submit(wrapper, result);
      wrapper.taskSubmitted(future, this, task);
      return new CUFuture(future, wrapper);
   }

   public Future<?> submit(Runnable task) {
      CURunnable wrapper = new CURunnable(task);
      Future<?> future = this.delegate.submit(wrapper);
      wrapper.taskSubmitted(future, this, task);
      return new CUFuture((Future)Future.class.cast(future), wrapper);
   }

   public void execute(Runnable command) {
      CURunnable wrapper = new CURunnable(command);
      this.delegate.execute(wrapper);
      wrapper.taskSubmitted((Future)null, this, command);
   }

   public ExecutorService getDelegate() {
      return this.delegate;
   }

   public void destroyResource() {
      List<Runnable> runnables = this.delegate.shutdownNow();
      if (runnables.size() > 0) {
         LOGGER.warning(runnables.size() + " tasks to execute");

         for(Runnable runnable : runnables) {
            try {
               LOGGER.info("Executing " + runnable);
               runnable.run();
            } catch (Throwable th) {
               LOGGER.error(th.getMessage(), th);
            }
         }
      }

   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, ManagedExecutorServiceImpl.class);
   }
}
