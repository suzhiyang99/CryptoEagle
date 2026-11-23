package org.apache.openejb.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.openejb.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.openejb.math.stat.descriptive.WeightedEvaluation;
import org.apache.openejb.math.stat.descriptive.summary.Sum;

public class Mean extends AbstractStorelessUnivariateStatistic implements Serializable, WeightedEvaluation {
   private static final long serialVersionUID = -1236043746617791564L;
   protected FirstMoment moment;
   protected boolean incMoment;

   public Mean() {
      this.incMoment = true;
      this.moment = new FirstMoment();
   }

   public Mean(FirstMoment m1) {
      this.moment = m1;
      this.incMoment = false;
   }

   public Mean(Mean original) {
      copy(original, this);
   }

   public void increment(double d) {
      if (this.incMoment) {
         this.moment.increment(d);
      }

   }

   public void clear() {
      if (this.incMoment) {
         this.moment.clear();
      }

   }

   public double getResult() {
      return this.moment.m1;
   }

   public long getN() {
      return this.moment.getN();
   }

   public double evaluate(double[] values, int begin, int length) {
      if (!this.test(values, begin, length)) {
         return Double.NaN;
      } else {
         Sum sum = new Sum();
         double sampleSize = (double)length;
         double xbar = sum.evaluate(values, begin, length) / sampleSize;
         double correction = (double)0.0F;

         for(int i = begin; i < begin + length; ++i) {
            correction += values[i] - xbar;
         }

         return xbar + correction / sampleSize;
      }
   }

   public double evaluate(double[] values, double[] weights, int begin, int length) {
      if (!this.test(values, weights, begin, length)) {
         return Double.NaN;
      } else {
         Sum sum = new Sum();
         double sumw = sum.evaluate(weights, begin, length);
         double xbarw = sum.evaluate(values, weights, begin, length) / sumw;
         double correction = (double)0.0F;

         for(int i = begin; i < begin + length; ++i) {
            correction += weights[i] * (values[i] - xbarw);
         }

         return xbarw + correction / sumw;
      }
   }

   public double evaluate(double[] values, double[] weights) {
      return this.evaluate(values, weights, 0, values.length);
   }

   public Mean copy() {
      Mean result = new Mean();
      copy(this, result);
      return result;
   }

   public static void copy(Mean source, Mean dest) {
      dest.incMoment = source.incMoment;
      dest.moment = source.moment.copy();
   }
}
