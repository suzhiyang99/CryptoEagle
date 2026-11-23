package org.apache.spark;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.shuffle.MetadataFetchFailedException;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManagerId;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.Map;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001I4Q!\u0001\u0002\u0001\u0005!\u0011a#T1q\u001fV$\b/\u001e;Ue\u0006\u001c7.\u001a:X_J\\WM\u001d\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sON\u0011\u0001!\u0003\t\u0003\u0015-i\u0011AA\u0005\u0003\u0019\t\u0011\u0001#T1q\u001fV$\b/\u001e;Ue\u0006\u001c7.\u001a:\t\u00119\u0001!\u0011!Q\u0001\nA\tAaY8oM\u000e\u0001\u0001C\u0001\u0006\u0012\u0013\t\u0011\"AA\u0005Ta\u0006\u00148nQ8oM\")A\u0003\u0001C\u0001+\u00051A(\u001b8jiz\"\"AF\f\u0011\u0005)\u0001\u0001\"\u0002\b\u0014\u0001\u0004\u0001\u0002bB\r\u0001\u0005\u0004%\tAG\u0001\f[\u0006\u00048\u000b^1ukN,7/F\u0001\u001c!\u0011a2%J\u0015\u000e\u0003uQ!AH\u0010\u0002\u000f5,H/\u00192mK*\u0011\u0001%I\u0001\u000bG>dG.Z2uS>t'\"\u0001\u0012\u0002\u000bM\u001c\u0017\r\\1\n\u0005\u0011j\"aA'baB\u0011aeJ\u0007\u0002C%\u0011\u0001&\t\u0002\u0004\u0013:$\bc\u0001\u0014+Y%\u00111&\t\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003[Aj\u0011A\f\u0006\u0003_\t\t\u0011b]2iK\u0012,H.\u001a:\n\u0005Er#!C'baN#\u0018\r^;t\u0011\u0019\u0019\u0004\u0001)A\u00057\u0005aQ.\u00199Ti\u0006$Xo]3tA!9Q\u0007\u0001b\u0001\n\u00131\u0014\u0001\u00034fi\u000eD\u0017N\\4\u0016\u0003]\u00022\u0001\b\u001d&\u0013\tITDA\u0004ICND7+\u001a;\t\rm\u0002\u0001\u0015!\u00038\u0003%1W\r^2iS:<\u0007\u0005C\u0003>\u0001\u0011\u0005c(A\fhKRl\u0015\r]*ju\u0016\u001c()_#yK\u000e,Ho\u001c:JIR!q\b\u00180a!\r\u0001\u0005j\u0013\b\u0003\u0003\u001as!AQ#\u000e\u0003\rS!\u0001R\b\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0011\u0013BA$\"\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u0013&\u0003\u0007M+\u0017O\u0003\u0002HCA!a\u0005\u0014(U\u0013\ti\u0015E\u0001\u0004UkBdWM\r\t\u0003\u001fJk\u0011\u0001\u0015\u0006\u0003#\n\tqa\u001d;pe\u0006<W-\u0003\u0002T!\nq!\t\\8dW6\u000bg.Y4fe&#\u0007c\u0001!I+B!a\u0005\u0014,Z!\tyu+\u0003\u0002Y!\n9!\t\\8dW&#\u0007C\u0001\u0014[\u0013\tY\u0016E\u0001\u0003M_:<\u0007\"B/=\u0001\u0004)\u0013!C:ik\u001a4G.Z%e\u0011\u0015yF\b1\u0001&\u00039\u0019H/\u0019:u!\u0006\u0014H/\u001b;j_:DQ!\u0019\u001fA\u0002\u0015\nA\"\u001a8e!\u0006\u0014H/\u001b;j_:DQa\u0019\u0001\u0005\n\u0011\f1bZ3u'R\fG/^:fgR\u0011\u0011&\u001a\u0005\u0006;\n\u0004\r!\n\u0005\u0006O\u0002!\t\u0001[\u0001\u0012k:\u0014XmZ5ti\u0016\u00148\u000b[;gM2,GCA5m!\t1#.\u0003\u0002lC\t!QK\\5u\u0011\u0015if\r1\u0001&\u0011\u0015q\u0007\u0001\"\u0001p\u0003-)\b\u000fZ1uK\u0016\u0003xn\u00195\u0015\u0005%\u0004\b\"B9n\u0001\u0004I\u0016\u0001\u00038fo\u0016\u0003xn\u00195"
)
public class MapOutputTrackerWorker extends MapOutputTracker {
   private final Map<Object, MapStatus[]> mapStatuses;
   private final HashSet<Object> fetching;

   public Map<Object, MapStatus[]> mapStatuses() {
      return this.mapStatuses;
   }

   private HashSet<Object> fetching() {
      return this.fetching;
   }

   public Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> getMapSizesByExecutorId(int shuffleId, int startPartition, int endPartition) {
      this.logDebug(new Serializable(this, shuffleId, startPartition, endPartition) {
         public static final long serialVersionUID = 0L;
         private final int shuffleId$4;
         private final int startPartition$3;
         private final int endPartition$3;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Fetching outputs for shuffle ", ", partitions ", "-", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.shuffleId$4), BoxesRunTime.boxToInteger(this.startPartition$3), BoxesRunTime.boxToInteger(this.endPartition$3)}));
         }

         public {
            this.shuffleId$4 = shuffleId$4;
            this.startPartition$3 = startPartition$3;
            this.endPartition$3 = endPartition$3;
         }
      });
      MapStatus[] statuses = this.getStatuses(shuffleId);

