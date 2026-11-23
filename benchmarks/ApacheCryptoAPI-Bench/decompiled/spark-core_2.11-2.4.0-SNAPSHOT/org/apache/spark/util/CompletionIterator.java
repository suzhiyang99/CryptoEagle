package org.apache.spark.util;

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
import scala.runtime.BoxedUnit;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001}3a!\u0001\u0002\u0002\u0002\u0011Q!AE\"p[BdW\r^5p]&#XM]1u_JT!a\u0001\u0003\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOV\u00191\"I\u0017\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0004'qybB\u0001\u000b\u001b\u001d\t)\u0012$D\u0001\u0017\u0015\t9\u0002$\u0001\u0004=e>|GOP\u0002\u0001\u0013\u0005y\u0011BA\u000e\u000f\u0003\u001d\u0001\u0018mY6bO\u0016L!!\b\u0010\u0003\u0011%#XM]1u_JT!a\u0007\b\u0011\u0005\u0001\nC\u0002\u0001\u0003\u0007E\u0001!)\u0019A\u0012\u0003\u0003\u0005\u000b\"\u0001J\u0014\u0011\u00055)\u0013B\u0001\u0014\u000f\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"!\u0004\u0015\n\u0005%r!aA!os\"A1\u0006\u0001B\u0001B\u0003%A&A\u0002tk\n\u0004\"\u0001I\u0017\u0005\r9\u0002AQ1\u00010\u0005\u0005I\u0015C\u0001\u0013\u0013\u0011\u0015\t\u0004\u0001\"\u00013\u0003\u0019a\u0014N\\5u}Q\u00111'\u000e\t\u0005i\u0001yB&D\u0001\u0003\u0011\u0015Y\u0003\u00071\u0001-\u0011\u00199\u0004\u0001)Q\u0005q\u0005I1m\\7qY\u0016$X\r\u001a\t\u0003\u001beJ!A\u000f\b\u0003\u000f\t{w\u000e\\3b]\")A\b\u0001C\u0001{\u0005!a.\u001a=u)\u0005y\u0002\"B \u0001\t\u0003\u0001\u0015a\u00025bg:+\u0007\u0010^\u000b\u0002q!)!\t\u0001D\u0001\u0007\u0006Q1m\\7qY\u0016$\u0018n\u001c8\u0015\u0003\u0011\u0003\"!D#\n\u0005\u0019s!\u0001B+oSR<a\u0001\u0013\u0002\t\u0002\u0011I\u0015AE\"p[BdW\r^5p]&#XM]1u_J\u0004\"\u0001\u000e&\u0007\r\u0005\u0011\u0001\u0012\u0001\u0003L'\tQE\u0002C\u00032\u0015\u0012\u0005Q\nF\u0001J\u0011\u0015y%\n\"\u0001Q\u0003\u0015\t\u0007\u000f\u001d7z+\r\tFK\u0016\u000b\u0004%fS\u0006\u0003\u0002\u001b\u0001'V\u0003\"\u0001\t+\u0005\u000b\tr%\u0019A\u0012\u0011\u0005\u00012F!\u0002\u0018O\u0005\u00049\u0016C\u0001\u0013Y!\r\u0019Bd\u0015\u0005\u0006W9\u0003\r!\u0016\u0005\u00077:#\t\u0019\u0001/\u0002%\r|W\u000e\u001d7fi&|gNR;oGRLwN\u001c\t\u0004\u001bu#\u0015B\u00010\u000f\u0005!a$-\u001f8b[\u0016t\u0004"
)
public abstract class CompletionIterator<A, I extends Iterator<A>> implements Iterator<A> {
   private final I sub;
   private boolean completed;

   public static <A, I extends Iterator<A>> CompletionIterator<A, I> apply(I var0, Function0<BoxedUnit> var1) {
      return CompletionIterator$.MODULE$.<A, I>apply(var0, var1);
   }

   public Iterator<A> seq() {
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

   public Iterator<A> take(int n) {
      return Iterator$class.take(this, n);
   }

   public Iterator<A> drop(int n) {
      return Iterator$class.drop(this, n);
   }

   public Iterator<A> slice(int from, int until) {
      return Iterator$class.slice(this, from, until);
   }

   public <B> Iterator<B> map(Function1<A, B> f) {
      return Iterator$class.map(this, f);
   }

   public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
      return Iterator$class.$plus$plus(this, that);
   }

   public <B> Iterator<B> flatMap(Function1<A, GenTraversableOnce<B>> f) {
      return Iterator$class.flatMap(this, f);
   }

