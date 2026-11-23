package org.apache.spark;

import scala.Serializable;

/** @deprecated */
public final class AccumulatorParam$ implements Serializable {
   public static final AccumulatorParam$ MODULE$;

   static {
      new AccumulatorParam$();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AccumulatorParam$() {
      MODULE$ = this;
   }
}
