package org.apache.spark.rdd;

import scala.Serializable;

public final class MapPartitionsRDD$ implements Serializable {
   public static final MapPartitionsRDD$ MODULE$;

   static {
      new MapPartitionsRDD$();
   }

   public <U, T> boolean $lessinit$greater$default$3() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private MapPartitionsRDD$() {
      MODULE$ = this;
   }
}
