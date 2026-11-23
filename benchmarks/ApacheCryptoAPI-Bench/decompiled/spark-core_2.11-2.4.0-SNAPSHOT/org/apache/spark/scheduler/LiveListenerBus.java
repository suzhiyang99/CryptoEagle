package org.apache.spark.scheduler;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.metrics.MetricsSystem;
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
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Buffer$;
import scala.collection.mutable.ListBuffer;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.DynamicVariable;

@ScalaSignature(
   bytes = "\u0006\u0001\t5c!B\u0001\u0003\u0001\u0011Q!a\u0004'jm\u0016d\u0015n\u001d;f]\u0016\u0014()^:\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001B\u0001B\u0003%A#\u0001\u0003d_:47\u0001\u0001\t\u0003+Yi\u0011\u0001B\u0005\u0003/\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000be\u0001A\u0011\u0001\u000e\u0002\rqJg.\u001b;?)\tYR\u0004\u0005\u0002\u001d\u00015\t!\u0001C\u0003\u00131\u0001\u0007A\u0003C\u0005 \u0001\u0001\u0007\t\u0019!C\u0005A\u0005a1\u000f]1sW\u000e{g\u000e^3yiV\t\u0011\u0005\u0005\u0002\u0016E%\u00111\u0005\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\nK\u0001\u0001\r\u00111A\u0005\n\u0019\n\u0001c\u001d9be.\u001cuN\u001c;fqR|F%Z9\u0015\u0005\u001dR\u0003C\u0001\u0007)\u0013\tISB\u0001\u0003V]&$\bbB\u0016%\u0003\u0003\u0005\r!I\u0001\u0004q\u0012\n\u0004BB\u0017\u0001A\u0003&\u0011%A\u0007ta\u0006\u00148nQ8oi\u0016DH\u000f\t\u0005\t_\u0001\u0011\r\u0011\"\u0001\u0005a\u00059Q.\u001a;sS\u000e\u001cX#A\u0019\u0011\u0005q\u0011\u0014BA\u001a\u0003\u0005Ya\u0015N^3MSN$XM\\3s\u0005V\u001cX*\u001a;sS\u000e\u001c\bBB\u001b\u0001A\u0003%\u0011'\u0001\u0005nKR\u0014\u0018nY:!\u0011\u001d9\u0004A1A\u0005\na\nqa\u001d;beR,G-F\u0001:!\tQ4)D\u0001<\u0015\taT(\u0001\u0004bi>l\u0017n\u0019\u0006\u0003}}\n!bY8oGV\u0014(/\u001a8u\u0015\t\u0001\u0015)\u0001\u0003vi&d'\"\u0001\"\u0002\t)\fg/Y\u0005\u0003\tn\u0012Q\"\u0011;p[&\u001c'i\\8mK\u0006t\u0007B\u0002$\u0001A\u0003%\u0011(\u0001\u0005ti\u0006\u0014H/\u001a3!\u0011\u001dA\u0005A1A\u0005\na\nqa\u001d;paB,G\r\u0003\u0004K\u0001\u0001\u0006I!O\u0001\tgR|\u0007\u000f]3eA!9A\n\u0001b\u0001\n\u0013i\u0015\u0001\u00063s_B\u0004X\rZ#wK:$8oQ8v]R,'/F\u0001O!\tQt*\u0003\u0002Qw\tQ\u0011\t^8nS\u000eduN\\4\t\rI\u0003\u0001\u0015!\u0003O\u0003U!'o\u001c9qK\u0012,e/\u001a8ug\u000e{WO\u001c;fe\u0002Bq\u0001\u0016\u0001A\u0002\u0013%Q+A\nmCN$(+\u001a9peR$\u0016.\\3ti\u0006l\u0007/F\u0001W!\taq+\u0003\u0002Y\u001b\t!Aj\u001c8h\u0011\u001dQ\u0006\u00011A\u0005\nm\u000bq\u0003\\1tiJ+\u0007o\u001c:u)&lWm\u001d;b[B|F%Z9\u0015\u0005\u001db\u0006bB\u0016Z\u0003\u0003\u0005\rA\u0016\u0005\u0007=\u0002\u0001\u000b\u0015\u0002,\u0002)1\f7\u000f\u001e*fa>\u0014H\u000fV5nKN$\u0018-\u001c9!Q\ti\u0006\r\u0005\u0002\rC&\u0011!-\u0004\u0002\tm>d\u0017\r^5mK\"9A\r\u0001b\u0001\n\u0013)\u0017AB9vKV,7/F\u0001g!\r9\u0007N[\u0007\u0002{%\u0011\u0011.\u0010\u0002\u0015\u0007>\u0004\u0018p\u00148Xe&$X-\u0011:sCfd\u0015n\u001d;\u0011\u0005qY\u0017B\u00017\u0003\u0005=\t5/\u001f8d\u000bZ,g\u000e^)vKV,\u0007B\u00028\u0001A\u0003%a-A\u0004rk\u0016,Xm\u001d\u0011\t\u0011A\u0004\u0001\u0019!C\u0001\u0005E\fA\"];fk\u0016$WI^3oiN,\u0012A\u001d\t\u0004gbTX\"\u0001;\u000b\u0005U4\u0018aB7vi\u0006\u0014G.\u001a\u0006\u0003o6\t!bY8mY\u0016\u001cG/[8o\u0013\tIHO\u0001\u0006MSN$()\u001e4gKJ\u0004\"\u0001H>\n\u0005q\u0014!AE*qCJ\\G*[:uK:,'/\u0012<f]RD\u0001B \u0001A\u0002\u0013\u0005!a`\u0001\u0011cV,W/\u001a3Fm\u0016tGo]0%KF$2aJA\u0001\u0011\u001dYS0!AA\u0002IDq!!\u0002\u0001A\u0003&!/A\u0007rk\u0016,X\rZ#wK:$8\u000f\t\u0015\u0004\u0003\u0007\u0001\u0007bBA\u0006\u0001\u0011\u0005\u0011QB\u0001\u0011C\u0012$Gk\\*iCJ,G-U;fk\u0016$2aJA\b\u0011!\t\t\"!\u0003A\u0002\u0005M\u0011\u0001\u00037jgR,g.\u001a:\u0011\u0007q\t)\"C\u0002\u0002\u0018\t\u0011ac\u00159be.d\u0015n\u001d;f]\u0016\u0014\u0018J\u001c;fe\u001a\f7-\u001a\u0005\b\u00037\u0001A\u0011AA\u000f\u0003Q\tG\r\u001a+p\u001b\u0006t\u0017mZ3nK:$\u0018+^3vKR\u0019q%a\b\t\u0011\u0005E\u0011\u0011\u0004a\u0001\u0003'Aq!a\t\u0001\t\u0003\t)#\u0001\tbI\u0012$vn\u0015;biV\u001c\u0018+^3vKR\u0019q%a\n\t\u0011\u0005E\u0011\u0011\u0005a\u0001\u0003'Aq!a\u000b\u0001\t\u0003\ti#\u0001\nbI\u0012$v.\u0012<f]RdunZ)vKV,GcA\u0014\u00020!A\u0011\u0011CA\u0015\u0001\u0004\t\u0019\u0002\u0003\u0005\u00024\u0001!\t\u0001BA\u001b\u0003)\tG\r\u001a+p#V,W/\u001a\u000b\u0006O\u0005]\u0012\u0011\b\u0005\t\u0003#\t\t\u00041\u0001\u0002\u0014!A\u00111HA\u0019\u0001\u0004\ti$A\u0003rk\u0016,X\r\u0005\u0003\u0002@\u0005\u0015cb\u0001\u0007\u0002B%\u0019\u00111I\u0007\u0002\rA\u0013X\rZ3g\u0013\u0011\t9%!\u0013\u0003\rM#(/\u001b8h\u0015\r\t\u0019%\u0004\u0005\b\u0003\u001b\u0002A\u0011AA(\u00039\u0011X-\\8wK2K7\u000f^3oKJ$2aJA)\u0011!\t\t\"a\u0013A\u0002\u0005M\u0001bBA+\u0001\u0011\u0005\u0011qK\u0001\u0005a>\u001cH\u000fF\u0002(\u00033Bq!a\u0017\u0002T\u0001\u0007!0A\u0003fm\u0016tG\u000fC\u0004\u0002`\u0001!I!!\u0019\u0002\u0019A|7\u000f\u001e+p#V,W/Z:\u0015\u0007\u001d\n\u0019\u0007C\u0004\u0002\\\u0005u\u0003\u0019\u0001>\t\u000f\u0005\u001d\u0004\u0001\"\u0001\u0002j\u0005)1\u000f^1siR)q%a\u001b\u0002p!9\u0011QNA3\u0001\u0004\t\u0013AA:d\u0011!\t\t(!\u001aA\u0002\u0005M\u0014!D7fiJL7m]*zgR,W\u000e\u0005\u0003\u0002v\u0005eTBAA<\u0015\tyC!\u0003\u0003\u0002|\u0005]$!D'fiJL7m]*zgR,W\u000eC\u0004\u0002\u0000\u0001!\t!!!\u0002\u001d]\f\u0017\u000e^+oi&dW)\u001c9usR\u0019q%a!\t\u000f\u0005\u0015\u0015Q\u0010a\u0001-\u0006iA/[7f_V$X*\u001b7mSNDc!! \u0002\n\u0006U\u0005#\u0002\u0007\u0002\f\u0006=\u0015bAAG\u001b\t1A\u000f\u001b:poN\u00042aZAI\u0013\r\t\u0019*\u0010\u0002\u0011)&lWm\\;u\u000bb\u001cW\r\u001d;j_:\u001c#!a$\t\u000f\u0005e\u0005\u0001\"\u0001\u0002\u001c\u0006!1\u000f^8q)\u00059\u0003\u0002CAP\u0001\u0011\u0005A!!)\u0002)\u0019Lg\u000e\u001a'jgR,g.\u001a:t\u0005f\u001cE.Y:t+\u0011\t\u0019+a1\u0015\u0005\u0005\u0015F\u0003BAT\u0003\u001f\u0004b!!+\u0002:\u0006}f\u0002BAV\u0003ksA!!,\u000246\u0011\u0011q\u0016\u0006\u0004\u0003c\u001b\u0012A\u0002\u001fs_>$h(C\u0001\u000f\u0013\r\t9,D\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\tY,!0\u0003\u0007M+\u0017OC\u0002\u000286\u0001B!!1\u0002D2\u0001A\u0001CAc\u0003;\u0013\r!a2\u0003\u0003Q\u000bB!!3\u0002\u0014A\u0019A\"a3\n\u0007\u00055WBA\u0004O_RD\u0017N\\4\t\u0015\u0005E\u0017QTA\u0001\u0002\b\t\u0019.\u0001\u0006fm&$WM\\2fIE\u0002b!!6\u0002\\\u0006}VBAAl\u0015\r\tI.D\u0001\be\u00164G.Z2u\u0013\u0011\ti.a6\u0003\u0011\rc\u0017m]:UC\u001eD\u0001\"!9\u0001\t\u0003!\u00111]\u0001\nY&\u001cH/\u001a8feN,\"!!:\u0011\r\u0005\u001d\u0018\u0011^A\n\u001b\u0005y\u0014bAAv\u007f\t!A*[:u\u0011!\ty\u000f\u0001C\u0001\u0005\u0005E\u0018\u0001D1di&4X-U;fk\u0016\u001cHCAAz!\u0019\ty$!>\u0002>%!\u0011q_A%\u0005\r\u0019V\r^\u0004\t\u0003w\u0014\u0001\u0012\u0001\u0003\u0002~\u0006yA*\u001b<f\u0019&\u001cH/\u001a8fe\n+8\u000fE\u0002\u001d\u0003\u007f4q!\u0001\u0002\t\u0002\u0011\u0011\taE\u0002\u0002\u0000.Aq!GA\u0000\t\u0003\u0011)\u0001\u0006\u0002\u0002~\"Q!\u0011BA\u0000\u0005\u0004%\tAa\u0003\u0002)]LG\u000f[5o\u0019&\u001cH/\u001a8feRC'/Z1e+\t\u0011i\u0001\u0005\u0004\u0003\u0010\tM!qC\u0007\u0003\u0005#Q!\u0001Q\u0007\n\t\tU!\u0011\u0003\u0002\u0010\tft\u0017-\\5d-\u0006\u0014\u0018.\u00192mKB\u0019AB!\u0007\n\u0007\tmQBA\u0004C_>dW-\u00198\t\u0013\t}\u0011q Q\u0001\n\t5\u0011!F<ji\"Lg\u000eT5ti\u0016tWM\u001d+ie\u0016\fG\r\t\u0005\f\u0005G\tyP1A\u0005\u0002\t\u0011)#\u0001\u0007T\u0011\u0006\u0013V\tR0R+\u0016+V)\u0006\u0002\u0003(A!!\u0011\u0006B\u0018\u001b\t\u0011YCC\u0002\u0003.\u0005\u000bA\u0001\\1oO&!\u0011q\tB\u0016\u0011%\u0011\u0019$a@!\u0002\u0013\u00119#A\u0007T\u0011\u0006\u0013V\tR0R+\u0016+V\t\t\u0005\f\u0005o\tyP1A\u0005\u0002\t\u0011)#\u0001\tB!B{6\u000bV!U+N{\u0016+V#V\u000b\"I!1HA\u0000A\u0003%!qE\u0001\u0012\u0003B\u0003vl\u0015+B)V\u001bv,U+F+\u0016\u0003\u0003b\u0003B \u0003\u007f\u0014\r\u0011\"\u0001\u0003\u0005K\t\u0011$\u0012-F\u0007V#vJU0N\u0003:\u000bu)R'F\u001dR{\u0016+V#V\u000b\"I!1IA\u0000A\u0003%!qE\u0001\u001b\u000bb+5)\u0016+P%~k\u0015IT!H\u000b6+e\nV0R+\u0016+V\t\t\u0005\f\u0005\u000f\nyP1A\u0005\u0002\t\u0011)#A\bF-\u0016sEk\u0018'P\u000f~\u000bV+R+F\u0011%\u0011Y%a@!\u0002\u0013\u00119#\u0001\tF-\u0016sEk\u0018'P\u000f~\u000bV+R+FA\u0001"
)
public class LiveListenerBus {
   private final SparkConf conf;
   private SparkContext sparkContext;
   private final LiveListenerBusMetrics metrics;
   private final AtomicBoolean org$apache$spark$scheduler$LiveListenerBus$$started;
   private final AtomicBoolean org$apache$spark$scheduler$LiveListenerBus$$stopped;
   private final AtomicLong droppedEventsCounter;
   private volatile long lastReportTimestamp;
   private final CopyOnWriteArrayList<AsyncEventQueue> org$apache$spark$scheduler$LiveListenerBus$$queues;
   private volatile ListBuffer<SparkListenerEvent> queuedEvents;

