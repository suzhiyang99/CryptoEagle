package org.apache.spark.deploy;

import org.apache.spark.SparkConf;
import org.apache.spark.deploy.master.Master$;
import org.apache.spark.deploy.worker.Worker$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001%4Q!\u0001\u0002\u0001\t)\u0011\u0011\u0003T8dC2\u001c\u0006/\u0019:l\u00072,8\u000f^3s\u0015\t\u0019A!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u0011R#D\u0001\u0014\u0015\t!B!\u0001\u0005j]R,'O\\1m\u0013\t12CA\u0004M_\u001e<\u0017N\\4\t\u0011a\u0001!\u0011!Q\u0001\ni\t!B\\;n/>\u00148.\u001a:t\u0007\u0001\u0001\"\u0001D\u000e\n\u0005qi!aA%oi\"Aa\u0004\u0001B\u0001B\u0003%!$\u0001\bd_J,7\u000fU3s/>\u00148.\u001a:\t\u0011\u0001\u0002!\u0011!Q\u0001\ni\tq\"\\3n_JL\b+\u001a:X_J\\WM\u001d\u0005\tE\u0001\u0011\t\u0011)A\u0005G\u0005!1m\u001c8g!\t!S%D\u0001\u0005\u0013\t1CAA\u0005Ta\u0006\u00148nQ8oM\")\u0001\u0006\u0001C\u0001S\u00051A(\u001b8jiz\"RA\u000b\u0017.]=\u0002\"a\u000b\u0001\u000e\u0003\tAQ\u0001G\u0014A\u0002iAQAH\u0014A\u0002iAQ\u0001I\u0014A\u0002iAQAI\u0014A\u0002\rBq!\r\u0001C\u0002\u0013%!'A\u0007m_\u000e\fG\u000eS8ti:\fW.Z\u000b\u0002gA\u0011Ag\u000e\b\u0003\u0019UJ!AN\u0007\u0002\rA\u0013X\rZ3g\u0013\tA\u0014H\u0001\u0004TiJLgn\u001a\u0006\u0003m5Aaa\u000f\u0001!\u0002\u0013\u0019\u0014A\u00047pG\u0006d\u0007j\\:u]\u0006lW\r\t\u0005\b{\u0001\u0011\r\u0011\"\u0003?\u00035i\u0017m\u001d;feJ\u00038-\u00128wgV\tq\bE\u0002A\u000b\u001ek\u0011!\u0011\u0006\u0003\u0005\u000e\u000bq!\\;uC\ndWM\u0003\u0002E\u001b\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005\u0019\u000b%aC!se\u0006L()\u001e4gKJ\u0004\"\u0001S&\u000e\u0003%S!A\u0013\u0003\u0002\u0007I\u00048-\u0003\u0002M\u0013\n1!\u000b]2F]ZDaA\u0014\u0001!\u0002\u0013y\u0014AD7bgR,'O\u00159d\u000b:48\u000f\t\u0005\b!\u0002\u0011\r\u0011\"\u0003?\u000359xN]6feJ\u00038-\u00128wg\"1!\u000b\u0001Q\u0001\n}\nab^8sW\u0016\u0014(\u000b]2F]Z\u001c\b\u0005C\u0004U\u0001\u0001\u0007I\u0011A+\u0002\u001f5\f7\u000f^3s/\u0016\u0014W+\u0013)peR,\u0012A\u0007\u0005\b/\u0002\u0001\r\u0011\"\u0001Y\u0003Mi\u0017m\u001d;fe^+'-V%Q_J$x\fJ3r)\tIF\f\u0005\u0002\r5&\u00111,\u0004\u0002\u0005+:LG\u000fC\u0004^-\u0006\u0005\t\u0019\u0001\u000e\u0002\u0007a$\u0013\u0007\u0003\u0004`\u0001\u0001\u0006KAG\u0001\u0011[\u0006\u001cH/\u001a:XK\n,\u0016\nU8si\u0002BQ!\u0019\u0001\u0005\u0002\t\fQa\u001d;beR$\u0012a\u0019\t\u0004\u0019\u0011\u001c\u0014BA3\u000e\u0005\u0015\t%O]1z\u0011\u00159\u0007\u0001\"\u0001i\u0003\u0011\u0019Ho\u001c9\u0015\u0003e\u0003"
)
public class LocalSparkCluster implements Logging {
   public final int org$apache$spark$deploy$LocalSparkCluster$$numWorkers;
   public final int org$apache$spark$deploy$LocalSparkCluster$$coresPerWorker;
   public final int org$apache$spark$deploy$LocalSparkCluster$$memoryPerWorker;
   private final SparkConf conf;
   private final String org$apache$spark$deploy$LocalSparkCluster$$localHostname;
   private final ArrayBuffer<RpcEnv> masterRpcEnvs;
   private final ArrayBuffer<RpcEnv> org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs;
   private int masterWebUIPort;
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

