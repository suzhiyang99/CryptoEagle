package org.apache.spark.util.collection;

import org.apache.spark.unsafe.array.ByteArrayMethods;

public final class PartitionedPairBuffer$ {
   public static final PartitionedPairBuffer$ MODULE$;
   private final int MAXIMUM_CAPACITY;

   static {
      new PartitionedPairBuffer$();
   }

   public int MAXIMUM_CAPACITY() {
      return this.MAXIMUM_CAPACITY;
   }

   public <K, V> int $lessinit$greater$default$1() {
      return 64;
   }

   private PartitionedPairBuffer$() {
      MODULE$ = this;
      this.MAXIMUM_CAPACITY = ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH / 2;
   }
}
