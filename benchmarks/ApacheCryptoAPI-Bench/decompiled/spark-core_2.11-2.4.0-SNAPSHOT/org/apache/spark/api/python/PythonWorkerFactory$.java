package org.apache.spark.api.python;

public final class PythonWorkerFactory$ {
   public static final PythonWorkerFactory$ MODULE$;
   private final int PROCESS_WAIT_TIMEOUT_MS;
   private final int IDLE_WORKER_TIMEOUT_MS;

   static {
      new PythonWorkerFactory$();
   }

   public int PROCESS_WAIT_TIMEOUT_MS() {
      return this.PROCESS_WAIT_TIMEOUT_MS;
   }

   public int IDLE_WORKER_TIMEOUT_MS() {
      return this.IDLE_WORKER_TIMEOUT_MS;
   }

   private PythonWorkerFactory$() {
      MODULE$ = this;
      this.PROCESS_WAIT_TIMEOUT_MS = 10000;
      this.IDLE_WORKER_TIMEOUT_MS = 60000;
   }
}
