package org.apache.openejb.threads.task;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import javax.enterprise.concurrent.Trigger;
import org.apache.openejb.threads.impl.ManagedScheduledExecutorServiceImpl;

public class TriggerRunnable extends TriggerTask<Void> implements Runnable {
   private final Runnable delegate;

   public TriggerRunnable(ManagedScheduledExecutorServiceImpl es, Runnable original, Runnable runnable, Trigger trigger, Date taskScheduledTime, String id, AtomicReference<Future<Void>> ref) {
      super(original, es, trigger, taskScheduledTime, id, ref);
      this.delegate = runnable;
   }

   protected Void doInvoke() {
      this.delegate.run();
      return null;
   }

   public void run() {
      try {
         this.invoke();
      } catch (RuntimeException re) {
         throw re;
      } catch (Exception var3) {
      }

   }
}
