package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class ExecutorLost$ extends AbstractFunction2<String, ExecutorLossReason, ExecutorLost> implements Serializable {
   public static final ExecutorLost$ MODULE$;

   static {
      new ExecutorLost$();
   }

   public final String toString() {
      return "ExecutorLost";
   }

   public ExecutorLost apply(String execId, ExecutorLossReason reason) {
      return new ExecutorLost(execId, reason);
   }

   public Option<Tuple2<String, ExecutorLossReason>> unapply(ExecutorLost x$0) {
      return (Option<Tuple2<String, ExecutorLossReason>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.execId(), x$0.reason())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorLost$() {
      MODULE$ = this;
   }
}
