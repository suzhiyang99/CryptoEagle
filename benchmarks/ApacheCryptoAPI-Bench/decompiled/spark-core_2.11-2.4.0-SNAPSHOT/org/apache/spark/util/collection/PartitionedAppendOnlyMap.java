package org.apache.spark.util.collection;

import java.util.Comparator;
import scala.Option;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001e3Q!\u0001\u0002\u0001\r1\u0011\u0001\u0004U1si&$\u0018n\u001c8fI\u0006\u0003\b/\u001a8e\u001f:d\u00170T1q\u0015\t\u0019A!\u0001\u0006d_2dWm\u0019;j_:T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOV\u0019Q\"\b\u0015\u0014\u0007\u0001q!\u0006\u0005\u0003\u0010!I9S\"\u0001\u0002\n\u0005E\u0011!!G*ju\u0016$&/Y2lS:<\u0017\t\u001d9f]\u0012|e\u000e\\=NCB\u0004Ba\u0005\f\u001975\tACC\u0001\u0016\u0003\u0015\u00198-\u00197b\u0013\t9BC\u0001\u0004UkBdWM\r\t\u0003'eI!A\u0007\u000b\u0003\u0007%sG\u000f\u0005\u0002\u001d;1\u0001A!\u0002\u0010\u0001\u0005\u0004\u0001#!A&\u0004\u0001E\u0011\u0011\u0005\n\t\u0003'\tJ!a\t\u000b\u0003\u000f9{G\u000f[5oOB\u00111#J\u0005\u0003MQ\u00111!\u00118z!\ta\u0002\u0006B\u0003*\u0001\t\u0007\u0001EA\u0001W!\u0011y1fG\u0014\n\u00051\u0012!!I,sSR\f'\r\\3QCJ$\u0018\u000e^5p]\u0016$\u0007+Y5s\u0007>dG.Z2uS>t\u0007\"\u0002\u0018\u0001\t\u0003y\u0013A\u0002\u001fj]&$h\bF\u00011!\u0011y\u0001aG\u0014\t\u000bI\u0002A\u0011A\u001a\u0002IA\f'\u000f^5uS>tW\r\u001a#fgR\u0014Xo\u0019;jm\u0016\u001cvN\u001d;fI&#XM]1u_J$\"\u0001N!\u0011\u0007Uj\u0004I\u0004\u00027w9\u0011qGO\u0007\u0002q)\u0011\u0011hH\u0001\u0007yI|w\u000e\u001e \n\u0003UI!\u0001\u0010\u000b\u0002\u000fA\f7m[1hK&\u0011ah\u0010\u0002\t\u0013R,'/\u0019;pe*\u0011A\b\u0006\t\u0005'Y\u0011r\u0005C\u0003Cc\u0001\u00071)A\u0007lKf\u001cu.\u001c9be\u0006$xN\u001d\t\u0004'\u00113\u0015BA#\u0015\u0005\u0019y\u0005\u000f^5p]B\u0019qiS\u000e\u000e\u0003!S!!B%\u000b\u0003)\u000bAA[1wC&\u0011A\n\u0013\u0002\u000b\u0007>l\u0007/\u0019:bi>\u0014\b\"\u0002(\u0001\t\u0003y\u0015AB5og\u0016\u0014H\u000f\u0006\u0003Q'V;\u0006CA\nR\u0013\t\u0011FC\u0001\u0003V]&$\b\"\u0002+N\u0001\u0004A\u0012!\u00039beRLG/[8o\u0011\u00151V\n1\u0001\u001c\u0003\rYW-\u001f\u0005\u000616\u0003\raJ\u0001\u0006m\u0006dW/\u001a"
)
public class PartitionedAppendOnlyMap<K, V> extends SizeTrackingAppendOnlyMap<Tuple2<Object, K>, V> implements WritablePartitionedPairCollection<K, V> {
   public WritablePartitionedIterator destructiveSortedWritablePartitionedIterator(Option<Comparator<K>> keyComparator) {
      return WritablePartitionedPairCollection$class.destructiveSortedWritablePartitionedIterator(this, keyComparator);
   }

   public Iterator<Tuple2<Tuple2<Object, K>, V>> partitionedDestructiveSortedIterator(Option<Comparator<K>> keyComparator) {
      Comparator comparator = (Comparator)keyComparator.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Comparator<Tuple2<Object, K>> apply(Comparator<K> keyComparator) {
            return WritablePartitionedPairCollection$.MODULE$.partitionKeyComparator(keyComparator);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Comparator<Tuple2<Object, K>> apply() {
            return WritablePartitionedPairCollection$.MODULE$.partitionComparator();
         }
      });
      return this.destructiveSortedIterator(comparator);
   }

   public void insert(int partition, K key, V value) {
      this.update(new Tuple2(BoxesRunTime.boxToInteger(partition), key), value);
   }

   public PartitionedAppendOnlyMap() {
      WritablePartitionedPairCollection$class.$init$(this);
   }
}
