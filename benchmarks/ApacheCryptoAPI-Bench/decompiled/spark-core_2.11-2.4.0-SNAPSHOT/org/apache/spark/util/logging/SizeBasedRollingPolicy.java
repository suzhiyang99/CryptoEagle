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
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-a!B\u0001\u0003\u0001\u0019a!AF*ju\u0016\u0014\u0015m]3e%>dG.\u001b8h!>d\u0017nY=\u000b\u0005\r!\u0011a\u00027pO\u001eLgn\u001a\u0006\u0003\u000b\u0019\tA!\u001e;jY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0003\u0001\u001bM9\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015+5\t!!\u0003\u0002\u0017\u0005\ti!k\u001c7mS:<\u0007k\u001c7jGf\u0004\"\u0001G\u000e\u000e\u0003eQ!A\u0007\u0004\u0002\u0011%tG/\u001a:oC2L!\u0001H\r\u0003\u000f1{wmZ5oO\"Aa\u0004\u0001BA\u0002\u0013\u0005\u0001%A\ts_2dwN^3s'&TXMQ=uKN\u001c\u0001!F\u0001\"!\tq!%\u0003\u0002$\u001f\t!Aj\u001c8h\u0011!)\u0003A!a\u0001\n\u00031\u0013!\u0006:pY2|g/\u001a:TSj,')\u001f;fg~#S-\u001d\u000b\u0003O)\u0002\"A\u0004\u0015\n\u0005%z!\u0001B+oSRDqa\u000b\u0013\u0002\u0002\u0003\u0007\u0011%A\u0002yIEB\u0001\"\f\u0001\u0003\u0002\u0003\u0006K!I\u0001\u0013e>dGn\u001c<feNK'0\u001a\"zi\u0016\u001c\b\u0005\u0003\u00050\u0001\t\u0005\t\u0015!\u00031\u0003M\u0019\u0007.Z2l'&TXmQ8ogR\u0014\u0018-\u001b8u!\tq\u0011'\u0003\u00023\u001f\t9!i\\8mK\u0006t\u0007\"\u0002\u001b\u0001\t\u0003)\u0014A\u0002\u001fj]&$h\bF\u00027oa\u0002\"\u0001\u0006\u0001\t\u000by\u0019\u0004\u0019A\u0011\t\u000f=\u001a\u0004\u0013!a\u0001a!9!\b\u0001a\u0001\n\u0013\u0001\u0013!\u00072zi\u0016\u001cxK]5ui\u0016t7+\u001b8dKJ{G\u000e\\8wKJDq\u0001\u0010\u0001A\u0002\u0013%Q(A\u000fcsR,7o\u0016:jiR,gnU5oG\u0016\u0014v\u000e\u001c7pm\u0016\u0014x\fJ3r)\t9c\bC\u0004,w\u0005\u0005\t\u0019A\u0011\t\r\u0001\u0003\u0001\u0015)\u0003\"\u0003i\u0011\u0017\u0010^3t/JLG\u000f^3o'&t7-\u001a*pY2|g/\u001a:!Q\ty$\t\u0005\u0002\u000f\u0007&\u0011Ai\u0004\u0002\tm>d\u0017\r^5mK\"9a\t\u0001b\u0001\n\u00039\u0015!\u00034pe6\fG\u000f^3s+\u0005A\u0005CA%O\u001b\u0005Q%BA&M\u0003\u0011!X\r\u001f;\u000b\u00035\u000bAA[1wC&\u0011qJ\u0013\u0002\u0011'&l\u0007\u000f\\3ECR,gi\u001c:nCRDa!\u0015\u0001!\u0002\u0013A\u0015A\u00034pe6\fG\u000f^3sA!)1\u000b\u0001C\u0001)\u0006q1\u000f[8vY\u0012\u0014v\u000e\u001c7pm\u0016\u0014HC\u0001\u0019V\u0011\u00151&\u000b1\u0001\"\u0003A\u0011\u0017\u0010^3t)>\u0014Um\u0016:jiR,g\u000eC\u0003Y\u0001\u0011\u0005\u0011,\u0001\u0006s_2dW\rZ(wKJ$\u0012a\n\u0005\u00067\u0002!\t\u0001X\u0001\rEf$Xm],sSR$XM\u001c\u000b\u0003OuCQA\u0018.A\u0002\u0005\nQAY=uKNDQ\u0001\u0019\u0001\u0005\u0002\u0005\fAdZ3oKJ\fG/\u001a*pY2,Gm\u0014<fe\u001aKG.Z*vM\u001aL\u0007\u0010F\u0001c!\t\u0019gM\u0004\u0002\u000fI&\u0011QmD\u0001\u0007!J,G-\u001a4\n\u0005\u001dD'AB*ue&twM\u0003\u0002f\u001f\u001d1!N\u0001E\u0001\r-\facU5{K\n\u000b7/\u001a3S_2d\u0017N\\4Q_2L7-\u001f\t\u0003)14a!\u0001\u0002\t\u0002\u0019i7C\u00017\u000e\u0011\u0015!D\u000e\"\u0001p)\u0005Y\u0007bB9m\u0005\u0004%\tA]\u0001\u0013\u001b&s\u0015*T+N?NK%,R0C3R+5+F\u0001t!\tqA/\u0003\u0002v\u001f\t\u0019\u0011J\u001c;\t\r]d\u0007\u0015!\u0003t\u0003Mi\u0015JT%N+6{6+\u0013.F?\nKF+R*!\u0011\u001dIH.%A\u0005\u0002i\f1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u0012T#A>+\u0005Ab8&A?\u0011\u0007y\f9!D\u0001\u0000\u0015\u0011\t\t!a\u0001\u0002\u0013Ut7\r[3dW\u0016$'bAA\u0003\u001f\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0007\u0005%qPA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016\u0004"
)
public class SizeBasedRollingPolicy implements RollingPolicy, Logging {
   private long rolloverSizeBytes;
   private volatile long org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover;
   private final SimpleDateFormat formatter;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static boolean $lessinit$greater$default$2() {
      return SizeBasedRollingPolicy$.MODULE$.$lessinit$greater$default$2();
   }

