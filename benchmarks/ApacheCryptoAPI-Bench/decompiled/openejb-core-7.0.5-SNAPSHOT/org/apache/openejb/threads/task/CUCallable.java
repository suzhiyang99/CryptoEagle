package org.apache.openejb.threads.task;

import java.util.concurrent.Callable;

public class CUCallable<T> extends CUTask<T> implements Callable<T> {
   private final Callable<? extends T> delegate;

   public CUCallable(Callable<? extends T> task) {
      super(task);
      this.delegate = task;
   }

   public T call() throws Exception {
      return (T)this.invoke(new Callable<T>() {
         public T call() throws Exception {
            return (T)CUCallable.this.delegate.call();
         }
      });
   }
}
