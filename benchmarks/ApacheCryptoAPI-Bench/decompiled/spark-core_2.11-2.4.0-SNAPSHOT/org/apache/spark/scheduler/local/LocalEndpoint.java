package org.apache.spark.scheduler.local;

import java.net.URL;
import java.nio.ByteBuffer;
import org.apache.spark.SparkContext$;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskState$;
import org.apache.spark.executor.Executor;
import org.apache.spark.executor.Executor$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.scheduler.TaskDescription;
import org.apache.spark.scheduler.TaskSchedulerImpl;
import org.apache.spark.scheduler.WorkerOffer;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.package$;
import scala.collection.IndexedSeq;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001db!B\u0001\u0003\u0001\u0019a!!\u0004'pG\u0006dWI\u001c3q_&tGO\u0003\u0002\u0004\t\u0005)An\\2bY*\u0011QAB\u0001\ng\u000eDW\rZ;mKJT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0005\u00015\u0019\u0012\u0004\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)]i\u0011!\u0006\u0006\u0003-\u0019\t1A\u001d9d\u0013\tARCA\u000bUQJ,\u0017\rZ*bM\u0016\u0014\u0006oY#oIB|\u0017N\u001c;\u0011\u0005iiR\"A\u000e\u000b\u0005q1\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005yY\"a\u0002'pO\u001eLgn\u001a\u0005\tA\u0001\u0011)\u0019!C!E\u00051!\u000f]2F]Z\u001c\u0001!F\u0001$!\t!B%\u0003\u0002&+\t1!\u000b]2F]ZD\u0001b\n\u0001\u0003\u0002\u0003\u0006IaI\u0001\beB\u001cWI\u001c<!\u0011!I\u0003A!A!\u0002\u0013Q\u0013!D;tKJ\u001cE.Y:t!\u0006$\b\u000eE\u0002,gYr!\u0001L\u0019\u000f\u00055\u0002T\"\u0001\u0018\u000b\u0005=\n\u0013A\u0002\u001fs_>$h(C\u0001\u0011\u0013\t\u0011t\"A\u0004qC\u000e\\\u0017mZ3\n\u0005Q*$aA*fc*\u0011!g\u0004\t\u0003oqj\u0011\u0001\u000f\u0006\u0003si\n1A\\3u\u0015\u0005Y\u0014\u0001\u00026bm\u0006L!!\u0010\u001d\u0003\u0007U\u0013F\n\u0003\u0005\u0006\u0001\t\u0005\t\u0015!\u0003@!\t\u0001\u0015)D\u0001\u0005\u0013\t\u0011EAA\tUCN\\7k\u00195fIVdWM]%na2D\u0001\u0002\u0012\u0001\u0003\u0002\u0003\u0006I!R\u0001\u0010Kb,7-\u001e;pe\n\u000b7m[3oIB\u0011aiR\u0007\u0002\u0005%\u0011\u0001J\u0001\u0002\u0016\u0019>\u001c\u0017\r\\*dQ\u0016$W\u000f\\3s\u0005\u0006\u001c7.\u001a8e\u0011!Q\u0005A!b\u0001\n\u0013Y\u0015A\u0003;pi\u0006d7i\u001c:fgV\tA\n\u0005\u0002\u000f\u001b&\u0011aj\u0004\u0002\u0004\u0013:$\b\u0002\u0003)\u0001\u0005\u0003\u0005\u000b\u0011\u0002'\u0002\u0017Q|G/\u00197D_J,7\u000f\t\u0005\u0006%\u0002!\taU\u0001\u0007y%t\u0017\u000e\u001e \u0015\rQ+fk\u0016-Z!\t1\u0005\u0001C\u0003!#\u0002\u00071\u0005C\u0003*#\u0002\u0007!\u0006C\u0003\u0006#\u0002\u0007q\bC\u0003E#\u0002\u0007Q\tC\u0003K#\u0002\u0007A\nC\u0004\\\u0001\u0001\u0007I\u0011B&\u0002\u0013\u0019\u0014X-Z\"pe\u0016\u001c\bbB/\u0001\u0001\u0004%IAX\u0001\u000eMJ,WmQ8sKN|F%Z9\u0015\u0005}\u0013\u0007C\u0001\ba\u0013\t\twB\u0001\u0003V]&$\bbB2]\u0003\u0003\u0005\r\u0001T\u0001\u0004q\u0012\n\u0004BB3\u0001A\u0003&A*\u0001\u0006ge\u0016,7i\u001c:fg\u0002Bqa\u001a\u0001C\u0002\u0013\u0005\u0001.A\bm_\u000e\fG.\u0012=fGV$xN]%e+\u0005I\u0007C\u00016n\u001b\u0005Y'B\u00017;\u0003\u0011a\u0017M\\4\n\u00059\\'AB*ue&tw\r\u0003\u0004q\u0001\u0001\u0006I![\u0001\u0011Y>\u001c\u0017\r\\#yK\u000e,Ho\u001c:JI\u0002BqA\u001d\u0001C\u0002\u0013\u0005\u0001.A\u000bm_\u000e\fG.\u0012=fGV$xN\u001d%pgRt\u0017-\\3\t\rQ\u0004\u0001\u0015!\u0003j\u0003YawnY1m\u000bb,7-\u001e;pe\"{7\u000f\u001e8b[\u0016\u0004\u0003b\u0002<\u0001\u0005\u0004%Ia^\u0001\tKb,7-\u001e;peV\t\u0001\u0010\u0005\u0002zw6\t!P\u0003\u0002w\r%\u0011AP\u001f\u0002\t\u000bb,7-\u001e;pe\"1a\u0010\u0001Q\u0001\na\f\u0011\"\u001a=fGV$xN\u001d\u0011\t\u000f\u0005\u0005\u0001\u0001\"\u0011\u0002\u0004\u00059!/Z2fSZ,WCAA\u0003!\u0019q\u0011qAA\u0006?&\u0019\u0011\u0011B\b\u0003\u001fA\u000b'\u000f^5bY\u001a+hn\u0019;j_:\u00042ADA\u0007\u0013\r\tya\u0004\u0002\u0004\u0003:L\bbBA\n\u0001\u0011\u0005\u0013QC\u0001\u0010e\u0016\u001cW-\u001b<f\u0003:$'+\u001a9msR!\u0011QAA\f\u0011!\tI\"!\u0005A\u0002\u0005m\u0011aB2p]R,\u0007\u0010\u001e\t\u0004)\u0005u\u0011bAA\u0010+\tq!\u000b]2DC2d7i\u001c8uKb$\bbBA\u0012\u0001\u0011\u0005\u0011QE\u0001\re\u00164\u0018N^3PM\u001a,'o\u001d\u000b\u0002?\u0002"
)
public class LocalEndpoint implements ThreadSafeRpcEndpoint, Logging {
   private final RpcEnv rpcEnv;
   public final TaskSchedulerImpl org$apache$spark$scheduler$local$LocalEndpoint$$scheduler;
   public final LocalSchedulerBackend org$apache$spark$scheduler$local$LocalEndpoint$$executorBackend;
   private final int totalCores;
   private int org$apache$spark$scheduler$local$LocalEndpoint$$freeCores;
   private final String localExecutorId;
   private final String localExecutorHostname;
   private final Executor org$apache$spark$scheduler$local$LocalEndpoint$$executor;
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

