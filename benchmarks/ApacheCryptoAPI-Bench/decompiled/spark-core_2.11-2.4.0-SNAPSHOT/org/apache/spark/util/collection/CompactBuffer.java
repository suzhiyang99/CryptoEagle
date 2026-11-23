package org.apache.spark.util.collection;

import java.util.NoSuchElementException;
import org.apache.spark.unsafe.array.ByteArrayMethods;
import scala.Function0;
import scala.Function1;
import scala.Function1$class;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.PartialFunction$class;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
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
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ed!B\u0001\u0003\u0001\u0019a!!D\"p[B\f7\r\u001e\"vM\u001a,'O\u0003\u0002\u0004\t\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\u000b\u0005\u00151\u0011\u0001B;uS2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u000b\u0003\u001b\r\u001aB\u0001\u0001\b\u0015YA\u0011qBE\u0007\u0002!)\t\u0011#A\u0003tG\u0006d\u0017-\u0003\u0002\u0014!\t1\u0011I\\=SK\u001a\u00042!\u0006\u0010\"\u001d\t1BD\u0004\u0002\u001875\t\u0001D\u0003\u0002\u001a5\u00051AH]8piz\u001a\u0001!C\u0001\u0012\u0013\ti\u0002#A\u0004qC\u000e\\\u0017mZ3\n\u0005}\u0001#aA*fc*\u0011Q\u0004\u0005\t\u0003E\rb\u0001\u0001B\u0003%\u0001\t\u0007QEA\u0001U#\t1\u0013\u0006\u0005\u0002\u0010O%\u0011\u0001\u0006\u0005\u0002\b\u001d>$\b.\u001b8h!\ty!&\u0003\u0002,!\t\u0019\u0011I\\=\u0011\u0005=i\u0013B\u0001\u0018\u0011\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!\u0001\u0004AaA!\u0002\u0017\t\u0014AC3wS\u0012,gnY3%cA\u0019!'N\u0011\u000e\u0003MR!\u0001\u000e\t\u0002\u000fI,g\r\\3di&\u0011ag\r\u0002\t\u00072\f7o\u001d+bO\")\u0001\b\u0001C\u0001s\u00051A(\u001b8jiz\"\u0012A\u000f\u000b\u0003wu\u00022\u0001\u0010\u0001\"\u001b\u0005\u0011\u0001\"\u0002\u00198\u0001\b\t\u0004\"C \u0001\u0001\u0004\u0005\r\u0011\"\u0003A\u0003!)G.Z7f]R\u0004T#A\u0011\t\u0013\t\u0003\u0001\u0019!a\u0001\n\u0013\u0019\u0015\u0001D3mK6,g\u000e\u001e\u0019`I\u0015\fHC\u0001#H!\tyQ)\u0003\u0002G!\t!QK\\5u\u0011\u001dA\u0015)!AA\u0002\u0005\n1\u0001\u001f\u00132\u0011\u0019Q\u0005\u0001)Q\u0005C\u0005IQ\r\\3nK:$\b\u0007\t\u0005\n\u0019\u0002\u0001\r\u00111A\u0005\n\u0001\u000b\u0001\"\u001a7f[\u0016tG/\r\u0005\n\u001d\u0002\u0001\r\u00111A\u0005\n=\u000bA\"\u001a7f[\u0016tG/M0%KF$\"\u0001\u0012)\t\u000f!k\u0015\u0011!a\u0001C!1!\u000b\u0001Q!\n\u0005\n\u0011\"\u001a7f[\u0016tG/\r\u0011\t\u000fQ\u0003\u0001\u0019!C\u0005+\u000691-\u001e:TSj,W#\u0001,\u0011\u0005=9\u0016B\u0001-\u0011\u0005\rIe\u000e\u001e\u0005\b5\u0002\u0001\r\u0011\"\u0003\\\u0003-\u0019WO]*ju\u0016|F%Z9\u0015\u0005\u0011c\u0006b\u0002%Z\u0003\u0003\u0005\rA\u0016\u0005\u0007=\u0002\u0001\u000b\u0015\u0002,\u0002\u0011\r,(oU5{K\u0002Bq\u0001\u0019\u0001A\u0002\u0013%\u0011-A\u0007pi\",'/\u00127f[\u0016tGo]\u000b\u0002EB\u0019qbY\u0011\n\u0005\u0011\u0004\"!B!se\u0006L\bb\u00024\u0001\u0001\u0004%IaZ\u0001\u0012_RDWM]#mK6,g\u000e^:`I\u0015\fHC\u0001#i\u0011\u001dAU-!AA\u0002\tDaA\u001b\u0001!B\u0013\u0011\u0017AD8uQ\u0016\u0014X\t\\3nK:$8\u000f\t\u0005\u0006Y\u0002!\t!\\\u0001\u0006CB\u0004H.\u001f\u000b\u0003C9DQa\\6A\u0002Y\u000b\u0001\u0002]8tSRLwN\u001c\u0005\u0006c\u0002!IA]\u0001\u0007kB$\u0017\r^3\u0015\u0007\u0011\u001bH\u000fC\u0003pa\u0002\u0007a\u000bC\u0003va\u0002\u0007\u0011%A\u0003wC2,X\rC\u0003x\u0001\u0011\u0005\u00010\u0001\u0005%a2,8\u000fJ3r)\tY\u0014\u0010C\u0003vm\u0002\u0007\u0011\u0005C\u0003|\u0001\u0011\u0005A0A\u0007%a2,8\u000f\n9mkN$S-\u001d\u000b\u0003wuDQA >A\u0002}\faA^1mk\u0016\u001c\b\u0003B\u000b\u0002\u0002\u0005J1!a\u0001!\u0005=!&/\u0019<feN\f'\r\\3P]\u000e,\u0007BBA\u0004\u0001\u0011\u0005S+\u0001\u0004mK:<G\u000f\u001b\u0005\u0007\u0003\u0017\u0001A\u0011I+\u0002\tML'0\u001a\u0005\b\u0003\u001f\u0001A\u0011IA\t\u0003!IG/\u001a:bi>\u0014XCAA\n!\u0011)\u0012QC\u0011\n\u0007\u0005]\u0001E\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d\tY\u0002\u0001C\u0005\u0003;\t!b\u001a:poR{7+\u001b>f)\r!\u0015q\u0004\u0005\b\u0003C\tI\u00021\u0001W\u0003\u001dqWm^*ju\u0016<\u0001\"!\n\u0003\u0011\u00031\u0011qE\u0001\u000e\u0007>l\u0007/Y2u\u0005V4g-\u001a:\u0011\u0007q\nICB\u0004\u0002\u0005!\u0005a!a\u000b\u0014\t\u0005%b\u0002\f\u0005\bq\u0005%B\u0011AA\u0018)\t\t9\u0003C\u0004m\u0003S!\t!a\r\u0016\t\u0005U\u0012Q\b\u000b\u0003\u0003o!B!!\u000f\u0002@A!A\bAA\u001e!\r\u0011\u0013Q\b\u0003\u0007I\u0005E\"\u0019A\u0013\t\u0015\u0005\u0005\u0013\u0011GA\u0001\u0002\b\t\u0019%\u0001\u0006fm&$WM\\2fII\u0002BAM\u001b\u0002<!9A.!\u000b\u0005\u0002\u0005\u001dS\u0003BA%\u0003#\"B!a\u0013\u0002ZQ!\u0011QJA*!\u0011a\u0004!a\u0014\u0011\u0007\t\n\t\u0006\u0002\u0004%\u0003\u000b\u0012\r!\n\u0005\u000b\u0003+\n)%!AA\u0004\u0005]\u0013AC3wS\u0012,gnY3%gA!!'NA(\u0011\u001d)\u0018Q\ta\u0001\u0003\u001fB!\"!\u0018\u0002*\u0005\u0005I\u0011BA0\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u0005\u0004\u0003BA2\u0003[j!!!\u001a\u000b\t\u0005\u001d\u0014\u0011N\u0001\u0005Y\u0006twM\u0003\u0002\u0002l\u0005!!.\u0019<b\u0013\u0011\ty'!\u001a\u0003\r=\u0013'.Z2u\u0001"
)
public class CompactBuffer<T> implements Seq<T>, Serializable {
   private final ClassTag<T> evidence$1;
   private T element0;
   private T element1;
   private int org$apache$spark$util$collection$CompactBuffer$$curSize;
   private Object otherElements;

