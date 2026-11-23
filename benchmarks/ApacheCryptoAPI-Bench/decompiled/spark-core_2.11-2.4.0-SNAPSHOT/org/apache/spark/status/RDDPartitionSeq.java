package org.apache.spark.status;

import java.util.NoSuchElementException;
import org.apache.spark.status.api.v1.RDDPartitionInfo;
import scala.Function0;
import scala.Function1;
import scala.Function1$class;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.PartialFunction$class;
import scala.Predef;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.BufferedIterator;
import scala.collection.GenIterable;
import scala.collection.GenIterable$class;
import scala.collection.GenSeq;
import scala.collection.GenSeq$class;
import scala.collection.GenSeqLike$class;
import scala.collection.GenTraversable;
import scala.collection.GenTraversable$class;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterable$class;
import scala.collection.IterableLike$class;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Parallel;
import scala.collection.Parallelizable$class;
import scala.collection.Seq;
import scala.collection.Seq$class;
import scala.collection.SeqLike$class;
import scala.collection.SeqView;
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
import scala.collection.immutable.Range;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Builder;
import scala.collection.mutable.StringBuilder;
import scala.collection.parallel.Combiner;
import scala.collection.parallel.ParSeq;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001-4A!\u0001\u0002\u0005\u0017\ty!\u000b\u0012#QCJ$\u0018\u000e^5p]N+\u0017O\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\r\u0001AB\u0005\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0007MYbD\u0004\u0002\u001539\u0011Q\u0003G\u0007\u0002-)\u0011qCC\u0001\u0007yI|w\u000e\u001e \n\u0003=I!A\u0007\b\u0002\u000fA\f7m[1hK&\u0011A$\b\u0002\u0004'\u0016\f(B\u0001\u000e\u000f!\tyB%D\u0001!\u0015\t\t#%\u0001\u0002wc)\u00111EA\u0001\u0004CBL\u0017BA\u0013!\u0005A\u0011F\t\u0012)beRLG/[8o\u0013:4w\u000eC\u0003(\u0001\u0011\u0005\u0001&\u0001\u0004=S:LGO\u0010\u000b\u0002SA\u0011!\u0006A\u0007\u0002\u0005!9A\u0006\u0001a\u0001\n\u0013i\u0013!B0iK\u0006$W#\u0001\u0018\u0011\u0005)z\u0013B\u0001\u0019\u0003\u0005Aa\u0015N^3S\t\u0012\u0003\u0016M\u001d;ji&|g\u000eC\u00043\u0001\u0001\u0007I\u0011B\u001a\u0002\u0013}CW-\u00193`I\u0015\fHC\u0001\u001b8!\tiQ'\u0003\u00027\u001d\t!QK\\5u\u0011\u001dA\u0014'!AA\u00029\n1\u0001\u001f\u00132\u0011\u0019Q\u0004\u0001)Q\u0005]\u00051q\f[3bI\u0002B#!\u000f\u001f\u0011\u00055i\u0014B\u0001 \u000f\u0005!1x\u000e\\1uS2,\u0007b\u0002!\u0001\u0001\u0004%I!L\u0001\u0006?R\f\u0017\u000e\u001c\u0005\b\u0005\u0002\u0001\r\u0011\"\u0003D\u0003%yF/Y5m?\u0012*\u0017\u000f\u0006\u00025\t\"9\u0001(QA\u0001\u0002\u0004q\u0003B\u0002$\u0001A\u0003&a&\u0001\u0004`i\u0006LG\u000e\t\u0015\u0003\u000brBq!\u0013\u0001A\u0002\u0013\u0005!*A\u0003d_VtG/F\u0001L!\tiA*\u0003\u0002N\u001d\t\u0019\u0011J\u001c;\t\u000f=\u0003\u0001\u0019!C\u0001!\u0006I1m\\;oi~#S-\u001d\u000b\u0003iECq\u0001\u000f(\u0002\u0002\u0003\u00071\n\u0003\u0004T\u0001\u0001\u0006KaS\u0001\u0007G>,h\u000e\u001e\u0011)\u0005Ic\u0004\"\u0002,\u0001\t\u0003:\u0016!B1qa2LHC\u0001\u0010Y\u0011\u0015IV\u000b1\u0001L\u0003\rIG\r\u001f\u0005\u00067\u0002!\t\u0005X\u0001\tSR,'/\u0019;peV\tQ\fE\u0002\u0014=zI!aX\u000f\u0003\u0011%#XM]1u_JDQ!\u0019\u0001\u0005B)\u000ba\u0001\\3oORD\u0007\"B2\u0001\t\u0003!\u0017\u0001D1eIB\u000b'\u000f^5uS>tGC\u0001\u001bf\u0011\u00151'\r1\u0001/\u0003\u0011\u0001\u0018M\u001d;\t\u000b!\u0004A\u0011A5\u0002\u001fI,Wn\u001c<f!\u0006\u0014H/\u001b;j_:$\"\u0001\u000e6\t\u000b\u0019<\u0007\u0019\u0001\u0018"
)
public class RDDPartitionSeq implements Seq<RDDPartitionInfo> {
   private volatile LiveRDDPartition org$apache$spark$status$RDDPartitionSeq$$_head;
   private volatile LiveRDDPartition _tail;
   private volatile int count;

