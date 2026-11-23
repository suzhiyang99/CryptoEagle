package org.apache.spark.rdd;

import org.apache.spark.Dependency;
import org.apache.spark.Partition;
import org.apache.spark.RangeDependency;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.annotation.DeveloperApi;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.GenSeq;
import scala.collection.GenSeq$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.parallel.ParSeq;
import scala.math.Numeric;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015t!B\u0001\u0003\u0011\u0003Y\u0011\u0001C+oS>t'\u000b\u0012#\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0011\u00051iQ\"\u0001\u0002\u0007\u000b9\u0011\u0001\u0012A\b\u0003\u0011Us\u0017n\u001c8S\t\u0012\u001b2!\u0004\t\u0017!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fMB\u0011\u0011cF\u0005\u00031I\u0011AbU3sS\u0006d\u0017N_1cY\u0016DQAG\u0007\u0005\u0002m\ta\u0001P5oSRtD#A\u0006\t\u0013ui\u0001R1A\u0005\u0002\u0011q\u0012\u0001\u00079beRLG/[8o\u000bZ\fG\u000eV1tWN+\b\u000f]8siV\tq\u0004\u0005\u0002!K5\t\u0011E\u0003\u0002#G\u0005A\u0001/\u0019:bY2,GN\u0003\u0002%%\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005\u0019\n#a\u0005$pe.Tu.\u001b8UCN\\7+\u001e9q_J$\b\u0002\u0003\u0015\u000e\u0011\u0003\u0005\u000b\u0015B\u0010\u00023A\f'\u000f^5uS>tWI^1m)\u0006\u001c8nU;qa>\u0014H\u000f\t\u0005\bU5\t\t\u0011\"\u0003,\u0003-\u0011X-\u00193SKN|GN^3\u0015\u00031\u0002\"!\f\u001a\u000e\u00039R!a\f\u0019\u0002\t1\fgn\u001a\u0006\u0002c\u0005!!.\u0019<b\u0013\t\u0019dF\u0001\u0004PE*,7\r\u001e\u0004\u0005\u001d\t\u0001Q'\u0006\u00027yM\u0011Ag\u000e\t\u0004\u0019aR\u0014BA\u001d\u0003\u0005\r\u0011F\t\u0012\t\u0003wqb\u0001\u0001B\u0003>i\t\u0007aHA\u0001U#\ty$\t\u0005\u0002\u0012\u0001&\u0011\u0011I\u0005\u0002\b\u001d>$\b.\u001b8h!\t\t2)\u0003\u0002E%\t\u0019\u0011I\\=\t\u0011\u0019#$\u0011!Q\u0001\n\u001d\u000b!a]2\u0011\u0005!KU\"\u0001\u0003\n\u0005)#!\u0001D*qCJ\\7i\u001c8uKb$\b\u0002\u0003'5\u0005\u0003\u0007I\u0011A'\u0002\tI$Gm]\u000b\u0002\u001dB\u0019qjV\u001c\u000f\u0005A+fBA)U\u001b\u0005\u0011&BA*\u000b\u0003\u0019a$o\\8u}%\t1#\u0003\u0002W%\u00059\u0001/Y2lC\u001e,\u0017B\u0001-Z\u0005\r\u0019V-\u001d\u0006\u0003-JA\u0001b\u0017\u001b\u0003\u0002\u0004%\t\u0001X\u0001\te\u0012$7o\u0018\u0013fcR\u0011Q\f\u0019\t\u0003#yK!a\u0018\n\u0003\tUs\u0017\u000e\u001e\u0005\bCj\u000b\t\u00111\u0001O\u0003\rAH%\r\u0005\tGR\u0012\t\u0011)Q\u0005\u001d\u0006)!\u000f\u001a3tA!AQ\r\u000eB\u0002B\u0003-a-\u0001\u0006fm&$WM\\2fII\u00022a\u001a6;\u001b\u0005A'BA5\u0013\u0003\u001d\u0011XM\u001a7fGRL!a\u001b5\u0003\u0011\rc\u0017m]:UC\u001eDQA\u0007\u001b\u0005\u00025$2A\\9s)\ty\u0007\u000fE\u0002\riiBQ!\u001a7A\u0004\u0019DQA\u00127A\u0002\u001dCQ\u0001\u00147A\u00029C\u0001\u0002\u001e\u001bC\u0002\u0013\u0005A!^\u0001\u001bSN\u0004\u0016M\u001d;ji&|g\u000eT5ti&tw\rU1sC2dW\r\\\u000b\u0002mB\u0011\u0011c^\u0005\u0003qJ\u0011qAQ8pY\u0016\fg\u000e\u0003\u0004{i\u0001\u0006IA^\u0001\u001cSN\u0004\u0016M\u001d;ji&|g\u000eT5ti&tw\rU1sC2dW\r\u001c\u0011\t\u000bq$D\u0011I?\u0002\u001b\u001d,G\u000fU1si&$\u0018n\u001c8t+\u0005q\b\u0003B\t\u0000\u0003\u0007I1!!\u0001\u0013\u0005\u0015\t%O]1z!\rA\u0015QA\u0005\u0004\u0003\u000f!!!\u0003)beRLG/[8o\u0011\u001d\tY\u0001\u000eC!\u0003\u001b\tqbZ3u\t\u0016\u0004XM\u001c3f]\u000eLWm]\u000b\u0003\u0003\u001f\u0001BaT,\u0002\u0012A\"\u00111CA\u000e!\u0015A\u0015QCA\r\u0013\r\t9\u0002\u0002\u0002\u000b\t\u0016\u0004XM\u001c3f]\u000eL\bcA\u001e\u0002\u001c\u0011Y\u0011QDA\u0005\u0003\u0003\u0005\tQ!\u0001?\u0005\ryF%\r\u0005\b\u0003C!D\u0011IA\u0012\u0003\u001d\u0019w.\u001c9vi\u0016$b!!\n\u0002,\u0005=\u0002\u0003B(\u0002(iJ1!!\u000bZ\u0005!IE/\u001a:bi>\u0014\b\u0002CA\u0017\u0003?\u0001\r!a\u0001\u0002\u0003MD\u0001\"!\r\u0002 \u0001\u0007\u00111G\u0001\bG>tG/\u001a=u!\rA\u0015QG\u0005\u0004\u0003o!!a\u0003+bg.\u001cuN\u001c;fqRDq!a\u000f5\t\u0003\ni$A\u000bhKR\u0004&/\u001a4feJ,G\rT8dCRLwN\\:\u0015\t\u0005}\u0012q\n\t\u0005\u001f^\u000b\t\u0005\u0005\u0003\u0002D\u0005%cbA\t\u0002F%\u0019\u0011q\t\n\u0002\rA\u0013X\rZ3g\u0013\u0011\tY%!\u0014\u0003\rM#(/\u001b8h\u0015\r\t9E\u0005\u0005\t\u0003[\tI\u00041\u0001\u0002\u0004!9\u00111\u000b\u001b\u0005B\u0005U\u0013!E2mK\u0006\u0014H)\u001a9f]\u0012,gnY5fgR\tQ\fK\u00025\u00033\u0002B!a\u0017\u0002b5\u0011\u0011Q\f\u0006\u0004\u0003?\"\u0011AC1o]>$\u0018\r^5p]&!\u00111MA/\u00051!UM^3m_B,'/\u00119j\u0001"
)
public class UnionRDD<T> extends RDD<T> {
   private Seq<RDD<T>> rdds;
   public final ClassTag<T> org$apache$spark$rdd$UnionRDD$$evidence$2;
   private final boolean isPartitionListingParallel;

