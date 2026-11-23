package org.apache.spark.deploy.history;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import scala.MatchError;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.collection.immutable.Iterable$;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
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
   bytes = "\u0006\u0001\u00054Q!\u0001\u0002\u0001\u00051\u00111\u0002S5ti>\u0014\u0018\u0010U1hK*\u00111\u0001B\u0001\bQ&\u001cHo\u001c:z\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0003!\u0019\t!!^5\n\u0005Iy!!C,fEVK\u0005+Y4f\u0011!!\u0002A!A!\u0002\u00131\u0012A\u00029be\u0016tGo\u0001\u0001\u0011\u0005]AR\"\u0001\u0002\n\u0005e\u0011!!\u0004%jgR|'/_*feZ,'\u000fC\u0003\u001c\u0001\u0011\u0005A$\u0001\u0004=S:LGO\u0010\u000b\u0003;y\u0001\"a\u0006\u0001\t\u000bQQ\u0002\u0019\u0001\f\t\u000b\u0001\u0002A\u0011A\u0011\u0002\rI,g\u000eZ3s)\t\u0011c\u0007E\u0002$[Ar!\u0001\n\u0016\u000f\u0005\u0015BS\"\u0001\u0014\u000b\u0005\u001d*\u0012A\u0002\u001fs_>$h(C\u0001*\u0003\u0015\u00198-\u00197b\u0013\tYC&A\u0004qC\u000e\\\u0017mZ3\u000b\u0003%J!AL\u0018\u0003\u0007M+\u0017O\u0003\u0002,YA\u0011\u0011\u0007N\u0007\u0002e)\u00111\u0007L\u0001\u0004q6d\u0017BA\u001b3\u0005\u0011qu\u000eZ3\t\u000b]z\u0002\u0019\u0001\u001d\u0002\u000fI,\u0017/^3tiB\u0011\u0011\bQ\u0007\u0002u)\u00111\bP\u0001\u0005QR$\bO\u0003\u0002>}\u000591/\u001a:wY\u0016$(\"A \u0002\u000b)\fg/\u0019=\n\u0005\u0005S$A\u0005%uiB\u001cVM\u001d<mKR\u0014V-];fgRDQa\u0011\u0001\u0005\n\u0011\u000bA\"\\1lKB\u000bw-\u001a'j].$\"!R'\u0011\u0005\u0019SeBA$I\u001b\u0005a\u0013BA%-\u0003\u0019\u0001&/\u001a3fM&\u00111\n\u0014\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005%c\u0003\"\u0002(C\u0001\u0004y\u0015AD:i_^LenY8na2,G/\u001a\t\u0003\u000fBK!!\u0015\u0017\u0003\u000f\t{w\u000e\\3b]\")1\u000b\u0001C\u0005)\u00061\u0012n]!qa2L7-\u0019;j_:\u001cu.\u001c9mKR,G\r\u0006\u0002P+\")aK\u0015a\u0001/\u00069\u0011\r\u001d9J]\u001a|\u0007C\u0001-`\u001b\u0005I&B\u0001.\\\u0003\t1\u0018G\u0003\u0002];\u0006\u0019\u0011\r]5\u000b\u0005y3\u0011AB:uCR,8/\u0003\u0002a3\ny\u0011\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000e"
)
public class HistoryPage extends WebUIPage {
   private final HistoryServer parent;

