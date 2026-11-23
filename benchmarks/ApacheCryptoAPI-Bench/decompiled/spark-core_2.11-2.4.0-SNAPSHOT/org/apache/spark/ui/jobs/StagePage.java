package org.apache.spark.ui.jobs;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.scheduler.TaskLocality$;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.AppStatusUtils$;
import org.apache.spark.status.api.v1.AccumulableInfo;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.TaskData;
import org.apache.spark.status.api.v1.TaskMetricDistributions;
import org.apache.spark.status.api.v1.TaskMetrics;
import org.apache.spark.ui.ToolTips$;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.ui.scope.RDDOperationGraph;
import org.apache.spark.util.Utils$;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IndexedSeq;
import scala.collection.IndexedSeq$;
import scala.collection.Iterable;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Iterable$;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.LongRef;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.Nothing$;
import scala.runtime.RichLong$;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.NodeSeq;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;

@ScalaSignature(
   bytes = "\u0006\u0001q4Q!\u0001\u0002\u0001\t1\u0011\u0011b\u0015;bO\u0016\u0004\u0016mZ3\u000b\u0005\r!\u0011\u0001\u00026pENT!!\u0002\u0004\u0002\u0005UL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0010\u001b\u0005!\u0011B\u0001\t\u0005\u0005%9VMY+J!\u0006<W\r\u0003\u0005\u0013\u0001\t\u0005\t\u0015!\u0003\u0015\u0003\u0019\u0001\u0018M]3oi\u000e\u0001\u0001CA\u000b\u0017\u001b\u0005\u0011\u0011BA\f\u0003\u0005%\u0019F/Y4fgR\u000b'\r\u0003\u0005\u001a\u0001\t\u0005\t\u0015!\u0003\u001b\u0003\u0015\u0019Ho\u001c:f!\tYb$D\u0001\u001d\u0015\tib!\u0001\u0004ti\u0006$Xo]\u0005\u0003?q\u0011a\"\u00119q'R\fG/^:Ti>\u0014X\rC\u0003\"\u0001\u0011\u0005!%\u0001\u0004=S:LGO\u0010\u000b\u0004G\u0011*\u0003CA\u000b\u0001\u0011\u0015\u0011\u0002\u00051\u0001\u0015\u0011\u0015I\u0002\u00051\u0001\u001b\u0011\u001d9\u0003A1A\u0005\n!\nq\u0002V%N\u000b2Ke*R0M\u000b\u001e+e\nR\u000b\u0002SA\u0011!fL\u0007\u0002W)\u0011A&L\u0001\u0004q6d'\"\u0001\u0018\u0002\u000bM\u001c\u0017\r\\1\n\u0005AZ#\u0001B#mK6DaA\r\u0001!\u0002\u0013I\u0013\u0001\u0005+J\u001b\u0016c\u0015JT#`\u0019\u0016;UI\u0014#!\u0011\u001d!\u0004A1A\u0005\nU\n!#T!Y?RKU*\u0012'J\u001d\u0016{F+Q*L'V\ta\u0007\u0005\u00028q5\tQ&\u0003\u0002:[\t\u0019\u0011J\u001c;\t\rm\u0002\u0001\u0015!\u00037\u0003Mi\u0015\tW0U\u00136+E*\u0013(F?R\u000b5kS*!\u0011\u0015i\u0004\u0001\"\u0003?\u0003a9W\r\u001e'pG\u0006d\u0017\u000e^=Tk6l\u0017M]=TiJLgn\u001a\u000b\u0003\u007f\u0019\u0003\"\u0001Q\"\u000f\u0005]\n\u0015B\u0001\".\u0003\u0019\u0001&/\u001a3fM&\u0011A)\u0012\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\tk\u0003\"B$=\u0001\u0004A\u0015a\u00047pG\u0006d\u0017\u000e^=Tk6l\u0017M]=\u0011\t\u0001KuhS\u0005\u0003\u0015\u0016\u00131!T1q!\t9D*\u0003\u0002N[\t!Aj\u001c8h\u0011\u0015y\u0005\u0001\"\u0001Q\u0003\u0019\u0011XM\u001c3feR\u0011\u0011\u000b\u0019\t\u0004%jkfBA*Y\u001d\t!v+D\u0001V\u0015\t16#\u0001\u0004=e>|GOP\u0005\u0002]%\u0011\u0011,L\u0001\ba\u0006\u001c7.Y4f\u0013\tYFLA\u0002TKFT!!W\u0017\u0011\u0005)r\u0016BA0,\u0005\u0011qu\u000eZ3\t\u000b\u0005t\u0005\u0019\u00012\u0002\u000fI,\u0017/^3tiB\u00111M[\u0007\u0002I*\u0011QMZ\u0001\u0005QR$\bO\u0003\u0002hQ\u000691/\u001a:wY\u0016$(\"A5\u0002\u000b)\fg/\u0019=\n\u0005-$'A\u0005%uiB\u001cVM\u001d<mKR\u0014V-];fgRDQ!\u001c\u0001\u0005\u00029\fA\"\\1lKRKW.\u001a7j]\u0016$2!U8{\u0011\u0015\u0001H\u000e1\u0001r\u0003\u0015!\u0018m]6t!\r\u0011&L\u001d\t\u0003gbl\u0011\u0001\u001e\u0006\u0003kZ\f!A^\u0019\u000b\u0005]d\u0012aA1qS&\u0011\u0011\u0010\u001e\u0002\t)\u0006\u001c8\u000eR1uC\")1\u0010\u001ca\u0001\u0017\u0006Y1-\u001e:sK:$H+[7f\u0001"
)
public class StagePage extends WebUIPage {
   public final StagesTab org$apache$spark$ui$jobs$StagePage$$parent;
   private final AppStatusStore store;
   private final Elem TIMELINE_LEGEND;
   private final int MAX_TIMELINE_TASKS;

   private Elem TIMELINE_LEGEND() {
      return this.TIMELINE_LEGEND;
   }

   private int MAX_TIMELINE_TASKS() {
      return this.MAX_TIMELINE_TASKS;
   }

