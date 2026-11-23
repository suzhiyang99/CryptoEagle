package org.apache.spark.rdd;

import org.apache.spark.Partition;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class NarrowCoGroupSplitDep$ extends AbstractFunction3<RDD<?>, Object, Partition, NarrowCoGroupSplitDep> implements Serializable {
   public static final NarrowCoGroupSplitDep$ MODULE$;

   static {
      new NarrowCoGroupSplitDep$();
   }

   public final String toString() {
      return "NarrowCoGroupSplitDep";
   }

   public NarrowCoGroupSplitDep apply(RDD<?> rdd, int splitIndex, Partition split) {
      return new NarrowCoGroupSplitDep(rdd, splitIndex, split);
   }

   public Option<Tuple3<RDD<Object>, Object, Partition>> unapply(NarrowCoGroupSplitDep x$0) {
      return (Option<Tuple3<RDD<Object>, Object, Partition>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.rdd(), BoxesRunTime.boxToInteger(x$0.splitIndex()), x$0.split())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private NarrowCoGroupSplitDep$() {
      MODULE$ = this;
   }
}
