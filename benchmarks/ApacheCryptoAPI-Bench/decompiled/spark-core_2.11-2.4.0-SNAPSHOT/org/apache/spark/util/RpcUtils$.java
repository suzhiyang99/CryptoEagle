package org.apache.spark.util;

import org.apache.spark.SparkConf;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcTimeout;
import org.apache.spark.rpc.RpcTimeout$;
import scala.Predef$;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.runtime.BoxesRunTime;

public final class RpcUtils$ {
   public static final RpcUtils$ MODULE$;
   private final int MAX_MESSAGE_SIZE_IN_MB;

   static {
      new RpcUtils$();
   }

   public RpcEndpointRef makeDriverRef(String name, SparkConf conf, RpcEnv rpcEnv) {
      String driverHost = conf.get("spark.driver.host", "localhost");
      int driverPort = conf.getInt("spark.driver.port", 7077);
      Utils$.MODULE$.checkHost(driverHost);
      return rpcEnv.setupEndpointRef(new RpcAddress(driverHost, driverPort), name);
   }

   public int numRetries(SparkConf conf) {
      return conf.getInt("spark.rpc.numRetries", 3);
   }

   public long retryWaitMs(SparkConf conf) {
      return conf.getTimeAsMs("spark.rpc.retry.wait", "3s");
   }

   public RpcTimeout askRpcTimeout(SparkConf conf) {
      return RpcTimeout$.MODULE$.apply(conf, (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.rpc.askTimeout", "spark.network.timeout"}))), "120s");
   }

   public RpcTimeout lookupRpcTimeout(SparkConf conf) {
      return RpcTimeout$.MODULE$.apply(conf, (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.rpc.lookupTimeout", "spark.network.timeout"}))), "120s");
   }

   private int MAX_MESSAGE_SIZE_IN_MB() {
      return this.MAX_MESSAGE_SIZE_IN_MB;
   }

   public int maxMessageSizeBytes(SparkConf conf) {
      int maxSizeInMB = conf.getInt("spark.rpc.message.maxSize", 128);
      if (maxSizeInMB > this.MAX_MESSAGE_SIZE_IN_MB()) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.rpc.message.maxSize should not be greater than ", " MB"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.MAX_MESSAGE_SIZE_IN_MB())})));
      } else {
         return maxSizeInMB * 1024 * 1024;
      }
   }

   private RpcUtils$() {
      MODULE$ = this;
      this.MAX_MESSAGE_SIZE_IN_MB = 2047;
   }
}
