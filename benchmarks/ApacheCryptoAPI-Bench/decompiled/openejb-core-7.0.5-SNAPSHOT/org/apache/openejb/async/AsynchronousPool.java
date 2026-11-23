package org.apache.openejb.async;

import java.rmi.NoSuchObjectException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.EJBException;
import javax.ejb.NoSuchEJBException;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.loader.Options;
import org.apache.openejb.util.DaemonThreadFactory;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.ExecutorBuilder;

public class AsynchronousPool {
   private final BlockingQueue<Runnable> blockingQueue;
   private final ExecutorService executor;
   private final Duration awaitDuration;

   public AsynchronousPool(ThreadPoolExecutor threadPoolExecutor, Duration awaitDuration) {
      this.blockingQueue = threadPoolExecutor.getQueue();
      this.executor = threadPoolExecutor;
      this.awaitDuration = awaitDuration;
   }

   public static AsynchronousPool create(AppContext appContext) {
      Options options = appContext.getOptions();
      ExecutorBuilder builder = (new ExecutorBuilder()).prefix("AsynchronousPool").size(options.get("AsynchronousPool.Size", 5)).threadFactory(new DaemonThreadFactory(new Object[]{"@Asynchronous", appContext.getId()}));
      return new AsynchronousPool(builder.build(options), (Duration)options.get("AsynchronousPool.ShutdownWaitDuration", new Duration(1L, TimeUnit.MINUTES)));
   }

   public Object invoke(Callable<Object> callable, boolean isVoid) throws Throwable {
      AtomicBoolean asynchronousCancelled = new AtomicBoolean(false);

      try {
         Future<Object> future = this.executor.submit(new AsynchronousCall(callable, asynchronousCancelled));
         return isVoid ? null : new FutureAdapter(future, asynchronousCancelled);
      } catch (RejectedExecutionException e) {
         throw new EJBException("fail to allocate internal resource to execute the target task", e);
      }
   }

   public void stop() {
      this.executor.shutdown();

      try {
         this.executor.awaitTermination(this.awaitDuration.getTime(), this.awaitDuration.getUnit());
      } catch (InterruptedException var2) {
      }

   }

   private final class AsynchronousCall implements Callable<Object> {
      private final Callable<Object> callable;
      private final AtomicBoolean asynchronousCancelled;

      private AsynchronousCall(Callable<Object> callable, AtomicBoolean asynchronousCancelled) {
         this.callable = callable;
         this.asynchronousCancelled = asynchronousCancelled;
      }

      public Object call() throws Exception {
         Object var3;
         try {
            ThreadContext.initAsynchronousCancelled(this.asynchronousCancelled);
            Object value = this.callable.call();
            if (!(value instanceof Future)) {
               Object var7 = null;
               return var7;
            }

            Future<?> future = (Future)value;
            var3 = future.get();
         } finally {
            ThreadContext.removeAsynchronousCancelled();
         }

         return var3;
      }
   }

   private class FutureAdapter<T> implements Future<T> {
      private final Future<T> target;
      private final AtomicBoolean asynchronousCancelled;
      private volatile boolean canceled;

      public FutureAdapter(Future<T> target, AtomicBoolean asynchronousCancelled) {
         this.target = target;
         this.asynchronousCancelled = asynchronousCancelled;
      }

      public boolean cancel(boolean mayInterruptIfRunning) {
         if (this.canceled) {
            return true;
         } else if (AsynchronousPool.this.blockingQueue.remove((Runnable)this.target)) {
            this.canceled = true;
            return true;
         } else {
            if (!this.target.isDone()) {
               this.asynchronousCancelled.set(mayInterruptIfRunning);
            }

            return false;
         }
      }

      public T get() throws InterruptedException, ExecutionException {
         if (this.canceled) {
            throw new CancellationException();
         } else {
            T object = null;

            try {
               object = (T)this.target.get();
            } catch (Throwable e) {
               this.handleException(e);
            }

            return object;
         }
      }

      public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
         if (this.canceled) {
            throw new CancellationException();
         } else {
            T object = null;

            try {
               object = (T)this.target.get(timeout, unit);
            } catch (Throwable e) {
               this.handleException(e);
            }

            return object;
         }
      }

      private void handleException(Throwable e) throws ExecutionException {
         while(e.getCause() != null) {
            e = e.getCause();
         }

         if (e instanceof NoSuchObjectException) {
            e = new NoSuchEJBException(e.getMessage(), (Exception)e);
         }

         boolean isExceptionUnchecked = e instanceof Error || e instanceof RuntimeException;
         if (isExceptionUnchecked && !(e instanceof EJBException)) {
            ThreadContext tc = ThreadContext.getThreadContext();
            if (tc != null) {
               BeanContext bc = tc.getBeanContext();
               if (bc != null) {
                  ExceptionType exceptionType = bc.getExceptionType(e);
                  if (exceptionType == ExceptionType.APPLICATION) {
                     throw new ExecutionException((Throwable)Exception.class.cast(e));
                  }
               }
            }

            throw e instanceof Exception ? new ExecutionException(new EJBException((Exception)e)) : new ExecutionException(new EJBException(new Exception(e)));
         } else {
            throw new ExecutionException(e);
         }
      }

      public boolean isCancelled() {
         return this.canceled;
      }

      public boolean isDone() {
         return this.canceled ? false : this.target.isDone();
      }
   }
}
