package org.apache.spark.scheduler;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.ByteBuffer;
import java.util.Properties;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDD;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.shuffle.ShuffleManager;
import org.apache.spark.shuffle.ShuffleWriter;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055e!B\u0001\u0003\u0001\u0011Q!AD*ik\u001a4G.Z'baR\u000b7o\u001b\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0013!\raQbD\u0007\u0002\u0005%\u0011aB\u0001\u0002\u0005)\u0006\u001c8\u000e\u0005\u0002\r!%\u0011\u0011C\u0001\u0002\n\u001b\u0006\u00048\u000b^1ukN\u0004\"a\u0005\f\u000e\u0003QQ!!\u0006\u0003\u0002\u0011%tG/\u001a:oC2L!a\u0006\u000b\u0003\u000f1{wmZ5oO\"I\u0011\u0004\u0001B\u0001B\u0003%1$I\u0001\bgR\fw-Z%e\u0007\u0001\u0001\"\u0001H\u0010\u000e\u0003uQ\u0011AH\u0001\u0006g\u000e\fG.Y\u0005\u0003Au\u00111!\u00138u\u0013\tIR\u0002C\u0005$\u0001\t\u0005\t\u0015!\u0003\u001cI\u0005q1\u000f^1hK\u0006#H/Z7qi&#\u0017BA\u0012\u000e\u0011!1\u0003A!A!\u0002\u00139\u0013A\u0003;bg.\u0014\u0015N\\1ssB\u0019\u0001fK\u0017\u000e\u0003%R!A\u000b\u0003\u0002\u0013\t\u0014x.\u00193dCN$\u0018B\u0001\u0017*\u0005%\u0011%o\\1eG\u0006\u001cH\u000fE\u0002\u001d]AJ!aL\u000f\u0003\u000b\u0005\u0013(/Y=\u0011\u0005q\t\u0014B\u0001\u001a\u001e\u0005\u0011\u0011\u0015\u0010^3\t\u0011Q\u0002!\u0011!Q\u0001\nU\n\u0011\u0002]1si&$\u0018n\u001c8\u0011\u0005Y:T\"\u0001\u0003\n\u0005a\"!!\u0003)beRLG/[8o\u0011!Q\u0004A!a\u0001\n\u0013Y\u0014\u0001\u00027pGN,\u0012\u0001\u0010\t\u0004{\u0015CeB\u0001 D\u001d\ty$)D\u0001A\u0015\t\t%$\u0001\u0004=e>|GOP\u0005\u0002=%\u0011A)H\u0001\ba\u0006\u001c7.Y4f\u0013\t1uIA\u0002TKFT!\u0001R\u000f\u0011\u00051I\u0015B\u0001&\u0003\u00051!\u0016m]6M_\u000e\fG/[8o\u0011!a\u0005A!a\u0001\n\u0013i\u0015\u0001\u00037pGN|F%Z9\u0015\u00059\u000b\u0006C\u0001\u000fP\u0013\t\u0001VD\u0001\u0003V]&$\bb\u0002*L\u0003\u0003\u0005\r\u0001P\u0001\u0004q\u0012\n\u0004\u0002\u0003+\u0001\u0005\u0003\u0005\u000b\u0015\u0002\u001f\u0002\u000b1|7m\u001d\u0011)\u0005M3\u0006C\u0001\u000fX\u0013\tAVDA\u0005ue\u0006t7/[3oi\"A!\f\u0001B\u0001B\u0003%1,A\bm_\u000e\fG\u000e\u0015:pa\u0016\u0014H/[3t!\ta\u0016-D\u0001^\u0015\tqv,\u0001\u0003vi&d'\"\u00011\u0002\t)\fg/Y\u0005\u0003Ev\u0013!\u0002\u0015:pa\u0016\u0014H/[3t\u0011!!\u0007A!A!\u0002\u0013i\u0013!F:fe&\fG.\u001b>fIR\u000b7o['fiJL7m\u001d\u0005\nM\u0002\u0011\t\u0011)A\u0005O*\fQA[8c\u0013\u0012\u00042\u0001\b5\u001c\u0013\tIWD\u0001\u0004PaRLwN\\\u0005\u0003M6A\u0011\u0002\u001c\u0001\u0003\u0002\u0003\u0006I!\\;\u0002\u000b\u0005\u0004\b/\u00133\u0011\u0007qAg\u000e\u0005\u0002pe:\u0011A\u0004]\u0005\u0003cv\ta\u0001\u0015:fI\u00164\u0017BA:u\u0005\u0019\u0019FO]5oO*\u0011\u0011/H\u0005\u0003Y6A\u0011b\u001e\u0001\u0003\u0002\u0003\u0006I!\u001c=\u0002\u0019\u0005\u0004\b/\u0011;uK6\u0004H/\u00133\n\u0005]l\u0001\"\u0002>\u0001\t\u0003Y\u0018A\u0002\u001fj]&$h\b\u0006\n}{z|\u0018\u0011AA\u0002\u0003\u000b\t9!!\u0003\u0002\f\u00055\u0001C\u0001\u0007\u0001\u0011\u0015I\u0012\u00101\u0001\u001c\u0011\u0015\u0019\u0013\u00101\u0001\u001c\u0011\u00151\u0013\u00101\u0001(\u0011\u0015!\u0014\u00101\u00016\u0011\u0015Q\u0014\u00101\u0001=\u0011\u0015Q\u0016\u00101\u0001\\\u0011\u0015!\u0017\u00101\u0001.\u0011\u001d1\u0017\u0010%AA\u0002\u001dDq\u0001\\=\u0011\u0002\u0003\u0007Q\u000eC\u0004xsB\u0005\t\u0019A7\t\ri\u0004A\u0011AA\t)\ra\u00181\u0003\u0005\b\u0003+\ty\u00011\u0001\u001c\u0003-\u0001\u0018M\u001d;ji&|g.\u00133\t\u0011\u0005e\u0001A1A\u0005\nm\nQ\u0002\u001d:fM\u0016\u0014(/\u001a3M_\u000e\u001c\bbBA\u000f\u0001\u0001\u0006I\u0001P\u0001\u000faJ,g-\u001a:sK\u0012dunY:!Q\r\tYB\u0016\u0005\b\u0003G\u0001A\u0011IA\u0013\u0003\u001d\u0011XO\u001c+bg.$2aDA\u0014\u0011!\tI#!\tA\u0002\u0005-\u0012aB2p]R,\u0007\u0010\u001e\t\u0004m\u00055\u0012bAA\u0018\t\tYA+Y:l\u0007>tG/\u001a=u\u0011\u0019\t\u0019\u0004\u0001C!w\u0005\u0011\u0002O]3gKJ\u0014X\r\u001a'pG\u0006$\u0018n\u001c8t\u0011\u001d\t9\u0004\u0001C!\u0003s\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002]\u001eQ\u0011Q\b\u0002\u0002\u0002#\u0005A!a\u0010\u0002\u001dMCWO\u001a4mK6\u000b\u0007\u000fV1tWB\u0019A\"!\u0011\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\t\u0005\r3CBA!\u0003\u000b\nY\u0005E\u0002\u001d\u0003\u000fJ1!!\u0013\u001e\u0005\u0019\te.\u001f*fMB\u0019A$!\u0014\n\u0007\u0005=SD\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0004{\u0003\u0003\"\t!a\u0015\u0015\u0005\u0005}\u0002BCA,\u0003\u0003\n\n\u0011\"\u0001\u0002Z\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIa*\"!a\u0017+\u0007\u001d\fif\u000b\u0002\u0002`A!\u0011\u0011MA6\u001b\t\t\u0019G\u0003\u0003\u0002f\u0005\u001d\u0014!C;oG\",7m[3e\u0015\r\tI'H\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BA7\u0003G\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011)\t\t(!\u0011\u0012\u0002\u0013\u0005\u00111O\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001d\u0016\u0005\u0005U$fA7\u0002^!Q\u0011\u0011PA!#\u0003%\t!a\u001d\u00029\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00132a!Q\u0011QPA!\u0003\u0003%I!a \u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u0003\u0003B!a!\u0002\n6\u0011\u0011Q\u0011\u0006\u0004\u0003\u000f{\u0016\u0001\u00027b]\u001eLA!a#\u0002\u0006\n1qJ\u00196fGR\u0004"
)
public class ShuffleMapTask extends Task<MapStatus> implements Logging {
   private final Broadcast<byte[]> taskBinary;
   private final Partition partition;
   private transient Seq<TaskLocation> locs;
   private final transient Seq<TaskLocation> preferredLocs;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<String> $lessinit$greater$default$10() {
      return ShuffleMapTask$.MODULE$.$lessinit$greater$default$10();
   }

