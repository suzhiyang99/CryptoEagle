package org.apache.spark.scheduler.cluster;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkConf$;
import org.apache.spark.SparkContext;
import org.apache.spark.deploy.ApplicationDescription;
import org.apache.spark.deploy.ApplicationDescription$;
import org.apache.spark.deploy.Command;
import org.apache.spark.deploy.client.StandaloneAppClient;
import org.apache.spark.deploy.client.StandaloneAppClientListener;
import org.apache.spark.launcher.LauncherBackend;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkAppHandle.State;
import org.apache.spark.rpc.RpcEndpointAddress$;
import org.apache.spark.scheduler.ExecutorExited;
import org.apache.spark.scheduler.ExecutorLossReason;
import org.apache.spark.scheduler.SchedulerBackend$class;
import org.apache.spark.scheduler.SlaveLost;
import org.apache.spark.scheduler.TaskSchedulerImpl;
import org.apache.spark.ui.SparkUI;
import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.sys.package$;

@ScalaSignature(
   bytes = "\u0006\u0001\t-a!B\u0001\u0003\u0001\u0019a!AG*uC:$\u0017\r\\8oKN\u001b\u0007.\u001a3vY\u0016\u0014()Y2lK:$'BA\u0002\u0005\u0003\u001d\u0019G.^:uKJT!!\u0002\u0004\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\t\u0001i\u0011#\u0007\t\u0003\u001d=i\u0011AA\u0005\u0003!\t\u0011QdQ8beN,wI]1j]\u0016$7k\u00195fIVdWM\u001d\"bG.,g\u000e\u001a\t\u0003%]i\u0011a\u0005\u0006\u0003)U\taa\u00197jK:$(B\u0001\f\u0007\u0003\u0019!W\r\u001d7ps&\u0011\u0001d\u0005\u0002\u001c'R\fg\u000eZ1m_:,\u0017\t\u001d9DY&,g\u000e\u001e'jgR,g.\u001a:\u0011\u0005iiR\"A\u000e\u000b\u0005q1\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005yY\"a\u0002'pO\u001eLgn\u001a\u0005\t\u000b\u0001\u0011\t\u0011)A\u0005C\r\u0001\u0001C\u0001\u0012$\u001b\u0005!\u0011B\u0001\u0013\u0005\u0005E!\u0016m]6TG\",G-\u001e7fe&k\u0007\u000f\u001c\u0005\tM\u0001\u0011\t\u0011)A\u0005O\u0005\u00111o\u0019\t\u0003Q%j\u0011AB\u0005\u0003U\u0019\u0011Ab\u00159be.\u001cuN\u001c;fqRD\u0001\u0002\f\u0001\u0003\u0002\u0003\u0006I!L\u0001\b[\u0006\u001cH/\u001a:t!\rq\u0013gM\u0007\u0002_)\t\u0001'A\u0003tG\u0006d\u0017-\u0003\u00023_\t)\u0011I\u001d:bsB\u0011Ag\u000e\b\u0003]UJ!AN\u0018\u0002\rA\u0013X\rZ3g\u0013\tA\u0014H\u0001\u0004TiJLgn\u001a\u0006\u0003m=BQa\u000f\u0001\u0005\u0002q\na\u0001P5oSRtD\u0003B\u001f?\u007f\u0001\u0003\"A\u0004\u0001\t\u000b\u0015Q\u0004\u0019A\u0011\t\u000b\u0019R\u0004\u0019A\u0014\t\u000b1R\u0004\u0019A\u0017\t\u000fQ\u0001\u0001\u0019!C\u0005\u0005V\t1\t\u0005\u0002\u0013\t&\u0011Qi\u0005\u0002\u0014'R\fg\u000eZ1m_:,\u0017\t\u001d9DY&,g\u000e\u001e\u0005\b\u000f\u0002\u0001\r\u0011\"\u0003I\u0003)\u0019G.[3oi~#S-\u001d\u000b\u0003\u00132\u0003\"A\f&\n\u0005-{#\u0001B+oSRDq!\u0014$\u0002\u0002\u0003\u00071)A\u0002yIEBaa\u0014\u0001!B\u0013\u0019\u0015aB2mS\u0016tG\u000f\t\u0005\b#\u0002\u0011\r\u0011\"\u0003S\u0003!\u0019Ho\u001c9qS:<W#A*\u0011\u0005QkV\"A+\u000b\u0005Y;\u0016AB1u_6L7M\u0003\u0002Y3\u0006Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005i[\u0016\u0001B;uS2T\u0011\u0001X\u0001\u0005U\u00064\u0018-\u0003\u0002_+\ni\u0011\t^8nS\u000e\u0014un\u001c7fC:Da\u0001\u0019\u0001!\u0002\u0013\u0019\u0016!C:u_B\u0004\u0018N\\4!\u0011\u001d\u0011\u0007A1A\u0005\n\r\fq\u0002\\1v]\u000eDWM\u001d\"bG.,g\u000eZ\u000b\u0002IB\u0011Q\r[\u0007\u0002M*\u0011qMB\u0001\tY\u0006,hn\u00195fe&\u0011\u0011N\u001a\u0002\u0010\u0019\u0006,hn\u00195fe\n\u000b7m[3oI\"11\u000e\u0001Q\u0001\n\u0011\f\u0001\u0003\\1v]\u000eDWM\u001d\"bG.,g\u000e\u001a\u0011\t\u00135\u0004\u0001\u0019!a\u0001\n\u0003q\u0017\u0001E:ikR$wn\u001e8DC2d'-Y2l+\u0005y\u0007\u0003\u0002\u0018q{%K!!]\u0018\u0003\u0013\u0019+hn\u0019;j_:\f\u0004\"C:\u0001\u0001\u0004\u0005\r\u0011\"\u0001u\u0003Q\u0019\b.\u001e;e_^t7)\u00197mE\u0006\u001c7n\u0018\u0013fcR\u0011\u0011*\u001e\u0005\b\u001bJ\f\t\u00111\u0001p\u0011\u00199\b\u0001)Q\u0005_\u0006\t2\u000f[;uI><hnQ1mY\n\f7m\u001b\u0011)\u0005YL\bC\u0001\u0018{\u0013\tYxF\u0001\u0005w_2\fG/\u001b7f\u0011%i\b\u00011AA\u0002\u0013%a0A\u0003baBLE-F\u00014\u0011-\t\t\u0001\u0001a\u0001\u0002\u0004%I!a\u0001\u0002\u0013\u0005\u0004\b/\u00133`I\u0015\fHcA%\u0002\u0006!9Qj`A\u0001\u0002\u0004\u0019\u0004bBA\u0005\u0001\u0001\u0006KaM\u0001\u0007CB\u0004\u0018\n\u001a\u0011)\u0007\u0005\u001d\u0011\u0010C\u0005\u0002\u0010\u0001\u0011\r\u0011\"\u0003\u0002\u0012\u0005\u0019\"/Z4jgR\u0014\u0018\r^5p]\n\u000b'O]5feV\u0011\u00111\u0003\t\u0005\u0003+\t9\"D\u0001X\u0013\r\tIb\u0016\u0002\n'\u0016l\u0017\r\u001d5pe\u0016D\u0001\"!\b\u0001A\u0003%\u00111C\u0001\u0015e\u0016<\u0017n\u001d;sCRLwN\u001c\"beJLWM\u001d\u0011\t\u0013\u0005\u0005\u0002A1A\u0005\n\u0005\r\u0012\u0001C7bq\u000e{'/Z:\u0016\u0005\u0005\u0015\u0002#\u0002\u0018\u0002(\u0005-\u0012bAA\u0015_\t1q\n\u001d;j_:\u00042ALA\u0017\u0013\r\tyc\f\u0002\u0004\u0013:$\b\u0002CA\u001a\u0001\u0001\u0006I!!\n\u0002\u00135\f\u0007pQ8sKN\u0004\u0003\"CA\u001c\u0001\t\u0007I\u0011BA\u001d\u0003I!x\u000e^1m\u000bb\u0004Xm\u0019;fI\u000e{'/Z:\u0016\u0005\u0005-\u0002\u0002CA\u001f\u0001\u0001\u0006I!a\u000b\u0002'Q|G/\u00197FqB,7\r^3e\u0007>\u0014Xm\u001d\u0011\t\u000f\u0005\u0005\u0003\u0001\"\u0011\u0002D\u0005)1\u000f^1siR\t\u0011\nC\u0004\u0002H\u0001!\t%a\u0011\u0002\tM$x\u000e\u001d\u0005\b\u0003\u0017\u0002A\u0011IA'\u0003%\u0019wN\u001c8fGR,G\rF\u0002J\u0003\u001fBa!`A%\u0001\u0004\u0019\u0004bBA*\u0001\u0011\u0005\u00131I\u0001\rI&\u001c8m\u001c8oK\u000e$X\r\u001a\u0005\b\u0003/\u0002A\u0011IA-\u0003\u0011!W-\u00193\u0015\u0007%\u000bY\u0006C\u0004\u0002^\u0005U\u0003\u0019A\u001a\u0002\rI,\u0017m]8o\u0011\u001d\t\t\u0007\u0001C!\u0003G\nQ\"\u001a=fGV$xN]!eI\u0016$GcC%\u0002f\u0005%\u0014QNA9\u0003kBq!a\u001a\u0002`\u0001\u00071'\u0001\u0004gk2d\u0017\n\u001a\u0005\b\u0003W\ny\u00061\u00014\u0003!9xN]6fe&#\u0007bBA8\u0003?\u0002\raM\u0001\tQ>\u001cH\u000fU8si\"A\u00111OA0\u0001\u0004\tY#A\u0003d_J,7\u000f\u0003\u0005\u0002x\u0005}\u0003\u0019AA\u0016\u0003\u0019iW-\\8ss\"9\u00111\u0010\u0001\u0005B\u0005u\u0014aD3yK\u000e,Ho\u001c:SK6|g/\u001a3\u0015\u0013%\u000by(!!\u0002\u0006\u0006%\u0005bBA4\u0003s\u0002\ra\r\u0005\b\u0003\u0007\u000bI\b1\u00014\u0003\u001diWm]:bO\u0016D\u0001\"a\"\u0002z\u0001\u0007\u0011QE\u0001\u000bKbLGo\u0015;biV\u001c\b\u0002CAF\u0003s\u0002\r!!$\u0002\u0015]|'o[3s\u0019>\u001cH\u000fE\u0002/\u0003\u001fK1!!%0\u0005\u001d\u0011un\u001c7fC:Dq!!&\u0001\t\u0003\n9*A\u0007x_J\\WM\u001d*f[>4X\r\u001a\u000b\b\u0013\u0006e\u00151TAP\u0011\u001d\tY'a%A\u0002MBq!!(\u0002\u0014\u0002\u00071'\u0001\u0003i_N$\bbBAB\u0003'\u0003\ra\r\u0005\b\u0003G\u0003A\u0011IAS\u0003u\u0019XO\u001a4jG&,g\u000e\u001e*fg>,(oY3t%\u0016<\u0017n\u001d;fe\u0016$GCAAG\u0011\u001d\tI\u000b\u0001C!\u0003W\u000bQ\"\u00199qY&\u001c\u0017\r^5p]&#G#A\u001a\t\u000f\u0005=\u0006\u0001\"\u0015\u00022\u00069Bm\u001c*fcV,7\u000f\u001e+pi\u0006dW\t_3dkR|'o\u001d\u000b\u0005\u0003g\u000bi\f\u0005\u0004\u00026\u0006e\u0016QR\u0007\u0003\u0003oS!\u0001W\u0018\n\t\u0005m\u0016q\u0017\u0002\u0007\rV$XO]3\t\u0011\u0005}\u0016Q\u0016a\u0001\u0003W\taB]3rk\u0016\u001cH/\u001a3U_R\fG\u000eC\u0004\u0002D\u0002!\t&!2\u0002\u001f\u0011|7*\u001b7m\u000bb,7-\u001e;peN$B!a-\u0002H\"A\u0011\u0011ZAa\u0001\u0004\tY-A\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u001c\b#BAg\u0003;\u001cd\u0002BAh\u00033tA!!5\u0002X6\u0011\u00111\u001b\u0006\u0004\u0003+\u0004\u0013A\u0002\u001fs_>$h(C\u00011\u0013\r\tYnL\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\ty.!9\u0003\u0007M+\u0017OC\u0002\u0002\\>Bq!!:\u0001\t\u0013\t\u0019%A\nxC&$hi\u001c:SK\u001eL7\u000f\u001e:bi&|g\u000eC\u0004\u0002j\u0002!I!a\u0011\u0002\u001b9|G/\u001b4z\u0007>tG/\u001a=u\u0011\u001d\t9\u0005\u0001C\u0005\u0003[$2!SAx\u0011!\t\t0a;A\u0002\u0005M\u0018A\u00034j]\u0006d7\u000b^1uKB!\u0011Q_A~\u001d\r)\u0017q_\u0005\u0004\u0003s4\u0017AD*qCJ\\\u0017\t\u001d9IC:$G.Z\u0005\u0005\u0003{\fyPA\u0003Ti\u0006$XMC\u0002\u0002z\u001aDaBa\u0001\u0001!\u0003\r\t\u0011!C\u0005\u0003W\u0013)!A\ntkB,'\u000fJ1qa2L7-\u0019;j_:LE-\u0003\u0003\u0002*\n\u001d\u0011b\u0001B\u0005\t\t\u00012k\u00195fIVdWM\u001d\"bG.,g\u000e\u001a"
)
public class StandaloneSchedulerBackend extends CoarseGrainedSchedulerBackend implements StandaloneAppClientListener {
   private final TaskSchedulerImpl scheduler;
   public final SparkContext org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc;
   private final String[] masters;
   private StandaloneAppClient client;
   private final AtomicBoolean stopping;
   private final LauncherBackend launcherBackend;
   private volatile Function1<StandaloneSchedulerBackend, BoxedUnit> shutdownCallback;
   private volatile String appId;
   private final Semaphore registrationBarrier;
   private final Option<Object> maxCores;
   private final int totalExpectedCores;

