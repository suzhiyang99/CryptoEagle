package org.apache.spark.scheduler;

import scala.runtime.BoxesRunTime;
import scala.util.DynamicVariable;

public final class LiveListenerBus$ {
   public static final LiveListenerBus$ MODULE$;
   private final DynamicVariable<Object> withinListenerThread;
   private final String SHARED_QUEUE;
   private final String APP_STATUS_QUEUE;
   private final String EXECUTOR_MANAGEMENT_QUEUE;
   private final String EVENT_LOG_QUEUE;

   static {
      new LiveListenerBus$();
   }

   public DynamicVariable<Object> withinListenerThread() {
      return this.withinListenerThread;
   }

   public String SHARED_QUEUE() {
      return this.SHARED_QUEUE;
   }

   public String APP_STATUS_QUEUE() {
      return this.APP_STATUS_QUEUE;
   }

   public String EXECUTOR_MANAGEMENT_QUEUE() {
      return this.EXECUTOR_MANAGEMENT_QUEUE;
   }

   public String EVENT_LOG_QUEUE() {
      return this.EVENT_LOG_QUEUE;
   }

   private LiveListenerBus$() {
      MODULE$ = this;
      this.withinListenerThread = new DynamicVariable<Object>(BoxesRunTime.boxToBoolean(false));
      this.SHARED_QUEUE = "shared";
      this.APP_STATUS_QUEUE = "appStatus";
      this.EXECUTOR_MANAGEMENT_QUEUE = "executorManagement";
      this.EVENT_LOG_QUEUE = "eventLog";
   }
}
