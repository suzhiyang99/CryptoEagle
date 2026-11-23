package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public final class BlockStatus$ implements Serializable {
   public static final BlockStatus$ MODULE$;

   static {
      new BlockStatus$();
   }

   public BlockStatus empty() {
      return new BlockStatus(StorageLevel$.MODULE$.NONE(), 0L, 0L);
   }

   public BlockStatus apply(StorageLevel storageLevel, long memSize, long diskSize) {
      return new BlockStatus(storageLevel, memSize, diskSize);
   }

   public Option<Tuple3<StorageLevel, Object, Object>> unapply(BlockStatus x$0) {
      return (Option<Tuple3<StorageLevel, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.storageLevel(), BoxesRunTime.boxToLong(x$0.memSize()), BoxesRunTime.boxToLong(x$0.diskSize()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BlockStatus$() {
      MODULE$ = this;
   }
}
