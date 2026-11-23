package org.apache.spark.status;

import org.apache.spark.scheduler.TaskInfo;
import org.apache.spark.status.api.v1.TaskMetrics;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u000194A!\u0001\u0002\u0005\u0017\tAA*\u001b<f)\u0006\u001c8N\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0002\u000e\u001d5\t!!\u0003\u0002\u0010\u0005\tQA*\u001b<f\u000b:$\u0018\u000e^=\t\u0011E\u0001!\u00111A\u0005\u0002I\tA!\u001b8g_V\t1\u0003\u0005\u0002\u0015/5\tQC\u0003\u0002\u0017\t\u0005I1o\u00195fIVdWM]\u0005\u00031U\u0011\u0001\u0002V1tW&sgm\u001c\u0005\t5\u0001\u0011\t\u0019!C\u00017\u0005A\u0011N\u001c4p?\u0012*\u0017\u000f\u0006\u0002\u001dEA\u0011Q\u0004I\u0007\u0002=)\tq$A\u0003tG\u0006d\u0017-\u0003\u0002\"=\t!QK\\5u\u0011\u001d\u0019\u0013$!AA\u0002M\t1\u0001\u001f\u00132\u0011!)\u0003A!A!B\u0013\u0019\u0012!B5oM>\u0004\u0003\u0002C\u0014\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0015\u0002\u000fM$\u0018mZ3JIB\u0011Q$K\u0005\u0003Uy\u00111!\u00138u\u0011!a\u0003A!A!\u0002\u0013A\u0013AD:uC\u001e,\u0017\t\u001e;f[B$\u0018\n\u001a\u0005\t]\u0001\u0011\t\u0011)A\u0005_\u0005qA.Y:u+B$\u0017\r^3US6,\u0007cA\u000f1e%\u0011\u0011G\b\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005u\u0019\u0014B\u0001\u001b\u001f\u0005\u0011auN\\4\t\u000bY\u0002A\u0011A\u001c\u0002\rqJg.\u001b;?)\u0015A\u0014HO\u001e=!\ti\u0001\u0001C\u0003\u0012k\u0001\u00071\u0003C\u0003(k\u0001\u0007\u0001\u0006C\u0003-k\u0001\u0007\u0001\u0006C\u0003/k\u0001\u0007q\u0006C\u0004?\u0001\u0001\u0007I\u0011B \u0002\u000f5,GO]5dgV\t\u0001\t\u0005\u0002B\r6\t!I\u0003\u0002D\t\u0006\u0011a/\r\u0006\u0003\u000b\n\t1!\u00199j\u0013\t9%IA\u0006UCN\\W*\u001a;sS\u000e\u001c\bbB%\u0001\u0001\u0004%IAS\u0001\f[\u0016$(/[2t?\u0012*\u0017\u000f\u0006\u0002\u001d\u0017\"91\u0005SA\u0001\u0002\u0004\u0001\u0005BB'\u0001A\u0003&\u0001)\u0001\u0005nKR\u0014\u0018nY:!\u0011\u001dy\u0005\u00011A\u0005\u0002A\u000bA\"\u001a:s_JlUm]:bO\u0016,\u0012!\u0015\t\u0004;A\u0012\u0006CA*W\u001d\tiB+\u0003\u0002V=\u00051\u0001K]3eK\u001aL!a\u0016-\u0003\rM#(/\u001b8h\u0015\t)f\u0004C\u0004[\u0001\u0001\u0007I\u0011A.\u0002!\u0015\u0014(o\u001c:NKN\u001c\u0018mZ3`I\u0015\fHC\u0001\u000f]\u0011\u001d\u0019\u0013,!AA\u0002ECaA\u0018\u0001!B\u0013\t\u0016!D3se>\u0014X*Z:tC\u001e,\u0007\u0005C\u0003a\u0001\u0011\u0005\u0011-A\u0007va\u0012\fG/Z'fiJL7m\u001d\u000b\u0003\u0001\nDQAP0A\u0002\r\u0004\"\u0001Z4\u000e\u0003\u0015T!A\u001a\u0003\u0002\u0011\u0015DXmY;u_JL!aR3\t\u000b%\u0004A\u0011\u000b6\u0002\u0011\u0011|W\u000b\u001d3bi\u0016$\u0012a\u001b\t\u0003;1L!!\u001c\u0010\u0003\u0007\u0005s\u0017\u0010"
)
public class LiveTask extends LiveEntity {
   private TaskInfo info;
   private final int stageId;
   private final int stageAttemptId;
   private final Option<Object> lastUpdateTime;
   private TaskMetrics metrics;
   private Option<String> errorMessage;

   public TaskInfo info() {
      return this.info;
   }

   public void info_$eq(TaskInfo x$1) {
      this.info = x$1;
   }

