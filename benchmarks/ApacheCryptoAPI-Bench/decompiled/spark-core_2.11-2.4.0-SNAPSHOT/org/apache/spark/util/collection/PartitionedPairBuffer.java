package org.apache.spark.util.collection;

import java.util.Comparator;
import java.util.NoSuchElementException;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
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
import scala.collection.mutable.Queue;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ub!B\u0001\u0003\u0001\u0019a!!\u0006)beRLG/[8oK\u0012\u0004\u0016-\u001b:Ck\u001a4WM\u001d\u0006\u0003\u0007\u0011\t!bY8mY\u0016\u001cG/[8o\u0015\t)a!\u0001\u0003vi&d'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0016\u00075QRe\u0005\u0003\u0001\u001dQ9\u0003CA\b\u0013\u001b\u0005\u0001\"\"A\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005M\u0001\"AB!osJ+g\r\u0005\u0003\u0016-a!S\"\u0001\u0002\n\u0005]\u0011!!I,sSR\f'\r\\3QCJ$\u0018\u000e^5p]\u0016$\u0007+Y5s\u0007>dG.Z2uS>t\u0007CA\r\u001b\u0019\u0001!Qa\u0007\u0001C\u0002u\u0011\u0011aS\u0002\u0001#\tq\u0012\u0005\u0005\u0002\u0010?%\u0011\u0001\u0005\u0005\u0002\b\u001d>$\b.\u001b8h!\ty!%\u0003\u0002$!\t\u0019\u0011I\\=\u0011\u0005e)C!\u0002\u0014\u0001\u0005\u0004i\"!\u0001,\u0011\u0005UA\u0013BA\u0015\u0003\u0005-\u0019\u0016N_3Ue\u0006\u001c7.\u001a:\t\u0011-\u0002!\u0011!Q\u0001\n1\nq\"\u001b8ji&\fGnQ1qC\u000eLG/\u001f\t\u0003\u001f5J!A\f\t\u0003\u0007%sG\u000fC\u00031\u0001\u0011\u0005\u0011'\u0001\u0004=S:LGO\u0010\u000b\u0003eM\u0002B!\u0006\u0001\u0019I!91f\fI\u0001\u0002\u0004a\u0003bB\u001b\u0001\u0001\u0004%IAN\u0001\tG\u0006\u0004\u0018mY5usV\tA\u0006C\u00049\u0001\u0001\u0007I\u0011B\u001d\u0002\u0019\r\f\u0007/Y2jif|F%Z9\u0015\u0005ij\u0004CA\b<\u0013\ta\u0004C\u0001\u0003V]&$\bb\u0002 8\u0003\u0003\u0005\r\u0001L\u0001\u0004q\u0012\n\u0004B\u0002!\u0001A\u0003&A&A\u0005dCB\f7-\u001b;zA!9!\t\u0001a\u0001\n\u00131\u0014aB2veNK'0\u001a\u0005\b\t\u0002\u0001\r\u0011\"\u0003F\u0003-\u0019WO]*ju\u0016|F%Z9\u0015\u0005i2\u0005b\u0002 D\u0003\u0003\u0005\r\u0001\f\u0005\u0007\u0011\u0002\u0001\u000b\u0015\u0002\u0017\u0002\u0011\r,(oU5{K\u0002BqA\u0013\u0001A\u0002\u0013%1*\u0001\u0003eCR\fW#\u0001'\u0011\u0007=ie\"\u0003\u0002O!\t)\u0011I\u001d:bs\"9\u0001\u000b\u0001a\u0001\n\u0013\t\u0016\u0001\u00033bi\u0006|F%Z9\u0015\u0005i\u0012\u0006b\u0002 P\u0003\u0003\u0005\r\u0001\u0014\u0005\u0007)\u0002\u0001\u000b\u0015\u0002'\u0002\u000b\u0011\fG/\u0019\u0011\t\u000bY\u0003A\u0011A,\u0002\r%t7/\u001a:u)\u0011Q\u0004L\u0017/\t\u000be+\u0006\u0019\u0001\u0017\u0002\u0013A\f'\u000f^5uS>t\u0007\"B.V\u0001\u0004A\u0012aA6fs\")Q,\u0016a\u0001I\u0005)a/\u00197vK\")q\f\u0001C\u0005A\u0006IqM]8x\u0003J\u0014\u0018-\u001f\u000b\u0002u!)!\r\u0001C!G\u0006!\u0003/\u0019:uSRLwN\\3e\t\u0016\u001cHO];di&4XmU8si\u0016$\u0017\n^3sCR|'\u000f\u0006\u0002eiB\u0019Q-\u001c9\u000f\u0005\u0019\\gBA4k\u001b\u0005A'BA5\u001d\u0003\u0019a$o\\8u}%\t\u0011#\u0003\u0002m!\u00059\u0001/Y2lC\u001e,\u0017B\u00018p\u0005!IE/\u001a:bi>\u0014(B\u00017\u0011!\u0011y\u0011o\u001d\u0013\n\u0005I\u0004\"A\u0002+va2,'\u0007\u0005\u0003\u0010c2B\u0002\"B;b\u0001\u00041\u0018!D6fs\u000e{W\u000e]1sCR|'\u000fE\u0002\u0010ofL!\u0001\u001f\t\u0003\r=\u0003H/[8o!\rQh\u0010G\u0007\u0002w*\u0011Q\u0001 \u0006\u0002{\u0006!!.\u0019<b\u0013\ty8P\u0001\u0006D_6\u0004\u0018M]1u_JDq!a\u0001\u0001\t\u0013\t)!\u0001\u0005ji\u0016\u0014\u0018\r^8s)\u0005!w\u0001CA\u0005\u0005!%a!a\u0003\u0002+A\u000b'\u000f^5uS>tW\r\u001a)bSJ\u0014UO\u001a4feB\u0019Q#!\u0004\u0007\u000f\u0005\u0011\u0001\u0012\u0002\u0004\u0002\u0010M\u0019\u0011Q\u0002\b\t\u000fA\ni\u0001\"\u0001\u0002\u0014Q\u0011\u00111\u0002\u0005\n\u0003/\tiA1A\u0005\u0002Y\n\u0001#T!Y\u00136+VjX\"B!\u0006\u001b\u0015\nV-\t\u0011\u0005m\u0011Q\u0002Q\u0001\n1\n\u0011#T!Y\u00136+VjX\"B!\u0006\u001b\u0015\nV-!\u0011)\ty\"!\u0004\u0012\u0002\u0013\u0005\u0011\u0011E\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u0019\u0016\r\u0005\r\u0012\u0011HA\u001e+\t\t)CK\u0002-\u0003OY#!!\u000b\u0011\t\u0005-\u0012QG\u0007\u0003\u0003[QA!a\f\u00022\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003g\u0001\u0012AC1o]>$\u0018\r^5p]&!\u0011qGA\u0017\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0003\u00077\u0005u!\u0019A\u000f\u0005\r\u0019\niB1\u0001\u001e\u0001"
)
public class PartitionedPairBuffer<K, V> implements WritablePartitionedPairCollection<K, V>, SizeTracker {
   private int capacity;
   private int org$apache$spark$util$collection$PartitionedPairBuffer$$curSize;
   private Object[] org$apache$spark$util$collection$PartitionedPairBuffer$$data;
   private final double org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE;
   private final Queue<SizeTracker.Sample> org$apache$spark$util$collection$SizeTracker$$samples;
   private double org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate;
   private long org$apache$spark$util$collection$SizeTracker$$numUpdates;
   private long org$apache$spark$util$collection$SizeTracker$$nextSampleNum;

