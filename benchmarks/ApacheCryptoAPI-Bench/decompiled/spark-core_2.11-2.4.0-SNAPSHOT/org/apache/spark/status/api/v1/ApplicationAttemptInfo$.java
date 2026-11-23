package org.apache.spark.status.api.v1;

import java.util.Date;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple8;
import scala.runtime.AbstractFunction8;
import scala.runtime.BoxesRunTime;

public final class ApplicationAttemptInfo$ extends AbstractFunction8<Option<String>, Date, Date, Date, Object, String, Object, String, ApplicationAttemptInfo> implements Serializable {
   public static final ApplicationAttemptInfo$ MODULE$;

   static {
      new ApplicationAttemptInfo$();
   }

   public final String toString() {
      return "ApplicationAttemptInfo";
   }

   public ApplicationAttemptInfo apply(Option<String> attemptId, Date startTime, Date endTime, Date lastUpdated, long duration, String sparkUser, boolean completed, String appSparkVersion) {
      return new ApplicationAttemptInfo(attemptId, startTime, endTime, lastUpdated, duration, sparkUser, completed, appSparkVersion);
   }

   public Option<Tuple8<Option<String>, Date, Date, Date, Object, String, Object, String>> unapply(ApplicationAttemptInfo x$0) {
      return (Option<Tuple8<Option<String>, Date, Date, Date, Object, String, Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple8(x$0.attemptId(), x$0.startTime(), x$0.endTime(), x$0.lastUpdated(), BoxesRunTime.boxToLong(x$0.duration()), x$0.sparkUser(), BoxesRunTime.boxToBoolean(x$0.completed()), x$0.appSparkVersion())));
   }

   public boolean apply$default$7() {
      return false;
   }

   public boolean $lessinit$greater$default$7() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ApplicationAttemptInfo$() {
      MODULE$ = this;
   }
}
