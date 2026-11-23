package org.apache.spark.util;

import java.io.Serializable;
import java.util.PriorityQueue;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Tuple2;
import scala.Tuple3;
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
import scala.collection.JavaConverters$;
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
import scala.collection.generic.Growable;
import scala.collection.generic.Growable$class;
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
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ma!B\u0001\u0003\u0001\u0011Q!\u0001\u0006\"pk:$W\r\u001a)sS>\u0014\u0018\u000e^=Rk\u0016,XM\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006\"'\u0015\u0001AB\u0005\u00163!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00191\u0003H\u0010\u000f\u0005QQbBA\u000b\u001a\u001b\u00051\"BA\f\u0019\u0003\u0019a$o\\8u}\r\u0001\u0011\"A\b\n\u0005mq\u0011a\u00029bG.\fw-Z\u0005\u0003;y\u0011\u0001\"\u0013;fe\u0006\u0014G.\u001a\u0006\u000379\u0001\"\u0001I\u0011\r\u0001\u0011)!\u0005\u0001b\u0001G\t\t\u0011)\u0005\u0002%OA\u0011Q\"J\u0005\u0003M9\u0011qAT8uQ&tw\r\u0005\u0002\u000eQ%\u0011\u0011F\u0004\u0002\u0004\u0003:L\bcA\u00161?5\tAF\u0003\u0002.]\u00059q-\u001a8fe&\u001c'BA\u0018\u000f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003c1\u0012\u0001b\u0012:po\u0006\u0014G.\u001a\t\u0003gaj\u0011\u0001\u000e\u0006\u0003kY\n!![8\u000b\u0003]\nAA[1wC&\u0011\u0011\b\u000e\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\tw\u0001\u0011\t\u0011)A\u0005y\u00059Q.\u0019=TSj,\u0007CA\u0007>\u0013\tqdBA\u0002J]RD\u0001\u0002\u0011\u0001\u0003\u0002\u0003\u0006Y!Q\u0001\u0004_J$\u0007cA\nC?%\u00111I\b\u0002\t\u001fJ$WM]5oO\")Q\t\u0001C\u0001\r\u00061A(\u001b8jiz\"\"aR&\u0015\u0005!S\u0005cA%\u0001?5\t!\u0001C\u0003A\t\u0002\u000f\u0011\tC\u0003<\t\u0002\u0007A\bC\u0004N\u0001\t\u0007I\u0011\u0002(\u0002\u0015UtG-\u001a:ms&tw-F\u0001P!\r\u0001&kH\u0007\u0002#*\u00111AN\u0005\u0003'F\u0013Q\u0002\u0015:j_JLG/_)vKV,\u0007BB+\u0001A\u0003%q*A\u0006v]\u0012,'\u000f\\=j]\u001e\u0004\u0003\"B,\u0001\t\u0003B\u0016\u0001C5uKJ\fGo\u001c:\u0016\u0003e\u00032a\u0005. \u0013\tYfD\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u0015i\u0006\u0001\"\u0011_\u0003\u0011\u0019\u0018N_3\u0016\u0003qBQ\u0001\u0019\u0001\u0005B\u0005\fQ\u0002\n9mkN$\u0003\u000f\\;tI\u0015\fHC\u00012d\u001b\u0005\u0001\u0001\"\u00023`\u0001\u0004)\u0017A\u0001=t!\r\u0019bmH\u0005\u0003Oz\u0011q\u0002\u0016:bm\u0016\u00148/\u00192mK>s7-\u001a\u0005\u0006S\u0002!\tE[\u0001\tIAdWo\u001d\u0013fcR\u0011!m\u001b\u0005\u0006Y\"\u0004\raH\u0001\u0005K2,W\u000eC\u0003o\u0001\u0011\u0005q.\u0001\u0003q_2dG#A\u0010\t\u000b%\u0004A\u0011I9\u0015\t\t\u0014HO\u001e\u0005\u0006gB\u0004\raH\u0001\u0006K2,W.\r\u0005\u0006kB\u0004\raH\u0001\u0006K2,WN\r\u0005\u0006oB\u0004\r\u0001_\u0001\u0006K2,Wn\u001d\t\u0004\u001be|\u0012B\u0001>\u000f\u0005)a$/\u001a9fCR,GM\u0010\u0005\u0006y\u0002!\t%`\u0001\u0006G2,\u0017M\u001d\u000b\u0002}B\u0011Qb`\u0005\u0004\u0003\u0003q!\u0001B+oSRDq!!\u0002\u0001\t\u0013\t9!\u0001\nnCf\u0014WMU3qY\u0006\u001cW\rT8xKN$H\u0003BA\u0005\u0003\u001f\u00012!DA\u0006\u0013\r\tiA\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d\t\t\"a\u0001A\u0002}\t\u0011!\u0019"
)
public class BoundedPriorityQueue<A> implements Iterable<A>, Growable<A>, Serializable {
   private final int maxSize;
   private final Ordering<A> ord;
   private final PriorityQueue<A> underlying;

