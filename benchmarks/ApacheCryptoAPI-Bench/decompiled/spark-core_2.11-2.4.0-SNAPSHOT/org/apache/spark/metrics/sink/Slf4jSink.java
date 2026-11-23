package org.apache.spark.metrics.sink;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
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
   bytes = "\u0006\u0001q4Q!\u0001\u0002\u0001\r1\u0011\u0011b\u00157gi)\u001c\u0016N\\6\u000b\u0005\r!\u0011\u0001B:j].T!!\u0002\u0004\u0002\u000f5,GO]5dg*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001bM\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u0016\u001b\u0005\u0011\u0011B\u0001\f\u0003\u0005\u0011\u0019\u0016N\\6\t\u0011a\u0001!Q1A\u0005\u0002i\t\u0001\u0002\u001d:pa\u0016\u0014H/_\u0002\u0001+\u0005Y\u0002C\u0001\u000f\"\u001b\u0005i\"B\u0001\u0010 \u0003\u0011)H/\u001b7\u000b\u0003\u0001\nAA[1wC&\u0011!%\b\u0002\u000b!J|\u0007/\u001a:uS\u0016\u001c\b\u0002\u0003\u0013\u0001\u0005\u0003\u0005\u000b\u0011B\u000e\u0002\u0013A\u0014x\u000e]3sif\u0004\u0003\u0002\u0003\u0014\u0001\u0005\u000b\u0007I\u0011A\u0014\u0002\u0011I,w-[:uef,\u0012\u0001\u000b\t\u0003S=j\u0011A\u000b\u0006\u0003\u000b-R!\u0001L\u0017\u0002\u0011\r|G-\u00195bY\u0016T\u0011AL\u0001\u0004G>l\u0017B\u0001\u0019+\u00059iU\r\u001e:jGJ+w-[:uefD\u0001B\r\u0001\u0003\u0002\u0003\u0006I\u0001K\u0001\ne\u0016<\u0017n\u001d;ss\u0002B\u0001\u0002\u000e\u0001\u0003\u0002\u0003\u0006I!N\u0001\fg\u0016\u001cWO]5us6;'\u000f\u0005\u00027o5\ta!\u0003\u00029\r\ty1+Z2ve&$\u00180T1oC\u001e,'\u000fC\u0003;\u0001\u0011\u00051(\u0001\u0004=S:LGO\u0010\u000b\u0005yurt\b\u0005\u0002\u0015\u0001!)\u0001$\u000fa\u00017!)a%\u000fa\u0001Q!)A'\u000fa\u0001k!9\u0011\t\u0001b\u0001\n\u0003\u0011\u0015\u0001F*M\rRRu\fR#G\u0003VcEk\u0018)F%&{E)F\u0001D!\tqA)\u0003\u0002F\u001f\t\u0019\u0011J\u001c;\t\r\u001d\u0003\u0001\u0015!\u0003D\u0003U\u0019FJ\u0012\u001bK?\u0012+e)Q+M)~\u0003VIU%P\t\u0002Bq!\u0013\u0001C\u0002\u0013\u0005!*\u0001\nT\u0019\u001a#$j\u0018#F\r\u0006+F\nV0V\u001d&#V#A&\u0011\u00051{U\"A'\u000b\u00059{\u0012\u0001\u00027b]\u001eL!\u0001U'\u0003\rM#(/\u001b8h\u0011\u0019\u0011\u0006\u0001)A\u0005\u0017\u0006\u00192\u000b\u0014$5\u0015~#UIR!V\u0019R{VKT%UA!9A\u000b\u0001b\u0001\n\u0003Q\u0015\u0001E*M\rRRulS#Z?B+%+S(E\u0011\u00191\u0006\u0001)A\u0005\u0017\u0006\t2\u000b\u0014$5\u0015~[U)W0Q\u000bJKu\n\u0012\u0011\t\u000fa\u0003!\u0019!C\u0001\u0015\u0006q1\u000b\u0014$5\u0015~[U)W0V\u001d&#\u0006B\u0002.\u0001A\u0003%1*A\bT\u0019\u001a#$jX&F3~+f*\u0013+!\u0011\u001da\u0006A1A\u0005\u0002\t\u000b!\u0002]8mYB+'/[8e\u0011\u0019q\u0006\u0001)A\u0005\u0007\u0006Y\u0001o\u001c7m!\u0016\u0014\u0018n\u001c3!\u0011\u001d\u0001\u0007A1A\u0005\u0002\u0005\f\u0001\u0002]8mYVs\u0017\u000e^\u000b\u0002EB\u00111MZ\u0007\u0002I*\u0011Q-H\u0001\u000bG>t7-\u001e:sK:$\u0018BA4e\u0005!!\u0016.\\3V]&$\bBB5\u0001A\u0003%!-A\u0005q_2dWK\\5uA!91\u000e\u0001b\u0001\n\u0003a\u0017\u0001\u0003:fa>\u0014H/\u001a:\u0016\u00035\u0004\"!\u000b8\n\u0005=T#!D*mMRR'+\u001a9peR,'\u000f\u0003\u0004r\u0001\u0001\u0006I!\\\u0001\ne\u0016\u0004xN\u001d;fe\u0002BQa\u001d\u0001\u0005BQ\fQa\u001d;beR$\u0012!\u001e\t\u0003\u001dYL!a^\b\u0003\tUs\u0017\u000e\u001e\u0005\u0006s\u0002!\t\u0005^\u0001\u0005gR|\u0007\u000fC\u0003|\u0001\u0011\u0005C/\u0001\u0004sKB|'\u000f\u001e"
)
public class Slf4jSink implements Sink {
   private final Properties property;
   private final MetricRegistry registry;
   private final int SLF4J_DEFAULT_PERIOD;
   private final String SLF4J_DEFAULT_UNIT;
   private final String SLF4J_KEY_PERIOD;
   private final String SLF4J_KEY_UNIT;
   private final int pollPeriod;
   private final TimeUnit pollUnit;
   private final Slf4jReporter reporter;