   public static DynamicVariable<Object> withinListenerThread() {
      return LiveListenerBus$.MODULE$.withinListenerThread();
   }

   private SparkContext sparkContext() {
      return this.sparkContext;
   }

   private void sparkContext_$eq(SparkContext x$1) {
      this.sparkContext = x$1;
   }

   public LiveListenerBusMetrics metrics() {
      return this.metrics;
   }

   public AtomicBoolean org$apache$spark$scheduler$LiveListenerBus$$started() {
      return this.org$apache$spark$scheduler$LiveListenerBus$$started;
   }

   public AtomicBoolean org$apache$spark$scheduler$LiveListenerBus$$stopped() {
      return this.org$apache$spark$scheduler$LiveListenerBus$$stopped;
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

   public CopyOnWriteArrayList<AsyncEventQueue> org$apache$spark$scheduler$LiveListenerBus$$queues() {
      return this.org$apache$spark$scheduler$LiveListenerBus$$queues;
   }

   public ListBuffer<SparkListenerEvent> queuedEvents() {
      return this.queuedEvents;
   }

   public void queuedEvents_$eq(ListBuffer<SparkListenerEvent> x$1) {
      this.queuedEvents = x$1;
   }

   public void addToSharedQueue(SparkListenerInterface listener) {
      this.addToQueue(listener, LiveListenerBus$.MODULE$.SHARED_QUEUE());
   }

   public void addToManagementQueue(SparkListenerInterface listener) {
      this.addToQueue(listener, LiveListenerBus$.MODULE$.EXECUTOR_MANAGEMENT_QUEUE());
   }

   public void addToStatusQueue(SparkListenerInterface listener) {
      this.addToQueue(listener, LiveListenerBus$.MODULE$.APP_STATUS_QUEUE());
   }

   public void addToEventLogQueue(SparkListenerInterface listener) {
      this.addToQueue(listener, LiveListenerBus$.MODULE$.EVENT_LOG_QUEUE());
   }

   public void addToQueue(SparkListenerInterface listener, String queue) {
      synchronized(this) {
         if (this.org$apache$spark$scheduler$LiveListenerBus$$stopped().get()) {
            throw new IllegalStateException("LiveListenerBus is stopped.");
         } else {
            Option var4 = ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).find(new Serializable(this, queue) {
               public static final long serialVersionUID = 0L;
               private final String queue$1;

               public final boolean apply(AsyncEventQueue x$1) {
                  boolean var3;
                  label23: {
                     String var10000 = x$1.name();
                     String var2 = this.queue$1;
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
                  this.queue$1 = queue$1;
               }
            });
            if (var4 instanceof Some) {
               Some var5 = (Some)var4;
               AsyncEventQueue queue = (AsyncEventQueue)var5.x();
               queue.addListener(listener);
               BoxedUnit var7 = BoxedUnit.UNIT;
            } else {
               if (!None$.MODULE$.equals(var4)) {
                  throw new MatchError(var4);
               }

               AsyncEventQueue newQueue = new AsyncEventQueue(queue, this.conf, this.metrics());
               newQueue.addListener(listener);
               if (this.org$apache$spark$scheduler$LiveListenerBus$$started().get()) {
                  newQueue.start(this.sparkContext());
               }

               Boolean var10 = BoxesRunTime.boxToBoolean(this.org$apache$spark$scheduler$LiveListenerBus$$queues().add(newQueue));
            }

         }
      }
   }

   public synchronized void removeListener(SparkListenerInterface listener) {
      ((IterableLike)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).filter(new Serializable(this, listener) {
         public static final long serialVersionUID = 0L;
         private final SparkListenerInterface listener$1;

         public final boolean apply(AsyncEventQueue queue) {
            queue.removeListener(this.listener$1);
            return queue.listeners().isEmpty();
         }

         public {
            this.listener$1 = listener$1;
         }
      })).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LiveListenerBus $outer;

         public final boolean apply(AsyncEventQueue toRemove) {
            if (this.$outer.org$apache$spark$scheduler$LiveListenerBus$$started().get() && !this.$outer.org$apache$spark$scheduler$LiveListenerBus$$stopped().get()) {
               toRemove.stop();
            }

            return this.$outer.org$apache$spark$scheduler$LiveListenerBus$$queues().remove(toRemove);
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

   public void post(SparkListenerEvent event) {
      if (!this.org$apache$spark$scheduler$LiveListenerBus$$stopped().get()) {
         this.metrics().numEventsPosted().inc();
         if (this.queuedEvents() == null) {
            this.postToQueues(event);
         } else {
            synchronized(this) {
               if (!this.org$apache$spark$scheduler$LiveListenerBus$$started().get()) {
                  this.queuedEvents().$plus$eq(event);
                  return;
               }

               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            this.postToQueues(event);
         }
      }
   }

   private void postToQueues(SparkListenerEvent event) {
      Iterator it = this.org$apache$spark$scheduler$LiveListenerBus$$queues().iterator();

      while(it.hasNext()) {
         ((AsyncEventQueue)it.next()).post(event);
      }

   }

   public synchronized void start(SparkContext sc, MetricsSystem metricsSystem) {
      if (this.org$apache$spark$scheduler$LiveListenerBus$$started().compareAndSet(false, true)) {
         this.sparkContext_$eq(sc);
         ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).foreach(new Serializable(this, sc) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final LiveListenerBus $outer;
            private final SparkContext sc$1;

            public final void apply(AsyncEventQueue q) {
               q.start(this.sc$1);
               this.$outer.queuedEvents().foreach(new Serializable(this, q) {
                  public static final long serialVersionUID = 0L;
                  private final AsyncEventQueue q$1;

                  public final void apply(SparkListenerEvent event) {
                     this.q$1.post(event);
                  }

                  public {
                     this.q$1 = q$1;
                  }
               });
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.sc$1 = sc$1;
               }
            }
         });
         this.queuedEvents_$eq((ListBuffer)null);
         metricsSystem.registerSource(this.metrics());
      } else {
         throw new IllegalStateException("LiveListenerBus already started.");
      }
   }

