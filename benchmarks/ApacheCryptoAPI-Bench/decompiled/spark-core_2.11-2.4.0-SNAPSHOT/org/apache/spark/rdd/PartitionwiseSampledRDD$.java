package org.apache.spark.rdd;

import org.apache.spark.util.Utils$;
import scala.Serializable;

public final class PartitionwiseSampledRDD$ implements Serializable {
   public static final PartitionwiseSampledRDD$ MODULE$;

   static {
      new PartitionwiseSampledRDD$();
   }

   public <T, U> long $lessinit$greater$default$4() {
      return Utils$.MODULE$.random().nextLong();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private PartitionwiseSampledRDD$() {
      MODULE$ = this;
   }
}
