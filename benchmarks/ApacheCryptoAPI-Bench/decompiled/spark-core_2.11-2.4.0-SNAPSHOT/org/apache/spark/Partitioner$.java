package org.apache.spark;

import org.apache.spark.rdd.RDD;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.math.Ordering;
import scala.runtime.BoxesRunTime;

public final class Partitioner$ implements Serializable {
   public static final Partitioner$ MODULE$;

   static {
      new Partitioner$();
   }

   public Partitioner defaultPartitioner(RDD<?> rdd, Seq<RDD<?>> others) {
      Seq rdds = (Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{rdd}))).$plus$plus(others, Seq$.MODULE$.canBuildFrom());
      Seq hasPartitioner = (Seq)rdds.filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(RDD<?> x$1) {
            return x$1.partitioner().exists(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Partitioner x$2) {
                  return x$2.numPartitions() > 0;
               }
            });
         }
      });
      Option hasMaxPartitioner = (Option)(hasPartitioner.nonEmpty() ? new Some(hasPartitioner.maxBy(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(RDD<?> x$3) {
            return x$3.partitions().length;
         }
      }, Ordering.Int$.MODULE$)) : None$.MODULE$);
      int defaultNumPartitions = rdd.context().conf().contains("spark.default.parallelism") ? rdd.context().defaultParallelism() : BoxesRunTime.unboxToInt(((TraversableOnce)rdds.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(RDD<?> x$4) {
            return x$4.partitions().length;
         }
      }, Seq$.MODULE$.canBuildFrom())).max(Ordering.Int$.MODULE$));
      return (Partitioner)(!hasMaxPartitioner.nonEmpty() || !this.isEligiblePartitioner((RDD)hasMaxPartitioner.get(), rdds) && defaultNumPartitions >= ((RDD)hasMaxPartitioner.get()).getNumPartitions() ? new HashPartitioner(defaultNumPartitions) : (Partitioner)((RDD)hasMaxPartitioner.get()).partitioner().get());
   }

   private boolean isEligiblePartitioner(RDD<?> hasMaxPartitioner, Seq<RDD<?>> rdds) {
      int maxPartitions = BoxesRunTime.unboxToInt(((TraversableOnce)rdds.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(RDD<?> x$5) {
            return x$5.partitions().length;
         }
      }, Seq$.MODULE$.canBuildFrom())).max(Ordering.Int$.MODULE$));
      return scala.math.package$.MODULE$.log10((double)maxPartitions) - scala.math.package$.MODULE$.log10((double)hasMaxPartitioner.getNumPartitions()) < (double)1;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Partitioner$() {
      MODULE$ = this;
   }
}
