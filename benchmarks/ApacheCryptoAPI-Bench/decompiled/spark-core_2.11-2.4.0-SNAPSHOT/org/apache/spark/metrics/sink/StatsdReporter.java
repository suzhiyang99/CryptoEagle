package org.apache.spark.metrics.sink;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.net.NetUtils;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.math.BigDecimal;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Null$;
import scala.runtime.TraitSetter;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;
import scala.util.Try$;
import scala.util.matching.Regex;

@ScalaSignature(
   bytes = "\u0006\u0001\tmb!B\u0001\u0003\u0001\u0019a!AD*uCR\u001cHMU3q_J$XM\u001d\u0006\u0003\u0007\u0011\tAa]5oW*\u0011QAB\u0001\b[\u0016$(/[2t\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e-A\u0011a\u0002F\u0007\u0002\u001f)\u0011Q\u0001\u0005\u0006\u0003#I\t\u0001bY8eC\"\fG.\u001a\u0006\u0002'\u0005\u00191m\\7\n\u0005Uy!!E*dQ\u0016$W\u000f\\3e%\u0016\u0004xN\u001d;feB\u0011qCG\u0007\u00021)\u0011\u0011DB\u0001\tS:$XM\u001d8bY&\u00111\u0004\u0007\u0002\b\u0019><w-\u001b8h\u0011!i\u0002A!A!\u0002\u0013y\u0012\u0001\u0003:fO&\u001cHO]=\u0004\u0001A\u0011a\u0002I\u0005\u0003C=\u0011a\"T3ue&\u001c'+Z4jgR\u0014\u0018\u0010\u0003\u0005$\u0001\t\u0005\t\u0015!\u0003%\u0003\u0011Awn\u001d;\u0011\u0005\u0015ZcB\u0001\u0014*\u001b\u00059#\"\u0001\u0015\u0002\u000bM\u001c\u0017\r\\1\n\u0005):\u0013A\u0002)sK\u0012,g-\u0003\u0002-[\t11\u000b\u001e:j]\u001eT!AK\u0014\t\u0011=\u0002!\u0011!Q\u0001\nA\nA\u0001]8siB\u0011a%M\u0005\u0003e\u001d\u00121!\u00138u\u0011!!\u0004A!A!\u0002\u0013!\u0013A\u00029sK\u001aL\u0007\u0010\u0003\u00057\u0001\t\u0005\t\u0015!\u00038\u0003\u00191\u0017\u000e\u001c;feB\u0011a\u0002O\u0005\u0003s=\u0011A\"T3ue&\u001cg)\u001b7uKJD\u0001b\u000f\u0001\u0003\u0002\u0003\u0006I\u0001P\u0001\te\u0006$X-\u00168jiB\u0011Q\bR\u0007\u0002})\u0011q\bQ\u0001\u000bG>t7-\u001e:sK:$(BA!C\u0003\u0011)H/\u001b7\u000b\u0003\r\u000bAA[1wC&\u0011QI\u0010\u0002\t)&lW-\u00168ji\"Aq\t\u0001B\u0001B\u0003%A(\u0001\u0007ekJ\fG/[8o+:LG\u000fC\u0003J\u0001\u0011\u0005!*\u0001\u0004=S:LGO\u0010\u000b\t\u00176su\nU)S'B\u0011A\nA\u0007\u0002\u0005!)Q\u0004\u0013a\u0001?!91\u0005\u0013I\u0001\u0002\u0004!\u0003bB\u0018I!\u0003\u0005\r\u0001\r\u0005\bi!\u0003\n\u00111\u0001%\u0011\u001d1\u0004\n%AA\u0002]Bqa\u000f%\u0011\u0002\u0003\u0007A\bC\u0004H\u0011B\u0005\t\u0019\u0001\u001f\t\u000fU\u0003!\u0019!C\u0005-\u00069\u0011\r\u001a3sKN\u001cX#A,\u0011\u0005a[V\"A-\u000b\u0005i\u0013\u0015a\u00018fi&\u0011A,\u0017\u0002\u0012\u0013:,GoU8dW\u0016$\u0018\t\u001a3sKN\u001c\bB\u00020\u0001A\u0003%q+\u0001\u0005bI\u0012\u0014Xm]:!\u0011\u001d\u0001\u0007A1A\u0005\n\u0005\f!b\u001e5ji\u0016\u001c\b/Y2f+\u0005\u0011\u0007CA2h\u001b\u0005!'BA3g\u0003!i\u0017\r^2iS:<'BA!(\u0013\tAGMA\u0003SK\u001e,\u0007\u0010\u0003\u0004k\u0001\u0001\u0006IAY\u0001\fo\"LG/Z:qC\u000e,\u0007\u0005C\u0003m\u0001\u0011\u0005S.\u0001\u0004sKB|'\u000f\u001e\u000b\u000b]F\fi!!\u0007\u0002&\u0005E\u0002C\u0001\u0014p\u0013\t\u0001xE\u0001\u0003V]&$\b\"\u0002:l\u0001\u0004\u0019\u0018AB4bk\u001e,7\u000f\u0005\u0003uk\u0012:X\"\u0001!\n\u0005Y\u0004%!C*peR,G-T1qa\tAX\u0010E\u0002\u000fsnL!A_\b\u0003\u000b\u001d\u000bWoZ3\u0011\u0005qlH\u0002\u0001\u0003\n}F\f\t\u0011!A\u0003\u0002}\u00141a\u0018\u00132#\u0011\t\t!a\u0002\u0011\u0007\u0019\n\u0019!C\u0002\u0002\u0006\u001d\u0012qAT8uQ&tw\rE\u0002'\u0003\u0013I1!a\u0003(\u0005\r\te.\u001f\u0005\b\u0003\u001fY\u0007\u0019AA\t\u0003!\u0019w.\u001e8uKJ\u001c\b#\u0002;vI\u0005M\u0001c\u0001\b\u0002\u0016%\u0019\u0011qC\b\u0003\u000f\r{WO\u001c;fe\"9\u00111D6A\u0002\u0005u\u0011A\u00035jgR|wM]1ngB)A/\u001e\u0013\u0002 A\u0019a\"!\t\n\u0007\u0005\rrBA\u0005ISN$xn\u001a:b[\"9\u0011qE6A\u0002\u0005%\u0012AB7fi\u0016\u00148\u000fE\u0003uk\u0012\nY\u0003E\u0002\u000f\u0003[I1!a\f\u0010\u0005\u0015iU\r^3s\u0011\u001d\t\u0019d\u001ba\u0001\u0003k\ta\u0001^5nKJ\u001c\b#\u0002;vI\u0005]\u0002c\u0001\b\u0002:%\u0019\u00111H\b\u0003\u000bQKW.\u001a:\t\u000f\u0005}\u0002\u0001\"\u0003\u0002B\u0005Y!/\u001a9peR<\u0015-^4f)\u0019\t\u0019%a\u0014\u0002TQ\u0019a.!\u0012\t\u0011\u0005\u001d\u0013Q\ba\u0002\u0003\u0013\naa]8dW\u0016$\bc\u0001-\u0002L%\u0019\u0011QJ-\u0003\u001d\u0011\u000bG/Y4sC6\u001cvnY6fi\"9\u0011\u0011KA\u001f\u0001\u0004!\u0013\u0001\u00028b[\u0016D\u0001\"!\u0016\u0002>\u0001\u0007\u0011qK\u0001\u0006O\u0006,x-\u001a\u0019\u0005\u00033\ni\u0006\u0005\u0003\u000fs\u0006m\u0003c\u0001?\u0002^\u0011Y\u0011qLA*\u0003\u0003\u0005\tQ!\u0001\u0000\u0005\ryFE\r\u0005\b\u0003G\u0002A\u0011BA3\u00035\u0011X\r]8si\u000e{WO\u001c;feR1\u0011qMA6\u0003[\"2A\\A5\u0011!\t9%!\u0019A\u0004\u0005%\u0003bBA)\u0003C\u0002\r\u0001\n\u0005\t\u0003_\n\t\u00071\u0001\u0002\u0014\u000591m\\;oi\u0016\u0014\bbBA:\u0001\u0011%\u0011QO\u0001\u0010e\u0016\u0004xN\u001d;ISN$xn\u001a:b[R1\u0011qOA>\u0003{\"2A\\A=\u0011!\t9%!\u001dA\u0004\u0005%\u0003bBA)\u0003c\u0002\r\u0001\n\u0005\t\u0003\u007f\n\t\b1\u0001\u0002 \u0005I\u0001.[:u_\u001e\u0014\u0018-\u001c\u0005\b\u0003\u0007\u0003A\u0011BAC\u00035\u0011X\r]8si6+G/\u001a:fIR1\u0011qQAF\u0003\u001b#2A\\AE\u0011!\t9%!!A\u0004\u0005%\u0003bBA)\u0003\u0003\u0003\r\u0001\n\u0005\t\u0003\u001f\u000b\t\t1\u0001\u0002\u0012\u0006)Q.\u001a;feB\u0019a\"a%\n\u0007\u0005UuBA\u0004NKR,'/\u001a3\t\u000f\u0005e\u0005\u0001\"\u0003\u0002\u001c\u0006Y!/\u001a9peR$\u0016.\\3s)\u0019\ti*!)\u0002$R\u0019a.a(\t\u0011\u0005\u001d\u0013q\u0013a\u0002\u0003\u0013Bq!!\u0015\u0002\u0018\u0002\u0007A\u0005\u0003\u0005\u0002&\u0006]\u0005\u0019AA\u001c\u0003\u0015!\u0018.\\3s\u0011\u001d\tI\u000b\u0001C\u0005\u0003W\u000bAa]3oIRA\u0011QVAY\u0003g\u000b9\fF\u0002o\u0003_C\u0001\"a\u0012\u0002(\u0002\u000f\u0011\u0011\n\u0005\b\u0003#\n9\u000b1\u0001%\u0011\u001d\t),a*A\u0002\u0011\nQA^1mk\u0016Dq!!/\u0002(\u0002\u0007A%\u0001\u0006nKR\u0014\u0018n\u0019+za\u0016Dq!!0\u0001\t\u0013\ty,\u0001\u0005gk2dg*Y7f)\r!\u0013\u0011\u0019\u0005\t\u0003\u0007\fY\f1\u0001\u0002F\u0006)a.Y7fgB!a%a2%\u0013\r\tIm\n\u0002\u000byI,\u0007/Z1uK\u0012t\u0004bBAg\u0001\u0011%\u0011qZ\u0001\tg\u0006t\u0017\u000e^5{KR\u0019A%!5\t\u000f\u0005M\u00171\u001aa\u0001I\u0005\t1\u000fC\u0004\u0002X\u0002!I!!7\u0002\r\u0019|'/\\1u)\r!\u00131\u001c\u0005\t\u0003;\f)\u000e1\u0001\u0002\b\u0005\ta\u000fC\u0004\u0002b\u0002!I!a9\u0002\u0013\u0019|'/\\1u\u0003:LH\u0003BAs\u0003W\u0004BAJAtI%\u0019\u0011\u0011^\u0014\u0003\r=\u0003H/[8o\u0011!\ti.a8A\u0002\u0005\u001dqACAx\u0005\u0005\u0005\t\u0012\u0001\u0004\u0002r\u0006q1\u000b^1ug\u0012\u0014V\r]8si\u0016\u0014\bc\u0001'\u0002t\u001aI\u0011AAA\u0001\u0012\u00031\u0011Q_\n\u0005\u0003g\f9\u0010E\u0002'\u0003sL1!a?(\u0005\u0019\te.\u001f*fM\"9\u0011*a=\u0005\u0002\u0005}HCAAy\u0011)\u0011\u0019!a=\u0012\u0002\u0013\u0005!QA\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\t\u001d!f\u0001\u0013\u0003\n-\u0012!1\u0002\t\u0005\u0005\u001b\u00119\"\u0004\u0002\u0003\u0010)!!\u0011\u0003B\n\u0003%)hn\u00195fG.,GMC\u0002\u0003\u0016\u001d\n!\"\u00198o_R\fG/[8o\u0013\u0011\u0011IBa\u0004\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0003\u0006\u0003\u001e\u0005M\u0018\u0013!C\u0001\u0005?\t1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u001aTC\u0001B\u0011U\r\u0001$\u0011\u0002\u0005\u000b\u0005K\t\u00190%A\u0005\u0002\t\u0015\u0011a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$C\u0007\u0003\u0006\u0003*\u0005M\u0018\u0013!C\u0001\u0005W\t1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012*TC\u0001B\u0017U\r9$\u0011\u0002\u0005\u000b\u0005c\t\u00190%A\u0005\u0002\tM\u0012a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$c'\u0006\u0002\u00036)\u001aAH!\u0003\t\u0015\te\u00121_I\u0001\n\u0003\u0011\u0019$A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$He\u000e"
)
public class StatsdReporter extends ScheduledReporter implements Logging {
   public final String org$apache$spark$metrics$sink$StatsdReporter$$host;
   public final int org$apache$spark$metrics$sink$StatsdReporter$$port;
   private final String prefix;
   private final InetSocketAddress org$apache$spark$metrics$sink$StatsdReporter$$address;
   private final Regex whitespace;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static TimeUnit $lessinit$greater$default$7() {
      return StatsdReporter$.MODULE$.$lessinit$greater$default$7();
   }

