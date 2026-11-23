package org.apache.spark.rdd;

import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.NumericRange;
import scala.collection.immutable.Range;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ClassTag;
import scala.runtime.BoxedUnit;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;

public final class ParallelCollectionRDD$ implements Serializable {
   public static final ParallelCollectionRDD$ MODULE$;

   static {
      new ParallelCollectionRDD$();
   }

   public <T> Seq<Seq<T>> slice(Seq<T> seq, int numSlices, ClassTag<T> evidence$3) {
      if (numSlices < 1) {
         throw new IllegalArgumentException("Positive number of partitions required");
      } else {
         Object var6;
         if (seq instanceof Range) {
            Range var5 = (Range)seq;
            var6 = this.positions$1((long)var5.length(), numSlices).zipWithIndex().map(new Serializable(numSlices, var5) {
               public static final long serialVersionUID = 0L;
               private final int numSlices$1;
               private final Range x2$1;

               public final Range apply(Tuple2<Tuple2<Object, Object>, Object> x0$1) {
                  if (x0$1 != null) {
                     Tuple2 var3 = (Tuple2)x0$1._1();
                     int index = x0$1._2$mcI$sp();
                     if (var3 != null) {
                        int start = var3._1$mcI$sp();
                        int end = var3._2$mcI$sp();
                        Object var7 = this.x2$1.isInclusive() && index == this.numSlices$1 - 1 ? new Range.Inclusive(this.x2$1.start() + start * this.x2$1.step(), this.x2$1.end(), this.x2$1.step()) : new Range(this.x2$1.start() + start * this.x2$1.step(), this.x2$1.start() + end * this.x2$1.step(), this.x2$1.step());
                        return (Range)var7;
                     }
                  }

                  throw new MatchError(x0$1);
               }

               public {
                  this.numSlices$1 = numSlices$1;
                  this.x2$1 = x2$1;
               }
            }).toSeq();
         } else if (seq instanceof NumericRange) {
            NumericRange var7 = (NumericRange)seq;
            ArrayBuffer slices = new ArrayBuffer(numSlices);
            ObjectRef r = ObjectRef.create(var7);
            this.positions$1((long)var7.length(), numSlices).withFilter(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Tuple2<Object, Object> check$ifrefutable$1) {
                  boolean var3;
                  if (check$ifrefutable$1 != null) {
                     var3 = true;
                  } else {
                     var3 = false;
                  }

                  return var3;
               }
            }).foreach(new Serializable(slices, r) {
               public static final long serialVersionUID = 0L;
               private final ArrayBuffer slices$2;
               private final ObjectRef r$1;

               public final void apply(Tuple2<Object, Object> x$2) {
                  if (x$2 != null) {
                     int start = x$2._1$mcI$sp();
                     int end = x$2._2$mcI$sp();
                     int sliceSize = end - start;
                     this.slices$2.$plus$eq(((NumericRange)this.r$1.elem).take(sliceSize));
                     this.r$1.elem = (T)((NumericRange)this.r$1.elem).drop(sliceSize);
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     throw new MatchError(x$2);
                  }
               }

               public {
                  this.slices$2 = slices$2;
                  this.r$1 = r$1;
               }
            });
            var6 = slices;
         } else {
            Object array = seq.toArray(evidence$3);
            var6 = this.positions$1((long)ScalaRunTime$.MODULE$.array_length(array), numSlices).map(new Serializable(array) {
               public static final long serialVersionUID = 0L;
               private final Object array$1;

               public final Seq<T> apply(Tuple2<Object, Object> x0$2) {
                  if (x0$2 != null) {
                     int start = x0$2._1$mcI$sp();
                     int end = x0$2._2$mcI$sp();
                     Seq var5 = Predef$.MODULE$.genericArrayOps(Predef$.MODULE$.genericArrayOps(this.array$1).slice(start, end)).toSeq();
                     return var5;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }

               public {
                  this.array$1 = array$1;
               }
            }).toSeq();
         }

         return (Seq<Seq<T>>)var6;
      }
   }

   private Object readResolve() {
      return MODULE$;
   }

   private final Iterator positions$1(long length, int numSlices) {
      return RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numSlices).iterator().map(new Serializable(length, numSlices) {
         public static final long serialVersionUID = 0L;
         private final long length$1;
         private final int numSlices$2;

         public final Tuple2<Object, Object> apply(int i) {
            int start = (int)((long)i * this.length$1 / (long)this.numSlices$2);
            int end = (int)((long)(i + 1) * this.length$1 / (long)this.numSlices$2);
            return new Tuple2$mcII$sp(start, end);
         }

         public {
            this.length$1 = length$1;
            this.numSlices$2 = numSlices$2;
         }
      });
   }

   private ParallelCollectionRDD$() {
      MODULE$ = this;
   }
}
