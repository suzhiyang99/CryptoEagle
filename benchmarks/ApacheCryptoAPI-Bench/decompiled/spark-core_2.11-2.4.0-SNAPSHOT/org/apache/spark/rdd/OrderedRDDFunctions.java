package org.apache.spark.rdd;

import org.apache.spark.Partitioner;
import org.apache.spark.RangePartitioner;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Product2;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.collection.immutable.Range;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ub\u0001B\u0001\u0003\u0001-\u00111c\u0014:eKJ,GM\u0015#E\rVt7\r^5p]NT!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001)B\u0001\u0004\u00186IM!\u0001!D\n\u001a!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011AcF\u0007\u0002+)\u0011a\u0003B\u0001\tS:$XM\u001d8bY&\u0011\u0001$\u0006\u0002\b\u0019><w-\u001b8h!\tq!$\u0003\u0002\u001c\u001f\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0004\u0001B\u0001B\u0003%a$\u0001\u0003tK24\u0007cA\u0010!E5\t!!\u0003\u0002\"\u0005\t\u0019!\u000b\u0012#\u0011\u0005\r\"C\u0002\u0001\u0003\u0006K\u0001\u0011\rA\n\u0002\u0002!F\u0011qE\u000b\t\u0003\u001d!J!!K\b\u0003\u000f9{G\u000f[5oOB!abK\u00175\u0013\tasB\u0001\u0005Qe>$Wo\u0019;3!\t\u0019c\u0006B\u00030\u0001\t\u0007\u0001GA\u0001L#\t9\u0013\u0007\u0005\u0002\u000fe%\u00111g\u0004\u0002\u0004\u0003:L\bCA\u00126\t\u00151\u0004A1\u00011\u0005\u00051\u0006\u0002\u0003\u001d\u0001\u0005\u0007\u0005\u000b1B\u001d\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002;\u00056r!a\u000f!\u000f\u0005qzT\"A\u001f\u000b\u0005yR\u0011A\u0002\u001fs_>$h(C\u0001\u0011\u0013\t\tu\"A\u0004qC\u000e\\\u0017mZ3\n\u0005\r#%\u0001C(sI\u0016\u0014\u0018N\\4\u000b\u0005\u0005{\u0001\u0002\u0003$\u0001\u0005\u0007\u0005\u000b1B$\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$#\u0007E\u0002I\u00176j\u0011!\u0013\u0006\u0003\u0015>\tqA]3gY\u0016\u001cG/\u0003\u0002M\u0013\nA1\t\\1tgR\u000bw\r\u0003\u0005O\u0001\t\r\t\u0015a\u0003P\u0003))g/\u001b3f]\u000e,Ge\r\t\u0004\u0011.#\u0004\u0002C)\u0001\u0005\u0007\u0005\u000b1\u0002*\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$C\u0007E\u0002I\u0017\nBQ\u0001\u0016\u0001\u0005\u0002U\u000ba\u0001P5oSRtDC\u0001,])\u00159\u0006,\u0017.\\!\u0015y\u0002!\f\u001b#\u0011\u0015A4\u000bq\u0001:\u0011\u001515\u000bq\u0001H\u0011\u0015q5\u000bq\u0001P\u0011\u0015\t6\u000bq\u0001S\u0011\u0015i2\u000b1\u0001\u001fQ\t\u0019f\f\u0005\u0002`E6\t\u0001M\u0003\u0002b\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\r\u0004'\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007bB3\u0001\u0005\u0004%IAZ\u0001\t_J$WM]5oOV\t\u0011\b\u0003\u0004i\u0001\u0001\u0006I!O\u0001\n_J$WM]5oO\u0002BQA\u001b\u0001\u0005\u0002-\f\u0011b]8si\nK8*Z=\u0015\u00071\u0004X\u000fE\u0002 A5\u0004BA\u00048.i%\u0011qn\u0004\u0002\u0007)V\u0004H.\u001a\u001a\t\u000fEL\u0007\u0013!a\u0001e\u0006I\u0011m]2f]\u0012Lgn\u001a\t\u0003\u001dML!\u0001^\b\u0003\u000f\t{w\u000e\\3b]\"9a/\u001bI\u0001\u0002\u00049\u0018!\u00048v[B\u000b'\u000f^5uS>t7\u000f\u0005\u0002\u000fq&\u0011\u0011p\u0004\u0002\u0004\u0013:$\b\"B>\u0001\t\u0003a\u0018A\t:fa\u0006\u0014H/\u001b;j_:\fe\u000eZ*peR<\u0016\u000e\u001e5j]B\u000b'\u000f^5uS>t7\u000f\u0006\u0002m{\")aP\u001fa\u0001\u007f\u0006Y\u0001/\u0019:uSRLwN\\3s!\u0011\t\t!a\u0001\u000e\u0003\u0011I1!!\u0002\u0005\u0005-\u0001\u0016M\u001d;ji&|g.\u001a:\t\u000f\u0005%\u0001\u0001\"\u0001\u0002\f\u0005ia-\u001b7uKJ\u0014\u0015PU1oO\u0016$RAHA\u0007\u0003#Aq!a\u0004\u0002\b\u0001\u0007Q&A\u0003m_^,'\u000fC\u0004\u0002\u0014\u0005\u001d\u0001\u0019A\u0017\u0002\u000bU\u0004\b/\u001a:\t\u0013\u0005]\u0001!%A\u0005\u0002\u0005e\u0011aE:peR\u0014\u0015pS3zI\u0011,g-Y;mi\u0012\nTCAA\u000eU\r\u0011\u0018QD\u0016\u0003\u0003?\u0001B!!\t\u0002*5\u0011\u00111\u0005\u0006\u0005\u0003K\t9#A\u0005v]\u000eDWmY6fI*\u0011\u0011mD\u0005\u0005\u0003W\t\u0019CA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D\u0011\"a\f\u0001#\u0003%\t!!\r\u0002'M|'\u000f\u001e\"z\u0017\u0016LH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005M\"fA<\u0002\u001e\u0001"
)
public class OrderedRDDFunctions<K, V, P extends Product2<K, V>> implements Logging, Serializable {
   public final RDD<P> org$apache$spark$rdd$OrderedRDDFunctions$$self;
   public final Ordering<K> org$apache$spark$rdd$OrderedRDDFunctions$$evidence$1;
   public final ClassTag<K> org$apache$spark$rdd$OrderedRDDFunctions$$evidence$2;
   public final ClassTag<V> org$apache$spark$rdd$OrderedRDDFunctions$$evidence$3;
   private final Ordering<K> org$apache$spark$rdd$OrderedRDDFunctions$$ordering;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public Ordering<K> org$apache$spark$rdd$OrderedRDDFunctions$$ordering() {
      return this.org$apache$spark$rdd$OrderedRDDFunctions$$ordering;
   }