   public static TimeUnit $lessinit$greater$default$6() {
      return StatsdReporter$.MODULE$.$lessinit$greater$default$6();
   }

   public static MetricFilter $lessinit$greater$default$5() {
      return StatsdReporter$.MODULE$.$lessinit$greater$default$5();
   }

   public static String $lessinit$greater$default$4() {
      return StatsdReporter$.MODULE$.$lessinit$greater$default$4();
   }

   public static int $lessinit$greater$default$3() {
      return StatsdReporter$.MODULE$.$lessinit$greater$default$3();
   }

   public static String $lessinit$greater$default$2() {
      return StatsdReporter$.MODULE$.$lessinit$greater$default$2();
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

   public InetSocketAddress org$apache$spark$metrics$sink$StatsdReporter$$address() {
      return this.org$apache$spark$metrics$sink$StatsdReporter$$address;
   }

   private Regex whitespace() {
      return this.whitespace;
   }

   public void report(SortedMap<String, Gauge<?>> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
      boolean var6 = false;
      Failure var7 = null;
      Try var8 = Try$.MODULE$.apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final DatagramSocket apply() {
            return new DatagramSocket();
         }
      });
      if (var8 instanceof Failure) {
         var6 = true;
         var7 = (Failure)var8;
         Throwable ioe = var7.exception();
         if (ioe instanceof IOException) {
            IOException var10 = (IOException)ioe;
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "StatsD datagram socket construction failed";
               }
            }, NetUtils.wrapException(this.org$apache$spark$metrics$sink$StatsdReporter$$host, this.org$apache$spark$metrics$sink$StatsdReporter$$port, NetUtils.getHostname(), 0, var10));
            BoxedUnit var19 = BoxedUnit.UNIT;
            return;
         }
      }

      if (var6) {
         Throwable e = var7.exception();
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "StatsD datagram socket construction failed";
            }
         }, e);
         BoxedUnit var11 = BoxedUnit.UNIT;
      } else {
         if (!(var8 instanceof Success)) {
            throw new MatchError(var8);
         }

         Success var13 = (Success)var8;
         DatagramSocket s = (DatagramSocket)var13.value();
         String localAddress = (String)Try$.MODULE$.apply(new Serializable(this, s) {
            public static final long serialVersionUID = 0L;
            private final DatagramSocket socket$1;

            public final InetAddress apply() {
               return this.socket$1.getLocalAddress();
            }

            public {
               this.socket$1 = socket$1;
            }
         }).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(InetAddress x$1) {
               return x$1.getHostAddress();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Null$ apply() {
               return null;
            }
         });
         int localPort = s.getLocalPort();
         Try$.MODULE$.apply(new Serializable(this, gauges, counters, histograms, meters, timers, s) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StatsdReporter $outer;
            private final SortedMap gauges$1;
            private final SortedMap counters$1;
            private final SortedMap histograms$1;
            private final SortedMap meters$1;
            private final SortedMap timers$1;
            public final DatagramSocket socket$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               ((IterableLike)JavaConverters$.MODULE$.asScalaSetConverter(this.gauges$1.entrySet()).asScala()).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(Map.Entry<String, Gauge<?>> e) {
                     this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer().org$apache$spark$metrics$sink$StatsdReporter$$reportGauge((String)e.getKey(), (Gauge)e.getValue(), this.$outer.socket$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               ((IterableLike)JavaConverters$.MODULE$.asScalaSetConverter(this.counters$1.entrySet()).asScala()).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(Map.Entry<String, Counter> e) {
                     this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer().org$apache$spark$metrics$sink$StatsdReporter$$reportCounter((String)e.getKey(), (Counter)e.getValue(), this.$outer.socket$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               ((IterableLike)JavaConverters$.MODULE$.asScalaSetConverter(this.histograms$1.entrySet()).asScala()).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(Map.Entry<String, Histogram> e) {
                     this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer().org$apache$spark$metrics$sink$StatsdReporter$$reportHistogram((String)e.getKey(), (Histogram)e.getValue(), this.$outer.socket$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               ((IterableLike)JavaConverters$.MODULE$.asScalaSetConverter(this.meters$1.entrySet()).asScala()).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(Map.Entry<String, Meter> e) {
                     this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer().org$apache$spark$metrics$sink$StatsdReporter$$reportMetered((String)e.getKey(), (Metered)e.getValue(), this.$outer.socket$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               ((IterableLike)JavaConverters$.MODULE$.asScalaSetConverter(this.timers$1.entrySet()).asScala()).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(Map.Entry<String, Timer> e) {
                     this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer().org$apache$spark$metrics$sink$StatsdReporter$$reportTimer((String)e.getKey(), (Timer)e.getValue(), this.$outer.socket$1);
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

            // $FF: synthetic method
            public StatsdReporter org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.gauges$1 = gauges$1;
                  this.counters$1 = counters$1;
                  this.histograms$1 = histograms$1;
                  this.meters$1 = meters$1;
                  this.timers$1 = timers$1;
                  this.socket$1 = socket$1;
               }
            }
         }).recover(new Serializable(this, localAddress, localPort) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StatsdReporter $outer;
            private final String localAddress$1;
            private final int localPort$1;

            public final <A1 extends Throwable, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
               Object var5;
               if (x1 instanceof IOException) {
                  IOException var4 = (IOException)x1;
                  this.$outer.logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unable to send packets to StatsD"})))).s(Nil$.MODULE$);
                     }
                  }, NetUtils.wrapException(this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$address().getHostString(), this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$address().getPort(), this.localAddress$1, this.localPort$1, var4));
                  var5 = BoxedUnit.UNIT;
               } else if (x1 != null) {
                  this.$outer.logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unable to send packets to StatsD at '", ":", "'"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer().org$apache$spark$metrics$sink$StatsdReporter$$host, BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer().org$apache$spark$metrics$sink$StatsdReporter$$port)}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  }, x1);
                  var5 = BoxedUnit.UNIT;
               } else {
                  var5 = var2.apply(x1);
               }

               return (B1)var5;
            }

            public final boolean isDefinedAt(Throwable x1) {
               boolean var3;
               if (x1 instanceof IOException) {
                  var3 = true;
               } else if (x1 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }

            // $FF: synthetic method
            public StatsdReporter org$apache$spark$metrics$sink$StatsdReporter$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.localAddress$1 = localAddress$1;
                  this.localPort$1 = localPort$1;
               }
            }
         });
         Try$.MODULE$.apply(new Serializable(this, s) {
            public static final long serialVersionUID = 0L;
            private final DatagramSocket socket$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.socket$1.close();
            }

            public {
               this.socket$1 = socket$1;
            }
         }).recover(new Serializable(this, localAddress, localPort) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StatsdReporter $outer;
            private final String localAddress$1;
            private final int localPort$1;

            public final <A1 extends Throwable, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
               Object var5;
               if (x2 instanceof IOException) {
                  IOException var4 = (IOException)x2;
                  this.$outer.logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Error when close socket to StatsD";
                     }
                  }, NetUtils.wrapException(this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$address().getHostString(), this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$address().getPort(), this.localAddress$1, this.localPort$1, var4));
                  var5 = BoxedUnit.UNIT;
               } else if (x2 != null) {
                  this.$outer.logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Error when close socket to StatsD";
                     }
                  }, x2);
                  var5 = BoxedUnit.UNIT;
               } else {
                  var5 = var2.apply(x2);
               }

               return (B1)var5;
            }

            public final boolean isDefinedAt(Throwable x2) {
               boolean var3;
               if (x2 instanceof IOException) {
                  var3 = true;
               } else if (x2 != null) {
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
                  this.localAddress$1 = localAddress$1;
                  this.localPort$1 = localPort$1;
               }
            }
         });
         BoxedUnit var18 = BoxedUnit.UNIT;
      }

   }

   public void org$apache$spark$metrics$sink$StatsdReporter$$reportGauge(String name, Gauge<?> gauge, DatagramSocket socket) {
      this.formatAny(gauge.getValue()).foreach(new Serializable(this, name, socket) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StatsdReporter $outer;
         private final String name$1;
         private final DatagramSocket socket$2;

         public final void apply(String v) {
            this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$send(this.$outer.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{this.name$1}))), v, StatsdMetricType$.MODULE$.GAUGE(), this.socket$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.name$1 = name$1;
               this.socket$2 = socket$2;
            }
         }
      });
   }

   public void org$apache$spark$metrics$sink$StatsdReporter$$reportCounter(String name, Counter counter, DatagramSocket socket) {
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name}))), this.format(BoxesRunTime.boxToLong(counter.getCount())), StatsdMetricType$.MODULE$.COUNTER(), socket);
   }

   public void org$apache$spark$metrics$sink$StatsdReporter$$reportHistogram(String name, Histogram histogram, DatagramSocket socket) {
      Snapshot snapshot = histogram.getSnapshot();
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "count"}))), this.format(BoxesRunTime.boxToLong(histogram.getCount())), StatsdMetricType$.MODULE$.GAUGE(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "max"}))), this.format(BoxesRunTime.boxToLong(snapshot.getMax())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "mean"}))), this.format(BoxesRunTime.boxToDouble(snapshot.getMean())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "min"}))), this.format(BoxesRunTime.boxToLong(snapshot.getMin())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "stddev"}))), this.format(BoxesRunTime.boxToDouble(snapshot.getStdDev())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p50"}))), this.format(BoxesRunTime.boxToDouble(snapshot.getMedian())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p75"}))), this.format(BoxesRunTime.boxToDouble(snapshot.get75thPercentile())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p95"}))), this.format(BoxesRunTime.boxToDouble(snapshot.get95thPercentile())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p98"}))), this.format(BoxesRunTime.boxToDouble(snapshot.get98thPercentile())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p99"}))), this.format(BoxesRunTime.boxToDouble(snapshot.get99thPercentile())), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p999"}))), this.format(BoxesRunTime.boxToDouble(snapshot.get999thPercentile())), StatsdMetricType$.MODULE$.TIMER(), socket);
   }

   public void org$apache$spark$metrics$sink$StatsdReporter$$reportMetered(String name, Metered meter, DatagramSocket socket) {
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "count"}))), this.format(BoxesRunTime.boxToLong(meter.getCount())), StatsdMetricType$.MODULE$.GAUGE(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "m1_rate"}))), this.format(BoxesRunTime.boxToDouble(this.convertRate(meter.getOneMinuteRate()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "m5_rate"}))), this.format(BoxesRunTime.boxToDouble(this.convertRate(meter.getFiveMinuteRate()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "m15_rate"}))), this.format(BoxesRunTime.boxToDouble(this.convertRate(meter.getFifteenMinuteRate()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "mean_rate"}))), this.format(BoxesRunTime.boxToDouble(this.convertRate(meter.getMeanRate()))), StatsdMetricType$.MODULE$.TIMER(), socket);
   }

   public void org$apache$spark$metrics$sink$StatsdReporter$$reportTimer(String name, Timer timer, DatagramSocket socket) {
      Snapshot snapshot = timer.getSnapshot();
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "max"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration((double)snapshot.getMax()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "mean"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.getMean()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "min"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration((double)snapshot.getMin()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "stddev"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.getStdDev()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p50"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.getMedian()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p75"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.get75thPercentile()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p95"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.get95thPercentile()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p98"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.get98thPercentile()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p99"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.get99thPercentile()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$send(this.org$apache$spark$metrics$sink$StatsdReporter$$fullName(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{name, "p999"}))), this.format(BoxesRunTime.boxToDouble(this.convertDuration(snapshot.get999thPercentile()))), StatsdMetricType$.MODULE$.TIMER(), socket);
      this.org$apache$spark$metrics$sink$StatsdReporter$$reportMetered(name, timer, socket);
   }

   public void org$apache$spark$metrics$sink$StatsdReporter$$send(String name, String value, String metricType, DatagramSocket socket) {
      byte[] bytes = this.sanitize((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ":", "|", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name, value, metricType}))).getBytes(StandardCharsets.UTF_8);
      DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.org$apache$spark$metrics$sink$StatsdReporter$$address());
      socket.send(packet);
   }

   public String org$apache$spark$metrics$sink$StatsdReporter$$fullName(Seq<String> names) {
      return MetricRegistry.name(this.prefix, (String[])names.toArray(ClassTag$.MODULE$.apply(String.class)));
   }

   private String sanitize(String s) {
      return this.whitespace().replaceAllIn(s, "-");
   }

   private String format(Object v) {
      return (String)this.formatAny(v).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
   }

   private Option<String> formatAny(Object v) {
      Object var4;
      if (v instanceof Float) {
         float var3 = BoxesRunTime.unboxToFloat(v);
         var4 = new Some((new StringOps(Predef$.MODULE$.augmentString("%2.2f"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToFloat(var3)})));
      } else if (v instanceof Double) {
         double var5 = BoxesRunTime.unboxToDouble(v);
         var4 = new Some((new StringOps(Predef$.MODULE$.augmentString("%2.2f"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(var5)})));
      } else if (v instanceof BigDecimal) {
         BigDecimal var7 = (BigDecimal)v;
         var4 = new Some((new StringOps(Predef$.MODULE$.augmentString("%2.2f"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{var7})));
      } else if (v instanceof Number) {
         var4 = new Some(v.toString());
      } else {
         var4 = None$.MODULE$;
      }

      return (Option<String>)var4;
   }

   public StatsdReporter(MetricRegistry registry, String host, int port, String prefix, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit) {
      super(registry, "statsd-reporter", filter, rateUnit, durationUnit);
      this.org$apache$spark$metrics$sink$StatsdReporter$$host = host;
      this.org$apache$spark$metrics$sink$StatsdReporter$$port = port;
      this.prefix = prefix;
      Logging$class.$init$(this);
      this.org$apache$spark$metrics$sink$StatsdReporter$$address = new InetSocketAddress(host, port);
      this.whitespace = (new StringOps(Predef$.MODULE$.augmentString("[\\s]+"))).r();
   }
}
