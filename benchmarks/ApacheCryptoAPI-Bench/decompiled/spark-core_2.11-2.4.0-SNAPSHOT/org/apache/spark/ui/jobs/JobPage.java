package org.apache.spark.ui.jobs;

import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.spark.JobExecutionStatus;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ExecutorSummary;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
import org.apache.spark.ui.ToolTips$;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.ui.scope.RDDOperationGraph;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.GenTraversableOnce;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Buffer$;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.ListBuffer$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.NodeSeq;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Utility.;

@ScalaSignature(
   bytes = "\u0006\u0001u4Q!\u0001\u0002\u0001\t1\u0011qAS8c!\u0006<WM\u0003\u0002\u0004\t\u0005!!n\u001c2t\u0015\t)a!\u0001\u0002vS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011abD\u0007\u0002\t%\u0011\u0001\u0003\u0002\u0002\n/\u0016\u0014W+\u0013)bO\u0016D\u0001B\u0005\u0001\u0003\u0002\u0003\u0006I\u0001F\u0001\u0007a\u0006\u0014XM\u001c;\u0004\u0001A\u0011QCF\u0007\u0002\u0005%\u0011qC\u0001\u0002\b\u0015>\u00147\u000fV1c\u0011!I\u0002A!A!\u0002\u0013Q\u0012!B:u_J,\u0007CA\u000e\u001f\u001b\u0005a\"BA\u000f\u0007\u0003\u0019\u0019H/\u0019;vg&\u0011q\u0004\b\u0002\u000f\u0003B\u00048\u000b^1ukN\u001cFo\u001c:f\u0011\u0015\t\u0003\u0001\"\u0001#\u0003\u0019a\u0014N\\5u}Q\u00191\u0005J\u0013\u0011\u0005U\u0001\u0001\"\u0002\n!\u0001\u0004!\u0002\"B\r!\u0001\u0004Q\u0002bB\u0014\u0001\u0005\u0004%I\u0001K\u0001\u000e'R\u000bu)R*`\u0019\u0016;UI\u0014#\u0016\u0003%\u0002\"A\u000b\u0019\u000f\u0005-rS\"\u0001\u0017\u000b\u00035\nQa]2bY\u0006L!a\f\u0017\u0002\rA\u0013X\rZ3g\u0013\t\t$G\u0001\u0004TiJLgn\u001a\u0006\u0003_1Ba\u0001\u000e\u0001!\u0002\u0013I\u0013AD*U\u0003\u001e+5k\u0018'F\u000f\u0016sE\t\t\u0005\bm\u0001\u0011\r\u0011\"\u0003)\u0003A)\u0005,R\"V)>\u00136k\u0018'F\u000f\u0016sE\t\u0003\u00049\u0001\u0001\u0006I!K\u0001\u0012\u000bb+5)\u0016+P%N{F*R$F\u001d\u0012\u0003\u0003\"\u0002\u001e\u0001\t\u0013Y\u0014AD7bW\u0016\u001cF/Y4f\u000bZ,g\u000e\u001e\u000b\u0003y!\u00032!P#*\u001d\tq4I\u0004\u0002@\u00056\t\u0001I\u0003\u0002B'\u00051AH]8pizJ\u0011!L\u0005\u0003\t2\nq\u0001]1dW\u0006<W-\u0003\u0002G\u000f\n\u00191+Z9\u000b\u0005\u0011c\u0003\"B%:\u0001\u0004Q\u0015AC:uC\u001e,\u0017J\u001c4pgB\u0019Q(R&\u0011\u00051\u000bV\"A'\u000b\u00059{\u0015A\u0001<2\u0015\t\u0001F$A\u0002ba&L!AU'\u0003\u0013M#\u0018mZ3ECR\f\u0007\"\u0002+\u0001\t\u0003)\u0016!E7bW\u0016,\u00050Z2vi>\u0014XI^3oiR\u0011AH\u0016\u0005\u0006/N\u0003\r\u0001W\u0001\nKb,7-\u001e;peN\u00042!P#Z!\ta%,\u0003\u0002\\\u001b\nyQ\t_3dkR|'oU;n[\u0006\u0014\u0018\u0010C\u0003^\u0001\u0011%a,\u0001\u0007nC.,G+[7fY&tW\r\u0006\u0003`M\"L\u0007cA\u001fFAB\u0011\u0011\rZ\u0007\u0002E*\u00111\rL\u0001\u0004q6d\u0017BA3c\u0005\u0011qu\u000eZ3\t\u000b\u001dd\u0006\u0019\u0001&\u0002\rM$\u0018mZ3t\u0011\u00159F\f1\u0001Y\u0011\u0015QG\f1\u0001l\u00031\t\u0007\u000f]*uCJ$H+[7f!\tYC.\u0003\u0002nY\t!Aj\u001c8h\u0011\u0015y\u0007\u0001\"\u0001q\u0003\u0019\u0011XM\u001c3feR\u0011q,\u001d\u0005\u0006e:\u0004\ra]\u0001\be\u0016\fX/Z:u!\t!80D\u0001v\u0015\t1x/\u0001\u0003iiR\u0004(B\u0001=z\u0003\u001d\u0019XM\u001d<mKRT\u0011A_\u0001\u0006U\u00064\u0018\r_\u0005\u0003yV\u0014!\u0003\u0013;uaN+'O\u001e7fiJ+\u0017/^3ti\u0002"
)
public class JobPage extends WebUIPage {
   public final JobsTab org$apache$spark$ui$jobs$JobPage$$parent;
   public final AppStatusStore org$apache$spark$ui$jobs$JobPage$$store;
   private final String STAGES_LEGEND;
   private final String EXECUTORS_LEGEND;

