package org.apache.spark.deploy.master.ui;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.JsonProtocol$;
import org.apache.spark.deploy.master.ApplicationInfo;
import org.apache.spark.deploy.master.ApplicationState$;
import org.apache.spark.deploy.master.DriverInfo;
import org.apache.spark.deploy.master.DriverState$;
import org.apache.spark.deploy.master.WorkerInfo;
import org.apache.spark.deploy.master.WorkerState$;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import org.json4s.JsonAST;
import scala.Array$;
import scala.Enumeration;
import scala.Function1;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
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
   bytes = "\u0006\u0001\u0005\u0015c!B\u0001\u0003\u0001\tq!AC'bgR,'\u000fU1hK*\u00111\u0001B\u0001\u0003k&T!!\u0002\u0004\u0002\r5\f7\u000f^3s\u0015\t9\u0001\"\u0001\u0004eKBdw.\u001f\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sON\u0011\u0001a\u0004\t\u0003!Ii\u0011!\u0005\u0006\u0003\u0007!I!aE\t\u0003\u0013]+'-V%QC\u001e,\u0007\u0002C\u000b\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\rA\f'/\u001a8u\u0007\u0001\u0001\"\u0001G\r\u000e\u0003\tI!A\u0007\u0002\u0003\u00175\u000b7\u000f^3s/\u0016\u0014W+\u0013\u0005\u00069\u0001!\t!H\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005yy\u0002C\u0001\r\u0001\u0011\u0015)2\u00041\u0001\u0018\u0011\u001d)\u0001A1A\u0005\n\u0005*\u0012A\t\t\u0003G\u0019j\u0011\u0001\n\u0006\u0003K!\t1A\u001d9d\u0013\t9CE\u0001\bSa\u000e,e\u000e\u001a9pS:$(+\u001a4\t\r%\u0002\u0001\u0015!\u0003#\u0003\u001di\u0017m\u001d;fe\u0002BQa\u000b\u0001\u0005\u00021\nabZ3u\u001b\u0006\u001cH/\u001a:Ti\u0006$X-F\u0001.!\tqCH\u0004\u00020u9\u0011\u0001'\u000f\b\u0003car!AM\u001c\u000f\u0005M2T\"\u0001\u001b\u000b\u0005U2\u0012A\u0002\u001fs_>$h(C\u0001\u000e\u0013\tYA\"\u0003\u0002\n\u0015%\u0011q\u0001C\u0005\u0003w\u0019\ta\u0002R3qY>LX*Z:tC\u001e,7/\u0003\u0002>}\t\u0019R*Y:uKJ\u001cF/\u0019;f%\u0016\u001c\bo\u001c8tK*\u00111H\u0002\u0005\u0006\u0001\u0002!\t%Q\u0001\u000be\u0016tG-\u001a:Kg>tGC\u0001\"N!\t\u0019%J\u0004\u0002E\u000f:\u0011!'R\u0005\u0003\r2\taA[:p]R\u001a\u0018B\u0001%J\u0003\u001d\u0001\u0018mY6bO\u0016T!A\u0012\u0007\n\u0005-c%A\u0002&WC2,XM\u0003\u0002I\u0013\")aj\u0010a\u0001\u001f\u00069!/Z9vKN$\bC\u0001)X\u001b\u0005\t&B\u0001*T\u0003\u0011AG\u000f\u001e9\u000b\u0005Q+\u0016aB:feZdW\r\u001e\u0006\u0002-\u0006)!.\u0019<bq&\u0011\u0001,\u0015\u0002\u0013\u0011R$\boU3sm2,GOU3rk\u0016\u001cH\u000fC\u0003[\u0001\u0011\u00051,\u0001\u000biC:$G.Z!qa.KG\u000e\u001c*fcV,7\u000f\u001e\u000b\u00039\n\u0004\"!\u00181\u000e\u0003yS\u0011aX\u0001\u0006g\u000e\fG.Y\u0005\u0003Cz\u0013A!\u00168ji\")a*\u0017a\u0001\u001f\")A\r\u0001C\u0001K\u00069\u0002.\u00198eY\u0016$%/\u001b<fe.KG\u000e\u001c*fcV,7\u000f\u001e\u000b\u00039\u001aDQAT2A\u0002=CQ\u0001\u001b\u0001\u0005\n%\f\u0011\u0003[1oI2,7*\u001b7m%\u0016\fX/Z:u)\ra&n\u001b\u0005\u0006\u001d\u001e\u0004\ra\u0014\u0005\u0006Y\u001e\u0004\r!\\\u0001\u0007C\u000e$\u0018n\u001c8\u0011\tus\u0007\u000fX\u0005\u0003_z\u0013\u0011BR;oGRLwN\\\u0019\u0011\u0005E$hBA/s\u0013\t\u0019h,\u0001\u0004Qe\u0016$WMZ\u0005\u0003kZ\u0014aa\u0015;sS:<'BA:_\u0011\u0015A\b\u0001\"\u0001z\u0003\u0019\u0011XM\u001c3feR\u0019!0!\u0005\u0011\tm|\u0018Q\u0001\b\u0003yzt!aM?\n\u0003}K!\u0001\u00130\n\t\u0005\u0005\u00111\u0001\u0002\u0004'\u0016\f(B\u0001%_!\u0011\t9!!\u0004\u000e\u0005\u0005%!bAA\u0006=\u0006\u0019\u00010\u001c7\n\t\u0005=\u0011\u0011\u0002\u0002\u0005\u001d>$W\rC\u0003Oo\u0002\u0007q\nC\u0004\u0002\u0016\u0001!I!a\u0006\u0002\u0013]|'o[3s%><Hc\u0001>\u0002\u001a!A\u00111DA\n\u0001\u0004\ti\"\u0001\u0004x_J\\WM\u001d\t\u0005\u0003?\t\t#D\u0001\u0005\u0013\r\t\u0019\u0003\u0002\u0002\u000b/>\u00148.\u001a:J]\u001a|\u0007bBA\u0014\u0001\u0011%\u0011\u0011F\u0001\u0007CB\u0004(k\\<\u0015\u0007i\fY\u0003\u0003\u0005\u0002.\u0005\u0015\u0002\u0019AA\u0018\u0003\r\t\u0007\u000f\u001d\t\u0005\u0003?\t\t$C\u0002\u00024\u0011\u0011q\"\u00119qY&\u001c\u0017\r^5p]&sgm\u001c\u0005\b\u0003o\u0001A\u0011BA\u001d\u0003%!'/\u001b<feJ{w\u000fF\u0002{\u0003wA\u0001\"!\u0010\u00026\u0001\u0007\u0011qH\u0001\u0007IJLg/\u001a:\u0011\t\u0005}\u0011\u0011I\u0005\u0004\u0003\u0007\"!A\u0003#sSZ,'/\u00138g_\u0002"
)
public class MasterPage extends WebUIPage {
   public final MasterWebUI org$apache$spark$deploy$master$ui$MasterPage$$parent;
   private final RpcEndpointRef org$apache$spark$deploy$master$ui$MasterPage$$master;

