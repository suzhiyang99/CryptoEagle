package org.apache.spark;

import scala.Function1;
import scala.Function2;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;

public final class Aggregator$ implements Serializable {
   public static final Aggregator$ MODULE$;

   static {
      new Aggregator$();
   }

   public final String toString() {
      return "Aggregator";
   }

   public <K, V, C> Aggregator<K, V, C> apply(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners) {
      return new Aggregator<K, V, C>(createCombiner, mergeValue, mergeCombiners);
   }

   public <K, V, C> Option<Tuple3<Function1<V, C>, Function2<C, V, C>, Function2<C, C, C>>> unapply(Aggregator<K, V, C> x$0) {
      return (Option<Tuple3<Function1<V, C>, Function2<C, V, C>, Function2<C, C, C>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.createCombiner(), x$0.mergeValue(), x$0.mergeCombiners())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Aggregator$() {
      MODULE$ = this;
   }
}
