package org.apache.spark.rdd;

import scala.Serializable;
import scala.collection.parallel.ForkJoinTaskSupport;
import scala.concurrent.forkjoin.ForkJoinPool;
import scala.runtime.BoxedUnit;

public final class UnionRDD$ implements Serializable {
   public static final UnionRDD$ MODULE$;
   private ForkJoinTaskSupport partitionEvalTaskSupport;
   private volatile boolean bitmap$0;

   static {
      new UnionRDD$();
   }

   private ForkJoinTaskSupport partitionEvalTaskSupport$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.partitionEvalTaskSupport = new ForkJoinTaskSupport(new ForkJoinPool(8));
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.partitionEvalTaskSupport;
   }

   public ForkJoinTaskSupport partitionEvalTaskSupport() {
      return this.bitmap$0 ? this.partitionEvalTaskSupport : this.partitionEvalTaskSupport$lzycompute();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private UnionRDD$() {
      MODULE$ = this;
   }
}
