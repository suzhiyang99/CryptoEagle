package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class SparkListenerExecutorBlacklistedForStage$ extends AbstractFunction5<Object, String, Object, Object, Object, SparkListenerExecutorBlacklistedForStage> implements Serializable {
   public static final SparkListenerExecutorBlacklistedForStage$ MODULE$;

   static {
      new SparkListenerExecutorBlacklistedForStage$();
   }

   public final String toString() {
      return "SparkListenerExecutorBlacklistedForStage";
   }

   public SparkListenerExecutorBlacklistedForStage apply(long time, String executorId, int taskFailures, int stageId, int stageAttemptId) {
      return new SparkListenerExecutorBlacklistedForStage(time, executorId, taskFailures, stageId, stageAttemptId);
   }

   public Option<Tuple5<Object, String, Object, Object, Object>> unapply(SparkListenerExecutorBlacklistedForStage x$0) {
      return (Option<Tuple5<Object, String, Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(BoxesRunTime.boxToLong(x$0.time()), x$0.executorId(), BoxesRunTime.boxToInteger(x$0.taskFailures()), BoxesRunTime.boxToInteger(x$0.stageId()), BoxesRunTime.boxToInteger(x$0.stageAttemptId()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerExecutorBlacklistedForStage$() {
      MODULE$ = this;
   }
}
