package org.apache.spark.util.logging;

public final class TimeBasedRollingPolicy$ {
   public static final TimeBasedRollingPolicy$ MODULE$;
   private final long MINIMUM_INTERVAL_SECONDS;

   static {
      new TimeBasedRollingPolicy$();
   }

   public long MINIMUM_INTERVAL_SECONDS() {
      return this.MINIMUM_INTERVAL_SECONDS;
   }

   public boolean $lessinit$greater$default$3() {
      return true;
   }

   private TimeBasedRollingPolicy$() {
      MODULE$ = this;
      this.MINIMUM_INTERVAL_SECONDS = 60L;
   }
}
