package org.apache.spark.metrics.sink;

public final class StatsdSink$ {
   public static final StatsdSink$ MODULE$;
   private final String STATSD_KEY_HOST;
   private final String STATSD_KEY_PORT;
   private final String STATSD_KEY_PERIOD;
   private final String STATSD_KEY_UNIT;
   private final String STATSD_KEY_PREFIX;
   private final String STATSD_DEFAULT_HOST;
   private final String STATSD_DEFAULT_PORT;
   private final String STATSD_DEFAULT_PERIOD;
   private final String STATSD_DEFAULT_UNIT;
   private final String STATSD_DEFAULT_PREFIX;

   static {
      new StatsdSink$();
   }

   public String STATSD_KEY_HOST() {
      return this.STATSD_KEY_HOST;
   }

   public String STATSD_KEY_PORT() {
      return this.STATSD_KEY_PORT;
   }

   public String STATSD_KEY_PERIOD() {
      return this.STATSD_KEY_PERIOD;
   }

   public String STATSD_KEY_UNIT() {
      return this.STATSD_KEY_UNIT;
   }

   public String STATSD_KEY_PREFIX() {
      return this.STATSD_KEY_PREFIX;
   }

   public String STATSD_DEFAULT_HOST() {
      return this.STATSD_DEFAULT_HOST;
   }

   public String STATSD_DEFAULT_PORT() {
      return this.STATSD_DEFAULT_PORT;
   }

   public String STATSD_DEFAULT_PERIOD() {
      return this.STATSD_DEFAULT_PERIOD;
   }

   public String STATSD_DEFAULT_UNIT() {
      return this.STATSD_DEFAULT_UNIT;
   }

   public String STATSD_DEFAULT_PREFIX() {
      return this.STATSD_DEFAULT_PREFIX;
   }

   private StatsdSink$() {
      MODULE$ = this;
      this.STATSD_KEY_HOST = "host";
      this.STATSD_KEY_PORT = "port";
      this.STATSD_KEY_PERIOD = "period";
      this.STATSD_KEY_UNIT = "unit";
      this.STATSD_KEY_PREFIX = "prefix";
      this.STATSD_DEFAULT_HOST = "127.0.0.1";
      this.STATSD_DEFAULT_PORT = "8125";
      this.STATSD_DEFAULT_PERIOD = "10";
      this.STATSD_DEFAULT_UNIT = "SECONDS";
      this.STATSD_DEFAULT_PREFIX = "";
   }
}
