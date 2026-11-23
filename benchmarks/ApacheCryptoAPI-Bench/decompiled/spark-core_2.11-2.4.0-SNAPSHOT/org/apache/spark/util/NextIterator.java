package org.apache.spark.util;

import java.util.NoSuchElementException;
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
   bytes = "\u0006\u0001\u00114a!\u0001\u0002\u0002\u0002\u0011Q!\u0001\u0004(fqRLE/\u001a:bi>\u0014(BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,\"aC\u0011\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0004'qybB\u0001\u000b\u001b\u001d\t)\u0012$D\u0001\u0017\u0015\t9\u0002$\u0001\u0004=e>|GOP\u0002\u0001\u0013\u0005y\u0011BA\u000e\u000f\u0003\u001d\u0001\u0018mY6bO\u0016L!!\b\u0010\u0003\u0011%#XM]1u_JT!a\u0007\b\u0011\u0005\u0001\nC\u0002\u0001\u0003\u0006E\u0001\u0011\ra\t\u0002\u0002+F\u0011Ae\n\t\u0003\u001b\u0015J!A\n\b\u0003\u000f9{G\u000f[5oOB\u0011Q\u0002K\u0005\u0003S9\u00111!\u00118z\u0011\u0015Y\u0003\u0001\"\u0001-\u0003\u0019a\u0014N\\5u}Q\tQ\u0006E\u0002/\u0001}i\u0011A\u0001\u0005\ba\u0001\u0001\r\u0011\"\u00032\u0003\u001d9w\u000e\u001e(fqR,\u0012A\r\t\u0003\u001bMJ!\u0001\u000e\b\u0003\u000f\t{w\u000e\\3b]\"9a\u0007\u0001a\u0001\n\u00139\u0014aC4pi:+\u0007\u0010^0%KF$\"\u0001O\u001e\u0011\u00055I\u0014B\u0001\u001e\u000f\u0005\u0011)f.\u001b;\t\u000fq*\u0014\u0011!a\u0001e\u0005\u0019\u0001\u0010J\u0019\t\ry\u0002\u0001\u0015)\u00033\u0003!9w\u000e\u001e(fqR\u0004\u0003\"\u0003!\u0001\u0001\u0004\u0005\r\u0011\"\u0003B\u0003%qW\r\u001f;WC2,X-F\u0001 \u0011%\u0019\u0005\u00011AA\u0002\u0013%A)A\u0007oKb$h+\u00197vK~#S-\u001d\u000b\u0003q\u0015Cq\u0001\u0010\"\u0002\u0002\u0003\u0007q\u0004\u0003\u0004H\u0001\u0001\u0006KaH\u0001\u000b]\u0016DHOV1mk\u0016\u0004\u0003bB%\u0001\u0001\u0004%I!M\u0001\u0007G2|7/\u001a3\t\u000f-\u0003\u0001\u0019!C\u0005\u0019\u0006Q1\r\\8tK\u0012|F%Z9\u0015\u0005aj\u0005b\u0002\u001fK\u0003\u0003\u0005\rA\r\u0005\u0007\u001f\u0002\u0001\u000b\u0015\u0002\u001a\u0002\u000f\rdwn]3eA!9\u0011\u000b\u0001a\u0001\n#\t\u0014\u0001\u00034j]&\u001c\b.\u001a3\t\u000fM\u0003\u0001\u0019!C\t)\u0006aa-\u001b8jg\",Gm\u0018\u0013fcR\u0011\u0001(\u0016\u0005\byI\u000b\t\u00111\u00013\u0011\u00199\u0006\u0001)Q\u0005e\u0005Ia-\u001b8jg\",G\r\t\u0005\u00063\u00021\tBW\u0001\bO\u0016$h*\u001a=u)\u0005y\u0002\"\u0002/\u0001\r#i\u0016!B2m_N,G#\u0001\u001d\t\u000b}\u0003A\u0011A/\u0002\u001b\rdwn]3JM:+W\rZ3e\u0011\u0015\t\u0007\u0001\"\u00112\u0003\u001dA\u0017m\u001d(fqRDQa\u0019\u0001\u0005Bi\u000bAA\\3yi\u0002"
)
public abstract class NextIterator<U> implements Iterator<U> {
   private boolean gotNext;
   private U nextValue;
   private boolean closed;
   private boolean finished;

