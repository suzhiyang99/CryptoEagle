package org.apache.spark;

import java.io.File;
import java.util.Locale;
import org.apache.spark.broadcast.BroadcastManager;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.memory.MemoryManager;
import org.apache.spark.memory.StaticMemoryManager;
import org.apache.spark.memory.UnifiedMemoryManager$;
import org.apache.spark.metrics.MetricsSystem;
import org.apache.spark.metrics.MetricsSystem$;
import org.apache.spark.network.netty.NettyBlockTransferService;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnv$;
import org.apache.spark.scheduler.LiveListenerBus;
import org.apache.spark.scheduler.OutputCommitCoordinator;
import org.apache.spark.security.CryptoStreamUtils$;
import org.apache.spark.serializer.JavaSerializer;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.shuffle.ShuffleManager;
import org.apache.spark.shuffle.sort.SortShuffleManager;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.BlockManagerMaster;
import org.apache.spark.storage.BlockManagerMaster$;
import org.apache.spark.storage.BlockManagerMasterEndpoint;
import org.apache.spark.util.RpcUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.Properties$;

public final class SparkEnv$ implements Logging {
   public static final SparkEnv$ MODULE$;
   private volatile SparkEnv env;
   private final String driverSystemName;
   private final String executorSystemName;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SparkEnv$();
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

   private SparkEnv env() {
      return this.env;
   }

   private void env_$eq(SparkEnv x$1) {
      this.env = x$1;
   }

   public String driverSystemName() {
      return this.driverSystemName;
   }

   public String executorSystemName() {
      return this.executorSystemName;
   }

   public void set(SparkEnv e) {
      this.env_$eq(e);
   }

   public SparkEnv get() {
      return this.env();
   }

