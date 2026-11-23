package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;

public final class TaskSetFailed$ extends AbstractFunction3<TaskSet, String, Option<Throwable>, TaskSetFailed> implements Serializable {
   public static final TaskSetFailed$ MODULE$;

   static {
      new TaskSetFailed$();
   }

   public final String toString() {
      return "TaskSetFailed";
   }

   public TaskSetFailed apply(TaskSet taskSet, String reason, Option<Throwable> exception) {
      return new TaskSetFailed(taskSet, reason, exception);
   }

   public Option<Tuple3<TaskSet, String, Option<Throwable>>> unapply(TaskSetFailed x$0) {
      return (Option<Tuple3<TaskSet, String, Option<Throwable>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.taskSet(), x$0.reason(), x$0.exception())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskSetFailed$() {
      MODULE$ = this;
   }
}
