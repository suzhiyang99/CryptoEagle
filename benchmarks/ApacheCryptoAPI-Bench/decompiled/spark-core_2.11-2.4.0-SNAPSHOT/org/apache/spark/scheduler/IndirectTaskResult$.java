package org.apache.spark.scheduler;

import org.apache.spark.storage.BlockId;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.BoxesRunTime;

public final class IndirectTaskResult$ implements Serializable {
   public static final IndirectTaskResult$ MODULE$;

   static {
      new IndirectTaskResult$();
   }

   public final String toString() {
      return "IndirectTaskResult";
   }

   public <T> IndirectTaskResult<T> apply(BlockId blockId, int size) {
      return new IndirectTaskResult<T>(blockId, size);
   }

   public <T> Option<Tuple2<BlockId, Object>> unapply(IndirectTaskResult<T> x$0) {
      return (Option<Tuple2<BlockId, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.blockId(), BoxesRunTime.boxToInteger(x$0.size()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private IndirectTaskResult$() {
      MODULE$ = this;
   }
}
