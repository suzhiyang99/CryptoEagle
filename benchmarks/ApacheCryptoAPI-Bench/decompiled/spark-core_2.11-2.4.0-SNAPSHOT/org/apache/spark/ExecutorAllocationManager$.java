package org.apache.spark;

public final class ExecutorAllocationManager$ {
   public static final ExecutorAllocationManager$ MODULE$;
   private final long NOT_SET;
   private final String TESTING_SCHEDULE_INTERVAL_KEY;

   static {
      new ExecutorAllocationManager$();
   }

   public long NOT_SET() {
      return this.NOT_SET;
   }

   public String TESTING_SCHEDULE_INTERVAL_KEY() {
      return this.TESTING_SCHEDULE_INTERVAL_KEY;
   }

   private ExecutorAllocationManager$() {
      MODULE$ = this;
      this.NOT_SET = Long.MAX_VALUE;
      this.TESTING_SCHEDULE_INTERVAL_KEY = "spark.testing.dynamicAllocation.scheduleInterval";
   }
}
