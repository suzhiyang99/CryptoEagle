package org.apache.spark.rdd;

import scala.Serializable;

public final class ZippedPartitionsBaseRDD$ implements Serializable {
   public static final ZippedPartitionsBaseRDD$ MODULE$;

   static {
      new ZippedPartitionsBaseRDD$();
   }

   public <V> boolean $lessinit$greater$default$3() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ZippedPartitionsBaseRDD$() {
      MODULE$ = this;
   }
}