   public static int MINIMUM_SIZE_BYTES() {
      return SizeBasedRollingPolicy$.MODULE$.MINIMUM_SIZE_BYTES();
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

   public long rolloverSizeBytes() {
      return this.rolloverSizeBytes;
   }

   public void rolloverSizeBytes_$eq(long x$1) {
      this.rolloverSizeBytes = x$1;
   }

   public long org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover() {
      return this.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover;
   }

   private void org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover_$eq(long x$1) {
      this.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover = x$1;
   }

   public SimpleDateFormat formatter() {
      return this.formatter;
   }

   public boolean shouldRollover(long bytesToBeWritten) {
      this.logDebug(new Serializable(this, bytesToBeWritten) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SizeBasedRollingPolicy $outer;
         private final long bytesToBeWritten$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " + ", " > ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.bytesToBeWritten$1), BoxesRunTime.boxToLong(this.$outer.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover()), BoxesRunTime.boxToLong(this.$outer.rolloverSizeBytes())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.bytesToBeWritten$1 = bytesToBeWritten$1;
            }
         }
      });
      return bytesToBeWritten + this.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover() > this.rolloverSizeBytes();
   }

   public void rolledOver() {
      this.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover_$eq(0L);
   }

   public void bytesWritten(long bytes) {
      this.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover_$eq(this.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover() + bytes);
   }

   public String generateRolledOverFileSuffix() {
      return this.formatter().format(Calendar.getInstance().getTime());
   }

   public SizeBasedRollingPolicy(long rolloverSizeBytes, boolean checkSizeConstraint) {
      this.rolloverSizeBytes = rolloverSizeBytes;
      super();
      Logging$class.$init$(this);
      if (checkSizeConstraint && this.rolloverSizeBytes() < (long)SizeBasedRollingPolicy$.MODULE$.MINIMUM_SIZE_BYTES()) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SizeBasedRollingPolicy $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolling size [", " bytes] is too small. "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.rolloverSizeBytes())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Setting the size to the acceptable minimum of ", " bytes."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(SizeBasedRollingPolicy$.MODULE$.MINIMUM_SIZE_BYTES())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.rolloverSizeBytes_$eq((long)SizeBasedRollingPolicy$.MODULE$.MINIMUM_SIZE_BYTES());
      }

      this.org$apache$spark$util$logging$SizeBasedRollingPolicy$$bytesWrittenSinceRollover = 0L;
      this.formatter = new SimpleDateFormat("--yyyy-MM-dd--HH-mm-ss--SSSS", Locale.US);
   }
}
