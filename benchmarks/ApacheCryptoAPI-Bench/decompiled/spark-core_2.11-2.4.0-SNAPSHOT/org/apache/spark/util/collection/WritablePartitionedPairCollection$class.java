package org.apache.spark.util.collection;

import org.apache.spark.storage.DiskBlockObjectWriter;
import scala.Option;
import scala.Tuple2;
import scala.collection.Iterator;

public abstract class WritablePartitionedPairCollection$class {
   public static WritablePartitionedIterator destructiveSortedWritablePartitionedIterator(WritablePartitionedPairCollection $this, Option keyComparator) {
      Iterator it = $this.partitionedDestructiveSortedIterator(keyComparator);
      return new WritablePartitionedIterator($this, it) {
         private Tuple2<Tuple2<Object, K>, V> cur;
         private final Iterator it$1;

         public void writeNext(DiskBlockObjectWriter writer) {
            writer.write((this.cur._1())._2(), this.cur._2());
            this.cur = this.it$1.hasNext() ? (Tuple2)this.it$1.next() : null;
         }

         public boolean hasNext() {
            return this.cur != null;
         }

         public int nextPartition() {
            return ((Tuple2)this.cur._1())._1$mcI$sp();
         }

         public {
            this.it$1 = it$1;
            this.cur = it$1.hasNext() ? (Tuple2)it$1.next() : null;
         }
      };
   }

   public static void $init$(WritablePartitionedPairCollection $this) {
   }
}
