package org.apache.spark.metrics;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
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
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.LinearSeqOptimized;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;
import scala.util.matching.Regex;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015a!B\u0001\u0003\u0001\u0011Q!!D'fiJL7m]\"p]\u001aLwM\u0003\u0002\u0004\t\u00059Q.\u001a;sS\u000e\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Ui\u0011a\u0005\u0006\u0003)\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003-M\u0011q\u0001T8hO&tw\r\u0003\u0005\u0019\u0001\t\u0005\t\u0015!\u0003\u001b\u0003\u0011\u0019wN\u001c4\u0004\u0001A\u00111\u0004H\u0007\u0002\t%\u0011Q\u0004\u0002\u0002\n'B\f'o[\"p]\u001aDQa\b\u0001\u0005\u0002\u0001\na\u0001P5oSRtDCA\u0011$!\t\u0011\u0003!D\u0001\u0003\u0011\u0015Ab\u00041\u0001\u001b\u0011\u001d)\u0003A1A\u0005\n\u0019\na\u0002R#G\u0003VcEk\u0018)S\u000b\u001aK\u0005,F\u0001(!\tAS&D\u0001*\u0015\tQ3&\u0001\u0003mC:<'\"\u0001\u0017\u0002\t)\fg/Y\u0005\u0003]%\u0012aa\u0015;sS:<\u0007B\u0002\u0019\u0001A\u0003%q%A\bE\u000b\u001a\u000bU\u000b\u0014+`!J+e)\u0013-!\u0011\u001d\u0011\u0004A1A\u0005\nM\na\"\u0013(T)\u0006s5)R0S\u000b\u001e+\u0005,F\u00015!\t)$(D\u00017\u0015\t9\u0004(\u0001\u0005nCR\u001c\u0007.\u001b8h\u0015\tIT\"\u0001\u0003vi&d\u0017BA\u001e7\u0005\u0015\u0011VmZ3y\u0011\u0019i\u0004\u0001)A\u0005i\u0005y\u0011JT*U\u0003:\u001bUi\u0018*F\u000f\u0016C\u0006\u0005C\u0004@\u0001\t\u0007I\u0011\u0002\u0014\u0002;\u0011+e)Q+M)~kU\t\u0016*J\u0007N{6i\u0014(G?\u001aKE*\u0012(B\u001b\u0016Ca!\u0011\u0001!\u0002\u00139\u0013A\b#F\r\u0006+F\nV0N\u000bR\u0013\u0016jQ*`\u0007>sei\u0018$J\u0019\u0016s\u0015)T#!\u0011!\u0019\u0005A1A\u0005\u0002\t!\u0015A\u00039s_B,'\u000f^5fgV\tQ\t\u0005\u0002G\u00116\tqI\u0003\u0002:W%\u0011\u0011j\u0012\u0002\u000b!J|\u0007/\u001a:uS\u0016\u001c\bBB&\u0001A\u0003%Q)A\u0006qe>\u0004XM\u001d;jKN\u0004\u0003\u0002C'\u0001\u0001\u0004%\tA\u0001(\u00021A,'/\u00138ti\u0006t7-Z*vEB\u0013x\u000e]3si&,7/F\u0001P!\u0011\u0001VkV#\u000e\u0003ES!AU*\u0002\u000f5,H/\u00192mK*\u0011A+D\u0001\u000bG>dG.Z2uS>t\u0017B\u0001,R\u0005\u001dA\u0015m\u001d5NCB\u0004\"\u0001W.\u000f\u00051I\u0016B\u0001.\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011a\u0006\u0018\u0006\u000356A\u0001B\u0018\u0001A\u0002\u0013\u0005!aX\u0001\u001da\u0016\u0014\u0018J\\:uC:\u001cWmU;c!J|\u0007/\u001a:uS\u0016\u001cx\fJ3r)\t\u00017\r\u0005\u0002\rC&\u0011!-\u0004\u0002\u0005+:LG\u000fC\u0004e;\u0006\u0005\t\u0019A(\u0002\u0007a$\u0013\u0007\u0003\u0004g\u0001\u0001\u0006KaT\u0001\u001aa\u0016\u0014\u0018J\\:uC:\u001cWmU;c!J|\u0007/\u001a:uS\u0016\u001c\b\u0005C\u0003i\u0001\u0011%\u0011.\u0001\u000btKR$UMZ1vYR\u0004&o\u001c9feRLWm\u001d\u000b\u0003A*DQa[4A\u0002\u0015\u000bA\u0001\u001d:pa\")Q\u000e\u0001C\u0001]\u0006Q\u0011N\\5uS\u0006d\u0017N_3\u0015\u0003\u0001DQ\u0001\u001d\u0001\u0005\u0002E\fQb];c!J|\u0007/\u001a:uS\u0016\u001cHcA(sg\")1n\u001ca\u0001\u000b\")Ao\u001ca\u0001i\u0005)!/Z4fq\")a\u000f\u0001C\u0001o\u0006Yq-\u001a;J]N$\u0018M\\2f)\t)\u0005\u0010C\u0003zk\u0002\u0007q+\u0001\u0003j]N$\bBB>\u0001A\u0013%A0\u0001\fm_\u0006$\u0007K]8qKJ$\u0018.Z:Ge>lg)\u001b7f)\t\u0001W\u0010C\u0003\u007fu\u0002\u0007q0\u0001\u0003qCRD\u0007\u0003\u0002\u0007\u0002\u0002]K1!a\u0001\u000e\u0005\u0019y\u0005\u000f^5p]\u0002"
)
public class MetricsConfig implements Logging {
   private final SparkConf conf;
   private final String org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX;
   private final Regex INSTANCE_REGEX;
   private final String org$apache$spark$metrics$MetricsConfig$$DEFAULT_METRICS_CONF_FILENAME;
   private final Properties properties;
   private HashMap<String, Properties> perInstanceSubProperties;
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

