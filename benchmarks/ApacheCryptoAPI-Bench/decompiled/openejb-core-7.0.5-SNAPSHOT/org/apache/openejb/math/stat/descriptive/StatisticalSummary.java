package org.apache.openejb.math.stat.descriptive;

public interface StatisticalSummary {
   double getMean();

   double getVariance();

   double getStandardDeviation();

   double getMax();

   double getMin();

   long getN();

   double getSum();
}
