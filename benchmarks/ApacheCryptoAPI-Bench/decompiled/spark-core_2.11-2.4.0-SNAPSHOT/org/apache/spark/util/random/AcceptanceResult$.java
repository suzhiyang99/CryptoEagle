package org.apache.spark.util.random;

import scala.Serializable;

public final class AcceptanceResult$ implements Serializable {
   public static final AcceptanceResult$ MODULE$;

   static {
      new AcceptanceResult$();
   }

   public long $lessinit$greater$default$1() {
      return 0L;
   }

   public long $lessinit$greater$default$2() {
      return 0L;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AcceptanceResult$() {
      MODULE$ = this;
   }
}