   public Seq<RDD<T>> rdds() {
      return this.rdds;
   }

   public void rdds_$eq(Seq<RDD<T>> x$1) {
      this.rdds = x$1;
   }

   public boolean isPartitionListingParallel() {
      return this.isPartitionListingParallel;
   }

   public Partition[] getPartitions() {
      Object var10000;
      if (this.isPartitionListingParallel()) {
         ParSeq parArray = (ParSeq)this.rdds().par();
         parArray.tasksupport_$eq(UnionRDD$.MODULE$.partitionEvalTaskSupport());
         var10000 = parArray;
      } else {
         var10000 = this.rdds();
      }

      GenSeq parRDDs = (GenSeq)var10000;
      Partition[] array = new Partition[BoxesRunTime.unboxToInt(((GenSeq)parRDDs.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(RDD<T> x$1) {
            return x$1.partitions().length;
         }
      }, GenSeq$.MODULE$.canBuildFrom())).seq().sum(Numeric.IntIsIntegral$.MODULE$))];
      IntRef pos = IntRef.create(0);
      ((TraversableLike)this.rdds().zipWithIndex(Seq$.MODULE$.canBuildFrom())).withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<RDD<T>, Object> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(this, array, pos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final UnionRDD $outer;
         public final Partition[] array$1;
         public final IntRef pos$1;

         public final void apply(Tuple2<RDD<T>, Object> x$2) {
            if (x$2 != null) {
               RDD rdd = (RDD)x$2._1();
               int rddIndex = x$2._2$mcI$sp();
               Predef$.MODULE$.refArrayOps(rdd.partitions()).foreach(new Serializable(this, rdd, rddIndex) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final RDD rdd$1;
                  private final int rddIndex$1;

                  public final void apply(Partition split) {
                     this.$outer.array$1[this.$outer.pos$1.elem] = new UnionPartition(this.$outer.pos$1.elem, this.rdd$1, this.rddIndex$1, split.index(), this.$outer.org$apache$spark$rdd$UnionRDD$$anonfun$$$outer().org$apache$spark$rdd$UnionRDD$$evidence$2);
                     ++this.$outer.pos$1.elem;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.rdd$1 = rdd$1;
                        this.rddIndex$1 = rddIndex$1;
                     }
                  }
               });
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x$2);
            }
         }

         // $FF: synthetic method
         public UnionRDD org$apache$spark$rdd$UnionRDD$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.array$1 = array$1;
               this.pos$1 = pos$1;
            }
         }
      });
      return array;
   }

   public Seq<Dependency<?>> getDependencies() {
      ArrayBuffer deps = new ArrayBuffer();
      IntRef pos = IntRef.create(0);
      this.rdds().foreach(new Serializable(this, deps, pos) {
         public static final long serialVersionUID = 0L;
         private final ArrayBuffer deps$1;
         private final IntRef pos$2;

         public final void apply(RDD<T> rdd) {
            this.deps$1.$plus$eq(new RangeDependency(rdd, 0, this.pos$2.elem, rdd.partitions().length));
            this.pos$2.elem += rdd.partitions().length;
         }

         public {
            this.deps$1 = deps$1;
            this.pos$2 = pos$2;
         }
      });
      return deps;
   }

   public Iterator<T> compute(Partition s, TaskContext context) {
      UnionPartition part = (UnionPartition)s;
      return this.parent(part.parentRddIndex(), this.org$apache$spark$rdd$UnionRDD$$evidence$2).iterator(part.parentPartition(), context);
   }

   public Seq<String> getPreferredLocations(Partition s) {
      return ((UnionPartition)s).preferredLocations();
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdds_$eq((Seq)null);
   }

   public UnionRDD(SparkContext sc, Seq<RDD<T>> rdds, ClassTag<T> evidence$2) {
      this.rdds = rdds;
      this.org$apache$spark$rdd$UnionRDD$$evidence$2 = evidence$2;
      super(sc, Nil$.MODULE$, evidence$2);
      this.isPartitionListingParallel = this.rdds().length() > this.conf().getInt("spark.rdd.parallelListingThreshold", 10);
   }
}
