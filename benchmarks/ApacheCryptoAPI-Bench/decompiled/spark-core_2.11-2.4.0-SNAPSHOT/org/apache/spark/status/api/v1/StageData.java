package org.apache.spark.status.api.v1;

import java.util.Date;
import scala.Option;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\tEa\u0001B\u0001\u0003\u0001=\u0011\u0011b\u0015;bO\u0016$\u0015\r^1\u000b\u0005\r!\u0011A\u0001<2\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\rM$\u0018\r^;t\u0015\tI!\"A\u0003ta\u0006\u00148N\u0003\u0002\f\u0019\u00051\u0011\r]1dQ\u0016T\u0011!D\u0001\u0004_J<7\u0001A\n\u0003\u0001A\u0001\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u0011a!\u00118z%\u00164\u0007\u0002C\u0004\u0001\u0005\u000b\u0007I\u0011A\f\u0016\u0003a\u0001\"!\u0007\u000e\u000e\u0003\tI!a\u0007\u0002\u0003\u0017M#\u0018mZ3Ti\u0006$Xo\u001d\u0005\t;\u0001\u0011\t\u0011)A\u00051\u000591\u000f^1ukN\u0004\u0003\u0002C\u0010\u0001\u0005\u000b\u0007I\u0011\u0001\u0011\u0002\u000fM$\u0018mZ3JIV\t\u0011\u0005\u0005\u0002\u0012E%\u00111E\u0005\u0002\u0004\u0013:$\b\u0002C\u0013\u0001\u0005\u0003\u0005\u000b\u0011B\u0011\u0002\u0011M$\u0018mZ3JI\u0002B\u0001b\n\u0001\u0003\u0006\u0004%\t\u0001I\u0001\nCR$X-\u001c9u\u0013\u0012D\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006I!I\u0001\u000bCR$X-\u001c9u\u0013\u0012\u0004\u0003\u0002C\u0016\u0001\u0005\u000b\u0007I\u0011\u0001\u0011\u0002\u00119,X\u000eV1tWND\u0001\"\f\u0001\u0003\u0002\u0003\u0006I!I\u0001\n]VlG+Y:lg\u0002B\u0001b\f\u0001\u0003\u0006\u0004%\t\u0001I\u0001\u000f]Vl\u0017i\u0019;jm\u0016$\u0016m]6t\u0011!\t\u0004A!A!\u0002\u0013\t\u0013a\u00048v[\u0006\u001bG/\u001b<f)\u0006\u001c8n\u001d\u0011\t\u0011M\u0002!Q1A\u0005\u0002\u0001\n\u0001C\\;n\u0007>l\u0007\u000f\\3uKR\u000b7o[:\t\u0011U\u0002!\u0011!Q\u0001\n\u0005\n\u0011C\\;n\u0007>l\u0007\u000f\\3uKR\u000b7o[:!\u0011!9\u0004A!b\u0001\n\u0003\u0001\u0013A\u00048v[\u001a\u000b\u0017\u000e\\3e)\u0006\u001c8n\u001d\u0005\ts\u0001\u0011\t\u0011)A\u0005C\u0005ya.^7GC&dW\r\u001a+bg.\u001c\b\u0005\u0003\u0005<\u0001\t\u0015\r\u0011\"\u0001!\u00039qW/\\&jY2,G\rV1tWND\u0001\"\u0010\u0001\u0003\u0002\u0003\u0006I!I\u0001\u0010]Vl7*\u001b7mK\u0012$\u0016m]6tA!Aq\b\u0001BC\u0002\u0013\u0005\u0001%A\nok6\u001cu.\u001c9mKR,G-\u00138eS\u000e,7\u000f\u0003\u0005B\u0001\t\u0005\t\u0015!\u0003\"\u0003QqW/\\\"p[BdW\r^3e\u0013:$\u0017nY3tA!A1\t\u0001BC\u0002\u0013\u0005A)A\bfq\u0016\u001cW\u000f^8s%VtG+[7f+\u0005)\u0005CA\tG\u0013\t9%C\u0001\u0003M_:<\u0007\u0002C%\u0001\u0005\u0003\u0005\u000b\u0011B#\u0002!\u0015DXmY;u_J\u0014VO\u001c+j[\u0016\u0004\u0003\u0002C&\u0001\u0005\u000b\u0007I\u0011\u0001#\u0002\u001f\u0015DXmY;u_J\u001c\u0005/\u001e+j[\u0016D\u0001\"\u0014\u0001\u0003\u0002\u0003\u0006I!R\u0001\u0011Kb,7-\u001e;pe\u000e\u0003X\u000fV5nK\u0002B\u0001b\u0014\u0001\u0003\u0006\u0004%\t\u0001U\u0001\u000fgV\u0014W.[:tS>tG+[7f+\u0005\t\u0006cA\tS)&\u00111K\u0005\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005USV\"\u0001,\u000b\u0005]C\u0016\u0001B;uS2T\u0011!W\u0001\u0005U\u00064\u0018-\u0003\u0002\\-\n!A)\u0019;f\u0011!i\u0006A!A!\u0002\u0013\t\u0016aD:vE6L7o]5p]RKW.\u001a\u0011\t\u0011}\u0003!Q1A\u0005\u0002A\u000bQCZ5sgR$\u0016m]6MCVt7\r[3e)&lW\r\u0003\u0005b\u0001\t\u0005\t\u0015!\u0003R\u0003Y1\u0017N]:u)\u0006\u001c8\u000eT1v]\u000eDW\r\u001a+j[\u0016\u0004\u0003\u0002C2\u0001\u0005\u000b\u0007I\u0011\u0001)\u0002\u001d\r|W\u000e\u001d7fi&|g\u000eV5nK\"AQ\r\u0001B\u0001B\u0003%\u0011+A\bd_6\u0004H.\u001a;j_:$\u0016.\\3!\u0011!9\u0007A!b\u0001\n\u0003A\u0017!\u00044bS2,(/\u001a*fCN|g.F\u0001j!\r\t\"K\u001b\t\u0003W:t!!\u00057\n\u00055\u0014\u0012A\u0002)sK\u0012,g-\u0003\u0002pa\n11\u000b\u001e:j]\u001eT!!\u001c\n\t\u0011I\u0004!\u0011!Q\u0001\n%\faBZ1jYV\u0014XMU3bg>t\u0007\u0005\u0003\u0005u\u0001\t\u0015\r\u0011\"\u0001E\u0003)Ig\u000e];u\u0005f$Xm\u001d\u0005\tm\u0002\u0011\t\u0011)A\u0005\u000b\u0006Y\u0011N\u001c9vi\nKH/Z:!\u0011!A\bA!b\u0001\n\u0003!\u0015\u0001D5oaV$(+Z2pe\u0012\u001c\b\u0002\u0003>\u0001\u0005\u0003\u0005\u000b\u0011B#\u0002\u001b%t\u0007/\u001e;SK\u000e|'\u000fZ:!\u0011!a\bA!b\u0001\n\u0003!\u0015aC8viB,HOQ=uKND\u0001B \u0001\u0003\u0002\u0003\u0006I!R\u0001\r_V$\b/\u001e;CsR,7\u000f\t\u0005\n\u0003\u0003\u0001!Q1A\u0005\u0002\u0011\u000bQb\\;uaV$(+Z2pe\u0012\u001c\b\"CA\u0003\u0001\t\u0005\t\u0015!\u0003F\u00039yW\u000f\u001e9viJ+7m\u001c:eg\u0002B\u0011\"!\u0003\u0001\u0005\u000b\u0007I\u0011\u0001#\u0002!MDWO\u001a4mKJ+\u0017\r\u001a\"zi\u0016\u001c\b\"CA\u0007\u0001\t\u0005\t\u0015!\u0003F\u0003E\u0019\b.\u001e4gY\u0016\u0014V-\u00193CsR,7\u000f\t\u0005\n\u0003#\u0001!Q1A\u0005\u0002\u0011\u000b!c\u001d5vM\u001adWMU3bIJ+7m\u001c:eg\"I\u0011Q\u0003\u0001\u0003\u0002\u0003\u0006I!R\u0001\u0014g\",hM\u001a7f%\u0016\fGMU3d_J$7\u000f\t\u0005\n\u00033\u0001!Q1A\u0005\u0002\u0011\u000b\u0011c\u001d5vM\u001adWm\u0016:ji\u0016\u0014\u0015\u0010^3t\u0011%\ti\u0002\u0001B\u0001B\u0003%Q)\u0001\ntQV4g\r\\3Xe&$XMQ=uKN\u0004\u0003\"CA\u0011\u0001\t\u0015\r\u0011\"\u0001E\u0003M\u0019\b.\u001e4gY\u0016<&/\u001b;f%\u0016\u001cwN\u001d3t\u0011%\t)\u0003\u0001B\u0001B\u0003%Q)\u0001\u000btQV4g\r\\3Xe&$XMU3d_J$7\u000f\t\u0005\n\u0003S\u0001!Q1A\u0005\u0002\u0011\u000b!#\\3n_JL()\u001f;fgN\u0003\u0018\u000e\u001c7fI\"I\u0011Q\u0006\u0001\u0003\u0002\u0003\u0006I!R\u0001\u0014[\u0016lwN]=CsR,7o\u00159jY2,G\r\t\u0005\n\u0003c\u0001!Q1A\u0005\u0002\u0011\u000b\u0001\u0003Z5tW\nKH/Z:Ta&dG.\u001a3\t\u0013\u0005U\u0002A!A!\u0002\u0013)\u0015!\u00053jg.\u0014\u0015\u0010^3t'BLG\u000e\\3eA!Q\u0011\u0011\b\u0001\u0003\u0006\u0004%\t!a\u000f\u0002\t9\fW.Z\u000b\u0002U\"I\u0011q\b\u0001\u0003\u0002\u0003\u0006IA[\u0001\u0006]\u0006lW\r\t\u0005\n\u0003\u0007\u0002!Q1A\u0005\u0002!\f1\u0002Z3tGJL\u0007\u000f^5p]\"I\u0011q\t\u0001\u0003\u0002\u0003\u0006I![\u0001\rI\u0016\u001c8M]5qi&|g\u000e\t\u0005\u000b\u0003\u0017\u0002!Q1A\u0005\u0002\u0005m\u0012a\u00023fi\u0006LGn\u001d\u0005\n\u0003\u001f\u0002!\u0011!Q\u0001\n)\f\u0001\u0002Z3uC&d7\u000f\t\u0005\u000b\u0003'\u0002!Q1A\u0005\u0002\u0005m\u0012AD:dQ\u0016$W\u000f\\5oOB{w\u000e\u001c\u0005\n\u0003/\u0002!\u0011!Q\u0001\n)\fqb]2iK\u0012,H.\u001b8h!>|G\u000e\t\u0005\u000b\u00037\u0002!Q1A\u0005\u0002\u0005u\u0013A\u0002:eI&#7/\u0006\u0002\u0002`A)\u0011\u0011MA9C9!\u00111MA7\u001d\u0011\t)'a\u001b\u000e\u0005\u0005\u001d$bAA5\u001d\u00051AH]8pizJ\u0011aE\u0005\u0004\u0003_\u0012\u0012a\u00029bG.\fw-Z\u0005\u0005\u0003g\n)HA\u0002TKFT1!a\u001c\u0013\u0011)\tI\b\u0001B\u0001B\u0003%\u0011qL\u0001\be\u0012$\u0017\nZ:!\u0011)\ti\b\u0001BC\u0002\u0013\u0005\u0011qP\u0001\u0013C\u000e\u001cW/\\;mCR|'/\u00169eCR,7/\u0006\u0002\u0002\u0002B1\u0011\u0011MA9\u0003\u0007\u00032!GAC\u0013\r\t9I\u0001\u0002\u0010\u0003\u000e\u001cW/\\;mC\ndW-\u00138g_\"Q\u00111\u0012\u0001\u0003\u0002\u0003\u0006I!!!\u0002'\u0005\u001c7-^7vY\u0006$xN]+qI\u0006$Xm\u001d\u0011\t\u0015\u0005=\u0005A!b\u0001\n\u0003\t\t*A\u0003uCN\\7/\u0006\u0002\u0002\u0014B!\u0011CUAK!\u0019Y\u0017qS#\u0002\u001c&\u0019\u0011\u0011\u00149\u0003\u00075\u000b\u0007\u000fE\u0002\u001a\u0003;K1!a(\u0003\u0005!!\u0016m]6ECR\f\u0007BCAR\u0001\t\u0005\t\u0015!\u0003\u0002\u0014\u00061A/Y:lg\u0002B!\"a*\u0001\u0005\u000b\u0007I\u0011AAU\u0003=)\u00070Z2vi>\u00148+^7nCJLXCAAV!\u0011\t\"+!,\u0011\r-\f9J[AX!\rI\u0012\u0011W\u0005\u0004\u0003g\u0013!\u0001F#yK\u000e,Ho\u001c:Ti\u0006<WmU;n[\u0006\u0014\u0018\u0010\u0003\u0006\u00028\u0002\u0011\t\u0011)A\u0005\u0003W\u000b\u0001#\u001a=fGV$xN]*v[6\f'/\u001f\u0011\t\u0015\u0005m\u0006A!b\u0001\n\u0003\ti,\u0001\nlS2dW\r\u001a+bg.\u001c8+^7nCJLXCAA`!\u0015Y\u0017q\u00136\"\u0011)\t\u0019\r\u0001B\u0001B\u0003%\u0011qX\u0001\u0014W&dG.\u001a3UCN\\7oU;n[\u0006\u0014\u0018\u0010\t\u0005\t\u0003\u000f\u0004A\u0011\u0001\u0005\u0002J\u00061A(\u001b8jiz\"b)a3\u0002N\u0006=\u0017\u0011[Aj\u0003+\f9.!7\u0002\\\u0006u\u0017q\\Aq\u0003G\f)/a:\u0002j\u0006-\u0018Q^Ax\u0003c\f\u00190!>\u0002x\u0006e\u00181`A\u007f\u0003\u007f\u0014\tAa\u0001\u0003\u0006\t\u001d!\u0011\u0002B\u0006\u0005\u001b\u0011y\u0001\u0005\u0002\u001a\u0001!1q!!2A\u0002aAaaHAc\u0001\u0004\t\u0003BB\u0014\u0002F\u0002\u0007\u0011\u0005\u0003\u0004,\u0003\u000b\u0004\r!\t\u0005\u0007_\u0005\u0015\u0007\u0019A\u0011\t\rM\n)\r1\u0001\"\u0011\u00199\u0014Q\u0019a\u0001C!11(!2A\u0002\u0005BaaPAc\u0001\u0004\t\u0003BB\"\u0002F\u0002\u0007Q\t\u0003\u0004L\u0003\u000b\u0004\r!\u0012\u0005\u0007\u001f\u0006\u0015\u0007\u0019A)\t\r}\u000b)\r1\u0001R\u0011\u0019\u0019\u0017Q\u0019a\u0001#\"1q-!2A\u0002%Da\u0001^Ac\u0001\u0004)\u0005B\u0002=\u0002F\u0002\u0007Q\t\u0003\u0004}\u0003\u000b\u0004\r!\u0012\u0005\b\u0003\u0003\t)\r1\u0001F\u0011\u001d\tI!!2A\u0002\u0015Cq!!\u0005\u0002F\u0002\u0007Q\tC\u0004\u0002\u001a\u0005\u0015\u0007\u0019A#\t\u000f\u0005\u0005\u0012Q\u0019a\u0001\u000b\"9\u0011\u0011FAc\u0001\u0004)\u0005bBA\u0019\u0003\u000b\u0004\r!\u0012\u0005\b\u0003s\t)\r1\u0001k\u0011\u001d\t\u0019%!2A\u0002%Dq!a\u0013\u0002F\u0002\u0007!\u000eC\u0004\u0002T\u0005\u0015\u0007\u0019\u00016\t\u0011\u0005m\u0013Q\u0019a\u0001\u0003?B\u0001\"! \u0002F\u0002\u0007\u0011\u0011\u0011\u0005\t\u0003\u001f\u000b)\r1\u0001\u0002\u0014\"A\u0011qUAc\u0001\u0004\tY\u000b\u0003\u0005\u0002<\u0006\u0015\u0007\u0019AA`\u0001"
)
public class StageData {
   private final StageStatus status;
   private final int stageId;
   private final int attemptId;
   private final int numTasks;
   private final int numActiveTasks;
   private final int numCompleteTasks;
   private final int numFailedTasks;
   private final int numKilledTasks;
   private final int numCompletedIndices;
   private final long executorRunTime;
   private final long executorCpuTime;
   private final Option<Date> submissionTime;
   private final Option<Date> firstTaskLaunchedTime;
   private final Option<Date> completionTime;
   private final Option<String> failureReason;
   private final long inputBytes;
   private final long inputRecords;
   private final long outputBytes;
   private final long outputRecords;
   private final long shuffleReadBytes;
   private final long shuffleReadRecords;
   private final long shuffleWriteBytes;
   private final long shuffleWriteRecords;
   private final long memoryBytesSpilled;
   private final long diskBytesSpilled;
   private final String name;
   private final Option<String> description;
   private final String details;
   private final String schedulingPool;
   private final Seq<Object> rddIds;
   private final Seq<AccumulableInfo> accumulatorUpdates;
   private final Option<Map<Object, TaskData>> tasks;
   private final Option<Map<String, ExecutorStageSummary>> executorSummary;
   private final Map<String, Object> killedTasksSummary;

