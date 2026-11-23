package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.runtime.AbstractFunction2;

public final class RDDBlockId$ extends AbstractFunction2<Object, Object, RDDBlockId> implements Serializable {
   public static final RDDBlockId$ MODULE$;

   static {
      new RDDBlockId$();
   }

   public final String toString() {
      return "RDDBlockId";
   }

   public RDDBlockId apply(int rddId, int splitIndex) {
      return new RDDBlockId(rddId, splitIndex);
   }

   public Option<Tuple2<Object, Object>> unapply(RDDBlockId x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcII$sp(x$0.rddId(), x$0.splitIndex())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RDDBlockId$() {
      MODULE$ = this;
   }
}