   public GenericCompanion<Seq> companion() {
      return Seq$class.companion(this);
   }

   public Seq<RDDPartitionInfo> seq() {
      return Seq$class.seq(this);
   }

   public Seq<RDDPartitionInfo> thisCollection() {
      return SeqLike$class.thisCollection(this);
   }

   public Seq toCollection(Object repr) {
      return SeqLike$class.toCollection(this, repr);
   }

   public Combiner<RDDPartitionInfo, ParSeq<RDDPartitionInfo>> parCombiner() {
      return SeqLike$class.parCombiner(this);
   }

   public int lengthCompare(int len) {
      return SeqLike$class.lengthCompare(this, len);
   }

   public boolean isEmpty() {
      return SeqLike$class.isEmpty(this);
   }

   public int size() {
      return SeqLike$class.size(this);
   }

   public int segmentLength(Function1<RDDPartitionInfo, Object> p, int from) {
      return SeqLike$class.segmentLength(this, p, from);
   }

   public int indexWhere(Function1<RDDPartitionInfo, Object> p, int from) {
      return SeqLike$class.indexWhere(this, p, from);
   }

   public int lastIndexWhere(Function1<RDDPartitionInfo, Object> p, int end) {
      return SeqLike$class.lastIndexWhere(this, p, end);
   }

   public Iterator<Seq<RDDPartitionInfo>> permutations() {
      return SeqLike$class.permutations(this);
   }

   public Iterator<Seq<RDDPartitionInfo>> combinations(int n) {
      return SeqLike$class.combinations(this, n);
   }

   public Object reverse() {
      return SeqLike$class.reverse(this);
   }

