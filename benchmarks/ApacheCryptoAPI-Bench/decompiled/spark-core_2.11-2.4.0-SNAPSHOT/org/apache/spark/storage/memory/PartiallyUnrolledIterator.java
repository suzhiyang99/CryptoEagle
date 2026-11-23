package org.apache.spark.storage.memory;

import org.apache.spark.memory.MemoryMode;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Tuple2;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001q3Q!\u0001\u0002\u0001\t1\u0011\u0011\u0004U1si&\fG\u000e\\=V]J|G\u000e\\3e\u0013R,'/\u0019;pe*\u00111\u0001B\u0001\u0007[\u0016lwN]=\u000b\u0005\u00151\u0011aB:u_J\fw-\u001a\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOV\u0011QbI\n\u0004\u00019!\u0002CA\b\u0013\u001b\u0005\u0001\"\"A\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005M\u0001\"AB!osJ+g\rE\u0002\u0016=\u0005r!A\u0006\u000f\u000f\u0005]YR\"\u0001\r\u000b\u0005eQ\u0012A\u0002\u001fs_>$hh\u0001\u0001\n\u0003EI!!\b\t\u0002\u000fA\f7m[1hK&\u0011q\u0004\t\u0002\t\u0013R,'/\u0019;pe*\u0011Q\u0004\u0005\t\u0003E\rb\u0001\u0001B\u0003%\u0001\t\u0007QEA\u0001U#\t1\u0013\u0006\u0005\u0002\u0010O%\u0011\u0001\u0006\u0005\u0002\b\u001d>$\b.\u001b8h!\ty!&\u0003\u0002,!\t\u0019\u0011I\\=\t\u00115\u0002!\u0011!Q\u0001\n9\n1\"\\3n_JL8\u000b^8sKB\u0011q\u0006M\u0007\u0002\u0005%\u0011\u0011G\u0001\u0002\f\u001b\u0016lwN]=Ti>\u0014X\r\u0003\u00054\u0001\t\u0005\t\u0015!\u00035\u0003)iW-\\8ss6{G-\u001a\t\u0003k]j\u0011A\u000e\u0006\u0003\u0007\u0019I!\u0001\u000f\u001c\u0003\u00155+Wn\u001c:z\u001b>$W\r\u0003\u0005;\u0001\t\u0005\t\u0015!\u0003<\u00031)hN]8mY6+Wn\u001c:z!\tyA(\u0003\u0002>!\t!Aj\u001c8h\u0011!y\u0004A!A!B\u0013!\u0012\u0001C;oe>dG.\u001a3\t\u0011\u0005\u0003!\u0011!Q\u0001\nQ\tAA]3ti\")1\t\u0001C\u0001\t\u00061A(\u001b8jiz\"b!\u0012$H\u0011&S\u0005cA\u0018\u0001C!)QF\u0011a\u0001]!)1G\u0011a\u0001i!)!H\u0011a\u0001w!)qH\u0011a\u0001)!)\u0011I\u0011a\u0001)!)A\n\u0001C\u0005\u001b\u0006\u0019\"/\u001a7fCN,WK\u001c:pY2lU-\\8ssR\ta\n\u0005\u0002\u0010\u001f&\u0011\u0001\u000b\u0005\u0002\u0005+:LG\u000fC\u0003S\u0001\u0011\u00053+A\u0004iCNtU\r\u001f;\u0016\u0003Q\u0003\"aD+\n\u0005Y\u0003\"a\u0002\"p_2,\u0017M\u001c\u0005\u00061\u0002!\t%W\u0001\u0005]\u0016DH\u000fF\u0001\"\u0011\u0015Y\u0006\u0001\"\u0001N\u0003\u0015\u0019Gn\\:f\u0001"
)
public class PartiallyUnrolledIterator<T> implements Iterator<T> {
   private final MemoryStore memoryStore;
   private final MemoryMode memoryMode;
   private final long unrollMemory;
   private Iterator<T> unrolled;
   private final Iterator<T> rest;

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

   private void releaseUnrollMemory() {
      this.memoryStore.releaseUnrollMemoryForThisTask(this.memoryMode, this.unrollMemory);
      this.unrolled = null;
   }

   public boolean hasNext() {
      boolean var10000;
      if (this.unrolled == null) {
         var10000 = this.rest.hasNext();
      } else if (this.unrolled.hasNext()) {
         var10000 = true;
      } else {
         this.releaseUnrollMemory();
         var10000 = this.rest.hasNext();
      }

      return var10000;
   }

   public T next() {
      return (T)(this.unrolled != null && this.unrolled.hasNext() ? this.unrolled.next() : this.rest.next());
   }

   public void close() {
      if (this.unrolled != null) {
         this.releaseUnrollMemory();
      }

   }

   public PartiallyUnrolledIterator(MemoryStore memoryStore, MemoryMode memoryMode, long unrollMemory, Iterator<T> unrolled, Iterator<T> rest) {
      this.memoryStore = memoryStore;
      this.memoryMode = memoryMode;
      this.unrollMemory = unrollMemory;
      this.unrolled = unrolled;
      this.rest = rest;
      super();
      TraversableOnce$class.$init$(this);
      Iterator$class.$init$(this);
   }
}
