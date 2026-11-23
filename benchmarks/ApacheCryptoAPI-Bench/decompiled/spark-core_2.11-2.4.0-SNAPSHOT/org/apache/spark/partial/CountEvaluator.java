package org.apache.spark.partial;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001i3Q!\u0001\u0002\u0001\t)\u0011abQ8v]R,e/\u00197vCR|'O\u0003\u0002\u0004\t\u00059\u0001/\u0019:uS\u0006d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0005%M)\u0002$D\u0001\u0003\u0013\t!\"A\u0001\u000bBaB\u0014x\u000e_5nCR,WI^1mk\u0006$xN\u001d\t\u0003\u0019YI!aF\u0007\u0003\t1{gn\u001a\t\u0003%eI!A\u0007\u0002\u0003\u001b\t{WO\u001c3fI\u0012{WO\u00197f\u0011!a\u0002A!A!\u0002\u0013q\u0012\u0001\u0004;pi\u0006dw*\u001e;qkR\u001c8\u0001\u0001\t\u0003\u0019}I!\u0001I\u0007\u0003\u0007%sG\u000f\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003$\u0003)\u0019wN\u001c4jI\u0016t7-\u001a\t\u0003\u0019\u0011J!!J\u0007\u0003\r\u0011{WO\u00197f\u0011\u00159\u0003\u0001\"\u0001)\u0003\u0019a\u0014N\\5u}Q\u0019\u0011FK\u0016\u0011\u0005I\u0001\u0001\"\u0002\u000f'\u0001\u0004q\u0002\"\u0002\u0012'\u0001\u0004\u0019\u0003bB\u0017\u0001\u0001\u0004%IAL\u0001\u000e_V$\b/\u001e;t\u001b\u0016\u0014x-\u001a3\u0016\u0003yAq\u0001\r\u0001A\u0002\u0013%\u0011'A\tpkR\u0004X\u000f^:NKJ<W\rZ0%KF$\"AM\u001b\u0011\u00051\u0019\u0014B\u0001\u001b\u000e\u0005\u0011)f.\u001b;\t\u000fYz\u0013\u0011!a\u0001=\u0005\u0019\u0001\u0010J\u0019\t\ra\u0002\u0001\u0015)\u0003\u001f\u00039yW\u000f\u001e9viNlUM]4fI\u0002BqA\u000f\u0001A\u0002\u0013%1(A\u0002tk6,\u0012!\u0006\u0005\b{\u0001\u0001\r\u0011\"\u0003?\u0003\u001d\u0019X/\\0%KF$\"AM \t\u000fYb\u0014\u0011!a\u0001+!1\u0011\t\u0001Q!\nU\tAa];nA!)1\t\u0001C!\t\u0006)Q.\u001a:hKR\u0019!'R$\t\u000b\u0019\u0013\u0005\u0019\u0001\u0010\u0002\u0011=,H\u000f];u\u0013\u0012DQ\u0001\u0013\"A\u0002U\t!\u0002^1tWJ+7/\u001e7u\u0011\u0015Q\u0005\u0001\"\u0011L\u00035\u0019WO\u001d:f]R\u0014Vm];miR\t\u0001d\u0002\u0004N\u0005!\u0005!AT\u0001\u000f\u0007>,h\u000e^#wC2,\u0018\r^8s!\t\u0011rJ\u0002\u0004\u0002\u0005!\u0005!\u0001U\n\u0003\u001f.AQaJ(\u0005\u0002I#\u0012A\u0014\u0005\u0006)>#\t!V\u0001\u0006E>,h\u000e\u001a\u000b\u00051Y;\u0006\fC\u0003#'\u0002\u00071\u0005C\u0003;'\u0002\u0007Q\u0003C\u0003Z'\u0002\u00071%A\u0001q\u0001"
)
public class CountEvaluator implements ApproximateEvaluator<Object, BoundedDouble> {
   private final int totalOutputs;
   private final double confidence;
   private int outputsMerged;
   private long sum;

   public static BoundedDouble bound(double var0, long var2, double var4) {
      return CountEvaluator$.MODULE$.bound(var0, var2, var4);
   }

   private int outputsMerged() {
      return this.outputsMerged;
   }

   private void outputsMerged_$eq(int x$1) {
      this.outputsMerged = x$1;
   }

   private long sum() {
      return this.sum;
   }

   private void sum_$eq(long x$1) {
      this.sum = x$1;
   }

   public void merge(int outputId, long taskResult) {
      this.outputsMerged_$eq(this.outputsMerged() + 1);
      this.sum_$eq(this.sum() + taskResult);
   }

   public BoundedDouble currentResult() {
      BoundedDouble var10000;
      if (this.outputsMerged() == this.totalOutputs) {
         var10000 = new BoundedDouble((double)this.sum(), (double)1.0F, (double)this.sum(), (double)this.sum());
      } else if (this.outputsMerged() != 0 && this.sum() != 0L) {
         double p = (double)this.outputsMerged() / (double)this.totalOutputs;
         var10000 = CountEvaluator$.MODULE$.bound(this.confidence, this.sum(), p);
      } else {
         var10000 = new BoundedDouble((double)0.0F, (double)0.0F, (double)0.0F, Double.POSITIVE_INFINITY);
      }

      return var10000;
   }

   public CountEvaluator(int totalOutputs, double confidence) {
      this.totalOutputs = totalOutputs;
      this.confidence = confidence;
      this.outputsMerged = 0;
      this.sum = 0L;
   }
}
