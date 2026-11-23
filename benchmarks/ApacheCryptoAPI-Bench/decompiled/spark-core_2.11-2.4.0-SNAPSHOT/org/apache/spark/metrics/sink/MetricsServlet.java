package org.apache.spark.metrics.sink;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.ui.JettyUtils;
import org.apache.spark.ui.JettyUtils$;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rc!B\u0001\u0003\u0001\u0019a!AD'fiJL7m]*feZdW\r\u001e\u0006\u0003\u0007\u0011\tAa]5oW*\u0011QAB\u0001\b[\u0016$(/[2t\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\u000b\u000e\u0003\tI!A\u0006\u0002\u0003\tMKgn\u001b\u0005\t1\u0001\u0011)\u0019!C\u00015\u0005A\u0001O]8qKJ$\u0018p\u0001\u0001\u0016\u0003m\u0001\"\u0001H\u0011\u000e\u0003uQ!AH\u0010\u0002\tU$\u0018\u000e\u001c\u0006\u0002A\u0005!!.\u0019<b\u0013\t\u0011SD\u0001\u0006Qe>\u0004XM\u001d;jKND\u0001\u0002\n\u0001\u0003\u0002\u0003\u0006IaG\u0001\naJ|\u0007/\u001a:us\u0002B\u0001B\n\u0001\u0003\u0006\u0004%\taJ\u0001\te\u0016<\u0017n\u001d;ssV\t\u0001\u0006\u0005\u0002*_5\t!F\u0003\u0002\u0006W)\u0011A&L\u0001\tG>$\u0017\r[1mK*\ta&A\u0002d_6L!\u0001\r\u0016\u0003\u001d5+GO]5d%\u0016<\u0017n\u001d;ss\"A!\u0007\u0001B\u0001B\u0003%\u0001&A\u0005sK\u001eL7\u000f\u001e:zA!AA\u0007\u0001B\u0001B\u0003%Q'A\u0006tK\u000e,(/\u001b;z\u001b\u001e\u0014\bC\u0001\u001c8\u001b\u00051\u0011B\u0001\u001d\u0007\u0005=\u0019VmY;sSRLX*\u00198bO\u0016\u0014\b\"\u0002\u001e\u0001\t\u0003Y\u0014A\u0002\u001fj]&$h\b\u0006\u0003={yz\u0004C\u0001\u000b\u0001\u0011\u0015A\u0012\b1\u0001\u001c\u0011\u00151\u0013\b1\u0001)\u0011\u0015!\u0014\b1\u00016\u0011\u001d\t\u0005A1A\u0005\u0002\t\u000b\u0001cU#S-2+EkX&F3~\u0003\u0016\t\u0016%\u0016\u0003\r\u0003\"\u0001R$\u000e\u0003\u0015S!AR\u0010\u0002\t1\fgnZ\u0005\u0003\u0011\u0016\u0013aa\u0015;sS:<\u0007B\u0002&\u0001A\u0003%1)A\tT\u000bJ3F*\u0012+`\u0017\u0016Kv\fU!U\u0011\u0002Bq\u0001\u0014\u0001C\u0002\u0013\u0005!)\u0001\nT\u000bJ3F*\u0012+`\u0017\u0016KvlU!N!2+\u0005B\u0002(\u0001A\u0003%1)A\nT\u000bJ3F*\u0012+`\u0017\u0016KvlU!N!2+\u0005\u0005C\u0004Q\u0001\t\u0007I\u0011A)\u0002-M+%K\u0016'F)~#UIR!V\u0019R{6+Q'Q\u0019\u0016+\u0012A\u0015\t\u0003\u001dMK!\u0001V\b\u0003\u000f\t{w\u000e\\3b]\"1a\u000b\u0001Q\u0001\nI\u000bqcU#S-2+Ek\u0018#F\r\u0006+F\nV0T\u00036\u0003F*\u0012\u0011\t\u000fa\u0003!\u0019!C\u0001\u0005\u0006Y1/\u001a:wY\u0016$\b+\u0019;i\u0011\u0019Q\u0006\u0001)A\u0005\u0007\u0006a1/\u001a:wY\u0016$\b+\u0019;iA!9A\f\u0001b\u0001\n\u0003\t\u0016!E:feZdW\r^*i_^\u001c\u0016-\u001c9mK\"1a\f\u0001Q\u0001\nI\u000b!c]3sm2,Go\u00155poN\u000bW\u000e\u001d7fA!9\u0001\r\u0001b\u0001\n\u0003\t\u0017AB7baB,'/F\u0001c!\t\u0019'.D\u0001e\u0015\t)g-\u0001\u0005eCR\f'-\u001b8e\u0015\t9\u0007.A\u0004kC\u000e\\7o\u001c8\u000b\u0005%l\u0013!\u00034bgR,'\u000f_7m\u0013\tYGM\u0001\u0007PE*,7\r^'baB,'\u000f\u0003\u0004n\u0001\u0001\u0006IAY\u0001\b[\u0006\u0004\b/\u001a:!\u0011\u0015y\u0007\u0001\"\u0001q\u0003-9W\r\u001e%b]\u0012dWM]:\u0015\u0005Et\bc\u0001\bsi&\u00111o\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003krl\u0011A\u001e\u0006\u0003ob\fqa]3sm2,GO\u0003\u0002zu\u0006)!.\u001a;us*\u00111PC\u0001\bK\u000ed\u0017\u000e]:f\u0013\tihOA\u000bTKJ4H.\u001a;D_:$X\r\u001f;IC:$G.\u001a:\t\r}t\u0007\u0019AA\u0001\u0003\u0011\u0019wN\u001c4\u0011\u0007Y\n\u0019!C\u0002\u0002\u0006\u0019\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000f\u0005%\u0001\u0001\"\u0001\u0002\f\u0005\u0011r-\u001a;NKR\u0014\u0018nY:T]\u0006\u00048\u000f[8u)\u0011\ti!!\u0007\u0011\t\u0005=\u0011Q\u0003\b\u0004\u001d\u0005E\u0011bAA\n\u001f\u00051\u0001K]3eK\u001aL1\u0001SA\f\u0015\r\t\u0019b\u0004\u0005\t\u00037\t9\u00011\u0001\u0002\u001e\u00059!/Z9vKN$\b\u0003BA\u0010\u0003Wi!!!\t\u000b\t\u0005\r\u0012QE\u0001\u0005QR$\bOC\u0002x\u0003OQ!!!\u000b\u0002\u000b)\fg/\u0019=\n\t\u00055\u0012\u0011\u0005\u0002\u0013\u0011R$\boU3sm2,GOU3rk\u0016\u001cH\u000fC\u0004\u00022\u0001!\t%a\r\u0002\u000bM$\u0018M\u001d;\u0015\u0005\u0005U\u0002c\u0001\b\u00028%\u0019\u0011\u0011H\b\u0003\tUs\u0017\u000e\u001e\u0005\b\u0003{\u0001A\u0011IA\u001a\u0003\u0011\u0019Ho\u001c9\t\u000f\u0005\u0005\u0003\u0001\"\u0011\u00024\u00051!/\u001a9peR\u0004"
)
public class MetricsServlet implements Sink {
   private final Properties property;
   private final MetricRegistry registry;
   private final SecurityManager securityMgr;
   private final String SERVLET_KEY_PATH;
   private final String SERVLET_KEY_SAMPLE;
   private final boolean SERVLET_DEFAULT_SAMPLE;
   private final String servletPath;
   private final boolean servletShowSample;
   private final ObjectMapper mapper;

