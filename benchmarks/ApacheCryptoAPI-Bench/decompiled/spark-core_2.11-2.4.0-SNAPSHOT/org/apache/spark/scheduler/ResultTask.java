package org.apache.spark.scheduler;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.ByteBuffer;
import java.util.Properties;
import org.apache.spark.Partition;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.rdd.RDD;
import org.apache.spark.serializer.SerializerInstance;
import scala.Function2;
import scala.MatchError;
import scala.Option;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}e!B\u0001\u0003\u0001\u0011Q!A\u0003*fgVdG\u000fV1tW*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u000b\u0004\u0017u\u00142c\u0001\u0001\r?A\u0019QB\u0004\t\u000e\u0003\tI!a\u0004\u0002\u0003\tQ\u000b7o\u001b\t\u0003#Ia\u0001\u0001B\u0003\u0014\u0001\t\u0007QCA\u0001V\u0007\u0001\t\"A\u0006\u000f\u0011\u0005]QR\"\u0001\r\u000b\u0003e\tQa]2bY\u0006L!a\u0007\r\u0003\u000f9{G\u000f[5oOB\u0011q#H\u0005\u0003=a\u00111!\u00118z!\t\u0001S%D\u0001\"\u0015\t\u00113%\u0001\u0002j_*\tA%\u0001\u0003kCZ\f\u0017B\u0001\u0014\"\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011%A\u0003A!A!\u0002\u0013IC&A\u0004ti\u0006<W-\u00133\u0011\u0005]Q\u0013BA\u0016\u0019\u0005\rIe\u000e^\u0005\u0003Q9A\u0011B\f\u0001\u0003\u0002\u0003\u0006I!K\u0018\u0002\u001dM$\u0018mZ3BiR,W\u000e\u001d;JI&\u0011aF\u0004\u0005\tc\u0001\u0011\t\u0011)A\u0005e\u0005QA/Y:l\u0005&t\u0017M]=\u0011\u0007M2\u0004(D\u00015\u0015\t)D!A\u0005ce>\fGmY1ti&\u0011q\u0007\u000e\u0002\n\u0005J|\u0017\rZ2bgR\u00042aF\u001d<\u0013\tQ\u0004DA\u0003BeJ\f\u0017\u0010\u0005\u0002\u0018y%\u0011Q\b\u0007\u0002\u0005\u0005f$X\r\u0003\u0005@\u0001\t\u0005\t\u0015!\u0003A\u0003%\u0001\u0018M\u001d;ji&|g\u000e\u0005\u0002B\u00056\tA!\u0003\u0002D\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\t\u000b\u0002\u0011\t\u0011)A\u0005\r\u0006!An\\2t!\r9uJ\u0015\b\u0003\u00116s!!\u0013'\u000e\u0003)S!a\u0013\u000b\u0002\rq\u0012xn\u001c;?\u0013\u0005I\u0012B\u0001(\u0019\u0003\u001d\u0001\u0018mY6bO\u0016L!\u0001U)\u0003\u0007M+\u0017O\u0003\u0002O1A\u0011QbU\u0005\u0003)\n\u0011A\u0002V1tW2{7-\u0019;j_:D\u0001B\u0016\u0001\u0003\u0006\u0004%\taV\u0001\t_V$\b/\u001e;JIV\t\u0011\u0006\u0003\u0005Z\u0001\t\u0005\t\u0015!\u0003*\u0003%yW\u000f\u001e9vi&#\u0007\u0005\u0003\u0005\\\u0001\t\u0005\t\u0015!\u0003]\u0003=awnY1m!J|\u0007/\u001a:uS\u0016\u001c\bCA/a\u001b\u0005q&BA0$\u0003\u0011)H/\u001b7\n\u0005\u0005t&A\u0003)s_B,'\u000f^5fg\"A1\r\u0001B\u0001B\u0003%\u0001(A\u000btKJL\u0017\r\\5{K\u0012$\u0016m]6NKR\u0014\u0018nY:\t\u0013\u0015\u0004!\u0011!Q\u0001\n\u0019L\u0017!\u00026pE&#\u0007cA\fhS%\u0011\u0001\u000e\u0007\u0002\u0007\u001fB$\u0018n\u001c8\n\u0005\u0015t\u0001\"C6\u0001\u0005\u0003\u0005\u000b\u0011\u00027u\u0003\u0015\t\u0007\u000f]%e!\r9r-\u001c\t\u0003]Ft!aF8\n\u0005AD\u0012A\u0002)sK\u0012,g-\u0003\u0002sg\n11\u000b\u001e:j]\u001eT!\u0001\u001d\r\n\u0005-t\u0001\"\u0003<\u0001\u0005\u0003\u0005\u000b\u0011\u00027x\u00031\t\u0007\u000f]!ui\u0016l\u0007\u000f^%e\u0013\t1h\u0002C\u0003z\u0001\u0011\u0005!0\u0001\u0004=S:LGO\u0010\u000b\u0017w~\f\t!a\u0001\u0002\u0006\u0005\u001d\u0011\u0011BA\u0006\u0003\u001b\ty!!\u0005\u0002\u0014A!Q\u0002\u0001?\u0011!\t\tR\u0010B\u0003\u007f\u0001\t\u0007QCA\u0001U\u0011\u0015A\u0003\u00101\u0001*\u0011\u0015q\u0003\u00101\u0001*\u0011\u0015\t\u0004\u00101\u00013\u0011\u0015y\u0004\u00101\u0001A\u0011\u0015)\u0005\u00101\u0001G\u0011\u00151\u0006\u00101\u0001*\u0011\u0015Y\u0006\u00101\u0001]\u0011\u0015\u0019\u0007\u00101\u00019\u0011\u001d)\u0007\u0010%AA\u0002\u0019Dqa\u001b=\u0011\u0002\u0003\u0007A\u000eC\u0004wqB\u0005\t\u0019\u00017\t\u000f\u0005]\u0001\u0001)A\u0005\r\u0006i\u0001O]3gKJ\u0014X\r\u001a'pGNDC!!\u0006\u0002\u001cA\u0019q#!\b\n\u0007\u0005}\u0001DA\u0005ue\u0006t7/[3oi\"9\u00111\u0005\u0001\u0005B\u0005\u0015\u0012a\u0002:v]R\u000b7o\u001b\u000b\u0004!\u0005\u001d\u0002\u0002CA\u0015\u0003C\u0001\r!a\u000b\u0002\u000f\r|g\u000e^3yiB\u0019\u0011)!\f\n\u0007\u0005=BAA\u0006UCN\\7i\u001c8uKb$\bbBA\u001a\u0001\u0011\u0005\u0013QG\u0001\u0013aJ,g-\u001a:sK\u0012dunY1uS>t7/F\u0001G\u0011\u001d\tI\u0004\u0001C!\u0003w\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002[\u001eQ\u0011q\b\u0002\u0002\u0002#\u0005A!!\u0011\u0002\u0015I+7/\u001e7u)\u0006\u001c8\u000eE\u0002\u000e\u0003\u00072\u0011\"\u0001\u0002\u0002\u0002#\u0005A!!\u0012\u0014\r\u0005\r\u0013qIA'!\r9\u0012\u0011J\u0005\u0004\u0003\u0017B\"AB!osJ+g\rE\u0002\u0018\u0003\u001fJ!A\n\r\t\u000fe\f\u0019\u0005\"\u0001\u0002TQ\u0011\u0011\u0011\t\u0005\u000b\u0003/\n\u0019%%A\u0005\u0002\u0005e\u0013a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$\u0013(\u0006\u0004\u0002\\\u0005E\u00141O\u000b\u0003\u0003;R3AZA0W\t\t\t\u0007\u0005\u0003\u0002d\u00055TBAA3\u0015\u0011\t9'!\u001b\u0002\u0013Ut7\r[3dW\u0016$'bAA61\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005=\u0014Q\r\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,GA\u0002@\u0002V\t\u0007Q\u0003\u0002\u0004\u0014\u0003+\u0012\r!\u0006\u0005\u000b\u0003o\n\u0019%%A\u0005\u0002\u0005e\u0014\u0001\b\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$\u0013\u0007M\u000b\u0007\u0003w\ny(!!\u0016\u0005\u0005u$f\u00017\u0002`\u00111a0!\u001eC\u0002U!aaEA;\u0005\u0004)\u0002BCAC\u0003\u0007\n\n\u0011\"\u0001\u0002\b\u0006aB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIE\nTCBA>\u0003\u0013\u000bY\t\u0002\u0004\u007f\u0003\u0007\u0013\r!\u0006\u0003\u0007'\u0005\r%\u0019A\u000b\t\u0015\u0005=\u00151IA\u0001\n\u0013\t\t*A\u0006sK\u0006$'+Z:pYZ,GCAAJ!\u0011\t)*a'\u000e\u0005\u0005]%bAAMG\u0005!A.\u00198h\u0013\u0011\ti*a&\u0003\r=\u0013'.Z2u\u0001"
)
public class ResultTask<T, U> extends Task<U> {
   private final Broadcast<byte[]> taskBinary;
   private final Partition partition;
   private final int outputId;
   private final transient Seq<TaskLocation> preferredLocs;

