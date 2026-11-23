package org.apache.openejb.threads.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.Trigger;
import org.apache.openejb.threads.future.CUScheduleFuture;
import org.apache.openejb.threads.task.CUCallable;
import org.apache.openejb.threads.task.CURunnable;
import org.apache.openejb.threads.task.TriggerCallable;
import org.apache.openejb.threads.task.TriggerRunnable;
import org.apache.openejb.threads.task.TriggerTask;

public class ManagedScheduledExecutorServiceImpl extends ManagedExecutorServiceImpl implements ManagedScheduledExecutorService {
   private final ScheduledExecutorService delegate;

   public ManagedScheduledExecutorServiceImpl(ScheduledExecutorService delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   public ScheduledFuture<?> schedule(Runnable runnable, Trigger trigger) {
      Date taskScheduledTime = new Date();
      AtomicReference<Future<?>> futureHandle = new AtomicReference();
      TriggerRunnable wrapper = new TriggerRunnable(this, runnable, new CURunnable(runnable), trigger, taskScheduledTime, getTaskId(runnable), (AtomicReference)AtomicReference.class.cast(futureHandle));
      ScheduledFuture<?> future = this.delegate.schedule(wrapper, trigger.getNextRunTime(wrapper.getLastExecution(), taskScheduledTime).getTime() - nowMs(), TimeUnit.MILLISECONDS);
      return this.initTriggerScheduledFuture(runnable, (AtomicReference)AtomicReference.class.cast(futureHandle), wrapper, (ScheduledFuture)ScheduledFuture.class.cast(future));
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> vCallable, Trigger trigger) {
      Date taskScheduledTime = new Date();
      AtomicReference<Future<V>> futureHandle = new AtomicReference();
      TriggerCallable<V> wrapper = new TriggerCallable<V>(this, vCallable, new CUCallable(vCallable), trigger, taskScheduledTime, getTaskId(vCallable), futureHandle);
      ScheduledFuture<V> future = this.delegate.schedule(wrapper, trigger.getNextRunTime(wrapper.getLastExecution(), taskScheduledTime).getTime() - nowMs(), TimeUnit.MILLISECONDS);
      return this.<V>initTriggerScheduledFuture(vCallable, futureHandle, wrapper, future);
   }

   private <V> ScheduledFuture<V> initTriggerScheduledFuture(Object original, AtomicReference<Future<V>> futureHandle, TriggerTask<V> wrapper, ScheduledFuture<V> future) {
      futureHandle.set(future);
      wrapper.taskSubmitted(future, this, original);
      return new CUScheduleFuture<V>(ManagedScheduledExecutorServiceImpl.ScheduledFutureFacade.newProxy(futureHandle), wrapper);
   }

   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      CURunnable wrapper = new CURunnable(command);
      ScheduledFuture<?> future = this.delegate.schedule(wrapper, delay, unit);
      wrapper.taskSubmitted(future, this, command);
      return new CUScheduleFuture((ScheduledFuture)ScheduledFuture.class.cast(future), wrapper);
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      CUCallable<V> wrapper = new CUCallable<V>(callable);
      ScheduledFuture<V> future = this.delegate.schedule(wrapper, delay, unit);
      wrapper.taskSubmitted(future, this, callable);
      return new CUScheduleFuture<V>(future, wrapper);
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      CURunnable wrapper = new CURunnable(command);
      ScheduledFuture<?> future = this.delegate.scheduleAtFixedRate(wrapper, initialDelay, period, unit);
      wrapper.taskSubmitted(future, this, command);
      return new CUScheduleFuture((ScheduledFuture)ScheduledFuture.class.cast(future), wrapper);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      CURunnable wrapper = new CURunnable(command);
      ScheduledFuture<?> future = this.delegate.scheduleWithFixedDelay(wrapper, initialDelay, delay, unit);
      wrapper.taskSubmitted(future, this, command);
      return new CUScheduleFuture((ScheduledFuture)ScheduledFuture.class.cast(future), wrapper);
   }

   public static long nowMs() {
      return System.currentTimeMillis();
   }

   private static String getTaskId(Object runnable) {
      return ManagedTask.class.isInstance(runnable) ? (String)((ManagedTask)ManagedTask.class.cast(runnable)).getExecutionProperties().get("javax.enterprise.concurrent.IDENTITY_NAME") : null;
   }

   private static final class ScheduledFutureFacade<V> implements InvocationHandler {
      private final AtomicReference<ScheduledFuture<V>> delegate;

      private ScheduledFutureFacade(AtomicReference<ScheduledFuture<V>> delegate) {
         this.delegate = delegate;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            return method.invoke(this.delegate.get(), args);
         } catch (InvocationTargetException ite) {
            throw ite.getCause();
         }
      }

      private static <V> ScheduledFuture<V> newProxy(AtomicReference<Future<V>> futureHandle) {
         return (ScheduledFuture)ScheduledFuture.class.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ScheduledFuture.class}, new ScheduledFutureFacade(futureHandle)));
      }
   }
}
