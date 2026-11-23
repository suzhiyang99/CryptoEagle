package org.apache.spark.status;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.spark.scheduler.StageInfo;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
import org.apache.spark.status.api.v1.TaskMetrics;
import org.apache.spark.storage.RDDInfo;
import org.apache.spark.ui.SparkUI$;
import org.apache.spark.util.collection.OpenHashSet;
import org.apache.spark.util.collection.OpenHashSet$mcI$sp;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.mutable.HashMap;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\tEb\u0001B\u0001\u0003\t-\u0011\u0011\u0002T5wKN#\u0018mZ3\u000b\u0005\r!\u0011AB:uCR,8O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0007\u0011\u00055qQ\"\u0001\u0002\n\u0005=\u0011!A\u0003'jm\u0016,e\u000e^5us\")\u0011\u0003\u0001C\u0001%\u00051A(\u001b8jiz\"\u0012a\u0005\t\u0003\u001b\u0001Aq!\u0006\u0001A\u0002\u0013\u0005a#\u0001\u0003k_\n\u001cX#A\f\u0011\u0007air$D\u0001\u001a\u0015\tQ2$\u0001\u0006d_2dWm\u0019;j_:T\u0011\u0001H\u0001\u0006g\u000e\fG.Y\u0005\u0003=e\u00111aU3r!\ti\u0001%\u0003\u0002\"\u0005\t9A*\u001b<f\u0015>\u0014\u0007bB\u0012\u0001\u0001\u0004%\t\u0001J\u0001\tU>\u00147o\u0018\u0013fcR\u0011Q%\u000b\t\u0003M\u001dj\u0011aG\u0005\u0003Qm\u0011A!\u00168ji\"9!FIA\u0001\u0002\u00049\u0012a\u0001=%c!1A\u0006\u0001Q!\n]\tQA[8cg\u0002BqA\f\u0001A\u0002\u0013\u0005q&\u0001\u0004k_\nLEm]\u000b\u0002aA\u0019\u0011\u0007\u000e\u001c\u000e\u0003IR!aM\r\u0002\u0013%lW.\u001e;bE2,\u0017BA\u001b3\u0005\r\u0019V\r\u001e\t\u0003M]J!\u0001O\u000e\u0003\u0007%sG\u000fC\u0004;\u0001\u0001\u0007I\u0011A\u001e\u0002\u0015)|'-\u00133t?\u0012*\u0017\u000f\u0006\u0002&y!9!&OA\u0001\u0002\u0004\u0001\u0004B\u0002 \u0001A\u0003&\u0001'A\u0004k_\nLEm\u001d\u0011\t\u000f\u0001\u0003\u0001\u0019!C\u0001\u0003\u0006!\u0011N\u001c4p+\u0005\u0011\u0005CA\"G\u001b\u0005!%BA#\u0005\u0003%\u00198\r[3ek2,'/\u0003\u0002H\t\nI1\u000b^1hK&sgm\u001c\u0005\b\u0013\u0002\u0001\r\u0011\"\u0001K\u0003!IgNZ8`I\u0015\fHCA\u0013L\u0011\u001dQ\u0003*!AA\u0002\tCa!\u0014\u0001!B\u0013\u0011\u0015!B5oM>\u0004\u0003bB\u0002\u0001\u0001\u0004%\taT\u000b\u0002!B\u0011\u0011KV\u0007\u0002%*\u00111\u000bV\u0001\u0003mFR!!\u0016\u0002\u0002\u0007\u0005\u0004\u0018.\u0003\u0002X%\nY1\u000b^1hKN#\u0018\r^;t\u0011\u001dI\u0006\u00011A\u0005\u0002i\u000b!b\u001d;biV\u001cx\fJ3r)\t)3\fC\u0004+1\u0006\u0005\t\u0019\u0001)\t\ru\u0003\u0001\u0015)\u0003Q\u0003\u001d\u0019H/\u0019;vg\u0002Bqa\u0018\u0001A\u0002\u0013\u0005\u0001-A\u0006eKN\u001c'/\u001b9uS>tW#A1\u0011\u0007\u0019\u0012G-\u0003\u0002d7\t1q\n\u001d;j_:\u0004\"!\u001a5\u000f\u0005\u00192\u0017BA4\u001c\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011N\u001b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u001d\\\u0002b\u00027\u0001\u0001\u0004%\t!\\\u0001\u0010I\u0016\u001c8M]5qi&|gn\u0018\u0013fcR\u0011QE\u001c\u0005\bU-\f\t\u00111\u0001b\u0011\u0019\u0001\b\u0001)Q\u0005C\u0006aA-Z:de&\u0004H/[8oA!9!\u000f\u0001a\u0001\n\u0003\u0019\u0018AD:dQ\u0016$W\u000f\\5oOB{w\u000e\\\u000b\u0002I\"9Q\u000f\u0001a\u0001\n\u00031\u0018AE:dQ\u0016$W\u000f\\5oOB{w\u000e\\0%KF$\"!J<\t\u000f)\"\u0018\u0011!a\u0001I\"1\u0011\u0010\u0001Q!\n\u0011\fqb]2iK\u0012,H.\u001b8h!>|G\u000e\t\u0005\bw\u0002\u0001\r\u0011\"\u0001}\u0003-\t7\r^5wKR\u000b7o[:\u0016\u0003YBqA \u0001A\u0002\u0013\u0005q0A\bbGRLg/\u001a+bg.\u001cx\fJ3r)\r)\u0013\u0011\u0001\u0005\bUu\f\t\u00111\u00017\u0011\u001d\t)\u0001\u0001Q!\nY\nA\"Y2uSZ,G+Y:lg\u0002B\u0001\"!\u0003\u0001\u0001\u0004%\t\u0001`\u0001\u000fG>l\u0007\u000f\\3uK\u0012$\u0016m]6t\u0011%\ti\u0001\u0001a\u0001\n\u0003\ty!\u0001\nd_6\u0004H.\u001a;fIR\u000b7o[:`I\u0015\fHcA\u0013\u0002\u0012!A!&a\u0003\u0002\u0002\u0003\u0007a\u0007C\u0004\u0002\u0016\u0001\u0001\u000b\u0015\u0002\u001c\u0002\u001f\r|W\u000e\u001d7fi\u0016$G+Y:lg\u0002B\u0001\"!\u0007\u0001\u0001\u0004%\t\u0001`\u0001\fM\u0006LG.\u001a3UCN\\7\u000fC\u0005\u0002\u001e\u0001\u0001\r\u0011\"\u0001\u0002 \u0005ya-Y5mK\u0012$\u0016m]6t?\u0012*\u0017\u000fF\u0002&\u0003CA\u0001BKA\u000e\u0003\u0003\u0005\rA\u000e\u0005\b\u0003K\u0001\u0001\u0015)\u00037\u000311\u0017-\u001b7fIR\u000b7o[:!\u0011%\tI\u0003\u0001b\u0001\n\u0003\tY#\u0001\td_6\u0004H.\u001a;fI&sG-[2fgV\u0011\u0011Q\u0006\t\u0006\u0003_\t9DN\u0007\u0003\u0003cQ1AGA\u001a\u0015\r\t)\u0004B\u0001\u0005kRLG.\u0003\u0003\u0002:\u0005E\"aC(qK:D\u0015m\u001d5TKRD\u0001\"!\u0010\u0001A\u0003%\u0011QF\u0001\u0012G>l\u0007\u000f\\3uK\u0012Le\u000eZ5dKN\u0004\u0003\u0002CA!\u0001\u0001\u0007I\u0011\u0001?\u0002\u0017-LG\u000e\\3e)\u0006\u001c8n\u001d\u0005\n\u0003\u000b\u0002\u0001\u0019!C\u0001\u0003\u000f\nqb[5mY\u0016$G+Y:lg~#S-\u001d\u000b\u0004K\u0005%\u0003\u0002\u0003\u0016\u0002D\u0005\u0005\t\u0019\u0001\u001c\t\u000f\u00055\u0003\u0001)Q\u0005m\u0005a1.\u001b7mK\u0012$\u0016m]6tA!I\u0011\u0011\u000b\u0001A\u0002\u0013\u0005\u00111K\u0001\u000eW&dG.\u001a3Tk6l\u0017M]=\u0016\u0005\u0005U\u0003#B3\u0002X\u00114\u0014bAA-U\n\u0019Q*\u00199\t\u0013\u0005u\u0003\u00011A\u0005\u0002\u0005}\u0013!E6jY2,GmU;n[\u0006\u0014\u0018p\u0018\u0013fcR\u0019Q%!\u0019\t\u0013)\nY&!AA\u0002\u0005U\u0003\u0002CA3\u0001\u0001\u0006K!!\u0016\u0002\u001d-LG\u000e\\3e'VlW.\u0019:zA!I\u0011\u0011\u000e\u0001A\u0002\u0013\u0005\u00111N\u0001\u0010M&\u00148\u000f\u001e'bk:\u001c\u0007\u000eV5nKV\u0011\u0011Q\u000e\t\u0004M\u0005=\u0014bAA97\t!Aj\u001c8h\u0011%\t)\b\u0001a\u0001\n\u0003\t9(A\ngSJ\u001cH\u000fT1v]\u000eDG+[7f?\u0012*\u0017\u000fF\u0002&\u0003sB\u0011BKA:\u0003\u0003\u0005\r!!\u001c\t\u0011\u0005u\u0004\u0001)Q\u0005\u0003[\n\u0001CZ5sgRd\u0015-\u001e8dQRKW.\u001a\u0011\t\u0013\u0005\u0005\u0005\u00011A\u0005\u0002\u0005\r\u0015a\u00047pG\u0006d\u0017\u000e^=Tk6l\u0017M]=\u0016\u0005\u0005\u0015\u0005CB3\u0002X\u0011\fi\u0007C\u0005\u0002\n\u0002\u0001\r\u0011\"\u0001\u0002\f\u0006\u0019Bn\\2bY&$\u0018pU;n[\u0006\u0014\u0018p\u0018\u0013fcR\u0019Q%!$\t\u0013)\n9)!AA\u0002\u0005\u0015\u0005\u0002CAI\u0001\u0001\u0006K!!\"\u0002!1|7-\u00197jif\u001cV/\\7bef\u0004\u0003\"CAK\u0001\u0001\u0007I\u0011AAL\u0003\u001diW\r\u001e:jGN,\"!!'\u0011\u0007E\u000bY*C\u0002\u0002\u001eJ\u00131\u0002V1tW6+GO]5dg\"I\u0011\u0011\u0015\u0001A\u0002\u0013\u0005\u00111U\u0001\f[\u0016$(/[2t?\u0012*\u0017\u000fF\u0002&\u0003KC\u0011BKAP\u0003\u0003\u0005\r!!'\t\u0011\u0005%\u0006\u0001)Q\u0005\u00033\u000b\u0001\"\\3ue&\u001c7\u000f\t\u0005\n\u0003[\u0003!\u0019!C\u0001\u0003_\u000b\u0011#\u001a=fGV$xN]*v[6\f'/[3t+\t\t\t\fE\u0004\u00024\u0006eF-!0\u000e\u0005\u0005U&bAA\\3\u00059Q.\u001e;bE2,\u0017\u0002BA^\u0003k\u0013q\u0001S1tQ6\u000b\u0007\u000fE\u0002\u000e\u0003\u007fK1!!1\u0003\u0005aa\u0015N^3Fq\u0016\u001cW\u000f^8s'R\fw-Z*v[6\f'/\u001f\u0005\t\u0003\u000b\u0004\u0001\u0015!\u0003\u00022\u0006\u0011R\r_3dkR|'oU;n[\u0006\u0014\u0018.Z:!\u0011%\tI\r\u0001a\u0001\n\u0003\tY-\u0001\u0005dY\u0016\fg.\u001b8h+\t\ti\rE\u0002'\u0003\u001fL1!!5\u001c\u0005\u001d\u0011un\u001c7fC:D\u0011\"!6\u0001\u0001\u0004%\t!a6\u0002\u0019\rdW-\u00198j]\u001e|F%Z9\u0015\u0007\u0015\nI\u000eC\u0005+\u0003'\f\t\u00111\u0001\u0002N\"A\u0011Q\u001c\u0001!B\u0013\ti-A\u0005dY\u0016\fg.\u001b8hA!\"\u00111\\Aq!\r1\u00131]\u0005\u0004\u0003K\\\"\u0001\u0003<pY\u0006$\u0018\u000e\\3\t\u0013\u0005%\b\u00011A\u0005\u0002\u0005-\u0018AC:bm\u0016$G+Y:lgV\u0011\u0011Q\u001e\t\u0005\u0003_\fy0\u0004\u0002\u0002r*!\u00111_A{\u0003\u0019\tGo\\7jG*!\u0011q_A}\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u0005\u0003k\tYP\u0003\u0002\u0002~\u0006!!.\u0019<b\u0013\u0011\u0011\t!!=\u0003\u001b\u0005#x.\\5d\u0013:$XmZ3s\u0011%\u0011)\u0001\u0001a\u0001\n\u0003\u00119!\u0001\btCZ,G\rV1tWN|F%Z9\u0015\u0007\u0015\u0012I\u0001C\u0005+\u0005\u0007\t\t\u00111\u0001\u0002n\"A!Q\u0002\u0001!B\u0013\ti/A\u0006tCZ,G\rV1tWN\u0004\u0003b\u0002B\t\u0001\u0011\u0005!1C\u0001\u0010Kb,7-\u001e;peN+X.\\1ssR!\u0011Q\u0018B\u000b\u0011\u001d\u00119Ba\u0004A\u0002\u0011\f!\"\u001a=fGV$xN]%e\u0011\u001d\u0011Y\u0002\u0001C\u0001\u0005;\tQ\u0001^8Ba&$\"Aa\b\u0011\u0007E\u0013\t#C\u0002\u0003$I\u0013\u0011b\u0015;bO\u0016$\u0015\r^1\t\u000f\t\u001d\u0002\u0001\"\u0015\u0003*\u0005AAm\\+qI\u0006$X\r\u0006\u0002\u0003,A\u0019aE!\f\n\u0007\t=2DA\u0002B]f\u0004"
)
public class LiveStage extends LiveEntity {
   private Seq<LiveJob> jobs;
   private Set<Object> jobIds;
   private StageInfo info;
   private StageStatus status;
   private Option<String> description;
   private String schedulingPool;
   private int activeTasks;
   private int completedTasks;
   private int failedTasks;
   private final OpenHashSet<Object> completedIndices;
   private int killedTasks;
   private Map<String, Object> killedSummary;
   private long firstLaunchTime;
   private Map<String, Object> localitySummary;
   private TaskMetrics metrics;
   private final HashMap<String, LiveExecutorStageSummary> executorSummaries;
   private volatile boolean cleaning;
   private AtomicInteger savedTasks;

