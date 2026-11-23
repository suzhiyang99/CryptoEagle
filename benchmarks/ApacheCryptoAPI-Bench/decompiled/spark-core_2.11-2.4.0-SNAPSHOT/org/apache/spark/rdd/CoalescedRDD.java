package org.apache.spark.rdd;

import org.apache.spark.Dependency;
import org.apache.spark.NarrowDependency;
import org.apache.spark.Partition;
import org.apache.spark.TaskContext;
import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rd!B\u0001\u0003\u0001\u0011Q!\u0001D\"pC2,7oY3e%\u0012#%BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOV\u00111BE\n\u0003\u00011\u00012!\u0004\b\u0011\u001b\u0005\u0011\u0011BA\b\u0003\u0005\r\u0011F\t\u0012\t\u0003#Ia\u0001\u0001B\u0003\u0014\u0001\t\u0007QCA\u0001U\u0007\u0001\t\"A\u0006\u000f\u0011\u0005]QR\"\u0001\r\u000b\u0003e\tQa]2bY\u0006L!a\u0007\r\u0003\u000f9{G\u000f[5oOB\u0011q#H\u0005\u0003=a\u00111!\u00118z\u0011!\u0001\u0003A!a\u0001\n\u0003\t\u0013\u0001\u00029sKZ,\u0012\u0001\u0004\u0005\tG\u0001\u0011\t\u0019!C\u0001I\u0005A\u0001O]3w?\u0012*\u0017\u000f\u0006\u0002&QA\u0011qCJ\u0005\u0003Oa\u0011A!\u00168ji\"9\u0011FIA\u0001\u0002\u0004a\u0011a\u0001=%c!A1\u0006\u0001B\u0001B\u0003&A\"A\u0003qe\u00164\b\u0005\u000b\u0002+[A\u0011qCL\u0005\u0003_a\u0011\u0011\u0002\u001e:b]NLWM\u001c;\t\u0011E\u0002!\u0011!Q\u0001\nI\nQ\"\\1y!\u0006\u0014H/\u001b;j_:\u001c\bCA\f4\u0013\t!\u0004DA\u0002J]RD\u0001B\u000e\u0001\u0003\u0002\u0003\u0006IaN\u0001\u0013a\u0006\u0014H/\u001b;j_:\u001cu.\u00197fg\u000e,'\u000fE\u0002\u0018qiJ!!\u000f\r\u0003\r=\u0003H/[8o!\ti1(\u0003\u0002=\u0005\t\u0011\u0002+\u0019:uSRLwN\\\"pC2,7oY3s\u0011!q\u0004AaA!\u0002\u0017y\u0014AC3wS\u0012,gnY3%cA\u0019\u0001i\u0011\t\u000e\u0003\u0005S!A\u0011\r\u0002\u000fI,g\r\\3di&\u0011A)\u0011\u0002\t\u00072\f7o\u001d+bO\")a\t\u0001C\u0001\u000f\u00061A(\u001b8jiz\"B\u0001S&M\u001bR\u0011\u0011J\u0013\t\u0004\u001b\u0001\u0001\u0002\"\u0002 F\u0001\by\u0004\"\u0002\u0011F\u0001\u0004a\u0001\"B\u0019F\u0001\u0004\u0011\u0004b\u0002\u001cF!\u0003\u0005\ra\u000e\u0005\u0006\u001f\u0002!\t\u0005U\u0001\u000eO\u0016$\b+\u0019:uSRLwN\\:\u0016\u0003E\u00032a\u0006*U\u0013\t\u0019\u0006DA\u0003BeJ\f\u0017\u0010\u0005\u0002V-6\tA!\u0003\u0002X\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\u00063\u0002!\tEW\u0001\bG>l\u0007/\u001e;f)\rYv-\u001b\t\u00049\u0012\u0004bBA/c\u001d\tq\u0016-D\u0001`\u0015\t\u0001G#\u0001\u0004=e>|GOP\u0005\u00023%\u00111\rG\u0001\ba\u0006\u001c7.Y4f\u0013\t)gM\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0015\t\u0019\u0007\u0004C\u0003i1\u0002\u0007A+A\u0005qCJ$\u0018\u000e^5p]\")!\u000e\u0017a\u0001W\u000691m\u001c8uKb$\bCA+m\u0013\tiGAA\u0006UCN\\7i\u001c8uKb$\b\"B8\u0001\t\u0003\u0002\u0018aD4fi\u0012+\u0007/\u001a8eK:\u001c\u0017.Z:\u0016\u0003E\u00042\u0001\u0018:u\u0013\t\u0019hMA\u0002TKF\u0004$!^=\u0011\u0007U3\b0\u0003\u0002x\t\tQA)\u001a9f]\u0012,gnY=\u0011\u0005EIH!\u0003>o\u0003\u0003\u0005\tQ!\u0001\u0016\u0005\ryFE\r\u0005\u0006y\u0002!\t%`\u0001\u0012G2,\u0017M\u001d#fa\u0016tG-\u001a8dS\u0016\u001cH#A\u0013\t\r}\u0004A\u0011IA\u0001\u0003U9W\r\u001e)sK\u001a,'O]3e\u0019>\u001c\u0017\r^5p]N$B!a\u0001\u0002\u0014A!AL]A\u0003!\u0011\t9!!\u0004\u000f\u0007]\tI!C\u0002\u0002\fa\ta\u0001\u0015:fI\u00164\u0017\u0002BA\b\u0003#\u0011aa\u0015;sS:<'bAA\u00061!)\u0001N a\u0001)\u001eQ\u0011q\u0003\u0002\u0002\u0002#\u0005A!!\u0007\u0002\u0019\r{\u0017\r\\3tG\u0016$'\u000b\u0012#\u0011\u00075\tYBB\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0003\u0002\u001eM1\u00111DA\u0010\u0003K\u00012aFA\u0011\u0013\r\t\u0019\u0003\u0007\u0002\u0007\u0003:L(+\u001a4\u0011\u0007]\t9#C\u0002\u0002*a\u0011AbU3sS\u0006d\u0017N_1cY\u0016DqARA\u000e\t\u0003\ti\u0003\u0006\u0002\u0002\u001a!Q\u0011\u0011GA\u000e#\u0003%\t!a\r\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00134+\u0011\t)$a\u0013\u0016\u0005\u0005]\"fA\u001c\u0002:-\u0012\u00111\b\t\u0005\u0003{\t9%\u0004\u0002\u0002@)!\u0011\u0011IA\"\u0003%)hn\u00195fG.,GMC\u0002\u0002Fa\t!\"\u00198o_R\fG/[8o\u0013\u0011\tI%a\u0010\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0002\u0004\u0014\u0003_\u0011\r!\u0006\u0005\u000b\u0003\u001f\nY\"!A\u0005\n\u0005E\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u0015\u0011\t\u0005U\u0013qL\u0007\u0003\u0003/RA!!\u0017\u0002\\\u0005!A.\u00198h\u0015\t\ti&\u0001\u0003kCZ\f\u0017\u0002BA1\u0003/\u0012aa\u00142kK\u000e$\b"
)
public class CoalescedRDD<T> extends RDD<T> {
   private transient RDD<T> prev;
   public final int org$apache$spark$rdd$CoalescedRDD$$maxPartitions;
   private final Option<PartitionCoalescer> partitionCoalescer;
   public final ClassTag<T> org$apache$spark$rdd$CoalescedRDD$$evidence$1;

