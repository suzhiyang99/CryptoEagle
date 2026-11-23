package org.apache.spark.rpc.netty;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class RpcFailure$ extends AbstractFunction1<Throwable, RpcFailure> implements Serializable {
   public static final RpcFailure$ MODULE$;

   static {
      new RpcFailure$();
   }

   public final String toString() {
      return "RpcFailure";
   }

   public RpcFailure apply(Throwable e) {
      return new RpcFailure(e);
   }

   public Option<Throwable> unapply(RpcFailure x$0) {
      return (Option<Throwable>)(x$0 == null ? None$.MODULE$ : new Some(x$0.e()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RpcFailure$() {
      MODULE$ = this;
   }
}
