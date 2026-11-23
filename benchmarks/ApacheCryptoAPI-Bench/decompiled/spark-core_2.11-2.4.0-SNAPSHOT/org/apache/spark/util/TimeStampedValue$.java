package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.BoxesRunTime;

public final class TimeStampedValue$ implements Serializable {
   public static final TimeStampedValue$ MODULE$;

   static {
      new TimeStampedValue$();
   }

   public final String toString() {
      return "TimeStampedValue";
   }

   public <V> TimeStampedValue<V> apply(V value, long timestamp) {
      return new TimeStampedValue<V>(value, timestamp);
   }

   public <V> Option<Tuple2<V, Object>> unapply(TimeStampedValue<V> x$0) {
      return (Option<Tuple2<V, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.value(), BoxesRunTime.boxToLong(x$0.timestamp()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TimeStampedValue$() {
      MODULE$ = this;
   }
}
