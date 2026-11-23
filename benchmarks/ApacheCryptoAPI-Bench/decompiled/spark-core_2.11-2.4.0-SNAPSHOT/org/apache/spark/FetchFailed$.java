package org.apache.spark;

import org.apache.spark.storage.BlockManagerId;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class FetchFailed$ extends AbstractFunction5<BlockManagerId, Object, Object, Object, String, FetchFailed> implements Serializable {
   public static final FetchFailed$ MODULE$;

   static {
      new FetchFailed$();
   }

   public final String toString() {
      return "FetchFailed";
   }

   public FetchFailed apply(BlockManagerId bmAddress, int shuffleId, int mapId, int reduceId, String message) {
      return new FetchFailed(bmAddress, shuffleId, mapId, reduceId, message);
   }

   public Option<Tuple5<BlockManagerId, Object, Object, Object, String>> unapply(FetchFailed x$0) {
      return (Option<Tuple5<BlockManagerId, Object, Object, Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.bmAddress(), BoxesRunTime.boxToInteger(x$0.shuffleId()), BoxesRunTime.boxToInteger(x$0.mapId()), BoxesRunTime.boxToInteger(x$0.reduceId()), x$0.message())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private FetchFailed$() {
      MODULE$ = this;
   }
}