   public Seq<LiveJob> jobs() {
      return this.jobs;
   }

   public void jobs_$eq(Seq<LiveJob> x$1) {
      this.jobs = x$1;
   }

   public Set<Object> jobIds() {
      return this.jobIds;
   }

   public void jobIds_$eq(Set<Object> x$1) {
      this.jobIds = x$1;
   }

   public StageInfo info() {
      return this.info;
   }

   public void info_$eq(StageInfo x$1) {
      this.info = x$1;
   }

   public StageStatus status() {
      return this.status;
   }

   public void status_$eq(StageStatus x$1) {
      this.status = x$1;
   }

   public Option<String> description() {
      return this.description;
   }

   public void description_$eq(Option<String> x$1) {
      this.description = x$1;
   }

   public String schedulingPool() {
      return this.schedulingPool;
   }

   public void schedulingPool_$eq(String x$1) {
      this.schedulingPool = x$1;
   }

   public int activeTasks() {
      return this.activeTasks;
   }

   public void activeTasks_$eq(int x$1) {
      this.activeTasks = x$1;
   }

   public int completedTasks() {
      return this.completedTasks;
   }

   public void completedTasks_$eq(int x$1) {
      this.completedTasks = x$1;
   }

   public int failedTasks() {
      return this.failedTasks;
   }