   public static <T, U> Option<String> $lessinit$greater$default$11() {
      return ResultTask$.MODULE$.$lessinit$greater$default$11();
   }

   public static <T, U> Option<String> $lessinit$greater$default$10() {
      return ResultTask$.MODULE$.$lessinit$greater$default$10();
   }

   public static <T, U> Option<Object> $lessinit$greater$default$9() {
      return ResultTask$.MODULE$.$lessinit$greater$default$9();
   }

   public int outputId() {
      return this.outputId;
   }

   public U runTask(TaskContext context) {
      ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
      long deserializeStartTime = System.currentTimeMillis();
      long deserializeStartCpuTime = threadMXBean.isCurrentThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() : 0L;
      SerializerInstance ser = SparkEnv$.MODULE$.get().closureSerializer().newInstance();
      Tuple2 var9 = (Tuple2)ser.deserialize(ByteBuffer.wrap(this.taskBinary.value()), Thread.currentThread().getContextClassLoader(), ClassTag$.MODULE$.apply(Tuple2.class));
      if (var9 != null) {
         RDD rdd = (RDD)var9._1();
         Function2 func = (Function2)var9._2();
         Tuple2 var12 = new Tuple2(rdd, func);
         RDD rdd = (RDD)var12._1();
         Function2 func = (Function2)var12._2();
         this._executorDeserializeTime_$eq(System.currentTimeMillis() - deserializeStartTime);
         this._executorDeserializeCpuTime_$eq(threadMXBean.isCurrentThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() - deserializeStartCpuTime : 0L);
         return (U)func.apply(context, rdd.iterator(this.partition, context));
      } else {
         throw new MatchError(var9);
      }
   }

   public Seq<TaskLocation> preferredLocations() {
      return this.preferredLocs;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"ResultTask(").append((Object)BoxesRunTime.boxToInteger(super.stageId())).append((Object)", ").append((Object)BoxesRunTime.boxToInteger(this.partitionId())).append((Object)")").toString();
   }

   public ResultTask(int stageId, int stageAttemptId, Broadcast<byte[]> taskBinary, Partition partition, Seq<TaskLocation> locs, int outputId, Properties localProperties, byte[] serializedTaskMetrics, Option<Object> jobId, Option<String> appId, Option<String> appAttemptId) {
      super(stageId, stageAttemptId, partition.index(), localProperties, serializedTaskMetrics, jobId, appId, appAttemptId);
      this.taskBinary = taskBinary;
      this.partition = partition;
      this.outputId = outputId;
      this.preferredLocs = (Seq<TaskLocation>)(locs == null ? Nil$.MODULE$ : locs.toSet().toSeq());
   }
}
