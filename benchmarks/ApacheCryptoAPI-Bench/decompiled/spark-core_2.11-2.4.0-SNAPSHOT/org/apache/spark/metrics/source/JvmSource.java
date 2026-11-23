package org.apache.spark.metrics.source;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import java.lang.management.ManagementFactory;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001]2Q!\u0001\u0002\u0001\r1\u0011\u0011B\u0013<n'>,(oY3\u000b\u0005\r!\u0011AB:pkJ\u001cWM\u0003\u0002\u0006\r\u00059Q.\u001a;sS\u000e\u001c(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0007\u0001i1\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)Ui\u0011AA\u0005\u0003-\t\u0011aaU8ve\u000e,\u0007\"\u0002\r\u0001\t\u0003Q\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003m\u0001\"\u0001\u0006\u0001\t\u000fu\u0001!\u0019!C!=\u0005Q1o\\;sG\u0016t\u0015-\\3\u0016\u0003}\u0001\"\u0001I\u0013\u000e\u0003\u0005R!AI\u0012\u0002\t1\fgn\u001a\u0006\u0002I\u0005!!.\u0019<b\u0013\t1\u0013E\u0001\u0004TiJLgn\u001a\u0005\u0007Q\u0001\u0001\u000b\u0011B\u0010\u0002\u0017M|WO]2f\u001d\u0006lW\r\t\u0005\bU\u0001\u0011\r\u0011\"\u0011,\u00039iW\r\u001e:jGJ+w-[:uef,\u0012\u0001\f\t\u0003[Mj\u0011A\f\u0006\u0003\u000b=R!\u0001M\u0019\u0002\u0011\r|G-\u00195bY\u0016T\u0011AM\u0001\u0004G>l\u0017B\u0001\u001b/\u00059iU\r\u001e:jGJ+w-[:uefDaA\u000e\u0001!\u0002\u0013a\u0013aD7fiJL7MU3hSN$(/\u001f\u0011"
)
public class JvmSource implements Source {
   private final String sourceName = "jvm";
   private final MetricRegistry metricRegistry = new MetricRegistry();

   public String sourceName() {
      return this.sourceName;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public JvmSource() {
      this.metricRegistry().registerAll(new GarbageCollectorMetricSet());
      this.metricRegistry().registerAll(new MemoryUsageGaugeSet());
      this.metricRegistry().registerAll(new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
   }
}
