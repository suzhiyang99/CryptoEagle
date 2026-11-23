package org.apache.spark.rdd;

import scala.Serializable;
import scala.Tuple2;
import scala.runtime.Null$;

public final class PairRDDFunctions$ implements Serializable {
   public static final PairRDDFunctions$ MODULE$;

   static {
      new PairRDDFunctions$();
   }

   public <K, V> Null$ $lessinit$greater$default$4(RDD<Tuple2<K, V>> self) {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private PairRDDFunctions$() {
      MODULE$ = this;
   }
}
