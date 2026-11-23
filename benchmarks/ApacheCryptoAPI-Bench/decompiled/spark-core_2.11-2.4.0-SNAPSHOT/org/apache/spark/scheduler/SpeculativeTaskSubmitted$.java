package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class SpeculativeTaskSubmitted$ extends AbstractFunction1<Task<?>, SpeculativeTaskSubmitted> implements Serializable {
   public static final SpeculativeTaskSubmitted$ MODULE$;

   static {
      new SpeculativeTaskSubmitted$();
   }

   public final String toString() {
      return "SpeculativeTaskSubmitted";
   }

   public SpeculativeTaskSubmitted apply(Task<?> task) {
      return new SpeculativeTaskSubmitted(task);
   }

   public Option<Task<Object>> unapply(SpeculativeTaskSubmitted x$0) {
      return (Option<Task<Object>>)(x$0 == null ? None$.MODULE$ : new Some(x$0.task()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SpeculativeTaskSubmitted$() {
      MODULE$ = this;
   }
}
