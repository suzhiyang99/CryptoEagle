package org.apache.spark.rdd;

import scala.Serializable;

public final class ZippedPartitionsRDD4$ implements Serializable {
   public static final ZippedPartitionsRDD4$ MODULE$;

   static {
      new ZippedPartitionsRDD4$();
   }

   public <A, B, C, D, V> boolean $lessinit$greater$default$7() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ZippedPartitionsRDD4$() {
      MODULE$ = this;
   }
}
