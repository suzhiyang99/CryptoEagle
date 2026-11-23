package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class SparkListenerExecutorBlacklisted$ extends AbstractFunction3<Object, String, Object, SparkListenerExecutorBlacklisted> implements Serializable {
   public static final SparkListenerExecutorBlacklisted$ MODULE$;

   static {
      new SparkListenerExecutorBlacklisted$();
   }

   public final String toString() {
      return "SparkListenerExecutorBlacklisted";
   }

   public SparkListenerExecutorBlacklisted apply(long time, String executorId, int taskFailures) {
      return new SparkListenerExecutorBlacklisted(time, executorId, taskFailures);
   }

   public Option<Tuple3<Object, String, Object>> unapply(SparkListenerExecutorBlacklisted x$0) {
      return (Option<Tuple3<Object, String, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.time()), x$0.executorId(), BoxesRunTime.boxToInteger(x$0.taskFailures()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerExecutorBlacklisted$() {
      MODULE$ = this;
   }
}