   public static <K, V> int $lessinit$greater$default$1() {
      return PartitionedPairBuffer$.MODULE$.$lessinit$greater$default$1();
   }

   public static int MAXIMUM_CAPACITY() {
      return PartitionedPairBuffer$.MODULE$.MAXIMUM_CAPACITY();
   }

   public double org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE() {
      return this.org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE;
   }

   public Queue<SizeTracker.Sample> org$apache$spark$util$collection$SizeTracker$$samples() {
      return this.org$apache$spark$util$collection$SizeTracker$$samples;
   }

   public double org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate() {
      return this.org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate;
   }

   public void org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate_$eq(double x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate = x$1;
   }

   public long org$apache$spark$util$collection$SizeTracker$$numUpdates() {
      return this.org$apache$spark$util$collection$SizeTracker$$numUpdates;
   }

   public void org$apache$spark$util$collection$SizeTracker$$numUpdates_$eq(long x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$numUpdates = x$1;
   }

   public long org$apache$spark$util$collection$SizeTracker$$nextSampleNum() {
      return this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum;
   }

   public void org$apache$spark$util$collection$SizeTracker$$nextSampleNum_$eq(long x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum = x$1;
   }

   public void org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE_$eq(double x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE = x$1;
   }

   public void org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$samples_$eq(Queue x$1) {
      this.org$apache$spark$util$collection$SizeTracker$$samples = x$1;
   }

