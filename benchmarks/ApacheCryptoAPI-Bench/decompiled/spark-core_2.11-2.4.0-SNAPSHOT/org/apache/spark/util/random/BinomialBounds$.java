package org.apache.spark.util.random;

public final class BinomialBounds$ {
   public static final BinomialBounds$ MODULE$;
   private final double minSamplingRate;

   static {
      new BinomialBounds$();
   }

   public double minSamplingRate() {
      return this.minSamplingRate;
   }

   public double getLowerBound(double delta, long n, double fraction) {
      double gamma = -scala.math.package$.MODULE$.log(delta) / (double)n * 0.6666666666666666;
      return fraction + gamma - scala.math.package$.MODULE$.sqrt(gamma * gamma + (double)3 * gamma * fraction);
   }

   public double getUpperBound(double delta, long n, double fraction) {
      double gamma = -scala.math.package$.MODULE$.log(delta) / (double)n;
      return scala.math.package$.MODULE$.min((double)1.0F, scala.math.package$.MODULE$.max(this.minSamplingRate(), fraction + gamma + scala.math.package$.MODULE$.sqrt(gamma * gamma + (double)2 * gamma * fraction)));
   }

   private BinomialBounds$() {
      MODULE$ = this;
      this.minSamplingRate = 1.0E-10;
   }
}