   public Iterator<A> filter(Function1<A, Object> p) {
      return Iterator$class.filter(this, p);
   }

   public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<A, B, Object> p) {
      return Iterator$class.corresponds(this, that, p);
   }

   public Iterator<A> withFilter(Function1<A, Object> p) {
      return Iterator$class.withFilter(this, p);
   }

   public Iterator<A> filterNot(Function1<A, Object> p) {
      return Iterator$class.filterNot(this, p);
   }

   public <B> Iterator<B> collect(PartialFunction<A, B> pf) {
      return Iterator$class.collect(this, pf);
   }

   public <B> Iterator<B> scanLeft(B z, Function2<B, A, B> op) {
      return Iterator$class.scanLeft(this, z, op);
   }

   public <B> Iterator<B> scanRight(B z, Function2<A, B, B> op) {
      return Iterator$class.scanRight(this, z, op);
   }

   public Iterator<A> takeWhile(Function1<A, Object> p) {
      return Iterator$class.takeWhile(this, p);
   }

   public Tuple2<Iterator<A>, Iterator<A>> partition(Function1<A, Object> p) {
      return Iterator$class.partition(this, p);
   }

   public Tuple2<Iterator<A>, Iterator<A>> span(Function1<A, Object> p) {
      return Iterator$class.span(this, p);
   }

   public Iterator<A> dropWhile(Function1<A, Object> p) {
      return Iterator$class.dropWhile(this, p);
   }

   public <B> Iterator<Tuple2<A, B>> zip(Iterator<B> that) {
      return Iterator$class.zip(this, that);
   }

   public <A1> Iterator<A1> padTo(int len, A1 elem) {
      return Iterator$class.padTo(this, len, elem);
   }

   public Iterator<Tuple2<A, Object>> zipWithIndex() {
      return Iterator$class.zipWithIndex(this);
   }

   public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
      return Iterator$class.zipAll(this, that, thisElem, thatElem);
   }

   public <U> void foreach(Function1<A, U> f) {
      Iterator$class.foreach(this, f);
   }

   public boolean forall(Function1<A, Object> p) {
      return Iterator$class.forall(this, p);
   }

   public boolean exists(Function1<A, Object> p) {
      return Iterator$class.exists(this, p);
   }

   public boolean contains(Object elem) {
      return Iterator$class.contains(this, elem);
   }

   public Option<A> find(Function1<A, Object> p) {
      return Iterator$class.find(this, p);
   }

   public int indexWhere(Function1<A, Object> p) {
      return Iterator$class.indexWhere(this, p);
   }

   public <B> int indexOf(B elem) {
      return Iterator$class.indexOf(this, elem);
   }

   public BufferedIterator<A> buffered() {
      return Iterator$class.buffered(this);
   }

   public <B> Iterator<A>.GroupedIterator<B> grouped(int size) {
      return Iterator$class.grouped(this, size);
   }

   public <B> Iterator<A>.GroupedIterator<B> sliding(int size, int step) {
      return Iterator$class.sliding(this, size, step);
   }

   public int length() {
      return Iterator$class.length(this);
   }

   public Tuple2<Iterator<A>, Iterator<A>> duplicate() {
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

   public Traversable<A> toTraversable() {
      return Iterator$class.toTraversable(this);
   }

   public Iterator<A> toIterator() {
      return Iterator$class.toIterator(this);
   }

   public Stream<A> toStream() {
      return Iterator$class.toStream(this);
   }

   public String toString() {
      return Iterator$class.toString(this);
   }

   public <B> int sliding$default$2() {
      return Iterator$class.sliding$default$2(this);
   }

   public List<A> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public int size() {
      return TraversableOnce$class.size(this);
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

   public <B> B foldRight(B z, Function2<A, B, B> op) {
      return (B)TraversableOnce$class.foldRight(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, A, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> B reduceRight(Function2<A, B, B> op) {
      return (B)TraversableOnce$class.reduceRight(this, op);
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

   public Iterable<A> toIterable() {
      return TraversableOnce$class.toIterable(this);
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

   public <Col> Col to(CanBuildFrom<Nothing$, A, Col> cbf) {
      return (Col)TraversableOnce$class.to(this, cbf);
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

   public A next() {
      return (A)this.sub.next();
   }

   public boolean hasNext() {
      boolean r = this.sub.hasNext();
      if (!r && !this.completed) {
         this.completed = true;
         this.completion();
      }

      return r;
   }

   public abstract void completion();

   public CompletionIterator(I sub) {
      this.sub = sub;
      TraversableOnce$class.$init$(this);
      Iterator$class.$init$(this);
      this.completed = false;
   }
}
