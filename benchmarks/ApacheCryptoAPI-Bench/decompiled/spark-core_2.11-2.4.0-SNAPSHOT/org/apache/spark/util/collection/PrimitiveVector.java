package org.apache.spark.util.collection;

import java.util.NoSuchElementException;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
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
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mc!B\u0001\u0003\u0001\u0019a!a\u0004)sS6LG/\u001b<f-\u0016\u001cGo\u001c:\u000b\u0005\r!\u0011AC2pY2,7\r^5p]*\u0011QAB\u0001\u0005kRLGN\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h+\tiAe\u0005\u0002\u0001\u001dA\u0011qBE\u0007\u0002!)\t\u0011#A\u0003tG\u0006d\u0017-\u0003\u0002\u0014!\t1\u0011I\\=SK\u001aD\u0001\"\u0006\u0001\u0003\u0002\u0003\u0006IaF\u0001\fS:LG/[1m'&TXm\u0001\u0001\u0011\u0005=A\u0012BA\r\u0011\u0005\rIe\u000e\u001e\u0005\t7\u0001\u0011\u0019\u0011)A\u00069\u0005QQM^5eK:\u001cW\rJ\u0019\u0011\u0007u\u0001#%D\u0001\u001f\u0015\ty\u0002#A\u0004sK\u001adWm\u0019;\n\u0005\u0005r\"\u0001C\"mCN\u001cH+Y4\u0011\u0005\r\"C\u0002\u0001\u0003\nK\u0001\u0001\u000b\u0011!AC\u0002\u0019\u0012\u0011AV\t\u0003O)\u0002\"a\u0004\u0015\n\u0005%\u0002\"a\u0002(pi\"Lgn\u001a\t\u0003\u001f-J!\u0001\f\t\u0003\u0007\u0005s\u0017\u0010K\u0003%]EZ\u0004\t\u0005\u0002\u0010_%\u0011\u0001\u0007\u0005\u0002\fgB,7-[1mSj,G-M\u0003$eM*DG\u0004\u0002\u0010g%\u0011A\u0007E\u0001\u0005\u0019>tw-\r\u0003%mi\nbBA\u001c;\u001b\u0005A$BA\u001d\u0017\u0003\u0019a$o\\8u}%\t\u0011#M\u0003$yuzdH\u0004\u0002\u0010{%\u0011a\bE\u0001\u0004\u0013:$\u0018\u0007\u0002\u00137uE\tTaI!C\t\u000es!a\u0004\"\n\u0005\r\u0003\u0012A\u0002#pk\ndW-\r\u0003%mi\n\u0002\"\u0002$\u0001\t\u00039\u0015A\u0002\u001fj]&$h\b\u0006\u0002I\u0019R\u0011\u0011j\u0013\t\u0004\u0015\u0002\u0011S\"\u0001\u0002\t\u000bm)\u00059\u0001\u000f\t\u000fU)\u0005\u0013!a\u0001/!9a\n\u0001a\u0001\n\u0013y\u0015\u0001D0ok6,E.Z7f]R\u001cX#A\f\t\u000fE\u0003\u0001\u0019!C\u0005%\u0006\u0001rL\\;n\u000b2,W.\u001a8ug~#S-\u001d\u000b\u0003'Z\u0003\"a\u0004+\n\u0005U\u0003\"\u0001B+oSRDqa\u0016)\u0002\u0002\u0003\u0007q#A\u0002yIEBa!\u0017\u0001!B\u00139\u0012!D0ok6,E.Z7f]R\u001c\b\u0005C\u0005\\\u0001\u0001\u0007\t\u0019!C\u00059\u00061q,\u0019:sCf,\u0012!\u0018\t\u0004\u001fy\u0013\u0013BA0\u0011\u0005\u0015\t%O]1z\u0011%\t\u0007\u00011AA\u0002\u0013%!-\u0001\u0006`CJ\u0014\u0018-_0%KF$\"aU2\t\u000f]\u0003\u0017\u0011!a\u0001;\"1Q\r\u0001Q!\nu\u000bqaX1se\u0006L\b\u0005C\u0003h\u0001\u0011\u0005\u0001.A\u0003baBd\u0017\u0010\u0006\u0002#S\")!N\u001aa\u0001/\u0005)\u0011N\u001c3fq\")A\u000e\u0001C\u0001[\u0006AA\u0005\u001d7vg\u0012*\u0017\u000f\u0006\u0002T]\")qn\u001ba\u0001E\u0005)a/\u00197vK\")\u0011\u000f\u0001C\u0001\u001f\u0006A1-\u00199bG&$\u0018\u0010C\u0003t\u0001\u0011\u0005q*\u0001\u0004mK:<G\u000f\u001b\u0005\u0006k\u0002!\taT\u0001\u0005g&TX\rC\u0003x\u0001\u0011\u0005\u00010\u0001\u0005ji\u0016\u0014\u0018\r^8s+\u0005I\bc\u0001>~E9\u0011ag_\u0005\u0003yB\tq\u0001]1dW\u0006<W-\u0003\u0002\u007f\u007f\nA\u0011\n^3sCR|'O\u0003\u0002}!!1\u00111\u0001\u0001\u0005\u0002q\u000bQ!\u0019:sCfDq!a\u0002\u0001\t\u0003\tI!\u0001\u0003ue&lG#A%\t\u000f\u00055\u0001\u0001\"\u0001\u0002\u0010\u00051!/Z:ju\u0016$2!SA\t\u0011\u001d\t\u0019\"a\u0003A\u0002]\t\u0011B\\3x\u0019\u0016tw\r\u001e5\t\r\u0005]\u0001\u0001\"\u0001]\u0003\u001d!x.\u0011:sCfDq!a\u0007\u0001\t\u0013\ti\"A\nd_BL\u0018I\u001d:bs^KG\u000f\u001b'f]\u001e$\b\u000eF\u0002^\u0003?Aaa]A\r\u0001\u00049rACA\u0012\u0005\u0005\u0005\t\u0012\u0001\u0004\u0002&\u0005y\u0001K]5nSRLg/\u001a,fGR|'\u000fE\u0002K\u0003O1\u0011\"\u0001\u0002\u0002\u0002#\u0005a!!\u000b\u0014\u0007\u0005\u001db\u0002C\u0004G\u0003O!\t!!\f\u0015\u0005\u0005\u0015\u0002BCA\u0019\u0003O\t\n\u0011\"\u0001\u00024\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIE*B!!\u000e\u0002LU\u0011\u0011q\u0007\u0016\u0004/\u0005e2FAA\u001e!\u0011\ti$a\u0012\u000e\u0005\u0005}\"\u0002BA!\u0003\u0007\n\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0005\u0015\u0003#\u0001\u0006b]:|G/\u0019;j_:LA!!\u0013\u0002@\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\u0005\u0015\u0015\ny\u0003)A\u0001\u0002\u000b\u0007a\u0005K\u0005\u0002L9\ny%a\u0015\u0002XE21EM\u001a\u0002RQ\nD\u0001\n\u001c;#E21\u0005P\u001f\u0002Vy\nD\u0001\n\u001c;#E21%\u0011\"\u0002Z\r\u000bD\u0001\n\u001c;#\u0001"
)
public class PrimitiveVector<V> {
   public final ClassTag<V> org$apache$spark$util$collection$PrimitiveVector$$evidence$1;
   public int org$apache$spark$util$collection$PrimitiveVector$$_numElements;
   public Object _array;

