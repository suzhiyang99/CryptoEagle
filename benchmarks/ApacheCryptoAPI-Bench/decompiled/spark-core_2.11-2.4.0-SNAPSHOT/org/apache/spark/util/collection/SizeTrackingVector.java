package org.apache.spark.util.collection;

import scala.collection.mutable.Queue;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\t3Q!\u0001\u0002\u0001\r1\u0011!cU5{KR\u0013\u0018mY6j]\u001e4Vm\u0019;pe*\u00111\u0001B\u0001\u000bG>dG.Z2uS>t'BA\u0003\u0007\u0003\u0011)H/\u001b7\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e,\"!\u0004\u000b\u0014\u0007\u0001q\u0011\u0005E\u0002\u0010!Ii\u0011AA\u0005\u0003#\t\u0011q\u0002\u0015:j[&$\u0018N^3WK\u000e$xN\u001d\t\u0003'Qa\u0001\u0001B\u0003\u0016\u0001\t\u0007qCA\u0001U\u0007\u0001\t\"\u0001\u0007\u0010\u0011\u0005eaR\"\u0001\u000e\u000b\u0003m\tQa]2bY\u0006L!!\b\u000e\u0003\u000f9{G\u000f[5oOB\u0011\u0011dH\u0005\u0003Ai\u00111!\u00118z!\ty!%\u0003\u0002$\u0005\tY1+\u001b>f)J\f7m[3s\u0011!)\u0003AaA!\u0002\u00171\u0013AC3wS\u0012,gnY3%cA\u0019qE\u000b\n\u000e\u0003!R!!\u000b\u000e\u0002\u000fI,g\r\\3di&\u00111\u0006\u000b\u0002\t\u00072\f7o\u001d+bO\")Q\u0006\u0001C\u0001]\u00051A(\u001b8jiz\"\u0012a\f\u000b\u0003aE\u00022a\u0004\u0001\u0013\u0011\u0015)C\u0006q\u0001'\u0011\u0015\u0019\u0004\u0001\"\u00115\u0003!!\u0003\u000f\\;tI\u0015\fHCA\u001b9!\tIb'\u0003\u000285\t!QK\\5u\u0011\u0015I$\u00071\u0001\u0013\u0003\u00151\u0018\r\\;f\u0011\u0015Y\u0004\u0001\"\u0011=\u0003\u0019\u0011Xm]5{KR\u0011a\"\u0010\u0005\u0006}i\u0002\raP\u0001\n]\u0016<H*\u001a8hi\"\u0004\"!\u0007!\n\u0005\u0005S\"aA%oi\u0002"
)
public class SizeTrackingVector<T> extends PrimitiveVector<T> implements SizeTracker {
   private final double org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE;
   private final Queue<SizeTracker.Sample> org$apache$spark$util$collection$SizeTracker$$samples;
   private double org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate;
   private long org$apache$spark$util$collection$SizeTracker$$numUpdates;
   private long org$apache$spark$util$collection$SizeTracker$$nextSampleNum;

   public double org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE() {
      return this.org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE;
   }

   public Queue<SizeTracker.Sample> org$apache$spark$util$collection$SizeTracker$$samples() {
      return this.org$apache$spark$util$collection$SizeTracker$$samples;
   }

   public double org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate() {
      return this.org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate;
   }

   @TraitSetter
   public void org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate_$eq(double x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate = x$1;
   }

   public long org$apache$spark$util$collection$SizeTracker$$numUpdates() {
      return this.org$apache$spark$util$collection$SizeTracker$$numUpdates;
   }

   @TraitSetter
   public void org$apache$spark$util$collection$SizeTracker$$numUpdates_$eq(long x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$numUpdates = x$1;
   }

   public long org$apache$spark$util$collection$SizeTracker$$nextSampleNum() {
      return this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum;
   }

   @TraitSetter
   public void org$apache$spark$util$collection$SizeTracker$$nextSampleNum_$eq(long x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum = x$1;
   }

   public void org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE_$eq(double x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE = x$1;
   }

   public void org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$samples_$eq(Queue x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$samples = x$1;
   }

   public void resetSamples() {
      SizeTracker$class.resetSamples(this);
   }

   public void afterUpdate() {
      SizeTracker$class.afterUpdate(this);
   }

   public long estimateSize() {
      return SizeTracker$class.estimateSize(this);
   }

   public void $plus$eq(T value) {
      super.$plus$eq(value);
      SizeTracker$class.afterUpdate(this);
   }

   public PrimitiveVector<T> resize(int newLength) {
      super.resize(newLength);
      this.resetSamples();
      return this;
   }

   public SizeTrackingVector(ClassTag<T> evidence$1) {
      super(PrimitiveVector$.MODULE$.$lessinit$greater$default$1(), evidence$1);
      SizeTracker$class.$init$(this);
   }
}
