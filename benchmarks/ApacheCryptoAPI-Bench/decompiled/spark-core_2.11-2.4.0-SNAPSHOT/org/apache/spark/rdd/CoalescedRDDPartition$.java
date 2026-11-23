package org.apache.spark.rdd;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple4;
import scala.runtime.AbstractFunction4;
import scala.runtime.BoxesRunTime;

public final class CoalescedRDDPartition$ extends AbstractFunction4<Object, RDD<?>, int[], Option<String>, CoalescedRDDPartition> implements Serializable {
   public static final CoalescedRDDPartition$ MODULE$;

   static {
      new CoalescedRDDPartition$();
   }

   public final String toString() {
      return "CoalescedRDDPartition";
   }

   public CoalescedRDDPartition apply(int index, RDD<?> rdd, int[] parentsIndices, Option<String> preferredLocation) {
      return new CoalescedRDDPartition(index, rdd, parentsIndices, preferredLocation);
   }

   public Option<Tuple4<Object, RDD<Object>, int[], Option<String>>> unapply(CoalescedRDDPartition x$0) {
      return (Option<Tuple4<Object, RDD<Object>, int[], Option<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple4(BoxesRunTime.boxToInteger(x$0.index()), x$0.rdd(), x$0.parentsIndices(), x$0.preferredLocation())));
   }

   public Option<String> $lessinit$greater$default$4() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$4() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CoalescedRDDPartition$() {
      MODULE$ = this;
   }
}
