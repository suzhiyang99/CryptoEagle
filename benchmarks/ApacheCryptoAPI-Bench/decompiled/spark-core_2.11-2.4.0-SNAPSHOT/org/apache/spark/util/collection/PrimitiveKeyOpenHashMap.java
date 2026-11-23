package org.apache.spark.util.collection;

import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.BufferedIterator;
import scala.collection.GenIterable;
import scala.collection.GenIterable$class;
import scala.collection.GenTraversable;
import scala.collection.GenTraversable$class;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterable$class;
import scala.collection.IterableLike$class;
import scala.collection.IterableView;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Parallel;
import scala.collection.Parallelizable$class;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.Traversable$class;
import scala.collection.TraversableLike$class;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.generic.FilterMonadic;
import scala.collection.generic.GenericCompanion;
import scala.collection.generic.GenericTraversableTemplate$class;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Builder;
import scala.collection.mutable.StringBuilder;
import scala.collection.parallel.Combiner;
import scala.collection.parallel.ParIterable;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.reflect.package$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055f!B\u0001\u0003\u0001\u0019a!a\u0006)sS6LG/\u001b<f\u0017\u0016Lx\n]3o\u0011\u0006\u001c\b.T1q\u0015\t\u0019A!\u0001\u0006d_2dWm\u0019;j_:T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOV\u0019QB\n \u0014\t\u0001qAC\u0013\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\u0007Uq\u0012E\u0004\u0002\u001799\u0011qcG\u0007\u00021)\u0011\u0011DG\u0001\u0007yI|w\u000e\u001e \u0004\u0001%\t\u0011#\u0003\u0002\u001e!\u00059\u0001/Y2lC\u001e,\u0017BA\u0010!\u0005!IE/\u001a:bE2,'BA\u000f\u0011!\u0011y!\u0005J\u001f\n\u0005\r\u0002\"A\u0002+va2,'\u0007\u0005\u0002&M1\u0001A!C\u0014\u0001A\u0003\u0005\tQ1\u0001)\u0005\u0005Y\u0015CA\u0015-!\ty!&\u0003\u0002,!\t9aj\u001c;iS:<\u0007CA\b.\u0013\tq\u0003CA\u0002B]fDCA\n\u00194qA\u0011q\"M\u0005\u0003eA\u00111b\u001d9fG&\fG.\u001b>fIF*1\u0005N\u001b8m9\u0011q\"N\u0005\u0003mA\tA\u0001T8oOF\"AEF\u000e\u0012c\u0015\u0019\u0013H\u000f\u001f<\u001d\ty!(\u0003\u0002<!\u0005\u0019\u0011J\u001c;2\t\u001122$\u0005\t\u0003Ky\"\u0011b\u0010\u0001!\u0002\u0003\u0005)\u0019\u0001\u0015\u0003\u0003YCSA\u0010\u0019B\u0007\u0016\u000bTa\t\u001b6\u0005Z\nD\u0001\n\f\u001c#E*1%\u000f\u001eEwE\"AEF\u000e\u0012c\u0015\u0019ciR%I\u001d\tyq)\u0003\u0002I!\u00051Ai\\;cY\u0016\fD\u0001\n\f\u001c#A\u0011qbS\u0005\u0003\u0019B\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001B\u0014\u0001\u0003\u0002\u0003\u0006IaT\u0001\u0010S:LG/[1m\u0007\u0006\u0004\u0018mY5usB\u0011q\u0002U\u0005\u0003#B\u00111!\u00138u\u0011!\u0019\u0006AaA!\u0002\u0017!\u0016AC3wS\u0012,gnY3%cA\u0019Q\u000b\u0017\u0013\u000e\u0003YS!a\u0016\t\u0002\u000fI,g\r\\3di&\u0011\u0011L\u0016\u0002\t\u00072\f7o\u001d+bO\"A1\f\u0001B\u0002B\u0003-A,\u0001\u0006fm&$WM\\2fII\u00022!\u0016->\u0011\u0015q\u0006\u0001\"\u0001`\u0003\u0019a\u0014N\\5u}Q\u0011\u0001-\u001a\u000b\u0004C\u000e$\u0007\u0003\u00022\u0001Iuj\u0011A\u0001\u0005\u0006'v\u0003\u001d\u0001\u0016\u0005\u00067v\u0003\u001d\u0001\u0018\u0005\u0006\u001dv\u0003\ra\u0014\u0005\u0006=\u0002!\ta\u001a\u000b\u0002QR\u0019\u0011-[6\t\u000f)4\u0017\u0011!a\u0002)\u0006QQM^5eK:\u001cW\rJ\u001a\t\u000f14\u0017\u0011!a\u00029\u0006QQM^5eK:\u001cW\r\n\u001b\t\u00139\u0004\u0001\u0019!a\u0001\n#y\u0017aB0lKf\u001cV\r^\u000b\u0002aB\u0019!-\u001d\u0013\n\u0005I\u0014!aC(qK:D\u0015m\u001d5TKRD\u0011\u0002\u001e\u0001A\u0002\u0003\u0007I\u0011C;\u0002\u0017}[W-_*fi~#S-\u001d\u000b\u0003mf\u0004\"aD<\n\u0005a\u0004\"\u0001B+oSRDqA_:\u0002\u0002\u0003\u0007\u0001/A\u0002yIEBa\u0001 \u0001!B\u0013\u0001\u0018\u0001C0lKf\u001cV\r\u001e\u0011\t\u0013y\u0004\u0001\u0019!a\u0001\n\u0013y\u0018aB0wC2,Xm]\u000b\u0003\u0003\u0003\u0001BaDA\u0002{%\u0019\u0011Q\u0001\t\u0003\u000b\u0005\u0013(/Y=\t\u0017\u0005%\u0001\u00011AA\u0002\u0013%\u00111B\u0001\f?Z\fG.^3t?\u0012*\u0017\u000fF\u0002w\u0003\u001bA\u0011B_A\u0004\u0003\u0003\u0005\r!!\u0001\t\u0011\u0005E\u0001\u0001)Q\u0005\u0003\u0003\t\u0001b\u0018<bYV,7\u000f\t\u0005\t\u0003+\u0001\u0001\u0019!C\u0005\u007f\u0006Qql\u001c7e-\u0006dW/Z:\t\u0013\u0005e\u0001\u00011A\u0005\n\u0005m\u0011AD0pY\u00124\u0016\r\\;fg~#S-\u001d\u000b\u0004m\u0006u\u0001\"\u0003>\u0002\u0018\u0005\u0005\t\u0019AA\u0001\u0011!\t\t\u0003\u0001Q!\n\u0005\u0005\u0011aC0pY\u00124\u0016\r\\;fg\u0002Bq!!\n\u0001\t\u0003\n9#\u0001\u0003tSj,W#A(\t\u000f\u0005-\u0002\u0001\"\u0001\u0002.\u0005A1m\u001c8uC&t7\u000f\u0006\u0003\u00020\u0005U\u0002cA\b\u00022%\u0019\u00111\u0007\t\u0003\u000f\t{w\u000e\\3b]\"9\u0011qGA\u0015\u0001\u0004!\u0013!A6\t\u000f\u0005m\u0002\u0001\"\u0001\u0002>\u0005)\u0011\r\u001d9msR\u0019Q(a\u0010\t\u000f\u0005]\u0012\u0011\ba\u0001I!9\u00111\t\u0001\u0005\u0002\u0005\u0015\u0013!C4fi>\u0013X\t\\:f)\u0015i\u0014qIA%\u0011\u001d\t9$!\u0011A\u0002\u0011Bq!a\u0013\u0002B\u0001\u0007Q(A\u0005fYN,g+\u00197vK\"9\u0011q\n\u0001\u0005\u0002\u0005E\u0013AB;qI\u0006$X\rF\u0003w\u0003'\n)\u0006C\u0004\u00028\u00055\u0003\u0019\u0001\u0013\t\u000f\u0005]\u0013Q\na\u0001{\u0005\ta\u000fC\u0004\u0002\\\u0001!\t!!\u0018\u0002\u0017\rD\u0017M\\4f-\u0006dW/\u001a\u000b\b{\u0005}\u0013\u0011MA6\u0011\u001d\t9$!\u0017A\u0002\u0011B\u0011\"a\u0019\u0002Z\u0011\u0005\r!!\u001a\u0002\u0019\u0011,g-Y;miZ\u000bG.^3\u0011\t=\t9'P\u0005\u0004\u0003S\u0002\"\u0001\u0003\u001fcs:\fW.\u001a \t\u0011\u00055\u0014\u0011\fa\u0001\u0003_\n!\"\\3sO\u00164\u0016\r\\;f!\u0015y\u0011\u0011O\u001f>\u0013\r\t\u0019\b\u0005\u0002\n\rVt7\r^5p]FBq!a\u001e\u0001\t\u0003\nI(\u0001\u0005ji\u0016\u0014\u0018\r^8s+\t\tY\b\u0005\u0003\u0016\u0003{\n\u0013bAA@A\tA\u0011\n^3sCR|'\u000fC\u0005\u0002\u0004\u0002\u0001\r\u0011\"\u0005\u0002\u0006\u0006!qM]8x+\t\t9\tE\u0003\u0010\u0003cze\u000fC\u0005\u0002\f\u0002\u0001\r\u0011\"\u0005\u0002\u000e\u0006AqM]8x?\u0012*\u0017\u000fF\u0002w\u0003\u001fC\u0011B_AE\u0003\u0003\u0005\r!a\"\t\u0011\u0005M\u0005\u0001)Q\u0005\u0003\u000f\u000bQa\u001a:po\u0002B\u0011\"a&\u0001\u0001\u0004%\t\"!'\u0002\t5|g/Z\u000b\u0003\u00037\u0003baDAO\u001f>3\u0018bAAP!\tIa)\u001e8di&|gN\r\u0005\n\u0003G\u0003\u0001\u0019!C\t\u0003K\u000b\u0001\"\\8wK~#S-\u001d\u000b\u0004m\u0006\u001d\u0006\"\u0003>\u0002\"\u0006\u0005\t\u0019AAN\u0011!\tY\u000b\u0001Q!\n\u0005m\u0015!B7pm\u0016\u0004\u0003"
)
public class PrimitiveKeyOpenHashMap<K, V> implements Iterable<Tuple2<K, V>>, Serializable {
   public final ClassTag<V> org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$evidence$2;
   public OpenHashSet<K> _keySet;
   public Object _values;
   public Object _oldValues;
   public Function1<Object, BoxedUnit> org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow;
   public Function2<Object, Object, BoxedUnit> org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move;

