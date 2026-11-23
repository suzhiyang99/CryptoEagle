package org.apache.spark.partial;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.spark.util.StatCounter;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-3Q!\u0001\u0002\u0001\t)\u0011Q\"T3b]\u00163\u0018\r\\;bi>\u0014(BA\u0002\u0005\u0003\u001d\u0001\u0018M\u001d;jC2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0004\u0001-\t\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0003\u0013'UYR\"\u0001\u0002\n\u0005Q\u0011!\u0001F!qaJ|\u00070[7bi\u0016,e/\u00197vCR|'\u000f\u0005\u0002\u001735\tqC\u0003\u0002\u0019\t\u0005!Q\u000f^5m\u0013\tQrCA\u0006Ti\u0006$8i\\;oi\u0016\u0014\bC\u0001\n\u001d\u0013\ti\"AA\u0007C_VtG-\u001a3E_V\u0014G.\u001a\u0005\t?\u0001\u0011\t\u0011)A\u0005C\u0005aAo\u001c;bY>+H\u000f];ug\u000e\u0001\u0001C\u0001\u0007#\u0013\t\u0019SBA\u0002J]RD\u0001\"\n\u0001\u0003\u0002\u0003\u0006IAJ\u0001\u000bG>tg-\u001b3f]\u000e,\u0007C\u0001\u0007(\u0013\tASB\u0001\u0004E_V\u0014G.\u001a\u0005\u0006U\u0001!\taK\u0001\u0007y%t\u0017\u000e\u001e \u0015\u00071jc\u0006\u0005\u0002\u0013\u0001!)q$\u000ba\u0001C!)Q%\u000ba\u0001M!9\u0001\u0007\u0001a\u0001\n\u0013\t\u0014!D8viB,Ho]'fe\u001e,G-F\u0001\"\u0011\u001d\u0019\u0004\u00011A\u0005\nQ\n\u0011c\\;uaV$8/T3sO\u0016$w\fJ3r)\t)\u0004\b\u0005\u0002\rm%\u0011q'\u0004\u0002\u0005+:LG\u000fC\u0004:e\u0005\u0005\t\u0019A\u0011\u0002\u0007a$\u0013\u0007\u0003\u0004<\u0001\u0001\u0006K!I\u0001\u000f_V$\b/\u001e;t\u001b\u0016\u0014x-\u001a3!\u0011\u001di\u0004A1A\u0005\ny\nqaY8v]R,'/F\u0001\u0016\u0011\u0019\u0001\u0005\u0001)A\u0005+\u0005A1m\\;oi\u0016\u0014\b\u0005C\u0003C\u0001\u0011\u00053)A\u0003nKJ<W\rF\u00026\t\u001aCQ!R!A\u0002\u0005\n\u0001b\\;uaV$\u0018\n\u001a\u0005\u0006\u000f\u0006\u0003\r!F\u0001\u000bi\u0006\u001c8NU3tk2$\b\"B%\u0001\t\u0003R\u0015!D2veJ,g\u000e\u001e*fgVdG\u000fF\u0001\u001c\u0001"
)
public class MeanEvaluator implements ApproximateEvaluator<StatCounter, BoundedDouble> {
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
         var10000 = new BoundedDouble(this.counter().mean(), (double)1.0F, this.counter().mean(), this.counter().mean());
      } else if (this.outputsMerged() != 0 && this.counter().count() != 0L) {
         if (this.counter().count() == 1L) {
            var10000 = new BoundedDouble(this.counter().mean(), this.confidence, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
         } else {
            double mean = this.counter().mean();
            double stdev = scala.math.package$.MODULE$.sqrt(this.counter().sampleVariance() / (double)this.counter().count());
            double var12;
            if (this.counter().count() > 100L) {
               var12 = (new NormalDistribution()).inverseCumulativeProbability(((double)1 + this.confidence) / (double)2);
            } else {
               int degreesOfFreedom = (int)(this.counter().count() - 1L);
               var12 = (new TDistribution((double)degreesOfFreedom)).inverseCumulativeProbability(((double)1 + this.confidence) / (double)2);
            }

            double confFactor = var12;
            double low = mean - confFactor * stdev;
            double high = mean + confFactor * stdev;
            var10000 = new BoundedDouble(mean, this.confidence, low, high);
         }
      } else {
         var10000 = new BoundedDouble((double)0.0F, (double)0.0F, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
      }

      return var10000;
   }

   public MeanEvaluator(int totalOutputs, double confidence) {
      this.totalOutputs = totalOutputs;
      this.confidence = confidence;
      this.outputsMerged = 0;
      this.counter = new StatCounter();
   }
}
