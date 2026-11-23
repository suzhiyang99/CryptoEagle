package org.apache.spark.deploy.worker.ui;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SSLOptions;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.worker.Worker;
import org.apache.spark.rpc.RpcTimeout;
import org.apache.spark.ui.JettyUtils$;
import org.apache.spark.ui.WebUI;
import org.apache.spark.ui.WebUI$;
import org.apache.spark.util.RpcUtils$;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001=4Q!\u0001\u0002\u0001\t9\u00111bV8sW\u0016\u0014x+\u001a2V\u0013*\u00111\u0001B\u0001\u0003k&T!!\u0002\u0004\u0002\r]|'o[3s\u0015\t9\u0001\"\u0001\u0004eKBdw.\u001f\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sON\u0019\u0001a\u0004\u000b\u0011\u0005A\u0011R\"A\t\u000b\u0005\rA\u0011BA\n\u0012\u0005\u00159VMY+J!\t)\u0002$D\u0001\u0017\u0015\t9\u0002\"\u0001\u0005j]R,'O\\1m\u0013\tIbCA\u0004M_\u001e<\u0017N\\4\t\u0011\u0015\u0001!Q1A\u0005\u0002q\u0019\u0001!F\u0001\u001e!\tqr$D\u0001\u0005\u0013\t\u0001CA\u0001\u0004X_J\\WM\u001d\u0005\tE\u0001\u0011\t\u0011)A\u0005;\u00059qo\u001c:lKJ\u0004\u0003\u0002\u0003\u0013\u0001\u0005\u000b\u0007I\u0011A\u0013\u0002\u000f]|'o\u001b#jeV\ta\u0005\u0005\u0002(Y5\t\u0001F\u0003\u0002*U\u0005\u0011\u0011n\u001c\u0006\u0002W\u0005!!.\u0019<b\u0013\ti\u0003F\u0001\u0003GS2,\u0007\u0002C\u0018\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0014\u0002\u0011]|'o\u001b#je\u0002B\u0001\"\r\u0001\u0003\u0002\u0003\u0006IAM\u0001\u000ee\u0016\fX/Z:uK\u0012\u0004vN\u001d;\u0011\u0005M2T\"\u0001\u001b\u000b\u0003U\nQa]2bY\u0006L!a\u000e\u001b\u0003\u0007%sG\u000fC\u0003:\u0001\u0011\u0005!(\u0001\u0004=S:LGO\u0010\u000b\u0005wurt\b\u0005\u0002=\u00015\t!\u0001C\u0003\u0006q\u0001\u0007Q\u0004C\u0003%q\u0001\u0007a\u0005C\u00032q\u0001\u0007!\u0007\u0003\u0005B\u0001\t\u0007I\u0011\u0001\u0002C\u0003\u001d!\u0018.\\3pkR,\u0012a\u0011\t\u0003\t\u001ek\u0011!\u0012\u0006\u0003\r\"\t1A\u001d9d\u0013\tAUI\u0001\u0006Sa\u000e$\u0016.\\3pkRDaA\u0013\u0001!\u0002\u0013\u0019\u0015\u0001\u0003;j[\u0016|W\u000f\u001e\u0011\t\u000b1\u0003A\u0011A'\u0002\u0015%t\u0017\u000e^5bY&TX\rF\u0001O!\t\u0019t*\u0003\u0002Qi\t!QK\\5u\u000f\u0019\u0011&\u0001#\u0001\u0005'\u0006Yqk\u001c:lKJ<VMY+J!\taDK\u0002\u0004\u0002\u0005!\u0005A!V\n\u0003)Z\u0003\"aM,\n\u0005a#$AB!osJ+g\rC\u0003:)\u0012\u0005!\fF\u0001T\u0011\u001daFK1A\u0005\u0002u\u000bAc\u0015+B)&\u001buLU#T\u001fV\u00136)R0C\u0003N+U#\u00010\u0011\u0005}\u0013W\"\u00011\u000b\u0005\u0005T\u0013\u0001\u00027b]\u001eL!a\u00191\u0003\rM#(/\u001b8h\u0011\u0019)G\u000b)A\u0005=\u0006)2\u000bV!U\u0013\u000e{&+R*P+J\u001bUi\u0018\"B'\u0016\u0003\u0003bB4U\u0005\u0004%\t\u0001[\u0001\u0019\t\u00163\u0015)\u0016'U?J+E+Q%O\u000b\u0012{FIU%W\u000bJ\u001bV#\u0001\u001a\t\r)$\u0006\u0015!\u00033\u0003e!UIR!V\u0019R{&+\u0012+B\u0013:+Ei\u0018#S\u0013Z+%k\u0015\u0011\t\u000f1$&\u0019!C\u0001Q\u0006QB)\u0012$B+2#vLU#U\u0003&sU\tR0F1\u0016\u001bU\u000bV(S'\"1a\u000e\u0016Q\u0001\nI\n1\u0004R#G\u0003VcEk\u0018*F)\u0006Ke*\u0012#`\u000bb+5)\u0016+P%N\u0003\u0003"
)
public class WorkerWebUI extends WebUI {
   private final Worker worker;
   private final File workDir;
   private final RpcTimeout timeout;

   public static int DEFAULT_RETAINED_EXECUTORS() {
      return WorkerWebUI$.MODULE$.DEFAULT_RETAINED_EXECUTORS();
   }

   public static int DEFAULT_RETAINED_DRIVERS() {
      return WorkerWebUI$.MODULE$.DEFAULT_RETAINED_DRIVERS();
   }

   public static String STATIC_RESOURCE_BASE() {
      return WorkerWebUI$.MODULE$.STATIC_RESOURCE_BASE();
   }

   public Worker worker() {
      return this.worker;
   }

   public File workDir() {
      return this.workDir;
   }

   public RpcTimeout timeout() {
      return this.timeout;
   }

   public void initialize() {
      LogPage logPage = new LogPage(this);
      this.attachPage(logPage);
      this.attachPage(new WorkerPage(this));
      this.attachHandler(JettyUtils$.MODULE$.createStaticHandler(WorkerWebUI$.MODULE$.STATIC_RESOURCE_BASE(), "/static"));
      this.attachHandler(JettyUtils$.MODULE$.createServletHandler("/log", JettyUtils$.MODULE$.textResponderToServlet(new Serializable(this, logPage) {
         public static final long serialVersionUID = 0L;
         private final LogPage logPage$1;

         public final String apply(HttpServletRequest request) {
            return this.logPage$1.renderLog(request);
         }

         public {
            this.logPage$1 = logPage$1;
         }
      }), this.worker().securityMgr(), this.worker().conf(), JettyUtils$.MODULE$.createServletHandler$default$5()));
   }

   public WorkerWebUI(Worker worker, File workDir, int requestedPort) {
      this.worker = worker;
      this.workDir = workDir;
      SecurityManager x$1 = worker.securityMgr();
      SSLOptions x$2 = worker.securityMgr().getSSLOptions("standalone");
      SparkConf x$4 = worker.conf();
      String x$5 = "WorkerUI";
      String x$6 = WebUI$.MODULE$.$lessinit$greater$default$5();
      super(x$1, x$2, requestedPort, x$4, x$6, x$5);
      this.timeout = RpcUtils$.MODULE$.askRpcTimeout(worker.conf());
      this.initialize();
   }
}
