package org.apache.spark.metrics.sink;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import java.io.File;
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
import scala.Some;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ea!B\u0001\u0003\u0001\u0019a!aB\"tmNKgn\u001b\u0006\u0003\u0007\u0011\tAa]5oW*\u0011QAB\u0001\b[\u0016$(/[2t\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\u000b\u000e\u0003\tI!A\u0006\u0002\u0003\tMKgn\u001b\u0005\t1\u0001\u0011)\u0019!C\u00015\u0005A\u0001O]8qKJ$\u0018p\u0001\u0001\u0016\u0003m\u0001\"\u0001H\u0011\u000e\u0003uQ!AH\u0010\u0002\tU$\u0018\u000e\u001c\u0006\u0002A\u0005!!.\u0019<b\u0013\t\u0011SD\u0001\u0006Qe>\u0004XM\u001d;jKND\u0001\u0002\n\u0001\u0003\u0002\u0003\u0006IaG\u0001\naJ|\u0007/\u001a:us\u0002B\u0001B\n\u0001\u0003\u0006\u0004%\taJ\u0001\te\u0016<\u0017n\u001d;ssV\t\u0001\u0006\u0005\u0002*_5\t!F\u0003\u0002\u0006W)\u0011A&L\u0001\tG>$\u0017\r[1mK*\ta&A\u0002d_6L!\u0001\r\u0016\u0003\u001d5+GO]5d%\u0016<\u0017n\u001d;ss\"A!\u0007\u0001B\u0001B\u0003%\u0001&A\u0005sK\u001eL7\u000f\u001e:zA!AA\u0007\u0001B\u0001B\u0003%Q'A\u0006tK\u000e,(/\u001b;z\u001b\u001e\u0014\bC\u0001\u001c8\u001b\u00051\u0011B\u0001\u001d\u0007\u0005=\u0019VmY;sSRLX*\u00198bO\u0016\u0014\b\"\u0002\u001e\u0001\t\u0003Y\u0014A\u0002\u001fj]&$h\b\u0006\u0003={yz\u0004C\u0001\u000b\u0001\u0011\u0015A\u0012\b1\u0001\u001c\u0011\u00151\u0013\b1\u0001)\u0011\u0015!\u0014\b1\u00016\u0011\u001d\t\u0005A1A\u0005\u0002\t\u000babQ*W?.+\u0015l\u0018)F%&{E)F\u0001D!\t!u)D\u0001F\u0015\t1u$\u0001\u0003mC:<\u0017B\u0001%F\u0005\u0019\u0019FO]5oO\"1!\n\u0001Q\u0001\n\r\u000bqbQ*W?.+\u0015l\u0018)F%&{E\t\t\u0005\b\u0019\u0002\u0011\r\u0011\"\u0001C\u00031\u00195KV0L\u000bf{VKT%U\u0011\u0019q\u0005\u0001)A\u0005\u0007\u0006i1i\u0015,`\u0017\u0016Kv,\u0016(J)\u0002Bq\u0001\u0015\u0001C\u0002\u0013\u0005!)A\u0006D'Z{6*R-`\t&\u0013\u0006B\u0002*\u0001A\u0003%1)\u0001\u0007D'Z{6*R-`\t&\u0013\u0006\u0005C\u0004U\u0001\t\u0007I\u0011A+\u0002%\r\u001bfk\u0018#F\r\u0006+F\nV0Q\u000bJKu\nR\u000b\u0002-B\u0011abV\u0005\u00031>\u00111!\u00138u\u0011\u0019Q\u0006\u0001)A\u0005-\u0006\u00192i\u0015,`\t\u00163\u0015)\u0016'U?B+%+S(EA!9A\f\u0001b\u0001\n\u0003\u0011\u0015\u0001E\"T-~#UIR!V\u0019R{VKT%U\u0011\u0019q\u0006\u0001)A\u0005\u0007\u0006\t2i\u0015,`\t\u00163\u0015)\u0016'U?Vs\u0015\n\u0016\u0011\t\u000f\u0001\u0004!\u0019!C\u0001\u0005\u0006y1i\u0015,`\t\u00163\u0015)\u0016'U?\u0012K%\u000b\u0003\u0004c\u0001\u0001\u0006IaQ\u0001\u0011\u0007N3v\fR#G\u0003VcEk\u0018#J%\u0002Bq\u0001\u001a\u0001C\u0002\u0013\u0005Q+\u0001\u0006q_2d\u0007+\u001a:j_\u0012DaA\u001a\u0001!\u0002\u00131\u0016a\u00039pY2\u0004VM]5pI\u0002Bq\u0001\u001b\u0001C\u0002\u0013\u0005\u0011.\u0001\u0005q_2dWK\\5u+\u0005Q\u0007CA6o\u001b\u0005a'BA7\u001e\u0003)\u0019wN\\2veJ,g\u000e^\u0005\u0003_2\u0014\u0001\u0002V5nKVs\u0017\u000e\u001e\u0005\u0007c\u0002\u0001\u000b\u0011\u00026\u0002\u0013A|G\u000e\\+oSR\u0004\u0003bB:\u0001\u0005\u0004%\tAQ\u0001\ba>dG\u000eR5s\u0011\u0019)\b\u0001)A\u0005\u0007\u0006A\u0001o\u001c7m\t&\u0014\b\u0005C\u0004x\u0001\t\u0007I\u0011\u0001=\u0002\u0011I,\u0007o\u001c:uKJ,\u0012!\u001f\t\u0003SiL!a\u001f\u0016\u0003\u0017\r\u001bhOU3q_J$XM\u001d\u0005\u0007{\u0002\u0001\u000b\u0011B=\u0002\u0013I,\u0007o\u001c:uKJ\u0004\u0003BB@\u0001\t\u0003\n\t!A\u0003ti\u0006\u0014H\u000f\u0006\u0002\u0002\u0004A\u0019a\"!\u0002\n\u0007\u0005\u001dqB\u0001\u0003V]&$\bbBA\u0006\u0001\u0011\u0005\u0013\u0011A\u0001\u0005gR|\u0007\u000fC\u0004\u0002\u0010\u0001!\t%!\u0001\u0002\rI,\u0007o\u001c:u\u0001"
)
public class CsvSink implements Sink {
   private final Properties property;
   private final MetricRegistry registry;
   private final String CSV_KEY_PERIOD;
   private final String CSV_KEY_UNIT;
   private final String CSV_KEY_DIR;
   private final int CSV_DEFAULT_PERIOD;
   private final String CSV_DEFAULT_UNIT;
   private final String CSV_DEFAULT_DIR;
   private final int pollPeriod;
   private final TimeUnit pollUnit;
   private final String pollDir;
   private final CsvReporter reporter;