   public GenericCompanion<Iterable> companion() {
      return Iterable$class.companion(this);
   }

   public Iterable<Tuple2<K, V>> seq() {
      return Iterable$class.seq(this);
   }

   public Iterable<Tuple2<K, V>> thisCollection() {
      return IterableLike$class.thisCollection(this);
   }

   public Iterable toCollection(Object repr) {
      return IterableLike$class.toCollection(this, repr);
   }

   public <U> void foreach(Function1<Tuple2<K, V>, U> f) {
      IterableLike$class.foreach(this, f);
   }

   public boolean forall(Function1<Tuple2<K, V>, Object> p) {
      return IterableLike$class.forall(this, p);
   }

   public boolean exists(Function1<Tuple2<K, V>, Object> p) {
      return IterableLike$class.exists(this, p);
   }

   public Option<Tuple2<K, V>> find(Function1<Tuple2<K, V>, Object> p) {
      return IterableLike$class.find(this, p);
   }

   public boolean isEmpty() {
      return IterableLike$class.isEmpty(this);
   }

   public <B> B foldRight(B z, Function2<Tuple2<K, V>, B, B> op) {
      return (B)IterableLike$class.foldRight(this, z, op);
   }

   public <B> B reduceRight(Function2<Tuple2<K, V>, B, B> op) {
      return (B)IterableLike$class.reduceRight(this, op);
   }

