package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class GettingResultEvent$ extends AbstractFunction1<TaskInfo, GettingResultEvent> implements Serializable {
   public static final GettingResultEvent$ MODULE$;

   static {
      new GettingResultEvent$();
   }

   public final String toString() {
      return "GettingResultEvent";
   }

   public GettingResultEvent apply(TaskInfo taskInfo) {
      return new GettingResultEvent(taskInfo);
   }

   public Option<TaskInfo> unapply(GettingResultEvent x$0) {
      return (Option<TaskInfo>)(x$0 == null ? None$.MODULE$ : new Some(x$0.taskInfo()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private GettingResultEvent$() {
      MODULE$ = this;
   }
}
