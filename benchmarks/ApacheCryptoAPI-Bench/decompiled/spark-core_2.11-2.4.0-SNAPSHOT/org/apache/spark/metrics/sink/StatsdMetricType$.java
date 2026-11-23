package org.apache.spark.metrics.sink;

public final class StatsdMetricType$ {
   public static final StatsdMetricType$ MODULE$;
   private final String COUNTER;
   private final String GAUGE;
   private final String TIMER;
   private final String Set;

   static {
      new StatsdMetricType$();
   }

   public String COUNTER() {
      return this.COUNTER;
   }

   public String GAUGE() {
      return this.GAUGE;
   }

   public String TIMER() {
      return this.TIMER;
   }

   public String Set() {
      return this.Set;
   }

   private StatsdMetricType$() {
      MODULE$ = this;
      this.COUNTER = "c";
      this.GAUGE = "g";
      this.TIMER = "ms";
      this.Set = "s";
   }
}
