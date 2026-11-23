package org.apache.spark.scheduler;

import com.codahale.metrics.Timer;
import java.util.Properties;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.CallSite;
import org.apache.spark.util.EventLoop;
import scala.Function2;
import scala.MatchError;
import scala.Option;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001U3Q!\u0001\u0002\u0001\u0005)\u0011A\u0004R!H'\u000eDW\rZ;mKJ,e/\u001a8u!J|7-Z:t\u0019>|\u0007O\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\u000b\u0011\u00071y\u0011#D\u0001\u000e\u0015\tqA!\u0001\u0003vi&d\u0017B\u0001\t\u000e\u0005%)e/\u001a8u\u0019>|\u0007\u000f\u0005\u0002\u0013'5\t!!\u0003\u0002\u0015\u0005\t\tB)Q$TG\",G-\u001e7fe\u00163XM\u001c;\u0011\u0005YIR\"A\f\u000b\u0005a!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005i9\"a\u0002'pO\u001eLgn\u001a\u0005\t9\u0001\u0011\t\u0011)A\u0005=\u0005aA-Y4TG\",G-\u001e7fe\u000e\u0001\u0001C\u0001\n \u0013\t\u0001#A\u0001\u0007E\u0003\u001e\u001b6\r[3ek2,'\u000fC\u0003#\u0001\u0011\u00051%\u0001\u0004=S:LGO\u0010\u000b\u0003I\u0015\u0002\"A\u0005\u0001\t\u000bq\t\u0003\u0019\u0001\u0010\t\r\u001d\u0002\u0001\u0015!\u0003)\u0003\u0015!\u0018.\\3s!\tI\u0003'D\u0001+\u0015\tYC&A\u0004nKR\u0014\u0018nY:\u000b\u00055r\u0013\u0001C2pI\u0006D\u0017\r\\3\u000b\u0003=\n1aY8n\u0013\t\t$FA\u0003US6,'\u000fC\u00034\u0001\u0011\u0005C'A\u0005p]J+7-Z5wKR\u0011Qg\u000f\t\u0003mej\u0011a\u000e\u0006\u0002q\u0005)1oY1mC&\u0011!h\u000e\u0002\u0005+:LG\u000fC\u0003=e\u0001\u0007\u0011#A\u0003fm\u0016tG\u000fC\u0003?\u0001\u0011%q(A\u0006e_>s'+Z2fSZ,GCA\u001bA\u0011\u0015aT\b1\u0001\u0012\u0011\u0015\u0011\u0005\u0001\"\u0011D\u0003\u001dyg.\u0012:s_J$\"!\u000e#\t\u000b\u0015\u000b\u0005\u0019\u0001$\u0002\u0003\u0015\u0004\"aR(\u000f\u0005!keBA%M\u001b\u0005Q%BA&\u001e\u0003\u0019a$o\\8u}%\t\u0001(\u0003\u0002Oo\u00059\u0001/Y2lC\u001e,\u0017B\u0001)R\u0005%!\u0006N]8xC\ndWM\u0003\u0002Oo!)1\u000b\u0001C!)\u00061qN\\*u_B$\u0012!\u000e"
)
public class DAGSchedulerEventProcessLoop extends EventLoop<DAGSchedulerEvent> {
   private final DAGScheduler dagScheduler;
   private final Timer timer;

   public void onReceive(DAGSchedulerEvent event) {
      Timer.Context timerContext = this.timer.time();

      try {
         this.doOnReceive(event);
      } finally {
         timerContext.stop();
      }

   }

