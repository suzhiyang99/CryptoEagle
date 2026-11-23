package org.apache.spark.util;

import org.apache.spark.AccumulableParam;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001U3A!\u0001\u0002\u0001\u0017\tAB*Z4bGf\f5mY;nk2\fGo\u001c:Xe\u0006\u0004\b/\u001a:\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001+\ra\u0001eE\n\u0003\u00015\u0001BAD\b\u0012?5\t!!\u0003\u0002\u0011\u0005\ti\u0011iY2v[Vd\u0017\r^8s-J\u0002\"AE\n\r\u0001\u0011)A\u0003\u0001b\u0001+\t\tA+\u0005\u0002\u00179A\u0011qCG\u0007\u00021)\t\u0011$A\u0003tG\u0006d\u0017-\u0003\u0002\u001c1\t9aj\u001c;iS:<\u0007CA\f\u001e\u0013\tq\u0002DA\u0002B]f\u0004\"A\u0005\u0011\u0005\u000b\u0005\u0002!\u0019A\u000b\u0003\u0003IC\u0001b\t\u0001\u0003\u0002\u0003\u0006IaH\u0001\rS:LG/[1m-\u0006dW/\u001a\u0005\tK\u0001\u0011\t\u0011)A\u0005M\u0005)\u0001/\u0019:b[B!q\u0005K\u0010\u0012\u001b\u0005!\u0011BA\u0015\u0005\u0005A\t5mY;nk2\f'\r\\3QCJ\fW\u000eC\u0003,\u0001\u0011\u0005A&\u0001\u0004=S:LGO\u0010\u000b\u0004[9z\u0003\u0003\u0002\b\u0001?EAQa\t\u0016A\u0002}AQ!\n\u0016A\u0002\u0019B\u0001\"\r\u0001A\u0002\u0013\u0005AAM\u0001\u0007?Z\fG.^3\u0016\u0003}A\u0001\u0002\u000e\u0001A\u0002\u0013\u0005A!N\u0001\u000b?Z\fG.^3`I\u0015\fHC\u0001\u001c:!\t9r'\u0003\u000291\t!QK\\5u\u0011\u001dQ4'!AA\u0002}\t1\u0001\u001f\u00132\u0011\u0019a\u0004\u0001)Q\u0005?\u00059qL^1mk\u0016\u0004\u0003\"\u0002 \u0001\t\u0003z\u0014AB5t5\u0016\u0014x.F\u0001A!\t9\u0012)\u0003\u0002C1\t9!i\\8mK\u0006t\u0007\"\u0002#\u0001\t\u0003*\u0015\u0001B2paf$\u0012!\f\u0005\u0006\u000f\u0002!\t\u0005S\u0001\u0006e\u0016\u001cX\r\u001e\u000b\u0002m!)!\n\u0001C!\u0017\u0006\u0019\u0011\r\u001a3\u0015\u0005Yb\u0005\"B'J\u0001\u0004\t\u0012!\u0001<\t\u000b=\u0003A\u0011\t)\u0002\u000b5,'oZ3\u0015\u0005Y\n\u0006\"\u0002*O\u0001\u0004i\u0011!B8uQ\u0016\u0014\b\"\u0002+\u0001\t\u0003\u0012\u0014!\u0002<bYV,\u0007"
)
public class LegacyAccumulatorWrapper<R, T> extends AccumulatorV2<T, R> {
   private final R initialValue;
   private final AccumulableParam<R, T> param;
   private R _value;

   public R _value() {
      return this._value;
   }

   public void _value_$eq(R x$1) {
      this._value = (R)x$1;
   }

   public boolean isZero() {
      return BoxesRunTime.equals(this._value(), this.param.zero(this.initialValue));
   }

   public LegacyAccumulatorWrapper<R, T> copy() {
      LegacyAccumulatorWrapper acc = new LegacyAccumulatorWrapper(this.initialValue, this.param);
      acc._value_$eq(this._value());
      return acc;
   }

   public void reset() {
      this._value_$eq(this.param.zero(this.initialValue));
   }

   public void add(T v) {
      this._value_$eq(this.param.addAccumulator(this._value(), v));
   }

   public void merge(AccumulatorV2<T, R> other) {
      if (other instanceof LegacyAccumulatorWrapper) {
         LegacyAccumulatorWrapper var3 = (LegacyAccumulatorWrapper)other;
         this._value_$eq(this.param.addInPlace(this._value(), var3.value()));
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         throw new UnsupportedOperationException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot merge ", " with ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.getClass().getName(), other.getClass().getName()})));
      }
   }

   public R value() {
      return (R)this._value();
   }

   public LegacyAccumulatorWrapper(R initialValue, AccumulableParam<R, T> param) {
      this.initialValue = (R)initialValue;
      this.param = param;
      this._value = (R)initialValue;
   }
}