   public Properties property() {
      return this.property;
   }

   public MetricRegistry registry() {
      return this.registry;
   }

   public String SERVLET_KEY_PATH() {
      return this.SERVLET_KEY_PATH;
   }

   public String SERVLET_KEY_SAMPLE() {
      return this.SERVLET_KEY_SAMPLE;
   }

   public boolean SERVLET_DEFAULT_SAMPLE() {
      return this.SERVLET_DEFAULT_SAMPLE;
   }

   public String servletPath() {
      return this.servletPath;
   }

   public boolean servletShowSample() {
      return this.servletShowSample;
   }

   public ObjectMapper mapper() {
      return this.mapper;
   }

   public ServletContextHandler[] getHandlers(SparkConf conf) {
      return (ServletContextHandler[])(new ServletContextHandler[]{JettyUtils$.MODULE$.createServletHandler(this.servletPath(), new JettyUtils.ServletParams(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MetricsServlet $outer;

         public final String apply(HttpServletRequest request) {
            return this.$outer.getMetricsSnapshot(request);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, "text/json", JettyUtils.ServletParams$.MODULE$.$lessinit$greater$default$3()), this.securityMgr, conf, JettyUtils$.MODULE$.createServletHandler$default$5())});
   }

   public String getMetricsSnapshot(HttpServletRequest request) {
      return this.mapper().writeValueAsString(this.registry());
   }

   public void start() {
   }

   public void stop() {
   }

   public void report() {
   }

   public MetricsServlet(Properties property, MetricRegistry registry, SecurityManager securityMgr) {
      this.property = property;
      this.registry = registry;
      this.securityMgr = securityMgr;
      this.SERVLET_KEY_PATH = "path";
      this.SERVLET_KEY_SAMPLE = "sample";
      this.SERVLET_DEFAULT_SAMPLE = false;
      this.servletPath = property.getProperty(this.SERVLET_KEY_PATH());
      this.servletShowSample = BoxesRunTime.unboxToBoolean(Option$.MODULE$.apply(property.getProperty(this.SERVLET_KEY_SAMPLE())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toBoolean();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MetricsServlet $outer;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return this.$outer.SERVLET_DEFAULT_SAMPLE();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      this.mapper = (new ObjectMapper()).registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, this.servletShowSample()));
   }
}
