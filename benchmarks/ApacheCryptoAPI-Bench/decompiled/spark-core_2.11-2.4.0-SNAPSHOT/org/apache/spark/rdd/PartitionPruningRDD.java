package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.TaskContext;
import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Predef$;
import scala.collection.Iterator;
import scala.collection.immutable.List$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005=a\u0001B\u0001\u0003\u0001-\u00111\u0003U1si&$\u0018n\u001c8QeVt\u0017N\\4S\t\u0012S!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001)\"\u0001D\n\u0014\u0005\u0001i\u0001c\u0001\b\u0010#5\t!!\u0003\u0002\u0011\u0005\t\u0019!\u000b\u0012#\u0011\u0005I\u0019B\u0002\u0001\u0003\u0006)\u0001\u0011\r!\u0006\u0002\u0002)F\u0011a\u0003\b\t\u0003/ii\u0011\u0001\u0007\u0006\u00023\u0005)1oY1mC&\u00111\u0004\u0007\u0002\b\u001d>$\b.\u001b8h!\t9R$\u0003\u0002\u001f1\t\u0019\u0011I\\=\t\u0011\u0001\u0002!\u0011!Q\u0001\n5\tA\u0001\u001d:fm\"A!\u0005\u0001B\u0001B\u0003%1%A\nqCJ$\u0018\u000e^5p]\u001aKG\u000e^3s\rVt7\r\u0005\u0003\u0018I\u0019J\u0013BA\u0013\u0019\u0005%1UO\\2uS>t\u0017\u0007\u0005\u0002\u0018O%\u0011\u0001\u0006\u0007\u0002\u0004\u0013:$\bCA\f+\u0013\tY\u0003DA\u0004C_>dW-\u00198\t\u00115\u0002!1!Q\u0001\f9\n!\"\u001a<jI\u0016t7-\u001a\u00132!\ry#'E\u0007\u0002a)\u0011\u0011\u0007G\u0001\be\u00164G.Z2u\u0013\t\u0019\u0004G\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011\u0015)\u0004\u0001\"\u00017\u0003\u0019a\u0014N\\5u}Q\u0019qGO\u001e\u0015\u0005aJ\u0004c\u0001\b\u0001#!)Q\u0006\u000ea\u0002]!)\u0001\u0005\u000ea\u0001\u001b!)!\u0005\u000ea\u0001G!)Q\b\u0001C!}\u000591m\\7qkR,GcA L#B\u0019\u0001\tS\t\u000f\u0005\u00053eB\u0001\"F\u001b\u0005\u0019%B\u0001#\u000b\u0003\u0019a$o\\8u}%\t\u0011$\u0003\u0002H1\u00059\u0001/Y2lC\u001e,\u0017BA%K\u0005!IE/\u001a:bi>\u0014(BA$\u0019\u0011\u0015aE\b1\u0001N\u0003\u0015\u0019\b\u000f\\5u!\tqu*D\u0001\u0005\u0013\t\u0001FAA\u0005QCJ$\u0018\u000e^5p]\")!\u000b\u0010a\u0001'\u000691m\u001c8uKb$\bC\u0001(U\u0013\t)FAA\u0006UCN\\7i\u001c8uKb$\b\"B,\u0001\t#B\u0016!D4fiB\u000b'\u000f^5uS>t7/F\u0001Z!\r9\",T\u0005\u00037b\u0011Q!\u0011:sCfD#\u0001A/\u0011\u0005y\u000bW\"A0\u000b\u0005\u0001$\u0011AC1o]>$\u0018\r^5p]&\u0011!m\u0018\u0002\r\t\u00164X\r\\8qKJ\f\u0005/[\u0004\u0006I\nA\t!Z\u0001\u0014!\u0006\u0014H/\u001b;j_:\u0004&/\u001e8j]\u001e\u0014F\t\u0012\t\u0003\u001d\u00194Q!\u0001\u0002\t\u0002\u001d\u001c2A\u001a5l!\t9\u0012.\u0003\u0002k1\t1\u0011I\\=SK\u001a\u0004\"a\u00067\n\u00055D\"\u0001D*fe&\fG.\u001b>bE2,\u0007\"B\u001bg\t\u0003yG#A3\t\u000bE4G\u0011\u0001:\u0002\r\r\u0014X-\u0019;f+\t\u0019h\u000fF\u0002uof\u00042A\u0004\u0001v!\t\u0011b\u000fB\u0003\u0015a\n\u0007Q\u0003C\u0003\u0004a\u0002\u0007\u0001\u0010E\u0002\u000f\u001fUDQA\t9A\u0002\rBqa\u001f4\u0002\u0002\u0013%A0A\u0006sK\u0006$'+Z:pYZ,G#A?\u0011\u0007y\f9!D\u0001\u0000\u0015\u0011\t\t!a\u0001\u0002\t1\fgn\u001a\u0006\u0003\u0003\u000b\tAA[1wC&\u0019\u0011\u0011B@\u0003\r=\u0013'.Z2uQ\t1W\f\u000b\u0002d;\u0002"
)
public class PartitionPruningRDD<T> extends RDD<T> {
   private final ClassTag<T> evidence$1;

   public static <T> PartitionPruningRDD<T> create(RDD<T> var0, Function1<Object, Object> var1) {
      return PartitionPruningRDD$.MODULE$.<T>create(var0, var1);
   }

   public Iterator<T> compute(Partition split, TaskContext context) {
      return this.firstParent(this.evidence$1).iterator(((PartitionPruningRDDPartition)split).parentSplit(), context);
   }

   public Partition[] getPartitions() {
      return ((PruneDependency)this.dependencies().head()).partitions();
   }

   public PartitionPruningRDD(RDD<T> prev, Function1<Object, Object> partitionFilterFunc, ClassTag<T> evidence$1) {
      super(prev.context(), List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new PruneDependency[]{new PruneDependency(prev, partitionFilterFunc)})), evidence$1);
      this.evidence$1 = evidence$1;
   }
}
