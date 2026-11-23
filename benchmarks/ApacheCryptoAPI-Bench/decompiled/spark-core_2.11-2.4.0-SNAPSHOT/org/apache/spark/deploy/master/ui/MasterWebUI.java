package org.apache.spark.deploy.master.ui;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SSLOptions;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.master.ApplicationInfo;
import org.apache.spark.deploy.master.Master;
import org.apache.spark.deploy.master.WorkerInfo;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.ui.JettyUtils$;
import org.apache.spark.ui.WebUI;
import org.apache.spark.ui.WebUI$;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.Set;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I4Q!\u0001\u0002\u0001\t9\u00111\"T1ti\u0016\u0014x+\u001a2V\u0013*\u00111\u0001B\u0001\u0003k&T!!\u0002\u0004\u0002\r5\f7\u000f^3s\u0015\t9\u0001\"\u0001\u0004eKBdw.\u001f\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sON\u0019\u0001a\u0004\u000b\u0011\u0005A\u0011R\"A\t\u000b\u0005\rA\u0011BA\n\u0012\u0005\u00159VMY+J!\t)\u0002$D\u0001\u0017\u0015\t9\u0002\"\u0001\u0005j]R,'O\\1m\u0013\tIbCA\u0004M_\u001e<\u0017N\\4\t\u0011\u0015\u0001!Q1A\u0005\u0002q\u0019\u0001!F\u0001\u001e!\tqr$D\u0001\u0005\u0013\t\u0001CA\u0001\u0004NCN$XM\u001d\u0005\tE\u0001\u0011\t\u0011)A\u0005;\u00059Q.Y:uKJ\u0004\u0003\u0002\u0003\u0013\u0001\u0005\u0003\u0005\u000b\u0011B\u0013\u0002\u001bI,\u0017/^3ti\u0016$\u0007k\u001c:u!\t1\u0013&D\u0001(\u0015\u0005A\u0013!B:dC2\f\u0017B\u0001\u0016(\u0005\rIe\u000e\u001e\u0005\u0006Y\u0001!\t!L\u0001\u0007y%t\u0017\u000e\u001e \u0015\u00079\u0002\u0014\u0007\u0005\u00020\u00015\t!\u0001C\u0003\u0006W\u0001\u0007Q\u0004C\u0003%W\u0001\u0007Q\u0005C\u00044\u0001\t\u0007I\u0011\u0001\u001b\u0002#5\f7\u000f^3s\u000b:$\u0007o\\5oiJ+g-F\u00016!\t1\u0014(D\u00018\u0015\tA\u0004\"A\u0002sa\u000eL!AO\u001c\u0003\u001dI\u00038-\u00128ea>Lg\u000e\u001e*fM\"1A\b\u0001Q\u0001\nU\n!#\\1ti\u0016\u0014XI\u001c3q_&tGOU3gA!9a\b\u0001b\u0001\n\u0003y\u0014aC6jY2,e.\u00192mK\u0012,\u0012\u0001\u0011\t\u0003M\u0005K!AQ\u0014\u0003\u000f\t{w\u000e\\3b]\"1A\t\u0001Q\u0001\n\u0001\u000bAb[5mY\u0016s\u0017M\u00197fI\u0002BQA\u0012\u0001\u0005\u0002\u001d\u000b!\"\u001b8ji&\fG.\u001b>f)\u0005A\u0005C\u0001\u0014J\u0013\tQuE\u0001\u0003V]&$\b\"\u0002'\u0001\t\u00039\u0015\u0001C1eIB\u0013x\u000e_=\t\u000b9\u0003A\u0011A(\u0002\u001b%$Gk\\+j\u0003\u0012$'/Z:t)\t\u0001&\fE\u0002'#NK!AU\u0014\u0003\r=\u0003H/[8o!\t!vK\u0004\u0002'+&\u0011akJ\u0001\u0007!J,G-\u001a4\n\u0005aK&AB*ue&twM\u0003\u0002WO!)1,\u0014a\u0001'\u0006\u0011\u0011\u000eZ\u0004\u0007;\nA\t\u0001\u00020\u0002\u00175\u000b7\u000f^3s/\u0016\u0014W+\u0013\t\u0003_}3a!\u0001\u0002\t\u0002\u0011\u00017CA0b!\t1#-\u0003\u0002dO\t1\u0011I\\=SK\u001aDQ\u0001L0\u0005\u0002\u0015$\u0012A\u0018\u0005\bO~\u0013\r\u0011\"\u0003i\u0003M\u0019F+\u0011+J\u0007~\u0013ViU(V%\u000e+u\fR%S+\u0005I\u0007C\u00016p\u001b\u0005Y'B\u00017n\u0003\u0011a\u0017M\\4\u000b\u00039\fAA[1wC&\u0011\u0001l\u001b\u0005\u0007c~\u0003\u000b\u0011B5\u0002)M#\u0016\tV%D?J+5kT+S\u0007\u0016{F)\u0013*!\u0001"
)
public class MasterWebUI extends WebUI {
   private final Master master;
   private final RpcEndpointRef masterEndpointRef;
   private final boolean killEnabled;

