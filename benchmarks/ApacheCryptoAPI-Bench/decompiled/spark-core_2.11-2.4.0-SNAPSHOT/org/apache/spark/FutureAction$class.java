package org.apache.spark;

import org.apache.spark.util.ThreadUtils$;
import scala.Function1;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration$;

public abstract class FutureAction$class {
   public static Future transform(FutureAction $this, Function1 f, ExecutionContext executor) {
      throw new UnsupportedOperationException();
   }

   public static Future transformWith(FutureAction $this, Function1 f, ExecutionContext executor) {
      throw new UnsupportedOperationException();
   }

   public static Object get(FutureAction $this) throws SparkException {
      return ThreadUtils$.MODULE$.awaitResult($this, Duration$.MODULE$.Inf());
   }

   public static void $init$(FutureAction $this) {
   }
}
