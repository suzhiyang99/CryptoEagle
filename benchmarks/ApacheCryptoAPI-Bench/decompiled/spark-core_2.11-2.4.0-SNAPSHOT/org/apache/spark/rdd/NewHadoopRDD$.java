package org.apache.spark.rdd;

import scala.Serializable;

public final class NewHadoopRDD$ implements Serializable {
   public static final NewHadoopRDD$ MODULE$;
   private final Object CONFIGURATION_INSTANTIATION_LOCK;

   static {
      new NewHadoopRDD$();
   }

   public Object CONFIGURATION_INSTANTIATION_LOCK() {
      return this.CONFIGURATION_INSTANTIATION_LOCK;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private NewHadoopRDD$() {
      MODULE$ = this;
      this.CONFIGURATION_INSTANTIATION_LOCK = new Object();
   }
}
