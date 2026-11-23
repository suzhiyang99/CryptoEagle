package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class SparkListenerTaskStart$ extends AbstractFunction3<Object, Object, TaskInfo, SparkListenerTaskStart> implements Serializable {
   public static final SparkListenerTaskStart$ MODULE$;

   static {
      new SparkListenerTaskStart$();
   }

   public final String toString() {
      return "SparkListenerTaskStart";
   }

   public SparkListenerTaskStart apply(int stageId, int stageAttemptId, TaskInfo taskInfo) {
      return new SparkListenerTaskStart(stageId, stageAttemptId, taskInfo);
   }

   public Option<Tuple3<Object, Object, TaskInfo>> unapply(SparkListenerTaskStart x$0) {
      return (Option<Tuple3<Object, Object, TaskInfo>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.stageId()), BoxesRunTime.boxToInteger(x$0.stageAttemptId()), x$0.taskInfo())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerTaskStart$() {
      MODULE$ = this;
   }
}
