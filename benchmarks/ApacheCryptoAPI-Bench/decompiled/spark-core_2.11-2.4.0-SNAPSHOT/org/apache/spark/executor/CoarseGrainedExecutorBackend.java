package org.apache.spark.executor;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.spark.SparkEnv;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.scheduler.ExecutorLossReason;
import org.apache.spark.scheduler.TaskDescription;
import org.apache.spark.scheduler.TaskDescription$;
import org.apache.spark.scheduler.cluster.CoarseGrainedClusterMessages;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.SerializableBuffer;
import org.apache.spark.util.ThreadUtils$;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Future;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u0015b!B\u0001\u0003\u0001\u0011Q!\u0001H\"pCJ\u001cXm\u0012:bS:,G-\u0012=fGV$xN\u001d\"bG.,g\u000e\u001a\u0006\u0003\u0007\u0011\t\u0001\"\u001a=fGV$xN\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON)\u0001aC\t\u00187A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\u000b\u000e\u0003MQ!\u0001\u0006\u0003\u0002\u0007I\u00048-\u0003\u0002\u0017'\t)B\u000b\u001b:fC\u0012\u001c\u0016MZ3Sa\u000e,e\u000e\u001a9pS:$\bC\u0001\r\u001a\u001b\u0005\u0011\u0011B\u0001\u000e\u0003\u0005=)\u00050Z2vi>\u0014()Y2lK:$\u0007C\u0001\u000f \u001b\u0005i\"B\u0001\u0010\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u0011\u001e\u0005\u001daunZ4j]\u001eD\u0001B\t\u0001\u0003\u0006\u0004%\t\u0005J\u0001\u0007eB\u001cWI\u001c<\u0004\u0001U\tQ\u0005\u0005\u0002\u0013M%\u0011qe\u0005\u0002\u0007%B\u001cWI\u001c<\t\u0011%\u0002!\u0011!Q\u0001\n\u0015\nqA\u001d9d\u000b:4\b\u0005\u0003\u0005,\u0001\t\u0005\t\u0015!\u0003-\u0003%!'/\u001b<feV\u0013H\u000e\u0005\u0002.a9\u0011ABL\u0005\u0003_5\ta\u0001\u0015:fI\u00164\u0017BA\u00193\u0005\u0019\u0019FO]5oO*\u0011q&\u0004\u0005\ti\u0001\u0011\t\u0011)A\u0005Y\u0005QQ\r_3dkR|'/\u00133\t\u0011Y\u0002!\u0011!Q\u0001\n1\n\u0001\u0002[8ti:\fW.\u001a\u0005\tq\u0001\u0011\t\u0011)A\u0005s\u0005)1m\u001c:fgB\u0011ABO\u0005\u0003w5\u00111!\u00138u\u0011!i\u0004A!A!\u0002\u0013q\u0014!D;tKJ\u001cE.Y:t!\u0006$\b\u000eE\u0002@\u000f*s!\u0001Q#\u000f\u0005\u0005#U\"\u0001\"\u000b\u0005\r\u001b\u0013A\u0002\u001fs_>$h(C\u0001\u000f\u0013\t1U\"A\u0004qC\u000e\\\u0017mZ3\n\u0005!K%aA*fc*\u0011a)\u0004\t\u0003\u0017Bk\u0011\u0001\u0014\u0006\u0003\u001b:\u000b1A\\3u\u0015\u0005y\u0015\u0001\u00026bm\u0006L!!\u0015'\u0003\u0007U\u0013F\n\u0003\u0005T\u0001\t\u0005\t\u0015!\u0003U\u0003\r)gN\u001e\t\u0003+Zk\u0011\u0001B\u0005\u0003/\u0012\u0011\u0001b\u00159be.,eN\u001e\u0005\u00063\u0002!\tAW\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0011mcVLX0aC\n\u0004\"\u0001\u0007\u0001\t\u000b\tB\u0006\u0019A\u0013\t\u000b-B\u0006\u0019\u0001\u0017\t\u000bQB\u0006\u0019\u0001\u0017\t\u000bYB\u0006\u0019\u0001\u0017\t\u000baB\u0006\u0019A\u001d\t\u000buB\u0006\u0019\u0001 \t\u000bMC\u0006\u0019\u0001+\t\r\u0011\u0004\u0001\u0015!\u0003f\u0003!\u0019Ho\u001c9qS:<\u0007C\u00014n\u001b\u00059'B\u00015j\u0003\u0019\tGo\\7jG*\u0011!n[\u0001\u000bG>t7-\u001e:sK:$(B\u00017O\u0003\u0011)H/\u001b7\n\u00059<'!D!u_6L7MQ8pY\u0016\fg\u000eC\u0004\u0004\u0001\u0001\u0007I\u0011\u00019\u0016\u0003E\u0004\"\u0001\u0007:\n\u0005M\u0014!\u0001C#yK\u000e,Ho\u001c:\t\u000fU\u0004\u0001\u0019!C\u0001m\u0006aQ\r_3dkR|'o\u0018\u0013fcR\u0011qO\u001f\t\u0003\u0019aL!!_\u0007\u0003\tUs\u0017\u000e\u001e\u0005\bwR\f\t\u00111\u0001r\u0003\rAH%\r\u0005\u0007{\u0002\u0001\u000b\u0015B9\u0002\u0013\u0015DXmY;u_J\u0004\u0003\u0002C@\u0001\u0001\u0004%\t!!\u0001\u0002\r\u0011\u0014\u0018N^3s+\t\t\u0019\u0001E\u0003\r\u0003\u000b\tI!C\u0002\u0002\b5\u0011aa\u00149uS>t\u0007c\u0001\n\u0002\f%\u0019\u0011QB\n\u0003\u001dI\u00038-\u00128ea>Lg\u000e\u001e*fM\"I\u0011\u0011\u0003\u0001A\u0002\u0013\u0005\u00111C\u0001\u000bIJLg/\u001a:`I\u0015\fHcA<\u0002\u0016!I10a\u0004\u0002\u0002\u0003\u0007\u00111\u0001\u0005\t\u00033\u0001\u0001\u0015)\u0003\u0002\u0004\u00059AM]5wKJ\u0004\u0003\u0006BA\f\u0003;\u00012\u0001DA\u0010\u0013\r\t\t#\u0004\u0002\tm>d\u0017\r^5mK\"A\u0011Q\u0005\u0001!\u0002\u0013\t9#A\u0002tKJ\u0004B!!\u000b\u000205\u0011\u00111\u0006\u0006\u0004\u0003[!\u0011AC:fe&\fG.\u001b>fe&!\u0011\u0011GA\u0016\u0005I\u0019VM]5bY&TXM]%ogR\fgnY3\t\u000f\u0005U\u0002\u0001\"\u0011\u00028\u00059qN\\*uCJ$H#A<\t\u000f\u0005m\u0002\u0001\"\u0001\u0002>\u0005qQ\r\u001f;sC\u000e$Hj\\4Ve2\u001cXCAA !\u0015i\u0013\u0011\t\u0017-\u0013\r\t\u0019E\r\u0002\u0004\u001b\u0006\u0004\bbBA$\u0001\u0011\u0005\u0013\u0011J\u0001\be\u0016\u001cW-\u001b<f+\t\tY\u0005\u0005\u0004\r\u0003\u001b\n\tf^\u0005\u0004\u0003\u001fj!a\u0004)beRL\u0017\r\u001c$v]\u000e$\u0018n\u001c8\u0011\u00071\t\u0019&C\u0002\u0002V5\u00111!\u00118z\u0011\u001d\tI\u0006\u0001C!\u00037\nab\u001c8ESN\u001cwN\u001c8fGR,G\rF\u0002x\u0003;B\u0001\"a\u0018\u0002X\u0001\u0007\u0011\u0011M\u0001\u000ee\u0016lw\u000e^3BI\u0012\u0014Xm]:\u0011\u0007I\t\u0019'C\u0002\u0002fM\u0011!B\u00159d\u0003\u0012$'/Z:t\u0011\u001d\tI\u0007\u0001C!\u0003W\nAb\u001d;biV\u001cX\u000b\u001d3bi\u0016$ra^A7\u0003o\n)\n\u0003\u0005\u0002p\u0005\u001d\u0004\u0019AA9\u0003\u0019!\u0018m]6JIB\u0019A\"a\u001d\n\u0007\u0005UTB\u0001\u0003M_:<\u0007\u0002CA=\u0003O\u0002\r!a\u001f\u0002\u000bM$\u0018\r^3\u0011\t\u0005u\u0014q\u0012\b\u0005\u0003\u007f\nYI\u0004\u0003\u0002\u0002\u0006%e\u0002BAB\u0003\u000fs1!QAC\u0013\u0005I\u0011BA\u0004\t\u0013\t)a!C\u0002\u0002\u000e\u0012\t\u0011\u0002V1tWN#\u0018\r^3\n\t\u0005E\u00151\u0013\u0002\n)\u0006\u001c8n\u0015;bi\u0016T1!!$\u0005\u0011!\t9*a\u001aA\u0002\u0005e\u0015\u0001\u00023bi\u0006\u0004B!a'\u0002\"6\u0011\u0011Q\u0014\u0006\u0004\u0003?s\u0015a\u00018j_&!\u00111UAO\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\b\u0003O\u0003A\u0011CAU\u00031)\u00070\u001b;Fq\u0016\u001cW\u000f^8s)%9\u00181VAX\u0003g\u000bi\fC\u0004\u0002.\u0006\u0015\u0006\u0019A\u001d\u0002\t\r|G-\u001a\u0005\b\u0003c\u000b)\u000b1\u0001-\u0003\u0019\u0011X-Y:p]\"Q\u0011QWAS!\u0003\u0005\r!a.\u0002\u0013QD'o\\<bE2,\u0007cA \u0002:&\u0019\u00111X%\u0003\u0013QC'o\\<bE2,\u0007BCA`\u0003K\u0003\n\u00111\u0001\u0002B\u0006aan\u001c;jMf$%/\u001b<feB\u0019A\"a1\n\u0007\u0005\u0015WBA\u0004C_>dW-\u00198\t\u0013\u0005%\u0007!%A\u0005\u0012\u0005-\u0017AF3ySR,\u00050Z2vi>\u0014H\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u00055'\u0006BA\\\u0003\u001f\\#!!5\u0011\t\u0005M\u0017Q\\\u0007\u0003\u0003+TA!a6\u0002Z\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u00037l\u0011AC1o]>$\u0018\r^5p]&!\u0011q\\Ak\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\n\u0003G\u0004\u0011\u0013!C\t\u0003K\fa#\u001a=ji\u0016CXmY;u_J$C-\u001a4bk2$H\u0005N\u000b\u0003\u0003OTC!!1\u0002P\u001eA\u00111\u001e\u0002\t\u0002\u0011\ti/\u0001\u000fD_\u0006\u00148/Z$sC&tW\rZ#yK\u000e,Ho\u001c:CC\u000e\\WM\u001c3\u0011\u0007a\tyOB\u0004\u0002\u0005!\u0005A!!=\u0014\t\u0005=8b\u0007\u0005\b3\u0006=H\u0011AA{)\t\ti\u000f\u0003\u0005\u0002z\u0006=H\u0011BA~\u0003\r\u0011XO\u001c\u000b\u0010o\u0006u\u0018q B\u0001\u0005\u0007\u0011)A!\u0003\u0003\u0010!11&a>A\u00021Ba\u0001NA|\u0001\u0004a\u0003B\u0002\u001c\u0002x\u0002\u0007A\u0006\u0003\u00049\u0003o\u0004\r!\u000f\u0005\b\u0005\u000f\t9\u00101\u0001-\u0003\u0015\t\u0007\u000f]%e\u0011!\u0011Y!a>A\u0002\t5\u0011!C<pe.,'/\u0016:m!\u0011a\u0011Q\u0001\u0017\t\ru\n9\u00101\u0001?\u0011!\u0011\u0019\"a<\u0005\u0002\tU\u0011\u0001B7bS:$2a\u001eB\f\u0011!\u0011IB!\u0005A\u0002\tm\u0011\u0001B1sON\u0004B\u0001\u0004B\u000fY%\u0019!qD\u0007\u0003\u000b\u0005\u0013(/Y=\t\u0011\t\r\u0012q\u001eC\u0005\u0003o\t\u0011\u0003\u001d:j]R,6/Y4f\u0003:$W\t_5u\u0001"
)
public class CoarseGrainedExecutorBackend implements ThreadSafeRpcEndpoint, ExecutorBackend, Logging {
   private final RpcEnv rpcEnv;
   public final String org$apache$spark$executor$CoarseGrainedExecutorBackend$$driverUrl;
   public final String org$apache$spark$executor$CoarseGrainedExecutorBackend$$executorId;
   public final String org$apache$spark$executor$CoarseGrainedExecutorBackend$$hostname;
   public final int org$apache$spark$executor$CoarseGrainedExecutorBackend$$cores;
   public final Seq<URL> org$apache$spark$executor$CoarseGrainedExecutorBackend$$userClassPath;
   public final SparkEnv org$apache$spark$executor$CoarseGrainedExecutorBackend$$env;
   public final AtomicBoolean org$apache$spark$executor$CoarseGrainedExecutorBackend$$stopping;
   private Executor executor;
   private volatile Option<RpcEndpointRef> driver;
   private final SerializerInstance ser;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static void main(String[] var0) {
      CoarseGrainedExecutorBackend$.MODULE$.main(var0);
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

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return RpcEndpoint$class.receiveAndReply(this, context);
   }

