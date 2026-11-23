package org.apache.spark.rdd;

import org.apache.spark.OneToOneDependency;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.scheduler.TaskLocation;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ma!B\u0001\u0003\u0001\u0011Q!\u0001\u0007)beRLG/[8oKJ\fu/\u0019:f+:LwN\u001c*E\t*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0016\u0005-\u00112C\u0001\u0001\r!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\u0004%\u0012#\u0005CA\t\u0013\u0019\u0001!Qa\u0005\u0001C\u0002U\u0011\u0011\u0001V\u0002\u0001#\t1B\u0004\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0004O_RD\u0017N\\4\u0011\u0005]i\u0012B\u0001\u0010\u0019\u0005\r\te.\u001f\u0005\tA\u0001\u0011\t\u0011)A\u0005C\u0005\u00111o\u0019\t\u0003E\rj\u0011\u0001B\u0005\u0003I\u0011\u0011Ab\u00159be.\u001cuN\u001c;fqRD\u0001B\n\u0001\u0003\u0002\u0004%\taJ\u0001\u0005e\u0012$7/F\u0001)!\rI\u0013\u0007\u0004\b\u0003U=r!a\u000b\u0018\u000e\u00031R!!\f\u000b\u0002\rq\u0012xn\u001c;?\u0013\u0005I\u0012B\u0001\u0019\u0019\u0003\u001d\u0001\u0018mY6bO\u0016L!AM\u001a\u0003\u0007M+\u0017O\u0003\u000211!AQ\u0007\u0001BA\u0002\u0013\u0005a'\u0001\u0005sI\u0012\u001cx\fJ3r)\t9$\b\u0005\u0002\u0018q%\u0011\u0011\b\u0007\u0002\u0005+:LG\u000fC\u0004<i\u0005\u0005\t\u0019\u0001\u0015\u0002\u0007a$\u0013\u0007\u0003\u0005>\u0001\t\u0005\t\u0015)\u0003)\u0003\u0015\u0011H\rZ:!\u0011!y\u0004AaA!\u0002\u0017\u0001\u0015AC3wS\u0012,gnY3%cA\u0019\u0011\t\u0012\t\u000e\u0003\tS!a\u0011\r\u0002\u000fI,g\r\\3di&\u0011QI\u0011\u0002\t\u00072\f7o\u001d+bO\")q\t\u0001C\u0001\u0011\u00061A(\u001b8jiz\"2!\u0013'N)\tQ5\nE\u0002\u000e\u0001AAQa\u0010$A\u0004\u0001CQ\u0001\t$A\u0002\u0005BQA\n$A\u0002!Bqa\u0014\u0001C\u0002\u0013\u0005\u0003+A\u0006qCJ$\u0018\u000e^5p]\u0016\u0014X#A)\u0011\u0007]\u0011F+\u0003\u0002T1\t1q\n\u001d;j_:\u0004\"AI+\n\u0005Y#!a\u0003)beRLG/[8oKJDa\u0001\u0017\u0001!\u0002\u0013\t\u0016\u0001\u00049beRLG/[8oKJ\u0004\u0003\"\u0002.\u0001\t\u0003Z\u0016!D4fiB\u000b'\u000f^5uS>t7/F\u0001]!\r9RlX\u0005\u0003=b\u0011Q!\u0011:sCf\u0004\"A\t1\n\u0005\u0005$!!\u0003)beRLG/[8o\u0011\u0015\u0019\u0007\u0001\"\u0011e\u0003U9W\r\u001e)sK\u001a,'O]3e\u0019>\u001c\u0017\r^5p]N$\"!Z7\u0011\u0007%\nd\r\u0005\u0002hU:\u0011q\u0003[\u0005\u0003Sb\ta\u0001\u0015:fI\u00164\u0017BA6m\u0005\u0019\u0019FO]5oO*\u0011\u0011\u000e\u0007\u0005\u0006]\n\u0004\raX\u0001\u0002g\")\u0001\u000f\u0001C!c\u000691m\\7qkR,Gc\u0001:vmB\u0019\u0011f\u001d\t\n\u0005Q\u001c$\u0001C%uKJ\fGo\u001c:\t\u000b9|\u0007\u0019A0\t\u000b]|\u0007\u0019\u0001=\u0002\u000f\r|g\u000e^3yiB\u0011!%_\u0005\u0003u\u0012\u00111\u0002V1tW\u000e{g\u000e^3yi\")A\u0010\u0001C!{\u0006\t2\r\\3be\u0012+\u0007/\u001a8eK:\u001c\u0017.Z:\u0015\u0003]Baa \u0001\u0005\n\u0005\u0005\u0011\u0001D2veJ\u0004&/\u001a4M_\u000e\u001cH#B3\u0002\u0004\u0005=\u0001BB\u0002\u007f\u0001\u0004\t)\u0001\r\u0003\u0002\b\u0005-\u0001\u0003B\u0007\u000f\u0003\u0013\u00012!EA\u0006\t-\ti!a\u0001\u0002\u0002\u0003\u0005)\u0011A\u000b\u0003\u0007}##\u0007\u0003\u0004\u0002\u0012y\u0004\raX\u0001\u0005a\u0006\u0014H\u000f"
)
public class PartitionerAwareUnionRDD<T> extends RDD<T> {
   private Seq<RDD<T>> rdds;
   private final Option<Partitioner> partitioner;

   public Seq<RDD<T>> rdds() {
      return this.rdds;
   }

   public void rdds_$eq(Seq<RDD<T>> x$1) {
      this.rdds = x$1;
   }

