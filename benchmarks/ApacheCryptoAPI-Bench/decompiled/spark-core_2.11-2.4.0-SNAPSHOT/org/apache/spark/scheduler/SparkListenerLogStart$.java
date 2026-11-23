package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class SparkListenerLogStart$ extends AbstractFunction1<String, SparkListenerLogStart> implements Serializable {
   public static final SparkListenerLogStart$ MODULE$;

   static {
      new SparkListenerLogStart$();
   }

   public final String toString() {
      return "SparkListenerLogStart";
   }

   public SparkListenerLogStart apply(String sparkVersion) {
      return new SparkListenerLogStart(sparkVersion);
   }

   public Option<String> unapply(SparkListenerLogStart x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.sparkVersion()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerLogStart$() {
      MODULE$ = this;
   }
}
