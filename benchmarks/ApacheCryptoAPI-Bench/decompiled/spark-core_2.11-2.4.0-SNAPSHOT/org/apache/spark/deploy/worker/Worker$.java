package org.apache.spark.deploy.worker;

import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.Command;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcAddress$;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnv$;
import org.apache.spark.util.SparkUncaughtExceptionHandler;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.LinearSeqOptimized;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;
import scala.sys.package$;
import scala.util.matching.Regex;

public final class Worker$ implements Logging {
   public static final Worker$ MODULE$;
   private final String SYSTEM_NAME;
   private final String ENDPOINT_NAME;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new Worker$();
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
      WorkerArguments args = new WorkerArguments(argStrings, conf);
      String x$36 = args.host();
      int x$37 = args.port();
      int x$38 = args.webUiPort();
      int x$39 = args.cores();
      int x$40 = args.memory();
      String[] x$41 = args.masters();
      String x$42 = args.workDir();
      Option x$44 = this.startRpcEnvAndEndpoint$default$8();
      RpcEnv rpcEnv = this.startRpcEnvAndEndpoint(x$36, x$37, x$38, x$39, x$40, x$41, x$42, x$44, conf);
      boolean externalShuffleServiceEnabled = conf.getBoolean("spark.shuffle.service.enabled", false);
      int sparkWorkerInstances = (new StringOps(Predef$.MODULE$.augmentString((String)package$.MODULE$.env().getOrElse("SPARK_WORKER_INSTANCES", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "1";
         }
      })))).toInt();
      Predef$.MODULE$.require(!externalShuffleServiceEnabled || sparkWorkerInstances <= 1, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Starting multiple workers on one host is failed because we may launch no more than one external shuffle service on each host, please set spark.shuffle.service.enabled to false or set SPARK_WORKER_INSTANCES to 1 to resolve the conflict.";
         }
      });
      rpcEnv.awaitTermination();
   }

   public RpcEnv startRpcEnvAndEndpoint(String host, int port, int webUiPort, int cores, int memory, String[] masterUrls, String workDir, Option<Object> workerNumber, SparkConf conf) {
      String systemName = (new StringBuilder()).append((Object)this.SYSTEM_NAME()).append(workerNumber.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(int x$19) {
            return BoxesRunTime.boxToInteger(x$19).toString();
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).toString();
      SecurityManager securityMgr = new SecurityManager(conf, SecurityManager$.MODULE$.$lessinit$greater$default$2());
      RpcEnv rpcEnv = RpcEnv$.MODULE$.create(systemName, host, port, conf, securityMgr, RpcEnv$.MODULE$.create$default$6());
      RpcAddress[] masterAddresses = (RpcAddress[])Predef$.MODULE$.refArrayOps((Object[])masterUrls).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final RpcAddress apply(String x$20) {
            return RpcAddress$.MODULE$.fromSparkURL(x$20);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(RpcAddress.class)));
      rpcEnv.setupEndpoint(this.ENDPOINT_NAME(), new Worker(rpcEnv, webUiPort, cores, memory, masterAddresses, this.ENDPOINT_NAME(), workDir, conf, securityMgr));
      return rpcEnv;
   }

   public Option<Object> startRpcEnvAndEndpoint$default$8() {
      return None$.MODULE$;
   }

   public SparkConf startRpcEnvAndEndpoint$default$9() {
      return new SparkConf();
   }

   public boolean isUseLocalNodeSSLConfig(Command cmd) {
      Regex pattern = (new StringOps(Predef$.MODULE$.augmentString("\\-Dspark\\.ssl\\.useNodeLocalConf\\=(.+)"))).r();
      Option result = cmd.javaOpts().collectFirst(new Serializable(pattern) {
         public static final long serialVersionUID = 0L;
         private final Regex pattern$1;

         public final <A1 extends String, B1> B1 applyOrElse(A1 x3, Function1<A1, B1> var2) {
            Option var4 = this.pattern$1.unapplySeq((CharSequence)x3);
            Object var6;
            if (!var4.isEmpty() && var4.get() != null && ((LinearSeqOptimized)var4.get()).lengthCompare(1) == 0) {
               String _result = (String)((LinearSeqOptimized)var4.get()).apply(0);
               var6 = BoxesRunTime.boxToBoolean((new StringOps(Predef$.MODULE$.augmentString(_result))).toBoolean());
            } else {
               var6 = var2.apply(x3);
            }

            return (B1)var6;
         }

         public final boolean isDefinedAt(String x3) {
            Option var3 = this.pattern$1.unapplySeq((CharSequence)x3);
            boolean var4;
            if (!var3.isEmpty() && var3.get() != null && ((LinearSeqOptimized)var3.get()).lengthCompare(1) == 0) {
               var4 = true;
            } else {
               var4 = false;
            }

            return var4;
         }

         public {
            this.pattern$1 = pattern$1;
         }
      });
      return BoxesRunTime.unboxToBoolean(result.getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return false;
         }
      }));
   }

   public Command maybeUpdateSSLSettings(Command cmd, SparkConf conf) {
      String prefix = "spark.ssl.";
      String useNLC = "spark.ssl.useNodeLocalConf";
      Command var10000;
      if (this.isUseLocalNodeSSLConfig(cmd)) {
         Seq newJavaOpts = (Seq)((SeqLike)((TraversableLike)cmd.javaOpts().filter(new Serializable(prefix) {
            public static final long serialVersionUID = 0L;
            private final String prefix$1;

            public final boolean apply(String opt) {
               return !opt.startsWith((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"-D", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.prefix$1})));
            }

            public {
               this.prefix$1 = prefix$1;
            }
         })).$plus$plus(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(conf.getAll()).collect(new Serializable(prefix) {
            public static final long serialVersionUID = 0L;
            private final String prefix$1;

            public final <A1 extends Tuple2<String, String>, B1> B1 applyOrElse(A1 x4, Function1<A1, B1> var2) {
               Object var6;
               if (x4 != null) {
                  String key = (String)x4._1();
                  String value = (String)x4._2();
                  if (key.startsWith(this.prefix$1)) {
                     var6 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"-D", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key, value}));
                     return (B1)var6;
                  }
               }

               var6 = var2.apply(x4);
               return (B1)var6;
            }

            public final boolean isDefinedAt(Tuple2<String, String> x4) {
               boolean var4;
               if (x4 != null) {
                  String key = (String)x4._1();
                  if (key.startsWith(this.prefix$1)) {
                     var4 = true;
                     return var4;
                  }
               }

               var4 = false;
               return var4;
            }

            public {
               this.prefix$1 = prefix$1;
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))), Seq$.MODULE$.canBuildFrom())).$colon$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"-D", "=true"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{useNLC})), Seq$.MODULE$.canBuildFrom());
         String x$46 = cmd.copy$default$1();
         Seq x$47 = cmd.copy$default$2();
         Map x$48 = cmd.copy$default$3();
         Seq x$49 = cmd.copy$default$4();
         Seq x$50 = cmd.copy$default$5();
         var10000 = cmd.copy(x$46, x$47, x$48, x$49, x$50, newJavaOpts);
      } else {
         var10000 = cmd;
      }

      return var10000;
   }

   public String $lessinit$greater$default$7() {
      return null;
   }

   private Worker$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.SYSTEM_NAME = "sparkWorker";
      this.ENDPOINT_NAME = "Worker";
   }
}
