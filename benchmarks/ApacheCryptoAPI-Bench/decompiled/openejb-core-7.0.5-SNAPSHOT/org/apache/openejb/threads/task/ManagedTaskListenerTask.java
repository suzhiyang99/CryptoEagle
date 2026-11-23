package org.apache.openejb.threads.task;

import java.util.concurrent.Future;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;

public abstract class ManagedTaskListenerTask implements ManagedTaskListener {
   private final ManagedTaskListener listener;
   protected Future<?> future;
   protected final Object delegate;
   protected ManagedExecutorService executor;

   protected ManagedTaskListenerTask(Object task) {
      if (ManagedTask.class.isInstance(task)) {
         this.listener = ((ManagedTask)ManagedTask.class.cast(task)).getManagedTaskListener();
      } else {
         this.listener = ManagedTaskListenerTask.NoopManagedTaskListener.INSTANCE;
      }

      this.delegate = task;
   }

   public void taskSubmitted(Future<?> future, ManagedExecutorService executor, Object task) {
      this.future = future;
      this.executor = executor;
      if (this.listener != null) {
         this.listener.taskSubmitted(future, executor, task);
      }

   }

   public void taskAborted(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
      if (this.listener != null) {
         this.listener.taskAborted(this.future, this.executor, this.delegate, exception);
      }

   }

   public void taskDone(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
      if (this.listener != null) {
         this.listener.taskDone(future, executor, task, exception);
      }

   }

   public void taskStarting(Future<?> future, ManagedExecutorService executor, Object task) {
      if (this.listener != null) {
         this.listener.taskStarting(future, executor, task);
      }

   }

   public void taskAborted(Throwable skippedException) {
      this.taskAborted(this.future, this.executor, this.delegate, skippedException);
   }

   public Object getDelegate() {
      return this.delegate;
   }

   private static class NoopManagedTaskListener implements ManagedTaskListener {
      private static final NoopManagedTaskListener INSTANCE = new NoopManagedTaskListener();

      public void taskSubmitted(Future<?> future, ManagedExecutorService executor, Object task) {
      }

      public void taskAborted(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
      }

      public void taskDone(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
      }

      public void taskStarting(Future<?> future, ManagedExecutorService executor, Object task) {
      }
   }
}
