package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Distribution;
import org.apache.spark.util.Distribution$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Buffer$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mh\u0001B\u0001\u0003\u0001-\u00111c\u0015;biN\u0014V\r]8si2K7\u000f^3oKJT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\t\u0011\u00055qQ\"\u0001\u0002\n\u0005=\u0011!!D*qCJ\\G*[:uK:,'\u000f\u0005\u0002\u0012)5\t!C\u0003\u0002\u0014\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0016%\t9Aj\\4hS:<\u0007\"B\f\u0001\t\u0003A\u0012A\u0002\u001fj]&$h\bF\u0001\u001a!\ti\u0001\u0001C\u0004\u001c\u0001\t\u0007I\u0011\u0002\u000f\u0002\u001fQ\f7o[%oM>lU\r\u001e:jGN,\u0012!\b\t\u0004=\u0015:S\"A\u0010\u000b\u0005\u0001\n\u0013aB7vi\u0006\u0014G.\u001a\u0006\u0003E\r\n!bY8mY\u0016\u001cG/[8o\u0015\u0005!\u0013!B:dC2\f\u0017B\u0001\u0014 \u0005\u0019\u0011UO\u001a4feB!\u0001&K\u0016/\u001b\u0005\u0019\u0013B\u0001\u0016$\u0005\u0019!V\u000f\u001d7feA\u0011Q\u0002L\u0005\u0003[\t\u0011\u0001\u0002V1tW&sgm\u001c\t\u0003_Ij\u0011\u0001\r\u0006\u0003c\u0011\t\u0001\"\u001a=fGV$xN]\u0005\u0003gA\u00121\u0002V1tW6+GO]5dg\"1Q\u0007\u0001Q\u0001\nu\t\u0001\u0003^1tW&sgm\\'fiJL7m\u001d\u0011\t\u000b]\u0002A\u0011\t\u001d\u0002\u0013=tG+Y:l\u000b:$GCA\u001d=!\tA#(\u0003\u0002<G\t!QK\\5u\u0011\u0015id\u00071\u0001?\u0003\u001d!\u0018m]6F]\u0012\u0004\"!D \n\u0005\u0001\u0013!\u0001F*qCJ\\G*[:uK:,'\u000fV1tW\u0016sG\rC\u0003C\u0001\u0011\u00053)\u0001\tp]N#\u0018mZ3D_6\u0004H.\u001a;fIR\u0011\u0011\b\u0012\u0005\u0006\u000b\u0006\u0003\rAR\u0001\u000fgR\fw-Z\"p[BdW\r^3e!\tiq)\u0003\u0002I\u0005\tY2\u000b]1sW2K7\u000f^3oKJ\u001cF/Y4f\u0007>l\u0007\u000f\\3uK\u0012DQA\u0013\u0001\u0005\n-\u000bqbZ3u'R\fG/^:EKR\f\u0017\u000e\u001c\u000b\u0003\u0019N\u0003\"!\u0014)\u000f\u0005!r\u0015BA($\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011K\u0015\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005=\u001b\u0003\"\u0002+J\u0001\u0004)\u0016\u0001B5oM>\u0004\"!\u0004,\n\u0005]\u0013!!C*uC\u001e,\u0017J\u001c4pQ\t\u0001\u0011\f\u0005\u0002[;6\t1L\u0003\u0002]\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005y[&\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLwA\u00021\u0003\u0011\u0003!\u0011-A\nTi\u0006$8OU3q_J$H*[:uK:,'\u000f\u0005\u0002\u000eE\u001a1\u0011A\u0001E\u0001\t\r\u001c2A\u00193\u0011!\tAS-\u0003\u0002gG\t1\u0011I\\=SK\u001aDQa\u00062\u0005\u0002!$\u0012!\u0019\u0005\bU\n\u0014\r\u0011\"\u0001l\u0003-\u0001XM]2f]RLG.Z:\u0016\u00031\u00042\u0001K7p\u0013\tq7EA\u0003BeJ\f\u0017\u0010\u0005\u0002)a&\u0011\u0011o\t\u0002\u0004\u0013:$\bBB:cA\u0003%A.\u0001\u0007qKJ\u001cWM\u001c;jY\u0016\u001c\b\u0005C\u0004vE\n\u0007I\u0011\u0001<\u0002\u001bA\u0014xNY1cS2LG/[3t+\u00059\bc\u0001\u0015nqB\u0011\u0001&_\u0005\u0003u\u000e\u0012a\u0001R8vE2,\u0007B\u0002?cA\u0003%q/\u0001\bqe>\u0014\u0017MY5mSRLWm\u001d\u0011\t\u000fy\u0014'\u0019!C\u0001\u007f\u0006\t\u0002/\u001a:dK:$\u0018\u000e\\3t\u0011\u0016\fG-\u001a:\u0016\u0005\u0005\u0005\u0001\u0003BA\u0002\u0003\u001bi!!!\u0002\u000b\t\u0005\u001d\u0011\u0011B\u0001\u0005Y\u0006twM\u0003\u0002\u0002\f\u0005!!.\u0019<b\u0013\r\t\u0016Q\u0001\u0005\t\u0003#\u0011\u0007\u0015!\u0003\u0002\u0002\u0005\u0011\u0002/\u001a:dK:$\u0018\u000e\\3t\u0011\u0016\fG-\u001a:!\u0011\u001d\t)B\u0019C\u0001\u0003/\t\u0011$\u001a=ue\u0006\u001cG\u000fR8vE2,G)[:ue&\u0014W\u000f^5p]R1\u0011\u0011DA\u0016\u0003\u000b\u0002R\u0001KA\u000e\u0003?I1!!\b$\u0005\u0019y\u0005\u000f^5p]B!\u0011\u0011EA\u0014\u001b\t\t\u0019CC\u0002\u0002&\u0011\tA!\u001e;jY&!\u0011\u0011FA\u0012\u00051!\u0015n\u001d;sS\n,H/[8o\u0011\u001dY\u00121\u0003a\u0001\u0003[\u0001R!a\f\u0002@\u001drA!!\r\u0002<9!\u00111GA\u001d\u001b\t\t)DC\u0002\u00028)\ta\u0001\u0010:p_Rt\u0014\"\u0001\u0013\n\u0007\u0005u2%A\u0004qC\u000e\\\u0017mZ3\n\t\u0005\u0005\u00131\t\u0002\u0004'\u0016\f(bAA\u001fG!A\u0011qIA\n\u0001\u0004\tI%A\u0005hKRlU\r\u001e:jGB1\u0001&a\u0013,]aL1!!\u0014$\u0005%1UO\\2uS>t'\u0007C\u0004\u0002R\t$\t!a\u0015\u0002/\u0015DHO]1di2{gn\u001a#jgR\u0014\u0018NY;uS>tGCBA\r\u0003+\n9\u0006C\u0004\u001c\u0003\u001f\u0002\r!!\f\t\u0011\u0005\u001d\u0013q\na\u0001\u00033\u0002r\u0001KA&W9\nY\u0006E\u0002)\u0003;J1!a\u0018$\u0005\u0011auN\\4\t\u000f\u0005\r$\r\"\u0001\u0002f\u0005\u00012\u000f[8x\t&\u001cHO]5ckRLwN\u001c\u000b\bs\u0005\u001d\u00141NA8\u0011\u001d\tI'!\u0019A\u00021\u000bq\u0001[3bI&tw\r\u0003\u0005\u0002n\u0005\u0005\u0004\u0019AA\u0010\u0003\u0005!\u0007\u0002CA9\u0003C\u0002\r!a\u001d\u0002\u0019\u0019|'/\\1u\u001dVl'-\u001a:\u0011\u000b!\n)\b\u001f'\n\u0007\u0005]4EA\u0005Gk:\u001cG/[8oc!9\u00111\r2\u0005\u0002\u0005mDcB\u001d\u0002~\u0005}\u00141\u0011\u0005\b\u0003S\nI\b1\u0001M\u0011!\t\t)!\u001fA\u0002\u0005e\u0011\u0001\u00023PaRD\u0001\"!\u001d\u0002z\u0001\u0007\u00111\u000f\u0005\b\u0003G\u0012G\u0011AAD)\u001dI\u0014\u0011RAF\u0003\u001bCq!!\u001b\u0002\u0006\u0002\u0007A\n\u0003\u0005\u0002\u0002\u0006\u0015\u0005\u0019AA\r\u0011\u001d\ty)!\"A\u00021\u000baAZ8s[\u0006$\bbBA2E\u0012\u0005\u00111\u0013\u000b\ns\u0005U\u0015qSAM\u00037Cq!!\u001b\u0002\u0012\u0002\u0007A\nC\u0004\u0002\u0010\u0006E\u0005\u0019\u0001'\t\u0011\u0005\u001d\u0013\u0011\u0013a\u0001\u0003\u0013BqaGAI\u0001\u0004\ti\u0003C\u0004\u0002 \n$\t!!)\u0002+MDwn\u001e\"zi\u0016\u001cH)[:ue&\u0014W\u000f^5p]R9\u0011(a)\u0002&\u0006\u001d\u0006bBA5\u0003;\u0003\r\u0001\u0014\u0005\t\u0003\u000f\ni\n1\u0001\u0002Z!91$!(A\u0002\u00055\u0002bBAPE\u0012\u0005\u00111\u0016\u000b\u0006s\u00055\u0016q\u0016\u0005\b\u0003S\nI\u000b1\u0001M\u0011!\t\t)!+A\u0002\u0005e\u0001bBAPE\u0012\u0005\u00111\u0017\u000b\u0006s\u0005U\u0016q\u0017\u0005\b\u0003S\n\t\f1\u0001M\u0011!\tI,!-A\u0002\u0005}\u0011\u0001\u00023jgRDq!!0c\t\u0003\ty,\u0001\ftQ><X*\u001b7mSN$\u0015n\u001d;sS\n,H/[8o)\u0015I\u0014\u0011YAb\u0011\u001d\tI'a/A\u00021C\u0001\"!!\u0002<\u0002\u0007\u0011\u0011\u0004\u0005\b\u0003{\u0013G\u0011AAd)\u001dI\u0014\u0011ZAf\u0003\u001bDq!!\u001b\u0002F\u0002\u0007A\n\u0003\u0005\u0002H\u0005\u0015\u0007\u0019AA-\u0011\u001dY\u0012Q\u0019a\u0001\u0003[A\u0011\"!5c\u0005\u0004%\t!a5\u0002\u000fM,7m\u001c8egV\u0011\u00111\f\u0005\t\u0003/\u0014\u0007\u0015!\u0003\u0002\\\u0005A1/Z2p]\u0012\u001c\b\u0005C\u0005\u0002\\\n\u0014\r\u0011\"\u0001\u0002T\u00069Q.\u001b8vi\u0016\u001c\b\u0002CApE\u0002\u0006I!a\u0017\u0002\u00115Lg.\u001e;fg\u0002B\u0011\"a9c\u0005\u0004%\t!a5\u0002\u000b!|WO]:\t\u0011\u0005\u001d(\r)A\u0005\u00037\na\u0001[8veN\u0004\u0003bBAvE\u0012\u0005\u0011Q^\u0001\u000f[&dG.[:U_N#(/\u001b8h)\ra\u0015q\u001e\u0005\t\u0003c\fI\u000f1\u0001\u0002\\\u0005\u0011Qn\u001d"
)
public class StatsReportListener extends SparkListener implements Logging {
   private final Buffer<Tuple2<TaskInfo, TaskMetrics>> taskInfoMetrics;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String millisToString(long var0) {
      return StatsReportListener$.MODULE$.millisToString(var0);
   }

