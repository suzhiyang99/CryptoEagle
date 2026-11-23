package org.apache.spark.rdd;

import org.apache.spark.WritableFactory;
import scala.Function1;
import scala.Serializable;
import scala.Tuple2;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.Null$;

public final class RDD$ implements Serializable {
   public static final RDD$ MODULE$;
   private final String CHECKPOINT_ALL_MARKED_ANCESTORS;

   static {
      new RDD$();
   }

   public String CHECKPOINT_ALL_MARKED_ANCESTORS() {
      return this.CHECKPOINT_ALL_MARKED_ANCESTORS;
   }

   public <K, V> PairRDDFunctions<K, V> rddToPairRDDFunctions(RDD<Tuple2<K, V>> rdd, ClassTag<K> kt, ClassTag<V> vt, Ordering<K> ord) {
      return new PairRDDFunctions<K, V>(rdd, kt, vt, ord);
   }

   public <K, V> Null$ rddToPairRDDFunctions$default$4(RDD<Tuple2<K, V>> rdd) {
      return null;
   }

   public <T> AsyncRDDActions<T> rddToAsyncRDDActions(RDD<T> rdd, ClassTag<T> evidence$34) {
      return new AsyncRDDActions<T>(rdd, evidence$34);
   }

   public <K, V> SequenceFileRDDFunctions<K, V> rddToSequenceFileRDDFunctions(RDD<Tuple2<K, V>> rdd, ClassTag<K> kt, ClassTag<V> vt, WritableFactory<K> keyWritableFactory, WritableFactory<V> valueWritableFactory) {
      Function1 keyConverter = keyWritableFactory.convert();
      Function1 valueConverter = valueWritableFactory.convert();
      return new SequenceFileRDDFunctions<K, V>(rdd, (Class)keyWritableFactory.writableClass().apply(kt), (Class)valueWritableFactory.writableClass().apply(vt), keyConverter, kt, valueConverter, vt);
   }

   public <K, V> OrderedRDDFunctions<K, V, Tuple2<K, V>> rddToOrderedRDDFunctions(RDD<Tuple2<K, V>> rdd, Ordering<K> evidence$35, ClassTag<K> evidence$36, ClassTag<V> evidence$37) {
      return new OrderedRDDFunctions<K, V, Tuple2<K, V>>(rdd, evidence$35, evidence$36, evidence$37, ClassTag$.MODULE$.apply(Tuple2.class));
   }

   public DoubleRDDFunctions doubleRDDToDoubleRDDFunctions(RDD<Object> rdd) {
      return new DoubleRDDFunctions(rdd);
   }

   public <T> DoubleRDDFunctions numericRDDToDoubleRDDFunctions(RDD<T> rdd, Numeric<T> num) {
      return new DoubleRDDFunctions(rdd.map(new Serializable(num) {
         public static final long serialVersionUID = 0L;
         private final Numeric num$1;

         public final double apply(T x) {
            return this.num$1.toDouble(x);
         }

         public {
            this.num$1 = num$1;
         }
      }, ClassTag$.MODULE$.Double()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RDD$() {
      MODULE$ = this;
      this.CHECKPOINT_ALL_MARKED_ANCESTORS = "spark.checkpoint.checkpointAllMarkedAncestors";
   }
}
