package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class ShuffleDataBlockId$ extends AbstractFunction3<Object, Object, Object, ShuffleDataBlockId> implements Serializable {
   public static final ShuffleDataBlockId$ MODULE$;

   static {
      new ShuffleDataBlockId$();
   }

   public final String toString() {
      return "ShuffleDataBlockId";
   }

   public ShuffleDataBlockId apply(int shuffleId, int mapId, int reduceId) {
      return new ShuffleDataBlockId(shuffleId, mapId, reduceId);
   }

   public Option<Tuple3<Object, Object, Object>> unapply(ShuffleDataBlockId x$0) {
      return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.shuffleId()), BoxesRunTime.boxToInteger(x$0.mapId()), BoxesRunTime.boxToInteger(x$0.reduceId()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ShuffleDataBlockId$() {
      MODULE$ = this;
   }
}
