package org.apache.spark.metrics.sink;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.graphite.GraphiteUDP;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.spark.SecurityManager;
import org.apache.spark.metrics.MetricsSystem$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-d!B\u0001\u0003\u0001\u0019a!\u0001D$sCBD\u0017\u000e^3TS:\\'BA\u0002\u0005\u0003\u0011\u0019\u0018N\\6\u000b\u0005\u00151\u0011aB7fiJL7m\u001d\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!R#D\u0001\u0003\u0013\t1\"A\u0001\u0003TS:\\\u0007\u0002\u0003\r\u0001\u0005\u000b\u0007I\u0011\u0001\u000e\u0002\u0011A\u0014x\u000e]3sif\u001c\u0001!F\u0001\u001c!\ta\u0012%D\u0001\u001e\u0015\tqr$\u0001\u0003vi&d'\"\u0001\u0011\u0002\t)\fg/Y\u0005\u0003Eu\u0011!\u0002\u0015:pa\u0016\u0014H/[3t\u0011!!\u0003A!A!\u0002\u0013Y\u0012!\u00039s_B,'\u000f^=!\u0011!1\u0003A!b\u0001\n\u00039\u0013\u0001\u0003:fO&\u001cHO]=\u0016\u0003!\u0002\"!K\u0018\u000e\u0003)R!!B\u0016\u000b\u00051j\u0013\u0001C2pI\u0006D\u0017\r\\3\u000b\u00039\n1aY8n\u0013\t\u0001$F\u0001\bNKR\u0014\u0018n\u0019*fO&\u001cHO]=\t\u0011I\u0002!\u0011!Q\u0001\n!\n\u0011B]3hSN$(/\u001f\u0011\t\u0011Q\u0002!\u0011!Q\u0001\nU\n1b]3dkJLG/_'heB\u0011agN\u0007\u0002\r%\u0011\u0001H\u0002\u0002\u0010'\u0016\u001cWO]5us6\u000bg.Y4fe\")!\b\u0001C\u0001w\u00051A(\u001b8jiz\"B\u0001P\u001f?\u007fA\u0011A\u0003\u0001\u0005\u00061e\u0002\ra\u0007\u0005\u0006Me\u0002\r\u0001\u000b\u0005\u0006ie\u0002\r!\u000e\u0005\b\u0003\u0002\u0011\r\u0011\"\u0001C\u0003]9%+\u0011)I\u0013R+u\fR#G\u0003VcEk\u0018)F%&{E)F\u0001D!\tqA)\u0003\u0002F\u001f\t\u0019\u0011J\u001c;\t\r\u001d\u0003\u0001\u0015!\u0003D\u0003a9%+\u0011)I\u0013R+u\fR#G\u0003VcEk\u0018)F%&{E\t\t\u0005\b\u0013\u0002\u0011\r\u0011\"\u0001K\u0003U9%+\u0011)I\u0013R+u\fR#G\u0003VcEkX+O\u0013R+\u0012a\u0013\t\u0003\u0019>k\u0011!\u0014\u0006\u0003\u001d~\tA\u0001\\1oO&\u0011\u0001+\u0014\u0002\u0007'R\u0014\u0018N\\4\t\rI\u0003\u0001\u0015!\u0003L\u0003Y9%+\u0011)I\u0013R+u\fR#G\u0003VcEkX+O\u0013R\u0003\u0003b\u0002+\u0001\u0005\u0004%\tAS\u0001\u0018\u000fJ\u000b\u0005\u000bS%U\u000b~#UIR!V\u0019R{\u0006KU#G\u0013bCaA\u0016\u0001!\u0002\u0013Y\u0015\u0001G$S\u0003BC\u0015\nV#`\t\u00163\u0015)\u0016'U?B\u0013VIR%YA!9\u0001\f\u0001b\u0001\n\u0003Q\u0015!E$S\u0003BC\u0015\nV#`\u0017\u0016Kv\fS(T)\"1!\f\u0001Q\u0001\n-\u000b!c\u0012*B!\"KE+R0L\u000bf{\u0006jT*UA!9A\f\u0001b\u0001\n\u0003Q\u0015!E$S\u0003BC\u0015\nV#`\u0017\u0016Kv\fU(S)\"1a\f\u0001Q\u0001\n-\u000b!c\u0012*B!\"KE+R0L\u000bf{\u0006k\u0014*UA!9\u0001\r\u0001b\u0001\n\u0003Q\u0015aE$S\u0003BC\u0015\nV#`\u0017\u0016Kv\fU#S\u0013>#\u0005B\u00022\u0001A\u0003%1*\u0001\u000bH%\u0006\u0003\u0006*\u0013+F?.+\u0015l\u0018)F%&{E\t\t\u0005\bI\u0002\u0011\r\u0011\"\u0001K\u0003E9%+\u0011)I\u0013R+ulS#Z?Vs\u0015\n\u0016\u0005\u0007M\u0002\u0001\u000b\u0011B&\u0002%\u001d\u0013\u0016\t\u0015%J)\u0016{6*R-`+:KE\u000b\t\u0005\bQ\u0002\u0011\r\u0011\"\u0001K\u0003M9%+\u0011)I\u0013R+ulS#Z?B\u0013VIR%Y\u0011\u0019Q\u0007\u0001)A\u0005\u0017\u0006!rIU!Q\u0011&#ViX&F3~\u0003&+\u0012$J1\u0002Bq\u0001\u001c\u0001C\u0002\u0013\u0005!*A\u000bH%\u0006\u0003\u0006*\u0013+F?.+\u0015l\u0018)S\u001fR{5i\u0014'\t\r9\u0004\u0001\u0015!\u0003L\u0003Y9%+\u0011)I\u0013R+ulS#Z?B\u0013v\nV(D\u001f2\u0003\u0003\"\u00029\u0001\t\u0003\t\u0018\u0001\u00059s_B,'\u000f^=U_>\u0003H/[8o)\t\u00118\u0010E\u0002\u000fgVL!\u0001^\b\u0003\r=\u0003H/[8o!\t1\u0018P\u0004\u0002\u000fo&\u0011\u0001pD\u0001\u0007!J,G-\u001a4\n\u0005AS(B\u0001=\u0010\u0011\u0015ax\u000e1\u0001v\u0003\u0011\u0001(o\u001c9\t\u000fy\u0004!\u0019!C\u0001\u007f\u0006!\u0001n\\:u+\u0005)\bbBA\u0002\u0001\u0001\u0006I!^\u0001\u0006Q>\u001cH\u000f\t\u0005\t\u0003\u000f\u0001!\u0019!C\u0001\u0005\u0006!\u0001o\u001c:u\u0011\u001d\tY\u0001\u0001Q\u0001\n\r\u000bQ\u0001]8si\u0002B\u0001\"a\u0004\u0001\u0005\u0004%\tAQ\u0001\u000ba>dG\u000eU3sS>$\u0007bBA\n\u0001\u0001\u0006IaQ\u0001\fa>dG\u000eU3sS>$\u0007\u0005C\u0005\u0002\u0018\u0001\u0011\r\u0011\"\u0001\u0002\u001a\u0005A\u0001o\u001c7m+:LG/\u0006\u0002\u0002\u001cA!\u0011QDA\u0012\u001b\t\tyBC\u0002\u0002\"u\t!bY8oGV\u0014(/\u001a8u\u0013\u0011\t)#a\b\u0003\u0011QKW.Z+oSRD\u0001\"!\u000b\u0001A\u0003%\u00111D\u0001\na>dG.\u00168ji\u0002B\u0001\"!\f\u0001\u0005\u0004%\ta`\u0001\u0007aJ,g-\u001b=\t\u000f\u0005E\u0002\u0001)A\u0005k\u00069\u0001O]3gSb\u0004\u0003\"CA\u001b\u0001\t\u0007I\u0011AA\u001c\u0003!9'/\u00199iSR,WCAA\u001d!\u0011\tY$a\u0010\u000e\u0005\u0005u\"bAA\u001bU%!\u0011\u0011IA\u001f\u000599%/\u00199iSR,7+\u001a8eKJD\u0001\"!\u0012\u0001A\u0003%\u0011\u0011H\u0001\nOJ\f\u0007\u000f[5uK\u0002B\u0011\"!\u0013\u0001\u0005\u0004%\t!a\u0013\u0002\u0011I,\u0007o\u001c:uKJ,\"!!\u0014\u0011\t\u0005m\u0012qJ\u0005\u0005\u0003#\niD\u0001\tHe\u0006\u0004\b.\u001b;f%\u0016\u0004xN\u001d;fe\"A\u0011Q\u000b\u0001!\u0002\u0013\ti%A\u0005sKB|'\u000f^3sA!9\u0011\u0011\f\u0001\u0005B\u0005m\u0013!B:uCJ$HCAA/!\rq\u0011qL\u0005\u0004\u0003Cz!\u0001B+oSRDq!!\u001a\u0001\t\u0003\nY&\u0001\u0003ti>\u0004\bbBA5\u0001\u0011\u0005\u00131L\u0001\u0007e\u0016\u0004xN\u001d;"
)
public class GraphiteSink implements Sink {
   private final Properties property;
   private final MetricRegistry registry;
   private final int GRAPHITE_DEFAULT_PERIOD;
   private final String GRAPHITE_DEFAULT_UNIT;
   private final String GRAPHITE_DEFAULT_PREFIX;
   private final String GRAPHITE_KEY_HOST;
   private final String GRAPHITE_KEY_PORT;
   private final String GRAPHITE_KEY_PERIOD;
   private final String GRAPHITE_KEY_UNIT;
   private final String GRAPHITE_KEY_PREFIX;
   private final String GRAPHITE_KEY_PROTOCOL;
   private final String host;
   private final int port;
   private final int pollPeriod;
   private final TimeUnit pollUnit;
   private final String prefix;
   private final GraphiteSender graphite;
   private final GraphiteReporter reporter;

