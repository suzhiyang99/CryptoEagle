package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class ExecutorRegistered$ extends AbstractFunction1<String, ExecutorRegistered> implements Serializable {
   public static final ExecutorRegistered$ MODULE$;

   static {
      new ExecutorRegistered$();
   }

   public final String toString() {
      return "ExecutorRegistered";
   }

   public ExecutorRegistered apply(String executorId) {
      return new ExecutorRegistered(executorId);
   }

   public Option<String> unapply(ExecutorRegistered x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.executorId()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorRegistered$() {
      MODULE$ = this;
   }
}
