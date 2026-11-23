package org.apache.spark.util.collection;

import scala.Serializable;
import scala.reflect.ClassTag;

public final class CompactBuffer$ implements Serializable {
   public static final CompactBuffer$ MODULE$;

   static {
      new CompactBuffer$();
   }

   public <T> CompactBuffer<T> apply(ClassTag<T> evidence$2) {
      return new CompactBuffer<T>(evidence$2);
   }

   public <T> CompactBuffer<T> apply(T value, ClassTag<T> evidence$3) {
      CompactBuffer buf = new CompactBuffer(evidence$3);
      return buf.$plus$eq(value);
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CompactBuffer$() {
      MODULE$ = this;
   }
}
