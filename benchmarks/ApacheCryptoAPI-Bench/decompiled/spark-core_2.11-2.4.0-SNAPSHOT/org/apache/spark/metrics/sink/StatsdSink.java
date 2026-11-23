package org.apache.spark.metrics.sink;

import com.codahale.metrics.MetricRegistry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.spark.SecurityManager;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.metrics.MetricsSystem$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005msAB\u0001\u0003\u0011\u00031A\"\u0001\u0006Ti\u0006$8\u000fZ*j].T!a\u0001\u0003\u0002\tMLgn\u001b\u0006\u0003\u000b\u0019\tq!\\3ue&\u001c7O\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h!\tia\"D\u0001\u0003\r\u0019y!\u0001#\u0001\u0007!\tQ1\u000b^1ug\u0012\u001c\u0016N\\6\u0014\u00059\t\u0002C\u0001\n\u0016\u001b\u0005\u0019\"\"\u0001\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u0019\"AB!osJ+g\rC\u0003\u0019\u001d\u0011\u0005!$\u0001\u0004=S:LGOP\u0002\u0001)\u0005a\u0001b\u0002\u000f\u000f\u0005\u0004%\t!H\u0001\u0010'R\u000bEk\u0015#`\u0017\u0016Kv\fS(T)V\ta\u0004\u0005\u0002 I5\t\u0001E\u0003\u0002\"E\u0005!A.\u00198h\u0015\u0005\u0019\u0013\u0001\u00026bm\u0006L!!\n\u0011\u0003\rM#(/\u001b8h\u0011\u00199c\u0002)A\u0005=\u0005\u00012\u000bV!U'\u0012{6*R-`\u0011>\u001bF\u000b\t\u0005\bS9\u0011\r\u0011\"\u0001\u001e\u0003=\u0019F+\u0011+T\t~[U)W0Q\u001fJ#\u0006BB\u0016\u000fA\u0003%a$\u0001\tT)\u0006#6\u000bR0L\u000bf{\u0006k\u0014*UA!9QF\u0004b\u0001\n\u0003i\u0012!E*U\u0003R\u001bFiX&F3~\u0003VIU%P\t\"1qF\u0004Q\u0001\ny\t!c\u0015+B)N#ulS#Z?B+%+S(EA!9\u0011G\u0004b\u0001\n\u0003i\u0012aD*U\u0003R\u001bFiX&F3~+f*\u0013+\t\rMr\u0001\u0015!\u0003\u001f\u0003A\u0019F+\u0011+T\t~[U)W0V\u001d&#\u0006\u0005C\u00046\u001d\t\u0007I\u0011A\u000f\u0002#M#\u0016\tV*E?.+\u0015l\u0018)S\u000b\u001aK\u0005\f\u0003\u00048\u001d\u0001\u0006IAH\u0001\u0013'R\u000bEk\u0015#`\u0017\u0016Kv\f\u0015*F\r&C\u0006\u0005C\u0004:\u001d\t\u0007I\u0011A\u000f\u0002'M#\u0016\tV*E?\u0012+e)Q+M)~Cuj\u0015+\t\rmr\u0001\u0015!\u0003\u001f\u0003Q\u0019F+\u0011+T\t~#UIR!V\u0019R{\u0006jT*UA!9QH\u0004b\u0001\n\u0003i\u0012aE*U\u0003R\u001bFi\u0018#F\r\u0006+F\nV0Q\u001fJ#\u0006BB \u000fA\u0003%a$\u0001\u000bT)\u0006#6\u000bR0E\u000b\u001a\u000bU\u000b\u0014+`!>\u0013F\u000b\t\u0005\b\u0003:\u0011\r\u0011\"\u0001\u001e\u0003U\u0019F+\u0011+T\t~#UIR!V\u0019R{\u0006+\u0012*J\u001f\u0012Caa\u0011\b!\u0002\u0013q\u0012AF*U\u0003R\u001bFi\u0018#F\r\u0006+F\nV0Q\u000bJKu\n\u0012\u0011\t\u000f\u0015s!\u0019!C\u0001;\u0005\u00192\u000bV!U'\u0012{F)\u0012$B+2#v,\u0016(J)\"1qI\u0004Q\u0001\ny\tAc\u0015+B)N#u\fR#G\u0003VcEkX+O\u0013R\u0003\u0003bB%\u000f\u0005\u0004%\t!H\u0001\u0016'R\u000bEk\u0015#`\t\u00163\u0015)\u0016'U?B\u0013VIR%Y\u0011\u0019Ye\u0002)A\u0005=\u000512\u000bV!U'\u0012{F)\u0012$B+2#v\f\u0015*F\r&C\u0006EB\u0003\u0010\u0005\u00011Qj\u0005\u0003M#9\u000b\u0006CA\u0007P\u0013\t\u0001&A\u0001\u0003TS:\\\u0007C\u0001*V\u001b\u0005\u0019&B\u0001+\u0007\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001,T\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0017'\u0003\u0006\u0004%\t!W\u0001\taJ|\u0007/\u001a:usV\t!\f\u0005\u0002\\=6\tAL\u0003\u0002^E\u0005!Q\u000f^5m\u0013\tyFL\u0001\u0006Qe>\u0004XM\u001d;jKND\u0001\"\u0019'\u0003\u0002\u0003\u0006IAW\u0001\naJ|\u0007/\u001a:us\u0002B\u0001b\u0019'\u0003\u0006\u0004%\t\u0001Z\u0001\te\u0016<\u0017n\u001d;ssV\tQ\r\u0005\u0002gY6\tqM\u0003\u0002\u0006Q*\u0011\u0011N[\u0001\tG>$\u0017\r[1mK*\t1.A\u0002d_6L!!\\4\u0003\u001d5+GO]5d%\u0016<\u0017n\u001d;ss\"Aq\u000e\u0014B\u0001B\u0003%Q-A\u0005sK\u001eL7\u000f\u001e:zA!A\u0011\u000f\u0014B\u0001B\u0003%!/A\u0006tK\u000e,(/\u001b;z\u001b\u001e\u0014\bCA:u\u001b\u00051\u0011BA;\u0007\u0005=\u0019VmY;sSRLX*\u00198bO\u0016\u0014\b\"\u0002\rM\t\u00039H\u0003\u0002=zun\u0004\"!\u0004'\t\u000ba3\b\u0019\u0001.\t\u000b\r4\b\u0019A3\t\u000bE4\b\u0019\u0001:\t\u000fud%\u0019!C\u0001;\u0005!\u0001n\\:u\u0011\u0019yH\n)A\u0005=\u0005)\u0001n\\:uA!I\u00111\u0001'C\u0002\u0013\u0005\u0011QA\u0001\u0005a>\u0014H/\u0006\u0002\u0002\bA\u0019!#!\u0003\n\u0007\u0005-1CA\u0002J]RD\u0001\"a\u0004MA\u0003%\u0011qA\u0001\u0006a>\u0014H\u000f\t\u0005\n\u0003'a%\u0019!C\u0001\u0003\u000b\t!\u0002]8mYB+'/[8e\u0011!\t9\u0002\u0014Q\u0001\n\u0005\u001d\u0011a\u00039pY2\u0004VM]5pI\u0002B\u0011\"a\u0007M\u0005\u0004%\t!!\b\u0002\u0011A|G\u000e\\+oSR,\"!a\b\u0011\t\u0005\u0005\u0012qE\u0007\u0003\u0003GQ1!!\n]\u0003)\u0019wN\\2veJ,g\u000e^\u0005\u0005\u0003S\t\u0019C\u0001\u0005US6,WK\\5u\u0011!\ti\u0003\u0014Q\u0001\n\u0005}\u0011!\u00039pY2,f.\u001b;!\u0011!\t\t\u0004\u0014b\u0001\n\u0003i\u0012A\u00029sK\u001aL\u0007\u0010C\u0004\u000261\u0003\u000b\u0011\u0002\u0010\u0002\u000fA\u0014XMZ5yA!I\u0011\u0011\b'C\u0002\u0013\u0005\u00111H\u0001\te\u0016\u0004xN\u001d;feV\u0011\u0011Q\b\t\u0004\u001b\u0005}\u0012bAA!\u0005\tq1\u000b^1ug\u0012\u0014V\r]8si\u0016\u0014\b\u0002CA#\u0019\u0002\u0006I!!\u0010\u0002\u0013I,\u0007o\u001c:uKJ\u0004\u0003bBA%\u0019\u0012\u0005\u00131J\u0001\u0006gR\f'\u000f\u001e\u000b\u0003\u0003\u001b\u00022AEA(\u0013\r\t\tf\u0005\u0002\u0005+:LG\u000fC\u0004\u0002V1#\t%a\u0013\u0002\tM$x\u000e\u001d\u0005\b\u00033bE\u0011IA&\u0003\u0019\u0011X\r]8si\u0002"
)
public class StatsdSink implements Sink, Logging {
   private final Properties property;
   private final MetricRegistry registry;
   private final String host;
   private final int port;
   private final int pollPeriod;
   private final TimeUnit pollUnit;
   private final String prefix;
   private final StatsdReporter reporter;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String STATSD_DEFAULT_PREFIX() {
      return StatsdSink$.MODULE$.STATSD_DEFAULT_PREFIX();
   }

