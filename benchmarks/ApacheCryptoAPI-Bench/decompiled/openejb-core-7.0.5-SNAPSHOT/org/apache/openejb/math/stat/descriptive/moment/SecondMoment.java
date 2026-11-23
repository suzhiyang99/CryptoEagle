package org.apache.openejb.math.stat.descriptive.moment;

import java.io.Serializable;

public class SecondMoment extends FirstMoment implements Serializable {
   private static final long serialVersionUID = 1232403127395076445L;
   protected double m2;

   public SecondMoment() {
      this.m2 = Double.NaN;
   }

   public SecondMoment(SecondMoment original) {
      super(original);
      this.m2 = original.m2;
   }

   public void increment(double d) {
      if (this.n < 1L) {
         this.m1 = this.m2 = (double)0.0F;
      }

      super.increment(d);
      this.m2 += ((double)this.n - (double)1.0F) * this.dev * this.nDev;
   }

   public void clear() {
      super.clear();
      this.m2 = Double.NaN;
   }

   public double getResult() {
      return this.m2;
   }

   public SecondMoment copy() {
      SecondMoment result = new SecondMoment();
      copy(this, result);
      return result;
   }

   public static void copy(SecondMoment source, SecondMoment dest) {
      FirstMoment.copy(source, dest);
      dest.m2 = source.m2;
   }
}
