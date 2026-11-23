package org.apache.spark.api.java;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
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
import org.apache.spark.storage.StorageLevel;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000112a!\u0001\u0002\u0002\u0002\u0019a!aE!cgR\u0014\u0018m\u0019;KCZ\f'\u000b\u0012#MS.,'BA\u0002\u0005\u0003\u0011Q\u0017M^1\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x-F\u0002\u000e5\u0015\u001a2\u0001\u0001\b\u0015!\ty!#D\u0001\u0011\u0015\u0005\t\u0012!B:dC2\f\u0017BA\n\u0011\u0005\u0019\te.\u001f*fMB!QC\u0006\r%\u001b\u0005\u0011\u0011BA\f\u0003\u0005-Q\u0015M^1S\t\u0012c\u0015n[3\u0011\u0005eQB\u0002\u0001\u0003\u00067\u0001\u0011\r!\b\u0002\u0002)\u000e\u0001\u0011C\u0001\u0010\"!\tyq$\u0003\u0002!!\t9aj\u001c;iS:<\u0007CA\b#\u0013\t\u0019\u0003CA\u0002B]f\u0004\"!G\u0013\u0005\u000b\u0019\u0002!\u0019A\u0014\u0003\tQC\u0017n]\t\u0003=QAQ!\u000b\u0001\u0005\u0002)\na\u0001P5oSRtD#A\u0016\u0011\tU\u0001\u0001\u0004\n"
)
public abstract class AbstractJavaRDDLike<T, This extends JavaRDDLike<T, This>> implements JavaRDDLike<T, This> {
   public List<Partition> partitions() {
      return JavaRDDLike$class.partitions(this);
   }

   public int getNumPartitions() {
      return JavaRDDLike$class.getNumPartitions(this);
   }

   public Optional<Partitioner> partitioner() {
      return JavaRDDLike$class.partitioner(this);
   }

   public SparkContext context() {
      return JavaRDDLike$class.context(this);
   }

   public int id() {
      return JavaRDDLike$class.id(this);
   }

   public StorageLevel getStorageLevel() {
      return JavaRDDLike$class.getStorageLevel(this);
   }

   public Iterator<T> iterator(Partition split, TaskContext taskContext) {
      return JavaRDDLike$class.iterator(this, split, taskContext);
   }

   public <R> JavaRDD<R> map(Function<T, R> f) {
      return JavaRDDLike$class.map(this, f);
   }

   public <R> JavaRDD<R> mapPartitionsWithIndex(Function2<Integer, Iterator<T>, Iterator<R>> f, boolean preservesPartitioning) {
      return JavaRDDLike$class.mapPartitionsWithIndex(this, f, preservesPartitioning);
   }

   public <R> JavaDoubleRDD mapToDouble(DoubleFunction<T> f) {
      return JavaRDDLike$class.mapToDouble(this, f);
   }

   public <K2, V2> JavaPairRDD<K2, V2> mapToPair(PairFunction<T, K2, V2> f) {
      return JavaRDDLike$class.mapToPair(this, f);
   }

   public <U> JavaRDD<U> flatMap(FlatMapFunction<T, U> f) {
      return JavaRDDLike$class.flatMap(this, f);
   }

   public JavaDoubleRDD flatMapToDouble(DoubleFlatMapFunction<T> f) {
      return JavaRDDLike$class.flatMapToDouble(this, f);
   }

   public <K2, V2> JavaPairRDD<K2, V2> flatMapToPair(PairFlatMapFunction<T, K2, V2> f) {
      return JavaRDDLike$class.flatMapToPair(this, f);
   }

   public <U> JavaRDD<U> mapPartitions(FlatMapFunction<Iterator<T>, U> f) {
      return JavaRDDLike$class.mapPartitions(this, f);
   }

   public <U> JavaRDD<U> mapPartitions(FlatMapFunction<Iterator<T>, U> f, boolean preservesPartitioning) {
      return JavaRDDLike$class.mapPartitions(this, f, preservesPartitioning);
   }

   public JavaDoubleRDD mapPartitionsToDouble(DoubleFlatMapFunction<Iterator<T>> f) {
      return JavaRDDLike$class.mapPartitionsToDouble(this, f);
   }

   public <K2, V2> JavaPairRDD<K2, V2> mapPartitionsToPair(PairFlatMapFunction<Iterator<T>, K2, V2> f) {
      return JavaRDDLike$class.mapPartitionsToPair(this, f);
   }

