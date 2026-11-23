package org.apache.openejb.threads.future;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.openejb.threads.task.ManagedTaskListenerTask;

public class CUScheduleFuture<V> extends CUFuture<V, Delayed> implements ScheduledFuture<V> {
   public CUScheduleFuture(ScheduledFuture<V> delegate, ManagedTaskListenerTask listener) {
      super(delegate, listener);
   }

   public long getDelay(TimeUnit unit) {
      return ((ScheduledFuture)ScheduledFuture.class.cast(this.delegate)).getDelay(unit);
   }

   public int compareTo(Delayed o) {
      return ((ScheduledFuture)ScheduledFuture.class.cast(this.delegate)).compareTo(o);
   }
}