   public GenericCompanion<Iterable> companion() {
      return Iterable$class.companion(this);
   }

   public Iterable<A> seq() {
      return Iterable$class.seq(this);
   }

   public Iterable<A> thisCollection() {
      return IterableLike$class.thisCollection(this);
   }

   public Iterable toCollection(Object repr) {
      return IterableLike$class.toCollection(this, repr);
   }

   public <U> void foreach(Function1<A, U> f) {
      IterableLike$class.foreach(this, f);
   }

   public boolean forall(Function1<A, Object> p) {
      return IterableLike$class.forall(this, p);
   }

   public boolean exists(Function1<A, Object> p) {
      return IterableLike$class.exists(this, p);
   }

   public Option<A> find(Function1<A, Object> p) {
      return IterableLike$class.find(this, p);
   }

   public boolean isEmpty() {
      return IterableLike$class.isEmpty(this);
   }

   public <B> B foldRight(B z, Function2<A, B, B> op) {
      return (B)IterableLike$class.foldRight(this, z, op);
   }

   public <B> B reduceRight(Function2<A, B, B> op) {
      return (B)IterableLike$class.reduceRight(this, op);
   }

   public Iterable<A> toIterable() {
      return IterableLike$class.toIterable(this);
   }

   public Iterator<A> toIterator() {
      return IterableLike$class.toIterator(this);
   }

