package org.apache.spark.ui.exec;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SparkContext;
import org.apache.spark.ui.SparkUITab;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.ThreadStackTrace;
import scala.Array$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;

@ScalaSignature(
   bytes = "\u0006\u0001-3Q!\u0001\u0002\u0001\t1\u0011a#\u0012=fGV$xN\u001d+ie\u0016\fG\rR;naB\u000bw-\u001a\u0006\u0003\u0007\u0011\tA!\u001a=fG*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\b\u000e\u0003\u0011I!\u0001\u0005\u0003\u0003\u0013]+'-V%QC\u001e,\u0007\u0002\u0003\n\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000b\u0002\rA\f'/\u001a8u\u0007\u0001\u0001\"AD\u000b\n\u0005Y!!AC*qCJ\\W+\u0013+bE\"A\u0001\u0004\u0001B\u0001B\u0003%\u0011$\u0001\u0002tGB\u0019!$H\u0010\u000e\u0003mQ\u0011\u0001H\u0001\u0006g\u000e\fG.Y\u0005\u0003=m\u0011aa\u00149uS>t\u0007C\u0001\u0011\"\u001b\u00051\u0011B\u0001\u0012\u0007\u00051\u0019\u0006/\u0019:l\u0007>tG/\u001a=u\u0011\u0015!\u0003\u0001\"\u0001&\u0003\u0019a\u0014N\\5u}Q\u0019a\u0005K\u0015\u0011\u0005\u001d\u0002Q\"\u0001\u0002\t\u000bI\u0019\u0003\u0019\u0001\u000b\t\u000ba\u0019\u0003\u0019A\r\t\u000b-\u0002A\u0011\u0001\u0017\u0002\rI,g\u000eZ3s)\tis\bE\u0002/mer!a\f\u001b\u000f\u0005A\u001aT\"A\u0019\u000b\u0005I\u001a\u0012A\u0002\u001fs_>$h(C\u0001\u001d\u0013\t)4$A\u0004qC\u000e\\\u0017mZ3\n\u0005]B$aA*fc*\u0011Qg\u0007\t\u0003uuj\u0011a\u000f\u0006\u0003ym\t1\u0001_7m\u0013\tq4H\u0001\u0003O_\u0012,\u0007\"\u0002!+\u0001\u0004\t\u0015a\u0002:fcV,7\u000f\u001e\t\u0003\u0005&k\u0011a\u0011\u0006\u0003\t\u0016\u000bA\u0001\u001b;ua*\u0011aiR\u0001\bg\u0016\u0014h\u000f\\3u\u0015\u0005A\u0015!\u00026bm\u0006D\u0018B\u0001&D\u0005IAE\u000f\u001e9TKJ4H.\u001a;SKF,Xm\u001d;"
)
public class ExecutorThreadDumpPage extends WebUIPage {
   private final SparkUITab parent;
   private final Option<SparkContext> sc;