   public final RpcEndpointRef self() {
      return RpcEndpoint$class.self(this);
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

   private int totalCores() {
      return this.totalCores;
   }

   public int org$apache$spark$scheduler$local$LocalEndpoint$$freeCores() {
      return this.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores;
   }

   public void org$apache$spark$scheduler$local$LocalEndpoint$$freeCores_$eq(int x$1) {
      this.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores = x$1;
   }

   public String localExecutorId() {
      return this.localExecutorId;
   }

   public String localExecutorHostname() {
      return this.localExecutorHostname;
   }

   public Executor org$apache$spark$scheduler$local$LocalEndpoint$$executor() {
      return this.org$apache$spark$scheduler$local$LocalEndpoint$$executor;
   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LocalEndpoint $outer;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var4;
            if (ReviveOffers$.MODULE$.equals(x1)) {
               this.$outer.reviveOffers();
               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof StatusUpdate) {
               StatusUpdate var5 = (StatusUpdate)x1;
               long taskId = var5.taskId();
               Enumeration.Value state = var5.state();
               ByteBuffer serializedData = var5.serializedData();
               this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$scheduler.statusUpdate(taskId, state, serializedData);
               BoxedUnit var10000;
               if (TaskState$.MODULE$.isFinished(state)) {
                  this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores_$eq(this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores() + this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$scheduler.CPUS_PER_TASK());
                  this.$outer.reviveOffers();
                  var10000 = BoxedUnit.UNIT;
               } else {
                  var10000 = BoxedUnit.UNIT;
               }

               var4 = var10000;
            } else if (x1 instanceof KillTask) {
               KillTask var10 = (KillTask)x1;
               long taskId = var10.taskId();
               boolean interruptThread = var10.interruptThread();
               String reason = var10.reason();
               this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$executor().killTask(taskId, interruptThread, reason);
               var4 = BoxedUnit.UNIT;
            } else {
               var4 = var2.apply(x1);
            }

            return (B1)var4;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (ReviveOffers$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof StatusUpdate) {
               var3 = true;
            } else if (x1 instanceof KillTask) {
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
            }
         }
      };
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LocalEndpoint $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
            Object var4;
            if (StopExecutor$.MODULE$.equals(x2)) {
               this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$executor().stop();
               this.context$1.reply(BoxesRunTime.boxToBoolean(true));
               var4 = BoxedUnit.UNIT;
            } else {
               var4 = var2.apply(x2);
            }