   public static <T> Option<PartitionCoalescer> $lessinit$greater$default$3() {
      return CoalescedRDD$.MODULE$.$lessinit$greater$default$3();
   }

   public RDD<T> prev() {
      return this.prev;
   }

   public void prev_$eq(RDD<T> x$1) {
      this.prev = x$1;
   }

   public Partition[] getPartitions() {
      PartitionCoalescer pc = (PartitionCoalescer)this.partitionCoalescer.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final DefaultPartitionCoalescer apply() {
            return new DefaultPartitionCoalescer(DefaultPartitionCoalescer$.MODULE$.$lessinit$greater$default$1());
         }
      });
      return (Partition[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(pc.coalesce(this.org$apache$spark$rdd$CoalescedRDD$$maxPartitions, this.prev())).zipWithIndex(Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoalescedRDD $outer;

         public final CoalescedRDDPartition apply(Tuple2<PartitionGroup, Object> x0$1) {
            if (x0$1 != null) {
               PartitionGroup pg = (PartitionGroup)x0$1._1();
               int i = x0$1._2$mcI$sp();
               int[] ids = (int[])((TraversableOnce)pg.partitions().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final int apply(Partition x$4) {
                     return x$4.index();
                  }
               }, ArrayBuffer$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Int());
               CoalescedRDDPartition var5 = new CoalescedRDDPartition(i, this.$outer.prev(), ids, pg.prefLoc());
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Partition.class)));
   }

   public Iterator<T> compute(Partition partition, TaskContext context) {
      return ((CoalescedRDDPartition)partition).parents().iterator().<T>flatMap(new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoalescedRDD $outer;
         private final TaskContext context$1;

         public final Iterator<T> apply(Partition parentPartition) {
            return this.$outer.firstParent(this.$outer.org$apache$spark$rdd$CoalescedRDD$$evidence$1).iterator(parentPartition, this.context$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.context$1 = context$1;
            }
         }
      });
   }

   public Seq<Dependency<?>> getDependencies() {
      return (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new NarrowDependency[]{new NarrowDependency<T>(this) {
         // $FF: synthetic field
         private final CoalescedRDD $outer;

         public Seq<Object> getParents(int id) {
            return Predef$.MODULE$.wrapIntArray(((CoalescedRDDPartition)this.$outer.partitions()[id]).parentsIndices());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }}));
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.prev_$eq((RDD)null);
   }

   public Seq<String> getPreferredLocations(Partition partition) {
      return Option$.MODULE$.option2Iterable(((CoalescedRDDPartition)partition).preferredLocation()).toSeq();
   }

   public CoalescedRDD(RDD<T> prev, int maxPartitions, Option<PartitionCoalescer> partitionCoalescer, ClassTag<T> evidence$1) {
      this.prev = prev;
      this.org$apache$spark$rdd$CoalescedRDD$$maxPartitions = maxPartitions;
      this.partitionCoalescer = partitionCoalescer;
      this.org$apache$spark$rdd$CoalescedRDD$$evidence$1 = evidence$1;
      super(prev.context(), Nil$.MODULE$, evidence$1);
      Predef$.MODULE$.require(maxPartitions > 0 || maxPartitions == this.prev().partitions().length, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoalescedRDD $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Number of partitions (", ") must be positive."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$rdd$CoalescedRDD$$maxPartitions)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      if (partitionCoalescer.isDefined()) {
         Predef$.MODULE$.require(partitionCoalescer.get() instanceof Serializable, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "The partition coalescer passed in must be serializable.";
            }
         });
      }

   }
}
