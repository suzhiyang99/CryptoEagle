package org.apache.spark.deploy.worker;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.metrics.source.Source;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00113Q!\u0001\u0002\u0001\u00051\u0011AbV8sW\u0016\u00148k\\;sG\u0016T!a\u0001\u0003\u0002\r]|'o[3s\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!\u0012$D\u0001\u0016\u0015\t1r#\u0001\u0004t_V\u00148-\u001a\u0006\u00031\u0019\tq!\\3ue&\u001c7/\u0003\u0002\u001b+\t11k\\;sG\u0016D\u0001b\u0001\u0001\u0003\u0006\u0004%\t!H\u0002\u0001+\u0005q\u0002CA\u0010!\u001b\u0005\u0011\u0011BA\u0011\u0003\u0005\u00199vN]6fe\"A1\u0005\u0001B\u0001B\u0003%a$A\u0004x_J\\WM\u001d\u0011\t\u000b\u0015\u0002A\u0011\u0001\u0014\u0002\rqJg.\u001b;?)\t9\u0003\u0006\u0005\u0002 \u0001!)1\u0001\na\u0001=!9!\u0006\u0001b\u0001\n\u0003Z\u0013AC:pkJ\u001cWMT1nKV\tA\u0006\u0005\u0002.e5\taF\u0003\u00020a\u0005!A.\u00198h\u0015\u0005\t\u0014\u0001\u00026bm\u0006L!a\r\u0018\u0003\rM#(/\u001b8h\u0011\u0019)\u0004\u0001)A\u0005Y\u0005Y1o\\;sG\u0016t\u0015-\\3!\u0011\u001d9\u0004A1A\u0005Ba\na\"\\3ue&\u001c'+Z4jgR\u0014\u00180F\u0001:!\tQ\u0004)D\u0001<\u0015\tABH\u0003\u0002>}\u0005A1m\u001c3bQ\u0006dWMC\u0001@\u0003\r\u0019w.\\\u0005\u0003\u0003n\u0012a\"T3ue&\u001c'+Z4jgR\u0014\u0018\u0010\u0003\u0004D\u0001\u0001\u0006I!O\u0001\u0010[\u0016$(/[2SK\u001eL7\u000f\u001e:zA\u0001"
)
public class WorkerSource implements Source {
   private final Worker worker;
   private final String sourceName;
   private final MetricRegistry metricRegistry;

   public Worker worker() {
      return this.worker;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public WorkerSource(Worker worker) {
      this.worker = worker;
      this.sourceName = "worker";
      this.metricRegistry = new MetricRegistry();
      this.metricRegistry().register(MetricRegistry.name("executors", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final WorkerSource $outer;

         public int getValue() {
            return this.$outer.worker().executors().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("coresUsed", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final WorkerSource $outer;

         public int getValue() {
            return this.$outer.worker().coresUsed();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("memUsed_MB", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final WorkerSource $outer;

         public int getValue() {
            return this.$outer.worker().memoryUsed();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("coresFree", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final WorkerSource $outer;

         public int getValue() {
            return this.$outer.worker().coresFree();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("memFree_MB", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final WorkerSource $outer;

         public int getValue() {
            return this.$outer.worker().memoryFree();
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