            return (B1)var4;
         }

         public final boolean isDefinedAt(Object x2) {
            boolean var3;
            if (StopExecutor$.MODULE$.equals(x2)) {
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

   public void reviveOffers() {
      IndexedSeq offers = (IndexedSeq)package$.MODULE$.IndexedSeq().apply(Predef$.MODULE$.wrapRefArray(new WorkerOffer[]{new WorkerOffer(this.localExecutorId(), this.localExecutorHostname(), this.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores())}));
      ((IterableLike)this.org$apache$spark$scheduler$local$LocalEndpoint$$scheduler.resourceOffers(offers).flatten(Predef$.MODULE$.$conforms())).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LocalEndpoint $outer;

         public final void apply(TaskDescription task) {
            this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores_$eq(this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores() - this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$scheduler.CPUS_PER_TASK());
            this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$executor().launchTask(this.$outer.org$apache$spark$scheduler$local$LocalEndpoint$$executorBackend, task);
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

   public LocalEndpoint(RpcEnv rpcEnv, Seq<URL> userClassPath, TaskSchedulerImpl scheduler, LocalSchedulerBackend executorBackend, int totalCores) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$scheduler$local$LocalEndpoint$$scheduler = scheduler;
      this.org$apache$spark$scheduler$local$LocalEndpoint$$executorBackend = executorBackend;
      this.totalCores = totalCores;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.org$apache$spark$scheduler$local$LocalEndpoint$$freeCores = totalCores;
      this.localExecutorId = SparkContext$.MODULE$.DRIVER_IDENTIFIER();
      this.localExecutorHostname = "localhost";
      this.org$apache$spark$scheduler$local$LocalEndpoint$$executor = new Executor(this.localExecutorId(), this.localExecutorHostname(), SparkEnv$.MODULE$.get(), userClassPath, true, Executor$.MODULE$.$lessinit$greater$default$6());
   }
}
