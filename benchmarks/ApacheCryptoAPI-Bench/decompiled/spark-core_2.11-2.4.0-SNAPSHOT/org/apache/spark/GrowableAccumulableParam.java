package org.apache.spark;

import org.apache.spark.serializer.JavaSerializer;
import org.apache.spark.serializer.SerializerInstance;
import scala.Function1;
import scala.collection.TraversableOnce;
import scala.collection.generic.Growable;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

/** @deprecated */
@ScalaSignature(
   bytes = "\u0006\u0001Q4Q!\u0001\u0002\u0001\u0005!\u0011\u0001d\u0012:po\u0006\u0014G.Z!dGVlW\u000f\\1cY\u0016\u0004\u0016M]1n\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<WcA\u0005\u0017CM\u0019\u0001A\u0003\t\u0011\u0005-qQ\"\u0001\u0007\u000b\u00035\tQa]2bY\u0006L!a\u0004\u0007\u0003\r\u0005s\u0017PU3g!\u0011\t\"\u0003\u0006\u0011\u000e\u0003\tI!a\u0005\u0002\u0003!\u0005\u001b7-^7vY\u0006\u0014G.\u001a)be\u0006l\u0007CA\u000b\u0017\u0019\u0001!Qa\u0006\u0001C\u0002e\u0011\u0011AU\u0002\u0001#\tQR\u0004\u0005\u0002\f7%\u0011A\u0004\u0004\u0002\b\u001d>$\b.\u001b8h!\tYa$\u0003\u0002 \u0019\t\u0019\u0011I\\=\u0011\u0005U\tC!\u0002\u0012\u0001\u0005\u0004I\"!\u0001+\t\u0011\u0011\u0002!1!Q\u0001\f\u0015\n!\"\u001a<jI\u0016t7-\u001a\u00132!\r1\u0013\u0006F\u0007\u0002O)\u0011\u0001\u0006D\u0001\be\u00164G.Z2u\u0013\tQsE\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011!a\u0003A!A!\u0002\u0017i\u0013A\u0001:h!\u0011Ya\u0006\u0006\u0019\n\u0005=b!!\u0003$v]\u000e$\u0018n\u001c82%\u0011\t4gO$\u0007\tI\u0002\u0001\u0001\r\u0002\ryI,g-\u001b8f[\u0016tGO\u0010\t\u0004ie\u0002S\"A\u001b\u000b\u0005Y:\u0014aB4f]\u0016\u0014\u0018n\u0019\u0006\u0003q1\t!bY8mY\u0016\u001cG/[8o\u0013\tQTG\u0001\u0005He><\u0018M\u00197f!\raD\t\t\b\u0003{\ts!AP!\u000e\u0003}R!\u0001\u0011\r\u0002\rq\u0012xn\u001c;?\u0013\u0005i\u0011BA\"\r\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u0012$\u0003\u001fQ\u0013\u0018M^3sg\u0006\u0014G.Z(oG\u0016T!a\u0011\u0007\u0011\u0005!kU\"A%\u000b\u0005)[\u0015AA5p\u0015\u0005a\u0015\u0001\u00026bm\u0006L!AT%\u0003\u0019M+'/[1mSj\f'\r\\3\t\u000bA\u0003A\u0011A)\u0002\rqJg.\u001b;?)\u0005\u0011FcA*U+B!\u0011\u0003\u0001\u000b!\u0011\u0015!s\nq\u0001&\u0011\u0015as\nq\u0001W!\u0011Ya\u0006F,\u0013\ta\u001b4h\u0012\u0004\u0005e\u0001\u0001q\u000bC\u0003[\u0001\u0011\u00051,\u0001\bbI\u0012\f5mY;nk2\fGo\u001c:\u0015\u0007Qaf\fC\u0003^3\u0002\u0007A#\u0001\u0005he><\u0018M\u00197f\u0011\u0015y\u0016\f1\u0001!\u0003\u0011)G.Z7\t\u000b\u0005\u0004A\u0011\u00012\u0002\u0015\u0005$G-\u00138QY\u0006\u001cW\rF\u0002\u0015G\u0016DQ\u0001\u001a1A\u0002Q\t!\u0001^\u0019\t\u000b\u0019\u0004\u0007\u0019\u0001\u000b\u0002\u0005Q\u0014\u0004\"\u00025\u0001\t\u0003I\u0017\u0001\u0002>fe>$\"\u0001\u00066\t\u000b-<\u0007\u0019\u0001\u000b\u0002\u0019%t\u0017\u000e^5bYZ\u000bG.^3)\t\u0001i\u0007O\u001d\t\u0003\u00179L!a\u001c\u0007\u0003\u0015\u0011,\u0007O]3dCR,G-I\u0001r\u0003E)8/\u001a\u0011BG\u000e,X.\u001e7bi>\u0014hKM\u0011\u0002g\u0006)!G\f\u0019/a\u0001"
)
public class GrowableAccumulableParam<R, T> implements AccumulableParam<R, T> {
   private final ClassTag<R> evidence$1;
   private final Function1<R, Growable<T>> rg;

   public R addAccumulator(R growable, T elem) {
      (this.rg.apply(growable)).$plus$eq(elem);
      return (R)growable;
   }

   public R addInPlace(R t1, R t2) {
      (this.rg.apply(t1)).$plus$plus$eq((TraversableOnce)this.rg.apply(t2));
      return (R)t1;
   }

   public R zero(R initialValue) {
      SerializerInstance ser = (new JavaSerializer(new SparkConf(false))).newInstance();
      Object copy = ser.deserialize(ser.serialize(initialValue, this.evidence$1), this.evidence$1);
      ((Growable)this.rg.apply(copy)).clear();
      return (R)copy;
   }

   public GrowableAccumulableParam(ClassTag<R> evidence$1, Function1<R, Growable<T>> rg) {
      this.evidence$1 = evidence$1;
      this.rg = rg;
   }
}
