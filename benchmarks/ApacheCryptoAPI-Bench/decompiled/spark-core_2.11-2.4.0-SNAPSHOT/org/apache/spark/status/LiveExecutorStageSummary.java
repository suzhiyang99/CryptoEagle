package org.apache.spark.status;

import org.apache.spark.status.api.v1.ExecutorStageSummary;
import org.apache.spark.status.api.v1.TaskMetrics;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Y4A!\u0001\u0002\u0005\u0017\tAB*\u001b<f\u000bb,7-\u001e;peN#\u0018mZ3Tk6l\u0017M]=\u000b\u0005\r!\u0011AB:uCR,8O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0007\u0011\u00055qQ\"\u0001\u0002\n\u0005=\u0011!A\u0003'jm\u0016,e\u000e^5us\"A\u0011\u0003\u0001B\u0001B\u0003%!#A\u0004ti\u0006<W-\u00133\u0011\u0005M1R\"\u0001\u000b\u000b\u0003U\tQa]2bY\u0006L!a\u0006\u000b\u0003\u0007%sG\u000f\u0003\u0005\u001a\u0001\t\u0005\t\u0015!\u0003\u0013\u0003%\tG\u000f^3naRLE\r\u0003\u0005\u001c\u0001\t\u0005\t\u0015!\u0003\u001d\u0003))\u00070Z2vi>\u0014\u0018\n\u001a\t\u0003;\u0001r!a\u0005\u0010\n\u0005}!\u0012A\u0002)sK\u0012,g-\u0003\u0002\"E\t11\u000b\u001e:j]\u001eT!a\b\u000b\t\u000b\u0011\u0002A\u0011A\u0013\u0002\rqJg.\u001b;?)\u00111s\u0005K\u0015\u0011\u00055\u0001\u0001\"B\t$\u0001\u0004\u0011\u0002\"B\r$\u0001\u0004\u0011\u0002\"B\u000e$\u0001\u0004a\u0002bB\u0016\u0001\u0001\u0004%\t\u0001L\u0001\ti\u0006\u001c8\u000eV5nKV\tQ\u0006\u0005\u0002\u0014]%\u0011q\u0006\u0006\u0002\u0005\u0019>tw\rC\u00042\u0001\u0001\u0007I\u0011\u0001\u001a\u0002\u0019Q\f7o\u001b+j[\u0016|F%Z9\u0015\u0005M2\u0004CA\n5\u0013\t)DC\u0001\u0003V]&$\bbB\u001c1\u0003\u0003\u0005\r!L\u0001\u0004q\u0012\n\u0004BB\u001d\u0001A\u0003&Q&A\u0005uCN\\G+[7fA!91\b\u0001a\u0001\n\u0003a\u0014AD:vG\u000e,W\rZ3e)\u0006\u001c8n]\u000b\u0002%!9a\b\u0001a\u0001\n\u0003y\u0014AE:vG\u000e,W\rZ3e)\u0006\u001c8n]0%KF$\"a\r!\t\u000f]j\u0014\u0011!a\u0001%!1!\t\u0001Q!\nI\tqb];dG\u0016,G-\u001a3UCN\\7\u000f\t\u0005\b\t\u0002\u0001\r\u0011\"\u0001=\u0003-1\u0017-\u001b7fIR\u000b7o[:\t\u000f\u0019\u0003\u0001\u0019!C\u0001\u000f\u0006ya-Y5mK\u0012$\u0016m]6t?\u0012*\u0017\u000f\u0006\u00024\u0011\"9q'RA\u0001\u0002\u0004\u0011\u0002B\u0002&\u0001A\u0003&!#\u0001\u0007gC&dW\r\u001a+bg.\u001c\b\u0005C\u0004M\u0001\u0001\u0007I\u0011\u0001\u001f\u0002\u0017-LG\u000e\\3e)\u0006\u001c8n\u001d\u0005\b\u001d\u0002\u0001\r\u0011\"\u0001P\u0003=Y\u0017\u000e\u001c7fIR\u000b7o[:`I\u0015\fHCA\u001aQ\u0011\u001d9T*!AA\u0002IAaA\u0015\u0001!B\u0013\u0011\u0012\u0001D6jY2,G\rV1tWN\u0004\u0003b\u0002+\u0001\u0001\u0004%\t!V\u0001\u000eSN\u0014E.Y2lY&\u001cH/\u001a3\u0016\u0003Y\u0003\"aE,\n\u0005a#\"a\u0002\"p_2,\u0017M\u001c\u0005\b5\u0002\u0001\r\u0011\"\u0001\\\u0003EI7O\u00117bG.d\u0017n\u001d;fI~#S-\u001d\u000b\u0003gqCqaN-\u0002\u0002\u0003\u0007a\u000b\u0003\u0004_\u0001\u0001\u0006KAV\u0001\u000fSN\u0014E.Y2lY&\u001cH/\u001a3!\u0011\u001d\u0001\u0007\u00011A\u0005\u0002\u0005\fq!\\3ue&\u001c7/F\u0001c!\t\u0019\u0007.D\u0001e\u0015\t)g-\u0001\u0002wc)\u0011qMA\u0001\u0004CBL\u0017BA5e\u0005-!\u0016m]6NKR\u0014\u0018nY:\t\u000f-\u0004\u0001\u0019!C\u0001Y\u0006YQ.\u001a;sS\u000e\u001cx\fJ3r)\t\u0019T\u000eC\u00048U\u0006\u0005\t\u0019\u00012\t\r=\u0004\u0001\u0015)\u0003c\u0003!iW\r\u001e:jGN\u0004\u0003\"B9\u0001\t#\u0012\u0018\u0001\u00033p+B$\u0017\r^3\u0015\u0003M\u0004\"a\u0005;\n\u0005U$\"aA!os\u0002"
)
public class LiveExecutorStageSummary extends LiveEntity {
   private final int stageId;
   private final int attemptId;
   private final String executorId;
   private long taskTime;
   private int succeededTasks;
   private int failedTasks;
   private int killedTasks;
   private boolean isBlacklisted;
   private TaskMetrics metrics;

