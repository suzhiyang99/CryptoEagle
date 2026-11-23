package org.apache.spark.scheduler;

import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Distribution;
import org.apache.spark.util.Distribution$;
import org.apache.spark.util.StatCounter;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Traversable;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class StatsReportListener$ implements Logging {
   public static final StatsReportListener$ MODULE$;
   private final int[] percentiles;
   private final double[] probabilities;
   private final String percentilesHeader;
   private final long seconds;
   private final long minutes;
   private final long hours;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new StatsReportListener$();
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

   public int[] percentiles() {
      return this.percentiles;
   }

   public double[] probabilities() {
      return this.probabilities;
   }

   public String percentilesHeader() {
      return this.percentilesHeader;
   }

   public Option<Distribution> extractDoubleDistribution(Seq<Tuple2<TaskInfo, TaskMetrics>> taskInfoMetrics, Function2<TaskInfo, TaskMetrics, Object> getMetric) {
      return Distribution$.MODULE$.apply((Traversable)taskInfoMetrics.map(new Serializable(getMetric) {
         public static final long serialVersionUID = 0L;
         private final Function2 getMetric$1;

         public final double apply(Tuple2<TaskInfo, TaskMetrics> x0$2) {
            if (x0$2 != null) {
               TaskInfo info = (TaskInfo)x0$2._1();
               TaskMetrics metric = (TaskMetrics)x0$2._2();
               double var5 = BoxesRunTime.unboxToDouble(this.getMetric$1.apply(info, metric));
               return var5;
            } else {
               throw new MatchError(x0$2);
            }
         }

         public {
            this.getMetric$1 = getMetric$1;
         }
      }, Seq$.MODULE$.canBuildFrom()));
   }

   public Option<Distribution> extractLongDistribution(Seq<Tuple2<TaskInfo, TaskMetrics>> taskInfoMetrics, Function2<TaskInfo, TaskMetrics, Object> getMetric) {
      return this.extractDoubleDistribution(taskInfoMetrics, new Serializable(getMetric) {
         public static final long serialVersionUID = 0L;
         private final Function2 getMetric$2;

         public final double apply(TaskInfo info, TaskMetrics metric) {
            return (double)BoxesRunTime.unboxToLong(this.getMetric$2.apply(info, metric));
         }

         public {
            this.getMetric$2 = getMetric$2;
         }
      });
   }

   public void showDistribution(String heading, Distribution d, Function1<Object, String> formatNumber) {
      StatCounter stats = d.statCounter();
      IndexedSeq quantiles = (IndexedSeq)d.getQuantiles(Predef$.MODULE$.wrapDoubleArray(this.probabilities())).map(formatNumber, IndexedSeq$.MODULE$.canBuildFrom());
      this.logInfo(new Serializable(heading, stats) {
         public static final long serialVersionUID = 0L;
         private final String heading$3;
         private final StatCounter stats$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)this.heading$3).append((Object)this.stats$1).toString();
         }

         public {
            this.heading$3 = heading$3;
            this.stats$1 = stats$1;
         }
      });
      this.logInfo(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return StatsReportListener$.MODULE$.percentilesHeader();
         }
      });
      this.logInfo(new Serializable(quantiles) {
         public static final long serialVersionUID = 0L;
         private final IndexedSeq quantiles$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"\t").append((Object)this.quantiles$1.mkString("\t")).toString();
         }

         public {
            this.quantiles$1 = quantiles$1;
         }
      });
   }

   public void showDistribution(String heading, Option<Distribution> dOpt, Function1<Object, String> formatNumber) {
      dOpt.foreach(new Serializable(heading, formatNumber) {
         public static final long serialVersionUID = 0L;
         private final String heading$2;
         private final Function1 formatNumber$1;

         public final void apply(Distribution d) {
            StatsReportListener$.MODULE$.showDistribution(this.heading$2, d, this.formatNumber$1);
         }

         public {
            this.heading$2 = heading$2;
            this.formatNumber$1 = formatNumber$1;
         }
      });
   }

   public void showDistribution(String heading, Option<Distribution> dOpt, String format) {
      this.showDistribution(heading, (Option)dOpt, new Serializable(format) {
         public static final long serialVersionUID = 0L;
         private final String format$1;

         public final String apply(double d) {
            return StatsReportListener$.MODULE$.org$apache$spark$scheduler$StatsReportListener$$f$1(d, this.format$1);
         }

         public {
            this.format$1 = format$1;
         }
      });
   }

   public void showDistribution(String heading, String format, Function2<TaskInfo, TaskMetrics, Object> getMetric, Seq<Tuple2<TaskInfo, TaskMetrics>> taskInfoMetrics) {
      this.showDistribution(heading, this.extractDoubleDistribution(taskInfoMetrics, getMetric), format);
   }

   public void showBytesDistribution(String heading, Function2<TaskInfo, TaskMetrics, Object> getMetric, Seq<Tuple2<TaskInfo, TaskMetrics>> taskInfoMetrics) {
      this.showBytesDistribution(heading, this.extractLongDistribution(taskInfoMetrics, getMetric));
   }

   public void showBytesDistribution(String heading, Option<Distribution> dOpt) {
      dOpt.foreach(new Serializable(heading) {
         public static final long serialVersionUID = 0L;
         private final String heading$1;

         public final void apply(Distribution dist) {
            StatsReportListener$.MODULE$.showBytesDistribution(this.heading$1, dist);
         }

         public {
            this.heading$1 = heading$1;
         }
      });
   }

   public void showBytesDistribution(String heading, Distribution dist) {
      this.showDistribution(heading, dist, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(double d) {
            return Utils$.MODULE$.bytesToString((long)d);
         }
      });
   }

   public void showMillisDistribution(String heading, Option<Distribution> dOpt) {
      this.showDistribution(heading, (Option)dOpt, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(double d) {
            return StatsReportListener$.MODULE$.millisToString((long)d);
         }
      });
   }

   public void showMillisDistribution(String heading, Function2<TaskInfo, TaskMetrics, Object> getMetric, Seq<Tuple2<TaskInfo, TaskMetrics>> taskInfoMetrics) {
      this.showMillisDistribution(heading, this.extractLongDistribution(taskInfoMetrics, getMetric));
   }

   public long seconds() {
      return this.seconds;
   }

   public long minutes() {
      return this.minutes;
   }

   public long hours() {
      return this.hours;
   }

   public String millisToString(long ms) {
      Tuple2 var4 = ms > this.hours() ? new Tuple2(BoxesRunTime.boxToDouble((double)ms / (double)this.hours()), "hours") : (ms > this.minutes() ? new Tuple2(BoxesRunTime.boxToDouble((double)ms / (double)this.minutes()), "min") : (ms > this.seconds() ? new Tuple2(BoxesRunTime.boxToDouble((double)ms / (double)this.seconds()), "s") : new Tuple2(BoxesRunTime.boxToDouble((double)ms), "ms")));
      if (var4 != null) {
         double size = var4._1$mcD$sp();
         String units = (String)var4._2();
         Tuple2 var8 = new Tuple2(BoxesRunTime.boxToDouble(size), units);
         double size = var8._1$mcD$sp();
         String units = (String)var8._2();
         return (new StringOps(Predef$.MODULE$.augmentString("%.1f %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(size), units}));
      } else {
         throw new MatchError(var4);
      }
   }

   public final String org$apache$spark$scheduler$StatsReportListener$$f$1(double d, String format$1) {
      return (new StringOps(Predef$.MODULE$.augmentString(format$1))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(d)}));
   }

   private StatsReportListener$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.percentiles = (int[])Array$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{0, 5, 10, 25, 50, 75, 90, 95, 100}), ClassTag$.MODULE$.Int());
      this.probabilities = (double[])Predef$.MODULE$.intArrayOps(this.percentiles()).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final double apply(int x$11) {
            return this.apply$mcDI$sp(x$11);
         }

         public double apply$mcDI$sp(int x$11) {
            return (double)x$11 / (double)100.0F;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Double()));
      this.percentilesHeader = (new StringBuilder()).append((Object)"\t").append((Object)Predef$.MODULE$.intArrayOps(this.percentiles()).mkString("%\t")).append((Object)"%").toString();
      this.seconds = 1000L;
      this.minutes = this.seconds() * 60L;
      this.hours = this.minutes() * 60L;
   }
}
