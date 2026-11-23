package org.apache.spark.util.random;

import scala.Serializable;

public final class BernoulliCellSampler$ implements Serializable {
   public static final BernoulliCellSampler$ MODULE$;

   static {
      new BernoulliCellSampler$();
   }

   public <T> boolean $lessinit$greater$default$3() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private BernoulliCellSampler$() {
      MODULE$ = this;
   }
}
