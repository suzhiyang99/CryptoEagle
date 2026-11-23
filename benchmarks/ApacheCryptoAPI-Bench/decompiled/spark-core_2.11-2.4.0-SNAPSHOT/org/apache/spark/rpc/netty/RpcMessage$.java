package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;

public final class RpcMessage$ extends AbstractFunction3<RpcAddress, Object, NettyRpcCallContext, RpcMessage> implements Serializable {
   public static final RpcMessage$ MODULE$;

   static {
      new RpcMessage$();
   }

   public final String toString() {
      return "RpcMessage";
   }

   public RpcMessage apply(RpcAddress senderAddress, Object content, NettyRpcCallContext context) {
      return new RpcMessage(senderAddress, content, context);
   }

   public Option<Tuple3<RpcAddress, Object, NettyRpcCallContext>> unapply(RpcMessage x$0) {
      return (Option<Tuple3<RpcAddress, Object, NettyRpcCallContext>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.senderAddress(), x$0.content(), x$0.context())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RpcMessage$() {
      MODULE$ = this;
   }
}