   public Properties property() {
      return this.property;
   }

   public MetricRegistry registry() {
      return this.registry;
   }

   public String CSV_KEY_PERIOD() {
      return this.CSV_KEY_PERIOD;
   }

   public String CSV_KEY_UNIT() {
      return this.CSV_KEY_UNIT;
   }

   public String CSV_KEY_DIR() {
      return this.CSV_KEY_DIR;
   }

   public int CSV_DEFAULT_PERIOD() {
      return this.CSV_DEFAULT_PERIOD;
   }

   public String CSV_DEFAULT_UNIT() {
      return this.CSV_DEFAULT_UNIT;
   }

   public String CSV_DEFAULT_DIR() {
      return this.CSV_DEFAULT_DIR;
   }

   public int pollPeriod() {
      return this.pollPeriod;
   }

   public TimeUnit pollUnit() {
      return this.pollUnit;
   }

   public String pollDir() {
      return this.pollDir;
   }

   public CsvReporter reporter() {
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

   public CsvSink(Properties property, MetricRegistry registry, SecurityManager securityMgr) {
      this.property = property;
      this.registry = registry;
      this.CSV_KEY_PERIOD = "period";
      this.CSV_KEY_UNIT = "unit";
      this.CSV_KEY_DIR = "directory";
      this.CSV_DEFAULT_PERIOD = 10;
      this.CSV_DEFAULT_UNIT = "SECONDS";
      this.CSV_DEFAULT_DIR = "/tmp/";
      Option var4 = Option$.MODULE$.apply(property.getProperty(this.CSV_KEY_PERIOD()));
      int var7;
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         String s = (String)var5.x();
         var7 = (new StringOps(Predef$.MODULE$.augmentString(s))).toInt();
      } else {
         if (!None$.MODULE$.equals(var4)) {
            throw new MatchError(var4);
         }

         var7 = this.CSV_DEFAULT_PERIOD();
      }

      this.pollPeriod = var7;
      Option var8 = Option$.MODULE$.apply(property.getProperty(this.CSV_KEY_UNIT()));
      TimeUnit var11;
      if (var8 instanceof Some) {
         Some var9 = (Some)var8;
         String s = (String)var9.x();
         var11 = TimeUnit.valueOf(s.toUpperCase(Locale.ROOT));
      } else {
         if (!None$.MODULE$.equals(var8)) {
            throw new MatchError(var8);
         }

         var11 = TimeUnit.valueOf(this.CSV_DEFAULT_UNIT());
      }

      this.pollUnit = var11;
      MetricsSystem$.MODULE$.checkMinimalPollingPeriod(this.pollUnit(), this.pollPeriod());
      Option var12 = Option$.MODULE$.apply(property.getProperty(this.CSV_KEY_DIR()));
      String var15;
      if (var12 instanceof Some) {
         Some var13 = (Some)var12;
         String s = (String)var13.x();
         var15 = s;
      } else {
         if (!None$.MODULE$.equals(var12)) {
            throw new MatchError(var12);
         }

         var15 = this.CSV_DEFAULT_DIR();
      }

      this.pollDir = var15;
      this.reporter = CsvReporter.forRegistry(registry).formatFor(Locale.US).convertDurationsTo(TimeUnit.MILLISECONDS).convertRatesTo(TimeUnit.SECONDS).build(new File(this.pollDir()));
   }
}
