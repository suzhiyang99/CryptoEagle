package org.apache.openejb.threads.future;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.openejb.threads.task.ManagedTaskListenerTask;

public class CUFuture<V, C> implements Future<V>, Comparable<C> {
   protected final Future<V> delegate;
   protected final ManagedTaskListenerTask listener;

   public CUFuture(Future<V> delegate, ManagedTaskListenerTask listener) {
      this.delegate = delegate;
      this.listener = listener;
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      boolean result = this.delegate.cancel(mayInterruptIfRunning);
      this.listener.taskAborted(new CancellationException());
      return result;
   }

   public boolean isCancelled() {
      return this.delegate.isCancelled();
   }

   public boolean isDone() {
      return this.delegate.isDone();
   }

   public V get() throws InterruptedException, ExecutionException {
      return (V)this.delegate.get();
   }

   public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return (V)this.delegate.get(timeout, unit);
   }

   public int compareTo(C o) {
      return Comparable.class.isInstance(this.delegate) ? ((Comparable)Comparable.class.cast(this.delegate)).compareTo(o) : -1;
   }
}
