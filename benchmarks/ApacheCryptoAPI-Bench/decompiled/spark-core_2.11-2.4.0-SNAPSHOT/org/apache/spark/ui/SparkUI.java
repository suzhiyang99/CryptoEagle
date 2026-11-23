package org.apache.spark.ui;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.ApiRootResource$;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.status.api.v1.UIRoot;
import org.apache.spark.status.api.v1.UIRoot$class;
import org.apache.spark.ui.env.EnvironmentTab;
import org.apache.spark.ui.exec.ExecutorsTab;
import org.apache.spark.ui.jobs.JobsTab;
import org.apache.spark.ui.jobs.StagesTab;
import org.apache.spark.ui.storage.StorageTab;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.package$;
import scala.collection.Iterator;
import scala.collection.Iterator$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\tMb!B\u0001\u0003\u0001\u0011Q!aB*qCJ\\W+\u0013\u0006\u0003\u0007\u0011\t!!^5\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u0010+A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\u0006/\u0016\u0014W+\u0013\t\u0003!Mi\u0011!\u0005\u0006\u0003%\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003)E\u0011q\u0001T8hO&tw\r\u0005\u0002\u0017;5\tqC\u0003\u0002\u00193\u0005\u0011a/\r\u0006\u00035m\t1!\u00199j\u0015\taB!\u0001\u0004ti\u0006$Xo]\u0005\u0003=]\u0011a!V%S_>$\b\u0002\u0003\u0011\u0001\u0005\u000b\u0007I\u0011\u0001\u0012\u0002\u000bM$xN]3\u0004\u0001U\t1\u0005\u0005\u0002%K5\t1$\u0003\u0002'7\tq\u0011\t\u001d9Ti\u0006$Xo]*u_J,\u0007\u0002\u0003\u0015\u0001\u0005\u0003\u0005\u000b\u0011B\u0012\u0002\rM$xN]3!\u0011!Q\u0003A!b\u0001\n\u0003Y\u0013AA:d+\u0005a\u0003cA\u00171e5\taFC\u00010\u0003\u0015\u00198-\u00197b\u0013\t\tdF\u0001\u0004PaRLwN\u001c\t\u0003gQj\u0011\u0001B\u0005\u0003k\u0011\u0011Ab\u00159be.\u001cuN\u001c;fqRD\u0001b\u000e\u0001\u0003\u0002\u0003\u0006I\u0001L\u0001\u0004g\u000e\u0004\u0003\u0002C\u001d\u0001\u0005\u000b\u0007I\u0011\u0001\u001e\u0002\t\r|gNZ\u000b\u0002wA\u00111\u0007P\u0005\u0003{\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011}\u0002!\u0011!Q\u0001\nm\nQaY8oM\u0002B\u0011\"\u0011\u0001\u0003\u0002\u0003\u0006IAQ#\u0002\u001fM,7-\u001e:jifl\u0015M\\1hKJ\u0004\"aM\"\n\u0005\u0011#!aD*fGV\u0014\u0018\u000e^=NC:\fw-\u001a:\n\u0005\u0005k\u0001\u0002C$\u0001\u0005\u0003\u0007I\u0011\u0001%\u0002\u000f\u0005\u0004\bOT1nKV\t\u0011\n\u0005\u0002K\u001b:\u0011QfS\u0005\u0003\u0019:\na\u0001\u0015:fI\u00164\u0017B\u0001(P\u0005\u0019\u0019FO]5oO*\u0011AJ\f\u0005\t#\u0002\u0011\t\u0019!C\u0001%\u0006Y\u0011\r\u001d9OC6,w\fJ3r)\t\u0019f\u000b\u0005\u0002.)&\u0011QK\f\u0002\u0005+:LG\u000fC\u0004X!\u0006\u0005\t\u0019A%\u0002\u0007a$\u0013\u0007\u0003\u0005Z\u0001\t\u0005\t\u0015)\u0003J\u0003!\t\u0007\u000f\u001d(b[\u0016\u0004\u0003\u0002C.\u0001\u0005\u000b\u0007I\u0011\u0001%\u0002\u0011\t\f7/\u001a)bi\"D\u0001\"\u0018\u0001\u0003\u0002\u0003\u0006I!S\u0001\nE\u0006\u001cX\rU1uQ\u0002B\u0001b\u0018\u0001\u0003\u0006\u0004%\t\u0001Y\u0001\ngR\f'\u000f\u001e+j[\u0016,\u0012!\u0019\t\u0003[\tL!a\u0019\u0018\u0003\t1{gn\u001a\u0005\tK\u0002\u0011\t\u0011)A\u0005C\u0006Q1\u000f^1siRKW.\u001a\u0011\t\u0011\u001d\u0004!Q1A\u0005\u0002!\u000bq\"\u00199q'B\f'o\u001b,feNLwN\u001c\u0005\tS\u0002\u0011\t\u0011)A\u0005\u0013\u0006\u0001\u0012\r\u001d9Ta\u0006\u00148NV3sg&|g\u000e\t\u0005\u0006W\u0002!I\u0001\\\u0001\u0007y%t\u0017\u000e\u001e \u0015\u00135tw\u000e]9sgR,\bC\u0001\u0007\u0001\u0011\u0015\u0001#\u000e1\u0001$\u0011\u0015Q#\u000e1\u0001-\u0011\u0015I$\u000e1\u0001<\u0011\u0015\t%\u000e1\u0001C\u0011\u00159%\u000e1\u0001J\u0011\u0015Y&\u000e1\u0001J\u0011\u0015y&\u000e1\u0001b\u0011\u00159'\u000e1\u0001J\u0011\u001d9\bA1A\u0005\u0002a\f1b[5mY\u0016s\u0017M\u00197fIV\t\u0011\u0010\u0005\u0002.u&\u00111P\f\u0002\b\u0005>|G.Z1o\u0011\u0019i\b\u0001)A\u0005s\u0006a1.\u001b7m\u000b:\f'\r\\3eA!Iq\u0010\u0001a\u0001\u0002\u0004%\t\u0001S\u0001\u0006CB\u0004\u0018\n\u001a\u0005\f\u0003\u0007\u0001\u0001\u0019!a\u0001\n\u0003\t)!A\u0005baBLEm\u0018\u0013fcR\u00191+a\u0002\t\u0011]\u000b\t!!AA\u0002%Cq!a\u0003\u0001A\u0003&\u0011*\u0001\u0004baBLE\r\t\u0005\n\u0003\u001f\u0001\u0001\u0019!C\u0005\u0003#\tAd\u001d;sK\u0006l\u0017N\\4K_\n\u0004&o\\4sKN\u001cH*[:uK:,'/\u0006\u0002\u0002\u0014A!Q\u0006MA\u000b!\u0011\t9\"!\b\u000e\u0005\u0005e!bAA\u000e\t\u0005I1o\u00195fIVdWM]\u0005\u0005\u0003?\tIBA\u0007Ta\u0006\u00148\u000eT5ti\u0016tWM\u001d\u0005\n\u0003G\u0001\u0001\u0019!C\u0005\u0003K\t\u0001e\u001d;sK\u0006l\u0017N\\4K_\n\u0004&o\\4sKN\u001cH*[:uK:,'o\u0018\u0013fcR\u00191+a\n\t\u0013]\u000b\t#!AA\u0002\u0005M\u0001\u0002CA\u0016\u0001\u0001\u0006K!a\u0005\u0002;M$(/Z1nS:<'j\u001c2Qe><'/Z:t\u0019&\u001cH/\u001a8fe\u0002Bq!a\f\u0001\t\u0003\t\t$\u0001\u0006j]&$\u0018.\u00197ju\u0016$\u0012a\u0015\u0005\u0007\u0003k\u0001A\u0011\u0001%\u0002\u0019\u001d,Go\u00159be.,6/\u001a:\t\r\u0005e\u0002\u0001\"\u0001I\u0003)9W\r^!qa:\u000bW.\u001a\u0005\b\u0003{\u0001A\u0011AA \u0003!\u0019X\r^!qa&#GcA*\u0002B!9\u00111IA\u001e\u0001\u0004I\u0015AA5e\u0011\u001d\t9\u0005\u0001C!\u0003c\tAa\u001d;pa\"9\u00111\n\u0001\u0005B\u00055\u0013aC<ji\"\u001c\u0006/\u0019:l+&+B!a\u0014\u0002XQ1\u0011\u0011KA:\u0003k\"B!a\u0015\u0002jA!\u0011QKA,\u0019\u0001!\u0001\"!\u0017\u0002J\t\u0007\u00111\f\u0002\u0002)F!\u0011QLA2!\ri\u0013qL\u0005\u0004\u0003Cr#a\u0002(pi\"Lgn\u001a\t\u0004[\u0005\u0015\u0014bAA4]\t\u0019\u0011I\\=\t\u0011\u0005-\u0014\u0011\na\u0001\u0003[\n!A\u001a8\u0011\r5\ny'\\A*\u0013\r\t\tH\f\u0002\n\rVt7\r^5p]FBaa`A%\u0001\u0004I\u0005\u0002CA<\u0003\u0013\u0002\r!!\u001f\u0002\u0013\u0005$H/Z7qi&#\u0007cA\u00171\u0013\"9\u0011Q\u0010\u0001\u0005\u0002\u0005}\u0014AF4fi\u0006\u0003\b\u000f\\5dCRLwN\\%oM>d\u0015n\u001d;\u0016\u0005\u0005\u0005\u0005CBAB\u0003'\u000bIJ\u0004\u0003\u0002\u0006\u0006=e\u0002BAD\u0003\u001bk!!!#\u000b\u0007\u0005-\u0015%\u0001\u0004=e>|GOP\u0005\u0002_%\u0019\u0011\u0011\u0013\u0018\u0002\u000fA\f7m[1hK&!\u0011QSAL\u0005!IE/\u001a:bi>\u0014(bAAI]A\u0019a#a'\n\u0007\u0005uuCA\bBaBd\u0017nY1uS>t\u0017J\u001c4p\u0011\u001d\t\t\u000b\u0001C\u0001\u0003G\u000b!cZ3u\u0003B\u0004H.[2bi&|g.\u00138g_R!\u0011QUAT!\u0011i\u0003'!'\t\r}\fy\n1\u0001J\u0011\u001d\tY\u000b\u0001C\u0001\u0003#\tqdZ3u'R\u0014X-Y7j]\u001eTuN\u0019)s_\u001e\u0014Xm]:MSN$XM\\3s\u0011\u001d\ty\u000b\u0001C\u0001\u0003c\u000bqd]3u'R\u0014X-Y7j]\u001eTuN\u0019)s_\u001e\u0014Xm]:MSN$XM\\3s)\r\u0019\u00161\u0017\u0005\t\u0003k\u000bi\u000b1\u0001\u0002\u0016\u0005i1\u000f]1sW2K7\u000f^3oKJ<\u0001\"!/\u0003\u0011\u0003!\u00111X\u0001\b'B\f'o[+J!\ra\u0011Q\u0018\u0004\b\u0003\tA\t\u0001BA`'\u0011\ti,!1\u0011\u00075\n\u0019-C\u0002\u0002F:\u0012a!\u00118z%\u00164\u0007bB6\u0002>\u0012\u0005\u0011\u0011\u001a\u000b\u0003\u0003wC!\"!4\u0002>\n\u0007I\u0011AAh\u00031!UIR!V\u0019R{\u0006k\u0014*U+\t\t\t\u000eE\u0002.\u0003'L1!!6/\u0005\rIe\u000e\u001e\u0005\n\u00033\fi\f)A\u0005\u0003#\fQ\u0002R#G\u0003VcEk\u0018)P%R\u0003\u0003BCAo\u0003{\u0013\r\u0011\"\u0001\u0002`\u0006\u00192\u000bV!U\u0013\u000e{&+R*P+J\u001bUi\u0018#J%V\u0011\u0011\u0011\u001d\t\u0005\u0003G\fi/\u0004\u0002\u0002f*!\u0011q]Au\u0003\u0011a\u0017M\\4\u000b\u0005\u0005-\u0018\u0001\u00026bm\u0006L1ATAs\u0011%\t\t0!0!\u0002\u0013\t\t/\u0001\u000bT)\u0006#\u0016jQ0S\u000bN{UKU\"F?\u0012K%\u000b\t\u0005\u000b\u0003k\fiL1A\u0005\u0002\u0005}\u0017!\u0005#F\r\u0006+F\nV0Q\u001f>cuLT!N\u000b\"I\u0011\u0011`A_A\u0003%\u0011\u0011]\u0001\u0013\t\u00163\u0015)\u0016'U?B{u\nT0O\u00036+\u0005\u0005\u0003\u0005\u0002~\u0006uF\u0011AA\u0000\u0003%9W\r^+J!>\u0014H\u000f\u0006\u0003\u0002R\n\u0005\u0001BB\u001d\u0002|\u0002\u00071\b\u0003\u0005\u0003\u0006\u0005uF\u0011\u0001B\u0004\u0003\u0019\u0019'/Z1uKR\tRN!\u0003\u0003\f\t5!q\u0002B\t\u0005'\u0011)Ba\u0006\t\r)\u0012\u0019\u00011\u0001-\u0011\u0019\u0001#1\u0001a\u0001G!1\u0011Ha\u0001A\u0002mBa!\u0011B\u0002\u0001\u0004\u0011\u0005BB$\u0003\u0004\u0001\u0007\u0011\n\u0003\u0004\\\u0005\u0007\u0001\r!\u0013\u0005\u0007?\n\r\u0001\u0019A1\t\u0011\u001d\u0014\u0019\u0001%AA\u0002%C!Ba\u0007\u0002>F\u0005I\u0011\u0001B\u000f\u0003A\u0019'/Z1uK\u0012\"WMZ1vYR$\u0003(\u0006\u0002\u0003 )\u001a\u0011J!\t,\u0005\t\r\u0002\u0003\u0002B\u0013\u0005_i!Aa\n\u000b\t\t%\"1F\u0001\nk:\u001c\u0007.Z2lK\u0012T1A!\f/\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0005c\u00119CA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016\u0004"
)
public class SparkUI extends WebUI implements UIRoot {
   private final AppStatusStore store;
   private final Option<SparkContext> sc;
   private final SparkConf conf;
   private String appName;
   private final String basePath;
   private final long startTime;
   private final String appSparkVersion;
   private final boolean killEnabled;
   private String appId;
   private Option<SparkListener> streamingJobProgressListener;

   public static String create$default$8() {
      return SparkUI$.MODULE$.create$default$8();
   }

   public static SparkUI create(Option<SparkContext> var0, AppStatusStore var1, SparkConf var2, SecurityManager var3, String var4, String var5, long var6, String var8) {
      return SparkUI$.MODULE$.create(var0, var1, var2, var3, var4, var5, var6, var8);
   }

   public static int getUIPort(SparkConf var0) {
      return SparkUI$.MODULE$.getUIPort(var0);
   }

   public static String DEFAULT_POOL_NAME() {
      return SparkUI$.MODULE$.DEFAULT_POOL_NAME();
   }

   public static String STATIC_RESOURCE_DIR() {
      return SparkUI$.MODULE$.STATIC_RESOURCE_DIR();
   }

   public static int DEFAULT_PORT() {
      return SparkUI$.MODULE$.DEFAULT_PORT();
   }

   public void writeEventLogs(String appId, Option<String> attemptId, ZipOutputStream zipStream) {
      UIRoot$class.writeEventLogs(this, appId, attemptId, zipStream);
   }

   public AppStatusStore store() {
      return this.store;
   }

   public Option<SparkContext> sc() {
      return this.sc;
   }

   public SparkConf conf() {
      return this.conf;
   }

   public String appName() {
      return this.appName;
   }

   public void appName_$eq(String x$1) {
      this.appName = x$1;
   }

   public String basePath() {
      return this.basePath;
   }

   public long startTime() {
      return this.startTime;
   }

   public String appSparkVersion() {
      return this.appSparkVersion;
   }

   public boolean killEnabled() {
      return this.killEnabled;
   }

   public String appId() {
      return this.appId;
   }

   public void appId_$eq(String x$1) {
      this.appId = x$1;
   }

   private Option<SparkListener> streamingJobProgressListener() {
      return this.streamingJobProgressListener;
   }

   private void streamingJobProgressListener_$eq(Option<SparkListener> x$1) {
      this.streamingJobProgressListener = x$1;
   }

   public void initialize() {
      JobsTab jobsTab = new JobsTab(this, this.store());
      this.attachTab(jobsTab);
      StagesTab stagesTab = new StagesTab(this, this.store());
      this.attachTab(stagesTab);
      this.attachTab(new StorageTab(this, this.store()));
      this.attachTab(new EnvironmentTab(this, this.store()));
      this.attachTab(new ExecutorsTab(this));
      this.attachHandler(JettyUtils$.MODULE$.createStaticHandler(SparkUI$.MODULE$.STATIC_RESOURCE_DIR(), "/static"));
      String x$3 = "/";
      String x$4 = "/jobs/";
      String x$5 = this.basePath();
      Function1 x$6 = JettyUtils$.MODULE$.createRedirectHandler$default$3();
      Set x$7 = JettyUtils$.MODULE$.createRedirectHandler$default$5();
      this.attachHandler(JettyUtils$.MODULE$.createRedirectHandler(x$3, x$4, x$6, x$5, x$7));
      this.attachHandler(ApiRootResource$.MODULE$.getServletHandler(this));
      String x$8 = "/jobs/job/kill";
      String x$9 = "/jobs/";
      Function1 x$10 = new Serializable(this, jobsTab) {
         public static final long serialVersionUID = 0L;
         private final JobsTab jobsTab$1;

         public final void apply(HttpServletRequest request) {
            this.jobsTab$1.handleKillRequest(request);
         }

         public {
            this.jobsTab$1 = jobsTab$1;
         }
      };
      Set x$11 = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"GET", "POST"})));
      String x$12 = JettyUtils$.MODULE$.createRedirectHandler$default$4();
      this.attachHandler(JettyUtils$.MODULE$.createRedirectHandler(x$8, x$9, x$10, x$12, x$11));
      String x$13 = "/stages/stage/kill";
      String x$14 = "/stages/";
      Function1 x$15 = new Serializable(this, stagesTab) {
         public static final long serialVersionUID = 0L;
         private final StagesTab stagesTab$1;

         public final void apply(HttpServletRequest request) {
            this.stagesTab$1.handleKillRequest(request);
         }

         public {
            this.stagesTab$1 = stagesTab$1;
         }
      };
      Set x$16 = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"GET", "POST"})));
      String x$17 = JettyUtils$.MODULE$.createRedirectHandler$default$4();
      this.attachHandler(JettyUtils$.MODULE$.createRedirectHandler(x$13, x$14, x$15, x$17, x$16));
   }

   public String getSparkUser() {
      String var10000;
      try {
         var10000 = (String)Option$.MODULE$.apply(((ApplicationAttemptInfo)this.store().applicationInfo().attempts().head()).sparkUser()).orElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkUI $outer;

            public final Option<String> apply() {
               return this.$outer.store().environmentInfo().systemProperties().toMap(Predef$.MODULE$.$conforms()).get("user.name");
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
               return "<unknown>";
            }
         });
      } catch (NoSuchElementException var1) {
         var10000 = "<unknown>";
      }

      return var10000;
   }

   public String getAppName() {
      return this.appName();
   }

   public void setAppId(String id) {
      this.appId_$eq(id);
   }

   public void stop() {
      super.stop();
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkUI $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stopped Spark web UI at ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.webUrl()}));
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

   public <T> T withSparkUI(String appId, Option<String> attemptId, Function1<SparkUI, T> fn) {
      String var4 = this.appId();
      if (appId == null) {
         if (var4 == null) {
            return (T)fn.apply(this);
         }
      } else if (appId.equals(var4)) {
         return (T)fn.apply(this);
      }

      throw new NoSuchElementException();
   }

   public Iterator<ApplicationInfo> getApplicationInfoList() {
      Iterator$ var10000 = package$.MODULE$.Iterator();
      Predef$ var10001 = Predef$.MODULE$;
      ApplicationInfo[] var10002 = new ApplicationInfo[1];
      String var10007 = this.appId();
      String var10008 = this.appName();
      None$ var10009 = None$.MODULE$;
      None$ var10010 = None$.MODULE$;
      None$ var10011 = None$.MODULE$;
      None$ var10012 = None$.MODULE$;
      Seq$ var10013 = Seq$.MODULE$;
      Predef$ var10014 = Predef$.MODULE$;
      ApplicationAttemptInfo[] var10015 = new ApplicationAttemptInfo[1];
      None$ x$18 = None$.MODULE$;
      Date x$19 = new Date(this.startTime());
      Date x$20 = new Date(-1L);
      long x$21 = 0L;
      Date x$22 = new Date(this.startTime());
      String x$23 = this.getSparkUser();
      boolean x$24 = false;
      String x$25 = this.appSparkVersion();
      var10015[0] = new ApplicationAttemptInfo(x$18, x$19, x$20, x$22, x$21, x$23, x$24, x$25);
      var10002[0] = new ApplicationInfo(var10007, var10008, var10009, var10010, var10011, var10012, (Seq)var10013.apply(var10014.wrapRefArray(var10015)));
      return var10000.<ApplicationInfo>apply(var10001.wrapRefArray(var10002));
   }

   public Option<ApplicationInfo> getApplicationInfo(String appId) {
      return this.getApplicationInfoList().find(new Serializable(this, appId) {
         public static final long serialVersionUID = 0L;
         private final String appId$1;

         public final boolean apply(ApplicationInfo x$2) {
            boolean var3;
            label23: {
               String var10000 = x$2.id();
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
      });
   }

   public Option<SparkListener> getStreamingJobProgressListener() {
      return this.streamingJobProgressListener();
   }

   public void setStreamingJobProgressListener(SparkListener sparkListener) {
      this.streamingJobProgressListener_$eq(Option$.MODULE$.apply(sparkListener));
   }

   public SparkUI(AppStatusStore store, Option<SparkContext> sc, SparkConf conf, SecurityManager securityManager, String appName, String basePath, long startTime, String appSparkVersion) {
      this.store = store;
      this.sc = sc;
      this.conf = conf;
      this.appName = appName;
      this.basePath = basePath;
      this.startTime = startTime;
      this.appSparkVersion = appSparkVersion;
      super(securityManager, securityManager.getSSLOptions("ui"), SparkUI$.MODULE$.getUIPort(conf), conf, basePath, "SparkUI");
      UIRoot$class.$init$(this);
      this.killEnabled = BoxesRunTime.unboxToBoolean(sc.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(SparkContext x$1) {
            return x$1.conf().getBoolean("spark.ui.killEnabled", true);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return false;
         }
      }));
      this.streamingJobProgressListener = None$.MODULE$;
      this.initialize();
   }
}
