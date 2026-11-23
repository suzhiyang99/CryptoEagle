package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class SparkListenerApplicationEnd$ extends AbstractFunction1<Object, SparkListenerApplicationEnd> implements Serializable {
   public static final SparkListenerApplicationEnd$ MODULE$;

   static {
      new SparkListenerApplicationEnd$();
   }

   public final String toString() {
      return "SparkListenerApplicationEnd";
   }

   public SparkListenerApplicationEnd apply(long time) {
      return new SparkListenerApplicationEnd(time);
   }

   public Option<Object> unapply(SparkListenerApplicationEnd x$0) {
      return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToLong(x$0.time())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerApplicationEnd$() {
      MODULE$ = this;
   }
}
