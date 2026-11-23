package org.apache.spark.scheduler;

import org.apache.spark.scheduler.cluster.ExecutorInfo;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class SparkListenerExecutorAdded$ extends AbstractFunction3<Object, String, ExecutorInfo, SparkListenerExecutorAdded> implements Serializable {
   public static final SparkListenerExecutorAdded$ MODULE$;

   static {
      new SparkListenerExecutorAdded$();
   }

   public final String toString() {
      return "SparkListenerExecutorAdded";
   }

   public SparkListenerExecutorAdded apply(long time, String executorId, ExecutorInfo executorInfo) {
      return new SparkListenerExecutorAdded(time, executorId, executorInfo);
   }

   public Option<Tuple3<Object, String, ExecutorInfo>> unapply(SparkListenerExecutorAdded x$0) {
      return (Option<Tuple3<Object, String, ExecutorInfo>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.time()), x$0.executorId(), x$0.executorInfo())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerExecutorAdded$() {
      MODULE$ = this;
   }
}
