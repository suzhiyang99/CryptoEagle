package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class SparkListenerNodeBlacklisted$ extends AbstractFunction3<Object, String, Object, SparkListenerNodeBlacklisted> implements Serializable {
   public static final SparkListenerNodeBlacklisted$ MODULE$;

   static {
      new SparkListenerNodeBlacklisted$();
   }

   public final String toString() {
      return "SparkListenerNodeBlacklisted";
   }

   public SparkListenerNodeBlacklisted apply(long time, String hostId, int executorFailures) {
      return new SparkListenerNodeBlacklisted(time, hostId, executorFailures);
   }

   public Option<Tuple3<Object, String, Object>> unapply(SparkListenerNodeBlacklisted x$0) {
      return (Option<Tuple3<Object, String, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.time()), x$0.hostId(), BoxesRunTime.boxToInteger(x$0.executorFailures()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerNodeBlacklisted$() {
      MODULE$ = this;
   }
}