   public GenericCompanion<Seq> companion() {
      return Seq$class.companion(this);
   }

   public Seq<T> seq() {
      return Seq$class.seq(this);
   }

   public Seq<T> thisCollection() {
      return SeqLike$class.thisCollection(this);
   }

   public Seq toCollection(Object repr) {
      return SeqLike$class.toCollection(this, repr);
   }

   public Combiner<T, ParSeq<T>> parCombiner() {
      return SeqLike$class.parCombiner(this);
   }

   public int lengthCompare(int len) {
      return SeqLike$class.lengthCompare(this, len);
   }

   public boolean isEmpty() {
      return SeqLike$class.isEmpty(this);
   }

   public int segmentLength(Function1<T, Object> p, int from) {
      return SeqLike$class.segmentLength(this, p, from);
   }

   public int indexWhere(Function1<T, Object> p, int from) {
      return SeqLike$class.indexWhere(this, p, from);
   }

   public int lastIndexWhere(Function1<T, Object> p, int end) {
      return SeqLike$class.lastIndexWhere(this, p, end);
   }

   public Iterator<Seq<T>> permutations() {
      return SeqLike$class.permutations(this);
   }

   public Iterator<Seq<T>> combinations(int n) {
      return SeqLike$class.combinations(this, n);
   }

