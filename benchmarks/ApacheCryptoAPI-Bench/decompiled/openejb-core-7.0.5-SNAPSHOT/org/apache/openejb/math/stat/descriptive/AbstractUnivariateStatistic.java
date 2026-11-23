package org.apache.openejb.math.stat.descriptive;

import org.apache.openejb.math.MathRuntimeException;

public abstract class AbstractUnivariateStatistic implements UnivariateStatistic {
   public double evaluate(double[] values) {
      this.test(values, 0, 0);
      return this.evaluate(values, 0, values.length);
   }

   public abstract double evaluate(double[] var1, int var2, int var3);

   public abstract UnivariateStatistic copy();

   protected boolean test(double[] values, int begin, int length) {
      if (values == null) {
         throw MathRuntimeException.createIllegalArgumentException("input values array is null");
      } else if (begin < 0) {
         throw MathRuntimeException.createIllegalArgumentException("start position cannot be negative ({0})", begin);
      } else if (length < 0) {
         throw MathRuntimeException.createIllegalArgumentException("length cannot be negative ({0})", length);
      } else if (begin + length > values.length) {
         throw MathRuntimeException.createIllegalArgumentException("subarray ends after array end");
      } else {
         return length != 0;
      }
   }

   protected boolean test(double[] values, double[] weights, int begin, int length) {
      if (weights == null) {
         throw MathRuntimeException.createIllegalArgumentException("input weights array is null");
      } else if (weights.length != values.length) {
         throw MathRuntimeException.createIllegalArgumentException("Different number of weights and values");
      } else {
         boolean containsPositiveWeight = false;

         for(int i = begin; i < begin + length; ++i) {
            if (Double.isNaN(weights[i])) {
               throw MathRuntimeException.createIllegalArgumentException("NaN weight at index {0}", i);
            }

            if (Double.isInfinite(weights[i])) {
               throw MathRuntimeException.createIllegalArgumentException("Infinite weight at index {0}", i);
            }

            if (weights[i] < (double)0.0F) {
               throw MathRuntimeException.createIllegalArgumentException("negative weight {0} at index {1} ", weights[i], i);
            }

            if (!containsPositiveWeight && weights[i] > (double)0.0F) {
               containsPositiveWeight = true;
            }
         }

         if (!containsPositiveWeight) {
            throw MathRuntimeException.createIllegalArgumentException("weight array must contain at least one non-zero value");
         } else {
            return this.test(values, begin, length);
         }
      }
   }
}
