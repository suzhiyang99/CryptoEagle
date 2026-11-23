package org.apache.spark.rpc.netty;

import java.nio.ByteBuffer;
import org.apache.spark.network.client.TransportClient;
import scala.Function1;
import scala.Function2;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxedUnit;

public final class RpcOutboxMessage$ extends AbstractFunction3<ByteBuffer, Function1<Throwable, BoxedUnit>, Function2<TransportClient, ByteBuffer, BoxedUnit>, RpcOutboxMessage> implements Serializable {
   public static final RpcOutboxMessage$ MODULE$;

   static {
      new RpcOutboxMessage$();
   }

   public final String toString() {
      return "RpcOutboxMessage";
   }

   public RpcOutboxMessage apply(ByteBuffer content, Function1<Throwable, BoxedUnit> _onFailure, Function2<TransportClient, ByteBuffer, BoxedUnit> _onSuccess) {
      return new RpcOutboxMessage(content, _onFailure, _onSuccess);
   }

   public Option<Tuple3<ByteBuffer, Function1<Throwable, BoxedUnit>, Function2<TransportClient, ByteBuffer, BoxedUnit>>> unapply(RpcOutboxMessage x$0) {
      return (Option<Tuple3<ByteBuffer, Function1<Throwable, BoxedUnit>, Function2<TransportClient, ByteBuffer, BoxedUnit>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.content(), x$0._onFailure(), x$0._onSuccess())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RpcOutboxMessage$() {
      MODULE$ = this;
   }
}
