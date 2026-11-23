package org.apache.spark.shuffle;

import scala.Serializable;

public final class FetchFailedException$ implements Serializable {
   public static final FetchFailedException$ MODULE$;

   static {
      new FetchFailedException$();
   }

   public Throwable $lessinit$greater$default$6() {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private FetchFailedException$() {
      MODULE$ = this;
   }
}
