package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class ExecutorRemoved$ extends AbstractFunction1<String, ExecutorRemoved> implements Serializable {
   public static final ExecutorRemoved$ MODULE$;

   static {
      new ExecutorRemoved$();
   }

   public final String toString() {
      return "ExecutorRemoved";
   }

   public ExecutorRemoved apply(String executorId) {
      return new ExecutorRemoved(executorId);
   }

   public Option<String> unapply(ExecutorRemoved x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.executorId()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorRemoved$() {
      MODULE$ = this;
   }
}
