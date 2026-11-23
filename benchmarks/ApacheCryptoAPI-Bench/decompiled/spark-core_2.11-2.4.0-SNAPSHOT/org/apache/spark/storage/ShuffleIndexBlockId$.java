package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class ShuffleIndexBlockId$ extends AbstractFunction3<Object, Object, Object, ShuffleIndexBlockId> implements Serializable {
   public static final ShuffleIndexBlockId$ MODULE$;

   static {
      new ShuffleIndexBlockId$();
   }

   public final String toString() {
      return "ShuffleIndexBlockId";
   }

   public ShuffleIndexBlockId apply(int shuffleId, int mapId, int reduceId) {
      return new ShuffleIndexBlockId(shuffleId, mapId, reduceId);
   }

   public Option<Tuple3<Object, Object, Object>> unapply(ShuffleIndexBlockId x$0) {
      return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.shuffleId()), BoxesRunTime.boxToInteger(x$0.mapId()), BoxesRunTime.boxToInteger(x$0.reduceId()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ShuffleIndexBlockId$() {
      MODULE$ = this;
   }
}
