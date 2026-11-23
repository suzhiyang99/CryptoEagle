package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class SparkListenerNodeBlacklistedForStage$ extends AbstractFunction5<Object, String, Object, Object, Object, SparkListenerNodeBlacklistedForStage> implements Serializable {
   public static final SparkListenerNodeBlacklistedForStage$ MODULE$;

   static {
      new SparkListenerNodeBlacklistedForStage$();
   }

   public final String toString() {
      return "SparkListenerNodeBlacklistedForStage";
   }

   public SparkListenerNodeBlacklistedForStage apply(long time, String hostId, int executorFailures, int stageId, int stageAttemptId) {
      return new SparkListenerNodeBlacklistedForStage(time, hostId, executorFailures, stageId, stageAttemptId);
   }

   public Option<Tuple5<Object, String, Object, Object, Object>> unapply(SparkListenerNodeBlacklistedForStage x$0) {
      return (Option<Tuple5<Object, String, Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(BoxesRunTime.boxToLong(x$0.time()), x$0.hostId(), BoxesRunTime.boxToInteger(x$0.executorFailures()), BoxesRunTime.boxToInteger(x$0.stageId()), BoxesRunTime.boxToInteger(x$0.stageAttemptId()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerNodeBlacklistedForStage$() {
      MODULE$ = this;
   }
}
