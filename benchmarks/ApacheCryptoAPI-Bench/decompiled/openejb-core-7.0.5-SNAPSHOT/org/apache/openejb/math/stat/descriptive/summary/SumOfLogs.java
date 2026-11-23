package org.apache.openejb.math.stat.descriptive.summary;

import java.io.Serializable;
import org.apache.openejb.math.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class SumOfLogs extends AbstractStorelessUnivariateStatistic implements Serializable {
   private static final long serialVersionUID = -123076995648386763L;
   private int n;
   private double value;

   public SumOfLogs() {
      this.value = (double)0.0F;
      this.n = 0;
   }

   public SumOfLogs(SumOfLogs original) {
      copy(original, this);
   }

   public void increment(double d) {
      this.value += Math.log(d);
      ++this.n;
   }

   public double getResult() {
      return this.n > 0 ? this.value : Double.NaN;
   }

   public long getN() {
      return (long)this.n;
   }

   public void clear() {
      this.value = (double)0.0F;
      this.n = 0;
   }

   public double evaluate(double[] values, int begin, int length) {
      double sumLog = Double.NaN;
      if (this.test(values, begin, length)) {
         sumLog = (double)0.0F;

         for(int i = begin; i < begin + length; ++i) {
            sumLog += Math.log(values[i]);
         }
      }

      return sumLog;
   }

   public SumOfLogs copy() {
      SumOfLogs result = new SumOfLogs();
      copy(this, result);
      return result;
   }

   public static void copy(SumOfLogs source, SumOfLogs dest) {
      dest.n = source.n;
      dest.value = source.value;
   }
}
