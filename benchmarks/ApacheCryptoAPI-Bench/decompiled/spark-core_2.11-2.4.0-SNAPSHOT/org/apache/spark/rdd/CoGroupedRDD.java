package org.apache.spark.rdd;

import org.apache.spark.Dependency;
import org.apache.spark.InterruptibleIterator;
import org.apache.spark.OneToOneDependency;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.ShuffleDependency$;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.util.collection.CompactBuffer;
import org.apache.spark.util.collection.ExternalAppendOnlyMap;
import org.apache.spark.util.collection.ExternalAppendOnlyMap$;
import scala.Array$;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Product2;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005]g\u0001B\u0001\u0003\u0001-\u0011AbQ8He>,\b/\u001a3S\t\u0012S!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001)\"\u0001D\r\u0014\u0005\u0001i\u0001c\u0001\b\u0010#5\t!!\u0003\u0002\u0011\u0005\t\u0019!\u000b\u0012#\u0011\tI)rCI\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\t1A+\u001e9mKJ\u0002\"\u0001G\r\r\u0001\u0011)!\u0004\u0001b\u00017\t\t1*\u0005\u0002\u001d?A\u0011!#H\u0005\u0003=M\u0011qAT8uQ&tw\r\u0005\u0002\u0013A%\u0011\u0011e\u0005\u0002\u0004\u0003:L\bc\u0001\n$K%\u0011Ae\u0005\u0002\u0006\u0003J\u0014\u0018-\u001f\u0019\u0003MM\u00022aJ\u00183\u001d\tASF\u0004\u0002*Y5\t!F\u0003\u0002,\u0015\u00051AH]8pizJ\u0011\u0001F\u0005\u0003]M\tq\u0001]1dW\u0006<W-\u0003\u00021c\tA\u0011\n^3sC\ndWM\u0003\u0002/'A\u0011\u0001d\r\u0003\ni\u0001\t\t\u0011!A\u0003\u0002m\u00111a\u0018\u00135\u0011!1\u0004A!a\u0001\n\u00039\u0014\u0001\u0002:eIN,\u0012\u0001\u000f\t\u0004OeZ\u0014B\u0001\u001e2\u0005\r\u0019V-\u001d\u0019\u0003yy\u00022AD\b>!\tAb\bB\u0005@\u0001\u0006\u0005\t\u0011!B\u0001\u000f\n\u0019q\f\n\u001a\t\u0011\u0005\u0003!\u0011!Q!\n\t\u000bQA\u001d3eg\u0002\u00022aJ\u001dDa\t!e\tE\u0002\u000f\u001f\u0015\u0003\"\u0001\u0007$\u0005\u0013}\u0002\u0015\u0011!A\u0001\u0006\u00039\u0015C\u0001\u000fIa\tIU\n\u0005\u0003\u0013\u0015^a\u0015BA&\u0014\u0005!\u0001&o\u001c3vGR\u0014\u0004C\u0001\rN\t%qu*!A\u0001\u0002\u000b\u00051DA\u0002`IM\"\u0011b\u0010!\u0002\u0002\u0007\u0005)\u0011A$)\u0005\u0001\u000b\u0006C\u0001\nS\u0013\t\u00196CA\u0005ue\u0006t7/[3oi\"AQ\u000b\u0001BA\u0002\u0013\u0005a+\u0001\u0005sI\u0012\u001cx\fJ3r)\t9&\f\u0005\u0002\u00131&\u0011\u0011l\u0005\u0002\u0005+:LG\u000fC\u0004\\)\u0006\u0005\t\u0019\u0001/\u0002\u0007a$\u0013\u0007E\u0002(su\u0003$A\u00181\u0011\u00079yq\f\u0005\u0002\u0019A\u0012Iq\bQA\u0001\u0002\u0003\u0015\ta\u0012\u0005\tE\u0002\u0011\t\u0011)A\u0005G\u0006!\u0001/\u0019:u!\t!W-D\u0001\u0005\u0013\t1GAA\u0006QCJ$\u0018\u000e^5p]\u0016\u0014\b\u0002\u00035\u0001\u0005\u0007\u0005\u000b1B5\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002k[^i\u0011a\u001b\u0006\u0003YN\tqA]3gY\u0016\u001cG/\u0003\u0002oW\nA1\t\\1tgR\u000bw\rC\u0003q\u0001\u0011\u0005\u0011/\u0001\u0004=S:LGO\u0010\u000b\u0005eV\f\u0019\u0001\u0006\u0002tiB\u0019a\u0002A\f\t\u000b!|\u00079A5\t\u000bYz\u0007\u0019\u0001<\u0011\u0007\u001dJt\u000f\r\u0002yuB\u0019abD=\u0011\u0005aQH!C v\u0003\u0003\u0005\tQ!\u0001|#\taB\u0010\r\u0002~\u007fB!!CS\f\u007f!\tAr\u0010\u0002\u0006O\u0003\u0003\t\t\u0011!A\u0003\u0002m!\u0011bP;\u0002\u0002\u0007\u0005)\u0011A>\t\u000b\t|\u0007\u0019A2\u0006\r\u0005\u001d\u0001\u0001BA\u0005\u0005\u001d\u0019un\u0012:pkB\u0004R!a\u0003\u0002\u0016}i!!!\u0004\u000b\t\u0005=\u0011\u0011C\u0001\u000bG>dG.Z2uS>t'bAA\n\t\u0005!Q\u000f^5m\u0013\u0011\t9\"!\u0004\u0003\u001b\r{W\u000e]1di\n+hMZ3s\u000b\u0019\tY\u0002\u0001\u0003\u0002\u001e\ta1i\\$s_V\u0004h+\u00197vKB)!#F\u0010\u0002 A\u0019!#!\t\n\u0007\u0005\r2CA\u0002J]R,a!a\n\u0001\t\u0005%\"aD\"p\u000fJ|W\u000f]\"p[\nLg.\u001a:\u0011\tI\u0019\u00131\u0006\t\u0005\u0003[\t)!D\u0001\u0001\u0011%\t\t\u0004\u0001a\u0001\n\u0013\t\u0019$\u0001\u0006tKJL\u0017\r\\5{KJ,\"!!\u000e\u0011\t\u0005]\u00121H\u0007\u0003\u0003sQ1!!\r\u0005\u0013\u0011\ti$!\u000f\u0003\u0015M+'/[1mSj,'\u000fC\u0005\u0002B\u0001\u0001\r\u0011\"\u0003\u0002D\u0005q1/\u001a:jC2L'0\u001a:`I\u0015\fHcA,\u0002F!I1,a\u0010\u0002\u0002\u0003\u0007\u0011Q\u0007\u0005\t\u0003\u0013\u0002\u0001\u0015)\u0003\u00026\u0005Y1/\u001a:jC2L'0\u001a:!\u0011\u001d\ti\u0005\u0001C\u0001\u0003\u001f\nQb]3u'\u0016\u0014\u0018.\u00197ju\u0016\u0014HcA:\u0002R!A\u0011\u0011GA&\u0001\u0004\t)\u0004C\u0004\u0002V\u0001!\t%a\u0016\u0002\u001f\u001d,G\u000fR3qK:$WM\\2jKN,\"!!\u0017\u0011\t\u001dJ\u00141\f\u0019\u0005\u0003;\n)\u0007E\u0003e\u0003?\n\u0019'C\u0002\u0002b\u0011\u0011!\u0002R3qK:$WM\\2z!\rA\u0012Q\r\u0003\f\u0003O\n\u0019&!A\u0001\u0002\u000b\u00051DA\u0002`IUBq!a\u001b\u0001\t\u0003\ni'A\u0007hKR\u0004\u0016M\u001d;ji&|gn]\u000b\u0003\u0003_\u0002BAE\u0012\u0002rA\u0019A-a\u001d\n\u0007\u0005UDAA\u0005QCJ$\u0018\u000e^5p]\"I\u0011\u0011\u0010\u0001C\u0002\u0013\u0005\u00131P\u0001\fa\u0006\u0014H/\u001b;j_:,'/\u0006\u0002\u0002~A!!#a d\u0013\r\t\ti\u0005\u0002\u0005'>lW\r\u0003\u0005\u0002\u0006\u0002\u0001\u000b\u0011BA?\u00031\u0001\u0018M\u001d;ji&|g.\u001a:!\u0011\u001d\tI\t\u0001C!\u0003\u0017\u000bqaY8naV$X\r\u0006\u0004\u0002\u000e\u0006\u0005\u0016Q\u0015\t\u0006O\u0005=\u00151S\u0005\u0004\u0003#\u000b$\u0001C%uKJ\fGo\u001c:\u0011\u000bI)r#!&\u0011\tI\u0019\u0013q\u0013\u0019\u0005\u00033\u000bi\n\u0005\u0003(_\u0005m\u0005c\u0001\r\u0002\u001e\u0012Y\u0011qTAD\u0003\u0003\u0005\tQ!\u0001\u001c\u0005\ryF%\u000f\u0005\t\u0003G\u000b9\t1\u0001\u0002r\u0005\t1\u000f\u0003\u0005\u0002(\u0006\u001d\u0005\u0019AAU\u0003\u001d\u0019wN\u001c;fqR\u00042\u0001ZAV\u0013\r\ti\u000b\u0002\u0002\f)\u0006\u001c8nQ8oi\u0016DH\u000fC\u0004\u00022\u0002!I!a-\u0002#\r\u0014X-\u0019;f\u000bb$XM\u001d8bY6\u000b\u0007\u000f\u0006\u0003\u00026\u0006}\u0006#CA\u0006\u0003o;\u00121XA_\u0013\u0011\tI,!\u0004\u0003+\u0015CH/\u001a:oC2\f\u0005\u000f]3oI>sG._'baB!\u0011QFA\r!\u0011\ti#!\n\t\u0011\u0005\u0005\u0017q\u0016a\u0001\u0003?\tqA\\;n%\u0012$7\u000fC\u0004\u0002F\u0002!\t%a2\u0002#\rdW-\u0019:EKB,g\u000eZ3oG&,7\u000fF\u0001XQ\r\u0001\u00111\u001a\t\u0005\u0003\u001b\f\u0019.\u0004\u0002\u0002P*\u0019\u0011\u0011\u001b\u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002V\u0006='\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public class CoGroupedRDD<K> extends RDD<Tuple2<K, Iterable<?>[]>> {
   private transient Seq<RDD<? extends Product2<K, ?>>> rdds;
   public final Partitioner org$apache$spark$rdd$CoGroupedRDD$$part;
   public final ClassTag<K> org$apache$spark$rdd$CoGroupedRDD$$evidence$1;
   private Serializer org$apache$spark$rdd$CoGroupedRDD$$serializer;
   private final Some<Partitioner> partitioner;

   public Seq<RDD<? extends Product2<K, ?>>> rdds() {
      return this.rdds;
   }

   public void rdds_$eq(Seq<RDD<? extends Product2<K, ?>>> x$1) {
      this.rdds = x$1;
   }

   public Serializer org$apache$spark$rdd$CoGroupedRDD$$serializer() {
      return this.org$apache$spark$rdd$CoGroupedRDD$$serializer;
   }

   private void org$apache$spark$rdd$CoGroupedRDD$$serializer_$eq(Serializer x$1) {
      this.org$apache$spark$rdd$CoGroupedRDD$$serializer = x$1;
   }

   public CoGroupedRDD<K> setSerializer(Serializer serializer) {
      this.org$apache$spark$rdd$CoGroupedRDD$$serializer_$eq(serializer);
      return this;
   }

   public Seq<Dependency<?>> getDependencies() {
      return (Seq)this.rdds().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoGroupedRDD $outer;

         public final Dependency<? super Object> apply(RDD<?> rdd) {
            Object var3;
            label23: {
               Option var10000 = rdd.partitioner();
               Some var2 = new Some(this.$outer.org$apache$spark$rdd$CoGroupedRDD$$part);
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               this.$outer.logDebug(new Serializable(this, rdd) {
                  public static final long serialVersionUID = 0L;
                  private final RDD rdd$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Adding shuffle dependency with ").append((Object)this.rdd$1).toString();
                  }

                  public {
                     this.rdd$1 = rdd$1;
                  }
               });
               var3 = new ShuffleDependency(rdd, this.$outer.org$apache$spark$rdd$CoGroupedRDD$$part, this.$outer.org$apache$spark$rdd$CoGroupedRDD$$serializer(), ShuffleDependency$.MODULE$.$lessinit$greater$default$4(), ShuffleDependency$.MODULE$.$lessinit$greater$default$5(), ShuffleDependency$.MODULE$.$lessinit$greater$default$6(), this.$outer.org$apache$spark$rdd$CoGroupedRDD$$evidence$1, ClassTag$.MODULE$.Any(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(CompactBuffer.class)));
               return (Dependency<? super Object>)var3;
            }

            this.$outer.logDebug(new Serializable(this, rdd) {
               public static final long serialVersionUID = 0L;
               private final RDD rdd$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Adding one-to-one dependency with ").append((Object)this.rdd$1).toString();
               }

               public {
                  this.rdd$1 = rdd$1;
               }
            });
            var3 = new OneToOneDependency(rdd);
            return (Dependency<? super Object>)var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public Partition[] getPartitions() {
      Partition[] array = new Partition[this.org$apache$spark$rdd$CoGroupedRDD$$part.numPartitions()];
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), array.length).foreach$mVc$sp(new Serializable(this, array) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoGroupedRDD $outer;
         private final Partition[] array$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.array$1[i] = new CoGroupPartition(i, (Option[])((TraversableOnce)((TraversableLike)this.$outer.rdds().zipWithIndex(Seq$.MODULE$.canBuildFrom())).map(new Serializable(this, i) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final int i$1;

               public final Option<NarrowCoGroupSplitDep> apply(Tuple2<RDD<? extends Product2<K, ?>>, Object> x0$1) {
                  if (x0$1 != null) {
                     RDD rdd = (RDD)x0$1._1();
                     int j = x0$1._2$mcI$sp();
                     Dependency var6 = (Dependency)this.$outer.org$apache$spark$rdd$CoGroupedRDD$$anonfun$$$outer().dependencies().apply(j);
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
         public CoGroupedRDD org$apache$spark$rdd$CoGroupedRDD$$anonfun$$$outer() {
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

   public Iterator<Tuple2<K, Iterable<?>[]>> compute(Partition s, TaskContext context) {
      CoGroupPartition split = (CoGroupPartition)s;
      int numRdds = this.dependencies().length();
      ArrayBuffer rddIterators = new ArrayBuffer();
      ((TraversableLike)this.dependencies().zipWithIndex(Seq$.MODULE$.canBuildFrom())).withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<Dependency<?>, Object> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(this, context, split, rddIterators) {
         public static final long serialVersionUID = 0L;
         private final TaskContext context$1;
         private final CoGroupPartition split$1;
         private final ArrayBuffer rddIterators$1;

         public final ArrayBuffer<Tuple2<Iterator<Product2<K, Object>>, Object>> apply(Tuple2<Dependency<?>, Object> x$1) {
            if (x$1 != null) {
               Dependency dep = (Dependency)x$1._1();
               int depNum = x$1._2$mcI$sp();
               ArrayBuffer var8;
               if (dep instanceof OneToOneDependency) {
                  OneToOneDependency var7 = (OneToOneDependency)dep;
                  Partition dependencyPartition = ((NarrowCoGroupSplitDep)this.split$1.narrowDeps()[depNum].get()).split();
                  Iterator it = var7.rdd().iterator(dependencyPartition, this.context$1);
                  var8 = this.rddIterators$1.$plus$eq(new Tuple2(it, BoxesRunTime.boxToInteger(depNum)));
               } else {
                  if (!(dep instanceof ShuffleDependency)) {
                     throw new MatchError(dep);
                  }

                  ShuffleDependency var11 = (ShuffleDependency)dep;
                  Iterator it = SparkEnv$.MODULE$.get().shuffleManager().getReader(var11.shuffleHandle(), this.split$1.index(), this.split$1.index() + 1, this.context$1).read();
                  var8 = this.rddIterators$1.$plus$eq(new Tuple2(it, BoxesRunTime.boxToInteger(depNum)));
               }

               return var8;
            } else {
               throw new MatchError(x$1);
            }
         }

         public {
            this.context$1 = context$1;
            this.split$1 = split$1;
            this.rddIterators$1 = rddIterators$1;
         }
      });
      ExternalAppendOnlyMap map = this.createExternalMap(numRdds);
      rddIterators.withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<Iterator<Product2<K, Object>>, Object> check$ifrefutable$2) {
            boolean var3;
            if (check$ifrefutable$2 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(this, map) {
         public static final long serialVersionUID = 0L;
         private final ExternalAppendOnlyMap map$1;

         public final void apply(Tuple2<Iterator<Product2<K, Object>>, Object> x$2) {
            if (x$2 != null) {
               Iterator it = (Iterator)x$2._1();
               int depNum = x$2._2$mcI$sp();
               this.map$1.insertAll(it.map(new Serializable(this, depNum) {
                  public static final long serialVersionUID = 0L;
                  private final int depNum$1;

                  public final Tuple2<K, Tuple2<Object, Object>> apply(Product2<K, Object> pair) {
                     return new Tuple2<K, Tuple2<Object, Object>>(pair._1(), new Tuple2(pair._2(), BoxesRunTime.boxToInteger(this.depNum$1)));
                  }

                  public {
                     this.depNum$1 = depNum$1;
                  }
               }));
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x$2);
            }
         }

         public {
            this.map$1 = map$1;
         }
      });
      context.taskMetrics().incMemoryBytesSpilled(map.memoryBytesSpilled());
      context.taskMetrics().incDiskBytesSpilled(map.diskBytesSpilled());
      context.taskMetrics().incPeakExecutionMemory(map.peakMemoryUsedBytes());
      return new InterruptibleIterator<Tuple2<K, Iterable<?>[]>>(context, map.iterator());
   }

   private ExternalAppendOnlyMap<K, Tuple2<Object, Object>, CompactBuffer<Object>[]> createExternalMap(int numRdds) {
      Function1 createCombiner = new Serializable(this, numRdds) {
         public static final long serialVersionUID = 0L;
         private final int numRdds$1;

         public final CompactBuffer<Object>[] apply(Tuple2<Object, Object> value) {
            CompactBuffer[] newCombiner = (CompactBuffer[])Array$.MODULE$.fill(this.numRdds$1, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final CompactBuffer<Object> apply() {
                  return new CompactBuffer<Object>(ClassTag$.MODULE$.Any());
               }
            }, ClassTag$.MODULE$.apply(CompactBuffer.class));
            newCombiner[value._2$mcI$sp()].$plus$eq(value._1());
            return newCombiner;
         }

         public {
            this.numRdds$1 = numRdds$1;
         }
      };
      Function2 mergeValue = new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final CompactBuffer<Object>[] apply(CompactBuffer<Object>[] combiner, Tuple2<Object, Object> value) {
            combiner[value._2$mcI$sp()].$plus$eq(value._1());
            return combiner;
         }
      };
      Function2 mergeCombiners = new Serializable(this, numRdds) {
         public static final long serialVersionUID = 0L;
         private final int numRdds$1;

         public final CompactBuffer<Object>[] apply(CompactBuffer<Object>[] combiner1, CompactBuffer<Object>[] combiner2) {
            for(int depNum = 0; depNum < this.numRdds$1; ++depNum) {
               combiner1[depNum].$plus$plus$eq(combiner2[depNum]);
            }

            return combiner1;
         }

         public {
            this.numRdds$1 = numRdds$1;
         }
      };
      return new ExternalAppendOnlyMap<K, Tuple2<Object, Object>, CompactBuffer<Object>[]>(createCombiner, mergeValue, mergeCombiners, ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$4(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$5(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$6(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$7());
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdds_$eq((Seq)null);
   }

   public CoGroupedRDD(Seq<RDD<? extends Product2<K, ?>>> rdds, Partitioner part, ClassTag<K> evidence$1) {
      this.rdds = rdds;
      this.org$apache$spark$rdd$CoGroupedRDD$$part = part;
      this.org$apache$spark$rdd$CoGroupedRDD$$evidence$1 = evidence$1;
      super(((RDD)rdds.head()).context(), Nil$.MODULE$, ClassTag$.MODULE$.apply(Tuple2.class));
      this.org$apache$spark$rdd$CoGroupedRDD$$serializer = SparkEnv$.MODULE$.get().serializer();
      this.partitioner = new Some<Partitioner>(part);
   }
}
