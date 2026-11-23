package org.apache.spark.status.api.v1;

import java.util.Date;
import org.apache.spark.JobExecutionStatus;
import scala.Option;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005uc\u0001B\u0001\u0003\u0001=\u0011qAS8c\t\u0006$\u0018M\u0003\u0002\u0004\t\u0005\u0011a/\r\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sO\u000e\u00011C\u0001\u0001\u0011!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fM\"Aq\u0003\u0001BC\u0002\u0013\u0005\u0001$A\u0003k_\nLE-F\u0001\u001a!\t\t\"$\u0003\u0002\u001c%\t\u0019\u0011J\u001c;\t\u0011u\u0001!\u0011!Q\u0001\ne\taA[8c\u0013\u0012\u0004\u0003\u0002C\u0010\u0001\u0005\u000b\u0007I\u0011\u0001\u0011\u0002\t9\fW.Z\u000b\u0002CA\u0011!%\n\b\u0003#\rJ!\u0001\n\n\u0002\rA\u0013X\rZ3g\u0013\t1sE\u0001\u0004TiJLgn\u001a\u0006\u0003IIA\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006I!I\u0001\u0006]\u0006lW\r\t\u0005\tW\u0001\u0011)\u0019!C\u0001Y\u0005YA-Z:de&\u0004H/[8o+\u0005i\u0003cA\t/C%\u0011qF\u0005\u0002\u0007\u001fB$\u0018n\u001c8\t\u0011E\u0002!\u0011!Q\u0001\n5\nA\u0002Z3tGJL\u0007\u000f^5p]\u0002B\u0001b\r\u0001\u0003\u0006\u0004%\t\u0001N\u0001\u000fgV\u0014W.[:tS>tG+[7f+\u0005)\u0004cA\t/mA\u0011q\u0007P\u0007\u0002q)\u0011\u0011HO\u0001\u0005kRLGNC\u0001<\u0003\u0011Q\u0017M^1\n\u0005uB$\u0001\u0002#bi\u0016D\u0001b\u0010\u0001\u0003\u0002\u0003\u0006I!N\u0001\u0010gV\u0014W.[:tS>tG+[7fA!A\u0011\t\u0001BC\u0002\u0013\u0005A'\u0001\bd_6\u0004H.\u001a;j_:$\u0016.\\3\t\u0011\r\u0003!\u0011!Q\u0001\nU\nqbY8na2,G/[8o)&lW\r\t\u0005\t\u000b\u0002\u0011)\u0019!C\u0001\r\u0006A1\u000f^1hK&#7/F\u0001H!\rA\u0005+\u0007\b\u0003\u0013:s!AS'\u000e\u0003-S!\u0001\u0014\b\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0019\u0012BA(\u0013\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u0015*\u0003\u0007M+\u0017O\u0003\u0002P%!AA\u000b\u0001B\u0001B\u0003%q)A\u0005ti\u0006<W-\u00133tA!Aa\u000b\u0001BC\u0002\u0013\u0005A&\u0001\u0005k_\n<%o\\;q\u0011!A\u0006A!A!\u0002\u0013i\u0013!\u00036pE\u001e\u0013x.\u001e9!\u0011!9\u0001A!b\u0001\n\u0003QV#A.\u0011\u0005qkV\"\u0001\u0005\n\u0005yC!A\u0005&pE\u0016CXmY;uS>t7\u000b^1ukND\u0001\u0002\u0019\u0001\u0003\u0002\u0003\u0006IaW\u0001\bgR\fG/^:!\u0011!\u0011\u0007A!b\u0001\n\u0003A\u0012\u0001\u00038v[R\u000b7o[:\t\u0011\u0011\u0004!\u0011!Q\u0001\ne\t\u0011B\\;n)\u0006\u001c8n\u001d\u0011\t\u0011\u0019\u0004!Q1A\u0005\u0002a\taB\\;n\u0003\u000e$\u0018N^3UCN\\7\u000f\u0003\u0005i\u0001\t\u0005\t\u0015!\u0003\u001a\u0003=qW/\\!di&4X\rV1tWN\u0004\u0003\u0002\u00036\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002#9,XnQ8na2,G/\u001a3UCN\\7\u000f\u0003\u0005m\u0001\t\u0005\t\u0015!\u0003\u001a\u0003IqW/\\\"p[BdW\r^3e)\u0006\u001c8n\u001d\u0011\t\u00119\u0004!Q1A\u0005\u0002a\tqB\\;n'.L\u0007\u000f]3e)\u0006\u001c8n\u001d\u0005\ta\u0002\u0011\t\u0011)A\u00053\u0005\u0001b.^7TW&\u0004\b/\u001a3UCN\\7\u000f\t\u0005\te\u0002\u0011)\u0019!C\u00011\u0005qa.^7GC&dW\r\u001a+bg.\u001c\b\u0002\u0003;\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u001f9,XNR1jY\u0016$G+Y:lg\u0002B\u0001B\u001e\u0001\u0003\u0006\u0004%\t\u0001G\u0001\u000f]Vl7*\u001b7mK\u0012$\u0016m]6t\u0011!A\bA!A!\u0002\u0013I\u0012a\u00048v[.KG\u000e\\3e)\u0006\u001c8n\u001d\u0011\t\u0011i\u0004!Q1A\u0005\u0002a\t1C\\;n\u0007>l\u0007\u000f\\3uK\u0012Le\u000eZ5dKND\u0001\u0002 \u0001\u0003\u0002\u0003\u0006I!G\u0001\u0015]Vl7i\\7qY\u0016$X\rZ%oI&\u001cWm\u001d\u0011\t\u0011y\u0004!Q1A\u0005\u0002a\tqB\\;n\u0003\u000e$\u0018N^3Ti\u0006<Wm\u001d\u0005\n\u0003\u0003\u0001!\u0011!Q\u0001\ne\t\u0001C\\;n\u0003\u000e$\u0018N^3Ti\u0006<Wm\u001d\u0011\t\u0013\u0005\u0015\u0001A!b\u0001\n\u0003A\u0012A\u00058v[\u000e{W\u000e\u001d7fi\u0016$7\u000b^1hKND\u0011\"!\u0003\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002'9,XnQ8na2,G/\u001a3Ti\u0006<Wm\u001d\u0011\t\u0013\u00055\u0001A!b\u0001\n\u0003A\u0012\u0001\u00058v[N[\u0017\u000e\u001d9fIN#\u0018mZ3t\u0011%\t\t\u0002\u0001B\u0001B\u0003%\u0011$A\tok6\u001c6.\u001b9qK\u0012\u001cF/Y4fg\u0002B\u0011\"!\u0006\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u001f9,XNR1jY\u0016$7\u000b^1hKND\u0011\"!\u0007\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002!9,XNR1jY\u0016$7\u000b^1hKN\u0004\u0003BCA\u000f\u0001\t\u0015\r\u0011\"\u0001\u0002 \u0005\u00112.\u001b7mK\u0012$\u0016m]6t'VlW.\u0019:z+\t\t\t\u0003E\u0003#\u0003G\t\u0013$C\u0002\u0002&\u001d\u00121!T1q\u0011)\tI\u0003\u0001B\u0001B\u0003%\u0011\u0011E\u0001\u0014W&dG.\u001a3UCN\\7oU;n[\u0006\u0014\u0018\u0010\t\u0005\t\u0003[\u0001A\u0011\u0001\u0005\u00020\u00051A(\u001b8jiz\"\"&!\r\u00026\u0005]\u0012\u0011HA\u001e\u0003{\ty$!\u0011\u0002D\u0005\u0015\u0013qIA%\u0003\u0017\ni%a\u0014\u0002R\u0005M\u0013QKA,\u00033\nY\u0006E\u0002\u00024\u0001i\u0011A\u0001\u0005\u0007/\u0005-\u0002\u0019A\r\t\r}\tY\u00031\u0001\"\u0011\u0019Y\u00131\u0006a\u0001[!11'a\u000bA\u0002UBa!QA\u0016\u0001\u0004)\u0004BB#\u0002,\u0001\u0007q\t\u0003\u0004W\u0003W\u0001\r!\f\u0005\u0007\u000f\u0005-\u0002\u0019A.\t\r\t\fY\u00031\u0001\u001a\u0011\u00191\u00171\u0006a\u00013!1!.a\u000bA\u0002eAaA\\A\u0016\u0001\u0004I\u0002B\u0002:\u0002,\u0001\u0007\u0011\u0004\u0003\u0004w\u0003W\u0001\r!\u0007\u0005\u0007u\u0006-\u0002\u0019A\r\t\ry\fY\u00031\u0001\u001a\u0011\u001d\t)!a\u000bA\u0002eAq!!\u0004\u0002,\u0001\u0007\u0011\u0004C\u0004\u0002\u0016\u0005-\u0002\u0019A\r\t\u0011\u0005u\u00111\u0006a\u0001\u0003C\u0001"
)
public class JobData {
   private final int jobId;
   private final String name;
   private final Option<String> description;
   private final Option<Date> submissionTime;
   private final Option<Date> completionTime;
   private final Seq<Object> stageIds;
   private final Option<String> jobGroup;
   private final JobExecutionStatus status;
   private final int numTasks;
   private final int numActiveTasks;
   private final int numCompletedTasks;
   private final int numSkippedTasks;
   private final int numFailedTasks;
   private final int numKilledTasks;
   private final int numCompletedIndices;
   private final int numActiveStages;
   private final int numCompletedStages;
   private final int numSkippedStages;
   private final int numFailedStages;
   private final Map<String, Object> killedTasksSummary;

