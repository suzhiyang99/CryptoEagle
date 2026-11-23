package org.apache.spark.scheduler;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.metrics.source.Source;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.Map;
import scala.collection.mutable.Map$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001U4Q!\u0001\u0002\u0001\t)\u0011a\u0003T5wK2K7\u000f^3oKJ\u0014Uo]'fiJL7m\u001d\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u00123A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\f\u000e\u0003MQ!\u0001F\u000b\u0002\rM|WO]2f\u0015\t1B!A\u0004nKR\u0014\u0018nY:\n\u0005a\u0019\"AB*pkJ\u001cW\r\u0005\u0002\u001b;5\t1D\u0003\u0002\u001d\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001f7\t9Aj\\4hS:<\u0007\u0002\u0003\u0011\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\t\r|gNZ\u0002\u0001!\t\u0019C%D\u0001\u0005\u0013\t)CAA\u0005Ta\u0006\u00148nQ8oM\")q\u0005\u0001C\u0001Q\u00051A(\u001b8jiz\"\"!K\u0016\u0011\u0005)\u0002Q\"\u0001\u0002\t\u000b\u00012\u0003\u0019\u0001\u0012\t\u000f5\u0002!\u0019!C!]\u0005Q1o\\;sG\u0016t\u0015-\\3\u0016\u0003=\u0002\"\u0001M\u001a\u000f\u00051\t\u0014B\u0001\u001a\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011A'\u000e\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005Ij\u0001BB\u001c\u0001A\u0003%q&A\u0006t_V\u00148-\u001a(b[\u0016\u0004\u0003bB\u001d\u0001\u0005\u0004%\tEO\u0001\u000f[\u0016$(/[2SK\u001eL7\u000f\u001e:z+\u0005Y\u0004C\u0001\u001fC\u001b\u0005i$B\u0001\f?\u0015\ty\u0004)\u0001\u0005d_\u0012\f\u0007.\u00197f\u0015\u0005\t\u0015aA2p[&\u00111)\u0010\u0002\u000f\u001b\u0016$(/[2SK\u001eL7\u000f\u001e:z\u0011\u0019)\u0005\u0001)A\u0005w\u0005yQ.\u001a;sS\u000e\u0014VmZ5tiJL\b\u0005C\u0004H\u0001\t\u0007I\u0011\u0001%\u0002\u001f9,X.\u0012<f]R\u001c\bk\\:uK\u0012,\u0012!\u0013\t\u0003y)K!aS\u001f\u0003\u000f\r{WO\u001c;fe\"1Q\n\u0001Q\u0001\n%\u000b\u0001C\\;n\u000bZ,g\u000e^:Q_N$X\r\u001a\u0011\t\u000f=\u0003!\u0019!C\u0005!\u00061\u0002/\u001a:MSN$XM\\3s\u00072\f7o\u001d+j[\u0016\u00148/F\u0001R!\u0011\u0011vkL-\u000e\u0003MS!\u0001V+\u0002\u000f5,H/\u00192mK*\u0011a+D\u0001\u000bG>dG.Z2uS>t\u0017B\u0001-T\u0005\ri\u0015\r\u001d\t\u0003yiK!aW\u001f\u0003\u000bQKW.\u001a:\t\ru\u0003\u0001\u0015!\u0003R\u0003]\u0001XM\u001d'jgR,g.\u001a:DY\u0006\u001c8\u000fV5nKJ\u001c\b\u0005C\u0003`\u0001\u0011\u0005\u0001-\u0001\rhKR$\u0016.\\3s\r>\u0014H*[:uK:,'o\u00117bgN$\"!\u00193\u0011\u00071\u0011\u0017,\u0003\u0002d\u001b\t1q\n\u001d;j_:DQ!\u001a0A\u0002\u0019\f1a\u00197ta\t9G\u000eE\u00021Q*L!![\u001b\u0003\u000b\rc\u0017m]:\u0011\u0005-dG\u0002\u0001\u0003\n[\u0012\f\t\u0011!A\u0003\u00029\u00141a\u0018\u00132#\ty'\u000f\u0005\u0002\ra&\u0011\u0011/\u0004\u0002\b\u001d>$\b.\u001b8h!\tQ3/\u0003\u0002u\u0005\t12\u000b]1sW2K7\u000f^3oKJLe\u000e^3sM\u0006\u001cW\r"
)
public class LiveListenerBusMetrics implements Source, Logging {
   private final SparkConf conf;
   private final String sourceName;
   private final MetricRegistry metricRegistry;
   private final Counter numEventsPosted;
   private final Map<String, Timer> org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers;
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

   public String sourceName() {
      return this.sourceName;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public Counter numEventsPosted() {
      return this.numEventsPosted;
   }

   public Map<String, Timer> org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers() {
      return this.org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers;
   }

   public synchronized Option<Timer> getTimerForListenerClass(Class<? extends SparkListenerInterface> cls) {
      String className = cls.getName();
      int maxTimed = BoxesRunTime.unboxToInt(this.conf.get(org.apache.spark.internal.config.package$.MODULE$.LISTENER_BUS_METRICS_MAX_LISTENER_CLASSES_TIMED()));
      return this.org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers().get(className).<Timer>orElse(new Serializable(this, className, maxTimed) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LiveListenerBusMetrics $outer;
         public final String className$1;
         public final int maxTimed$1;

         public final Option<Timer> apply() {
            Object var10000;
            if (this.$outer.org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers().size() == this.maxTimed$1) {
               this.$outer.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not measuring processing time for listener class ", " because a "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.className$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"maximum of ", " listener classes are already timed."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.maxTimed$1)}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               var10000 = None$.MODULE$;
            } else {
               this.$outer.org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers().update(this.className$1, this.$outer.metricRegistry().timer(MetricRegistry.name("listenerProcessingTime", new String[]{this.className$1})));
               var10000 = this.$outer.org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers().get(this.className$1);
            }

            return (Option<Timer>)var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.className$1 = className$1;
               this.maxTimed$1 = maxTimed$1;
            }
         }
      });
   }

   public LiveListenerBusMetrics(SparkConf conf) {
      this.conf = conf;
      Logging$class.$init$(this);
      this.sourceName = "LiveListenerBus";
      this.metricRegistry = new MetricRegistry();
      this.numEventsPosted = this.metricRegistry().counter(MetricRegistry.name("numEventsPosted", new String[0]));
      this.org$apache$spark$scheduler$LiveListenerBusMetrics$$perListenerClassTimers = (Map)Map$.MODULE$.apply(Nil$.MODULE$);
   }
}
