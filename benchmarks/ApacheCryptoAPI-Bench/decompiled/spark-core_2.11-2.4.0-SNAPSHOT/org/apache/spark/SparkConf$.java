package org.apache.spark;

import org.apache.spark.deploy.history.config$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.NonLocalReturnControl$mcV$sp;
import scala.runtime.Nothing$;

public final class SparkConf$ implements Logging, Serializable {
   public static final SparkConf$ MODULE$;
   private final Map<String, SparkConf.DeprecatedConfig> deprecatedConfigs;
   private final Map<String, Seq<SparkConf.AlternateConfig>> org$apache$spark$SparkConf$$configsWithAlternatives;
   private final Map<String, Tuple2<String, SparkConf.AlternateConfig>> allAlternatives;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SparkConf$();
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

   private Map<String, SparkConf.DeprecatedConfig> deprecatedConfigs() {
      return this.deprecatedConfigs;
   }

   public Map<String, Seq<SparkConf.AlternateConfig>> org$apache$spark$SparkConf$$configsWithAlternatives() {
      return this.org$apache$spark$SparkConf$$configsWithAlternatives;
   }

   private Map<String, Tuple2<String, SparkConf.AlternateConfig>> allAlternatives() {
      return this.allAlternatives;
   }

   public boolean isExecutorStartupConf(String name) {
      boolean var10000;
      label26: {
         if (name.startsWith("spark.auth")) {
            String var2 = SecurityManager$.MODULE$.SPARK_AUTH_SECRET_CONF();
            if (name == null) {
               if (var2 != null) {
                  break label26;
               }
            } else if (!name.equals(var2)) {
               break label26;
            }
         }

         if (!name.startsWith("spark.ssl") && !name.startsWith("spark.rpc") && !name.startsWith("spark.network") && !this.isSparkPortConf(name)) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public boolean isSparkPortConf(String name) {
      return name.startsWith("spark.") && name.endsWith(".port") || name.startsWith("spark.port.");
   }

   public Option<String> getDeprecatedConfig(String key, java.util.Map<String, String> conf) {
      return this.org$apache$spark$SparkConf$$configsWithAlternatives().get(key).<String>flatMap(new Serializable(conf) {
         public static final long serialVersionUID = 0L;
         public final java.util.Map conf$1;

         public final Option<String> apply(Seq<SparkConf.AlternateConfig> alts) {
            return alts.collectFirst(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final <A1 extends SparkConf.AlternateConfig, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
                  Object var4;
                  if (this.$outer.conf$1.containsKey(x1.key())) {
                     String value = (String)this.$outer.conf$1.get(x1.key());
                     var4 = x1.translation() == null ? value : x1.translation().apply(value);
                  } else {
                     var4 = var2.apply(x1);
                  }

                  return (B1)var4;
               }

               public final boolean isDefinedAt(SparkConf.AlternateConfig x1) {
                  boolean var3;
                  if (this.$outer.conf$1.containsKey(x1.key())) {
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
            });
         }

         public {
            this.conf$1 = conf$1;
         }
      });
   }

   public void logDeprecationWarning(String key) {
      Object var2 = new Object();

      try {
         this.deprecatedConfigs().get(key).foreach(new Serializable(key, var2) {
            public static final long serialVersionUID = 0L;
            public final String key$3;
            private final Object nonLocalReturnKey1$1;

            public final Nothing$ apply(SparkConf.DeprecatedConfig cfg) {
               SparkConf$.MODULE$.logWarning(new Serializable(this, cfg) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final SparkConf.DeprecatedConfig cfg$2;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The configuration key '", "' has been deprecated as of Spark ", " and "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.key$3, this.cfg$2.version()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"may be removed in the future. ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.cfg$2.deprecationMessage()}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.cfg$2 = cfg$2;
                     }
                  }
               });
               throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
            }

            public {
               this.key$3 = key$3;
               this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
            }
         });
         this.allAlternatives().get(key).foreach(new Serializable(key, var2) {
            public static final long serialVersionUID = 0L;
            public final String key$3;
            private final Object nonLocalReturnKey1$1;

            public final Nothing$ apply(Tuple2<String, SparkConf.AlternateConfig> x0$7) {
               if (x0$7 != null) {
                  String newKey = (String)x0$7._1();
                  SparkConf.AlternateConfig cfg = (SparkConf.AlternateConfig)x0$7._2();
                  SparkConf$.MODULE$.logWarning(new Serializable(this, newKey, cfg) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final String newKey$1;
                     private final SparkConf.AlternateConfig cfg$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The configuration key '", "' has been deprecated as of Spark ", " and "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.key$3, this.cfg$1.version()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"may be removed in the future. Please use the new key '", "' instead."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.newKey$1}))).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.newKey$1 = newKey$1;
                           this.cfg$1 = cfg$1;
                        }
                     }
                  });
                  throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
               } else {
                  throw new MatchError(x0$7);
               }
            }

            public {
               this.key$3 = key$3;
               this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
            }
         });
         if (key.startsWith("spark.akka") || key.startsWith("spark.ssl.akka")) {
            this.logWarning(new Serializable(key) {
               public static final long serialVersionUID = 0L;
               private final String key$3;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The configuration key ", " is not supported any more "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.key$3}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"because Spark doesn't use Akka since 2.0"})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.key$3 = key$3;
               }
            });
         }
      } catch (NonLocalReturnControl var4) {
         if (var4.key() != var2) {
            throw var4;
         }

         var4.value$mcV$sp();
      }

   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkConf$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      Seq configs = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.DeprecatedConfig[]{new SparkConf.DeprecatedConfig("spark.cache.class", "0.8", "The spark.cache.class property is no longer being used! Specify storage levels using the RDD.persist() method instead."), new SparkConf.DeprecatedConfig("spark.yarn.user.classpath.first", "1.3", "Please use spark.{driver,executor}.userClassPathFirst instead."), new SparkConf.DeprecatedConfig("spark.kryoserializer.buffer.mb", "1.4", "Please use spark.kryoserializer.buffer instead. The default value for spark.kryoserializer.buffer.mb was previously specified as '0.064'. Fractional values are no longer accepted. To specify the equivalent now, one may use '64k'."), new SparkConf.DeprecatedConfig("spark.rpc", "2.0", "Not used any more."), new SparkConf.DeprecatedConfig("spark.scheduler.executorTaskBlacklistTime", "2.1.0", "Please use the new blacklisting options, spark.blacklist.*"), new SparkConf.DeprecatedConfig("spark.yarn.am.port", "2.0.0", "Not used any more"), new SparkConf.DeprecatedConfig("spark.executor.port", "2.0.0", "Not used any more"), new SparkConf.DeprecatedConfig("spark.shuffle.service.index.cache.entries", "2.3.0", "Not used any more. Please use spark.shuffle.service.index.cache.size")}));
      this.deprecatedConfigs = (Map)Predef$.MODULE$.Map().apply((Seq)configs.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, SparkConf.DeprecatedConfig> apply(SparkConf.DeprecatedConfig cfg) {
            return Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(cfg.key()), cfg);
         }
      }, Seq$.MODULE$.canBuildFrom()));
      this.org$apache$spark$SparkConf$$configsWithAlternatives = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.executor.userClassPathFirst"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.files.userClassPathFirst", "1.3", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.history.fs.update.interval"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.history.fs.update.interval.seconds", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3()), new SparkConf.AlternateConfig("spark.history.fs.updateInterval", "1.3", SparkConf.AlternateConfig$.MODULE$.apply$default$3()), new SparkConf.AlternateConfig("spark.history.updateInterval", "1.3", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.history.fs.cleaner.interval"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.history.fs.cleaner.interval.seconds", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(config$.MODULE$.MAX_LOG_AGE_S().key()), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.history.fs.cleaner.maxAge.seconds", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.yarn.am.waitTime"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.yarn.applicationMaster.waitTries", "1.3", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String s) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "s"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong((new StringOps(Predef$.MODULE$.augmentString(s))).toLong() * 10L)}));
         }
      })}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.reducer.maxSizeInFlight"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.reducer.maxMbInFlight", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.kryoserializer.buffer"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.kryoserializer.buffer.mb", "1.4", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String s) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "k"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger((int)((new StringOps(Predef$.MODULE$.augmentString(s))).toDouble() * (double)1000))}));
         }
      })}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.kryoserializer.buffer.max"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.kryoserializer.buffer.max.mb", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.shuffle.file.buffer"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.shuffle.file.buffer.kb", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.executor.logs.rolling.maxSize"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.executor.logs.rolling.size.maxBytes", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.io.compression.snappy.blockSize"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.io.compression.snappy.block.size", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.io.compression.lz4.blockSize"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.io.compression.lz4.block.size", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.rpc.numRetries"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.akka.num.retries", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.rpc.retry.wait"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.akka.retry.wait", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.rpc.askTimeout"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.akka.askTimeout", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.rpc.lookupTimeout"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.akka.lookupTimeout", "1.4", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.streaming.fileStream.minRememberDuration"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.streaming.minRememberDuration", "1.5", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.yarn.max.executor.failures"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.yarn.max.worker.failures", "1.5", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(org.apache.spark.internal.config.package$.MODULE$.MEMORY_OFFHEAP_ENABLED().key()), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.unsafe.offHeap", "1.6", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.rpc.message.maxSize"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.akka.frameSize", "1.6", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.yarn.jars"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.yarn.jar", "2.0", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("spark.yarn.access.hadoopFileSystems"), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.yarn.access.namenodes", "2.2", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(org.apache.spark.internal.config.package$.MODULE$.MAX_REMOTE_BLOCK_SIZE_FETCH_TO_MEM().key()), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.reducer.maxReqSizeShuffleToMem", "2.3", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(org.apache.spark.internal.config.package$.MODULE$.LISTENER_BUS_EVENT_QUEUE_CAPACITY().key()), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.scheduler.listenerbus.eventqueue.size", "2.3", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(org.apache.spark.internal.config.package$.MODULE$.DRIVER_MEMORY_OVERHEAD().key()), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.yarn.driver.memoryOverhead", "2.3", SparkConf.AlternateConfig$.MODULE$.apply$default$3())}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(org.apache.spark.internal.config.package$.MODULE$.EXECUTOR_MEMORY_OVERHEAD().key()), Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new SparkConf.AlternateConfig[]{new SparkConf.AlternateConfig("spark.yarn.executor.memoryOverhead", "2.3", SparkConf.AlternateConfig$.MODULE$.apply$default$3())})))}));
      this.allAlternatives = ((TraversableOnce)this.org$apache$spark$SparkConf$$configsWithAlternatives().keys().flatMap(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<Tuple2<String, Tuple2<String, SparkConf.AlternateConfig>>> apply(String key) {
            return (Seq)((TraversableLike)SparkConf$.MODULE$.org$apache$spark$SparkConf$$configsWithAlternatives().apply(key)).map(new Serializable(this, key) {
               public static final long serialVersionUID = 0L;
               private final String key$4;

               public final Tuple2<String, Tuple2<String, SparkConf.AlternateConfig>> apply(SparkConf.AlternateConfig cfg) {
                  return Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(cfg.key()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(this.key$4), cfg));
               }

               public {
                  this.key$4 = key$4;
               }
            }, Seq$.MODULE$.canBuildFrom());
         }
      }, Iterable$.MODULE$.canBuildFrom())).<String, Tuple2<String, SparkConf.AlternateConfig>>toMap(Predef$.MODULE$.$conforms());
   }
}
