package org.apache.spark;

import org.apache.spark.rpc.RpcCallContext;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class GetMapOutputMessage$ extends AbstractFunction2<Object, RpcCallContext, GetMapOutputMessage> implements Serializable {
   public static final GetMapOutputMessage$ MODULE$;

   static {
      new GetMapOutputMessage$();
   }

   public final String toString() {
      return "GetMapOutputMessage";
   }

   public GetMapOutputMessage apply(int shuffleId, RpcCallContext context) {
      return new GetMapOutputMessage(shuffleId, context);
   }

   public Option<Tuple2<Object, RpcCallContext>> unapply(GetMapOutputMessage x$0) {
      return (Option<Tuple2<Object, RpcCallContext>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToInteger(x$0.shuffleId()), x$0.context())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private GetMapOutputMessage$() {
      MODULE$ = this;
   }
}
