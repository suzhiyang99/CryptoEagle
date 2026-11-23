package org.apache.spark;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.PartialFunction;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d3Q!\u0001\u0002\u0001\u0005!\u0011a$T1q\u001fV$\b/\u001e;Ue\u0006\u001c7.\u001a:NCN$XM]#oIB|\u0017N\u001c;\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001cB\u0001A\u0005\u0010+A\u0011!\"D\u0007\u0002\u0017)\tA\"A\u0003tG\u0006d\u0017-\u0003\u0002\u000f\u0017\t1\u0011I\\=SK\u001a\u0004\"\u0001E\n\u000e\u0003EQ!A\u0005\u0002\u0002\u0007I\u00048-\u0003\u0002\u0015#\tY!\u000b]2F]\u0012\u0004x.\u001b8u!\t1\u0012$D\u0001\u0018\u0015\tA\"!\u0001\u0005j]R,'O\\1m\u0013\tQrCA\u0004M_\u001e<\u0017N\\4\t\u0011q\u0001!Q1A\u0005By\taA\u001d9d\u000b:48\u0001A\u000b\u0002?A\u0011\u0001\u0003I\u0005\u0003CE\u0011aA\u00159d\u000b:4\b\u0002C\u0012\u0001\u0005\u0003\u0005\u000b\u0011B\u0010\u0002\u000fI\u00048-\u00128wA!AQ\u0005\u0001B\u0001B\u0003%a%A\u0004ue\u0006\u001c7.\u001a:\u0011\u0005\u001dBS\"\u0001\u0002\n\u0005%\u0012!AF'ba>+H\u000f];u)J\f7m[3s\u001b\u0006\u001cH/\u001a:\t\u0011-\u0002!\u0011!Q\u0001\n1\nAaY8oMB\u0011q%L\u0005\u0003]\t\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000bA\u0002A\u0011A\u0019\u0002\rqJg.\u001b;?)\u0011\u00114\u0007N\u001b\u0011\u0005\u001d\u0002\u0001\"\u0002\u000f0\u0001\u0004y\u0002\"B\u00130\u0001\u00041\u0003\"B\u00160\u0001\u0004a\u0003\"B\u001c\u0001\t\u0003B\u0014a\u0004:fG\u0016Lg/Z!oIJ+\u0007\u000f\\=\u0015\u0005e\u0012\u0005\u0003\u0002\u0006;y}J!aO\u0006\u0003\u001fA\u000b'\u000f^5bY\u001a+hn\u0019;j_:\u0004\"AC\u001f\n\u0005yZ!aA!osB\u0011!\u0002Q\u0005\u0003\u0003.\u0011A!\u00168ji\")1I\u000ea\u0001\t\u000691m\u001c8uKb$\bC\u0001\tF\u0013\t1\u0015C\u0001\bSa\u000e\u001c\u0015\r\u001c7D_:$X\r\u001f;"
)
public class MapOutputTrackerMasterEndpoint implements RpcEndpoint, Logging {
   private final RpcEnv rpcEnv;
   public final MapOutputTrackerMaster org$apache$spark$MapOutputTrackerMasterEndpoint$$tracker;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public final RpcEndpointRef self() {
      return RpcEndpoint$class.self(this);
   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return RpcEndpoint$class.receive(this);
   }

   public void onError(Throwable cause) {
      RpcEndpoint$class.onError(this, cause);
   }

   public void onConnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onConnected(this, remoteAddress);
   }

   public void onDisconnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onDisconnected(this, remoteAddress);
   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      RpcEndpoint$class.onNetworkError(this, cause, remoteAddress);
   }

   public void onStart() {
      RpcEndpoint$class.onStart(this);
   }

   public void onStop() {
      RpcEndpoint$class.onStop(this);
   }

   public final void stop() {
      RpcEndpoint$class.stop(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MapOutputTrackerMasterEndpoint $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var7;
            if (x1 instanceof GetMapOutputStatuses) {
               GetMapOutputStatuses var4 = (GetMapOutputStatuses)x1;
               int shuffleId = var4.shuffleId();
               String hostPort = this.context$1.senderAddress().hostPort();
               this.$outer.logInfo(new Serializable(this, hostPort, shuffleId) {
                  public static final long serialVersionUID = 0L;
                  private final String hostPort$1;
                  private final int x3$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Asked to send map output locations for shuffle ").append((Object)BoxesRunTime.boxToInteger(this.x3$1)).append((Object)" to ").append((Object)this.hostPort$1).toString();
                  }

                  public {
                     this.hostPort$1 = hostPort$1;
                     this.x3$1 = x3$1;
                  }
               });
               this.$outer.org$apache$spark$MapOutputTrackerMasterEndpoint$$tracker.post(new GetMapOutputMessage(shuffleId, this.context$1));
               BoxedUnit mapOutputStatuses = BoxedUnit.UNIT;
               var7 = BoxedUnit.UNIT;
            } else if (StopMapOutputTracker$.MODULE$.equals(x1)) {
               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "MapOutputTrackerMasterEndpoint stopped!";
                  }
               });
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               this.$outer.stop();
               var7 = BoxedUnit.UNIT;
            } else {
               var7 = var2.apply(x1);
            }

            return (B1)var7;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (x1 instanceof GetMapOutputStatuses) {
               var3 = true;
            } else if (StopMapOutputTracker$.MODULE$.equals(x1)) {
               var3 = true;
            } else {
               var3 = false;
            }

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

   public MapOutputTrackerMasterEndpoint(RpcEnv rpcEnv, MapOutputTrackerMaster tracker, SparkConf conf) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$MapOutputTrackerMasterEndpoint$$tracker = tracker;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "init";
         }
      });
   }
}
