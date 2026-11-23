package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class BeginEvent$ extends AbstractFunction2<Task<?>, TaskInfo, BeginEvent> implements Serializable {
   public static final BeginEvent$ MODULE$;

   static {
      new BeginEvent$();
   }

   public final String toString() {
      return "BeginEvent";
   }

   public BeginEvent apply(Task<?> task, TaskInfo taskInfo) {
      return new BeginEvent(task, taskInfo);
   }

   public Option<Tuple2<Task<Object>, TaskInfo>> unapply(BeginEvent x$0) {
      return (Option<Tuple2<Task<Object>, TaskInfo>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.task(), x$0.taskInfo())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BeginEvent$() {
      MODULE$ = this;
   }
}
