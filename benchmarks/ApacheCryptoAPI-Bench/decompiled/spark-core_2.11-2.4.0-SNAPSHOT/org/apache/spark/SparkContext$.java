package org.apache.spark;

import java.net.URL;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;
import org.apache.spark.deploy.LocalSparkCluster;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.scheduler.ExternalClusterManager;
import org.apache.spark.scheduler.SchedulerBackend;
import org.apache.spark.scheduler.TaskScheduler;
import org.apache.spark.scheduler.TaskSchedulerImpl;
import org.apache.spark.scheduler.cluster.StandaloneSchedulerBackend;
import org.apache.spark.scheduler.local.LocalSchedulerBackend;
import org.apache.spark.util.CallSite;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.JavaConverters$;
import scala.collection.LinearSeqOptimized;
import scala.collection.Map;
import scala.collection.Map$;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.Traversable$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.control.NonFatal$;

public final class SparkContext$ implements Logging {
   public static final SparkContext$ MODULE$;
   private final Set<String> org$apache$spark$SparkContext$$VALID_LOG_LEVELS;
   private final Object SPARK_CONTEXT_CONSTRUCTOR_LOCK;
   private final AtomicReference<SparkContext> org$apache$spark$SparkContext$$activeContext;
   private Option<SparkContext> contextBeingConstructed;
   private final String SPARK_JOB_DESCRIPTION;
   private final String SPARK_JOB_GROUP_ID;
   private final String SPARK_JOB_INTERRUPT_ON_CANCEL;
   private final String RDD_SCOPE_KEY;
   private final String RDD_SCOPE_NO_OVERRIDE_KEY;
   private final String DRIVER_IDENTIFIER;
   private final String LEGACY_DRIVER_IDENTIFIER;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SparkContext$();
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

   public Set<String> org$apache$spark$SparkContext$$VALID_LOG_LEVELS() {
      return this.org$apache$spark$SparkContext$$VALID_LOG_LEVELS;
   }

   private Object SPARK_CONTEXT_CONSTRUCTOR_LOCK() {
      return this.SPARK_CONTEXT_CONSTRUCTOR_LOCK;
   }

   public AtomicReference<SparkContext> org$apache$spark$SparkContext$$activeContext() {
      return this.org$apache$spark$SparkContext$$activeContext;
   }

   private Option<SparkContext> contextBeingConstructed() {
      return this.contextBeingConstructed;
   }

   private void contextBeingConstructed_$eq(Option<SparkContext> x$1) {
      this.contextBeingConstructed = x$1;
   }

