package org.apache.spark.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.package$;
import org.apache.spark.metrics.sink.MetricsServlet;
import org.apache.spark.metrics.sink.Sink;
import org.apache.spark.metrics.source.Source;
import org.apache.spark.metrics.source.StaticSources$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Array$;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;
import scala.util.matching.Regex;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-h!B\u0001\u0003\u0001\u0011Q!!D'fiJL7m]*zgR,WN\u0003\u0002\u0004\t\u00059Q.\u001a;sS\u000e\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Ui\u0011a\u0005\u0006\u0003)\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003-M\u0011q\u0001T8hO&tw\r\u0003\u0005\u0019\u0001\t\u0015\r\u0011\"\u0001\u001b\u0003!Ign\u001d;b]\u000e,7\u0001A\u000b\u00027A\u0011Ad\b\b\u0003\u0019uI!AH\u0007\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0013E\u0001\u0004TiJLgn\u001a\u0006\u0003=5A\u0001b\t\u0001\u0003\u0002\u0003\u0006IaG\u0001\nS:\u001cH/\u00198dK\u0002B\u0001\"\n\u0001\u0003\u0002\u0003\u0006IAJ\u0001\u0005G>tg\r\u0005\u0002(Q5\tA!\u0003\u0002*\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\tW\u0001\u0011\t\u0011)A\u0005Y\u0005Y1/Z2ve&$\u00180T4s!\t9S&\u0003\u0002/\t\ty1+Z2ve&$\u00180T1oC\u001e,'\u000fC\u00031\u0001\u0011%\u0011'\u0001\u0004=S:LGO\u0010\u000b\u0005eQ*d\u0007\u0005\u00024\u00015\t!\u0001C\u0003\u0019_\u0001\u00071\u0004C\u0003&_\u0001\u0007a\u0005C\u0003,_\u0001\u0007A\u0006\u0003\u00049\u0001\u0001\u0006I!O\u0001\u000e[\u0016$(/[2t\u0007>tg-[4\u0011\u0005MR\u0014BA\u001e\u0003\u00055iU\r\u001e:jGN\u001cuN\u001c4jO\"9Q\b\u0001b\u0001\n\u0013q\u0014!B:j].\u001cX#A \u0011\u0007\u0001+u)D\u0001B\u0015\t\u00115)A\u0004nkR\f'\r\\3\u000b\u0005\u0011k\u0011AC2pY2,7\r^5p]&\u0011a)\u0011\u0002\f\u0003J\u0014\u0018-\u001f\"vM\u001a,'\u000f\u0005\u0002I\u00176\t\u0011J\u0003\u0002K\u0005\u0005!1/\u001b8l\u0013\ta\u0015J\u0001\u0003TS:\\\u0007B\u0002(\u0001A\u0003%q(\u0001\u0004tS:\\7\u000f\t\u0005\b!\u0002\u0011\r\u0011\"\u0003R\u0003\u001d\u0019x.\u001e:dKN,\u0012A\u0015\t\u0004\u0001\u0016\u001b\u0006C\u0001+X\u001b\u0005)&B\u0001,\u0003\u0003\u0019\u0019x.\u001e:dK&\u0011\u0001,\u0016\u0002\u0007'>,(oY3\t\ri\u0003\u0001\u0015!\u0003S\u0003!\u0019x.\u001e:dKN\u0004\u0003b\u0002/\u0001\u0005\u0004%I!X\u0001\te\u0016<\u0017n\u001d;ssV\ta\f\u0005\u0002`K6\t\u0001M\u0003\u0002\u0004C*\u0011!mY\u0001\tG>$\u0017\r[1mK*\tA-A\u0002d_6L!A\u001a1\u0003\u001d5+GO]5d%\u0016<\u0017n\u001d;ss\"1\u0001\u000e\u0001Q\u0001\ny\u000b\u0011B]3hSN$(/\u001f\u0011\t\u000f)\u0004\u0001\u0019!C\u0005W\u00069!/\u001e8oS:<W#\u00017\u0011\u00051i\u0017B\u00018\u000e\u0005\u001d\u0011un\u001c7fC:Dq\u0001\u001d\u0001A\u0002\u0013%\u0011/A\u0006sk:t\u0017N\\4`I\u0015\fHC\u0001:v!\ta1/\u0003\u0002u\u001b\t!QK\\5u\u0011\u001d1x.!AA\u00021\f1\u0001\u001f\u00132\u0011\u0019A\b\u0001)Q\u0005Y\u0006A!/\u001e8oS:<\u0007\u0005C\u0004{\u0001\u0001\u0007I\u0011B>\u0002\u001d5,GO]5dgN+'O\u001e7fiV\tA\u0010E\u0002\r{~L!A`\u0007\u0003\r=\u0003H/[8o!\rA\u0015\u0011A\u0005\u0004\u0003\u0007I%AD'fiJL7m]*feZdW\r\u001e\u0005\n\u0003\u000f\u0001\u0001\u0019!C\u0005\u0003\u0013\t!#\\3ue&\u001c7oU3sm2,Go\u0018\u0013fcR\u0019!/a\u0003\t\u0011Y\f)!!AA\u0002qDq!a\u0004\u0001A\u0003&A0A\bnKR\u0014\u0018nY:TKJ4H.\u001a;!\u0011\u001d\t\u0019\u0002\u0001C\u0001\u0003+\t!cZ3u'\u0016\u0014h\u000f\\3u\u0011\u0006tG\r\\3sgV\u0011\u0011q\u0003\t\u0006\u0019\u0005e\u0011QD\u0005\u0004\u00037i!!B!se\u0006L\b\u0003BA\u0010\u0003[i!!!\t\u000b\t\u0005\r\u0012QE\u0001\bg\u0016\u0014h\u000f\\3u\u0015\u0011\t9#!\u000b\u0002\u000b),G\u000f^=\u000b\u0007\u0005-\u0002\"A\u0004fG2L\u0007o]3\n\t\u0005=\u0012\u0011\u0005\u0002\u0016'\u0016\u0014h\u000f\\3u\u0007>tG/\u001a=u\u0011\u0006tG\r\\3s\u0011\u001d\t\u0019\u0004\u0001C\u0001\u0003k\tQa\u001d;beR$\u0012A\u001d\u0005\b\u0003s\u0001A\u0011AA\u001b\u0003\u0011\u0019Ho\u001c9\t\u000f\u0005u\u0002\u0001\"\u0001\u00026\u00051!/\u001a9peRD\u0001\"!\u0011\u0001\t\u0003!\u00111I\u0001\u0012EVLG\u000e\u001a*fO&\u001cHO]=OC6,GcA\u000e\u0002F!1a+a\u0010A\u0002MCq!!\u0013\u0001\t\u0003\tY%\u0001\thKR\u001cv.\u001e:dKN\u0014\u0015PT1nKR!\u0011QJA3!\u0015\ty%a\u0018T\u001d\u0011\t\t&a\u0017\u000f\t\u0005M\u0013\u0011L\u0007\u0003\u0003+R1!a\u0016\u001a\u0003\u0019a$o\\8u}%\ta\"C\u0002\u0002^5\tq\u0001]1dW\u0006<W-\u0003\u0003\u0002b\u0005\r$aA*fc*\u0019\u0011QL\u0007\t\u000f\u0005\u001d\u0014q\ta\u00017\u0005Q1o\\;sG\u0016t\u0015-\\3\t\u000f\u0005-\u0004\u0001\"\u0001\u0002n\u0005q!/Z4jgR,'oU8ve\u000e,Gc\u0001:\u0002p!1a+!\u001bA\u0002MCq!a\u001d\u0001\t\u0003\t)(\u0001\u0007sK6|g/Z*pkJ\u001cW\rF\u0002s\u0003oBaAVA9\u0001\u0004\u0019\u0006bBA>\u0001\u0011%\u0011QG\u0001\u0010e\u0016<\u0017n\u001d;feN{WO]2fg\"9\u0011q\u0010\u0001\u0005\n\u0005U\u0012!\u0004:fO&\u001cH/\u001a:TS:\\7o\u0002\u0005\u0002\u0004\nA\t\u0001BAC\u00035iU\r\u001e:jGN\u001c\u0016p\u001d;f[B\u00191'a\"\u0007\u000f\u0005\u0011\u0001\u0012\u0001\u0003\u0002\nN\u0019\u0011qQ\u0006\t\u000fA\n9\t\"\u0001\u0002\u000eR\u0011\u0011Q\u0011\u0005\u000b\u0003#\u000b9I1A\u0005\u0002\u0005M\u0015AC*J\u001d.{&+R$F1V\u0011\u0011Q\u0013\t\u0005\u0003/\u000b\t+\u0004\u0002\u0002\u001a*!\u00111TAO\u0003!i\u0017\r^2iS:<'bAAP\u001b\u0005!Q\u000f^5m\u0013\u0011\t\u0019+!'\u0003\u000bI+w-\u001a=\t\u0013\u0005\u001d\u0016q\u0011Q\u0001\n\u0005U\u0015aC*J\u001d.{&+R$F1\u0002B!\"a+\u0002\b\n\u0007I\u0011AAJ\u00031\u0019v*\u0016*D\u000b~\u0013ViR#Y\u0011%\ty+a\"!\u0002\u0013\t)*A\u0007T\u001fV\u00136)R0S\u000b\u001e+\u0005\f\t\u0005\n\u0003g\u000b9\t)A\u0005\u0003k\u000b\u0011#T%O\u00136\u000bEj\u0018)P\u00192{VKT%U!\u0011\t9,a1\u000e\u0005\u0005e&\u0002BA^\u0003{\u000b!bY8oGV\u0014(/\u001a8u\u0015\u0011\ty*a0\u000b\u0005\u0005\u0005\u0017\u0001\u00026bm\u0006LA!!2\u0002:\nAA+[7f+:LG\u000fC\u0005\u0002J\u0006\u001d\u0005\u0015!\u0003\u0002L\u0006\u0019R*\u0013(J\u001b\u0006cu\fU(M\u0019~\u0003VIU%P\tB\u0019A\"!4\n\u0007\u0005=WBA\u0002J]RD\u0001\"a5\u0002\b\u0012\u0005\u0011Q[\u0001\u001aG\",7m['j]&l\u0017\r\u001c)pY2Lgn\u001a)fe&|G\rF\u0003s\u0003/\fY\u000e\u0003\u0005\u0002Z\u0006E\u0007\u0019AA[\u0003!\u0001x\u000e\u001c7V]&$\b\u0002CAo\u0003#\u0004\r!a3\u0002\u0015A|G\u000e\u001c)fe&|G\r\u0003\u0005\u0002b\u0006\u001dE\u0011AAr\u0003M\u0019'/Z1uK6+GO]5dgNK8\u000f^3n)\u001d\u0011\u0014Q]At\u0003SDa\u0001GAp\u0001\u0004Y\u0002BB\u0013\u0002`\u0002\u0007a\u0005\u0003\u0004,\u0003?\u0004\r\u0001\f"
)
public class MetricsSystem implements Logging {
   private final String instance;
   public final SparkConf org$apache$spark$metrics$MetricsSystem$$conf;
   public final SecurityManager org$apache$spark$metrics$MetricsSystem$$securityMgr;
   private final MetricsConfig metricsConfig;
   private final ArrayBuffer<Sink> org$apache$spark$metrics$MetricsSystem$$sinks;
   private final ArrayBuffer<Source> sources;
   private final MetricRegistry org$apache$spark$metrics$MetricsSystem$$registry;
   private boolean running;
   private Option<MetricsServlet> org$apache$spark$metrics$MetricsSystem$$metricsServlet;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static MetricsSystem createMetricsSystem(String var0, SparkConf var1, SecurityManager var2) {
      return MetricsSystem$.MODULE$.createMetricsSystem(var0, var1, var2);
   }

