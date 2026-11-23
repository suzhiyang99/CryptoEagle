package org.apache.spark.deploy.history;

import java.util.NoSuchElementException;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.config.package$;
import org.apache.spark.status.api.v1.ApiRootResource$;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.status.api.v1.UIRoot;
import org.apache.spark.status.api.v1.UIRoot$class;
import org.apache.spark.ui.JettyUtils$;
import org.apache.spark.ui.SparkUI;
import org.apache.spark.ui.SparkUI$;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUI;
import org.apache.spark.ui.WebUI$;
import org.apache.spark.util.SystemClock;
import org.spark_project.jetty.servlet.ServletContextHandler;
import org.spark_project.jetty.servlet.ServletHolder;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.util.control.NonFatal$;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;

@ScalaSignature(
   bytes = "\u0006\u0001\t-d\u0001B\u0001\u0003\u00015\u0011Q\u0002S5ti>\u0014\u0018pU3sm\u0016\u0014(BA\u0002\u0005\u0003\u001dA\u0017n\u001d;pefT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\n\u0006\u00019!\"\u0004\n\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0003#\u0019\t!!^5\n\u0005M\u0001\"!B,fEVK\u0005CA\u000b\u0019\u001b\u00051\"BA\f\u0007\u0003!Ig\u000e^3s]\u0006d\u0017BA\r\u0017\u0005\u001daunZ4j]\u001e\u0004\"a\u0007\u0012\u000e\u0003qQ!!\b\u0010\u0002\u0005Y\f$BA\u0010!\u0003\r\t\u0007/\u001b\u0006\u0003C\u0019\taa\u001d;biV\u001c\u0018BA\u0012\u001d\u0005\u0019)\u0016JU8piB\u0011QEJ\u0007\u0002\u0005%\u0011qE\u0001\u0002\u001b\u0003B\u0004H.[2bi&|gnQ1dQ\u0016|\u0005/\u001a:bi&|gn\u001d\u0005\tS\u0001\u0011\t\u0011)A\u0005U\u0005!1m\u001c8g!\tYC&D\u0001\u0007\u0013\ticAA\u0005Ta\u0006\u00148nQ8oM\"Aq\u0006\u0001B\u0001B\u0003%\u0001'\u0001\u0005qe>4\u0018\u000eZ3s!\t)\u0013'\u0003\u00023\u0005\tQ\u0012\t\u001d9mS\u000e\fG/[8o\u0011&\u001cHo\u001c:z!J|g/\u001b3fe\"IA\u0007\u0001B\u0001B\u0003%Q\u0007O\u0001\u0010g\u0016\u001cWO]5us6\u000bg.Y4feB\u00111FN\u0005\u0003o\u0019\u0011qbU3dkJLG/_'b]\u0006<WM]\u0005\u0003iIA\u0001B\u000f\u0001\u0003\u0002\u0003\u0006IaO\u0001\u0005a>\u0014H\u000f\u0005\u0002=\u007f5\tQHC\u0001?\u0003\u0015\u00198-\u00197b\u0013\t\u0001UHA\u0002J]RDQA\u0011\u0001\u0005\u0002\r\u000ba\u0001P5oSRtD#\u0002#F\r\u001eC\u0005CA\u0013\u0001\u0011\u0015I\u0013\t1\u0001+\u0011\u0015y\u0013\t1\u00011\u0011\u0015!\u0014\t1\u00016\u0011\u0015Q\u0014\t1\u0001<\u0011\u001dQ\u0005A1A\u0005\n-\u000bAC]3uC&tW\rZ!qa2L7-\u0019;j_:\u001cX#A\u001e\t\r5\u0003\u0001\u0015!\u0003<\u0003U\u0011X\r^1j]\u0016$\u0017\t\u001d9mS\u000e\fG/[8og\u0002B\u0001b\u0014\u0001C\u0002\u0013\u0005!aS\u0001\u0010[\u0006D\u0018\t\u001d9mS\u000e\fG/[8og\"1\u0011\u000b\u0001Q\u0001\nm\n\u0001#\\1y\u0003B\u0004H.[2bi&|gn\u001d\u0011\t\u000fM\u0003!\u0019!C\u0005)\u0006A\u0011\r\u001d9DC\u000eDW-F\u0001V!\t)c+\u0003\u0002X\u0005\t\u0001\u0012\t\u001d9mS\u000e\fG/[8o\u0007\u0006\u001c\u0007.\u001a\u0005\u00073\u0002\u0001\u000b\u0011B+\u0002\u0013\u0005\u0004\boQ1dQ\u0016\u0004\u0003bB.\u0001\u0005\u0004%\t\u0001X\u0001\rG\u0006\u001c\u0007.Z'fiJL7m]\u000b\u0002;B\u0011QEX\u0005\u0003?\n\u0011AbQ1dQ\u0016lU\r\u001e:jGNDa!\u0019\u0001!\u0002\u0013i\u0016!D2bG\",W*\u001a;sS\u000e\u001c\b\u0005C\u0004d\u0001\t\u0007I\u0011\u00023\u0002\u001b1|\u0017\rZ3s'\u0016\u0014h\u000f\\3u+\u0005)\u0007C\u00014n\u001b\u00059'B\u00015j\u0003\u0011AG\u000f\u001e9\u000b\u0005)\\\u0017aB:feZdW\r\u001e\u0006\u0002Y\u0006)!.\u0019<bq&\u0011an\u001a\u0002\f\u0011R$\boU3sm2,G\u000f\u0003\u0004q\u0001\u0001\u0006I!Z\u0001\u000fY>\fG-\u001a:TKJ4H.\u001a;!\u0011\u0015\u0011\b\u0001\"\u0011t\u0003-9\u0018\u000e\u001e5Ta\u0006\u00148.V%\u0016\u0005QDH#B;\u0002\u0014\u0005\u0015Bc\u0001<\u0002\u0004A\u0011q\u000f\u001f\u0007\u0001\t\u0015I\u0018O1\u0001{\u0005\u0005!\u0016CA>\u007f!\taD0\u0003\u0002~{\t9aj\u001c;iS:<\u0007C\u0001\u001f\u0000\u0013\r\t\t!\u0010\u0002\u0004\u0003:L\bbBA\u0003c\u0002\u0007\u0011qA\u0001\u0003M:\u0004b\u0001PA\u0005\u0003\u001b1\u0018bAA\u0006{\tIa)\u001e8di&|g.\r\t\u0004\u001f\u0005=\u0011bAA\t!\t91\u000b]1sWVK\u0005bBA\u000bc\u0002\u0007\u0011qC\u0001\u0006CB\u0004\u0018\n\u001a\t\u0005\u00033\tyBD\u0002=\u00037I1!!\b>\u0003\u0019\u0001&/\u001a3fM&!\u0011\u0011EA\u0012\u0005\u0019\u0019FO]5oO*\u0019\u0011QD\u001f\t\u000f\u0005\u001d\u0012\u000f1\u0001\u0002*\u0005I\u0011\r\u001e;f[B$\u0018\n\u001a\t\u0006y\u0005-\u0012qC\u0005\u0004\u0003[i$AB(qi&|g\u000eC\u0004\u00022\u0001!\t!a\r\u0002\u0015%t\u0017\u000e^5bY&TX\r\u0006\u0002\u00026A\u0019A(a\u000e\n\u0007\u0005eRH\u0001\u0003V]&$\bbBA\u001f\u0001\u0011\u0005\u00131G\u0001\u0005E&tG\rC\u0004\u0002B\u0001!\t%a\r\u0002\tM$x\u000e\u001d\u0005\b\u0003\u000b\u0002A\u0011IA$\u00035\tG\u000f^1dQN\u0003\u0018M]6V\u0013RQ\u0011QGA%\u0003\u0017\ni%a\u0014\t\u0011\u0005U\u00111\ta\u0001\u0003/A\u0001\"a\n\u0002D\u0001\u0007\u0011\u0011\u0006\u0005\b#\u0005\r\u0003\u0019AA\u0007\u0011!\t\t&a\u0011A\u0002\u0005M\u0013!C2p[BdW\r^3e!\ra\u0014QK\u0005\u0004\u0003/j$a\u0002\"p_2,\u0017M\u001c\u0005\b\u00037\u0002A\u0011IA/\u00035!W\r^1dQN\u0003\u0018M]6V\u0013RA\u0011QGA0\u0003C\n\u0019\u0007\u0003\u0005\u0002\u0016\u0005e\u0003\u0019AA\f\u0011!\t9#!\u0017A\u0002\u0005%\u0002bB\t\u0002Z\u0001\u0007\u0011Q\u0002\u0005\b\u0003O\u0002A\u0011IA5\u0003!9W\r^!qaVKECBA6\u0003g\n)\bE\u0003=\u0003W\ti\u0007E\u0002&\u0003_J1!!\u001d\u0003\u0005-au.\u00193fI\u0006\u0003\b/V%\t\u0011\u0005U\u0011Q\ra\u0001\u0003/A\u0001\"a\n\u0002f\u0001\u0007\u0011\u0011\u0006\u0005\b\u0003s\u0002A\u0011AA>\u0003I9W\r^!qa2L7-\u0019;j_:d\u0015n\u001d;\u0015\u0005\u0005u\u0004CBA@\u0003\u001f\u000b)J\u0004\u0003\u0002\u0002\u0006-e\u0002BAB\u0003\u0013k!!!\"\u000b\u0007\u0005\u001dE\"\u0001\u0004=e>|GOP\u0005\u0002}%\u0019\u0011QR\u001f\u0002\u000fA\f7m[1hK&!\u0011\u0011SAJ\u0005!IE/\u001a:bi>\u0014(bAAG{A\u00191$a&\n\u0007\u0005eEDA\bBaBd\u0017nY1uS>t\u0017J\u001c4p\u0011\u001d\ti\n\u0001C\u0001\u0003?\u000b\u0001dZ3u\u000bZ,g\u000e\u001e'pON,f\u000eZ3s!J|7-Z:t)\u0005Y\u0004bBAR\u0001\u0011\u0005\u0011QU\u0001\u0013O\u0016$H*Y:u+B$\u0017\r^3e)&lW\r\u0006\u0002\u0002(B\u0019A(!+\n\u0007\u0005-VH\u0001\u0003M_:<\u0007bBAX\u0001\u0011\u0005\u0011\u0011W\u0001\u0017O\u0016$\u0018\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000eT5tiV\u0011\u0011Q\u0010\u0005\b\u0003k\u0003A\u0011AA\\\u0003I9W\r^!qa2L7-\u0019;j_:LeNZ8\u0015\t\u0005e\u00161\u0018\t\u0006y\u0005-\u0012Q\u0013\u0005\t\u0003+\t\u0019\f1\u0001\u0002\u0018!9\u0011q\u0018\u0001\u0005B\u0005\u0005\u0017AD<sSR,WI^3oi2{wm\u001d\u000b\t\u0003k\t\u0019-!2\u0002H\"A\u0011QCA_\u0001\u0004\t9\u0002\u0003\u0005\u0002(\u0005u\u0006\u0019AA\u0015\u0011!\tI-!0A\u0002\u0005-\u0017!\u0003>jaN#(/Z1n!\u0011\ti-a7\u000e\u0005\u0005='\u0002BAi\u0003'\f1A_5q\u0015\u0011\t).a6\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u00033\fAA[1wC&!\u0011Q\\Ah\u0005=Q\u0016\u000e](viB,Ho\u0015;sK\u0006l\u0007bBAq\u0001\u0011\u0005\u00111]\u0001\u0011K6\u0004H/\u001f'jgRLgn\u001a%u[2$\"!!:\u0011\r\u0005}\u0014q]Av\u0013\u0011\tI/a%\u0003\u0007M+\u0017\u000f\u0005\u0003\u0002n\u0006MXBAAx\u0015\r\t\t0P\u0001\u0004q6d\u0017\u0002BA{\u0003_\u0014AAT8eK\"9\u0011\u0011 \u0001\u0005\u0002\u0005m\u0018!E4fiB\u0013xN^5eKJ\u001cuN\u001c4jOR\u0011\u0011Q \t\t\u00033\ty0a\u0006\u0002\u0018%!!\u0011AA\u0012\u0005\ri\u0015\r\u001d\u0005\b\u0005\u000b\u0001A\u0011\u0002B\u0004\u0003%aw.\u00193BaB,\u0016\u000e\u0006\u0004\u0002T\t%!1\u0002\u0005\t\u0003+\u0011\u0019\u00011\u0001\u0002\u0018!A\u0011q\u0005B\u0002\u0001\u0004\tI\u0003C\u0004\u0003\u0010\u0001!\tE!\u0005\u0002\u0011Q|7\u000b\u001e:j]\u001e$\"!a\u0006\b\u000f\tU!\u0001#\u0001\u0003\u0018\u0005i\u0001*[:u_JL8+\u001a:wKJ\u00042!\nB\r\r\u0019\t!\u0001#\u0001\u0003\u001cM)!\u0011\u0004B\u000f)A\u0019AHa\b\n\u0007\t\u0005RH\u0001\u0004B]f\u0014VM\u001a\u0005\b\u0005\neA\u0011\u0001B\u0013)\t\u00119\u0002C\u0005*\u00053\u0011\r\u0011\"\u0003\u0003*U\t!\u0006\u0003\u0005\u0003.\te\u0001\u0015!\u0003+\u0003\u0015\u0019wN\u001c4!\u0011)\u0011\tD!\u0007C\u0002\u0013\u0005!1G\u0001\u000f+&{\u0006+\u0011+I?B\u0013VIR%Y+\t\u0011)\u0004\u0005\u0003\u00038\tuRB\u0001B\u001d\u0015\u0011\u0011Y$a6\u0002\t1\fgnZ\u0005\u0005\u0003C\u0011I\u0004C\u0005\u0003B\te\u0001\u0015!\u0003\u00036\u0005yQ+S0Q\u0003RCu\f\u0015*F\r&C\u0006\u0005\u0003\u0005\u0003F\teA\u0011\u0001B$\u0003\u0011i\u0017-\u001b8\u0015\t\u0005U\"\u0011\n\u0005\t\u0005\u0017\u0012\u0019\u00051\u0001\u0003N\u0005Q\u0011M]4TiJLgnZ:\u0011\u000bq\u0012y%a\u0006\n\u0007\tESHA\u0003BeJ\f\u0017\u0010C\u0005\u0003V\teA\u0011\u0001\u0002\u0003X\u0005)2M]3bi\u0016\u001cVmY;sSRLX*\u00198bO\u0016\u0014HcA\u001b\u0003Z!9!1\fB*\u0001\u0004Q\u0013AB2p]\u001aLw\r\u0003\u0005\u0003`\teA\u0011AA\u001a\u00031Ig.\u001b;TK\u000e,(/\u001b;z\u0011%\u0011\u0019G!\u0007\u0005\u0002\t\u0011)'A\u0007hKR\fE\u000f^3naR,&+\u0013\u000b\u0007\u0003/\u00119G!\u001b\t\u0011\u0005U!\u0011\ra\u0001\u0003/A\u0001\"a\n\u0003b\u0001\u0007\u0011\u0011\u0006"
)
public class HistoryServer extends WebUI implements UIRoot, ApplicationCacheOperations {
   private final SparkConf conf;
   private final ApplicationHistoryProvider provider;
   private final int retainedApplications;
   private final int maxApplications;
   private final ApplicationCache appCache;
   private final CacheMetrics cacheMetrics;
   private final HttpServlet loaderServlet;

