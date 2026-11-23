package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class RemoteProcessConnected$ extends AbstractFunction1<RpcAddress, RemoteProcessConnected> implements Serializable {
   public static final RemoteProcessConnected$ MODULE$;

   static {
      new RemoteProcessConnected$();
   }

   public final String toString() {
      return "RemoteProcessConnected";
   }

   public RemoteProcessConnected apply(RpcAddress remoteAddress) {
      return new RemoteProcessConnected(remoteAddress);
   }

   public Option<RpcAddress> unapply(RemoteProcessConnected x$0) {
      return (Option<RpcAddress>)(x$0 == null ? None$.MODULE$ : new Some(x$0.remoteAddress()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RemoteProcessConnected$() {
      MODULE$ = this;
   }
}