   public Iterable<Tuple2<K, V>> toIterable() {
      return IterableLike$class.toIterable(this);
   }

   public Iterator<Tuple2<K, V>> toIterator() {
      return IterableLike$class.toIterator(this);
   }

   public Object head() {
      return IterableLike$class.head(this);
   }

   public Object slice(int from, int until) {
      return IterableLike$class.slice(this, from, until);
   }

   public Object take(int n) {
      return IterableLike$class.take(this, n);
   }

   public Object drop(int n) {
      return IterableLike$class.drop(this, n);
   }

   public Object takeWhile(Function1 p) {
      return IterableLike$class.takeWhile(this, p);
   }

   public Iterator<Iterable<Tuple2<K, V>>> grouped(int size) {
      return IterableLike$class.grouped(this, size);
   }

   public Iterator<Iterable<Tuple2<K, V>>> sliding(int size) {
      return IterableLike$class.sliding(this, size);
   }

   public Iterator<Iterable<Tuple2<K, V>>> sliding(int size, int step) {
      return IterableLike$class.sliding(this, size, step);
   }

   public Object takeRight(int n) {
      return IterableLike$class.takeRight(this, n);
   }

   public Object dropRight(int n) {
      return IterableLike$class.dropRight(this, n);
   }

   public <B> void copyToArray(Object xs, int start, int len) {
      IterableLike$class.copyToArray(this, xs, start, len);
   }

   public <A1, B, That> That zip(GenIterable<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zip(this, that, bf);
   }

   public <B, A1, That> That zipAll(GenIterable<B> that, A1 thisElem, B thatElem, CanBuildFrom<Iterable<Tuple2<K, V>>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zipAll(this, that, thisElem, thatElem, bf);
   }

   public <A1, That> That zipWithIndex(CanBuildFrom<Iterable<Tuple2<K, V>>, Tuple2<A1, Object>, That> bf) {
      return (That)IterableLike$class.zipWithIndex(this, bf);
   }

   public <B> boolean sameElements(GenIterable<B> that) {
      return IterableLike$class.sameElements(this, that);
   }

   public Stream<Tuple2<K, V>> toStream() {
      return IterableLike$class.toStream(this);
   }

   public boolean canEqual(Object that) {
      return IterableLike$class.canEqual(this, that);
   }

   public Object view() {
      return IterableLike$class.view(this);
   }

   public IterableView<Tuple2<K, V>, Iterable<Tuple2<K, V>>> view(int from, int until) {
      return IterableLike$class.view(this, from, until);
   }

   public Builder<Tuple2<K, V>, Iterable<Tuple2<K, V>>> newBuilder() {
      return GenericTraversableTemplate$class.newBuilder(this);
   }

   public <B> Builder<B, Iterable<B>> genericBuilder() {
      return GenericTraversableTemplate$class.genericBuilder(this);
   }

   public <A1, A2> Tuple2<Iterable<A1>, Iterable<A2>> unzip(Function1<Tuple2<K, V>, Tuple2<A1, A2>> asPair) {
      return GenericTraversableTemplate$class.unzip(this, asPair);
   }

