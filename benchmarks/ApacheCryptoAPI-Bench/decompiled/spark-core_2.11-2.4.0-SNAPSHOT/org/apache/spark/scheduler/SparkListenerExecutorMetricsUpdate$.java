package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple4;
import scala.collection.Seq;
import scala.runtime.AbstractFunction2;

public final class SparkListenerExecutorMetricsUpdate$ extends AbstractFunction2<String, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>>, SparkListenerExecutorMetricsUpdate> implements Serializable {
   public static final SparkListenerExecutorMetricsUpdate$ MODULE$;

   static {
      new SparkListenerExecutorMetricsUpdate$();
   }

   public final String toString() {
      return "SparkListenerExecutorMetricsUpdate";
   }

   public SparkListenerExecutorMetricsUpdate apply(String execId, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> accumUpdates) {
      return new SparkListenerExecutorMetricsUpdate(execId, accumUpdates);
   }

   public Option<Tuple2<String, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>>>> unapply(SparkListenerExecutorMetricsUpdate x$0) {
      return (Option<Tuple2<String, Seq<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.execId(), x$0.accumUpdates())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerExecutorMetricsUpdate$() {
      MODULE$ = this;
   }
}
