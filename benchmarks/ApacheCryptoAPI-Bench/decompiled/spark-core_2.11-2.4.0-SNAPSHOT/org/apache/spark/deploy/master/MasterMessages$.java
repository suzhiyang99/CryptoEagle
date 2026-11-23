package org.apache.spark.deploy.master;

import scala.Serializable;

public final class MasterMessages$ implements Serializable {
   public static final MasterMessages$ MODULE$;

   static {
      new MasterMessages$();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private MasterMessages$() {
      MODULE$ = this;
   }
}
