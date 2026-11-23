package org.apache.spark.deploy.history;

import org.apache.spark.util.kvstore.KVIndex;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class ApplicationStoreInfo$ extends AbstractFunction5<String, Object, String, Option<String>, Object, ApplicationStoreInfo> implements Serializable {
   public static final ApplicationStoreInfo$ MODULE$;

   static {
      new ApplicationStoreInfo$();
   }

   public final String toString() {
      return "ApplicationStoreInfo";
   }

   public ApplicationStoreInfo apply(@KVIndex String path, @KVIndex("lastAccess") long lastAccess, String appId, Option<String> attemptId, long size) {
      return new ApplicationStoreInfo(path, lastAccess, appId, attemptId, size);
   }

   public Option<Tuple5<String, Object, String, Option<String>, Object>> unapply(ApplicationStoreInfo x$0) {
      return (Option<Tuple5<String, Object, String, Option<String>, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.path(), BoxesRunTime.boxToLong(x$0.lastAccess()), x$0.appId(), x$0.attemptId(), BoxesRunTime.boxToLong(x$0.size()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ApplicationStoreInfo$() {
      MODULE$ = this;
   }
}
