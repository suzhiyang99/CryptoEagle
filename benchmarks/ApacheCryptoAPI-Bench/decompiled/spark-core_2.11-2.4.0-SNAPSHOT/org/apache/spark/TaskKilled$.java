package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class TaskKilled$ extends AbstractFunction1<String, TaskKilled> implements Serializable {
   public static final TaskKilled$ MODULE$;

   static {
      new TaskKilled$();
   }

   public final String toString() {
      return "TaskKilled";
   }

   public TaskKilled apply(String reason) {
      return new TaskKilled(reason);
   }

   public Option<String> unapply(TaskKilled x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.reason()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskKilled$() {
      MODULE$ = this;
   }
}
