package org.apache.spark.util.random;

import java.util.Random;
import scala.Serializable;

public final class RandomSampler$ implements Serializable {
   public static final RandomSampler$ MODULE$;
   private final double defaultMaxGapSamplingFraction;
   private final double rngEpsilon;
   private final double roundingEpsilon;

   static {
      new RandomSampler$();
   }

   public Random newDefaultRNG() {
      return new XORShiftRandom();
   }

   public double defaultMaxGapSamplingFraction() {
      return this.defaultMaxGapSamplingFraction;
   }

   public double rngEpsilon() {
      return this.rngEpsilon;
   }

   public double roundingEpsilon() {
      return this.roundingEpsilon;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RandomSampler$() {
      MODULE$ = this;
      this.defaultMaxGapSamplingFraction = 0.4;
      this.rngEpsilon = 5.0E-11;
      this.roundingEpsilon = 1.0E-6;
   }
}
