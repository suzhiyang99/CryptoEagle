package org.apache.spark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.scheduler.SlaveLost;
import org.apache.spark.scheduler.SlaveLost$;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerExecutorAdded;
import org.apache.spark.scheduler.SparkListenerExecutorRemoved;
import org.apache.spark.scheduler.TaskScheduler;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.Clock;
import org.apache.spark.util.SystemClock;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Future;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-h!B\u0001\u0003\u0001\tA!!\u0005%fCJ$(-Z1u%\u0016\u001cW-\u001b<fe*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0013=)\u0002C\u0001\u0006\u000e\u001b\u0005Y!B\u0001\u0007\u0003\u0003%\u00198\r[3ek2,'/\u0003\u0002\u000f\u0017\ti1\u000b]1sW2K7\u000f^3oKJ\u0004\"\u0001E\n\u000e\u0003EQ!A\u0005\u0002\u0002\u0007I\u00048-\u0003\u0002\u0015#\t)B\u000b\u001b:fC\u0012\u001c\u0016MZ3Sa\u000e,e\u000e\u001a9pS:$\bC\u0001\f\u001a\u001b\u00059\"B\u0001\r\u0003\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u000e\u0018\u0005\u001daunZ4j]\u001eD\u0001\u0002\b\u0001\u0003\u0002\u0003\u0006IAH\u0001\u0003g\u000e\u001c\u0001\u0001\u0005\u0002 A5\t!!\u0003\u0002\"\u0005\ta1\u000b]1sW\u000e{g\u000e^3yi\"A1\u0005\u0001B\u0001B\u0003%A%A\u0003dY>\u001c7\u000e\u0005\u0002&Q5\taE\u0003\u0002(\u0005\u0005!Q\u000f^5m\u0013\tIcEA\u0003DY>\u001c7\u000eC\u0003,\u0001\u0011\u0005A&\u0001\u0004=S:LGO\u0010\u000b\u0004[9z\u0003CA\u0010\u0001\u0011\u0015a\"\u00061\u0001\u001f\u0011\u0015\u0019#\u00061\u0001%\u0011\u0015Y\u0003\u0001\"\u00012)\ti#\u0007C\u0003\u001da\u0001\u0007a\u0004C\u00045\u0001\t\u0007I\u0011I\u001b\u0002\rI\u00048-\u00128w+\u00051\u0004C\u0001\t8\u0013\tA\u0014C\u0001\u0004Sa\u000e,eN\u001e\u0005\u0007u\u0001\u0001\u000b\u0011\u0002\u001c\u0002\u000fI\u00048-\u00128wA!AA\u0002\u0001a\u0001\n\u0003\u0011A(F\u0001>!\tQa(\u0003\u0002@\u0017\tiA+Y:l'\u000eDW\rZ;mKJD\u0001\"\u0011\u0001A\u0002\u0013\u0005!AQ\u0001\u000eg\u000eDW\rZ;mKJ|F%Z9\u0015\u0005\rK\u0005C\u0001#H\u001b\u0005)%\"\u0001$\u0002\u000bM\u001c\u0017\r\\1\n\u0005!+%\u0001B+oSRDqA\u0013!\u0002\u0002\u0003\u0007Q(A\u0002yIEBa\u0001\u0014\u0001!B\u0013i\u0014AC:dQ\u0016$W\u000f\\3sA!9a\n\u0001b\u0001\n\u0013y\u0015\u0001E3yK\u000e,Ho\u001c:MCN$8+Z3o+\u0005\u0001\u0006\u0003B)W1~k\u0011A\u0015\u0006\u0003'R\u000bq!\\;uC\ndWM\u0003\u0002V\u000b\u0006Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005]\u0013&a\u0002%bg\"l\u0015\r\u001d\t\u00033rs!\u0001\u0012.\n\u0005m+\u0015A\u0002)sK\u0012,g-\u0003\u0002^=\n11\u000b\u001e:j]\u001eT!aW#\u0011\u0005\u0011\u0003\u0017BA1F\u0005\u0011auN\\4\t\r\r\u0004\u0001\u0015!\u0003Q\u0003E)\u00070Z2vi>\u0014H*Y:u'\u0016,g\u000e\t\u0005\bK\u0002\u0011\r\u0011\"\u0003g\u00039\u0019H.\u0019<f)&lWm\\;u\u001bN,\u0012a\u0018\u0005\u0007Q\u0002\u0001\u000b\u0011B0\u0002\u001fMd\u0017M^3US6,w.\u001e;Ng\u0002BqA\u001b\u0001C\u0002\u0013%a-A\tfq\u0016\u001cW\u000f^8s)&lWm\\;u\u001bNDa\u0001\u001c\u0001!\u0002\u0013y\u0016AE3yK\u000e,Ho\u001c:US6,w.\u001e;Ng\u0002BqA\u001c\u0001C\u0002\u0013%a-A\tuS6,w.\u001e;J]R,'O^1m\u001bNDa\u0001\u001d\u0001!\u0002\u0013y\u0016A\u0005;j[\u0016|W\u000f^%oi\u0016\u0014h/\u00197Ng\u0002BqA\u001d\u0001C\u0002\u0013%a-\u0001\fdQ\u0016\u001c7\u000eV5nK>,H/\u00138uKJ4\u0018\r\\'t\u0011\u0019!\b\u0001)A\u0005?\u000692\r[3dWRKW.Z8vi&sG/\u001a:wC2l5\u000f\t\u0005\bm\u0002\u0001\r\u0011\"\u0003x\u0003M!\u0018.\\3pkR\u001c\u0005.Z2lS:<G+Y:l+\u0005A\bgA=\u0002\nA)!0!\u0001\u0002\u00065\t1P\u0003\u0002}{\u0006Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005\u001dr(\"A@\u0002\t)\fg/Y\u0005\u0004\u0003\u0007Y(aD*dQ\u0016$W\u000f\\3e\rV$XO]3\u0011\t\u0005\u001d\u0011\u0011\u0002\u0007\u0001\t1\tY!!\u0004\u0002\u0002\u0003\u0005)\u0011AA\r\u0005\ryFe\r\u0005\t\u0003\u001f\u0001\u0001\u0015)\u0003\u0002\u0012\u0005!B/[7f_V$8\t[3dW&tw\rV1tW\u0002\u0002D!a\u0005\u0002\u0018A)!0!\u0001\u0002\u0016A!\u0011qAA\f\t1\tY!!\u0004\u0002\u0002\u0003\u0005)\u0011AA\r#\u0011\tY\"!\t\u0011\u0007\u0011\u000bi\"C\u0002\u0002 \u0015\u0013qAT8uQ&tw\rE\u0002E\u0003GI1!!\nF\u0005\r\te.\u001f\u0005\n\u0003S\u0001\u0001\u0019!C\u0005\u0003W\tq\u0003^5nK>,Ho\u00115fG.Lgn\u001a+bg.|F%Z9\u0015\u0007\r\u000bi\u0003C\u0005K\u0003O\t\t\u00111\u0001\u00020A\"\u0011\u0011GA\u001b!\u0015Q\u0018\u0011AA\u001a!\u0011\t9!!\u000e\u0005\u0019\u0005-\u0011QBA\u0001\u0002\u0003\u0015\t!!\u0007\t\u0013\u0005e\u0002A1A\u0005\n\u0005m\u0012aD3wK:$Hj\\8q)\"\u0014X-\u00193\u0016\u0005\u0005u\u0002c\u0001>\u0002@%\u0019\u0011\u0011I>\u00031M\u001b\u0007.\u001a3vY\u0016$W\t_3dkR|'oU3sm&\u001cW\r\u0003\u0005\u0002F\u0001\u0001\u000b\u0011BA\u001f\u0003A)g/\u001a8u\u0019>|\u0007\u000f\u00165sK\u0006$\u0007\u0005C\u0005\u0002J\u0001\u0011\r\u0011\"\u0003\u0002L\u0005\u00112.\u001b7m\u000bb,7-\u001e;peRC'/Z1e+\t\ti\u0005E\u0002{\u0003\u001fJ1!!\u0015|\u0005=)\u00050Z2vi>\u00148+\u001a:wS\u000e,\u0007\u0002CA+\u0001\u0001\u0006I!!\u0014\u0002'-LG\u000e\\#yK\u000e,Ho\u001c:UQJ,\u0017\r\u001a\u0011\t\u000f\u0005e\u0003\u0001\"\u0011\u0002\\\u00059qN\\*uCJ$H#A\"\t\u000f\u0005}\u0003\u0001\"\u0011\u0002b\u0005y!/Z2fSZ,\u0017I\u001c3SKBd\u0017\u0010\u0006\u0003\u0002d\u0005%\u0004C\u0002#\u0002f\u0005\u00052)C\u0002\u0002h\u0015\u0013q\u0002U1si&\fGNR;oGRLwN\u001c\u0005\t\u0003W\ni\u00061\u0001\u0002n\u000591m\u001c8uKb$\bc\u0001\t\u0002p%\u0019\u0011\u0011O\t\u0003\u001dI\u00038mQ1mY\u000e{g\u000e^3yi\"9\u0011Q\u000f\u0001\u0005\u0002\u0005]\u0014aC1eI\u0016CXmY;u_J$B!!\u001f\u0002\u0010B)A)a\u001f\u0002\u0000%\u0019\u0011QP#\u0003\r=\u0003H/[8o!\u0019\t\t)!\"\u0002\n6\u0011\u00111\u0011\u0006\u0003y\u0016KA!a\"\u0002\u0004\n1a)\u001e;ve\u0016\u00042\u0001RAF\u0013\r\ti)\u0012\u0002\b\u0005>|G.Z1o\u0011\u001d\t\t*a\u001dA\u0002a\u000b!\"\u001a=fGV$xN]%e\u0011\u001d\t)\n\u0001C!\u0003/\u000bqb\u001c8Fq\u0016\u001cW\u000f^8s\u0003\u0012$W\r\u001a\u000b\u0004\u0007\u0006e\u0005\u0002CAN\u0003'\u0003\r!!(\u0002\u001b\u0015DXmY;u_J\fE\rZ3e!\rQ\u0011qT\u0005\u0004\u0003C[!AG*qCJ\\G*[:uK:,'/\u0012=fGV$xN]!eI\u0016$\u0007bBAS\u0001\u0011\u0005\u0011qU\u0001\u000fe\u0016lwN^3Fq\u0016\u001cW\u000f^8s)\u0011\tI(!+\t\u000f\u0005E\u00151\u0015a\u00011\"9\u0011Q\u0016\u0001\u0005B\u0005=\u0016!E8o\u000bb,7-\u001e;peJ+Wn\u001c<fIR\u00191)!-\t\u0011\u0005M\u00161\u0016a\u0001\u0003k\u000bq\"\u001a=fGV$xN\u001d*f[>4X\r\u001a\t\u0004\u0015\u0005]\u0016bAA]\u0017\ta2\u000b]1sW2K7\u000f^3oKJ,\u00050Z2vi>\u0014(+Z7pm\u0016$\u0007bBA_\u0001\u0011%\u00111L\u0001\u0010Kb\u0004\u0018N]3EK\u0006$\u0007j\\:ug\"9\u0011\u0011\u0019\u0001\u0005B\u0005m\u0013AB8o'R|\u0007o\u0002\u0005\u0002F\nA\tAAAd\u0003EAU-\u0019:uE\u0016\fGOU3dK&4XM\u001d\t\u0004?\u0005%gaB\u0001\u0003\u0011\u0003\u0011\u00111Z\n\u0005\u0003\u0013\fi\rE\u0002E\u0003\u001fL1!!5F\u0005\u0019\te.\u001f*fM\"91&!3\u0005\u0002\u0005UGCAAd\u0011)\tI.!3C\u0002\u0013\u0005\u00111\\\u0001\u000e\u000b:#\u0005kT%O)~s\u0015)T#\u0016\u0005\u0005u\u0007\u0003BAp\u0003Kl!!!9\u000b\u0007\u0005\rh0\u0001\u0003mC:<\u0017bA/\u0002b\"I\u0011\u0011^AeA\u0003%\u0011Q\\\u0001\u000f\u000b:#\u0005kT%O)~s\u0015)T#!\u0001"
)
public class HeartbeatReceiver extends SparkListener implements ThreadSafeRpcEndpoint, Logging {
   public final SparkContext org$apache$spark$HeartbeatReceiver$$sc;
   public final Clock org$apache$spark$HeartbeatReceiver$$clock;
   private final RpcEnv rpcEnv;
   private TaskScheduler scheduler;
   private final HashMap<String, Object> org$apache$spark$HeartbeatReceiver$$executorLastSeen;
   private final long slaveTimeoutMs;
   private final long org$apache$spark$HeartbeatReceiver$$executorTimeoutMs;
   private final long timeoutIntervalMs;
   private final long checkTimeoutIntervalMs;
   private ScheduledFuture<?> timeoutCheckingTask;
   private final ScheduledExecutorService org$apache$spark$HeartbeatReceiver$$eventLoopThread;
   private final ExecutorService org$apache$spark$HeartbeatReceiver$$killExecutorThread;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String ENDPOINT_NAME() {
      return HeartbeatReceiver$.MODULE$.ENDPOINT_NAME();
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

   public final void stop() {
      RpcEndpoint$class.stop(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public TaskScheduler scheduler() {
      return this.scheduler;
   }

   public void scheduler_$eq(TaskScheduler x$1) {
      this.scheduler = x$1;
   }

   public HashMap<String, Object> org$apache$spark$HeartbeatReceiver$$executorLastSeen() {
      return this.org$apache$spark$HeartbeatReceiver$$executorLastSeen;
   }

   private long slaveTimeoutMs() {
      return this.slaveTimeoutMs;
   }

   public long org$apache$spark$HeartbeatReceiver$$executorTimeoutMs() {
      return this.org$apache$spark$HeartbeatReceiver$$executorTimeoutMs;
   }

   private long timeoutIntervalMs() {
      return this.timeoutIntervalMs;
   }

   private long checkTimeoutIntervalMs() {
      return this.checkTimeoutIntervalMs;
   }

   private ScheduledFuture<?> timeoutCheckingTask() {
      return this.timeoutCheckingTask;
   }

   private void timeoutCheckingTask_$eq(ScheduledFuture<?> x$1) {
      this.timeoutCheckingTask = x$1;
   }

   public ScheduledExecutorService org$apache$spark$HeartbeatReceiver$$eventLoopThread() {
      return this.org$apache$spark$HeartbeatReceiver$$eventLoopThread;
   }

   public ExecutorService org$apache$spark$HeartbeatReceiver$$killExecutorThread() {
      return this.org$apache$spark$HeartbeatReceiver$$killExecutorThread;
   }

   public void onStart() {
      this.timeoutCheckingTask_$eq(this.org$apache$spark$HeartbeatReceiver$$eventLoopThread().scheduleAtFixedRate(new Runnable(this) {
         // $FF: synthetic field
         private final HeartbeatReceiver $outer;

         public void run() {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  Option$.MODULE$.apply(this.$outer.org$apache$spark$HeartbeatReceiver$$anon$$$outer().self()).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Future<Object> apply(RpcEndpointRef x$1) {
                        return x$1.<Object>ask(ExpireDeadHosts$.MODULE$, ClassTag$.MODULE$.Boolean());
                     }
                  });
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         // $FF: synthetic method
         public HeartbeatReceiver org$apache$spark$HeartbeatReceiver$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, 0L, this.checkTimeoutIntervalMs(), TimeUnit.MILLISECONDS));
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HeartbeatReceiver $outer;
         public final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var6;
            if (x1 instanceof ExecutorRegistered) {
               ExecutorRegistered var4 = (ExecutorRegistered)x1;
               String executorId = var4.executorId();
               this.$outer.org$apache$spark$HeartbeatReceiver$$executorLastSeen().update(executorId, BoxesRunTime.boxToLong(this.$outer.org$apache$spark$HeartbeatReceiver$$clock.getTimeMillis()));
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof ExecutorRemoved) {
               ExecutorRemoved var7 = (ExecutorRemoved)x1;
               String executorId = var7.executorId();
               this.$outer.org$apache$spark$HeartbeatReceiver$$executorLastSeen().remove(executorId);
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               var6 = BoxedUnit.UNIT;
            } else if (TaskSchedulerIsSet$.MODULE$.equals(x1)) {
               this.$outer.scheduler_$eq(this.$outer.org$apache$spark$HeartbeatReceiver$$sc.taskScheduler());
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               var6 = BoxedUnit.UNIT;
            } else if (ExpireDeadHosts$.MODULE$.equals(x1)) {
               this.$outer.org$apache$spark$HeartbeatReceiver$$expireDeadHosts();
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof Heartbeat) {
               Heartbeat var9 = (Heartbeat)x1;
               String executorId = var9.executorId();
               Tuple2[] accumUpdates = var9.accumUpdates();
               BlockManagerId blockManagerId = var9.blockManagerId();
               BoxedUnit var10000;
               if (this.$outer.scheduler() == null) {
                  this.$outer.logWarning(new Serializable(this, var9) {
                     public static final long serialVersionUID = 0L;
                     private final Heartbeat x4$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Dropping ", " because TaskScheduler is not ready yet"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x4$1}));
                     }

                     public {
                        this.x4$1 = x4$1;
                     }
                  });
                  this.context$1.reply(new HeartbeatResponse(true));
                  var10000 = BoxedUnit.UNIT;
               } else if (this.$outer.org$apache$spark$HeartbeatReceiver$$executorLastSeen().contains(executorId)) {
                  this.$outer.org$apache$spark$HeartbeatReceiver$$executorLastSeen().update(executorId, BoxesRunTime.boxToLong(this.$outer.org$apache$spark$HeartbeatReceiver$$clock.getTimeMillis()));
                  this.$outer.org$apache$spark$HeartbeatReceiver$$eventLoopThread().submit(new Runnable(this, executorId, accumUpdates, blockManagerId) {
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     public final String executorId$1;
                     public final Tuple2[] accumUpdates$1;
                     public final BlockManagerId blockManagerId$1;

                     public void run() {
                        Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final void apply() {
                              this.apply$mcV$sp();
                           }

                           public void apply$mcV$sp() {
                              boolean unknownExecutor = !this.$outer.org$apache$spark$HeartbeatReceiver$$anonfun$$anon$$$outer().org$apache$spark$HeartbeatReceiver$$anonfun$$$outer().scheduler().executorHeartbeatReceived(this.$outer.executorId$1, this.$outer.accumUpdates$1, this.$outer.blockManagerId$1);
                              HeartbeatResponse response = new HeartbeatResponse(unknownExecutor);
                              this.$outer.org$apache$spark$HeartbeatReceiver$$anonfun$$anon$$$outer().context$1.reply(response);
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                     }

                     // $FF: synthetic method
                     public <undefinedtype> org$apache$spark$HeartbeatReceiver$$anonfun$$anon$$$outer() {
                        return this.$outer;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.executorId$1 = executorId$1;
                           this.accumUpdates$1 = accumUpdates$1;
                           this.blockManagerId$1 = blockManagerId$1;
                        }
                     }
                  });
                  var10000 = BoxedUnit.UNIT;
               } else {
                  this.$outer.logDebug(new Serializable(this, executorId) {
                     public static final long serialVersionUID = 0L;
                     private final String executorId$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received heartbeat from unknown executor ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$1}));
                     }

                     public {
                        this.executorId$1 = executorId$1;
                     }
                  });
                  this.context$1.reply(new HeartbeatResponse(true));
                  var10000 = BoxedUnit.UNIT;
               }

