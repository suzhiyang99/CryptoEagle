package org.apache.spark.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.spark.SparkContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.status.api.v1.StageData;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;
import scala.sys.package$;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055b!B\u0001\u0003\u0001\u0011Q!AE\"p]N|G.\u001a)s_\u001e\u0014Xm]:CCJT!a\u0001\u0003\u0002\u0005UL'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Ui\u0011a\u0005\u0006\u0003)\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003-M\u0011q\u0001T8hO&tw\r\u0003\u0005\u0019\u0001\t\u0005\t\u0015!\u0003\u001b\u0003\t\u00198m\u0001\u0001\u0011\u0005maR\"\u0001\u0003\n\u0005u!!\u0001D*qCJ\\7i\u001c8uKb$\b\"B\u0010\u0001\t\u0003\u0001\u0013A\u0002\u001fj]&$h\b\u0006\u0002\"GA\u0011!\u0005A\u0007\u0002\u0005!)\u0001D\ba\u00015!9Q\u0005\u0001b\u0001\n\u00131\u0013AA\"S+\u00059\u0003C\u0001\u0007)\u0013\tISB\u0001\u0003DQ\u0006\u0014\bBB\u0016\u0001A\u0003%q%A\u0002D%\u0002Bq!\f\u0001C\u0002\u0013%a&\u0001\tva\u0012\fG/\u001a)fe&|G-T*fGV\tq\u0006\u0005\u0002\ra%\u0011\u0011'\u0004\u0002\u0005\u0019>tw\r\u0003\u00044\u0001\u0001\u0006IaL\u0001\u0012kB$\u0017\r^3QKJLw\u000eZ'TK\u000e\u0004\u0003bB\u001b\u0001\u0005\u0004%IAL\u0001\u000fM&\u00148\u000f\u001e#fY\u0006LXjU3d\u0011\u00199\u0004\u0001)A\u0005_\u0005ya-\u001b:ti\u0012+G.Y=N'\u0016\u001c\u0007\u0005C\u0004:\u0001\t\u0007I\u0011\u0002\u001e\u0002\u001bQ+'/\\5oC2<\u0016\u000e\u001a;i+\u0005Y\u0004C\u0001\u0007=\u0013\tiTBA\u0002J]RDaa\u0010\u0001!\u0002\u0013Y\u0014A\u0004+fe6Lg.\u00197XS\u0012$\b\u000e\t\u0005\b\u0003\u0002\u0001\r\u0011\"\u0003/\u00039a\u0017m\u001d;GS:L7\u000f\u001b+j[\u0016Dqa\u0011\u0001A\u0002\u0013%A)\u0001\nmCN$h)\u001b8jg\"$\u0016.\\3`I\u0015\fHCA#I!\taa)\u0003\u0002H\u001b\t!QK\\5u\u0011\u001dI%)!AA\u0002=\n1\u0001\u001f\u00132\u0011\u0019Y\u0005\u0001)Q\u0005_\u0005yA.Y:u\r&t\u0017n\u001d5US6,\u0007\u0005C\u0004N\u0001\u0001\u0007I\u0011\u0002\u0018\u0002\u001d1\f7\u000f^+qI\u0006$X\rV5nK\"9q\n\u0001a\u0001\n\u0013\u0001\u0016A\u00057bgR,\u0006\u000fZ1uKRKW.Z0%KF$\"!R)\t\u000f%s\u0015\u0011!a\u0001_!11\u000b\u0001Q!\n=\nq\u0002\\1tiV\u0003H-\u0019;f)&lW\r\t\u0005\b+\u0002\u0001\r\u0011\"\u0003W\u0003=a\u0017m\u001d;Qe><'/Z:t\u0005\u0006\u0014X#A,\u0011\u0005akV\"A-\u000b\u0005i[\u0016\u0001\u00027b]\u001eT\u0011\u0001X\u0001\u0005U\u00064\u0018-\u0003\u0002_3\n11\u000b\u001e:j]\u001eDq\u0001\u0019\u0001A\u0002\u0013%\u0011-A\nmCN$\bK]8he\u0016\u001c8OQ1s?\u0012*\u0017\u000f\u0006\u0002FE\"9\u0011jXA\u0001\u0002\u00049\u0006B\u00023\u0001A\u0003&q+\u0001\tmCN$\bK]8he\u0016\u001c8OQ1sA!9a\r\u0001b\u0001\n\u00139\u0017!\u0002;j[\u0016\u0014X#\u00015\u0011\u0005%dW\"\u00016\u000b\u0005-\\\u0016\u0001B;uS2L!!\u001c6\u0003\u000bQKW.\u001a:\t\r=\u0004\u0001\u0015!\u0003i\u0003\u0019!\u0018.\\3sA!)\u0011\u000f\u0001C\u0005e\u00069!/\u001a4sKNDG#A#\t\u000bQ\u0004A\u0011B;\u0002\tMDwn\u001e\u000b\u0004\u000bZD\b\"B<t\u0001\u0004y\u0013a\u00018po\")\u0011p\u001da\u0001u\u000611\u000f^1hKN\u0004Ra_A\u0004\u0003\u001bq1\u0001`A\u0002\u001d\ri\u0018\u0011A\u0007\u0002}*\u0011q0G\u0001\u0007yI|w\u000e\u001e \n\u00039I1!!\u0002\u000e\u0003\u001d\u0001\u0018mY6bO\u0016LA!!\u0003\u0002\f\t\u00191+Z9\u000b\u0007\u0005\u0015Q\u0002\u0005\u0003\u0002\u0010\u0005uQBAA\t\u0015\u0011\t\u0019\"!\u0006\u0002\u0005Y\f$\u0002BA\f\u00033\t1!\u00199j\u0015\r\tY\u0002B\u0001\u0007gR\fG/^:\n\t\u0005}\u0011\u0011\u0003\u0002\n'R\fw-\u001a#bi\u0006Da!a\t\u0001\t\u0013\u0011\u0018!B2mK\u0006\u0014\bBBA\u0014\u0001\u0011\u0005!/A\u0005gS:L7\u000f[!mY\"1\u00111\u0006\u0001\u0005\u0002I\fAa\u001d;pa\u0002"
)
public class ConsoleProgressBar implements Logging {
   private final SparkContext sc;
   private final char CR;
   private final long updatePeriodMSec;
   private final long org$apache$spark$ui$ConsoleProgressBar$$firstDelayMSec;
   private final int TerminalWidth;
   private long lastFinishTime;
   private long lastUpdateTime;
   private String lastProgressBar;
   private final Timer timer;
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