   public static long hours() {
      return StatsReportListener$.MODULE$.hours();
   }

   public static long minutes() {
      return StatsReportListener$.MODULE$.minutes();
   }

   public static long seconds() {
      return StatsReportListener$.MODULE$.seconds();
   }

   public static void showMillisDistribution(String var0, Function2<TaskInfo, TaskMetrics, Object> var1, Seq<Tuple2<TaskInfo, TaskMetrics>> var2) {
      StatsReportListener$.MODULE$.showMillisDistribution(var0, var1, var2);
   }

   public static void showMillisDistribution(String var0, Option<Distribution> var1) {
      StatsReportListener$.MODULE$.showMillisDistribution(var0, var1);
   }

   public static void showBytesDistribution(String var0, Distribution var1) {
      StatsReportListener$.MODULE$.showBytesDistribution(var0, var1);
   }

   public static void showBytesDistribution(String var0, Option<Distribution> var1) {
      StatsReportListener$.MODULE$.showBytesDistribution(var0, var1);
   }

   public static void showBytesDistribution(String var0, Function2<TaskInfo, TaskMetrics, Object> var1, Seq<Tuple2<TaskInfo, TaskMetrics>> var2) {
      StatsReportListener$.MODULE$.showBytesDistribution(var0, var1, var2);
   }

