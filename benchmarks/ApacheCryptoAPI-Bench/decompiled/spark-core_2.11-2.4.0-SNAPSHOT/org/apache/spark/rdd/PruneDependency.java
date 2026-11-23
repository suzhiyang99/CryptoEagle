package org.apache.spark.rdd;

import org.apache.spark.NarrowDependency;
import org.apache.spark.Partition;
import scala.Array$;
import scala.Function1;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001U3Q!\u0001\u0002\u0001\t)\u0011q\u0002\u0015:v]\u0016$U\r]3oI\u0016t7-\u001f\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006\u0013'\t\u0001A\u0002E\u0002\u000e\u001dAi\u0011\u0001B\u0005\u0003\u001f\u0011\u0011\u0001CT1se><H)\u001a9f]\u0012,gnY=\u0011\u0005E\u0011B\u0002\u0001\u0003\u0006'\u0001\u0011\r!\u0006\u0002\u0002)\u000e\u0001\u0011C\u0001\f\u001d!\t9\"$D\u0001\u0019\u0015\u0005I\u0012!B:dC2\f\u0017BA\u000e\u0019\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"aF\u000f\n\u0005yA\"aA!os\"A1\u0001\u0001B\u0001B\u0003%\u0001\u0005E\u0002\"EAi\u0011AA\u0005\u0003G\t\u00111A\u0015#E\u0011!)\u0003A!A!\u0002\u00131\u0013a\u00059beRLG/[8o\r&dG/\u001a:Gk:\u001c\u0007\u0003B\f(S1J!\u0001\u000b\r\u0003\u0013\u0019+hn\u0019;j_:\f\u0004CA\f+\u0013\tY\u0003DA\u0002J]R\u0004\"aF\u0017\n\u00059B\"a\u0002\"p_2,\u0017M\u001c\u0005\u0006a\u0001!\t!M\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007I\u001aD\u0007E\u0002\"\u0001AAQaA\u0018A\u0002\u0001BQ!J\u0018A\u0002\u0019BqA\u000e\u0001C\u0002\u0013\u0005q'\u0001\u0006qCJ$\u0018\u000e^5p]N,\u0012\u0001\u000f\t\u0004/eZ\u0014B\u0001\u001e\u0019\u0005\u0015\t%O]1z!\tiA(\u0003\u0002>\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\u0007\u007f\u0001\u0001\u000b\u0011\u0002\u001d\u0002\u0017A\f'\u000f^5uS>t7\u000f\t\u0015\u0003}\u0005\u0003\"a\u0006\"\n\u0005\rC\"!\u0003;sC:\u001c\u0018.\u001a8u\u0011\u0015)\u0005\u0001\"\u0011G\u0003)9W\r\u001e)be\u0016tGo\u001d\u000b\u0003\u000fN\u00032\u0001\u0013)*\u001d\tIeJ\u0004\u0002K\u001b6\t1J\u0003\u0002M)\u00051AH]8pizJ\u0011!G\u0005\u0003\u001fb\tq\u0001]1dW\u0006<W-\u0003\u0002R%\n!A*[:u\u0015\ty\u0005\u0004C\u0003U\t\u0002\u0007\u0011&A\u0006qCJ$\u0018\u000e^5p]&#\u0007"
)
public class PruneDependency<T> extends NarrowDependency<T> {
   public final Function1<Object, Object> org$apache$spark$rdd$PruneDependency$$partitionFilterFunc;
   private final transient Partition[] partitions;

   public Partition[] partitions() {
      return this.partitions;
   }

   public List<Object> getParents(int partitionId) {
      return List$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{((PartitionPruningRDDPartition)this.partitions()[partitionId]).parentSplit().index()}));
   }

   public PruneDependency(RDD<T> rdd, Function1<Object, Object> partitionFilterFunc) {
      super(rdd);
      this.org$apache$spark$rdd$PruneDependency$$partitionFilterFunc = partitionFilterFunc;
      this.partitions = (Partition[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(rdd.partitions()).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PruneDependency $outer;

         public final boolean apply(Partition s) {
            return this.$outer.org$apache$spark$rdd$PruneDependency$$partitionFilterFunc.apply$mcZI$sp(s.index());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })).zipWithIndex(Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Partition apply(Tuple2<Partition, Object> x0$1) {
            if (x0$1 != null) {
               Partition split = (Partition)x0$1._1();
               int idx = x0$1._2$mcI$sp();
               PartitionPruningRDDPartition var5 = new PartitionPruningRDDPartition(idx, split);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Partition.class)));
   }
}
