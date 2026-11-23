package org.apache.spark;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class ExecutorLostFailure$ extends AbstractFunction3<String, Object, Option<String>, ExecutorLostFailure> implements Serializable {
   public static final ExecutorLostFailure$ MODULE$;

   static {
      new ExecutorLostFailure$();
   }

   public final String toString() {
      return "ExecutorLostFailure";
   }

   public ExecutorLostFailure apply(String execId, boolean exitCausedByApp, Option<String> reason) {
      return new ExecutorLostFailure(execId, exitCausedByApp, reason);
   }

   public Option<Tuple3<String, Object, Option<String>>> unapply(ExecutorLostFailure x$0) {
      return (Option<Tuple3<String, Object, Option<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.execId(), BoxesRunTime.boxToBoolean(x$0.exitCausedByApp()), x$0.reason())));
   }

   public boolean $lessinit$greater$default$2() {
      return true;
   }

   public boolean apply$default$2() {
      return true;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorLostFailure$() {
      MODULE$ = this;
   }
}
