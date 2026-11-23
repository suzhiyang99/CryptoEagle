package org.apache.spark.rdd;

import java.util.Random;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.TaskContext;
import org.apache.spark.util.random.RandomSampler;
import scala.Array$;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Md!B\u0001\u0003\u0001\u0011Q!a\u0006)beRLG/[8oo&\u001cXmU1na2,GM\u0015#E\u0015\t\u0019A!A\u0002sI\u0012T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u000b\u0004\u0017\r\u00122C\u0001\u0001\r!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\u0004%\u0012#\u0005CA\t\u0013\u0019\u0001!Qa\u0005\u0001C\u0002U\u0011\u0011!V\u0002\u0001#\t1B\u0004\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0004O_RD\u0017N\\4\u0011\u0005]i\u0012B\u0001\u0010\u0019\u0005\r\te.\u001f\u0005\tA\u0001\u0011\t\u0011)A\u0005C\u0005!\u0001O]3w!\riaB\t\t\u0003#\r\"Q\u0001\n\u0001C\u0002U\u0011\u0011\u0001\u0016\u0005\tM\u0001\u0011\t\u0011)A\u0005O\u000591/Y7qY\u0016\u0014\b\u0003\u0002\u0015.EAi\u0011!\u000b\u0006\u0003U-\naA]1oI>l'B\u0001\u0017\u0005\u0003\u0011)H/\u001b7\n\u00059J#!\u0004*b]\u0012|WnU1na2,'\u000f\u0003\u00051\u0001\t\u0005\t\u0015!\u00032\u0003U\u0001(/Z:feZ,7\u000fU1si&$\u0018n\u001c8j]\u001e\u0004\"a\u0006\u001a\n\u0005MB\"a\u0002\"p_2,\u0017M\u001c\u0005\tk\u0001\u0011)\u0019!C\u0005m\u0005!1/Z3e+\u00059\u0004CA\f9\u0013\tI\u0004D\u0001\u0003M_:<\u0007\u0002C\u001e\u0001\u0005\u0003\u0005\u000b\u0011B\u001c\u0002\u000bM,W\r\u001a\u0011)\u0005ij\u0004CA\f?\u0013\ty\u0004DA\u0005ue\u0006t7/[3oi\"A\u0011\t\u0001B\u0002B\u0003-!)\u0001\u0006fm&$WM\\2fIE\u00022a\u0011$#\u001b\u0005!%BA#\u0019\u0003\u001d\u0011XM\u001a7fGRL!a\u0012#\u0003\u0011\rc\u0017m]:UC\u001eD\u0001\"\u0013\u0001\u0003\u0004\u0003\u0006YAS\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004cA\"G!!)A\n\u0001C\u0001\u001b\u00061A(\u001b8jiz\"RA\u0014*T)V#2a\u0014)R!\u0011i\u0001A\t\t\t\u000b\u0005[\u00059\u0001\"\t\u000b%[\u00059\u0001&\t\u000b\u0001Z\u0005\u0019A\u0011\t\u000b\u0019Z\u0005\u0019A\u0014\t\u000bAZ\u0005\u0019A\u0019\t\u000fUZ\u0005\u0013!a\u0001o!9q\u000b\u0001b\u0001\n\u0003B\u0016a\u00039beRLG/[8oKJ,\u0012!\u0017\t\u0004/ic\u0016BA.\u0019\u0005\u0019y\u0005\u000f^5p]B\u0011QLX\u0007\u0002\t%\u0011q\f\u0002\u0002\f!\u0006\u0014H/\u001b;j_:,'\u000f\u0003\u0004b\u0001\u0001\u0006I!W\u0001\ra\u0006\u0014H/\u001b;j_:,'\u000f\t\u0015\u0003AvBQ\u0001\u001a\u0001\u0005B\u0015\fQbZ3u!\u0006\u0014H/\u001b;j_:\u001cX#\u00014\u0011\u0007]9\u0017.\u0003\u0002i1\t)\u0011I\u001d:bsB\u0011QL[\u0005\u0003W\u0012\u0011\u0011\u0002U1si&$\u0018n\u001c8\t\u000b5\u0004A\u0011\t8\u0002+\u001d,G\u000f\u0015:fM\u0016\u0014(/\u001a3M_\u000e\fG/[8ogR\u0019q.!\u0002\u0011\u0007AD8P\u0004\u0002rm:\u0011!/^\u0007\u0002g*\u0011A\u000fF\u0001\u0007yI|w\u000e\u001e \n\u0003eI!a\u001e\r\u0002\u000fA\f7m[1hK&\u0011\u0011P\u001f\u0002\u0004'\u0016\f(BA<\u0019!\taxP\u0004\u0002\u0018{&\u0011a\u0010G\u0001\u0007!J,G-\u001a4\n\t\u0005\u0005\u00111\u0001\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005yD\u0002BBA\u0004Y\u0002\u0007\u0011.A\u0003ta2LG\u000fC\u0004\u0002\f\u0001!\t%!\u0004\u0002\u000f\r|W\u000e];uKR1\u0011qBA\u000b\u00033\u0001B\u0001]A\t!%\u0019\u00111\u0003>\u0003\u0011%#XM]1u_JDq!a\u0006\u0002\n\u0001\u0007\u0011.A\u0004ta2LG/\u00138\t\u0011\u0005m\u0011\u0011\u0002a\u0001\u0003;\tqaY8oi\u0016DH\u000fE\u0002^\u0003?I1!!\t\u0005\u0005-!\u0016m]6D_:$X\r\u001f;\b\u0015\u0005\u0015\"!!A\t\u0002\u0011\t9#A\fQCJ$\u0018\u000e^5p]^L7/Z*b[BdW\r\u001a*E\tB\u0019Q\"!\u000b\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\t\u0005-2CBA\u0015\u0003[\t\u0019\u0004E\u0002\u0018\u0003_I1!!\r\u0019\u0005\u0019\te.\u001f*fMB\u0019q#!\u000e\n\u0007\u0005]\u0002D\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0004M\u0003S!\t!a\u000f\u0015\u0005\u0005\u001d\u0002BCA \u0003S\t\n\u0011\"\u0001\u0002B\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIQ*b!a\u0011\u0002Z\u0005mSCAA#U\r9\u0014qI\u0016\u0003\u0003\u0013\u0002B!a\u0013\u0002V5\u0011\u0011Q\n\u0006\u0005\u0003\u001f\n\t&A\u0005v]\u000eDWmY6fI*\u0019\u00111\u000b\r\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002X\u00055#!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u00121A%!\u0010C\u0002U!aaEA\u001f\u0005\u0004)\u0002BCA0\u0003S\t\t\u0011\"\u0003\u0002b\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t\u0019\u0007\u0005\u0003\u0002f\u0005=TBAA4\u0015\u0011\tI'a\u001b\u0002\t1\fgn\u001a\u0006\u0003\u0003[\nAA[1wC&!\u0011\u0011OA4\u0005\u0019y%M[3di\u0002"
)
public class PartitionwiseSampledRDD<T, U> extends RDD<U> {
   private final RandomSampler<T, U> sampler;
   private final transient long seed;
   private final ClassTag<T> evidence$1;
   private final transient Option<Partitioner> partitioner;