   private void doOnReceive(DAGSchedulerEvent event) {
      if (event instanceof JobSubmitted) {
         JobSubmitted var3 = (JobSubmitted)event;
         int jobId = var3.jobId();
         RDD rdd = var3.finalRDD();
         Function2 func = var3.func();
         int[] partitions = var3.partitions();
         CallSite callSite = var3.callSite();
         JobListener listener = var3.listener();
         Properties properties = var3.properties();
         this.dagScheduler.handleJobSubmitted(jobId, rdd, func, partitions, callSite, listener, properties);
         BoxedUnit var11 = BoxedUnit.UNIT;
      } else if (event instanceof MapStageSubmitted) {
         MapStageSubmitted var12 = (MapStageSubmitted)event;
         int jobId = var12.jobId();
         ShuffleDependency dependency = var12.dependency();
         CallSite callSite = var12.callSite();
         JobListener listener = var12.listener();
         Properties properties = var12.properties();
         this.dagScheduler.handleMapStageSubmitted(jobId, dependency, callSite, listener, properties);
         BoxedUnit var53 = BoxedUnit.UNIT;
      } else if (event instanceof StageCancelled) {
         StageCancelled var18 = (StageCancelled)event;
         int stageId = var18.stageId();
         Option reason = var18.reason();
         this.dagScheduler.handleStageCancellation(stageId, reason);
         BoxedUnit var54 = BoxedUnit.UNIT;
      } else if (event instanceof JobCancelled) {
         JobCancelled var21 = (JobCancelled)event;
         int jobId = var21.jobId();
         Option reason = var21.reason();
         this.dagScheduler.handleJobCancellation(jobId, reason);
         BoxedUnit var55 = BoxedUnit.UNIT;
      } else if (event instanceof JobGroupCancelled) {
         JobGroupCancelled var24 = (JobGroupCancelled)event;
         String groupId = var24.groupId();
         this.dagScheduler.handleJobGroupCancelled(groupId);
         BoxedUnit var56 = BoxedUnit.UNIT;
      } else if (AllJobsCancelled$.MODULE$.equals(event)) {
         this.dagScheduler.doCancelAllJobs();
         BoxedUnit var57 = BoxedUnit.UNIT;
      } else if (event instanceof ExecutorAdded) {
         ExecutorAdded var26 = (ExecutorAdded)event;
         String execId = var26.execId();
         String host = var26.host();
         this.dagScheduler.handleExecutorAdded(execId, host);
         BoxedUnit var58 = BoxedUnit.UNIT;
      } else if (event instanceof ExecutorLost) {
         String execId;
         boolean workerLost;
         label51: {
            ExecutorLost var29 = (ExecutorLost)event;
            execId = var29.execId();
            ExecutorLossReason reason = var29.reason();
            if (reason instanceof SlaveLost) {
               SlaveLost var34 = (SlaveLost)reason;
               boolean var35 = var34.workerLost();
               if (var35) {
                  workerLost = true;
                  break label51;
               }
            }

            workerLost = false;
         }

         this.dagScheduler.handleExecutorLost(execId, workerLost);
         BoxedUnit var59 = BoxedUnit.UNIT;
      } else if (event instanceof WorkerRemoved) {
         WorkerRemoved var37 = (WorkerRemoved)event;
         String workerId = var37.workerId();
         String host = var37.host();
         String message = var37.message();
         this.dagScheduler.handleWorkerRemoved(workerId, host, message);
         BoxedUnit var60 = BoxedUnit.UNIT;
      } else if (event instanceof BeginEvent) {
         BeginEvent var41 = (BeginEvent)event;
         Task task = var41.task();
         TaskInfo taskInfo = var41.taskInfo();
         this.dagScheduler.handleBeginEvent(task, taskInfo);
         BoxedUnit var61 = BoxedUnit.UNIT;
      } else if (event instanceof SpeculativeTaskSubmitted) {
         SpeculativeTaskSubmitted var44 = (SpeculativeTaskSubmitted)event;
         Task task = var44.task();
         this.dagScheduler.handleSpeculativeTaskSubmitted(task);
         BoxedUnit var62 = BoxedUnit.UNIT;
      } else if (event instanceof GettingResultEvent) {
         GettingResultEvent var46 = (GettingResultEvent)event;
         TaskInfo taskInfo = var46.taskInfo();
         this.dagScheduler.handleGetTaskResult(taskInfo);
         BoxedUnit var63 = BoxedUnit.UNIT;
      } else if (event instanceof CompletionEvent) {
         CompletionEvent var48 = (CompletionEvent)event;
         this.dagScheduler.handleTaskCompletion(var48);
         BoxedUnit var64 = BoxedUnit.UNIT;
      } else if (event instanceof TaskSetFailed) {
         TaskSetFailed var49 = (TaskSetFailed)event;
         TaskSet taskSet = var49.taskSet();
         String reason = var49.reason();
         Option exception = var49.exception();
         this.dagScheduler.handleTaskSetFailed(taskSet, reason, exception);
         BoxedUnit var65 = BoxedUnit.UNIT;
      } else {
         if (!ResubmitFailedStages$.MODULE$.equals(event)) {
            throw new MatchError(event);
         }

         this.dagScheduler.resubmitFailedStages();
         BoxedUnit var66 = BoxedUnit.UNIT;
      }

   }

   public void onError(Throwable e) {
      this.logError(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "DAGSchedulerEventProcessLoop failed; shutting down SparkContext";
         }
      }, e);

      label23:
      try {
         this.dagScheduler.doCancelAllJobs();
      } catch (Throwable var4) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "DAGScheduler failed to cancel all jobs.";
            }
         }, var4);
         break label23;
      }

      this.dagScheduler.sc().stopInNewThread();
   }

   public void onStop() {
      this.dagScheduler.cleanUpAfterSchedulerStop();
   }

   public DAGSchedulerEventProcessLoop(DAGScheduler dagScheduler) {
      super("dag-scheduler-event-loop");
      this.dagScheduler = dagScheduler;
      this.timer = dagScheduler.metricsSource().messageProcessingTimer();
   }
}
