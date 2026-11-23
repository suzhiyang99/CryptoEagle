package org.apache.spark.partial;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.spark.util.StatCounter;
import scala.Predef$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-3Q!\u0001\u0002\u0001\t)\u0011AbU;n\u000bZ\fG.^1u_JT!a\u0001\u0003\u0002\u000fA\f'\u000f^5bY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007\u0003\u0002\n\u0014+mi\u0011AA\u0005\u0003)\t\u0011A#\u00119qe>D\u0018.\\1uK\u00163\u0018\r\\;bi>\u0014\bC\u0001\f\u001a\u001b\u00059\"B\u0001\r\u0005\u0003\u0011)H/\u001b7\n\u0005i9\"aC*uCR\u001cu.\u001e8uKJ\u0004\"A\u0005\u000f\n\u0005u\u0011!!\u0004\"pk:$W\r\u001a#pk\ndW\r\u0003\u0005 \u0001\t\u0005\t\u0015!\u0003\"\u00031!x\u000e^1m\u001fV$\b/\u001e;t\u0007\u0001\u0001\"\u0001\u0004\u0012\n\u0005\rj!aA%oi\"AQ\u0005\u0001B\u0001B\u0003%a%\u0001\u0006d_:4\u0017\u000eZ3oG\u0016\u0004\"\u0001D\u0014\n\u0005!j!A\u0002#pk\ndW\rC\u0003+\u0001\u0011\u00051&\u0001\u0004=S:LGO\u0010\u000b\u0004Y5r\u0003C\u0001\n\u0001\u0011\u0015y\u0012\u00061\u0001\"\u0011\u0015)\u0013\u00061\u0001'\u0011\u001d\u0001\u0004\u00011A\u0005\nE\nQb\\;uaV$8/T3sO\u0016$W#A\u0011\t\u000fM\u0002\u0001\u0019!C\u0005i\u0005\tr.\u001e;qkR\u001cX*\u001a:hK\u0012|F%Z9\u0015\u0005UB\u0004C\u0001\u00077\u0013\t9TB\u0001\u0003V]&$\bbB\u001d3\u0003\u0003\u0005\r!I\u0001\u0004q\u0012\n\u0004BB\u001e\u0001A\u0003&\u0011%\u0001\bpkR\u0004X\u000f^:NKJ<W\r\u001a\u0011\t\u000fu\u0002!\u0019!C\u0005}\u000591m\\;oi\u0016\u0014X#A\u000b\t\r\u0001\u0003\u0001\u0015!\u0003\u0016\u0003!\u0019w.\u001e8uKJ\u0004\u0003\"\u0002\"\u0001\t\u0003\u001a\u0015!B7fe\u001e,GcA\u001bE\r\")Q)\u0011a\u0001C\u0005Aq.\u001e;qkRLE\rC\u0003H\u0003\u0002\u0007Q#\u0001\u0006uCN\\'+Z:vYRDQ!\u0013\u0001\u0005B)\u000bQbY;se\u0016tGOU3tk2$H#A\u000e"
)
public class SumEvaluator implements ApproximateEvaluator<StatCounter, BoundedDouble> {
   private final int totalOutputs;
   private final double confidence;
   private int outputsMerged;
   private final StatCounter counter;

   private int outputsMerged() {
      return this.outputsMerged;
   }

   private void outputsMerged_$eq(int x$1) {
      this.outputsMerged = x$1;
   }

   private StatCounter counter() {
      return this.counter;
   }

   public void merge(int outputId, StatCounter taskResult) {
      this.outputsMerged_$eq(this.outputsMerged() + 1);
      this.counter().merge(taskResult);
   }

   public BoundedDouble currentResult() {
      BoundedDouble var10000;
      if (this.outputsMerged() == this.totalOutputs) {
         var10000 = new BoundedDouble(this.counter().sum(), (double)1.0F, this.counter().sum(), this.counter().sum());
      } else if (this.outputsMerged() != 0 && this.counter().count() != 0L) {
         double p = (double)this.outputsMerged() / (double)this.totalOutputs;
         double meanEstimate = this.counter().mean();
         double countEstimate = (double)this.counter().count() * ((double)1 - p) / p;
         double sumEstimate = meanEstimate * countEstimate;
         double meanVar = this.counter().sampleVariance() / (double)this.counter().count();
         if (!Predef$.MODULE$.double2Double(meanVar).isNaN() && this.counter().count() != 1L) {
            double countVar = (double)this.counter().count() * ((double)1 - p) / (p * p);
            double sumVar = meanEstimate * meanEstimate * countVar + countEstimate * countEstimate * meanVar + meanVar * countVar;
            double sumStdev = scala.math.package$.MODULE$.sqrt(sumVar);
            double var24;
            if (this.counter().count() > 100L) {
               var24 = (new NormalDistribution()).inverseCumulativeProbability(((double)1 + this.confidence) / (double)2);
            } else {
               int degreesOfFreedom = (int)(this.counter().count() - 1L);
               var24 = (new TDistribution((double)degreesOfFreedom)).inverseCumulativeProbability(((double)1 + this.confidence) / (double)2);
            }

            double confFactor = var24;
            double low = sumEstimate - confFactor * sumStdev;
            double high = sumEstimate + confFactor * sumStdev;
            var10000 = new BoundedDouble(this.counter().sum() + sumEstimate, this.confidence, this.counter().sum() + low, this.counter().sum() + high);
         } else {
            var10000 = new BoundedDouble(this.counter().sum() + sumEstimate, this.confidence, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
         }
      } else {
         var10000 = new BoundedDouble((double)0.0F, (double)0.0F, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
      }

      return var10000;
   }

   public SumEvaluator(int totalOutputs, double confidence) {
      this.totalOutputs = totalOutputs;
      this.confidence = confidence;
      this.outputsMerged = 0;
      this.counter = new StatCounter();
   }
}
