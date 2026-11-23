package org.apache.spark.scheduler.cluster;

public final class CoarseGrainedSchedulerBackend$ {
   public static final CoarseGrainedSchedulerBackend$ MODULE$;
   private final String ENDPOINT_NAME;

   static {
      new CoarseGrainedSchedulerBackend$();
   }

   public String ENDPOINT_NAME() {
      return this.ENDPOINT_NAME;
   }

   private CoarseGrainedSchedulerBackend$() {
      MODULE$ = this;
      this.ENDPOINT_NAME = "CoarseGrainedScheduler";
   }
}
