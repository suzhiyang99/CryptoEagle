package org.apache.spark.ui.scope;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple4;
import scala.runtime.AbstractFunction4;
import scala.runtime.BoxesRunTime;

public final class RDDOperationNode$ extends AbstractFunction4<Object, String, Object, String, RDDOperationNode> implements Serializable {
   public static final RDDOperationNode$ MODULE$;

   static {
      new RDDOperationNode$();
   }

   public final String toString() {
      return "RDDOperationNode";
   }

   public RDDOperationNode apply(int id, String name, boolean cached, String callsite) {
      return new RDDOperationNode(id, name, cached, callsite);
   }

   public Option<Tuple4<Object, String, Object, String>> unapply(RDDOperationNode x$0) {
      return (Option<Tuple4<Object, String, Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple4(BoxesRunTime.boxToInteger(x$0.id()), x$0.name(), BoxesRunTime.boxToBoolean(x$0.cached()), x$0.callsite())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RDDOperationNode$() {
      MODULE$ = this;
   }
}
