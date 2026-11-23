package org.apache.spark.api.java;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.spark.JavaFutureActionWrapper;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.function.DoubleFlatMapFunction;
import org.apache.spark.api.java.function.DoubleFunction;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.FlatMapFunction2;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.partial.BoundedDouble;
import org.apache.spark.partial.PartialResult;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDD$;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.mutable.Buffer;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.Null$;
import scala.runtime.ScalaRunTime$;

public abstract class JavaRDDLike$class {
   public static List partitions(JavaRDDLike $this) {
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.refArrayOps($this.rdd().partitions()).toSeq()).asJava();
   }

   public static int getNumPartitions(JavaRDDLike $this) {
      return $this.rdd().getNumPartitions();
   }

   public static Optional partitioner(JavaRDDLike $this) {
      return JavaUtils$.MODULE$.optionToOptional($this.rdd().partitioner());
   }

   public static SparkContext context(JavaRDDLike $this) {
      return $this.rdd().context();
   }

   public static int id(JavaRDDLike $this) {
      return $this.rdd().id();
   }

   public static StorageLevel getStorageLevel(JavaRDDLike $this) {
      return $this.rdd().getStorageLevel();
   }

   public static Iterator iterator(JavaRDDLike $this, Partition split, TaskContext taskContext) {
      return (Iterator)JavaConverters$.MODULE$.asJavaIteratorConverter($this.rdd().iterator(split, taskContext)).asJava();
   }

   public static JavaRDD map(JavaRDDLike $this, Function f) {
      return new JavaRDD($this.rdd().map(JavaPairRDD$.MODULE$.toScalaFunction(f), JavaSparkContext$.MODULE$.fakeClassTag()), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaRDD mapPartitionsWithIndex(JavaRDDLike $this, Function2 f, boolean preservesPartitioning) {
      return new JavaRDD($this.rdd().mapPartitionsWithIndex(new Serializable($this, f) {
         public static final long serialVersionUID = 0L;
         private final Function2 f$1;

         public final scala.collection.Iterator<R> apply(int a, scala.collection.Iterator<T> b) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter((Iterator)this.f$1.call(Predef$.MODULE$.int2Integer(a), JavaConverters$.MODULE$.asJavaIteratorConverter(b).asJava())).asScala();
         }

         public {
            this.f$1 = f$1;
         }
      }, preservesPartitioning, JavaSparkContext$.MODULE$.fakeClassTag()), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaDoubleRDD mapToDouble(JavaRDDLike $this, DoubleFunction f) {
      return new JavaDoubleRDD($this.rdd().map(new Serializable($this, f) {
         public static final long serialVersionUID = 0L;
         private final DoubleFunction f$2;

         public final double apply(T x$1) {
            return Predef$.MODULE$.double2Double(this.f$2.call(x$1));
         }

         public {
            this.f$2 = f$2;
         }
      }, ClassTag$.MODULE$.Double()));
   }

   public static JavaPairRDD mapToPair(JavaRDDLike $this, PairFunction f) {
      return new JavaPairRDD($this.rdd().map(JavaPairRDD$.MODULE$.pairFunToScalaFun(f), cm$1($this)), JavaSparkContext$.MODULE$.fakeClassTag(), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaRDD flatMap(JavaRDDLike $this, FlatMapFunction f) {
      return JavaRDD$.MODULE$.fromRDD($this.rdd().flatMap(fn$1($this, f), JavaSparkContext$.MODULE$.fakeClassTag()), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaDoubleRDD flatMapToDouble(JavaRDDLike $this, DoubleFlatMapFunction f) {
      return new JavaDoubleRDD($this.rdd().flatMap(fn$2($this, f), ClassTag$.MODULE$.apply(Double.class)).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final double apply(Double x$2) {
            return x$2;
         }
      }, ClassTag$.MODULE$.Double()));
   }

   public static JavaPairRDD flatMapToPair(JavaRDDLike $this, PairFlatMapFunction f) {
      return JavaPairRDD$.MODULE$.fromRDD($this.rdd().flatMap(fn$3($this, f), cm$2($this)), JavaSparkContext$.MODULE$.fakeClassTag(), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaRDD mapPartitions(JavaRDDLike $this, FlatMapFunction f) {
      JavaRDD$ var10000 = JavaRDD$.MODULE$;
      RDD qual$1 = $this.rdd();
      Function1 x$7 = fn$4($this, f);
      boolean x$8 = qual$1.mapPartitions$default$2();
      ClassTag x$9 = JavaSparkContext$.MODULE$.fakeClassTag();
      return var10000.fromRDD(qual$1.mapPartitions(x$7, x$8, x$9), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaRDD mapPartitions(JavaRDDLike $this, FlatMapFunction f, boolean preservesPartitioning) {
      return JavaRDD$.MODULE$.fromRDD($this.rdd().mapPartitions(fn$5($this, f), preservesPartitioning, JavaSparkContext$.MODULE$.fakeClassTag()), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static boolean mapPartitionsWithIndex$default$2(JavaRDDLike $this) {
      return false;
   }

   public static JavaDoubleRDD mapPartitionsToDouble(JavaRDDLike $this, DoubleFlatMapFunction f) {
      RDD qual$2 = $this.rdd();
      Function1 x$10 = fn$6($this, f);
      boolean x$11 = qual$2.mapPartitions$default$2();
      return new JavaDoubleRDD(qual$2.mapPartitions(x$10, x$11, ClassTag$.MODULE$.apply(Double.class)).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final double apply(Double x$3) {
            return x$3;
         }
      }, ClassTag$.MODULE$.Double()));
   }

   public static JavaPairRDD mapPartitionsToPair(JavaRDDLike $this, PairFlatMapFunction f) {
      JavaPairRDD$ var10000 = JavaPairRDD$.MODULE$;
      RDD qual$3 = $this.rdd();
      Function1 x$12 = fn$7($this, f);
      boolean x$13 = qual$3.mapPartitions$default$2();
      return var10000.fromRDD(qual$3.mapPartitions(x$12, x$13, ClassTag$.MODULE$.apply(Tuple2.class)), JavaSparkContext$.MODULE$.fakeClassTag(), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaDoubleRDD mapPartitionsToDouble(JavaRDDLike $this, DoubleFlatMapFunction f, boolean preservesPartitioning) {
      return new JavaDoubleRDD($this.rdd().mapPartitions(fn$8($this, f), preservesPartitioning, ClassTag$.MODULE$.apply(Double.class)).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final double apply(Double x$4) {
            return x$4;
         }
      }, ClassTag$.MODULE$.Double()));
   }

   public static JavaPairRDD mapPartitionsToPair(JavaRDDLike $this, PairFlatMapFunction f, boolean preservesPartitioning) {
      return JavaPairRDD$.MODULE$.fromRDD($this.rdd().mapPartitions(fn$9($this, f), preservesPartitioning, ClassTag$.MODULE$.apply(Tuple2.class)), JavaSparkContext$.MODULE$.fakeClassTag(), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static void foreachPartition(JavaRDDLike $this, VoidFunction f) {
      $this.rdd().foreachPartition(new Serializable($this, f) {
         public static final long serialVersionUID = 0L;
         private final VoidFunction f$12;

         public final void apply(scala.collection.Iterator<T> x) {
            this.f$12.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava());
         }

         public {
            this.f$12 = f$12;
         }
      });
   }

   public static JavaRDD glom(JavaRDDLike $this) {
      return new JavaRDD($this.rdd().glom().map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final List<T> apply(Object x$5) {
            return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.genericArrayOps(x$5).toSeq()).asJava();
         }
      }, ClassTag$.MODULE$.apply(List.class)), ClassTag$.MODULE$.apply(List.class));
   }

   public static JavaPairRDD cartesian(JavaRDDLike $this, JavaRDDLike other) {
      return JavaPairRDD$.MODULE$.fromRDD($this.rdd().cartesian(other.rdd(), other.classTag()), $this.classTag(), other.classTag());
   }

   public static JavaPairRDD groupBy(JavaRDDLike $this, Function f) {
      ClassTag ctagK = JavaSparkContext$.MODULE$.fakeClassTag();
      ClassTag ctagV = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaPairRDD$.MODULE$.fromRDD(JavaPairRDD$.MODULE$.groupByResultToJava($this.rdd().groupBy(JavaPairRDD$.MODULE$.toScalaFunction(f), JavaSparkContext$.MODULE$.fakeClassTag()), ctagK), ctagK, ClassTag$.MODULE$.apply(Iterable.class));
   }

   public static JavaPairRDD groupBy(JavaRDDLike $this, Function f, int numPartitions) {
      ClassTag ctagK = JavaSparkContext$.MODULE$.fakeClassTag();
      ClassTag ctagV = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaPairRDD$.MODULE$.fromRDD(JavaPairRDD$.MODULE$.groupByResultToJava($this.rdd().groupBy(JavaPairRDD$.MODULE$.toScalaFunction(f), numPartitions, JavaSparkContext$.MODULE$.fakeClassTag()), ctagK), ctagK, ClassTag$.MODULE$.apply(Iterable.class));
   }

   public static JavaRDD pipe(JavaRDDLike $this, String command) {
      return JavaRDD$.MODULE$.fromRDD($this.rdd().pipe(command), ClassTag$.MODULE$.apply(String.class));
   }

   public static JavaRDD pipe(JavaRDDLike $this, List command) {
      JavaRDD$ var10000 = JavaRDD$.MODULE$;
      RDD qual$4 = $this.rdd();
      Buffer x$14 = (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(command).asScala();
      Map x$15 = qual$4.pipe$default$2();
      Function1 x$16 = qual$4.pipe$default$3();
      scala.Function2 x$17 = qual$4.pipe$default$4();
      boolean x$18 = qual$4.pipe$default$5();
      int x$19 = qual$4.pipe$default$6();
      String x$20 = qual$4.pipe$default$7();
      return var10000.fromRDD(qual$4.pipe(x$14, x$15, x$16, x$17, x$18, x$19, x$20), ClassTag$.MODULE$.apply(String.class));
   }

   public static JavaRDD pipe(JavaRDDLike $this, List command, java.util.Map env) {
      JavaRDD$ var10000 = JavaRDD$.MODULE$;
      RDD qual$5 = $this.rdd();
      Buffer x$21 = (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(command).asScala();
      scala.collection.mutable.Map x$22 = (scala.collection.mutable.Map)JavaConverters$.MODULE$.mapAsScalaMapConverter(env).asScala();
      Function1 x$23 = qual$5.pipe$default$3();
      scala.Function2 x$24 = qual$5.pipe$default$4();
      boolean x$25 = qual$5.pipe$default$5();
      int x$26 = qual$5.pipe$default$6();
      String x$27 = qual$5.pipe$default$7();
      return var10000.fromRDD(qual$5.pipe(x$21, x$22, x$23, x$24, x$25, x$26, x$27), ClassTag$.MODULE$.apply(String.class));
   }

   public static JavaRDD pipe(JavaRDDLike $this, List command, java.util.Map env, boolean separateWorkingDir, int bufferSize) {
      JavaRDD$ var10000 = JavaRDD$.MODULE$;
      RDD qual$6 = $this.rdd();
      Buffer x$28 = (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(command).asScala();
      scala.collection.mutable.Map x$29 = (scala.collection.mutable.Map)JavaConverters$.MODULE$.mapAsScalaMapConverter(env).asScala();
      Null$ x$30 = null;
      Null$ x$31 = null;
      String x$34 = qual$6.pipe$default$7();
      return var10000.fromRDD(qual$6.pipe(x$28, x$29, (Function1)null, (scala.Function2)null, separateWorkingDir, bufferSize, x$34), ClassTag$.MODULE$.apply(String.class));
   }

   public static JavaRDD pipe(JavaRDDLike $this, List command, java.util.Map env, boolean separateWorkingDir, int bufferSize, String encoding) {
      return JavaRDD$.MODULE$.fromRDD($this.rdd().pipe((Seq)JavaConverters$.MODULE$.asScalaBufferConverter(command).asScala(), (Map)JavaConverters$.MODULE$.mapAsScalaMapConverter(env).asScala(), (Function1)null, (scala.Function2)null, separateWorkingDir, bufferSize, encoding), ClassTag$.MODULE$.apply(String.class));
   }

   public static JavaPairRDD zip(JavaRDDLike $this, JavaRDDLike other) {
      return JavaPairRDD$.MODULE$.fromRDD($this.rdd().zip(other.rdd(), other.classTag()), $this.classTag(), other.classTag());
   }

   public static JavaRDD zipPartitions(JavaRDDLike $this, JavaRDDLike other, FlatMapFunction2 f) {
      return JavaRDD$.MODULE$.fromRDD($this.rdd().zipPartitions(other.rdd(), fn$10($this, f), other.classTag(), JavaSparkContext$.MODULE$.fakeClassTag()), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static JavaPairRDD zipWithUniqueId(JavaRDDLike $this) {
      return JavaPairRDD$.MODULE$.fromRDD($this.rdd().zipWithUniqueId(), $this.classTag(), ClassTag$.MODULE$.Long());
   }

   public static JavaPairRDD zipWithIndex(JavaRDDLike $this) {
      return JavaPairRDD$.MODULE$.fromRDD($this.rdd().zipWithIndex(), $this.classTag(), ClassTag$.MODULE$.Long());
   }

   public static void foreach(JavaRDDLike $this, VoidFunction f) {
      $this.rdd().foreach(new Serializable($this, f) {
         public static final long serialVersionUID = 0L;
         private final VoidFunction f$14;

         public final void apply(T x) {
            this.f$14.call(x);
         }

         public {
            this.f$14 = f$14;
         }
      });
   }

   public static List collect(JavaRDDLike $this) {
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.genericArrayOps($this.rdd().collect()).toSeq()).asJava();
   }

   public static Iterator toLocalIterator(JavaRDDLike $this) {
      return (Iterator)JavaConverters$.MODULE$.asJavaIteratorConverter($this.rdd().toLocalIterator()).asJava();
   }

   public static List[] collectPartitions(JavaRDDLike $this, int[] partitionIds) {
      Object[] res = $this.context().runJob($this.rdd(), new Serializable($this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final JavaRDDLike $outer;

         public final Object apply(scala.collection.Iterator<T> it) {
            return it.toArray(this.$outer.classTag());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Predef$.MODULE$.wrapIntArray(partitionIds), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass($this.classTag().runtimeClass())));
      return (List[])Predef$.MODULE$.refArrayOps(res).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final List<T> apply(Object x$6) {
            return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.genericArrayOps(x$6).toSeq()).asJava();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(List.class)));
   }

   public static Object reduce(JavaRDDLike $this, Function2 f) {
      return $this.rdd().reduce(JavaPairRDD$.MODULE$.toScalaFunction2(f));
   }

   public static Object treeReduce(JavaRDDLike $this, Function2 f, int depth) {
      return $this.rdd().treeReduce(JavaPairRDD$.MODULE$.toScalaFunction2(f), depth);
   }

   public static Object treeReduce(JavaRDDLike $this, Function2 f) {
      return $this.treeReduce(f, 2);
   }

   public static Object fold(JavaRDDLike $this, Object zeroValue, Function2 f) {
      return $this.rdd().fold(zeroValue, JavaPairRDD$.MODULE$.toScalaFunction2(f));
   }

   public static Object aggregate(JavaRDDLike $this, Object zeroValue, Function2 seqOp, Function2 combOp) {
      return $this.rdd().aggregate(zeroValue, JavaPairRDD$.MODULE$.toScalaFunction2(seqOp), JavaPairRDD$.MODULE$.toScalaFunction2(combOp), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static Object treeAggregate(JavaRDDLike $this, Object zeroValue, Function2 seqOp, Function2 combOp, int depth) {
      return $this.rdd().treeAggregate(zeroValue, JavaPairRDD$.MODULE$.toScalaFunction2(seqOp), JavaPairRDD$.MODULE$.toScalaFunction2(combOp), depth, JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public static Object treeAggregate(JavaRDDLike $this, Object zeroValue, Function2 seqOp, Function2 combOp) {
      return $this.treeAggregate(zeroValue, seqOp, combOp, 2);
   }

   public static long count(JavaRDDLike $this) {
      return $this.rdd().count();
   }

   public static PartialResult countApprox(JavaRDDLike $this, long timeout, double confidence) {
      return $this.rdd().countApprox(timeout, confidence);
   }

   public static PartialResult countApprox(JavaRDDLike $this, long timeout) {
      RDD qual$7 = $this.rdd();
      double x$36 = qual$7.countApprox$default$2();
      return qual$7.countApprox(timeout, x$36);
   }

   public static java.util.Map countByValue(JavaRDDLike $this) {
      JavaUtils$ var10000 = JavaUtils$.MODULE$;
      RDD qual$8 = $this.rdd();
      Ordering x$37 = qual$8.countByValue$default$1();
      return var10000.mapAsSerializableJavaMap(qual$8.countByValue(x$37));
   }

   public static PartialResult countByValueApprox(JavaRDDLike $this, long timeout, double confidence) {
      RDD qual$9 = $this.rdd();
      Ordering x$40 = qual$9.countByValueApprox$default$3(timeout, confidence);
      return qual$9.countByValueApprox(timeout, confidence, x$40).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final JavaUtils.SerializableMapWrapper<T, BoundedDouble> apply(Map<T, BoundedDouble> underlying) {
            return JavaUtils$.MODULE$.<T, BoundedDouble>mapAsSerializableJavaMap(underlying);
         }
      });
   }

   public static PartialResult countByValueApprox(JavaRDDLike $this, long timeout) {
      RDD qual$10 = $this.rdd();
      double x$42 = qual$10.countByValueApprox$default$2();
      Ordering x$45 = qual$10.countByValueApprox$default$3(timeout, x$42);
      return qual$10.countByValueApprox(timeout, x$42, x$45).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final JavaUtils.SerializableMapWrapper<T, BoundedDouble> apply(Map<T, BoundedDouble> underlying) {
            return JavaUtils$.MODULE$.<T, BoundedDouble>mapAsSerializableJavaMap(underlying);
         }
      });
   }

   public static List take(JavaRDDLike $this, int num) {
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.genericArrayOps($this.rdd().take(num)).toSeq()).asJava();
   }

   public static List takeSample(JavaRDDLike $this, boolean withReplacement, int num) {
      return $this.takeSample(withReplacement, num, Utils$.MODULE$.random().nextLong());
   }

   public static List takeSample(JavaRDDLike $this, boolean withReplacement, int num, long seed) {
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.genericArrayOps($this.rdd().takeSample(withReplacement, num, seed)).toSeq()).asJava();
   }

   public static Object first(JavaRDDLike $this) {
      return $this.rdd().first();
   }

   public static boolean isEmpty(JavaRDDLike $this) {
      return $this.rdd().isEmpty();
   }

   public static void saveAsTextFile(JavaRDDLike $this, String path) {
      $this.rdd().saveAsTextFile(path);
   }

   public static void saveAsTextFile(JavaRDDLike $this, String path, Class codec) {
      $this.rdd().saveAsTextFile(path, codec);
   }

   public static void saveAsObjectFile(JavaRDDLike $this, String path) {
      $this.rdd().saveAsObjectFile(path);
   }

   public static JavaPairRDD keyBy(JavaRDDLike $this, Function f) {
      ClassTag ctag = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaPairRDD$.MODULE$.fromRDD($this.rdd().keyBy(JavaPairRDD$.MODULE$.toScalaFunction(f)), ctag, $this.classTag());
   }

   public static void checkpoint(JavaRDDLike $this) {
      $this.rdd().checkpoint();
   }

   public static boolean isCheckpointed(JavaRDDLike $this) {
      return $this.rdd().isCheckpointed();
   }

   public static Optional getCheckpointFile(JavaRDDLike $this) {
      return JavaUtils$.MODULE$.optionToOptional($this.rdd().getCheckpointFile());
   }

   public static String toDebugString(JavaRDDLike $this) {
      return $this.rdd().toDebugString();
   }

   public static List top(JavaRDDLike $this, int num, Comparator comp) {
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.genericArrayOps($this.rdd().top(num, scala.package$.MODULE$.Ordering().comparatorToOrdering(comp))).toSeq()).asJava();
   }

   public static List top(JavaRDDLike $this, int num) {
      Comparator comp = org.spark_project.guava.collect.Ordering.natural();
      return $this.top(num, comp);
   }

   public static List takeOrdered(JavaRDDLike $this, int num, Comparator comp) {
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(Predef$.MODULE$.genericArrayOps($this.rdd().takeOrdered(num, scala.package$.MODULE$.Ordering().comparatorToOrdering(comp))).toSeq()).asJava();
   }

   public static Object max(JavaRDDLike $this, Comparator comp) {
      return $this.rdd().max(scala.package$.MODULE$.Ordering().comparatorToOrdering(comp));
   }

   public static Object min(JavaRDDLike $this, Comparator comp) {
      return $this.rdd().min(scala.package$.MODULE$.Ordering().comparatorToOrdering(comp));
   }

   public static List takeOrdered(JavaRDDLike $this, int num) {
      Comparator comp = org.spark_project.guava.collect.Ordering.natural();
      return $this.takeOrdered(num, comp);
   }

   public static long countApproxDistinct(JavaRDDLike $this, double relativeSD) {
      return $this.rdd().countApproxDistinct(relativeSD);
   }

   public static String name(JavaRDDLike $this) {
      return $this.rdd().name();
   }

   public static JavaFutureAction countAsync(JavaRDDLike $this) {
      return new JavaFutureActionWrapper(RDD$.MODULE$.rddToAsyncRDDActions($this.rdd(), $this.classTag()).countAsync(), new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final Long apply(long x$1) {
            return x$1;
         }
      });
   }

   public static JavaFutureAction collectAsync(JavaRDDLike $this) {
      return new JavaFutureActionWrapper(RDD$.MODULE$.rddToAsyncRDDActions($this.rdd(), $this.classTag()).collectAsync(), new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final List<T> apply(Seq<T> x) {
            return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(x).asJava();
         }
      });
   }

   public static JavaFutureAction takeAsync(JavaRDDLike $this, int num) {
      return new JavaFutureActionWrapper(RDD$.MODULE$.rddToAsyncRDDActions($this.rdd(), $this.classTag()).takeAsync(num), new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final List<T> apply(Seq<T> x) {
            return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(x).asJava();
         }
      });
   }

   public static JavaFutureAction foreachAsync(JavaRDDLike $this, VoidFunction f) {
      return new JavaFutureActionWrapper(RDD$.MODULE$.rddToAsyncRDDActions($this.rdd(), $this.classTag()).foreachAsync(new Serializable($this, f) {
         public static final long serialVersionUID = 0L;
         private final VoidFunction f$15;

         public final void apply(T x) {
            this.f$15.call(x);
         }

         public {
            this.f$15 = f$15;
         }
      }), new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final Void apply(BoxedUnit x) {
            return null;
         }
      });
   }

   public static JavaFutureAction foreachPartitionAsync(JavaRDDLike $this, VoidFunction f) {
      return new JavaFutureActionWrapper(RDD$.MODULE$.rddToAsyncRDDActions($this.rdd(), $this.classTag()).foreachPartitionAsync(new Serializable($this, f) {
         public static final long serialVersionUID = 0L;
         private final VoidFunction f$16;

         public final void apply(scala.collection.Iterator<T> x) {
            this.f$16.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava());
         }

         public {
            this.f$16 = f$16;
         }
      }), new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final Void apply(BoxedUnit x) {
            return null;
         }
      });
   }

   private static final ClassTag cm$1(JavaRDDLike $this) {
      return (ClassTag)Predef$.MODULE$.implicitly(ClassTag$.MODULE$.apply(Tuple2.class));
   }

   private static final Function1 fn$1(JavaRDDLike $this, FlatMapFunction f$3) {
      return new Serializable($this, f$3) {
         public static final long serialVersionUID = 0L;
         private final FlatMapFunction f$3;

         public final scala.collection.Iterator<U> apply(T x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$3.call(x)).asScala();
         }

         public {
            this.f$3 = f$3;
         }
      };
   }

   private static final Function1 fn$2(JavaRDDLike $this, DoubleFlatMapFunction f$4) {
      return new Serializable($this, f$4) {
         public static final long serialVersionUID = 0L;
         private final DoubleFlatMapFunction f$4;

         public final scala.collection.Iterator<Double> apply(T x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$4.call(x)).asScala();
         }

         public {
            this.f$4 = f$4;
         }
      };
   }

   private static final Function1 fn$3(JavaRDDLike $this, PairFlatMapFunction f$5) {
      return new Serializable($this, f$5) {
         public static final long serialVersionUID = 0L;
         private final PairFlatMapFunction f$5;

         public final scala.collection.Iterator<Tuple2<K2, V2>> apply(T x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$5.call(x)).asScala();
         }

         public {
            this.f$5 = f$5;
         }
      };
   }

   private static final ClassTag cm$2(JavaRDDLike $this) {
      return (ClassTag)Predef$.MODULE$.implicitly(ClassTag$.MODULE$.apply(Tuple2.class));
   }

   private static final Function1 fn$4(JavaRDDLike $this, FlatMapFunction f$6) {
      return new Serializable($this, f$6) {
         public static final long serialVersionUID = 0L;
         private final FlatMapFunction f$6;

         public final scala.collection.Iterator<U> apply(scala.collection.Iterator<T> x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$6.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava())).asScala();
         }

         public {
            this.f$6 = f$6;
         }
      };
   }

   private static final Function1 fn$5(JavaRDDLike $this, FlatMapFunction f$7) {
      return new Serializable($this, f$7) {
         public static final long serialVersionUID = 0L;
         private final FlatMapFunction f$7;

         public final scala.collection.Iterator<U> apply(scala.collection.Iterator<T> x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$7.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava())).asScala();
         }

         public {
            this.f$7 = f$7;
         }
      };
   }

   private static final Function1 fn$6(JavaRDDLike $this, DoubleFlatMapFunction f$8) {
      return new Serializable($this, f$8) {
         public static final long serialVersionUID = 0L;
         private final DoubleFlatMapFunction f$8;

         public final scala.collection.Iterator<Double> apply(scala.collection.Iterator<T> x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$8.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava())).asScala();
         }

         public {
            this.f$8 = f$8;
         }
      };
   }

   private static final Function1 fn$7(JavaRDDLike $this, PairFlatMapFunction f$9) {
      return new Serializable($this, f$9) {
         public static final long serialVersionUID = 0L;
         private final PairFlatMapFunction f$9;

         public final scala.collection.Iterator<Tuple2<K2, V2>> apply(scala.collection.Iterator<T> x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$9.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava())).asScala();
         }

         public {
            this.f$9 = f$9;
         }
      };
   }

   private static final Function1 fn$8(JavaRDDLike $this, DoubleFlatMapFunction f$10) {
      return new Serializable($this, f$10) {
         public static final long serialVersionUID = 0L;
         private final DoubleFlatMapFunction f$10;

         public final scala.collection.Iterator<Double> apply(scala.collection.Iterator<T> x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$10.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava())).asScala();
         }

         public {
            this.f$10 = f$10;
         }
      };
   }

   private static final Function1 fn$9(JavaRDDLike $this, PairFlatMapFunction f$11) {
      return new Serializable($this, f$11) {
         public static final long serialVersionUID = 0L;
         private final PairFlatMapFunction f$11;

         public final scala.collection.Iterator<Tuple2<K2, V2>> apply(scala.collection.Iterator<T> x) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$11.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava())).asScala();
         }

         public {
            this.f$11 = f$11;
         }
      };
   }

   private static final scala.Function2 fn$10(JavaRDDLike $this, FlatMapFunction2 f$13) {
      return new Serializable($this, f$13) {
         public static final long serialVersionUID = 0L;
         private final FlatMapFunction2 f$13;

         public final scala.collection.Iterator<V> apply(scala.collection.Iterator<T> x, scala.collection.Iterator<U> y) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.f$13.call(JavaConverters$.MODULE$.asJavaIteratorConverter(x).asJava(), JavaConverters$.MODULE$.asJavaIteratorConverter(y).asJava())).asScala();
         }

         public {
            this.f$13 = f$13;
         }
      };
   }

   public static void $init$(JavaRDDLike $this) {
   }
}
