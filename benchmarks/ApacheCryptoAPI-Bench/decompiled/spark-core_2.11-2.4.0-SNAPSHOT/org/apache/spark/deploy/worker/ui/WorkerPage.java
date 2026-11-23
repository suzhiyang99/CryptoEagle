package org.apache.spark.deploy.worker.ui;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.ExecutorState$;
import org.apache.spark.deploy.JsonProtocol$;
import org.apache.spark.deploy.master.DriverState$;
import org.apache.spark.deploy.worker.DriverRunner;
import org.apache.spark.deploy.worker.ExecutorRunner;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import org.json4s.JsonAST;
import scala.Enumeration;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.List;
import scala.collection.immutable.StringOps;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.xml.Comment;
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
   bytes = "\u0006\u0001e4Q!\u0001\u0002\u0001\u00059\u0011!bV8sW\u0016\u0014\b+Y4f\u0015\t\u0019A!\u0001\u0002vS*\u0011QAB\u0001\u0007o>\u00148.\u001a:\u000b\u0005\u001dA\u0011A\u00023fa2|\u0017P\u0003\u0002\n\u0015\u0005)1\u000f]1sW*\u00111\u0002D\u0001\u0007CB\f7\r[3\u000b\u00035\t1a\u001c:h'\t\u0001q\u0002\u0005\u0002\u0011%5\t\u0011C\u0003\u0002\u0004\u0011%\u00111#\u0005\u0002\n/\u0016\u0014W+\u0013)bO\u0016D\u0001\"\u0006\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0007a\u0006\u0014XM\u001c;\u0004\u0001A\u0011\u0001$G\u0007\u0002\u0005%\u0011!D\u0001\u0002\f/>\u00148.\u001a:XK\n,\u0016\nC\u0003\u001d\u0001\u0011\u0005Q$\u0001\u0004=S:LGO\u0010\u000b\u0003=}\u0001\"\u0001\u0007\u0001\t\u000bUY\u0002\u0019A\f\t\u000f\u0005\u0002!\u0019!C\u0005E\u0005qqo\u001c:lKJ,e\u000e\u001a9pS:$X#A\u0012\u0011\u0005\u0011:S\"A\u0013\u000b\u0005\u0019B\u0011a\u0001:qG&\u0011\u0001&\n\u0002\u000f%B\u001cWI\u001c3q_&tGOU3g\u0011\u0019Q\u0003\u0001)A\u0005G\u0005yqo\u001c:lKJ,e\u000e\u001a9pS:$\b\u0005C\u0003-\u0001\u0011\u0005S&\u0001\u0006sK:$WM\u001d&t_:$\"A\f \u0011\u0005=ZdB\u0001\u00199\u001d\t\tdG\u0004\u00023k5\t1G\u0003\u00025-\u00051AH]8pizJ\u0011!D\u0005\u0003o1\taA[:p]R\u001a\u0018BA\u001d;\u0003\u001d\u0001\u0018mY6bO\u0016T!a\u000e\u0007\n\u0005qj$A\u0002&WC2,XM\u0003\u0002:u!)qh\u000ba\u0001\u0001\u00069!/Z9vKN$\bCA!I\u001b\u0005\u0011%BA\"E\u0003\u0011AG\u000f\u001e9\u000b\u0005\u00153\u0015aB:feZdW\r\u001e\u0006\u0002\u000f\u0006)!.\u0019<bq&\u0011\u0011J\u0011\u0002\u0013\u0011R$\boU3sm2,GOU3rk\u0016\u001cH\u000fC\u0003L\u0001\u0011\u0005A*\u0001\u0004sK:$WM\u001d\u000b\u0003\u001bv\u00032A\u0014+X\u001d\ty%K\u0004\u00023!&\t\u0011+A\u0003tG\u0006d\u0017-\u0003\u0002:'*\t\u0011+\u0003\u0002V-\n\u00191+Z9\u000b\u0005e\u001a\u0006C\u0001-\\\u001b\u0005I&B\u0001.T\u0003\rAX\u000e\\\u0005\u00039f\u0013AAT8eK\")qH\u0013a\u0001\u0001\")q\f\u0001C\u0001A\u0006YQ\r_3dkR|'OU8x)\ti\u0015\rC\u0003c=\u0002\u00071-\u0001\u0005fq\u0016\u001cW\u000f^8s!\t!W-D\u0001\u0005\u0013\t1GA\u0001\bFq\u0016\u001cW\u000f^8s%Vtg.\u001a:\t\u000b!\u0004A\u0011A5\u0002\u0013\u0011\u0014\u0018N^3s%><HcA'ki\")1n\u001aa\u0001Y\u0006Aqo\u001c:lKJLE\r\u0005\u0002nc:\u0011an\\\u0007\u0002'&\u0011\u0001oU\u0001\u0007!J,G-\u001a4\n\u0005I\u001c(AB*ue&twM\u0003\u0002q'\")Qo\u001aa\u0001m\u00061AM]5wKJ\u0004\"\u0001Z<\n\u0005a$!\u0001\u0004#sSZ,'OU;o]\u0016\u0014\b"
)
public class WorkerPage extends WebUIPage {
   private final WorkerWebUI parent;
   private final RpcEndpointRef workerEndpoint;

