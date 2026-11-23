package org.apache.spark.scheduler;

import org.apache.spark.storage.BlockManagerId;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class SparkListenerBlockManagerAdded$ extends AbstractFunction5<Object, BlockManagerId, Object, Option<Object>, Option<Object>, SparkListenerBlockManagerAdded> implements Serializable {
   public static final SparkListenerBlockManagerAdded$ MODULE$;

   static {
      new SparkListenerBlockManagerAdded$();
   }

   public final String toString() {
      return "SparkListenerBlockManagerAdded";
   }

   public SparkListenerBlockManagerAdded apply(long time, BlockManagerId blockManagerId, long maxMem, Option<Object> maxOnHeapMem, Option<Object> maxOffHeapMem) {
      return new SparkListenerBlockManagerAdded(time, blockManagerId, maxMem, maxOnHeapMem, maxOffHeapMem);
   }

   public Option<Tuple5<Object, BlockManagerId, Object, Option<Object>, Option<Object>>> unapply(SparkListenerBlockManagerAdded x$0) {
      return (Option<Tuple5<Object, BlockManagerId, Object, Option<Object>, Option<Object>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(BoxesRunTime.boxToLong(x$0.time()), x$0.blockManagerId(), BoxesRunTime.boxToLong(x$0.maxMem()), x$0.maxOnHeapMem(), x$0.maxOffHeapMem())));
   }

   public Option<Object> $lessinit$greater$default$4() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$5() {
      return None$.MODULE$;
   }

   public Option<Object> apply$default$4() {
      return None$.MODULE$;
   }

   public Option<Object> apply$default$5() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerBlockManagerAdded$() {
      MODULE$ = this;
   }
}