   // $FF: synthetic method
   public String org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$super$applicationId() {
      return SchedulerBackend$class.applicationId(this);
   }

   private StandaloneAppClient client() {
      return this.client;
   }

   private void client_$eq(StandaloneAppClient x$1) {
      this.client = x$1;
   }

   private AtomicBoolean stopping() {
      return this.stopping;
   }

   private LauncherBackend launcherBackend() {
      return this.launcherBackend;
   }

   public Function1<StandaloneSchedulerBackend, BoxedUnit> shutdownCallback() {
      return this.shutdownCallback;
   }

   public void shutdownCallback_$eq(Function1<StandaloneSchedulerBackend, BoxedUnit> x$1) {
      this.shutdownCallback = x$1;
   }

   private String appId() {
      return this.appId;
   }

   private void appId_$eq(String x$1) {
      this.appId = x$1;
   }

   private Semaphore registrationBarrier() {
      return this.registrationBarrier;
   }

   private Option<Object> maxCores() {
      return this.maxCores;
   }

   private int totalExpectedCores() {
      return this.totalExpectedCores;
   }

   public void start() {
      label24: {
         super.start();
         String var10000 = this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.deployMode();
         String var1 = "client";
         if (var10000 == null) {
            if (var1 != null) {
               break label24;
            }
         } else if (!var10000.equals(var1)) {
            break label24;
         }

         this.launcherBackend().connect();
      }

      String driverUrl = RpcEndpointAddress$.MODULE$.apply(this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.conf().get("spark.driver.host"), (new StringOps(Predef$.MODULE$.augmentString(this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.conf().get("spark.driver.port")))).toInt(), CoarseGrainedSchedulerBackend$.MODULE$.ENDPOINT_NAME()).toString();
      Seq args = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"--driver-url", driverUrl, "--executor-id", "{{EXECUTOR_ID}}", "--hostname", "{{HOSTNAME}}", "--cores", "{{CORES}}", "--app-id", "{{APP_ID}}", "--worker-url", "{{WORKER_URL}}"})));
      Seq extraJavaOpts = (Seq)this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.conf().getOption("spark.executor.extraJavaOptions").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<String> apply(String s) {
            return Utils$.MODULE$.splitCommandString(s);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      Seq classPathEntries = (Seq)this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.conf().getOption("spark.executor.extraClassPath").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<String> apply(String x$2) {
            return Predef$.MODULE$.refArrayOps((Object[])x$2.split(File.pathSeparator)).toSeq();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      });
      Seq libraryPathEntries = (Seq)this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.conf().getOption("spark.executor.extraLibraryPath").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<String> apply(String x$3) {
            return Predef$.MODULE$.refArrayOps((Object[])x$3.split(File.pathSeparator)).toSeq();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      });
      Seq testingClassPath = (Seq)(package$.MODULE$.props().contains("spark.testing") ? Predef$.MODULE$.refArrayOps((Object[])((String)package$.MODULE$.props().apply("java.class.path")).split(File.pathSeparator)).toSeq() : Nil$.MODULE$);
      Seq sparkJavaOpts = Utils$.MODULE$.sparkJavaOpts(this.conf(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String name) {
            return SparkConf$.MODULE$.isExecutorStartupConf(name);
         }
      });
      Seq javaOpts = (Seq)sparkJavaOpts.$plus$plus(extraJavaOpts, Seq$.MODULE$.canBuildFrom());
      Command command = new Command("org.apache.spark.executor.CoarseGrainedExecutorBackend", args, this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.executorEnvs(), (Seq)classPathEntries.$plus$plus(testingClassPath, Seq$.MODULE$.canBuildFrom()), libraryPathEntries, javaOpts);
      String webUrl = (String)this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.ui().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(SparkUI x$4) {
            return x$4.webUrl();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      Option coresPerExecutor = this.conf().getOption("spark.executor.cores").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$5) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$5))).toInt();
         }
      });
      Option initialExecutorLimit = (Option)(Utils$.MODULE$.isDynamicAllocationEnabled(this.conf()) ? new Some(BoxesRunTime.boxToInteger(0)) : None$.MODULE$);
      ApplicationDescription appDesc = new ApplicationDescription(this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.appName(), this.maxCores(), this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.executorMemory(), command, webUrl, this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.eventLogDir(), this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.eventLogCodec(), coresPerExecutor, initialExecutorLimit, ApplicationDescription$.MODULE$.apply$default$10());
      this.client_$eq(new StandaloneAppClient(this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.env().rpcEnv(), this.masters, appDesc, this, this.conf()));
      this.client().start();
      this.launcherBackend().setState(State.SUBMITTED);
      this.waitForRegistration();
      this.launcherBackend().setState(State.RUNNING);
   }

   public void stop() {
      this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$stop(State.FINISHED);
   }

   public void connected(String appId) {
      this.logInfo(new Serializable(this, appId) {
         public static final long serialVersionUID = 0L;
         private final String appId$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Connected to Spark cluster with app ID ").append((Object)this.appId$1).toString();
         }

         public {
            this.appId$1 = appId$1;
         }
      });
      this.appId_$eq(appId);
      this.notifyContext();
      this.launcherBackend().setAppId(appId);
   }

   public void disconnected() {
      this.notifyContext();
      if (!this.stopping().get()) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Disconnected from Spark cluster! Waiting for reconnection...";
            }
         });
      }

   }

   public void dead(String reason) {
      this.notifyContext();
      if (!this.stopping().get()) {
         this.launcherBackend().setState(State.KILLED);
         this.logError(new Serializable(this, reason) {
            public static final long serialVersionUID = 0L;
            private final String reason$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Application has been killed. Reason: ").append((Object)this.reason$1).toString();
            }

            public {
               this.reason$1 = reason$1;
            }
         });

         try {
            this.scheduler.error(reason);
         } finally {
            this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.stopInNewThread();
         }
      }

   }

   public void executorAdded(String fullId, String workerId, String hostPort, int cores, int memory) {
      this.logInfo(new Serializable(this, fullId, hostPort, cores, memory) {
         public static final long serialVersionUID = 0L;
         private final String fullId$1;
         private final String hostPort$1;
         private final int cores$1;
         private final int memory$1;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Granted executor ID %s on hostPort %s with %d core(s), %s RAM"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.fullId$1, this.hostPort$1, BoxesRunTime.boxToInteger(this.cores$1), Utils$.MODULE$.megabytesToString((long)this.memory$1)}));
         }

         public {
            this.fullId$1 = fullId$1;
            this.hostPort$1 = hostPort$1;
            this.cores$1 = cores$1;
            this.memory$1 = memory$1;
         }
      });
   }

   public void executorRemoved(String fullId, String message, Option<Object> exitStatus, boolean workerLost) {
      Object reason;
      if (exitStatus instanceof Some) {
         Some var7 = (Some)exitStatus;
         int code = BoxesRunTime.unboxToInt(var7.x());
         reason = new ExecutorExited(code, true, message);
      } else {
         if (!None$.MODULE$.equals(exitStatus)) {
            throw new MatchError(exitStatus);
         }

         reason = new SlaveLost(message, workerLost);
      }

      this.logInfo(new Serializable(this, fullId, message) {
         public static final long serialVersionUID = 0L;
         private final String fullId$2;
         private final String message$1;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Executor %s removed: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.fullId$2, this.message$1}));
         }

         public {
            this.fullId$2 = fullId$2;
            this.message$1 = message$1;
         }
      });
      this.removeExecutor(fullId.split("/")[1], (ExecutorLossReason)reason);
   }

   public void workerRemoved(String workerId, String host, String message) {
      this.logInfo(new Serializable(this, workerId, message) {
         public static final long serialVersionUID = 0L;
         private final String workerId$1;
         private final String message$2;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Worker %s removed: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerId$1, this.message$2}));
         }

         public {
            this.workerId$1 = workerId$1;
            this.message$2 = message$2;
         }
      });
      this.removeWorker(workerId, host, message);
   }

   public boolean sufficientResourcesRegistered() {
      return (double)this.totalCoreCount().get() >= (double)this.totalExpectedCores() * this.minRegisteredRatio();
   }

   public String applicationId() {
      return (String)Option$.MODULE$.apply(this.appId()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StandaloneSchedulerBackend $outer;

         public final String apply() {
            this.$outer.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Application ID is not initialized yet.";
               }
            });
            return this.$outer.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$super$applicationId();
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

   public Future<Object> doRequestTotalExecutors(int requestedTotal) {
      Option var2 = Option$.MODULE$.apply(this.client());
      Future var5;
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         StandaloneAppClient c = (StandaloneAppClient)var3.x();
         var5 = c.requestTotalExecutors(requestedTotal);
      } else {
         if (!None$.MODULE$.equals(var2)) {
            throw new MatchError(var2);
         }

         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Attempted to request executors before driver fully initialized.";
            }
         });
         var5 = Future$.MODULE$.successful(BoxesRunTime.boxToBoolean(false));
      }

      return var5;
   }

   public Future<Object> doKillExecutors(Seq<String> executorIds) {
      Option var2 = Option$.MODULE$.apply(this.client());
      Future var5;
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         StandaloneAppClient c = (StandaloneAppClient)var3.x();
         var5 = c.killExecutors(executorIds);
      } else {
         if (!None$.MODULE$.equals(var2)) {
            throw new MatchError(var2);
         }

         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Attempted to kill executors before driver fully initialized.";
            }
         });
         var5 = Future$.MODULE$.successful(BoxesRunTime.boxToBoolean(false));
      }

      return var5;
   }

   private void waitForRegistration() {
      this.registrationBarrier().acquire();
   }

   private void notifyContext() {
      this.registrationBarrier().release();
   }

   public void org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$stop(SparkAppHandle.State finalState) {
      if (this.stopping().compareAndSet(false, true)) {
         try {
            super.stop();
            this.client().stop();
            Function1 callback = this.shutdownCallback();
            if (callback != null) {
               callback.apply(this);
            }
         } finally {
            this.launcherBackend().setState(finalState);
            this.launcherBackend().close();
         }
      }

   }

   public StandaloneSchedulerBackend(TaskSchedulerImpl scheduler, SparkContext sc, String[] masters) {
      super(scheduler, sc.env().rpcEnv());
      this.scheduler = scheduler;
      this.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc = sc;
      this.masters = masters;
      this.client = null;
      this.stopping = new AtomicBoolean(false);
      this.launcherBackend = new LauncherBackend(this) {
         // $FF: synthetic field
         private final StandaloneSchedulerBackend $outer;

         public SparkConf conf() {
            return this.$outer.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$sc.conf();
         }

         public void onStopRequest() {
            this.$outer.org$apache$spark$scheduler$cluster$StandaloneSchedulerBackend$$stop(State.KILLED);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.registrationBarrier = new Semaphore(0);
      this.maxCores = this.conf().getOption("spark.cores.max").<Object>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toInt();
         }
      });
      this.totalExpectedCores = BoxesRunTime.unboxToInt(this.maxCores().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
   }
}
