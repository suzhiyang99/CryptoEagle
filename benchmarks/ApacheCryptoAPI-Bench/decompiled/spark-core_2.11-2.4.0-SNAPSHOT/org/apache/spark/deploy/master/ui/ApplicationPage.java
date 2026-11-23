package org.apache.spark.deploy.master.ui;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.ExecutorState$;
import org.apache.spark.deploy.master.ApplicationInfo;
import org.apache.spark.deploy.master.ExecutorDesc;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.ui.ToolTips$;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import scala.Enumeration;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
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
   bytes = "\u0006\u0001Y3Q!\u0001\u0002\u0001\u00059\u0011q\"\u00119qY&\u001c\u0017\r^5p]B\u000bw-\u001a\u0006\u0003\u0007\u0011\t!!^5\u000b\u0005\u00151\u0011AB7bgR,'O\u0003\u0002\b\u0011\u00051A-\u001a9m_fT!!\u0003\u0006\u0002\u000bM\u0004\u0018M]6\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\n\u0003\u0001=\u0001\"\u0001\u0005\n\u000e\u0003EQ!a\u0001\u0005\n\u0005M\t\"!C,fEVK\u0005+Y4f\u0011!)\u0002A!A!\u0002\u00139\u0012A\u00029be\u0016tGo\u0001\u0001\u0011\u0005aIR\"\u0001\u0002\n\u0005i\u0011!aC'bgR,'oV3c+&CQ\u0001\b\u0001\u0005\u0002u\ta\u0001P5oSRtDC\u0001\u0010 !\tA\u0002\u0001C\u0003\u00167\u0001\u0007q\u0003C\u0004\u0006\u0001\t\u0007I\u0011B\u0011\u0016\u0003\t\u0002\"a\t\u0014\u000e\u0003\u0011R!!\n\u0005\u0002\u0007I\u00048-\u0003\u0002(I\tq!\u000b]2F]\u0012\u0004x.\u001b8u%\u00164\u0007BB\u0015\u0001A\u0003%!%A\u0004nCN$XM\u001d\u0011\t\u000b-\u0002A\u0011\u0001\u0017\u0002\rI,g\u000eZ3s)\ti\u0013\tE\u0002/qmr!aL\u001b\u000f\u0005A\u001aT\"A\u0019\u000b\u0005I2\u0012A\u0002\u001fs_>$h(C\u00015\u0003\u0015\u00198-\u00197b\u0013\t1t'A\u0004qC\u000e\\\u0017mZ3\u000b\u0003QJ!!\u000f\u001e\u0003\u0007M+\u0017O\u0003\u00027oA\u0011AhP\u0007\u0002{)\u0011ahN\u0001\u0004q6d\u0017B\u0001!>\u0005\u0011qu\u000eZ3\t\u000b\tS\u0003\u0019A\"\u0002\u000fI,\u0017/^3tiB\u0011AiS\u0007\u0002\u000b*\u0011aiR\u0001\u0005QR$\bO\u0003\u0002I\u0013\u000691/\u001a:wY\u0016$(\"\u0001&\u0002\u000b)\fg/\u0019=\n\u00051+%A\u0005%uiB\u001cVM\u001d<mKR\u0014V-];fgRDQA\u0014\u0001\u0005\n=\u000b1\"\u001a=fGV$xN\u001d*poR\u0011Q\u0006\u0015\u0005\u0006#6\u0003\rAU\u0001\tKb,7-\u001e;peB\u00111\u000bV\u0007\u0002\t%\u0011Q\u000b\u0002\u0002\r\u000bb,7-\u001e;pe\u0012+7o\u0019"
)
public class ApplicationPage extends WebUIPage {
   private final MasterWebUI parent;
   private final RpcEndpointRef master;

   private RpcEndpointRef master() {
      return this.master;
   }

