package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class BlockException$ extends AbstractFunction2<BlockId, String, BlockException> implements Serializable {
   public static final BlockException$ MODULE$;

   static {
      new BlockException$();
   }

   public final String toString() {
      return "BlockException";
   }

   public BlockException apply(BlockId blockId, String message) {
      return new BlockException(blockId, message);
   }

   public Option<Tuple2<BlockId, String>> unapply(BlockException x$0) {
      return (Option<Tuple2<BlockId, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.blockId(), x$0.message())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BlockException$() {
      MODULE$ = this;
   }
}