   private String getLocalitySummaryString(Map<String, Object> localitySummary) {
      Map names = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(TaskLocality$.MODULE$.PROCESS_LOCAL().toString()), "Process local"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(TaskLocality$.MODULE$.NODE_LOCAL().toString()), "Node local"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(TaskLocality$.MODULE$.RACK_LOCAL().toString()), "Rack local"), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(TaskLocality$.MODULE$.ANY().toString()), "Any")}));
      Seq localityNamesAndCounts = ((TraversableOnce)names.flatMap(new Serializable(this, localitySummary) {
         public static final long serialVersionUID = 0L;
         private final Map localitySummary$1;

         public final Iterable<String> apply(Tuple2<String, String> x0$2) {
            if (x0$2 != null) {
               String key = (String)x0$2._1();
               String name = (String)x0$2._2();
               Iterable var5 = Option$.MODULE$.option2Iterable(this.localitySummary$1.get(key).map(new Serializable(this, name) {
                  public static final long serialVersionUID = 0L;
                  private final String name$1;

                  public final String apply(long count) {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.name$1, BoxesRunTime.boxToLong(count)}));
                  }

                  public {
                     this.name$1 = name$1;
                  }
               }));
               return var5;
            } else {
               throw new MatchError(x0$2);
            }
         }

         public {
            this.localitySummary$1 = localitySummary$1;
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq();
      return ((TraversableOnce)localityNamesAndCounts.sorted(Ordering.String$.MODULE$)).mkString("; ");
   }

   public Seq<Node> render(HttpServletRequest request) {
      Object var2 = new Object();

      Seq var10000;
      try {
         String parameterId = UIUtils$.MODULE$.stripXSS(request.getParameter("id"));
         Predef$.MODULE$.require(parameterId != null && (new StringOps(Predef$.MODULE$.augmentString(parameterId))).nonEmpty(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Missing id parameter";
            }
         });
         String parameterAttempt = UIUtils$.MODULE$.stripXSS(request.getParameter("attempt"));
         Predef$.MODULE$.require(parameterAttempt != null && (new StringOps(Predef$.MODULE$.augmentString(parameterAttempt))).nonEmpty(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Missing attempt parameter";
            }
         });
         String parameterTaskPage = UIUtils$.MODULE$.stripXSS(request.getParameter("task.page"));
         String parameterTaskSortColumn = UIUtils$.MODULE$.stripXSS(request.getParameter("task.sort"));
         String parameterTaskSortDesc = UIUtils$.MODULE$.stripXSS(request.getParameter("task.desc"));
         String parameterTaskPageSize = UIUtils$.MODULE$.stripXSS(request.getParameter("task.pageSize"));
         String parameterTaskPrevPageSize = UIUtils$.MODULE$.stripXSS(request.getParameter("task.prevPageSize"));
         int taskPage = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterTaskPage).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String x$1) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toInt();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return 1;
            }
         }));
         String taskSortColumn = (String)Option$.MODULE$.apply(parameterTaskSortColumn).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(String sortColumn) {
               return UIUtils$.MODULE$.decodeURLParameter(sortColumn);
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Index";
            }
         });
         boolean taskSortDesc = BoxesRunTime.unboxToBoolean(Option$.MODULE$.apply(parameterTaskSortDesc).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(String x$2) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$2))).toBoolean();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply() {
               return this.apply$mcZ$sp();
            }

            public boolean apply$mcZ$sp() {
               return false;
            }
         }));
         int taskPageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterTaskPageSize).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String x$3) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$3))).toInt();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return 100;
            }
         }));
         int taskPrevPageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterTaskPrevPageSize).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String x$4) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$4))).toInt();
            }
         }).getOrElse(new Serializable(this, taskPageSize) {
            public static final long serialVersionUID = 0L;
            private final int taskPageSize$1;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return this.taskPageSize$1;
            }

            public {
               this.taskPageSize$1 = taskPageSize$1;
            }
         }));
         int stageId = (new StringOps(Predef$.MODULE$.augmentString(parameterId))).toInt();
         int stageAttemptId = (new StringOps(Predef$.MODULE$.augmentString(parameterAttempt))).toInt();
         String stageHeader = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Details for Stage ", " (Attempt ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(stageAttemptId)}));
         StageData stageData = (StageData)this.org$apache$spark$ui$jobs$StagePage$$parent.store().asOption(new Serializable(this, stageId, stageAttemptId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StagePage $outer;
            private final int stageId$1;
            private final int stageAttemptId$1;

            public final StageData apply() {
               return this.$outer.org$apache$spark$ui$jobs$StagePage$$parent.store().stageAttempt(this.stageId$1, this.stageAttemptId$1, false);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.stageId$1 = stageId$1;
                  this.stageAttemptId$1 = stageAttemptId$1;
               }
            }
         }).getOrElse(new Serializable(this, stageId, stageAttemptId, stageHeader, var2) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StagePage $outer;
            private final int stageId$1;
            private final int stageAttemptId$1;
            private final String stageHeader$1;
            private final Object nonLocalReturnKey1$1;

            public final Nothing$ apply() {
               MetaData $md = .MODULE$;
               MetaData var5 = new UnprefixedAttribute("id", new Text("no-info"), $md);
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n            "));
               Null var10012 = .MODULE$;
               TopScope var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("No information to display for Stage "));
               $buf.$amp$plus(BoxesRunTime.boxToInteger(this.stageId$1));
               $buf.$amp$plus(new Text(" (Attempt "));
               $buf.$amp$plus(BoxesRunTime.boxToInteger(this.stageAttemptId$1));
               $buf.$amp$plus(new Text(")"));
               $buf.$amp$plus(new Elem((String)null, "p", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n          "));
               Elem content = new Elem((String)null, "div", var5, var10005, false, $buf);
               throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, UIUtils$.MODULE$.headerSparkPage(this.stageHeader$1, new Serializable(this, content) {
                  public static final long serialVersionUID = 0L;
                  private final Elem content$2;

                  public final Elem apply() {
                     return this.content$2;
                  }

                  public {
                     this.content$2 = content$2;
                  }
               }, this.$outer.org$apache$spark$ui$jobs$StagePage$$parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7()));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.stageId$1 = stageId$1;
                  this.stageAttemptId$1 = stageAttemptId$1;
                  this.stageHeader$1 = stageHeader$1;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            }
         });
         Map localitySummary = this.store.localitySummary(stageData.stageId(), stageData.attemptId());
         int totalTasks = stageData.numActiveTasks() + stageData.numCompleteTasks() + stageData.numFailedTasks() + stageData.numKilledTasks();
         if (totalTasks == 0) {
            Null var227 = .MODULE$;
            TopScope var233 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var10012 = .MODULE$;
            TopScope var245 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Summary Metrics"));
            $buf.$amp$plus(new Elem((String)null, "h4", var10012, var245, false, $buf));
            $buf.$amp$plus(new Text(" No tasks have started yet\n          "));
            var10012 = .MODULE$;
            var245 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Tasks"));
            $buf.$amp$plus(new Elem((String)null, "h4", var10012, var245, false, $buf));
            $buf.$amp$plus(new Text(" No tasks have started yet\n        "));
            Elem content = new Elem((String)null, "div", var227, var233, false, $buf);
            return UIUtils$.MODULE$.headerSparkPage(stageHeader, new Serializable(this, content) {
               public static final long serialVersionUID = 0L;
               private final Elem content$3;

               public final Elem apply() {
                  return this.content$3;
               }

               public {
                  this.content$3 = content$3;
               }
            }, this.org$apache$spark$ui$jobs$StagePage$$parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
         }

         long storedTasks = this.store.taskCount(stageData.stageId(), stageData.attemptId());
         int numCompleted = stageData.numCompleteTasks();
         String totalTasksNumStr = (long)totalTasks == storedTasks ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(totalTasks)})) : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", showing ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(totalTasks), BoxesRunTime.boxToLong(storedTasks)}));
         Elem var217 = new Elem;
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var158 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
         Elem var10008 = new Elem;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10020 = .MODULE$;
         TopScope var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         Null var10028 = .MODULE$;
         TopScope var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Total Time Across All Tasks: "));
         $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(UIUtils$.MODULE$.formatDuration(stageData.executorRunTime()));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "li", var10020, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         var10020 = .MODULE$;
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Locality Level Summary: "));
         $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(this.getLocalitySummaryString(localitySummary));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "li", var10020, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         Object var10016;
         if (ApiHelper$.MODULE$.hasInput(stageData)) {
            var10020 = .MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Input Size / Records: "));
            $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(stageData.inputBytes()), BoxesRunTime.boxToLong(stageData.inputRecords())})));
            $buf.$amp$plus(new Text("\n            "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (ApiHelper$.MODULE$.hasOutput(stageData)) {
            var10020 = .MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Output: "));
            $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(stageData.outputBytes()), BoxesRunTime.boxToLong(stageData.outputRecords())})));
            $buf.$amp$plus(new Text("\n            "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (ApiHelper$.MODULE$.hasShuffleRead(stageData)) {
            var10020 = .MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Shuffle Read: "));
            $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(stageData.shuffleReadBytes())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(stageData.shuffleReadRecords())}))).toString());
            $buf.$amp$plus(new Text("\n            "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (ApiHelper$.MODULE$.hasShuffleWrite(stageData)) {
            var10020 = .MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Shuffle Write: "));
            $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n               "));
            $buf.$amp$plus((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(stageData.shuffleWriteBytes())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(stageData.shuffleWriteRecords())}))).toString());
            $buf.$amp$plus(new Text("\n            "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (ApiHelper$.MODULE$.hasBytesSpilled(stageData)) {
            NodeBuffer $buf = new NodeBuffer();
            Null var266 = .MODULE$;
            TopScope var10022 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            Null var291 = .MODULE$;
            TopScope var10030 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Shuffle Spill (Memory): "));
            $buf.$amp$plus(new Elem((String)null, "strong", var291, var10030, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(Utils$.MODULE$.bytesToString(stageData.memoryBytesSpilled()));
            $buf.$amp$plus(new Text("\n            "));
            $buf.$amp$plus(new Elem((String)null, "li", var266, var10022, false, $buf));
            var266 = .MODULE$;
            var10022 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            var291 = .MODULE$;
            var10030 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Shuffle Spill (Disk): "));
            $buf.$amp$plus(new Elem((String)null, "strong", var291, var10030, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(Utils$.MODULE$.bytesToString(stageData.diskBytesSpilled()));
            $buf.$amp$plus(new Text("\n            "));
            $buf.$amp$plus(new Elem((String)null, "li", var266, var10022, false, $buf));
            var10016 = $buf;
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n        "));
         var10008.<init>((String)null, "ul", var158, var10013, false, $buf);
         $buf.$amp$plus(var10008);
         $buf.$amp$plus(new Text("\n      "));
         var217.<init>((String)null, "div", var10004, var10005, false, $buf);
         Elem summary = var217;
         var217 = new Elem;
         var10004 = .MODULE$;
         var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var159 = new UnprefixedAttribute("class", new Text("expand-additional-metrics"), $md);
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var160 = new UnprefixedAttribute("class", new Text("expand-additional-metrics-arrow arrow-closed"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var160, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n          "));
         var10020 = .MODULE$;
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Show Additional Metrics"));
         $buf.$amp$plus(new Elem((String)null, "a", var10020, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "span", var159, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var161 = new UnprefixedAttribute("class", new Text("additional-metrics collapsed"), $md);
         var10008 = new Elem;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Elem var253 = new Elem;
         var10020 = .MODULE$;
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var162 = new UnprefixedAttribute("id", new Text("select-all-metrics"), $md);
         var162 = new UnprefixedAttribute("type", new Text("checkbox"), var162);
         $buf.$amp$plus(new Elem((String)null, "input", var162, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var164 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
         TopScope var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var10044 = .MODULE$;
         TopScope var10045 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("(De)select All"));
         $buf.$amp$plus(new Elem((String)null, "em", var10044, var10045, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "span", var164, var10037, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var165 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
         var165 = new UnprefixedAttribute("title", ToolTips$.MODULE$.SCHEDULER_DELAY(), var165);
         var165 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var165);
         var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var168 = new UnprefixedAttribute("name", TaskDetailsClassNames$.MODULE$.SCHEDULER_DELAY(), $md);
         var168 = new UnprefixedAttribute("type", new Text("checkbox"), var168);
         $buf.$amp$plus(new Elem((String)null, "input", var168, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var170 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
         var10045 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Scheduler Delay"));
         $buf.$amp$plus(new Elem((String)null, "span", var170, var10045, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(new Elem((String)null, "span", var165, var10037, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var171 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
         var171 = new UnprefixedAttribute("title", ToolTips$.MODULE$.TASK_DESERIALIZATION_TIME(), var171);
         var171 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var171);
         var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var174 = new UnprefixedAttribute("name", TaskDetailsClassNames$.MODULE$.TASK_DESERIALIZATION_TIME(), $md);
         var174 = new UnprefixedAttribute("type", new Text("checkbox"), var174);
         $buf.$amp$plus(new Elem((String)null, "input", var174, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var176 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
         var10045 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Task Deserialization Time"));
         $buf.$amp$plus(new Elem((String)null, "span", var176, var10045, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(new Elem((String)null, "span", var171, var10037, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         Object var10024;
         if (stageData.shuffleReadBytes() > 0L) {
            NodeBuffer $buf = new NodeBuffer();
            Null var296 = .MODULE$;
            TopScope var302 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = .MODULE$;
            MetaData var177 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
            var177 = new UnprefixedAttribute("title", ToolTips$.MODULE$.SHUFFLE_READ_BLOCKED_TIME(), var177);
            var177 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var177);
            TopScope var10038 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                  "));
            MetaData $md = .MODULE$;
            MetaData var180 = new UnprefixedAttribute("name", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_BLOCKED_TIME(), $md);
            var180 = new UnprefixedAttribute("type", new Text("checkbox"), var180);
            $buf.$amp$plus(new Elem((String)null, "input", var180, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n                  "));
            MetaData $md = .MODULE$;
            MetaData var182 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
            TopScope var10046 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Shuffle Read Blocked Time"));
            $buf.$amp$plus(new Elem((String)null, "span", var182, var10046, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new Elem((String)null, "span", var177, var10038, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(new Elem((String)null, "li", var296, var302, false, $buf));
            var296 = .MODULE$;
            var302 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = .MODULE$;
            MetaData var183 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
            var183 = new UnprefixedAttribute("title", ToolTips$.MODULE$.SHUFFLE_READ_REMOTE_SIZE(), var183);
            var183 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var183);
            var10038 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                  "));
            MetaData $md = .MODULE$;
            MetaData var186 = new UnprefixedAttribute("name", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_REMOTE_SIZE(), $md);
            var186 = new UnprefixedAttribute("type", new Text("checkbox"), var186);
            $buf.$amp$plus(new Elem((String)null, "input", var186, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n                  "));
            MetaData $md = .MODULE$;
            MetaData var188 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
            var10046 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Shuffle Remote Reads"));
            $buf.$amp$plus(new Elem((String)null, "span", var188, var10046, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new Elem((String)null, "span", var183, var10038, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(new Elem((String)null, "li", var296, var302, false, $buf));
            var10024 = $buf;
         } else {
            var10024 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10024);
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var189 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
         var189 = new UnprefixedAttribute("title", ToolTips$.MODULE$.RESULT_SERIALIZATION_TIME(), var189);
         var189 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var189);
         var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var192 = new UnprefixedAttribute("name", TaskDetailsClassNames$.MODULE$.RESULT_SERIALIZATION_TIME(), $md);
         var192 = new UnprefixedAttribute("type", new Text("checkbox"), var192);
         $buf.$amp$plus(new Elem((String)null, "input", var192, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var194 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
         var10045 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Result Serialization Time"));
         $buf.$amp$plus(new Elem((String)null, "span", var194, var10045, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(new Elem((String)null, "span", var189, var10037, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var195 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
         var195 = new UnprefixedAttribute("title", ToolTips$.MODULE$.GETTING_RESULT_TIME(), var195);
         var195 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var195);
         var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var198 = new UnprefixedAttribute("name", TaskDetailsClassNames$.MODULE$.GETTING_RESULT_TIME(), $md);
         var198 = new UnprefixedAttribute("type", new Text("checkbox"), var198);
         $buf.$amp$plus(new Elem((String)null, "input", var198, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var200 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
         var10045 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Getting Result Time"));
         $buf.$amp$plus(new Elem((String)null, "span", var200, var10045, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(new Elem((String)null, "span", var195, var10037, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var201 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
         var201 = new UnprefixedAttribute("title", ToolTips$.MODULE$.PEAK_EXECUTION_MEMORY(), var201);
         var201 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var201);
         var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var204 = new UnprefixedAttribute("name", TaskDetailsClassNames$.MODULE$.PEAK_EXECUTION_MEMORY(), $md);
         var204 = new UnprefixedAttribute("type", new Text("checkbox"), var204);
         $buf.$amp$plus(new Elem((String)null, "input", var204, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var206 = new UnprefixedAttribute("class", new Text("additional-metric-title"), $md);
         var10045 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Peak Execution Memory"));
         $buf.$amp$plus(new Elem((String)null, "span", var206, var10045, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(new Elem((String)null, "span", var201, var10037, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         var253.<init>((String)null, "ul", var10020, var10021, false, $buf);
         $buf.$amp$plus(var253);
         $buf.$amp$plus(new Text("\n        "));
         var10008.<init>((String)null, "div", var161, var10013, false, $buf);
         $buf.$amp$plus(var10008);
         $buf.$amp$plus(new Text("\n      "));
         var217.<init>((String)null, "div", var10004, var10005, false, $buf);
         Elem showAdditionalMetrics = var217;
         Option stageGraph = this.org$apache$spark$ui$jobs$StagePage$$parent.store().asOption(new Serializable(this, stageId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StagePage $outer;
            private final int stageId$1;

            public final RDDOperationGraph apply() {
               return this.$outer.org$apache$spark$ui$jobs$StagePage$$parent.store().operationGraphForStage(this.stageId$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.stageId$1 = stageId$1;
               }
            }
         });
         Seq dagViz = UIUtils$.MODULE$.showDagVizForStage(stageId, stageGraph);
         Seq accumulableHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Accumulable", "Value"})));
         Seq accumulableTable = UIUtils$.MODULE$.listingTable(accumulableHeaders, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StagePage $outer;

            public final Seq<Node> apply(AccumulableInfo acc) {
               return this.$outer.org$apache$spark$ui$jobs$StagePage$$accumulableRow$1(acc);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, stageData.accumulatorUpdates().toSeq(), UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
         int page = taskPageSize <= taskPrevPageSize ? taskPage : 1;
         long currentTime = System.currentTimeMillis();
         Tuple2 var116 = this.liftedTree1$1(taskSortColumn, taskSortDesc, taskPageSize, stageId, stageAttemptId, stageData, page, currentTime);
         if (var116 == null) {
            throw new MatchError(var116);
         }

         TaskPagedTable taskTable = (TaskPagedTable)var116._1();
         Seq taskTableHTML = (Seq)var116._2();
         Tuple2 var119 = new Tuple2(taskTable, taskTableHTML);
         TaskPagedTable taskTable = (TaskPagedTable)var119._1();
         Seq taskTableHTML = (Seq)var119._2();
         var10004 = .MODULE$;
         var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply((new StringOps(Predef$.MODULE$.augmentString("\n            |$(function() {\n            |  if (/.*&task.sort=.*$/.test(location.search)) {\n            |    var topOffset = $(\"#tasks-section\").offset().top;\n            |    $(\"html,body\").animate({scrollTop: topOffset}, 200);\n            |  }\n            |});\n          "))).stripMargin()));
         $buf.$amp$plus(new Text("\n      "));
         Elem jsForScrollingDownToTaskTable = new Elem((String)null, "script", var10004, var10005, false, $buf);
         Option metricsSummary = this.store.taskSummary(stageData.stageId(), stageData.attemptId(), new double[]{(double)0.0F, (double)0.25F, (double)0.5F, (double)0.75F, (double)1.0F});
         Option summaryTable = metricsSummary.map(new Serializable(this, stageData) {
            public static final long serialVersionUID = 0L;
            private final StageData stageData$1;

            public final Seq<Node> apply(TaskMetricDistributions metrics) {
               Seq deserializationQuantiles = (Seq)this.titleCell$1("Task Deserialization Time", ToolTips$.MODULE$.TASK_DESERIALIZATION_TIME()).$plus$plus(this.timeQuantiles$1(metrics.executorDeserializeTime()), Seq$.MODULE$.canBuildFrom());
               Seq serviceQuantiles = (Seq)this.simpleTitleCell$1("Duration").$plus$plus(this.timeQuantiles$1(metrics.executorRunTime()), Seq$.MODULE$.canBuildFrom());
               Seq gcQuantiles = (Seq)this.titleCell$1("GC Time", ToolTips$.MODULE$.GC_TIME()).$plus$plus(this.timeQuantiles$1(metrics.jvmGcTime()), Seq$.MODULE$.canBuildFrom());
               Seq serializationQuantiles = (Seq)this.titleCell$1("Result Serialization Time", ToolTips$.MODULE$.RESULT_SERIALIZATION_TIME()).$plus$plus(this.timeQuantiles$1(metrics.resultSerializationTime()), Seq$.MODULE$.canBuildFrom());
               Seq gettingResultQuantiles = (Seq)this.titleCell$1("Getting Result Time", ToolTips$.MODULE$.GETTING_RESULT_TIME()).$plus$plus(this.timeQuantiles$1(metrics.gettingResultTime()), Seq$.MODULE$.canBuildFrom());
               Seq peakExecutionMemoryQuantiles = (Seq)this.titleCell$1("Peak Execution Memory", ToolTips$.MODULE$.PEAK_EXECUTION_MEMORY()).$plus$plus(this.sizeQuantiles$1(metrics.peakExecutionMemory()), Seq$.MODULE$.canBuildFrom());
               Seq schedulerDelayQuantiles = (Seq)this.titleCell$1("Scheduler Delay", ToolTips$.MODULE$.SCHEDULER_DELAY()).$plus$plus(this.timeQuantiles$1(metrics.schedulerDelay()), Seq$.MODULE$.canBuildFrom());
               Seq$ var10000 = Seq$.MODULE$;
               Predef$ var10001 = Predef$.MODULE$;
               Seq[] var10002 = new Seq[12];
               Null var10009 = .MODULE$;
               TopScope var10010 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(serviceQuantiles);
               var10002[0] = new Elem((String)null, "tr", var10009, var10010, false, $buf);
               MetaData $md = .MODULE$;
               MetaData var43 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.SCHEDULER_DELAY(), $md);
               var10010 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(schedulerDelayQuantiles);
               var10002[1] = new Elem((String)null, "tr", var43, var10010, false, $buf);
               NodeBuffer $buf = new NodeBuffer();
               MetaData $md = .MODULE$;
               MetaData var44 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.TASK_DESERIALIZATION_TIME(), $md);
               TopScope var10011 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n          "));
               $buf.$amp$plus(deserializationQuantiles);
               $buf.$amp$plus(new Text("\n        "));
               $buf.$amp$plus(new Elem((String)null, "tr", var44, var10011, false, $buf));
               Null var61 = .MODULE$;
               var10011 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(gcQuantiles);
               $buf.$amp$plus(new Elem((String)null, "tr", var61, var10011, false, $buf));
               var10002[2] = $buf;
               MetaData $md = .MODULE$;
               MetaData var45 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.RESULT_SERIALIZATION_TIME(), $md);
               TopScope var62 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n          "));
               $buf.$amp$plus(serializationQuantiles);
               $buf.$amp$plus(new Text("\n        "));
               var10002[3] = new Elem((String)null, "tr", var45, var62, false, $buf);
               MetaData $md = .MODULE$;
               MetaData var46 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.GETTING_RESULT_TIME(), $md);
               var62 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(gettingResultQuantiles);
               var10002[4] = new Elem((String)null, "tr", var46, var62, false, $buf);
               MetaData $md = .MODULE$;
               MetaData var47 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.PEAK_EXECUTION_MEMORY(), $md);
               var62 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n          "));
               $buf.$amp$plus(peakExecutionMemoryQuantiles);
               $buf.$amp$plus(new Text("\n        "));
               var10002[5] = new Elem((String)null, "tr", var47, var62, false, $buf);
               Object var10005;
               if (ApiHelper$.MODULE$.hasInput(this.stageData$1)) {
                  var10009 = .MODULE$;
                  var62 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(this.inputQuantiles$1(metrics));
                  var10005 = new Elem((String)null, "tr", var10009, var62, false, $buf);
               } else {
                  var10005 = Nil$.MODULE$;
               }

               var10002[6] = (Seq)var10005;
               if (ApiHelper$.MODULE$.hasOutput(this.stageData$1)) {
                  var10009 = .MODULE$;
                  var62 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(this.outputQuantiles$1(metrics));
                  var10005 = new Elem((String)null, "tr", var10009, var62, false, $buf);
               } else {
                  var10005 = Nil$.MODULE$;
               }

               var10002[7] = (Seq)var10005;
               if (ApiHelper$.MODULE$.hasShuffleRead(this.stageData$1)) {
                  NodeBuffer $buf = new NodeBuffer();
                  MetaData $md = .MODULE$;
                  MetaData var48 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_BLOCKED_TIME(), $md);
                  var10011 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus(this.shuffleReadBlockedQuantiles$1(metrics));
                  $buf.$amp$plus(new Text("\n          "));
                  $buf.$amp$plus(new Elem((String)null, "tr", var48, var10011, false, $buf));
                  Null var67 = .MODULE$;
                  var10011 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(this.shuffleReadTotalQuantiles$1(metrics));
                  $buf.$amp$plus(new Elem((String)null, "tr", var67, var10011, false, $buf));
                  MetaData $md = .MODULE$;
                  MetaData var49 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_REMOTE_SIZE(), $md);
                  var10011 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus(this.shuffleReadRemoteQuantiles$1(metrics));
                  $buf.$amp$plus(new Text("\n          "));
                  $buf.$amp$plus(new Elem((String)null, "tr", var49, var10011, false, $buf));
                  var10005 = $buf;
               } else {
                  var10005 = Nil$.MODULE$;
               }

               var10002[8] = (Seq)var10005;
               if (ApiHelper$.MODULE$.hasShuffleWrite(this.stageData$1)) {
                  var10009 = .MODULE$;
                  var62 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(this.shuffleWriteQuantiles$1(metrics));
                  var10005 = new Elem((String)null, "tr", var10009, var62, false, $buf);
               } else {
                  var10005 = Nil$.MODULE$;
               }

               var10002[9] = (Seq)var10005;
               if (ApiHelper$.MODULE$.hasBytesSpilled(this.stageData$1)) {
                  var10009 = .MODULE$;
                  var62 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(this.memoryBytesSpilledQuantiles$1(metrics));
                  var10005 = new Elem((String)null, "tr", var10009, var62, false, $buf);
               } else {
                  var10005 = Nil$.MODULE$;
               }

               var10002[10] = (Seq)var10005;
               if (ApiHelper$.MODULE$.hasBytesSpilled(this.stageData$1)) {
                  var10009 = .MODULE$;
                  var62 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(this.diskBytesSpilledQuantiles$1(metrics));
                  var10005 = new Elem((String)null, "tr", var10009, var62, false, $buf);
               } else {
                  var10005 = Nil$.MODULE$;
               }

               var10002[11] = (Seq)var10005;
               Seq listings = (Seq)var10000.apply(var10001.wrapRefArray(var10002));
               Seq quantileHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Metric", "Min", "25th percentile", "Median", "75th percentile", "Max"})));
               Function1 x$27 = new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Seq<Node> apply(Seq<Node> x) {
                     return (Seq)Predef$.MODULE$.identity(x);
                  }
               };
               boolean x$29 = true;
               Some x$30 = new Some("task-summary-table");
               boolean x$31 = false;
               Seq x$32 = UIUtils$.MODULE$.listingTable$default$6();
               boolean x$33 = UIUtils$.MODULE$.listingTable$default$8();
               return UIUtils$.MODULE$.listingTable(quantileHeaders, x$27, listings, x$29, x$30, x$32, x$31, x$33);
            }

            private final Seq timeQuantiles$1(IndexedSeq data) {
               return (Seq)data.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Elem apply(double millis) {
                     Null var10004 = .MODULE$;
                     TopScope var10005 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(UIUtils$.MODULE$.formatDuration((long)millis));
                     return new Elem((String)null, "td", var10004, var10005, false, $buf);
                  }
               }, IndexedSeq$.MODULE$.canBuildFrom());
            }

            private final Seq sizeQuantiles$1(IndexedSeq data) {
               return (Seq)data.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Elem apply(double size) {
                     Null var10004 = .MODULE$;
                     TopScope var10005 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(Utils$.MODULE$.bytesToString((long)size));
                     return new Elem((String)null, "td", var10004, var10005, false, $buf);
                  }
               }, IndexedSeq$.MODULE$.canBuildFrom());
            }

            private final Seq sizeQuantilesWithRecords$1(IndexedSeq data, IndexedSeq records) {
               return (Seq)((TraversableLike)data.zip(records, IndexedSeq$.MODULE$.canBuildFrom())).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Elem apply(Tuple2<Object, Object> x0$3) {
                     if (x0$3 != null) {
                        double d = x0$3._1$mcD$sp();
                        double r = x0$3._2$mcD$sp();
                        Null var10004 = .MODULE$;
                        TopScope var10005 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString((long)d), BoxesRunTime.boxToLong((long)r)})));
                        Elem var7 = new Elem((String)null, "td", var10004, var10005, false, $buf);
                        return var7;
                     } else {
                        throw new MatchError(x0$3);
                     }
                  }
               }, IndexedSeq$.MODULE$.canBuildFrom());
            }

            private final Seq titleCell$1(String title, String tooltip) {
               Null var10004 = .MODULE$;
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n          "));
               MetaData $md = .MODULE$;
               MetaData var6 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
               var6 = new UnprefixedAttribute("title", tooltip, var6);
               var6 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var6);
               TopScope var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n            "));
               $buf.$amp$plus(title);
               $buf.$amp$plus(new Text("\n          "));
               $buf.$amp$plus(new Elem((String)null, "span", var6, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               return new Elem((String)null, "td", var10004, var10005, false, $buf);
            }

            private final Seq simpleTitleCell$1(String title) {
               Null var10004 = .MODULE$;
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(title);
               return new Elem((String)null, "td", var10004, var10005, false, $buf);
            }

            private final Seq inputQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.simpleTitleCell$1("Input Size / Records").$plus$plus(this.sizeQuantilesWithRecords$1(metrics$1.inputMetrics().bytesRead(), metrics$1.inputMetrics().recordsRead()), Seq$.MODULE$.canBuildFrom());
            }

            private final Seq outputQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.simpleTitleCell$1("Output Size / Records").$plus$plus(this.sizeQuantilesWithRecords$1(metrics$1.outputMetrics().bytesWritten(), metrics$1.outputMetrics().recordsWritten()), Seq$.MODULE$.canBuildFrom());
            }

            private final Seq shuffleReadBlockedQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.titleCell$1("Shuffle Read Blocked Time", ToolTips$.MODULE$.SHUFFLE_READ_BLOCKED_TIME()).$plus$plus(this.timeQuantiles$1(metrics$1.shuffleReadMetrics().fetchWaitTime()), Seq$.MODULE$.canBuildFrom());
            }

            private final Seq shuffleReadTotalQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.titleCell$1("Shuffle Read Size / Records", ToolTips$.MODULE$.SHUFFLE_READ()).$plus$plus(this.sizeQuantilesWithRecords$1(metrics$1.shuffleReadMetrics().readBytes(), metrics$1.shuffleReadMetrics().readRecords()), Seq$.MODULE$.canBuildFrom());
            }

            private final Seq shuffleReadRemoteQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.titleCell$1("Shuffle Remote Reads", ToolTips$.MODULE$.SHUFFLE_READ_REMOTE_SIZE()).$plus$plus(this.sizeQuantiles$1(metrics$1.shuffleReadMetrics().remoteBytesRead()), Seq$.MODULE$.canBuildFrom());
            }

            private final Seq shuffleWriteQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.simpleTitleCell$1("Shuffle Write Size / Records").$plus$plus(this.sizeQuantilesWithRecords$1(metrics$1.shuffleWriteMetrics().writeBytes(), metrics$1.shuffleWriteMetrics().writeRecords()), Seq$.MODULE$.canBuildFrom());
            }

            private final Seq memoryBytesSpilledQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.simpleTitleCell$1("Shuffle spill (memory)").$plus$plus(this.sizeQuantiles$1(metrics$1.memoryBytesSpilled()), Seq$.MODULE$.canBuildFrom());
            }

            private final Seq diskBytesSpilledQuantiles$1(TaskMetricDistributions metrics$1) {
               return (Seq)this.simpleTitleCell$1("Shuffle spill (disk)").$plus$plus(this.sizeQuantiles$1(metrics$1.diskBytesSpilled()), Seq$.MODULE$.canBuildFrom());
            }

            public {
               this.stageData$1 = stageData$1;
            }
         });
         ExecutorTable executorTable = new ExecutorTable(stageData, this.org$apache$spark$ui$jobs$StagePage$$parent.store());
         Seq var219;
         if (ApiHelper$.MODULE$.hasAccumulators(stageData)) {
            var10004 = .MODULE$;
            var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Accumulators"));
            var219 = (Seq)(new Elem((String)null, "h4", var10004, var10005, false, $buf)).$plus$plus(accumulableTable, scala.xml.NodeSeq..MODULE$.canBuildFrom());
         } else {
            var219 = (Seq)Seq$.MODULE$.apply(Nil$.MODULE$);
         }

         Seq maybeAccumulableTable = var219;
         NodeBuffer $buf = new NodeBuffer();
         MetaData $md = .MODULE$;
         MetaData var207 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-metrics','aggregated-metrics')"), $md);
         var207 = new UnprefixedAttribute("class", new Text("collapse-aggregated-metrics collapse-table"), var207);
         TopScope var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         Null var243 = .MODULE$;
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var209 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var209, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n          "));
         Null var270 = .MODULE$;
         TopScope var273 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Aggregated Metrics by Executor"));
         $buf.$amp$plus(new Elem((String)null, "a", var270, var273, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "h4", var243, var10014, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         $buf.$amp$plus(new Elem((String)null, "span", var207, var10006, false, $buf));
         MetaData $md = .MODULE$;
         MetaData var210 = new UnprefixedAttribute("class", new Text("aggregated-metrics collapsible-table"), $md);
         var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(executorTable.toNodeSeq());
         $buf.$amp$plus(new Text("\n      "));
         $buf.$amp$plus(new Elem((String)null, "div", var210, var10006, false, $buf));
         TraversableLike var220 = (TraversableLike)((TraversableLike)((TraversableLike)summary.$plus$plus(dagViz, scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(showAdditionalMetrics, scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(this.makeTimeline((Seq)Option$.MODULE$.apply(taskTable).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Seq<TaskData> apply(TaskPagedTable x$6) {
               return x$6.dataSource().tasks();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Nil$ apply() {
               return Nil$.MODULE$;
            }
         }), currentTime), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         Null var231 = .MODULE$;
         var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Summary Metrics for "));
         MetaData $md = .MODULE$;
         MetaData var211 = new UnprefixedAttribute("href", new Text("#tasks-section"), $md);
         var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(BoxesRunTime.boxToInteger(numCompleted));
         $buf.$amp$plus(new Text(" Completed Tasks"));
         $buf.$amp$plus(new Elem((String)null, "a", var211, var10014, false, $buf));
         var220 = (TraversableLike)var220.$plus$plus(new Elem((String)null, "h4", var231, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         var231 = .MODULE$;
         var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(summaryTable.getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "No tasks have reported metrics yet.";
            }
         }));
         var220 = (TraversableLike)((TraversableLike)((TraversableLike)var220.$plus$plus(new Elem((String)null, "div", var231, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus($buf, scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(maybeAccumulableTable, scala.xml.NodeSeq..MODULE$.canBuildFrom());
         MetaData $md = .MODULE$;
         MetaData var212 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-tasks','aggregated-tasks')"), $md);
         var212 = new UnprefixedAttribute("class", new Text("collapse-aggregated-tasks collapse-table"), var212);
         var212 = new UnprefixedAttribute("id", new Text("tasks-section"), var212);
         var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         var243 = .MODULE$;
         var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var215 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var215, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n          "));
         var270 = .MODULE$;
         var273 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Tasks ("));
         $buf.$amp$plus(totalTasksNumStr);
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var270, var273, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "h4", var243, var10014, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         var220 = (TraversableLike)var220.$plus$plus(new Elem((String)null, "span", var212, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         MetaData $md = .MODULE$;
         MetaData var216 = new UnprefixedAttribute("class", new Text("aggregated-tasks collapsible-table"), $md);
         var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(taskTableHTML.$plus$plus(jsForScrollingDownToTaskTable, Seq$.MODULE$.canBuildFrom()));
         $buf.$amp$plus(new Text("\n      "));
         NodeSeq content = (NodeSeq)var220.$plus$plus(new Elem((String)null, "div", var216, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         Function0 x$35 = new Serializable(this, content) {
            public static final long serialVersionUID = 0L;
            private final NodeSeq content$1;

            public final NodeSeq apply() {
               return this.content$1;
            }

            public {
               this.content$1 = content$1;
            }
         };
         StagesTab x$36 = this.org$apache$spark$ui$jobs$StagePage$$parent;
         boolean x$37 = true;
         Option x$38 = UIUtils$.MODULE$.headerSparkPage$default$4();
         Option x$39 = UIUtils$.MODULE$.headerSparkPage$default$5();
         boolean x$40 = UIUtils$.MODULE$.headerSparkPage$default$7();
         var10000 = UIUtils$.MODULE$.headerSparkPage(stageHeader, x$35, x$36, x$38, x$39, x$37, x$40);
      } catch (NonLocalReturnControl var157) {
         if (var157.key() != var2) {
            throw var157;
         }

         var10000 = (Seq)var157.value();
      }

      return var10000;
   }

   public Seq<Node> makeTimeline(Seq<TaskData> tasks, long currentTime) {
      HashSet executorsSet = new HashSet();
      LongRef minLaunchTime = LongRef.create(Long.MAX_VALUE);
      LongRef maxFinishTime = LongRef.create(Long.MIN_VALUE);
      String executorsArrayStr = ((TraversableOnce)((TraversableLike)((IterableLike)tasks.sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskData x$7) {
            return -x$7.launchTime().getTime();
         }
      }, Ordering.Long$.MODULE$)).take(this.MAX_TIMELINE_TASKS())).map(new Serializable(this, currentTime, executorsSet, minLaunchTime, maxFinishTime) {
         public static final long serialVersionUID = 0L;
         public final long currentTime$2;
         private final HashSet executorsSet$1;
         private final LongRef minLaunchTime$1;
         private final LongRef maxFinishTime$1;

         public final String apply(TaskData taskInfo) {
            String executorId = taskInfo.executorId();
            String host = taskInfo.host();
            this.executorsSet$1.$plus$eq(new Tuple2(executorId, host));
            long launchTime = taskInfo.launchTime().getTime();
            long finishTime = BoxesRunTime.unboxToLong(taskInfo.duration().map(new Serializable(this, taskInfo) {
               public static final long serialVersionUID = 0L;
               private final TaskData taskInfo$1;

               public final long apply(long x$8) {
                  return this.apply$mcJJ$sp(x$8);
               }

               public long apply$mcJJ$sp(long x$8) {
                  return this.taskInfo$1.launchTime().getTime() + x$8;
               }

               public {
                  this.taskInfo$1 = taskInfo$1;
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return this.$outer.currentTime$2;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }));
            long totalExecutionTime = finishTime - launchTime;
            this.minLaunchTime$1.elem = RichLong$.MODULE$.min$extension(Predef$.MODULE$.longWrapper(launchTime), this.minLaunchTime$1.elem);
            this.maxFinishTime$1.elem = RichLong$.MODULE$.max$extension(Predef$.MODULE$.longWrapper(finishTime), this.maxFinishTime$1.elem);
            Option metricsOpt = taskInfo.taskMetrics();
            long shuffleReadTime = BoxesRunTime.unboxToLong(metricsOpt.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskMetrics x$9) {
                  return x$9.shuffleReadMetrics().fetchWaitTime();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            }));
            double shuffleReadTimeProportion = this.toProportion$1(shuffleReadTime, totalExecutionTime);
            long shuffleWriteTime = (long)((double)BoxesRunTime.unboxToLong(metricsOpt.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskMetrics x$10) {
                  return x$10.shuffleWriteMetrics().writeTime();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })) / (double)1000000.0F);
            double shuffleWriteTimeProportion = this.toProportion$1(shuffleWriteTime, totalExecutionTime);
            long serializationTime = BoxesRunTime.unboxToLong(metricsOpt.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskMetrics x$11) {
                  return x$11.resultSerializationTime();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            }));
            double serializationTimeProportion = this.toProportion$1(serializationTime, totalExecutionTime);
            long deserializationTime = BoxesRunTime.unboxToLong(metricsOpt.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskMetrics x$12) {
                  return x$12.executorDeserializeTime();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            }));
            double deserializationTimeProportion = this.toProportion$1(deserializationTime, totalExecutionTime);
            long gettingResultTime = AppStatusUtils$.MODULE$.gettingResultTime(taskInfo);
            double gettingResultTimeProportion = this.toProportion$1(gettingResultTime, totalExecutionTime);
            long schedulerDelay = AppStatusUtils$.MODULE$.schedulerDelay(taskInfo);
            double schedulerDelayProportion = this.toProportion$1(schedulerDelay, totalExecutionTime);
            long executorOverhead = serializationTime + deserializationTime;
            long executorRunTime = taskInfo.duration().isDefined() ? totalExecutionTime - executorOverhead - gettingResultTime : BoxesRunTime.unboxToLong(metricsOpt.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskMetrics x$13) {
                  return x$13.executorRunTime();
               }
            }).getOrElse(new Serializable(this, totalExecutionTime, gettingResultTime, executorOverhead) {
               public static final long serialVersionUID = 0L;
               private final long totalExecutionTime$1;
               private final long gettingResultTime$1;
               private final long executorOverhead$1;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return this.totalExecutionTime$1 - this.executorOverhead$1 - this.gettingResultTime$1;
               }

               public {
                  this.totalExecutionTime$1 = totalExecutionTime$1;
                  this.gettingResultTime$1 = gettingResultTime$1;
                  this.executorOverhead$1 = executorOverhead$1;
               }
            }));
            long executorComputingTime = executorRunTime - shuffleReadTime - shuffleWriteTime;
            double executorComputingTimeProportion = package$.MODULE$.max((double)100 - schedulerDelayProportion - shuffleReadTimeProportion - shuffleWriteTimeProportion - serializationTimeProportion - deserializationTimeProportion - gettingResultTimeProportion, (double)0.0F);
            int schedulerDelayProportionPos = 0;
            double deserializationTimeProportionPos = (double)schedulerDelayProportionPos + schedulerDelayProportion;
            double shuffleReadTimeProportionPos = deserializationTimeProportionPos + deserializationTimeProportion;
            double executorRuntimeProportionPos = shuffleReadTimeProportionPos + shuffleReadTimeProportion;
            double shuffleWriteTimeProportionPos = executorRuntimeProportionPos + executorComputingTimeProportion;
            double serializationTimeProportionPos = shuffleWriteTimeProportionPos + shuffleWriteTimeProportion;
            double gettingResultTimeProportionPos = serializationTimeProportionPos + serializationTimeProportion;
            int index = taskInfo.index();
            int attempt = taskInfo.attempt();
            String svgTag = totalExecutionTime == 0L ? "<svg class=\"task-assignment-timeline-duration-bar\"></svg>" : (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"<svg class=\"task-assignment-timeline-duration-bar\">\n                 |<rect class=\"scheduler-delay-proportion\"\n                   |x=\"", "%\" y=\"0px\" height=\"26px\"\n                   |width=\"", "%\"></rect>\n                 |<rect class=\"deserialization-time-proportion\"\n                   |x=\"", "%\" y=\"0px\" height=\"26px\"\n                   |width=\"", "%\"></rect>\n                 |<rect class=\"shuffle-read-time-proportion\"\n                   |x=\"", "%\" y=\"0px\" height=\"26px\"\n                   |width=\"", "%\"></rect>\n                 |<rect class=\"executor-runtime-proportion\"\n                   |x=\"", "%\" y=\"0px\" height=\"26px\"\n                   |width=\"", "%\"></rect>\n                 |<rect class=\"shuffle-write-time-proportion\"\n                   |x=\"", "%\" y=\"0px\" height=\"26px\"\n                   |width=\"", "%\"></rect>\n                 |<rect class=\"serialization-time-proportion\"\n                   |x=\"", "%\" y=\"0px\" height=\"26px\"\n                   |width=\"", "%\"></rect>\n                 |<rect class=\"getting-result-time-proportion\"\n                   |x=\"", "%\" y=\"0px\" height=\"26px\"\n                   |width=\"", "%\"></rect></svg>"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(schedulerDelayProportionPos), BoxesRunTime.boxToDouble(schedulerDelayProportion), BoxesRunTime.boxToDouble(deserializationTimeProportionPos), BoxesRunTime.boxToDouble(deserializationTimeProportion), BoxesRunTime.boxToDouble(shuffleReadTimeProportionPos), BoxesRunTime.boxToDouble(shuffleReadTimeProportion), BoxesRunTime.boxToDouble(executorRuntimeProportionPos), BoxesRunTime.boxToDouble(executorComputingTimeProportion), BoxesRunTime.boxToDouble(shuffleWriteTimeProportionPos), BoxesRunTime.boxToDouble(shuffleWriteTimeProportion), BoxesRunTime.boxToDouble(serializationTimeProportionPos), BoxesRunTime.boxToDouble(serializationTimeProportion), BoxesRunTime.boxToDouble(gettingResultTimeProportionPos), BoxesRunTime.boxToDouble(gettingResultTimeProportion)}))))).stripMargin();
            String timelineObject = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n             |{\n               |'className': 'task task-assignment-timeline-object',\n               |'group': '", "',\n               |'content': '<div class=\"task-assignment-timeline-content\"\n                 |data-toggle=\"tooltip\" data-placement=\"top\"\n                 |data-html=\"true\" data-container=\"body\"\n                 |data-title=\"", "<br>\n                 |Status: ", "<br>\n                 |Launch Time: ", "\n                 |", "\n                 |<br>Scheduler Delay: ", " ms\n                 |<br>Task Deserialization Time: ", "\n                 |<br>Shuffle Read Time: ", "\n                 |<br>Executor Computing Time: ", "\n                 |<br>Shuffle Write Time: ", "\n                 |<br>Result Serialization Time: ", "\n                 |<br>Getting Result Time: ", "\">\n                 |", "',\n               |'start': new Date(", "),\n               |'end': new Date(", ")\n             |}\n           |"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{executorId, (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task "})))).s(Nil$.MODULE$)).append((Object)BoxesRunTime.boxToInteger(index)).append((Object)" (attempt ").append((Object)BoxesRunTime.boxToInteger(attempt)).append((Object)")").toString(), taskInfo.status(), UIUtils$.MODULE$.formatDate(new Date(launchTime)), taskInfo.duration().isDefined() ? "" : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"<br>Finish Time: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{UIUtils$.MODULE$.formatDate(new Date(finishTime))})), BoxesRunTime.boxToLong(schedulerDelay), UIUtils$.MODULE$.formatDuration(deserializationTime), UIUtils$.MODULE$.formatDuration(shuffleReadTime), UIUtils$.MODULE$.formatDuration(executorComputingTime), UIUtils$.MODULE$.formatDuration(shuffleWriteTime), UIUtils$.MODULE$.formatDuration(serializationTime), UIUtils$.MODULE$.formatDuration(gettingResultTime), svgTag, BoxesRunTime.boxToLong(launchTime), BoxesRunTime.boxToLong(finishTime)}))))).stripMargin().replaceAll("[\\r\\n]+", " ");
            return timelineObject;
         }

         private final double toProportion$1(long time, long totalExecutionTime$1) {
            return (double)time / (double)totalExecutionTime$1 * (double)100;
         }

         public {
            this.currentTime$2 = currentTime$2;
            this.executorsSet$1 = executorsSet$1;
            this.minLaunchTime$1 = minLaunchTime$1;
            this.maxFinishTime$1 = maxFinishTime$1;
         }
      }, Seq$.MODULE$.canBuildFrom())).mkString("[", ",", "]");
      String groupArrayStr = ((TraversableOnce)executorsSet.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, String> x0$4) {
            if (x0$4 != null) {
               String executorId = (String)x0$4._1();
               String host = (String)x0$4._2();
               String var5 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n            {\n              'id': '", "',\n              'content': '", " / ", "',\n            }\n          "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{executorId, executorId, host}));
               return var5;
            } else {
               throw new MatchError(x0$4);
            }
         }
      }, HashSet$.MODULE$.canBuildFrom())).mkString("[", ",", "]");
      MetaData $md = .MODULE$;
      MetaData var24 = new UnprefixedAttribute("class", new Text("expand-task-assignment-timeline"), $md);
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var25 = new UnprefixedAttribute("class", new Text("expand-task-assignment-timeline-arrow arrow-closed"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var25, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Event Timeline"));
      $buf.$amp$plus(new Elem((String)null, "a", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      Elem var10000 = new Elem((String)null, "span", var24, var10005, false, $buf);
      MetaData $md = .MODULE$;
      MetaData var26 = new UnprefixedAttribute("class", new Text("collapsed"), $md);
      var26 = new UnprefixedAttribute("id", new Text("task-assignment-timeline"), var26);
      Elem var10001 = new Elem;
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Object var10009;
      if (this.MAX_TIMELINE_TASKS() < tasks.size()) {
         Null var34 = .MODULE$;
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            This page has more than the maximum number of tasks that can be shown in the\n            visualization! Only the most recent "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(this.MAX_TIMELINE_TASKS()));
         $buf.$amp$plus(new Text(" tasks\n            (of "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(tasks.size()));
         $buf.$amp$plus(new Text(" total) are shown.\n          "));
         var10009 = new Elem((String)null, "strong", var34, var10014, false, $buf);
      } else {
         var10009 = Seq$.MODULE$.empty();
      }

      $buf.$amp$plus(var10009);
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var28 = new UnprefixedAttribute("class", new Text("control-panel"), $md);
      TopScope var35 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var29 = new UnprefixedAttribute("id", new Text("task-assignment-timeline-zoom-lock"), $md);
      TopScope var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var30 = new UnprefixedAttribute("type", new Text("checkbox"), $md);
      $buf.$amp$plus(new Elem((String)null, "input", var30, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n          "));
      Null var10029 = .MODULE$;
      TopScope var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Enable zooming"));
      $buf.$amp$plus(new Elem((String)null, "span", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var29, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "div", var28, var35, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(this.TIMELINE_LEGEND());
      $buf.$amp$plus(new Text("\n    "));
      var10001.<init>((String)null, "div", var26, var10006, false, $buf);
      TraversableLike var32 = (TraversableLike)var10000.$plus$plus(var10001, scala.xml.NodeSeq..MODULE$.canBuildFrom());
      MetaData $md = .MODULE$;
      MetaData var31 = new UnprefixedAttribute("type", new Text("text/javascript"), $md);
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"drawTaskAssignmentTimeline("})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", ", ", ", ", ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{groupArrayStr, executorsArrayStr, BoxesRunTime.boxToLong(minLaunchTime.elem), BoxesRunTime.boxToLong(maxFinishTime.elem)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(UIUtils$.MODULE$.getTimeZoneOffset())}))).toString()));
      $buf.$amp$plus(new Text("\n    "));
      return (Seq)var32.$plus$plus(new Elem((String)null, "script", var31, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
   }

   public final Seq org$apache$spark$ui$jobs$StagePage$$accumulableRow$1(AccumulableInfo acc) {
      Object var10000;
      if (acc.name() != null && acc.value() != null) {
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var10012 = .MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(acc.name());
         $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
         var10012 = .MODULE$;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(acc.value());
         $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
         var10000 = new Elem((String)null, "tr", var10004, var10005, false, $buf);
      } else {
         var10000 = Nil$.MODULE$;
      }

      return (Seq)var10000;
   }

   private final Tuple2 liftedTree1$1(String taskSortColumn$1, boolean taskSortDesc$1, int taskPageSize$1, int stageId$1, int stageAttemptId$1, StageData stageData$1, int page$1, long currentTime$1) {
      Tuple2 var10000;
      try {
         TaskPagedTable _taskTable = new TaskPagedTable(stageData$1, (new StringBuilder()).append((Object)UIUtils$.MODULE$.prependBaseUri(this.org$apache$spark$ui$jobs$StagePage$$parent.basePath(), UIUtils$.MODULE$.prependBaseUri$default$2())).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/stages/stage?id=", "&attempt=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(stageId$1), BoxesRunTime.boxToInteger(stageAttemptId$1)}))).toString(), currentTime$1, taskPageSize$1, taskSortColumn$1, taskSortDesc$1, this.org$apache$spark$ui$jobs$StagePage$$parent.store());
         var10000 = new Tuple2(_taskTable, _taskTable.table(page$1));
      } catch (Throwable var21) {
         boolean var12;
         if (var21 instanceof IllegalArgumentException) {
            var12 = true;
         } else if (var21 instanceof IndexOutOfBoundsException) {
            var12 = true;
         } else {
            var12 = false;
         }

         if (!var12) {
            throw var21;
         }

         MetaData $md = .MODULE$;
         MetaData var22 = new UnprefixedAttribute("class", new Text("alert alert-error"), $md);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         Null var10012 = .MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Error while rendering stage table:"));
         $buf.$amp$plus(new Elem((String)null, "p", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10012 = .MODULE$;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(Utils$.MODULE$.exceptionString(var21));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "pre", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         Elem errorMessage = new Elem((String)null, "div", var22, var10005, false, $buf);
         Tuple2 var13 = new Tuple2((Object)null, errorMessage);
         var10000 = var13;
         return var10000;
      }

      return var10000;
   }

   public StagePage(StagesTab parent, AppStatusStore store) {
      super("stage");
      this.org$apache$spark$ui$jobs$StagePage$$parent = parent;
      this.store = store;
      MetaData $md = .MODULE$;
      MetaData var7 = new UnprefixedAttribute("class", new Text("legend-area"), $md);
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10013 = .MODULE$;
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      List legendPairs = List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("scheduler-delay-proportion", "Scheduler Delay"), new Tuple2("deserialization-time-proportion", "Task Deserialization Time"), new Tuple2("shuffle-read-time-proportion", "Shuffle Read Time"), new Tuple2("executor-runtime-proportion", "Executor Computing Time"), new Tuple2("shuffle-write-time-proportion", "Shuffle Write Time"), new Tuple2("serialization-time-proportion", "Result Serialization Time"), new Tuple2("getting-result-time-proportion", "Getting Result Time")}));
      $buf.$amp$plus(((List)legendPairs.zipWithIndex(List$.MODULE$.canBuildFrom())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final NodeBuffer apply(Tuple2<Tuple2<String, String>, Object> x0$1) {
            if (x0$1 != null) {
               Tuple2 var3 = (Tuple2)x0$1._1();
               int index = x0$1._2$mcI$sp();
               if (var3 != null) {
                  String classAttr = (String)var3._1();
                  String name = (String)var3._2();
                  NodeBuffer $buf = new NodeBuffer();
                  MetaData $md = .MODULE$;
                  MetaData var12 = new UnprefixedAttribute("class", classAttr, $md);
                  var12 = new UnprefixedAttribute("height", new Text("10px"), var12);
                  var12 = new UnprefixedAttribute("width", new Text("10px"), var12);
                  var12 = new UnprefixedAttribute("y", (new StringBuilder()).append(10 + index % 3 * 15).append((Object)"px").toString(), var12);
                  var12 = new UnprefixedAttribute("x", (new StringBuilder()).append(5 + index / 3 * 210).append((Object)"px").toString(), var12);
                  $buf.$amp$plus(new Elem((String)null, "rect", var12, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
                  MetaData $md = .MODULE$;
                  MetaData var17 = new UnprefixedAttribute("y", (new StringBuilder()).append(20 + index % 3 * 15).append((Object)"px").toString(), $md);
                  var17 = new UnprefixedAttribute("x", (new StringBuilder()).append(25 + index / 3 * 210).append((Object)"px").toString(), var17);
                  TopScope var10006 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(name);
                  $buf.$amp$plus(new Elem((String)null, "text", var17, var10006, false, $buf));
                  return $buf;
               }
            }

            throw new MatchError(x0$1);
         }
      }, List$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "svg", var10013, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      this.TIMELINE_LEGEND = new Elem((String)null, "div", var7, var10006, false, $buf);
      this.MAX_TIMELINE_TASKS = parent.conf().getInt("spark.ui.timeline.tasks.maximum", 1000);
   }
}