   public Iterator<U> seq() {
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

   public Iterator<U> take(int n) {
      return Iterator$class.take(this, n);
   }

   public Iterator<U> drop(int n) {
      return Iterator$class.drop(this, n);
   }

   public Iterator<U> slice(int from, int until) {
      return Iterator$class.slice(this, from, until);
   }

   public <B> Iterator<B> map(Function1<U, B> f) {
      return Iterator$class.map(this, f);
   }

   public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
      return Iterator$class.$plus$plus(this, that);
   }

   public <B> Iterator<B> flatMap(Function1<U, GenTraversableOnce<B>> f) {
      return Iterator$class.flatMap(this, f);
   }

   public Iterator<U> filter(Function1<U, Object> p) {
      return Iterator$class.filter(this, p);
   }

   public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<U, B, Object> p) {
      return Iterator$class.corresponds(this, that, p);
   }

   public Iterator<U> withFilter(Function1<U, Object> p) {
      return Iterator$class.withFilter(this, p);
   }

   public Iterator<U> filterNot(Function1<U, Object> p) {
      return Iterator$class.filterNot(this, p);
   }

   public <B> Iterator<B> collect(PartialFunction<U, B> pf) {
      return Iterator$class.collect(this, pf);
   }

   public <B> Iterator<B> scanLeft(B z, Function2<B, U, B> op) {
      return Iterator$class.scanLeft(this, z, op);
   }

   public <B> Iterator<B> scanRight(B z, Function2<U, B, B> op) {
      return Iterator$class.scanRight(this, z, op);
   }

   public Iterator<U> takeWhile(Function1<U, Object> p) {
      return Iterator$class.takeWhile(this, p);
   }

   public Tuple2<Iterator<U>, Iterator<U>> partition(Function1<U, Object> p) {
      return Iterator$class.partition(this, p);
   }

   public Tuple2<Iterator<U>, Iterator<U>> span(Function1<U, Object> p) {
      return Iterator$class.span(this, p);
   }

   public Iterator<U> dropWhile(Function1<U, Object> p) {
      return Iterator$class.dropWhile(this, p);
   }

   public <B> Iterator<Tuple2<U, B>> zip(Iterator<B> that) {
      return Iterator$class.zip(this, that);
   }

   public <A1> Iterator<A1> padTo(int len, A1 elem) {
      return Iterator$class.padTo(this, len, elem);
   }

   public Iterator<Tuple2<U, Object>> zipWithIndex() {
      return Iterator$class.zipWithIndex(this);
   }

   public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
      return Iterator$class.zipAll(this, that, thisElem, thatElem);
   }

   public <U> void foreach(Function1<U, U> f) {
      Iterator$class.foreach(this, f);
   }

   public boolean forall(Function1<U, Object> p) {
      return Iterator$class.forall(this, p);
   }

   public boolean exists(Function1<U, Object> p) {
      return Iterator$class.exists(this, p);
   }

   public boolean contains(Object elem) {
      return Iterator$class.contains(this, elem);
   }

   public Option<U> find(Function1<U, Object> p) {
      return Iterator$class.find(this, p);
   }

   public int indexWhere(Function1<U, Object> p) {
      return Iterator$class.indexWhere(this, p);
   }

   public <B> int indexOf(B elem) {
      return Iterator$class.indexOf(this, elem);
   }

   public BufferedIterator<U> buffered() {
      return Iterator$class.buffered(this);
   }

   public <B> Iterator<U>.GroupedIterator<B> grouped(int size) {
      return Iterator$class.grouped(this, size);
   }

   public <B> Iterator<U>.GroupedIterator<B> sliding(int size, int step) {
      return Iterator$class.sliding(this, size, step);
   }

   public int length() {
      return Iterator$class.length(this);
   }

   public Tuple2<Iterator<U>, Iterator<U>> duplicate() {
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

   public Traversable<U> toTraversable() {
      return Iterator$class.toTraversable(this);
   }

   public Iterator<U> toIterator() {
      return Iterator$class.toIterator(this);
   }

   public Stream<U> toStream() {
      return Iterator$class.toStream(this);
   }

   public String toString() {
      return Iterator$class.toString(this);
   }

   public <B> int sliding$default$2() {
      return Iterator$class.sliding$default$2(this);
   }

   public List<U> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public int size() {
      return TraversableOnce$class.size(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<U, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<U, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, U, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<U, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, U, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B foldRight(B z, Function2<U, B, B> op) {
      return (B)TraversableOnce$class.foldRight(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, U, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> B reduceRight(Function2<U, B, B> op) {
      return (B)TraversableOnce$class.reduceRight(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, U, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<U, B, B> op) {
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

   public <B> B aggregate(Function0<B> z, Function2<B, U, B> seqop, Function2<B, B, B> combop) {
      return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
   }

   public <B> B sum(Numeric<B> num) {
      return (B)TraversableOnce$class.sum(this, num);
   }

   public <B> B product(Numeric<B> num) {
      return (B)TraversableOnce$class.product(this, num);
   }

   public <B> U min(Ordering<B> cmp) {
      return (U)TraversableOnce$class.min(this, cmp);
   }

   public <B> U max(Ordering<B> cmp) {
      return (U)TraversableOnce$class.max(this, cmp);
   }

   public <B> U maxBy(Function1<U, B> f, Ordering<B> cmp) {
      return (U)TraversableOnce$class.maxBy(this, f, cmp);
   }

   public <B> U minBy(Function1<U, B> f, Ordering<B> cmp) {
      return (U)TraversableOnce$class.minBy(this, f, cmp);
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

   public List<U> toList() {
      return TraversableOnce$class.toList(this);
   }

   public Iterable<U> toIterable() {
      return TraversableOnce$class.toIterable(this);
   }

   public Seq<U> toSeq() {
      return TraversableOnce$class.toSeq(this);
   }

   public IndexedSeq<U> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<U> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, U, Col> cbf) {
      return (Col)TraversableOnce$class.to(this, cbf);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<U, Tuple2<T, U>> ev) {
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

   private boolean gotNext() {
      return this.gotNext;
   }

   private void gotNext_$eq(boolean x$1) {
      this.gotNext = x$1;
   }

   private U nextValue() {
      return this.nextValue;
   }

   private void nextValue_$eq(U x$1) {
      this.nextValue = (U)x$1;
   }

   private boolean closed() {
      return this.closed;
   }

   private void closed_$eq(boolean x$1) {
      this.closed = x$1;
   }

   public boolean finished() {
      return this.finished;
   }

   public void finished_$eq(boolean x$1) {
      this.finished = x$1;
   }

   public abstract U getNext();

   public abstract void close();

   public void closeIfNeeded() {
      if (!this.closed()) {
         this.closed_$eq(true);
         this.close();
      }

   }

   public boolean hasNext() {
      if (!this.finished() && !this.gotNext()) {
         this.nextValue_$eq(this.getNext());
         if (this.finished()) {
            this.closeIfNeeded();
         }

         this.gotNext_$eq(true);
      }

      return !this.finished();
   }

   public U next() {
      if (this.hasNext()) {
         this.gotNext_$eq(false);
         return (U)this.nextValue();
      } else {
         throw new NoSuchElementException("End of stream");
      }
   }

   public NextIterator() {
      TraversableOnce$class.$init$(this);
      Iterator$class.$init$(this);
      this.gotNext = false;
      this.closed = false;
      this.finished = false;
   }
}
