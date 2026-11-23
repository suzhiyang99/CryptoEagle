package org.apache.spark.scheduler.local;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.executor.ExecutorBackend;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.launcher.LauncherBackend;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkAppHandle.State;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.scheduler.LiveListenerBus;
import org.apache.spark.scheduler.SchedulerBackend;
import org.apache.spark.scheduler.SchedulerBackend$class;
import org.apache.spark.scheduler.SparkListenerExecutorAdded;
import org.apache.spark.scheduler.TaskSchedulerImpl;
import org.apache.spark.scheduler.cluster.ExecutorInfo;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Map;
import scala.collection.mutable.ArrayOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055f!B\u0001\u0003\u0001\u0019a!!\u0006'pG\u0006d7k\u00195fIVdWM\u001d\"bG.,g\u000e\u001a\u0006\u0003\u0007\u0011\tQ\u0001\\8dC2T!!\u0002\u0004\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u000b\u0001i1cF\u000f\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!R#D\u0001\u0005\u0013\t1BA\u0001\tTG\",G-\u001e7fe\n\u000b7m[3oIB\u0011\u0001dG\u0007\u00023)\u0011!DB\u0001\tKb,7-\u001e;pe&\u0011A$\u0007\u0002\u0010\u000bb,7-\u001e;pe\n\u000b7m[3oIB\u0011a$I\u0007\u0002?)\u0011\u0001EB\u0001\tS:$XM\u001d8bY&\u0011!e\b\u0002\b\u0019><w-\u001b8h\u0011!!\u0003A!A!\u0002\u00131\u0013\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0002(Q5\ta!\u0003\u0002*\r\tI1\u000b]1sW\u000e{gN\u001a\u0005\t\u000b\u0001\u0011\t\u0011)A\u0005WA\u0011A\u0003L\u0005\u0003[\u0011\u0011\u0011\u0003V1tWN\u001b\u0007.\u001a3vY\u0016\u0014\u0018*\u001c9m\u0011!y\u0003A!b\u0001\n\u0003\u0001\u0014A\u0003;pi\u0006d7i\u001c:fgV\t\u0011\u0007\u0005\u0002\u000fe%\u00111g\u0004\u0002\u0004\u0013:$\b\u0002C\u001b\u0001\u0005\u0003\u0005\u000b\u0011B\u0019\u0002\u0017Q|G/\u00197D_J,7\u000f\t\u0005\u0006o\u0001!\t\u0001O\u0001\u0007y%t\u0017\u000e\u001e \u0015\teZD(\u0010\t\u0003u\u0001i\u0011A\u0001\u0005\u0006IY\u0002\rA\n\u0005\u0006\u000bY\u0002\ra\u000b\u0005\u0006_Y\u0002\r!\r\u0005\b\u007f\u0001\u0011\r\u0011\"\u0003A\u0003\u0015\t\u0007\u000f]%e+\u0005\t\u0005C\u0001\"H\u001b\u0005\u0019%B\u0001#F\u0003\u0011a\u0017M\\4\u000b\u0003\u0019\u000bAA[1wC&\u0011\u0001j\u0011\u0002\u0007'R\u0014\u0018N\\4\t\r)\u0003\u0001\u0015!\u0003B\u0003\u0019\t\u0007\u000f]%eA!9A\n\u0001a\u0001\n\u0013i\u0015!\u00047pG\u0006dWI\u001c3q_&tG/F\u0001O!\ty%+D\u0001Q\u0015\t\tf!A\u0002sa\u000eL!a\u0015)\u0003\u001dI\u00038-\u00128ea>Lg\u000e\u001e*fM\"9Q\u000b\u0001a\u0001\n\u00131\u0016!\u00057pG\u0006dWI\u001c3q_&tGo\u0018\u0013fcR\u0011qK\u0017\t\u0003\u001daK!!W\b\u0003\tUs\u0017\u000e\u001e\u0005\b7R\u000b\t\u00111\u0001O\u0003\rAH%\r\u0005\u0007;\u0002\u0001\u000b\u0015\u0002(\u0002\u001d1|7-\u00197F]\u0012\u0004x.\u001b8uA!9q\f\u0001b\u0001\n\u0013\u0001\u0017!D;tKJ\u001cE.Y:t!\u0006$\b.F\u0001b!\r\u0011'.\u001c\b\u0003G\"t!\u0001Z4\u000e\u0003\u0015T!AZ\u0013\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0012BA5\u0010\u0003\u001d\u0001\u0018mY6bO\u0016L!a\u001b7\u0003\u0007M+\u0017O\u0003\u0002j\u001fA\u0011a.]\u0007\u0002_*\u0011\u0001/R\u0001\u0004]\u0016$\u0018B\u0001:p\u0005\r)&\u000b\u0014\u0005\u0007i\u0002\u0001\u000b\u0011B1\u0002\u001dU\u001cXM]\"mCN\u001c\b+\u0019;iA!9a\u000f\u0001b\u0001\n\u00139\u0018a\u00037jgR,g.\u001a:CkN,\u0012\u0001\u001f\t\u0003)eL!A\u001f\u0003\u0003\u001f1Kg/\u001a'jgR,g.\u001a:CkNDa\u0001 \u0001!\u0002\u0013A\u0018\u0001\u00047jgR,g.\u001a:CkN\u0004\u0003b\u0002@\u0001\u0005\u0004%Ia`\u0001\u0010Y\u0006,hn\u00195fe\n\u000b7m[3oIV\u0011\u0011\u0011\u0001\t\u0005\u0003\u0007\tI!\u0004\u0002\u0002\u0006)\u0019\u0011q\u0001\u0004\u0002\u00111\fWO\\2iKJLA!a\u0003\u0002\u0006\tyA*Y;oG\",'OQ1dW\u0016tG\r\u0003\u0005\u0002\u0010\u0001\u0001\u000b\u0011BA\u0001\u0003Aa\u0017-\u001e8dQ\u0016\u0014()Y2lK:$\u0007\u0005C\u0004\u0002\u0014\u0001!\t!!\u0006\u0002!\u001d,G/V:fe\u000ec\u0017m]:qCRDGcA1\u0002\u0018!1A%!\u0005A\u0002\u0019Bq!a\u0007\u0001\t\u0003\ni\"A\u0003ti\u0006\u0014H\u000fF\u0001X\u0011\u001d\t\t\u0003\u0001C!\u0003;\tAa\u001d;pa\"9\u0011Q\u0005\u0001\u0005B\u0005u\u0011\u0001\u0004:fm&4Xm\u00144gKJ\u001c\bbBA\u0015\u0001\u0011\u0005\u00131F\u0001\u0013I\u00164\u0017-\u001e7u!\u0006\u0014\u0018\r\u001c7fY&\u001cX\u000eF\u00012\u0011\u001d\ty\u0003\u0001C!\u0003c\t\u0001b[5mYR\u000b7o\u001b\u000b\n/\u0006M\u0012QHA'\u0003/B\u0001\"!\u000e\u0002.\u0001\u0007\u0011qG\u0001\u0007i\u0006\u001c8.\u00133\u0011\u00079\tI$C\u0002\u0002<=\u0011A\u0001T8oO\"A\u0011qHA\u0017\u0001\u0004\t\t%\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u0004B!a\u0011\u0002J9\u0019a\"!\u0012\n\u0007\u0005\u001ds\"\u0001\u0004Qe\u0016$WMZ\u0005\u0004\u0011\u0006-#bAA$\u001f!A\u0011qJA\u0017\u0001\u0004\t\t&A\bj]R,'O];qiRC'/Z1e!\rq\u00111K\u0005\u0004\u0003+z!a\u0002\"p_2,\u0017M\u001c\u0005\t\u00033\ni\u00031\u0001\u0002B\u00051!/Z1t_:Dq!!\u0018\u0001\t\u0003\ny&\u0001\u0007ti\u0006$Xo]+qI\u0006$X\rF\u0004X\u0003C\n\u0019'!!\t\u0011\u0005U\u00121\fa\u0001\u0003oA\u0001\"!\u001a\u0002\\\u0001\u0007\u0011qM\u0001\u0006gR\fG/\u001a\t\u0005\u0003S\nYH\u0004\u0003\u0002l\u0005]d\u0002BA7\u0003krA!a\u001c\u0002t9\u0019A-!\u001d\n\u0003-I!!\u0003\u0006\n\u0005\u001dA\u0011bAA=\r\u0005IA+Y:l'R\fG/Z\u0005\u0005\u0003{\nyHA\u0005UCN\\7\u000b^1uK*\u0019\u0011\u0011\u0010\u0004\t\u0011\u0005\r\u00151\fa\u0001\u0003\u000b\u000bab]3sS\u0006d\u0017N_3e\t\u0006$\u0018\r\u0005\u0003\u0002\b\u00065UBAAE\u0015\r\tY)R\u0001\u0004]&|\u0017\u0002BAH\u0003\u0013\u0013!BQ=uK\n+hMZ3s\u0011\u001d\t\u0019\n\u0001C!\u0003+\u000bQ\"\u00199qY&\u001c\u0017\r^5p]&#GCAA!\u0011\u001d\t\t\u0003\u0001C\u0005\u00033#2aVAN\u0011!\ti*a&A\u0002\u0005}\u0015A\u00034j]\u0006d7\u000b^1uKB!\u0011\u0011UAT\u001d\u0011\t\u0019!a)\n\t\u0005\u0015\u0016QA\u0001\u000f'B\f'o[!qa\"\u000bg\u000e\u001a7f\u0013\u0011\tI+a+\u0003\u000bM#\u0018\r^3\u000b\t\u0005\u0015\u0016Q\u0001"
)
public class LocalSchedulerBackend implements SchedulerBackend, ExecutorBackend, Logging {
   public final SparkConf org$apache$spark$scheduler$local$LocalSchedulerBackend$$conf;
   private final TaskSchedulerImpl scheduler;
   private final int totalCores;
   private final String appId;
   private RpcEndpointRef localEndpoint;
   private final Seq<URL> userClassPath;
   private final LiveListenerBus listenerBus;
   private final LauncherBackend launcherBackend;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private final String org$apache$spark$scheduler$SchedulerBackend$$appId;

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

