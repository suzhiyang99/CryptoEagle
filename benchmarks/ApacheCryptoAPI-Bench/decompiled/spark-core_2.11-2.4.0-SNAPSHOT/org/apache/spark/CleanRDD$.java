package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class CleanRDD$ extends AbstractFunction1<Object, CleanRDD> implements Serializable {
   public static final CleanRDD$ MODULE$;

   static {
      new CleanRDD$();
   }

   public final String toString() {
      return "CleanRDD";
   }

   public CleanRDD apply(int rddId) {
      return new CleanRDD(rddId);
   }

   public Option<Object> unapply(CleanRDD x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.rddId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CleanRDD$() {
      MODULE$ = this;
   }
}
