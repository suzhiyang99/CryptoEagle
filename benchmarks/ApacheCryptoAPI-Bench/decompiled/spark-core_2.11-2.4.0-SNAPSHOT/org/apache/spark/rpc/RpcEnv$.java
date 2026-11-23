package org.apache.spark.rpc;

import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.rpc.netty.NettyRpcEnvFactory;

public final class RpcEnv$ {
   public static final RpcEnv$ MODULE$;

   static {
      new RpcEnv$();
   }

   public RpcEnv create(String name, String host, int port, SparkConf conf, SecurityManager securityManager, boolean clientMode) {
      return this.create(name, host, host, port, conf, securityManager, 0, clientMode);
   }

   public RpcEnv create(String name, String bindAddress, String advertiseAddress, int port, SparkConf conf, SecurityManager securityManager, int numUsableCores, boolean clientMode) {
      RpcEnvConfig config = new RpcEnvConfig(conf, name, bindAddress, advertiseAddress, port, securityManager, numUsableCores, clientMode);
      return (new NettyRpcEnvFactory()).create(config);
   }

   public boolean create$default$6() {
      return false;
   }

   private RpcEnv$() {
      MODULE$ = this;
   }
}