   public static void initSecurity() {
      HistoryServer$.MODULE$.initSecurity();
   }

   public static void main(String[] var0) {
      HistoryServer$.MODULE$.main(var0);
   }

   public static String UI_PATH_PREFIX() {
      return HistoryServer$.MODULE$.UI_PATH_PREFIX();
   }

   private int retainedApplications() {
      return this.retainedApplications;
   }

   public int maxApplications() {
      return this.maxApplications;
   }

   private ApplicationCache appCache() {
      return this.appCache;
   }

   public CacheMetrics cacheMetrics() {
      return this.cacheMetrics;
   }

   private HttpServlet loaderServlet() {
      return this.loaderServlet;
   }

   public <T> T withSparkUI(String appId, Option<String> attemptId, Function1<SparkUI, T> fn) {
      return (T)this.appCache().withSparkUI(appId, attemptId, fn);
   }

   public void initialize() {
      this.attachPage(new HistoryPage(this));
      this.attachHandler(ApiRootResource$.MODULE$.getServletHandler(this));
      this.attachHandler(JettyUtils$.MODULE$.createStaticHandler(SparkUI$.MODULE$.STATIC_RESOURCE_DIR(), "/static"));
      ServletContextHandler contextHandler = new ServletContextHandler();
      contextHandler.setContextPath(HistoryServer$.MODULE$.UI_PATH_PREFIX());
      contextHandler.addServlet(new ServletHolder(this.loaderServlet()), "/*");
      this.attachHandler(contextHandler);
   }