   private void assertNoOtherContextIsRunning(SparkContext sc, boolean allowMultipleContexts) {
      synchronized(this.SPARK_CONTEXT_CONSTRUCTOR_LOCK()) {
         Option$.MODULE$.apply(this.org$apache$spark$SparkContext$$activeContext().get()).filter(new Serializable(sc) {
            public static final long serialVersionUID = 0L;
            private final SparkContext sc$1;

            public final boolean apply(SparkContext x$31) {
               return x$31 != this.sc$1;
            }

            public {
               this.sc$1 = sc$1;
            }
         }).foreach(new Serializable(allowMultipleContexts) {
            public static final long serialVersionUID = 0L;
            private final boolean allowMultipleContexts$1;

            public final void apply(SparkContext ctx) {
               String errMsg = (new StringBuilder()).append((Object)"Only one SparkContext may be running in this JVM (see SPARK-2243). To ignore this error, set spark.driver.allowMultipleContexts = true. ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The currently running SparkContext was created at:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ctx.org$apache$spark$SparkContext$$creationSite().longForm()}))).toString();
               SparkException exception = new SparkException(errMsg);
               if (this.allowMultipleContexts$1) {
                  SparkContext$.MODULE$.logWarning(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Multiple running SparkContexts detected in the same JVM!";
                     }
                  }, exception);
               } else {
                  throw exception;
               }
            }

            public {
               this.allowMultipleContexts$1 = allowMultipleContexts$1;
            }
         });
         this.contextBeingConstructed().filter(new Serializable(sc) {
            public static final long serialVersionUID = 0L;
            private final SparkContext sc$1;

            public final boolean apply(SparkContext x$32) {
               return x$32 != this.sc$1;
            }

            public {
               this.sc$1 = sc$1;
            }
         }).foreach(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final void apply(SparkContext otherContext) {
               String otherContextCreationSite = (String)Option$.MODULE$.apply(otherContext.org$apache$spark$SparkContext$$creationSite()).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply(CallSite x$33) {
                     return x$33.longForm();
                  }
               }).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "unknown location";
                  }
               });
               String warnMsg = (new StringBuilder()).append((Object)"Another SparkContext is being constructed (or threw an exception in its constructor).  This may indicate an error, since only one SparkContext may be running in this JVM (see SPARK-2243).").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" The other SparkContext was created at:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{otherContextCreationSite}))).toString();
               SparkContext$.MODULE$.logWarning(new Serializable(this, warnMsg) {
                  public static final long serialVersionUID = 0L;
                  private final String warnMsg$1;

                  public final String apply() {
                     return this.warnMsg$1;
                  }

                  public {
                     this.warnMsg$1 = warnMsg$1;
                  }
               });
            }
         });
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public SparkContext getOrCreate(SparkConf config) {
      Object var3;
      synchronized(this.SPARK_CONTEXT_CONSTRUCTOR_LOCK()) {
         if (this.org$apache$spark$SparkContext$$activeContext().get() == null) {
            this.setActiveContext(new SparkContext(config), false);
         } else if (Predef$.MODULE$.refArrayOps(config.getAll()).nonEmpty()) {
            this.logWarning(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Using an existing SparkContext; some configuration may not take effect.";
               }
            });
         }

         var3 = this.org$apache$spark$SparkContext$$activeContext().get();
      }

      return (SparkContext)var3;
   }

   public SparkContext getOrCreate() {
      Object var2;
      synchronized(this.SPARK_CONTEXT_CONSTRUCTOR_LOCK()) {
         if (this.org$apache$spark$SparkContext$$activeContext().get() == null) {
            this.setActiveContext(new SparkContext(), false);
         }

         var2 = this.org$apache$spark$SparkContext$$activeContext().get();
      }

      return (SparkContext)var2;
   }

   public Option<SparkContext> getActive() {
      Option var2;
      synchronized(this.SPARK_CONTEXT_CONSTRUCTOR_LOCK()) {
         var2 = Option$.MODULE$.apply(this.org$apache$spark$SparkContext$$activeContext().get());
      }

      return var2;
   }

   public void markPartiallyConstructed(SparkContext sc, boolean allowMultipleContexts) {
      synchronized(this.SPARK_CONTEXT_CONSTRUCTOR_LOCK()) {
         this.assertNoOtherContextIsRunning(sc, allowMultipleContexts);
         this.contextBeingConstructed_$eq(new Some(sc));
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public void setActiveContext(SparkContext sc, boolean allowMultipleContexts) {
      synchronized(this.SPARK_CONTEXT_CONSTRUCTOR_LOCK()) {
         this.assertNoOtherContextIsRunning(sc, allowMultipleContexts);
         this.contextBeingConstructed_$eq(None$.MODULE$);
         this.org$apache$spark$SparkContext$$activeContext().set(sc);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public void clearActiveContext() {
      synchronized(this.SPARK_CONTEXT_CONSTRUCTOR_LOCK()) {
         this.org$apache$spark$SparkContext$$activeContext().set((Object)null);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public String SPARK_JOB_DESCRIPTION() {
      return this.SPARK_JOB_DESCRIPTION;
   }

   public String SPARK_JOB_GROUP_ID() {
      return this.SPARK_JOB_GROUP_ID;
   }

   public String SPARK_JOB_INTERRUPT_ON_CANCEL() {
      return this.SPARK_JOB_INTERRUPT_ON_CANCEL;
   }

   public String RDD_SCOPE_KEY() {
      return this.RDD_SCOPE_KEY;
   }

   public String RDD_SCOPE_NO_OVERRIDE_KEY() {
      return this.RDD_SCOPE_NO_OVERRIDE_KEY;
   }

   public String DRIVER_IDENTIFIER() {
      return this.DRIVER_IDENTIFIER;
   }

   public String LEGACY_DRIVER_IDENTIFIER() {
      return this.LEGACY_DRIVER_IDENTIFIER;
   }

   private <T extends Writable> ArrayWritable arrayToArrayWritable(Traversable<T> arr, ClassTag<T> evidence$19) {
      return new ArrayWritable(scala.reflect.package$.MODULE$.classTag(evidence$19).runtimeClass(), (Writable[])((TraversableOnce)arr.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Writable apply(T x) {
            return SparkContext$.MODULE$.org$apache$spark$SparkContext$$anyToWritable$1(x);
         }
      }, Traversable$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Writable.class)));
   }

   public Option<String> jarOfClass(Class<?> cls) {
      URL uri = cls.getResource((new StringBuilder()).append((Object)"/").append((Object)cls.getName().replace('.', '/')).append((Object)".class").toString());
      Object var10000;
      if (uri == null) {
         var10000 = None$.MODULE$;
      } else {
         String uriStr = uri.toString();
         var10000 = uriStr.startsWith("jar:file:") ? new Some(uriStr.substring("jar:file:".length(), uriStr.indexOf(33))) : None$.MODULE$;
      }

      return (Option<String>)var10000;
   }

   public Option<String> jarOfObject(Object obj) {
      return this.jarOfClass(obj.getClass());
   }

   public SparkConf updatedConf(SparkConf conf, String master, String appName, String sparkHome, Seq<String> jars, Map<String, String> environment) {
      SparkConf res = conf.clone();
      res.setMaster(master);
      res.setAppName(appName);
      if (sparkHome == null) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         res.setSparkHome(sparkHome);
      }

      if (jars != null && !jars.isEmpty()) {
         res.setJars(jars);
      } else {
         BoxedUnit var8 = BoxedUnit.UNIT;
      }

      res.setExecutorEnv(environment.toSeq());
      return res;
   }

   public String updatedConf$default$4() {
      return null;
   }

   public Seq<String> updatedConf$default$5() {
      return Nil$.MODULE$;
   }

   public Map<String, String> updatedConf$default$6() {
      return (Map)Map$.MODULE$.apply(Nil$.MODULE$);
   }

   public int numDriverCores(String master) {
      int var3;
      if ("local".equals(master)) {
         var3 = 1;
      } else {
         Option var4 = SparkMasterRegex$.MODULE$.LOCAL_N_REGEX().unapplySeq((CharSequence)master);
         if (!var4.isEmpty() && var4.get() != null && ((LinearSeqOptimized)var4.get()).lengthCompare(1) == 0) {
            String threads = (String)((LinearSeqOptimized)var4.get()).apply(0);
            var3 = this.convertToInt$1(threads);
         } else {
            Option var6 = SparkMasterRegex$.MODULE$.LOCAL_N_FAILURES_REGEX().unapplySeq((CharSequence)master);
            if (!var6.isEmpty() && var6.get() != null && ((LinearSeqOptimized)var6.get()).lengthCompare(2) == 0) {
               String threads = (String)((LinearSeqOptimized)var6.get()).apply(0);
               var3 = this.convertToInt$1(threads);
            } else {
               var3 = 0;
            }
         }
      }

      return var3;
   }

   public Tuple2<SchedulerBackend, TaskScheduler> org$apache$spark$SparkContext$$createTaskScheduler(SparkContext sc, String master, String deployMode) {
      int MAX_LOCAL_TASK_FAILURES = 1;
      String var5 = master;
      Tuple2 var6;
      if ("local".equals(master)) {
         TaskSchedulerImpl scheduler = new TaskSchedulerImpl(sc, MAX_LOCAL_TASK_FAILURES, true);
         LocalSchedulerBackend backend = new LocalSchedulerBackend(sc.getConf(), scheduler, 1);
         scheduler.initialize(backend);
         var6 = new Tuple2(backend, scheduler);
      } else {
         Option var9 = SparkMasterRegex$.MODULE$.LOCAL_N_REGEX().unapplySeq((CharSequence)master);
         if (!var9.isEmpty() && var9.get() != null && ((LinearSeqOptimized)var9.get()).lengthCompare(1) == 0) {
            int var52;
            label180: {
               label179: {
                  String threads = (String)((LinearSeqOptimized)var9.get()).apply(0);
                  String var12 = "*";
                  if (threads == null) {
                     if (var12 == null) {
                        break label179;
                     }
                  } else if (threads.equals(var12)) {
                     break label179;
                  }

                  var52 = (new StringOps(Predef$.MODULE$.augmentString(threads))).toInt();
                  break label180;
               }

               var52 = this.localCpuCount$1();
            }

            int threadCount = var52;
            if (threadCount <= 0) {
               throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to run locally with ", " threads"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(threadCount)})));
            }

            TaskSchedulerImpl scheduler = new TaskSchedulerImpl(sc, MAX_LOCAL_TASK_FAILURES, true);
            LocalSchedulerBackend backend = new LocalSchedulerBackend(sc.getConf(), scheduler, threadCount);
            scheduler.initialize(backend);
            var6 = new Tuple2(backend, scheduler);
         } else {
            Option var15 = SparkMasterRegex$.MODULE$.LOCAL_N_FAILURES_REGEX().unapplySeq((CharSequence)master);
            if (!var15.isEmpty() && var15.get() != null && ((LinearSeqOptimized)var15.get()).lengthCompare(2) == 0) {
               String maxFailures;
               int var51;
               label198: {
                  label197: {
                     String threads = (String)((LinearSeqOptimized)var15.get()).apply(0);
                     maxFailures = (String)((LinearSeqOptimized)var15.get()).apply(1);
                     String var19 = "*";
                     if (threads == null) {
                        if (var19 == null) {
                           break label197;
                        }
                     } else if (threads.equals(var19)) {
                        break label197;
                     }

                     var51 = (new StringOps(Predef$.MODULE$.augmentString(threads))).toInt();
                     break label198;
                  }

                  var51 = this.localCpuCount$2();
               }

               int threadCount = var51;
               TaskSchedulerImpl scheduler = new TaskSchedulerImpl(sc, (new StringOps(Predef$.MODULE$.augmentString(maxFailures))).toInt(), true);
               LocalSchedulerBackend backend = new LocalSchedulerBackend(sc.getConf(), scheduler, threadCount);
               scheduler.initialize(backend);
               var6 = new Tuple2(backend, scheduler);
            } else {
               Option var22 = SparkMasterRegex$.MODULE$.SPARK_REGEX().unapplySeq((CharSequence)master);
               if (!var22.isEmpty() && var22.get() != null && ((LinearSeqOptimized)var22.get()).lengthCompare(1) == 0) {
                  String sparkUrl = (String)((LinearSeqOptimized)var22.get()).apply(0);
                  TaskSchedulerImpl scheduler = new TaskSchedulerImpl(sc);
                  String[] masterUrls = (String[])Predef$.MODULE$.refArrayOps((Object[])sparkUrl.split(",")).map(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final String apply(String x$34) {
                        return (new StringBuilder()).append((Object)"spark://").append((Object)x$34).toString();
                     }
                  }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
                  StandaloneSchedulerBackend backend = new StandaloneSchedulerBackend(scheduler, sc, masterUrls);
                  scheduler.initialize(backend);
                  var6 = new Tuple2(backend, scheduler);
               } else {
                  Option var27 = SparkMasterRegex$.MODULE$.LOCAL_CLUSTER_REGEX().unapplySeq((CharSequence)master);
                  if (!var27.isEmpty() && var27.get() != null && ((LinearSeqOptimized)var27.get()).lengthCompare(3) == 0) {
                     String numSlaves = (String)((LinearSeqOptimized)var27.get()).apply(0);
                     String coresPerSlave = (String)((LinearSeqOptimized)var27.get()).apply(1);
                     String memoryPerSlave = (String)((LinearSeqOptimized)var27.get()).apply(2);
                     int memoryPerSlaveInt = (new StringOps(Predef$.MODULE$.augmentString(memoryPerSlave))).toInt();
                     if (sc.executorMemory() > memoryPerSlaveInt) {
                        throw new SparkException((new StringOps(Predef$.MODULE$.augmentString("Asked to launch cluster with %d MB RAM / worker but requested %d MB/worker"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(memoryPerSlaveInt), BoxesRunTime.boxToInteger(sc.executorMemory())})));
                     }

                     TaskSchedulerImpl scheduler = new TaskSchedulerImpl(sc);
                     LocalSparkCluster localCluster = new LocalSparkCluster((new StringOps(Predef$.MODULE$.augmentString(numSlaves))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(coresPerSlave))).toInt(), memoryPerSlaveInt, sc.conf());
                     String[] masterUrls = localCluster.start();
                     StandaloneSchedulerBackend backend = new StandaloneSchedulerBackend(scheduler, sc, masterUrls);
                     scheduler.initialize(backend);
                     backend.shutdownCallback_$eq(new Serializable(localCluster) {
                        public static final long serialVersionUID = 0L;
                        private final LocalSparkCluster localCluster$1;

                        public final void apply(StandaloneSchedulerBackend backend) {
                           this.localCluster$1.stop();
                        }

                        public {
                           this.localCluster$1 = localCluster$1;
                        }
                     });
                     var6 = new Tuple2(backend, scheduler);
                  } else {
                     Option var37 = this.getClusterManager(master);
                     if (!(var37 instanceof Some)) {
                        if (None$.MODULE$.equals(var37)) {
                           throw new SparkException((new StringBuilder()).append((Object)"Could not parse Master URL: '").append((Object)master).append((Object)"'").toString());
                        }

                        throw new MatchError(var37);
                     }

                     Some var38 = (Some)var37;
                     ExternalClusterManager clusterMgr = (ExternalClusterManager)var38.x();
                     ExternalClusterManager cm = clusterMgr;

                     Tuple2 var10000;
                     try {
                        TaskScheduler scheduler = cm.createTaskScheduler(sc, var5);
                        SchedulerBackend backend = cm.createSchedulerBackend(sc, var5, scheduler);
                        cm.initialize(scheduler, backend);
                        var10000 = new Tuple2(backend, scheduler);
                     } catch (Throwable var50) {
                        if (var50 instanceof SparkException) {
                           SparkException var43 = (SparkException)var50;
                           throw var43;
                        }

                        Option var45 = NonFatal$.MODULE$.unapply(var50);
                        if (var45.isEmpty()) {
                           throw var50;
                        }

                        Throwable e = (Throwable)var45.get();
                        throw new SparkException("External scheduler cannot be instantiated", e);
                     }

                     var6 = var10000;
                  }
               }
            }
         }
      }

      return var6;
   }

   private Option<ExternalClusterManager> getClusterManager(String url) {
      ClassLoader loader = Utils$.MODULE$.getContextOrSparkClassLoader();
      Iterable serviceLoaders = (Iterable)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(ServiceLoader.load(ExternalClusterManager.class, loader)).asScala()).filter(new Serializable(url) {
         public static final long serialVersionUID = 0L;
         private final String url$1;

         public final boolean apply(ExternalClusterManager x$35) {
            return x$35.canCreate(this.url$1);
         }

         public {
            this.url$1 = url$1;
         }
      });
      if (serviceLoaders.size() > 1) {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Multiple external cluster managers registered for the url ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{url, serviceLoaders})));
      } else {
         return serviceLoaders.headOption();
      }
   }

   public String $lessinit$greater$default$3() {
      return null;
   }

   public Seq<String> $lessinit$greater$default$4() {
      return Nil$.MODULE$;
   }

   public Map<String, String> $lessinit$greater$default$5() {
      return (Map)Map$.MODULE$.apply(Nil$.MODULE$);
   }

   public final Writable org$apache$spark$SparkContext$$anyToWritable$1(Writable u) {
      return u;
   }

   private final int convertToInt$1(String threads) {
      int var10000;
      label23: {
         String var2 = "*";
         if (threads == null) {
            if (var2 == null) {
               break label23;
            }
         } else if (threads.equals(var2)) {
            break label23;
         }

         var10000 = (new StringOps(Predef$.MODULE$.augmentString(threads))).toInt();
         return var10000;
      }

      var10000 = Runtime.getRuntime().availableProcessors();
      return var10000;
   }

   private final int localCpuCount$1() {
      return Runtime.getRuntime().availableProcessors();
   }

   private final int localCpuCount$2() {
      return Runtime.getRuntime().availableProcessors();
   }

   private SparkContext$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.org$apache$spark$SparkContext$$VALID_LOG_LEVELS = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ALL", "DEBUG", "ERROR", "FATAL", "INFO", "OFF", "TRACE", "WARN"})));
      this.SPARK_CONTEXT_CONSTRUCTOR_LOCK = new Object();
      this.org$apache$spark$SparkContext$$activeContext = new AtomicReference((Object)null);
      this.contextBeingConstructed = None$.MODULE$;
      this.SPARK_JOB_DESCRIPTION = "spark.job.description";
      this.SPARK_JOB_GROUP_ID = "spark.jobGroup.id";
      this.SPARK_JOB_INTERRUPT_ON_CANCEL = "spark.job.interruptOnCancel";
      this.RDD_SCOPE_KEY = "spark.rdd.scope";
      this.RDD_SCOPE_NO_OVERRIDE_KEY = "spark.rdd.scope.noOverride";
      this.DRIVER_IDENTIFIER = "driver";
      this.LEGACY_DRIVER_IDENTIFIER = "<driver>";
   }
}
