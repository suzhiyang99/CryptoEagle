package org.apache.spark.ui.jobs;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.spark.JobExecutionStatus;
import org.apache.spark.scheduler.SchedulingMode$;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.AppSummary;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.status.api.v1.ExecutorSummary;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.ui.ToolTips$;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Iterable;
import scala.collection.immutable.Iterable$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.ListBuffer$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
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
   bytes = "\u0006\u0001\u0005Ma!B\u0001\u0003\u0001\u0011a!aC!mY*{'m\u001d)bO\u0016T!a\u0001\u0003\u0002\t)|'m\u001d\u0006\u0003\u000b\u0019\t!!^5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059yQ\"\u0001\u0003\n\u0005A!!!C,fEVK\u0005+Y4f\u0011!\u0011\u0002A!A!\u0002\u0013!\u0012A\u00029be\u0016tGo\u0001\u0001\u0011\u0005U1R\"\u0001\u0002\n\u0005]\u0011!a\u0002&pEN$\u0016M\u0019\u0005\t3\u0001\u0011\t\u0011)A\u00055\u0005)1\u000f^8sKB\u00111DH\u0007\u00029)\u0011QDB\u0001\u0007gR\fG/^:\n\u0005}a\"AD!qaN#\u0018\r^;t'R|'/\u001a\u0005\u0006C\u0001!\tAI\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007\r\"S\u0005\u0005\u0002\u0016\u0001!)!\u0003\ta\u0001)!)\u0011\u0004\ta\u00015!9q\u0005\u0001b\u0001\n\u0013A\u0013a\u0003&P\u0005N{F*R$F\u001d\u0012+\u0012!\u000b\t\u0003UAr!a\u000b\u0018\u000e\u00031R\u0011!L\u0001\u0006g\u000e\fG.Y\u0005\u0003_1\na\u0001\u0015:fI\u00164\u0017BA\u00193\u0005\u0019\u0019FO]5oO*\u0011q\u0006\f\u0005\u0007i\u0001\u0001\u000b\u0011B\u0015\u0002\u0019){%iU0M\u000b\u001e+e\n\u0012\u0011\t\u000fY\u0002!\u0019!C\u0005Q\u0005\u0001R\tW#D+R{%kU0M\u000b\u001e+e\n\u0012\u0005\u0007q\u0001\u0001\u000b\u0011B\u0015\u0002#\u0015CViQ+U\u001fJ\u001bv\fT#H\u000b:#\u0005\u0005C\u0003;\u0001\u0011%1(\u0001\u0007nC.,'j\u001c2Fm\u0016tG\u000f\u0006\u0002=\u0011B\u0019Q(R\u0015\u000f\u0005y\u001aeBA C\u001b\u0005\u0001%BA!\u0014\u0003\u0019a$o\\8u}%\tQ&\u0003\u0002EY\u00059\u0001/Y2lC\u001e,\u0017B\u0001$H\u0005\r\u0019V-\u001d\u0006\u0003\t2BQaA\u001dA\u0002%\u00032!P#K!\tY\u0005+D\u0001M\u0015\tie*\u0001\u0002wc)\u0011q\nH\u0001\u0004CBL\u0017BA)M\u0005\u001dQuN\u0019#bi\u0006DQa\u0015\u0001\u0005\nQ\u000b\u0011#\\1lK\u0016CXmY;u_J,e/\u001a8u)\taT\u000bC\u0003W%\u0002\u0007q+A\u0005fq\u0016\u001cW\u000f^8sgB\u0019Q(\u0012-\u0011\u0005-K\u0016B\u0001.M\u0005=)\u00050Z2vi>\u00148+^7nCJL\b\"\u0002/\u0001\t\u0013i\u0016\u0001D7bW\u0016$\u0016.\\3mS:,G\u0003\u00020fM\u001e\u00042!P#`!\t\u00017-D\u0001b\u0015\t\u0011G&A\u0002y[2L!\u0001Z1\u0003\t9{G-\u001a\u0005\u0006\u0007m\u0003\r!\u0013\u0005\u0006-n\u0003\ra\u0016\u0005\u0006Qn\u0003\r![\u0001\ngR\f'\u000f\u001e+j[\u0016\u0004\"a\u000b6\n\u0005-d#\u0001\u0002'p]\u001eDQ!\u001c\u0001\u0005\n9\f\u0011B[8cgR\u000b'\r\\3\u0015\u000fy{70`@\u0002\u0002!)\u0001\u000f\u001ca\u0001c\u00069!/Z9vKN$\bC\u0001:z\u001b\u0005\u0019(B\u0001;v\u0003\u0011AG\u000f\u001e9\u000b\u0005Y<\u0018aB:feZdW\r\u001e\u0006\u0002q\u0006)!.\u0019<bq&\u0011!p\u001d\u0002\u0013\u0011R$\boU3sm2,GOU3rk\u0016\u001cH\u000fC\u0003}Y\u0002\u0007\u0011&A\u0007uC\ndW\rS3bI\u0016\u0014\u0018\n\u001a\u0005\u0006}2\u0004\r!K\u0001\u0007U>\u0014G+Y4\t\u000b\ra\u0007\u0019A%\t\u000f\u0005\rA\u000e1\u0001\u0002\u0006\u0005Y1.\u001b7m\u000b:\f'\r\\3e!\rY\u0013qA\u0005\u0004\u0003\u0013a#a\u0002\"p_2,\u0017M\u001c\u0005\b\u0003\u001b\u0001A\u0011AA\b\u0003\u0019\u0011XM\u001c3feR\u0019a,!\u0005\t\rA\fY\u00011\u0001r\u0001"
)
public class AllJobsPage extends WebUIPage {
   private final JobsTab parent;
   public final AppStatusStore org$apache$spark$ui$jobs$AllJobsPage$$store;
   private final String JOBS_LEGEND;
   private final String EXECUTORS_LEGEND;

