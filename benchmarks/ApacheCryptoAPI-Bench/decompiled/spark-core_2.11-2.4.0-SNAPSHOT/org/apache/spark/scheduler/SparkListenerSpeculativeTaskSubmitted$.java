package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class SparkListenerSpeculativeTaskSubmitted$ extends AbstractFunction1<Object, SparkListenerSpeculativeTaskSubmitted> implements Serializable {
   public static final SparkListenerSpeculativeTaskSubmitted$ MODULE$;

   static {
      new SparkListenerSpeculativeTaskSubmitted$();
   }

   public final String toString() {
      return "SparkListenerSpeculativeTaskSubmitted";
   }

   public SparkListenerSpeculativeTaskSubmitted apply(int stageId) {
      return new SparkListenerSpeculativeTaskSubmitted(stageId);
   }

   public Option<Object> unapply(SparkListenerSpeculativeTaskSubmitted x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.stageId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerSpeculativeTaskSubmitted$() {
      MODULE$ = this;
   }
}
