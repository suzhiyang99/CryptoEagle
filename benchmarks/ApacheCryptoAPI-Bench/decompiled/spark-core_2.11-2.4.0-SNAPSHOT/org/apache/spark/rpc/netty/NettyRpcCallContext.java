package org.apache.spark.rpc.netty;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u000153a!\u0001\u0002\u0002\u0002\ta!a\u0005(fiRL(\u000b]2DC2d7i\u001c8uKb$(BA\u0002\u0005\u0003\u0015qW\r\u001e;z\u0015\t)a!A\u0002sa\u000eT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0005\u00015\u0019r\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)Ui\u0011\u0001B\u0005\u0003-\u0011\u0011aB\u00159d\u0007\u0006dGnQ8oi\u0016DH\u000f\u0005\u0002\u001975\t\u0011D\u0003\u0002\u001b\r\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001d3\t9Aj\\4hS:<\u0007\u0002\u0003\u0010\u0001\u0005\u000b\u0007I\u0011\t\u0011\u0002\u001bM,g\u000eZ3s\u0003\u0012$'/Z:t\u0007\u0001)\u0012!\t\t\u0003)\tJ!a\t\u0003\u0003\u0015I\u00038-\u00113ee\u0016\u001c8\u000f\u0003\u0005&\u0001\t\u0005\t\u0015!\u0003\"\u00039\u0019XM\u001c3fe\u0006#GM]3tg\u0002BQa\n\u0001\u0005\u0002!\na\u0001P5oSRtDCA\u0015,!\tQ\u0003!D\u0001\u0003\u0011\u0015qb\u00051\u0001\"\u0011\u0015i\u0003A\"\u0005/\u0003\u0011\u0019XM\u001c3\u0015\u0005=\u0012\u0004C\u0001\b1\u0013\t\ttB\u0001\u0003V]&$\b\"B\u001a-\u0001\u0004!\u0014aB7fgN\fw-\u001a\t\u0003\u001dUJ!AN\b\u0003\u0007\u0005s\u0017\u0010C\u00039\u0001\u0011\u0005\u0013(A\u0003sKBd\u0017\u0010\u0006\u00020u!)1h\u000ea\u0001i\u0005A!/Z:q_:\u001cX\rC\u0003>\u0001\u0011\u0005c(A\u0006tK:$g)Y5mkJ,GCA\u0018@\u0011\u0015\u0001E\b1\u0001B\u0003\u0005)\u0007C\u0001\"K\u001d\t\u0019\u0005J\u0004\u0002E\u000f6\tQI\u0003\u0002G?\u00051AH]8pizJ\u0011\u0001E\u0005\u0003\u0013>\tq\u0001]1dW\u0006<W-\u0003\u0002L\u0019\nIA\u000b\u001b:po\u0006\u0014G.\u001a\u0006\u0003\u0013>\u0001"
)
public abstract class NettyRpcCallContext implements RpcCallContext, Logging {
   private final RpcAddress senderAddress;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public RpcAddress senderAddress() {
      return this.senderAddress;
   }

   public abstract void send(Object var1);

   public void reply(Object response) {
      this.send(response);
   }

   public void sendFailure(Throwable e) {
      this.send(new RpcFailure(e));
   }

   public NettyRpcCallContext(RpcAddress senderAddress) {
      this.senderAddress = senderAddress;
      Logging$class.$init$(this);
   }
}