   public Option<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Partition[] getPartitions() {
      int numPartitions = ((Partitioner)this.partitioner().get()).numPartitions();
      return (Partition[])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numPartitions).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartitionerAwareUnionRDD $outer;

         public final PartitionerAwareUnionRDDPartition apply(int index) {
            return new PartitionerAwareUnionRDDPartition(this.$outer.rdds(), index);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Partition.class));
   }

   public Seq<String> getPreferredLocations(Partition s) {
      this.logDebug(new Serializable(this, s) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartitionerAwareUnionRDD $outer;
         private final Partition s$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Finding preferred location for ").append((Object)this.$outer).append((Object)", partition ").append((Object)BoxesRunTime.boxToInteger(this.s$1.index())).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.s$1 = s$1;
            }
         }
      });
      Partition[] parentPartitions = ((PartitionerAwareUnionRDDPartition)s).parents();
      Seq locations = (Seq)((TraversableLike)this.rdds().zip(Predef$.MODULE$.wrapRefArray(parentPartitions), Seq$.MODULE$.canBuildFrom())).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartitionerAwareUnionRDD $outer;

         public final Seq<String> apply(Tuple2<RDD<T>, Partition> x0$1) {
            if (x0$1 != null) {
               RDD rdd = (RDD)x0$1._1();
               Partition part = (Partition)x0$1._2();
               Seq parentLocations = this.$outer.org$apache$spark$rdd$PartitionerAwareUnionRDD$$currPrefLocs(rdd, part);
               this.$outer.logDebug(new Serializable(this, rdd, part, parentLocations) {
                  public static final long serialVersionUID = 0L;
                  private final RDD rdd$1;
                  private final Partition part$1;
                  private final Seq parentLocations$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Location of ").append((Object)this.rdd$1).append((Object)" partition ").append((Object)BoxesRunTime.boxToInteger(this.part$1.index())).append((Object)" = ").append((Object)this.parentLocations$1).toString();
                  }

                  public {
                     this.rdd$1 = rdd$1;
                     this.part$1 = part$1;
                     this.parentLocations$1 = parentLocations$1;
                  }
               });
               return parentLocations;
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
      }, Seq$.MODULE$.canBuildFrom());
      Option location = (Option)(locations.isEmpty() ? None$.MODULE$ : new Some(((Tuple2)locations.groupBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x) {
            return x;
         }
      }).maxBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Tuple2<String, Seq<String>> x$6) {
            return ((SeqLike)x$6._2()).length();
         }
      }, Ordering.Int$.MODULE$))._1()));
      this.logDebug(new Serializable(this, s, location) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartitionerAwareUnionRDD $outer;
         private final Partition s$1;
         private final Option location$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Selected location for ").append((Object)this.$outer).append((Object)", partition ").append((Object)BoxesRunTime.boxToInteger(this.s$1.index())).append((Object)" = ").append((Object)this.location$1).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.s$1 = s$1;
               this.location$1 = location$1;
            }
         }
      });
      return Option$.MODULE$.option2Iterable(location).toSeq();
   }

   public Iterator<T> compute(Partition s, TaskContext context) {
      Partition[] parentPartitions = ((PartitionerAwareUnionRDDPartition)s).parents();
      return ((IterableLike)this.rdds().zip(Predef$.MODULE$.wrapRefArray(parentPartitions), Seq$.MODULE$.canBuildFrom())).iterator().<T>flatMap(new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         private final TaskContext context$1;

         public final GenTraversableOnce<T> apply(Tuple2<RDD<T>, Partition> x0$2) {
            if (x0$2 != null) {
               RDD rdd = (RDD)x0$2._1();
               Partition p = (Partition)x0$2._2();
               Iterator var5 = rdd.iterator(p, this.context$1);
               return var5;
            } else {
               throw new MatchError(x0$2);
            }
         }

         public {
            this.context$1 = context$1;
         }
      });
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdds_$eq((Seq)null);
   }

   public Seq<String> org$apache$spark$rdd$PartitionerAwareUnionRDD$$currPrefLocs(RDD<?> rdd, Partition part) {
      return (Seq)rdd.context().getPreferredLocs(rdd, part.index()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(TaskLocation tl) {
            return tl.host();
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public PartitionerAwareUnionRDD(SparkContext sc, Seq<RDD<T>> rdds, ClassTag<T> evidence$1) {
      this.rdds = rdds;
      super(sc, (Seq)rdds.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final OneToOneDependency<T> apply(RDD<T> x) {
            return new OneToOneDependency<T>(x);
         }
      }, Seq$.MODULE$.canBuildFrom()), evidence$1);
      Predef$.MODULE$.require(this.rdds().nonEmpty());
      Predef$.MODULE$.require(this.rdds().forall(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(RDD<T> x$3) {
            return x$3.partitioner().isDefined();
         }
      }));
      Predef$.MODULE$.require(((TraversableOnce)this.rdds().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterable<Partitioner> apply(RDD<T> x$4) {
            return Option$.MODULE$.<Partitioner>option2Iterable(x$4.partitioner());
         }
      }, Seq$.MODULE$.canBuildFrom())).toSet().size() == 1, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartitionerAwareUnionRDD $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Parent RDDs have different partitioners: ").append(this.$outer.rdds().flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Iterable<Partitioner> apply(RDD<T> x$5) {
                  return Option$.MODULE$.<Partitioner>option2Iterable(x$5.partitioner());
               }
            }, Seq$.MODULE$.canBuildFrom())).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.partitioner = ((RDD)this.rdds().head()).partitioner();
   }
}
