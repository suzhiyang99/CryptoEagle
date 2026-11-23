package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class ExecutorAdded$ extends AbstractFunction2<String, String, ExecutorAdded> implements Serializable {
   public static final ExecutorAdded$ MODULE$;

   static {
      new ExecutorAdded$();
   }

   public final String toString() {
      return "ExecutorAdded";
   }

   public ExecutorAdded apply(String execId, String host) {
      return new ExecutorAdded(execId, host);
   }

   public Option<Tuple2<String, String>> unapply(ExecutorAdded x$0) {
      return (Option<Tuple2<String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.execId(), x$0.host())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorAdded$() {
      MODULE$ = this;
   }
}