   public int jobId() {
      return this.jobId;
   }

   public String name() {
      return this.name;
   }

   public Option<String> description() {
      return this.description;
   }

   public Option<Date> submissionTime() {
      return this.submissionTime;
   }

   public Option<Date> completionTime() {
      return this.completionTime;
   }

   public Seq<Object> stageIds() {
      return this.stageIds;
   }

   public Option<String> jobGroup() {
      return this.jobGroup;
   }

   public JobExecutionStatus status() {
      return this.status;
   }

   public int numTasks() {
      return this.numTasks;
   }

   public int numActiveTasks() {
      return this.numActiveTasks;
   }

   public int numCompletedTasks() {
      return this.numCompletedTasks;
   }

   public int numSkippedTasks() {
      return this.numSkippedTasks;
   }

   public int numFailedTasks() {
      return this.numFailedTasks;
   }

   public int numKilledTasks() {
      return this.numKilledTasks;
   }

   public int numCompletedIndices() {
      return this.numCompletedIndices;
   }

   public int numActiveStages() {
      return this.numActiveStages;
   }

   public int numCompletedStages() {
      return this.numCompletedStages;
   }

   public int numSkippedStages() {
      return this.numSkippedStages;
   }

   public int numFailedStages() {
      return this.numFailedStages;
   }

