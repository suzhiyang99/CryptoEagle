package org.apache.spark.util.random;

import java.util.Random;
import scala.Serializable;

public final class GapSamplingReplacement$ implements Serializable {
   public static final GapSamplingReplacement$ MODULE$;

   static {
      new GapSamplingReplacement$();
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

   private GapSamplingReplacement$() {
      MODULE$ = this;
   }
}
