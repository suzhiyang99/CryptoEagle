package org.apache.spark.rdd;

import org.apache.spark.InterruptibleIterator;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.Nil$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015c!B\u0001\u0003\u0001\u0011Q!!\u0006)be\u0006dG.\u001a7D_2dWm\u0019;j_:\u0014F\t\u0012\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006\u0013'\t\u0001A\u0002E\u0002\u000e\u001dAi\u0011AA\u0005\u0003\u001f\t\u00111A\u0015#E!\t\t\"\u0003\u0004\u0001\u0005\u000bM\u0001!\u0019A\u000b\u0003\u0003Q\u001b\u0001!\u0005\u0002\u00179A\u0011qCG\u0007\u00021)\t\u0011$A\u0003tG\u0006d\u0017-\u0003\u0002\u001c1\t9aj\u001c;iS:<\u0007CA\f\u001e\u0013\tq\u0002DA\u0002B]fD\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006I!I\u0001\u0003g\u000e\u0004\"AI\u0012\u000e\u0003\u0011I!\u0001\n\u0003\u0003\u0019M\u0003\u0018M]6D_:$X\r\u001f;\t\u0011\u0019\u0002!Q1A\u0005\n\u001d\nA\u0001Z1uCV\t\u0001\u0006E\u0002*cAq!AK\u0018\u000f\u0005-rS\"\u0001\u0017\u000b\u00055\"\u0012A\u0002\u001fs_>$h(C\u0001\u001a\u0013\t\u0001\u0004$A\u0004qC\u000e\\\u0017mZ3\n\u0005I\u001a$aA*fc*\u0011\u0001\u0007\u0007\u0005\tk\u0001\u0011\t\u0011)A\u0005Q\u0005)A-\u0019;bA!\u0012Ag\u000e\t\u0003/aJ!!\u000f\r\u0003\u0013Q\u0014\u0018M\\:jK:$\b\u0002C\u001e\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001f\u0002\u00139,Xn\u00157jG\u0016\u001c\bCA\f>\u0013\tq\u0004DA\u0002J]RD\u0001\u0002\u0011\u0001\u0003\u0002\u0003\u0006I!Q\u0001\u000eY>\u001c\u0017\r^5p]B\u0013XMZ:\u0011\t\t+EhR\u0007\u0002\u0007*\u0011A\tG\u0001\u000bG>dG.Z2uS>t\u0017B\u0001$D\u0005\ri\u0015\r\u001d\t\u0004SEB\u0005CA%M\u001d\t9\"*\u0003\u0002L1\u00051\u0001K]3eK\u001aL!!\u0014(\u0003\rM#(/\u001b8h\u0015\tY\u0005\u0004\u0003\u0005Q\u0001\t\r\t\u0015a\u0003R\u0003))g/\u001b3f]\u000e,GE\r\t\u0004%V\u0003R\"A*\u000b\u0005QC\u0012a\u0002:fM2,7\r^\u0005\u0003-N\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\u00061\u0002!\t!W\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000bikfl\u00181\u0015\u0005mc\u0006cA\u0007\u0001!!)\u0001k\u0016a\u0002#\")\u0001e\u0016a\u0001C!)ae\u0016a\u0001Q!)1h\u0016a\u0001y!)\u0001i\u0016a\u0001\u0003\")!\r\u0001C!G\u0006iq-\u001a;QCJ$\u0018\u000e^5p]N,\u0012\u0001\u001a\t\u0004/\u0015<\u0017B\u00014\u0019\u0005\u0015\t%O]1z!\t\u0011\u0003.\u0003\u0002j\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\u0006W\u0002!\t\u0005\\\u0001\bG>l\u0007/\u001e;f)\ri\u0007O\u001d\t\u0004S9\u0004\u0012BA84\u0005!IE/\u001a:bi>\u0014\b\"B9k\u0001\u00049\u0017!A:\t\u000bMT\u0007\u0019\u0001;\u0002\u000f\r|g\u000e^3yiB\u0011!%^\u0005\u0003m\u0012\u00111\u0002V1tW\u000e{g\u000e^3yi\")\u0001\u0010\u0001C!s\u0006)r-\u001a;Qe\u00164WM\u001d:fI2{7-\u0019;j_:\u001cHCA${\u0011\u0015\tx\u000f1\u0001h\u000f\u0015a(\u0001#\u0003~\u0003U\u0001\u0016M]1mY\u0016d7i\u001c7mK\u000e$\u0018n\u001c8S\t\u0012\u0003\"!\u0004@\u0007\u000b\u0005\u0011\u0001\u0012B@\u0014\u000by\f\t!a\u0002\u0011\u0007]\t\u0019!C\u0002\u0002\u0006a\u0011a!\u00118z%\u00164\u0007cA\f\u0002\n%\u0019\u00111\u0002\r\u0003\u0019M+'/[1mSj\f'\r\\3\t\rasH\u0011AA\b)\u0005i\bbBA\n}\u0012\u0005\u0011QC\u0001\u0006g2L7-Z\u000b\u0005\u0003/\t\t\u0003\u0006\u0004\u0002\u001a\u0005%\u0012Q\u0006\u000b\u0005\u00037\t\u0019\u0003\u0005\u0003*c\u0005u\u0001\u0003B\u00152\u0003?\u00012!EA\u0011\t\u0019\u0019\u0012\u0011\u0003b\u0001+!Q\u0011QEA\t\u0003\u0003\u0005\u001d!a\n\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$3\u0007\u0005\u0003S+\u0006}\u0001\u0002CA\u0016\u0003#\u0001\r!!\b\u0002\u0007M,\u0017\u000f\u0003\u0004<\u0003#\u0001\r\u0001\u0010\u0005\n\u0003cq\u0018\u0011!C\u0005\u0003g\t1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\u0007\t\u0005\u0003o\t\t%\u0004\u0002\u0002:)!\u00111HA\u001f\u0003\u0011a\u0017M\\4\u000b\u0005\u0005}\u0012\u0001\u00026bm\u0006LA!a\u0011\u0002:\t1qJ\u00196fGR\u0004"
)
public class ParallelCollectionRDD<T> extends RDD<T> {
   private final transient Seq<T> data;
   private final int numSlices;
   private final Map<Object, Seq<String>> locationPrefs;
   public final ClassTag<T> org$apache$spark$rdd$ParallelCollectionRDD$$evidence$2;