   public void failedTasks_$eq(int x$1) {
      this.failedTasks = x$1;
   }

   public OpenHashSet<Object> completedIndices() {
      return this.completedIndices;
   }

   public int killedTasks() {
      return this.killedTasks;
   }

   public void killedTasks_$eq(int x$1) {
      this.killedTasks = x$1;
   }

   public Map<String, Object> killedSummary() {
      return this.killedSummary;
   }

   public void killedSummary_$eq(Map<String, Object> x$1) {
      this.killedSummary = x$1;
   }

   public long firstLaunchTime() {
      return this.firstLaunchTime;
   }

   public void firstLaunchTime_$eq(long x$1) {
      this.firstLaunchTime = x$1;
   }

   public Map<String, Object> localitySummary() {
      return this.localitySummary;
   }

   public void localitySummary_$eq(Map<String, Object> x$1) {
      this.localitySummary = x$1;
   }

   public TaskMetrics metrics() {
      return this.metrics;
   }

   public void metrics_$eq(TaskMetrics x$1) {
      this.metrics = x$1;
   }

   public HashMap<String, LiveExecutorStageSummary> executorSummaries() {
      return this.executorSummaries;
   }

   public boolean cleaning() {
      return this.cleaning;
   }

   public void cleaning_$eq(boolean x$1) {
      this.cleaning = x$1;
   }

