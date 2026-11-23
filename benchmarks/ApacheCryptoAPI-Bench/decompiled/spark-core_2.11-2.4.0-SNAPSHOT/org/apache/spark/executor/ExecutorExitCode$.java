package org.apache.spark.executor;

import org.apache.spark.util.SparkExitCode$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;

public final class ExecutorExitCode$ {
   public static final ExecutorExitCode$ MODULE$;
   private final int DISK_STORE_FAILED_TO_CREATE_DIR;
   private final int EXTERNAL_BLOCK_STORE_FAILED_TO_INITIALIZE;
   private final int EXTERNAL_BLOCK_STORE_FAILED_TO_CREATE_DIR;
   private final int HEARTBEAT_FAILURE;

   static {
      new ExecutorExitCode$();
   }

   public int DISK_STORE_FAILED_TO_CREATE_DIR() {
      return this.DISK_STORE_FAILED_TO_CREATE_DIR;
   }

   public int EXTERNAL_BLOCK_STORE_FAILED_TO_INITIALIZE() {
      return this.EXTERNAL_BLOCK_STORE_FAILED_TO_INITIALIZE;
   }

   public int EXTERNAL_BLOCK_STORE_FAILED_TO_CREATE_DIR() {
      return this.EXTERNAL_BLOCK_STORE_FAILED_TO_CREATE_DIR;
   }

   public int HEARTBEAT_FAILURE() {
      return this.HEARTBEAT_FAILURE;
   }

   public String explainExitCode(int exitCode) {
      String var3;
      if (SparkExitCode$.MODULE$.UNCAUGHT_EXCEPTION() == exitCode) {
         var3 = "Uncaught exception";
      } else if (SparkExitCode$.MODULE$.UNCAUGHT_EXCEPTION_TWICE() == exitCode) {
         var3 = "Uncaught exception, and logging the exception failed";
      } else if (SparkExitCode$.MODULE$.OOM() == exitCode) {
         var3 = "OutOfMemoryError";
      } else if (this.DISK_STORE_FAILED_TO_CREATE_DIR() == exitCode) {
         var3 = "Failed to create local directory (bad spark.local.dir?)";
      } else if (this.EXTERNAL_BLOCK_STORE_FAILED_TO_INITIALIZE() == exitCode) {
         var3 = "ExternalBlockStore failed to initialize.";
      } else if (this.EXTERNAL_BLOCK_STORE_FAILED_TO_CREATE_DIR() == exitCode) {
         var3 = "ExternalBlockStore failed to create a local temporary directory.";
      } else if (this.HEARTBEAT_FAILURE() == exitCode) {
         var3 = "Unable to send heartbeats to driver.";
      } else {
         var3 = (new StringBuilder()).append((Object)"Unknown executor exit code (").append((Object)BoxesRunTime.boxToInteger(exitCode)).append((Object)")").append((Object)(exitCode > 128 ? (new StringBuilder()).append((Object)" (died from signal ").append((Object)BoxesRunTime.boxToInteger(exitCode - 128)).append((Object)"?)").toString() : "")).toString();
      }

      return var3;
   }

   private ExecutorExitCode$() {
      MODULE$ = this;
      this.DISK_STORE_FAILED_TO_CREATE_DIR = 53;
      this.EXTERNAL_BLOCK_STORE_FAILED_TO_INITIALIZE = 54;
      this.EXTERNAL_BLOCK_STORE_FAILED_TO_CREATE_DIR = 55;
      this.HEARTBEAT_FAILURE = 56;
   }
}
