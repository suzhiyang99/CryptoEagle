package org.apache.spark.status.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import scala.Option;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=e\u0001B\u0001\u0003\u0001=\u0011\u0001\u0002V1tW\u0012\u000bG/\u0019\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001!A\u0011\u0011\u0003F\u0007\u0002%)\t1#A\u0003tG\u0006d\u0017-\u0003\u0002\u0016%\t1\u0011I\\=SK\u001aD\u0001b\u0006\u0001\u0003\u0006\u0004%\t\u0001G\u0001\u0007i\u0006\u001c8.\u00133\u0016\u0003e\u0001\"!\u0005\u000e\n\u0005m\u0011\"\u0001\u0002'p]\u001eD\u0001\"\b\u0001\u0003\u0002\u0003\u0006I!G\u0001\bi\u0006\u001c8.\u00133!\u0011!y\u0002A!b\u0001\n\u0003\u0001\u0013!B5oI\u0016DX#A\u0011\u0011\u0005E\u0011\u0013BA\u0012\u0013\u0005\rIe\u000e\u001e\u0005\tK\u0001\u0011\t\u0011)A\u0005C\u00051\u0011N\u001c3fq\u0002B\u0001b\n\u0001\u0003\u0006\u0004%\t\u0001I\u0001\bCR$X-\u001c9u\u0011!I\u0003A!A!\u0002\u0013\t\u0013\u0001C1ui\u0016l\u0007\u000f\u001e\u0011\t\u0011-\u0002!Q1A\u0005\u00021\n!\u0002\\1v]\u000eDG+[7f+\u0005i\u0003C\u0001\u00184\u001b\u0005y#B\u0001\u00192\u0003\u0011)H/\u001b7\u000b\u0003I\nAA[1wC&\u0011Ag\f\u0002\u0005\t\u0006$X\r\u0003\u00057\u0001\t\u0005\t\u0015!\u0003.\u0003-a\u0017-\u001e8dQRKW.\u001a\u0011\t\u0011a\u0002!Q1A\u0005\u0002e\n\u0001C]3tk2$h)\u001a;dQN#\u0018M\u001d;\u0016\u0003i\u00022!E\u001e.\u0013\ta$C\u0001\u0004PaRLwN\u001c\u0005\t}\u0001\u0011\t\u0011)A\u0005u\u0005\t\"/Z:vYR4U\r^2i'R\f'\u000f\u001e\u0011\t\u0011\u0001\u0003!Q1A\u0005\u0002\u0005\u000b\u0001\u0002Z;sCRLwN\\\u000b\u0002\u0005B\u0019\u0011cO\r\t\u0011\u0011\u0003!\u0011!Q\u0001\n\t\u000b\u0011\u0002Z;sCRLwN\u001c\u0011\t\u0011\u0019\u0003!Q1A\u0005\u0002\u001d\u000b!\"\u001a=fGV$xN]%e+\u0005A\u0005CA%M\u001d\t\t\"*\u0003\u0002L%\u00051\u0001K]3eK\u001aL!!\u0014(\u0003\rM#(/\u001b8h\u0015\tY%\u0003\u0003\u0005Q\u0001\t\u0005\t\u0015!\u0003I\u0003-)\u00070Z2vi>\u0014\u0018\n\u001a\u0011\t\u0011I\u0003!Q1A\u0005\u0002\u001d\u000bA\u0001[8ti\"AA\u000b\u0001B\u0001B\u0003%\u0001*A\u0003i_N$\b\u0005\u0003\u0005\b\u0001\t\u0015\r\u0011\"\u0001H\u0011!9\u0006A!A!\u0002\u0013A\u0015aB:uCR,8\u000f\t\u0005\t3\u0002\u0011)\u0019!C\u0001\u000f\u0006aA/Y:l\u0019>\u001c\u0017\r\\5us\"A1\f\u0001B\u0001B\u0003%\u0001*A\u0007uCN\\Gj\\2bY&$\u0018\u0010\t\u0005\t;\u0002\u0011)\u0019!C\u0001=\u0006Y1\u000f]3dk2\fG/\u001b<f+\u0005y\u0006CA\ta\u0013\t\t'CA\u0004C_>dW-\u00198\t\u0011\r\u0004!\u0011!Q\u0001\n}\u000bAb\u001d9fGVd\u0017\r^5wK\u0002B\u0001\"\u001a\u0001\u0003\u0006\u0004%\tAZ\u0001\u0013C\u000e\u001cW/\\;mCR|'/\u00169eCR,7/F\u0001h!\rA\u0007o\u001d\b\u0003S:t!A[7\u000e\u0003-T!\u0001\u001c\b\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0019\u0012BA8\u0013\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u001d:\u0003\u0007M+\u0017O\u0003\u0002p%A\u0011A/^\u0007\u0002\u0005%\u0011aO\u0001\u0002\u0010\u0003\u000e\u001cW/\\;mC\ndW-\u00138g_\"A\u0001\u0010\u0001B\u0001B\u0003%q-A\nbG\u000e,X.\u001e7bi>\u0014X\u000b\u001d3bi\u0016\u001c\b\u0005\u0003\u0005{\u0001\t\u0015\r\u0011\"\u0001|\u00031)'O]8s\u001b\u0016\u001c8/Y4f+\u0005a\bcA\t<\u0011\"Aa\u0010\u0001B\u0001B\u0003%A0A\u0007feJ|'/T3tg\u0006<W\r\t\u0005\u000b\u0003\u0003\u0001!Q1A\u0005\u0002\u0005\r\u0011a\u0003;bg.lU\r\u001e:jGN,\"!!\u0002\u0011\tEY\u0014q\u0001\t\u0004i\u0006%\u0011bAA\u0006\u0005\tYA+Y:l\u001b\u0016$(/[2t\u0011)\ty\u0001\u0001B\u0001B\u0003%\u0011QA\u0001\ri\u0006\u001c8.T3ue&\u001c7\u000f\t\u0005\t\u0003'\u0001A\u0011\u0001\u0005\u0002\u0016\u00051A(\u001b8jiz\"b$a\u0006\u0002\u001a\u0005m\u0011QDA\u0010\u0003C\t\u0019#!\u0015\u0002T\u0005U\u0013qKA-\u00037\ni&a\u0018\u0011\u0005Q\u0004\u0001BB\f\u0002\u0012\u0001\u0007\u0011\u0004\u0003\u0004 \u0003#\u0001\r!\t\u0005\u0007O\u0005E\u0001\u0019A\u0011\t\r-\n\t\u00021\u0001.\u0011\u0019A\u0014\u0011\u0003a\u0001u!1\u0001)!\u0005A\u0002\tC\u0003\"a\t\u0002(\u0005\r\u0013Q\t\t\u0005\u0003S\ty$\u0004\u0002\u0002,)!\u0011QFA\u0018\u0003)\tgN\\8uCRLwN\u001c\u0006\u0005\u0003c\t\u0019$\u0001\u0005eCR\f'-\u001b8e\u0015\u0011\t)$a\u000e\u0002\u000f)\f7m[:p]*!\u0011\u0011HA\u001e\u0003%1\u0017m\u001d;feblGN\u0003\u0002\u0002>\u0005\u00191m\\7\n\t\u0005\u0005\u00131\u0006\u0002\u0010\u0015N|g\u000eR3tKJL\u0017\r\\5{K\u0006I1m\u001c8uK:$\u0018i]\u0012\u0003\u0003\u000f\u0002B!!\u0013\u0002P5\u0011\u00111\n\u0006\u0004\u0003\u001b\n\u0014\u0001\u00027b]\u001eL1aGA&\u0011\u00191\u0015\u0011\u0003a\u0001\u0011\"1!+!\u0005A\u0002!CaaBA\t\u0001\u0004A\u0005BB-\u0002\u0012\u0001\u0007\u0001\n\u0003\u0004^\u0003#\u0001\ra\u0018\u0005\u0007K\u0006E\u0001\u0019A4\t\u0011i\f\t\u0002%AA\u0002qD!\"!\u0001\u0002\u0012A\u0005\t\u0019AA\u0003\u000f%\t\u0019GAA\u0001\u0012\u0003\t)'\u0001\u0005UCN\\G)\u0019;b!\r!\u0018q\r\u0004\t\u0003\t\t\t\u0011#\u0001\u0002jM\u0019\u0011q\r\t\t\u0011\u0005M\u0011q\rC\u0001\u0003[\"\"!!\u001a\t\u0015\u0005E\u0014qMI\u0001\n\u0003\t\u0019(\u0001\u000f%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%M\u001a\u0016\u0005\u0005U$f\u0001?\u0002x-\u0012\u0011\u0011\u0010\t\u0005\u0003w\n\u0019)\u0004\u0002\u0002~)!\u0011qPAA\u0003%)hn\u00195fG.,GMC\u0002\u0002.IIA!!\"\u0002~\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0015\u0005%\u0015qMI\u0001\n\u0003\tY)\u0001\u000f%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%\r\u001b\u0016\u0005\u00055%\u0006BA\u0003\u0003o\u0002"
)
public class TaskData {
   private final long taskId;
   private final int index;
   private final int attempt;
   private final Date launchTime;
   private final Option<Date> resultFetchStart;
   private final Option<Object> duration;
   private final String executorId;
   private final String host;
   private final String status;
   private final String taskLocality;
   private final boolean speculative;
   private final Seq<AccumulableInfo> accumulatorUpdates;
   private final Option<String> errorMessage;
   private final Option<TaskMetrics> taskMetrics;