   private RpcEndpointRef workerEndpoint() {
      return this.workerEndpoint;
   }

   public JsonAST.JValue renderJson(HttpServletRequest request) {
      DeployMessages.WorkerStateResponse workerState = (DeployMessages.WorkerStateResponse)this.workerEndpoint().askSync(DeployMessages.RequestWorkerState$.MODULE$, ClassTag$.MODULE$.apply(DeployMessages.WorkerStateResponse.class));
      return JsonProtocol$.MODULE$.writeWorkerState(workerState);
   }

   public Seq<Node> render(HttpServletRequest request) {
      DeployMessages.WorkerStateResponse workerState = (DeployMessages.WorkerStateResponse)this.workerEndpoint().askSync(DeployMessages.RequestWorkerState$.MODULE$, ClassTag$.MODULE$.apply(DeployMessages.WorkerStateResponse.class));
      Seq executorHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ExecutorID", "Cores", "State", "Memory", "Job Details", "Logs"})));
      List runningExecutors = workerState.executors();
      Seq runningExecutorTable = UIUtils$.MODULE$.listingTable(executorHeaders, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WorkerPage $outer;

         public final Seq<Node> apply(ExecutorRunner executor) {
            return this.$outer.executorRow(executor);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, runningExecutors, UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      List finishedExecutors = workerState.finishedExecutors();
      Seq finishedExecutorTable = UIUtils$.MODULE$.listingTable(executorHeaders, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WorkerPage $outer;

         public final Seq<Node> apply(ExecutorRunner executor) {
            return this.$outer.executorRow(executor);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, finishedExecutors, UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      Seq driverHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"DriverID", "Main Class", "State", "Cores", "Memory", "Logs", "Notes"})));
      List runningDrivers = ((List)workerState.drivers().sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(DriverRunner x$1) {
            return x$1.driverId();
         }
      }, Ordering.String$.MODULE$)).reverse();
      Seq runningDriverTable = UIUtils$.MODULE$.listingTable(driverHeaders, new Serializable(this, workerState) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WorkerPage $outer;
         private final DeployMessages.WorkerStateResponse workerState$1;

         public final Seq<Node> apply(DriverRunner x$2) {
            return this.$outer.driverRow(this.workerState$1.workerId(), x$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.workerState$1 = workerState$1;
            }
         }
      }, runningDrivers, UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      List finishedDrivers = ((List)workerState.finishedDrivers().sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(DriverRunner x$3) {
            return x$3.driverId();
         }
      }, Ordering.String$.MODULE$)).reverse();
      Seq finishedDriverTable = UIUtils$.MODULE$.listingTable(driverHeaders, new Serializable(this, workerState) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WorkerPage $outer;
         private final DeployMessages.WorkerStateResponse workerState$1;

         public final Seq<Node> apply(DriverRunner x$4) {
            return this.$outer.driverRow(this.workerState$1.workerId(), x$4);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.workerState$1 = workerState$1;
            }
         }
      }, finishedDrivers, UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var64 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(new Comment(" Worker Details "));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var65 = new UnprefixedAttribute("class", new Text("span12"), $md);
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var66 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
      TopScope var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      Null var10029 = .MODULE$;
      TopScope var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      Null var10037 = .MODULE$;
      TopScope var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("ID:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(workerState.workerId());
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              Master URL:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(workerState.masterUrl());
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Cores:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(workerState.cores()));
      $buf.$amp$plus(new Text(" ("));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(workerState.coresUsed()));
      $buf.$amp$plus(new Text(" Used)"));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Memory:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)workerState.memory()));
      $buf.$amp$plus(new Text("\n              ("));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)workerState.memoryUsed()));
      $buf.$amp$plus(new Text(" Used)"));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "ul", var66, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      Null var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var67 = new UnprefixedAttribute("href", workerState.masterWebUiUrl(), $md);
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Back to Master"));
      $buf.$amp$plus(new Elem((String)null, "a", var67, var10030, false, $buf));
      $buf.$amp$plus(new Elem((String)null, "p", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var65, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "div", var64, var10006, false, $buf));
      MetaData $md = .MODULE$;
      MetaData var68 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      Elem var10001 = new Elem;
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(new Comment(" Executors and Drivers "));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var69 = new UnprefixedAttribute("class", new Text("span12"), $md);
      Elem var10009 = new Elem;
      var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var70 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-runningExecutors',\n              'aggregated-runningExecutors')"), $md);
      var70 = new UnprefixedAttribute("class", new Text("collapse-aggregated-runningExecutors collapse-table"), var70);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      MetaData $md = .MODULE$;
      MetaData var72 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var72, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n              "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Running Executors ("));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(runningExecutors.size()));
      $buf.$amp$plus(new Text(")"));
      $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "span", var70, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var73 = new UnprefixedAttribute("class", new Text("aggregated-runningExecutors collapsible-table"), $md);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(runningExecutorTable);
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var73, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      Object var88;
      if (runningDrivers.nonEmpty()) {
         MetaData $md = .MODULE$;
         MetaData var74 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-runningDrivers',\n                  'aggregated-runningDrivers')"), $md);
         var74 = new UnprefixedAttribute("class", new Text("collapse-aggregated-runningDrivers collapse-table"), var74);
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                  "));
         MetaData $md = .MODULE$;
         MetaData var76 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var76, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                  "));
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Running Drivers ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(runningDrivers.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         Elem var10017 = new Elem((String)null, "span", var74, var10022, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var77 = new UnprefixedAttribute("class", new Text("aggregated-runningDrivers collapsible-table"), $md);
         TopScope var10023 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(runningDriverTable);
         $buf.$amp$plus(new Text("\n              "));
         var88 = var10017.$plus$plus(new Elem((String)null, "div", var77, var10023, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else {
         var88 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var88);
      $buf.$amp$plus(new Text("\n          "));
      if (finishedExecutors.nonEmpty()) {
         MetaData $md = .MODULE$;
         MetaData var78 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-finishedExecutors',\n                  'aggregated-finishedExecutors')"), $md);
         var78 = new UnprefixedAttribute("class", new Text("collapse-aggregated-finishedExecutors collapse-table"), var78);
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                  "));
         MetaData $md = .MODULE$;
         MetaData var80 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var80, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                  "));
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Finished Executors ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(finishedExecutors.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         Elem var89 = new Elem((String)null, "span", var78, var10022, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var81 = new UnprefixedAttribute("class", new Text("aggregated-finishedExecutors collapsible-table"), $md);
         TopScope var99 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(finishedExecutorTable);
         $buf.$amp$plus(new Text("\n              "));
         var88 = var89.$plus$plus(new Elem((String)null, "div", var81, var99, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else {
         var88 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var88);
      $buf.$amp$plus(new Text("\n          "));
      if (finishedDrivers.nonEmpty()) {
         MetaData $md = .MODULE$;
         MetaData var82 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-finishedDrivers',\n                  'aggregated-finishedDrivers')"), $md);
         var82 = new UnprefixedAttribute("class", new Text("collapse-aggregated-finishedDrivers collapse-table"), var82);
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                  "));
         MetaData $md = .MODULE$;
         MetaData var84 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var84, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                  "));
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Finished Drivers ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(finishedDrivers.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         Elem var91 = new Elem((String)null, "span", var82, var10022, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var85 = new UnprefixedAttribute("class", new Text("aggregated-finishedDrivers collapsible-table"), $md);
         TopScope var100 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(finishedDriverTable);
         $buf.$amp$plus(new Text("\n              "));
         var88 = var91.$plus$plus(new Elem((String)null, "div", var85, var100, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else {
         var88 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var88);
      $buf.$amp$plus(new Text("\n        "));
      var10009.<init>((String)null, "div", var69, var10014, false, $buf);
      $buf.$amp$plus(var10009);
      $buf.$amp$plus(new Text("\n      "));
      var10001.<init>((String)null, "div", var68, var10006, false, $buf);
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
      }, (new StringOps(Predef$.MODULE$.augmentString("Spark Worker at %s:%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{workerState.host(), BoxesRunTime.boxToInteger(workerState.port())})), UIUtils$.MODULE$.basicSparkPage$default$3());
   }

   public Seq<Node> executorRow(ExecutorRunner executor) {
      String workerUrlRef;
      NodeBuffer $buf;
      NodeBuffer $buf;
      NodeBuffer $buf;
      NodeBuffer $buf;
      UnprefixedAttribute var28;
      Null var34;
      TopScope var39;
      Null var43;
      TopScope var45;
      Object var47;
      Elem var10000;
      Null var10004;
      TopScope var10005;
      Elem var10008;
      Elem var10016;
      TopScope var10021;
      Elem var10024;
      label19: {
         label18: {
            workerUrlRef = UIUtils$.MODULE$.makeHref(this.parent.worker().reverseProxy(), executor.workerId(), this.parent.webUrl());
            String appUrlRef = UIUtils$.MODULE$.makeHref(this.parent.worker().reverseProxy(), executor.appId(), executor.appDesc().appUiUrl());
            var10000 = new Elem;
            var10004 = .MODULE$;
            var10005 = scala.xml.TopScope..MODULE$;
            $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n      "));
            var34 = .MODULE$;
            var39 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(BoxesRunTime.boxToInteger(executor.execId()));
            $buf.$amp$plus(new Elem((String)null, "td", var34, var39, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            var34 = .MODULE$;
            var39 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(BoxesRunTime.boxToInteger(executor.cores()));
            $buf.$amp$plus(new Elem((String)null, "td", var34, var39, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            var34 = .MODULE$;
            var39 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(executor.state());
            $buf.$amp$plus(new Elem((String)null, "td", var34, var39, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            MetaData $md = .MODULE$;
            MetaData var27 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToInteger(executor.memory()).toString(), $md);
            var39 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        "));
            $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)executor.memory()));
            $buf.$amp$plus(new Text("\n      "));
            $buf.$amp$plus(new Elem((String)null, "td", var27, var39, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            var10008 = new Elem;
            var34 = .MODULE$;
            var39 = scala.xml.TopScope..MODULE$;
            $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        "));
            MetaData $md = .MODULE$;
            var28 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
            var10016 = new Elem;
            var10021 = scala.xml.TopScope..MODULE$;
            $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            var43 = .MODULE$;
            var45 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            Null var10036 = .MODULE$;
            TopScope var10037 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("ID:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var10036, var10037, false, $buf));
            $buf.$amp$plus(new Text(" "));
            $buf.$amp$plus(executor.appId());
            $buf.$amp$plus(new Elem((String)null, "li", var43, var45, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            var10024 = new Elem;
            var43 = .MODULE$;
            var45 = scala.xml.TopScope..MODULE$;
            $buf = new NodeBuffer();
            var10036 = .MODULE$;
            var10037 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Name:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var10036, var10037, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            Enumeration.Value var10032 = executor.state();
            Enumeration.Value var17 = ExecutorState$.MODULE$.RUNNING();
            if (var10032 == null) {
               if (var17 != null) {
                  break label18;
               }
            } else if (!var10032.equals(var17)) {
               break label18;
            }

            if ((new StringOps(Predef$.MODULE$.augmentString(executor.appDesc().appUiUrl()))).nonEmpty()) {
               MetaData $md = .MODULE$;
               MetaData var29 = new UnprefixedAttribute("href", appUrlRef, $md);
               var10037 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text(" "));
               $buf.$amp$plus(executor.appDesc().name());
               var47 = new Elem((String)null, "a", var29, var10037, false, $buf);
               break label19;
            }
         }

         var47 = executor.appDesc().name();
      }

      $buf.$amp$plus(var47);
      $buf.$amp$plus(new Text("\n          "));
      var10024.<init>((String)null, "li", var43, var45, false, $buf);
      $buf.$amp$plus(var10024);
      $buf.$amp$plus(new Text("\n          "));
      var43 = .MODULE$;
      var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      Null var49 = .MODULE$;
      TopScope var52 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("User:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var49, var52, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(executor.appDesc().user());
      $buf.$amp$plus(new Elem((String)null, "li", var43, var45, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10016.<init>((String)null, "ul", var28, var10021, false, $buf);
      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n      "));
      var10008.<init>((String)null, "td", var34, var39, false, $buf);
      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      var34 = .MODULE$;
      var39 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var30 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/logPage?appId=", "&executorId=", "&logType=stdout"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{workerUrlRef, executor.appId(), BoxesRunTime.boxToInteger(executor.execId())})), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("stdout"));
      $buf.$amp$plus(new Elem((String)null, "a", var30, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var31 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/logPage?appId=", "&executorId=", "&logType=stderr"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{workerUrlRef, executor.appId(), BoxesRunTime.boxToInteger(executor.execId())})), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("stderr"));
      $buf.$amp$plus(new Elem((String)null, "a", var31, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var34, var39, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      var10000.<init>((String)null, "tr", var10004, var10005, false, $buf);
      return var10000;
   }

   public Seq<Node> driverRow(String workerId, DriverRunner driver) {
      String workerUrlRef = UIUtils$.MODULE$.makeHref(this.parent.worker().reverseProxy(), workerId, this.parent.webUrl());
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(driver.driverId());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(driver.driverDesc().command().arguments().apply(2));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(driver.finalState().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Enumeration.Value apply() {
            return DriverState$.MODULE$.RUNNING();
         }
      }));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var18 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToInteger(driver.driverDesc().cores()).toString(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(driver.driverDesc().cores()).toString());
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var18, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var19 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToInteger(driver.driverDesc().mem()).toString(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)driver.driverDesc().mem()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var19, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var20 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/logPage?driverId=", "&logType=stdout"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{workerUrlRef, driver.driverId()})), $md);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("stdout"));
      $buf.$amp$plus(new Elem((String)null, "a", var20, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var21 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/logPage?driverId=", "&logType=stderr"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{workerUrlRef, driver.driverId()})), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("stderr"));
      $buf.$amp$plus(new Elem((String)null, "a", var21, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(driver.finalException().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      }));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public WorkerPage(WorkerWebUI parent) {
      super("");
      this.parent = parent;
      this.workerEndpoint = parent.worker().self();
   }
}