   public void waitUntilEmpty(long timeoutMillis) throws TimeoutException {
      long deadline = System.currentTimeMillis() + timeoutMillis;
      ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).foreach(new Serializable(this, timeoutMillis, deadline) {
         public static final long serialVersionUID = 0L;
         private final long timeoutMillis$1;
         private final long deadline$1;

         public final void apply(AsyncEventQueue queue) {
            if (!queue.waitUntilEmpty(this.deadline$1)) {
               throw new TimeoutException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The event queue is not empty after ", " ms."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.timeoutMillis$1)})));
            }
         }

         public {
            this.timeoutMillis$1 = timeoutMillis$1;
            this.deadline$1 = deadline$1;
         }
      });
   }

   public void stop() {
      if (this.org$apache$spark$scheduler$LiveListenerBus$$started().get()) {
         if (this.org$apache$spark$scheduler$LiveListenerBus$$stopped().compareAndSet(false, true)) {
            synchronized(this) {
               ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final void apply(AsyncEventQueue x$2) {
                     x$2.stop();
                  }
               });
               this.org$apache$spark$scheduler$LiveListenerBus$$queues().clear();
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }
         }
      } else {
         throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to stop bus that has not yet started!"})))).s(Nil$.MODULE$));
      }
   }

   public <T extends SparkListenerInterface> Seq<T> findListenersByClass(ClassTag<T> evidence$1) {
      return (Seq)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).flatMap(new Serializable(this, evidence$1) {
         public static final long serialVersionUID = 0L;
         private final ClassTag evidence$1$1;

         public final Seq<T> apply(AsyncEventQueue queue) {
            return queue.<T>findListenersByClass(this.evidence$1$1);
         }

         public {
            this.evidence$1$1 = evidence$1$1;
         }
      }, Buffer$.MODULE$.canBuildFrom());
   }

   public List<SparkListenerInterface> listeners() {
      return (List)JavaConverters$.MODULE$.bufferAsJavaListConverter((Buffer)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Buffer<SparkListenerInterface> apply(AsyncEventQueue x$3) {
            return (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(x$3.listeners()).asScala();
         }
      }, Buffer$.MODULE$.canBuildFrom())).asJava();
   }

   public Set<String> activeQueues() {
      return ((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.org$apache$spark$scheduler$LiveListenerBus$$queues()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(AsyncEventQueue x$4) {
            return x$4.name();
         }
      }, Buffer$.MODULE$.canBuildFrom())).<String>toSet();
   }

   public LiveListenerBus(SparkConf conf) {
      this.conf = conf;
      this.metrics = new LiveListenerBusMetrics(conf);
      this.org$apache$spark$scheduler$LiveListenerBus$$started = new AtomicBoolean(false);
      this.org$apache$spark$scheduler$LiveListenerBus$$stopped = new AtomicBoolean(false);
      this.droppedEventsCounter = new AtomicLong(0L);
      this.lastReportTimestamp = 0L;
      this.org$apache$spark$scheduler$LiveListenerBus$$queues = new CopyOnWriteArrayList();
      this.queuedEvents = new ListBuffer<SparkListenerEvent>();
   }
}