   public void bind() {
      super.bind();
   }

   public void stop() {
      super.stop();
      this.provider.stop();
   }

   public void attachSparkUI(String appId, Option<String> attemptId, SparkUI ui, boolean completed) {
      Predef$.MODULE$.assert(this.serverInfo().isDefined(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "HistoryServer must be bound before attaching SparkUIs";
         }
      });
      ui.getHandlers().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HistoryServer $outer;

         public final void apply(ServletContextHandler handler) {
            this.$outer.attachHandler(handler);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      JettyUtils$.MODULE$.addFilters(ui.getHandlers(), this.conf);
   }

   public void detachSparkUI(String appId, Option<String> attemptId, SparkUI ui) {
      Predef$.MODULE$.assert(this.serverInfo().isDefined(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "HistoryServer must be bound before detaching SparkUIs";
         }
      });
      ui.getHandlers().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HistoryServer $outer;

         public final void apply(ServletContextHandler handler) {
            this.$outer.detachHandler(handler);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.provider.onUIDetached(appId, attemptId, ui);
   }

   public Option<LoadedAppUI> getAppUI(String appId, Option<String> attemptId) {
      return this.provider.getAppUI(appId, attemptId);
   }

   public Iterator<ApplicationInfo> getApplicationList() {
      return this.provider.getListing();
   }

