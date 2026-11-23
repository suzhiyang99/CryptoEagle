package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class SparkListenerTaskGettingResult$ extends AbstractFunction1<TaskInfo, SparkListenerTaskGettingResult> implements Serializable {
   public static final SparkListenerTaskGettingResult$ MODULE$;

   static {
      new SparkListenerTaskGettingResult$();
   }

   public final String toString() {
      return "SparkListenerTaskGettingResult";
   }

   public SparkListenerTaskGettingResult apply(TaskInfo taskInfo) {
      return new SparkListenerTaskGettingResult(taskInfo);
   }

   public Option<TaskInfo> unapply(SparkListenerTaskGettingResult x$0) {
      return (Option<TaskInfo>)(x$0 == null ? None$.MODULE$ : new Some(x$0.taskInfo()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerTaskGettingResult$() {
      MODULE$ = this;
   }
}