   public static Option<TaskMetrics> $lessinit$greater$default$14() {
      return TaskData$.MODULE$.$lessinit$greater$default$14();
   }

   public static Option<String> $lessinit$greater$default$13() {
      return TaskData$.MODULE$.$lessinit$greater$default$13();
   }

   public long taskId() {
      return this.taskId;
   }

   public int index() {
      return this.index;
   }

   public int attempt() {
      return this.attempt;
   }

   public Date launchTime() {
      return this.launchTime;
   }

   public Option<Date> resultFetchStart() {
      return this.resultFetchStart;
   }

   public Option<Object> duration() {
      return this.duration;
   }

   public String executorId() {
      return this.executorId;
   }

   public String host() {
      return this.host;
   }

   public String status() {
      return this.status;
   }

   public String taskLocality() {
      return this.taskLocality;
   }

   public boolean speculative() {
      return this.speculative;
   }

   public Seq<AccumulableInfo> accumulatorUpdates() {
      return this.accumulatorUpdates;
   }

   public Option<String> errorMessage() {
      return this.errorMessage;
   }

   public Option<TaskMetrics> taskMetrics() {
      return this.taskMetrics;
   }

   public TaskData(long taskId, int index, int attempt, Date launchTime, Option<Date> resultFetchStart, @JsonDeserialize(contentAs = Long.class) Option<Object> duration, String executorId, String host, String status, String taskLocality, boolean speculative, Seq<AccumulableInfo> accumulatorUpdates, Option<String> errorMessage, Option<TaskMetrics> taskMetrics) {
      this.taskId = taskId;
      this.index = index;
      this.attempt = attempt;
      this.launchTime = launchTime;
      this.resultFetchStart = resultFetchStart;
      this.duration = duration;
      this.executorId = executorId;
      this.host = host;
      this.status = status;
      this.taskLocality = taskLocality;
      this.speculative = speculative;
      this.accumulatorUpdates = accumulatorUpdates;
      this.errorMessage = errorMessage;
      this.taskMetrics = taskMetrics;
   }
}
