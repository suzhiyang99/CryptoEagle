package org.apache.spark.ui.scope;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.runtime.AbstractFunction2;

public final class RDDOperationEdge$ extends AbstractFunction2<Object, Object, RDDOperationEdge> implements Serializable {
   public static final RDDOperationEdge$ MODULE$;

   static {
      new RDDOperationEdge$();
   }

   public final String toString() {
      return "RDDOperationEdge";
   }

   public RDDOperationEdge apply(int fromId, int toId) {
      return new RDDOperationEdge(fromId, toId);
   }

   public Option<Tuple2<Object, Object>> unapply(RDDOperationEdge x$0) {
      return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcII$sp(x$0.fromId(), x$0.toId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RDDOperationEdge$() {
      MODULE$ = this;
   }
}
