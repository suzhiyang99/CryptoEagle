package org.spark_project.jetty.util.thread.strategy;

import java.io.Closeable;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ExecutionStrategy;

public abstract class ExecutingExecutionStrategy implements ExecutionStrategy {
   private static final Logger LOG = Log.getLogger(ExecutingExecutionStrategy.class);
   private final Executor _executor;

   protected ExecutingExecutionStrategy(Executor executor) {
      this._executor = executor;
   }

   protected boolean execute(Runnable task) {
      try {
         this._executor.execute(task);
         return true;
      } catch (RejectedExecutionException e) {
         LOG.debug(e);
         LOG.warn("Rejected execution of {}", task);

         try {
            if (task instanceof Closeable) {
               ((Closeable)task).close();
            }
         } catch (Exception x) {
            e.addSuppressed(x);
            LOG.warn(e);
         }

         return false;
      }
   }
}
