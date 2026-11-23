package org.apache.spark.util.random;

import java.util.Random;
import scala.Serializable;

public final class GapSampling$ implements Serializable {
   public static final GapSampling$ MODULE$;

   static {
      new GapSampling$();
   }

   public Random $lessinit$greater$default$2() {
      return RandomSampler$.MODULE$.newDefaultRNG();
   }

   public double $lessinit$greater$default$3() {
      return RandomSampler$.MODULE$.rngEpsilon();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private GapSampling$() {
      MODULE$ = this;
   }
}