   public A head() {
      return (A)IterableLike$class.head(this);
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

   public Iterator<Iterable<A>> grouped(int size) {
      return IterableLike$class.grouped(this, size);
   }

   public Iterator<Iterable<A>> sliding(int size) {
      return IterableLike$class.sliding(this, size);
   }

   public Iterator<Iterable<A>> sliding(int size, int step) {
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

   public <A1, B, That> That zip(GenIterable<B> that, CanBuildFrom<Iterable<A>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zip(this, that, bf);
   }

   public <B, A1, That> That zipAll(GenIterable<B> that, A1 thisElem, B thatElem, CanBuildFrom<Iterable<A>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zipAll(this, that, thisElem, thatElem, bf);
   }

   public <A1, That> That zipWithIndex(CanBuildFrom<Iterable<A>, Tuple2<A1, Object>, That> bf) {
      return (That)IterableLike$class.zipWithIndex(this, bf);
   }

   public <B> boolean sameElements(GenIterable<B> that) {
      return IterableLike$class.sameElements(this, that);
   }

   public Stream<A> toStream() {
      return IterableLike$class.toStream(this);
   }

   public boolean canEqual(Object that) {
      return IterableLike$class.canEqual(this, that);
   }

   public Object view() {
      return IterableLike$class.view(this);
   }

   public IterableView<A, Iterable<A>> view(int from, int until) {
      return IterableLike$class.view(this, from, until);
   }

   public Builder<A, Iterable<A>> newBuilder() {
      return GenericTraversableTemplate$class.newBuilder(this);
   }

   public <B> Builder<B, Iterable<B>> genericBuilder() {
      return GenericTraversableTemplate$class.genericBuilder(this);
   }

   public <A1, A2> Tuple2<Iterable<A1>, Iterable<A2>> unzip(Function1<A, Tuple2<A1, A2>> asPair) {
      return GenericTraversableTemplate$class.unzip(this, asPair);
   }

   public <A1, A2, A3> Tuple3<Iterable<A1>, Iterable<A2>, Iterable<A3>> unzip3(Function1<A, Tuple3<A1, A2, A3>> asTriple) {
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

   public Combiner<A, ParIterable<A>> parCombiner() {
      return TraversableLike$class.parCombiner(this);
   }

   public boolean hasDefiniteSize() {
      return TraversableLike$class.hasDefiniteSize(this);
   }

   public <B, That> That $plus$plus(GenTraversableOnce<B> that, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus(this, that, bf);
   }

   public <B, That> That $plus$plus$colon(TraversableOnce<B> that, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (TraversableOnce)that, bf);
   }

   public <B, That> That $plus$plus$colon(Traversable<B> that, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (Traversable)that, bf);
   }

   public <B, That> That map(Function1<A, B> f, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.map(this, f, bf);
   }

   public <B, That> That flatMap(Function1<A, GenTraversableOnce<B>> f, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.flatMap(this, f, bf);
   }

   public Object filter(Function1 p) {
      return TraversableLike$class.filter(this, p);
   }

   public Object filterNot(Function1 p) {
      return TraversableLike$class.filterNot(this, p);
   }

   public <B, That> That collect(PartialFunction<A, B> pf, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.collect(this, pf, bf);
   }

   public Tuple2<Iterable<A>, Iterable<A>> partition(Function1<A, Object> p) {
      return TraversableLike$class.partition(this, p);
   }

   public <K> Map<K, Iterable<A>> groupBy(Function1<A, K> f) {
      return TraversableLike$class.groupBy(this, f);
   }

   public <B, That> That scan(B z, Function2<B, B, B> op, CanBuildFrom<Iterable<A>, B, That> cbf) {
      return (That)TraversableLike$class.scan(this, z, op, cbf);
   }

   public <B, That> That scanLeft(B z, Function2<B, A, B> op, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.scanLeft(this, z, op, bf);
   }

   public <B, That> That scanRight(B z, Function2<A, B, B> op, CanBuildFrom<Iterable<A>, B, That> bf) {
      return (That)TraversableLike$class.scanRight(this, z, op, bf);
   }

   public Option<A> headOption() {
      return TraversableLike$class.headOption(this);
   }

   public Object tail() {
      return TraversableLike$class.tail(this);
   }

   public A last() {
      return (A)TraversableLike$class.last(this);
   }

   public Option<A> lastOption() {
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

   public Tuple2<Iterable<A>, Iterable<A>> span(Function1<A, Object> p) {
      return TraversableLike$class.span(this, p);
   }

   public Tuple2<Iterable<A>, Iterable<A>> splitAt(int n) {
      return TraversableLike$class.splitAt(this, n);
   }

   public Iterator<Iterable<A>> tails() {
      return TraversableLike$class.tails(this);
   }

   public Iterator<Iterable<A>> inits() {
      return TraversableLike$class.inits(this);
   }

   public Traversable<A> toTraversable() {
      return TraversableLike$class.toTraversable(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, A, Col> cbf) {
      return (Col)TraversableLike$class.to(this, cbf);
   }

   public String toString() {
      return TraversableLike$class.toString(this);
   }

   public String stringPrefix() {
      return TraversableLike$class.stringPrefix(this);
   }

   public FilterMonadic<A, Iterable<A>> withFilter(Function1<A, Object> p) {
      return TraversableLike$class.withFilter(this, p);
   }

   public Parallel par() {
      return Parallelizable$class.par(this);
   }

   public List<A> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<A, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<A, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, A, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<A, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, A, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, A, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, A, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<A, B, B> op) {
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

   public <B> B aggregate(Function0<B> z, Function2<B, A, B> seqop, Function2<B, B, B> combop) {
      return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
   }

   public <B> B sum(Numeric<B> num) {
      return (B)TraversableOnce$class.sum(this, num);
   }

   public <B> B product(Numeric<B> num) {
      return (B)TraversableOnce$class.product(this, num);
   }

   public <B> A min(Ordering<B> cmp) {
      return (A)TraversableOnce$class.min(this, cmp);
   }

   public <B> A max(Ordering<B> cmp) {
      return (A)TraversableOnce$class.max(this, cmp);
   }

   public <B> A maxBy(Function1<A, B> f, Ordering<B> cmp) {
      return (A)TraversableOnce$class.maxBy(this, f, cmp);
   }

   public <B> A minBy(Function1<A, B> f, Ordering<B> cmp) {
      return (A)TraversableOnce$class.minBy(this, f, cmp);
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

   public List<A> toList() {
      return TraversableOnce$class.toList(this);
   }

   public Seq<A> toSeq() {
      return TraversableOnce$class.toSeq(this);
   }

   public IndexedSeq<A> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<A> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<A, Tuple2<T, U>> ev) {
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

   private PriorityQueue<A> underlying() {
      return this.underlying;
   }

   public Iterator<A> iterator() {
      return (Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.underlying().iterator()).asScala();
   }

   public int size() {
      return this.underlying().size();
   }

   public BoundedPriorityQueue<A> $plus$plus$eq(TraversableOnce<A> xs) {
      xs.foreach(new scala.Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BoundedPriorityQueue $outer;

         public final BoundedPriorityQueue<A> apply(A x$1) {
            return this.$outer.$plus$eq(x$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return this;
   }

   public BoundedPriorityQueue<A> $plus$eq(A elem) {
      if (this.size() < this.maxSize) {
         this.underlying().offer(elem);
      } else {
         this.maybeReplaceLowest(elem);
      }

      return this;
   }

   public A poll() {
      return (A)this.underlying().poll();
   }

   public BoundedPriorityQueue<A> $plus$eq(A elem1, A elem2, Seq<A> elems) {
      return this.$plus$eq(elem1).$plus$eq(elem2).$plus$plus$eq(elems);
   }

   public void clear() {
      this.underlying().clear();
   }

   private boolean maybeReplaceLowest(A a) {
      Object head = this.underlying().peek();
      boolean var10000;
      if (head != null && this.ord.gt(a, head)) {
         this.underlying().poll();
         var10000 = this.underlying().offer(a);
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public BoundedPriorityQueue(int maxSize, Ordering<A> ord) {
      this.maxSize = maxSize;
      this.ord = ord;
      TraversableOnce$class.$init$(this);
      Parallelizable$class.$init$(this);
      TraversableLike$class.$init$(this);
      GenericTraversableTemplate$class.$init$(this);
      GenTraversable$class.$init$(this);
      Traversable$class.$init$(this);
      GenIterable$class.$init$(this);
      IterableLike$class.$init$(this);
      Iterable$class.$init$(this);
      Growable$class.$init$(this);
      this.underlying = new PriorityQueue(maxSize, ord);
   }
}
