package org.apache.spark.storage;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.metrics.source.Source;
import scala.Array$;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.math.Numeric;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001q3Q!\u0001\u0002\u0001\t)\u0011!C\u00117pG.l\u0015M\\1hKJ\u001cv.\u001e:dK*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\f\u000e\u0003MQ!\u0001F\u000b\u0002\rM|WO]2f\u0015\t1B!A\u0004nKR\u0014\u0018nY:\n\u0005a\u0019\"AB*pkJ\u001cW\r\u0003\u0005\u001b\u0001\t\u0015\r\u0011\"\u0001\u001d\u00031\u0011Gn\\2l\u001b\u0006t\u0017mZ3s\u0007\u0001)\u0012!\b\t\u0003=}i\u0011AA\u0005\u0003A\t\u0011AB\u00117pG.l\u0015M\\1hKJD\u0001B\t\u0001\u0003\u0002\u0003\u0006I!H\u0001\u000eE2|7m['b]\u0006<WM\u001d\u0011\t\u000b\u0011\u0002A\u0011A\u0013\u0002\rqJg.\u001b;?)\t1s\u0005\u0005\u0002\u001f\u0001!)!d\ta\u0001;!9\u0011\u0006\u0001b\u0001\n\u0003R\u0013AD7fiJL7MU3hSN$(/_\u000b\u0002WA\u0011AFM\u0007\u0002[)\u0011aC\f\u0006\u0003_A\n\u0001bY8eC\"\fG.\u001a\u0006\u0002c\u0005\u00191m\\7\n\u0005Mj#AD'fiJL7MU3hSN$(/\u001f\u0005\u0007k\u0001\u0001\u000b\u0011B\u0016\u0002\u001f5,GO]5d%\u0016<\u0017n\u001d;ss\u0002Bqa\u000e\u0001C\u0002\u0013\u0005\u0003(\u0001\u0006t_V\u00148-\u001a(b[\u0016,\u0012!\u000f\t\u0003u}j\u0011a\u000f\u0006\u0003yu\nA\u0001\\1oO*\ta(\u0001\u0003kCZ\f\u0017B\u0001!<\u0005\u0019\u0019FO]5oO\"1!\t\u0001Q\u0001\ne\n1b]8ve\u000e,g*Y7fA!)A\t\u0001C\u0005\u000b\u0006i!/Z4jgR,'oR1vO\u0016$2AR%R!\taq)\u0003\u0002I\u001b\t!QK\\5u\u0011\u0015Q5\t1\u0001L\u0003\u0011q\u0017-\\3\u0011\u00051{eB\u0001\u0007N\u0013\tqU\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u0001BS!AT\u0007\t\u000bI\u001b\u0005\u0019A*\u0002\t\u0019,hn\u0019\t\u0005\u0019Q3\u0016,\u0003\u0002V\u001b\tIa)\u001e8di&|g.\r\t\u0003=]K!\u0001\u0017\u0002\u0003%\tcwnY6NC:\fw-\u001a:NCN$XM\u001d\t\u0003\u0019iK!aW\u0007\u0003\t1{gn\u001a"
)
public class BlockManagerSource implements Source {
   private final BlockManager blockManager;
   private final MetricRegistry metricRegistry;
   private final String sourceName;

   public BlockManager blockManager() {
      return this.blockManager;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public String sourceName() {
      return this.sourceName;
   }

   private void registerGauge(String name, Function1<BlockManagerMaster, Object> func) {
      this.metricRegistry().register(name, new Gauge<Object>(this, func) {
         // $FF: synthetic field
         private final BlockManagerSource $outer;
         private final Function1 func$1;

         public long getValue() {
            return BoxesRunTime.unboxToLong(this.func$1.apply(this.$outer.blockManager().master())) / 1024L / 1024L;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.func$1 = func$1;
            }
         }
      });
   }

   public BlockManagerSource(BlockManager blockManager) {
      this.blockManager = blockManager;
      this.metricRegistry = new MetricRegistry();
      this.sourceName = "BlockManager";
      this.registerGauge(MetricRegistry.name("memory", new String[]{"maxMem_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$1) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$1.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$2) {
                  return x$2.maxMem();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"maxOnHeapMem_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$3) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$3.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$4) {
                  return BoxesRunTime.unboxToLong(x$4.maxOnHeapMem().getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return 0L;
                     }
                  }));
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"maxOffHeapMem_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$5) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$5.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$6) {
                  return BoxesRunTime.unboxToLong(x$6.maxOffHeapMem().getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return 0L;
                     }
                  }));
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"remainingMem_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$7) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$7.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$8) {
                  return x$8.memRemaining();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"remainingOnHeapMem_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$9) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$9.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$10) {
                  return BoxesRunTime.unboxToLong(x$10.onHeapMemRemaining().getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return 0L;
                     }
                  }));
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"remainingOffHeapMem_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$11) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$11.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$12) {
                  return BoxesRunTime.unboxToLong(x$12.offHeapMemRemaining().getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return 0L;
                     }
                  }));
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"memUsed_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$13) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$13.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$14) {
                  return x$14.memUsed();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"onHeapMemUsed_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$15) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$15.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$16) {
                  return BoxesRunTime.unboxToLong(x$16.onHeapMemUsed().getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return 0L;
                     }
                  }));
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("memory", new String[]{"offHeapMemUsed_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$17) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$17.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$18) {
                  return BoxesRunTime.unboxToLong(x$18.offHeapMemUsed().getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return 0L;
                     }
                  }));
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
      this.registerGauge(MetricRegistry.name("disk", new String[]{"diskSpaceUsed_MB"}), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMaster x$19) {
            return BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps((long[])Predef$.MODULE$.refArrayOps(x$19.getStorageStatus()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(StorageStatus x$20) {
                  return x$20.diskUsed();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()))).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      });
   }
}
