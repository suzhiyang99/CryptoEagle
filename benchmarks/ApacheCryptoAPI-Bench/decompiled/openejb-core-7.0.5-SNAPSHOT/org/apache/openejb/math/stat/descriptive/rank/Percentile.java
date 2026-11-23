package org.apache.openejb.math.stat.descriptive.rank;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.openejb.math.MathRuntimeException;
import org.apache.openejb.math.stat.descriptive.AbstractUnivariateStatistic;

public class Percentile extends AbstractUnivariateStatistic implements Serializable {
   private static final long serialVersionUID = -1231216485095130416L;
   private double quantile;

   public Percentile() {
      this((double)50.0F);
   }

   public Percentile(double p) {
      this.setQuantile(p);
   }

   public Percentile(Percentile original) {
      copy(original, this);
   }

   public double evaluate(double[] values, double p) {
      this.test(values, 0, 0);
      return this.evaluate(values, 0, values.length, p);
   }

   public double evaluate(double[] values, int start, int length) {
      return this.evaluate(values, start, length, this.quantile);
   }

   public double evaluate(double[] values, int begin, int length, double p) {
      this.test(values, begin, length);
      if (!(p > (double)100.0F) && !(p <= (double)0.0F)) {
         if (length == 0) {
            return Double.NaN;
         } else if (length == 1) {
            return values[begin];
         } else {
            double n = (double)length;
            double pos = p * (n + (double)1.0F) / (double)100.0F;
            double fpos = Math.floor(pos);
            int intPos = (int)fpos;
            double dif = pos - fpos;
            double[] sorted = new double[length];
            System.arraycopy(values, begin, sorted, 0, length);
            Arrays.sort(sorted);
            if (pos < (double)1.0F) {
               return sorted[0];
            } else if (pos >= n) {
               return sorted[length - 1];
            } else {
               double lower = sorted[intPos - 1];
               double upper = sorted[intPos];
               return lower + dif * (upper - lower);
            }
         }
      } else {
         throw MathRuntimeException.createIllegalArgumentException("out of bounds quantile value: {0}, must be in (0, 100]", p);
      }
   }

   public double getQuantile() {
      return this.quantile;
   }

   public void setQuantile(double p) {
      if (!(p <= (double)0.0F) && !(p > (double)100.0F)) {
         this.quantile = p;
      } else {
         throw MathRuntimeException.createIllegalArgumentException("out of bounds quantile value: {0}, must be in (0, 100]", p);
      }
   }

   public Percentile copy() {
      Percentile result = new Percentile();
      copy(this, result);
      return result;
   }

   public static void copy(Percentile source, Percentile dest) {
      dest.quantile = source.quantile;
   }
}
