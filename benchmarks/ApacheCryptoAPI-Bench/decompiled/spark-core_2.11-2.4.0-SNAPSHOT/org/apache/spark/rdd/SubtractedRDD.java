package org.apache.spark.rdd;

import java.util.HashMap;
import org.apache.spark.Dependency;
import org.apache.spark.OneToOneDependency;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.ShuffleDependency$;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Product2;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.IndexedSeqLike;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.MapLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\re!B\u0001\u0003\u0001\u0011Q!!D*vER\u0014\u0018m\u0019;fIJ#EI\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,Ba\u0003\r$1N\u0011\u0001\u0001\u0004\t\u0004\u001b9\u0001R\"\u0001\u0002\n\u0005=\u0011!a\u0001*E\tB!\u0011\u0003\u0006\f#\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"A\u0002+va2,'\u0007\u0005\u0002\u001811\u0001A!B\r\u0001\u0005\u0004Y\"!A&\u0004\u0001E\u0011Ad\b\t\u0003#uI!A\b\n\u0003\u000f9{G\u000f[5oOB\u0011\u0011\u0003I\u0005\u0003CI\u00111!\u00118z!\t92\u0005B\u0003%\u0001\t\u00071DA\u0001W\u0011!1\u0003A!a\u0001\n\u00039\u0013\u0001\u0002:eIF*\u0012\u0001\u000b\u0019\u0003S-\u00022!\u0004\b+!\t92\u0006B\u0005-[\u0005\u0005\t\u0011!B\u0001g\t\u0019q\fJ\u0019\t\u00119\u0002!\u0011!Q!\n=\nQA\u001d3ec\u0001\u0002$\u0001\r\u001a\u0011\u00075q\u0011\u0007\u0005\u0002\u0018e\u0011IA&LA\u0001\u0002\u0003\u0015\taM\t\u00039Q\u0002B!E\u001b\u0017E%\u0011aG\u0005\u0002\t!J|G-^2ue!\u0012Q\u0006\u000f\t\u0003#eJ!A\u000f\n\u0003\u0013Q\u0014\u0018M\\:jK:$\b\u0002\u0003\u001f\u0001\u0005\u0003\u0007I\u0011A\u001f\u0002\u0011I$G-M0%KF$\"AP!\u0011\u0005Ey\u0014B\u0001!\u0013\u0005\u0011)f.\u001b;\t\u000f\t[\u0014\u0011!a\u0001\u0007\u0006\u0019\u0001\u0010J\u00191\u0005\u00113\u0005cA\u0007\u000f\u000bB\u0011qC\u0012\u0003\nY5\n\t\u0011!A\u0003\u0002MB\u0001\u0002\u0013\u0001\u0003\u0002\u0004%\t!S\u0001\u0005e\u0012$''F\u0001Ka\tYU\nE\u0002\u000e\u001d1\u0003\"aF'\u0005\u00139{\u0015\u0011!A\u0001\u0006\u0003)&aA0%e!A\u0001\u000b\u0001B\u0001B\u0003&\u0011+A\u0003sI\u0012\u0014\u0004\u0005\r\u0002S)B\u0019QBD*\u0011\u0005]!F!\u0003(P\u0003\u0003\u0005\tQ!\u0001V#\tab\u000b\u0005\u0003\u0012kY9\u0006CA\fY\t\u0015I\u0006A1\u0001\u001c\u0005\u00059\u0006FA(9\u0011!a\u0006A!a\u0001\n\u0003i\u0016\u0001\u0003:eIJzF%Z9\u0015\u0005yr\u0006b\u0002\"\\\u0003\u0003\u0005\ra\u0018\u0019\u0003A\n\u00042!\u0004\bb!\t9\"\rB\u0005O\u001f\u0006\u0005\t\u0011!B\u0001+\"AA\r\u0001B\u0001B\u0003%Q-\u0001\u0003qCJ$\bC\u00014h\u001b\u0005!\u0011B\u00015\u0005\u0005-\u0001\u0016M\u001d;ji&|g.\u001a:\t\u0011)\u0004!1!Q\u0001\f-\f!\"\u001a<jI\u0016t7-\u001a\u00132!\rawNF\u0007\u0002[*\u0011aNE\u0001\be\u00164G.Z2u\u0013\t\u0001XN\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011!\u0011\bAaA!\u0002\u0017\u0019\u0018AC3wS\u0012,gnY3%eA\u0019An\u001c\u0012\t\u0011U\u0004!1!Q\u0001\fY\f!\"\u001a<jI\u0016t7-\u001a\u00134!\rawn\u0016\u0005\u0006q\u0002!\t!_\u0001\u0007y%t\u0017\u000e\u001e \u0015\ri|\u0018\u0011BA\n)\u0011YH0 @\u0011\u000b5\u0001aCI,\t\u000b)<\b9A6\t\u000bI<\b9A:\t\u000bU<\b9\u0001<\t\r\u0019:\b\u0019AA\u0001a\u0011\t\u0019!a\u0002\u0011\t5q\u0011Q\u0001\t\u0004/\u0005\u001dA!\u0003\u0017\u0000\u0003\u0003\u0005\tQ!\u00014\u0011\u0019Au\u000f1\u0001\u0002\fA\"\u0011QBA\t!\u0011ia\"a\u0004\u0011\u0007]\t\t\u0002\u0002\u0006O\u0003\u0013\t\t\u0011!A\u0003\u0002UCQ\u0001Z<A\u0002\u0015Dq!a\u0006\u0001\t\u0003\nI\"A\bhKR$U\r]3oI\u0016t7-[3t+\t\tY\u0002\u0005\u0004\u0002\u001e\u00055\u00121\u0007\b\u0005\u0003?\tIC\u0004\u0003\u0002\"\u0005\u001dRBAA\u0012\u0015\r\t)CG\u0001\u0007yI|w\u000e\u001e \n\u0003MI1!a\u000b\u0013\u0003\u001d\u0001\u0018mY6bO\u0016LA!a\f\u00022\t\u00191+Z9\u000b\u0007\u0005-\"\u0003\r\u0003\u00026\u0005u\u0002#\u00024\u00028\u0005m\u0012bAA\u001d\t\tQA)\u001a9f]\u0012,gnY=\u0011\u0007]\ti\u0004B\u0006\u0002@\u0005U\u0011\u0011!A\u0001\u0006\u0003Y\"aA0%g!9\u00111\t\u0001\u0005B\u0005\u0015\u0013!D4fiB\u000b'\u000f^5uS>t7/\u0006\u0002\u0002HA)\u0011#!\u0013\u0002N%\u0019\u00111\n\n\u0003\u000b\u0005\u0013(/Y=\u0011\u0007\u0019\fy%C\u0002\u0002R\u0011\u0011\u0011\u0002U1si&$\u0018n\u001c8\t\u0013\u0005U\u0003A1A\u0005B\u0005]\u0013a\u00039beRLG/[8oKJ,\"!!\u0017\u0011\tE\tY&Z\u0005\u0004\u0003;\u0012\"\u0001B*p[\u0016D\u0001\"!\u0019\u0001A\u0003%\u0011\u0011L\u0001\ra\u0006\u0014H/\u001b;j_:,'\u000f\t\u0005\b\u0003K\u0002A\u0011IA4\u0003\u001d\u0019w.\u001c9vi\u0016$b!!\u001b\u0002p\u0005M\u0004#BA\u000f\u0003W\u0002\u0012\u0002BA7\u0003c\u0011\u0001\"\u0013;fe\u0006$xN\u001d\u0005\t\u0003c\n\u0019\u00071\u0001\u0002N\u0005\t\u0001\u000f\u0003\u0005\u0002v\u0005\r\u0004\u0019AA<\u0003\u001d\u0019wN\u001c;fqR\u00042AZA=\u0013\r\tY\b\u0002\u0002\f)\u0006\u001c8nQ8oi\u0016DH\u000fC\u0004\u0002\u0000\u0001!\t%!!\u0002#\rdW-\u0019:EKB,g\u000eZ3oG&,7\u000fF\u0001?\u0001"
)
public class SubtractedRDD<K, V, W> extends RDD<Tuple2<K, V>> {
   private transient RDD<? extends Product2<K, V>> rdd1;
   private transient RDD<? extends Product2<K, W>> rdd2;
   private final Partitioner part;
   private final ClassTag<K> evidence$1;
   private final ClassTag<V> evidence$2;
   private final ClassTag<W> evidence$3;
   private final Some<Partitioner> partitioner;