   public String org$apache$spark$scheduler$SchedulerBackend$$appId() {
      return this.org$apache$spark$scheduler$SchedulerBackend$$appId;
   }

   public void org$apache$spark$scheduler$SchedulerBackend$_setter_$org$apache$spark$scheduler$SchedulerBackend$$appId_$eq(String x$1) {
      this.org$apache$spark$scheduler$SchedulerBackend$$appId = x$1;
   }

   public boolean isReady() {
      return SchedulerBackend$class.isReady(this);
   }

   public Option<String> applicationAttemptId() {
      return SchedulerBackend$class.applicationAttemptId(this);
   }

   public Option<Map<String, String>> getDriverLogUrls() {
      return SchedulerBackend$class.getDriverLogUrls(this);
   }

   public int totalCores() {
      return this.totalCores;
   }

   private String appId() {
      return this.appId;
   }

   private RpcEndpointRef localEndpoint() {
      return this.localEndpoint;
   }

   private void localEndpoint_$eq(RpcEndpointRef x$1) {
      this.localEndpoint = x$1;
   }

   private Seq<URL> userClassPath() {
      return this.userClassPath;
   }

   private LiveListenerBus listenerBus() {
      return this.listenerBus;
   }

   private LauncherBackend launcherBackend() {
      return this.launcherBackend;
   }

