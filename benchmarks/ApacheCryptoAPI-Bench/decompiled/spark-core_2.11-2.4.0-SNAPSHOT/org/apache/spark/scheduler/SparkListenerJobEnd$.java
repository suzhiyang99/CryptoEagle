package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class SparkListenerJobEnd$ extends AbstractFunction3<Object, Object, JobResult, SparkListenerJobEnd> implements Serializable {
   public static final SparkListenerJobEnd$ MODULE$;

   static {
      new SparkListenerJobEnd$();
   }

   public final String toString() {
      return "SparkListenerJobEnd";
   }

   public SparkListenerJobEnd apply(int jobId, long time, JobResult jobResult) {
      return new SparkListenerJobEnd(jobId, time, jobResult);
   }

   public Option<Tuple3<Object, Object, JobResult>> unapply(SparkListenerJobEnd x$0) {
      return (Option<Tuple3<Object, Object, JobResult>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.jobId()), BoxesRunTime.boxToLong(x$0.time()), x$0.jobResult())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SparkListenerJobEnd$() {
      MODULE$ = this;
   }
}
