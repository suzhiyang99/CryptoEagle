package org.apache.spark.shuffle.sort;

import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkConf;
import scala.Predef$;
import scala.Serializable;

public final class SortShuffleWriter$ {
   public static final SortShuffleWriter$ MODULE$;

   static {
      new SortShuffleWriter$();
   }

   public boolean shouldBypassMergeSort(SparkConf conf, ShuffleDependency<?, ?, ?> dep) {
      boolean var10000;
      if (dep.mapSideCombine()) {
         Predef$.MODULE$.require(dep.aggregator().isDefined(), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Map-side combine without Aggregator specified!";
            }
         });
         var10000 = false;
      } else {
         int bypassMergeThreshold = conf.getInt("spark.shuffle.sort.bypassMergeThreshold", 200);
         var10000 = dep.partitioner().numPartitions() <= bypassMergeThreshold;
      }

      return var10000;
   }

   private SortShuffleWriter$() {
      MODULE$ = this;
   }
}
