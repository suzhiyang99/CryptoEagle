package org.apache.spark.rpc;

import org.apache.spark.SparkException;
import scala.Function1;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.runtime.BoxedUnit;

public abstract class RpcEndpoint$class {
   public static final RpcEndpointRef self(RpcEndpoint $this) {
      Predef$.MODULE$.require($this.rpcEnv() != null, new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "rpcEnv has not been initialized";
         }
      });
      return $this.rpcEnv().endpointRef($this);
   }

   public static PartialFunction receive(RpcEndpoint $this) {
      return new Serializable($this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RpcEndpoint $outer;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            throw new SparkException(Predef.any2stringadd$.MODULE$.$plus$extension(Predef$.MODULE$.any2stringadd(this.$outer.self()), " does not implement 'receive'"));
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3 = true;
            return var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }

   public static PartialFunction receiveAndReply(RpcEndpoint $this, RpcCallContext context) {
      return new Serializable($this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RpcEndpoint $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
            this.context$1.sendFailure(new SparkException(Predef.any2stringadd$.MODULE$.$plus$extension(Predef$.MODULE$.any2stringadd(this.$outer.self()), " won't reply anything")));
            BoxedUnit var4 = BoxedUnit.UNIT;
            return (B1)var4;
         }

         public final boolean isDefinedAt(Object x2) {
            boolean var3 = true;
            return var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.context$1 = context$1;
            }
         }
      };
   }

   public static void onError(RpcEndpoint $this, Throwable cause) {
      throw cause;
   }

   public static void onConnected(RpcEndpoint $this, RpcAddress remoteAddress) {
   }

   public static void onDisconnected(RpcEndpoint $this, RpcAddress remoteAddress) {
   }

   public static void onNetworkError(RpcEndpoint $this, Throwable cause, RpcAddress remoteAddress) {
   }

   public static void onStart(RpcEndpoint $this) {
   }

   public static void onStop(RpcEndpoint $this) {
   }

   public static final void stop(RpcEndpoint $this) {
      RpcEndpointRef _self = $this.self();
      if (_self != null) {
         $this.rpcEnv().stop(_self);
      }

   }

   public static void $init$(RpcEndpoint $this) {
   }
}
