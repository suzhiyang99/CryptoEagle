package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class AskPermissionToCommitOutput$ extends AbstractFunction3<Object, Object, Object, AskPermissionToCommitOutput> implements Serializable {
   public static final AskPermissionToCommitOutput$ MODULE$;

   static {
      new AskPermissionToCommitOutput$();
   }

   public final String toString() {
      return "AskPermissionToCommitOutput";
   }

   public AskPermissionToCommitOutput apply(int stage, int partition, int attemptNumber) {
      return new AskPermissionToCommitOutput(stage, partition, attemptNumber);
   }

   public Option<Tuple3<Object, Object, Object>> unapply(AskPermissionToCommitOutput x$0) {
      return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.stage()), BoxesRunTime.boxToInteger(x$0.partition()), BoxesRunTime.boxToInteger(x$0.attemptNumber()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AskPermissionToCommitOutput$() {
      MODULE$ = this;
   }
}
