package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class SparkListenerStageCompleted$ extends AbstractFunction1<StageInfo, SparkListenerStageCompleted> implements Serializable {
   public static final SparkListenerStageCompleted$ MODULE$;

   static {
      new SparkListenerStageCompleted$();
   }

   public final String toString() {
      return "SparkListenerStageCompleted";
   }

   public SparkListenerStageCompleted apply(StageInfo stageInfo) {
      return new SparkListenerStageCompleted(stageInfo);
   }

   public Option<StageInfo> unapply(SparkListenerStageCompleted x$0) {
      return (Option<StageInfo>)(x$0 == null ? None$.MODULE$ : new Some(x$0.stageInfo()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerStageCompleted$() {
      MODULE$ = this;
   }
}