   public StageStatus status() {
      return this.status;
   }

   public int stageId() {
      return this.stageId;
   }

   public int attemptId() {
      return this.attemptId;
   }

   public int numTasks() {
      return this.numTasks;
   }

   public int numActiveTasks() {
      return this.numActiveTasks;
   }

   public int numCompleteTasks() {
      return this.numCompleteTasks;
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

   public long executorRunTime() {
      return this.executorRunTime;
   }

   public long executorCpuTime() {
      return this.executorCpuTime;
   }

   public Option<Date> submissionTime() {
      return this.submissionTime;
   }

   public Option<Date> firstTaskLaunchedTime() {
      return this.firstTaskLaunchedTime;
   }

   public Option<Date> completionTime() {
      return this.completionTime;
   }

   public Option<String> failureReason() {
      return this.failureReason;
   }

   public long inputBytes() {
      return this.inputBytes;
   }

   public long inputRecords() {
      return this.inputRecords;
   }

   public long outputBytes() {
      return this.outputBytes;
   }

   public long outputRecords() {
      return this.outputRecords;
   }

   public long shuffleReadBytes() {
      return this.shuffleReadBytes;
   }

   public long shuffleReadRecords() {
      return this.shuffleReadRecords;
   }

   public long shuffleWriteBytes() {
      return this.shuffleWriteBytes;
   }

   public long shuffleWriteRecords() {
      return this.shuffleWriteRecords;
   }

   public long memoryBytesSpilled() {
      return this.memoryBytesSpilled;
   }

   public long diskBytesSpilled() {
      return this.diskBytesSpilled;
   }

   public String name() {
      return this.name;
   }

   public Option<String> description() {
      return this.description;
   }

   public String details() {
      return this.details;
   }

   public String schedulingPool() {
      return this.schedulingPool;
   }

   public Seq<Object> rddIds() {
      return this.rddIds;
   }

   public Seq<AccumulableInfo> accumulatorUpdates() {
      return this.accumulatorUpdates;
   }

   public Option<Map<Object, TaskData>> tasks() {
      return this.tasks;
   }

   public Option<Map<String, ExecutorStageSummary>> executorSummary() {
      return this.executorSummary;
   }

   public Map<String, Object> killedTasksSummary() {
      return this.killedTasksSummary;
   }

   public StageData(StageStatus status, int stageId, int attemptId, int numTasks, int numActiveTasks, int numCompleteTasks, int numFailedTasks, int numKilledTasks, int numCompletedIndices, long executorRunTime, long executorCpuTime, Option<Date> submissionTime, Option<Date> firstTaskLaunchedTime, Option<Date> completionTime, Option<String> failureReason, long inputBytes, long inputRecords, long outputBytes, long outputRecords, long shuffleReadBytes, long shuffleReadRecords, long shuffleWriteBytes, long shuffleWriteRecords, long memoryBytesSpilled, long diskBytesSpilled, String name, Option<String> description, String details, String schedulingPool, Seq<Object> rddIds, Seq<AccumulableInfo> accumulatorUpdates, Option<Map<Object, TaskData>> tasks, Option<Map<String, ExecutorStageSummary>> executorSummary, Map<String, Object> killedTasksSummary) {
      this.status = status;
      this.stageId = stageId;
      this.attemptId = attemptId;
      this.numTasks = numTasks;
      this.numActiveTasks = numActiveTasks;
      this.numCompleteTasks = numCompleteTasks;
      this.numFailedTasks = numFailedTasks;
      this.numKilledTasks = numKilledTasks;
      this.numCompletedIndices = numCompletedIndices;
      this.executorRunTime = executorRunTime;
      this.executorCpuTime = executorCpuTime;
      this.submissionTime = submissionTime;
      this.firstTaskLaunchedTime = firstTaskLaunchedTime;
      this.completionTime = completionTime;
      this.failureReason = failureReason;
      this.inputBytes = inputBytes;
      this.inputRecords = inputRecords;
      this.outputBytes = outputBytes;
      this.outputRecords = outputRecords;
      this.shuffleReadBytes = shuffleReadBytes;
      this.shuffleReadRecords = shuffleReadRecords;
      this.shuffleWriteBytes = shuffleWriteBytes;
      this.shuffleWriteRecords = shuffleWriteRecords;
      this.memoryBytesSpilled = memoryBytesSpilled;
      this.diskBytesSpilled = diskBytesSpilled;
      this.name = name;
      this.description = description;
      this.details = details;
      this.schedulingPool = schedulingPool;
      this.rddIds = rddIds;
      this.accumulatorUpdates = accumulatorUpdates;
      this.tasks = tasks;
      this.executorSummary = executorSummary;
      this.killedTasksSummary = killedTasksSummary;
   }
}
