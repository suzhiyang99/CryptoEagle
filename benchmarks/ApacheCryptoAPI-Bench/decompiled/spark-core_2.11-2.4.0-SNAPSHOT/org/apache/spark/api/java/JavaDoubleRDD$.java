package org.apache.spark.api.java;

import org.apache.spark.rdd.RDD;
import scala.Serializable;

public final class JavaDoubleRDD$ implements Serializable {
   public static final JavaDoubleRDD$ MODULE$;

   static {
      new JavaDoubleRDD$();
   }

   public JavaDoubleRDD fromRDD(RDD<Object> rdd) {
      return new JavaDoubleRDD(rdd);
   }

   public RDD<Object> toRDD(JavaDoubleRDD rdd) {
      return rdd.srdd();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JavaDoubleRDD$() {
      MODULE$ = this;
   }
}
