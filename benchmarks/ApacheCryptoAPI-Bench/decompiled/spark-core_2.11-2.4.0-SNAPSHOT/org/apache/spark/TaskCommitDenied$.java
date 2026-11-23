package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class TaskCommitDenied$ extends AbstractFunction3<Object, Object, Object, TaskCommitDenied> implements Serializable {
   public static final TaskCommitDenied$ MODULE$;

   static {
      new TaskCommitDenied$();
   }

   public final String toString() {
      return "TaskCommitDenied";
   }

   public TaskCommitDenied apply(int jobID, int partitionID, int attemptNumber) {
      return new TaskCommitDenied(jobID, partitionID, attemptNumber);
   }

   public Option<Tuple3<Object, Object, Object>> unapply(TaskCommitDenied x$0) {
      return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.jobID()), BoxesRunTime.boxToInteger(x$0.partitionID()), BoxesRunTime.boxToInteger(x$0.attemptNumber()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskCommitDenied$() {
      MODULE$ = this;
   }
}
