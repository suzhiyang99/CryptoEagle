package org.apache.spark.deploy.master;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.metrics.source.Source;
import scala.Enumeration;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00113Q!\u0001\u0002\u0001\r1\u0011A\"T1ti\u0016\u00148k\\;sG\u0016T!a\u0001\u0003\u0002\r5\f7\u000f^3s\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!\u0012$D\u0001\u0016\u0015\t1r#\u0001\u0004t_V\u00148-\u001a\u0006\u00031\u0019\tq!\\3ue&\u001c7/\u0003\u0002\u001b+\t11k\\;sG\u0016D\u0001b\u0001\u0001\u0003\u0006\u0004%\t!H\u0002\u0001+\u0005q\u0002CA\u0010!\u001b\u0005\u0011\u0011BA\u0011\u0003\u0005\u0019i\u0015m\u001d;fe\"A1\u0005\u0001B\u0001B\u0003%a$A\u0004nCN$XM\u001d\u0011\t\u000b\u0015\u0002A\u0011\u0001\u0014\u0002\rqJg.\u001b;?)\t9\u0003\u0006\u0005\u0002 \u0001!)1\u0001\na\u0001=!9!\u0006\u0001b\u0001\n\u0003Z\u0013AD7fiJL7MU3hSN$(/_\u000b\u0002YA\u0011QfM\u0007\u0002])\u0011\u0001d\f\u0006\u0003aE\n\u0001bY8eC\"\fG.\u001a\u0006\u0002e\u0005\u00191m\\7\n\u0005Qr#AD'fiJL7MU3hSN$(/\u001f\u0005\u0007m\u0001\u0001\u000b\u0011\u0002\u0017\u0002\u001f5,GO]5d%\u0016<\u0017n\u001d;ss\u0002Bq\u0001\u000f\u0001C\u0002\u0013\u0005\u0013(\u0001\u0006t_V\u00148-\u001a(b[\u0016,\u0012A\u000f\t\u0003w\u0001k\u0011\u0001\u0010\u0006\u0003{y\nA\u0001\\1oO*\tq(\u0001\u0003kCZ\f\u0017BA!=\u0005\u0019\u0019FO]5oO\"11\t\u0001Q\u0001\ni\n1b]8ve\u000e,g*Y7fA\u0001"
)
public class MasterSource implements Source {
   private final Master master;
   private final MetricRegistry metricRegistry;
   private final String sourceName;

   public Master master() {
      return this.master;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public MasterSource(Master master) {
      this.master = master;
      this.metricRegistry = new MetricRegistry();
      this.sourceName = "master";
      this.metricRegistry().register(MetricRegistry.name("workers", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final MasterSource $outer;

         public int getValue() {
            return this.$outer.master().workers().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("aliveWorkers", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final MasterSource $outer;

         public int getValue() {
            return this.$outer.master().workers().count(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(WorkerInfo x$1) {
                  boolean var3;
                  label23: {
                     Enumeration.Value var10000 = x$1.state();
                     Enumeration.Value var2 = WorkerState$.MODULE$.ALIVE();
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
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("apps", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final MasterSource $outer;

         public int getValue() {
            return this.$outer.master().apps().size();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("waitingApps", new String[0]), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final MasterSource $outer;

         public int getValue() {
            return this.$outer.master().apps().count(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(ApplicationInfo x$2) {
                  boolean var3;
                  label23: {
                     Enumeration.Value var10000 = x$2.state();
                     Enumeration.Value var2 = ApplicationState$.MODULE$.WAITING();
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
            });
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