   public Seq<Node> render(HttpServletRequest request) {
      String appId = UIUtils$.MODULE$.stripXSS(request.getParameter("appId"));
      DeployMessages.MasterStateResponse state = (DeployMessages.MasterStateResponse)this.master().askSync(DeployMessages.RequestMasterState$.MODULE$, ClassTag$.MODULE$.apply(DeployMessages.MasterStateResponse.class));
      ApplicationInfo app = (ApplicationInfo)Predef$.MODULE$.refArrayOps(state.activeApps()).find(new Serializable(this, appId) {
         public static final long serialVersionUID = 0L;
         private final String appId$1;

         public final boolean apply(ApplicationInfo x$1) {
            boolean var3;
            label23: {
               String var10000 = x$1.id();
               String var2 = this.appId$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }

         public {
            this.appId$1 = appId$1;
         }
      }).getOrElse(new Serializable(this, appId, state) {
         public static final long serialVersionUID = 0L;
         public final String appId$1;
         private final DeployMessages.MasterStateResponse state$1;

         public final ApplicationInfo apply() {
            return (ApplicationInfo)Predef$.MODULE$.refArrayOps(this.state$1.completedApps()).find(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(ApplicationInfo x$2) {
                  boolean var3;
                  label23: {
                     String var10000 = x$2.id();
                     String var2 = this.$outer.appId$1;
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label23;
                        }
                     } else if (var10000.equals(var2)) {
                        break label23;
                     }

                     var3 = false;
                     return var3;
                  }

                  var3 = true;
                  return var3;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }).orNull(Predef$.MODULE$.$conforms());
         }

         public {
            this.appId$1 = appId$1;
            this.state$1 = state$1;
         }
      });
      if (app == null) {
         MetaData $md = .MODULE$;
         MetaData var62 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("No running application with ID "));
         $buf.$amp$plus(appId);
         Elem msg = new Elem((String)null, "div", var62, var10005, false, $buf);
         return UIUtils$.MODULE$.basicSparkPage(new Serializable(this, msg) {
            public static final long serialVersionUID = 0L;
            private final Elem msg$1;

            public final Elem apply() {
               return this.msg$1;
            }

            public {
               this.msg$1 = msg$1;
            }
         }, "Not Found", UIUtils$.MODULE$.basicSparkPage$default$3());
      } else {
         Seq executorHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ExecutorID", "Worker", "Cores", "Memory", "State", "Logs"})));
         Seq allExecutors = ((TraversableOnce)app.executors().values().$plus$plus(app.removedExecutors(), Iterable$.MODULE$.canBuildFrom())).toSet().toSeq();
         Seq executors = (Seq)allExecutors.filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(ExecutorDesc exec) {
               boolean var3;
               label25: {
                  if (ExecutorState$.MODULE$.isFinished(exec.state())) {
                     Enumeration.Value var10000 = exec.state();
                     Enumeration.Value var2 = ExecutorState$.MODULE$.EXITED();
                     if (var10000 == null) {
                        if (var2 != null) {
                           break label25;
                        }
                     } else if (!var10000.equals(var2)) {
                        break label25;
                     }
                  }

                  var3 = true;
                  return var3;
               }

               var3 = false;
               return var3;
            }
         });
         Seq removedExecutors = (Seq)allExecutors.diff(executors);
         Seq executorsTable = UIUtils$.MODULE$.listingTable(executorHeaders, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ApplicationPage $outer;

            public final Seq<Node> apply(ExecutorDesc executor) {
               return this.$outer.org$apache$spark$deploy$master$ui$ApplicationPage$$executorRow(executor);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, executors, UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
         Seq removedExecutorsTable = UIUtils$.MODULE$.listingTable(executorHeaders, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ApplicationPage $outer;

            public final Seq<Node> apply(ExecutorDesc executor) {
               return this.$outer.org$apache$spark$deploy$master$ui$ApplicationPage$$executorRow(executor);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, removedExecutors, UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
         NodeBuffer $buf = new NodeBuffer();
         MetaData $md = .MODULE$;
         MetaData var63 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
         Elem var10001 = new Elem;
         TopScope var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var64 = new UnprefixedAttribute("class", new Text("span12"), $md);
         Elem var10009 = new Elem;
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var65 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
         Elem var10017 = new Elem;
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
         $buf.$amp$plus(app.id());
         $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Name:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text(" "));
         $buf.$amp$plus(app.desc().name());
         $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("User:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text(" "));
         $buf.$amp$plus(app.desc().user());
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
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(app.desc().maxCores().isEmpty() ? (new StringOps(Predef$.MODULE$.augmentString("Unlimited (%s granted)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(app.coresGranted())})) : (new StringOps(Predef$.MODULE$.augmentString("%s (%s granted, %s left)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{app.desc().maxCores().get(), BoxesRunTime.boxToInteger(app.coresGranted()), BoxesRunTime.boxToInteger(app.coresLeft())})));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var66 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
         var66 = new UnprefixedAttribute("title", ToolTips$.MODULE$.APPLICATION_EXECUTOR_LIMIT(), var66);
         var66 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var66);
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         Null var10045 = .MODULE$;
         TopScope var10046 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Executor Limit: "));
         $buf.$amp$plus(new Elem((String)null, "strong", var10045, var10046, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(app.executorLimit() == Integer.MAX_VALUE ? "Unlimited" : BoxesRunTime.boxToInteger(app.executorLimit()));
         $buf.$amp$plus(new Text("\n                ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(app.executors().size()));
         $buf.$amp$plus(new Text(" granted)\n              "));
         $buf.$amp$plus(new Elem((String)null, "span", var66, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Executor Memory:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)app.desc().memoryPerExecutorMB()));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Submit Date:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text(" "));
         $buf.$amp$plus(UIUtils$.MODULE$.formatDate(app.submitDate()));
         $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("State:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text(" "));
         $buf.$amp$plus(app.state());
         $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         Object var10025;
         if (app.isFinished()) {
            var10025 = BoxedUnit.UNIT;
         } else {
            var10029 = .MODULE$;
            var10030 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            var10037 = .MODULE$;
            var10038 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                    "));
            MetaData $md = .MODULE$;
            MetaData var69 = new UnprefixedAttribute("href", UIUtils$.MODULE$.makeHref(this.parent.master().reverseProxy(), app.id(), app.desc().appUiUrl()), $md);
            var10046 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Application Detail UI"));
            $buf.$amp$plus(new Elem((String)null, "a", var69, var10046, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
            var10025 = new Elem((String)null, "li", var10029, var10030, false, $buf);
         }

         $buf.$amp$plus(var10025);
         $buf.$amp$plus(new Text("\n          "));
         var10017.<init>((String)null, "ul", var65, var10022, false, $buf);
         $buf.$amp$plus(var10017);
         $buf.$amp$plus(new Text("\n        "));
         var10009.<init>((String)null, "div", var64, var10014, false, $buf);
         $buf.$amp$plus(var10009);
         $buf.$amp$plus(new Text("\n      "));
         var10001.<init>((String)null, "div", var63, var10006, false, $buf);
         $buf.$amp$plus(var10001);
         MetaData $md = .MODULE$;
         MetaData var70 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
         var10001 = new Elem;
         var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text(" "));
         $buf.$amp$plus(new Comment(" Executors "));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var71 = new UnprefixedAttribute("class", new Text("span12"), $md);
         var10009 = new Elem;
         var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var72 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-executors','aggregated-executors')"), $md);
         var72 = new UnprefixedAttribute("class", new Text("collapse-aggregated-executors collapse-table"), var72);
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         var10029 = .MODULE$;
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var74 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var74, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n              "));
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Executor Summary ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(allExecutors.length()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "span", var72, var10022, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var75 = new UnprefixedAttribute("class", new Text("aggregated-executors collapsible-table"), $md);
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(executorsTable);
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "div", var75, var10022, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         Object var85;
         if (removedExecutors.nonEmpty()) {
            MetaData $md = .MODULE$;
            MetaData var76 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-removedExecutors',\n                  'aggregated-removedExecutors')"), $md);
            var76 = new UnprefixedAttribute("class", new Text("collapse-aggregated-removedExecutors collapse-table"), var76);
            var10022 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            var10029 = .MODULE$;
            var10030 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                  "));
            MetaData $md = .MODULE$;
            MetaData var78 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
            $buf.$amp$plus(new Elem((String)null, "span", var78, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
            $buf.$amp$plus(new Text("\n                  "));
            var10037 = .MODULE$;
            var10038 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Removed Executors ("));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(removedExecutors.length()));
            $buf.$amp$plus(new Text(")"));
            $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            Elem var84 = new Elem((String)null, "span", var76, var10022, false, $buf);
            MetaData $md = .MODULE$;
            MetaData var79 = new UnprefixedAttribute("class", new Text("aggregated-removedExecutors collapsible-table"), $md);
            TopScope var10023 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(removedExecutorsTable);
            $buf.$amp$plus(new Text("\n              "));
            var85 = var84.$plus$plus(new Elem((String)null, "div", var79, var10023, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
         } else {
            var85 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var85);
         $buf.$amp$plus(new Text("\n        "));
         var10009.<init>((String)null, "div", var71, var10014, false, $buf);
         $buf.$amp$plus(var10009);
         $buf.$amp$plus(new Text("\n      "));
         var10001.<init>((String)null, "div", var70, var10006, false, $buf);
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
         }, (new StringBuilder()).append((Object)"Application: ").append((Object)app.desc().name()).toString(), UIUtils$.MODULE$.basicSparkPage$default$3());
      }
   }

   public Seq<Node> org$apache$spark$deploy$master$ui$ApplicationPage$$executorRow(ExecutorDesc executor) {
      String workerUrlRef = UIUtils$.MODULE$.makeHref(this.parent.master().reverseProxy(), executor.worker().id(), executor.worker().webUiAddress());
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(executor.id()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var16 = new UnprefixedAttribute("href", workerUrlRef, $md);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(executor.worker().id());
      $buf.$amp$plus(new Elem((String)null, "a", var16, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(executor.cores()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(executor.memory()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(executor.state());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var17 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/logPage?appId=", "&executorId=", "&logType=stdout"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{workerUrlRef, executor.application().id(), BoxesRunTime.boxToInteger(executor.id())})), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("stdout"));
      $buf.$amp$plus(new Elem((String)null, "a", var17, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var18 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/logPage?appId=", "&executorId=", "&logType=stderr"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{workerUrlRef, executor.application().id(), BoxesRunTime.boxToInteger(executor.id())})), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("stderr"));
      $buf.$amp$plus(new Elem((String)null, "a", var18, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public ApplicationPage(MasterWebUI parent) {
      super("app");
      this.parent = parent;
      this.master = parent.masterEndpointRef();
   }
}
