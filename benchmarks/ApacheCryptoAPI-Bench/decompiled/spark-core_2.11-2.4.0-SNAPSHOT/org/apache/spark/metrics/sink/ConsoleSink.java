package org.apache.spark.metrics.sink;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
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
   bytes = "\u0006\u0001q4Q!\u0001\u0002\u0001\r1\u00111bQ8og>dWmU5oW*\u00111\u0001B\u0001\u0005g&t7N\u0003\u0002\u0006\r\u00059Q.\u001a;sS\u000e\u001c(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0007\u0001i1\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)Ui\u0011AA\u0005\u0003-\t\u0011AaU5oW\"A\u0001\u0004\u0001BC\u0002\u0013\u0005!$\u0001\u0005qe>\u0004XM\u001d;z\u0007\u0001)\u0012a\u0007\t\u00039\u0005j\u0011!\b\u0006\u0003=}\tA!\u001e;jY*\t\u0001%\u0001\u0003kCZ\f\u0017B\u0001\u0012\u001e\u0005)\u0001&o\u001c9feRLWm\u001d\u0005\tI\u0001\u0011\t\u0011)A\u00057\u0005I\u0001O]8qKJ$\u0018\u0010\t\u0005\tM\u0001\u0011)\u0019!C\u0001O\u0005A!/Z4jgR\u0014\u00180F\u0001)!\tIs&D\u0001+\u0015\t)1F\u0003\u0002-[\u0005A1m\u001c3bQ\u0006dWMC\u0001/\u0003\r\u0019w.\\\u0005\u0003a)\u0012a\"T3ue&\u001c'+Z4jgR\u0014\u0018\u0010\u0003\u00053\u0001\t\u0005\t\u0015!\u0003)\u0003%\u0011XmZ5tiJL\b\u0005\u0003\u00055\u0001\t\u0005\t\u0015!\u00036\u0003-\u0019XmY;sSRLXj\u001a:\u0011\u0005Y:T\"\u0001\u0004\n\u0005a2!aD*fGV\u0014\u0018\u000e^=NC:\fw-\u001a:\t\u000bi\u0002A\u0011A\u001e\u0002\rqJg.\u001b;?)\u0011aTHP \u0011\u0005Q\u0001\u0001\"\u0002\r:\u0001\u0004Y\u0002\"\u0002\u0014:\u0001\u0004A\u0003\"\u0002\u001b:\u0001\u0004)\u0004bB!\u0001\u0005\u0004%\tAQ\u0001\u0017\u0007>s5k\u0014'F?\u0012+e)Q+M)~\u0003VIU%P\tV\t1\t\u0005\u0002\u000f\t&\u0011Qi\u0004\u0002\u0004\u0013:$\bBB$\u0001A\u0003%1)A\fD\u001f:\u001bv\nT#`\t\u00163\u0015)\u0016'U?B+%+S(EA!9\u0011\n\u0001b\u0001\n\u0003Q\u0015\u0001F\"P\u001dN{E*R0E\u000b\u001a\u000bU\u000b\u0014+`+:KE+F\u0001L!\tau*D\u0001N\u0015\tqu$\u0001\u0003mC:<\u0017B\u0001)N\u0005\u0019\u0019FO]5oO\"1!\u000b\u0001Q\u0001\n-\u000bQcQ(O'>cUi\u0018#F\r\u0006+F\nV0V\u001d&#\u0006\u0005C\u0004U\u0001\t\u0007I\u0011\u0001&\u0002%\r{ejU(M\u000b~[U)W0Q\u000bJKu\n\u0012\u0005\u0007-\u0002\u0001\u000b\u0011B&\u0002'\r{ejU(M\u000b~[U)W0Q\u000bJKu\n\u0012\u0011\t\u000fa\u0003!\u0019!C\u0001\u0015\u0006\u00012i\u0014(T\u001f2+ulS#Z?Vs\u0015\n\u0016\u0005\u00075\u0002\u0001\u000b\u0011B&\u0002#\r{ejU(M\u000b~[U)W0V\u001d&#\u0006\u0005C\u0004]\u0001\t\u0007I\u0011\u0001\"\u0002\u0015A|G\u000e\u001c)fe&|G\r\u0003\u0004_\u0001\u0001\u0006IaQ\u0001\fa>dG\u000eU3sS>$\u0007\u0005C\u0004a\u0001\t\u0007I\u0011A1\u0002\u0011A|G\u000e\\+oSR,\u0012A\u0019\t\u0003G\u001al\u0011\u0001\u001a\u0006\u0003Kv\t!bY8oGV\u0014(/\u001a8u\u0013\t9GM\u0001\u0005US6,WK\\5u\u0011\u0019I\u0007\u0001)A\u0005E\u0006I\u0001o\u001c7m+:LG\u000f\t\u0005\bW\u0002\u0011\r\u0011\"\u0001m\u0003!\u0011X\r]8si\u0016\u0014X#A7\u0011\u0005%r\u0017BA8+\u0005=\u0019uN\\:pY\u0016\u0014V\r]8si\u0016\u0014\bBB9\u0001A\u0003%Q.A\u0005sKB|'\u000f^3sA!)1\u000f\u0001C!i\u0006)1\u000f^1siR\tQ\u000f\u0005\u0002\u000fm&\u0011qo\u0004\u0002\u0005+:LG\u000fC\u0003z\u0001\u0011\u0005C/\u0001\u0003ti>\u0004\b\"B>\u0001\t\u0003\"\u0018A\u0002:fa>\u0014H\u000f"
)
public class ConsoleSink implements Sink {
   private final Properties property;
   private final MetricRegistry registry;
   private final int CONSOLE_DEFAULT_PERIOD;
   private final String CONSOLE_DEFAULT_UNIT;
   private final String CONSOLE_KEY_PERIOD;
   private final String CONSOLE_KEY_UNIT;
   private final int pollPeriod;
   private final TimeUnit pollUnit;
   private final ConsoleReporter reporter;

