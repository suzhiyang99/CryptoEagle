package org.apache.spark.partial;

import org.apache.commons.math3.distribution.PoissonDistribution;

public final class CountEvaluator$ {
   public static final CountEvaluator$ MODULE$;

   static {
      new CountEvaluator$();
   }

   public BoundedDouble bound(double confidence, long sum, double p) {
      PoissonDistribution dist = new PoissonDistribution((double)sum * ((double)1 - p) / p);
      int low = dist.inverseCumulativeProbability(((double)1 - confidence) / (double)2);
      int high = dist.inverseCumulativeProbability(((double)1 + confidence) / (double)2);
      return new BoundedDouble((double)sum + dist.getNumericalMean(), confidence, (double)(sum + (long)low), (double)(sum + (long)high));
   }

   private CountEvaluator$() {
      MODULE$ = this;
   }
}