   public AtomicInteger savedTasks() {
      return this.savedTasks;
   }

   public void savedTasks_$eq(AtomicInteger x$1) {
      this.savedTasks = x$1;
   }

   public LiveExecutorStageSummary executorSummary(String executorId) {
      return (LiveExecutorStageSummary)this.executorSummaries().getOrElseUpdate(executorId, new Serializable(this, executorId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LiveStage $outer;
         private final String executorId$1;

         public final LiveExecutorStageSummary apply() {
            return new LiveExecutorStageSummary(this.$outer.info().stageId(), this.$outer.info().attemptNumber(), this.executorId$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.executorId$1 = executorId$1;
            }
         }
      });
   }

   public StageData toApi() {
      return new StageData(this.status(), this.info().stageId(), this.info().attemptNumber(), this.info().numTasks(), this.activeTasks(), this.completedTasks(), this.failedTasks(), this.killedTasks(), this.completedIndices().size(), this.metrics().executorRunTime(), this.metrics().executorCpuTime(), this.info().submissionTime().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Date apply(long x$1) {
            return new Date(x$1);
         }
      }), (Option)(this.firstLaunchTime() < Long.MAX_VALUE ? new Some(new Date(this.firstLaunchTime())) : None$.MODULE$), this.info().completionTime().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Date apply(long x$2) {
            return new Date(x$2);
         }
      }), this.info().failureReason(), this.metrics().inputMetrics().bytesRead(), this.metrics().inputMetrics().recordsRead(), this.metrics().outputMetrics().bytesWritten(), this.metrics().outputMetrics().recordsWritten(), this.metrics().shuffleReadMetrics().localBytesRead() + this.metrics().shuffleReadMetrics().remoteBytesRead(), this.metrics().shuffleReadMetrics().recordsRead(), this.metrics().shuffleWriteMetrics().bytesWritten(), this.metrics().shuffleWriteMetrics().recordsWritten(), this.metrics().memoryBytesSpilled(), this.metrics().diskBytesSpilled(), this.info().name(), this.description(), this.info().details(), this.schedulingPool(), (Seq)this.info().rddInfos().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(RDDInfo x$3) {
            return x$3.id();
         }
      }, Seq$.MODULE$.canBuildFrom()), LiveEntityHelpers$.MODULE$.newAccumulatorInfos(this.info().accumulables().values()), None$.MODULE$, None$.MODULE$, this.killedSummary());
   }

   public Object doUpdate() {
      return new StageDataWrapper(this.toApi(), this.jobIds(), this.localitySummary());
   }

   public LiveStage() {
      this.jobs = (Seq)Seq$.MODULE$.apply(Nil$.MODULE$);
      this.jobIds = (Set)Predef$.MODULE$.Set().apply(Nil$.MODULE$);
      this.info = null;
      this.status = StageStatus.PENDING;
      this.description = None$.MODULE$;
      this.schedulingPool = SparkUI$.MODULE$.DEFAULT_POOL_NAME();
      this.activeTasks = 0;
      this.completedTasks = 0;
      this.failedTasks = 0;
      this.completedIndices = new OpenHashSet$mcI$sp(ClassTag$.MODULE$.Int());
      this.killedTasks = 0;
      this.killedSummary = (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$);
      this.firstLaunchTime = Long.MAX_VALUE;
      this.localitySummary = (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$);
      this.metrics = LiveEntityHelpers$.MODULE$.createMetrics(0L);
      this.executorSummaries = new HashMap<String, LiveExecutorStageSummary>();
      this.cleaning = false;
      this.savedTasks = new AtomicInteger(0);
   }
}
