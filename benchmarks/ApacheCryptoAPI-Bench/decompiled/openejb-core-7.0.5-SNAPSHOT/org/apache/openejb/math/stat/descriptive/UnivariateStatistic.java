package org.apache.openejb.math.stat.descriptive;

public interface UnivariateStatistic {
   double evaluate(double[] var1);

   double evaluate(double[] var1, int var2, int var3);

   UnivariateStatistic copy();
}
