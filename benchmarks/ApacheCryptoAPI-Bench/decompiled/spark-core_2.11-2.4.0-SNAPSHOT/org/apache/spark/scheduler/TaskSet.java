package org.apache.spark.scheduler;

import java.util.Properties;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001%4Q!\u0001\u0002\u0001\t)\u0011q\u0001V1tWN+GO\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011I\u0001!Q1A\u0005\u0002Q\tQ\u0001^1tWN\u001c\u0001!F\u0001\u0016!\raa\u0003G\u0005\u0003/5\u0011Q!\u0011:sCf\u0004$!G\u0010\u0011\u0007iYR$D\u0001\u0003\u0013\ta\"A\u0001\u0003UCN\\\u0007C\u0001\u0010 \u0019\u0001!\u0011\u0002I\u0011\u0002\u0002\u0003\u0005)\u0011\u0001\u0015\u0003\u0007}#\u0013\u0007\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003$\u0003\u0019!\u0018m]6tAA\u0019AB\u0006\u00131\u0005\u0015:\u0003c\u0001\u000e\u001cMA\u0011ad\n\u0003\nA\u0005\n\t\u0011!A\u0003\u0002!\n\"!\u000b\u0017\u0011\u00051Q\u0013BA\u0016\u000e\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"\u0001D\u0017\n\u00059j!aA!os\"A\u0001\u0007\u0001BC\u0002\u0013\u0005\u0011'A\u0004ti\u0006<W-\u00133\u0016\u0003I\u0002\"\u0001D\u001a\n\u0005Qj!aA%oi\"Aa\u0007\u0001B\u0001B\u0003%!'\u0001\u0005ti\u0006<W-\u00133!\u0011!A\u0004A!b\u0001\n\u0003\t\u0014AD:uC\u001e,\u0017\t\u001e;f[B$\u0018\n\u001a\u0005\tu\u0001\u0011\t\u0011)A\u0005e\u0005y1\u000f^1hK\u0006#H/Z7qi&#\u0007\u0005\u0003\u0005=\u0001\t\u0015\r\u0011\"\u00012\u0003!\u0001(/[8sSRL\b\u0002\u0003 \u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001a\u0002\u0013A\u0014\u0018n\u001c:jif\u0004\u0003\u0002\u0003!\u0001\u0005\u000b\u0007I\u0011A!\u0002\u0015A\u0014x\u000e]3si&,7/F\u0001C!\t\u0019\u0005*D\u0001E\u0015\t)e)\u0001\u0003vi&d'\"A$\u0002\t)\fg/Y\u0005\u0003\u0013\u0012\u0013!\u0002\u0015:pa\u0016\u0014H/[3t\u0011!Y\u0005A!A!\u0002\u0013\u0011\u0015a\u00039s_B,'\u000f^5fg\u0002BQ!\u0014\u0001\u0005\u00029\u000ba\u0001P5oSRtDCB(Q-^C\u0016\f\u0005\u0002\u001b\u0001!)!\u0003\u0014a\u0001#B\u0019AB\u0006*1\u0005M+\u0006c\u0001\u000e\u001c)B\u0011a$\u0016\u0003\nAA\u000b\t\u0011!A\u0003\u0002!BQ\u0001\r'A\u0002IBQ\u0001\u000f'A\u0002IBQ\u0001\u0010'A\u0002IBQ\u0001\u0011'A\u0002\tCqa\u0017\u0001C\u0002\u0013\u0005A,\u0001\u0002jIV\tQ\f\u0005\u0002_C:\u0011AbX\u0005\u0003A6\ta\u0001\u0015:fI\u00164\u0017B\u00012d\u0005\u0019\u0019FO]5oO*\u0011\u0001-\u0004\u0005\u0007K\u0002\u0001\u000b\u0011B/\u0002\u0007%$\u0007\u0005C\u0003h\u0001\u0011\u0005\u0003.\u0001\u0005u_N#(/\u001b8h)\u0005i\u0006"
)
public class TaskSet {
   private final Task<?>[] tasks;
   private final int stageId;
   private final int stageAttemptId;
   private final int priority;
   private final Properties properties;
   private final String id;

   public Task<?>[] tasks() {
      return this.tasks;
   }

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public int priority() {
      return this.priority;
   }

   public Properties properties() {
      return this.properties;
   }

   public String id() {
      return this.id;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"TaskSet ").append((Object)this.id()).toString();
   }

   public TaskSet(Task<?>[] tasks, int stageId, int stageAttemptId, int priority, Properties properties) {
      this.tasks = tasks;
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.priority = priority;
      this.properties = properties;
      this.id = (new StringBuilder()).append(stageId).append((Object)".").append((Object)BoxesRunTime.boxToInteger(stageAttemptId)).toString();
   }
}
