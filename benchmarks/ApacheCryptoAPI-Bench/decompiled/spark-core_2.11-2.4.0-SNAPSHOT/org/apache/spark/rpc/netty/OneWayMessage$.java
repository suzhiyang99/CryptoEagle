package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class OneWayMessage$ extends AbstractFunction2<RpcAddress, Object, OneWayMessage> implements Serializable {
   public static final OneWayMessage$ MODULE$;

   static {
      new OneWayMessage$();
   }

   public final String toString() {
      return "OneWayMessage";
   }

   public OneWayMessage apply(RpcAddress senderAddress, Object content) {
      return new OneWayMessage(senderAddress, content);
   }

   public Option<Tuple2<RpcAddress, Object>> unapply(OneWayMessage x$0) {
      return (Option<Tuple2<RpcAddress, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.senderAddress(), x$0.content())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private OneWayMessage$() {
      MODULE$ = this;
   }
}