   public static String STATSD_DEFAULT_UNIT() {
      return StatsdSink$.MODULE$.STATSD_DEFAULT_UNIT();
   }

   public static String STATSD_DEFAULT_PERIOD() {
      return StatsdSink$.MODULE$.STATSD_DEFAULT_PERIOD();
   }

   public static String STATSD_DEFAULT_PORT() {
      return StatsdSink$.MODULE$.STATSD_DEFAULT_PORT();
   }

   public static String STATSD_DEFAULT_HOST() {
      return StatsdSink$.MODULE$.STATSD_DEFAULT_HOST();
   }

   public static String STATSD_KEY_PREFIX() {
      return StatsdSink$.MODULE$.STATSD_KEY_PREFIX();
   }

   public static String STATSD_KEY_UNIT() {
      return StatsdSink$.MODULE$.STATSD_KEY_UNIT();
   }

   public static String STATSD_KEY_PERIOD() {
      return StatsdSink$.MODULE$.STATSD_KEY_PERIOD();
   }

   public static String STATSD_KEY_PORT() {
      return StatsdSink$.MODULE$.STATSD_KEY_PORT();
   }

   public static String STATSD_KEY_HOST() {
      return StatsdSink$.MODULE$.STATSD_KEY_HOST();
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

   public Properties property() {
      return this.property;
   }

   public MetricRegistry registry() {
      return this.registry;
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public int pollPeriod() {
      return this.pollPeriod;
   }

   public TimeUnit pollUnit() {
      return this.pollUnit;
   }

   public String prefix() {
      return this.prefix;
   }

   public StatsdReporter reporter() {
      return this.reporter;
   }

   public void start() {
      this.reporter().start((long)this.pollPeriod(), this.pollUnit());
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StatsdSink $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"StatsdSink started with prefix: '", "'"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.prefix()}));
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

