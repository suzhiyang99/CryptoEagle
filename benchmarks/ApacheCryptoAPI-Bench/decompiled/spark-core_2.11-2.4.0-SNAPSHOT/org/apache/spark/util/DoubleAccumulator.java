package org.apache.spark.util;

import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u00014A!\u0001\u0002\u0001\u0017\t\tBi\\;cY\u0016\f5mY;nk2\fGo\u001c:\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0003\u000e\u001dA\u0001R\"\u0001\u0002\n\u0005=\u0011!!D!dGVlW\u000f\\1u_J4&\u0007\u0005\u0002\u0012-5\t!C\u0003\u0002\u0014)\u0005!A.\u00198h\u0015\u0005)\u0012\u0001\u00026bm\u0006L!a\u0006\n\u0003\r\u0011{WO\u00197f\u0011\u0015I\u0002\u0001\"\u0001\u001b\u0003\u0019a\u0014N\\5u}Q\t1\u0004\u0005\u0002\u000e\u0001!9Q\u0004\u0001a\u0001\n\u0013q\u0012\u0001B0tk6,\u0012a\b\t\u0003A\rj\u0011!\t\u0006\u0002E\u0005)1oY1mC&\u0011q#\t\u0005\bK\u0001\u0001\r\u0011\"\u0003'\u0003!y6/^7`I\u0015\fHCA\u0014+!\t\u0001\u0003&\u0003\u0002*C\t!QK\\5u\u0011\u001dYC%!AA\u0002}\t1\u0001\u001f\u00132\u0011\u0019i\u0003\u0001)Q\u0005?\u0005)ql];nA!9q\u0006\u0001a\u0001\n\u0013\u0001\u0014AB0d_VtG/F\u00012!\t\u0001#'\u0003\u00024C\t!Aj\u001c8h\u0011\u001d)\u0004\u00011A\u0005\nY\n!bX2pk:$x\fJ3r)\t9s\u0007C\u0004,i\u0005\u0005\t\u0019A\u0019\t\re\u0002\u0001\u0015)\u00032\u0003\u001dy6m\\;oi\u0002BQa\u000f\u0001\u0005Bq\na![:[KJ|W#A\u001f\u0011\u0005\u0001r\u0014BA \"\u0005\u001d\u0011un\u001c7fC:DQ!\u0011\u0001\u0005Bi\tAaY8qs\")1\t\u0001C!\t\u0006)!/Z:fiR\tq\u0005C\u0003G\u0001\u0011\u0005s)A\u0002bI\u0012$\"a\n%\t\u000b%+\u0005\u0019\u0001\t\u0002\u0003YDQA\u0012\u0001\u0005\u0002-#\"a\n'\t\u000b%S\u0005\u0019A\u0010\t\u000b9\u0003A\u0011\u0001\u0019\u0002\u000b\r|WO\u001c;\t\u000bA\u0003A\u0011\u0001\u0010\u0002\u0007M,X\u000eC\u0003S\u0001\u0011\u0005a$A\u0002bm\u001eDQ\u0001\u0016\u0001\u0005BU\u000bQ!\\3sO\u0016$\"a\n,\t\u000b]\u001b\u0006\u0019\u0001\u0007\u0002\u000b=$\b.\u001a:\t\re\u0003A\u0011\u0001\u0003[\u0003!\u0019X\r\u001e,bYV,GCA\u0014\\\u0011\u0015a\u0006\f1\u0001 \u0003!qWm\u001e,bYV,\u0007\"\u00020\u0001\t\u0003z\u0016!\u0002<bYV,W#\u0001\t"
)
public class DoubleAccumulator extends AccumulatorV2<Double, Double> {
   private double _sum = (double)0.0F;
   private long _count = 0L;

   private double _sum() {
      return this._sum;
   }

   private void _sum_$eq(double x$1) {
      this._sum = x$1;
   }

   private long _count() {
      return this._count;
   }

   private void _count_$eq(long x$1) {
      this._count = x$1;
   }

   public boolean isZero() {
      return this._sum() == (double)0.0F && this._count() == 0L;
   }

   public DoubleAccumulator copy() {
      DoubleAccumulator newAcc = new DoubleAccumulator();
      newAcc._count_$eq(this._count());
      newAcc._sum_$eq(this._sum());
      return newAcc;
   }

   public void reset() {
      this._sum_$eq((double)0.0F);
      this._count_$eq(0L);
   }

   public void add(Double v) {
      this._sum_$eq(this._sum() + Predef$.MODULE$.Double2double(v));
      this._count_$eq(this._count() + 1L);
   }

   public void add(double v) {
      this._sum_$eq(this._sum() + v);
      this._count_$eq(this._count() + 1L);
   }

   public long count() {
      return this._count();
   }

   public double sum() {
      return this._sum();
   }

   public double avg() {
      return this._sum() / (double)this._count();
   }

   public void merge(AccumulatorV2<Double, Double> other) {
      if (other instanceof DoubleAccumulator) {
         DoubleAccumulator var3 = (DoubleAccumulator)other;
         this._sum_$eq(this._sum() + var3.sum());
         this._count_$eq(this._count() + var3.count());
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         throw new UnsupportedOperationException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot merge ", " with ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.getClass().getName(), other.getClass().getName()})));
      }
   }

   public void setValue(double newValue) {
      this._sum_$eq(newValue);
   }

   public Double value() {
      return Predef$.MODULE$.double2Double(this._sum());
   }
}
