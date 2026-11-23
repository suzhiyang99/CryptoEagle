package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class RemoteProcessDisconnected$ extends AbstractFunction1<RpcAddress, RemoteProcessDisconnected> implements Serializable {
   public static final RemoteProcessDisconnected$ MODULE$;

   static {
      new RemoteProcessDisconnected$();
   }

   public final String toString() {
      return "RemoteProcessDisconnected";
   }

   public RemoteProcessDisconnected apply(RpcAddress remoteAddress) {
      return new RemoteProcessDisconnected(remoteAddress);
   }

   public Option<RpcAddress> unapply(RemoteProcessDisconnected x$0) {
      return (Option<RpcAddress>)(x$0 == null ? None$.MODULE$ : new Some(x$0.remoteAddress()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RemoteProcessDisconnected$() {
      MODULE$ = this;
   }
}
