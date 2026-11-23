package org.apache.spark;

import org.apache.spark.rdd.RDD;
import org.apache.spark.util.random.SamplingUtils$;
import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.TraversableOnce;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;

public final class RangePartitioner$ implements Serializable {
   public static final RangePartitioner$ MODULE$;

   static {
      new RangePartitioner$();
   }

   public <K> Tuple2<Object, Tuple3<Object, Object, Object>[]> sketch(RDD<K> rdd, int sampleSizePerPartition, ClassTag<K> evidence$5) {
      int shift = rdd.id();
      Tuple3[] sketched = (Tuple3[])rdd.mapPartitionsWithIndex(new Serializable(sampleSizePerPartition, evidence$5, shift) {
         public static final long serialVersionUID = 0L;
         private final int sampleSizePerPartition$2;
         private final ClassTag evidence$5$1;
         private final int shift$1;

         public final Iterator<Tuple3<Object, Object, Object>> apply(int idx, Iterator<K> iter) {
            int seed = scala.util.hashing.package$.MODULE$.byteswap32(idx ^ this.shift$1 << 16);
            Tuple2 var5 = SamplingUtils$.MODULE$.reservoirSampleAndCount(iter, this.sampleSizePerPartition$2, (long)seed, this.evidence$5$1);
            if (var5 != null) {
               Object sample = var5._1();
               long n = var5._2$mcJ$sp();
               Tuple2 var9 = new Tuple2(sample, BoxesRunTime.boxToLong(n));
               Object sample = var9._1();
               long n = var9._2$mcJ$sp();
               return scala.package$.MODULE$.Iterator().<Tuple3<Object, Object, Object>>apply(Predef$.MODULE$.wrapRefArray(new Tuple3[]{new Tuple3(BoxesRunTime.boxToInteger(idx), BoxesRunTime.boxToLong(n), sample)}));
            } else {
               throw new MatchError(var5);
            }
         }

         public {
            this.sampleSizePerPartition$2 = sampleSizePerPartition$2;
            this.evidence$5$1 = evidence$5$1;
            this.shift$1 = shift$1;
         }
      }, rdd.mapPartitionsWithIndex$default$2(), ClassTag$.MODULE$.apply(Tuple3.class)).collect();
      long numItems = BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(sketched).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(Tuple3<Object, Object, Object> x$10) {
            return BoxesRunTime.unboxToLong(x$10._2());
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
      return new Tuple2<Object, Tuple3<Object, Object, Object>[]>(BoxesRunTime.boxToLong(numItems), sketched);
   }

   public <K> Object determineBounds(ArrayBuffer<Tuple2<K, Object>> candidates, int partitions, Ordering<K> evidence$6, ClassTag<K> evidence$7) {
      Ordering ordering = (Ordering)Predef$.MODULE$.implicitly(evidence$6);
      ArrayBuffer ordered = (ArrayBuffer)candidates.sortBy(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final K apply(Tuple2<K, Object> x$11) {
            return (K)x$11._1();
         }
      }, evidence$6);
      int numCandidates = ordered.size();
      double sumWeights = BoxesRunTime.unboxToDouble(((TraversableOnce)ordered.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final double apply(Tuple2<K, Object> x$12) {
            return (double)BoxesRunTime.unboxToFloat(x$12._2());
         }
      }, ArrayBuffer$.MODULE$.canBuildFrom())).sum(Numeric.DoubleIsFractional$.MODULE$));
      double step = sumWeights / (double)partitions;
      double cumWeight = (double)0.0F;
      double target = step;
      ArrayBuffer bounds = (ArrayBuffer)ArrayBuffer$.MODULE$.empty();
      int i = 0;
      int j = 0;

      for(Option previousBound = Option$.MODULE$.empty(); i < numCandidates && j < partitions - 1; ++i) {
         Tuple2 var21 = (Tuple2)ordered.apply(i);
         if (var21 == null) {
            throw new MatchError(var21);
         }

         Object key = var21._1();
         float weight = BoxesRunTime.unboxToFloat(var21._2());
         Tuple2 var24 = new Tuple2(key, BoxesRunTime.boxToFloat(weight));
         Object key = var24._1();
         float weight = BoxesRunTime.unboxToFloat(var24._2());
         cumWeight += (double)weight;
         if (cumWeight >= target && (previousBound.isEmpty() || ordering.gt(key, previousBound.get()))) {
            bounds.$plus$eq(key);
            target += step;
            ++j;
            previousBound = new Some(key);
         }
      }

      return bounds.toArray(evidence$7);
   }

   public <K, V> boolean $lessinit$greater$default$3() {
      return true;
   }

   public <K, V> int $lessinit$greater$default$4() {
      return 20;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RangePartitioner$() {
      MODULE$ = this;
   }
}
