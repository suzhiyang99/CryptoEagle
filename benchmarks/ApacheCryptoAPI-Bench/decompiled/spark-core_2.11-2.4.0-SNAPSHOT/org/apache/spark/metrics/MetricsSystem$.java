package org.apache.spark.metrics;

import java.util.concurrent.TimeUnit;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;
import scala.util.matching.Regex;

public final class MetricsSystem$ {
   public static final MetricsSystem$ MODULE$;
   private final Regex SINK_REGEX;
   private final Regex SOURCE_REGEX;
   private final TimeUnit MINIMAL_POLL_UNIT;
   private final int MINIMAL_POLL_PERIOD;

   static {
      new MetricsSystem$();
   }

   public Regex SINK_REGEX() {
      return this.SINK_REGEX;
   }

   public Regex SOURCE_REGEX() {
      return this.SOURCE_REGEX;
   }

   public void checkMinimalPollingPeriod(TimeUnit pollUnit, int pollPeriod) {
      long period = this.MINIMAL_POLL_UNIT.convert((long)pollPeriod, pollUnit);
      if (period < (long)this.MINIMAL_POLL_PERIOD) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"Polling period ").append((Object)BoxesRunTime.boxToInteger(pollPeriod)).append((Object)" ").append((Object)pollUnit).append((Object)" below than minimal polling period ").toString());
      }
   }

   public MetricsSystem createMetricsSystem(String instance, SparkConf conf, SecurityManager securityMgr) {
      return new MetricsSystem(instance, conf, securityMgr);
   }

   private MetricsSystem$() {
      MODULE$ = this;
      this.SINK_REGEX = (new StringOps(Predef$.MODULE$.augmentString("^sink\\.(.+)\\.(.+)"))).r();
      this.SOURCE_REGEX = (new StringOps(Predef$.MODULE$.augmentString("^source\\.(.+)\\.(.+)"))).r();
      this.MINIMAL_POLL_UNIT = TimeUnit.SECONDS;
      this.MINIMAL_POLL_PERIOD = 1;
   }
}