   public void resetSamples() {
      SizeTracker$class.resetSamples(this);
   }

   public void afterUpdate() {
      SizeTracker$class.afterUpdate(this);
   }

   public long estimateSize() {
      return SizeTracker$class.estimateSize(this);
   }

   public WritablePartitionedIterator destructiveSortedWritablePartitionedIterator(Option<Comparator<K>> keyComparator) {
      return WritablePartitionedPairCollection$class.destructiveSortedWritablePartitionedIterator(this, keyComparator);
   }

   private int capacity() {
      return this.capacity;
   }

   private void capacity_$eq(int x$1) {
      this.capacity = x$1;
   }

   public int org$apache$spark$util$collection$PartitionedPairBuffer$$curSize() {
      return this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize;
   }

   private void org$apache$spark$util$collection$PartitionedPairBuffer$$curSize_$eq(int x$1) {
      this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize = x$1;
   }

   public Object[] org$apache$spark$util$collection$PartitionedPairBuffer$$data() {
      return this.org$apache$spark$util$collection$PartitionedPairBuffer$$data;
   }

   private void org$apache$spark$util$collection$PartitionedPairBuffer$$data_$eq(Object[] x$1) {
      this.org$apache$spark$util$collection$PartitionedPairBuffer$$data = x$1;
   }

   public void insert(int partition, K key, V value) {
      if (this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize() == this.capacity()) {
         this.growArray();
      }

      this.org$apache$spark$util$collection$PartitionedPairBuffer$$data()[2 * this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize()] = new Tuple2(BoxesRunTime.boxToInteger(partition), key);
      this.org$apache$spark$util$collection$PartitionedPairBuffer$$data()[2 * this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize() + 1] = value;
      this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize_$eq(this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize() + 1);
      this.afterUpdate();
   }

   private void growArray() {
      if (this.capacity() >= PartitionedPairBuffer$.MODULE$.MAXIMUM_CAPACITY()) {
         throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't insert more than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(PartitionedPairBuffer$.MODULE$.MAXIMUM_CAPACITY())})));
      } else {
         int newCapacity = this.capacity() * 2 > PartitionedPairBuffer$.MODULE$.MAXIMUM_CAPACITY() ? PartitionedPairBuffer$.MODULE$.MAXIMUM_CAPACITY() : this.capacity() * 2;
         Object[] newArray = new Object[2 * newCapacity];
         System.arraycopy(this.org$apache$spark$util$collection$PartitionedPairBuffer$$data(), 0, newArray, 0, 2 * this.capacity());
         this.org$apache$spark$util$collection$PartitionedPairBuffer$$data_$eq(newArray);
         this.capacity_$eq(newCapacity);
         this.resetSamples();
      }
   }

