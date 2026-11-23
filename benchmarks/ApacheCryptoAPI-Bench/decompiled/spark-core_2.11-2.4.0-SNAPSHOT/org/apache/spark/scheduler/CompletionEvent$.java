package org.apache.spark.scheduler;

import org.apache.spark.TaskEndReason;
import org.apache.spark.util.AccumulatorV2;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.collection.Seq;
import scala.runtime.AbstractFunction5;

public final class CompletionEvent$ extends AbstractFunction5<Task<?>, TaskEndReason, Object, Seq<AccumulatorV2<?, ?>>, TaskInfo, CompletionEvent> implements Serializable {
   public static final CompletionEvent$ MODULE$;

   static {
      new CompletionEvent$();
   }

   public final String toString() {
      return "CompletionEvent";
   }

   public CompletionEvent apply(Task<?> task, TaskEndReason reason, Object result, Seq<AccumulatorV2<?, ?>> accumUpdates, TaskInfo taskInfo) {
      return new CompletionEvent(task, reason, result, accumUpdates, taskInfo);
   }

   public Option<Tuple5<Task<Object>, TaskEndReason, Object, Seq<AccumulatorV2<?, ?>>, TaskInfo>> unapply(CompletionEvent x$0) {
      return (Option<Tuple5<Task<Object>, TaskEndReason, Object, Seq<AccumulatorV2<?, ?>>, TaskInfo>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.task(), x$0.reason(), x$0.result(), x$0.accumUpdates(), x$0.taskInfo())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CompletionEvent$() {
      MODULE$ = this;
   }
}
