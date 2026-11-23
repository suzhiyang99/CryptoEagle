package org.apache.spark;

import java.util.Properties;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.metrics.MetricsSystem;
import scala.Serializable;

public final class TaskContext$ implements Serializable {
   public static final TaskContext$ MODULE$;
   private final ThreadLocal<TaskContext> taskContext;

   static {
      new TaskContext$();
   }

   public TaskContext get() {
      return (TaskContext)this.taskContext.get();
   }

   public int getPartitionId() {
      TaskContext tc = (TaskContext)this.taskContext.get();
      return tc == null ? 0 : tc.partitionId();
   }

   public void setTaskContext(TaskContext tc) {
      this.taskContext.set(tc);
   }

   public void unset() {
      this.taskContext.remove();
   }

   public TaskContextImpl empty() {
      return new TaskContextImpl(0, 0, 0, 0L, 0, (TaskMemoryManager)null, new Properties(), (MetricsSystem)null, TaskContextImpl$.MODULE$.$lessinit$greater$default$9());
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskContext$() {
      MODULE$ = this;
      this.taskContext = new ThreadLocal();
   }
}