   public Properties property() {
      return this.property;
   }

   public MetricRegistry registry() {
      return this.registry;
   }

   public int SLF4J_DEFAULT_PERIOD() {
      return this.SLF4J_DEFAULT_PERIOD;
   }

   public String SLF4J_DEFAULT_UNIT() {
      return this.SLF4J_DEFAULT_UNIT;
   }

   public String SLF4J_KEY_PERIOD() {
      return this.SLF4J_KEY_PERIOD;
   }

   public String SLF4J_KEY_UNIT() {
      return this.SLF4J_KEY_UNIT;
   }

   public int pollPeriod() {
      return this.pollPeriod;
   }

   public TimeUnit pollUnit() {
      return this.pollUnit;
   }

   public Slf4jReporter reporter() {
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

   public Slf4jSink(Properties property, MetricRegistry registry, SecurityManager securityMgr) {
      this.property = property;
      this.registry = registry;
      this.SLF4J_DEFAULT_PERIOD = 10;
      this.SLF4J_DEFAULT_UNIT = "SECONDS";
      this.SLF4J_KEY_PERIOD = "period";
      this.SLF4J_KEY_UNIT = "unit";
      Option var4 = Option$.MODULE$.apply(property.getProperty(this.SLF4J_KEY_PERIOD()));
      int var7;
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         String s = (String)var5.x();
         var7 = (new StringOps(Predef$.MODULE$.augmentString(s))).toInt();
      } else {
         if (!None$.MODULE$.equals(var4)) {
            throw new MatchError(var4);
         }

         var7 = this.SLF4J_DEFAULT_PERIOD();
      }

      this.pollPeriod = var7;
      Option var8 = Option$.MODULE$.apply(property.getProperty(this.SLF4J_KEY_UNIT()));
      TimeUnit var11;
      if (var8 instanceof Some) {
         Some var9 = (Some)var8;
         String s = (String)var9.x();
         var11 = TimeUnit.valueOf(s.toUpperCase(Locale.ROOT));
      } else {
         if (!None$.MODULE$.equals(var8)) {
            throw new MatchError(var8);
         }

         var11 = TimeUnit.valueOf(this.SLF4J_DEFAULT_UNIT());
      }

      this.pollUnit = var11;
      MetricsSystem$.MODULE$.checkMinimalPollingPeriod(this.pollUnit(), this.pollPeriod());
      this.reporter = Slf4jReporter.forRegistry(registry).convertDurationsTo(TimeUnit.MILLISECONDS).convertRatesTo(TimeUnit.SECONDS).build();
   }
}
