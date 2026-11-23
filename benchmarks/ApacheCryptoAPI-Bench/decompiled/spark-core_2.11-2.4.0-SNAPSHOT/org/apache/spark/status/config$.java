package org.apache.spark.status;

import java.util.concurrent.TimeUnit;
import org.apache.spark.internal.config.ConfigBuilder;
import org.apache.spark.internal.config.ConfigEntry;
import scala.runtime.BoxesRunTime;

public final class config$ {
   public static final config$ MODULE$;
   private final ConfigEntry<Object> ASYNC_TRACKING_ENABLED;
   private final ConfigEntry<Object> LIVE_ENTITY_UPDATE_PERIOD;
   private final ConfigEntry<Object> MAX_RETAINED_JOBS;
   private final ConfigEntry<Object> MAX_RETAINED_STAGES;
   private final ConfigEntry<Object> MAX_RETAINED_TASKS_PER_STAGE;
   private final ConfigEntry<Object> MAX_RETAINED_DEAD_EXECUTORS;
   private final ConfigEntry<Object> MAX_RETAINED_ROOT_NODES;

   static {
      new config$();
   }

   public ConfigEntry<Object> ASYNC_TRACKING_ENABLED() {
      return this.ASYNC_TRACKING_ENABLED;
   }

   public ConfigEntry<Object> LIVE_ENTITY_UPDATE_PERIOD() {
      return this.LIVE_ENTITY_UPDATE_PERIOD;
   }

   public ConfigEntry<Object> MAX_RETAINED_JOBS() {
      return this.MAX_RETAINED_JOBS;
   }

   public ConfigEntry<Object> MAX_RETAINED_STAGES() {
      return this.MAX_RETAINED_STAGES;
   }

   public ConfigEntry<Object> MAX_RETAINED_TASKS_PER_STAGE() {
      return this.MAX_RETAINED_TASKS_PER_STAGE;
   }

   public ConfigEntry<Object> MAX_RETAINED_DEAD_EXECUTORS() {
      return this.MAX_RETAINED_DEAD_EXECUTORS;
   }

   public ConfigEntry<Object> MAX_RETAINED_ROOT_NODES() {
      return this.MAX_RETAINED_ROOT_NODES;
   }

   private config$() {
      MODULE$ = this;
      this.ASYNC_TRACKING_ENABLED = (new ConfigBuilder("spark.appStateStore.asyncTracking.enable")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(true));
      this.LIVE_ENTITY_UPDATE_PERIOD = (new ConfigBuilder("spark.ui.liveUpdate.period")).timeConf(TimeUnit.NANOSECONDS).createWithDefaultString("100ms");
      this.MAX_RETAINED_JOBS = (new ConfigBuilder("spark.ui.retainedJobs")).intConf().createWithDefault(BoxesRunTime.boxToInteger(1000));
      this.MAX_RETAINED_STAGES = (new ConfigBuilder("spark.ui.retainedStages")).intConf().createWithDefault(BoxesRunTime.boxToInteger(1000));
      this.MAX_RETAINED_TASKS_PER_STAGE = (new ConfigBuilder("spark.ui.retainedTasks")).intConf().createWithDefault(BoxesRunTime.boxToInteger(100000));
      this.MAX_RETAINED_DEAD_EXECUTORS = (new ConfigBuilder("spark.ui.retainedDeadExecutors")).intConf().createWithDefault(BoxesRunTime.boxToInteger(100));
      this.MAX_RETAINED_ROOT_NODES = (new ConfigBuilder("spark.ui.dagGraph.retainedRootRDDs")).intConf().createWithDefault(BoxesRunTime.boxToInteger(Integer.MAX_VALUE));
   }
}