   public void stop() {
      this.reporter().stop();
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "StatsdSink stopped.";
         }
      });
   }

   public void report() {
      this.reporter().report();
   }

   public StatsdSink(Properties property, MetricRegistry registry, SecurityManager securityMgr) {
      this.property = property;
      this.registry = registry;
      Logging$class.$init$(this);
      this.host = property.getProperty(StatsdSink$.MODULE$.STATSD_KEY_HOST(), StatsdSink$.MODULE$.STATSD_DEFAULT_HOST());
      this.port = (new StringOps(Predef$.MODULE$.augmentString(property.getProperty(StatsdSink$.MODULE$.STATSD_KEY_PORT(), StatsdSink$.MODULE$.STATSD_DEFAULT_PORT())))).toInt();
      this.pollPeriod = (new StringOps(Predef$.MODULE$.augmentString(property.getProperty(StatsdSink$.MODULE$.STATSD_KEY_PERIOD(), StatsdSink$.MODULE$.STATSD_DEFAULT_PERIOD())))).toInt();
      this.pollUnit = TimeUnit.valueOf(property.getProperty(StatsdSink$.MODULE$.STATSD_KEY_UNIT(), StatsdSink$.MODULE$.STATSD_DEFAULT_UNIT()).toUpperCase());
      this.prefix = property.getProperty(StatsdSink$.MODULE$.STATSD_KEY_PREFIX(), StatsdSink$.MODULE$.STATSD_DEFAULT_PREFIX());
      MetricsSystem$.MODULE$.checkMinimalPollingPeriod(this.pollUnit(), this.pollPeriod());
      this.reporter = new StatsdReporter(registry, this.host(), this.port(), this.prefix(), StatsdReporter$.MODULE$.$lessinit$greater$default$5(), StatsdReporter$.MODULE$.$lessinit$greater$default$6(), StatsdReporter$.MODULE$.$lessinit$greater$default$7());
   }
}