   public <B, That> That reverseMap(Function1<RDDPartitionInfo, B> f, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)SeqLike$class.reverseMap(this, f, bf);
   }

   public Iterator<RDDPartitionInfo> reverseIterator() {
      return SeqLike$class.reverseIterator(this);
   }

   public <B> boolean startsWith(GenSeq<B> that, int offset) {
      return SeqLike$class.startsWith(this, that, offset);
   }

   public <B> boolean endsWith(GenSeq<B> that) {
      return SeqLike$class.endsWith(this, that);
   }

   public <B> int indexOfSlice(GenSeq<B> that) {
      return SeqLike$class.indexOfSlice(this, that);
   }

   public <B> int indexOfSlice(GenSeq<B> that, int from) {
      return SeqLike$class.indexOfSlice(this, that, from);
   }

   public <B> int lastIndexOfSlice(GenSeq<B> that) {
      return SeqLike$class.lastIndexOfSlice(this, that);
   }

   public <B> int lastIndexOfSlice(GenSeq<B> that, int end) {
      return SeqLike$class.lastIndexOfSlice(this, that, end);
   }

   public <B> boolean containsSlice(GenSeq<B> that) {
      return SeqLike$class.containsSlice(this, that);
   }

   public <A1> boolean contains(A1 elem) {
      return SeqLike$class.contains(this, elem);
   }

   public <B, That> That union(GenSeq<B> that, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)SeqLike$class.union(this, that, bf);
   }

   public Object diff(GenSeq that) {
      return SeqLike$class.diff(this, that);
   }

   public Object intersect(GenSeq that) {
      return SeqLike$class.intersect(this, that);
   }

   public Object distinct() {
      return SeqLike$class.distinct(this);
   }

   public <B, That> That patch(int from, GenSeq<B> patch, int replaced, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)SeqLike$class.patch(this, from, patch, replaced, bf);
   }

   public <B, That> That updated(int index, B elem, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)SeqLike$class.updated(this, index, elem, bf);
   }

   public <B, That> That $plus$colon(B elem, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)SeqLike$class.$plus$colon(this, elem, bf);
   }

   public <B, That> That $colon$plus(B elem, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)SeqLike$class.$colon$plus(this, elem, bf);
   }

   public <B, That> That padTo(int len, B elem, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)SeqLike$class.padTo(this, len, elem, bf);
   }

   public <B> boolean corresponds(GenSeq<B> that, Function2<RDDPartitionInfo, B, Object> p) {
      return SeqLike$class.corresponds(this, that, p);
   }

   public Object sortWith(Function2 lt) {
      return SeqLike$class.sortWith(this, lt);
   }

   public Object sortBy(Function1 f, Ordering ord) {
      return SeqLike$class.sortBy(this, f, ord);
   }

   public Object sorted(Ordering ord) {
      return SeqLike$class.sorted(this, ord);
   }

   public Seq<RDDPartitionInfo> toSeq() {
      return SeqLike$class.toSeq(this);
   }

   public Range indices() {
      return SeqLike$class.indices(this);
   }

   public Object view() {
      return SeqLike$class.view(this);
   }

   public SeqView<RDDPartitionInfo, Seq<RDDPartitionInfo>> view(int from, int until) {
      return SeqLike$class.view(this, from, until);
   }

   public String toString() {
      return SeqLike$class.toString(this);
   }

   public boolean isDefinedAt(int idx) {
      return GenSeqLike$class.isDefinedAt(this, idx);
   }

   public int prefixLength(Function1<RDDPartitionInfo, Object> p) {
      return GenSeqLike$class.prefixLength(this, p);
   }

   public int indexWhere(Function1<RDDPartitionInfo, Object> p) {
      return GenSeqLike$class.indexWhere(this, p);
   }

   public <B> int indexOf(B elem) {
      return GenSeqLike$class.indexOf(this, elem);
   }

   public <B> int indexOf(B elem, int from) {
      return GenSeqLike$class.indexOf(this, elem, from);
   }

   public <B> int lastIndexOf(B elem) {
      return GenSeqLike$class.lastIndexOf(this, elem);
   }

   public <B> int lastIndexOf(B elem, int end) {
      return GenSeqLike$class.lastIndexOf(this, elem, end);
   }

   public int lastIndexWhere(Function1<RDDPartitionInfo, Object> p) {
      return GenSeqLike$class.lastIndexWhere(this, p);
   }

   public <B> boolean startsWith(GenSeq<B> that) {
      return GenSeqLike$class.startsWith(this, that);
   }

   public int hashCode() {
      return GenSeqLike$class.hashCode(this);
   }

   public boolean equals(Object that) {
      return GenSeqLike$class.equals(this, that);
   }

   public <U> void foreach(Function1<RDDPartitionInfo, U> f) {
      IterableLike$class.foreach(this, f);
   }

   public boolean forall(Function1<RDDPartitionInfo, Object> p) {
      return IterableLike$class.forall(this, p);
   }

   public boolean exists(Function1<RDDPartitionInfo, Object> p) {
      return IterableLike$class.exists(this, p);
   }

   public Option<RDDPartitionInfo> find(Function1<RDDPartitionInfo, Object> p) {
      return IterableLike$class.find(this, p);
   }

   public <B> B foldRight(B z, Function2<RDDPartitionInfo, B, B> op) {
      return (B)IterableLike$class.foldRight(this, z, op);
   }

   public <B> B reduceRight(Function2<RDDPartitionInfo, B, B> op) {
      return (B)IterableLike$class.reduceRight(this, op);
   }

   public Iterable<RDDPartitionInfo> toIterable() {
      return IterableLike$class.toIterable(this);
   }

   public Iterator<RDDPartitionInfo> toIterator() {
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

   public Iterator<Seq<RDDPartitionInfo>> grouped(int size) {
      return IterableLike$class.grouped(this, size);
   }

   public Iterator<Seq<RDDPartitionInfo>> sliding(int size) {
      return IterableLike$class.sliding(this, size);
   }

   public Iterator<Seq<RDDPartitionInfo>> sliding(int size, int step) {
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

   public <A1, B, That> That zip(GenIterable<B> that, CanBuildFrom<Seq<RDDPartitionInfo>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zip(this, that, bf);
   }

   public <B, A1, That> That zipAll(GenIterable<B> that, A1 thisElem, B thatElem, CanBuildFrom<Seq<RDDPartitionInfo>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zipAll(this, that, thisElem, thatElem, bf);
   }

   public <A1, That> That zipWithIndex(CanBuildFrom<Seq<RDDPartitionInfo>, Tuple2<A1, Object>, That> bf) {
      return (That)IterableLike$class.zipWithIndex(this, bf);
   }

   public <B> boolean sameElements(GenIterable<B> that) {
      return IterableLike$class.sameElements(this, that);
   }

   public Stream<RDDPartitionInfo> toStream() {
      return IterableLike$class.toStream(this);
   }

   public boolean canEqual(Object that) {
      return IterableLike$class.canEqual(this, that);
   }

   public Builder<RDDPartitionInfo, Seq<RDDPartitionInfo>> newBuilder() {
      return GenericTraversableTemplate$class.newBuilder(this);
   }

   public <B> Builder<B, Seq<B>> genericBuilder() {
      return GenericTraversableTemplate$class.genericBuilder(this);
   }

   public <A1, A2> Tuple2<Seq<A1>, Seq<A2>> unzip(Function1<RDDPartitionInfo, Tuple2<A1, A2>> asPair) {
      return GenericTraversableTemplate$class.unzip(this, asPair);
   }

   public <A1, A2, A3> Tuple3<Seq<A1>, Seq<A2>, Seq<A3>> unzip3(Function1<RDDPartitionInfo, Tuple3<A1, A2, A3>> asTriple) {
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

   public boolean hasDefiniteSize() {
      return TraversableLike$class.hasDefiniteSize(this);
   }

   public <B, That> That $plus$plus(GenTraversableOnce<B> that, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus(this, that, bf);
   }

   public <B, That> That $plus$plus$colon(TraversableOnce<B> that, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (TraversableOnce)that, bf);
   }

   public <B, That> That $plus$plus$colon(Traversable<B> that, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (Traversable)that, bf);
   }

   public <B, That> That map(Function1<RDDPartitionInfo, B> f, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.map(this, f, bf);
   }

   public <B, That> That flatMap(Function1<RDDPartitionInfo, GenTraversableOnce<B>> f, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.flatMap(this, f, bf);
   }

   public Object filter(Function1 p) {
      return TraversableLike$class.filter(this, p);
   }

   public Object filterNot(Function1 p) {
      return TraversableLike$class.filterNot(this, p);
   }

   public <B, That> That collect(PartialFunction<RDDPartitionInfo, B> pf, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.collect(this, pf, bf);
   }

   public Tuple2<Seq<RDDPartitionInfo>, Seq<RDDPartitionInfo>> partition(Function1<RDDPartitionInfo, Object> p) {
      return TraversableLike$class.partition(this, p);
   }

   public <K> Map<K, Seq<RDDPartitionInfo>> groupBy(Function1<RDDPartitionInfo, K> f) {
      return TraversableLike$class.groupBy(this, f);
   }

   public <B, That> That scan(B z, Function2<B, B, B> op, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> cbf) {
      return (That)TraversableLike$class.scan(this, z, op, cbf);
   }

   public <B, That> That scanLeft(B z, Function2<B, RDDPartitionInfo, B> op, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.scanLeft(this, z, op, bf);
   }

   public <B, That> That scanRight(B z, Function2<RDDPartitionInfo, B, B> op, CanBuildFrom<Seq<RDDPartitionInfo>, B, That> bf) {
      return (That)TraversableLike$class.scanRight(this, z, op, bf);
   }

   public Option<RDDPartitionInfo> headOption() {
      return TraversableLike$class.headOption(this);
   }

   public Object tail() {
      return TraversableLike$class.tail(this);
   }

   public Object last() {
      return TraversableLike$class.last(this);
   }

   public Option<RDDPartitionInfo> lastOption() {
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

   public Tuple2<Seq<RDDPartitionInfo>, Seq<RDDPartitionInfo>> span(Function1<RDDPartitionInfo, Object> p) {
      return TraversableLike$class.span(this, p);
   }

   public Tuple2<Seq<RDDPartitionInfo>, Seq<RDDPartitionInfo>> splitAt(int n) {
      return TraversableLike$class.splitAt(this, n);
   }

   public Iterator<Seq<RDDPartitionInfo>> tails() {
      return TraversableLike$class.tails(this);
   }

   public Iterator<Seq<RDDPartitionInfo>> inits() {
      return TraversableLike$class.inits(this);
   }

   public Traversable<RDDPartitionInfo> toTraversable() {
      return TraversableLike$class.toTraversable(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, RDDPartitionInfo, Col> cbf) {
      return (Col)TraversableLike$class.to(this, cbf);
   }

   public String stringPrefix() {
      return TraversableLike$class.stringPrefix(this);
   }

   public FilterMonadic<RDDPartitionInfo, Seq<RDDPartitionInfo>> withFilter(Function1<RDDPartitionInfo, Object> p) {
      return TraversableLike$class.withFilter(this, p);
   }

   public Parallel par() {
      return Parallelizable$class.par(this);
   }

   public List<RDDPartitionInfo> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<RDDPartitionInfo, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<RDDPartitionInfo, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, RDDPartitionInfo, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<RDDPartitionInfo, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, RDDPartitionInfo, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, RDDPartitionInfo, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, RDDPartitionInfo, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<RDDPartitionInfo, B, B> op) {
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

   public <B> B aggregate(Function0<B> z, Function2<B, RDDPartitionInfo, B> seqop, Function2<B, B, B> combop) {
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

   public List<RDDPartitionInfo> toList() {
      return TraversableOnce$class.toList(this);
   }

   public IndexedSeq<RDDPartitionInfo> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<RDDPartitionInfo> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<RDDPartitionInfo, Tuple2<T, U>> ev) {
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

   public <A1, B1> PartialFunction<A1, B1> orElse(PartialFunction<A1, B1> that) {
      return PartialFunction$class.orElse(this, that);
   }

   public <C> PartialFunction<Object, C> andThen(Function1<RDDPartitionInfo, C> k) {
      return PartialFunction$class.andThen(this, k);
   }

   public Function1<Object, Option<RDDPartitionInfo>> lift() {
      return PartialFunction$class.lift(this);
   }

   public Object applyOrElse(Object x, Function1 var2) {
      return PartialFunction$class.applyOrElse(this, x, var2);
   }

   public <U> Function1<Object, Object> runWith(Function1<RDDPartitionInfo, U> action) {
      return PartialFunction$class.runWith(this, action);
   }

   public boolean apply$mcZD$sp(double v1) {
      return Function1$class.apply$mcZD$sp(this, v1);
   }

   public double apply$mcDD$sp(double v1) {
      return Function1$class.apply$mcDD$sp(this, v1);
   }

   public float apply$mcFD$sp(double v1) {
      return Function1$class.apply$mcFD$sp(this, v1);
   }

   public int apply$mcID$sp(double v1) {
      return Function1$class.apply$mcID$sp(this, v1);
   }

   public long apply$mcJD$sp(double v1) {
      return Function1$class.apply$mcJD$sp(this, v1);
   }

   public void apply$mcVD$sp(double v1) {
      Function1$class.apply$mcVD$sp(this, v1);
   }

   public boolean apply$mcZF$sp(float v1) {
      return Function1$class.apply$mcZF$sp(this, v1);
   }

   public double apply$mcDF$sp(float v1) {
      return Function1$class.apply$mcDF$sp(this, v1);
   }

   public float apply$mcFF$sp(float v1) {
      return Function1$class.apply$mcFF$sp(this, v1);
   }

   public int apply$mcIF$sp(float v1) {
      return Function1$class.apply$mcIF$sp(this, v1);
   }

   public long apply$mcJF$sp(float v1) {
      return Function1$class.apply$mcJF$sp(this, v1);
   }

   public void apply$mcVF$sp(float v1) {
      Function1$class.apply$mcVF$sp(this, v1);
   }

   public boolean apply$mcZI$sp(int v1) {
      return Function1$class.apply$mcZI$sp(this, v1);
   }

   public double apply$mcDI$sp(int v1) {
      return Function1$class.apply$mcDI$sp(this, v1);
   }

   public float apply$mcFI$sp(int v1) {
      return Function1$class.apply$mcFI$sp(this, v1);
   }

   public int apply$mcII$sp(int v1) {
      return Function1$class.apply$mcII$sp(this, v1);
   }

   public long apply$mcJI$sp(int v1) {
      return Function1$class.apply$mcJI$sp(this, v1);
   }

   public void apply$mcVI$sp(int v1) {
      Function1$class.apply$mcVI$sp(this, v1);
   }

   public boolean apply$mcZJ$sp(long v1) {
      return Function1$class.apply$mcZJ$sp(this, v1);
   }

   public double apply$mcDJ$sp(long v1) {
      return Function1$class.apply$mcDJ$sp(this, v1);
   }

   public float apply$mcFJ$sp(long v1) {
      return Function1$class.apply$mcFJ$sp(this, v1);
   }

   public int apply$mcIJ$sp(long v1) {
      return Function1$class.apply$mcIJ$sp(this, v1);
   }

   public long apply$mcJJ$sp(long v1) {
      return Function1$class.apply$mcJJ$sp(this, v1);
   }

   public void apply$mcVJ$sp(long v1) {
      Function1$class.apply$mcVJ$sp(this, v1);
   }

   public <A> Function1<A, RDDPartitionInfo> compose(Function1<A, Object> g) {
      return Function1$class.compose(this, g);
   }

   public LiveRDDPartition org$apache$spark$status$RDDPartitionSeq$$_head() {
      return this.org$apache$spark$status$RDDPartitionSeq$$_head;
   }

   private void org$apache$spark$status$RDDPartitionSeq$$_head_$eq(LiveRDDPartition x$1) {
      this.org$apache$spark$status$RDDPartitionSeq$$_head = x$1;
   }

   private LiveRDDPartition _tail() {
      return this._tail;
   }

   private void _tail_$eq(LiveRDDPartition x$1) {
      this._tail = x$1;
   }

   public int count() {
      return this.count;
   }

   public void count_$eq(int x$1) {
      this.count = x$1;
   }

   public RDDPartitionInfo apply(int idx) {
      int curr = 0;

      LiveRDDPartition e;
      for(e = this.org$apache$spark$status$RDDPartitionSeq$$_head(); curr < idx && e != null; e = e.next()) {
         ++curr;
      }

      if (e == null) {
         throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(idx).toString());
      } else {
         return e.value();
      }
   }

   public Iterator<RDDPartitionInfo> iterator() {
      return new Iterator<RDDPartitionInfo>(this) {
         private LiveRDDPartition current;

         public Iterator<RDDPartitionInfo> seq() {
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

         public Iterator<RDDPartitionInfo> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<RDDPartitionInfo> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<RDDPartitionInfo> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<RDDPartitionInfo, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<RDDPartitionInfo, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<RDDPartitionInfo> filter(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<RDDPartitionInfo, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<RDDPartitionInfo> withFilter(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<RDDPartitionInfo> filterNot(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<RDDPartitionInfo, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, RDDPartitionInfo, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<RDDPartitionInfo, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<RDDPartitionInfo> takeWhile(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<RDDPartitionInfo>, Iterator<RDDPartitionInfo>> partition(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<RDDPartitionInfo>, Iterator<RDDPartitionInfo>> span(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<RDDPartitionInfo> dropWhile(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<RDDPartitionInfo, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<RDDPartitionInfo, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<RDDPartitionInfo, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<RDDPartitionInfo> find(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<RDDPartitionInfo, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<RDDPartitionInfo> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<RDDPartitionInfo>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<RDDPartitionInfo>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<RDDPartitionInfo>, Iterator<RDDPartitionInfo>> duplicate() {
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

         public Traversable<RDDPartitionInfo> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<RDDPartitionInfo> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<RDDPartitionInfo> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<RDDPartitionInfo> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<RDDPartitionInfo, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<RDDPartitionInfo, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, RDDPartitionInfo, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<RDDPartitionInfo, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, RDDPartitionInfo, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<RDDPartitionInfo, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, RDDPartitionInfo, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<RDDPartitionInfo, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, RDDPartitionInfo, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<RDDPartitionInfo, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, RDDPartitionInfo, B> seqop, Function2<B, B, B> combop) {
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

         public List<RDDPartitionInfo> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<RDDPartitionInfo> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<RDDPartitionInfo> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<RDDPartitionInfo> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<RDDPartitionInfo> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, RDDPartitionInfo, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<RDDPartitionInfo, Tuple2<T, U>> ev) {
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

         private LiveRDDPartition current() {
            return this.current;
         }

         private void current_$eq(LiveRDDPartition x$1) {
            this.current = x$1;
         }

         public boolean hasNext() {
            return this.current() != null;
         }

         public RDDPartitionInfo next() {
            if (this.current() == null) {
               throw new NoSuchElementException();
            } else {
               LiveRDDPartition tmp = this.current();
               this.current_$eq(tmp.next());
               return tmp.value();
            }
         }

         public {
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            this.current = $outer.org$apache$spark$status$RDDPartitionSeq$$_head();
         }
      };
   }

   public int length() {
      return this.count();
   }

   public void addPartition(LiveRDDPartition part) {
      part.prev_$eq(this._tail());
      if (this._tail() != null) {
         this._tail().next_$eq(part);
      }

      if (this.org$apache$spark$status$RDDPartitionSeq$$_head() == null) {
         this.org$apache$spark$status$RDDPartitionSeq$$_head_$eq(part);
      }

      this._tail_$eq(part);
      this.count_$eq(this.count() + 1);
   }

   public void removePartition(LiveRDDPartition part) {
      this.count_$eq(this.count() - 1);
      if (part.prev() != null) {
         part.prev().next_$eq(part.next());
      }

      if (part == this.org$apache$spark$status$RDDPartitionSeq$$_head()) {
         this.org$apache$spark$status$RDDPartitionSeq$$_head_$eq(part.next());
      }

      if (part.next() != null) {
         part.next().prev_$eq(part.prev());
      }

      if (part == this._tail()) {
         this._tail_$eq(part.prev());
      }

   }

   public RDDPartitionSeq() {
      Function1$class.$init$(this);
      PartialFunction$class.$init$(this);
      TraversableOnce$class.$init$(this);
      Parallelizable$class.$init$(this);
      TraversableLike$class.$init$(this);
      GenericTraversableTemplate$class.$init$(this);
      GenTraversable$class.$init$(this);
      Traversable$class.$init$(this);
      GenIterable$class.$init$(this);
      IterableLike$class.$init$(this);
      Iterable$class.$init$(this);
      GenSeqLike$class.$init$(this);
      GenSeq$class.$init$(this);
      SeqLike$class.$init$(this);
      Seq$class.$init$(this);
      this.org$apache$spark$status$RDDPartitionSeq$$_head = null;
      this._tail = null;
      this.count = 0;
   }
}