               var6 = var10000;
            } else {
               var6 = var2.apply(x1);
            }

            return (B1)var6;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (x1 instanceof ExecutorRegistered) {
               var3 = true;
            } else if (x1 instanceof ExecutorRemoved) {
               var3 = true;
            } else if (TaskSchedulerIsSet$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (ExpireDeadHosts$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof Heartbeat) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         // $FF: synthetic method
         public HeartbeatReceiver org$apache$spark$HeartbeatReceiver$$anonfun$$$outer() {
            return this.$outer;
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

   public Option<Future<Object>> addExecutor(String executorId) {
      return Option$.MODULE$.<Future<Object>>apply(this.self()).<Future<Object>>map(new Serializable(this, executorId) {
         public static final long serialVersionUID = 0L;
         private final String executorId$2;

         public final Future<Object> apply(RpcEndpointRef x$2) {
            return x$2.<Object>ask(new ExecutorRegistered(this.executorId$2), ClassTag$.MODULE$.Boolean());
         }

         public {
            this.executorId$2 = executorId$2;
         }
      });
   }

   public void onExecutorAdded(SparkListenerExecutorAdded executorAdded) {
      this.addExecutor(executorAdded.executorId());
   }

   public Option<Future<Object>> removeExecutor(String executorId) {
      return Option$.MODULE$.<Future<Object>>apply(this.self()).<Future<Object>>map(new Serializable(this, executorId) {
         public static final long serialVersionUID = 0L;
         private final String executorId$3;

         public final Future<Object> apply(RpcEndpointRef x$3) {
            return x$3.<Object>ask(new ExecutorRemoved(this.executorId$3), ClassTag$.MODULE$.Boolean());
         }

         public {
            this.executorId$3 = executorId$3;
         }
      });
   }

   public void onExecutorRemoved(SparkListenerExecutorRemoved executorRemoved) {
      this.removeExecutor(executorRemoved.executorId());
   }

   public void org$apache$spark$HeartbeatReceiver$$expireDeadHosts() {
      this.logTrace(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Checking for hosts with no recent heartbeats in HeartbeatReceiver.";
         }
      });
      long now = this.org$apache$spark$HeartbeatReceiver$$clock.getTimeMillis();
      this.org$apache$spark$HeartbeatReceiver$$executorLastSeen().withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, Object> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HeartbeatReceiver $outer;
         public final long now$1;

         public final Object apply(Tuple2<String, Object> x$4) {
            if (x$4 != null) {
               String executorId = (String)x$4._1();
               long lastSeenMs = x$4._2$mcJ$sp();
               Object var10000;
               if (this.now$1 - lastSeenMs > this.$outer.org$apache$spark$HeartbeatReceiver$$executorTimeoutMs()) {
                  this.$outer.logWarning(new Serializable(this, executorId, lastSeenMs) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final String executorId$4;
                     private final long lastSeenMs$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing executor ", " with no recent heartbeats: "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$4}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ms exceeds timeout ", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.now$1 - this.lastSeenMs$1), BoxesRunTime.boxToLong(this.$outer.org$apache$spark$HeartbeatReceiver$$anonfun$$$outer().org$apache$spark$HeartbeatReceiver$$executorTimeoutMs())}))).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.executorId$4 = executorId$4;
                           this.lastSeenMs$1 = lastSeenMs$1;
                        }
                     }
                  });
                  this.$outer.scheduler().executorLost(executorId, new SlaveLost((new StringBuilder()).append((Object)"Executor heartbeat ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"timed out after ", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.now$1 - lastSeenMs)}))).toString(), SlaveLost$.MODULE$.apply$default$2()));
                  this.$outer.org$apache$spark$HeartbeatReceiver$$killExecutorThread().submit(new Runnable(this, executorId) {
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     public final String executorId$4;

                     public void run() {
                        Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final void apply() {
                              this.apply$mcV$sp();
                           }

                           public void apply$mcV$sp() {
                              this.$outer.org$apache$spark$HeartbeatReceiver$$anonfun$$anon$$$outer().org$apache$spark$HeartbeatReceiver$$anonfun$$$outer().org$apache$spark$HeartbeatReceiver$$sc.killAndReplaceExecutor(this.$outer.executorId$4);
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                     }

                     // $FF: synthetic method
                     public <undefinedtype> org$apache$spark$HeartbeatReceiver$$anonfun$$anon$$$outer() {
                        return this.$outer;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.executorId$4 = executorId$4;
                        }
                     }
                  });
                  var10000 = this.$outer.org$apache$spark$HeartbeatReceiver$$executorLastSeen().remove(executorId);
               } else {
                  var10000 = BoxedUnit.UNIT;
               }

               Object var6 = var10000;
               return var6;
            } else {
               throw new MatchError(x$4);
            }
         }

         // $FF: synthetic method
         public HeartbeatReceiver org$apache$spark$HeartbeatReceiver$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$1 = now$1;
            }
         }
      });
   }

   public void onStop() {
      if (this.timeoutCheckingTask() == null) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         BoxesRunTime.boxToBoolean(this.timeoutCheckingTask().cancel(true));
      }

      this.org$apache$spark$HeartbeatReceiver$$eventLoopThread().shutdownNow();
      this.org$apache$spark$HeartbeatReceiver$$killExecutorThread().shutdownNow();
   }

   public HeartbeatReceiver(SparkContext sc, Clock clock) {
      this.org$apache$spark$HeartbeatReceiver$$sc = sc;
      this.org$apache$spark$HeartbeatReceiver$$clock = clock;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      sc.listenerBus().addToManagementQueue(this);
      this.rpcEnv = sc.env().rpcEnv();
      this.scheduler = null;
      this.org$apache$spark$HeartbeatReceiver$$executorLastSeen = new HashMap<String, Object>();
      this.slaveTimeoutMs = sc.conf().getTimeAsMs("spark.storage.blockManagerSlaveTimeoutMs", "120s");
      this.org$apache$spark$HeartbeatReceiver$$executorTimeoutMs = sc.conf().getTimeAsSeconds("spark.network.timeout", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.slaveTimeoutMs())}))) * 1000L;
      this.timeoutIntervalMs = sc.conf().getTimeAsMs("spark.storage.blockManagerTimeoutIntervalMs", "60s");
      this.checkTimeoutIntervalMs = sc.conf().getTimeAsSeconds("spark.network.timeoutInterval", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.timeoutIntervalMs())}))) * 1000L;
      this.timeoutCheckingTask = null;
      this.org$apache$spark$HeartbeatReceiver$$eventLoopThread = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("heartbeat-receiver-event-loop-thread");
      this.org$apache$spark$HeartbeatReceiver$$killExecutorThread = ThreadUtils$.MODULE$.newDaemonSingleThreadExecutor("kill-executor-thread");
   }

   public HeartbeatReceiver(SparkContext sc) {
      this(sc, new SystemClock());
   }
}