   public String org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX() {
      return this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX;
   }

   private Regex INSTANCE_REGEX() {
      return this.INSTANCE_REGEX;
   }

   public String org$apache$spark$metrics$MetricsConfig$$DEFAULT_METRICS_CONF_FILENAME() {
      return this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_METRICS_CONF_FILENAME;
   }

   public Properties properties() {
      return this.properties;
   }

   public HashMap<String, Properties> perInstanceSubProperties() {
      return this.perInstanceSubProperties;
   }

   public void perInstanceSubProperties_$eq(HashMap<String, Properties> x$1) {
      this.perInstanceSubProperties = x$1;
   }

   private void setDefaultProperties(Properties prop) {
      prop.setProperty("*.sink.servlet.class", "org.apache.spark.metrics.sink.MetricsServlet");
      prop.setProperty("*.sink.servlet.path", "/metrics/json");
      prop.setProperty("master.sink.servlet.path", "/metrics/master/json");
      prop.setProperty("applications.sink.servlet.path", "/metrics/applications/json");
   }

   public void initialize() {
      this.setDefaultProperties(this.properties());
      this.loadPropertiesFromFile(this.conf.getOption("spark.metrics.conf"));
      String prefix = "spark.metrics.conf.";
      Predef$.MODULE$.refArrayOps(this.conf.getAll()).foreach(new Serializable(this, prefix) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MetricsConfig $outer;
         private final String prefix$1;

         public final Object apply(Tuple2<String, String> x0$1) {
            Object var5;
            if (x0$1 != null) {
               String k = (String)x0$1._1();
               String v = (String)x0$1._2();
               if (k.startsWith(this.prefix$1)) {
                  var5 = this.$outer.properties().setProperty(k.substring(this.prefix$1.length()), v);
                  return var5;
               }
            }

            var5 = BoxedUnit.UNIT;
            return var5;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.prefix$1 = prefix$1;
            }
         }
      });
      this.perInstanceSubProperties_$eq(this.subProperties(this.properties(), this.INSTANCE_REGEX()));
      if (this.perInstanceSubProperties().contains(this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX())) {
         Map defaultSubProperties = (Map)JavaConverters$.MODULE$.propertiesAsScalaMapConverter((Properties)this.perInstanceSubProperties().apply(this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX())).asScala();
         this.perInstanceSubProperties().withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<String, Properties> check$ifrefutable$1) {
               boolean var3;
               if (check$ifrefutable$1 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         }).withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final MetricsConfig $outer;

            public final boolean apply(Tuple2<String, Properties> x$1) {
               if (x$1 == null) {
                  throw new MatchError(x$1);
               } else {
                  boolean var10000;
                  label20: {
                     label19: {
                        String instance = (String)x$1._1();
                        String var5 = this.$outer.org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX();
                        if (instance == null) {
                           if (var5 == null) {
                              break label19;
                           }
                        } else if (instance.equals(var5)) {
                           break label19;
                        }

                        var10000 = true;
                        break label20;
                     }

                     var10000 = false;
                  }

                  boolean var4 = var10000;
                  return var4;
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).foreach(new Serializable(this, defaultSubProperties) {
            public static final long serialVersionUID = 0L;
            private final Map defaultSubProperties$1;

            public final void apply(Tuple2<String, Properties> x$4) {
               if (x$4 != null) {
                  Properties prop = (Properties)x$4._2();
                  this.defaultSubProperties$1.withFilter(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final boolean apply(Tuple2<String, String> check$ifrefutable$2) {
                        boolean var3;
                        if (check$ifrefutable$2 != null) {
                           var3 = true;
                        } else {
                           var3 = false;
                        }

                        return var3;
                     }
                  }).withFilter(new Serializable(this, prop) {
                     public static final long serialVersionUID = 0L;
                     private final Properties prop$1;

                     public final boolean apply(Tuple2<String, String> x$2) {
                        if (x$2 != null) {
                           String k = (String)x$2._1();
                           boolean var4 = this.prop$1.get(k) == null;
                           return var4;
                        } else {
                           throw new MatchError(x$2);
                        }
                     }

                     public {
                        this.prop$1 = prop$1;
                     }
                  }).foreach(new Serializable(this, prop) {
                     public static final long serialVersionUID = 0L;
                     private final Properties prop$1;

                     public final Object apply(Tuple2<String, String> x$3) {
                        if (x$3 != null) {
                           String k = (String)x$3._1();
                           String v = (String)x$3._2();
                           Object var5 = this.prop$1.put(k, v);
                           return var5;
                        } else {
                           throw new MatchError(x$3);
                        }
                     }

                     public {
                        this.prop$1 = prop$1;
                     }
                  });
                  BoxedUnit var4 = BoxedUnit.UNIT;
               } else {
                  throw new MatchError(x$4);
               }
            }

            public {
               this.defaultSubProperties$1 = defaultSubProperties$1;
            }
         });
      }

   }

   public HashMap<String, Properties> subProperties(Properties prop, Regex regex) {
      HashMap subProperties = new HashMap();
      ((IterableLike)JavaConverters$.MODULE$.propertiesAsScalaMapConverter(prop).asScala()).foreach(new Serializable(this, regex, subProperties) {
         public static final long serialVersionUID = 0L;
         private final Regex regex$1;
         private final HashMap subProperties$1;

         public final Object apply(Tuple2<String, String> kv) {
            Object var10000;
            if (this.regex$1.findPrefixOf(((String)kv._1()).toString()).isDefined()) {
               String var3 = ((String)kv._1()).toString();
               Option var4 = this.regex$1.unapplySeq((CharSequence)var3);
               if (var4.isEmpty() || var4.get() == null || ((LinearSeqOptimized)var4.get()).lengthCompare(2) != 0) {
                  throw new MatchError(var3);
               }

               String prefix = (String)((LinearSeqOptimized)var4.get()).apply(0);
               String suffix = (String)((LinearSeqOptimized)var4.get()).apply(1);
               Tuple2 var7 = new Tuple2(prefix, suffix);
               String prefix = (String)var7._1();
               String suffix = (String)var7._2();
               var10000 = ((Properties)this.subProperties$1.getOrElseUpdate(prefix, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Properties apply() {
                     return new Properties();
                  }
               })).setProperty(suffix, ((String)kv._2()).toString());
            } else {
               var10000 = BoxedUnit.UNIT;
            }

            return var10000;
         }

         public {
            this.regex$1 = regex$1;
            this.subProperties$1 = subProperties$1;
         }
      });
      return subProperties;
   }

   public Properties getInstance(String inst) {
      Option var2 = this.perInstanceSubProperties().get(inst);
      Properties var5;
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         Properties s = (Properties)var3.x();
         var5 = s;
      } else {
         if (!None$.MODULE$.equals(var2)) {
            throw new MatchError(var2);
         }

         var5 = (Properties)this.perInstanceSubProperties().getOrElse(this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Properties apply() {
               return new Properties();
            }
         });
      }

      return var5;
   }

   private void loadPropertiesFromFile(Option<String> path) {
      InputStream is = null;

      try {
         Object var9;
         if (path instanceof Some) {
            Some var7 = (Some)path;
            String f = (String)var7.x();
            var9 = new FileInputStream(f);
         } else {
            if (!None$.MODULE$.equals(path)) {
               throw new MatchError(path);
            }

            var9 = Utils$.MODULE$.getSparkClassLoader().getResourceAsStream(this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_METRICS_CONF_FILENAME());
         }

         is = (InputStream)var9;
         if (var9 != null) {
            this.properties().load((InputStream)var9);
         }
      } catch (Exception var12) {
         String file = (String)path.getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final MetricsConfig $outer;

            public final String apply() {
               return this.$outer.org$apache$spark$metrics$MetricsConfig$$DEFAULT_METRICS_CONF_FILENAME();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.logError(new Serializable(this, file) {
            public static final long serialVersionUID = 0L;
            private final String file$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error loading configuration file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1}));
            }

            public {
               this.file$1 = file$1;
            }
         }, var12);
      } finally {
         if (is != null) {
            is.close();
         }

      }

   }

   public MetricsConfig(SparkConf conf) {
      this.conf = conf;
      Logging$class.$init$(this);
      this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_PREFIX = "*";
      this.INSTANCE_REGEX = (new StringOps(Predef$.MODULE$.augmentString("^(\\*|[a-zA-Z]+)\\.(.+)"))).r();
      this.org$apache$spark$metrics$MetricsConfig$$DEFAULT_METRICS_CONF_FILENAME = "metrics.properties";
      this.properties = new Properties();
      this.perInstanceSubProperties = null;
   }
}
