package org.apache.spark.ui.jobs;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.AppStatusUtils$;
import org.apache.spark.status.api.v1.AccumulableInfo;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.TaskData;
import org.apache.spark.status.api.v1.TaskMetrics;
import org.apache.spark.ui.PagedTable;
import org.apache.spark.ui.PagedTable$class;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.AbstractSeq;
import scala.collection.GenTraversableOnce;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.immutable.Iterable$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.xml.Elem;
import scala.xml.EntityRef;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.Unparsed;
import scala.xml.UnprefixedAttribute;
import scala.xml.Unparsed.;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mb!B\u0001\u0003\u0001\u0011a!A\u0004+bg.\u0004\u0016mZ3e)\u0006\u0014G.\u001a\u0006\u0003\u0007\u0011\tAA[8cg*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0004\u00015\u0019\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\rE\u0002\u0015+]i\u0011\u0001B\u0005\u0003-\u0011\u0011!\u0002U1hK\u0012$\u0016M\u00197f!\tAr$D\u0001\u001a\u0015\tQ2$\u0001\u0002wc)\u0011A$H\u0001\u0004CBL'B\u0001\u0010\u0007\u0003\u0019\u0019H/\u0019;vg&\u0011\u0001%\u0007\u0002\t)\u0006\u001c8\u000eR1uC\"A!\u0005\u0001B\u0001B\u0003%A%A\u0003ti\u0006<Wm\u0001\u0001\u0011\u0005a)\u0013B\u0001\u0014\u001a\u0005%\u0019F/Y4f\t\u0006$\u0018\r\u0003\u0005)\u0001\t\u0005\t\u0015!\u0003*\u0003!\u0011\u0017m]3QCRD\u0007C\u0001\u0016.\u001d\tq1&\u0003\u0002-\u001f\u00051\u0001K]3eK\u001aL!AL\u0018\u0003\rM#(/\u001b8h\u0015\tas\u0002\u0003\u00052\u0001\t\u0005\t\u0015!\u00033\u0003-\u0019WO\u001d:f]R$\u0016.\\3\u0011\u00059\u0019\u0014B\u0001\u001b\u0010\u0005\u0011auN\\4\t\u0011Y\u0002!\u0011!Q\u0001\n]\n\u0001\u0002]1hKNK'0\u001a\t\u0003\u001daJ!!O\b\u0003\u0007%sG\u000f\u0003\u0005<\u0001\t\u0005\t\u0015!\u0003*\u0003)\u0019xN\u001d;D_2,XN\u001c\u0005\t{\u0001\u0011\t\u0011)A\u0005}\u0005!A-Z:d!\tqq(\u0003\u0002A\u001f\t9!i\\8mK\u0006t\u0007\u0002\u0003\"\u0001\u0005\u0003\u0005\u000b\u0011B\"\u0002\u000bM$xN]3\u0011\u0005\u0011+U\"A\u000f\n\u0005\u0019k\"AD!qaN#\u0018\r^;t'R|'/\u001a\u0005\u0006\u0011\u0002!\t!S\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0011)cUJT(Q#J\u0003\"a\u0013\u0001\u000e\u0003\tAQAI$A\u0002\u0011BQ\u0001K$A\u0002%BQ!M$A\u0002IBQAN$A\u0002]BQaO$A\u0002%BQ!P$A\u0002yBQAQ$A\u0002\rCQ\u0001\u0016\u0001\u0005BU\u000bq\u0001^1cY\u0016LE-F\u0001*\u0011\u00159\u0006\u0001\"\u0011V\u00035!\u0018M\u00197f\u0007N\u001c8\t\\1tg\")\u0011\f\u0001C!+\u0006\t\u0002/Y4f'&TXMR8s[\u001aKW\r\u001c3\t\u000bm\u0003A\u0011I+\u0002+A\u0014XM\u001e)bO\u0016\u001c\u0016N_3G_Jlg)[3mI\")Q\f\u0001C!+\u0006\u0019\u0002/Y4f\u001dVl'-\u001a:G_Jlg)[3mI\"9q\f\u0001b\u0001\n\u0003\u0002\u0017A\u00033bi\u0006\u001cv.\u001e:dKV\t\u0011\r\u0005\u0002LE&\u00111M\u0001\u0002\u000f)\u0006\u001c8\u000eR1uCN{WO]2f\u0011\u0019)\u0007\u0001)A\u0005C\u0006YA-\u0019;b'>,(oY3!\u0011\u00159\u0007\u0001\"\u0011i\u0003!\u0001\u0018mZ3MS:\\GCA\u0015j\u0011\u0015Qg\r1\u00018\u0003\u0011\u0001\u0018mZ3\t\u000b1\u0004A\u0011I+\u0002!\u001d|')\u001e;u_:4uN]7QCRD\u0007\"\u00028\u0001\t\u0003y\u0017a\u00025fC\u0012,'o]\u000b\u0002aB\u0019\u0011/\u001f?\u000f\u0005I<hBA:w\u001b\u0005!(BA;$\u0003\u0019a$o\\8u}%\t\u0001#\u0003\u0002y\u001f\u00059\u0001/Y2lC\u001e,\u0017B\u0001>|\u0005\r\u0019V-\u001d\u0006\u0003q>\u00012!`A\u0001\u001b\u0005q(BA@\u0010\u0003\rAX\u000e\\\u0005\u0004\u0003\u0007q(\u0001\u0002(pI\u0016Dq!a\u0002\u0001\t\u0003\tI!A\u0002s_^$2\u0001]A\u0006\u0011\u001d\ti!!\u0002A\u0002]\tA\u0001^1tW\"9\u0011\u0011\u0003\u0001\u0005\n\u0005M\u0011\u0001E1dGVlW\u000f\\1u_J\u001c\u0018J\u001c4p)\r\u0001\u0018Q\u0003\u0005\b\u0003\u001b\ty\u00011\u0001\u0018\u0011\u001d\tI\u0002\u0001C\u0005\u00037\t!\"\\3ue&\u001c\u0017J\u001c4p)\u0011\ti\"a\f\u0015\u0007A\fy\u0002\u0003\u0005\u0002\"\u0005]\u0001\u0019AA\u0012\u0003\t1g\u000e\u0005\u0004\u000f\u0003K\tI\u0003]\u0005\u0004\u0003Oy!!\u0003$v]\u000e$\u0018n\u001c82!\rA\u00121F\u0005\u0004\u0003[I\"a\u0003+bg.lU\r\u001e:jGNDq!!\u0004\u0002\u0018\u0001\u0007q\u0003C\u0004\u00024\u0001!I!!\u000e\u0002!\u0015\u0014(o\u001c:NKN\u001c\u0018mZ3DK2dGc\u00019\u00028!9\u0011\u0011HA\u0019\u0001\u0004I\u0013!B3se>\u0014\b"
)
public class TaskPagedTable implements PagedTable<TaskData> {
   private final StageData stage;
   public final String org$apache$spark$ui$jobs$TaskPagedTable$$basePath;
   public final int org$apache$spark$ui$jobs$TaskPagedTable$$pageSize;
   public final String org$apache$spark$ui$jobs$TaskPagedTable$$sortColumn;
   public final boolean org$apache$spark$ui$jobs$TaskPagedTable$$desc;
   private final TaskDataSource dataSource;