   public static <V> int $lessinit$greater$default$1() {
      return PrimitiveVector$.MODULE$.$lessinit$greater$default$1();
   }

   public int org$apache$spark$util$collection$PrimitiveVector$$_numElements() {
      return this.org$apache$spark$util$collection$PrimitiveVector$$_numElements;
   }

   public void org$apache$spark$util$collection$PrimitiveVector$$_numElements_$eq(int x$1) {
      this.org$apache$spark$util$collection$PrimitiveVector$$_numElements = x$1;
   }

   public Object _array() {
      return this._array;
   }

   public void _array_$eq(Object x$1) {
      this._array = x$1;
   }

   public V apply(int index) {
      Predef$.MODULE$.require(index < this.org$apache$spark$util$collection$PrimitiveVector$$_numElements());
      return (V)ScalaRunTime$.MODULE$.array_apply(this._array(), index);
   }

   public void $plus$eq(V value) {
      if (this.org$apache$spark$util$collection$PrimitiveVector$$_numElements() == ScalaRunTime$.MODULE$.array_length(this._array())) {
         this.resize(ScalaRunTime$.MODULE$.array_length(this._array()) * 2);
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      ScalaRunTime$.MODULE$.array_update(this._array(), this.org$apache$spark$util$collection$PrimitiveVector$$_numElements(), value);
      this.org$apache$spark$util$collection$PrimitiveVector$$_numElements_$eq(this.org$apache$spark$util$collection$PrimitiveVector$$_numElements() + 1);
   }

   public int capacity() {
      return ScalaRunTime$.MODULE$.array_length(this._array());
   }

   public int length() {
      return this.org$apache$spark$util$collection$PrimitiveVector$$_numElements();
   }

   public int size() {
      return this.org$apache$spark$util$collection$PrimitiveVector$$_numElements();
   }

   public Iterator<V> iterator() {
      return new Iterator<V>(this) {
         private int index;
         // $FF: synthetic field
         private final PrimitiveVector $outer;

         public Iterator<V> seq() {
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

         public Iterator<V> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<V> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<V> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<V, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<V, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<V> filter(Function1<V, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<V, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<V> withFilter(Function1<V, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<V> filterNot(Function1<V, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<V, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, V, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<V, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<V> takeWhile(Function1<V, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<V>, Iterator<V>> partition(Function1<V, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<V>, Iterator<V>> span(Function1<V, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<V> dropWhile(Function1<V, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<V, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<V, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<V, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<V, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<V, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<V> find(Function1<V, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<V, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<V> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<V>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<V>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<V>, Iterator<V>> duplicate() {
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

         public Traversable<V> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<V> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<V> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<V> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<V, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<V, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, V, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<V, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, V, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<V, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, V, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<V, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, V, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<V, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, V, B> seqop, Function2<B, B, B> combop) {
            return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
         }

         public <B> B sum(Numeric<B> num) {
            return (B)TraversableOnce$class.sum(this, num);
         }

         public <B> B product(Numeric<B> num) {
            return (B)TraversableOnce$class.product(this, num);
         }

         public <B> V min(Ordering<B> cmp) {
            return (V)TraversableOnce$class.min(this, cmp);
         }

         public <B> V max(Ordering<B> cmp) {
            return (V)TraversableOnce$class.max(this, cmp);
         }

         public <B> V maxBy(Function1<V, B> f, Ordering<B> cmp) {
            return (V)TraversableOnce$class.maxBy(this, f, cmp);
         }

         public <B> V minBy(Function1<V, B> f, Ordering<B> cmp) {
            return (V)TraversableOnce$class.minBy(this, f, cmp);
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

         public List<V> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<V> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<V> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<V> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<V> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, V, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<V, Tuple2<T, U>> ev) {
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

         private int index() {
            return this.index;
         }

         private void index_$eq(int x$1) {
            this.index = x$1;
         }

         public boolean hasNext() {
            return this.index() < this.$outer.org$apache$spark$util$collection$PrimitiveVector$$_numElements();
         }

         public V next() {
            if (this.hasNext()) {
               Object value = ScalaRunTime$.MODULE$.array_apply(this.$outer._array(), this.index());
               this.index_$eq(this.index() + 1);
               return (V)value;
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
               this.index = 0;
            }
         }
      };
   }

   public Object array() {
      return this._array();
   }

   public PrimitiveVector<V> trim() {
      return this.resize(this.size());
   }

   public PrimitiveVector<V> resize(int newLength) {
      this._array_$eq(this.copyArrayWithLength(newLength));
      if (newLength < this.org$apache$spark$util$collection$PrimitiveVector$$_numElements()) {
         this.org$apache$spark$util$collection$PrimitiveVector$$_numElements_$eq(newLength);
      }

      return this;
   }

   public Object toArray() {
      return this.copyArrayWithLength(this.size());
   }

   public Object copyArrayWithLength(int length) {
      Object copy = this.org$apache$spark$util$collection$PrimitiveVector$$evidence$1.newArray(length);
      Predef$.MODULE$.genericArrayOps(this._array()).copyToArray(copy);
      return copy;
   }

   public double[] _array$mcD$sp() {
      return (double[])this._array();
   }

   public int[] _array$mcI$sp() {
      return (int[])this._array();
   }

   public long[] _array$mcJ$sp() {
      return (long[])this._array();
   }

   public void _array$mcD$sp_$eq(double[] x$1) {
      this._array_$eq(x$1);
   }

   public void _array$mcI$sp_$eq(int[] x$1) {
      this._array_$eq(x$1);
   }

   public void _array$mcJ$sp_$eq(long[] x$1) {
      this._array_$eq(x$1);
   }

   public double apply$mcD$sp(int index) {
      return BoxesRunTime.unboxToDouble(this.apply(index));
   }

   public int apply$mcI$sp(int index) {
      return BoxesRunTime.unboxToInt(this.apply(index));
   }

   public long apply$mcJ$sp(int index) {
      return BoxesRunTime.unboxToLong(this.apply(index));
   }

   public void $plus$eq$mcD$sp(double value) {
      this.$plus$eq(BoxesRunTime.boxToDouble(value));
   }

   public void $plus$eq$mcI$sp(int value) {
      this.$plus$eq(BoxesRunTime.boxToInteger(value));
   }

   public void $plus$eq$mcJ$sp(long value) {
      this.$plus$eq(BoxesRunTime.boxToLong(value));
   }

   public double[] array$mcD$sp() {
      return (double[])this.array();
   }

   public int[] array$mcI$sp() {
      return (int[])this.array();
   }

   public long[] array$mcJ$sp() {
      return (long[])this.array();
   }

   public PrimitiveVector<Object> trim$mcD$sp() {
      return this.trim();
   }

   public PrimitiveVector<Object> trim$mcI$sp() {
      return this.trim();
   }

   public PrimitiveVector<Object> trim$mcJ$sp() {
      return this.trim();
   }

   public PrimitiveVector<Object> resize$mcD$sp(int newLength) {
      return this.resize(newLength);
   }

   public PrimitiveVector<Object> resize$mcI$sp(int newLength) {
      return this.resize(newLength);
   }

   public PrimitiveVector<Object> resize$mcJ$sp(int newLength) {
      return this.resize(newLength);
   }

   public double[] toArray$mcD$sp() {
      return (double[])this.toArray();
   }

   public int[] toArray$mcI$sp() {
      return (int[])this.toArray();
   }

   public long[] toArray$mcJ$sp() {
      return (long[])this.toArray();
   }

   public double[] copyArrayWithLength$mcD$sp(int length) {
      return (double[])this.copyArrayWithLength(length);
   }

   public int[] copyArrayWithLength$mcI$sp(int length) {
      return (int[])this.copyArrayWithLength(length);
   }

   public long[] copyArrayWithLength$mcJ$sp(int length) {
      return (long[])this.copyArrayWithLength(length);
   }

   public boolean specInstance$() {
      return false;
   }

   public PrimitiveVector(int initialSize, ClassTag<V> evidence$1) {
      this.org$apache$spark$util$collection$PrimitiveVector$$evidence$1 = evidence$1;
      if (!this.specInstance$()) {
         this.org$apache$spark$util$collection$PrimitiveVector$$_numElements = 0;
         this._array_$eq(evidence$1.newArray(initialSize));
      }

   }
}