   private String STAGES_LEGEND() {
      return this.STAGES_LEGEND;
   }

   private String EXECUTORS_LEGEND() {
      return this.EXECUTORS_LEGEND;
   }

   private Seq<String> makeStageEvent(Seq<StageData> stageInfos) {
      return (Seq)stageInfos.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(StageData stage) {
            int stageId;
            int attemptId;
            String jsEscapedName;
            StringOps var10000;
            Predef$ var10002;
            StringContext var10003;
            Predef$ var10004;
            Object[] var10005;
            String var10008;
            label17: {
               label16: {
                  stageId = stage.stageId();
                  attemptId = stage.attemptId();
                  String name = stage.name();
                  String status = stage.status().toString();
                  long submissionTime = ((Date)stage.submissionTime().get()).getTime();
                  long completionTime = BoxesRunTime.unboxToLong(stage.completionTime().map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply(Date x$3) {
                        return x$3.getTime();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return System.currentTimeMillis();
                     }
                  }));
                  String escapedName = .MODULE$.escape(name);
                  jsEscapedName = StringEscapeUtils.escapeEcmaScript(escapedName);
                  var10000 = new StringOps;
                  var10002 = Predef$.MODULE$;
                  var10003 = new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n         |{\n         |  'className': 'stage job-timeline-object ", "',\n         |  'group': 'stages',\n         |  'start': new Date(", "),\n         |  'end': new Date(", "),\n         |  'content': '<div class=\"job-timeline-content\" data-toggle=\"tooltip\"' +\n         |   'data-placement=\"top\" data-html=\"true\"' +\n         |   'data-title=\"", " (Stage ", ".", ")<br>' +\n         |   'Status: ", "<br>' +\n         |   'Submitted: ", "' +\n         |   '", "\">' +\n         |    '", " (Stage ", ".", ")</div>',\n         |}\n       "})));
                  var10004 = Predef$.MODULE$;
                  var10005 = new Object[]{status, BoxesRunTime.boxToLong(submissionTime), BoxesRunTime.boxToLong(completionTime), jsEscapedName, BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(attemptId), status.toUpperCase(Locale.ROOT), UIUtils$.MODULE$.formatDate(submissionTime), null, null, null, null};
                  String var12 = "running";
                  if (status == null) {
                     if (var12 == null) {
                        break label16;
                     }
                  } else if (status.equals(var12)) {
                     break label16;
                  }

                  var10008 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"<br>Completed: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{UIUtils$.MODULE$.formatDate(completionTime)}));
                  break label17;
               }

               var10008 = "";
            }