   public SparkEnv createDriverEnv(SparkConf conf, boolean isLocal, LiveListenerBus listenerBus, int numCores, Option<OutputCommitCoordinator> mockOutputCommitCoordinator) {
      Predef$.MODULE$.assert(conf.contains(org.apache.spark.internal.config.package$.MODULE$.DRIVER_HOST_ADDRESS()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is not set on the driver!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.DRIVER_HOST_ADDRESS().key()}));
         }
      });
      Predef$.MODULE$.assert(conf.contains("spark.driver.port"), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "spark.driver.port is not set on the driver!";
         }
      });
      String bindAddress = (String)conf.get(org.apache.spark.internal.config.package$.MODULE$.DRIVER_BIND_ADDRESS());
      String advertiseAddress = (String)conf.get(org.apache.spark.internal.config.package$.MODULE$.DRIVER_HOST_ADDRESS());
      int port = (new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.driver.port")))).toInt();
      Option ioEncryptionKey = (Option)(BoxesRunTime.unboxToBoolean(conf.get(org.apache.spark.internal.config.package$.MODULE$.IO_ENCRYPTION_ENABLED())) ? new Some(CryptoStreamUtils$.MODULE$.createKey(conf)) : None$.MODULE$);
      return this.create(conf, SparkContext$.MODULE$.DRIVER_IDENTIFIER(), bindAddress, advertiseAddress, Option$.MODULE$.apply(BoxesRunTime.boxToInteger(port)), isLocal, numCores, ioEncryptionKey, listenerBus, mockOutputCommitCoordinator);
   }

   public SparkEnv createExecutorEnv(SparkConf conf, String executorId, String hostname, int numCores, Option<byte[]> ioEncryptionKey, boolean isLocal) {
      SparkEnv env = this.create(conf, executorId, hostname, hostname, None$.MODULE$, isLocal, numCores, ioEncryptionKey, this.create$default$9(), this.create$default$10());
      this.set(env);
      return env;
   }

   private SparkEnv create(SparkConf conf, String executorId, String bindAddress, String advertiseAddress, Option<Object> port, boolean isLocal, int numUsableCores, Option<byte[]> ioEncryptionKey, LiveListenerBus listenerBus, Option<OutputCommitCoordinator> mockOutputCommitCoordinator) {
      boolean var10000;
      label69: {
         label68: {
            String var12 = SparkContext$.MODULE$.DRIVER_IDENTIFIER();
            if (executorId == null) {
               if (var12 == null) {
                  break label68;
               }
            } else if (executorId.equals(var12)) {
               break label68;
            }

            var10000 = false;
            break label69;
         }

         var10000 = true;
      }

      boolean isDriver = var10000;
      if (isDriver) {
         Predef$.MODULE$.assert(listenerBus != null, new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Attempted to create driver SparkEnv with null listener bus!";
            }
         });
      }

      SecurityManager securityManager = new SecurityManager(conf, ioEncryptionKey);
      if (isDriver) {
         securityManager.initializeAuth();
      }

      ioEncryptionKey.foreach(new Serializable(securityManager) {
         public static final long serialVersionUID = 0L;
         private final SecurityManager securityManager$1;

         public final void apply(byte[] x$4) {
            if (!this.securityManager$1.isEncryptionEnabled()) {
               SparkEnv$.MODULE$.logWarning(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "I/O encryption enabled without RPC encryption: keys will be visible on the wire.";
                  }
               });
            }

         }

         public {
            this.securityManager$1 = securityManager$1;
         }
      });
      String systemName = isDriver ? this.driverSystemName() : this.executorSystemName();
      RpcEnv rpcEnv = RpcEnv$.MODULE$.create(systemName, bindAddress, advertiseAddress, BoxesRunTime.unboxToInt(port.getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return -1;
         }
      })), conf, securityManager, numUsableCores, !isDriver);
      if (isDriver) {
         conf.set("spark.driver.port", BoxesRunTime.boxToInteger(rpcEnv.address().port()).toString());
      } else {
         BoxedUnit var37 = BoxedUnit.UNIT;
      }

      Serializer serializer = (Serializer)this.instantiateClassFromConf$1("spark.serializer", "org.apache.spark.serializer.JavaSerializer", conf, isDriver);
      this.logDebug(new Serializable(serializer) {
         public static final long serialVersionUID = 0L;
         private final Serializer serializer$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using serializer: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.serializer$1.getClass()}));
         }

         public {
            this.serializer$1 = serializer$1;
         }
      });
      SerializerManager serializerManager = new SerializerManager(serializer, conf, ioEncryptionKey);
      JavaSerializer closureSerializer = new JavaSerializer(conf);
      BroadcastManager broadcastManager = new BroadcastManager(isDriver, conf, securityManager);
      MapOutputTracker mapOutputTracker = (MapOutputTracker)(isDriver ? new MapOutputTrackerMaster(conf, broadcastManager, isLocal) : new MapOutputTrackerWorker(conf));
      mapOutputTracker.trackerEndpoint_$eq(this.registerOrLookupEndpoint$1(MapOutputTracker$.MODULE$.ENDPOINT_NAME(), new Serializable(conf, rpcEnv, mapOutputTracker) {
         public static final long serialVersionUID = 0L;
         private final SparkConf conf$1;
         private final RpcEnv rpcEnv$1;
         private final MapOutputTracker mapOutputTracker$1;

         public final MapOutputTrackerMasterEndpoint apply() {
            return new MapOutputTrackerMasterEndpoint(this.rpcEnv$1, (MapOutputTrackerMaster)this.mapOutputTracker$1, this.conf$1);
         }

         public {
            this.conf$1 = conf$1;
            this.rpcEnv$1 = rpcEnv$1;
            this.mapOutputTracker$1 = mapOutputTracker$1;
         }
      }, conf, isDriver, rpcEnv));
      Map shortShuffleMgrNames = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("sort"), SortShuffleManager.class.getName()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("tungsten-sort"), SortShuffleManager.class.getName())}));
      String shuffleMgrName = conf.get("spark.shuffle.manager", "sort");
      String shuffleMgrClass = (String)shortShuffleMgrNames.getOrElse(shuffleMgrName.toLowerCase(Locale.ROOT), new Serializable(shuffleMgrName) {
         public static final long serialVersionUID = 0L;
         private final String shuffleMgrName$1;

         public final String apply() {
            return this.shuffleMgrName$1;
         }

         public {
            this.shuffleMgrName$1 = shuffleMgrName$1;
         }
      });
      ShuffleManager shuffleManager = (ShuffleManager)this.instantiateClass$1(shuffleMgrClass, conf, isDriver);
      boolean useLegacyMemoryManager = conf.getBoolean("spark.memory.useLegacyMode", false);
      MemoryManager memoryManager = (MemoryManager)(useLegacyMemoryManager ? new StaticMemoryManager(conf, numUsableCores) : UnifiedMemoryManager$.MODULE$.apply(conf, numUsableCores));
      int blockManagerPort = isDriver ? BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DRIVER_BLOCK_MANAGER_PORT())) : BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.BLOCK_MANAGER_PORT()));
      NettyBlockTransferService blockTransferService = new NettyBlockTransferService(conf, securityManager, bindAddress, advertiseAddress, blockManagerPort, numUsableCores);
      BlockManagerMaster blockManagerMaster = new BlockManagerMaster(this.registerOrLookupEndpoint$1(BlockManagerMaster$.MODULE$.DRIVER_ENDPOINT_NAME(), new Serializable(conf, isLocal, listenerBus, rpcEnv) {
         public static final long serialVersionUID = 0L;
         private final SparkConf conf$1;
         private final boolean isLocal$1;
         private final LiveListenerBus listenerBus$1;
         private final RpcEnv rpcEnv$1;

         public final BlockManagerMasterEndpoint apply() {
            return new BlockManagerMasterEndpoint(this.rpcEnv$1, this.isLocal$1, this.conf$1, this.listenerBus$1);
         }

         public {
            this.conf$1 = conf$1;
            this.isLocal$1 = isLocal$1;
            this.listenerBus$1 = listenerBus$1;
            this.rpcEnv$1 = rpcEnv$1;
         }
      }, conf, isDriver, rpcEnv), conf, isDriver);
      BlockManager blockManager = new BlockManager(executorId, rpcEnv, blockManagerMaster, serializerManager, conf, memoryManager, mapOutputTracker, shuffleManager, blockTransferService, securityManager, numUsableCores);
      MetricsSystem var38;
      if (isDriver) {
         var38 = MetricsSystem$.MODULE$.createMetricsSystem("driver", conf, securityManager);
      } else {
         conf.set("spark.executor.id", executorId);
         MetricsSystem ms = MetricsSystem$.MODULE$.createMetricsSystem("executor", conf, securityManager);
         ms.start();
         var38 = ms;
      }

      MetricsSystem metricsSystem = var38;
      OutputCommitCoordinator outputCommitCoordinator = (OutputCommitCoordinator)mockOutputCommitCoordinator.getOrElse(new Serializable(conf, isDriver) {
         public static final long serialVersionUID = 0L;
         private final SparkConf conf$1;
         private final boolean isDriver$1;

         public final OutputCommitCoordinator apply() {
            return new OutputCommitCoordinator(this.conf$1, this.isDriver$1);
         }

         public {
            this.conf$1 = conf$1;
            this.isDriver$1 = isDriver$1;
         }
      });
      RpcEndpointRef outputCommitCoordinatorRef = this.registerOrLookupEndpoint$1("OutputCommitCoordinator", new Serializable(rpcEnv, outputCommitCoordinator) {
         public static final long serialVersionUID = 0L;
         private final RpcEnv rpcEnv$1;
         private final OutputCommitCoordinator outputCommitCoordinator$1;

         public final OutputCommitCoordinator.OutputCommitCoordinatorEndpoint apply() {
            return new OutputCommitCoordinator.OutputCommitCoordinatorEndpoint(this.rpcEnv$1, this.outputCommitCoordinator$1);
         }

         public {
            this.rpcEnv$1 = rpcEnv$1;
            this.outputCommitCoordinator$1 = outputCommitCoordinator$1;
         }
      }, conf, isDriver, rpcEnv);
      outputCommitCoordinator.coordinatorRef_$eq(new Some(outputCommitCoordinatorRef));
      SparkEnv envInstance = new SparkEnv(executorId, rpcEnv, serializer, closureSerializer, serializerManager, mapOutputTracker, shuffleManager, broadcastManager, blockManager, securityManager, metricsSystem, memoryManager, outputCommitCoordinator, conf);
      if (isDriver) {
         String sparkFilesDir = Utils$.MODULE$.createTempDir(Utils$.MODULE$.getLocalDir(conf), "userFiles").getAbsolutePath();
         envInstance.driverTmpDir_$eq(new Some(sparkFilesDir));
      }

      return envInstance;
   }

   public Option<OutputCommitCoordinator> createDriverEnv$default$5() {
      return None$.MODULE$;
   }

   private LiveListenerBus create$default$9() {
      return null;
   }

   private Option<OutputCommitCoordinator> create$default$10() {
      return None$.MODULE$;
   }

   public Map<String, Seq<Tuple2<String, String>>> environmentDetails(SparkConf conf, String schedulingMode, Seq<String> addedJars, Seq<String> addedFiles) {
      Seq jvmInformation = (Seq)((SeqLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Java Version", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Properties$.MODULE$.javaVersion(), Properties$.MODULE$.javaVendor()}))), new Tuple2("Java Home", Properties$.MODULE$.javaHome()), new Tuple2("Scala Version", Properties$.MODULE$.versionString())}))).sorted(Ordering$.MODULE$.Tuple2(Ordering.String$.MODULE$, Ordering.String$.MODULE$));
      Seq schedulerMode = conf.contains("spark.scheduler.mode") ? (Seq)Seq$.MODULE$.empty() : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("spark.scheduler.mode", schedulingMode)}));
      Tuple2[] sparkProperties = (Tuple2[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(conf.getAll()).$plus$plus(schedulerMode, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).sorted(Ordering$.MODULE$.Tuple2(Ordering.String$.MODULE$, Ordering.String$.MODULE$));
      Seq systemProperties = Utils$.MODULE$.getSystemProperties().toSeq();
      Seq otherProperties = (Seq)((SeqLike)systemProperties.filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> x0$1) {
            if (x0$1 == null) {
               throw new MatchError(x0$1);
            } else {
               boolean var10000;
               label22: {
                  label21: {
                     String k = (String)x0$1._1();
                     String var5 = "java.class.path";
                     if (k == null) {
                        if (var5 == null) {
                           break label21;
                        }
                     } else if (k.equals(var5)) {
                        break label21;
                     }

                     if (!k.startsWith("spark.")) {
                        var10000 = true;
                        break label22;
                     }
                  }

                  var10000 = false;
               }

               boolean var4 = var10000;
               return var4;
            }
         }
      })).sorted(Ordering$.MODULE$.Tuple2(Ordering.String$.MODULE$, Ordering.String$.MODULE$));
      Tuple2[] classPathEntries = (Tuple2[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])Properties$.MODULE$.javaClassPath().split(File.pathSeparator)).filterNot(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$5) {
            return x$5.isEmpty();
         }
      })).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, String> apply(String x$6) {
            return new Tuple2<String, String>(x$6, "System Classpath");
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)));
      Seq addedJarsAndFiles = (Seq)((TraversableLike)addedJars.$plus$plus(addedFiles, Seq$.MODULE$.canBuildFrom())).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, String> apply(String x$7) {
            return new Tuple2<String, String>(x$7, "Added By User");
         }
      }, Seq$.MODULE$.canBuildFrom());
      Seq classPaths = (Seq)((SeqLike)addedJarsAndFiles.$plus$plus(Predef$.MODULE$.refArrayOps(classPathEntries), Seq$.MODULE$.canBuildFrom())).sorted(Ordering$.MODULE$.Tuple2(Ordering.String$.MODULE$, Ordering.String$.MODULE$));
      return (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("JVM Information"), jvmInformation), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Spark Properties"), Predef$.MODULE$.wrapRefArray(sparkProperties)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("System Properties"), otherProperties), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Classpath Entries"), classPaths)}));
   }

   private final Object instantiateClass$1(String className, SparkConf conf$1, boolean isDriver$1) {
      Class cls = Utils$.MODULE$.classForName(className);

      Object var10000;
      try {
         var10000 = cls.getConstructor(SparkConf.class, Boolean.TYPE).newInstance(conf$1, new Boolean(isDriver$1));
      } catch (NoSuchMethodException var6) {
         try {
            var10000 = cls.getConstructor(SparkConf.class).newInstance(conf$1);
         } catch (NoSuchMethodException var5) {
            var10000 = cls.getConstructor().newInstance();
         }
      }

      return var10000;
   }

   private final Object instantiateClassFromConf$1(String propertyName, String defaultClassName, SparkConf conf$1, boolean isDriver$1) {
      return this.instantiateClass$1(conf$1.get(propertyName, defaultClassName), conf$1, isDriver$1);
   }

   private final RpcEndpointRef registerOrLookupEndpoint$1(String name, Function0 endpointCreator, SparkConf conf$1, boolean isDriver$1, RpcEnv rpcEnv$1) {
      RpcEndpointRef var10000;
      if (isDriver$1) {
         this.logInfo(new Serializable(name) {
            public static final long serialVersionUID = 0L;
            private final String name$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Registering ").append((Object)this.name$1).toString();
            }

            public {
               this.name$1 = name$1;
            }
         });
         var10000 = rpcEnv$1.setupEndpoint(name, (RpcEndpoint)endpointCreator.apply());
      } else {
         var10000 = RpcUtils$.MODULE$.makeDriverRef(name, conf$1, rpcEnv$1);
      }

      return var10000;
   }

   private SparkEnv$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.driverSystemName = "sparkDriver";
      this.executorSystemName = "sparkExecutor";
   }
}