      try {
         return MapOutputTracker$.MODULE$.convertMapStatuses(shuffleId, startPartition, endPartition, statuses);
      } catch (MetadataFetchFailedException var6) {
         this.mapStatuses().clear();
         throw var6;
      }
   }

   private MapStatus[] getStatuses(int shuffleId) {
      MapStatus[] statuses = (MapStatus[])this.mapStatuses().get(BoxesRunTime.boxToInteger(shuffleId)).orNull(Predef$.MODULE$.$conforms());
      MapStatus[] var20;
      if (statuses == null) {
         this.logInfo(new Serializable(this, shuffleId) {
            public static final long serialVersionUID = 0L;
            private final int shuffleId$5;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Don't have map outputs for shuffle ").append((Object)BoxesRunTime.boxToInteger(this.shuffleId$5)).append((Object)", fetching them").toString();
            }

            public {
               this.shuffleId$5 = shuffleId$5;
            }
         });
         long startTime = System.currentTimeMillis();
         MapStatus[] fetchedStatuses = null;
         synchronized(this.fetching()) {
            while(this.fetching().contains(BoxesRunTime.boxToInteger(shuffleId))) {
               this.liftedTree1$1();
            }

            fetchedStatuses = (MapStatus[])this.mapStatuses().get(BoxesRunTime.boxToInteger(shuffleId)).orNull(Predef$.MODULE$.$conforms());
            if (fetchedStatuses == null) {
               this.fetching().$plus$eq(BoxesRunTime.boxToInteger(shuffleId));
            } else {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }
         }

         if (fetchedStatuses == null) {
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final MapOutputTrackerWorker $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Doing the fetch; tracker endpoint = ").append((Object)this.$outer.trackerEndpoint()).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });

            try {
               byte[] fetchedBytes = (byte[])this.askTracker(new GetMapOutputStatuses(shuffleId), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
               fetchedStatuses = MapOutputTracker$.MODULE$.deserializeMapStatuses(fetchedBytes);
               this.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Got the output locations";
                  }
               });
               this.mapStatuses().put(BoxesRunTime.boxToInteger(shuffleId), fetchedStatuses);
            } finally {
               synchronized(this.fetching()) {
                  this.fetching().$minus$eq(BoxesRunTime.boxToInteger(shuffleId));
                  this.fetching().notifyAll();
                  BoxedUnit var10001 = BoxedUnit.UNIT;
               }
            }
         } else {
            BoxedUnit var19 = BoxedUnit.UNIT;
         }

         this.logDebug(new Serializable(this, shuffleId, startTime) {
            public static final long serialVersionUID = 0L;
            private final int shuffleId$5;
            private final long startTime$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Fetching map output statuses for shuffle ", " took "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.shuffleId$5)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(System.currentTimeMillis() - this.startTime$1)}))).toString();
            }

            public {
               this.shuffleId$5 = shuffleId$5;
               this.startTime$1 = startTime$1;
            }
         });
         if (fetchedStatuses == null) {
            this.logError(new Serializable(this, shuffleId) {
               public static final long serialVersionUID = 0L;
               private final int shuffleId$5;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Missing all output locations for shuffle ").append((Object)BoxesRunTime.boxToInteger(this.shuffleId$5)).toString();
               }

               public {
                  this.shuffleId$5 = shuffleId$5;
               }
            });
            throw new MetadataFetchFailedException(shuffleId, -1, (new StringBuilder()).append((Object)"Missing all output locations for shuffle ").append((Object)BoxesRunTime.boxToInteger(shuffleId)).toString());
         }

         var20 = fetchedStatuses;
      } else {
         var20 = statuses;
      }

      return var20;
   }

   public void unregisterShuffle(int shuffleId) {
      this.mapStatuses().remove(BoxesRunTime.boxToInteger(shuffleId));
   }

   public void updateEpoch(long newEpoch) {
      synchronized(this.epochLock()) {
         if (newEpoch > this.epoch()) {
            this.logInfo(new Serializable(this, newEpoch) {
               public static final long serialVersionUID = 0L;
               private final long newEpoch$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Updating epoch to ").append((Object)BoxesRunTime.boxToLong(this.newEpoch$1)).append((Object)" and clearing cache").toString();
               }

               public {
                  this.newEpoch$1 = newEpoch$1;
               }
            });
            this.epoch_$eq(newEpoch);
            this.mapStatuses().clear();
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            BoxedUnit var5 = BoxedUnit.UNIT;
         }

      }
   }

   private final void liftedTree1$1() {
      try {
         this.fetching().wait();
      } catch (InterruptedException var2) {
      }

   }

   public MapOutputTrackerWorker(SparkConf conf) {
      super(conf);
      this.mapStatuses = (Map)JavaConverters$.MODULE$.mapAsScalaConcurrentMapConverter(new ConcurrentHashMap()).asScala();
      this.fetching = new HashSet<Object>();
   }
}
