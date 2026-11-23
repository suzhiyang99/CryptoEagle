package org.apache.spark.rdd;

import scala.Serializable;

public final class ZippedPartitionsRDD2$ implements Serializable {
   public static final ZippedPartitionsRDD2$ MODULE$;

   static {
      new ZippedPartitionsRDD2$();
   }

   public <A, B, V> boolean $lessinit$greater$default$5() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ZippedPartitionsRDD2$() {
      MODULE$ = this;
   }
}
