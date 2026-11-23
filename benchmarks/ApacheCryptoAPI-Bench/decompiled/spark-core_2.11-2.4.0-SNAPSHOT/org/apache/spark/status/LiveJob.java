package org.apache.spark.status;

import java.util.Date;
import org.apache.spark.JobExecutionStatus;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.util.collection.OpenHashSet;
import org.apache.spark.util.collection.OpenHashSet$mcJ$sp;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005uf\u0001B\u0001\u0003\t-\u0011q\u0001T5wK*{'M\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0002\u000e\u001d5\t!!\u0003\u0002\u0010\u0005\tQA*\u001b<f\u000b:$\u0018\u000e^=\t\u0011E\u0001!Q1A\u0005\u0002I\tQA[8c\u0013\u0012,\u0012a\u0005\t\u0003)]i\u0011!\u0006\u0006\u0002-\u0005)1oY1mC&\u0011\u0001$\u0006\u0002\u0004\u0013:$\b\u0002\u0003\u000e\u0001\u0005\u0003\u0005\u000b\u0011B\n\u0002\r)|'-\u00133!\u0011!a\u0002A!A!\u0002\u0013i\u0012\u0001\u00028b[\u0016\u0004\"AH\u0011\u000f\u0005Qy\u0012B\u0001\u0011\u0016\u0003\u0019\u0001&/\u001a3fM&\u0011!e\t\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u0001*\u0002\u0002C\u0013\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0014\u0002\u001dM,(-\\5tg&|g\u000eV5nKB\u0019AcJ\u0015\n\u0005!*\"AB(qi&|g\u000e\u0005\u0002+_5\t1F\u0003\u0002-[\u0005!Q\u000f^5m\u0015\u0005q\u0013\u0001\u00026bm\u0006L!\u0001M\u0016\u0003\t\u0011\u000bG/\u001a\u0005\te\u0001\u0011)\u0019!C\u0001g\u0005A1\u000f^1hK&#7/F\u00015!\r)Th\u0005\b\u0003mmr!a\u000e\u001e\u000e\u0003aR!!\u000f\u0006\u0002\rq\u0012xn\u001c;?\u0013\u00051\u0012B\u0001\u001f\u0016\u0003\u001d\u0001\u0018mY6bO\u0016L!AP \u0003\u0007M+\u0017O\u0003\u0002=+!A\u0011\t\u0001B\u0001B\u0003%A'A\u0005ti\u0006<W-\u00133tA!A1\t\u0001B\u0001B\u0003%A)\u0001\u0005k_\n<%o\\;q!\r!r%\b\u0005\t\r\u0002\u0011\t\u0011)A\u0005'\u0005Aa.^7UCN\\7\u000fC\u0003I\u0001\u0011\u0005\u0011*\u0001\u0004=S:LGO\u0010\u000b\b\u0015.cUJT(Q!\ti\u0001\u0001C\u0003\u0012\u000f\u0002\u00071\u0003C\u0003\u001d\u000f\u0002\u0007Q\u0004C\u0003&\u000f\u0002\u0007a\u0005C\u00033\u000f\u0002\u0007A\u0007C\u0003D\u000f\u0002\u0007A\tC\u0003G\u000f\u0002\u00071\u0003C\u0004S\u0001\u0001\u0007I\u0011\u0001\n\u0002\u0017\u0005\u001cG/\u001b<f)\u0006\u001c8n\u001d\u0005\b)\u0002\u0001\r\u0011\"\u0001V\u0003=\t7\r^5wKR\u000b7o[:`I\u0015\fHC\u0001,Z!\t!r+\u0003\u0002Y+\t!QK\\5u\u0011\u001dQ6+!AA\u0002M\t1\u0001\u001f\u00132\u0011\u0019a\u0006\u0001)Q\u0005'\u0005a\u0011m\u0019;jm\u0016$\u0016m]6tA!9a\f\u0001a\u0001\n\u0003\u0011\u0012AD2p[BdW\r^3e)\u0006\u001c8n\u001d\u0005\bA\u0002\u0001\r\u0011\"\u0001b\u0003I\u0019w.\u001c9mKR,G\rV1tWN|F%Z9\u0015\u0005Y\u0013\u0007b\u0002.`\u0003\u0003\u0005\ra\u0005\u0005\u0007I\u0002\u0001\u000b\u0015B\n\u0002\u001f\r|W\u000e\u001d7fi\u0016$G+Y:lg\u0002BqA\u001a\u0001A\u0002\u0013\u0005!#A\u0006gC&dW\r\u001a+bg.\u001c\bb\u00025\u0001\u0001\u0004%\t![\u0001\u0010M\u0006LG.\u001a3UCN\\7o\u0018\u0013fcR\u0011aK\u001b\u0005\b5\u001e\f\t\u00111\u0001\u0014\u0011\u0019a\u0007\u0001)Q\u0005'\u0005aa-Y5mK\u0012$\u0016m]6tA!9a\u000e\u0001b\u0001\n\u0003y\u0017\u0001E2p[BdW\r^3e\u0013:$\u0017nY3t+\u0005\u0001\bcA9vo6\t!O\u0003\u0002ti\u0006Q1m\u001c7mK\u000e$\u0018n\u001c8\u000b\u00051\"\u0011B\u0001<s\u0005-y\u0005/\u001a8ICND7+\u001a;\u0011\u0005QA\u0018BA=\u0016\u0005\u0011auN\\4\t\rm\u0004\u0001\u0015!\u0003q\u0003E\u0019w.\u001c9mKR,G-\u00138eS\u000e,7\u000f\t\u0005\b{\u0002\u0001\r\u0011\"\u0001\u0013\u0003-Y\u0017\u000e\u001c7fIR\u000b7o[:\t\u0011}\u0004\u0001\u0019!C\u0001\u0003\u0003\tqb[5mY\u0016$G+Y:lg~#S-\u001d\u000b\u0004-\u0006\r\u0001b\u0002.\u007f\u0003\u0003\u0005\ra\u0005\u0005\b\u0003\u000f\u0001\u0001\u0015)\u0003\u0014\u00031Y\u0017\u000e\u001c7fIR\u000b7o[:!\u0011%\tY\u0001\u0001a\u0001\n\u0003\ti!A\u0007lS2dW\rZ*v[6\f'/_\u000b\u0003\u0003\u001f\u0001RAHA\t;MI1!a\u0005$\u0005\ri\u0015\r\u001d\u0005\n\u0003/\u0001\u0001\u0019!C\u0001\u00033\t\u0011c[5mY\u0016$7+^7nCJLx\fJ3r)\r1\u00161\u0004\u0005\n5\u0006U\u0011\u0011!a\u0001\u0003\u001fA\u0001\"a\b\u0001A\u0003&\u0011qB\u0001\u000fW&dG.\u001a3Tk6l\u0017M]=!\u0011!\t\u0019\u0003\u0001a\u0001\n\u0003\u0011\u0012\u0001D:lSB\u0004X\r\u001a+bg.\u001c\b\"CA\u0014\u0001\u0001\u0007I\u0011AA\u0015\u0003A\u00198.\u001b9qK\u0012$\u0016m]6t?\u0012*\u0017\u000fF\u0002W\u0003WA\u0001BWA\u0013\u0003\u0003\u0005\ra\u0005\u0005\b\u0003_\u0001\u0001\u0015)\u0003\u0014\u00035\u00198.\u001b9qK\u0012$\u0016m]6tA!I\u00111\u0007\u0001A\u0002\u0013\u0005\u0011QG\u0001\u000eg.L\u0007\u000f]3e'R\fw-Z:\u0016\u0005\u0005]\u0002#BA\u001d\u0003\u0003\u001aRBAA\u001e\u0015\u0011\ti$a\u0010\u0002\u0013%lW.\u001e;bE2,'BA:\u0016\u0013\u0011\t\u0019%a\u000f\u0003\u0007M+G\u000fC\u0005\u0002H\u0001\u0001\r\u0011\"\u0001\u0002J\u0005\t2o[5qa\u0016$7\u000b^1hKN|F%Z9\u0015\u0007Y\u000bY\u0005C\u0005[\u0003\u000b\n\t\u00111\u0001\u00028!A\u0011q\n\u0001!B\u0013\t9$\u0001\btW&\u0004\b/\u001a3Ti\u0006<Wm\u001d\u0011\t\u0011\r\u0001\u0001\u0019!C\u0001\u0003'*\"!!\u0016\u0011\t\u0005]\u0013\u0011L\u0007\u0002\t%\u0019\u00111\f\u0003\u0003%){'-\u0012=fGV$\u0018n\u001c8Ti\u0006$Xo\u001d\u0005\n\u0003?\u0002\u0001\u0019!C\u0001\u0003C\n!b\u001d;biV\u001cx\fJ3r)\r1\u00161\r\u0005\n5\u0006u\u0013\u0011!a\u0001\u0003+B\u0001\"a\u001a\u0001A\u0003&\u0011QK\u0001\bgR\fG/^:!\u0011%\tY\u0007\u0001a\u0001\n\u0003\ti'\u0001\bd_6\u0004H.\u001a;j_:$\u0016.\\3\u0016\u0003\u0019B\u0011\"!\u001d\u0001\u0001\u0004%\t!a\u001d\u0002%\r|W\u000e\u001d7fi&|g\u000eV5nK~#S-\u001d\u000b\u0004-\u0006U\u0004\u0002\u0003.\u0002p\u0005\u0005\t\u0019\u0001\u0014\t\u000f\u0005e\u0004\u0001)Q\u0005M\u0005y1m\\7qY\u0016$\u0018n\u001c8US6,\u0007\u0005C\u0005\u0002~\u0001\u0001\r\u0011\"\u0001\u0002\u0000\u0005y1m\\7qY\u0016$X\rZ*uC\u001e,7/\u0006\u0002\u0002\u0002B!a$a!\u0014\u0013\r\t\u0019e\t\u0005\n\u0003\u000f\u0003\u0001\u0019!C\u0001\u0003\u0013\u000b1cY8na2,G/\u001a3Ti\u0006<Wm]0%KF$2AVAF\u0011%Q\u0016QQA\u0001\u0002\u0004\t\t\t\u0003\u0005\u0002\u0010\u0002\u0001\u000b\u0015BAA\u0003A\u0019w.\u001c9mKR,Gm\u0015;bO\u0016\u001c\b\u0005\u0003\u0005\u0002\u0014\u0002\u0001\r\u0011\"\u0001\u0013\u00031\t7\r^5wKN#\u0018mZ3t\u0011%\t9\n\u0001a\u0001\n\u0003\tI*\u0001\tbGRLg/Z*uC\u001e,7o\u0018\u0013fcR\u0019a+a'\t\u0011i\u000b)*!AA\u0002MAq!a(\u0001A\u0003&1#A\u0007bGRLg/Z*uC\u001e,7\u000f\t\u0005\t\u0003G\u0003\u0001\u0019!C\u0001%\u0005aa-Y5mK\u0012\u001cF/Y4fg\"I\u0011q\u0015\u0001A\u0002\u0013\u0005\u0011\u0011V\u0001\u0011M\u0006LG.\u001a3Ti\u0006<Wm]0%KF$2AVAV\u0011!Q\u0016QUA\u0001\u0002\u0004\u0019\u0002bBAX\u0001\u0001\u0006KaE\u0001\u000eM\u0006LG.\u001a3Ti\u0006<Wm\u001d\u0011\t\u000f\u0005M\u0006\u0001\"\u0015\u00026\u0006AAm\\+qI\u0006$X\r\u0006\u0002\u00028B\u0019A#!/\n\u0007\u0005mVCA\u0002B]f\u0004"
)
public class LiveJob extends LiveEntity {
   private final int jobId;
   private final String name;
   private final Option<Date> submissionTime;
   private final Seq<Object> stageIds;
   private final Option<String> jobGroup;
   private final int numTasks;
   private int activeTasks;
   private int completedTasks;
   private int failedTasks;
   private final OpenHashSet<Object> completedIndices;
   private int killedTasks;
   private Map<String, Object> killedSummary;
   private int skippedTasks;
   private Set<Object> skippedStages;
   private JobExecutionStatus status;
   private Option<Date> completionTime;
   private Set<Object> completedStages;
   private int activeStages;
   private int failedStages;

   public int jobId() {
      return this.jobId;
   }

   public Seq<Object> stageIds() {
      return this.stageIds;
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

   public int skippedTasks() {
      return this.skippedTasks;
   }

   public void skippedTasks_$eq(int x$1) {
      this.skippedTasks = x$1;
   }

   public Set<Object> skippedStages() {
      return this.skippedStages;
   }

   public void skippedStages_$eq(Set<Object> x$1) {
      this.skippedStages = x$1;
   }

   public JobExecutionStatus status() {
      return this.status;
   }

   public void status_$eq(JobExecutionStatus x$1) {
      this.status = x$1;
   }

   public Option<Date> completionTime() {
      return this.completionTime;
   }

   public void completionTime_$eq(Option<Date> x$1) {
      this.completionTime = x$1;
   }

   public Set<Object> completedStages() {
      return this.completedStages;
   }

   public void completedStages_$eq(Set<Object> x$1) {
      this.completedStages = x$1;
   }

   public int activeStages() {
      return this.activeStages;
   }

   public void activeStages_$eq(int x$1) {
      this.activeStages = x$1;
   }

   public int failedStages() {
      return this.failedStages;
   }

   public void failedStages_$eq(int x$1) {
      this.failedStages = x$1;
   }

   public Object doUpdate() {
      JobData info = new JobData(this.jobId(), this.name, None$.MODULE$, this.submissionTime, this.completionTime(), this.stageIds(), this.jobGroup, this.status(), this.numTasks, this.activeTasks(), this.completedTasks(), this.skippedTasks(), this.failedTasks(), this.killedTasks(), this.completedIndices().size(), this.activeStages(), this.completedStages().size(), this.skippedStages().size(), this.failedStages(), this.killedSummary());
      return new JobDataWrapper(info, this.skippedStages());
   }

   public LiveJob(int jobId, String name, Option<Date> submissionTime, Seq<Object> stageIds, Option<String> jobGroup, int numTasks) {
      this.jobId = jobId;
      this.name = name;
      this.submissionTime = submissionTime;
      this.stageIds = stageIds;
      this.jobGroup = jobGroup;
      this.numTasks = numTasks;
      this.activeTasks = 0;
      this.completedTasks = 0;
      this.failedTasks = 0;
      this.completedIndices = new OpenHashSet$mcJ$sp(ClassTag$.MODULE$.Long());
      this.killedTasks = 0;
      this.killedSummary = (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$);
      this.skippedTasks = 0;
      this.skippedStages = (Set)Predef$.MODULE$.Set().apply(Nil$.MODULE$);
      this.status = JobExecutionStatus.RUNNING;
      this.completionTime = None$.MODULE$;
      this.completedStages = (Set)Predef$.MODULE$.Set().apply(Nil$.MODULE$);
      this.activeStages = 0;
      this.failedStages = 0;
   }
}
