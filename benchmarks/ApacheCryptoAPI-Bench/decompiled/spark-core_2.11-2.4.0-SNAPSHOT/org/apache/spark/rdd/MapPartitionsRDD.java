package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.TaskContext;
import scala.Function3;
import scala.None$;
import scala.Option;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mc!B\u0001\u0003\u0001\u0011Q!\u0001E'baB\u000b'\u000f^5uS>t7O\u0015#E\u0015\t\u0019A!A\u0002sI\u0012T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u000b\u0004\u0017I!3C\u0001\u0001\r!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\u0004%\u0012#\u0005CA\t\u0013\u0019\u0001!Qa\u0005\u0001C\u0002U\u0011\u0011!V\u0002\u0001#\t1B\u0004\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0004O_RD\u0017N\\4\u0011\u0005]i\u0012B\u0001\u0010\u0019\u0005\r\te.\u001f\u0005\tA\u0001\u0011\t\u0019!C\u0001C\u0005!\u0001O]3w+\u0005\u0011\u0003cA\u0007\u000fGA\u0011\u0011\u0003\n\u0003\u0006K\u0001\u0011\r!\u0006\u0002\u0002)\"Aq\u0005\u0001BA\u0002\u0013\u0005\u0001&\u0001\u0005qe\u00164x\fJ3r)\tIC\u0006\u0005\u0002\u0018U%\u00111\u0006\u0007\u0002\u0005+:LG\u000fC\u0004.M\u0005\u0005\t\u0019\u0001\u0012\u0002\u0007a$\u0013\u0007\u0003\u00050\u0001\t\u0005\t\u0015)\u0003#\u0003\u0015\u0001(/\u001a<!\u0011!\t\u0004A!A!\u0002\u0013\u0011\u0014!\u00014\u0011\r]\u0019T'\u000f\u001fI\u0013\t!\u0004DA\u0005Gk:\u001cG/[8ogA\u0011agN\u0007\u0002\t%\u0011\u0001\b\u0002\u0002\f)\u0006\u001c8nQ8oi\u0016DH\u000f\u0005\u0002\u0018u%\u00111\b\u0007\u0002\u0004\u0013:$\bcA\u001fFG9\u0011ah\u0011\b\u0003\u007f\tk\u0011\u0001\u0011\u0006\u0003\u0003R\ta\u0001\u0010:p_Rt\u0014\"A\r\n\u0005\u0011C\u0012a\u00029bG.\fw-Z\u0005\u0003\r\u001e\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0006\u0003\tb\u00012!P#\u0011\u0011!Q\u0005A!A!\u0002\u0013Y\u0015!\u00069sKN,'O^3t!\u0006\u0014H/\u001b;j_:Lgn\u001a\t\u0003/1K!!\u0014\r\u0003\u000f\t{w\u000e\\3b]\"Aq\n\u0001B\u0002B\u0003-\u0001+\u0001\u0006fm&$WM\\2fIE\u00022!\u0015+\u0011\u001b\u0005\u0011&BA*\u0019\u0003\u001d\u0011XM\u001a7fGRL!!\u0016*\u0003\u0011\rc\u0017m]:UC\u001eD\u0001b\u0016\u0001\u0003\u0004\u0003\u0006Y\u0001W\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004cA)UG!)!\f\u0001C\u00017\u00061A(\u001b8jiz\"B\u0001\u00181bER\u0019QLX0\u0011\t5\u0001\u0001c\t\u0005\u0006\u001ff\u0003\u001d\u0001\u0015\u0005\u0006/f\u0003\u001d\u0001\u0017\u0005\u0006Ae\u0003\rA\t\u0005\u0006ce\u0003\rA\r\u0005\b\u0015f\u0003\n\u00111\u0001L\u0011\u001d!\u0007A1A\u0005B\u0015\f1\u0002]1si&$\u0018n\u001c8feV\ta\rE\u0002\u0018O&L!\u0001\u001b\r\u0003\r=\u0003H/[8o!\t1$.\u0003\u0002l\t\tY\u0001+\u0019:uSRLwN\\3s\u0011\u0019i\u0007\u0001)A\u0005M\u0006a\u0001/\u0019:uSRLwN\\3sA!)q\u000e\u0001C!a\u0006iq-\u001a;QCJ$\u0018\u000e^5p]N,\u0012!\u001d\t\u0004/I$\u0018BA:\u0019\u0005\u0015\t%O]1z!\t1T/\u0003\u0002w\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\u0006q\u0002!\t%_\u0001\bG>l\u0007/\u001e;f)\rA%\u0010 \u0005\u0006w^\u0004\r\u0001^\u0001\u0006gBd\u0017\u000e\u001e\u0005\u0006{^\u0004\r!N\u0001\bG>tG/\u001a=u\u0011\u0019y\b\u0001\"\u0011\u0002\u0002\u0005\t2\r\\3be\u0012+\u0007/\u001a8eK:\u001c\u0017.Z:\u0015\u0003%:!\"!\u0002\u0003\u0003\u0003E\t\u0001BA\u0004\u0003Ai\u0015\r\u001d)beRLG/[8ogJ#E\tE\u0002\u000e\u0003\u00131\u0011\"\u0001\u0002\u0002\u0002#\u0005A!a\u0003\u0014\r\u0005%\u0011QBA\n!\r9\u0012qB\u0005\u0004\u0003#A\"AB!osJ+g\rE\u0002\u0018\u0003+I1!a\u0006\u0019\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011\u001dQ\u0016\u0011\u0002C\u0001\u00037!\"!a\u0002\t\u0015\u0005}\u0011\u0011BI\u0001\n\u0003\t\t#A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HeM\u000b\u0007\u0003G\tI$a\u000f\u0016\u0005\u0005\u0015\"fA&\u0002(-\u0012\u0011\u0011\u0006\t\u0005\u0003W\t)$\u0004\u0002\u0002.)!\u0011qFA\u0019\u0003%)hn\u00195fG.,GMC\u0002\u00024a\t!\"\u00198o_R\fG/[8o\u0013\u0011\t9$!\f\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0002\u0004\u0014\u0003;\u0011\r!\u0006\u0003\u0007K\u0005u!\u0019A\u000b\t\u0015\u0005}\u0012\u0011BA\u0001\n\u0013\t\t%A\u0006sK\u0006$'+Z:pYZ,GCAA\"!\u0011\t)%a\u0014\u000e\u0005\u0005\u001d#\u0002BA%\u0003\u0017\nA\u0001\\1oO*\u0011\u0011QJ\u0001\u0005U\u00064\u0018-\u0003\u0003\u0002R\u0005\u001d#AB(cU\u0016\u001cG\u000f"
)
public class MapPartitionsRDD<U, T> extends RDD<U> {
   private RDD<T> prev;
   private final Function3<TaskContext, Object, Iterator<T>, Iterator<U>> f;
   private final ClassTag<T> evidence$2;
   private final Option<Partitioner> partitioner;

   public static <U, T> boolean $lessinit$greater$default$3() {
      return MapPartitionsRDD$.MODULE$.$lessinit$greater$default$3();
   }

   public RDD<T> prev() {
      return this.prev;
   }

   public void prev_$eq(RDD<T> x$1) {
      this.prev = x$1;
   }

   public Option<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Partition[] getPartitions() {
      return this.firstParent(this.evidence$2).partitions();
   }

   public Iterator<U> compute(Partition split, TaskContext context) {
      return this.f.apply(context, BoxesRunTime.boxToInteger(split.index()), this.firstParent(this.evidence$2).iterator(split, context));
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.prev_$eq((RDD)null);
   }

   public MapPartitionsRDD(RDD<T> prev, Function3<TaskContext, Object, Iterator<T>, Iterator<U>> f, boolean preservesPartitioning, ClassTag<U> evidence$1, ClassTag<T> evidence$2) {
      this.prev = prev;
      this.f = f;
      this.evidence$2 = evidence$2;
      super(prev, evidence$1);
      this.partitioner = (Option<Partitioner>)(preservesPartitioning ? this.firstParent(evidence$2).partitioner() : None$.MODULE$);
   }
}
