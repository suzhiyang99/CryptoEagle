package org.apache.spark.deploy.history;

import org.apache.spark.util.kvstore.KVIndex;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class LogInfo$ extends AbstractFunction5<String, Object, Option<String>, Option<String>, Object, LogInfo> implements Serializable {
   public static final LogInfo$ MODULE$;

   static {
      new LogInfo$();
   }

   public final String toString() {
      return "LogInfo";
   }

   public LogInfo apply(@KVIndex String logPath, @KVIndex("lastProcessed") long lastProcessed, Option<String> appId, Option<String> attemptId, long fileSize) {
      return new LogInfo(logPath, lastProcessed, appId, attemptId, fileSize);
   }

   public Option<Tuple5<String, Object, Option<String>, Option<String>, Object>> unapply(LogInfo x$0) {
      return (Option<Tuple5<String, Object, Option<String>, Option<String>, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.logPath(), BoxesRunTime.boxToLong(x$0.lastProcessed()), x$0.appId(), x$0.attemptId(), BoxesRunTime.boxToLong(x$0.fileSize()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private LogInfo$() {
      MODULE$ = this;
   }
}
