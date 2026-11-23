package org.apache.spark.api.java;

import org.apache.spark.rdd.RDD;
import scala.Serializable;
import scala.reflect.ClassTag;

public final class JavaRDD$ implements Serializable {
   public static final JavaRDD$ MODULE$;

   static {
      new JavaRDD$();
   }

   public <T> JavaRDD<T> fromRDD(RDD<T> rdd, ClassTag<T> evidence$1) {
      return new JavaRDD<T>(rdd, evidence$1);
   }

   public <T> RDD<T> toRDD(JavaRDD<T> rdd) {
      return rdd.rdd();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JavaRDD$() {
      MODULE$ = this;
   }
}
