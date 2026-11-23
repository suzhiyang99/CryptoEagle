package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;

public final class WorkerRemoved$ extends AbstractFunction3<String, String, String, WorkerRemoved> implements Serializable {
   public static final WorkerRemoved$ MODULE$;

   static {
      new WorkerRemoved$();
   }

   public final String toString() {
      return "WorkerRemoved";
   }

   public WorkerRemoved apply(String workerId, String host, String message) {
      return new WorkerRemoved(workerId, host, message);
   }

   public Option<Tuple3<String, String, String>> unapply(WorkerRemoved x$0) {
      return (Option<Tuple3<String, String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.workerId(), x$0.host(), x$0.message())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private WorkerRemoved$() {
      MODULE$ = this;
   }
}
