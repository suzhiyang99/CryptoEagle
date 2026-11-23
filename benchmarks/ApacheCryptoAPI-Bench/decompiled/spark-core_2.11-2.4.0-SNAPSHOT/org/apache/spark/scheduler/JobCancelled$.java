package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;

public final class JobCancelled$ extends AbstractFunction2<Object, Option<String>, JobCancelled> implements Serializable {
   public static final JobCancelled$ MODULE$;

   static {
      new JobCancelled$();
   }

   public final String toString() {
      return "JobCancelled";
   }

   public JobCancelled apply(int jobId, Option<String> reason) {
      return new JobCancelled(jobId, reason);
   }

   public Option<Tuple2<Object, Option<String>>> unapply(JobCancelled x$0) {
      return (Option<Tuple2<Object, Option<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToInteger(x$0.jobId()), x$0.reason())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JobCancelled$() {
      MODULE$ = this;
   }
}