   public <A1, A2, A3> Tuple3<Iterable<A1>, Iterable<A2>, Iterable<A3>> unzip3(Function1<Tuple2<K, V>, Tuple3<A1, A2, A3>> asTriple) {
      return GenericTraversableTemplate$class.unzip3(this, asTriple);
   }

   public GenTraversable flatten(Function1 asTraversable) {
      return GenericTraversableTemplate$class.flatten(this, asTraversable);
   }

   public GenTraversable transpose(Function1 asTraversable) {
      return GenericTraversableTemplate$class.transpose(this, asTraversable);
   }

   public Object repr() {
      return TraversableLike$class.repr(this);
   }

   public final boolean isTraversableAgain() {
      return TraversableLike$class.isTraversableAgain(this);
   }

   public Combiner<Tuple2<K, V>, ParIterable<Tuple2<K, V>>> parCombiner() {
      return TraversableLike$class.parCombiner(this);
   }

   public boolean hasDefiniteSize() {
      return TraversableLike$class.hasDefiniteSize(this);
   }

   public <B, That> That $plus$plus(GenTraversableOnce<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus(this, that, bf);
   }

   public <B, That> That $plus$plus$colon(TraversableOnce<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (TraversableOnce)that, bf);
   }

   public <B, That> That $plus$plus$colon(Traversable<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (Traversable)that, bf);
   }

   public <B, That> That map(Function1<Tuple2<K, V>, B> f, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.map(this, f, bf);
   }

   public <B, That> That flatMap(Function1<Tuple2<K, V>, GenTraversableOnce<B>> f, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.flatMap(this, f, bf);
   }

   public Object filter(Function1 p) {
      return TraversableLike$class.filter(this, p);
   }

   public Object filterNot(Function1 p) {
      return TraversableLike$class.filterNot(this, p);
   }

   public <B, That> That collect(PartialFunction<Tuple2<K, V>, B> pf, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.collect(this, pf, bf);
   }

   public Tuple2<Iterable<Tuple2<K, V>>, Iterable<Tuple2<K, V>>> partition(Function1<Tuple2<K, V>, Object> p) {
      return TraversableLike$class.partition(this, p);
   }

   public <K> Map<K, Iterable<Tuple2<K, V>>> groupBy(Function1<Tuple2<K, V>, K> f) {
      return TraversableLike$class.groupBy(this, f);
   }

   public <B, That> That scan(B z, Function2<B, B, B> op, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> cbf) {
      return (That)TraversableLike$class.scan(this, z, op, cbf);
   }

   public <B, That> That scanLeft(B z, Function2<B, Tuple2<K, V>, B> op, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.scanLeft(this, z, op, bf);
   }

   public <B, That> That scanRight(B z, Function2<Tuple2<K, V>, B, B> op, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.scanRight(this, z, op, bf);
   }

   public Option<Tuple2<K, V>> headOption() {
      return TraversableLike$class.headOption(this);
   }

   public Object tail() {
      return TraversableLike$class.tail(this);
   }

   public Object last() {
      return TraversableLike$class.last(this);
   }

   public Option<Tuple2<K, V>> lastOption() {
      return TraversableLike$class.lastOption(this);
   }

   public Object init() {
      return TraversableLike$class.init(this);
   }

   public Object sliceWithKnownDelta(int from, int until, int delta) {
      return TraversableLike$class.sliceWithKnownDelta(this, from, until, delta);
   }

   public Object sliceWithKnownBound(int from, int until) {
      return TraversableLike$class.sliceWithKnownBound(this, from, until);
   }

   public Object dropWhile(Function1 p) {
      return TraversableLike$class.dropWhile(this, p);
   }

   public Tuple2<Iterable<Tuple2<K, V>>, Iterable<Tuple2<K, V>>> span(Function1<Tuple2<K, V>, Object> p) {
      return TraversableLike$class.span(this, p);
   }

   public Tuple2<Iterable<Tuple2<K, V>>, Iterable<Tuple2<K, V>>> splitAt(int n) {
      return TraversableLike$class.splitAt(this, n);
   }

   public Iterator<Iterable<Tuple2<K, V>>> tails() {
      return TraversableLike$class.tails(this);
   }

   public Iterator<Iterable<Tuple2<K, V>>> inits() {
      return TraversableLike$class.inits(this);
   }

   public Traversable<Tuple2<K, V>> toTraversable() {
      return TraversableLike$class.toTraversable(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, V>, Col> cbf) {
      return (Col)TraversableLike$class.to(this, cbf);
   }

   public String toString() {
      return TraversableLike$class.toString(this);
   }

   public String stringPrefix() {
      return TraversableLike$class.stringPrefix(this);
   }

   public FilterMonadic<Tuple2<K, V>, Iterable<Tuple2<K, V>>> withFilter(Function1<Tuple2<K, V>, Object> p) {
      return TraversableLike$class.withFilter(this, p);
   }

   public Parallel par() {
      return Parallelizable$class.par(this);
   }

   public List<Tuple2<K, V>> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<Tuple2<K, V>, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, V>, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, Tuple2<K, V>, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<Tuple2<K, V>, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, Tuple2<K, V>, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, V>, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<Tuple2<K, V>, B, B> op) {
      return TraversableOnce$class.reduceRightOption(this, op);
   }

   public <A1> A1 reduce(Function2<A1, A1, A1> op) {
      return (A1)TraversableOnce$class.reduce(this, op);
   }

   public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
      return TraversableOnce$class.reduceOption(this, op);
   }

   public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
      return (A1)TraversableOnce$class.fold(this, z, op);
   }

   public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, V>, B> seqop, Function2<B, B, B> combop) {
      return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
   }

   public <B> B sum(Numeric<B> num) {
      return (B)TraversableOnce$class.sum(this, num);
   }

   public <B> B product(Numeric<B> num) {
      return (B)TraversableOnce$class.product(this, num);
   }

   public Object min(Ordering cmp) {
      return TraversableOnce$class.min(this, cmp);
   }

   public Object max(Ordering cmp) {
      return TraversableOnce$class.max(this, cmp);
   }

   public Object maxBy(Function1 f, Ordering cmp) {
      return TraversableOnce$class.maxBy(this, f, cmp);
   }

   public Object minBy(Function1 f, Ordering cmp) {
      return TraversableOnce$class.minBy(this, f, cmp);
   }

   public <B> void copyToBuffer(Buffer<B> dest) {
      TraversableOnce$class.copyToBuffer(this, dest);
   }

   public <B> void copyToArray(Object xs, int start) {
      TraversableOnce$class.copyToArray(this, xs, start);
   }

   public <B> void copyToArray(Object xs) {
      TraversableOnce$class.copyToArray(this, xs);
   }

   public <B> Object toArray(ClassTag<B> evidence$1) {
      return TraversableOnce$class.toArray(this, evidence$1);
   }

   public List<Tuple2<K, V>> toList() {
      return TraversableOnce$class.toList(this);
   }

   public Seq<Tuple2<K, V>> toSeq() {
      return TraversableOnce$class.toSeq(this);
   }

   public IndexedSeq<Tuple2<K, V>> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<Tuple2<K, V>> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, V>, Tuple2<T, U>> ev) {
      return TraversableOnce$class.toMap(this, ev);
   }

   public String mkString(String start, String sep, String end) {
      return TraversableOnce$class.mkString(this, start, sep, end);
   }

   public String mkString(String sep) {
      return TraversableOnce$class.mkString(this, sep);
   }

   public String mkString() {
      return TraversableOnce$class.mkString(this);
   }

   public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
      return TraversableOnce$class.addString(this, b, start, sep, end);
   }

   public StringBuilder addString(StringBuilder b, String sep) {
      return TraversableOnce$class.addString(this, b, sep);
   }

   public StringBuilder addString(StringBuilder b) {
      return TraversableOnce$class.addString(this, b);
   }

   public OpenHashSet<K> _keySet() {
      return this._keySet;
   }

   public void _keySet_$eq(OpenHashSet<K> x$1) {
      this._keySet = x$1;
   }

   public Object _values() {
      return this._values;
   }

   public void _values_$eq(Object x$1) {
      this._values = x$1;
   }

   public Object _oldValues() {
      return this._oldValues;
   }

   public void _oldValues_$eq(Object x$1) {
      this._oldValues = x$1;
   }

   public int size() {
      return this._keySet().size();
   }

   public boolean contains(K k) {
      return this._keySet().getPos(k) != OpenHashSet$.MODULE$.INVALID_POS();
   }

   public V apply(K k) {
      int pos = this._keySet().getPos(k);
      return (V)ScalaRunTime$.MODULE$.array_apply(this._values(), pos);
   }

   public V getOrElse(K k, V elseValue) {
      int pos = this._keySet().getPos(k);
      return (V)(pos >= 0 ? ScalaRunTime$.MODULE$.array_apply(this._values(), pos) : elseValue);
   }

   public void update(K k, V v) {
      int pos = this._keySet().addWithoutResize(k) & OpenHashSet$.MODULE$.POSITION_MASK();
      ScalaRunTime$.MODULE$.array_update(this._values(), pos, v);
      this._keySet().rehashIfNeeded(k, this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow(), this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move());
      this._oldValues_$eq((Object)null);
   }

   public V changeValue(K k, Function0<V> defaultValue, Function1<V, V> mergeValue) {
      int pos = this._keySet().addWithoutResize(k);
      Object var10000;
      if ((pos & OpenHashSet$.MODULE$.NONEXISTENCE_MASK()) != 0) {
         Object newValue = defaultValue.apply();
         ScalaRunTime$.MODULE$.array_update(this._values(), pos & OpenHashSet$.MODULE$.POSITION_MASK(), newValue);
         this._keySet().rehashIfNeeded(k, this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow(), this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move());
         var10000 = newValue;
      } else {
         ScalaRunTime$.MODULE$.array_update(this._values(), pos, mergeValue.apply(ScalaRunTime$.MODULE$.array_apply(this._values(), pos)));
         var10000 = ScalaRunTime$.MODULE$.array_apply(this._values(), pos);
      }

      return (V)var10000;
   }

   public Iterator<Tuple2<K, V>> iterator() {
      return new Iterator<Tuple2<K, V>>(this) {
         private int pos;
         private Tuple2<K, V> nextPair;
         // $FF: synthetic field
         private final PrimitiveKeyOpenHashMap $outer;

         public Iterator<Tuple2<K, V>> seq() {
            return Iterator$class.seq(this);
         }

         public boolean isEmpty() {
            return Iterator$class.isEmpty(this);
         }

         public boolean isTraversableAgain() {
            return Iterator$class.isTraversableAgain(this);
         }

         public boolean hasDefiniteSize() {
            return Iterator$class.hasDefiniteSize(this);
         }

         public Iterator<Tuple2<K, V>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Tuple2<K, V>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Tuple2<K, V>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Tuple2<K, V>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Tuple2<K, V>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Tuple2<K, V>> filter(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<K, V>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Tuple2<K, V>> withFilter(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Tuple2<K, V>> filterNot(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Tuple2<K, V>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Tuple2<K, V>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Tuple2<K, V>> takeWhile(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> partition(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> span(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Tuple2<K, V>> dropWhile(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Tuple2<K, V>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Tuple2<K, V>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Tuple2<K, V>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Tuple2<K, V>> find(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Tuple2<K, V>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Tuple2<K, V>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Tuple2<K, V>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> duplicate() {
            return Iterator$class.duplicate(this);
         }

         public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
            return Iterator$class.patch(this, from, patchElems, replaced);
         }

         public <B> void copyToArray(Object xs, int start, int len) {
            Iterator$class.copyToArray(this, xs, start, len);
         }

         public boolean sameElements(Iterator<?> that) {
            return Iterator$class.sameElements(this, that);
         }

         public Traversable<Tuple2<K, V>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Tuple2<K, V>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Tuple2<K, V>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Tuple2<K, V>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Tuple2<K, V>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, V>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, V>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Tuple2<K, V>, B, B> op) {
            return TraversableOnce$class.reduceRightOption(this, op);
         }

         public <A1> A1 reduce(Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.reduce(this, op);
         }

         public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
            return TraversableOnce$class.reduceOption(this, op);
         }

         public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.fold(this, z, op);
         }

         public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, V>, B> seqop, Function2<B, B, B> combop) {
            return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
         }

         public <B> B sum(Numeric<B> num) {
            return (B)TraversableOnce$class.sum(this, num);
         }

         public <B> B product(Numeric<B> num) {
            return (B)TraversableOnce$class.product(this, num);
         }

         public Object min(Ordering cmp) {
            return TraversableOnce$class.min(this, cmp);
         }

         public Object max(Ordering cmp) {
            return TraversableOnce$class.max(this, cmp);
         }

         public Object maxBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.maxBy(this, f, cmp);
         }

         public Object minBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.minBy(this, f, cmp);
         }

         public <B> void copyToBuffer(Buffer<B> dest) {
            TraversableOnce$class.copyToBuffer(this, dest);
         }

         public <B> void copyToArray(Object xs, int start) {
            TraversableOnce$class.copyToArray(this, xs, start);
         }

         public <B> void copyToArray(Object xs) {
            TraversableOnce$class.copyToArray(this, xs);
         }

         public <B> Object toArray(ClassTag<B> evidence$1) {
            return TraversableOnce$class.toArray(this, evidence$1);
         }

         public List<Tuple2<K, V>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Tuple2<K, V>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Tuple2<K, V>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Tuple2<K, V>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Tuple2<K, V>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, V>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, V>, Tuple2<T, U>> ev) {
            return TraversableOnce$class.toMap(this, ev);
         }

         public String mkString(String start, String sep, String end) {
            return TraversableOnce$class.mkString(this, start, sep, end);
         }

         public String mkString(String sep) {
            return TraversableOnce$class.mkString(this, sep);
         }

         public String mkString() {
            return TraversableOnce$class.mkString(this);
         }

         public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
            return TraversableOnce$class.addString(this, b, start, sep, end);
         }

         public StringBuilder addString(StringBuilder b, String sep) {
            return TraversableOnce$class.addString(this, b, sep);
         }

         public StringBuilder addString(StringBuilder b) {
            return TraversableOnce$class.addString(this, b);
         }

         private int pos() {
            return this.pos;
         }

         private void pos_$eq(int x$1) {
            this.pos = x$1;
         }

         private Tuple2<K, V> nextPair() {
            return this.nextPair;
         }

         private void nextPair_$eq(Tuple2<K, V> x$1) {
            this.nextPair = x$1;
         }

         private Tuple2<K, V> computeNextPair() {
            this.pos_$eq(this.$outer._keySet().nextPos(this.pos()));
            Tuple2 var10000;
            if (this.pos() >= 0) {
               Tuple2 ret = new Tuple2(this.$outer._keySet().getValue(this.pos()), ScalaRunTime$.MODULE$.array_apply(this.$outer._values(), this.pos()));
               this.pos_$eq(this.pos() + 1);
               var10000 = ret;
            } else {
               var10000 = null;
            }

            return var10000;
         }

         public boolean hasNext() {
            return this.nextPair() != null;
         }

         public Tuple2<K, V> next() {
            Tuple2 pair = this.nextPair();
            this.nextPair_$eq(this.computeNextPair());
            return pair;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.pos = 0;
               this.nextPair = this.computeNextPair();
            }
         }
      };
   }

   public Function1<Object, BoxedUnit> org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow() {
      return this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow;
   }

   public void org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow_$eq(Function1<Object, BoxedUnit> x$1) {
      this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow = x$1;
   }

   public Function2<Object, Object, BoxedUnit> org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move() {
      return this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move;
   }

   public void org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move_$eq(Function2<Object, Object, BoxedUnit> x$1) {
      this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move = x$1;
   }

   public OpenHashSet<Object> _keySet$mcI$sp() {
      return this._keySet();
   }

   public OpenHashSet<Object> _keySet$mcJ$sp() {
      return this._keySet();
   }

   public void _keySet$mcI$sp_$eq(OpenHashSet<Object> x$1) {
      this._keySet_$eq(x$1);
   }

   public void _keySet$mcJ$sp_$eq(OpenHashSet<Object> x$1) {
      this._keySet_$eq(x$1);
   }

   public double[] _values$mcD$sp() {
      return (double[])this._values();
   }

   public int[] _values$mcI$sp() {
      return (int[])this._values();
   }

   public long[] _values$mcJ$sp() {
      return (long[])this._values();
   }

   public void _values$mcD$sp_$eq(double[] x$1) {
      this._values_$eq(x$1);
   }

   public void _values$mcI$sp_$eq(int[] x$1) {
      this._values_$eq(x$1);
   }

   public void _values$mcJ$sp_$eq(long[] x$1) {
      this._values_$eq(x$1);
   }

   public double[] _oldValues$mcD$sp() {
      return (double[])this._oldValues();
   }

   public int[] _oldValues$mcI$sp() {
      return (int[])this._oldValues();
   }

   public long[] _oldValues$mcJ$sp() {
      return (long[])this._oldValues();
   }

   public void _oldValues$mcD$sp_$eq(double[] x$1) {
      this._oldValues_$eq(x$1);
   }

   public void _oldValues$mcI$sp_$eq(int[] x$1) {
      this._oldValues_$eq(x$1);
   }

   public void _oldValues$mcJ$sp_$eq(long[] x$1) {
      this._oldValues_$eq(x$1);
   }

   public boolean contains$mcI$sp(int k) {
      return this.contains(BoxesRunTime.boxToInteger(k));
   }

   public boolean contains$mcJ$sp(long k) {
      return this.contains(BoxesRunTime.boxToLong(k));
   }

   public double apply$mcID$sp(int k) {
      return BoxesRunTime.unboxToDouble(this.apply(BoxesRunTime.boxToInteger(k)));
   }

   public int apply$mcII$sp(int k) {
      return BoxesRunTime.unboxToInt(this.apply(BoxesRunTime.boxToInteger(k)));
   }

   public long apply$mcIJ$sp(int k) {
      return BoxesRunTime.unboxToLong(this.apply(BoxesRunTime.boxToInteger(k)));
   }

   public double apply$mcJD$sp(long k) {
      return BoxesRunTime.unboxToDouble(this.apply(BoxesRunTime.boxToLong(k)));
   }

   public int apply$mcJI$sp(long k) {
      return BoxesRunTime.unboxToInt(this.apply(BoxesRunTime.boxToLong(k)));
   }

   public long apply$mcJJ$sp(long k) {
      return BoxesRunTime.unboxToLong(this.apply(BoxesRunTime.boxToLong(k)));
   }

   public double getOrElse$mcID$sp(int k, double elseValue) {
      return BoxesRunTime.unboxToDouble(this.getOrElse(BoxesRunTime.boxToInteger(k), BoxesRunTime.boxToDouble(elseValue)));
   }

   public int getOrElse$mcII$sp(int k, int elseValue) {
      return BoxesRunTime.unboxToInt(this.getOrElse(BoxesRunTime.boxToInteger(k), BoxesRunTime.boxToInteger(elseValue)));
   }

   public long getOrElse$mcIJ$sp(int k, long elseValue) {
      return BoxesRunTime.unboxToLong(this.getOrElse(BoxesRunTime.boxToInteger(k), BoxesRunTime.boxToLong(elseValue)));
   }

   public double getOrElse$mcJD$sp(long k, double elseValue) {
      return BoxesRunTime.unboxToDouble(this.getOrElse(BoxesRunTime.boxToLong(k), BoxesRunTime.boxToDouble(elseValue)));
   }

   public int getOrElse$mcJI$sp(long k, int elseValue) {
      return BoxesRunTime.unboxToInt(this.getOrElse(BoxesRunTime.boxToLong(k), BoxesRunTime.boxToInteger(elseValue)));
   }

   public long getOrElse$mcJJ$sp(long k, long elseValue) {
      return BoxesRunTime.unboxToLong(this.getOrElse(BoxesRunTime.boxToLong(k), BoxesRunTime.boxToLong(elseValue)));
   }

   public void update$mcID$sp(int k, double v) {
      this.update(BoxesRunTime.boxToInteger(k), BoxesRunTime.boxToDouble(v));
   }

   public void update$mcII$sp(int k, int v) {
      this.update(BoxesRunTime.boxToInteger(k), BoxesRunTime.boxToInteger(v));
   }

   public void update$mcIJ$sp(int k, long v) {
      this.update(BoxesRunTime.boxToInteger(k), BoxesRunTime.boxToLong(v));
   }

   public void update$mcJD$sp(long k, double v) {
      this.update(BoxesRunTime.boxToLong(k), BoxesRunTime.boxToDouble(v));
   }

   public void update$mcJI$sp(long k, int v) {
      this.update(BoxesRunTime.boxToLong(k), BoxesRunTime.boxToInteger(v));
   }

   public void update$mcJJ$sp(long k, long v) {
      this.update(BoxesRunTime.boxToLong(k), BoxesRunTime.boxToLong(v));
   }

   public double changeValue$mcID$sp(int k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToDouble(this.changeValue(BoxesRunTime.boxToInteger(k), defaultValue, mergeValue));
   }

   public int changeValue$mcII$sp(int k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToInt(this.changeValue(BoxesRunTime.boxToInteger(k), defaultValue, mergeValue));
   }

   public long changeValue$mcIJ$sp(int k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToLong(this.changeValue(BoxesRunTime.boxToInteger(k), defaultValue, mergeValue));
   }

   public double changeValue$mcJD$sp(long k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToDouble(this.changeValue(BoxesRunTime.boxToLong(k), defaultValue, mergeValue));
   }

   public int changeValue$mcJI$sp(long k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToInt(this.changeValue(BoxesRunTime.boxToLong(k), defaultValue, mergeValue));
   }

   public long changeValue$mcJJ$sp(long k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToLong(this.changeValue(BoxesRunTime.boxToLong(k), defaultValue, mergeValue));
   }

   public boolean specInstance$() {
      return false;
   }

   public PrimitiveKeyOpenHashMap(int initialCapacity, ClassTag<K> evidence$1, ClassTag<V> evidence$2) {
      this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$evidence$2 = evidence$2;
      TraversableOnce$class.$init$(this);
      Parallelizable$class.$init$(this);
      TraversableLike$class.$init$(this);
      GenericTraversableTemplate$class.$init$(this);
      GenTraversable$class.$init$(this);
      Traversable$class.$init$(this);
      GenIterable$class.$init$(this);
      IterableLike$class.$init$(this);
      Iterable$class.$init$(this);
      if (!this.specInstance$()) {
         boolean var7;
         Predef$ var10000;
         label27: {
            label31: {
               var10000 = Predef$.MODULE$;
               ClassTag var10001 = package$.MODULE$.classTag(evidence$1);
               ClassTag var4 = package$.MODULE$.classTag(ClassTag$.MODULE$.Long());
               if (var10001 == null) {
                  if (var4 == null) {
                     break label31;
                  }
               } else if (var10001.equals(var4)) {
                  break label31;
               }

               var10001 = package$.MODULE$.classTag(evidence$1);
               ClassTag var5 = package$.MODULE$.classTag(ClassTag$.MODULE$.Int());
               if (var10001 == null) {
                  if (var5 == null) {
                     break label31;
                  }
               } else if (var10001.equals(var5)) {
                  break label31;
               }

               var7 = false;
               break label27;
            }

            var7 = true;
         }

         var10000.require(var7);
         this._keySet_$eq(new OpenHashSet(initialCapacity, evidence$1));
         this._values_$eq(evidence$2.newArray(this._keySet().capacity()));
         this._oldValues = null;
         this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow = new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final PrimitiveKeyOpenHashMap $outer;

            public final void apply(int newCapacity) {
               this.apply$mcVI$sp(newCapacity);
            }

            public void apply$mcVI$sp(int newCapacity) {
               this.$outer._oldValues_$eq(this.$outer._values());
               this.$outer._values_$eq(this.$outer.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$evidence$2.newArray(newCapacity));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
         this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move = new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final PrimitiveKeyOpenHashMap $outer;

            public final void apply(int oldPos, int newPos) {
               this.apply$mcVII$sp(oldPos, newPos);
            }

            public void apply$mcVII$sp(int oldPos, int newPos) {
               ScalaRunTime$.MODULE$.array_update(this.$outer._values(), newPos, ScalaRunTime$.MODULE$.array_apply(this.$outer._oldValues(), oldPos));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
      }

   }

   public PrimitiveKeyOpenHashMap(ClassTag<K> evidence$3, ClassTag<V> evidence$4) {
      this(64, evidence$3, evidence$4);
   }
}
