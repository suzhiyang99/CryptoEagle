package org.apache.spark.executor;

import java.net.URL;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkConf$;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.deploy.worker.WorkerWatcher;
import org.apache.spark.deploy.worker.WorkerWatcher$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnv$;
import org.apache.spark.scheduler.cluster.CoarseGrainedClusterMessages;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;

public final class CoarseGrainedExecutorBackend$ implements Logging {
   public static final CoarseGrainedExecutorBackend$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new CoarseGrainedExecutorBackend$();
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

   private void run(String driverUrl, String executorId, String hostname, int cores, String appId, Option<String> workerUrl, Seq<URL> userClassPath) {
      Utils$.MODULE$.initDaemon(this.log());
      SparkHadoopUtil$.MODULE$.get().runAsSparkUser(new Serializable(driverUrl, executorId, hostname, cores, appId, workerUrl, userClassPath) {
         public static final long serialVersionUID = 0L;
         private final String driverUrl$1;
         private final String executorId$1;
         private final String hostname$1;
         private final int cores$1;
         private final String appId$1;
         private final Option workerUrl$1;
         private final Seq userClassPath$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Utils$.MODULE$.checkHost(this.hostname$1);
            SparkConf executorConf = new SparkConf();
            RpcEnv fetcher = RpcEnv$.MODULE$.create("driverPropsFetcher", this.hostname$1, -1, executorConf, new SecurityManager(executorConf, SecurityManager$.MODULE$.$lessinit$greater$default$2()), true);
            RpcEndpointRef driver = fetcher.setupEndpointRefByURI(this.driverUrl$1);
            CoarseGrainedClusterMessages.SparkAppConfig cfg = (CoarseGrainedClusterMessages.SparkAppConfig)driver.askSync(CoarseGrainedClusterMessages.RetrieveSparkAppConfig$.MODULE$, ClassTag$.MODULE$.apply(CoarseGrainedClusterMessages.SparkAppConfig.class));
            Seq props = (Seq)cfg.sparkProperties().$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("spark.app.id", this.appId$1)})), Seq$.MODULE$.canBuildFrom());
            fetcher.shutdown();
            SparkConf driverConf = new SparkConf();
            props.withFilter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Tuple2<String, String> check$ifrefutable$1) {
                  boolean var3;
                  if (check$ifrefutable$1 != null) {
                     var3 = true;
                  } else {
                     var3 = false;
                  }

                  return var3;
               }
            }).foreach(new Serializable(this, driverConf) {
               public static final long serialVersionUID = 0L;
               private final SparkConf driverConf$1;

               public final SparkConf apply(Tuple2<String, String> x$3) {
                  if (x$3 != null) {
                     String key = (String)x$3._1();
                     String value = (String)x$3._2();
                     SparkConf var5 = SparkConf$.MODULE$.isExecutorStartupConf(key) ? this.driverConf$1.setIfMissing(key, value) : this.driverConf$1.set(key, value);
                     return var5;
                  } else {
                     throw new MatchError(x$3);
                  }
               }

               public {
                  this.driverConf$1 = driverConf$1;
               }
            });
            if (driverConf.contains("spark.yarn.credentials.file")) {
               CoarseGrainedExecutorBackend$.MODULE$.logInfo(new Serializable(this, driverConf) {
                  public static final long serialVersionUID = 0L;
                  private final SparkConf driverConf$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Will periodically update credentials from: ").append((Object)this.driverConf$1.get("spark.yarn.credentials.file")).toString();
                  }

                  public {
                     this.driverConf$1 = driverConf$1;
                  }
               });
               Utils$.MODULE$.classForName("org.apache.spark.deploy.yarn.YarnSparkHadoopUtil").getMethod("startCredentialUpdater", SparkConf.class).invoke((Object)null, driverConf);
            } else {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            cfg.hadoopDelegationCreds().foreach(new Serializable(this, driverConf) {
               public static final long serialVersionUID = 0L;
               private final SparkConf driverConf$1;

               public final void apply(byte[] tokens) {
                  SparkHadoopUtil$.MODULE$.get().addDelegationTokens(tokens, this.driverConf$1);
               }

               public {
                  this.driverConf$1 = driverConf$1;
               }
            });
            SparkEnv env = SparkEnv$.MODULE$.createExecutorEnv(driverConf, this.executorId$1, this.hostname$1, this.cores$1, cfg.ioEncryptionKey(), false);
            env.rpcEnv().setupEndpoint("Executor", new CoarseGrainedExecutorBackend(env.rpcEnv(), this.driverUrl$1, this.executorId$1, this.hostname$1, this.cores$1, this.userClassPath$1, env));
            this.workerUrl$1.foreach(new Serializable(this, env) {
               public static final long serialVersionUID = 0L;
               private final SparkEnv env$1;

               public final RpcEndpointRef apply(String url) {
                  return this.env$1.rpcEnv().setupEndpoint("WorkerWatcher", new WorkerWatcher(this.env$1.rpcEnv(), url, WorkerWatcher$.MODULE$.$lessinit$greater$default$3()));
               }

               public {
                  this.env$1 = env$1;
               }
            });
            env.rpcEnv().awaitTermination();
            if (driverConf.contains("spark.yarn.credentials.file")) {
               Utils$.MODULE$.classForName("org.apache.spark.deploy.yarn.YarnSparkHadoopUtil").getMethod("stopCredentialUpdater").invoke((Object)null);
            }

         }

         public {
            this.driverUrl$1 = driverUrl$1;
            this.executorId$1 = executorId$1;
            this.hostname$1 = hostname$1;
            this.cores$1 = cores$1;
            this.appId$1 = appId$1;
            this.workerUrl$1 = workerUrl$1;
            this.userClassPath$1 = userClassPath$1;
         }
      });
   }

   public void main(String[] args) {
      String driverUrl = null;
      String executorId = null;
      String hostname = null;
      int cores = 0;
      String appId = null;
      Option workerUrl = None$.MODULE$;
      ListBuffer userClassPath = new ListBuffer();
      List argv = Predef$.MODULE$.refArrayOps((Object[])args).toList();

      while(!argv.isEmpty()) {
         boolean var10 = false;
         $colon$colon var11 = null;
         if (argv instanceof $colon$colon) {
            var10 = true;
            var11 = ($colon$colon)argv;
            String var13 = (String)var11.head();
            List var14 = var11.tl$1();
            if ("--driver-url".equals(var13) && var14 instanceof $colon$colon) {
               $colon$colon var15 = ($colon$colon)var14;
               String value = (String)var15.head();
               List tail = var15.tl$1();
               driverUrl = value;
               argv = tail;
               BoxedUnit var56 = BoxedUnit.UNIT;
               continue;
            }
         }

         if (var10) {
            String var19 = (String)var11.head();
            List var20 = var11.tl$1();
            if ("--executor-id".equals(var19) && var20 instanceof $colon$colon) {
               $colon$colon var21 = ($colon$colon)var20;
               String value = (String)var21.head();
               List tail = var21.tl$1();
               executorId = value;
               argv = tail;
               BoxedUnit var55 = BoxedUnit.UNIT;
               continue;
            }
         }

         if (var10) {
            String var24 = (String)var11.head();
            List var25 = var11.tl$1();
            if ("--hostname".equals(var24) && var25 instanceof $colon$colon) {
               $colon$colon var26 = ($colon$colon)var25;
               String value = (String)var26.head();
               List tail = var26.tl$1();
               hostname = value;
               argv = tail;
               BoxedUnit var54 = BoxedUnit.UNIT;
               continue;
            }
         }

         if (var10) {
            String var29 = (String)var11.head();
            List var30 = var11.tl$1();
            if ("--cores".equals(var29) && var30 instanceof $colon$colon) {
               $colon$colon var31 = ($colon$colon)var30;
               String value = (String)var31.head();
               List tail = var31.tl$1();
               cores = (new StringOps(Predef$.MODULE$.augmentString(value))).toInt();
               argv = tail;
               BoxedUnit var53 = BoxedUnit.UNIT;
               continue;
            }
         }

         if (var10) {
            String var34 = (String)var11.head();
            List var35 = var11.tl$1();
            if ("--app-id".equals(var34) && var35 instanceof $colon$colon) {
               $colon$colon var36 = ($colon$colon)var35;
               String value = (String)var36.head();
               List tail = var36.tl$1();
               appId = value;
               argv = tail;
               BoxedUnit var52 = BoxedUnit.UNIT;
               continue;
            }
         }

         if (var10) {
            String var39 = (String)var11.head();
            List var40 = var11.tl$1();
            if ("--worker-url".equals(var39) && var40 instanceof $colon$colon) {
               $colon$colon var41 = ($colon$colon)var40;
               String value = (String)var41.head();
               List tail = var41.tl$1();
               workerUrl = new Some(value);
               argv = tail;
               BoxedUnit var51 = BoxedUnit.UNIT;
               continue;
            }
         }

         if (var10) {
            String var44 = (String)var11.head();
            List var45 = var11.tl$1();
            if ("--user-class-path".equals(var44) && var45 instanceof $colon$colon) {
               $colon$colon var46 = ($colon$colon)var45;
               String value = (String)var46.head();
               List tail = var46.tl$1();
               userClassPath.$plus$eq(new URL(value));
               argv = tail;
               BoxedUnit var50 = BoxedUnit.UNIT;
               continue;
            }
         }

         if (Nil$.MODULE$.equals(argv)) {
            BoxedUnit var18 = BoxedUnit.UNIT;
         } else {
            System.err.println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unrecognized options: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{argv.mkString(" ")})));
            this.printUsageAndExit();
            BoxedUnit var49 = BoxedUnit.UNIT;
         }
      }

      if (driverUrl == null || executorId == null || hostname == null || cores <= 0 || appId == null) {
         this.printUsageAndExit();
      }

      this.run(driverUrl, executorId, hostname, cores, appId, workerUrl, userClassPath);
      System.exit(0);
   }

   private void printUsageAndExit() {
      System.err.println((new StringOps(Predef$.MODULE$.augmentString("\n      |Usage: CoarseGrainedExecutorBackend [options]\n      |\n      | Options are:\n      |   --driver-url <driverUrl>\n      |   --executor-id <executorId>\n      |   --hostname <hostname>\n      |   --cores <cores>\n      |   --app-id <appid>\n      |   --worker-url <workerUrl>\n      |   --user-class-path <url>\n      |"))).stripMargin());
      System.exit(1);
   }

   private CoarseGrainedExecutorBackend$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
