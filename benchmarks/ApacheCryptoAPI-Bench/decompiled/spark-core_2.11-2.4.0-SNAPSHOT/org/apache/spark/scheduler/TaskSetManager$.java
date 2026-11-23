package org.apache.spark.scheduler;

import org.apache.spark.util.Clock;
import org.apache.spark.util.SystemClock;
import scala.None$;
import scala.Option;

public final class TaskSetManager$ {
   public static final TaskSetManager$ MODULE$;
   private final int TASK_SIZE_TO_WARN_KB;

   static {
      new TaskSetManager$();
   }

   public int TASK_SIZE_TO_WARN_KB() {
      return this.TASK_SIZE_TO_WARN_KB;
   }

   public Option<BlacklistTracker> $lessinit$greater$default$4() {
      return None$.MODULE$;
   }

   public Clock $lessinit$greater$default$5() {
      return new SystemClock();
   }

   private TaskSetManager$() {
      MODULE$ = this;
      this.TASK_SIZE_TO_WARN_KB = 100;
   }
}
