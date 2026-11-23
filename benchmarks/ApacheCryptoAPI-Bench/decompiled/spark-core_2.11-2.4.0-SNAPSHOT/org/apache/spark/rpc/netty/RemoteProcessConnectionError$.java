package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;

public final class RemoteProcessConnectionError$ extends AbstractFunction2<Throwable, RpcAddress, RemoteProcessConnectionError> implements Serializable {
   public static final RemoteProcessConnectionError$ MODULE$;

   static {
      new RemoteProcessConnectionError$();
   }

   public final String toString() {
      return "RemoteProcessConnectionError";
   }

   public RemoteProcessConnectionError apply(Throwable cause, RpcAddress remoteAddress) {
      return new RemoteProcessConnectionError(cause, remoteAddress);
   }

   public Option<Tuple2<Throwable, RpcAddress>> unapply(RemoteProcessConnectionError x$0) {
      return (Option<Tuple2<Throwable, RpcAddress>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.cause(), x$0.remoteAddress())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RemoteProcessConnectionError$() {
      MODULE$ = this;
   }
}