   public RDD<? extends Product2<K, V>> rdd1() {
      return this.rdd1;
   }

   public void rdd1_$eq(RDD<? extends Product2<K, V>> x$1) {
      this.rdd1 = x$1;
   }

   public RDD<? extends Product2<K, W>> rdd2() {
      return this.rdd2;
   }

   public void rdd2_$eq(RDD<? extends Product2<K, W>> x$1) {
      this.rdd2 = x$1;
   }

   public Seq<Dependency<?>> getDependencies() {
      return (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Dependency[]{this.rddDependency$1(this.rdd1(), this.evidence$1, this.evidence$2), this.rddDependency$1(this.rdd2(), this.evidence$1, this.evidence$3)}));
   }

   public Partition[] getPartitions() {
      Partition[] array = new Partition[this.part.numPartitions()];
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), array.length).foreach$mVc$sp(new Serializable(this, array) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SubtractedRDD $outer;
         private final Partition[] array$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.array$1[i] = new CoGroupPartition(i, (Option[])((TraversableOnce)((TraversableLike)((IterableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{this.$outer.rdd1(), this.$outer.rdd2()}))).zipWithIndex(Seq$.MODULE$.canBuildFrom())).map(new Serializable(this, i) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final int i$1;

               public final Option<NarrowCoGroupSplitDep> apply(Tuple2<RDD<? extends Product2<K, Object>>, Object> x0$1) {
                  if (x0$1 != null) {
                     RDD rdd = (RDD)x0$1._1();
                     int j = x0$1._2$mcI$sp();
                     Dependency var6 = (Dependency)this.$outer.org$apache$spark$rdd$SubtractedRDD$$anonfun$$$outer().dependencies().apply(j);
                     Object var7;
                     if (var6 instanceof ShuffleDependency) {
                        var7 = None$.MODULE$;
                     } else {
                        var7 = new Some(new NarrowCoGroupSplitDep(rdd, this.i$1, rdd.partitions()[this.i$1]));
                     }

                     return (Option<NarrowCoGroupSplitDep>)var7;
                  } else {
                     throw new MatchError(x0$1);
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.i$1 = i$1;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Option.class)));
         }

         // $FF: synthetic method
         public SubtractedRDD org$apache$spark$rdd$SubtractedRDD$$anonfun$$$outer() {
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

   public Some<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Iterator<Tuple2<K, V>> compute(Partition p, TaskContext context) {
      CoGroupPartition partition = (CoGroupPartition)p;
      HashMap map = new HashMap();
      this.integrate$1(0, new Serializable(this, map) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SubtractedRDD $outer;
         private final HashMap map$1;

         public final void apply(Product2<K, V> t) {
            this.$outer.org$apache$spark$rdd$SubtractedRDD$$getSeq$1(t._1(), this.map$1).$plus$eq(t._2());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.map$1 = map$1;
            }
         }
      }, context, partition);
      this.integrate$1(1, new Serializable(this, map) {
         public static final long serialVersionUID = 0L;
         private final HashMap map$1;

         public final void apply(Product2<K, V> t) {
            this.map$1.remove(t._1());
         }

         public {
            this.map$1 = map$1;
         }
      }, context, partition);
      return TraversableOnce$.MODULE$.flattenTraversableOnce(((MapLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(map).asScala()).iterator().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterator<Tuple2<K, V>> apply(Tuple2<K, ArrayBuffer<V>> t) {
            return ((IndexedSeqLike)t._2()).iterator().<Tuple2<K, V>>map(new Serializable(this, t) {
               public static final long serialVersionUID = 0L;
               private final Tuple2 t$1;

               public final Tuple2<K, V> apply(V x$1) {
                  return new Tuple2<K, V>(this.t$1._1(), x$1);
               }

               public {
                  this.t$1 = t$1;
               }
            });
         }
      }), Predef$.MODULE$.$conforms()).flatten();
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdd1_$eq((RDD)null);
      this.rdd2_$eq((RDD)null);
   }

   private final Dependency rddDependency$1(RDD rdd, ClassTag evidence$4, ClassTag evidence$5) {
      Object var5;
      label23: {
         Option var10000 = rdd.partitioner();
         Some var4 = new Some(this.part);
         if (var10000 == null) {
            if (var4 == null) {
               break label23;
            }
         } else if (var10000.equals(var4)) {
            break label23;
         }

         this.logDebug(new Serializable(this, rdd) {
            public static final long serialVersionUID = 0L;
            private final RDD rdd$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Adding shuffle dependency with ").append((Object)this.rdd$1).toString();
            }

            public {
               this.rdd$1 = rdd$1;
            }
         });
         var5 = new ShuffleDependency(rdd, this.part, ShuffleDependency$.MODULE$.$lessinit$greater$default$3(), ShuffleDependency$.MODULE$.$lessinit$greater$default$4(), ShuffleDependency$.MODULE$.$lessinit$greater$default$5(), ShuffleDependency$.MODULE$.$lessinit$greater$default$6(), evidence$4, evidence$5, ClassTag$.MODULE$.Any());
         return (Dependency)var5;
      }

      this.logDebug(new Serializable(this, rdd) {
         public static final long serialVersionUID = 0L;
         private final RDD rdd$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Adding one-to-one dependency with ").append((Object)this.rdd$1).toString();
         }

         public {
            this.rdd$1 = rdd$1;
         }
      });
      var5 = new OneToOneDependency(rdd);
      return (Dependency)var5;
   }

   public final ArrayBuffer org$apache$spark$rdd$SubtractedRDD$$getSeq$1(Object k, HashMap map$1) {
      ArrayBuffer seq = (ArrayBuffer)map$1.get(k);
      ArrayBuffer var10000;
      if (seq == null) {
         ArrayBuffer seq = new ArrayBuffer();
         map$1.put(k, seq);
         var10000 = seq;
      } else {
         var10000 = seq;
      }

      return var10000;
   }

   private final void integrate$1(int depNum, Function1 op, TaskContext context$1, CoGroupPartition partition$1) {
      Dependency var5 = (Dependency)this.dependencies().apply(depNum);
      if (var5 instanceof OneToOneDependency) {
         OneToOneDependency var6 = (OneToOneDependency)var5;
         Partition dependencyPartition = ((NarrowCoGroupSplitDep)partition$1.narrowDeps()[depNum].get()).split();
         var6.rdd().iterator(dependencyPartition, context$1).foreach(op);
         BoxedUnit var7 = BoxedUnit.UNIT;
      } else {
         if (!(var5 instanceof ShuffleDependency)) {
            throw new MatchError(var5);
         }

         ShuffleDependency var9 = (ShuffleDependency)var5;
         Iterator iter = SparkEnv$.MODULE$.get().shuffleManager().getReader(var9.shuffleHandle(), partition$1.index(), partition$1.index() + 1, context$1).read();
         iter.foreach(op);
         BoxedUnit var11 = BoxedUnit.UNIT;
      }

   }

   public SubtractedRDD(RDD<? extends Product2<K, V>> rdd1, RDD<? extends Product2<K, W>> rdd2, Partitioner part, ClassTag<K> evidence$1, ClassTag<V> evidence$2, ClassTag<W> evidence$3) {
      this.rdd1 = rdd1;
      this.rdd2 = rdd2;
      this.part = part;
      this.evidence$1 = evidence$1;
      this.evidence$2 = evidence$2;
      this.evidence$3 = evidence$3;
      super(rdd1.context(), Nil$.MODULE$, ClassTag$.MODULE$.apply(Tuple2.class));
      this.partitioner = new Some<Partitioner>(part);
   }
}
