package org.apache.spark.rpc.netty;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.client.TransportClient;
import org.slf4j.Logger;
import scala.Function0;
import scala.runtime.TraitSetter;
import scala.util.DynamicVariable;

public final class NettyRpcEnv$ implements Logging {
   public static final NettyRpcEnv$ MODULE$;
   private final DynamicVariable<NettyRpcEnv> currentEnv;
   private final DynamicVariable<TransportClient> currentClient;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new NettyRpcEnv$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public DynamicVariable<NettyRpcEnv> currentEnv() {
      return this.currentEnv;
   }

   public DynamicVariable<TransportClient> currentClient() {
      return this.currentClient;
   }

   private NettyRpcEnv$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.currentEnv = new DynamicVariable<NettyRpcEnv>((Object)null);
      this.currentClient = new DynamicVariable<TransportClient>((Object)null);
   }
}
