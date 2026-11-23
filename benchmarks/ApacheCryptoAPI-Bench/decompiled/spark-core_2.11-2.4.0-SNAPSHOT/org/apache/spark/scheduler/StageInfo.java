package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.storage.RDDInfo;
import scala.None$;
import scala.Option;
import scala.Some;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005eg\u0001B\u0001\u0003\u0001-\u0011\u0011b\u0015;bO\u0016LeNZ8\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007\u0002C\n\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002\u000fM$\u0018mZ3JIV\tQ\u0003\u0005\u0002\u000e-%\u0011qC\u0004\u0002\u0004\u0013:$\b\u0002C\r\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u0011M$\u0018mZ3JI\u0002B\u0001b\u0007\u0001\u0003\u0006\u0004%\t\u0001F\u0001\nCR$X-\u001c9u\u0013\u0012DCAG\u000f!EA\u0011QBH\u0005\u0003?9\u0011!\u0002Z3qe\u0016\u001c\u0017\r^3eC\u0005\t\u0013!G+tK\u0002\nG\u000f^3naRtU/\u001c2fe\u0002Jgn\u001d;fC\u0012\f\u0013aI\u0001\u0006e9\u001ad\u0006\r\u0005\tK\u0001\u0011\t\u0011)A\u0005+\u0005Q\u0011\r\u001e;f[B$\u0018\n\u001a\u0011\t\u0011\u001d\u0002!Q1A\u0005\u0002!\nAA\\1nKV\t\u0011\u0006\u0005\u0002+[9\u0011QbK\u0005\u0003Y9\ta\u0001\u0015:fI\u00164\u0017B\u0001\u00180\u0005\u0019\u0019FO]5oO*\u0011AF\u0004\u0005\tc\u0001\u0011\t\u0011)A\u0005S\u0005)a.Y7fA!A1\u0007\u0001BC\u0002\u0013\u0005A#\u0001\u0005ok6$\u0016m]6t\u0011!)\u0004A!A!\u0002\u0013)\u0012!\u00038v[R\u000b7o[:!\u0011!9\u0004A!b\u0001\n\u0003A\u0014\u0001\u0003:eI&sgm\\:\u0016\u0003e\u00022A\u000f\"F\u001d\tY\u0004I\u0004\u0002=\u007f5\tQH\u0003\u0002?\u0015\u00051AH]8pizJ\u0011aD\u0005\u0003\u0003:\tq\u0001]1dW\u0006<W-\u0003\u0002D\t\n\u00191+Z9\u000b\u0005\u0005s\u0001C\u0001$J\u001b\u00059%B\u0001%\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016L!AS$\u0003\u000fI#E)\u00138g_\"AA\n\u0001B\u0001B\u0003%\u0011(A\u0005sI\u0012LeNZ8tA!Aa\n\u0001BC\u0002\u0013\u0005q*A\u0005qCJ,g\u000e^%egV\t\u0001\u000bE\u0002;\u0005VA\u0001B\u0015\u0001\u0003\u0002\u0003\u0006I\u0001U\u0001\u000ba\u0006\u0014XM\u001c;JIN\u0004\u0003\u0002\u0003+\u0001\u0005\u000b\u0007I\u0011\u0001\u0015\u0002\u000f\u0011,G/Y5mg\"Aa\u000b\u0001B\u0001B\u0003%\u0011&\u0001\u0005eKR\f\u0017\u000e\\:!\u0011!A\u0006A!b\u0001\n\u0003I\u0016a\u0003;bg.lU\r\u001e:jGN,\u0012A\u0017\t\u00037zk\u0011\u0001\u0018\u0006\u0003;\u0012\t\u0001\"\u001a=fGV$xN]\u0005\u0003?r\u00131\u0002V1tW6+GO]5dg\"A\u0011\r\u0001B\u0001B\u0003%!,\u0001\u0007uCN\\W*\u001a;sS\u000e\u001c\b\u0005C\u0005d\u0001\t\u0015\r\u0011\"\u0001\u0005I\u00069B/Y:l\u0019>\u001c\u0017\r\\5usB\u0013XMZ3sK:\u001cWm]\u000b\u0002KB\u0019!H\u00114\u0011\u0007i\u0012u\r\u0005\u0002iS6\t!!\u0003\u0002k\u0005\taA+Y:l\u0019>\u001c\u0017\r^5p]\"AA\u000e\u0001B\u0001B\u0003%Q-\u0001\ruCN\\Gj\\2bY&$\u0018\u0010\u0015:fM\u0016\u0014XM\\2fg\u0002BQA\u001c\u0001\u0005\u0002=\fa\u0001P5oSRtDC\u00039reN$XO^<ysB\u0011\u0001\u000e\u0001\u0005\u0006'5\u0004\r!\u0006\u0005\u000675\u0004\r!\u0006\u0005\u0006O5\u0004\r!\u000b\u0005\u0006g5\u0004\r!\u0006\u0005\u0006o5\u0004\r!\u000f\u0005\u0006\u001d6\u0004\r\u0001\u0015\u0005\u0006)6\u0004\r!\u000b\u0005\b16\u0004\n\u00111\u0001[\u0011\u001d\u0019W\u000e%AA\u0002\u0015Dqa\u001f\u0001A\u0002\u0013\u0005A0\u0001\btk\nl\u0017n]:j_:$\u0016.\\3\u0016\u0003u\u0004B!\u0004@\u0002\u0002%\u0011qP\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u00075\t\u0019!C\u0002\u0002\u00069\u0011A\u0001T8oO\"I\u0011\u0011\u0002\u0001A\u0002\u0013\u0005\u00111B\u0001\u0013gV\u0014W.[:tS>tG+[7f?\u0012*\u0017\u000f\u0006\u0003\u0002\u000e\u0005M\u0001cA\u0007\u0002\u0010%\u0019\u0011\u0011\u0003\b\u0003\tUs\u0017\u000e\u001e\u0005\n\u0003+\t9!!AA\u0002u\f1\u0001\u001f\u00132\u0011\u001d\tI\u0002\u0001Q!\nu\fqb];c[&\u001c8/[8o)&lW\r\t\u0005\t\u0003;\u0001\u0001\u0019!C\u0001y\u0006q1m\\7qY\u0016$\u0018n\u001c8US6,\u0007\"CA\u0011\u0001\u0001\u0007I\u0011AA\u0012\u0003I\u0019w.\u001c9mKRLwN\u001c+j[\u0016|F%Z9\u0015\t\u00055\u0011Q\u0005\u0005\n\u0003+\ty\"!AA\u0002uDq!!\u000b\u0001A\u0003&Q0A\bd_6\u0004H.\u001a;j_:$\u0016.\\3!\u0011%\ti\u0003\u0001a\u0001\n\u0003\ty#A\u0007gC&dWO]3SK\u0006\u001cxN\\\u000b\u0003\u0003c\u00012!\u0004@*\u0011%\t)\u0004\u0001a\u0001\n\u0003\t9$A\tgC&dWO]3SK\u0006\u001cxN\\0%KF$B!!\u0004\u0002:!Q\u0011QCA\u001a\u0003\u0003\u0005\r!!\r\t\u0011\u0005u\u0002\u0001)Q\u0005\u0003c\taBZ1jYV\u0014XMU3bg>t\u0007\u0005C\u0005\u0002B\u0001\u0011\r\u0011\"\u0001\u0002D\u0005a\u0011mY2v[Vd\u0017M\u00197fgV\u0011\u0011Q\t\t\t\u0003\u000f\n\t&!\u0001\u0002V5\u0011\u0011\u0011\n\u0006\u0005\u0003\u0017\ni%A\u0004nkR\f'\r\\3\u000b\u0007\u0005=c\"\u0001\u0006d_2dWm\u0019;j_:LA!a\u0015\u0002J\t9\u0001*Y:i\u001b\u0006\u0004\bc\u00015\u0002X%\u0019\u0011\u0011\f\u0002\u0003\u001f\u0005\u001b7-^7vY\u0006\u0014G.Z%oM>D\u0001\"!\u0018\u0001A\u0003%\u0011QI\u0001\u000eC\u000e\u001cW/\\;mC\ndWm\u001d\u0011\t\u000f\u0005\u0005\u0004\u0001\"\u0001\u0002d\u0005Y1\u000f^1hK\u001a\u000b\u0017\u000e\\3e)\u0011\ti!!\u001a\t\u000f\u0005\u001d\u0014q\fa\u0001S\u00051!/Z1t_:Dq!a\u001b\u0001\t\u0003\ti'A\u0007biR,W\u000e\u001d;Ok6\u0014WM\u001d\u000b\u0002+!9\u0011\u0011\u000f\u0001\u0005\u0002\u0011A\u0013aD4fiN#\u0018\r^;t'R\u0014\u0018N\\4)\u0007\u0001\t)\b\u0005\u0003\u0002x\u0005uTBAA=\u0015\r\tY\bB\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BA@\u0003s\u0012A\u0002R3wK2|\u0007/\u001a:Ba&<\u0001\"a!\u0003\u0011\u0003!\u0011QQ\u0001\n'R\fw-Z%oM>\u00042\u0001[AD\r\u001d\t!\u0001#\u0001\u0005\u0003\u0013\u001b2!a\"\r\u0011\u001dq\u0017q\u0011C\u0001\u0003\u001b#\"!!\"\t\u0011\u0005E\u0015q\u0011C\u0001\u0003'\u000b\u0011B\u001a:p[N#\u0018mZ3\u0015\u0017A\f)*a(\u0002\"\u0006\u0015\u0016q\u0015\u0005\t\u0003/\u000by\t1\u0001\u0002\u001a\u0006)1\u000f^1hKB\u0019\u0001.a'\n\u0007\u0005u%AA\u0003Ti\u0006<W\r\u0003\u0004\u001c\u0003\u001f\u0003\r!\u0006\u0005\ng\u0005=\u0005\u0013!a\u0001\u0003G\u00032!\u0004@\u0016\u0011!A\u0016q\u0012I\u0001\u0002\u0004Q\u0006\u0002C2\u0002\u0010B\u0005\t\u0019A3\t\u0015\u0005-\u0016qQI\u0001\n\u0003\ti+A\nge>l7\u000b^1hK\u0012\"WMZ1vYR$3'\u0006\u0002\u00020*\"\u00111UAYW\t\t\u0019\f\u0005\u0003\u00026\u0006uVBAA\\\u0015\u0011\tI,a/\u0002\u0013Ut7\r[3dW\u0016$'bAA>\u001d%!\u0011qXA\\\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\u000b\u0003\u0007\f9)%A\u0005\u0002\u0005\u0015\u0017a\u00054s_6\u001cF/Y4fI\u0011,g-Y;mi\u0012\"TCAAdU\rQ\u0016\u0011\u0017\u0005\u000b\u0003\u0017\f9)%A\u0005\u0002\u00055\u0017a\u00054s_6\u001cF/Y4fI\u0011,g-Y;mi\u0012*TCAAhU\r)\u0017\u0011\u0017\u0005\u000b\u0003'\f9)%A\u0005\u0002\u0005\u0015\u0017a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$\u0003\b\u0003\u0006\u0002X\u0006\u001d\u0015\u0013!C\u0001\u0003\u001b\f1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012J\u0004"
)
public class StageInfo {
   private final int stageId;
   private final int attemptId;
   private final String name;
   private final int numTasks;
   private final Seq<RDDInfo> rddInfos;
   private final Seq<Object> parentIds;
   private final String details;
   private final TaskMetrics taskMetrics;
   private final Seq<Seq<TaskLocation>> taskLocalityPreferences;
   private Option<Object> submissionTime;
   private Option<Object> completionTime;
   private Option<String> failureReason;
   private final HashMap<Object, AccumulableInfo> accumulables;

