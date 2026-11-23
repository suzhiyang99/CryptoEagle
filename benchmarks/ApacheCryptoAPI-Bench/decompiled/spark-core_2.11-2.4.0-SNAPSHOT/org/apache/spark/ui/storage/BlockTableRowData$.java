package org.apache.spark.ui.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class BlockTableRowData$ extends AbstractFunction5<String, String, Object, Object, String, BlockTableRowData> implements Serializable {
   public static final BlockTableRowData$ MODULE$;

   static {
      new BlockTableRowData$();
   }

   public final String toString() {
      return "BlockTableRowData";
   }

   public BlockTableRowData apply(String blockName, String storageLevel, long memoryUsed, long diskUsed, String executors) {
      return new BlockTableRowData(blockName, storageLevel, memoryUsed, diskUsed, executors);
   }

   public Option<Tuple5<String, String, Object, Object, String>> unapply(BlockTableRowData x$0) {
      return (Option<Tuple5<String, String, Object, Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.blockName(), x$0.storageLevel(), BoxesRunTime.boxToLong(x$0.memoryUsed()), BoxesRunTime.boxToLong(x$0.diskUsed()), x$0.executors())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BlockTableRowData$() {
      MODULE$ = this;
   }
}