   public Map<String, Object> killedTasksSummary() {
      return this.killedTasksSummary;
   }

   public JobData(int jobId, String name, Option<String> description, Option<Date> submissionTime, Option<Date> completionTime, Seq<Object> stageIds, Option<String> jobGroup, JobExecutionStatus status, int numTasks, int numActiveTasks, int numCompletedTasks, int numSkippedTasks, int numFailedTasks, int numKilledTasks, int numCompletedIndices, int numActiveStages, int numCompletedStages, int numSkippedStages, int numFailedStages, Map<String, Object> killedTasksSummary) {
      this.jobId = jobId;
      this.name = name;
      this.description = description;
      this.submissionTime = submissionTime;
      this.completionTime = completionTime;
      this.stageIds = stageIds;
      this.jobGroup = jobGroup;
      this.status = status;
      this.numTasks = numTasks;
      this.numActiveTasks = numActiveTasks;
      this.numCompletedTasks = numCompletedTasks;
      this.numSkippedTasks = numSkippedTasks;
      this.numFailedTasks = numFailedTasks;
      this.numKilledTasks = numKilledTasks;
      this.numCompletedIndices = numCompletedIndices;
      this.numActiveStages = numActiveStages;
      this.numCompletedStages = numCompletedStages;
      this.numSkippedStages = numSkippedStages;
      this.numFailedStages = numFailedStages;
      this.killedTasksSummary = killedTasksSummary;
   }
}
