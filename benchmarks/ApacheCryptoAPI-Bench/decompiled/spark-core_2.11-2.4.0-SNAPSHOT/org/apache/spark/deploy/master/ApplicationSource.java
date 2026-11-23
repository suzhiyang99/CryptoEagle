package org.apache.spark.deploy.master;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.metrics.source.Source;
import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u00113Q!\u0001\u0002\u0001\u00051\u0011\u0011#\u00119qY&\u001c\u0017\r^5p]N{WO]2f\u0015\t\u0019A!\u0001\u0004nCN$XM\u001d\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0007\u0001i1\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)ei\u0011!\u0006\u0006\u0003-]\taa]8ve\u000e,'B\u0001\r\u0007\u0003\u001diW\r\u001e:jGNL!AG\u000b\u0003\rM{WO]2f\u0011!a\u0002A!b\u0001\n\u0003q\u0012aC1qa2L7-\u0019;j_:\u001c\u0001!F\u0001 !\t\u0001\u0013%D\u0001\u0003\u0013\t\u0011#AA\bBaBd\u0017nY1uS>t\u0017J\u001c4p\u0011!!\u0003A!A!\u0002\u0013y\u0012\u0001D1qa2L7-\u0019;j_:\u0004\u0003\"\u0002\u0014\u0001\t\u00039\u0013A\u0002\u001fj]&$h\b\u0006\u0002)SA\u0011\u0001\u0005\u0001\u0005\u00069\u0015\u0002\ra\b\u0005\bW\u0001\u0011\r\u0011\"\u0011-\u00039iW\r\u001e:jGJ+w-[:uef,\u0012!\f\t\u0003]Qj\u0011a\f\u0006\u00031AR!!\r\u001a\u0002\u0011\r|G-\u00195bY\u0016T\u0011aM\u0001\u0004G>l\u0017BA\u001b0\u00059iU\r\u001e:jGJ+w-[:uefDaa\u000e\u0001!\u0002\u0013i\u0013aD7fiJL7MU3hSN$(/\u001f\u0011\t\u000fe\u0002!\u0019!C!u\u0005Q1o\\;sG\u0016t\u0015-\\3\u0016\u0003m\u0002\"\u0001P \u000f\u00059i\u0014B\u0001 \u0010\u0003\u0019\u0001&/\u001a3fM&\u0011\u0001)\u0011\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005yz\u0001BB\"\u0001A\u0003%1(A\u0006t_V\u00148-\u001a(b[\u0016\u0004\u0003"
)
public class ApplicationSource implements Source {
   private final ApplicationInfo application;
   private final MetricRegistry metricRegistry;
   private final String sourceName;

   public ApplicationInfo application() {
      return this.application;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public ApplicationSource(ApplicationInfo application) {
      this.application = application;
      this.metricRegistry = new MetricRegistry();
      this.sourceName = (new StringOps(Predef$.MODULE$.augmentString("%s.%s.%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{"application", application.desc().name(), BoxesRunTime.boxToLong(System.currentTimeMillis())}));
      this.metricRegistry().register(MetricRegistry.name("status", new String[0]), new Gauge<String>(this) {
         // $FF: synthetic field
         private final ApplicationSource $outer;

         public String getValue() {
            return this.$outer.application().state().toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("runtime_ms", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final ApplicationSource $outer;

         public long getValue() {
            return this.$outer.application().duration();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("cores", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final ApplicationSource $outer;

         public int getValue() {
            return this.$outer.application().coresGranted();
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
}
