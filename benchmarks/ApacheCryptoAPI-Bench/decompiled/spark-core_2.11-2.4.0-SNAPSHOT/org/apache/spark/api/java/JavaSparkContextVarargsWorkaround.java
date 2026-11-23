package org.apache.spark.api.java;

import java.util.ArrayList;
import java.util.List;

abstract class JavaSparkContextVarargsWorkaround {
   @SafeVarargs
   public final <T> JavaRDD<T> union(JavaRDD<T>... rdds) {
      if (rdds.length == 0) {
         throw new IllegalArgumentException("Union called on empty list");
      } else {
         List<JavaRDD<T>> rest = new ArrayList(rdds.length - 1);

         for(int i = 1; i < rdds.length; ++i) {
            rest.add(rdds[i]);
         }

         return this.union(rdds[0], rest);
      }
   }

   public JavaDoubleRDD union(JavaDoubleRDD... rdds) {
      if (rdds.length == 0) {
         throw new IllegalArgumentException("Union called on empty list");
      } else {
         List<JavaDoubleRDD> rest = new ArrayList(rdds.length - 1);

         for(int i = 1; i < rdds.length; ++i) {
            rest.add(rdds[i]);
         }

         return this.union(rdds[0], rest);
      }
   }

   @SafeVarargs
   public final <K, V> JavaPairRDD<K, V> union(JavaPairRDD<K, V>... rdds) {
      if (rdds.length == 0) {
         throw new IllegalArgumentException("Union called on empty list");
      } else {
         List<JavaPairRDD<K, V>> rest = new ArrayList(rdds.length - 1);

         for(int i = 1; i < rdds.length; ++i) {
            rest.add(rdds[i]);
         }

         return this.union(rdds[0], rest);
      }
   }

   public abstract <T> JavaRDD<T> union(JavaRDD<T> var1, List<JavaRDD<T>> var2);

   public abstract JavaDoubleRDD union(JavaDoubleRDD var1, List<JavaDoubleRDD> var2);

   public abstract <K, V> JavaPairRDD<K, V> union(JavaPairRDD<K, V> var1, List<JavaPairRDD<K, V>> var2);
}
