package org.apache.spark.scheduler;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Timer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.ListenerBus$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-g\u0001B\u0001\u0003\t-\u0011q\"Q:z]\u000e,e/\u001a8u#V,W/\u001a\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u0019I1\u0002CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0005\u0002\u0014)5\t!!\u0003\u0002\u0016\u0005\t\u00012\u000b]1sW2K7\u000f^3oKJ\u0014Uo\u001d\t\u0003/ii\u0011\u0001\u0007\u0006\u00033\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00037a\u0011q\u0001T8hO&tw\r\u0003\u0005\u001e\u0001\t\u0015\r\u0011\"\u0001\u001f\u0003\u0011q\u0017-\\3\u0016\u0003}\u0001\"\u0001I\u0012\u000f\u00055\t\u0013B\u0001\u0012\u000f\u0003\u0019\u0001&/\u001a3fM&\u0011A%\n\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\tr\u0001\u0002C\u0014\u0001\u0005\u0003\u0005\u000b\u0011B\u0010\u0002\u000b9\fW.\u001a\u0011\t\u0011%\u0002!\u0011!Q\u0001\n)\nAaY8oMB\u00111\u0006L\u0007\u0002\t%\u0011Q\u0006\u0002\u0002\n'B\f'o[\"p]\u001aD\u0001b\f\u0001\u0003\u0002\u0003\u0006I\u0001M\u0001\b[\u0016$(/[2t!\t\u0019\u0012'\u0003\u00023\u0005\t1B*\u001b<f\u0019&\u001cH/\u001a8fe\n+8/T3ue&\u001c7\u000fC\u00035\u0001\u0011\u0005Q'\u0001\u0004=S:LGO\u0010\u000b\u0005m]B\u0014\b\u0005\u0002\u0014\u0001!)Qd\ra\u0001?!)\u0011f\ra\u0001U!)qf\ra\u0001a!91\b\u0001b\u0001\n\u0013a\u0014AC3wK:$\u0018+^3vKV\tQ\bE\u0002?\u000b\u001ek\u0011a\u0010\u0006\u0003\u0001\u0006\u000b!bY8oGV\u0014(/\u001a8u\u0015\t\u00115)\u0001\u0003vi&d'\"\u0001#\u0002\t)\fg/Y\u0005\u0003\r~\u00121\u0003T5oW\u0016$'\t\\8dW&tw-U;fk\u0016\u0004\"a\u0005%\n\u0005%\u0013!AE*qCJ\\G*[:uK:,'/\u0012<f]RDaa\u0013\u0001!\u0002\u0013i\u0014aC3wK:$\u0018+^3vK\u0002Bq!\u0014\u0001C\u0002\u0013%a*\u0001\u0006fm\u0016tGoQ8v]R,\u0012a\u0014\t\u0003!Nk\u0011!\u0015\u0006\u0003%~\na!\u0019;p[&\u001c\u0017B\u0001+R\u0005)\tEo\\7jG2{gn\u001a\u0005\u0007-\u0002\u0001\u000b\u0011B(\u0002\u0017\u00154XM\u001c;D_VtG\u000f\t\u0005\b1\u0002\u0011\r\u0011\"\u0003O\u0003Q!'o\u001c9qK\u0012,e/\u001a8ug\u000e{WO\u001c;fe\"1!\f\u0001Q\u0001\n=\u000bQ\u0003\u001a:paB,G-\u0012<f]R\u001c8i\\;oi\u0016\u0014\b\u0005C\u0004]\u0001\u0001\u0007I\u0011B/\u0002'1\f7\u000f\u001e*fa>\u0014H\u000fV5nKN$\u0018-\u001c9\u0016\u0003y\u0003\"!D0\n\u0005\u0001t!\u0001\u0002'p]\u001eDqA\u0019\u0001A\u0002\u0013%1-A\fmCN$(+\u001a9peR$\u0016.\\3ti\u0006l\u0007o\u0018\u0013fcR\u0011Am\u001a\t\u0003\u001b\u0015L!A\u001a\b\u0003\tUs\u0017\u000e\u001e\u0005\bQ\u0006\f\t\u00111\u0001_\u0003\rAH%\r\u0005\u0007U\u0002\u0001\u000b\u0015\u00020\u0002)1\f7\u000f\u001e*fa>\u0014H\u000fV5nKN$\u0018-\u001c9!Q\tIG\u000e\u0005\u0002\u000e[&\u0011aN\u0004\u0002\tm>d\u0017\r^5mK\"9\u0001\u000f\u0001b\u0001\n\u0013\t\u0018a\u00047pO\u0012\u0013x\u000e\u001d9fI\u00163XM\u001c;\u0016\u0003I\u0004\"\u0001U:\n\u0005Q\f&!D!u_6L7MQ8pY\u0016\fg\u000e\u0003\u0004w\u0001\u0001\u0006IA]\u0001\u0011Y><GI]8qa\u0016$WI^3oi\u0002Bq\u0001\u001f\u0001A\u0002\u0013%\u00110\u0001\u0002tGV\t!\u0010\u0005\u0002,w&\u0011A\u0010\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\b}\u0002\u0001\r\u0011\"\u0003\u0000\u0003\u0019\u00198m\u0018\u0013fcR\u0019A-!\u0001\t\u000f!l\u0018\u0011!a\u0001u\"9\u0011Q\u0001\u0001!B\u0013Q\u0018aA:dA!A\u0011\u0011\u0002\u0001C\u0002\u0013%\u0011/A\u0004ti\u0006\u0014H/\u001a3\t\u000f\u00055\u0001\u0001)A\u0005e\u0006A1\u000f^1si\u0016$\u0007\u0005\u0003\u0005\u0002\u0012\u0001\u0011\r\u0011\"\u0003r\u0003\u001d\u0019Ho\u001c9qK\u0012Dq!!\u0006\u0001A\u0003%!/\u0001\u0005ti>\u0004\b/\u001a3!\u0011%\tI\u0002\u0001b\u0001\n\u0013\tY\"A\u0007ee>\u0004\b/\u001a3Fm\u0016tGo]\u000b\u0003\u0003;\u0001B!a\b\u0002,5\u0011\u0011\u0011\u0005\u0006\u0004_\u0005\r\"\u0002BA\u0013\u0003O\t\u0001bY8eC\"\fG.\u001a\u0006\u0003\u0003S\t1aY8n\u0013\u0011\ti#!\t\u0003\u000f\r{WO\u001c;fe\"A\u0011\u0011\u0007\u0001!\u0002\u0013\ti\"\u0001\bee>\u0004\b/\u001a3Fm\u0016tGo\u001d\u0011\t\u0013\u0005U\u0002A1A\u0005\n\u0005]\u0012A\u00049s_\u000e,7o]5oORKW.Z\u000b\u0003\u0003s\u0001B!a\b\u0002<%!\u0011QHA\u0011\u0005\u0015!\u0016.\\3s\u0011!\t\t\u0005\u0001Q\u0001\n\u0005e\u0012a\u00049s_\u000e,7o]5oORKW.\u001a\u0011\t\u0013\u0005\u0015\u0003A1A\u0005\n\u0005\u001d\u0013A\u00043jgB\fGo\u00195UQJ,\u0017\rZ\u000b\u0003\u0003\u0013\u0002B!a\u0013\u0002R5\u0011\u0011Q\n\u0006\u0004\u0003\u001f\u001a\u0015\u0001\u00027b]\u001eLA!a\u0015\u0002N\t1A\u000b\u001b:fC\u0012D\u0001\"a\u0016\u0001A\u0003%\u0011\u0011J\u0001\u0010I&\u001c\b/\u0019;dQRC'/Z1eA!9\u00111\f\u0001\u0005\n\u0005u\u0013\u0001\u00033jgB\fGo\u00195\u0015\u0003\u0011Dq!!\u0019\u0001\t#\n\u0019'\u0001\u0005hKR$\u0016.\\3s)\u0011\t)'a\u001b\u0011\u000b5\t9'!\u000f\n\u0007\u0005%dB\u0001\u0004PaRLwN\u001c\u0005\t\u0003[\ny\u00061\u0001\u0002p\u0005AA.[:uK:,'\u000fE\u0002\u0014\u0003cJ1!a\u001d\u0003\u0005Y\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe&sG/\u001a:gC\u000e,\u0007\u0002CA<\u0001\u0011\u0005!!!\u001f\u0002\u000bM$\u0018M\u001d;\u0015\u0007\u0011\fY\b\u0003\u0004y\u0003k\u0002\rA\u001f\u0005\t\u0003\u007f\u0002A\u0011\u0001\u0002\u0002^\u0005!1\u000f^8q\u0011\u001d\t\u0019\t\u0001C\u0001\u0003\u000b\u000bA\u0001]8tiR\u0019A-a\"\t\u000f\u0005%\u0015\u0011\u0011a\u0001\u000f\u0006)QM^3oi\"9\u0011Q\u0012\u0001\u0005\u0002\u0005=\u0015AD<bSR,f\u000e^5m\u000b6\u0004H/\u001f\u000b\u0005\u0003#\u000b9\nE\u0002\u000e\u0003'K1!!&\u000f\u0005\u001d\u0011un\u001c7fC:Dq!!'\u0002\f\u0002\u0007a,\u0001\u0005eK\u0006$G.\u001b8f\u00119\ti\n\u0001I\u0001\u0004\u0003\u0005I\u0011BAP\u0003G\u000bqb];qKJ$\u0003o\\:u)>\fE\u000e\u001c\u000b\u0004I\u0006\u0005\u0006bBAE\u00037\u0003\raR\u0005\u0005\u0003K\u000b9+A\u0005q_N$Hk\\!mY&!\u0011\u0011VAV\u0005-a\u0015n\u001d;f]\u0016\u0014()^:\u000b\u0005\t#qaBAX\u0005!%\u0011\u0011W\u0001\u0010\u0003NLhnY#wK:$\u0018+^3vKB\u00191#a-\u0007\r\u0005\u0011\u0001\u0012BA['\r\t\u0019\f\u0004\u0005\bi\u0005MF\u0011AA])\t\t\t\f\u0003\u0006\u0002>\u0006M&\u0019!C\u0001\u0003\u007f\u000b1\u0002U(J'>su\fU%M\u0019V\u0011\u0011\u0011\u0019\n\u0005\u0003\u0007dqIB\u0004\u0002F\u0006\u001d\u0007!!1\u0003\u0019q\u0012XMZ5oK6,g\u000e\u001e \t\u0013\u0005%\u00171\u0017Q\u0001\n\u0005\u0005\u0017\u0001\u0004)P\u0013N{ej\u0018)J\u00192\u0003\u0003"
)
public class AsyncEventQueue implements SparkListenerBus {
   private final String name;
   private final LiveListenerBusMetrics metrics;
   private final LinkedBlockingQueue<SparkListenerEvent> org$apache$spark$scheduler$AsyncEventQueue$$eventQueue;
   private final AtomicLong org$apache$spark$scheduler$AsyncEventQueue$$eventCount;
   private final AtomicLong droppedEventsCounter;
   private volatile long lastReportTimestamp;
   private final AtomicBoolean logDroppedEvent;
   private SparkContext org$apache$spark$scheduler$AsyncEventQueue$$sc;
   private final AtomicBoolean started;
   private final AtomicBoolean stopped;
   private final Counter org$apache$spark$scheduler$AsyncEventQueue$$droppedEvents;
   private final Timer org$apache$spark$scheduler$AsyncEventQueue$$processingTime;
   private final Thread dispatchThread;
   private final CopyOnWriteArrayList org$apache$spark$util$ListenerBus$$listenersPlusTimers;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static SparkListenerEvent POISON_PILL() {
      return AsyncEventQueue$.MODULE$.POISON_PILL();
   }

