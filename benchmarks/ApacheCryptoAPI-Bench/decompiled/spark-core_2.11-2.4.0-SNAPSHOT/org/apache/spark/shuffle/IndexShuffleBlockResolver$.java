package org.apache.spark.shuffle;

import org.apache.spark.storage.BlockManager;

public final class IndexShuffleBlockResolver$ {
   public static final IndexShuffleBlockResolver$ MODULE$;
   private final int NOOP_REDUCE_ID;

   static {
      new IndexShuffleBlockResolver$();
   }

   public int NOOP_REDUCE_ID() {
      return this.NOOP_REDUCE_ID;
   }

   public BlockManager $lessinit$greater$default$2() {
      return null;
   }

   private IndexShuffleBlockResolver$() {
      MODULE$ = this;
      this.NOOP_REDUCE_ID = 0;
   }
}