   public Seq<Node> table(int page) {
      return PagedTable$class.table(this, page);
   }

   public Seq<Node> pageNavigation(int page, int pageSize, int totalPages) {
      return PagedTable$class.pageNavigation(this, page, pageSize, totalPages);
   }

   public String tableId() {
      return "task-table";
   }

   public String tableCssClass() {
      return "table table-bordered table-condensed table-striped table-head-clickable";
   }

   public String pageSizeFormField() {
      return "task.pageSize";
   }

   public String prevPageSizeFormField() {
      return "task.prevPageSize";
   }

   public String pageNumberFormField() {
      return "task.page";
   }

   public TaskDataSource dataSource() {
      return this.dataSource;
   }

   public String pageLink(int page) {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$jobs$TaskPagedTable$$sortColumn, "UTF-8");
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$TaskPagedTable$$basePath).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageNumberFormField(), BoxesRunTime.boxToInteger(page)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&task.sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{encodedSortColumn}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&task.desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$jobs$TaskPagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageSizeFormField(), BoxesRunTime.boxToInteger(this.org$apache$spark$ui$jobs$TaskPagedTable$$pageSize)}))).toString();
   }

   public String goButtonFormPath() {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$jobs$TaskPagedTable$$sortColumn, "UTF-8");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "&task.sort=", "&task.desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$TaskPagedTable$$basePath, encodedSortColumn, BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$jobs$TaskPagedTable$$desc)}));
   }

   public Seq<Node> headers() {
      Seq taskHeadersAndCssClasses = (Seq)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Index", ""), new Tuple2("ID", ""), new Tuple2("Attempt", ""), new Tuple2("Status", ""), new Tuple2("Locality Level", ""), new Tuple2("Executor ID", ""), new Tuple2("Host", ""), new Tuple2("Launch Time", ""), new Tuple2("Duration", ""), new Tuple2("Scheduler Delay", TaskDetailsClassNames$.MODULE$.SCHEDULER_DELAY()), new Tuple2("Task Deserialization Time", TaskDetailsClassNames$.MODULE$.TASK_DESERIALIZATION_TIME()), new Tuple2("GC Time", ""), new Tuple2("Result Serialization Time", TaskDetailsClassNames$.MODULE$.RESULT_SERIALIZATION_TIME()), new Tuple2("Getting Result Time", TaskDetailsClassNames$.MODULE$.GETTING_RESULT_TIME()), new Tuple2("Peak Execution Memory", TaskDetailsClassNames$.MODULE$.PEAK_EXECUTION_MEMORY())}))).$plus$plus((GenTraversableOnce)(ApiHelper$.MODULE$.hasAccumulators(this.stage) ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Accumulators", "")})) : Nil$.MODULE$), Seq$.MODULE$.canBuildFrom())).$plus$plus((GenTraversableOnce)(ApiHelper$.MODULE$.hasInput(this.stage) ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Input Size / Records", "")})) : Nil$.MODULE$), Seq$.MODULE$.canBuildFrom())).$plus$plus((GenTraversableOnce)(ApiHelper$.MODULE$.hasOutput(this.stage) ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Output Size / Records", "")})) : Nil$.MODULE$), Seq$.MODULE$.canBuildFrom())).$plus$plus((GenTraversableOnce)(ApiHelper$.MODULE$.hasShuffleRead(this.stage) ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Shuffle Read Blocked Time", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_BLOCKED_TIME()), new Tuple2("Shuffle Read Size / Records", ""), new Tuple2("Shuffle Remote Reads", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_REMOTE_SIZE())})) : Nil$.MODULE$), Seq$.MODULE$.canBuildFrom())).$plus$plus((GenTraversableOnce)(ApiHelper$.MODULE$.hasShuffleWrite(this.stage) ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Write Time", ""), new Tuple2("Shuffle Write Size / Records", "")})) : Nil$.MODULE$), Seq$.MODULE$.canBuildFrom())).$plus$plus((GenTraversableOnce)(ApiHelper$.MODULE$.hasBytesSpilled(this.stage) ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Shuffle Spill (Memory)", ""), new Tuple2("Shuffle Spill (Disk)", "")})) : Nil$.MODULE$), Seq$.MODULE$.canBuildFrom())).$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Errors", "")})), Seq$.MODULE$.canBuildFrom());
      if (((SeqLike)taskHeadersAndCssClasses.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, String> x$15) {
            return (String)x$15._1();
         }
      }, Seq$.MODULE$.canBuildFrom())).contains(this.org$apache$spark$ui$jobs$TaskPagedTable$$sortColumn)) {
         Seq headerRow = (Seq)taskHeadersAndCssClasses.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskPagedTable $outer;

            public final Elem apply(Tuple2<String, String> x0$5) {
               if (x0$5 == null) {
                  throw new MatchError(x0$5);
               } else {
                  Elem var10000;
                  label30: {
                     String header;
                     String cssClass;
                     label34: {
                        header = (String)x0$5._1();
                        cssClass = (String)x0$5._2();
                        String var6 = this.$outer.org$apache$spark$ui$jobs$TaskPagedTable$$sortColumn;
                        if (header == null) {
                           if (var6 != null) {
                              break label34;
                           }
                        } else if (!header.equals(var6)) {
                           break label34;
                        }

                        Unparsed headerLink = .MODULE$.apply((new StringBuilder()).append((Object)this.$outer.org$apache$spark$ui$jobs$TaskPagedTable$$basePath).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&task.sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&task.desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(!this.$outer.org$apache$spark$ui$jobs$TaskPagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&task.pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$jobs$TaskPagedTable$$pageSize)}))).toString());
                        String arrow = this.$outer.org$apache$spark$ui$jobs$TaskPagedTable$$desc ? "&#x25BE;" : "&#x25B4;";
                        MetaData $md = scala.xml.Null..MODULE$;
                        MetaData var19 = new UnprefixedAttribute("class", cssClass, $md);
                        TopScope var10005 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n            "));
                        MetaData $md = scala.xml.Null..MODULE$;
                        MetaData var20 = new UnprefixedAttribute("href", headerLink, $md);
                        TopScope var10013 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(header);
                        $buf.$amp$plus(new Text("\n              "));
                        Null var10020 = scala.xml.Null..MODULE$;
                        TopScope var10021 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new EntityRef("nbsp"));
                        $buf.$amp$plus(.MODULE$.apply(arrow));
                        $buf.$amp$plus(new Elem((String)null, "span", var10020, var10021, false, $buf));
                        $buf.$amp$plus(new Text("\n            "));
                        $buf.$amp$plus(new Elem((String)null, "a", var20, var10013, false, $buf));
                        $buf.$amp$plus(new Text("\n          "));
                        var10000 = new Elem((String)null, "th", var19, var10005, false, $buf);
                        break label30;
                     }

                     Unparsed headerLink = .MODULE$.apply((new StringBuilder()).append((Object)this.$outer.org$apache$spark$ui$jobs$TaskPagedTable$$basePath).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&task.sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&task.pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$jobs$TaskPagedTable$$pageSize)}))).toString());
                     MetaData $md = scala.xml.Null..MODULE$;
                     MetaData var21 = new UnprefixedAttribute("class", cssClass, $md);
                     TopScope var23 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(new Text("\n            "));
                     MetaData $md = scala.xml.Null..MODULE$;
                     MetaData var22 = new UnprefixedAttribute("href", headerLink, $md);
                     TopScope var24 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(new Text("\n              "));
                     $buf.$amp$plus(header);
                     $buf.$amp$plus(new Text("\n            "));
                     $buf.$amp$plus(new Elem((String)null, "a", var22, var24, false, $buf));
                     $buf.$amp$plus(new Text("\n          "));
                     var10000 = new Elem((String)null, "th", var21, var23, false, $buf);
                  }

                  Elem var5 = var10000;
                  return var5;
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
         Null var10004 = scala.xml.Null..MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(headerRow);
         return new Elem((String)null, "thead", var10004, var10005, false, $buf);
      } else {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$TaskPagedTable$$sortColumn})));
      }
   }

   public Seq<Node> row(TaskData task) {
      Elem var10000 = new Elem;
      Null var10004 = scala.xml.Null..MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = scala.xml.Null..MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(task.index()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToLong(task.taskId()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(task.speculative() ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " (speculative)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(task.attempt())})) : BoxesRunTime.boxToInteger(task.attempt()).toString());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(task.status());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(task.taskLocality());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(task.executorId());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var40 = new UnprefixedAttribute("style", new Text("float: left"), $md);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(task.host());
      $buf.$amp$plus(new Elem((String)null, "div", var40, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var41 = new UnprefixedAttribute("style", new Text("float: right"), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.dataSource().executorLogs(task.executorId()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Elem apply(Tuple2<String, String> x0$6) {
            if (x0$6 != null) {
               String logName = (String)x0$6._1();
               String logUrl = (String)x0$6._2();
               Null var10004 = scala.xml.Null..MODULE$;
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               MetaData $md = scala.xml.Null..MODULE$;
               MetaData var9 = new UnprefixedAttribute("href", logUrl, $md);
               TopScope var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(logName);
               $buf.$amp$plus(new Elem((String)null, "a", var9, var10013, false, $buf));
               Elem var5 = new Elem((String)null, "div", var10004, var10005, false, $buf);
               return var5;
            } else {
               throw new MatchError(x0$6);
            }
         }
      }, Iterable$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var41, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(UIUtils$.MODULE$.formatDate(task.launchTime()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(this.formatDuration$1(task.duration(), this.formatDuration$default$2$1()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var42 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.SCHEDULER_DELAY(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(UIUtils$.MODULE$.formatDuration(AppStatusUtils$.MODULE$.schedulerDelay(task)));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var42, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var43 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.TASK_DESERIALIZATION_TIME(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.formatDuration$1(task.taskMetrics().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskMetrics x$16) {
            return x$16.executorDeserializeTime();
         }
      }), this.formatDuration$default$2$1()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var43, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.formatDuration$1(task.taskMetrics().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskMetrics x$17) {
            return x$17.jvmGcTime();
         }
      }), true));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var44 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.RESULT_SERIALIZATION_TIME(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.formatDuration$1(task.taskMetrics().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskMetrics x$18) {
            return x$18.resultSerializationTime();
         }
      }), this.formatDuration$default$2$1()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var44, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var45 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.GETTING_RESULT_TIME(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(UIUtils$.MODULE$.formatDuration(AppStatusUtils$.MODULE$.gettingResultTime(task)));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var45, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var46 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.PEAK_EXECUTION_MEMORY(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.formatBytes$1(task.taskMetrics().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskMetrics x$19) {
            return x$19.peakExecutionMemory();
         }
      })));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var46, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      Object var10008;
      if (ApiHelper$.MODULE$.hasAccumulators(this.stage)) {
         var10012 = scala.xml.Null..MODULE$;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(this.accumulatorsInfo(task));
         var10008 = new Elem((String)null, "td", var10012, var10013, false, $buf);
      } else {
         var10008 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(ApiHelper$.MODULE$.hasInput(this.stage) ? this.metricInfo(task, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Elem apply(TaskMetrics m) {
            String bytesRead = Utils$.MODULE$.bytesToString(m.inputMetrics().bytesRead());
            long records = m.inputMetrics().recordsRead();
            Null var10004 = scala.xml.Null..MODULE$;
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(bytesRead);
            $buf.$amp$plus(new Text(" / "));
            $buf.$amp$plus(BoxesRunTime.boxToLong(records));
            return new Elem((String)null, "td", var10004, var10005, false, $buf);
         }
      }) : BoxedUnit.UNIT);
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(ApiHelper$.MODULE$.hasOutput(this.stage) ? this.metricInfo(task, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Elem apply(TaskMetrics m) {
            String bytesWritten = Utils$.MODULE$.bytesToString(m.outputMetrics().bytesWritten());
            long records = m.outputMetrics().recordsWritten();
            Null var10004 = scala.xml.Null..MODULE$;
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(bytesWritten);
            $buf.$amp$plus(new Text(" / "));
            $buf.$amp$plus(BoxesRunTime.boxToLong(records));
            return new Elem((String)null, "td", var10004, var10005, false, $buf);
         }
      }) : BoxedUnit.UNIT);
      $buf.$amp$plus(new Text("\n      "));
      if (ApiHelper$.MODULE$.hasShuffleRead(this.stage)) {
         NodeBuffer $buf = new NodeBuffer();
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var47 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_BLOCKED_TIME(), $md);
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(this.formatDuration$1(task.taskMetrics().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(TaskMetrics x$20) {
               return x$20.shuffleReadMetrics().fetchWaitTime();
            }
         }), this.formatDuration$default$2$1()));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "td", var47, var10014, false, $buf));
         Null var77 = scala.xml.Null..MODULE$;
         var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(this.metricInfo(task, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Unparsed apply(TaskMetrics m) {
               String bytesRead = Utils$.MODULE$.bytesToString(ApiHelper$.MODULE$.totalBytesRead(m.shuffleReadMetrics()));
               long records = m.shuffleReadMetrics().recordsRead();
               return .MODULE$.apply((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{bytesRead, BoxesRunTime.boxToLong(records)})));
            }
         }));
         $buf.$amp$plus(new Elem((String)null, "td", var77, var10014, false, $buf));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var48 = new UnprefixedAttribute("class", TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_REMOTE_SIZE(), $md);
         var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(this.formatBytes$1(task.taskMetrics().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(TaskMetrics x$21) {
               return x$21.shuffleReadMetrics().remoteBytesRead();
            }
         })));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "td", var48, var10014, false, $buf));
         var10008 = $buf;
      } else {
         var10008 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      if (ApiHelper$.MODULE$.hasShuffleWrite(this.stage)) {
         NodeBuffer $buf = new NodeBuffer();
         Null var78 = scala.xml.Null..MODULE$;
         TopScope var84 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(this.formatDuration$1(task.taskMetrics().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(TaskMetrics m) {
               return TimeUnit.NANOSECONDS.toMillis(m.shuffleWriteMetrics().writeTime());
            }
         }), true));
         $buf.$amp$plus(new Elem((String)null, "td", var78, var84, false, $buf));
         var78 = scala.xml.Null..MODULE$;
         var84 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(this.metricInfo(task, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Unparsed apply(TaskMetrics m) {
               String bytesWritten = Utils$.MODULE$.bytesToString(m.shuffleWriteMetrics().bytesWritten());
               long records = m.shuffleWriteMetrics().recordsWritten();
               return .MODULE$.apply((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{bytesWritten, BoxesRunTime.boxToLong(records)})));
            }
         }));
         $buf.$amp$plus(new Elem((String)null, "td", var78, var84, false, $buf));
         var10008 = $buf;
      } else {
         var10008 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      if (ApiHelper$.MODULE$.hasBytesSpilled(this.stage)) {
         NodeBuffer $buf = new NodeBuffer();
         Null var80 = scala.xml.Null..MODULE$;
         TopScope var86 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(this.formatBytes$1(task.taskMetrics().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(TaskMetrics x$22) {
               return x$22.memoryBytesSpilled();
            }
         })));
         $buf.$amp$plus(new Elem((String)null, "td", var80, var86, false, $buf));
         var80 = scala.xml.Null..MODULE$;
         var86 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(this.formatBytes$1(task.taskMetrics().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(TaskMetrics x$23) {
               return x$23.diskBytesSpilled();
            }
         })));
         $buf.$amp$plus(new Elem((String)null, "td", var80, var86, false, $buf));
         var10008 = $buf;
      } else {
         var10008 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(this.errorMessageCell((String)task.errorMessage().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })));
      $buf.$amp$plus(new Text("\n    "));
      var10000.<init>((String)null, "tr", var10004, var10005, false, $buf);
      return var10000;
   }

   private Seq<Node> accumulatorsInfo(TaskData task) {
      return (Seq)task.accumulatorUpdates().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final scala.collection.immutable.Seq<Node> apply(AccumulableInfo acc) {
            return (scala.collection.immutable.Seq<Node>)(acc.name() != null && acc.update().isDefined() ? (AbstractSeq).MODULE$.apply(StringEscapeUtils.escapeHtml4((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{acc.name(), acc.update().get()})))).$plus$plus(new Elem((String)null, "br", scala.xml.Null..MODULE$, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom()) : Nil$.MODULE$);
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   private Seq<Node> metricInfo(TaskData task, Function1<TaskMetrics, Seq<Node>> fn) {
      return (Seq)task.taskMetrics().<Node>map(fn).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      });
   }

   private Seq<Node> errorMessageCell(String error) {
      boolean isMultiline = error.indexOf(10) >= 0;
      String errorSummary = StringEscapeUtils.escapeHtml4(isMultiline ? error.substring(0, error.indexOf(10)) : error);
      Object var14;
      if (isMultiline) {
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var11 = new UnprefixedAttribute("class", new Text("expand-details"), $md);
         var11 = new UnprefixedAttribute("onclick", new Text("this.parentNode.querySelector('.stacktrace-details').classList.toggle('collapsed')"), var11);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        +details\n      "));
         Elem var10000 = new Elem((String)null, "span", var11, var10005, false, $buf);
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var13 = new UnprefixedAttribute("class", new Text("stacktrace-details collapsed"), $md);
         TopScope var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10013 = scala.xml.Null..MODULE$;
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(error);
         $buf.$amp$plus(new Elem((String)null, "pre", var10013, var10014, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         var14 = var10000.$plus$plus(new Elem((String)null, "div", var13, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else {
         var14 = "";
      }

      Object details = var14;
      Null var10004 = scala.xml.Null..MODULE$;
      TopScope var15 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(errorSummary);
      $buf.$amp$plus(details);
      return new Elem((String)null, "td", var10004, var15, false, $buf);
   }

   private final String formatDuration$1(Option value, boolean hideZero) {
      return (String)value.map(new Serializable(this, hideZero) {
         public static final long serialVersionUID = 0L;
         private final boolean hideZero$1;

         public final String apply(long v) {
            return v <= 0L && this.hideZero$1 ? "" : UIUtils$.MODULE$.formatDuration(v);
         }

         public {
            this.hideZero$1 = hideZero$1;
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
   }

   private final boolean formatDuration$default$2$1() {
      return false;
   }

   private final String formatBytes$1(Option value) {
      return Utils$.MODULE$.bytesToString(BoxesRunTime.unboxToLong(value.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      })));
   }

   public TaskPagedTable(StageData stage, String basePath, long currentTime, int pageSize, String sortColumn, boolean desc, AppStatusStore store) {
      this.stage = stage;
      this.org$apache$spark$ui$jobs$TaskPagedTable$$basePath = basePath;
      this.org$apache$spark$ui$jobs$TaskPagedTable$$pageSize = pageSize;
      this.org$apache$spark$ui$jobs$TaskPagedTable$$sortColumn = sortColumn;
      this.org$apache$spark$ui$jobs$TaskPagedTable$$desc = desc;
      PagedTable$class.$init$(this);
      this.dataSource = new TaskDataSource(stage, currentTime, pageSize, sortColumn, desc, store);
   }
}