   private char CR() {
      return this.CR;
   }

   private long updatePeriodMSec() {
      return this.updatePeriodMSec;
   }

   public long org$apache$spark$ui$ConsoleProgressBar$$firstDelayMSec() {
      return this.org$apache$spark$ui$ConsoleProgressBar$$firstDelayMSec;
   }

   private int TerminalWidth() {
      return this.TerminalWidth;
   }

   private long lastFinishTime() {
      return this.lastFinishTime;
   }

   private void lastFinishTime_$eq(long x$1) {
      this.lastFinishTime = x$1;
   }

   private long lastUpdateTime() {
      return this.lastUpdateTime;
   }

   private void lastUpdateTime_$eq(long x$1) {
      this.lastUpdateTime = x$1;
   }

   private String lastProgressBar() {
      return this.lastProgressBar;
   }

   private void lastProgressBar_$eq(String x$1) {
      this.lastProgressBar = x$1;
   }

   private Timer timer() {
      return this.timer;
   }

   public synchronized void org$apache$spark$ui$ConsoleProgressBar$$refresh() {
      long now = System.currentTimeMillis();
      if (now - this.lastFinishTime() >= this.org$apache$spark$ui$ConsoleProgressBar$$firstDelayMSec()) {
         Seq stages = (Seq)this.sc.statusStore().activeStages().filter(new Serializable(this, now) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ConsoleProgressBar $outer;
            private final long now$1;

            public final boolean apply(StageData s) {
               return this.now$1 - ((Date)s.submissionTime().get()).getTime() > this.$outer.org$apache$spark$ui$ConsoleProgressBar$$firstDelayMSec();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.now$1 = now$1;
               }
            }
         });
         if (stages.length() > 0) {
            this.show(now, (Seq)stages.take(3));
         }

      }
   }

   private void show(long now, Seq<StageData> stages) {
      String bar;
      label18: {
         label17: {
            int width = this.TerminalWidth() / stages.size();
            bar = ((TraversableOnce)stages.map(new Serializable(this, width) {
               public static final long serialVersionUID = 0L;
               private final int width$1;

               public final String apply(StageData s) {
                  int total = s.numTasks();
                  String header = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"[Stage ", ":"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(s.stageId())}));
                  String tailer = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " + ", ") / ", "]"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(s.numCompleteTasks()), BoxesRunTime.boxToInteger(s.numActiveTasks()), BoxesRunTime.boxToInteger(total)}));
                  int w = this.width$1 - header.length() - tailer.length();
                  String var10000;
                  if (w > 0) {
                     int percent = w * s.numCompleteTasks() / total;
                     var10000 = ((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), w).map(new Serializable(this, percent) {
                        public static final long serialVersionUID = 0L;
                        private final int percent$1;

                        public final String apply(int i) {
                           return i < this.percent$1 ? "=" : (i == this.percent$1 ? ">" : " ");
                        }

                        public {
                           this.percent$1 = percent$1;
                        }
                     }, IndexedSeq$.MODULE$.canBuildFrom())).mkString("");
                  } else {
                     var10000 = "";
                  }

                  String bar = var10000;
                  return (new StringBuilder()).append((Object)header).append((Object)bar).append((Object)tailer).toString();
               }

               public {
                  this.width$1 = width$1;
               }
            }, Seq$.MODULE$.canBuildFrom())).mkString("");
            String var6 = this.lastProgressBar();
            if (bar == null) {
               if (var6 != null) {
                  break label17;
               }
            } else if (!bar.equals(var6)) {
               break label17;
            }

            if (now - this.lastUpdateTime() <= 60000L) {
               break label18;
            }
         }

         System.err.print((new StringBuilder()).append(this.CR()).append((Object)bar).toString());
         this.lastUpdateTime_$eq(now);
      }

      this.lastProgressBar_$eq(bar);
   }

   private void clear() {
      if (!this.lastProgressBar().isEmpty()) {
         System.err.printf((new StringBuilder()).append(this.CR()).append((Object)(new StringOps(Predef$.MODULE$.augmentString(" "))).$times(this.TerminalWidth())).append((Object)BoxesRunTime.boxToCharacter(this.CR())).toString());
         this.lastProgressBar_$eq("");
      }

   }

   public synchronized void finishAll() {
      this.clear();
      this.lastFinishTime_$eq(System.currentTimeMillis());
   }

   public void stop() {
      this.timer().cancel();
   }

   public ConsoleProgressBar(SparkContext sc) {
      this.sc = sc;
      Logging$class.$init$(this);
      this.CR = '\r';
      this.updatePeriodMSec = sc.getConf().getTimeAsMs("spark.ui.consoleProgress.update.interval", "200");
      this.org$apache$spark$ui$ConsoleProgressBar$$firstDelayMSec = 500L;
      this.TerminalWidth = ((String)package$.MODULE$.env().getOrElse("COLUMNS", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).isEmpty() ? 80 : (new StringOps(Predef$.MODULE$.augmentString((String)package$.MODULE$.env().get("COLUMNS").get()))).toInt();
      this.lastFinishTime = 0L;
      this.lastUpdateTime = 0L;
      this.lastProgressBar = "";
      this.timer = new Timer("refresh progress", true);
      this.timer().schedule(new TimerTask(this) {
         // $FF: synthetic field
         private final ConsoleProgressBar $outer;

         public void run() {
            this.$outer.org$apache$spark$ui$ConsoleProgressBar$$refresh();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, this.org$apache$spark$ui$ConsoleProgressBar$$firstDelayMSec(), this.updatePeriodMSec());
   }
}
