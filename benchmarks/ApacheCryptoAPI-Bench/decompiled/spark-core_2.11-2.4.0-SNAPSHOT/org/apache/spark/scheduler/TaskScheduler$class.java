package org.apache.spark.scheduler;

import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;

public abstract class TaskScheduler$class {
   public static void postStartHook(TaskScheduler $this) {
   }

   public static String applicationId(TaskScheduler $this) {
      return $this.org$apache$spark$scheduler$TaskScheduler$$appId();
   }

   public static void $init$(TaskScheduler $this) {
      $this.org$apache$spark$scheduler$TaskScheduler$_setter_$org$apache$spark$scheduler$TaskScheduler$$appId_$eq((new StringBuilder()).append((Object)"spark-application-").append((Object)BoxesRunTime.boxToLong(System.currentTimeMillis())).toString());
   }
}
