package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class SparkListenerNodeUnblacklisted$ extends AbstractFunction2<Object, String, SparkListenerNodeUnblacklisted> implements Serializable {
   public static final SparkListenerNodeUnblacklisted$ MODULE$;

   static {
      new SparkListenerNodeUnblacklisted$();
   }

   public final String toString() {
      return "SparkListenerNodeUnblacklisted";
   }

   public SparkListenerNodeUnblacklisted apply(long time, String hostId) {
      return new SparkListenerNodeUnblacklisted(time, hostId);
   }

   public Option<Tuple2<Object, String>> unapply(SparkListenerNodeUnblacklisted x$0) {
      return (Option<Tuple2<Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToLong(x$0.time()), x$0.hostId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerNodeUnblacklisted$() {
      MODULE$ = this;
   }
}