   public RDD<Tuple2<K, V>> sortByKey(boolean ascending, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$OrderedRDDFunctions$$self.withScope(new Serializable(this, ascending, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OrderedRDDFunctions $outer;
         private final boolean ascending$1;
         private final int numPartitions$1;

         public final ShuffledRDD<K, V, V> apply() {
            RangePartitioner part = new RangePartitioner(this.numPartitions$1, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$self, this.ascending$1, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$1, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$2);
            return (new ShuffledRDD<K, V, V>(this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$self, part, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$2, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$3, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$3)).setKeyOrdering(this.ascending$1 ? this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$ordering() : this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$ordering().reverse());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.ascending$1 = ascending$1;
               this.numPartitions$1 = numPartitions$1;
            }
         }
      });
   }

   public boolean sortByKey$default$1() {
      return true;
   }

   public int sortByKey$default$2() {
      return this.org$apache$spark$rdd$OrderedRDDFunctions$$self.partitions().length;
   }

   public RDD<Tuple2<K, V>> repartitionAndSortWithinPartitions(Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$OrderedRDDFunctions$$self.withScope(new Serializable(this, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OrderedRDDFunctions $outer;
         private final Partitioner partitioner$1;

         public final ShuffledRDD<K, V, V> apply() {
            return (new ShuffledRDD<K, V, V>(this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$self, this.partitioner$1, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$2, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$3, this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$3)).setKeyOrdering(this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$ordering());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.partitioner$1 = partitioner$1;
            }
         }
      });
   }

   public RDD<P> filterByRange(K lower, K upper) {
      return (RDD)this.org$apache$spark$rdd$OrderedRDDFunctions$$self.withScope(new Serializable(this, lower, upper) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OrderedRDDFunctions $outer;
         private final Object lower$1;
         private final Object upper$1;

         public final RDD<P> apply() {
            Option var2 = this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$self.partitioner();
            Object rddToFilter;
            if (var2 instanceof Some) {
               Some var3 = (Some)var2;
               Partitioner rp = (Partitioner)var3.x();
               if (rp instanceof RangePartitioner) {
                  RangePartitioner var5 = (RangePartitioner)rp;
                  Tuple2$mcII$sp var8 = new Tuple2$mcII$sp(var5.getPartition(this.lower$1), var5.getPartition(this.upper$1));
                  if (var8 == null) {
                     throw new MatchError(var8);
                  }

                  int l = ((Tuple2)var8)._1$mcI$sp();
                  int u = ((Tuple2)var8)._2$mcI$sp();
                  Range.Inclusive partitionIndicies = RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(Math.min(l, u)), Math.max(l, u));
                  rddToFilter = PartitionPruningRDD$.MODULE$.create(this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$self, new Serializable(this, partitionIndicies) {
                     public static final long serialVersionUID = 0L;
                     private final Range.Inclusive partitionIndicies$1;

                     public final boolean apply(int x) {
                        return this.apply$mcZI$sp(x);
                     }

                     public boolean apply$mcZI$sp(int x) {
                        return this.partitionIndicies$1.contains(x);
                     }

                     public {
                        this.partitionIndicies$1 = partitionIndicies$1;
                     }
                  });
                  return ((RDD)rddToFilter).filter(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final boolean apply(P x0$1) {
                        if (x0$1 instanceof Tuple2) {
                           Tuple2 var3 = (Tuple2)x0$1;
                           Object k = var3._1();
                           boolean var5 = this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$anonfun$$inRange$1(k);
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
                  });
               }
            }

            rddToFilter = this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$self;
            return ((RDD)rddToFilter).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(P x0$1) {
                  if (x0$1 instanceof Tuple2) {
                     Tuple2 var3 = (Tuple2)x0$1;
                     Object k = var3._1();
                     boolean var5 = this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$anonfun$$inRange$1(k);
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
            });
         }

         public final boolean org$apache$spark$rdd$OrderedRDDFunctions$$anonfun$$inRange$1(Object k) {
            return this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$ordering().gteq(k, this.lower$1) && this.$outer.org$apache$spark$rdd$OrderedRDDFunctions$$ordering().lteq(k, this.upper$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.lower$1 = lower$1;
               this.upper$1 = upper$1;
            }
         }
      });
   }

   @DeveloperApi
   public OrderedRDDFunctions(RDD<P> self, Ordering<K> evidence$1, ClassTag<K> evidence$2, ClassTag<V> evidence$3, ClassTag<P> evidence$4) {
      this.org$apache$spark$rdd$OrderedRDDFunctions$$self = self;
      this.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$1 = evidence$1;
      this.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$2 = evidence$2;
      this.org$apache$spark$rdd$OrderedRDDFunctions$$evidence$3 = evidence$3;
      Logging$class.$init$(this);
      this.org$apache$spark$rdd$OrderedRDDFunctions$$ordering = (Ordering)Predef$.MODULE$.implicitly(evidence$1);
   }
}
