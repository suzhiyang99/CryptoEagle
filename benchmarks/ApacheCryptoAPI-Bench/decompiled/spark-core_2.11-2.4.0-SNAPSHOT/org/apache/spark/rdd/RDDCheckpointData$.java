package org.apache.spark.rdd;

import scala.Serializable;

public final class RDDCheckpointData$ implements Serializable {
   public static final RDDCheckpointData$ MODULE$;

   static {
      new RDDCheckpointData$();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RDDCheckpointData$() {
      MODULE$ = this;
   }
}