   public void doPostEvent(SparkListenerInterface listener, SparkListenerEvent event) {
      SparkListenerBus$class.doPostEvent(this, listener, event);
   }

   public CopyOnWriteArrayList org$apache$spark$util$ListenerBus$$listenersPlusTimers() {
      return this.org$apache$spark$util$ListenerBus$$listenersPlusTimers;
   }

   public void org$apache$spark$util$ListenerBus$_setter_$org$apache$spark$util$ListenerBus$$listenersPlusTimers_$eq(CopyOnWriteArrayList x$1) {
      this.org$apache$spark$util$ListenerBus$$listenersPlusTimers = x$1;
   }

   public List<SparkListenerInterface> listeners() {
      return ListenerBus$class.listeners(this);
   }

   public final void addListener(Object listener) {
      ListenerBus$class.addListener(this, listener);
   }

   public final void removeListener(Object listener) {
      ListenerBus$class.removeListener(this, listener);
   }

   public void postToAll(Object event) {
      ListenerBus$class.postToAll(this, event);
   }

   public <T extends SparkListenerInterface> Seq<T> findListenersByClass(ClassTag<T> evidence$1) {
      return ListenerBus$class.findListenersByClass(this, evidence$1);
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

   // $FF: synthetic method
   public void org$apache$spark$scheduler$AsyncEventQueue$$super$postToAll(SparkListenerEvent event) {
      ListenerBus$class.postToAll(this, event);
   }

   public String name() {
      return this.name;
   }

   public LinkedBlockingQueue<SparkListenerEvent> org$apache$spark$scheduler$AsyncEventQueue$$eventQueue() {
      return this.org$apache$spark$scheduler$AsyncEventQueue$$eventQueue;
   }

   public AtomicLong org$apache$spark$scheduler$AsyncEventQueue$$eventCount() {
      return this.org$apache$spark$scheduler$AsyncEventQueue$$eventCount;
   }

   private AtomicLong droppedEventsCounter() {
      return this.droppedEventsCounter;
   }

   private long lastReportTimestamp() {
      return this.lastReportTimestamp;
   }

   private void lastReportTimestamp_$eq(long x$1) {
      this.lastReportTimestamp = x$1;
   }

   private AtomicBoolean logDroppedEvent() {
      return this.logDroppedEvent;
   }

   public SparkContext org$apache$spark$scheduler$AsyncEventQueue$$sc() {
      return this.org$apache$spark$scheduler$AsyncEventQueue$$sc;
   }

   private void org$apache$spark$scheduler$AsyncEventQueue$$sc_$eq(SparkContext x$1) {
      this.org$apache$spark$scheduler$AsyncEventQueue$$sc = x$1;
   }

   private AtomicBoolean started() {
      return this.started;
   }

   private AtomicBoolean stopped() {
      return this.stopped;
   }

   public Counter org$apache$spark$scheduler$AsyncEventQueue$$droppedEvents() {
      return this.org$apache$spark$scheduler$AsyncEventQueue$$droppedEvents;
   }

   public Timer org$apache$spark$scheduler$AsyncEventQueue$$processingTime() {
      return this.org$apache$spark$scheduler$AsyncEventQueue$$processingTime;
   }

   private Thread dispatchThread() {
      return this.dispatchThread;
   }

   public void org$apache$spark$scheduler$AsyncEventQueue$$dispatch() {
      LiveListenerBus$.MODULE$.withinListenerThread().withValue(BoxesRunTime.boxToBoolean(true), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AsyncEventQueue $outer;

         public final Object apply() {
            Object var10000;
            try {
               SparkListenerEvent next = (SparkListenerEvent)this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$eventQueue().take();

               while(true) {
                  SparkListenerEvent var3 = AsyncEventQueue$.MODULE$.POISON_PILL();
                  if (next == null) {
                     if (var3 == null) {
                        break;
                     }
                  } else if (next.equals(var3)) {
                     break;
                  }

                  Timer.Context ctx = this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$processingTime().time();

                  try {
                     this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$super$postToAll(next);
                  } finally {
                     ctx.stop();
                  }

                  this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$eventCount().decrementAndGet();
                  next = (SparkListenerEvent)this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$eventQueue().take();
               }

               var10000 = BoxesRunTime.boxToLong(this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$eventCount().decrementAndGet());
            } catch (InterruptedException var9) {
               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stopping listener queue ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$anonfun$$$outer().name()}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, var9);
               var10000 = BoxedUnit.UNIT;
            }

            return var10000;
         }

         // $FF: synthetic method
         public AsyncEventQueue org$apache$spark$scheduler$AsyncEventQueue$$anonfun$$$outer() {
            return this.$outer;
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

   public Option<Timer> getTimer(SparkListenerInterface listener) {
      return this.metrics.getTimerForListenerClass(listener.getClass().asSubclass(SparkListenerInterface.class));
   }

   public void start(SparkContext sc) {
      if (this.started().compareAndSet(false, true)) {
         this.org$apache$spark$scheduler$AsyncEventQueue$$sc_$eq(sc);
         this.dispatchThread().start();
      } else {
         throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " already started!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.name()})));
      }
   }

   public void stop() {
      if (this.started().get()) {
         if (this.stopped().compareAndSet(false, true)) {
            this.org$apache$spark$scheduler$AsyncEventQueue$$eventCount().incrementAndGet();
            this.org$apache$spark$scheduler$AsyncEventQueue$$eventQueue().put(AsyncEventQueue$.MODULE$.POISON_PILL());
         }

         this.dispatchThread().join();
      } else {
         throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to stop ", " that has not yet started!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.name()})));
      }
   }

   public void post(SparkListenerEvent event) {
      if (!this.stopped().get()) {
         this.org$apache$spark$scheduler$AsyncEventQueue$$eventCount().incrementAndGet();
         if (!this.org$apache$spark$scheduler$AsyncEventQueue$$eventQueue().offer(event)) {
            this.org$apache$spark$scheduler$AsyncEventQueue$$eventCount().decrementAndGet();
            this.org$apache$spark$scheduler$AsyncEventQueue$$droppedEvents().inc();
            this.droppedEventsCounter().incrementAndGet();
            if (this.logDroppedEvent().compareAndSet(false, true)) {
               this.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final AsyncEventQueue $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Dropping event from queue ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.name()}))).append((Object)"This likely means one of the listeners is too slow and cannot keep up with ").append((Object)"the rate at which tasks are being started by the scheduler.").toString();
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

            this.logTrace(new Serializable(this, event) {
               public static final long serialVersionUID = 0L;
               private final SparkListenerEvent event$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Dropping event ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.event$1}));
               }

               public {
                  this.event$1 = event$1;
               }
            });
            long droppedCount = this.droppedEventsCounter().get();
            if (droppedCount > 0L && System.currentTimeMillis() - this.lastReportTimestamp() >= 60000L && this.droppedEventsCounter().compareAndSet(droppedCount, 0L)) {
               long prevLastReportTimestamp = this.lastReportTimestamp();
               this.lastReportTimestamp_$eq(System.currentTimeMillis());
               Date previous = new Date(prevLastReportTimestamp);
               this.logWarning(new Serializable(this, previous) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final AsyncEventQueue $outer;
                  private final Date previous$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Dropped ", " events from ", " since ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$droppedEvents(), this.$outer.name(), this.previous$1}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.previous$1 = previous$1;
                     }
                  }
               });
            }

         }
      }
   }

   public boolean waitUntilEmpty(long deadline) {
      while(this.org$apache$spark$scheduler$AsyncEventQueue$$eventCount().get() != 0L) {
         if (System.currentTimeMillis() > deadline) {
            return false;
         }

         Thread.sleep(10L);
      }

      return true;
   }

   public AsyncEventQueue(String name, SparkConf conf, LiveListenerBusMetrics metrics) {
      this.name = name;
      this.metrics = metrics;
      Logging$class.$init$(this);
      ListenerBus$class.$init$(this);
      SparkListenerBus$class.$init$(this);
      this.org$apache$spark$scheduler$AsyncEventQueue$$eventQueue = new LinkedBlockingQueue(BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.LISTENER_BUS_EVENT_QUEUE_CAPACITY())));
      this.org$apache$spark$scheduler$AsyncEventQueue$$eventCount = new AtomicLong();
      this.droppedEventsCounter = new AtomicLong(0L);
      this.lastReportTimestamp = 0L;
      this.logDroppedEvent = new AtomicBoolean(false);
      this.org$apache$spark$scheduler$AsyncEventQueue$$sc = null;
      this.started = new AtomicBoolean(false);
      this.stopped = new AtomicBoolean(false);
      this.org$apache$spark$scheduler$AsyncEventQueue$$droppedEvents = metrics.metricRegistry().counter((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"queue.", ".numDroppedEvents"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name})));
      this.org$apache$spark$scheduler$AsyncEventQueue$$processingTime = metrics.metricRegistry().timer((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"queue.", ".listenerProcessingTime"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name})));
      metrics.metricRegistry().remove((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"queue.", ".size"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name})));
      metrics.metricRegistry().register((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"queue.", ".size"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name})), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final AsyncEventQueue $outer;

         public int getValue() {
            return this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$eventQueue().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.dispatchThread = new Thread(this) {
         // $FF: synthetic field
         private final AsyncEventQueue $outer;

         public void run() {
            Utils$.MODULE$.tryOrStopSparkContext(this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$sc(), new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$scheduler$AsyncEventQueue$$anon$$$outer().org$apache$spark$scheduler$AsyncEventQueue$$dispatch();
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
         public AsyncEventQueue org$apache$spark$scheduler$AsyncEventQueue$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.setDaemon(true);
            }
         }
      };
   }
}