   public int getEventLogsUnderProcess() {
      return this.provider.getEventLogsUnderProcess();
   }

   public long getLastUpdatedTime() {
      return this.provider.getLastUpdatedTime();
   }

   public Iterator<ApplicationInfo> getApplicationInfoList() {
      return this.getApplicationList();
   }

   public Option<ApplicationInfo> getApplicationInfo(String appId) {
      return this.provider.getApplicationInfo(appId);
   }

   public void writeEventLogs(String appId, Option<String> attemptId, ZipOutputStream zipStream) {
      this.provider.writeEventLogs(appId, attemptId, zipStream);
   }

   public Seq<Node> emptyListingHtml() {
      return this.provider.getEmptyListingHtml();
   }

   public Map<String, String> getProviderConfig() {
      return this.provider.getConfig();
   }

   public boolean org$apache$spark$deploy$history$HistoryServer$$loadAppUi(String appId, Option<String> attemptId) {
      boolean var10000;
      try {
         this.appCache().withSparkUI(appId, attemptId, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(SparkUI x$2) {
            }
         });
         var10000 = true;
      } catch (Throwable var9) {
         Option var5 = NonFatal$.MODULE$.unapply(var9);
         if (!var5.isEmpty()) {
            Throwable e = (Throwable)var5.get();
            if (e instanceof NoSuchElementException) {
               boolean var7 = false;
               var10000 = var7;
               return var10000;
            }
         }

         throw var9;
      }

