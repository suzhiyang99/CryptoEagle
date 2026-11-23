package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.BoxesRunTime;

public final class BlockUpdatedInfo$ implements Serializable {
   public static final BlockUpdatedInfo$ MODULE$;

   static {
      new BlockUpdatedInfo$();
   }

   public BlockUpdatedInfo apply(BlockManagerMessages.UpdateBlockInfo updateBlockInfo) {
      return new BlockUpdatedInfo(updateBlockInfo.blockManagerId(), updateBlockInfo.blockId(), updateBlockInfo.storageLevel(), updateBlockInfo.memSize(), updateBlockInfo.diskSize());
   }

   public BlockUpdatedInfo apply(BlockManagerId blockManagerId, BlockId blockId, StorageLevel storageLevel, long memSize, long diskSize) {
      return new BlockUpdatedInfo(blockManagerId, blockId, storageLevel, memSize, diskSize);
   }

   public Option<Tuple5<BlockManagerId, BlockId, StorageLevel, Object, Object>> unapply(BlockUpdatedInfo x$0) {
      return (Option<Tuple5<BlockManagerId, BlockId, StorageLevel, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.blockManagerId(), x$0.blockId(), x$0.storageLevel(), BoxesRunTime.boxToLong(x$0.memSize()), BoxesRunTime.boxToLong(x$0.diskSize()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BlockUpdatedInfo$() {
      MODULE$ = this;
   }
}