            var10005[8] = var10008;
            var10005[9] = jsEscapedName;
            var10005[10] = BoxesRunTime.boxToInteger(stageId);
            var10005[11] = BoxesRunTime.boxToInteger(attemptId);
            var10000.<init>(var10002.augmentString(var10003.s(var10004.genericWrapArray(var10005))));
            return var10000.stripMargin();
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public Seq<String> makeExecutorEvent(Seq<ExecutorSummary> executors) {
      ListBuffer events = (ListBuffer)ListBuffer$.MODULE$.apply(Nil$.MODULE$);
      executors.foreach(new Serializable(this, events) {
         public static final long serialVersionUID = 0L;
         public final ListBuffer events$1;

         public final void apply(ExecutorSummary e) {
            String addedEvent = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n           |{\n           |  'className': 'executor added',\n           |  'group': 'executors',\n           |  'start': new Date(", "),\n           |  'content': '<div class=\"executor-event-content\"' +\n           |    'data-toggle=\"tooltip\" data-placement=\"bottom\"' +\n           |    'data-title=\"Executor ", "<br>' +\n           |    'Added at ", "\"' +\n           |    'data-html=\"true\">Executor ", " added</div>'\n           |}\n         "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(e.addTime().getTime()), e.id(), UIUtils$.MODULE$.formatDate(e.addTime()), e.id()}))))).stripMargin();
            this.events$1.$plus$eq(addedEvent);
            e.removeTime().foreach(new Serializable(this, e) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final ExecutorSummary e$1;

               public final ListBuffer<String> apply(Date removeTime) {
                  String removedEvent = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n             |{\n             |  'className': 'executor removed',\n             |  'group': 'executors',\n             |  'start': new Date(", "),\n             |  'content': '<div class=\"executor-event-content\"' +\n             |    'data-toggle=\"tooltip\" data-placement=\"bottom\"' +\n             |    'data-title=\"Executor ", "<br>' +\n             |    'Removed at ", "' +\n             |    '", "\"' +\n             |    'data-html=\"true\">Executor ", " removed</div>'\n             |}\n           "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(removeTime.getTime()), this.e$1.id(), UIUtils$.MODULE$.formatDate(removeTime), this.e$1.removeReason().map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(String reason) {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"<br>Reason: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{reason.replace("\n", " ")}));
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "";
                     }
                  }), this.e$1.id()}))))).stripMargin();
                  return this.$outer.events$1.$plus$eq(removedEvent);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.e$1 = e$1;
                  }
               }
            });
         }

         public {
            this.events$1 = events$1;
         }
      });
      return events.toSeq();
   }

   private Seq<Node> makeTimeline(Seq<StageData> stages, Seq<ExecutorSummary> executors, long appStartTime) {
      Seq stageEventJsonAsStrSeq = this.makeStageEvent(stages);
      Seq executorsJsonAsStrSeq = this.makeExecutorEvent(executors);
      String groupJsonArrayAsStr = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n          |[\n          |  {\n          |    'id': 'executors',\n          |    'content': '<div>Executors</div>", "',\n          |  },\n          |  {\n          |    'id': 'stages',\n          |    'content': '<div>Stages</div>", "',\n          |  }\n          |]\n        "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.EXECUTORS_LEGEND(), this.STAGES_LEGEND()}))))).stripMargin();
      String eventArrayAsStr = ((TraversableOnce)stageEventJsonAsStrSeq.$plus$plus(executorsJsonAsStrSeq, Seq$.MODULE$.canBuildFrom())).mkString("[", ",", "]");
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var24 = new UnprefixedAttribute("class", new Text("expand-job-timeline"), $md);
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var25 = new UnprefixedAttribute("class", new Text("expand-job-timeline-arrow arrow-closed"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var25, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var26 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
      var26 = new UnprefixedAttribute("title", ToolTips$.MODULE$.STAGE_TIMELINE(), var26);
      var26 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var26);
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        Event Timeline\n      "));
      $buf.$amp$plus(new Elem((String)null, "a", var26, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      Elem var10000 = new Elem((String)null, "span", var24, var10005, false, $buf);
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var29 = new UnprefixedAttribute("class", new Text("collapsed"), $md);
      var29 = new UnprefixedAttribute("id", new Text("job-timeline"), var29);
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var31 = new UnprefixedAttribute("class", new Text("control-panel"), $md);
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var32 = new UnprefixedAttribute("id", new Text("job-timeline-zoom-lock"), $md);
      TopScope var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var33 = new UnprefixedAttribute("type", new Text("checkbox"), $md);
      $buf.$amp$plus(new Elem((String)null, "input", var33, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n          "));
      Null var10029 = scala.xml.Null..MODULE$;
      TopScope var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Enable zooming"));
      $buf.$amp$plus(new Elem((String)null, "span", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var32, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "div", var31, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      TraversableLike var35 = (TraversableLike)var10000.$plus$plus(new Elem((String)null, "div", var29, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var34 = new UnprefixedAttribute("type", new Text("text/javascript"), $md);
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"drawJobTimeline(", ", ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{groupJsonArrayAsStr, eventArrayAsStr}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", ", ");"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(appStartTime), BoxesRunTime.boxToInteger(UIUtils$.MODULE$.getTimeZoneOffset())}))).toString()));
      $buf.$amp$plus(new Text("\n    "));
      return (Seq)var35.$plus$plus(new Elem((String)null, "script", var34, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
   }

   public Seq<Node> render(HttpServletRequest request) {
      Object var2 = new Object();

      Seq var10000;
      try {
         int jobId;
         JobData jobData;
         label104: {
            label103: {
               String parameterId = UIUtils$.MODULE$.stripXSS(request.getParameter("id"));
               Predef$.MODULE$.require(parameterId != null && (new StringOps(Predef$.MODULE$.augmentString(parameterId))).nonEmpty(), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Missing id parameter";
                  }
               });
               jobId = (new StringOps(Predef$.MODULE$.augmentString(parameterId))).toInt();
               jobData = (JobData)this.org$apache$spark$ui$jobs$JobPage$$store.asOption(new Serializable(this, jobId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final JobPage $outer;
                  private final int jobId$1;

                  public final JobData apply() {
                     return this.$outer.org$apache$spark$ui$jobs$JobPage$$store.job(this.jobId$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.jobId$1 = jobId$1;
                     }
                  }
               }).getOrElse(new Serializable(this, jobId, var2) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final JobPage $outer;
                  private final int jobId$1;
                  private final Object nonLocalReturnKey1$1;

                  public final Nothing$ apply() {
                     MetaData $md = scala.xml.Null..MODULE$;
                     MetaData var5 = new UnprefixedAttribute("id", new Text("no-info"), $md);
                     TopScope var10005 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(new Text("\n          "));
                     Null var10012 = scala.xml.Null..MODULE$;
                     TopScope var10013 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(new Text("No information to display for job "));
                     $buf.$amp$plus(BoxesRunTime.boxToInteger(this.jobId$1));
                     $buf.$amp$plus(new Elem((String)null, "p", var10012, var10013, false, $buf));
                     $buf.$amp$plus(new Text("\n        "));
                     Elem content = new Elem((String)null, "div", var5, var10005, false, $buf);
                     throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, UIUtils$.MODULE$.headerSparkPage((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Details for Job ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.jobId$1)})), new Serializable(this, content) {
                        public static final long serialVersionUID = 0L;
                        private final Elem content$2;

                        public final Elem apply() {
                           return this.content$2;
                        }

                        public {
                           this.content$2 = content$2;
                        }
                     }, this.$outer.org$apache$spark$ui$jobs$JobPage$$parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7()));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.jobId$1 = jobId$1;
                        this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                     }
                  }
               });
               JobExecutionStatus var139 = jobData.status();
               JobExecutionStatus var8 = JobExecutionStatus.RUNNING;
               if (var139 == null) {
                  if (var8 == null) {
                     break label103;
                  }
               } else if (var139.equals(var8)) {
                  break label103;
               }

               var140 = true;
               break label104;
            }

            var140 = false;
         }

         boolean isComplete = var140;
         Seq stages = (Seq)jobData.stageIds().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final JobPage $outer;

            public final StageData apply(int stageId) {
               return (StageData)this.$outer.org$apache$spark$ui$jobs$JobPage$$store.asOption(new Serializable(this, stageId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int stageId$1;

                  public final StageData apply() {
                     return this.$outer.org$apache$spark$ui$jobs$JobPage$$anonfun$$$outer().org$apache$spark$ui$jobs$JobPage$$store.lastStageAttempt(this.stageId$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.stageId$1 = stageId$1;
                     }
                  }
               }).getOrElse(new Serializable(this, stageId) {
                  public static final long serialVersionUID = 0L;
                  private final int stageId$1;

                  public final StageData apply() {
                     return new StageData(StageStatus.PENDING, this.stageId$1, 0, 0, 0, 0, 0, 0, 0, 0L, 0L, None$.MODULE$, None$.MODULE$, None$.MODULE$, None$.MODULE$, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, "Unknown", None$.MODULE$, "Unknown", (String)null, Nil$.MODULE$, Nil$.MODULE$, None$.MODULE$, None$.MODULE$, (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$));
                  }

                  public {
                     this.stageId$1 = stageId$1;
                  }
               });
            }

            // $FF: synthetic method
            public JobPage org$apache$spark$ui$jobs$JobPage$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Seq$.MODULE$.canBuildFrom());
         Buffer activeStages = (Buffer)Buffer$.MODULE$.apply(Nil$.MODULE$);
         Buffer completedStages = (Buffer)Buffer$.MODULE$.apply(Nil$.MODULE$);
         Buffer pendingOrSkippedStages = (Buffer)Buffer$.MODULE$.apply(Nil$.MODULE$);
         Buffer failedStages = (Buffer)Buffer$.MODULE$.apply(Nil$.MODULE$);
         stages.foreach(new Serializable(this, activeStages, completedStages, pendingOrSkippedStages, failedStages) {
            public static final long serialVersionUID = 0L;
            private final Buffer activeStages$1;
            private final Buffer completedStages$1;
            private final Buffer pendingOrSkippedStages$1;
            private final Buffer failedStages$1;

            public final Buffer<StageData> apply(StageData stage) {
               Buffer var10000;
               if (stage.submissionTime().isEmpty()) {
                  var10000 = this.pendingOrSkippedStages$1.$plus$eq(stage);
               } else if (stage.completionTime().isDefined()) {
                  label28: {
                     StageStatus var3 = stage.status();
                     StageStatus var2 = StageStatus.FAILED;
                     if (var3 == null) {
                        if (var2 == null) {
                           break label28;
                        }
                     } else if (var3.equals(var2)) {
                        break label28;
                     }

                     var10000 = this.completedStages$1.$plus$eq(stage);
                     return var10000;
                  }

                  var10000 = this.failedStages$1.$plus$eq(stage);
               } else {
                  var10000 = this.activeStages$1.$plus$eq(stage);
               }

               return var10000;
            }

            public {
               this.activeStages$1 = activeStages$1;
               this.completedStages$1 = completedStages$1;
               this.pendingOrSkippedStages$1 = pendingOrSkippedStages$1;
               this.failedStages$1 = failedStages$1;
            }
         });
         String basePath = "jobs/job";
         String pendingOrSkippedTableId = isComplete ? "pending" : "skipped";
         StageTableBase activeStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$JobPage$$store, request, activeStages, "active", "activeStage", this.org$apache$spark$ui$jobs$JobPage$$parent.basePath(), basePath, this.org$apache$spark$ui$jobs$JobPage$$parent.isFairScheduler(), this.org$apache$spark$ui$jobs$JobPage$$parent.killEnabled(), false);
         StageTableBase pendingOrSkippedStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$JobPage$$store, request, pendingOrSkippedStages, pendingOrSkippedTableId, "pendingStage", this.org$apache$spark$ui$jobs$JobPage$$parent.basePath(), basePath, this.org$apache$spark$ui$jobs$JobPage$$parent.isFairScheduler(), false, false);
         StageTableBase completedStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$JobPage$$store, request, completedStages, "completed", "completedStage", this.org$apache$spark$ui$jobs$JobPage$$parent.basePath(), basePath, this.org$apache$spark$ui$jobs$JobPage$$parent.isFairScheduler(), false, false);
         StageTableBase failedStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$JobPage$$store, request, failedStages, "failed", "failedStage", this.org$apache$spark$ui$jobs$JobPage$$parent.basePath(), basePath, this.org$apache$spark$ui$jobs$JobPage$$parent.isFairScheduler(), false, true);
         boolean shouldShowActiveStages = activeStages.nonEmpty();
         boolean shouldShowPendingStages = !isComplete && pendingOrSkippedStages.nonEmpty();
         boolean shouldShowCompletedStages = completedStages.nonEmpty();
         boolean shouldShowSkippedStages = isComplete && pendingOrSkippedStages.nonEmpty();
         boolean shouldShowFailedStages = failedStages.nonEmpty();
         Elem var141 = new Elem;
         Null var10004 = scala.xml.Null..MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var107 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
         Elem var10008 = new Elem;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10020 = scala.xml.Null..MODULE$;
         TopScope var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         Null var10028 = scala.xml.Null..MODULE$;
         TopScope var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Status:"));
         $buf.$amp$plus(new Elem((String)null, "Strong", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(jobData.status());
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "li", var10020, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         Object var10016;
         if (jobData.jobGroup().isDefined()) {
            var10020 = scala.xml.Null..MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            var10028 = scala.xml.Null..MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Job Group:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(jobData.jobGroup().get());
            $buf.$amp$plus(new Text("\n              "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (shouldShowActiveStages) {
            var10020 = scala.xml.Null..MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var108 = new UnprefixedAttribute("href", new Text("#active"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            Null var10036 = scala.xml.Null..MODULE$;
            TopScope var10037 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Active Stages:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var10036, var10037, false, $buf));
            $buf.$amp$plus(new Elem((String)null, "a", var108, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(activeStages.size()));
            $buf.$amp$plus(new Text("\n              "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (shouldShowPendingStages) {
            var10020 = scala.xml.Null..MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var109 = new UnprefixedAttribute("href", new Text("#pending"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                  "));
            Null var201 = scala.xml.Null..MODULE$;
            TopScope var205 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Pending Stages:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var201, var205, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new Elem((String)null, "a", var109, var10029, false, $buf));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(pendingOrSkippedStages.size()));
            $buf.$amp$plus(new Text("\n              "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (shouldShowCompletedStages) {
            var10020 = scala.xml.Null..MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var110 = new UnprefixedAttribute("href", new Text("#completed"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            Null var202 = scala.xml.Null..MODULE$;
            TopScope var206 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Completed Stages:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var202, var206, false, $buf));
            $buf.$amp$plus(new Elem((String)null, "a", var110, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(completedStages.size()));
            $buf.$amp$plus(new Text("\n              "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (shouldShowSkippedStages) {
            var10020 = scala.xml.Null..MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var111 = new UnprefixedAttribute("href", new Text("#skipped"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            Null var203 = scala.xml.Null..MODULE$;
            TopScope var207 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Skipped Stages:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var203, var207, false, $buf));
            $buf.$amp$plus(new Elem((String)null, "a", var111, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(pendingOrSkippedStages.size()));
            $buf.$amp$plus(new Text("\n            "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n          "));
         if (shouldShowFailedStages) {
            var10020 = scala.xml.Null..MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var112 = new UnprefixedAttribute("href", new Text("#failed"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            Null var204 = scala.xml.Null..MODULE$;
            TopScope var208 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Failed Stages:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var204, var208, false, $buf));
            $buf.$amp$plus(new Elem((String)null, "a", var112, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(failedStages.size()));
            $buf.$amp$plus(new Text("\n              "));
            var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
         } else {
            var10016 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n        "));
         var10008.<init>((String)null, "ul", var107, var10013, false, $buf);
         $buf.$amp$plus(var10008);
         $buf.$amp$plus(new Text("\n      "));
         var141.<init>((String)null, "div", var10004, var10005, false, $buf);
         NodeSeq summary = var141;
         ObjectRef content = ObjectRef.create(summary);
         long appStartTime = ((ApplicationAttemptInfo)this.org$apache$spark$ui$jobs$JobPage$$store.applicationInfo().attempts().head()).startTime().getTime();
         content.elem = (T)((NodeSeq)((NodeSeq)content.elem).$plus$plus(this.makeTimeline(activeStages.$plus$plus(completedStages).$plus$plus(failedStages), this.org$apache$spark$ui$jobs$JobPage$$store.executorList(false), appStartTime), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
         Option var57 = this.org$apache$spark$ui$jobs$JobPage$$store.asOption(new Serializable(this, jobId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final JobPage $outer;
            private final int jobId$1;

            public final Seq<RDDOperationGraph> apply() {
               return this.$outer.org$apache$spark$ui$jobs$JobPage$$store.operationGraphForJob(this.jobId$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.jobId$1 = jobId$1;
               }
            }
         });
         Object operationGraphContent;
         if (var57 instanceof Some) {
            Some var58 = (Some)var57;
            Seq operationGraph = (Seq)var58.x();
            operationGraphContent = UIUtils$.MODULE$.showDagVizForJob(jobId, operationGraph);
         } else {
            if (!None$.MODULE$.equals(var57)) {
               throw new MatchError(var57);
            }

            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var113 = new UnprefixedAttribute("id", new Text("no-info"), $md);
            var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var10012 = scala.xml.Null..MODULE$;
            var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("No DAG visualization information to display for job "));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(jobId));
            $buf.$amp$plus(new Elem((String)null, "p", var10012, var10013, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            operationGraphContent = new Elem((String)null, "div", var113, var10005, false, $buf);
         }

         content.elem = (T)((NodeSeq)((NodeSeq)content.elem).$plus$plus((GenTraversableOnce)operationGraphContent, scala.xml.NodeSeq..MODULE$.canBuildFrom()));
         if (shouldShowActiveStages) {
            NodeSeq var10001 = (NodeSeq)content.elem;
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var114 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-activeStages','aggregated-activeStages')"), $md);
            var114 = new UnprefixedAttribute("class", new Text("collapse-aggregated-activeStages collapse-table"), var114);
            var114 = new UnprefixedAttribute("id", new Text("active"), var114);
            TopScope var10007 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var10014 = scala.xml.Null..MODULE$;
            TopScope var10015 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var117 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
            $buf.$amp$plus(new Elem((String)null, "span", var117, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n            "));
            Null var10022 = scala.xml.Null..MODULE$;
            TopScope var10023 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Active Stages ("));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(activeStages.size()));
            $buf.$amp$plus(new Text(")"));
            $buf.$amp$plus(new Elem((String)null, "a", var10022, var10023, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "h4", var10014, var10015, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            Elem var10002 = new Elem((String)null, "span", var114, var10007, false, $buf);
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var118 = new UnprefixedAttribute("class", new Text("aggregated-activeStages collapsible-table"), $md);
            TopScope var155 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(activeStagesTable.toNodeSeq());
            $buf.$amp$plus(new Text("\n        "));
            content.elem = (T)((NodeSeq)var10001.$plus$plus((GenTraversableOnce)var10002.$plus$plus(new Elem((String)null, "div", var118, var155, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
         }

         if (shouldShowPendingStages) {
            NodeSeq var142 = (NodeSeq)content.elem;
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var119 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-pendingOrSkippedStages',\n            'aggregated-pendingOrSkippedStages')"), $md);
            var119 = new UnprefixedAttribute("class", new Text("collapse-aggregated-pendingOrSkippedStages collapse-table"), var119);
            var119 = new UnprefixedAttribute("id", new Text("pending"), var119);
            TopScope var151 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var161 = scala.xml.Null..MODULE$;
            TopScope var165 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var122 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
            $buf.$amp$plus(new Elem((String)null, "span", var122, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n            "));
            Null var186 = scala.xml.Null..MODULE$;
            TopScope var190 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Pending Stages ("));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(pendingOrSkippedStages.size()));
            $buf.$amp$plus(new Text(")"));
            $buf.$amp$plus(new Elem((String)null, "a", var186, var190, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "h4", var161, var165, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            Elem var146 = new Elem((String)null, "span", var119, var151, false, $buf);
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var123 = new UnprefixedAttribute("class", new Text("aggregated-pendingOrSkippedStages collapsible-table"), $md);
            TopScope var156 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(pendingOrSkippedStagesTable.toNodeSeq());
            $buf.$amp$plus(new Text("\n        "));
            content.elem = (T)((NodeSeq)var142.$plus$plus((GenTraversableOnce)var146.$plus$plus(new Elem((String)null, "div", var123, var156, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
         }

         if (shouldShowCompletedStages) {
            NodeSeq var143 = (NodeSeq)content.elem;
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var124 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-completedStages',\n            'aggregated-completedStages')"), $md);
            var124 = new UnprefixedAttribute("class", new Text("collapse-aggregated-completedStages collapse-table"), var124);
            var124 = new UnprefixedAttribute("id", new Text("completed"), var124);
            TopScope var152 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var162 = scala.xml.Null..MODULE$;
            TopScope var166 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var127 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
            $buf.$amp$plus(new Elem((String)null, "span", var127, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n            "));
            Null var187 = scala.xml.Null..MODULE$;
            TopScope var191 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Completed Stages ("));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(completedStages.size()));
            $buf.$amp$plus(new Text(")"));
            $buf.$amp$plus(new Elem((String)null, "a", var187, var191, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "h4", var162, var166, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            Elem var147 = new Elem((String)null, "span", var124, var152, false, $buf);
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var128 = new UnprefixedAttribute("class", new Text("aggregated-completedStages collapsible-table"), $md);
            TopScope var157 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(completedStagesTable.toNodeSeq());
            $buf.$amp$plus(new Text("\n        "));
            content.elem = (T)((NodeSeq)var143.$plus$plus((GenTraversableOnce)var147.$plus$plus(new Elem((String)null, "div", var128, var157, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
         }

         if (shouldShowSkippedStages) {
            NodeSeq var144 = (NodeSeq)content.elem;
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var129 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-pendingOrSkippedStages',\n            'aggregated-pendingOrSkippedStages')"), $md);
            var129 = new UnprefixedAttribute("class", new Text("collapse-aggregated-pendingOrSkippedStages collapse-table"), var129);
            var129 = new UnprefixedAttribute("id", new Text("skipped"), var129);
            TopScope var153 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var163 = scala.xml.Null..MODULE$;
            TopScope var167 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var132 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
            $buf.$amp$plus(new Elem((String)null, "span", var132, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n            "));
            Null var188 = scala.xml.Null..MODULE$;
            TopScope var192 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Skipped Stages ("));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(pendingOrSkippedStages.size()));
            $buf.$amp$plus(new Text(")"));
            $buf.$amp$plus(new Elem((String)null, "a", var188, var192, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "h4", var163, var167, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            Elem var148 = new Elem((String)null, "span", var129, var153, false, $buf);
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var133 = new UnprefixedAttribute("class", new Text("aggregated-pendingOrSkippedStages collapsible-table"), $md);
            TopScope var158 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(pendingOrSkippedStagesTable.toNodeSeq());
            $buf.$amp$plus(new Text("\n        "));
            content.elem = (T)((NodeSeq)var144.$plus$plus((GenTraversableOnce)var148.$plus$plus(new Elem((String)null, "div", var133, var158, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
         }

         if (shouldShowFailedStages) {
            NodeSeq var145 = (NodeSeq)content.elem;
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var134 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-failedStages','aggregated-failedStages')"), $md);
            var134 = new UnprefixedAttribute("class", new Text("collapse-aggregated-failedStages collapse-table"), var134);
            var134 = new UnprefixedAttribute("id", new Text("failed"), var134);
            TopScope var154 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var164 = scala.xml.Null..MODULE$;
            TopScope var168 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var137 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
            $buf.$amp$plus(new Elem((String)null, "span", var137, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n            "));
            Null var189 = scala.xml.Null..MODULE$;
            TopScope var193 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Failed Stages ("));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(failedStages.size()));
            $buf.$amp$plus(new Text(")"));
            $buf.$amp$plus(new Elem((String)null, "a", var189, var193, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "h4", var164, var168, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            Elem var149 = new Elem((String)null, "span", var134, var154, false, $buf);
            MetaData $md = scala.xml.Null..MODULE$;
            MetaData var138 = new UnprefixedAttribute("class", new Text("aggregated-failedStages collapsible-table"), $md);
            TopScope var159 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(failedStagesTable.toNodeSeq());
            $buf.$amp$plus(new Text("\n        "));
            content.elem = (T)((NodeSeq)var145.$plus$plus((GenTraversableOnce)var149.$plus$plus(new Elem((String)null, "div", var138, var159, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
         }

         String x$4 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Details for Job ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(jobId)}));
         Function0 x$5 = new Serializable(this, content) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef content$1;

            public final NodeSeq apply() {
               return (NodeSeq)this.content$1.elem;
            }

            public {
               this.content$1 = content$1;
            }
         };
         JobsTab x$6 = this.org$apache$spark$ui$jobs$JobPage$$parent;
         boolean x$7 = true;
         Option x$8 = UIUtils$.MODULE$.headerSparkPage$default$4();
         Option x$9 = UIUtils$.MODULE$.headerSparkPage$default$5();
         boolean x$10 = UIUtils$.MODULE$.headerSparkPage$default$7();
         var10000 = UIUtils$.MODULE$.headerSparkPage(x$4, x$5, x$6, x$8, x$9, x$7, x$10);
      } catch (NonLocalReturnControl var106) {
         if (var106.key() != var2) {
            throw var106;
         }

         var10000 = (Seq)var106.value();
      }

      return var10000;
   }

   public JobPage(JobsTab parent, AppStatusStore store) {
      super("job");
      this.org$apache$spark$ui$jobs$JobPage$$parent = parent;
      this.org$apache$spark$ui$jobs$JobPage$$store = store;
      Predef$ var10003 = Predef$.MODULE$;
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var26 = new UnprefixedAttribute("class", new Text("legend-area"), $md);
      TopScope var10009 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var27 = new UnprefixedAttribute("height", new Text("85px"), $md);
      var27 = new UnprefixedAttribute("width", new Text("150px"), var27);
      TopScope var10017 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var29 = new UnprefixedAttribute("ry", new Text("2px"), $md);
      var29 = new UnprefixedAttribute("rx", new Text("2px"), var29);
      var29 = new UnprefixedAttribute("height", new Text("15px"), var29);
      var29 = new UnprefixedAttribute("width", new Text("20px"), var29);
      var29 = new UnprefixedAttribute("y", new Text("5px"), var29);
      var29 = new UnprefixedAttribute("x", new Text("5px"), var29);
      var29 = new UnprefixedAttribute("class", new Text("completed-stage-legend"), var29);
      $buf.$amp$plus(new Elem((String)null, "rect", var29, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var36 = new UnprefixedAttribute("y", new Text("17px"), $md);
      var36 = new UnprefixedAttribute("x", new Text("35px"), var36);
      TopScope var10025 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Completed"));
      $buf.$amp$plus(new Elem((String)null, "text", var36, var10025, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var38 = new UnprefixedAttribute("ry", new Text("2px"), $md);
      var38 = new UnprefixedAttribute("rx", new Text("2px"), var38);
      var38 = new UnprefixedAttribute("height", new Text("15px"), var38);
      var38 = new UnprefixedAttribute("width", new Text("20px"), var38);
      var38 = new UnprefixedAttribute("y", new Text("30px"), var38);
      var38 = new UnprefixedAttribute("x", new Text("5px"), var38);
      var38 = new UnprefixedAttribute("class", new Text("failed-stage-legend"), var38);
      $buf.$amp$plus(new Elem((String)null, "rect", var38, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var45 = new UnprefixedAttribute("y", new Text("42px"), $md);
      var45 = new UnprefixedAttribute("x", new Text("35px"), var45);
      var10025 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Failed"));
      $buf.$amp$plus(new Elem((String)null, "text", var45, var10025, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var47 = new UnprefixedAttribute("ry", new Text("2px"), $md);
      var47 = new UnprefixedAttribute("rx", new Text("2px"), var47);
      var47 = new UnprefixedAttribute("height", new Text("15px"), var47);
      var47 = new UnprefixedAttribute("width", new Text("20px"), var47);
      var47 = new UnprefixedAttribute("y", new Text("55px"), var47);
      var47 = new UnprefixedAttribute("x", new Text("5px"), var47);
      var47 = new UnprefixedAttribute("class", new Text("active-stage-legend"), var47);
      $buf.$amp$plus(new Elem((String)null, "rect", var47, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var54 = new UnprefixedAttribute("y", new Text("67px"), $md);
      var54 = new UnprefixedAttribute("x", new Text("35px"), var54);
      var10025 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Active"));
      $buf.$amp$plus(new Elem((String)null, "text", var54, var10025, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      $buf.$amp$plus(new Elem((String)null, "svg", var27, var10017, false, $buf));
      this.STAGES_LEGEND = (String)(new StringOps(var10003.augmentString((new Elem((String)null, "div", var26, var10009, false, $buf)).toString()))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(char x$1) {
            return x$1 != '\n';
         }
      });
      var10003 = Predef$.MODULE$;
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var56 = new UnprefixedAttribute("class", new Text("legend-area"), $md);
      var10009 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var57 = new UnprefixedAttribute("height", new Text("55px"), $md);
      var57 = new UnprefixedAttribute("width", new Text("150px"), var57);
      var10017 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var59 = new UnprefixedAttribute("ry", new Text("2px"), $md);
      var59 = new UnprefixedAttribute("rx", new Text("2px"), var59);
      var59 = new UnprefixedAttribute("height", new Text("15px"), var59);
      var59 = new UnprefixedAttribute("width", new Text("20px"), var59);
      var59 = new UnprefixedAttribute("y", new Text("5px"), var59);
      var59 = new UnprefixedAttribute("x", new Text("5px"), var59);
      var59 = new UnprefixedAttribute("class", new Text("executor-added-legend"), var59);
      $buf.$amp$plus(new Elem((String)null, "rect", var59, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var66 = new UnprefixedAttribute("y", new Text("17px"), $md);
      var66 = new UnprefixedAttribute("x", new Text("35px"), var66);
      var10025 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Added"));
      $buf.$amp$plus(new Elem((String)null, "text", var66, var10025, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var68 = new UnprefixedAttribute("ry", new Text("2px"), $md);
      var68 = new UnprefixedAttribute("rx", new Text("2px"), var68);
      var68 = new UnprefixedAttribute("height", new Text("15px"), var68);
      var68 = new UnprefixedAttribute("width", new Text("20px"), var68);
      var68 = new UnprefixedAttribute("y", new Text("30px"), var68);
      var68 = new UnprefixedAttribute("x", new Text("5px"), var68);
      var68 = new UnprefixedAttribute("class", new Text("executor-removed-legend"), var68);
      $buf.$amp$plus(new Elem((String)null, "rect", var68, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var75 = new UnprefixedAttribute("y", new Text("42px"), $md);
      var75 = new UnprefixedAttribute("x", new Text("35px"), var75);
      var10025 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Removed"));
      $buf.$amp$plus(new Elem((String)null, "text", var75, var10025, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      $buf.$amp$plus(new Elem((String)null, "svg", var57, var10017, false, $buf));
      this.EXECUTORS_LEGEND = (String)(new StringOps(var10003.augmentString((new Elem((String)null, "div", var56, var10009, false, $buf)).toString()))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(char x$2) {
            return x$2 != '\n';
         }
      });
   }
}
