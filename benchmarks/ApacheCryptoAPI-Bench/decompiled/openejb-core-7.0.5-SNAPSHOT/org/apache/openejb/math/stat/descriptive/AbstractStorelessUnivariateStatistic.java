package org.apache.openejb.math.stat.descriptive;

import org.apache.openejb.math.MathRuntimeException;
import org.apache.openejb.math.util.MathUtils;

public abstract class AbstractStorelessUnivariateStatistic extends AbstractUnivariateStatistic implements StorelessUnivariateStatistic {
   public double evaluate(double[] values) {
      if (values == null) {
         throw MathRuntimeException.createIllegalArgumentException("input values array is null");
      } else {
         return this.evaluate(values, 0, values.length);
      }
   }

   public double evaluate(double[] values, int begin, int length) {
      if (this.test(values, begin, length)) {
         this.clear();
         this.incrementAll(values, begin, length);
      }

      return this.getResult();
   }

   public abstract StorelessUnivariateStatistic copy();

   public abstract void clear();

   public abstract double getResult();

   public abstract void increment(double var1);

   public void incrementAll(double[] values) {
      if (values == null) {
         throw MathRuntimeException.createIllegalArgumentException("input values array is null");
      } else {
         this.incrementAll(values, 0, values.length);
      }
   }

   public void incrementAll(double[] values, int begin, int length) {
      if (this.test(values, begin, length)) {
         int k = begin + length;

         for(int i = begin; i < k; ++i) {
            this.increment(values[i]);
         }
      }

   }

   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof AbstractStorelessUnivariateStatistic)) {
         return false;
      } else {
         AbstractStorelessUnivariateStatistic stat = (AbstractStorelessUnivariateStatistic)object;
         return MathUtils.equals(stat.getResult(), this.getResult()) && MathUtils.equals((double)stat.getN(), (double)this.getN());
      }
   }

   public int hashCode() {
      return 31 * (31 + MathUtils.hash(this.getResult())) + MathUtils.hash((double)this.getN());
   }
}