   public static void checkMinimalPollingPeriod(TimeUnit var0, int var1) {
      MetricsSystem$.MODULE$.checkMinimalPollingPeriod(var0, var1);
   }

   public static Regex SOURCE_REGEX() {
      return MetricsSystem$.MODULE$.SOURCE_REGEX();
   }

   public static Regex SINK_REGEX() {
      return MetricsSystem$.MODULE$.SINK_REGEX();
   }

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

   public String instance() {
      return this.instance;
   }

   public ArrayBuffer<Sink> org$apache$spark$metrics$MetricsSystem$$sinks() {
      return this.org$apache$spark$metrics$MetricsSystem$$sinks;
   }

   private ArrayBuffer<Source> sources() {
      return this.sources;
   }

   public MetricRegistry org$apache$spark$metrics$MetricsSystem$$registry() {
      return this.org$apache$spark$metrics$MetricsSystem$$registry;
   }

   private boolean running() {
      return this.running;
   }

   private void running_$eq(boolean x$1) {
      this.running = x$1;
   }

   private Option<MetricsServlet> org$apache$spark$metrics$MetricsSystem$$metricsServlet() {
      return this.org$apache$spark$metrics$MetricsSystem$$metricsServlet;
   }

   public void org$apache$spark$metrics$MetricsSystem$$metricsServlet_$eq(Option<MetricsServlet> x$1) {
      this.org$apache$spark$metrics$MetricsSystem$$metricsServlet = x$1;
   }