   public static Option<String> $lessinit$greater$default$9() {
      return ShuffleMapTask$.MODULE$.$lessinit$greater$default$9();
   }

   public static Option<Object> $lessinit$greater$default$8() {
      return ShuffleMapTask$.MODULE$.$lessinit$greater$default$8();
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

   private Seq<TaskLocation> locs() {
      return this.locs;
   }

   private void locs_$eq(Seq<TaskLocation> x$1) {
      this.locs = x$1;
   }

   private Seq<TaskLocation> preferredLocs() {
      return this.preferredLocs;
   }

   public MapStatus runTask(TaskContext context) {
      ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
      long deserializeStartTime = System.currentTimeMillis();
      long deserializeStartCpuTime = threadMXBean.isCurrentThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() : 0L;
      SerializerInstance ser = SparkEnv$.MODULE$.get().closureSerializer().newInstance();
      Tuple2 var9 = (Tuple2)ser.deserialize(ByteBuffer.wrap(this.taskBinary.value()), Thread.currentThread().getContextClassLoader(), ClassTag$.MODULE$.apply(Tuple2.class));
      if (var9 != null) {
         RDD rdd = (RDD)var9._1();
         ShuffleDependency dep = (ShuffleDependency)var9._2();
         Tuple2 var12 = new Tuple2(rdd, dep);
         RDD rdd = (RDD)var12._1();
         ShuffleDependency dep = (ShuffleDependency)var12._2();
         this._executorDeserializeTime_$eq(System.currentTimeMillis() - deserializeStartTime);
         this._executorDeserializeCpuTime_$eq(threadMXBean.isCurrentThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() - deserializeStartCpuTime : 0L);
         ShuffleWriter writer = null;

         try {
            ShuffleManager manager = SparkEnv$.MODULE$.get().shuffleManager();
            writer = manager.getWriter(dep.shuffleHandle(), this.partitionId(), context);
            writer.write(rdd.iterator(this.partition, context));
            return (MapStatus)writer.stop(true).get();
         } catch (Exception var20) {
            try {
               if (writer == null) {
                  BoxedUnit var22 = BoxedUnit.UNIT;
               } else {
                  writer.stop(false);
               }
            } catch (Exception var19) {
               this.log().debug("Could not stop writer", var19);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            throw var20;
         }
      } else {
         throw new MatchError(var9);
      }
   }

   public Seq<TaskLocation> preferredLocations() {
      return this.preferredLocs();
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("ShuffleMapTask(%d, %d)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(super.stageId()), BoxesRunTime.boxToInteger(this.partitionId())}));
   }

   public ShuffleMapTask(int stageId, int stageAttemptId, Broadcast<byte[]> taskBinary, Partition partition, Seq<TaskLocation> locs, Properties localProperties, byte[] serializedTaskMetrics, Option<Object> jobId, Option<String> appId, Option<String> appAttemptId) {
      this.taskBinary = taskBinary;
      this.partition = partition;
      this.locs = locs;
      super(stageId, stageAttemptId, partition.index(), localProperties, serializedTaskMetrics, jobId, appId, appAttemptId);
      Logging$class.$init$(this);
      this.preferredLocs = (Seq<TaskLocation>)(this.locs() == null ? Nil$.MODULE$ : this.locs().toSet().toSeq());
   }

   public ShuffleMapTask(int partitionId) {
      this(0, 0, (Broadcast)null, new Partition() {
         // $FF: synthetic method
         public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
            return super.equals(x$1);
         }

         public int hashCode() {
            return Partition$class.hashCode(this);
         }

         public boolean equals(Object other) {
            return Partition$class.equals(this, other);
         }

         public int index() {
            return 0;
         }

         public {
            Partition$class.$init$(this);
         }
      }, (Seq)null, new Properties(), (byte[])null, ShuffleMapTask$.MODULE$.$lessinit$greater$default$8(), ShuffleMapTask$.MODULE$.$lessinit$greater$default$9(), ShuffleMapTask$.MODULE$.$lessinit$greater$default$10());
   }
}
