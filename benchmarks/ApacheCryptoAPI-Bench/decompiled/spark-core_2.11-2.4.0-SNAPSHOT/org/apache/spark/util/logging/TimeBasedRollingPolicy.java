package org.apache.spark.util.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=a!B\u0001\u0003\u0001\u0019a!A\u0006+j[\u0016\u0014\u0015m]3e%>dG.\u001b8h!>d\u0017nY=\u000b\u0005\r!\u0011a\u00027pO\u001eLgn\u001a\u0006\u0003\u000b\u0019\tA!\u001e;jY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0003\u0001\u001bM9\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015+5\t!!\u0003\u0002\u0017\u0005\ti!k\u001c7mS:<\u0007k\u001c7jGf\u0004\"\u0001G\u000e\u000e\u0003eQ!A\u0007\u0004\u0002\u0011%tG/\u001a:oC2L!\u0001H\r\u0003\u000f1{wmZ5oO\"Aa\u0004\u0001BA\u0002\u0013\u0005\u0001%\u0001\fs_2dwN^3s\u0013:$XM\u001d<bY6KG\u000e\\5t\u0007\u0001)\u0012!\t\t\u0003\u001d\tJ!aI\b\u0003\t1{gn\u001a\u0005\tK\u0001\u0011\t\u0019!C\u0001M\u0005Q\"o\u001c7m_Z,'/\u00138uKJ4\u0018\r\\'jY2L7o\u0018\u0013fcR\u0011qE\u000b\t\u0003\u001d!J!!K\b\u0003\tUs\u0017\u000e\u001e\u0005\bW\u0011\n\t\u00111\u0001\"\u0003\rAH%\r\u0005\t[\u0001\u0011\t\u0011)Q\u0005C\u00059\"o\u001c7m_Z,'/\u00138uKJ4\u0018\r\\'jY2L7\u000f\t\u0005\t_\u0001\u0011\t\u0011)A\u0005a\u0005A\"o\u001c7mS:<g)\u001b7f'V4g-\u001b=QCR$XM\u001d8\u0011\u0005E\"dB\u0001\b3\u0013\t\u0019t\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003kY\u0012aa\u0015;sS:<'BA\u001a\u0010\u0011!A\u0004A!A!\u0002\u0013I\u0014aF2iK\u000e\\\u0017J\u001c;feZ\fGnQ8ogR\u0014\u0018-\u001b8u!\tq!(\u0003\u0002<\u001f\t9!i\\8mK\u0006t\u0007\"B\u001f\u0001\t\u0003q\u0014A\u0002\u001fj]&$h\b\u0006\u0003@\u0001\u0006\u0013\u0005C\u0001\u000b\u0001\u0011\u0015qB\b1\u0001\"\u0011\u0015yC\b1\u00011\u0011\u001dAD\b%AA\u0002eBq\u0001\u0012\u0001A\u0002\u0013%\u0001%\u0001\toKb$(k\u001c7m_Z,'\u000fV5nK\"9a\t\u0001a\u0001\n\u00139\u0015\u0001\u00068fqR\u0014v\u000e\u001c7pm\u0016\u0014H+[7f?\u0012*\u0017\u000f\u0006\u0002(\u0011\"91&RA\u0001\u0002\u0004\t\u0003B\u0002&\u0001A\u0003&\u0011%A\toKb$(k\u001c7m_Z,'\u000fV5nK\u0002B#!\u0013'\u0011\u00059i\u0015B\u0001(\u0010\u0005!1x\u000e\\1uS2,\u0007b\u0002)\u0001\u0005\u0004%I!U\u0001\nM>\u0014X.\u0019;uKJ,\u0012A\u0015\t\u0003'bk\u0011\u0001\u0016\u0006\u0003+Z\u000bA\u0001^3yi*\tq+\u0001\u0003kCZ\f\u0017BA-U\u0005A\u0019\u0016.\u001c9mK\u0012\u000bG/\u001a$pe6\fG\u000f\u0003\u0004\\\u0001\u0001\u0006IAU\u0001\u000bM>\u0014X.\u0019;uKJ\u0004\u0003\"B/\u0001\t\u0003q\u0016AD:i_VdGMU8mY>4XM\u001d\u000b\u0003s}CQ\u0001\u0019/A\u0002\u0005\n\u0001CY=uKN$vNQ3Xe&$H/\u001a8\t\u000b\t\u0004A\u0011A2\u0002\u0015I|G\u000e\\3e\u001fZ,'\u000fF\u0001(\u0011\u0015)\u0007\u0001\"\u0001g\u00031\u0011\u0017\u0010^3t/JLG\u000f^3o)\t9s\rC\u0003iI\u0002\u0007\u0011%A\u0003csR,7\u000fC\u0003k\u0001\u0011%1.A\rdC2\u001cW\u000f\\1uK:+\u0007\u0010\u001e*pY2|g/\u001a:US6,G#A\u0011\t\u000b5\u0004A\u0011\u00018\u00029\u001d,g.\u001a:bi\u0016\u0014v\u000e\u001c7fI>3XM\u001d$jY\u0016\u001cVO\u001a4jqR\t\u0001g\u0002\u0004q\u0005!\u0005a!]\u0001\u0017)&lWMQ1tK\u0012\u0014v\u000e\u001c7j]\u001e\u0004v\u000e\\5dsB\u0011AC\u001d\u0004\u0007\u0003\tA\tAB:\u0014\u0005Il\u0001\"B\u001fs\t\u0003)H#A9\t\u000f]\u0014(\u0019!C\u0001A\u0005AR*\u0013(J\u001bVku,\u0013(U\u000bJ3\u0016\tT0T\u000b\u000e{e\nR*\t\re\u0014\b\u0015!\u0003\"\u0003ei\u0015JT%N+6{\u0016J\u0014+F%Z\u000bEjX*F\u0007>sEi\u0015\u0011\t\u000fm\u0014\u0018\u0013!C\u0001y\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIM*\u0012! \u0016\u0003sy\\\u0013a \t\u0005\u0003\u0003\tY!\u0004\u0002\u0002\u0004)!\u0011QAA\u0004\u0003%)hn\u00195fG.,GMC\u0002\u0002\n=\t!\"\u00198o_R\fG/[8o\u0013\u0011\ti!a\u0001\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r"
)
public class TimeBasedRollingPolicy implements RollingPolicy, Logging {
   private long rolloverIntervalMillis;
   private volatile long org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime;
   private final SimpleDateFormat formatter;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static boolean $lessinit$greater$default$3() {
      return TimeBasedRollingPolicy$.MODULE$.$lessinit$greater$default$3();
   }

