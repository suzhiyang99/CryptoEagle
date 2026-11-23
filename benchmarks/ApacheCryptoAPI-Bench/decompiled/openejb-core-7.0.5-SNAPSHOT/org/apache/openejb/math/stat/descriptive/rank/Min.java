package org.apache.openejb.math.stat.descriptive.rank;

import java.io.Serializable;
import org.apache.openejb.math.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Min extends AbstractStorelessUnivariateStatistic implements Serializable {
   private static final long serialVersionUID = -1231995784909003131L;
   private long n;
   private double value;

   public Min() {
      this.n = 0L;
      this.value = Double.NaN;
   }

   public Min(Min original) {
      copy(original, this);
   }

   public void increment(double d) {
      if (d < this.value || Double.isNaN(this.value)) {
         this.value = d;
      }

      ++this.n;
   }

   public void clear() {
      this.value = Double.NaN;
      this.n = 0L;
   }

   public double getResult() {
      return this.value;
   }

   public long getN() {
      return this.n;
   }

   public double evaluate(double[] values, int begin, int length) {
      double min = Double.NaN;
      if (this.test(values, begin, length)) {
         min = values[begin];

         for(int i = begin; i < begin + length; ++i) {
            if (!Double.isNaN(values[i])) {
               min = min < values[i] ? min : values[i];
            }
         }
      }

      return min;
   }

   public Min copy() {
      Min result = new Min();
      copy(this, result);
      return result;
   }

   public static void copy(Min source, Min dest) {
      dest.n = source.n;
      dest.value = source.value;
   }
}