   public static void showDistribution(String var0, String var1, Function2<TaskInfo, TaskMetrics, Object> var2, Seq<Tuple2<TaskInfo, TaskMetrics>> var3) {
      StatsReportListener$.MODULE$.showDistribution(var0, var1, var2, var3);
   }

   public static void showDistribution(String var0, Option<Distribution> var1, String var2) {
      StatsReportListener$.MODULE$.showDistribution(var0, var1, var2);
   }

   public static void showDistribution(String var0, Option<Distribution> var1, Function1<Object, String> var2) {
      StatsReportListener$.MODULE$.showDistribution(var0, var1, var2);
   }

   public static void showDistribution(String var0, Distribution var1, Function1<Object, String> var2) {
      StatsReportListener$.MODULE$.showDistribution(var0, var1, var2);
   }

   public static Option<Distribution> extractLongDistribution(Seq<Tuple2<TaskInfo, TaskMetrics>> var0, Function2<TaskInfo, TaskMetrics, Object> var1) {
      return StatsReportListener$.MODULE$.extractLongDistribution(var0, var1);
   }

   public static Option<Distribution> extractDoubleDistribution(Seq<Tuple2<TaskInfo, TaskMetrics>> var0, Function2<TaskInfo, TaskMetrics, Object> var1) {
      return StatsReportListener$.MODULE$.extractDoubleDistribution(var0, var1);
   }

