package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class TaskResultBlockId$ extends AbstractFunction1<Object, TaskResultBlockId> implements Serializable {
   public static final TaskResultBlockId$ MODULE$;

   static {
      new TaskResultBlockId$();
   }

   public final String toString() {
      return "TaskResultBlockId";
   }

   public TaskResultBlockId apply(long taskId) {
      return new TaskResultBlockId(taskId);
   }

   public Option<Object> unapply(TaskResultBlockId x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToLong(x$0.taskId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskResultBlockId$() {
      MODULE$ = this;
   }
}
