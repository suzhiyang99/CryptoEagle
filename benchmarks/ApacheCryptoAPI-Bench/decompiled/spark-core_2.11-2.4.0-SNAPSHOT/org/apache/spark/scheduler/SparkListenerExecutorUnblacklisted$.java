package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class SparkListenerExecutorUnblacklisted$ extends AbstractFunction2<Object, String, SparkListenerExecutorUnblacklisted> implements Serializable {
   public static final SparkListenerExecutorUnblacklisted$ MODULE$;

   static {
      new SparkListenerExecutorUnblacklisted$();
   }

   public final String toString() {
      return "SparkListenerExecutorUnblacklisted";
   }

   public SparkListenerExecutorUnblacklisted apply(long time, String executorId) {
      return new SparkListenerExecutorUnblacklisted(time, executorId);
   }

   public Option<Tuple2<Object, String>> unapply(SparkListenerExecutorUnblacklisted x$0) {
      return (Option<Tuple2<Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToLong(x$0.time()), x$0.executorId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerExecutorUnblacklisted$() {
      MODULE$ = this;
   }
}
