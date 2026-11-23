package org.apache.spark.rdd;

import scala.None$;
import scala.Option;
import scala.Serializable;

public final class CoalescedRDD$ implements Serializable {
   public static final CoalescedRDD$ MODULE$;

   static {
      new CoalescedRDD$();
   }

   public <T> Option<PartitionCoalescer> $lessinit$greater$default$3() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CoalescedRDD$() {
      MODULE$ = this;
   }
}
