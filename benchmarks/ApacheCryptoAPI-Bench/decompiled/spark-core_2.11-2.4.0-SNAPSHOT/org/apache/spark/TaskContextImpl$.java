package org.apache.spark;

import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.executor.TaskMetrics$;
import scala.Serializable;

public final class TaskContextImpl$ implements Serializable {
   public static final TaskContextImpl$ MODULE$;

   static {
      new TaskContextImpl$();
   }

   public TaskMetrics $lessinit$greater$default$9() {
      return TaskMetrics$.MODULE$.empty();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskContextImpl$() {
      MODULE$ = this;
   }
}