   private String JOBS_LEGEND() {
      return this.JOBS_LEGEND;
   }

   private String EXECUTORS_LEGEND() {
      return this.EXECUTORS_LEGEND;
   }

   private Seq<String> makeJobEvent(Seq<JobData> jobs) {
      return (Seq)((TraversableLike)jobs.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(JobData job) {
            boolean var3;
            label18: {
               JobExecutionStatus var10000 = job.status();
               JobExecutionStatus var2 = JobExecutionStatus.UNKNOWN;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label18;
                  }
               } else if (var10000.equals(var2)) {
                  break label18;
               }

               if (job.submissionTime().isDefined()) {
                  var3 = true;
                  return var3;
               }
            }

            var3 = false;
            return var3;
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AllJobsPage $outer;

         public final String apply(JobData job) {
            int jobId = job.jobId();
            JobExecutionStatus status = job.status();
            Tuple2 var5 = ApiHelper$.MODULE$.lastStageNameAndDescription(this.$outer.org$apache$spark$ui$jobs$AllJobsPage$$store, job);
            if (var5 != null) {
               String lastStageDescription = (String)var5._2();
               String jobDescription = UIUtils$.MODULE$.makeDescription(lastStageDescription, "", true).text();
               long submissionTime = ((Date)job.submissionTime().get()).getTime();
               long completionTime = BoxesRunTime.unboxToLong(job.completionTime().map(new Serializable(this) {
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
               String classNameByStatus;
               if (JobExecutionStatus.SUCCEEDED.equals(status)) {
                  classNameByStatus = "succeeded";
               } else if (JobExecutionStatus.FAILED.equals(status)) {
                  classNameByStatus = "failed";
               } else if (JobExecutionStatus.RUNNING.equals(status)) {
                  classNameByStatus = "running";
               } else {
                  if (!JobExecutionStatus.UNKNOWN.equals(status)) {
                     throw new MatchError(status);
                  }

                  classNameByStatus = "unknown";
               }

               String jsEscapedDesc;
               StringOps var10000;
               Predef$ var10002;
               StringContext var10003;
               Predef$ var10004;
               Object[] var10005;
               String var10008;
               label33: {
                  label32: {
                     String escapedDesc = .MODULE$.escape(jobDescription);
                     jsEscapedDesc = StringEscapeUtils.escapeEcmaScript(escapedDesc);
                     var10000 = new StringOps;
                     var10002 = Predef$.MODULE$;
                     var10003 = new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n           |{\n           |  'className': 'job application-timeline-object ", "',\n           |  'group': 'jobs',\n           |  'start': new Date(", "),\n           |  'end': new Date(", "),\n           |  'content': '<div class=\"application-timeline-content\"' +\n           |     'data-html=\"true\" data-placement=\"top\" data-toggle=\"tooltip\"' +\n           |     'data-title=\"", " (Job ", ")<br>' +\n           |     'Status: ", "<br>' +\n           |     'Submitted: ", "' +\n           |     '", "\">' +\n           |    '", " (Job ", ")</div>'\n           |}\n         "})));
                     var10004 = Predef$.MODULE$;
                     var10005 = new Object[]{classNameByStatus, BoxesRunTime.boxToLong(submissionTime), BoxesRunTime.boxToLong(completionTime), jsEscapedDesc, BoxesRunTime.boxToInteger(jobId), status, UIUtils$.MODULE$.formatDate(new Date(submissionTime)), null, null, null};
                     JobExecutionStatus var19 = JobExecutionStatus.RUNNING;
                     if (status == null) {
                        if (var19 == null) {
                           break label32;
                        }
                     } else if (status.equals(var19)) {
                        break label32;
                     }

                     var10008 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"<br>Completed: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{UIUtils$.MODULE$.formatDate(new Date(completionTime))}));
                     break label33;
                  }

                  var10008 = "";
               }

               var10005[7] = var10008;
               var10005[8] = jsEscapedDesc;
               var10005[9] = BoxesRunTime.boxToInteger(jobId);
               var10000.<init>(var10002.augmentString(var10003.s(var10004.genericWrapArray(var10005))));
               String jobEventJsonAsStr = var10000.stripMargin();
               return jobEventJsonAsStr;
            } else {
               throw new MatchError(var5);
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   private Seq<String> makeExecutorEvent(Seq<ExecutorSummary> executors) {
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

   private Seq<Node> makeTimeline(Seq<JobData> jobs, Seq<ExecutorSummary> executors, long startTime) {
      Seq jobEventJsonAsStrSeq = this.makeJobEvent(jobs);
      Seq executorEventJsonAsStrSeq = this.makeExecutorEvent(executors);
      String groupJsonArrayAsStr = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n          |[\n          |  {\n          |    'id': 'executors',\n          |    'content': '<div>Executors</div>", "',\n          |  },\n          |  {\n          |    'id': 'jobs',\n          |    'content': '<div>Jobs</div>", "',\n          |  }\n          |]\n        "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.EXECUTORS_LEGEND(), this.JOBS_LEGEND()}))))).stripMargin();
      String eventArrayAsStr = ((TraversableOnce)jobEventJsonAsStrSeq.$plus$plus(executorEventJsonAsStrSeq, Seq$.MODULE$.canBuildFrom())).mkString("[", ",", "]");
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var24 = new UnprefixedAttribute("class", new Text("expand-application-timeline"), $md);
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var25 = new UnprefixedAttribute("class", new Text("expand-application-timeline-arrow arrow-closed"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var25, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var26 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
      var26 = new UnprefixedAttribute("title", ToolTips$.MODULE$.JOB_TIMELINE(), var26);
      var26 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var26);
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        Event Timeline\n      "));
      $buf.$amp$plus(new Elem((String)null, "a", var26, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      Elem var10000 = new Elem((String)null, "span", var24, var10005, false, $buf);
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var29 = new UnprefixedAttribute("class", new Text("collapsed"), $md);
      var29 = new UnprefixedAttribute("id", new Text("application-timeline"), var29);
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var31 = new UnprefixedAttribute("class", new Text("control-panel"), $md);
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var32 = new UnprefixedAttribute("id", new Text("application-timeline-zoom-lock"), $md);
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
      $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"drawApplicationTimeline(", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{groupJsonArrayAsStr}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", ", ", ", ");"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{eventArrayAsStr, BoxesRunTime.boxToLong(startTime), BoxesRunTime.boxToInteger(UIUtils$.MODULE$.getTimeZoneOffset())}))).toString()));
      $buf.$amp$plus(new Text("\n    "));
      return (Seq)var35.$plus$plus(new Elem((String)null, "script", var34, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
   }

   private Seq<Node> jobsTable(HttpServletRequest request, String tableHeaderId, String jobTag, Seq<JobData> jobs, boolean killEnabled) {
      Map allParameters = ((TraversableOnce)JavaConverters$.MODULE$.mapAsScalaMapConverter(request.getParameterMap()).asScala()).toMap(Predef$.MODULE$.$conforms()).mapValues(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String[] x$4) {
            return (String[])Predef$.MODULE$.refArrayOps((Object[])x$4).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String requestParameter) {
                  return UIUtils$.MODULE$.stripXSS(requestParameter);
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
         }
      });
      Iterable parameterOtherTable = (Iterable)allParameters.filterNot(new Serializable(this, jobTag) {
         public static final long serialVersionUID = 0L;
         private final String jobTag$1;

         public final boolean apply(Tuple2<String, String[]> x$5) {
            return ((String)x$5._1()).startsWith(this.jobTag$1);
         }

         public {
            this.jobTag$1 = jobTag$1;
         }
      }).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, String[]> para) {
            return (new StringBuilder()).append((Object)((String)para._1())).append((Object)"=").append((Object)((String[])para._2())[0]).toString();
         }
      }, Iterable$.MODULE$.canBuildFrom());
      boolean someJobHasJobGroup = jobs.exists(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(JobData x$6) {
            return x$6.jobGroup().isDefined();
         }
      });
      String jobIdTitle = someJobHasJobGroup ? "Job Id (Job Group)" : "Job Id";
      String parameterJobPage = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)jobTag).append((Object)".page").toString()));
      String parameterJobSortColumn = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)jobTag).append((Object)".sort").toString()));
      String parameterJobSortDesc = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)jobTag).append((Object)".desc").toString()));
      String parameterJobPageSize = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)jobTag).append((Object)".pageSize").toString()));
      String parameterJobPrevPageSize = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)jobTag).append((Object)".prevPageSize").toString()));
      int jobPage = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterJobPage).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$7) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$7))).toInt();
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
      String jobSortColumn = (String)Option$.MODULE$.apply(parameterJobSortColumn).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String sortColumn) {
            return UIUtils$.MODULE$.decodeURLParameter(sortColumn);
         }
      }).getOrElse(new Serializable(this, jobIdTitle) {
         public static final long serialVersionUID = 0L;
         private final String jobIdTitle$1;

         public final String apply() {
            return this.jobIdTitle$1;
         }

         public {
            this.jobIdTitle$1 = jobIdTitle$1;
         }
      });
      boolean jobSortDesc = BoxesRunTime.unboxToBoolean(Option$.MODULE$.apply(parameterJobSortDesc).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$8) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$8))).toBoolean();
         }
      }).getOrElse(new Serializable(this, jobIdTitle, jobSortColumn) {
         public static final long serialVersionUID = 0L;
         private final String jobIdTitle$1;
         private final String jobSortColumn$1;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            boolean var2;
            label23: {
               String var10000 = this.jobSortColumn$1;
               String var1 = this.jobIdTitle$1;
               if (var10000 == null) {
                  if (var1 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var1)) {
                  break label23;
               }

               var2 = false;
               return var2;
            }

            var2 = true;
            return var2;
         }

         public {
            this.jobIdTitle$1 = jobIdTitle$1;
            this.jobSortColumn$1 = jobSortColumn$1;
         }
      }));
      int jobPageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterJobPageSize).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$9) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$9))).toInt();
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
      int jobPrevPageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterJobPrevPageSize).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$10) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$10))).toInt();
         }
      }).getOrElse(new Serializable(this, jobPageSize) {
         public static final long serialVersionUID = 0L;
         private final int jobPageSize$1;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.jobPageSize$1;
         }

         public {
            this.jobPageSize$1 = jobPageSize$1;
         }
      }));
      int page = jobPageSize <= jobPrevPageSize ? jobPage : 1;
      long currentTime = System.currentTimeMillis();

      Object var10000;
      try {
         var10000 = (new JobPagedTable(this.org$apache$spark$ui$jobs$AllJobsPage$$store, jobs, tableHeaderId, jobTag, UIUtils$.MODULE$.prependBaseUri(this.parent.basePath(), UIUtils$.MODULE$.prependBaseUri$default$2()), "jobs", parameterOtherTable, killEnabled, currentTime, jobIdTitle, jobPageSize, jobSortColumn, jobSortDesc)).table(page);
      } catch (Throwable var32) {
         boolean var25;
         if (var32 instanceof IllegalArgumentException) {
            var25 = true;
         } else if (var32 instanceof IndexOutOfBoundsException) {
            var25 = true;
         } else {
            var25 = false;
         }

         if (!var25) {
            throw var32;
         }

         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var33 = new UnprefixedAttribute("class", new Text("alert alert-error"), $md);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10012 = scala.xml.Null..MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Error while rendering job table:"));
         $buf.$amp$plus(new Elem((String)null, "p", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         var10012 = scala.xml.Null..MODULE$;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(Utils$.MODULE$.exceptionString(var32));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "pre", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var26 = new Elem((String)null, "div", var33, var10005, false, $buf);
         var10000 = var26;
         return (Seq<Node>)var10000;
      }

      return (Seq<Node>)var10000;
   }

   public Seq<Node> render(HttpServletRequest request) {
      ApplicationInfo appInfo = this.org$apache$spark$ui$jobs$AllJobsPage$$store.applicationInfo();
      long startTime = ((ApplicationAttemptInfo)appInfo.attempts().head()).startTime().getTime();
      long endTime = ((ApplicationAttemptInfo)appInfo.attempts().head()).endTime().getTime();
      ListBuffer activeJobs = new ListBuffer();
      ListBuffer completedJobs = new ListBuffer();
      ListBuffer failedJobs = new ListBuffer();
      this.org$apache$spark$ui$jobs$AllJobsPage$$store.jobsList((List)null).foreach(new Serializable(this, activeJobs, completedJobs, failedJobs) {
         public static final long serialVersionUID = 0L;
         private final ListBuffer activeJobs$1;
         private final ListBuffer completedJobs$1;
         private final ListBuffer failedJobs$1;

         public final ListBuffer<JobData> apply(JobData job) {
            JobExecutionStatus var2 = job.status();
            ListBuffer var3;
            if (JobExecutionStatus.SUCCEEDED.equals(var2)) {
               var3 = this.completedJobs$1.$plus$eq(job);
            } else if (JobExecutionStatus.FAILED.equals(var2)) {
               var3 = this.failedJobs$1.$plus$eq(job);
            } else {
               var3 = this.activeJobs$1.$plus$eq(job);
            }

            return var3;
         }

         public {
            this.activeJobs$1 = activeJobs$1;
            this.completedJobs$1 = completedJobs$1;
            this.failedJobs$1 = failedJobs$1;
         }
      });
      Seq activeJobsTable = this.jobsTable(request, "active", "activeJob", activeJobs, this.parent.killEnabled());
      Seq completedJobsTable = this.jobsTable(request, "completed", "completedJob", completedJobs, false);
      Seq failedJobsTable = this.jobsTable(request, "failed", "failedJob", failedJobs, false);
      boolean shouldShowActiveJobs = activeJobs.nonEmpty();
      boolean shouldShowCompletedJobs = completedJobs.nonEmpty();
      boolean shouldShowFailedJobs = failedJobs.nonEmpty();
      AppSummary appSummary = this.org$apache$spark$ui$jobs$AllJobsPage$$store.appSummary();
      String completedJobNumStr = completedJobs.size() == appSummary.numCompletedJobs() ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(completedJobs.size())})) : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", only showing ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(appSummary.numCompletedJobs()), BoxesRunTime.boxToInteger(completedJobs.size())}));
      String schedulingMode = (String)this.org$apache$spark$ui$jobs$AllJobsPage$$store.environmentInfo().sparkProperties().toMap(Predef$.MODULE$.$conforms()).get("spark.scheduler.mode").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String mode) {
            return SchedulingMode$.MODULE$.withName(mode).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Unknown";
         }
      });
      Elem var10000 = new Elem;
      Null var10004 = scala.xml.Null..MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var72 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
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
      $buf.$amp$plus(new Text("User:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(this.parent.getSparkUser());
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "li", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      var10020 = scala.xml.Null..MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      var10028 = scala.xml.Null..MODULE$;
      var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Total Uptime:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(endTime < 0L && this.parent.sc().isDefined() ? UIUtils$.MODULE$.formatDuration(System.currentTimeMillis() - startTime) : (endTime > 0L ? UIUtils$.MODULE$.formatDuration(endTime - startTime) : BoxedUnit.UNIT));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "li", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      var10020 = scala.xml.Null..MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      var10028 = scala.xml.Null..MODULE$;
      var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Scheduling Mode: "));
      $buf.$amp$plus(new Elem((String)null, "strong", var10028, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(schedulingMode);
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "li", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      Object var10016;
      if (shouldShowActiveJobs) {
         var10020 = scala.xml.Null..MODULE$;
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var73 = new UnprefixedAttribute("href", new Text("#active"), $md);
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var10036 = scala.xml.Null..MODULE$;
         TopScope var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Active Jobs:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var10036, var10037, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "a", var73, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(activeJobs.size()));
         $buf.$amp$plus(new Text("\n              "));
         var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
      } else {
         var10016 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n          "));
      if (shouldShowCompletedJobs) {
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var74 = new UnprefixedAttribute("id", new Text("completed-summary"), $md);
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var75 = new UnprefixedAttribute("href", new Text("#completed"), $md);
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var127 = scala.xml.Null..MODULE$;
         TopScope var129 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Completed Jobs:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var127, var129, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "a", var75, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(completedJobNumStr);
         $buf.$amp$plus(new Text("\n              "));
         var10016 = new Elem((String)null, "li", var74, var10021, false, $buf);
      } else {
         var10016 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n          "));
      if (shouldShowFailedJobs) {
         var10020 = scala.xml.Null..MODULE$;
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var76 = new UnprefixedAttribute("href", new Text("#failed"), $md);
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var128 = scala.xml.Null..MODULE$;
         TopScope var130 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Failed Jobs:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var128, var130, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "a", var76, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(failedJobs.size()));
         $buf.$amp$plus(new Text("\n              "));
         var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
      } else {
         var10016 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n        "));
      var10008.<init>((String)null, "ul", var72, var10013, false, $buf);
      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      var10000.<init>((String)null, "div", var10004, var10005, false, $buf);
      NodeSeq summary = var10000;
      ObjectRef content = ObjectRef.create(summary);
      content.elem = (T)((NodeSeq)((NodeSeq)content.elem).$plus$plus(this.makeTimeline(activeJobs.$plus$plus(completedJobs).$plus$plus(failedJobs), this.org$apache$spark$ui$jobs$AllJobsPage$$store.executorList(false), startTime), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      if (shouldShowActiveJobs) {
         NodeSeq var10001 = (NodeSeq)content.elem;
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var77 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-activeJobs','aggregated-activeJobs')"), $md);
         var77 = new UnprefixedAttribute("class", new Text("collapse-aggregated-activeJobs collapse-table"), var77);
         var77 = new UnprefixedAttribute("id", new Text("active"), var77);
         TopScope var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10014 = scala.xml.Null..MODULE$;
         TopScope var10015 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var80 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var80, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var10022 = scala.xml.Null..MODULE$;
         TopScope var10023 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Active Jobs ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(activeJobs.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var10022, var10023, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10014, var10015, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var10002 = new Elem((String)null, "span", var77, var10007, false, $buf);
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var81 = new UnprefixedAttribute("class", new Text("aggregated-activeJobs collapsible-table"), $md);
         TopScope var98 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(activeJobsTable);
         $buf.$amp$plus(new Text("\n        "));
         content.elem = (T)((NodeSeq)var10001.$plus$plus((GenTraversableOnce)var10002.$plus$plus(new Elem((String)null, "div", var81, var98, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      if (shouldShowCompletedJobs) {
         NodeSeq var92 = (NodeSeq)content.elem;
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var82 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-completedJobs','aggregated-completedJobs')"), $md);
         var82 = new UnprefixedAttribute("class", new Text("collapse-aggregated-completedJobs collapse-table"), var82);
         var82 = new UnprefixedAttribute("id", new Text("completed"), var82);
         TopScope var96 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var101 = scala.xml.Null..MODULE$;
         TopScope var103 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var85 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var85, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var116 = scala.xml.Null..MODULE$;
         TopScope var118 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Completed Jobs ("));
         $buf.$amp$plus(completedJobNumStr);
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var116, var118, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var101, var103, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var94 = new Elem((String)null, "span", var82, var96, false, $buf);
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var86 = new UnprefixedAttribute("class", new Text("aggregated-completedJobs collapsible-table"), $md);
         TopScope var99 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(completedJobsTable);
         $buf.$amp$plus(new Text("\n        "));
         content.elem = (T)((NodeSeq)var92.$plus$plus((GenTraversableOnce)var94.$plus$plus(new Elem((String)null, "div", var86, var99, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      if (shouldShowFailedJobs) {
         NodeSeq var93 = (NodeSeq)content.elem;
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var87 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-failedJobs','aggregated-failedJobs')"), $md);
         var87 = new UnprefixedAttribute("class", new Text("collapse-aggregated-failedJobs collapse-table"), var87);
         var87 = new UnprefixedAttribute("id", new Text("failed"), var87);
         TopScope var97 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var102 = scala.xml.Null..MODULE$;
         TopScope var104 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var90 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var90, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var117 = scala.xml.Null..MODULE$;
         TopScope var119 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Failed Jobs ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(failedJobs.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var117, var119, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var102, var104, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var95 = new Elem((String)null, "span", var87, var97, false, $buf);
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var91 = new UnprefixedAttribute("class", new Text("aggregated-failedJobs collapsible-table"), $md);
         TopScope var100 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(failedJobsTable);
         $buf.$amp$plus(new Text("\n      "));
         content.elem = (T)((NodeSeq)var93.$plus$plus((GenTraversableOnce)var95.$plus$plus(new Elem((String)null, "div", var91, var100, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      String helpText = "A job is triggered by an action, like count() or saveAsTextFile(). Click on a job to see information about the stages of tasks inside it.";
      String x$20 = "Spark Jobs";
      Function0 x$21 = new Serializable(this, content) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef content$1;

         public final NodeSeq apply() {
            return (NodeSeq)this.content$1.elem;
         }

         public {
            this.content$1 = content$1;
         }
      };
      JobsTab x$22 = this.parent;
      Some x$23 = new Some(helpText);
      Option x$24 = UIUtils$.MODULE$.headerSparkPage$default$4();
      boolean x$25 = UIUtils$.MODULE$.headerSparkPage$default$6();
      boolean x$26 = UIUtils$.MODULE$.headerSparkPage$default$7();
      return UIUtils$.MODULE$.headerSparkPage(x$20, x$21, x$22, x$24, x$23, x$25, x$26);
   }

   public AllJobsPage(JobsTab parent, AppStatusStore store) {
      super("");
      this.parent = parent;
      this.org$apache$spark$ui$jobs$AllJobsPage$$store = store;
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
      var29 = new UnprefixedAttribute("class", new Text("succeeded-job-legend"), var29);
      $buf.$amp$plus(new Elem((String)null, "rect", var29, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var36 = new UnprefixedAttribute("y", new Text("17px"), $md);
      var36 = new UnprefixedAttribute("x", new Text("35px"), var36);
      TopScope var10025 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Succeeded"));
      $buf.$amp$plus(new Elem((String)null, "text", var36, var10025, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var38 = new UnprefixedAttribute("ry", new Text("2px"), $md);
      var38 = new UnprefixedAttribute("rx", new Text("2px"), var38);
      var38 = new UnprefixedAttribute("height", new Text("15px"), var38);
      var38 = new UnprefixedAttribute("width", new Text("20px"), var38);
      var38 = new UnprefixedAttribute("y", new Text("30px"), var38);
      var38 = new UnprefixedAttribute("x", new Text("5px"), var38);
      var38 = new UnprefixedAttribute("class", new Text("failed-job-legend"), var38);
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
      var47 = new UnprefixedAttribute("class", new Text("running-job-legend"), var47);
      $buf.$amp$plus(new Elem((String)null, "rect", var47, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var54 = new UnprefixedAttribute("y", new Text("67px"), $md);
      var54 = new UnprefixedAttribute("x", new Text("35px"), var54);
      var10025 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Running"));
      $buf.$amp$plus(new Elem((String)null, "text", var54, var10025, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      $buf.$amp$plus(new Elem((String)null, "svg", var27, var10017, false, $buf));
      this.JOBS_LEGEND = (String)(new StringOps(var10003.augmentString((new Elem((String)null, "div", var26, var10009, false, $buf)).toString()))).filter(new Serializable(this) {
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
