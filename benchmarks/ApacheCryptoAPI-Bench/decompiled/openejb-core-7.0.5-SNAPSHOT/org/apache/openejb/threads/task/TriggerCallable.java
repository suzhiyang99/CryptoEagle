package org.apache.openejb.threads.task;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import javax.enterprise.concurrent.Trigger;
import org.apache.openejb.threads.impl.ManagedScheduledExecutorServiceImpl;

public class TriggerCallable<V> extends TriggerTask<V> implements Callable<V> {
   private final Callable<V> delegate;

   public TriggerCallable(ManagedScheduledExecutorServiceImpl es, Callable<V> original, Callable<V> wrap, Trigger trigger, Date taskScheduledTime, String id, AtomicReference<Future<V>> ref) {
      super(original, es, trigger, taskScheduledTime, id, ref);
      this.delegate = wrap;
   }

   public V call() throws Exception {
      return (V)this.invoke();
   }

   protected V doInvoke() throws Exception {
      return (V)this.delegate.call();
   }
}
