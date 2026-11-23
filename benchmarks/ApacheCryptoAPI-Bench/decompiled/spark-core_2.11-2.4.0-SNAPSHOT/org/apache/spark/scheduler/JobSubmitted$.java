package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.TaskContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.CallSite;
import scala.Function2;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple7;
import scala.collection.Iterator;
import scala.runtime.AbstractFunction7;
import scala.runtime.BoxesRunTime;

public final class JobSubmitted$ extends AbstractFunction7<Object, RDD<?>, Function2<TaskContext, Iterator<?>, ?>, int[], CallSite, JobListener, Properties, JobSubmitted> implements Serializable {
   public static final JobSubmitted$ MODULE$;

   static {
      new JobSubmitted$();
   }

   public final String toString() {
      return "JobSubmitted";
   }

   public JobSubmitted apply(int jobId, RDD<?> finalRDD, Function2<TaskContext, Iterator<?>, ?> func, int[] partitions, CallSite callSite, JobListener listener, Properties properties) {
      return new JobSubmitted(jobId, finalRDD, func, partitions, callSite, listener, properties);
   }

   public Option<Tuple7<Object, RDD<Object>, Function2<TaskContext, Iterator<?>, Object>, int[], CallSite, JobListener, Properties>> unapply(JobSubmitted x$0) {
      return (Option<Tuple7<Object, RDD<Object>, Function2<TaskContext, Iterator<?>, Object>, int[], CallSite, JobListener, Properties>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple7(BoxesRunTime.boxToInteger(x$0.jobId()), x$0.finalRDD(), x$0.func(), x$0.partitions(), x$0.callSite(), x$0.listener(), x$0.properties())));
   }

   public Properties apply$default$7() {
      return null;
   }

   public Properties $lessinit$greater$default$7() {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JobSubmitted$() {
      MODULE$ = this;
   }
}
