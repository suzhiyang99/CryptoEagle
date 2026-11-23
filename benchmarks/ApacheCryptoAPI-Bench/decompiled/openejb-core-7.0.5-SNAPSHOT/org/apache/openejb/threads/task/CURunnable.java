package org.apache.openejb.threads.task;

import java.util.concurrent.Callable;

public class CURunnable extends CUTask<Void> implements Runnable {
   private final Runnable delegate;

   public CURunnable(Runnable task) {
      super(task);
      this.delegate = task;
   }

   public void run() {
      try {
         this.invoke(new Callable<Void>() {
            public Void call() throws Exception {
               CURunnable.this.delegate.run();
               return null;
            }
         });
      } catch (RuntimeException re) {
         throw re;
      } catch (Exception var3) {
      }

   }
}
