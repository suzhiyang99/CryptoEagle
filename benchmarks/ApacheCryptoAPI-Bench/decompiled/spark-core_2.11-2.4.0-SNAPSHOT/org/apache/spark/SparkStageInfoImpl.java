package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A3A!\u0001\u0002\u0005\u0013\t\u00112\u000b]1sWN#\u0018mZ3J]\u001a|\u0017*\u001c9m\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\n\u0004\u0001)\u0001\u0002CA\u0006\u000f\u001b\u0005a!\"A\u0007\u0002\u000bM\u001c\u0017\r\\1\n\u0005=a!AB!osJ+g\r\u0005\u0002\u0012%5\t!!\u0003\u0002\u0014\u0005\tq1\u000b]1sWN#\u0018mZ3J]\u001a|\u0007\u0002C\u000b\u0001\u0005\u000b\u0007I\u0011\u0001\f\u0002\u000fM$\u0018mZ3JIV\tq\u0003\u0005\u0002\f1%\u0011\u0011\u0004\u0004\u0002\u0004\u0013:$\b\u0002C\u000e\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\u0011M$\u0018mZ3JI\u0002B\u0001\"\b\u0001\u0003\u0006\u0004%\tAF\u0001\u0011GV\u0014(/\u001a8u\u0003R$X-\u001c9u\u0013\u0012D\u0001b\b\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0012GV\u0014(/\u001a8u\u0003R$X-\u001c9u\u0013\u0012\u0004\u0003\u0002C\u0011\u0001\u0005\u000b\u0007I\u0011\u0001\u0012\u0002\u001dM,(-\\5tg&|g\u000eV5nKV\t1\u0005\u0005\u0002\fI%\u0011Q\u0005\u0004\u0002\u0005\u0019>tw\r\u0003\u0005(\u0001\t\u0005\t\u0015!\u0003$\u0003=\u0019XOY7jgNLwN\u001c+j[\u0016\u0004\u0003\u0002C\u0015\u0001\u0005\u000b\u0007I\u0011\u0001\u0016\u0002\t9\fW.Z\u000b\u0002WA\u0011Af\f\b\u0003\u00175J!A\f\u0007\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0014G\u0001\u0004TiJLgn\u001a\u0006\u0003]1A\u0001b\r\u0001\u0003\u0002\u0003\u0006IaK\u0001\u0006]\u0006lW\r\t\u0005\tk\u0001\u0011)\u0019!C\u0001-\u0005Aa.^7UCN\\7\u000f\u0003\u00058\u0001\t\u0005\t\u0015!\u0003\u0018\u0003%qW/\u001c+bg.\u001c\b\u0005\u0003\u0005:\u0001\t\u0015\r\u0011\"\u0001\u0017\u00039qW/\\!di&4X\rV1tWND\u0001b\u000f\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0010]Vl\u0017i\u0019;jm\u0016$\u0016m]6tA!AQ\b\u0001BC\u0002\u0013\u0005a#A\tok6\u001cu.\u001c9mKR,G\rV1tWND\u0001b\u0010\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0013]Vl7i\\7qY\u0016$X\r\u001a+bg.\u001c\b\u0005\u0003\u0005B\u0001\t\u0015\r\u0011\"\u0001\u0017\u00039qW/\u001c$bS2,G\rV1tWND\u0001b\u0011\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0010]Vlg)Y5mK\u0012$\u0016m]6tA!)Q\t\u0001C\u0001\r\u00061A(\u001b8jiz\"\u0012b\u0012%J\u0015.cUJT(\u0011\u0005E\u0001\u0001\"B\u000bE\u0001\u00049\u0002\"B\u000fE\u0001\u00049\u0002\"B\u0011E\u0001\u0004\u0019\u0003\"B\u0015E\u0001\u0004Y\u0003\"B\u001bE\u0001\u00049\u0002\"B\u001dE\u0001\u00049\u0002\"B\u001fE\u0001\u00049\u0002\"B!E\u0001\u00049\u0002"
)
public class SparkStageInfoImpl implements SparkStageInfo {
   private final int stageId;
   private final int currentAttemptId;
   private final long submissionTime;
   private final String name;
   private final int numTasks;
   private final int numActiveTasks;
   private final int numCompletedTasks;
   private final int numFailedTasks;

   public int stageId() {
      return this.stageId;
   }

   public int currentAttemptId() {
      return this.currentAttemptId;
   }

   public long submissionTime() {
      return this.submissionTime;
   }

   public String name() {
      return this.name;
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

   public int numFailedTasks() {
      return this.numFailedTasks;
   }

   public SparkStageInfoImpl(int stageId, int currentAttemptId, long submissionTime, String name, int numTasks, int numActiveTasks, int numCompletedTasks, int numFailedTasks) {
      this.stageId = stageId;
      this.currentAttemptId = currentAttemptId;
      this.submissionTime = submissionTime;
      this.name = name;
      this.numTasks = numTasks;
      this.numActiveTasks = numActiveTasks;
      this.numCompletedTasks = numCompletedTasks;
      this.numFailedTasks = numFailedTasks;
   }
}
