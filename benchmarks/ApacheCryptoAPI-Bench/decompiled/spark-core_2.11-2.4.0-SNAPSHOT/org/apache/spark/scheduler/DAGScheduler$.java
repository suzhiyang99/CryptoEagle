package org.apache.spark.scheduler;

import org.apache.spark.util.Clock;
import org.apache.spark.util.SystemClock;

public final class DAGScheduler$ {
   public static final DAGScheduler$ MODULE$;
   private final int RESUBMIT_TIMEOUT;
   private final int DEFAULT_MAX_CONSECUTIVE_STAGE_ATTEMPTS;

   static {
      new DAGScheduler$();
   }

   public int RESUBMIT_TIMEOUT() {
      return this.RESUBMIT_TIMEOUT;
   }

   public int DEFAULT_MAX_CONSECUTIVE_STAGE_ATTEMPTS() {
      return this.DEFAULT_MAX_CONSECUTIVE_STAGE_ATTEMPTS;
   }

   public Clock $lessinit$greater$default$7() {
      return new SystemClock();
   }

   private DAGScheduler$() {
      MODULE$ = this;
      this.RESUBMIT_TIMEOUT = 200;
      this.DEFAULT_MAX_CONSECUTIVE_STAGE_ATTEMPTS = 4;
   }
}
