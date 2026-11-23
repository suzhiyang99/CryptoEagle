package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Serializable;

public final class TaskCompletionListenerException$ implements Serializable {
   public static final TaskCompletionListenerException$ MODULE$;

   static {
      new TaskCompletionListenerException$();
   }

   public Option<Throwable> $lessinit$greater$default$2() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskCompletionListenerException$() {
      MODULE$ = this;
   }
}