   public Master master() {
      return this.master;
   }

   public RpcEndpointRef masterEndpointRef() {
      return this.masterEndpointRef;
   }

   public boolean killEnabled() {
      return this.killEnabled;
   }

   public void initialize() {
      MasterPage masterPage = new MasterPage(this);
      this.attachPage(new ApplicationPage(this));
      this.attachPage(masterPage);
      this.attachHandler(JettyUtils$.MODULE$.createStaticHandler(MasterWebUI$.MODULE$.org$apache$spark$deploy$master$ui$MasterWebUI$$STATIC_RESOURCE_DIR(), "/static"));
      String x$11 = "/app/kill";
      String x$12 = "/";
      Function1 x$13 = new Serializable(this, masterPage) {
         public static final long serialVersionUID = 0L;
         private final MasterPage masterPage$1;

         public final void apply(HttpServletRequest request) {
            this.masterPage$1.handleAppKillRequest(request);
         }

         public {
            this.masterPage$1 = masterPage$1;
         }
      };
      Set x$14 = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"POST"})));
      String x$15 = JettyUtils$.MODULE$.createRedirectHandler$default$4();
      this.attachHandler(JettyUtils$.MODULE$.createRedirectHandler(x$11, x$12, x$13, x$15, x$14));
      String x$16 = "/driver/kill";
      String x$17 = "/";
      Function1 x$18 = new Serializable(this, masterPage) {
         public static final long serialVersionUID = 0L;
         private final MasterPage masterPage$1;

         public final void apply(HttpServletRequest request) {
            this.masterPage$1.handleDriverKillRequest(request);
         }

         public {
            this.masterPage$1 = masterPage$1;
         }
      };
      Set x$19 = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"POST"})));
      String x$20 = JettyUtils$.MODULE$.createRedirectHandler$default$4();
      this.attachHandler(JettyUtils$.MODULE$.createRedirectHandler(x$16, x$17, x$18, x$20, x$19));
   }

   public void addProxy() {
      ServletContextHandler handler = JettyUtils$.MODULE$.createProxyHandler(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MasterWebUI $outer;

         public final Option<String> apply(String id) {
            return this.$outer.idToUiAddress(id);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.attachHandler(handler);
   }

   public Option<String> idToUiAddress(String id) {
      DeployMessages.MasterStateResponse state = (DeployMessages.MasterStateResponse)this.masterEndpointRef().askSync(DeployMessages.RequestMasterState$.MODULE$, ClassTag$.MODULE$.apply(DeployMessages.MasterStateResponse.class));
      Option maybeWorkerUiAddress = Predef$.MODULE$.refArrayOps(state.workers()).find(new Serializable(this, id) {
         public static final long serialVersionUID = 0L;
         private final String id$1;

         public final boolean apply(WorkerInfo x$1) {
            boolean var3;
            label23: {
               String var10000 = x$1.id();
               String var2 = this.id$1;
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
            this.id$1 = id$1;
         }
      }).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(WorkerInfo x$2) {
            return x$2.webUiAddress();
         }
      });
      Option maybeAppUiAddress = Predef$.MODULE$.refArrayOps(state.activeApps()).find(new Serializable(this, id) {
         public static final long serialVersionUID = 0L;
         private final String id$1;

         public final boolean apply(ApplicationInfo x$3) {
            boolean var3;
            label23: {
               String var10000 = x$3.id();
               String var2 = this.id$1;
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
            this.id$1 = id$1;
         }
      }).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(ApplicationInfo x$4) {
            return x$4.desc().appUiUrl();
         }
      });
      return maybeWorkerUiAddress.<String>orElse(new Serializable(this, maybeAppUiAddress) {
         public static final long serialVersionUID = 0L;
         private final Option maybeAppUiAddress$1;

         public final Option<String> apply() {
            return this.maybeAppUiAddress$1;
         }

         public {
            this.maybeAppUiAddress$1 = maybeAppUiAddress$1;
         }
      });
   }

   public MasterWebUI(Master master, int requestedPort) {
      this.master = master;
      SecurityManager x$5 = master.securityMgr();
      SSLOptions x$6 = master.securityMgr().getSSLOptions("standalone");
      SparkConf x$8 = master.conf();
      String x$9 = "MasterUI";
      String x$10 = WebUI$.MODULE$.$lessinit$greater$default$5();
      super(x$5, x$6, requestedPort, x$8, x$10, x$9);
      this.masterEndpointRef = master.self();
      this.killEnabled = master.conf().getBoolean("spark.ui.killEnabled", true);
      this.initialize();
   }
}
