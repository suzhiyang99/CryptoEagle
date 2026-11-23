package org.apache.spark.api.r;

public final class SparkRDefaults$ {
   public static final SparkRDefaults$ MODULE$;
   private final int DEFAULT_CONNECTION_TIMEOUT;
   private final int DEFAULT_HEARTBEAT_INTERVAL;
   private final int DEFAULT_NUM_RBACKEND_THREADS;

   static {
      new SparkRDefaults$();
   }

   public int DEFAULT_CONNECTION_TIMEOUT() {
      return this.DEFAULT_CONNECTION_TIMEOUT;
   }

   public int DEFAULT_HEARTBEAT_INTERVAL() {
      return this.DEFAULT_HEARTBEAT_INTERVAL;
   }

   public int DEFAULT_NUM_RBACKEND_THREADS() {
      return this.DEFAULT_NUM_RBACKEND_THREADS;
   }

   private SparkRDefaults$() {
      MODULE$ = this;
      this.DEFAULT_CONNECTION_TIMEOUT = 6000;
      this.DEFAULT_HEARTBEAT_INTERVAL = 100;
      this.DEFAULT_NUM_RBACKEND_THREADS = 2;
   }
}