   public ServletContextHandler[] getServletHandlers() {
      Predef$.MODULE$.require(this.running(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Can only call getServletHandlers on a running MetricsSystem";
         }
      });
      return (ServletContextHandler[])this.org$apache$spark$metrics$MetricsSystem$$metricsServlet().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MetricsSystem $outer;

         public final ServletContextHandler[] apply(MetricsServlet x$1) {
            return x$1.getHandlers(this.$outer.org$apache$spark$metrics$MetricsSystem$$conf);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ServletContextHandler[] apply() {
            return (ServletContextHandler[])Array$.MODULE$.apply(Nil$.MODULE$, ClassTag$.MODULE$.apply(ServletContextHandler.class));
         }
      });
   }

   public void start() {
      Predef$.MODULE$.require(!this.running(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Attempting to start a MetricsSystem that is already running";
         }
      });
      this.running_$eq(true);
      StaticSources$.MODULE$.allSources().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MetricsSystem $outer;

         public final void apply(Source source) {
            this.$outer.registerSource(source);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.registerSources();
      this.registerSinks();
      this.org$apache$spark$metrics$MetricsSystem$$sinks().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(Sink x$2) {
            x$2.start();
         }
      });
   }

   public void stop() {
      if (this.running()) {
         this.org$apache$spark$metrics$MetricsSystem$$sinks().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(Sink x$3) {
               x$3.stop();
            }
         });
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Stopping a MetricsSystem that is not running";
            }
         });
      }

      this.running_$eq(false);
   }

   public void report() {
      this.org$apache$spark$metrics$MetricsSystem$$sinks().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(Sink x$4) {
            x$4.report();
         }
      });
   }

   public String buildRegistryName(Source source) {
      String defaultName;
      String var8;
      label51: {
         Option metricsNamespace;
         Option executorId;
         label39: {
            metricsNamespace = ((Option)this.org$apache$spark$metrics$MetricsSystem$$conf.get((ConfigEntry)package$.MODULE$.METRICS_NAMESPACE())).orElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final MetricsSystem $outer;

               public final Option<String> apply() {
                  return this.$outer.org$apache$spark$metrics$MetricsSystem$$conf.getOption("spark.app.id");
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            executorId = this.org$apache$spark$metrics$MetricsSystem$$conf.getOption("spark.executor.id");
            defaultName = MetricRegistry.name(source.sourceName(), new String[0]);
            var8 = this.instance();
            String var5 = "driver";
            if (var8 == null) {
               if (var5 == null) {
                  break label39;
               }
            } else if (var8.equals(var5)) {
               break label39;
            }

            var8 = this.instance();
            String var6 = "executor";
            if (var8 == null) {
               if (var6 != null) {
                  break label51;
               }
            } else if (!var8.equals(var6)) {
               break label51;
            }
         }

         if (metricsNamespace.isDefined() && executorId.isDefined()) {
            var8 = MetricRegistry.name((String)metricsNamespace.get(), new String[]{(String)executorId.get(), source.sourceName()});
            return var8;
         }

         if (metricsNamespace.isEmpty()) {
            this.logWarning(new Serializable(this, defaultName) {
               public static final long serialVersionUID = 0L;
               private final String defaultName$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using default name ", " for source because neither "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.defaultName$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " nor spark.app.id is set."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.METRICS_NAMESPACE().key()}))).toString();
               }

               public {
                  this.defaultName$1 = defaultName$1;
               }
            });
         }

         if (executorId.isEmpty()) {
            this.logWarning(new Serializable(this, defaultName) {
               public static final long serialVersionUID = 0L;
               private final String defaultName$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using default name ", " for source because spark.executor.id is "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.defaultName$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"not set."})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.defaultName$1 = defaultName$1;
               }
            });
         }

         var8 = defaultName;
         return var8;
      }

      var8 = defaultName;
      return var8;
   }

   public Seq<Source> getSourcesByName(String sourceName) {
      return (Seq)this.sources().filter(new Serializable(this, sourceName) {
         public static final long serialVersionUID = 0L;
         private final String sourceName$1;

         public final boolean apply(Source x$5) {
            boolean var3;
            label23: {
               String var10000 = x$5.sourceName();
               String var2 = this.sourceName$1;
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
            this.sourceName$1 = sourceName$1;
         }
      });
   }

   public void registerSource(Source source) {
      this.sources().$plus$eq(source);

      try {
         String regName = this.buildRegistryName(source);
         this.org$apache$spark$metrics$MetricsSystem$$registry().register(regName, source.metricRegistry());
      } catch (IllegalArgumentException var4) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Metrics already registered";
            }
         }, var4);
      }

   }

   public void removeSource(Source source) {
      this.sources().$minus$eq(source);
      String regName = this.buildRegistryName(source);
      this.org$apache$spark$metrics$MetricsSystem$$registry().removeMatching(new MetricFilter(this, regName) {
         private final String regName$1;

         public boolean matches(String name, Metric metric) {
            return name.startsWith(this.regName$1);
         }

         public {
            this.regName$1 = regName$1;
         }
      });
   }

   private void registerSources() {
      Properties instConfig = this.metricsConfig.getInstance(this.instance());
      HashMap sourceConfigs = this.metricsConfig.subProperties(instConfig, MetricsSystem$.MODULE$.SOURCE_REGEX());
      sourceConfigs.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MetricsSystem $outer;

         public final void apply(Tuple2<String, Properties> kv) {
            String classPath = ((Properties)kv._2()).getProperty("class");

            try {
               Object source = Utils$.MODULE$.classForName(classPath).newInstance();
               this.$outer.registerSource((Source)source);
            } catch (Exception var5) {
               this.$outer.logError(new Serializable(this, classPath) {
                  public static final long serialVersionUID = 0L;
                  private final String classPath$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Source class ").append((Object)this.classPath$1).append((Object)" cannot be instantiated").toString();
                  }

                  public {
                     this.classPath$1 = classPath$1;
                  }
               }, var5);
            }

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

   private void registerSinks() {
      Properties instConfig = this.metricsConfig.getInstance(this.instance());
      HashMap sinkConfigs = this.metricsConfig.subProperties(instConfig, MetricsSystem$.MODULE$.SINK_REGEX());
      sinkConfigs.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MetricsSystem $outer;

         public final Object apply(Tuple2<String, Properties> kv) {
            String classPath = ((Properties)kv._2()).getProperty("class");
            Object var10000;
            if (classPath == null) {
               var10000 = BoxedUnit.UNIT;
            } else {
               try {
                  Object sink;
                  label29: {
                     sink = Utils$.MODULE$.classForName(classPath).getConstructor(Properties.class, MetricRegistry.class, SecurityManager.class).newInstance(kv._2(), this.$outer.org$apache$spark$metrics$MetricsSystem$$registry(), this.$outer.org$apache$spark$metrics$MetricsSystem$$securityMgr);
                     var10000 = kv._1();
                     String var5 = "servlet";
                     if (var10000 == null) {
                        if (var5 == null) {
                           break label29;
                        }
                     } else if (var10000.equals(var5)) {
                        break label29;
                     }

                     var10000 = this.$outer.org$apache$spark$metrics$MetricsSystem$$sinks().$plus$eq((Sink)sink);
                     return var10000;
                  }

                  this.$outer.org$apache$spark$metrics$MetricsSystem$$metricsServlet_$eq(new Some((MetricsServlet)sink));
                  var10000 = BoxedUnit.UNIT;
               } catch (Exception var6) {
                  this.$outer.logError(new Serializable(this, classPath) {
                     public static final long serialVersionUID = 0L;
                     private final String classPath$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Sink class ").append((Object)this.classPath$2).append((Object)" cannot be instantiated").toString();
                     }

                     public {
                        this.classPath$2 = classPath$2;
                     }
                  });
                  throw var6;
               }
            }

            return var10000;
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

   public MetricsSystem(String instance, SparkConf conf, SecurityManager securityMgr) {
      this.instance = instance;
      this.org$apache$spark$metrics$MetricsSystem$$conf = conf;
      this.org$apache$spark$metrics$MetricsSystem$$securityMgr = securityMgr;
      Logging$class.$init$(this);
      this.metricsConfig = new MetricsConfig(conf);
      this.org$apache$spark$metrics$MetricsSystem$$sinks = new ArrayBuffer<Sink>();
      this.sources = new ArrayBuffer<Source>();
      this.org$apache$spark$metrics$MetricsSystem$$registry = new MetricRegistry();
      this.running = false;
      this.org$apache$spark$metrics$MetricsSystem$$metricsServlet = None$.MODULE$;
      this.metricsConfig.initialize();
   }
}
