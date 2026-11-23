package org.apache.spark.scheduler;

import scala.None$;
import scala.Option;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;

public abstract class SchedulerBackend$class {
   public static void killTask(SchedulerBackend $this, long taskId, String executorId, boolean interruptThread, String reason) {
      throw new UnsupportedOperationException();
   }

   public static boolean isReady(SchedulerBackend $this) {
      return true;
   }

   public static String applicationId(SchedulerBackend $this) {
      return $this.org$apache$spark$scheduler$SchedulerBackend$$appId();
   }

   public static Option applicationAttemptId(SchedulerBackend $this) {
      return None$.MODULE$;
   }

   public static Option getDriverLogUrls(SchedulerBackend $this) {
      return None$.MODULE$;
   }

   public static void $init$(SchedulerBackend $this) {
      $this.org$apache$spark$scheduler$SchedulerBackend$_setter_$org$apache$spark$scheduler$SchedulerBackend$$appId_$eq((new StringBuilder()).append((Object)"spark-application-").append((Object)BoxesRunTime.boxToLong(System.currentTimeMillis())).toString());
   }
}
