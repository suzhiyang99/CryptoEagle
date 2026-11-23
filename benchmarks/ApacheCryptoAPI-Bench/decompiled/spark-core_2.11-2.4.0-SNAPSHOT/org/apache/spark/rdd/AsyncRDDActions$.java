package org.apache.spark.rdd;

import org.apache.spark.util.ThreadUtils$;
import scala.Serializable;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutorService;

public final class AsyncRDDActions$ implements Serializable {
   public static final AsyncRDDActions$ MODULE$;
   private final ExecutionContextExecutorService futureExecutionContext;

   static {
      new AsyncRDDActions$();
   }

   public ExecutionContextExecutorService futureExecutionContext() {
      return this.futureExecutionContext;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AsyncRDDActions$() {
      MODULE$ = this;
      this.futureExecutionContext = ExecutionContext$.MODULE$.fromExecutorService(ThreadUtils$.MODULE$.newDaemonCachedThreadPool("AsyncRDDActions-future", 128, ThreadUtils$.MODULE$.newDaemonCachedThreadPool$default$3()));
   }
}