   public static long MINIMUM_INTERVAL_SECONDS() {
      return TimeBasedRollingPolicy$.MODULE$.MINIMUM_INTERVAL_SECONDS();
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

   public long rolloverIntervalMillis() {
      return this.rolloverIntervalMillis;
   }

   public void rolloverIntervalMillis_$eq(long x$1) {
      this.rolloverIntervalMillis = x$1;
   }

   public long org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime() {
      return this.org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime;
   }

   private void org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime_$eq(long x$1) {
      this.org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime = x$1;
   }

   private SimpleDateFormat formatter() {
      return this.formatter;
   }

   public boolean shouldRollover(long bytesToBeWritten) {
      return System.currentTimeMillis() > this.org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime();
   }

   public void rolledOver() {
      this.org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime_$eq(this.calculateNextRolloverTime());
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TimeBasedRollingPolicy $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Current time: ", ", next rollover time: "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(System.currentTimeMillis())}))).append((Object)BoxesRunTime.boxToLong(this.$outer.org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime())).toString();
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

   public void bytesWritten(long bytes) {
   }

   private long calculateNextRolloverTime() {
      long now = System.currentTimeMillis();
      long targetTime = (long)(package$.MODULE$.ceil((double)now / (double)this.rolloverIntervalMillis()) * (double)this.rolloverIntervalMillis());
      this.logDebug(new Serializable(this, targetTime) {
         public static final long serialVersionUID = 0L;
         private final long targetTime$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Next rollover time is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.targetTime$1)}));
         }

         public {
            this.targetTime$1 = targetTime$1;
         }
      });
      return targetTime;
   }

   public String generateRolledOverFileSuffix() {
      return this.formatter().format(Calendar.getInstance().getTime());
   }

   public TimeBasedRollingPolicy(long rolloverIntervalMillis, String rollingFileSuffixPattern, boolean checkIntervalConstraint) {
      this.rolloverIntervalMillis = rolloverIntervalMillis;
      super();
      Logging$class.$init$(this);
      if (checkIntervalConstraint && this.rolloverIntervalMillis() < TimeBasedRollingPolicy$.MODULE$.MINIMUM_INTERVAL_SECONDS() * 1000L) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TimeBasedRollingPolicy $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolling interval [", " seconds] is too small. "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.rolloverIntervalMillis() / 1000L)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Setting the interval to the acceptable minimum of ", " seconds."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(TimeBasedRollingPolicy$.MODULE$.MINIMUM_INTERVAL_SECONDS())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.rolloverIntervalMillis_$eq(TimeBasedRollingPolicy$.MODULE$.MINIMUM_INTERVAL_SECONDS() * 1000L);
      }

      this.org$apache$spark$util$logging$TimeBasedRollingPolicy$$nextRolloverTime = this.calculateNextRolloverTime();
      this.formatter = new SimpleDateFormat(rollingFileSuffixPattern, Locale.US);
   }
}