   public static Seq<Seq<TaskLocation>> $lessinit$greater$default$9() {
      return StageInfo$.MODULE$.$lessinit$greater$default$9();
   }

   public static TaskMetrics $lessinit$greater$default$8() {
      return StageInfo$.MODULE$.$lessinit$greater$default$8();
   }

   public static Seq<Seq<TaskLocation>> fromStage$default$5() {
      return StageInfo$.MODULE$.fromStage$default$5();
   }

   public static TaskMetrics fromStage$default$4() {
      return StageInfo$.MODULE$.fromStage$default$4();
   }

   public static Option<Object> fromStage$default$3() {
      return StageInfo$.MODULE$.fromStage$default$3();
   }

   public static StageInfo fromStage(Stage var0, int var1, Option<Object> var2, TaskMetrics var3, Seq<Seq<TaskLocation>> var4) {
      return StageInfo$.MODULE$.fromStage(var0, var1, var2, var3, var4);
   }

   public int stageId() {
      return this.stageId;
   }

   /** @deprecated */
   public int attemptId() {
      return this.attemptId;
   }

   public String name() {
      return this.name;
   }

   public int numTasks() {
      return this.numTasks;
   }

   public Seq<RDDInfo> rddInfos() {
      return this.rddInfos;
   }

   public Seq<Object> parentIds() {
      return this.parentIds;
   }