   public void onError(Throwable cause) {
      RpcEndpoint$class.onError(this, cause);
   }

   public void onConnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onConnected(this, remoteAddress);
   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      RpcEndpoint$class.onNetworkError(this, cause, remoteAddress);
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

   public Executor executor() {
      return this.executor;
   }

   public void executor_$eq(Executor x$1) {
      this.executor = x$1;
   }

   public Option<RpcEndpointRef> driver() {
      return this.driver;
   }

   public void driver_$eq(Option<RpcEndpointRef> x$1) {
      this.driver = x$1;
   }

   public void onStart() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoarseGrainedExecutorBackend $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Connecting to driver: ").append((Object)this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$driverUrl).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.rpcEnv().asyncSetupEndpointRefByURI(this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$driverUrl).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoarseGrainedExecutorBackend $outer;

         public final Future<Object> apply(RpcEndpointRef ref) {
            this.$outer.driver_$eq(new Some(ref));
            return ref.<Object>ask(new CoarseGrainedClusterMessages.RegisterExecutor(this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$executorId, this.$outer.self(), this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$hostname, this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$cores, this.$outer.extractLogUrls()), ClassTag$.MODULE$.Boolean());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, ThreadUtils$.MODULE$.sameThread()).onComplete(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoarseGrainedExecutorBackend $outer;

         public final void apply(Try<Object> x0$1) {
            if (x0$1 instanceof Success) {
               BoxedUnit var3 = BoxedUnit.UNIT;
            } else {
               if (!(x0$1 instanceof Failure)) {
                  throw new MatchError(x0$1);
               }

               Failure var4 = (Failure)x0$1;
               Throwable e = var4.exception();
               this.$outer.exitExecutor(1, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot register with driver: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$driverUrl})), e, false);
               BoxedUnit var6 = BoxedUnit.UNIT;
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, ThreadUtils$.MODULE$.sameThread());
   }

   public Map<String, String> extractLogUrls() {
      String prefix = "SPARK_LOG_URL_";
      return (Map)scala.sys.package$.MODULE$.env().filterKeys(new Serializable(this, prefix) {
         public static final long serialVersionUID = 0L;
         private final String prefix$1;

         public final boolean apply(String x$1) {
            return x$1.startsWith(this.prefix$1);
         }

         public {
            this.prefix$1 = prefix$1;
         }
      }).map(new Serializable(this, prefix) {
         public static final long serialVersionUID = 0L;
         private final String prefix$1;

         public final Tuple2<String, String> apply(Tuple2<String, String> e) {
            return new Tuple2<String, String>(((String)e._1()).substring(this.prefix$1.length()).toLowerCase(Locale.ROOT), e._2());
         }

         public {
            this.prefix$1 = prefix$1;
         }
      }, Map$.MODULE$.canBuildFrom());
   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoarseGrainedExecutorBackend $outer;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var4;
            if (CoarseGrainedClusterMessages.RegisteredExecutor$.MODULE$.equals(x1)) {
               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Successfully registered with driver";
                  }
               });

               BoxedUnit var25;
               try {
                  this.$outer.executor_$eq(new Executor(this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$executorId, this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$hostname, this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$env, this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$userClassPath, false, Executor$.MODULE$.$lessinit$greater$default$6()));
                  var25 = BoxedUnit.UNIT;
               } catch (Throwable var23) {
                  label101: {
                     Option var7 = NonFatal$.MODULE$.unapply(var23);
                     if (var7.isEmpty()) {
                        throw var23;
                     }

                     Throwable e = (Throwable)var7.get();
                     this.$outer.exitExecutor(1, (new StringBuilder()).append((Object)"Unable to create executor due to ").append((Object)e.getMessage()).toString(), e, this.$outer.exitExecutor$default$4());
                     BoxedUnit var9 = BoxedUnit.UNIT;
                     var25 = BoxedUnit.UNIT;
                     break label101;
                  }
               }

               var4 = var25;
            } else if (x1 instanceof CoarseGrainedClusterMessages.RegisterExecutorFailed) {
               CoarseGrainedClusterMessages.RegisterExecutorFailed var10 = (CoarseGrainedClusterMessages.RegisterExecutorFailed)x1;
               String message = var10.message();
               this.$outer.exitExecutor(1, (new StringBuilder()).append((Object)"Slave registration failed: ").append((Object)message).toString(), this.$outer.exitExecutor$default$3(), this.$outer.exitExecutor$default$4());
               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof CoarseGrainedClusterMessages.LaunchTask) {
               CoarseGrainedClusterMessages.LaunchTask var12 = (CoarseGrainedClusterMessages.LaunchTask)x1;
               SerializableBuffer data = var12.data();
               BoxedUnit var10000;
               if (this.$outer.executor() == null) {
                  this.$outer.exitExecutor(1, "Received LaunchTask command but executor was null", this.$outer.exitExecutor$default$3(), this.$outer.exitExecutor$default$4());
                  var10000 = BoxedUnit.UNIT;
               } else {
                  TaskDescription taskDesc = TaskDescription$.MODULE$.decode(data.value());
                  this.$outer.logInfo(new Serializable(this, taskDesc) {
                     public static final long serialVersionUID = 0L;
                     private final TaskDescription taskDesc$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Got assigned task ").append((Object)BoxesRunTime.boxToLong(this.taskDesc$1.taskId())).toString();
                     }

                     public {
                        this.taskDesc$1 = taskDesc$1;
                     }
                  });
                  this.$outer.executor().launchTask(this.$outer, taskDesc);
                  var10000 = BoxedUnit.UNIT;
               }

               var4 = var10000;
            } else if (x1 instanceof CoarseGrainedClusterMessages.KillTask) {
               CoarseGrainedClusterMessages.KillTask var15 = (CoarseGrainedClusterMessages.KillTask)x1;
               long taskId = var15.taskId();
               boolean interruptThread = var15.interruptThread();
               String reason = var15.reason();
               BoxedUnit var24;
               if (this.$outer.executor() == null) {
                  this.$outer.exitExecutor(1, "Received KillTask command but executor was null", this.$outer.exitExecutor$default$3(), this.$outer.exitExecutor$default$4());
                  var24 = BoxedUnit.UNIT;
               } else {
                  this.$outer.executor().killTask(taskId, interruptThread, reason);
                  var24 = BoxedUnit.UNIT;
               }

               var4 = var24;
            } else if (CoarseGrainedClusterMessages.StopExecutor$.MODULE$.equals(x1)) {
               this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$stopping.set(true);
               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Driver commanded a shutdown";
                  }
               });
               this.$outer.self().send(CoarseGrainedClusterMessages.Shutdown$.MODULE$);
               var4 = BoxedUnit.UNIT;
            } else if (CoarseGrainedClusterMessages.Shutdown$.MODULE$.equals(x1)) {
               this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$stopping.set(true);
               (new Thread(this) {
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public void run() {
                     this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$anonfun$$$outer().executor().stop();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }).start();
               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof CoarseGrainedClusterMessages.UpdateDelegationTokens) {
               CoarseGrainedClusterMessages.UpdateDelegationTokens var20 = (CoarseGrainedClusterMessages.UpdateDelegationTokens)x1;
               byte[] tokenBytes = var20.tokens();
               this.$outer.logInfo(new Serializable(this, tokenBytes) {
                  public static final long serialVersionUID = 0L;
                  private final byte[] tokenBytes$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received tokens of ", " bytes"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.tokenBytes$1.length)}));
                  }

                  public {
                     this.tokenBytes$1 = tokenBytes$1;
                  }
               });
               SparkHadoopUtil$.MODULE$.get().addDelegationTokens(tokenBytes, this.$outer.org$apache$spark$executor$CoarseGrainedExecutorBackend$$env.conf());
               var4 = BoxedUnit.UNIT;
            } else {
               var4 = var2.apply(x1);
            }

            return (B1)var4;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (CoarseGrainedClusterMessages.RegisteredExecutor$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof CoarseGrainedClusterMessages.RegisterExecutorFailed) {
               var3 = true;
            } else if (x1 instanceof CoarseGrainedClusterMessages.LaunchTask) {
               var3 = true;
            } else if (x1 instanceof CoarseGrainedClusterMessages.KillTask) {
               var3 = true;
            } else if (CoarseGrainedClusterMessages.StopExecutor$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (CoarseGrainedClusterMessages.Shutdown$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof CoarseGrainedClusterMessages.UpdateDelegationTokens) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         // $FF: synthetic method
         public CoarseGrainedExecutorBackend org$apache$spark$executor$CoarseGrainedExecutorBackend$$anonfun$$$outer() {
            return this.$outer;
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

   public void onDisconnected(RpcAddress remoteAddress) {
      if (this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$stopping.get()) {
         this.logInfo(new Serializable(this, remoteAddress) {
            public static final long serialVersionUID = 0L;
            private final RpcAddress remoteAddress$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver from ", " disconnected during shutdown"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddress$1}));
            }

            public {
               this.remoteAddress$1 = remoteAddress$1;
            }
         });
      } else if (this.driver().exists(new Serializable(this, remoteAddress) {
         public static final long serialVersionUID = 0L;
         private final RpcAddress remoteAddress$1;

         public final boolean apply(RpcEndpointRef x$2) {
            boolean var3;
            label23: {
               RpcAddress var10000 = x$2.address();
               RpcAddress var2 = this.remoteAddress$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }

         public {
            this.remoteAddress$1 = remoteAddress$1;
         }
      })) {
         this.exitExecutor(1, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " disassociated! Shutting down."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{remoteAddress})), (Throwable)null, false);
      } else {
         this.logWarning(new Serializable(this, remoteAddress) {
            public static final long serialVersionUID = 0L;
            private final RpcAddress remoteAddress$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"An unknown (", ") driver disconnected."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddress$1}));
            }

            public {
               this.remoteAddress$1 = remoteAddress$1;
            }
         });
      }

   }

   public void statusUpdate(long taskId, Enumeration.Value state, ByteBuffer data) {
      CoarseGrainedClusterMessages.StatusUpdate msg = CoarseGrainedClusterMessages.StatusUpdate$.MODULE$.apply(this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$executorId, taskId, state, data);
      Option var6 = this.driver();
      if (var6 instanceof Some) {
         Some var7 = (Some)var6;
         RpcEndpointRef driverRef = (RpcEndpointRef)var7.x();
         driverRef.send(msg);
         BoxedUnit var9 = BoxedUnit.UNIT;
      } else {
         if (!None$.MODULE$.equals(var6)) {
            throw new MatchError(var6);
         }

         this.logWarning(new Serializable(this, msg) {
            public static final long serialVersionUID = 0L;
            private final CoarseGrainedClusterMessages.StatusUpdate msg$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Drop ", " because has not yet connected to driver"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.msg$1}));
            }

            public {
               this.msg$1 = msg$1;
            }
         });
         BoxedUnit var10 = BoxedUnit.UNIT;
      }

   }

   public void exitExecutor(int code, String reason, Throwable throwable, boolean notifyDriver) {
      String message = (new StringBuilder()).append((Object)"Executor self-exiting due to : ").append((Object)reason).toString();
      if (throwable == null) {
         this.logError(new Serializable(this, message) {
            public static final long serialVersionUID = 0L;
            private final String message$1;

            public final String apply() {
               return this.message$1;
            }

            public {
               this.message$1 = message$1;
            }
         });
      } else {
         this.logError(new Serializable(this, message) {
            public static final long serialVersionUID = 0L;
            private final String message$1;

            public final String apply() {
               return this.message$1;
            }

            public {
               this.message$1 = message$1;
            }
         }, throwable);
      }

      if (notifyDriver && this.driver().nonEmpty()) {
         ((RpcEndpointRef)this.driver().get()).send(new CoarseGrainedClusterMessages.RemoveExecutor(this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$executorId, new ExecutorLossReason(reason)));
      }

      System.exit(code);
   }

   public Throwable exitExecutor$default$3() {
      return null;
   }

   public boolean exitExecutor$default$4() {
      return true;
   }

   public CoarseGrainedExecutorBackend(RpcEnv rpcEnv, String driverUrl, String executorId, String hostname, int cores, Seq<URL> userClassPath, SparkEnv env) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$driverUrl = driverUrl;
      this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$executorId = executorId;
      this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$hostname = hostname;
      this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$cores = cores;
      this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$userClassPath = userClassPath;
      this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$env = env;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.org$apache$spark$executor$CoarseGrainedExecutorBackend$$stopping = new AtomicBoolean(false);
      this.executor = null;
      this.driver = None$.MODULE$;
      this.ser = env.closureSerializer().newInstance();
   }
}
