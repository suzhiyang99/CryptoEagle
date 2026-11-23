package org.apache.spark.scheduler;

public final class ExecutorKilled$ extends ExecutorLossReason {
   public static final ExecutorKilled$ MODULE$;

   static {
      new ExecutorKilled$();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorKilled$() {
      super("Executor killed by driver.");
      MODULE$ = this;
   }
}
