package org.apache.spark.deploy.master;

import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnv$;
import org.apache.spark.util.SparkUncaughtExceptionHandler;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Tuple3;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;

public final class Master$ implements Logging {
   public static final Master$ MODULE$;
   private final String SYSTEM_NAME;
   private final String ENDPOINT_NAME;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new Master$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public String SYSTEM_NAME() {
      return this.SYSTEM_NAME;
   }

   public String ENDPOINT_NAME() {
      return this.ENDPOINT_NAME;
   }

   public void main(String[] argStrings) {
      Thread.setDefaultUncaughtExceptionHandler(new SparkUncaughtExceptionHandler(false));
      Utils$.MODULE$.initDaemon(this.log());
      SparkConf conf = new SparkConf();
      MasterArguments args = new MasterArguments(argStrings, conf);
      Tuple3 var5 = this.startRpcEnvAndEndpoint(args.host(), args.port(), args.webUiPort(), conf);
      if (var5 != null) {
         RpcEnv rpcEnv = (RpcEnv)var5._1();
         rpcEnv.awaitTermination();
      } else {
         throw new MatchError(var5);
      }
   }

   public Tuple3<RpcEnv, Object, Option<Object>> startRpcEnvAndEndpoint(String host, int port, int webUiPort, SparkConf conf) {
      SecurityManager securityMgr = new SecurityManager(conf, SecurityManager$.MODULE$.$lessinit$greater$default$2());
      RpcEnv rpcEnv = RpcEnv$.MODULE$.create(this.SYSTEM_NAME(), host, port, conf, securityMgr, RpcEnv$.MODULE$.create$default$6());
      RpcEndpointRef masterEndpoint = rpcEnv.setupEndpoint(this.ENDPOINT_NAME(), new Master(rpcEnv, rpcEnv.address(), webUiPort, securityMgr, conf));
      MasterMessages.BoundPortsResponse portsResponse = (MasterMessages.BoundPortsResponse)masterEndpoint.askSync(MasterMessages.BoundPortsRequest$.MODULE$, ClassTag$.MODULE$.apply(MasterMessages.BoundPortsResponse.class));
      return new Tuple3<RpcEnv, Object, Option<Object>>(rpcEnv, BoxesRunTime.boxToInteger(portsResponse.webUIPort()), portsResponse.restPort());
   }

   private Master$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.SYSTEM_NAME = "sparkMaster";
      this.ENDPOINT_NAME = "Master";
   }
}
