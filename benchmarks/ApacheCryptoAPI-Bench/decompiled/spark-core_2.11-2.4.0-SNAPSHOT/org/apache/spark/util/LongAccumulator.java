package org.apache.spark.util;

import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u00014A!\u0001\u0002\u0001\u0017\tyAj\u001c8h\u0003\u000e\u001cW/\\;mCR|'O\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u0001B!\u0004\b\u0011!5\t!!\u0003\u0002\u0010\u0005\ti\u0011iY2v[Vd\u0017\r^8s-J\u0002\"!\u0005\f\u000e\u0003IQ!a\u0005\u000b\u0002\t1\fgn\u001a\u0006\u0002+\u0005!!.\u0019<b\u0013\t9\"C\u0001\u0003M_:<\u0007\"B\r\u0001\t\u0003Q\u0012A\u0002\u001fj]&$h\bF\u0001\u001c!\ti\u0001\u0001C\u0004\u001e\u0001\u0001\u0007I\u0011\u0002\u0010\u0002\t}\u001bX/\\\u000b\u0002?A\u0011\u0001eI\u0007\u0002C)\t!%A\u0003tG\u0006d\u0017-\u0003\u0002\u0018C!9Q\u0005\u0001a\u0001\n\u00131\u0013\u0001C0tk6|F%Z9\u0015\u0005\u001dR\u0003C\u0001\u0011)\u0013\tI\u0013E\u0001\u0003V]&$\bbB\u0016%\u0003\u0003\u0005\raH\u0001\u0004q\u0012\n\u0004BB\u0017\u0001A\u0003&q$A\u0003`gVl\u0007\u0005C\u00040\u0001\u0001\u0007I\u0011\u0002\u0010\u0002\r}\u001bw.\u001e8u\u0011\u001d\t\u0004\u00011A\u0005\nI\n!bX2pk:$x\fJ3r)\t93\u0007C\u0004,a\u0005\u0005\t\u0019A\u0010\t\rU\u0002\u0001\u0015)\u0003 \u0003\u001dy6m\\;oi\u0002BQa\u000e\u0001\u0005Ba\na![:[KJ|W#A\u001d\u0011\u0005\u0001R\u0014BA\u001e\"\u0005\u001d\u0011un\u001c7fC:DQ!\u0010\u0001\u0005Bi\tAaY8qs\")q\b\u0001C!\u0001\u0006)!/Z:fiR\tq\u0005C\u0003C\u0001\u0011\u00053)A\u0002bI\u0012$\"a\n#\t\u000b\u0015\u000b\u0005\u0019\u0001\t\u0002\u0003YDQA\u0011\u0001\u0005\u0002\u001d#\"a\n%\t\u000b\u00153\u0005\u0019A\u0010\t\u000b)\u0003A\u0011\u0001\u0010\u0002\u000b\r|WO\u001c;\t\u000b1\u0003A\u0011\u0001\u0010\u0002\u0007M,X\u000eC\u0003O\u0001\u0011\u0005q*A\u0002bm\u001e,\u0012\u0001\u0015\t\u0003AEK!AU\u0011\u0003\r\u0011{WO\u00197f\u0011\u0015!\u0006\u0001\"\u0011V\u0003\u0015iWM]4f)\t9c\u000bC\u0003X'\u0002\u0007A\"A\u0003pi\",'\u000f\u0003\u0004Z\u0001\u0011\u0005AAW\u0001\tg\u0016$h+\u00197vKR\u0011qe\u0017\u0005\u00069b\u0003\raH\u0001\t]\u0016<h+\u00197vK\")a\f\u0001C!?\u0006)a/\u00197vKV\t\u0001\u0003"
)
public class LongAccumulator extends AccumulatorV2<Long, Long> {
   private long _sum = 0L;
   private long _count = 0L;

   private long _sum() {
      return this._sum;
   }

   private void _sum_$eq(long x$1) {
      this._sum = x$1;
   }

   private long _count() {
      return this._count;
   }

   private void _count_$eq(long x$1) {
      this._count = x$1;
   }

   public boolean isZero() {
      return this._sum() == 0L && this._count() == 0L;
   }

   public LongAccumulator copy() {
      LongAccumulator newAcc = new LongAccumulator();
      newAcc._count_$eq(this._count());
      newAcc._sum_$eq(this._sum());
      return newAcc;
   }

   public void reset() {
      this._sum_$eq(0L);
      this._count_$eq(0L);
   }

   public void add(Long v) {
      this._sum_$eq(this._sum() + Predef$.MODULE$.Long2long(v));
      this._count_$eq(this._count() + 1L);
   }

   public void add(long v) {
      this._sum_$eq(this._sum() + v);
      this._count_$eq(this._count() + 1L);
   }

   public long count() {
      return this._count();
   }

   public long sum() {
      return this._sum();
   }

   public double avg() {
      return (double)this._sum() / (double)this._count();
   }

   public void merge(AccumulatorV2<Long, Long> other) {
      if (other instanceof LongAccumulator) {
         LongAccumulator var3 = (LongAccumulator)other;
         this._sum_$eq(this._sum() + var3.sum());
         this._count_$eq(this._count() + var3.count());
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         throw new UnsupportedOperationException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot merge ", " with ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.getClass().getName(), other.getClass().getName()})));
      }
   }

   public void setValue(long newValue) {
      this._sum_$eq(newValue);
   }

   public Long value() {
      return Predef$.MODULE$.long2Long(this._sum());
   }
}
