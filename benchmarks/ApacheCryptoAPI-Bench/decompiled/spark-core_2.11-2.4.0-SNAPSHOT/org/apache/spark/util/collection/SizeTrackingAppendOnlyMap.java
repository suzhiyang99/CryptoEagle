package org.apache.spark.util.collection;

import scala.Function2;
import scala.collection.mutable.Queue;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00113Q!\u0001\u0002\u0001\r1\u0011\u0011dU5{KR\u0013\u0018mY6j]\u001e\f\u0005\u000f]3oI>sG._'ba*\u00111\u0001B\u0001\u000bG>dG.Z2uS>t'BA\u0003\u0007\u0003\u0011)H/\u001b7\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e,2!\u0004\u000b#'\r\u0001a\u0002\n\t\u0005\u001fA\u0011\u0012%D\u0001\u0003\u0013\t\t\"AA\u0007BaB,g\u000eZ(oYfl\u0015\r\u001d\t\u0003'Qa\u0001\u0001B\u0003\u0016\u0001\t\u0007qCA\u0001L\u0007\u0001\t\"\u0001\u0007\u0010\u0011\u0005eaR\"\u0001\u000e\u000b\u0003m\tQa]2bY\u0006L!!\b\u000e\u0003\u000f9{G\u000f[5oOB\u0011\u0011dH\u0005\u0003Ai\u00111!\u00118z!\t\u0019\"\u0005B\u0003$\u0001\t\u0007qCA\u0001W!\tyQ%\u0003\u0002'\u0005\tY1+\u001b>f)J\f7m[3s\u0011\u0015A\u0003\u0001\"\u0001*\u0003\u0019a\u0014N\\5u}Q\t!\u0006\u0005\u0003\u0010\u0001I\t\u0003\"\u0002\u0017\u0001\t\u0003j\u0013AB;qI\u0006$X\rF\u0002/cM\u0002\"!G\u0018\n\u0005AR\"\u0001B+oSRDQAM\u0016A\u0002I\t1a[3z\u0011\u0015!4\u00061\u0001\"\u0003\u00151\u0018\r\\;f\u0011\u00151\u0004\u0001\"\u00118\u0003-\u0019\u0007.\u00198hKZ\u000bG.^3\u0015\u0007\u0005B\u0014\bC\u00033k\u0001\u0007!\u0003C\u0003;k\u0001\u00071(\u0001\u0006va\u0012\fG/\u001a$v]\u000e\u0004R!\u0007\u001f?C\u0005J!!\u0010\u000e\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004CA\r@\u0013\t\u0001%DA\u0004C_>dW-\u00198\t\u000b\t\u0003A\u0011K\"\u0002\u0013\u001d\u0014xn\u001e+bE2,G#\u0001\u0018"
)
public class SizeTrackingAppendOnlyMap<K, V> extends AppendOnlyMap<K, V> implements SizeTracker {
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

   public void org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate_$eq(double x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate = x$1;
   }

   public long org$apache$spark$util$collection$SizeTracker$$numUpdates() {
      return this.org$apache$spark$util$collection$SizeTracker$$numUpdates;
   }

   public void org$apache$spark$util$collection$SizeTracker$$numUpdates_$eq(long x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$numUpdates = x$1;
   }

   public long org$apache$spark$util$collection$SizeTracker$$nextSampleNum() {
      return this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum;
   }

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

   public void update(K key, V value) {
      super.update(key, value);
      SizeTracker$class.afterUpdate(this);
   }

   public V changeValue(K key, Function2<Object, V, V> updateFunc) {
      Object newValue = super.changeValue(key, updateFunc);
      SizeTracker$class.afterUpdate(this);
      return (V)newValue;
   }

   public void growTable() {
      super.growTable();
      this.resetSamples();
   }

   public SizeTrackingAppendOnlyMap() {
      super(AppendOnlyMap$.MODULE$.$lessinit$greater$default$1());
      SizeTracker$class.$init$(this);
   }
}
