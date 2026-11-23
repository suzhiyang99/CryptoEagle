package org.apache.spark.rdd;

import org.apache.spark.Dependency;
import org.apache.spark.NarrowDependency;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.immutable.List$;
import scala.collection.immutable.Nil$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ub!B\u0001\u0003\u0001\u0011Q!\u0001D\"beR,7/[1o%\u0012#%BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOV\u00191\u0002G\u0012\u0014\u0007\u0001aQ\u0005E\u0002\u000e\u001dAi\u0011AA\u0005\u0003\u001f\t\u00111A\u0015#E!\u0011\tBC\u0006\u0012\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u0011a\u0001V;qY\u0016\u0014\u0004CA\f\u0019\u0019\u0001!Q!\u0007\u0001C\u0002m\u0011\u0011\u0001V\u0002\u0001#\tar\u0004\u0005\u0002\u0012;%\u0011aD\u0005\u0002\b\u001d>$\b.\u001b8h!\t\t\u0002%\u0003\u0002\"%\t\u0019\u0011I\\=\u0011\u0005]\u0019C!\u0002\u0013\u0001\u0005\u0004Y\"!A+\u0011\u0005E1\u0013BA\u0014\u0013\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!I\u0003A!A!\u0002\u0013Q\u0013AA:d!\tYC&D\u0001\u0005\u0013\tiCA\u0001\u0007Ta\u0006\u00148nQ8oi\u0016DH\u000f\u0003\u00050\u0001\t\u0005\r\u0011\"\u00011\u0003\u0011\u0011H\rZ\u0019\u0016\u0003E\u00022!\u0004\b\u0017\u0011!\u0019\u0004A!a\u0001\n\u0003!\u0014\u0001\u0003:eIFzF%Z9\u0015\u0005UB\u0004CA\t7\u0013\t9$C\u0001\u0003V]&$\bbB\u001d3\u0003\u0003\u0005\r!M\u0001\u0004q\u0012\n\u0004\u0002C\u001e\u0001\u0005\u0003\u0005\u000b\u0015B\u0019\u0002\u000bI$G-\r\u0011\t\u0011u\u0002!\u00111A\u0005\u0002y\nAA\u001d3eeU\tq\bE\u0002\u000e\u001d\tB\u0001\"\u0011\u0001\u0003\u0002\u0004%\tAQ\u0001\te\u0012$'g\u0018\u0013fcR\u0011Qg\u0011\u0005\bs\u0001\u000b\t\u00111\u0001@\u0011!)\u0005A!A!B\u0013y\u0014!\u0002:eIJ\u0002\u0003\u0002C$\u0001\u0005\u0007\u0005\u000b1\u0002%\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002J\u0019Zi\u0011A\u0013\u0006\u0003\u0017J\tqA]3gY\u0016\u001cG/\u0003\u0002N\u0015\nA1\t\\1tgR\u000bw\r\u0003\u0005P\u0001\t\r\t\u0015a\u0003Q\u0003))g/\u001b3f]\u000e,GE\r\t\u0004\u00132\u0013\u0003\"\u0002*\u0001\t\u0003\u0019\u0016A\u0002\u001fj]&$h\b\u0006\u0003U1fSFcA+W/B!Q\u0002\u0001\f#\u0011\u00159\u0015\u000bq\u0001I\u0011\u0015y\u0015\u000bq\u0001Q\u0011\u0015I\u0013\u000b1\u0001+\u0011\u0015y\u0013\u000b1\u00012\u0011\u0015i\u0014\u000b1\u0001@\u0011\u001da\u0006A1A\u0005\u0002u\u000b1C\\;n!\u0006\u0014H/\u001b;j_:\u001c\u0018J\u001c*eIJ*\u0012A\u0018\t\u0003#}K!\u0001\u0019\n\u0003\u0007%sG\u000f\u0003\u0004c\u0001\u0001\u0006IAX\u0001\u0015]Vl\u0007+\u0019:uSRLwN\\:J]J#GM\r\u0011\t\u000b\u0011\u0004A\u0011I3\u0002\u001b\u001d,G\u000fU1si&$\u0018n\u001c8t+\u00051\u0007cA\thS&\u0011\u0001N\u0005\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003W)L!a\u001b\u0003\u0003\u0013A\u000b'\u000f^5uS>t\u0007\"B7\u0001\t\u0003r\u0017!F4fiB\u0013XMZ3se\u0016$Gj\\2bi&|gn\u001d\u000b\u0004_\u0006\u0015\u0001c\u00019yw:\u0011\u0011O\u001e\b\u0003eVl\u0011a\u001d\u0006\u0003ij\ta\u0001\u0010:p_Rt\u0014\"A\n\n\u0005]\u0014\u0012a\u00029bG.\fw-Z\u0005\u0003sj\u00141aU3r\u0015\t9(\u0003\u0005\u0002}\u007f:\u0011\u0011#`\u0005\u0003}J\ta\u0001\u0015:fI\u00164\u0017\u0002BA\u0001\u0003\u0007\u0011aa\u0015;sS:<'B\u0001@\u0013\u0011\u0019\t9\u0001\u001ca\u0001S\u0006)1\u000f\u001d7ji\"9\u00111\u0002\u0001\u0005B\u00055\u0011aB2p[B,H/\u001a\u000b\u0007\u0003\u001f\t)\"a\u0006\u0011\tA\f\t\u0002E\u0005\u0004\u0003'Q(\u0001C%uKJ\fGo\u001c:\t\u000f\u0005\u001d\u0011\u0011\u0002a\u0001S\"A\u0011\u0011DA\u0005\u0001\u0004\tY\"A\u0004d_:$X\r\u001f;\u0011\u0007-\ni\"C\u0002\u0002 \u0011\u00111\u0002V1tW\u000e{g\u000e^3yi\"9\u00111\u0005\u0001\u0005B\u0005\u0015\u0012aD4fi\u0012+\u0007/\u001a8eK:\u001c\u0017.Z:\u0016\u0005\u0005\u001d\u0002\u0003\u00029y\u0003S\u0001D!a\u000b\u00024A)1&!\f\u00022%\u0019\u0011q\u0006\u0003\u0003\u0015\u0011+\u0007/\u001a8eK:\u001c\u0017\u0010E\u0002\u0018\u0003g!1\"!\u000e\u0002\"\u0005\u0005\t\u0011!B\u00017\t\u0019q\fJ\u001a\t\u000f\u0005e\u0002\u0001\"\u0011\u0002<\u0005\t2\r\\3be\u0012+\u0007/\u001a8eK:\u001c\u0017.Z:\u0015\u0003U\u0002"
)
public class CartesianRDD<T, U> extends RDD<Tuple2<T, U>> {
   private RDD<T> rdd1;
   private RDD<U> rdd2;
   private final int numPartitionsInRdd2;

