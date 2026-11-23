package org.apache.spark.scheduler;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.apache.spark.metrics.source.Source;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-3Q!\u0001\u0002\u0001\u0005)\u0011!\u0003R!H'\u000eDW\rZ;mKJ\u001cv.\u001e:dK*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0004\u0001-\t\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\u0013/5\t1C\u0003\u0002\u0015+\u000511o\\;sG\u0016T!A\u0006\u0003\u0002\u000f5,GO]5dg&\u0011\u0001d\u0005\u0002\u0007'>,(oY3\t\u0011i\u0001!Q1A\u0005\u0002q\tA\u0002Z1h'\u000eDW\rZ;mKJ\u001c\u0001!F\u0001\u001e!\tqr$D\u0001\u0003\u0013\t\u0001#A\u0001\u0007E\u0003\u001e\u001b6\r[3ek2,'\u000f\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003\u001e\u00035!\u0017mZ*dQ\u0016$W\u000f\\3sA!)A\u0005\u0001C\u0001K\u00051A(\u001b8jiz\"\"AJ\u0014\u0011\u0005y\u0001\u0001\"\u0002\u000e$\u0001\u0004i\u0002bB\u0015\u0001\u0005\u0004%\tEK\u0001\u000f[\u0016$(/[2SK\u001eL7\u000f\u001e:z+\u0005Y\u0003C\u0001\u00173\u001b\u0005i#B\u0001\f/\u0015\ty\u0003'\u0001\u0005d_\u0012\f\u0007.\u00197f\u0015\u0005\t\u0014aA2p[&\u00111'\f\u0002\u000f\u001b\u0016$(/[2SK\u001eL7\u000f\u001e:z\u0011\u0019)\u0004\u0001)A\u0005W\u0005yQ.\u001a;sS\u000e\u0014VmZ5tiJL\b\u0005C\u00048\u0001\t\u0007I\u0011\t\u001d\u0002\u0015M|WO]2f\u001d\u0006lW-F\u0001:!\tQt(D\u0001<\u0015\taT(\u0001\u0003mC:<'\"\u0001 \u0002\t)\fg/Y\u0005\u0003\u0001n\u0012aa\u0015;sS:<\u0007B\u0002\"\u0001A\u0003%\u0011(A\u0006t_V\u00148-\u001a(b[\u0016\u0004\u0003b\u0002#\u0001\u0005\u0004%\t!R\u0001\u0017[\u0016\u001c8/Y4f!J|7-Z:tS:<G+[7feV\ta\t\u0005\u0002-\u000f&\u0011\u0001*\f\u0002\u0006)&lWM\u001d\u0005\u0007\u0015\u0002\u0001\u000b\u0011\u0002$\u0002/5,7o]1hKB\u0013xnY3tg&tw\rV5nKJ\u0004\u0003"
)
public class DAGSchedulerSource implements Source {
   private final DAGScheduler dagScheduler;
   private final MetricRegistry metricRegistry;
   private final String sourceName;
   private final Timer messageProcessingTimer;

   public DAGScheduler dagScheduler() {
      return this.dagScheduler;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public Timer messageProcessingTimer() {
      return this.messageProcessingTimer;
   }

   public DAGSchedulerSource(DAGScheduler dagScheduler) {
      this.dagScheduler = dagScheduler;
      this.metricRegistry = new MetricRegistry();
      this.sourceName = "DAGScheduler";
      this.metricRegistry().register(MetricRegistry.name("stage", new String[]{"failedStages"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final DAGSchedulerSource $outer;

         public int getValue() {
            return this.$outer.dagScheduler().failedStages().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("stage", new String[]{"runningStages"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final DAGSchedulerSource $outer;

         public int getValue() {
            return this.$outer.dagScheduler().runningStages().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("stage", new String[]{"waitingStages"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final DAGSchedulerSource $outer;

         public int getValue() {
            return this.$outer.dagScheduler().waitingStages().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("job", new String[]{"allJobs"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final DAGSchedulerSource $outer;

         public int getValue() {
            return this.$outer.dagScheduler().numTotalJobs();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("job", new String[]{"activeJobs"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final DAGSchedulerSource $outer;

         public int getValue() {
            return this.$outer.dagScheduler().activeJobs().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.messageProcessingTimer = this.metricRegistry().timer(MetricRegistry.name("messageProcessingTime", new String[0]));
   }
}
