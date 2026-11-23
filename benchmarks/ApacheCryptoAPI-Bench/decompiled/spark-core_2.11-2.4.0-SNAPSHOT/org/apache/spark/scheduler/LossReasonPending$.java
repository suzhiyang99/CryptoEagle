package org.apache.spark.scheduler;

public final class LossReasonPending$ extends ExecutorLossReason {
   public static final LossReasonPending$ MODULE$;

   static {
      new LossReasonPending$();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private LossReasonPending$() {
      super("Pending loss reason.");
      MODULE$ = this;
   }
}