   public JavaDoubleRDD mapPartitionsToDouble(DoubleFlatMapFunction<Iterator<T>> f, boolean preservesPartitioning) {
      return JavaRDDLike$class.mapPartitionsToDouble(this, f, preservesPartitioning);
   }

   public <K2, V2> JavaPairRDD<K2, V2> mapPartitionsToPair(PairFlatMapFunction<Iterator<T>, K2, V2> f, boolean preservesPartitioning) {
      return JavaRDDLike$class.mapPartitionsToPair(this, f, preservesPartitioning);
   }

   public void foreachPartition(VoidFunction<Iterator<T>> f) {
      JavaRDDLike$class.foreachPartition(this, f);
   }

   public JavaRDD<List<T>> glom() {
      return JavaRDDLike$class.glom(this);
   }

   public <U> JavaPairRDD<T, U> cartesian(JavaRDDLike<U, ?> other) {
      return JavaRDDLike$class.cartesian(this, other);
   }

   public <U> JavaPairRDD<U, Iterable<T>> groupBy(Function<T, U> f) {
      return JavaRDDLike$class.groupBy(this, f);
   }

   public <U> JavaPairRDD<U, Iterable<T>> groupBy(Function<T, U> f, int numPartitions) {
      return JavaRDDLike$class.groupBy(this, f, numPartitions);
   }

   public JavaRDD<String> pipe(String command) {
      return JavaRDDLike$class.pipe(this, (String)command);
   }

   public JavaRDD<String> pipe(List<String> command) {
      return JavaRDDLike$class.pipe(this, (List)command);
   }

   public JavaRDD<String> pipe(List<String> command, Map<String, String> env) {
      return JavaRDDLike$class.pipe(this, command, env);
   }

   public JavaRDD<String> pipe(List<String> command, Map<String, String> env, boolean separateWorkingDir, int bufferSize) {
      return JavaRDDLike$class.pipe(this, command, env, separateWorkingDir, bufferSize);
   }

   public JavaRDD<String> pipe(List<String> command, Map<String, String> env, boolean separateWorkingDir, int bufferSize, String encoding) {
      return JavaRDDLike$class.pipe(this, command, env, separateWorkingDir, bufferSize, encoding);
   }

   public <U> JavaPairRDD<T, U> zip(JavaRDDLike<U, ?> other) {
      return JavaRDDLike$class.zip(this, other);
   }

   public <U, V> JavaRDD<V> zipPartitions(JavaRDDLike<U, ?> other, FlatMapFunction2<Iterator<T>, Iterator<U>, V> f) {
      return JavaRDDLike$class.zipPartitions(this, other, f);
   }

   public JavaPairRDD<T, Long> zipWithUniqueId() {
      return JavaRDDLike$class.zipWithUniqueId(this);
   }

   public JavaPairRDD<T, Long> zipWithIndex() {
      return JavaRDDLike$class.zipWithIndex(this);
   }

   public void foreach(VoidFunction<T> f) {
      JavaRDDLike$class.foreach(this, f);
   }

   public List<T> collect() {
      return JavaRDDLike$class.collect(this);
   }

   public Iterator<T> toLocalIterator() {
      return JavaRDDLike$class.toLocalIterator(this);
   }

   public List<T>[] collectPartitions(int[] partitionIds) {
      return JavaRDDLike$class.collectPartitions(this, partitionIds);
   }

   public T reduce(Function2<T, T, T> f) {
      return (T)JavaRDDLike$class.reduce(this, f);
   }

   public T treeReduce(Function2<T, T, T> f, int depth) {
      return (T)JavaRDDLike$class.treeReduce(this, f, depth);
   }

   public T treeReduce(Function2<T, T, T> f) {
      return (T)JavaRDDLike$class.treeReduce(this, f);
   }

   public T fold(T zeroValue, Function2<T, T, T> f) {
      return (T)JavaRDDLike$class.fold(this, zeroValue, f);
   }

   public <U> U aggregate(U zeroValue, Function2<U, T, U> seqOp, Function2<U, U, U> combOp) {
      return (U)JavaRDDLike$class.aggregate(this, zeroValue, seqOp, combOp);
   }

   public <U> U treeAggregate(U zeroValue, Function2<U, T, U> seqOp, Function2<U, U, U> combOp, int depth) {
      return (U)JavaRDDLike$class.treeAggregate(this, zeroValue, seqOp, combOp, depth);
   }

