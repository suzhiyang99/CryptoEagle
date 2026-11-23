package org.apache.spark.status;

import java.util.Date;
import org.apache.spark.status.api.v1.TaskData;
import org.apache.spark.status.api.v1.TaskMetrics;
import scala.Serializable;
import scala.math.package$;
import scala.runtime.BoxesRunTime;

public final class AppStatusUtils$ {
   public static final AppStatusUtils$ MODULE$;

   static {
      new AppStatusUtils$();
   }

   public long schedulerDelay(TaskData task) {
      long var10000;
      if (task.taskMetrics().isDefined() && task.duration().isDefined()) {
         TaskMetrics m = (TaskMetrics)task.taskMetrics().get();
         var10000 = this.schedulerDelay(task.launchTime().getTime(), this.fetchStart(task), BoxesRunTime.unboxToLong(task.duration().get()), m.executorDeserializeTime(), m.resultSerializationTime(), m.executorRunTime());
      } else {
         var10000 = 0L;
      }

      return var10000;
   }

   public long gettingResultTime(TaskData task) {
      return this.gettingResultTime(task.launchTime().getTime(), this.fetchStart(task), BoxesRunTime.unboxToLong(task.duration().getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      })));
   }

   public long schedulerDelay(long launchTime, long fetchStart, long duration, long deserializeTime, long serializeTime, long runTime) {
      return package$.MODULE$.max(0L, duration - runTime - deserializeTime - serializeTime - this.gettingResultTime(launchTime, fetchStart, duration));
   }

   public long gettingResultTime(long launchTime, long fetchStart, long duration) {
      return fetchStart > 0L ? (duration > 0L ? launchTime + duration - fetchStart : System.currentTimeMillis() - fetchStart) : 0L;
   }

   private long fetchStart(TaskData task) {
      return task.resultFetchStart().isDefined() ? ((Date)task.resultFetchStart().get()).getTime() : -1L;
   }

   private AppStatusUtils$() {
      MODULE$ = this;
   }
}