   public static <T, U> long $lessinit$greater$default$4() {
      return PartitionwiseSampledRDD$.MODULE$.$lessinit$greater$default$4();
   }

   private long seed() {
      return this.seed;
   }

   public Option<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Partition[] getPartitions() {
      Random random = new Random(this.seed());
      return (Partition[])Predef$.MODULE$.refArrayOps(this.firstParent(this.evidence$1).partitions()).map(new Serializable(this, random) {
         public static final long serialVersionUID = 0L;
         private final Random random$1;

         public final PartitionwiseSampledRDDPartition apply(Partition x) {
            return new PartitionwiseSampledRDDPartition(x, this.random$1.nextLong());
         }

         public {
            this.random$1 = random$1;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Partition.class)));
   }

   public Seq<String> getPreferredLocations(Partition split) {
      return this.firstParent(this.evidence$1).preferredLocations(((PartitionwiseSampledRDDPartition)split).prev());
   }

   public Iterator<U> compute(Partition splitIn, TaskContext context) {
      PartitionwiseSampledRDDPartition split = (PartitionwiseSampledRDDPartition)splitIn;
      RandomSampler thisSampler = this.sampler.clone();
      thisSampler.setSeed(split.seed());
      return thisSampler.sample(this.firstParent(this.evidence$1).iterator(split.prev(), context));
   }

   public PartitionwiseSampledRDD(RDD<T> prev, RandomSampler<T, U> sampler, boolean preservesPartitioning, long seed, ClassTag<T> evidence$1, ClassTag<U> evidence$2) {
      super(prev, evidence$2);
      this.sampler = sampler;
      this.seed = seed;
      this.evidence$1 = evidence$1;
      this.partitioner = (Option<Partitioner>)(preservesPartitioning ? prev.partitioner() : None$.MODULE$);
   }
}
