package org.apache.spark.status.api.v1;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple7;
import scala.collection.Seq;
import scala.runtime.AbstractFunction7;

public final class ApplicationInfo$ extends AbstractFunction7<String, String, Option<Object>, Option<Object>, Option<Object>, Option<Object>, Seq<ApplicationAttemptInfo>, ApplicationInfo> implements Serializable {
   public static final ApplicationInfo$ MODULE$;

   static {
      new ApplicationInfo$();
   }

   public final String toString() {
      return "ApplicationInfo";
   }

   public ApplicationInfo apply(String id, String name, Option<Object> coresGranted, Option<Object> maxCores, Option<Object> coresPerExecutor, Option<Object> memoryPerExecutorMB, Seq<ApplicationAttemptInfo> attempts) {
      return new ApplicationInfo(id, name, coresGranted, maxCores, coresPerExecutor, memoryPerExecutorMB, attempts);
   }

   public Option<Tuple7<String, String, Option<Object>, Option<Object>, Option<Object>, Option<Object>, Seq<ApplicationAttemptInfo>>> unapply(ApplicationInfo x$0) {
      return (Option<Tuple7<String, String, Option<Object>, Option<Object>, Option<Object>, Option<Object>, Seq<ApplicationAttemptInfo>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple7(x$0.id(), x$0.name(), x$0.coresGranted(), x$0.maxCores(), x$0.coresPerExecutor(), x$0.memoryPerExecutorMB(), x$0.attempts())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ApplicationInfo$() {
      MODULE$ = this;
   }
}
