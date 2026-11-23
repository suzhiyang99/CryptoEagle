package org.apache.spark.util.random;

public final class PoissonBounds$ {
   public static final PoissonBounds$ MODULE$;

   static {
      new PoissonBounds$();
   }

   public double getLowerBound(double s) {
      return scala.math.package$.MODULE$.max(s - this.numStd(s) * scala.math.package$.MODULE$.sqrt(s), 1.0E-15);
   }

   public double getUpperBound(double s) {
      return scala.math.package$.MODULE$.max(s + this.numStd(s) * scala.math.package$.MODULE$.sqrt(s), 1.0E-10);
   }

   private double numStd(double s) {
      return s < (double)6.0F ? (double)12.0F : (s < (double)16.0F ? (double)9.0F : (double)6.0F);
   }

   private PoissonBounds$() {
      MODULE$ = this;
   }
}
