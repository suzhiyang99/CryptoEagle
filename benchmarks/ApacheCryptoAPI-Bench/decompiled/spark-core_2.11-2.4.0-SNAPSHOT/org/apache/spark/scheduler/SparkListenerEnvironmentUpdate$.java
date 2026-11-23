package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Map;
import scala.collection.Seq;
import scala.runtime.AbstractFunction1;

public final class SparkListenerEnvironmentUpdate$ extends AbstractFunction1<Map<String, Seq<Tuple2<String, String>>>, SparkListenerEnvironmentUpdate> implements Serializable {
   public static final SparkListenerEnvironmentUpdate$ MODULE$;

   static {
      new SparkListenerEnvironmentUpdate$();
   }

   public final String toString() {
      return "SparkListenerEnvironmentUpdate";
   }

   public SparkListenerEnvironmentUpdate apply(Map<String, Seq<Tuple2<String, String>>> environmentDetails) {
      return new SparkListenerEnvironmentUpdate(environmentDetails);
   }

   public Option<Map<String, Seq<Tuple2<String, String>>>> unapply(SparkListenerEnvironmentUpdate x$0) {
      return (Option<Map<String, Seq<Tuple2<String, String>>>>)(x$0 == null ? None$.MODULE$ : new Some(x$0.environmentDetails()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerEnvironmentUpdate$() {
      MODULE$ = this;
   }
}
