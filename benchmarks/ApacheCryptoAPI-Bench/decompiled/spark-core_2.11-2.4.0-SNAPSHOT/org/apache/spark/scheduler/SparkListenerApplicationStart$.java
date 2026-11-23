package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple6;
import scala.collection.Map;
import scala.runtime.AbstractFunction6;
import scala.runtime.BoxesRunTime;

public final class SparkListenerApplicationStart$ extends AbstractFunction6<String, Option<String>, Object, String, Option<String>, Option<Map<String, String>>, SparkListenerApplicationStart> implements Serializable {
   public static final SparkListenerApplicationStart$ MODULE$;

   static {
      new SparkListenerApplicationStart$();
   }

   public final String toString() {
      return "SparkListenerApplicationStart";
   }

   public SparkListenerApplicationStart apply(String appName, Option<String> appId, long time, String sparkUser, Option<String> appAttemptId, Option<Map<String, String>> driverLogs) {
      return new SparkListenerApplicationStart(appName, appId, time, sparkUser, appAttemptId, driverLogs);
   }

   public Option<Tuple6<String, Option<String>, Object, String, Option<String>, Option<Map<String, String>>>> unapply(SparkListenerApplicationStart x$0) {
      return (Option<Tuple6<String, Option<String>, Object, String, Option<String>, Option<Map<String, String>>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple6(x$0.appName(), x$0.appId(), BoxesRunTime.boxToLong(x$0.time()), x$0.sparkUser(), x$0.appAttemptId(), x$0.driverLogs())));
   }

   public Option<Map<String, String>> apply$default$6() {
      return None$.MODULE$;
   }

   public Option<Map<String, String>> $lessinit$greater$default$6() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerApplicationStart$() {
      MODULE$ = this;
   }
}