   public String details() {
      return this.details;
   }

   public TaskMetrics taskMetrics() {
      return this.taskMetrics;
   }

   public Seq<Seq<TaskLocation>> taskLocalityPreferences() {
      return this.taskLocalityPreferences;
   }

   public Option<Object> submissionTime() {
      return this.submissionTime;
   }

   public void submissionTime_$eq(Option<Object> x$1) {
      this.submissionTime = x$1;
   }

   public Option<Object> completionTime() {
      return this.completionTime;
   }

   public void completionTime_$eq(Option<Object> x$1) {
      this.completionTime = x$1;
   }

   public Option<String> failureReason() {
      return this.failureReason;
   }

   public void failureReason_$eq(Option<String> x$1) {
      this.failureReason = x$1;
   }

   public HashMap<Object, AccumulableInfo> accumulables() {
      return this.accumulables;
   }

   public void stageFailed(String reason) {
      this.failureReason_$eq(new Some(reason));
      this.completionTime_$eq(new Some(BoxesRunTime.boxToLong(System.currentTimeMillis())));
   }

   public int attemptNumber() {
      return this.attemptId();
   }

   public String getStatusString() {
      return this.completionTime().isDefined() ? (this.failureReason().isDefined() ? "failed" : "succeeded") : "running";
   }

   public StageInfo(int stageId, int attemptId, String name, int numTasks, Seq<RDDInfo> rddInfos, Seq<Object> parentIds, String details, TaskMetrics taskMetrics, Seq<Seq<TaskLocation>> taskLocalityPreferences) {
      this.stageId = stageId;
      this.attemptId = attemptId;
      this.name = name;
      this.numTasks = numTasks;
      this.rddInfos = rddInfos;
      this.parentIds = parentIds;
      this.details = details;
      this.taskMetrics = taskMetrics;
      this.taskLocalityPreferences = taskLocalityPreferences;
      this.submissionTime = None$.MODULE$;
      this.completionTime = None$.MODULE$;
      this.failureReason = None$.MODULE$;
      this.accumulables = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
   }
}
