package org.apache.openejb.threads.task;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.enterprise.concurrent.LastExecution;
import javax.enterprise.concurrent.SkippedException;
import javax.enterprise.concurrent.Trigger;
import org.apache.openejb.threads.impl.ManagedScheduledExecutorServiceImpl;

public abstract class TriggerTask<T> extends CUTask<T> {
   protected final ManagedScheduledExecutorServiceImpl executorService;
   protected final Trigger trigger;
   protected final Date scheduledTime;
   protected final String id;
   protected final AtomicReference<Future<T>> futureRef;
   protected LastExecution lastExecution;
   protected volatile boolean skipped;
   protected volatile boolean done;
   private final AtomicBoolean running = new AtomicBoolean(true);
   private volatile T result;

   protected TriggerTask(Object original, ManagedScheduledExecutorServiceImpl es, Trigger trigger, Date taskScheduledTime, String id, AtomicReference<Future<T>> ref) {
      super(original);
      this.executorService = es;
      this.trigger = trigger;
      this.scheduledTime = taskScheduledTime;
      this.id = id;
      this.futureRef = ref;
   }

   public T invoke() throws Exception {
      return (T)this.invoke(new Callable<T>() {
         public T call() throws Exception {
            long wait = TriggerTask.this.nextDelay(TriggerTask.this.trigger.getNextRunTime(TriggerTask.this.lastExecution, TriggerTask.this.scheduledTime));
            if (wait > 0L) {
               Thread.sleep(wait);
            }

            Date now = new Date();

            try {
               boolean skip = TriggerTask.this.trigger.skipRun(TriggerTask.this.lastExecution, now);
               if (!skip) {
                  TriggerTask.this.result = (T)TriggerTask.this.doInvoke();
                  TriggerTask.this.taskDone(TriggerTask.this.future, TriggerTask.this.executor, TriggerTask.this.delegate, (Throwable)null);
                  TriggerTask.this.lastExecution = new LastExecutionImpl(TriggerTask.this.id, TriggerTask.this.result, TriggerTask.this.scheduledTime, now, new Date());
               } else {
                  TriggerTask.this.result = (T)null;
                  TriggerTask.this.skipped = true;
                  TriggerTask.this.running.set(false);
               }
            } catch (RuntimeException re) {
               SkippedException skippedException = new SkippedException(re);
               TriggerTask.this.taskAborted(skippedException);
               throw skippedException;
            }

            ScheduledFuture<T> future = TriggerTask.this.executorService.schedule((Callable)this, TriggerTask.this.trigger.getNextRunTime(TriggerTask.this.lastExecution, TriggerTask.this.scheduledTime).getTime() - ManagedScheduledExecutorServiceImpl.nowMs(), TimeUnit.MILLISECONDS);
            TriggerTask.this.futureRef.set(future);
            TriggerTask.this.taskSubmitted(future, TriggerTask.this.executorService, TriggerTask.this.delegate);
            return TriggerTask.this.result;
         }
      });
   }

   protected long nextDelay(Date next) {
      return next.getTime() - ManagedScheduledExecutorServiceImpl.nowMs();
   }

   protected abstract T doInvoke() throws Exception;

   public String getId() {
      return this.id;
   }

   public void stop() {
      this.running.set(false);
   }

   public boolean isDone() {
      return this.done;
   }

   public T getResult() {
      return this.result;
   }

   public boolean isSkipped() {
      return this.skipped;
   }

   public LastExecution getLastExecution() {
      return this.lastExecution;
   }

   private static class LastExecutionImpl implements LastExecution {
      private final String identityName;
      private final Object result;
      private final Date scheduledStart;
      private final Date runStart;
      private final Date runEnd;

      public LastExecutionImpl(String identityName, Object result, Date scheduledStart, Date runStart, Date runEnd) {
         this.identityName = identityName;
         this.result = result;
         this.scheduledStart = scheduledStart;
         this.runStart = runStart;
         this.runEnd = runEnd;
      }

      public String getIdentityName() {
         return this.identityName;
      }

      public Object getResult() {
         return this.result;
      }

      public Date getScheduledStart() {
         return this.scheduledStart;
      }

      public Date getRunStart() {
         return this.runStart;
      }

      public Date getRunEnd() {
         return this.runEnd;
      }

      public String toString() {
         return "LastExecutionImpl{" + (this.identityName != null ? "identityName='" + this.identityName + "', " : "") + "result=" + this.result + ", scheduledStart=" + this.scheduledStart + ", runStart=" + this.runStart + ", runEnd=" + this.runEnd + '}';
      }
   }
}