   private TaskMetrics metrics() {
      return this.metrics;
   }

   private void metrics_$eq(TaskMetrics x$1) {
      this.metrics = x$1;
   }

   public Option<String> errorMessage() {
      return this.errorMessage;
   }

   public void errorMessage_$eq(Option<String> x$1) {
      this.errorMessage = x$1;
   }

   public TaskMetrics updateMetrics(org.apache.spark.executor.TaskMetrics metrics) {
      TaskMetrics var10000;
      if (metrics == null) {
         var10000 = null;
      } else {
         TaskMetrics old = this.metrics();
         TaskMetrics newMetrics = LiveEntityHelpers$.MODULE$.createMetrics(metrics.executorDeserializeTime(), metrics.executorDeserializeCpuTime(), metrics.executorRunTime(), metrics.executorCpuTime(), metrics.resultSize(), metrics.jvmGCTime(), metrics.resultSerializationTime(), metrics.memoryBytesSpilled(), metrics.diskBytesSpilled(), metrics.peakExecutionMemory(), metrics.inputMetrics().bytesRead(), metrics.inputMetrics().recordsRead(), metrics.outputMetrics().bytesWritten(), metrics.outputMetrics().recordsWritten(), metrics.shuffleReadMetrics().remoteBlocksFetched(), metrics.shuffleReadMetrics().localBlocksFetched(), metrics.shuffleReadMetrics().fetchWaitTime(), metrics.shuffleReadMetrics().remoteBytesRead(), metrics.shuffleReadMetrics().remoteBytesReadToDisk(), metrics.shuffleReadMetrics().localBytesRead(), metrics.shuffleReadMetrics().recordsRead(), metrics.shuffleWriteMetrics().bytesWritten(), metrics.shuffleWriteMetrics().writeTime(), metrics.shuffleWriteMetrics().recordsWritten());
         this.metrics_$eq(newMetrics);
         var10000 = old.executorDeserializeTime() >= 0L ? LiveEntityHelpers$.MODULE$.subtractMetrics(newMetrics, old) : newMetrics;
      }

      return var10000;
   }

   public Object doUpdate() {
      long duration = this.info().finished() ? this.info().duration() : this.info().timeRunning(BoxesRunTime.unboxToLong(this.lastUpdateTime.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return System.currentTimeMillis();
         }
      })));
      return new TaskDataWrapper(Predef$.MODULE$.long2Long(this.info().taskId()), this.info().index(), this.info().attemptNumber(), this.info().launchTime(), this.info().gettingResult() ? this.info().gettingResultTime() : -1L, duration, LiveEntityHelpers$.MODULE$.weakIntern(this.info().executorId()), LiveEntityHelpers$.MODULE$.weakIntern(this.info().host()), LiveEntityHelpers$.MODULE$.weakIntern(this.info().status()), LiveEntityHelpers$.MODULE$.weakIntern(this.info().taskLocality().toString()), this.info().speculative(), LiveEntityHelpers$.MODULE$.newAccumulatorInfos(this.info().accumulables()), this.errorMessage(), this.metrics().executorDeserializeTime(), this.metrics().executorDeserializeCpuTime(), this.metrics().executorRunTime(), this.metrics().executorCpuTime(), this.metrics().resultSize(), this.metrics().jvmGcTime(), this.metrics().resultSerializationTime(), this.metrics().memoryBytesSpilled(), this.metrics().diskBytesSpilled(), this.metrics().peakExecutionMemory(), this.metrics().inputMetrics().bytesRead(), this.metrics().inputMetrics().recordsRead(), this.metrics().outputMetrics().bytesWritten(), this.metrics().outputMetrics().recordsWritten(), this.metrics().shuffleReadMetrics().remoteBlocksFetched(), this.metrics().shuffleReadMetrics().localBlocksFetched(), this.metrics().shuffleReadMetrics().fetchWaitTime(), this.metrics().shuffleReadMetrics().remoteBytesRead(), this.metrics().shuffleReadMetrics().remoteBytesReadToDisk(), this.metrics().shuffleReadMetrics().localBytesRead(), this.metrics().shuffleReadMetrics().recordsRead(), this.metrics().shuffleWriteMetrics().bytesWritten(), this.metrics().shuffleWriteMetrics().writeTime(), this.metrics().shuffleWriteMetrics().recordsWritten(), this.stageId, this.stageAttemptId);
   }

   public LiveTask(TaskInfo info, int stageId, int stageAttemptId, Option<Object> lastUpdateTime) {
      this.info = info;
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.lastUpdateTime = lastUpdateTime;
      super();
      this.metrics = LiveEntityHelpers$.MODULE$.createMetrics(-1L);
      this.errorMessage = None$.MODULE$;
   }
}