   public <U> U treeAggregate(U zeroValue, Function2<U, T, U> seqOp, Function2<U, U, U> combOp) {
      return (U)JavaRDDLike$class.treeAggregate(this, zeroValue, seqOp, combOp);
   }

   public long count() {
      return JavaRDDLike$class.count(this);
   }

   public PartialResult<BoundedDouble> countApprox(long timeout, double confidence) {
      return JavaRDDLike$class.countApprox(this, timeout, confidence);
   }

   public PartialResult<BoundedDouble> countApprox(long timeout) {
      return JavaRDDLike$class.countApprox(this, timeout);
   }

   public Map<T, Long> countByValue() {
      return JavaRDDLike$class.countByValue(this);
   }

   public PartialResult<Map<T, BoundedDouble>> countByValueApprox(long timeout, double confidence) {
      return JavaRDDLike$class.countByValueApprox(this, timeout, confidence);
   }

   public PartialResult<Map<T, BoundedDouble>> countByValueApprox(long timeout) {
      return JavaRDDLike$class.countByValueApprox(this, timeout);
   }

   public List<T> take(int num) {
      return JavaRDDLike$class.take(this, num);
   }

   public List<T> takeSample(boolean withReplacement, int num) {
      return JavaRDDLike$class.takeSample(this, withReplacement, num);
   }

   public List<T> takeSample(boolean withReplacement, int num, long seed) {
      return JavaRDDLike$class.takeSample(this, withReplacement, num, seed);
   }

   public T first() {
      return (T)JavaRDDLike$class.first(this);
   }

   public boolean isEmpty() {
      return JavaRDDLike$class.isEmpty(this);
   }

   public void saveAsTextFile(String path) {
      JavaRDDLike$class.saveAsTextFile(this, path);
   }

   public void saveAsTextFile(String path, Class<? extends CompressionCodec> codec) {
      JavaRDDLike$class.saveAsTextFile(this, path, codec);
   }

   public void saveAsObjectFile(String path) {
      JavaRDDLike$class.saveAsObjectFile(this, path);
   }

   public <U> JavaPairRDD<U, T> keyBy(Function<T, U> f) {
      return JavaRDDLike$class.keyBy(this, f);
   }

   public void checkpoint() {
      JavaRDDLike$class.checkpoint(this);
   }

   public boolean isCheckpointed() {
      return JavaRDDLike$class.isCheckpointed(this);
   }

   public Optional<String> getCheckpointFile() {
      return JavaRDDLike$class.getCheckpointFile(this);
   }

   public String toDebugString() {
      return JavaRDDLike$class.toDebugString(this);
   }

   public List<T> top(int num, Comparator<T> comp) {
      return JavaRDDLike$class.top(this, num, comp);
   }

   public List<T> top(int num) {
      return JavaRDDLike$class.top(this, num);
   }

   public List<T> takeOrdered(int num, Comparator<T> comp) {
      return JavaRDDLike$class.takeOrdered(this, num, comp);
   }

   public T max(Comparator<T> comp) {
      return (T)JavaRDDLike$class.max(this, comp);
   }

   public T min(Comparator<T> comp) {
      return (T)JavaRDDLike$class.min(this, comp);
   }

   public List<T> takeOrdered(int num) {
      return JavaRDDLike$class.takeOrdered(this, num);
   }

   public long countApproxDistinct(double relativeSD) {
      return JavaRDDLike$class.countApproxDistinct(this, relativeSD);
   }

   public String name() {
      return JavaRDDLike$class.name(this);
   }

   public JavaFutureAction<Long> countAsync() {
      return JavaRDDLike$class.countAsync(this);
   }

   public JavaFutureAction<List<T>> collectAsync() {
      return JavaRDDLike$class.collectAsync(this);
   }

   public JavaFutureAction<List<T>> takeAsync(int num) {
      return JavaRDDLike$class.takeAsync(this, num);
   }

   public JavaFutureAction<Void> foreachAsync(VoidFunction<T> f) {
      return JavaRDDLike$class.foreachAsync(this, f);
   }

   public JavaFutureAction<Void> foreachPartitionAsync(VoidFunction<Iterator<T>> f) {
      return JavaRDDLike$class.foreachPartitionAsync(this, f);
   }

   public <R> boolean mapPartitionsWithIndex$default$2() {
      return JavaRDDLike$class.mapPartitionsWithIndex$default$2(this);
   }

   public AbstractJavaRDDLike() {
      JavaRDDLike$class.$init$(this);
   }
}
