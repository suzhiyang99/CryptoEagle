package org.apache.spark;

import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.AccumulatorV2;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Seq;
import scala.runtime.AbstractFunction3;

public final class Heartbeat$ extends AbstractFunction3<String, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[], BlockManagerId, Heartbeat> implements Serializable {
   public static final Heartbeat$ MODULE$;

   static {
      new Heartbeat$();
   }

   public final String toString() {
      return "Heartbeat";
   }

   public Heartbeat apply(String executorId, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] accumUpdates, BlockManagerId blockManagerId) {
      return new Heartbeat(executorId, accumUpdates, blockManagerId);
   }

   public Option<Tuple3<String, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[], BlockManagerId>> unapply(Heartbeat x$0) {
      return (Option<Tuple3<String, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[], BlockManagerId>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.executorId(), x$0.accumUpdates(), x$0.blockManagerId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Heartbeat$() {
      MODULE$ = this;
   }
}
