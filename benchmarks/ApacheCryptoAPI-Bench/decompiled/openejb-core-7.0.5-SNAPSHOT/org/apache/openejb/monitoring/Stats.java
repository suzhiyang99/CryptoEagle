package org.apache.openejb.monitoring;

import org.apache.openejb.math.stat.descriptive.SynchronizedDescriptiveStatistics;

@Managed(
   append = true
)
public class Stats {
   private final Event event;
   private final SynchronizedDescriptiveStatistics samples;

   public Stats() {
      this(1000);
   }

   public Stats(int window) {
      this.event = new Event();
      this.samples = new SynchronizedDescriptiveStatistics(window);
   }

   @Managed
   public void setSampleSize(int i) {
      this.samples.setWindowSize(i);
   }

   @Managed
   public int getSampleSize() {
      return this.samples.getWindowSize();
   }

   @Managed
   public double getPercentile99() {
      return this.samples.getPercentile((double)99.0F);
   }

   @Managed
   public double getPercentile90() {
      return this.samples.getPercentile((double)90.0F);
   }

   @Managed
   public double getPercentile75() {
      return this.samples.getPercentile((double)75.0F);
   }

   @Managed
   public double getPercentile50() {
      return this.samples.getPercentile((double)50.0F);
   }

   @Managed
   public double getPercentile25() {
      return this.samples.getPercentile((double)25.0F);
   }

   @Managed
   public double getPercentile10() {
      return this.samples.getPercentile((double)10.0F);
   }

   @Managed
   public double getPercentile01() {
      return this.samples.getPercentile((double)1.0F);
   }

   @Managed
   public double getStandardDeviation() {
      return this.samples.getStandardDeviation();
   }

   @Managed
   public double getMean() {
      return this.samples.getMean();
   }

   @Managed
   public double getVariance() {
      return this.samples.getVariance();
   }

   @Managed
   public double getGeometricMean() {
      return this.samples.getGeometricMean();
   }

   @Managed
   public double getSkewness() {
      return this.samples.getSkewness();
   }

   @Managed
   public double getKurtosis() {
      return this.samples.getKurtosis();
   }

   @Managed
   public double getMax() {
      return this.samples.getMax();
   }

   @Managed
   public double getMin() {
      return this.samples.getMin();
   }

   @Managed
   public double getSum() {
      return this.samples.getSum();
   }

   @Managed
   public double getSumsq() {
      return this.samples.getSumsq();
   }

   @Managed
   public double[] sortedValues() {
      return this.samples.getSortedValues();
   }

   @Managed
   public double[] values() {
      return this.samples.getValues();
   }

   public void record(long time) {
      this.event.record();
      this.samples.addValue((double)time);
   }

   @Managed
   public long getCount() {
      return this.event.get();
   }

   @Managed
   public String getLatest() {
      return this.event.getLatest();
   }

   @Managed
   public long getLatestTime() {
      return this.event.getLatestTime();
   }
}
