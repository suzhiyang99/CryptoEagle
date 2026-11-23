package org.apache.spark.deploy.worker.ui;

import org.apache.spark.ui.SparkUI$;

public final class WorkerWebUI$ {
   public static final WorkerWebUI$ MODULE$;
   private final String STATIC_RESOURCE_BASE;
   private final int DEFAULT_RETAINED_DRIVERS;
   private final int DEFAULT_RETAINED_EXECUTORS;

   static {
      new WorkerWebUI$();
   }

   public String STATIC_RESOURCE_BASE() {
      return this.STATIC_RESOURCE_BASE;
   }

   public int DEFAULT_RETAINED_DRIVERS() {
      return this.DEFAULT_RETAINED_DRIVERS;
   }

   public int DEFAULT_RETAINED_EXECUTORS() {
      return this.DEFAULT_RETAINED_EXECUTORS;
   }

   private WorkerWebUI$() {
      MODULE$ = this;
      this.STATIC_RESOURCE_BASE = SparkUI$.MODULE$.STATIC_RESOURCE_DIR();
      this.DEFAULT_RETAINED_DRIVERS = 1000;
      this.DEFAULT_RETAINED_EXECUTORS = 1000;
   }
}
