package org.apache.spark.scheduler;

import org.apache.spark.storage.BlockManagerId;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class SparkListenerBlockManagerRemoved$ extends AbstractFunction2<Object, BlockManagerId, SparkListenerBlockManagerRemoved> implements Serializable {
   public static final SparkListenerBlockManagerRemoved$ MODULE$;

   static {
      new SparkListenerBlockManagerRemoved$();
   }

   public final String toString() {
      return "SparkListenerBlockManagerRemoved";
   }

   public SparkListenerBlockManagerRemoved apply(long time, BlockManagerId blockManagerId) {
      return new SparkListenerBlockManagerRemoved(time, blockManagerId);
   }

   public Option<Tuple2<Object, BlockManagerId>> unapply(SparkListenerBlockManagerRemoved x$0) {
      return (Option<Tuple2<Object, BlockManagerId>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToLong(x$0.time()), x$0.blockManagerId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerBlockManagerRemoved$() {
      MODULE$ = this;
   }
}
