package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class SparkListenerExecutorRemoved$ extends AbstractFunction3<Object, String, String, SparkListenerExecutorRemoved> implements Serializable {
   public static final SparkListenerExecutorRemoved$ MODULE$;

   static {
      new SparkListenerExecutorRemoved$();
   }

   public final String toString() {
      return "SparkListenerExecutorRemoved";
   }

   public SparkListenerExecutorRemoved apply(long time, String executorId, String reason) {
      return new SparkListenerExecutorRemoved(time, executorId, reason);
   }

   public Option<Tuple3<Object, String, String>> unapply(SparkListenerExecutorRemoved x$0) {
      return (Option<Tuple3<Object, String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.time()), x$0.executorId(), x$0.reason())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerExecutorRemoved$() {
      MODULE$ = this;
   }
}
