package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class ShuffleBlockId$ extends AbstractFunction3<Object, Object, Object, ShuffleBlockId> implements Serializable {
   public static final ShuffleBlockId$ MODULE$;

   static {
      new ShuffleBlockId$();
   }

   public final String toString() {
      return "ShuffleBlockId";
   }

   public ShuffleBlockId apply(int shuffleId, int mapId, int reduceId) {
      return new ShuffleBlockId(shuffleId, mapId, reduceId);
   }

   public Option<Tuple3<Object, Object, Object>> unapply(ShuffleBlockId x$0) {
      return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.shuffleId()), BoxesRunTime.boxToInteger(x$0.mapId()), BoxesRunTime.boxToInteger(x$0.reduceId()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ShuffleBlockId$() {
      MODULE$ = this;
   }
}
