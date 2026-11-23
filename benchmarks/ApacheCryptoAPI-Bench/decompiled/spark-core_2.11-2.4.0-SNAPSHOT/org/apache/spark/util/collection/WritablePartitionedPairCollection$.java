package org.apache.spark.util.collection;

import java.util.Comparator;
import scala.Tuple2;

public final class WritablePartitionedPairCollection$ {
   public static final WritablePartitionedPairCollection$ MODULE$;

   static {
      new WritablePartitionedPairCollection$();
   }

   public <K> Comparator<Tuple2<Object, K>> partitionComparator() {
      return new Comparator<Tuple2<Object, K>>() {
         public int compare(Tuple2<Object, K> a, Tuple2<Object, K> b) {
            return a._1$mcI$sp() - b._1$mcI$sp();
         }
      };
   }

   public <K> Comparator<Tuple2<Object, K>> partitionKeyComparator(Comparator<K> keyComparator) {
      return new Comparator<Tuple2<Object, K>>(keyComparator) {
         private final Comparator keyComparator$1;

         public int compare(Tuple2<Object, K> a, Tuple2<Object, K> b) {
            int partitionDiff = a._1$mcI$sp() - b._1$mcI$sp();
            return partitionDiff != 0 ? partitionDiff : this.keyComparator$1.compare(a._2(), b._2());
         }

         public {
            this.keyComparator$1 = keyComparator$1;
         }
      };
   }

   private WritablePartitionedPairCollection$() {
      MODULE$ = this;
   }
}
