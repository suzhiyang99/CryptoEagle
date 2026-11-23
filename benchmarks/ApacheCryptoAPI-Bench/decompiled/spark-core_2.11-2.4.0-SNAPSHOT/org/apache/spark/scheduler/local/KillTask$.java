package org.apache.spark.scheduler.local;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class KillTask$ extends AbstractFunction3<Object, Object, String, KillTask> implements Serializable {
   public static final KillTask$ MODULE$;

   static {
      new KillTask$();
   }

   public final String toString() {
      return "KillTask";
   }

   public KillTask apply(long taskId, boolean interruptThread, String reason) {
      return new KillTask(taskId, interruptThread, reason);
   }

   public Option<Tuple3<Object, Object, String>> unapply(KillTask x$0) {
      return (Option<Tuple3<Object, Object, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.taskId()), BoxesRunTime.boxToBoolean(x$0.interruptThread()), x$0.reason())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private KillTask$() {
      MODULE$ = this;
   }
}
