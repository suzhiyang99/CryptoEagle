package org.apache.spark.status;

import org.apache.spark.status.api.v1.RDDDataDistribution;
import org.apache.spark.status.api.v1.RDDStorageInfo;
import org.apache.spark.storage.RDDInfo;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.Iterable$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055b\u0001B\u0001\u0003\t-\u0011q\u0001T5wKJ#EI\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0002\u000e\u001d5\t!!\u0003\u0002\u0010\u0005\tQA*\u001b<f\u000b:$\u0018\u000e^=\t\u0011E\u0001!Q1A\u0005\u0002I\tA!\u001b8g_V\t1\u0003\u0005\u0002\u0015/5\tQC\u0003\u0002\u0017\t\u000591\u000f^8sC\u001e,\u0017B\u0001\r\u0016\u0005\u001d\u0011F\tR%oM>D\u0001B\u0007\u0001\u0003\u0002\u0003\u0006IaE\u0001\u0006S:4w\u000e\t\u0005\u00069\u0001!\t!H\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005yy\u0002CA\u0007\u0001\u0011\u0015\t2\u00041\u0001\u0014\u0011\u001d\t\u0003\u00011A\u0005\u0002\t\nAb\u001d;pe\u0006<W\rT3wK2,\u0012a\t\t\u0003I)r!!\n\u0015\u000e\u0003\u0019R\u0011aJ\u0001\u0006g\u000e\fG.Y\u0005\u0003S\u0019\na\u0001\u0015:fI\u00164\u0017BA\u0016-\u0005\u0019\u0019FO]5oO*\u0011\u0011F\n\u0005\b]\u0001\u0001\r\u0011\"\u00010\u0003A\u0019Ho\u001c:bO\u0016dUM^3m?\u0012*\u0017\u000f\u0006\u00021gA\u0011Q%M\u0005\u0003e\u0019\u0012A!\u00168ji\"9A'LA\u0001\u0002\u0004\u0019\u0013a\u0001=%c!1a\u0007\u0001Q!\n\r\nQb\u001d;pe\u0006<W\rT3wK2\u0004\u0003b\u0002\u001d\u0001\u0001\u0004%\t!O\u0001\u000b[\u0016lwN]=Vg\u0016$W#\u0001\u001e\u0011\u0005\u0015Z\u0014B\u0001\u001f'\u0005\u0011auN\\4\t\u000fy\u0002\u0001\u0019!C\u0001\u007f\u0005qQ.Z7pef,6/\u001a3`I\u0015\fHC\u0001\u0019A\u0011\u001d!T(!AA\u0002iBaA\u0011\u0001!B\u0013Q\u0014aC7f[>\u0014\u00180V:fI\u0002Bq\u0001\u0012\u0001A\u0002\u0013\u0005\u0011(\u0001\u0005eSN\\Wk]3e\u0011\u001d1\u0005\u00011A\u0005\u0002\u001d\u000bA\u0002Z5tWV\u001bX\rZ0%KF$\"\u0001\r%\t\u000fQ*\u0015\u0011!a\u0001u!1!\n\u0001Q!\ni\n\u0011\u0002Z5tWV\u001bX\r\u001a\u0011\t\u000f1\u0003!\u0019!C\u0005\u001b\u0006Q\u0001/\u0019:uSRLwN\\:\u0016\u00039\u0003Ba\u0014+$-6\t\u0001K\u0003\u0002R%\u00069Q.\u001e;bE2,'BA*'\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003+B\u0013q\u0001S1tQ6\u000b\u0007\u000f\u0005\u0002\u000e/&\u0011\u0001L\u0001\u0002\u0011\u0019&4XM\u0015#E!\u0006\u0014H/\u001b;j_:DaA\u0017\u0001!\u0002\u0013q\u0015a\u00039beRLG/[8og\u0002Bq\u0001\u0018\u0001C\u0002\u0013%Q,\u0001\u0007qCJ$\u0018\u000e^5p]N+\u0017/F\u0001_!\tiq,\u0003\u0002a\u0005\ty!\u000b\u0012#QCJ$\u0018\u000e^5p]N+\u0017\u000f\u0003\u0004c\u0001\u0001\u0006IAX\u0001\u000ea\u0006\u0014H/\u001b;j_:\u001cV-\u001d\u0011\t\u000f\u0011\u0004!\u0019!C\u0005K\u0006iA-[:ue&\u0014W\u000f^5p]N,\u0012A\u001a\t\u0005\u001fR\u001bs\r\u0005\u0002\u000eQ&\u0011\u0011N\u0001\u0002\u0014\u0019&4XM\u0015#E\t&\u001cHO]5ckRLwN\u001c\u0005\u0007W\u0002\u0001\u000b\u0011\u00024\u0002\u001d\u0011L7\u000f\u001e:jEV$\u0018n\u001c8tA!)Q\u000e\u0001C\u0001]\u0006y1/\u001a;Ti>\u0014\u0018mZ3MKZ,G\u000e\u0006\u00021_\")\u0001\u000f\u001ca\u0001G\u0005)A.\u001a<fY\")!\u000f\u0001C\u0001g\u0006I\u0001/\u0019:uSRLwN\u001c\u000b\u0003-RDQ!^9A\u0002\r\n\u0011B\u00197pG.t\u0015-\\3\t\u000b]\u0004A\u0011\u0001=\u0002\u001fI,Wn\u001c<f!\u0006\u0014H/\u001b;j_:$\"\u0001M=\t\u000bU4\b\u0019A\u0012\t\u000bm\u0004A\u0011\u0001?\u0002\u0019\u0011L7\u000f\u001e:jEV$\u0018n\u001c8\u0015\u0005\u001dl\b\"\u0002@{\u0001\u0004y\u0018\u0001B3yK\u000e\u00042!DA\u0001\u0013\r\t\u0019A\u0001\u0002\r\u0019&4X-\u0012=fGV$xN\u001d\u0005\b\u0003\u000f\u0001A\u0011AA\u0005\u0003I\u0011X-\\8wK\u0012K7\u000f\u001e:jEV$\u0018n\u001c8\u0015\t\u0005-\u0011\u0011\u0003\t\u0004K\u00055\u0011bAA\bM\t9!i\\8mK\u0006t\u0007B\u0002@\u0002\u0006\u0001\u0007q\u0010C\u0004\u0002\u0016\u0001!\t!a\u0006\u0002\u001f\u0011L7\u000f\u001e:jEV$\u0018n\u001c8PaR$B!!\u0007\u0002 A!Q%a\u0007h\u0013\r\tiB\n\u0002\u0007\u001fB$\u0018n\u001c8\t\ry\f\u0019\u00021\u0001\u0000\u0011\u001d\t\u0019\u0003\u0001C)\u0003K\t\u0001\u0002Z8Va\u0012\fG/\u001a\u000b\u0003\u0003O\u00012!JA\u0015\u0013\r\tYC\n\u0002\u0004\u0003:L\b"
)
public class LiveRDD extends LiveEntity {
   private final RDDInfo info;
   private String storageLevel;
   private long memoryUsed;
   private long diskUsed;
   private final HashMap<String, LiveRDDPartition> partitions;
   private final RDDPartitionSeq org$apache$spark$status$LiveRDD$$partitionSeq;
   private final HashMap<String, LiveRDDDistribution> distributions;

