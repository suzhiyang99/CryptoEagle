package org.apache.spark.rdd;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Serializable;

@DeveloperApi
public final class PartitionPruningRDD$ implements Serializable {
   public static final PartitionPruningRDD$ MODULE$;

   static {
      new PartitionPruningRDD$();
   }

   public <T> PartitionPruningRDD<T> create(RDD<T> rdd, Function1<Object, Object> partitionFilterFunc) {
      return new PartitionPruningRDD<T>(rdd, partitionFilterFunc, rdd.elementClassTag());
   }

   private Object readResolve() {
      return MODULE$;
   }

   private PartitionPruningRDD$() {
      MODULE$ = this;
   }
}
