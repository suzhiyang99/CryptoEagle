package org.apache.spark.rdd;

import org.apache.spark.OneToOneDependency;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkContext;
import scala.Array$;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001ddAB\u0001\u0003\u0003\u0003!!BA\f[SB\u0004X\r\u001a)beRLG/[8og\n\u000b7/\u001a*E\t*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0016\u0005-\u00112C\u0001\u0001\r!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\u0004%\u0012#\u0005CA\t\u0013\u0019\u0001!Qa\u0005\u0001C\u0002U\u0011\u0011AV\u0002\u0001#\t1B\u0004\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0004O_RD\u0017N\\4\u0011\u0005]i\u0012B\u0001\u0010\u0019\u0005\r\te.\u001f\u0005\tA\u0001\u0011\t\u0011)A\u0005C\u0005\u00111o\u0019\t\u0003E\rj\u0011\u0001B\u0005\u0003I\u0011\u0011Ab\u00159be.\u001cuN\u001c;fqRD\u0001B\n\u0001\u0003\u0002\u0004%\taJ\u0001\u0005e\u0012$7/F\u0001)!\rI\u0013\u0007\u000e\b\u0003U=r!a\u000b\u0018\u000e\u00031R!!\f\u000b\u0002\rq\u0012xn\u001c;?\u0013\u0005I\u0012B\u0001\u0019\u0019\u0003\u001d\u0001\u0018mY6bO\u0016L!AM\u001a\u0003\u0007M+\u0017O\u0003\u000211A\u0012Qg\u000e\t\u0004\u001b91\u0004CA\t8\t%A\u0014(!A\u0001\u0002\u000b\u0005QCA\u0002`IIB\u0001B\u000f\u0001\u0003\u0002\u0003\u0006KaO\u0001\u0006e\u0012$7\u000f\t\t\u0004SEb\u0004GA\u001f@!\riaB\u0010\t\u0003#}\"\u0011\u0002O\u001d\u0002\u0002\u0003\u0005)\u0011A\u000b\t\u0011\u0005\u0003!\u00111A\u0005\u0002\t\u000b\u0001B\u001d3eg~#S-\u001d\u000b\u0003\u0007\u001a\u0003\"a\u0006#\n\u0005\u0015C\"\u0001B+oSRDqa\u0012!\u0002\u0002\u0003\u0007\u0001*A\u0002yIE\u00022!K\u0019Ja\tQE\nE\u0002\u000e\u001d-\u0003\"!\u0005'\u0005\u0013aJ\u0014\u0011!A\u0001\u0006\u0003)\u0002\u0002\u0003(\u0001\u0005\u0003\u0005\u000b\u0011B(\u0002+A\u0014Xm]3sm\u0016\u001c\b+\u0019:uSRLwN\\5oOB\u0011q\u0003U\u0005\u0003#b\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005T\u0001\t\r\t\u0015a\u0003U\u0003))g/\u001b3f]\u000e,G%\r\t\u0004+b\u0003R\"\u0001,\u000b\u0005]C\u0012a\u0002:fM2,7\r^\u0005\u00033Z\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\u00067\u0002!\t\u0001X\u0001\u0007y%t\u0017\u000e\u001e \u0015\tu\u0003\u0017m\u001a\u000b\u0003=~\u00032!\u0004\u0001\u0011\u0011\u0015\u0019&\fq\u0001U\u0011\u0015\u0001#\f1\u0001\"\u0011\u00151#\f1\u0001c!\rI\u0013g\u0019\u0019\u0003I\u001a\u00042!\u0004\bf!\t\tb\rB\u00059C\u0006\u0005\t\u0011!B\u0001+!9aJ\u0017I\u0001\u0002\u0004y\u0005bB5\u0001\u0005\u0004%\tE[\u0001\fa\u0006\u0014H/\u001b;j_:,'/F\u0001l!\r9BN\\\u0005\u0003[b\u0011aa\u00149uS>t\u0007C\u0001\u0012p\u0013\t\u0001HAA\u0006QCJ$\u0018\u000e^5p]\u0016\u0014\bB\u0002:\u0001A\u0003%1.\u0001\u0007qCJ$\u0018\u000e^5p]\u0016\u0014\b\u0005C\u0003u\u0001\u0011\u0005S/A\u0007hKR\u0004\u0016M\u001d;ji&|gn]\u000b\u0002mB\u0019qc^=\n\u0005aD\"!B!se\u0006L\bC\u0001\u0012{\u0013\tYHAA\u0005QCJ$\u0018\u000e^5p]\")Q\u0010\u0001C!}\u0006)r-\u001a;Qe\u00164WM\u001d:fI2{7-\u0019;j_:\u001cHcA@\u0002\u0010A!\u0011&MA\u0001!\u0011\t\u0019!!\u0003\u000f\u0007]\t)!C\u0002\u0002\ba\ta\u0001\u0015:fI\u00164\u0017\u0002BA\u0006\u0003\u001b\u0011aa\u0015;sS:<'bAA\u00041!1\u0011\u0011\u0003?A\u0002e\f\u0011a\u001d\u0005\b\u0003+\u0001A\u0011IA\f\u0003E\u0019G.Z1s\t\u0016\u0004XM\u001c3f]\u000eLWm\u001d\u000b\u0002\u0007\u001eQ\u00111\u0004\u0002\u0002\u0002#\u0005A!!\b\u0002/iK\u0007\u000f]3e!\u0006\u0014H/\u001b;j_:\u001c()Y:f%\u0012#\u0005cA\u0007\u0002 \u0019I\u0011AAA\u0001\u0012\u0003!\u0011\u0011E\n\u0007\u0003?\t\u0019#!\u000b\u0011\u0007]\t)#C\u0002\u0002(a\u0011a!\u00118z%\u00164\u0007cA\f\u0002,%\u0019\u0011Q\u0006\r\u0003\u0019M+'/[1mSj\f'\r\\3\t\u000fm\u000by\u0002\"\u0001\u00022Q\u0011\u0011Q\u0004\u0005\u000b\u0003k\ty\"%A\u0005\u0002\u0005]\u0012a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$3'\u0006\u0003\u0002:\u0005=SCAA\u001eU\ry\u0015QH\u0016\u0003\u0003\u007f\u0001B!!\u0011\u0002L5\u0011\u00111\t\u0006\u0005\u0003\u000b\n9%A\u0005v]\u000eDWmY6fI*\u0019\u0011\u0011\n\r\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002N\u0005\r#!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u001211#a\rC\u0002UA!\"a\u0015\u0002 \u0005\u0005I\u0011BA+\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005]\u0003\u0003BA-\u0003Gj!!a\u0017\u000b\t\u0005u\u0013qL\u0001\u0005Y\u0006twM\u0003\u0002\u0002b\u0005!!.\u0019<b\u0013\u0011\t)'a\u0017\u0003\r=\u0013'.Z2u\u0001"
)
public abstract class ZippedPartitionsBaseRDD<V> extends RDD<V> {
   private Seq<RDD<?>> rdds;
   private final Option<Partitioner> partitioner;

