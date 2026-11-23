package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.TaskContext;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001A4Q!\u0001\u0002\u0001\t)\u0011!CW5qa\u0016$w+\u001b;i\u0013:$W\r\u001f*E\t*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0016\u0005-A2C\u0001\u0001\r!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\u0004%\u0012#\u0005\u0003B\t\u0015-\tj\u0011A\u0005\u0006\u0002'\u0005)1oY1mC&\u0011QC\u0005\u0002\u0007)V\u0004H.\u001a\u001a\u0011\u0005]AB\u0002\u0001\u0003\u00063\u0001\u0011\ra\u0007\u0002\u0002)\u000e\u0001\u0011C\u0001\u000f !\t\tR$\u0003\u0002\u001f%\t9aj\u001c;iS:<\u0007CA\t!\u0013\t\t#CA\u0002B]f\u0004\"!E\u0012\n\u0005\u0011\u0012\"\u0001\u0002'p]\u001eD\u0001B\n\u0001\u0003\u0002\u0003\u0006IaJ\u0001\u0005aJ,g\u000fE\u0002\u000e\u001dYA\u0001\"\u000b\u0001\u0003\u0004\u0003\u0006YAK\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004cA\u0016/-5\tAF\u0003\u0002.%\u00059!/\u001a4mK\u000e$\u0018BA\u0018-\u0005!\u0019E.Y:t)\u0006<\u0007\"B\u0019\u0001\t\u0003\u0011\u0014A\u0002\u001fj]&$h\b\u0006\u00024mQ\u0011A'\u000e\t\u0004\u001b\u00011\u0002\"B\u00151\u0001\bQ\u0003\"\u0002\u00141\u0001\u00049\u0003b\u0002\u001d\u0001\u0005\u0004%I!O\u0001\rgR\f'\u000f^%oI&\u001cWm]\u000b\u0002uA\u0019\u0011c\u000f\u0012\n\u0005q\u0012\"!B!se\u0006L\bB\u0002 \u0001A\u0003%!(A\u0007ti\u0006\u0014H/\u00138eS\u000e,7\u000f\t\u0015\u0003{\u0001\u0003\"!E!\n\u0005\t\u0013\"!\u0003;sC:\u001c\u0018.\u001a8u\u0011\u0015!\u0005\u0001\"\u0011F\u000359W\r\u001e)beRLG/[8ogV\ta\tE\u0002\u0012w\u001d\u0003\"\u0001S%\u000e\u0003\u0011I!A\u0013\u0003\u0003\u0013A\u000b'\u000f^5uS>t\u0007\"\u0002'\u0001\t\u0003j\u0015!F4fiB\u0013XMZ3se\u0016$Gj\\2bi&|gn\u001d\u000b\u0003\u001d\u0006\u00042aT,[\u001d\t\u0001VK\u0004\u0002R)6\t!K\u0003\u0002T5\u00051AH]8pizJ\u0011aE\u0005\u0003-J\tq\u0001]1dW\u0006<W-\u0003\u0002Y3\n\u00191+Z9\u000b\u0005Y\u0013\u0002CA._\u001d\t\tB,\u0003\u0002^%\u00051\u0001K]3eK\u001aL!a\u00181\u0003\rM#(/\u001b8h\u0015\ti&\u0003C\u0003c\u0017\u0002\u0007q)A\u0003ta2LG\u000fC\u0003e\u0001\u0011\u0005S-A\u0004d_6\u0004X\u000f^3\u0015\u0007\u0019L7\u000eE\u0002POBI!\u0001[-\u0003\u0011%#XM]1u_JDQA[2A\u0002\u001d\u000bqa\u001d9mSRLe\u000eC\u0003mG\u0002\u0007Q.A\u0004d_:$X\r\u001f;\u0011\u0005!s\u0017BA8\u0005\u0005-!\u0016m]6D_:$X\r\u001f;"
)
public class ZippedWithIndexRDD<T> extends RDD<Tuple2<T, Object>> {
   private final ClassTag<T> evidence$1;
   private final transient long[] org$apache$spark$rdd$ZippedWithIndexRDD$$startIndices;

   public long[] org$apache$spark$rdd$ZippedWithIndexRDD$$startIndices() {
      return this.org$apache$spark$rdd$ZippedWithIndexRDD$$startIndices;
   }

   public Partition[] getPartitions() {
      return (Partition[])Predef$.MODULE$.refArrayOps(this.firstParent(this.evidence$1).partitions()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ZippedWithIndexRDD $outer;

         public final ZippedWithIndexRDDPartition apply(Partition x) {
            return new ZippedWithIndexRDDPartition(x, this.$outer.org$apache$spark$rdd$ZippedWithIndexRDD$$startIndices()[x.index()]);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Partition.class)));
   }

   public Seq<String> getPreferredLocations(Partition split) {
      return this.firstParent(this.evidence$1).preferredLocations(((ZippedWithIndexRDDPartition)split).prev());
   }

   public Iterator<Tuple2<T, Object>> compute(Partition splitIn, TaskContext context) {
      ZippedWithIndexRDDPartition split = (ZippedWithIndexRDDPartition)splitIn;
      Iterator parentIter = this.firstParent(this.evidence$1).iterator(split.prev(), context);
      return Utils$.MODULE$.getIteratorZipWithIndex(parentIter, split.startIndex());
   }

   public ZippedWithIndexRDD(RDD<T> prev, ClassTag<T> evidence$1) {
      super(prev, ClassTag$.MODULE$.apply(Tuple2.class));
      this.evidence$1 = evidence$1;
      int n = prev.partitions().length;
      this.org$apache$spark$rdd$ZippedWithIndexRDD$$startIndices = n == 0 ? (long[])Array$.MODULE$.empty(ClassTag$.MODULE$.Long()) : (n == 1 ? new long[]{0L} : (long[])Predef$.MODULE$.longArrayOps((long[])prev.context().runJob(prev, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(Iterator<Object> iterator) {
            return Utils$.MODULE$.getIteratorSize(iterator);
         }
      }, RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), n - 1), ClassTag$.MODULE$.Long())).scanLeft(BoxesRunTime.boxToLong(0L), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(long x$1, long x$2) {
            return this.apply$mcJJJ$sp(x$1, x$2);
         }

         public long apply$mcJJJ$sp(long x$1, long x$2) {
            return x$1 + x$2;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long())));
   }
}