   public RDD<T> rdd1() {
      return this.rdd1;
   }

   public void rdd1_$eq(RDD<T> x$1) {
      this.rdd1 = x$1;
   }

   public RDD<U> rdd2() {
      return this.rdd2;
   }

   public void rdd2_$eq(RDD<U> x$1) {
      this.rdd2 = x$1;
   }

   public int numPartitionsInRdd2() {
      return this.numPartitionsInRdd2;
   }

   public Partition[] getPartitions() {
      Partition[] array = new Partition[this.rdd1().partitions().length * this.rdd2().partitions().length];
      Predef$.MODULE$.refArrayOps(this.rdd1().partitions()).foreach(new Serializable(this, array) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CartesianRDD $outer;
         public final Partition[] array$1;

         public final void apply(Partition s1) {
            Predef$.MODULE$.refArrayOps(this.$outer.rdd2().partitions()).foreach(new Serializable(this, s1) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Partition s1$1;

               public final void apply(Partition s2) {
                  int idx = this.s1$1.index() * this.$outer.org$apache$spark$rdd$CartesianRDD$$anonfun$$$outer().numPartitionsInRdd2() + s2.index();
                  this.$outer.array$1[idx] = new CartesianPartition(idx, this.$outer.org$apache$spark$rdd$CartesianRDD$$anonfun$$$outer().rdd1(), this.$outer.org$apache$spark$rdd$CartesianRDD$$anonfun$$$outer().rdd2(), this.s1$1.index(), s2.index());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.s1$1 = s1$1;
                  }
               }
            });
         }

         // $FF: synthetic method
         public CartesianRDD org$apache$spark$rdd$CartesianRDD$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.array$1 = array$1;
            }
         }
      });
      return array;
   }

   public Seq<String> getPreferredLocations(Partition split) {
      CartesianPartition currSplit = (CartesianPartition)split;
      return (Seq)((SeqLike)this.rdd1().preferredLocations(currSplit.s1()).$plus$plus(this.rdd2().preferredLocations(currSplit.s2()), Seq$.MODULE$.canBuildFrom())).distinct();
   }

   public Iterator<Tuple2<T, U>> compute(Partition split, TaskContext context) {
      CartesianPartition currSplit = (CartesianPartition)split;
      return this.rdd1().iterator(currSplit.s1(), context).<Tuple2<T, U>>flatMap(new Serializable(this, context, currSplit) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CartesianRDD $outer;
         private final TaskContext context$1;
         private final CartesianPartition currSplit$1;

         public final Iterator<Tuple2<T, U>> apply(T x) {
            return this.$outer.rdd2().iterator(this.currSplit$1.s2(), this.context$1).<Tuple2<T, U>>map(new Serializable(this, x) {
               public static final long serialVersionUID = 0L;
               private final Object x$1;

               public final Tuple2<T, U> apply(U y) {
                  return new Tuple2<T, U>(this.x$1, y);
               }

               public {
                  this.x$1 = x$1;
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.context$1 = context$1;
               this.currSplit$1 = currSplit$1;
            }
         }
      });
   }

   public Seq<Dependency<?>> getDependencies() {
      return List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new NarrowDependency[]{new NarrowDependency<T>(this) {
         // $FF: synthetic field
         private final CartesianRDD $outer;

         public Seq<Object> getParents(int id) {
            return List$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{id / this.$outer.numPartitionsInRdd2()}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, new NarrowDependency<U>(this) {
         // $FF: synthetic field
         private final CartesianRDD $outer;

         public Seq<Object> getParents(int id) {
            return List$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{id % this.$outer.numPartitionsInRdd2()}));
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
      this.rdd1_$eq((RDD)null);
      this.rdd2_$eq((RDD)null);
   }

   public CartesianRDD(SparkContext sc, RDD<T> rdd1, RDD<U> rdd2, ClassTag<T> evidence$1, ClassTag<U> evidence$2) {
      this.rdd1 = rdd1;
      this.rdd2 = rdd2;
      super(sc, Nil$.MODULE$, ClassTag$.MODULE$.apply(Tuple2.class));
      this.numPartitionsInRdd2 = this.rdd2().partitions().length;
   }
}
