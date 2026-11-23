package org.apache.spark.deploy.history;

import java.util.concurrent.TimeUnit;
import org.apache.spark.internal.config.ConfigBuilder;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.OptionalConfigEntry;
import org.apache.spark.network.util.ByteUnit;
import scala.runtime.BoxesRunTime;

public final class config$ {
   public static final config$ MODULE$;
   private final String DEFAULT_LOG_DIR;
   private final ConfigEntry<String> EVENT_LOG_DIR;
   private final ConfigEntry<Object> MAX_LOG_AGE_S;
   private final OptionalConfigEntry<String> LOCAL_STORE_DIR;
   private final ConfigEntry<Object> MAX_LOCAL_DISK_USAGE;
   private final ConfigEntry<Object> HISTORY_SERVER_UI_PORT;

   static {
      new config$();
   }

   public String DEFAULT_LOG_DIR() {
      return this.DEFAULT_LOG_DIR;
   }

   public ConfigEntry<String> EVENT_LOG_DIR() {
      return this.EVENT_LOG_DIR;
   }

   public ConfigEntry<Object> MAX_LOG_AGE_S() {
      return this.MAX_LOG_AGE_S;
   }

   public OptionalConfigEntry<String> LOCAL_STORE_DIR() {
      return this.LOCAL_STORE_DIR;
   }

   public ConfigEntry<Object> MAX_LOCAL_DISK_USAGE() {
      return this.MAX_LOCAL_DISK_USAGE;
   }

   public ConfigEntry<Object> HISTORY_SERVER_UI_PORT() {
      return this.HISTORY_SERVER_UI_PORT;
   }

   private config$() {
      MODULE$ = this;
      this.DEFAULT_LOG_DIR = "file:/tmp/spark-events";
      this.EVENT_LOG_DIR = (new ConfigBuilder("spark.history.fs.logDirectory")).stringConf().createWithDefault(this.DEFAULT_LOG_DIR());
      this.MAX_LOG_AGE_S = (new ConfigBuilder("spark.history.fs.cleaner.maxAge")).timeConf(TimeUnit.SECONDS).createWithDefaultString("7d");
      this.LOCAL_STORE_DIR = (new ConfigBuilder("spark.history.store.path")).doc("Local directory where to cache application history information. By default this is not set, meaning all history information will be kept in memory.").stringConf().createOptional();
      this.MAX_LOCAL_DISK_USAGE = (new ConfigBuilder("spark.history.store.maxDiskUsage")).bytesConf(ByteUnit.BYTE).createWithDefaultString("10g");
      this.HISTORY_SERVER_UI_PORT = (new ConfigBuilder("spark.history.ui.port")).doc("Web UI port to bind Spark History Server").intConf().createWithDefault(BoxesRunTime.boxToInteger(18080));
   }
}
