package org.apache.spark.scheduler;

import javax.annotation.Nullable;
import org.apache.spark.TaskEndReason;
import org.apache.spark.executor.TaskMetrics;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple6;
import scala.runtime.AbstractFunction6;
import scala.runtime.BoxesRunTime;

public final class SparkListenerTaskEnd$ extends AbstractFunction6<Object, Object, String, TaskEndReason, TaskInfo, TaskMetrics, SparkListenerTaskEnd> implements Serializable {
   public static final SparkListenerTaskEnd$ MODULE$;

   static {
      new SparkListenerTaskEnd$();
   }

   public final String toString() {
      return "SparkListenerTaskEnd";
   }

   public SparkListenerTaskEnd apply(int stageId, int stageAttemptId, String taskType, TaskEndReason reason, TaskInfo taskInfo, @Nullable TaskMetrics taskMetrics) {
      return new SparkListenerTaskEnd(stageId, stageAttemptId, taskType, reason, taskInfo, taskMetrics);
   }

   public Option<Tuple6<Object, Object, String, TaskEndReason, TaskInfo, TaskMetrics>> unapply(SparkListenerTaskEnd x$0) {
      return (Option<Tuple6<Object, Object, String, TaskEndReason, TaskInfo, TaskMetrics>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple6(BoxesRunTime.boxToInteger(x$0.stageId()), BoxesRunTime.boxToInteger(x$0.stageAttemptId()), x$0.taskType(), x$0.reason(), x$0.taskInfo(), x$0.taskMetrics())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerTaskEnd$() {
      MODULE$ = this;
   }
}