   public Iterator<Tuple2<Tuple2<Object, K>, V>> partitionedDestructiveSortedIterator(Option<Comparator<K>> keyComparator) {
      Comparator comparator = (Comparator)keyComparator.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Comparator<Tuple2<Object, K>> apply(Comparator<K> keyComparator) {
            return WritablePartitionedPairCollection$.MODULE$.partitionKeyComparator(keyComparator);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Comparator<Tuple2<Object, K>> apply() {
            return WritablePartitionedPairCollection$.MODULE$.partitionComparator();
         }
      });
      (new Sorter(new KVArraySortDataFormat(ClassTag$.MODULE$.AnyRef()))).sort(this.org$apache$spark$util$collection$PartitionedPairBuffer$$data(), 0, this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize(), comparator);
      return this.iterator();
   }

   private Iterator<Tuple2<Tuple2<Object, K>, V>> iterator() {
      return new Iterator<Tuple2<Tuple2<Object, K>, V>>(this) {
         private int pos;
         // $FF: synthetic field
         private final PartitionedPairBuffer $outer;

         public Iterator<Tuple2<Tuple2<Object, K>, V>> seq() {
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

         public Iterator<Tuple2<Tuple2<Object, K>, V>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Tuple2<Tuple2<Object, K>, V>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Tuple2<Tuple2<Object, K>, V>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> filter(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<Tuple2<Object, K>, V>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> withFilter(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> filterNot(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Tuple2<Tuple2<Object, K>, V>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<Tuple2<Object, K>, V>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Tuple2<Tuple2<Object, K>, V>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> takeWhile(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Tuple2<Tuple2<Object, K>, V>>, Iterator<Tuple2<Tuple2<Object, K>, V>>> partition(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Tuple2<Tuple2<Object, K>, V>>, Iterator<Tuple2<Tuple2<Object, K>, V>>> span(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> dropWhile(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Tuple2<Tuple2<Object, K>, V>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Tuple2<Tuple2<Object, K>, V>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Tuple2<Tuple2<Object, K>, V>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Tuple2<Tuple2<Object, K>, V>> find(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Tuple2<Tuple2<Object, K>, V>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Tuple2<Tuple2<Object, K>, V>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Tuple2<Tuple2<Object, K>, V>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Tuple2<Tuple2<Object, K>, V>>, Iterator<Tuple2<Tuple2<Object, K>, V>>> duplicate() {
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

         public Traversable<Tuple2<Tuple2<Object, K>, V>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Tuple2<Tuple2<Object, K>, V>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Tuple2<Tuple2<Object, K>, V>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Tuple2<Tuple2<Object, K>, V>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Tuple2<Tuple2<Object, K>, V>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Tuple2<Tuple2<Object, K>, V>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Tuple2<Tuple2<Object, K>, V>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Tuple2<Tuple2<Object, K>, V>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Tuple2<Tuple2<Object, K>, V>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Tuple2<Tuple2<Object, K>, V>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Tuple2<Tuple2<Object, K>, V>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Tuple2<Tuple2<Object, K>, V>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<Tuple2<Object, K>, V>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Tuple2<Tuple2<Object, K>, V>, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<Tuple2<Object, K>, V>, B> seqop, Function2<B, B, B> combop) {
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

         public List<Tuple2<Tuple2<Object, K>, V>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Tuple2<Tuple2<Object, K>, V>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Tuple2<Tuple2<Object, K>, V>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Tuple2<Tuple2<Object, K>, V>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Tuple2<Tuple2<Object, K>, V>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<Tuple2<Object, K>, V>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<Tuple2<Object, K>, V>, Tuple2<T, U>> ev) {
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
            return this.pos() < this.$outer.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize();
         }

         public Tuple2<Tuple2<Object, K>, V> next() {
            if (this.hasNext()) {
               Tuple2 pair = new Tuple2((Tuple2)this.$outer.org$apache$spark$util$collection$PartitionedPairBuffer$$data()[2 * this.pos()], this.$outer.org$apache$spark$util$collection$PartitionedPairBuffer$$data()[2 * this.pos() + 1]);
               this.pos_$eq(this.pos() + 1);
               return pair;
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

   public PartitionedPairBuffer(int initialCapacity) {
      WritablePartitionedPairCollection$class.$init$(this);
      SizeTracker$class.$init$(this);
      Predef$.MODULE$.require(initialCapacity <= PartitionedPairBuffer$.MODULE$.MAXIMUM_CAPACITY(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't make capacity bigger than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(PartitionedPairBuffer$.MODULE$.MAXIMUM_CAPACITY())}));
         }
      });
      Predef$.MODULE$.require(initialCapacity >= 1, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Invalid initial capacity";
         }
      });
      this.capacity = initialCapacity;
      this.org$apache$spark$util$collection$PartitionedPairBuffer$$curSize = 0;
      this.org$apache$spark$util$collection$PartitionedPairBuffer$$data = new Object[2 * initialCapacity];
   }
}
