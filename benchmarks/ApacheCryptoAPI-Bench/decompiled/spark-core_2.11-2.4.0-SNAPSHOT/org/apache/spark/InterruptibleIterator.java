package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
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

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u000113A!\u0001\u0002\u0001\u0013\t)\u0012J\u001c;feJ,\b\u000f^5cY\u0016LE/\u001a:bi>\u0014(BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0004\u0001U\u0011!bH\n\u0004\u0001-\t\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\rE\u0002\u00135uq!a\u0005\r\u000f\u0005Q9R\"A\u000b\u000b\u0005YA\u0011A\u0002\u001fs_>$h(C\u0001\u000f\u0013\tIR\"A\u0004qC\u000e\\\u0017mZ3\n\u0005ma\"\u0001C%uKJ\fGo\u001c:\u000b\u0005ei\u0001C\u0001\u0010 \u0019\u0001!a\u0001\t\u0001\u0005\u0006\u0004\t#!\u0001+\u0012\u0005\t*\u0003C\u0001\u0007$\u0013\t!SBA\u0004O_RD\u0017N\\4\u0011\u000511\u0013BA\u0014\u000e\u0005\r\te.\u001f\u0005\tS\u0001\u0011)\u0019!C\u0001U\u000591m\u001c8uKb$X#A\u0016\u0011\u00051jS\"\u0001\u0002\n\u00059\u0012!a\u0003+bg.\u001cuN\u001c;fqRD\u0001\u0002\r\u0001\u0003\u0002\u0003\u0006IaK\u0001\tG>tG/\u001a=uA!A!\u0007\u0001BC\u0002\u0013\u00051'\u0001\u0005eK2,w-\u0019;f+\u0005\t\u0002\u0002C\u001b\u0001\u0005\u0003\u0005\u000b\u0011B\t\u0002\u0013\u0011,G.Z4bi\u0016\u0004\u0003\"B\u001c\u0001\t\u0003A\u0014A\u0002\u001fj]&$h\bF\u0002:um\u00022\u0001\f\u0001\u001e\u0011\u0015Ic\u00071\u0001,\u0011\u0015\u0011d\u00071\u0001\u0012\u0011\u0015i\u0004\u0001\"\u0001?\u0003\u001dA\u0017m\u001d(fqR,\u0012a\u0010\t\u0003\u0019\u0001K!!Q\u0007\u0003\u000f\t{w\u000e\\3b]\")1\t\u0001C\u0001\t\u0006!a.\u001a=u)\u0005i\u0002F\u0001\u0001G!\t9%*D\u0001I\u0015\tI%!\u0001\u0006b]:|G/\u0019;j_:L!a\u0013%\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5"
)
public class InterruptibleIterator<T> implements Iterator<T> {
   private final TaskContext context;
   private final Iterator<T> delegate;

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

   public TaskContext context() {
      return this.context;
   }

   public Iterator<T> delegate() {
      return this.delegate;
   }

   public boolean hasNext() {
      this.context().killTaskIfInterrupted();
      return this.delegate().hasNext();
   }

   public T next() {
      return (T)this.delegate().next();
   }

   public InterruptibleIterator(TaskContext context, Iterator<T> delegate) {
      this.context = context;
      this.delegate = delegate;
      TraversableOnce$class.$init$(this);
      Iterator$class.$init$(this);
   }
}
