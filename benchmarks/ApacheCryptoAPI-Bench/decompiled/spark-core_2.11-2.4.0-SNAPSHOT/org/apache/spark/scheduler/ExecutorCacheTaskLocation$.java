package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class ExecutorCacheTaskLocation$ extends AbstractFunction2<String, String, ExecutorCacheTaskLocation> implements Serializable {
   public static final ExecutorCacheTaskLocation$ MODULE$;

   static {
      new ExecutorCacheTaskLocation$();
   }

   public final String toString() {
      return "ExecutorCacheTaskLocation";
   }

   public ExecutorCacheTaskLocation apply(String host, String executorId) {
      return new ExecutorCacheTaskLocation(host, executorId);
   }

   public Option<Tuple2<String, String>> unapply(ExecutorCacheTaskLocation x$0) {
      return (Option<Tuple2<String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.host(), x$0.executorId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorCacheTaskLocation$() {
      MODULE$ = this;
   }
}