   public static <V> boolean $lessinit$greater$default$3() {
      return ZippedPartitionsBaseRDD$.MODULE$.$lessinit$greater$default$3();
   }

   public Seq<RDD<?>> rdds() {
      return this.rdds;
   }

   public void rdds_$eq(Seq<RDD<?>> x$1) {
      this.rdds = x$1;
   }

   public Option<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Partition[] getPartitions() {
      int numParts = ((RDD)this.rdds().head()).partitions().length;
      if (this.rdds().forall(new Serializable(this, numParts) {
         public static final long serialVersionUID = 0L;
         private final int numParts$1;

         public final boolean apply(RDD<?> rdd) {
            return rdd.partitions().length == this.numParts$1;
         }

         public {
            this.numParts$1 = numParts$1;
         }
      })) {
         return (Partition[])Array$.MODULE$.tabulate(numParts, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ZippedPartitionsBaseRDD $outer;

            public final ZippedPartitionsPartition apply(int i) {
               Seq prefs = (Seq)this.$outer.rdds().map(new Serializable(this, i) {
                  public static final long serialVersionUID = 0L;
                  private final int i$1;

                  public final Seq<String> apply(RDD<?> rdd) {
                     return rdd.preferredLocations(rdd.partitions()[this.i$1]);
                  }

                  public {
                     this.i$1 = i$1;
                  }
               }, Seq$.MODULE$.canBuildFrom());
               Seq exactMatchLocations = (Seq)prefs.reduce(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Seq<String> apply(Seq<String> x, Seq<String> y) {
                     return (Seq)x.intersect(y);
                  }
               });
               Seq locs = exactMatchLocations.isEmpty() ? (Seq)((SeqLike)prefs.flatten(Predef$.MODULE$.$conforms())).distinct() : exactMatchLocations;
               return new ZippedPartitionsPartition(i, this.$outer.rdds(), locs);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, ClassTag$.MODULE$.apply(Partition.class));
      } else {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't zip RDDs with unequal numbers of partitions: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rdds().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(RDD<?> x$1) {
               return x$1.partitions().length;
            }
         }, Seq$.MODULE$.canBuildFrom())})));
      }
   }

   public Seq<String> getPreferredLocations(Partition s) {
      return ((ZippedPartitionsPartition)s).preferredLocations();
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdds_$eq((Seq)null);
   }

   public ZippedPartitionsBaseRDD(SparkContext sc, Seq<RDD<?>> rdds, boolean preservesPartitioning, ClassTag<V> evidence$1) {
      this.rdds = rdds;
      super(sc, (Seq)rdds.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final OneToOneDependency<?> apply(RDD<?> x) {
            return new OneToOneDependency(x);
         }
      }, Seq$.MODULE$.canBuildFrom()), evidence$1);
      this.partitioner = (Option<Partitioner>)(preservesPartitioning ? this.firstParent(ClassTag$.MODULE$.Any()).partitioner() : None$.MODULE$);
   }
}