   public static String percentilesHeader() {
      return StatsReportListener$.MODULE$.percentilesHeader();
   }

   public static double[] probabilities() {
      return StatsReportListener$.MODULE$.probabilities();
   }

   public static int[] percentiles() {
      return StatsReportListener$.MODULE$.percentiles();
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

   private Buffer<Tuple2<TaskInfo, TaskMetrics>> taskInfoMetrics() {
      return this.taskInfoMetrics;
   }

   public void onTaskEnd(SparkListenerTaskEnd taskEnd) {
      TaskInfo info = taskEnd.taskInfo();
      TaskMetrics metrics = taskEnd.taskMetrics();
      if (info != null && metrics != null) {
         this.taskInfoMetrics().$plus$eq(new Tuple2(info, metrics));
      }

   }

   public void onStageCompleted(SparkListenerStageCompleted stageCompleted) {
      this.logInfo(new Serializable(this, stageCompleted) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StatsReportListener $outer;
         private final SparkListenerStageCompleted stageCompleted$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Finished stage: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$StatsReportListener$$getStatusDetail(this.stageCompleted$1.stageInfo())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.stageCompleted$1 = stageCompleted$1;
            }
         }
      });
      StatsReportListener$.MODULE$.showMillisDistribution("task runtime:", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskInfo info, TaskMetrics x$1) {
            return info.duration();
         }
      }, this.taskInfoMetrics());
      StatsReportListener$.MODULE$.showBytesDistribution("shuffle bytes written:", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskInfo x$2, TaskMetrics metric) {
            return metric.shuffleWriteMetrics().bytesWritten();
         }
      }, this.taskInfoMetrics());
      StatsReportListener$.MODULE$.showMillisDistribution("fetch wait time:", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskInfo x$3, TaskMetrics metric) {
            return metric.shuffleReadMetrics().fetchWaitTime();
         }
      }, this.taskInfoMetrics());
      StatsReportListener$.MODULE$.showBytesDistribution("remote bytes read:", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskInfo x$4, TaskMetrics metric) {
            return metric.shuffleReadMetrics().remoteBytesRead();
         }
      }, this.taskInfoMetrics());
      StatsReportListener$.MODULE$.showBytesDistribution("task result size:", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskInfo x$5, TaskMetrics metric) {
            return metric.resultSize();
         }
      }, this.taskInfoMetrics());
      Buffer runtimePcts = (Buffer)this.taskInfoMetrics().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RuntimePercentage apply(Tuple2<TaskInfo, TaskMetrics> x0$1) {
            if (x0$1 != null) {
               TaskInfo info = (TaskInfo)x0$1._1();
               TaskMetrics metrics = (TaskMetrics)x0$1._2();
               RuntimePercentage var5 = RuntimePercentage$.MODULE$.apply(info.duration(), metrics);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Buffer$.MODULE$.canBuildFrom());
      StatsReportListener$.MODULE$.showDistribution("executor (non-fetch) time pct: ", Distribution$.MODULE$.apply((Traversable)runtimePcts.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(RuntimePercentage x$6) {
            return x$6.executorPct() * (double)100;
         }
      }, Buffer$.MODULE$.canBuildFrom())), "%2.0f %%");
      StatsReportListener$.MODULE$.showDistribution("fetch wait time pct: ", Distribution$.MODULE$.apply((Traversable)runtimePcts.flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterable<Object> apply(RuntimePercentage x$7) {
            return Option$.MODULE$.<Object>option2Iterable(x$7.fetchPct().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(double x$8) {
                  return this.apply$mcDD$sp(x$8);
               }

               public double apply$mcDD$sp(double x$8) {
                  return x$8 * (double)100;
               }
            }));
         }
      }, Buffer$.MODULE$.canBuildFrom())), "%2.0f %%");
      StatsReportListener$.MODULE$.showDistribution("other time pct: ", Distribution$.MODULE$.apply((Traversable)runtimePcts.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(RuntimePercentage x$9) {
            return x$9.other() * (double)100;
         }
      }, Buffer$.MODULE$.canBuildFrom())), "%2.0f %%");
      this.taskInfoMetrics().clear();
   }

   public String org$apache$spark$scheduler$StatsReportListener$$getStatusDetail(StageInfo info) {
      String failureReason = (String)info.failureReason().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$10) {
            return (new StringBuilder()).append((Object)"(").append((Object)x$10).append((Object)")").toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      Object timeTaken = info.submissionTime().map(new Serializable(this, info) {
         public static final long serialVersionUID = 0L;
         private final StageInfo info$1;

         public final long apply(long x) {
            return this.apply$mcJJ$sp(x);
         }

         public long apply$mcJJ$sp(long x) {
            return BoxesRunTime.unboxToLong(this.info$1.completionTime().getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return System.currentTimeMillis();
               }
            })) - x;
         }

         public {
            this.info$1 = info$1;
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "-";
         }
      });
      return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stage(", ", ", "); Name: '", "'; "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(info.stageId()), BoxesRunTime.boxToInteger(info.attemptNumber()), info.name()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Status: ", "", "; numTasks: ", "; "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{info.getStatusString(), failureReason, BoxesRunTime.boxToInteger(info.numTasks())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Took: ", " msec"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{timeTaken}))).toString();
   }

   public StatsReportListener() {
      Logging$class.$init$(this);
      this.taskInfoMetrics = (Buffer)Buffer$.MODULE$.apply(Nil$.MODULE$);
   }
}
