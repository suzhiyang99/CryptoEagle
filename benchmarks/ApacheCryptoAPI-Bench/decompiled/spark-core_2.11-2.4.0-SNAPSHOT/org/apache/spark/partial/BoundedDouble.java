package org.apache.spark.partial;

import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001)3A!\u0001\u0002\u0001\u0017\ti!i\\;oI\u0016$Gi\\;cY\u0016T!a\u0001\u0003\u0002\u000fA\f'\u000f^5bY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001a\u0001CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0003\u0005\u0014\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u0011iW-\u00198\u0016\u0003U\u0001\"!\u0004\f\n\u0005]q!A\u0002#pk\ndW\r\u0003\u0005\u001a\u0001\t\u0005\t\u0015!\u0003\u0016\u0003\u0015iW-\u00198!\u0011!Y\u0002A!b\u0001\n\u0003!\u0012AC2p]\u001aLG-\u001a8dK\"AQ\u0004\u0001B\u0001B\u0003%Q#A\u0006d_:4\u0017\u000eZ3oG\u0016\u0004\u0003\u0002C\u0010\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002\u00071|w\u000f\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003\u0016\u0003\u0011awn\u001e\u0011\t\u0011\r\u0002!Q1A\u0005\u0002Q\tA\u0001[5hQ\"AQ\u0005\u0001B\u0001B\u0003%Q#A\u0003iS\u001eD\u0007\u0005C\u0003(\u0001\u0011\u0005\u0001&\u0001\u0004=S:LGO\u0010\u000b\u0006S-bSF\f\t\u0003U\u0001i\u0011A\u0001\u0005\u0006'\u0019\u0002\r!\u0006\u0005\u00067\u0019\u0002\r!\u0006\u0005\u0006?\u0019\u0002\r!\u0006\u0005\u0006G\u0019\u0002\r!\u0006\u0005\u0006a\u0001!\t%M\u0001\ti>\u001cFO]5oOR\t!\u0007\u0005\u00024m9\u0011Q\u0002N\u0005\u0003k9\ta\u0001\u0015:fI\u00164\u0017BA\u001c9\u0005\u0019\u0019FO]5oO*\u0011QG\u0004\u0005\u0006u\u0001!\teO\u0001\tQ\u0006\u001c\bnQ8eKR\tA\b\u0005\u0002\u000e{%\u0011aH\u0004\u0002\u0004\u0013:$\b\"\u0002!\u0001\t\u0003\n\u0015AB3rk\u0006d7\u000f\u0006\u0002C\u000bB\u0011QbQ\u0005\u0003\t:\u0011qAQ8pY\u0016\fg\u000eC\u0003G\u007f\u0001\u0007q)\u0001\u0003uQ\u0006$\bCA\u0007I\u0013\tIeBA\u0002B]f\u0004"
)
public class BoundedDouble {
   private final double mean;
   private final double confidence;
   private final double low;
   private final double high;

   public double mean() {
      return this.mean;
   }

   public double confidence() {
      return this.confidence;
   }

   public double low() {
      return this.low;
   }

   public double high() {
      return this.high;
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("[%.3f, %.3f]"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.low()), BoxesRunTime.boxToDouble(this.high())}));
   }

   public int hashCode() {
      return BoxesRunTime.boxToDouble(this.mean()).hashCode() ^ BoxesRunTime.boxToDouble(this.confidence()).hashCode() ^ BoxesRunTime.boxToDouble(this.low()).hashCode() ^ BoxesRunTime.boxToDouble(this.high()).hashCode();
   }

   public boolean equals(Object that) {
      boolean var4;
      if (that instanceof BoundedDouble) {
         BoundedDouble var3 = (BoundedDouble)that;
         var4 = this.mean() == var3.mean() && this.confidence() == var3.confidence() && this.low() == var3.low() && this.high() == var3.high();
      } else {
         var4 = false;
      }

      return var4;
   }

   public BoundedDouble(double mean, double confidence, double low, double high) {
      this.mean = mean;
      this.confidence = confidence;
      this.low = low;
      this.high = high;
   }
}
