package org.apache.spark.deploy.history;

public final class FsHistoryProvider$ {
   public static final FsHistoryProvider$ MODULE$;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$SPARK_HISTORY_FS_NUM_REPLAY_THREADS;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$APPL_START_EVENT_PREFIX;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$APPL_END_EVENT_PREFIX;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$LOG_START_EVENT_PREFIX;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$ENV_UPDATE_EVENT_PREFIX;
   private final long CURRENT_LISTING_VERSION;

   static {
      new FsHistoryProvider$();
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$SPARK_HISTORY_FS_NUM_REPLAY_THREADS() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$SPARK_HISTORY_FS_NUM_REPLAY_THREADS;
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$APPL_START_EVENT_PREFIX() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$APPL_START_EVENT_PREFIX;
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$APPL_END_EVENT_PREFIX() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$APPL_END_EVENT_PREFIX;
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$LOG_START_EVENT_PREFIX() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$LOG_START_EVENT_PREFIX;
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$ENV_UPDATE_EVENT_PREFIX() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$ENV_UPDATE_EVENT_PREFIX;
   }

   public long CURRENT_LISTING_VERSION() {
      return this.CURRENT_LISTING_VERSION;
   }

   private FsHistoryProvider$() {
      MODULE$ = this;
      this.org$apache$spark$deploy$history$FsHistoryProvider$$SPARK_HISTORY_FS_NUM_REPLAY_THREADS = "spark.history.fs.numReplayThreads";
      this.org$apache$spark$deploy$history$FsHistoryProvider$$APPL_START_EVENT_PREFIX = "{\"Event\":\"SparkListenerApplicationStart\"";
      this.org$apache$spark$deploy$history$FsHistoryProvider$$APPL_END_EVENT_PREFIX = "{\"Event\":\"SparkListenerApplicationEnd\"";
      this.org$apache$spark$deploy$history$FsHistoryProvider$$LOG_START_EVENT_PREFIX = "{\"Event\":\"SparkListenerLogStart\"";
      this.org$apache$spark$deploy$history$FsHistoryProvider$$ENV_UPDATE_EVENT_PREFIX = "{\"Event\":\"SparkListenerEnvironmentUpdate\",";
      this.CURRENT_LISTING_VERSION = 1L;
   }
}