   public Object reverse() {
      return SeqLike$class.reverse(this);
   }

   public <B, That> That reverseMap(Function1<T, B> f, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)SeqLike$class.reverseMap(this, f, bf);
   }

   public Iterator<T> reverseIterator() {
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

   public <B, That> That union(GenSeq<B> that, CanBuildFrom<Seq<T>, B, That> bf) {
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

   public <B, That> That patch(int from, GenSeq<B> patch, int replaced, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)SeqLike$class.patch(this, from, patch, replaced, bf);
   }

   public <B, That> That updated(int index, B elem, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)SeqLike$class.updated(this, index, elem, bf);
   }

   public <B, That> That $plus$colon(B elem, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)SeqLike$class.$plus$colon(this, elem, bf);
   }

   public <B, That> That $colon$plus(B elem, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)SeqLike$class.$colon$plus(this, elem, bf);
   }

   public <B, That> That padTo(int len, B elem, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)SeqLike$class.padTo(this, len, elem, bf);
   }

   public <B> boolean corresponds(GenSeq<B> that, Function2<T, B, Object> p) {
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

   public Seq<T> toSeq() {
      return SeqLike$class.toSeq(this);
   }

   public Range indices() {
      return SeqLike$class.indices(this);
   }

   public Object view() {
      return SeqLike$class.view(this);
   }

   public SeqView<T, Seq<T>> view(int from, int until) {
      return SeqLike$class.view(this, from, until);
   }

   public String toString() {
      return SeqLike$class.toString(this);
   }

   public boolean isDefinedAt(int idx) {
      return GenSeqLike$class.isDefinedAt(this, idx);
   }

   public int prefixLength(Function1<T, Object> p) {
      return GenSeqLike$class.prefixLength(this, p);
   }

   public int indexWhere(Function1<T, Object> p) {
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

   public int lastIndexWhere(Function1<T, Object> p) {
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

   public <U> void foreach(Function1<T, U> f) {
      IterableLike$class.foreach(this, f);
   }

   public boolean forall(Function1<T, Object> p) {
      return IterableLike$class.forall(this, p);
   }

   public boolean exists(Function1<T, Object> p) {
      return IterableLike$class.exists(this, p);
   }

   public Option<T> find(Function1<T, Object> p) {
      return IterableLike$class.find(this, p);
   }

   public <B> B foldRight(B z, Function2<T, B, B> op) {
      return (B)IterableLike$class.foldRight(this, z, op);
   }

   public <B> B reduceRight(Function2<T, B, B> op) {
      return (B)IterableLike$class.reduceRight(this, op);
   }

   public Iterable<T> toIterable() {
      return IterableLike$class.toIterable(this);
   }

   public Iterator<T> toIterator() {
      return IterableLike$class.toIterator(this);
   }

   public T head() {
      return (T)IterableLike$class.head(this);
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

   public Iterator<Seq<T>> grouped(int size) {
      return IterableLike$class.grouped(this, size);
   }

   public Iterator<Seq<T>> sliding(int size) {
      return IterableLike$class.sliding(this, size);
   }

   public Iterator<Seq<T>> sliding(int size, int step) {
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

   public <A1, B, That> That zip(GenIterable<B> that, CanBuildFrom<Seq<T>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zip(this, that, bf);
   }

   public <B, A1, That> That zipAll(GenIterable<B> that, A1 thisElem, B thatElem, CanBuildFrom<Seq<T>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zipAll(this, that, thisElem, thatElem, bf);
   }

   public <A1, That> That zipWithIndex(CanBuildFrom<Seq<T>, Tuple2<A1, Object>, That> bf) {
      return (That)IterableLike$class.zipWithIndex(this, bf);
   }

   public <B> boolean sameElements(GenIterable<B> that) {
      return IterableLike$class.sameElements(this, that);
   }

   public Stream<T> toStream() {
      return IterableLike$class.toStream(this);
   }

   public boolean canEqual(Object that) {
      return IterableLike$class.canEqual(this, that);
   }

   public Builder<T, Seq<T>> newBuilder() {
      return GenericTraversableTemplate$class.newBuilder(this);
   }

   public <B> Builder<B, Seq<B>> genericBuilder() {
      return GenericTraversableTemplate$class.genericBuilder(this);
   }

   public <A1, A2> Tuple2<Seq<A1>, Seq<A2>> unzip(Function1<T, Tuple2<A1, A2>> asPair) {
      return GenericTraversableTemplate$class.unzip(this, asPair);
   }

   public <A1, A2, A3> Tuple3<Seq<A1>, Seq<A2>, Seq<A3>> unzip3(Function1<T, Tuple3<A1, A2, A3>> asTriple) {
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

   public <B, That> That $plus$plus(GenTraversableOnce<B> that, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus(this, that, bf);
   }

   public <B, That> That $plus$plus$colon(TraversableOnce<B> that, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (TraversableOnce)that, bf);
   }

   public <B, That> That $plus$plus$colon(Traversable<B> that, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (Traversable)that, bf);
   }

   public <B, That> That map(Function1<T, B> f, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.map(this, f, bf);
   }

   public <B, That> That flatMap(Function1<T, GenTraversableOnce<B>> f, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.flatMap(this, f, bf);
   }

   public Object filter(Function1 p) {
      return TraversableLike$class.filter(this, p);
   }

   public Object filterNot(Function1 p) {
      return TraversableLike$class.filterNot(this, p);
   }

   public <B, That> That collect(PartialFunction<T, B> pf, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.collect(this, pf, bf);
   }

   public Tuple2<Seq<T>, Seq<T>> partition(Function1<T, Object> p) {
      return TraversableLike$class.partition(this, p);
   }

   public <K> Map<K, Seq<T>> groupBy(Function1<T, K> f) {
      return TraversableLike$class.groupBy(this, f);
   }

   public <B, That> That scan(B z, Function2<B, B, B> op, CanBuildFrom<Seq<T>, B, That> cbf) {
      return (That)TraversableLike$class.scan(this, z, op, cbf);
   }

   public <B, That> That scanLeft(B z, Function2<B, T, B> op, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.scanLeft(this, z, op, bf);
   }

   public <B, That> That scanRight(B z, Function2<T, B, B> op, CanBuildFrom<Seq<T>, B, That> bf) {
      return (That)TraversableLike$class.scanRight(this, z, op, bf);
   }

   public Option<T> headOption() {
      return TraversableLike$class.headOption(this);
   }

   public Object tail() {
      return TraversableLike$class.tail(this);
   }

   public T last() {
      return (T)TraversableLike$class.last(this);
   }

   public Option<T> lastOption() {
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

   public Tuple2<Seq<T>, Seq<T>> span(Function1<T, Object> p) {
      return TraversableLike$class.span(this, p);
   }

   public Tuple2<Seq<T>, Seq<T>> splitAt(int n) {
      return TraversableLike$class.splitAt(this, n);
   }

   public Iterator<Seq<T>> tails() {
      return TraversableLike$class.tails(this);
   }

   public Iterator<Seq<T>> inits() {
      return TraversableLike$class.inits(this);
   }

   public Traversable<T> toTraversable() {
      return TraversableLike$class.toTraversable(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, T, Col> cbf) {
      return (Col)TraversableLike$class.to(this, cbf);
   }

   public String stringPrefix() {
      return TraversableLike$class.stringPrefix(this);
   }

   public FilterMonadic<T, Seq<T>> withFilter(Function1<T, Object> p) {
      return TraversableLike$class.withFilter(this, p);
   }

   public Parallel par() {
      return Parallelizable$class.par(this);
   }

   public List<T> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<T, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<T, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, T, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<T, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, T, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, T, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, T, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<T, B, B> op) {
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

   public <B> B aggregate(Function0<B> z, Function2<B, T, B> seqop, Function2<B, B, B> combop) {
      return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
   }

   public <B> B sum(Numeric<B> num) {
      return (B)TraversableOnce$class.sum(this, num);
   }

   public <B> B product(Numeric<B> num) {
      return (B)TraversableOnce$class.product(this, num);
   }

   public <B> T min(Ordering<B> cmp) {
      return (T)TraversableOnce$class.min(this, cmp);
   }

   public <B> T max(Ordering<B> cmp) {
      return (T)TraversableOnce$class.max(this, cmp);
   }

   public <B> T maxBy(Function1<T, B> f, Ordering<B> cmp) {
      return (T)TraversableOnce$class.maxBy(this, f, cmp);
   }

   public <B> T minBy(Function1<T, B> f, Ordering<B> cmp) {
      return (T)TraversableOnce$class.minBy(this, f, cmp);
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

   public List<T> toList() {
      return TraversableOnce$class.toList(this);
   }

   public IndexedSeq<T> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<T> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<T, Tuple2<T, U>> ev) {
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

   public <C> PartialFunction<Object, C> andThen(Function1<T, C> k) {
      return PartialFunction$class.andThen(this, k);
   }

   public Function1<Object, Option<T>> lift() {
      return PartialFunction$class.lift(this);
   }

   public Object applyOrElse(Object x, Function1 var2) {
      return PartialFunction$class.applyOrElse(this, x, var2);
   }

   public <U> Function1<Object, Object> runWith(Function1<T, U> action) {
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

   public <A> Function1<A, T> compose(Function1<A, Object> g) {
      return Function1$class.compose(this, g);
   }

   private T element0() {
      return this.element0;
   }

   private void element0_$eq(T x$1) {
      this.element0 = (T)x$1;
   }

   private T element1() {
      return this.element1;
   }

   private void element1_$eq(T x$1) {
      this.element1 = (T)x$1;
   }

   public int org$apache$spark$util$collection$CompactBuffer$$curSize() {
      return this.org$apache$spark$util$collection$CompactBuffer$$curSize;
   }

   private void org$apache$spark$util$collection$CompactBuffer$$curSize_$eq(int x$1) {
      this.org$apache$spark$util$collection$CompactBuffer$$curSize = x$1;
   }

   private Object otherElements() {
      return this.otherElements;
   }

   private void otherElements_$eq(Object x$1) {
      this.otherElements = x$1;
   }

   public T apply(int position) {
      if (position >= 0 && position < this.org$apache$spark$util$collection$CompactBuffer$$curSize()) {
         return (T)(position == 0 ? this.element0() : (position == 1 ? this.element1() : ScalaRunTime$.MODULE$.array_apply(this.otherElements(), position - 2)));
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void update(int position, T value) {
      if (position >= 0 && position < this.org$apache$spark$util$collection$CompactBuffer$$curSize()) {
         if (position == 0) {
            this.element0_$eq(value);
         } else if (position == 1) {
            this.element1_$eq(value);
         } else {
            ScalaRunTime$.MODULE$.array_update(this.otherElements(), position - 2, value);
         }

      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public CompactBuffer<T> $plus$eq(T value) {
      int newIndex = this.org$apache$spark$util$collection$CompactBuffer$$curSize();
      if (newIndex == 0) {
         this.element0_$eq(value);
         this.org$apache$spark$util$collection$CompactBuffer$$curSize_$eq(1);
      } else if (newIndex == 1) {
         this.element1_$eq(value);
         this.org$apache$spark$util$collection$CompactBuffer$$curSize_$eq(2);
      } else {
         this.growToSize(this.org$apache$spark$util$collection$CompactBuffer$$curSize() + 1);
         ScalaRunTime$.MODULE$.array_update(this.otherElements(), newIndex - 2, value);
      }

      return this;
   }

   public CompactBuffer<T> $plus$plus$eq(TraversableOnce<T> values) {
      if (values instanceof CompactBuffer) {
         CompactBuffer var3 = (CompactBuffer)values;
         int oldSize = this.org$apache$spark$util$collection$CompactBuffer$$curSize();
         int itsSize = var3.org$apache$spark$util$collection$CompactBuffer$$curSize();
         Object itsElements = var3.otherElements();
         this.growToSize(this.org$apache$spark$util$collection$CompactBuffer$$curSize() + itsSize);
         if (itsSize == 1) {
            this.update(oldSize, var3.element0());
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else if (itsSize == 2) {
            this.update(oldSize, var3.element0());
            this.update(oldSize + 1, var3.element1());
            BoxedUnit var8 = BoxedUnit.UNIT;
         } else if (itsSize > 2) {
            this.update(oldSize, var3.element0());
            this.update(oldSize + 1, var3.element1());
            System.arraycopy(itsElements, 0, this.otherElements(), oldSize, itsSize - 2);
            BoxedUnit var9 = BoxedUnit.UNIT;
         } else {
            BoxedUnit var10 = BoxedUnit.UNIT;
         }
      } else {
         values.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CompactBuffer $outer;

            public final CompactBuffer<T> apply(T e) {
               return this.$outer.$plus$eq(e);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         BoxedUnit var4 = BoxedUnit.UNIT;
      }

      return this;
   }

   public int length() {
      return this.org$apache$spark$util$collection$CompactBuffer$$curSize();
   }

   public int size() {
      return this.org$apache$spark$util$collection$CompactBuffer$$curSize();
   }

   public Iterator<T> iterator() {
      return new Iterator<T>(this) {
         private int pos;
         // $FF: synthetic field
         private final CompactBuffer $outer;

         public Iterator<T> seq() {
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

         public Iterator<T> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<T> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<T> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<T, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<T, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<T> filter(Function1<T, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<T, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<T> withFilter(Function1<T, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<T> filterNot(Function1<T, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<T, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, T, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<T, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<T> takeWhile(Function1<T, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<T>, Iterator<T>> partition(Function1<T, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<T>, Iterator<T>> span(Function1<T, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<T> dropWhile(Function1<T, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<T, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<T, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<T, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<T, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<T, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<T> find(Function1<T, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<T, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<T> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<T>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<T>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<T>, Iterator<T>> duplicate() {
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

         public Traversable<T> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<T> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<T> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<T> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<T, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<T, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, T, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<T, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, T, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<T, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, T, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<T, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, T, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<T, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, T, B> seqop, Function2<B, B, B> combop) {
            return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
         }

         public <B> B sum(Numeric<B> num) {
            return (B)TraversableOnce$class.sum(this, num);
         }

         public <B> B product(Numeric<B> num) {
            return (B)TraversableOnce$class.product(this, num);
         }

         public <B> T min(Ordering<B> cmp) {
            return (T)TraversableOnce$class.min(this, cmp);
         }

         public <B> T max(Ordering<B> cmp) {
            return (T)TraversableOnce$class.max(this, cmp);
         }

         public <B> T maxBy(Function1<T, B> f, Ordering<B> cmp) {
            return (T)TraversableOnce$class.maxBy(this, f, cmp);
         }

         public <B> T minBy(Function1<T, B> f, Ordering<B> cmp) {
            return (T)TraversableOnce$class.minBy(this, f, cmp);
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

         public List<T> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<T> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<T> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<T> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<T> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, T, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<T, Tuple2<T, U>> ev) {
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

         public boolean hasNext() {
            return this.pos() < this.$outer.org$apache$spark$util$collection$CompactBuffer$$curSize();
         }

         public T next() {
            if (this.hasNext()) {
               this.pos_$eq(this.pos() + 1);
               return (T)this.$outer.apply(this.pos() - 1);
            } else {
               throw new NoSuchElementException();
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.pos = 0;
            }
         }
      };
   }

   private void growToSize(int newSize) {
      int newArraySize = newSize - 2;
      int arrayMax = ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH;
      if (newSize >= 0 && newArraySize <= arrayMax) {
         int capacity = this.otherElements() == null ? 0 : ScalaRunTime$.MODULE$.array_length(this.otherElements());
         if (newArraySize > capacity) {
            long newArrayLen;
            for(newArrayLen = 8L; (long)newArraySize > newArrayLen; newArrayLen *= 2L) {
            }

            if (newArrayLen > (long)arrayMax) {
               newArrayLen = (long)arrayMax;
            }

            Object newArray = this.evidence$1.newArray((int)newArrayLen);
            if (this.otherElements() != null) {
               System.arraycopy(this.otherElements(), 0, newArray, 0, ScalaRunTime$.MODULE$.array_length(this.otherElements()));
            }

            this.otherElements_$eq(newArray);
         }

         this.org$apache$spark$util$collection$CompactBuffer$$curSize_$eq(newSize);
      } else {
         throw new UnsupportedOperationException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't grow buffer past ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(arrayMax)})));
      }
   }

   public CompactBuffer(ClassTag<T> evidence$1) {
      this.evidence$1 = evidence$1;
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
      this.org$apache$spark$util$collection$CompactBuffer$$curSize = 0;
      this.otherElements = null;
   }
}
