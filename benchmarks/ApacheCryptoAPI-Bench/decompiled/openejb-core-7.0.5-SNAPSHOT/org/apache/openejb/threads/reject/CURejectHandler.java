package org.apache.openejb.threads.reject;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.openejb.threads.task.ManagedTaskListenerTask;

public class CURejectHandler extends ThreadPoolExecutor.AbortPolicy {
   public static final RejectedExecutionHandler INSTANCE = new CURejectHandler();

   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      if (ManagedTaskListenerTask.class.isInstance(r)) {
         ((ManagedTaskListenerTask)ManagedTaskListenerTask.class.cast(r)).taskAborted(new RejectedExecutionException());
      }

      super.rejectedExecution(r, executor);
   }
}
