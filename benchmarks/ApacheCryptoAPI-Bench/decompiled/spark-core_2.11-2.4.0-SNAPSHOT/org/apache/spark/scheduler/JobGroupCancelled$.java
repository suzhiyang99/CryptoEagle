package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class JobGroupCancelled$ extends AbstractFunction1<String, JobGroupCancelled> implements Serializable {
   public static final JobGroupCancelled$ MODULE$;

   static {
      new JobGroupCancelled$();
   }

   public final String toString() {
      return "JobGroupCancelled";
   }

   public JobGroupCancelled apply(String groupId) {
      return new JobGroupCancelled(groupId);
   }

   public Option<String> unapply(JobGroupCancelled x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.groupId()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JobGroupCancelled$() {
      MODULE$ = this;
   }
}