      return var10000;
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n      | History Server;\n      | provider = ", "\n      | cache = ", "\n    "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.provider, this.appCache()}))))).stripMargin();
   }

   public HistoryServer(SparkConf conf, ApplicationHistoryProvider provider, SecurityManager securityManager, int port) {
      super(securityManager, securityManager.getSSLOptions("historyServer"), port, conf, WebUI$.MODULE$.$lessinit$greater$default$5(), WebUI$.MODULE$.$lessinit$greater$default$6());
      this.conf = conf;
      this.provider = provider;
      UIRoot$class.$init$(this);
      this.retainedApplications = conf.getInt("spark.history.retainedApplications", 50);
      this.maxApplications = BoxesRunTime.unboxToInt(conf.get(package$.MODULE$.HISTORY_UI_MAX_APPS()));
      this.appCache = new ApplicationCache(this, this.retainedApplications(), new SystemClock());
      this.cacheMetrics = this.appCache().metrics();
      this.loaderServlet = new HttpServlet(this) {
         // $FF: synthetic field
         private final HistoryServer $outer;

         public void doGet(HttpServletRequest req, HttpServletResponse res) {
            String[] parts = ((String)Option$.MODULE$.apply(req.getPathInfo()).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "";
               }
            })).split("/");
            if (parts.length < 2) {
               res.sendError(400, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unexpected path info in request (URI = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{req.getRequestURI()})));
            } else {
               String appId = parts[1];
               Option attemptId = (Option)(parts.length >= 3 ? new Some(parts[2]) : None$.MODULE$);
               if (!this.$outer.org$apache$spark$deploy$history$HistoryServer$$loadAppUi(appId, None$.MODULE$) && (!attemptId.isDefined() || !this.$outer.org$apache$spark$deploy$history$HistoryServer$$loadAppUi(appId, attemptId))) {
                  MetaData $md = .MODULE$;
                  MetaData var10 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
                  TopScope var10005 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("Application "));
                  $buf.$amp$plus(appId);
                  $buf.$amp$plus(new Text(" not found."));
                  Elem msg = new Elem((String)null, "div", var10, var10005, false, $buf);
                  res.setStatus(404);
                  UIUtils$.MODULE$.basicSparkPage(new Serializable(this, msg) {
                     public static final long serialVersionUID = 0L;
                     private final Elem msg$1;

                     public final Elem apply() {
                        return this.msg$1;
                     }

                     public {
                        this.msg$1 = msg$1;
                     }
                  }, "Not Found", UIUtils$.MODULE$.basicSparkPage$default$3()).foreach(new Serializable(this, res) {
                     public static final long serialVersionUID = 0L;
                     private final HttpServletResponse res$1;

                     public final void apply(Node n) {
                        this.res$1.getWriter().write(n.toString());
                     }

                     public {
                        this.res$1 = res$1;
                     }
                  });
               } else {
                  String requestURI = (new StringBuilder()).append((Object)req.getRequestURI()).append(Option$.MODULE$.apply(req.getQueryString()).map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(String x$1) {
                        return (new StringBuilder()).append((Object)"?").append((Object)x$1).toString();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "";
                     }
                  })).toString();
                  res.sendRedirect(res.encodeRedirectURL(requestURI));
               }
            }
         }

         public void doTrace(HttpServletRequest req, HttpServletResponse res) {
            res.sendError(405);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.initialize();
   }
}