   public Properties property() {
      return this.property;
   }

   public MetricRegistry registry() {
      return this.registry;
   }

   public int GRAPHITE_DEFAULT_PERIOD() {
      return this.GRAPHITE_DEFAULT_PERIOD;
   }

   public String GRAPHITE_DEFAULT_UNIT() {
      return this.GRAPHITE_DEFAULT_UNIT;
   }

   public String GRAPHITE_DEFAULT_PREFIX() {
      return this.GRAPHITE_DEFAULT_PREFIX;
   }

   public String GRAPHITE_KEY_HOST() {
      return this.GRAPHITE_KEY_HOST;
   }

   public String GRAPHITE_KEY_PORT() {
      return this.GRAPHITE_KEY_PORT;
   }

   public String GRAPHITE_KEY_PERIOD() {
      return this.GRAPHITE_KEY_PERIOD;
   }

   public String GRAPHITE_KEY_UNIT() {
      return this.GRAPHITE_KEY_UNIT;
   }

   public String GRAPHITE_KEY_PREFIX() {
      return this.GRAPHITE_KEY_PREFIX;
   }

   public String GRAPHITE_KEY_PROTOCOL() {
      return this.GRAPHITE_KEY_PROTOCOL;
   }

   public Option<String> propertyToOption(String prop) {
      return Option$.MODULE$.<String>apply(this.property().getProperty(prop));
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public int pollPeriod() {
      return this.pollPeriod;
   }

   public TimeUnit pollUnit() {
      return this.pollUnit;
   }

   public String prefix() {
      return this.prefix;
   }

   public GraphiteSender graphite() {
      return this.graphite;
   }

   public GraphiteReporter reporter() {
      return this.reporter;
   }

   public void start() {
      this.reporter().start((long)this.pollPeriod(), this.pollUnit());
   }

   public void stop() {
      this.reporter().stop();
   }

   public void report() {
      this.reporter().report();
   }

   public GraphiteSink(Properties property, MetricRegistry registry, SecurityManager securityMgr) {
      this.property = property;
      this.registry = registry;
      this.GRAPHITE_DEFAULT_PERIOD = 10;
      this.GRAPHITE_DEFAULT_UNIT = "SECONDS";
      this.GRAPHITE_DEFAULT_PREFIX = "";
      this.GRAPHITE_KEY_HOST = "host";
      this.GRAPHITE_KEY_PORT = "port";
      this.GRAPHITE_KEY_PERIOD = "period";
      this.GRAPHITE_KEY_UNIT = "unit";
      this.GRAPHITE_KEY_PREFIX = "prefix";
      this.GRAPHITE_KEY_PROTOCOL = "protocol";
      if (!this.propertyToOption(this.GRAPHITE_KEY_HOST()).isDefined()) {
         throw new Exception("Graphite sink requires 'host' property.");
      } else if (this.propertyToOption(this.GRAPHITE_KEY_PORT()).isDefined()) {
         this.host = (String)this.propertyToOption(this.GRAPHITE_KEY_HOST()).get();
         this.port = (new StringOps(Predef$.MODULE$.augmentString((String)this.propertyToOption(this.GRAPHITE_KEY_PORT()).get()))).toInt();
         Option var4 = this.propertyToOption(this.GRAPHITE_KEY_PERIOD());
         int var7;
         if (var4 instanceof Some) {
            Some var5 = (Some)var4;
            String s = (String)var5.x();
            var7 = (new StringOps(Predef$.MODULE$.augmentString(s))).toInt();
         } else {
            if (!None$.MODULE$.equals(var4)) {
               throw new MatchError(var4);
            }

            var7 = this.GRAPHITE_DEFAULT_PERIOD();
         }

         this.pollPeriod = var7;
         Option var8 = this.propertyToOption(this.GRAPHITE_KEY_UNIT());
         TimeUnit var11;
         if (var8 instanceof Some) {
            Some var9 = (Some)var8;
            String s = (String)var9.x();
            var11 = TimeUnit.valueOf(s.toUpperCase(Locale.ROOT));
         } else {
            if (!None$.MODULE$.equals(var8)) {
               throw new MatchError(var8);
            }

            var11 = TimeUnit.valueOf(this.GRAPHITE_DEFAULT_UNIT());
         }

         Object var16;
         label62: {
            this.pollUnit = var11;
            this.prefix = (String)this.propertyToOption(this.GRAPHITE_KEY_PREFIX()).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final GraphiteSink $outer;

               public final String apply() {
                  return this.$outer.GRAPHITE_DEFAULT_PREFIX();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            MetricsSystem$.MODULE$.checkMinimalPollingPeriod(this.pollUnit(), this.pollPeriod());
            boolean var12 = false;
            Some var13 = null;
            Option var14 = this.propertyToOption(this.GRAPHITE_KEY_PROTOCOL()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String x$1) {
                  return x$1.toLowerCase(Locale.ROOT);
               }
            });
            if (var14 instanceof Some) {
               var12 = true;
               var13 = (Some)var14;
               String var15 = (String)var13.x();
               if ("udp".equals(var15)) {
                  var16 = new GraphiteUDP(this.host(), this.port());
                  break label62;
               }
            }

            boolean var19;
            label49: {
               if (var14 instanceof Some) {
                  Some var17 = (Some)var14;
                  String var18 = (String)var17.x();
                  if ("tcp".equals(var18)) {
                     var19 = true;
                     break label49;
                  }
               }

               if (None$.MODULE$.equals(var14)) {
                  var19 = true;
               } else {
                  var19 = false;
               }
            }

            if (!var19) {
               if (var12) {
                  String p = (String)var13.x();
                  throw new Exception((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid Graphite protocol: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{p})));
               }

               throw new MatchError(var14);
            }

            var16 = new Graphite(this.host(), this.port());
         }

         this.graphite = (GraphiteSender)var16;
         this.reporter = GraphiteReporter.forRegistry(registry).convertDurationsTo(TimeUnit.MILLISECONDS).convertRatesTo(TimeUnit.SECONDS).prefixedWith(this.prefix()).build(this.graphite());
      } else {
         throw new Exception("Graphite sink requires 'port' property.");
      }
   }
}
