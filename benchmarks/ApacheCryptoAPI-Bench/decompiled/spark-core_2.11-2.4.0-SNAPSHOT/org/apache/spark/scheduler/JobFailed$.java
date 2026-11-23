package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class JobFailed$ extends AbstractFunction1<Exception, JobFailed> implements Serializable {
   public static final JobFailed$ MODULE$;

   static {
      new JobFailed$();
   }

   public final String toString() {
      return "JobFailed";
   }

   public JobFailed apply(Exception exception) {
      return new JobFailed(exception);
   }

   public Option<Exception> unapply(JobFailed x$0) {
      return (Option<Exception>)(x$0 == null ? None$.MODULE$ : new Some(x$0.exception()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JobFailed$() {
      MODULE$ = this;
   }
}
