package org.apache.spark.util.random;

import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.util.Random$;

public final class SamplingUtils$ {
   public static final SamplingUtils$ MODULE$;

   static {
      new SamplingUtils$();
   }

   public <T> Tuple2<Object, Object> reservoirSampleAndCount(Iterator<T> input, int k, long seed, ClassTag<T> evidence$1) {
      Object reservoir = evidence$1.newArray(k);

      int i;
      for(i = 0; i < k && input.hasNext(); ++i) {
         Object item = input.next();
         ScalaRunTime$.MODULE$.array_update(reservoir, i, item);
      }

      Tuple2 var10000;
      if (i < k) {
         Object trimReservoir = evidence$1.newArray(i);
         System.arraycopy(reservoir, 0, trimReservoir, 0, i);
         var10000 = new Tuple2(trimReservoir, BoxesRunTime.boxToLong((long)i));
      } else {
         long l = (long)i;
         XORShiftRandom rand = new XORShiftRandom(seed);

         while(input.hasNext()) {
            Object item = input.next();
            ++l;
            long replacementIndex = (long)(rand.nextDouble() * (double)l);
            if (replacementIndex < (long)k) {
               ScalaRunTime$.MODULE$.array_update(reservoir, (int)replacementIndex, item);
            }
         }

         var10000 = new Tuple2(reservoir, BoxesRunTime.boxToLong(l));
      }

      return var10000;
   }

   public <T> long reservoirSampleAndCount$default$3() {
      return Random$.MODULE$.nextLong();
   }

   public double computeFractionForSampleSize(int sampleSizeLowerBound, long total, boolean withReplacement) {
      double var10000;
      if (withReplacement) {
         var10000 = PoissonBounds$.MODULE$.getUpperBound((double)sampleSizeLowerBound) / (double)total;
      } else {
         double fraction = (double)sampleSizeLowerBound / (double)total;
         var10000 = BinomialBounds$.MODULE$.getUpperBound(1.0E-4, total, fraction);
      }

      return var10000;
   }

   private SamplingUtils$() {
      MODULE$ = this;
   }
}
