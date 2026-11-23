package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class BroadcastBlockId$ extends AbstractFunction2<Object, String, BroadcastBlockId> implements Serializable {
   public static final BroadcastBlockId$ MODULE$;

   static {
      new BroadcastBlockId$();
   }

   public final String toString() {
      return "BroadcastBlockId";
   }

   public BroadcastBlockId apply(long broadcastId, String field) {
      return new BroadcastBlockId(broadcastId, field);
   }

   public Option<Tuple2<Object, String>> unapply(BroadcastBlockId x$0) {
      return (Option<Tuple2<Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToLong(x$0.broadcastId()), x$0.field())));
   }

   public String apply$default$2() {
      return "";
   }

   public String $lessinit$greater$default$2() {
      return "";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BroadcastBlockId$() {
      MODULE$ = this;
   }
}
