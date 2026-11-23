package org.apache.spark.rpc;

import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple8;
import scala.runtime.AbstractFunction8;
import scala.runtime.BoxesRunTime;

public final class RpcEnvConfig$ extends AbstractFunction8<SparkConf, String, String, String, Object, SecurityManager, Object, Object, RpcEnvConfig> implements Serializable {
   public static final RpcEnvConfig$ MODULE$;

   static {
      new RpcEnvConfig$();
   }

   public final String toString() {
      return "RpcEnvConfig";
   }

   public RpcEnvConfig apply(SparkConf conf, String name, String bindAddress, String advertiseAddress, int port, SecurityManager securityManager, int numUsableCores, boolean clientMode) {
      return new RpcEnvConfig(conf, name, bindAddress, advertiseAddress, port, securityManager, numUsableCores, clientMode);
   }

   public Option<Tuple8<SparkConf, String, String, String, Object, SecurityManager, Object, Object>> unapply(RpcEnvConfig x$0) {
      return (Option<Tuple8<SparkConf, String, String, String, Object, SecurityManager, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple8(x$0.conf(), x$0.name(), x$0.bindAddress(), x$0.advertiseAddress(), BoxesRunTime.boxToInteger(x$0.port()), x$0.securityManager(), BoxesRunTime.boxToInteger(x$0.numUsableCores()), BoxesRunTime.boxToBoolean(x$0.clientMode()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RpcEnvConfig$() {
      MODULE$ = this;
   }
}
