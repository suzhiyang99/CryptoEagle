package org.apache.spark.util.collection;

import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.math.Ordering;

public final class Utils$ {
   public static final Utils$ MODULE$;

   static {
      new Utils$();
   }

   public <T> Iterator<T> takeOrdered(Iterator<T> input, int num, Ordering<T> ord) {
      org.spark_project.guava.collect.Ordering ordering = new org.spark_project.guava.collect.Ordering<T>(ord) {
         private final Ordering ord$1;

         public int compare(T l, T r) {
            return this.ord$1.compare(l, r);
         }

         public {
            this.ord$1 = ord$1;
         }
      };
      return (Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(ordering.leastOf((java.util.Iterator)JavaConverters$.MODULE$.asJavaIteratorConverter(input).asJava(), num).iterator()).asScala();
   }

   private Utils$() {
      MODULE$ = this;
   }
}
