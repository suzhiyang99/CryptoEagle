package org.apache.openejb.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.openejb.math.MathRuntimeException;
import org.apache.openejb.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.openejb.math.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.openejb.math.stat.descriptive.summary.SumOfLogs;

public class GeometricMean extends AbstractStorelessUnivariateStatistic implements Serializable {
   private static final long serialVersionUID = -1238734905303459453L;
   private StorelessUnivariateStatistic sumOfLogs;

   public GeometricMean() {
      this.sumOfLogs = new SumOfLogs();
   }

   public GeometricMean(GeometricMean original) {
      copy(original, this);
   }

   public GeometricMean(SumOfLogs sumOfLogs) {
      this.sumOfLogs = sumOfLogs;
   }

   public GeometricMean copy() {
      GeometricMean result = new GeometricMean();
      copy(this, result);
      return result;
   }

   public void increment(double d) {
      this.sumOfLogs.increment(d);
   }

   public double getResult() {
      return this.sumOfLogs.getN() > 0L ? Math.exp(this.sumOfLogs.getResult() / (double)this.sumOfLogs.getN()) : Double.NaN;
   }

   public void clear() {
      this.sumOfLogs.clear();
   }

   public double evaluate(double[] values, int begin, int length) {
      return Math.exp(this.sumOfLogs.evaluate(values, begin, length) / (double)length);
   }

   public long getN() {
      return this.sumOfLogs.getN();
   }

   public void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl) {
      this.checkEmpty();
      this.sumOfLogs = sumLogImpl;
   }

   public StorelessUnivariateStatistic getSumLogImpl() {
      return this.sumOfLogs;
   }

   public static void copy(GeometricMean source, GeometricMean dest) {
      dest.sumOfLogs = source.sumOfLogs.copy();
   }

   private void checkEmpty() {
      if (this.getN() > 0L) {
         throw MathRuntimeException.createIllegalStateException("{0} values have been added before statistic is configured", this.getN());
      }
   }
}
