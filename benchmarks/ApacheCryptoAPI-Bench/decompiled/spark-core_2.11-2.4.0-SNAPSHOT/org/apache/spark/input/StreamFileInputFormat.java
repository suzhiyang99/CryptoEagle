package org.apache.spark.input;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.spark.SparkContext;
import org.apache.spark.internal.config.package$;
import scala.Serializable;
import scala.collection.JavaConverters$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Buffer$;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001!4a!\u0001\u0002\u0002\u0002\u0011Q!!F*ue\u0016\fWNR5mK&s\u0007/\u001e;G_Jl\u0017\r\u001e\u0006\u0003\u0007\u0011\tQ!\u001b8qkRT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u000b\u0003\u0017\r\u001a\"\u0001\u0001\u0007\u0011\t5)r#I\u0007\u0002\u001d)\u00111a\u0004\u0006\u0003!E\t1\u0001\\5c\u0015\t\u00112#A\u0005nCB\u0014X\rZ;dK*\u0011ACB\u0001\u0007Q\u0006$wn\u001c9\n\u0005Yq!AF\"p[\nLg.\u001a$jY\u0016Le\u000e];u\r>\u0014X.\u0019;\u0011\u0005aqbBA\r\u001d\u001b\u0005Q\"\"A\u000e\u0002\u000bM\u001c\u0017\r\\1\n\u0005uQ\u0012A\u0002)sK\u0012,g-\u0003\u0002 A\t11\u000b\u001e:j]\u001eT!!\b\u000e\u0011\u0005\t\u001aC\u0002\u0001\u0003\u0006I\u0001\u0011\rA\n\u0002\u0002)\u000e\u0001\u0011CA\u0014+!\tI\u0002&\u0003\u0002*5\t9aj\u001c;iS:<\u0007CA\r,\u0013\ta#DA\u0002B]fDQA\f\u0001\u0005\u0002=\na\u0001P5oSRtD#\u0001\u0019\u0011\u0007E\u0002\u0011%D\u0001\u0003\u0011\u0015\u0019\u0004\u0001\"\u00155\u0003-I7o\u00159mSR\f'\r\\3\u0015\u0007UBd\b\u0005\u0002\u001am%\u0011qG\u0007\u0002\b\u0005>|G.Z1o\u0011\u0015I$\u00071\u0001;\u0003\u001d\u0019wN\u001c;fqR\u0004\"a\u000f\u001f\u000e\u0003EI!!P\t\u0003\u0015){'mQ8oi\u0016DH\u000fC\u0003@e\u0001\u0007\u0001)\u0001\u0003gS2,\u0007CA!E\u001b\u0005\u0011%BA\"\u0014\u0003\t17/\u0003\u0002F\u0005\n!\u0001+\u0019;i\u0011\u00159\u0005\u0001\"\u0001I\u0003A\u0019X\r^'j]B\u000b'\u000f^5uS>t7\u000f\u0006\u0003J\u0019J\u001b\u0006CA\rK\u0013\tY%D\u0001\u0003V]&$\b\"B'G\u0001\u0004q\u0015AA:d!\ty\u0005+D\u0001\u0005\u0013\t\tFA\u0001\u0007Ta\u0006\u00148nQ8oi\u0016DH\u000fC\u0003:\r\u0002\u0007!\bC\u0003U\r\u0002\u0007Q+A\u0007nS:\u0004\u0016M\u001d;ji&|gn\u001d\t\u00033YK!a\u0016\u000e\u0003\u0007%sG\u000fC\u0003Z\u0001\u0019\u0005!,\u0001\nde\u0016\fG/\u001a*fG>\u0014HMU3bI\u0016\u0014HcA._GB!1\bX\f\"\u0013\ti\u0016C\u0001\u0007SK\u000e|'\u000f\u001a*fC\u0012,'\u000fC\u0003`1\u0002\u0007\u0001-A\u0003ta2LG\u000f\u0005\u0002<C&\u0011!-\u0005\u0002\u000b\u0013:\u0004X\u000f^*qY&$\b\"\u00023Y\u0001\u0004)\u0017!\u0003;b\u0007>tG/\u001a=u!\tYd-\u0003\u0002h#\t\u0011B+Y:l\u0003R$X-\u001c9u\u0007>tG/\u001a=u\u0001"
)
public abstract class StreamFileInputFormat<T> extends CombineFileInputFormat<String, T> {
   public boolean isSplitable(JobContext context, Path file) {
      return false;
   }

   public void setMinPartitions(SparkContext sc, JobContext context, int minPartitions) {
      long defaultMaxSplitBytes = BoxesRunTime.unboxToLong(sc.getConf().get(package$.MODULE$.FILES_MAX_PARTITION_BYTES()));
      long openCostInBytes = BoxesRunTime.unboxToLong(sc.getConf().get(package$.MODULE$.FILES_OPEN_COST_IN_BYTES()));
      int defaultParallelism = sc.defaultParallelism();
      Buffer files = (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(this.listStatus(context)).asScala();
      long totalBytes = BoxesRunTime.unboxToLong(((TraversableOnce)((TraversableLike)files.filterNot(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(FileStatus x$1) {
            return x$1.isDirectory();
         }
      })).map(new Serializable(this, openCostInBytes) {
         public static final long serialVersionUID = 0L;
         private final long openCostInBytes$1;

         public final long apply(FileStatus x$2) {
            return x$2.getLen() + this.openCostInBytes$1;
         }

         public {
            this.openCostInBytes$1 = openCostInBytes$1;
         }
      }, Buffer$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
      long bytesPerCore = totalBytes / (long)defaultParallelism;
      long maxSplitSize = Math.min(defaultMaxSplitBytes, Math.max(openCostInBytes, bytesPerCore));
      super.setMaxSplitSize(maxSplitSize);
   }

   public abstract RecordReader<String, T> createRecordReader(InputSplit var1, TaskAttemptContext var2);
}
