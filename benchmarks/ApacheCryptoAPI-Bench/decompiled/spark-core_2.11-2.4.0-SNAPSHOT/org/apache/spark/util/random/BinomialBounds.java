package org.apache.spark.util.random;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U:a!\u0001\u0002\t\u0002\u0019a\u0011A\u0004\"j]>l\u0017.\u00197C_VtGm\u001d\u0006\u0003\u0007\u0011\taA]1oI>l'BA\u0003\u0007\u0003\u0011)H/\u001b7\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u0004\"!\u0004\b\u000e\u0003\t1aa\u0004\u0002\t\u0002\u0019\u0001\"A\u0004\"j]>l\u0017.\u00197C_VtGm]\n\u0003\u001dE\u0001\"AE\u000b\u000e\u0003MQ\u0011\u0001F\u0001\u0006g\u000e\fG.Y\u0005\u0003-M\u0011a!\u00118z%\u00164\u0007\"\u0002\r\u000f\t\u0003Q\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u00031Aq\u0001\b\bC\u0002\u0013\u0005Q$A\bnS:\u001c\u0016-\u001c9mS:<'+\u0019;f+\u0005q\u0002C\u0001\n \u0013\t\u00013C\u0001\u0004E_V\u0014G.\u001a\u0005\u0007E9\u0001\u000b\u0011\u0002\u0010\u0002!5LgnU1na2Lgn\u001a*bi\u0016\u0004\u0003\"\u0002\u0013\u000f\t\u0003)\u0013!D4fi2{w/\u001a:C_VtG\r\u0006\u0003\u001fM!j\u0003\"B\u0014$\u0001\u0004q\u0012!\u00023fYR\f\u0007\"B\u0015$\u0001\u0004Q\u0013!\u00018\u0011\u0005IY\u0013B\u0001\u0017\u0014\u0005\u0011auN\\4\t\u000b9\u001a\u0003\u0019\u0001\u0010\u0002\u0011\u0019\u0014\u0018m\u0019;j_:DQ\u0001\r\b\u0005\u0002E\nQbZ3u+B\u0004XM\u001d\"pk:$G\u0003\u0002\u00103gQBQaJ\u0018A\u0002yAQ!K\u0018A\u0002)BQAL\u0018A\u0002y\u0001"
)
public final class BinomialBounds {
   public static double getUpperBound(double var0, long var2, double var4) {
      return BinomialBounds$.MODULE$.getUpperBound(var0, var2, var4);
   }

   public static double getLowerBound(double var0, long var2, double var4) {
      return BinomialBounds$.MODULE$.getLowerBound(var0, var2, var4);
   }

   public static double minSamplingRate() {
      return BinomialBounds$.MODULE$.minSamplingRate();
   }
}
