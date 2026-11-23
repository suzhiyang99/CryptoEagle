package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class SparkListenerUnpersistRDD$ extends AbstractFunction1<Object, SparkListenerUnpersistRDD> implements Serializable {
   public static final SparkListenerUnpersistRDD$ MODULE$;

   static {
      new SparkListenerUnpersistRDD$();
   }

   public final String toString() {
      return "SparkListenerUnpersistRDD";
   }

   public SparkListenerUnpersistRDD apply(int rddId) {
      return new SparkListenerUnpersistRDD(rddId);
   }

   public Option<Object> unapply(SparkListenerUnpersistRDD x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.rddId())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerUnpersistRDD$() {
      MODULE$ = this;
   }
}