   public RDDInfo info() {
      return this.info;
   }

   public String storageLevel() {
      return this.storageLevel;
   }

   public void storageLevel_$eq(String x$1) {
      this.storageLevel = x$1;
   }

   public long memoryUsed() {
      return this.memoryUsed;
   }

   public void memoryUsed_$eq(long x$1) {
      this.memoryUsed = x$1;
   }

   public long diskUsed() {
      return this.diskUsed;
   }

   public void diskUsed_$eq(long x$1) {
      this.diskUsed = x$1;
   }

   private HashMap<String, LiveRDDPartition> partitions() {
      return this.partitions;
   }

   public RDDPartitionSeq org$apache$spark$status$LiveRDD$$partitionSeq() {
      return this.org$apache$spark$status$LiveRDD$$partitionSeq;
   }

   private HashMap<String, LiveRDDDistribution> distributions() {
      return this.distributions;
   }

   public void setStorageLevel(String level) {
      this.storageLevel_$eq(LiveEntityHelpers$.MODULE$.weakIntern(level));
   }

   public LiveRDDPartition partition(String blockName) {
      return (LiveRDDPartition)this.partitions().getOrElseUpdate(blockName, new Serializable(this, blockName) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LiveRDD $outer;
         private final String blockName$1;

         public final LiveRDDPartition apply() {
            LiveRDDPartition part = new LiveRDDPartition(this.blockName$1);
            part.update(Nil$.MODULE$, this.$outer.storageLevel(), 0L, 0L);
            this.$outer.org$apache$spark$status$LiveRDD$$partitionSeq().addPartition(part);
            return part;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockName$1 = blockName$1;
            }
         }
      });
   }

   public void removePartition(String blockName) {
      this.partitions().remove(blockName).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final LiveRDD $outer;

         public final void apply(LiveRDDPartition part) {
            this.$outer.org$apache$spark$status$LiveRDD$$partitionSeq().removePartition(part);
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

   public LiveRDDDistribution distribution(LiveExecutor exec) {
      return (LiveRDDDistribution)this.distributions().getOrElseUpdate(exec.executorId(), new Serializable(this, exec) {
         public static final long serialVersionUID = 0L;
         private final LiveExecutor exec$1;

         public final LiveRDDDistribution apply() {
            return new LiveRDDDistribution(this.exec$1);
         }

         public {
            this.exec$1 = exec$1;
         }
      });
   }

   public boolean removeDistribution(LiveExecutor exec) {
      return this.distributions().remove(exec.executorId()).isDefined();
   }

   public Option<LiveRDDDistribution> distributionOpt(LiveExecutor exec) {
      return this.distributions().get(exec.executorId());
   }

   public Object doUpdate() {
      Option dists = (Option)(this.distributions().nonEmpty() ? new Some(((TraversableOnce)this.distributions().values().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RDDDataDistribution apply(LiveRDDDistribution x$4) {
            return x$4.toApi();
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq()) : None$.MODULE$);
      RDDStorageInfo rdd = new RDDStorageInfo(this.info().id(), this.info().name(), this.info().numPartitions(), this.partitions().size(), this.storageLevel(), this.memoryUsed(), this.diskUsed(), dists, new Some(this.org$apache$spark$status$LiveRDD$$partitionSeq()));
      return new RDDStorageInfoWrapper(rdd);
   }

   public LiveRDD(RDDInfo info) {
      this.info = info;
      this.storageLevel = LiveEntityHelpers$.MODULE$.weakIntern(info.storageLevel().description());
      this.memoryUsed = 0L;
      this.diskUsed = 0L;
      this.partitions = new HashMap<String, LiveRDDPartition>();
      this.org$apache$spark$status$LiveRDD$$partitionSeq = new RDDPartitionSeq();
      this.distributions = new HashMap<String, LiveRDDDistribution>();
   }
}