   public Properties property() {
      return this.property;
   }

   public MetricRegistry registry() {
      return this.registry;
   }

   public int CONSOLE_DEFAULT_PERIOD() {
      return this.CONSOLE_DEFAULT_PERIOD;
   }

   public String CONSOLE_DEFAULT_UNIT() {
      return this.CONSOLE_DEFAULT_UNIT;
   }

   public String CONSOLE_KEY_PERIOD() {
      return this.CONSOLE_KEY_PERIOD;
   }

   public String CONSOLE_KEY_UNIT() {
      return this.CONSOLE_KEY_UNIT;
   }

   public int pollPeriod() {
      return this.pollPeriod;
   }

   public TimeUnit pollUnit() {
      return this.pollUnit;
   }

   public ConsoleReporter reporter() {
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

   public ConsoleSink(Properties property, MetricRegistry registry, SecurityManager securityMgr) {
      this.property = property;
      this.registry = registry;
      this.CONSOLE_DEFAULT_PERIOD = 10;
      this.CONSOLE_DEFAULT_UNIT = "SECONDS";
      this.CONSOLE_KEY_PERIOD = "period";
      this.CONSOLE_KEY_UNIT = "unit";
      Option var4 = Option$.MODULE$.apply(property.getProperty(this.CONSOLE_KEY_PERIOD()));
      int var7;
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         String s = (String)var5.x();
         var7 = (new StringOps(Predef$.MODULE$.augmentString(s))).toInt();
      } else {
         if (!None$.MODULE$.equals(var4)) {
            throw new MatchError(var4);
         }

         var7 = this.CONSOLE_DEFAULT_PERIOD();
      }

      this.pollPeriod = var7;
      Option var8 = Option$.MODULE$.apply(property.getProperty(this.CONSOLE_KEY_UNIT()));
      TimeUnit var11;
      if (var8 instanceof Some) {
         Some var9 = (Some)var8;
         String s = (String)var9.x();
         var11 = TimeUnit.valueOf(s.toUpperCase(Locale.ROOT));
      } else {
         if (!None$.MODULE$.equals(var8)) {
            throw new MatchError(var8);
         }

         var11 = TimeUnit.valueOf(this.CONSOLE_DEFAULT_UNIT());
      }

      this.pollUnit = var11;
      MetricsSystem$.MODULE$.checkMinimalPollingPeriod(this.pollUnit(), this.pollPeriod());
      this.reporter = ConsoleReporter.forRegistry(registry).convertDurationsTo(TimeUnit.MILLISECONDS).convertRatesTo(TimeUnit.SECONDS).build();
   }
}