   public RpcEndpointRef org$apache$spark$deploy$master$ui$MasterPage$$master() {
      return this.org$apache$spark$deploy$master$ui$MasterPage$$master;
   }

   public DeployMessages.MasterStateResponse getMasterState() {
      return (DeployMessages.MasterStateResponse)this.org$apache$spark$deploy$master$ui$MasterPage$$master().askSync(DeployMessages.RequestMasterState$.MODULE$, ClassTag$.MODULE$.apply(DeployMessages.MasterStateResponse.class));
   }

   public JsonAST.JValue renderJson(HttpServletRequest request) {
      return JsonProtocol$.MODULE$.writeMasterState(this.getMasterState());
   }

   public void handleAppKillRequest(HttpServletRequest request) {
      this.handleKillRequest(request, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final void apply(String id) {
            this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$parent.master().idToApp().get(id).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(ApplicationInfo app) {
                  this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$anonfun$$$outer().org$apache$spark$deploy$master$ui$MasterPage$$parent.master().removeApplication(app, ApplicationState$.MODULE$.KILLED());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         // $FF: synthetic method
         public MasterPage org$apache$spark$deploy$master$ui$MasterPage$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void handleDriverKillRequest(HttpServletRequest request) {
      this.handleKillRequest(request, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final void apply(String id) {
            this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$master().ask(new DeployMessages.RequestKillDriver(id), ClassTag$.MODULE$.apply(DeployMessages.KillDriverResponse.class));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   private void handleKillRequest(HttpServletRequest request, Function1<String, BoxedUnit> action) {
      if (this.org$apache$spark$deploy$master$ui$MasterPage$$parent.killEnabled() && this.org$apache$spark$deploy$master$ui$MasterPage$$parent.master().securityMgr().checkModifyPermissions(request.getRemoteUser())) {
         boolean killFlag = (new StringOps(Predef$.MODULE$.augmentString((String)Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("terminate"))).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "false";
            }
         })))).toBoolean();
         Option id = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("id")));
         if (id.isDefined() && killFlag) {
            action.apply(id.get());
         } else {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         Thread.sleep(100L);
      }

   }

   public Seq<Node> render(HttpServletRequest request) {
      DeployMessages.MasterStateResponse state = this.getMasterState();
      Seq workerHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Worker Id", "Address", "State", "Cores", "Memory"})));
      WorkerInfo[] workers = (WorkerInfo[])Predef$.MODULE$.refArrayOps(state.workers()).sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(WorkerInfo x$1) {
            return x$1.id();
         }
      }, Ordering.String$.MODULE$);
      WorkerInfo[] aliveWorkers = (WorkerInfo[])Predef$.MODULE$.refArrayOps(state.workers()).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(WorkerInfo x$2) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$2.state();
               Enumeration.Value var2 = WorkerState$.MODULE$.ALIVE();
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
      });
      Seq workerTable = UIUtils$.MODULE$.listingTable(workerHeaders, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final Seq<Node> apply(WorkerInfo worker) {
            return this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$workerRow(worker);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Predef$.MODULE$.wrapRefArray(workers), UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      Seq appHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Application ID", "Name", "Cores", "Memory per Executor", "Submitted Time", "User", "State", "Duration"})));
      ApplicationInfo[] activeApps = (ApplicationInfo[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(state.activeApps()).sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(ApplicationInfo x$3) {
            return x$3.startTime();
         }
      }, Ordering.Long$.MODULE$)).reverse();
      Seq activeAppsTable = UIUtils$.MODULE$.listingTable(appHeaders, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final Seq<Node> apply(ApplicationInfo app) {
            return this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$appRow(app);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Predef$.MODULE$.wrapRefArray(activeApps), UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      ApplicationInfo[] completedApps = (ApplicationInfo[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(state.completedApps()).sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(ApplicationInfo x$4) {
            return x$4.endTime();
         }
      }, Ordering.Long$.MODULE$)).reverse();
      Seq completedAppsTable = UIUtils$.MODULE$.listingTable(appHeaders, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final Seq<Node> apply(ApplicationInfo app) {
            return this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$appRow(app);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Predef$.MODULE$.wrapRefArray(completedApps), UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      Seq driverHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Submission ID", "Submitted Time", "Worker", "State", "Cores", "Memory", "Main Class"})));
      DriverInfo[] activeDrivers = (DriverInfo[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(state.activeDrivers()).sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(DriverInfo x$5) {
            return x$5.startTime();
         }
      }, Ordering.Long$.MODULE$)).reverse();
      Seq activeDriversTable = UIUtils$.MODULE$.listingTable(driverHeaders, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final Seq<Node> apply(DriverInfo driver) {
            return this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$driverRow(driver);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Predef$.MODULE$.wrapRefArray(activeDrivers), UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      DriverInfo[] completedDrivers = (DriverInfo[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(state.completedDrivers()).sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(DriverInfo x$6) {
            return x$6.startTime();
         }
      }, Ordering.Long$.MODULE$)).reverse();
      Seq completedDriversTable = UIUtils$.MODULE$.listingTable(driverHeaders, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final Seq<Node> apply(DriverInfo driver) {
            return this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$driverRow(driver);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Predef$.MODULE$.wrapRefArray(completedDrivers), UIUtils$.MODULE$.listingTable$default$4(), UIUtils$.MODULE$.listingTable$default$5(), UIUtils$.MODULE$.listingTable$default$6(), UIUtils$.MODULE$.listingTable$default$7(), UIUtils$.MODULE$.listingTable$default$8());
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var100 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var101 = new UnprefixedAttribute("class", new Text("span12"), $md);
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var102 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
      TopScope var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      Null var10029 = .MODULE$;
      TopScope var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      Null var10037 = .MODULE$;
      TopScope var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("URL:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(state.uri());
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(state.restUri().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Elem apply(String uri) {
            Null var10004 = .MODULE$;
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                    "));
            Null var10012 = .MODULE$;
            TopScope var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("REST URL:"));
            $buf.$amp$plus(new Elem((String)null, "strong", var10012, var10013, false, $buf));
            $buf.$amp$plus(new Text(" "));
            $buf.$amp$plus(uri);
            $buf.$amp$plus(new Text("\n                    "));
            MetaData $md = .MODULE$;
            MetaData var6 = new UnprefixedAttribute("class", new Text("rest-uri"), $md);
            var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text(" (cluster mode)"));
            $buf.$amp$plus(new Elem((String)null, "span", var6, var10013, false, $buf));
            $buf.$amp$plus(new Text("\n                  "));
            return new Elem((String)null, "li", var10004, var10005, false, $buf);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      }));
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Alive Workers:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(aliveWorkers.length));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Cores in use:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$7) {
            return x$7.cores();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$));
      $buf.$amp$plus(new Text(" Total,\n                "));
      $buf.$amp$plus(Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$8) {
            return x$8.coresUsed();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$));
      $buf.$amp$plus(new Text(" Used"));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Memory in use:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n                "));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)BoxesRunTime.unboxToInt(Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$9) {
            return x$9.memory();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$))));
      $buf.$amp$plus(new Text(" Total,\n                "));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)BoxesRunTime.unboxToInt(Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$10) {
            return x$10.memoryUsed();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$))));
      $buf.$amp$plus(new Text(" Used"));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Applications:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n                "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(state.activeApps().length));
      $buf.$amp$plus(new Text(" "));
      MetaData $md = .MODULE$;
      MetaData var103 = new UnprefixedAttribute("href", new Text("#running-app"), $md);
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Running"));
      $buf.$amp$plus(new Elem((String)null, "a", var103, var10038, false, $buf));
      $buf.$amp$plus(new Text(",\n                "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(state.completedApps().length));
      $buf.$amp$plus(new Text(" "));
      MetaData $md = .MODULE$;
      MetaData var104 = new UnprefixedAttribute("href", new Text("#completed-app"), $md);
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Completed"));
      $buf.$amp$plus(new Elem((String)null, "a", var104, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Drivers:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n                "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(state.activeDrivers().length));
      $buf.$amp$plus(new Text(" Running,\n                "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(state.completedDrivers().length));
      $buf.$amp$plus(new Text(" Completed "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Status:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(state.status());
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "ul", var102, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var101, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var100, var10006, false, $buf));
      MetaData $md = .MODULE$;
      MetaData var105 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var106 = new UnprefixedAttribute("class", new Text("span12"), $md);
      var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var107 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-workers','aggregated-workers')"), $md);
      var107 = new UnprefixedAttribute("class", new Text("collapse-aggregated-workers collapse-table"), var107);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n                "));
      MetaData $md = .MODULE$;
      MetaData var109 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var109, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n                "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Workers ("));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(workers.length));
      $buf.$amp$plus(new Text(")"));
      $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "span", var107, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var110 = new UnprefixedAttribute("class", new Text("aggregated-workers collapsible-table"), $md);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(workerTable);
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "div", var110, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var106, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var105, var10006, false, $buf));
      MetaData $md = .MODULE$;
      MetaData var111 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var112 = new UnprefixedAttribute("class", new Text("span12"), $md);
      var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var113 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-activeApps','aggregated-activeApps')"), $md);
      var113 = new UnprefixedAttribute("class", new Text("collapse-aggregated-activeApps collapse-table"), var113);
      var113 = new UnprefixedAttribute("id", new Text("running-app"), var113);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n                "));
      MetaData $md = .MODULE$;
      MetaData var116 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var116, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n                "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Running Applications ("));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(activeApps.length));
      $buf.$amp$plus(new Text(")"));
      $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "span", var113, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var117 = new UnprefixedAttribute("class", new Text("aggregated-activeApps collapsible-table"), $md);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(activeAppsTable);
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "div", var117, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var112, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var111, var10006, false, $buf));
      Elem var10001 = new Elem;
      Null var10005 = .MODULE$;
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      Object var10009;
      if (this.hasDrivers$1(activeDrivers, completedDrivers)) {
         MetaData $md = .MODULE$;
         MetaData var118 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
         var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n               "));
         MetaData $md = .MODULE$;
         MetaData var119 = new UnprefixedAttribute("class", new Text("span12"), $md);
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                 "));
         MetaData $md = .MODULE$;
         MetaData var120 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-activeDrivers',\n                     'aggregated-activeDrivers')"), $md);
         var120 = new UnprefixedAttribute("class", new Text("collapse-aggregated-activeDrivers collapse-table"), var120);
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                   "));
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                     "));
         MetaData $md = .MODULE$;
         MetaData var122 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var122, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                     "));
         Null var10045 = .MODULE$;
         TopScope var10046 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Running Drivers ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(activeDrivers.length));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var10045, var10046, false, $buf));
         $buf.$amp$plus(new Text("\n                   "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n                 "));
         $buf.$amp$plus(new Elem((String)null, "span", var120, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n                 "));
         MetaData $md = .MODULE$;
         MetaData var123 = new UnprefixedAttribute("class", new Text("aggregated-activeDrivers collapsible-table"), $md);
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                   "));
         $buf.$amp$plus(activeDriversTable);
         $buf.$amp$plus(new Text("\n                 "));
         $buf.$amp$plus(new Elem((String)null, "div", var123, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n               "));
         $buf.$amp$plus(new Elem((String)null, "div", var119, var10022, false, $buf));
         $buf.$amp$plus(new Text("\n             "));
         var10009 = new Elem((String)null, "div", var118, var10014, false, $buf);
      } else {
         var10009 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10009);
      $buf.$amp$plus(new Text("\n        "));
      var10001.<init>((String)null, "div", var10005, var10006, false, $buf);
      $buf.$amp$plus(var10001);
      MetaData $md = .MODULE$;
      MetaData var124 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var125 = new UnprefixedAttribute("class", new Text("span12"), $md);
      var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var126 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-completedApps',\n                'aggregated-completedApps')"), $md);
      var126 = new UnprefixedAttribute("class", new Text("collapse-aggregated-completedApps collapse-table"), var126);
      var126 = new UnprefixedAttribute("id", new Text("completed-app"), var126);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n                "));
      MetaData $md = .MODULE$;
      MetaData var129 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var129, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n                "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Completed Applications ("));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(completedApps.length));
      $buf.$amp$plus(new Text(")"));
      $buf.$amp$plus(new Elem((String)null, "a", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "span", var126, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var130 = new UnprefixedAttribute("class", new Text("aggregated-completedApps collapsible-table"), $md);
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(completedAppsTable);
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "div", var130, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var125, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var124, var10006, false, $buf));
      var10001 = new Elem;
      var10005 = .MODULE$;
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      if (this.hasDrivers$1(activeDrivers, completedDrivers)) {
         MetaData $md = .MODULE$;
         MetaData var131 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
         var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var132 = new UnprefixedAttribute("class", new Text("span12"), $md);
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                  "));
         MetaData $md = .MODULE$;
         MetaData var133 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-completedDrivers',\n                      'aggregated-completedDrivers')"), $md);
         var133 = new UnprefixedAttribute("class", new Text("collapse-aggregated-completedDrivers collapse-table"), var133);
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                    "));
         var10037 = .MODULE$;
         var10038 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                      "));
         MetaData $md = .MODULE$;
         MetaData var135 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var135, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n                      "));
         Null var204 = .MODULE$;
         TopScope var205 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Completed Drivers ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(completedDrivers.length));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var204, var205, false, $buf));
         $buf.$amp$plus(new Text("\n                    "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10037, var10038, false, $buf));
         $buf.$amp$plus(new Text("\n                  "));
         $buf.$amp$plus(new Elem((String)null, "span", var133, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n                  "));
         MetaData $md = .MODULE$;
         MetaData var136 = new UnprefixedAttribute("class", new Text("aggregated-completedDrivers collapsible-table"), $md);
         var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                    "));
         $buf.$amp$plus(completedDriversTable);
         $buf.$amp$plus(new Text("\n                  "));
         $buf.$amp$plus(new Elem((String)null, "div", var136, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(new Elem((String)null, "div", var132, var10022, false, $buf));
         $buf.$amp$plus(new Text("\n              "));
         var10009 = new Elem((String)null, "div", var131, var10014, false, $buf);
      } else {
         var10009 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10009);
      $buf.$amp$plus(new Text("\n        "));
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
      }, (new StringBuilder()).append((Object)"Spark Master at ").append((Object)state.uri()).toString(), UIUtils$.MODULE$.basicSparkPage$default$3());
   }

   public Seq<Node> org$apache$spark$deploy$master$ui$MasterPage$$workerRow(WorkerInfo worker) {
      Elem var10000 = new Elem;
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Elem var10008 = new Elem;
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      Object var10016;
      if (worker.isAlive()) {
         MetaData $md = .MODULE$;
         MetaData var11 = new UnprefixedAttribute("href", UIUtils$.MODULE$.makeHref(this.org$apache$spark$deploy$master$ui$MasterPage$$parent.master().reverseProxy(), worker.id(), worker.webUiAddress()), $md);
         TopScope var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(worker.id());
         $buf.$amp$plus(new Text("\n            "));
         var10016 = new Elem((String)null, "a", var11, var10021, false, $buf);
      } else {
         var10016 = worker.id();
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n      "));
      var10008.<init>((String)null, "td", var10012, var10013, false, $buf);
      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(worker.host());
      $buf.$amp$plus(new Text(":"));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(worker.port()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(worker.state());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(worker.cores()));
      $buf.$amp$plus(new Text(" ("));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(worker.coresUsed()));
      $buf.$amp$plus(new Text(" Used)"));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var12 = new UnprefixedAttribute("sorttable_customkey", (new StringOps(Predef$.MODULE$.augmentString("%s.%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(worker.memory()), BoxesRunTime.boxToInteger(worker.memoryUsed())})), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)worker.memory()));
      $buf.$amp$plus(new Text("\n        ("));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)worker.memoryUsed()));
      $buf.$amp$plus(new Text(" Used)\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var12, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      var10000.<init>((String)null, "tr", var10004, var10005, false, $buf);
      return var10000;
   }

   public Seq<Node> org$apache$spark$deploy$master$ui$MasterPage$$appRow(ApplicationInfo app) {
      Object var42;
      label32: {
         label31: {
            if (this.org$apache$spark$deploy$master$ui$MasterPage$$parent.killEnabled()) {
               Enumeration.Value var10000 = app.state();
               Enumeration.Value var3 = ApplicationState$.MODULE$.RUNNING();
               if (var10000 == null) {
                  if (var3 == null) {
                     break label31;
                  }
               } else if (var10000.equals(var3)) {
                  break label31;
               }

               var10000 = app.state();
               Enumeration.Value var4 = ApplicationState$.MODULE$.WAITING();
               if (var10000 == null) {
                  if (var4 == null) {
                     break label31;
                  }
               } else if (var10000.equals(var4)) {
                  break label31;
               }
            }

            var42 = BoxedUnit.UNIT;
            break label32;
         }

         String confirm = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"if (window.confirm('Are you sure you want to kill application ", " ?')) "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{app.id()}))).append((Object)"{ this.parentNode.submit(); return true; } else { return false; }").toString();
         MetaData $md = .MODULE$;
         MetaData var26 = new UnprefixedAttribute("style", new Text("display:inline"), $md);
         var26 = new UnprefixedAttribute("method", new Text("POST"), var26);
         var26 = new UnprefixedAttribute("action", new Text("app/kill/"), var26);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var29 = new UnprefixedAttribute("value", app.id().toString(), $md);
         var29 = new UnprefixedAttribute("name", new Text("id"), var29);
         var29 = new UnprefixedAttribute("type", new Text("hidden"), var29);
         $buf.$amp$plus(new Elem((String)null, "input", var29, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var32 = new UnprefixedAttribute("value", new Text("true"), $md);
         var32 = new UnprefixedAttribute("name", new Text("terminate"), var32);
         var32 = new UnprefixedAttribute("type", new Text("hidden"), var32);
         $buf.$amp$plus(new Elem((String)null, "input", var32, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var35 = new UnprefixedAttribute("class", new Text("kill-link"), $md);
         var35 = new UnprefixedAttribute("onclick", confirm, var35);
         var35 = new UnprefixedAttribute("href", new Text("#"), var35);
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("(kill)"));
         $buf.$amp$plus(new Elem((String)null, "a", var35, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         var42 = new Elem((String)null, "form", var26, var10005, false, $buf);
      }

      Object killLink = var42;
      Elem var43 = new Elem;
      Null var10004 = .MODULE$;
      TopScope var44 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var38 = new UnprefixedAttribute("href", (new StringBuilder()).append((Object)"app?appId=").append((Object)app.id()).toString(), $md);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(app.id());
      $buf.$amp$plus(new Elem((String)null, "a", var38, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(killLink);
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var51, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      Elem var10008 = new Elem;
      var10012 = .MODULE$;
      var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      Object var10016;
      if (app.isFinished()) {
         var10016 = app.desc().name();
      } else {
         MetaData $md = .MODULE$;
         MetaData var39 = new UnprefixedAttribute("href", UIUtils$.MODULE$.makeHref(this.org$apache$spark$deploy$master$ui$MasterPage$$parent.master().reverseProxy(), app.id(), app.desc().appUiUrl()), $md);
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(app.desc().name());
         var10016 = new Elem((String)null, "a", var39, var10021, false, $buf);
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n      "));
      var10008.<init>((String)null, "td", var10012, var51, false, $buf);
      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(app.coresGranted()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var51, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var40 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToInteger(app.desc().memoryPerExecutorMB()).toString(), $md);
      var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)app.desc().memoryPerExecutorMB()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var40, var51, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(UIUtils$.MODULE$.formatDate(app.submitDate()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var51, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(app.desc().user());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var51, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(app.state().toString());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var51, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var51 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(UIUtils$.MODULE$.formatDuration(app.duration()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var51, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      var43.<init>((String)null, "tr", var10004, var44, false, $buf);
      return var43;
   }

   public Seq<Node> org$apache$spark$deploy$master$ui$MasterPage$$driverRow(DriverInfo driver) {
      Object var39;
      label35: {
         label34: {
            if (this.org$apache$spark$deploy$master$ui$MasterPage$$parent.killEnabled()) {
               Enumeration.Value var10000 = driver.state();
               Enumeration.Value var3 = DriverState$.MODULE$.RUNNING();
               if (var10000 == null) {
                  if (var3 == null) {
                     break label34;
                  }
               } else if (var10000.equals(var3)) {
                  break label34;
               }

               var10000 = driver.state();
               Enumeration.Value var4 = DriverState$.MODULE$.SUBMITTED();
               if (var10000 == null) {
                  if (var4 == null) {
                     break label34;
                  }
               } else if (var10000.equals(var4)) {
                  break label34;
               }

               var10000 = driver.state();
               Enumeration.Value var5 = DriverState$.MODULE$.RELAUNCHING();
               if (var10000 == null) {
                  if (var5 == null) {
                     break label34;
                  }
               } else if (var10000.equals(var5)) {
                  break label34;
               }
            }

            var39 = BoxedUnit.UNIT;
            break label35;
         }

         String confirm = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"if (window.confirm('Are you sure you want to kill driver ", " ?')) "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{driver.id()}))).append((Object)"{ this.parentNode.submit(); return true; } else { return false; }").toString();
         MetaData $md = .MODULE$;
         MetaData var23 = new UnprefixedAttribute("style", new Text("display:inline"), $md);
         var23 = new UnprefixedAttribute("method", new Text("POST"), var23);
         var23 = new UnprefixedAttribute("action", new Text("driver/kill/"), var23);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var26 = new UnprefixedAttribute("value", driver.id().toString(), $md);
         var26 = new UnprefixedAttribute("name", new Text("id"), var26);
         var26 = new UnprefixedAttribute("type", new Text("hidden"), var26);
         $buf.$amp$plus(new Elem((String)null, "input", var26, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var29 = new UnprefixedAttribute("value", new Text("true"), $md);
         var29 = new UnprefixedAttribute("name", new Text("terminate"), var29);
         var29 = new UnprefixedAttribute("type", new Text("hidden"), var29);
         $buf.$amp$plus(new Elem((String)null, "input", var29, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var32 = new UnprefixedAttribute("class", new Text("kill-link"), $md);
         var32 = new UnprefixedAttribute("onclick", confirm, var32);
         var32 = new UnprefixedAttribute("href", new Text("#"), var32);
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("(kill)"));
         $buf.$amp$plus(new Elem((String)null, "a", var32, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         var39 = new Elem((String)null, "form", var23, var10005, false, $buf);
      }

      Object killLink = var39;
      Null var10004 = .MODULE$;
      TopScope var40 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(driver.id());
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(killLink);
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var45, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(UIUtils$.MODULE$.formatDate(driver.submitDate()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var45, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(driver.worker().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterPage $outer;

         public final Object apply(WorkerInfo w) {
            Object var10000;
            if (w.isAlive()) {
               MetaData $md = .MODULE$;
               MetaData var4 = new UnprefixedAttribute("href", UIUtils$.MODULE$.makeHref(this.$outer.org$apache$spark$deploy$master$ui$MasterPage$$parent.master().reverseProxy(), w.id(), w.webUiAddress()), $md);
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n            "));
               $buf.$amp$plus(w.id().toString());
               $buf.$amp$plus(new Text("\n          "));
               var10000 = new Elem((String)null, "a", var4, var10005, false, $buf);
            } else {
               var10000 = w.id().toString();
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "None";
         }
      }));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var45, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(driver.state());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var45, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var35 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToInteger(driver.desc().cores()).toString(), $md);
      var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(driver.desc().cores()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var35, var45, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var36 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToInteger(driver.desc().mem()).toString(), $md);
      var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.megabytesToString((long)driver.desc().mem()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var36, var45, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var45 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(driver.desc().command().arguments().apply(2));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var45, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var40, false, $buf);
   }

   private final boolean hasDrivers$1(DriverInfo[] activeDrivers$1, DriverInfo[] completedDrivers$1) {
      return activeDrivers$1.length > 0 || completedDrivers$1.length > 0;
   }

   public MasterPage(MasterWebUI parent) {
      super("");
      this.org$apache$spark$deploy$master$ui$MasterPage$$parent = parent;
      this.org$apache$spark$deploy$master$ui$MasterPage$$master = parent.masterEndpointRef();
   }
}