   public Seq<Node> render(HttpServletRequest request) {
      String executorId = (String)Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("executorId"))).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String executorId) {
            return UIUtils$.MODULE$.decodeURLParameter(executorId);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply() {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Missing executorId parameter"})))).s(Nil$.MODULE$));
         }
      });
      long time = System.currentTimeMillis();
      Option maybeThreadDump = (this.sc.get()).getExecutorThreadDump(executorId);
      Node content = (Node)maybeThreadDump.map(new Serializable(this, time) {
         public static final long serialVersionUID = 0L;
         private final long time$1;

         public final Elem apply(ThreadStackTrace[] threadDump) {
            Elem[] dumpRows = (Elem[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(threadDump).sortWith(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(ThreadStackTrace x0$1, ThreadStackTrace x1$1) {
                  Tuple2 var3 = new Tuple2(x0$1, x1$1);
                  if (var3 != null) {
                     ThreadStackTrace threadTrace1 = (ThreadStackTrace)var3._1();
                     ThreadStackTrace threadTrace2 = (ThreadStackTrace)var3._2();
                     int v1 = threadTrace1.threadName().contains("Executor task launch") ? 1 : 0;
                     int v2 = threadTrace2.threadName().contains("Executor task launch") ? 1 : 0;
                     boolean var6 = v1 == v2 ? (new StringOps(Predef$.MODULE$.augmentString(threadTrace1.threadName().toLowerCase(Locale.ROOT)))).$less(threadTrace2.threadName().toLowerCase(Locale.ROOT)) : v1 > v2;
                     return var6;
                  } else {
                     throw new MatchError(var3);
                  }
               }
            })).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Elem apply(ThreadStackTrace thread) {
                  long threadId = thread.threadId();
                  Option var5 = thread.blockedByThreadId();
                  Object blockedBy;
                  if (var5 instanceof Some) {
                     Null var10004 = .MODULE$;
                     TopScope var10005 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(new Text("\n              Blocked by "));
                     MetaData $md = .MODULE$;
                     MetaData var23 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"#", "_td_id"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{thread.blockedByThreadId()})), $md);
                     TopScope var10013 = scala.xml.TopScope..MODULE$;
                     NodeBuffer $buf = new NodeBuffer();
                     $buf.$amp$plus(new Text("\n              Thread "));
                     $buf.$amp$plus(thread.blockedByThreadId());
                     $buf.$amp$plus(new Text(" "));
                     $buf.$amp$plus(thread.blockedByLock());
                     $buf.$amp$plus(new Elem((String)null, "a", var23, var10013, false, $buf));
                     $buf.$amp$plus(new Text("\n            "));
                     blockedBy = new Elem((String)null, "div", var10004, var10005, false, $buf);
                  } else {
                     if (!None$.MODULE$.equals(var5)) {
                        throw new MatchError(var5);
                     }

                     blockedBy = scala.xml.Text..MODULE$.apply("");
                  }

                  String heldLocks = thread.holdingLocks().mkString(", ");
                  MetaData $md = .MODULE$;
                  MetaData var24 = new UnprefixedAttribute("onmouseout", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"onMouseOverAndOut(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), $md);
                  var24 = new UnprefixedAttribute("onmouseover", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"onMouseOverAndOut(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), var24);
                  var24 = new UnprefixedAttribute("onclick", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"toggleThreadStackTrace(", ", false)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), var24);
                  var24 = new UnprefixedAttribute("class", new Text("accordion-heading"), var24);
                  var24 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"thread_", "_tr"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), var24);
                  TopScope var35 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n          "));
                  MetaData $md = .MODULE$;
                  MetaData var29 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "_td_id"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), $md);
                  TopScope var36 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(BoxesRunTime.boxToLong(threadId));
                  $buf.$amp$plus(new Elem((String)null, "td", var29, var36, false, $buf));
                  $buf.$amp$plus(new Text("\n          "));
                  MetaData $md = .MODULE$;
                  MetaData var30 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "_td_name"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), $md);
                  var36 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(thread.threadName());
                  $buf.$amp$plus(new Elem((String)null, "td", var30, var36, false, $buf));
                  $buf.$amp$plus(new Text("\n          "));
                  MetaData $md = .MODULE$;
                  MetaData var31 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "_td_state"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), $md);
                  var36 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(thread.threadState());
                  $buf.$amp$plus(new Elem((String)null, "td", var31, var36, false, $buf));
                  $buf.$amp$plus(new Text("\n          "));
                  MetaData $md = .MODULE$;
                  MetaData var32 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "_td_locking"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), $md);
                  var36 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(blockedBy);
                  $buf.$amp$plus(heldLocks);
                  $buf.$amp$plus(new Elem((String)null, "td", var32, var36, false, $buf));
                  $buf.$amp$plus(new Text("\n          "));
                  MetaData $md = .MODULE$;
                  MetaData var33 = new UnprefixedAttribute("class", new Text("hidden"), $md);
                  var33 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "_td_stacktrace"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(threadId)})), var33);
                  var36 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(thread.stackTrace());
                  $buf.$amp$plus(new Elem((String)null, "td", var33, var36, false, $buf));
                  $buf.$amp$plus(new Text("\n        "));
                  return new Elem((String)null, "tr", var24, var35, false, $buf);
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Elem.class)));
            MetaData $md = .MODULE$;
            MetaData var34 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n      "));
            Null var10012 = .MODULE$;
            TopScope var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Updated at "));
            $buf.$amp$plus(UIUtils$.MODULE$.formatDate(this.time$1));
            $buf.$amp$plus(new Elem((String)null, "p", var10012, var10013, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            NodeBuffer $buf = new NodeBuffer();
            Null var53 = .MODULE$;
            TopScope var10014 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            MetaData $md = .MODULE$;
            MetaData var35 = new UnprefixedAttribute("onClick", new Text("expandAllThreadStackTrace(true)"), $md);
            var35 = new UnprefixedAttribute("class", new Text("expandbutton"), var35);
            TopScope var10022 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          Expand All\n        "));
            $buf.$amp$plus(new Elem((String)null, "a", var35, var10022, false, $buf));
            $buf.$amp$plus(new Elem((String)null, "p", var53, var10014, false, $buf));
            var53 = .MODULE$;
            var10014 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            MetaData $md = .MODULE$;
            MetaData var37 = new UnprefixedAttribute("onClick", new Text("collapseAllThreadStackTrace(true)"), $md);
            var37 = new UnprefixedAttribute("class", new Text("expandbutton hidden"), var37);
            var10022 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          Collapse All\n        "));
            $buf.$amp$plus(new Elem((String)null, "a", var37, var10022, false, $buf));
            $buf.$amp$plus(new Elem((String)null, "p", var53, var10014, false, $buf));
            MetaData $md = .MODULE$;
            MetaData var39 = new UnprefixedAttribute("class", new Text("form-inline"), $md);
            var10014 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        "));
            MetaData $md = .MODULE$;
            MetaData var40 = new UnprefixedAttribute("data-example-id", new Text("simple-form-inline"), $md);
            var40 = new UnprefixedAttribute("class", new Text("bs-example"), var40);
            var10022 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            MetaData $md = .MODULE$;
            MetaData var42 = new UnprefixedAttribute("class", new Text("form-group"), $md);
            TopScope var10030 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = .MODULE$;
            MetaData var43 = new UnprefixedAttribute("class", new Text("input-group"), $md);
            TopScope var10038 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              Search: "));
            MetaData $md = .MODULE$;
            MetaData var44 = new UnprefixedAttribute("oninput", new Text("onSearchStringChange()"), $md);
            var44 = new UnprefixedAttribute("id", new Text("search"), var44);
            var44 = new UnprefixedAttribute("class", new Text("form-control"), var44);
            var44 = new UnprefixedAttribute("type", new Text("text"), var44);
            $buf.$amp$plus(new Elem((String)null, "input", var44, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n            "));
            $buf.$amp$plus(new Elem((String)null, "div", var43, var10038, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "div", var42, var10030, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            $buf.$amp$plus(new Elem((String)null, "div", var40, var10022, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            $buf.$amp$plus(new Elem((String)null, "div", var39, var10014, false, $buf));
            $buf.$amp$plus(new Elem((String)null, "p", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus($buf);
            $buf.$amp$plus(new Text("\n      "));
            MetaData $md = .MODULE$;
            MetaData var48 = new UnprefixedAttribute("class", (new StringBuilder()).append((Object)UIUtils$.MODULE$.TABLE_CLASS_STRIPED()).append((Object)" accordion-group").append((Object)" sortable").toString(), $md);
            TopScope var55 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        "));
            Null var10020 = .MODULE$;
            TopScope var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            MetaData $md = .MODULE$;
            MetaData var49 = new UnprefixedAttribute("onClick", new Text("collapseAllThreadStackTrace(false)"), $md);
            TopScope var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Thread ID"));
            $buf.$amp$plus(new Elem((String)null, "th", var49, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            MetaData $md = .MODULE$;
            MetaData var50 = new UnprefixedAttribute("onClick", new Text("collapseAllThreadStackTrace(false)"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Thread Name"));
            $buf.$amp$plus(new Elem((String)null, "th", var50, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            MetaData $md = .MODULE$;
            MetaData var51 = new UnprefixedAttribute("onClick", new Text("collapseAllThreadStackTrace(false)"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Thread State"));
            $buf.$amp$plus(new Elem((String)null, "th", var51, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            MetaData $md = .MODULE$;
            MetaData var52 = new UnprefixedAttribute("onClick", new Text("collapseAllThreadStackTrace(false)"), $md);
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Thread Locks"));
            $buf.$amp$plus(new Elem((String)null, "th", var52, var10029, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            $buf.$amp$plus(new Elem((String)null, "thead", var10020, var10021, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            var10020 = .MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(dumpRows);
            $buf.$amp$plus(new Elem((String)null, "tbody", var10020, var10021, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            $buf.$amp$plus(new Elem((String)null, "table", var48, var55, false, $buf));
            $buf.$amp$plus(new Text("\n    "));
            return new Elem((String)null, "div", var34, var10005, false, $buf);
         }

         public {
            this.time$1 = time$1;
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Text apply() {
            return scala.xml.Text..MODULE$.apply("Error fetching thread dump");
         }
      });
      return UIUtils$.MODULE$.headerSparkPage((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Thread dump for executor ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{executorId})), new Serializable(this, content) {
         public static final long serialVersionUID = 0L;
         private final Node content$1;

         public final Node apply() {
            return this.content$1;
         }

         public {
            this.content$1 = content$1;
         }
      }, this.parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
   }

   public ExecutorThreadDumpPage(SparkUITab parent, Option<SparkContext> sc) {
      super("threadDump");
      this.parent = parent;
      this.sc = sc;
   }
}
