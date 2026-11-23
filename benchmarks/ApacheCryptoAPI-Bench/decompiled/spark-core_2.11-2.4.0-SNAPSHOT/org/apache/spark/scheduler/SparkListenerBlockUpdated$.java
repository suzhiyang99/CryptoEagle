package org.apache.spark.scheduler;

import org.apache.spark.storage.BlockUpdatedInfo;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class SparkListenerBlockUpdated$ extends AbstractFunction1<BlockUpdatedInfo, SparkListenerBlockUpdated> implements Serializable {
   public static final SparkListenerBlockUpdated$ MODULE$;

   static {
      new SparkListenerBlockUpdated$();
   }

   public final String toString() {
      return "SparkListenerBlockUpdated";
   }

   public SparkListenerBlockUpdated apply(BlockUpdatedInfo blockUpdatedInfo) {
      return new SparkListenerBlockUpdated(blockUpdatedInfo);
   }

   public Option<BlockUpdatedInfo> unapply(SparkListenerBlockUpdated x$0) {
      return (Option<BlockUpdatedInfo>)(x$0 == null ? None$.MODULE$ : new Some(x$0.blockUpdatedInfo()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerBlockUpdated$() {
      MODULE$ = this;
   }
}