   public Seq<URL> getUserClasspath(SparkConf conf) {
      Option userClassPathStr = conf.getOption("spark.executor.extraClassPath");
      return (Seq)((TraversableLike)Option$.MODULE$.option2Iterable(userClassPathStr.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String x$1) {
            return x$1.split(File.pathSeparator);
         }
      })).toSeq().flatten(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<String> apply(String[] xs) {
            return Predef$.MODULE$.<String>refArrayOps((Object[])xs);
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final URL apply(String x$2) {
            return (new File(x$2)).toURI().toURL();
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public void start() {
      RpcEnv rpcEnv = SparkEnv$.MODULE$.get().rpcEnv();
      LocalEndpoint executorEndpoint = new LocalEndpoint(rpcEnv, this.userClassPath(), this.scheduler, this, this.totalCores());
      this.localEndpoint_$eq(rpcEnv.setupEndpoint("LocalSchedulerBackendEndpoint", executorEndpoint));
      this.listenerBus().post(new SparkListenerExecutorAdded(System.currentTimeMillis(), executorEndpoint.localExecutorId(), new ExecutorInfo(executorEndpoint.localExecutorHostname(), this.totalCores(), Predef$.MODULE$.Map().empty())));
      this.launcherBackend().setAppId(this.appId());
      this.launcherBackend().setState(State.RUNNING);
   }

   public void stop() {
      this.org$apache$spark$scheduler$local$LocalSchedulerBackend$$stop(State.FINISHED);
   }

   public void reviveOffers() {
      this.localEndpoint().send(ReviveOffers$.MODULE$);
   }

   public int defaultParallelism() {
      return this.scheduler.conf().getInt("spark.default.parallelism", this.totalCores());
   }

   public void killTask(long taskId, String executorId, boolean interruptThread, String reason) {
      this.localEndpoint().send(new KillTask(taskId, interruptThread, reason));
   }

   public void statusUpdate(long taskId, Enumeration.Value state, ByteBuffer serializedData) {
      this.localEndpoint().send(new StatusUpdate(taskId, state, serializedData));
   }

   public String applicationId() {
      return this.appId();
   }

   public void org$apache$spark$scheduler$local$LocalSchedulerBackend$$stop(SparkAppHandle.State finalState) {
      this.localEndpoint().ask(StopExecutor$.MODULE$, ClassTag$.MODULE$.Nothing());

      try {
         this.launcherBackend().setState(finalState);
      } finally {
         this.launcherBackend().close();
      }

   }

   public LocalSchedulerBackend(SparkConf conf, TaskSchedulerImpl scheduler, int totalCores) {
      this.org$apache$spark$scheduler$local$LocalSchedulerBackend$$conf = conf;
      this.scheduler = scheduler;
      this.totalCores = totalCores;
      SchedulerBackend$class.$init$(this);
      Logging$class.$init$(this);
      this.appId = (new StringBuilder()).append((Object)"local-").append((Object)BoxesRunTime.boxToLong(System.currentTimeMillis())).toString();
      this.localEndpoint = null;
      this.userClassPath = this.getUserClasspath(conf);
      this.listenerBus = scheduler.sc().listenerBus();
      this.launcherBackend = new LauncherBackend(this) {
         // $FF: synthetic field
         private final LocalSchedulerBackend $outer;

         public SparkConf conf() {
            return this.$outer.org$apache$spark$scheduler$local$LocalSchedulerBackend$$conf;
         }

         public void onStopRequest() {
            this.$outer.org$apache$spark$scheduler$local$LocalSchedulerBackend$$stop(State.KILLED);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.launcherBackend().connect();
   }
}