   public Seq<Node> render(HttpServletRequest request) {
      boolean requestedIncomplete = (new StringOps(Predef$.MODULE$.augmentString((String)Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("showIncomplete"))).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "false";
         }
      })))).toBoolean();
      int allAppsSize = this.parent.getApplicationList().count(new Serializable(this, requestedIncomplete) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HistoryPage $outer;
         private final boolean requestedIncomplete$1;

         public final boolean apply(ApplicationInfo x$1) {
            return this.$outer.org$apache$spark$deploy$history$HistoryPage$$isApplicationCompleted(x$1) != this.requestedIncomplete$1;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.requestedIncomplete$1 = requestedIncomplete$1;
            }
         }
      });
      int eventLogsUnderProcessCount = this.parent.getEventLogsUnderProcess();
      long lastUpdatedTime = this.parent.getLastUpdatedTime();
      Map providerConfig = this.parent.getProviderConfig();
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var32 = new UnprefixedAttribute("src", UIUtils$.MODULE$.prependBaseUri("/static/historypage-common.js", UIUtils$.MODULE$.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var32, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      Elem var10001 = new Elem;
      Null var10005 = .MODULE$;
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var33 = new UnprefixedAttribute("class", new Text("container-fluid"), $md);
      Elem var10009 = new Elem;
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var34 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
      TopScope var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(providerConfig.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Elem apply(Tuple2<String, String> x0$1) {
            if (x0$1 != null) {
               String k = (String)x0$1._1();
               String v = (String)x0$1._2();
               Null var10004 = .MODULE$;
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               Null var10012 = .MODULE$;
               TopScope var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(k);
               $buf.$amp$plus(new Text(":"));
               $buf.$amp$plus(new Elem((String)null, "strong", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text(" "));
               $buf.$amp$plus(v);
               Elem var5 = new Elem((String)null, "li", var10004, var10005, false, $buf);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Iterable$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "ul", var34, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      Object var10017;
      if (eventLogsUnderProcessCount > 0) {
         Null var10021 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("There are "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(eventLogsUnderProcessCount));
         $buf.$amp$plus(new Text(" event log(s) currently being\n                processed which may result in additional applications getting listed on this page.\n                Refresh the page to view updates. "));
         var10017 = new Elem((String)null, "p", var10021, var10022, false, $buf);
      } else {
         var10017 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n\n            "));
      if (lastUpdatedTime > 0L) {
         Null var49 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Last updated: "));
         MetaData $md = .MODULE$;
         MetaData var35 = new UnprefixedAttribute("id", new Text("last-updated"), $md);
         TopScope var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(BoxesRunTime.boxToLong(lastUpdatedTime));
         $buf.$amp$plus(new Elem((String)null, "span", var35, var10030, false, $buf));
         var10017 = new Elem((String)null, "p", var49, var10022, false, $buf);
      } else {
         var10017 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n\n            "));
      Null var50 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Client local time zone: "));
      MetaData $md = .MODULE$;
      MetaData var36 = new UnprefixedAttribute("id", new Text("time-zone"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var36, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Elem((String)null, "p", var50, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n\n            "));
      if (allAppsSize > 0) {
         MetaData $md = .MODULE$;
         MetaData var37 = new UnprefixedAttribute("src", UIUtils$.MODULE$.prependBaseUri("/static/dataTables.rowsGroup.js", UIUtils$.MODULE$.prependBaseUri$default$2()), $md);
         Elem var44 = new Elem((String)null, "script", var37, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0])));
         MetaData $md = .MODULE$;
         MetaData var38 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
         var38 = new UnprefixedAttribute("id", new Text("history-summary"), var38);
         TraversableLike var45 = (TraversableLike)var44.$plus$plus(new Elem((String)null, "div", var38, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         MetaData $md = .MODULE$;
         MetaData var40 = new UnprefixedAttribute("src", UIUtils$.MODULE$.prependBaseUri("/static/utils.js", UIUtils$.MODULE$.prependBaseUri$default$2()), $md);
         var45 = (TraversableLike)var45.$plus$plus(new Elem((String)null, "script", var40, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         MetaData $md = .MODULE$;
         MetaData var41 = new UnprefixedAttribute("src", UIUtils$.MODULE$.prependBaseUri("/static/historypage.js", UIUtils$.MODULE$.prependBaseUri$default$2()), $md);
         var45 = (TraversableLike)var45.$plus$plus(new Elem((String)null, "script", var41, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         Null var57 = .MODULE$;
         TopScope var10023 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("setAppLimit("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(this.parent.maxApplications()));
         $buf.$amp$plus(new Text(")"));
         var10017 = var45.$plus$plus(new Elem((String)null, "script", var57, var10023, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else if (requestedIncomplete) {
         var50 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("No incomplete applications found!"));
         var10017 = new Elem((String)null, "h4", var50, var10022, false, $buf);
      } else if (eventLogsUnderProcessCount > 0) {
         var50 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("No completed applications found!"));
         var10017 = new Elem((String)null, "h4", var50, var10022, false, $buf);
      } else {
         var50 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("No completed applications found!"));
         var10017 = (new Elem((String)null, "h4", var50, var10022, false, $buf)).$plus$plus(this.parent.emptyListingHtml(), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n\n            "));
      MetaData $md = .MODULE$;
      MetaData var42 = new UnprefixedAttribute("href", this.makePageLink(!requestedIncomplete), $md);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(requestedIncomplete ? "Back to completed applications" : "Show incomplete applications");
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "a", var42, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      var10009.<init>((String)null, "div", var33, var10014, false, $buf);
      $buf.$amp$plus(var10009);
      $buf.$amp$plus(new Text("\n      "));
      var10001.<init>((String)null, "div", var10005, var10006, false, $buf);
      $buf.$amp$plus(var10001);
      return UIUtils$.MODULE$.basicSparkPage(new Serializable(this, $buf) {
         public static final long serialVersionUID = 0L;
         private final NodeBuffer content$1;

         public final NodeBuffer apply() {
            return this.content$1;
         }

         public {
            this.content$1 = content$1;
         }
      }, "History Server", true);
   }

   private String makePageLink(boolean showIncomplete) {
      return UIUtils$.MODULE$.prependBaseUri((new StringBuilder()).append((Object)"/?showIncomplete=").append((Object)BoxesRunTime.boxToBoolean(showIncomplete)).toString(), UIUtils$.MODULE$.prependBaseUri$default$2());
   }

   public boolean org$apache$spark$deploy$history$HistoryPage$$isApplicationCompleted(ApplicationInfo appInfo) {
      return appInfo.attempts().nonEmpty() && ((ApplicationAttemptInfo)appInfo.attempts().head()).completed();
   }

   public HistoryPage(HistoryServer parent) {
      super("");
      this.parent = parent;
   }
}
