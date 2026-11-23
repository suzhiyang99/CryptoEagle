package org.apache.spark.util;

public final class SparkExitCode$ {
   public static final SparkExitCode$ MODULE$;
   private final int UNCAUGHT_EXCEPTION;
   private final int UNCAUGHT_EXCEPTION_TWICE;
   private final int OOM;

   static {
      new SparkExitCode$();
   }

   public int UNCAUGHT_EXCEPTION() {
      return this.UNCAUGHT_EXCEPTION;
   }

   public int UNCAUGHT_EXCEPTION_TWICE() {
      return this.UNCAUGHT_EXCEPTION_TWICE;
   }

   public int OOM() {
      return this.OOM;
   }

   private SparkExitCode$() {
      MODULE$ = this;
      this.UNCAUGHT_EXCEPTION = 50;
      this.UNCAUGHT_EXCEPTION_TWICE = 51;
      this.OOM = 52;
   }
}
