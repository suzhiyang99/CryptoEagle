package org.apache.spark.unsafe.map;

import org.apache.spark.unsafe.array.ByteArrayMethods;

public interface HashMapGrowthStrategy {
   HashMapGrowthStrategy DOUBLING = new Doubling();

   int nextCapacity(int var1);

   public static class Doubling implements HashMapGrowthStrategy {
      private static final int ARRAY_MAX;

      public int nextCapacity(int currentCapacity) {
         assert currentCapacity > 0;

         int doubleCapacity = currentCapacity * 2;
         return doubleCapacity > 0 && doubleCapacity <= ARRAY_MAX ? doubleCapacity : ARRAY_MAX;
      }

      static {
         ARRAY_MAX = ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH;
      }
   }
}