   public static <T> Seq<Seq<T>> slice(Seq<T> var0, int var1, ClassTag<T> var2) {
      return ParallelCollectionRDD$.MODULE$.slice(var0, var1, var2);
   }

   private Seq<T> data() {
      return this.data;
   }

   public Partition[] getPartitions() {
      Seq[] slices = (Seq[])ParallelCollectionRDD$.MODULE$.slice(this.data(), this.numSlices, this.org$apache$spark$rdd$ParallelCollectionRDD$$evidence$2).toArray(ClassTag$.MODULE$.apply(Seq.class));
      return (Partition[])((TraversableOnce)Predef$.MODULE$.refArrayOps(slices).indices().map(new Serializable(this, slices) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ParallelCollectionRDD $outer;
         private final Seq[] slices$1;

         public final ParallelCollectionPartition<T> apply(int i) {
            return new ParallelCollectionPartition<T>((long)this.$outer.id(), i, this.slices$1[i], this.$outer.org$apache$spark$rdd$ParallelCollectionRDD$$evidence$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.slices$1 = slices$1;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Partition.class));
   }

   public Iterator<T> compute(Partition s, TaskContext context) {
      return new InterruptibleIterator<T>(context, ((ParallelCollectionPartition)s).iterator());
   }

   public Seq<String> getPreferredLocations(Partition s) {
      return (Seq)this.locationPrefs.getOrElse(BoxesRunTime.boxToInteger(s.index()), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      });
   }

   public ParallelCollectionRDD(SparkContext sc, Seq<T> data, int numSlices, Map<Object, Seq<String>> locationPrefs, ClassTag<T> evidence$2) {
      super(sc, Nil$.MODULE$, evidence$2);
      this.data = data;
      this.numSlices = numSlices;
      this.locationPrefs = locationPrefs;
      this.org$apache$spark$rdd$ParallelCollectionRDD$$evidence$2 = evidence$2;
   }
}
