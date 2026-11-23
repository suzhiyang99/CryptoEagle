package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class WorkerOffer$ extends AbstractFunction3<String, String, Object, WorkerOffer> implements Serializable {
   public static final WorkerOffer$ MODULE$;

   static {
      new WorkerOffer$();
   }

   public final String toString() {
      return "WorkerOffer";
   }

   public WorkerOffer apply(String executorId, String host, int cores) {
      return new WorkerOffer(executorId, host, cores);
   }

   public Option<Tuple3<String, String, Object>> unapply(WorkerOffer x$0) {
      return (Option<Tuple3<String, String, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.executorId(), x$0.host(), BoxesRunTime.boxToInteger(x$0.cores()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private WorkerOffer$() {
      MODULE$ = this;
   }
}