   public long taskTime() {
      return this.taskTime;
   }

   public void taskTime_$eq(long x$1) {
      this.taskTime = x$1;
   }

   public int succeededTasks() {
      return this.succeededTasks;
   }

   public void succeededTasks_$eq(int x$1) {
      this.succeededTasks = x$1;
   }

   public int failedTasks() {
      return this.failedTasks;
   }

   public void failedTasks_$eq(int x$1) {
      this.failedTasks = x$1;
   }

   public int killedTasks() {
      return this.killedTasks;
   }

   public void killedTasks_$eq(int x$1) {
      this.killedTasks = x$1;
   }

   public boolean isBlacklisted() {
      return this.isBlacklisted;
   }

   public void isBlacklisted_$eq(boolean x$1) {
      this.isBlacklisted = x$1;
   }

   public TaskMetrics metrics() {
      return this.metrics;
   }

   public void metrics_$eq(TaskMetrics x$1) {
      this.metrics = x$1;
   }

   public Object doUpdate() {
      ExecutorStageSummary info = new ExecutorStageSummary(this.taskTime(), this.failedTasks(), this.succeededTasks(), this.killedTasks(), this.metrics().inputMetrics().bytesRead(), this.metrics().inputMetrics().recordsRead(), this.metrics().outputMetrics().bytesWritten(), this.metrics().outputMetrics().recordsWritten(), this.metrics().shuffleReadMetrics().remoteBytesRead() + this.metrics().shuffleReadMetrics().localBytesRead(), this.metrics().shuffleReadMetrics().recordsRead(), this.metrics().shuffleWriteMetrics().bytesWritten(), this.metrics().shuffleWriteMetrics().recordsWritten(), this.metrics().memoryBytesSpilled(), this.metrics().diskBytesSpilled(), this.isBlacklisted());
      return new ExecutorStageSummaryWrapper(this.stageId, this.attemptId, this.executorId, info);
   }

   public LiveExecutorStageSummary(int stageId, int attemptId, String executorId) {
      this.stageId = stageId;
      this.attemptId = attemptId;
      this.executorId = executorId;
      this.taskTime = 0L;
      this.succeededTasks = 0;
      this.failedTasks = 0;
      this.killedTasks = 0;
      this.isBlacklisted = false;
      this.metrics = LiveEntityHelpers$.MODULE$.createMetrics(0L);
   }
}