   public String org$apache$spark$deploy$LocalSparkCluster$$localHostname() {
      return this.org$apache$spark$deploy$LocalSparkCluster$$localHostname;
   }

   private ArrayBuffer<RpcEnv> masterRpcEnvs() {
      return this.masterRpcEnvs;
   }

   public ArrayBuffer<RpcEnv> org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs() {
      return this.org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs;
   }

   public int masterWebUIPort() {
      return this.masterWebUIPort;
   }

   public void masterWebUIPort_$eq(int x$1) {
      this.masterWebUIPort = x$1;
   }

   public String[] start() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LocalSparkCluster $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Starting a local Spark cluster with ").append((Object)BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$deploy$LocalSparkCluster$$numWorkers)).append((Object)" workers.").toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      SparkConf _conf = this.conf.clone().setIfMissing("spark.master.rest.enabled", "false").set("spark.shuffle.service.enabled", "false");
      Tuple3 var3 = Master$.MODULE$.startRpcEnvAndEndpoint(this.org$apache$spark$deploy$LocalSparkCluster$$localHostname(), 0, 0, _conf);
      if (var3 != null) {
         RpcEnv rpcEnv = (RpcEnv)var3._1();
         int webUiPort = BoxesRunTime.unboxToInt(var3._2());
         Tuple2 var6 = new Tuple2(rpcEnv, BoxesRunTime.boxToInteger(webUiPort));
         RpcEnv rpcEnv = (RpcEnv)var6._1();
         int webUiPort = var6._2$mcI$sp();
         this.masterWebUIPort_$eq(webUiPort);
         this.masterRpcEnvs().$plus$eq(rpcEnv);
         String masterUrl = (new StringBuilder()).append((Object)"spark://").append((Object)Utils$.MODULE$.localHostNameForURI()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(rpcEnv.address().port())).toString();
         String[] masters = (String[])((Object[])(new String[]{masterUrl}));
         RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), this.org$apache$spark$deploy$LocalSparkCluster$$numWorkers).foreach(new Serializable(this, _conf, masters) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final LocalSparkCluster $outer;
            private final SparkConf _conf$1;
            private final String[] masters$1;

            public final ArrayBuffer<RpcEnv> apply(int workerNum) {
               RpcEnv workerEnv = Worker$.MODULE$.startRpcEnvAndEndpoint(this.$outer.org$apache$spark$deploy$LocalSparkCluster$$localHostname(), 0, 0, this.$outer.org$apache$spark$deploy$LocalSparkCluster$$coresPerWorker, this.$outer.org$apache$spark$deploy$LocalSparkCluster$$memoryPerWorker, this.masters$1, (String)null, new Some(BoxesRunTime.boxToInteger(workerNum)), this._conf$1);
               return this.$outer.org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs().$plus$eq(workerEnv);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this._conf$1 = _conf$1;
                  this.masters$1 = masters$1;
               }
            }
         });
         return masters;
      } else {
         throw new MatchError(var3);
      }
   }

   public void stop() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Shutting down local Spark cluster.";
         }
      });
      this.org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(RpcEnv x$2) {
            x$2.shutdown();
         }
      });
      this.masterRpcEnvs().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(RpcEnv x$3) {
            x$3.shutdown();
         }
      });
      this.org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(RpcEnv x$4) {
            x$4.awaitTermination();
         }
      });
      this.masterRpcEnvs().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(RpcEnv x$5) {
            x$5.awaitTermination();
         }
      });
      this.masterRpcEnvs().clear();
      this.org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs().clear();
   }

   public LocalSparkCluster(int numWorkers, int coresPerWorker, int memoryPerWorker, SparkConf conf) {
      this.org$apache$spark$deploy$LocalSparkCluster$$numWorkers = numWorkers;
      this.org$apache$spark$deploy$LocalSparkCluster$$coresPerWorker = coresPerWorker;
      this.org$apache$spark$deploy$LocalSparkCluster$$memoryPerWorker = memoryPerWorker;
      this.conf = conf;
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$LocalSparkCluster$$localHostname = Utils$.MODULE$.localHostName();
      this.masterRpcEnvs = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.org$apache$spark$deploy$LocalSparkCluster$$workerRpcEnvs = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.masterWebUIPort = -1;
   }
}
