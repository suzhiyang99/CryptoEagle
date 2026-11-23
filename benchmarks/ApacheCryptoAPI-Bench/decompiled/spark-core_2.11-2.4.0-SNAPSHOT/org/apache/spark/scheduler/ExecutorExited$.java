package org.apache.spark.scheduler;

import org.apache.spark.executor.ExecutorExitCode$;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.BoxesRunTime;

public final class ExecutorExited$ implements Serializable {
   public static final ExecutorExited$ MODULE$;

   static {
      new ExecutorExited$();
   }

   public ExecutorExited apply(int exitCode, boolean exitCausedByApp) {
      return new ExecutorExited(exitCode, exitCausedByApp, ExecutorExitCode$.MODULE$.explainExitCode(exitCode));
   }

   public ExecutorExited apply(int exitCode, boolean exitCausedByApp, String reason) {
      return new ExecutorExited(exitCode, exitCausedByApp, reason);
   }

   public Option<Tuple3<Object, Object, String>> unapply(ExecutorExited x$0) {
      return (Option<Tuple3<Object, Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.exitCode()), BoxesRunTime.boxToBoolean(x$0.exitCausedByApp()), x$0.reason())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExecutorExited$() {
      MODULE$ = this;
   }
}
