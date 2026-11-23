package org.apache.spark.api.python;

import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaPairRDD$;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.Utils$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u00014A!\u0001\u0002\u0005\u001b\tY\u0001+Y5so&\u001cXM\u0015#E\u0015\t\u0019A!\u0001\u0004qsRDwN\u001c\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\n\u0003\u00019\u00012a\u0004\n\u0015\u001b\u0005\u0001\"BA\t\u0007\u0003\r\u0011H\rZ\u0005\u0003'A\u00111A\u0015#E!\u0011)\u0002DG\u000f\u000e\u0003YQ\u0011aF\u0001\u0006g\u000e\fG.Y\u0005\u00033Y\u0011a\u0001V;qY\u0016\u0014\u0004CA\u000b\u001c\u0013\tabC\u0001\u0003M_:<\u0007cA\u000b\u001fA%\u0011qD\u0006\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003+\u0005J!A\t\f\u0003\t\tKH/\u001a\u0005\tI\u0001\u0011\t\u0011)A\u0005K\u0005!\u0001O]3w!\ry!#\b\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005%Z\u0003C\u0001\u0016\u0001\u001b\u0005\u0011\u0001\"\u0002\u0013'\u0001\u0004)\u0003\"B\u0017\u0001\t\u0003r\u0013!D4fiB\u000b'\u000f^5uS>t7/F\u00010!\r)b\u0004\r\t\u0003cIj\u0011AB\u0005\u0003g\u0019\u0011\u0011\u0002U1si&$\u0018n\u001c8\t\u000fU\u0002!\u0019!C!m\u0005Y\u0001/\u0019:uSRLwN\\3s+\u00059\u0004cA\u000b9u%\u0011\u0011H\u0006\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005EZ\u0014B\u0001\u001f\u0007\u0005-\u0001\u0016M\u001d;ji&|g.\u001a:\t\ry\u0002\u0001\u0015!\u00038\u00031\u0001\u0018M\u001d;ji&|g.\u001a:!\u0011\u0015\u0001\u0005\u0001\"\u0011B\u0003\u001d\u0019w.\u001c9vi\u0016$2A\u0011(Q!\r\u00195\n\u0006\b\u0003\t&s!!\u0012%\u000e\u0003\u0019S!a\u0012\u0007\u0002\rq\u0012xn\u001c;?\u0013\u00059\u0012B\u0001&\u0017\u0003\u001d\u0001\u0018mY6bO\u0016L!\u0001T'\u0003\u0011%#XM]1u_JT!A\u0013\f\t\u000b={\u0004\u0019\u0001\u0019\u0002\u000bM\u0004H.\u001b;\t\u000bE{\u0004\u0019\u0001*\u0002\u000f\r|g\u000e^3yiB\u0011\u0011gU\u0005\u0003)\u001a\u00111\u0002V1tW\u000e{g\u000e^3yi\"9a\u000b\u0001b\u0001\n\u00039\u0016!D1t\u0015\u00064\u0018\rU1jeJ#E)F\u0001Y!\u0011IFLG\u000f\u000e\u0003iS!a\u0017\u0003\u0002\t)\fg/Y\u0005\u0003;j\u00131BS1wCB\u000b\u0017N\u001d*E\t\"1q\f\u0001Q\u0001\na\u000ba\"Y:KCZ\f\u0007+Y5s%\u0012#\u0005\u0005"
)
public class PairwiseRDD extends RDD<Tuple2<Object, byte[]>> {
   private final RDD<byte[]> prev;
   private final Option<Partitioner> partitioner;
   private final JavaPairRDD<Object, byte[]> asJavaPairRDD;

   public Partition[] getPartitions() {
      return this.prev.partitions();
   }

   public Option<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Iterator<Tuple2<Object, byte[]>> compute(Partition split, TaskContext context) {
      return this.prev.iterator(split, context).grouped(2).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Object, byte[]> apply(Seq<byte[]> x0$1) {
            Some var3 = Seq$.MODULE$.unapplySeq(x0$1);
            if (!var3.isEmpty() && var3.get() != null && ((SeqLike)var3.get()).lengthCompare(2) == 0) {
               byte[] a = (byte[])((SeqLike)var3.get()).apply(0);
               byte[] b = (byte[])((SeqLike)var3.get()).apply(1);
               Tuple2 var6 = new Tuple2(BoxesRunTime.boxToLong(Utils$.MODULE$.deserializeLongValue(a)), b);
               return var6;
            } else {
               throw new SparkException((new StringBuilder()).append((Object)"PairwiseRDD: unexpected value: ").append((Object)x0$1).toString());
            }
         }
      });
   }

   public JavaPairRDD<Object, byte[]> asJavaPairRDD() {
      return this.asJavaPairRDD;
   }

   public PairwiseRDD(RDD<byte[]> prev) {
      super(prev, ClassTag$.MODULE$.apply(Tuple2.class));
      this.prev = prev;
      this.partitioner = prev.partitioner();
      this.asJavaPairRDD = JavaPairRDD$.MODULE$.<Object, byte[]>fromRDD(this, ClassTag$.MODULE$.Long(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }
}
