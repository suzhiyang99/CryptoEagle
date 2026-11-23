package org.apache.spark.api.java;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDD$;
import scala.Function1;
import scala.Function2;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import scala.collection.JavaConverters$;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.Null$;

public final class JavaPairRDD$ implements Serializable {
   public static final JavaPairRDD$ MODULE$;

   static {
      new JavaPairRDD$();
   }

   public <K, T> RDD<Tuple2<K, Iterable<T>>> groupByResultToJava(RDD<Tuple2<K, scala.collection.Iterable<T>>> rdd, ClassTag<K> evidence$1) {
      ClassTag x$320 = ClassTag$.MODULE$.apply(scala.collection.Iterable.class);
      RDD$.MODULE$.rddToPairRDDFunctions$default$4(rdd);
      Null$ x$321 = null;
      return RDD$.MODULE$.rddToPairRDDFunctions(rdd, evidence$1, x$320, (Ordering)null).mapValues(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Iterable<T> apply(scala.collection.Iterable<T> x$5) {
            return (Iterable)JavaConverters$.MODULE$.asJavaIterableConverter(x$5).asJava();
         }
      });
   }

   public <K, V, W> RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> cogroupResultToJava(RDD<Tuple2<K, Tuple2<scala.collection.Iterable<V>, scala.collection.Iterable<W>>>> rdd, ClassTag<K> evidence$2) {
      ClassTag x$324 = ClassTag$.MODULE$.apply(Tuple2.class);
      RDD$.MODULE$.rddToPairRDDFunctions$default$4(rdd);
      Null$ x$325 = null;
      return RDD$.MODULE$.rddToPairRDDFunctions(rdd, evidence$2, x$324, (Ordering)null).mapValues(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Iterable<V>, Iterable<W>> apply(Tuple2<scala.collection.Iterable<V>, scala.collection.Iterable<W>> x) {
            return new Tuple2<Iterable<V>, Iterable<W>>(JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._1()).asJava(), JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._2()).asJava());
         }
      });
   }

   public <K, V, W1, W2> RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> cogroupResult2ToJava(RDD<Tuple2<K, Tuple3<scala.collection.Iterable<V>, scala.collection.Iterable<W1>, scala.collection.Iterable<W2>>>> rdd, ClassTag<K> evidence$3) {
      ClassTag x$328 = ClassTag$.MODULE$.apply(Tuple3.class);
      RDD$.MODULE$.rddToPairRDDFunctions$default$4(rdd);
      Null$ x$329 = null;
      return RDD$.MODULE$.rddToPairRDDFunctions(rdd, evidence$3, x$328, (Ordering)null).mapValues(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>> apply(Tuple3<scala.collection.Iterable<V>, scala.collection.Iterable<W1>, scala.collection.Iterable<W2>> x) {
            return new Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>(JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._1()).asJava(), JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._2()).asJava(), JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._3()).asJava());
         }
      });
   }

   public <K, V, W1, W2, W3> RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> cogroupResult3ToJava(RDD<Tuple2<K, Tuple4<scala.collection.Iterable<V>, scala.collection.Iterable<W1>, scala.collection.Iterable<W2>, scala.collection.Iterable<W3>>>> rdd, ClassTag<K> evidence$4) {
      ClassTag x$332 = ClassTag$.MODULE$.apply(Tuple4.class);
      RDD$.MODULE$.rddToPairRDDFunctions$default$4(rdd);
      Null$ x$333 = null;
      return RDD$.MODULE$.rddToPairRDDFunctions(rdd, evidence$4, x$332, (Ordering)null).mapValues(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>> apply(Tuple4<scala.collection.Iterable<V>, scala.collection.Iterable<W1>, scala.collection.Iterable<W2>, scala.collection.Iterable<W3>> x) {
            return new Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>(JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._1()).asJava(), JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._2()).asJava(), JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._3()).asJava(), JavaConverters$.MODULE$.asJavaIterableConverter((scala.collection.Iterable)x._4()).asJava());
         }
      });
   }

   public <K, V> JavaPairRDD<K, V> fromRDD(RDD<Tuple2<K, V>> rdd, ClassTag<K> evidence$5, ClassTag<V> evidence$6) {
      return new JavaPairRDD<K, V>(rdd, evidence$5, evidence$6);
   }

   public <K, V> RDD<Tuple2<K, V>> toRDD(JavaPairRDD<K, V> rdd) {
      return rdd.rdd();
   }

   public <T1, T2, R> Function2<T1, T2, R> toScalaFunction2(org.apache.spark.api.java.function.Function2<T1, T2, R> fun) {
      return new Serializable(fun) {
         public static final long serialVersionUID = 0L;
         private final org.apache.spark.api.java.function.Function2 fun$2;

         public final R apply(T1 x, T2 x1) {
            return (R)this.fun$2.call(x, x1);
         }

         public {
            this.fun$2 = fun$2;
         }
      };
   }

   public <T, R> Function1<T, R> toScalaFunction(Function<T, R> fun) {
      return new Serializable(fun) {
         public static final long serialVersionUID = 0L;
         private final Function fun$1;

         public final R apply(T x) {
            return (R)this.fun$1.call(x);
         }

         public {
            this.fun$1 = fun$1;
         }
      };
   }

   public <A, B, C> Function1<A, Tuple2<B, C>> pairFunToScalaFun(PairFunction<A, B, C> x) {
      return new Serializable(x) {
         public static final long serialVersionUID = 0L;
         private final PairFunction x$334;

         public final Tuple2<B, C> apply(A y) {
            return this.x$334.call(y);
         }

         public {
            this.x$334 = x$334;
         }
      };
   }

   public <K, V> JavaPairRDD<K, V> fromJavaRDD(JavaRDD<Tuple2<K, V>> rdd) {
      ClassTag ctagK = JavaSparkContext$.MODULE$.fakeClassTag();
      ClassTag ctagV = JavaSparkContext$.MODULE$.fakeClassTag();
      return new JavaPairRDD<K, V>(rdd.rdd(), ctagK, ctagV);
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JavaPairRDD$() {
      MODULE$ = this;
   }
}
