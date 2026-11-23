package org.apache.spark.rdd;

import scala.Serializable;

public final class ZippedPartitionsRDD3$ implements Serializable {
   public static final ZippedPartitionsRDD3$ MODULE$;

   static {
      new ZippedPartitionsRDD3$();
   }

   public <A, B, C, V> boolean $lessinit$greater$default$6() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ZippedPartitionsRDD3$() {
      MODULE$ = this;
   }
}
