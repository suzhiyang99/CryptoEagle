package org.apache.spark.util.collection;

import java.util.Arrays;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
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
import scala.math.package$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001}4A!\u0001\u0002\u0001\u001b\t1!)\u001b;TKRT!a\u0001\u0003\u0002\u0015\r|G\u000e\\3di&|gN\u0003\u0002\u0006\r\u0005!Q\u000f^5m\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\n\u0004\u00019!\u0002CA\b\u0013\u001b\u0005\u0001\"\"A\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005M\u0001\"AB!osJ+g\r\u0005\u0002\u0010+%\u0011a\u0003\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t1\u0001\u0011\t\u0011)A\u00053\u00059a.^7CSR\u001c\bCA\b\u001b\u0013\tY\u0002CA\u0002J]RDQ!\b\u0001\u0005\u0002y\ta\u0001P5oSRtDCA\u0010\"!\t\u0001\u0003!D\u0001\u0003\u0011\u0015AB\u00041\u0001\u001a\u0011\u001d\u0019\u0003A1A\u0005\n\u0011\nQa^8sIN,\u0012!\n\t\u0004\u001f\u0019B\u0013BA\u0014\u0011\u0005\u0015\t%O]1z!\ty\u0011&\u0003\u0002+!\t!Aj\u001c8h\u0011\u0019a\u0003\u0001)A\u0005K\u00051qo\u001c:eg\u0002BqA\f\u0001C\u0002\u0013%q&\u0001\u0005ok6<vN\u001d3t+\u0005I\u0002BB\u0019\u0001A\u0003%\u0011$A\u0005ok6<vN\u001d3tA!)1\u0007\u0001C\u0001_\u0005A1-\u00199bG&$\u0018\u0010C\u00036\u0001\u0011\u0005a'A\u0003dY\u0016\f'\u000fF\u00018!\ty\u0001(\u0003\u0002:!\t!QK\\5u\u0011\u0015Y\u0004\u0001\"\u0001=\u0003!\u0019X\r^+oi&dGCA\u001c>\u0011\u0015q$\b1\u0001\u001a\u0003!\u0011\u0017\u000e^%oI\u0016D\b\"\u0002!\u0001\t\u0003\t\u0015AC2mK\u0006\u0014XK\u001c;jYR\u0011qG\u0011\u0005\u0006}}\u0002\r!\u0007\u0005\u0006\t\u0002!\t!R\u0001\u0005I\u0005l\u0007\u000f\u0006\u0002 \r\")qi\u0011a\u0001?\u0005)q\u000e\u001e5fe\")\u0011\n\u0001C\u0001\u0015\u0006!AEY1s)\ty2\nC\u0003H\u0011\u0002\u0007q\u0004C\u0003N\u0001\u0011\u0005a*A\u0002%kB$\"aH(\t\u000b\u001dc\u0005\u0019A\u0010\t\u000bE\u0003A\u0011\u0001*\u0002\r\u0005tGMT8u)\ty2\u000bC\u0003H!\u0002\u0007q\u0004C\u0003V\u0001\u0011\u0005a+A\u0002tKR$\"aN,\t\u000ba#\u0006\u0019A\r\u0002\u000b%tG-\u001a=\t\u000bi\u0003A\u0011A.\u0002\u000bUt7/\u001a;\u0015\u0005]b\u0006\"\u0002-Z\u0001\u0004I\u0002\"\u00020\u0001\t\u0003y\u0016aA4fiR\u0011\u0001m\u0019\t\u0003\u001f\u0005L!A\u0019\t\u0003\u000f\t{w\u000e\\3b]\")\u0001,\u0018a\u00013!)Q\r\u0001C\u0001M\u0006A\u0011\u000e^3sCR|'/F\u0001h!\rA\u0007/\u0007\b\u0003S:t!A[7\u000e\u0003-T!\u0001\u001c\u0007\u0002\rq\u0012xn\u001c;?\u0013\u0005\t\u0012BA8\u0011\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u001d:\u0003\u0011%#XM]1u_JT!a\u001c\t\t\u000bQ\u0004A\u0011A;\u0002\u0017\r\f'\u000fZ5oC2LG/\u001f\u000b\u00023!)q\u000f\u0001C\u0001q\u0006Qa.\u001a=u'\u0016$()\u001b;\u0015\u0005eI\b\"\u0002>w\u0001\u0004I\u0012!\u00034s_6Le\u000eZ3y\u0011\u0015a\b\u0001\"\u0003~\u0003%\u0011\u0017\u000e\u001e\u001ax_J$7\u000f\u0006\u0002\u001a}\")\u0001d\u001fa\u00013\u0001"
)
public class BitSet implements Serializable {
   private final long[] words;
   private final int numWords;

   private long[] words() {
      return this.words;
   }

   private int numWords() {
      return this.numWords;
   }

   public int capacity() {
      return this.numWords() * 64;
   }

   public void clear() {
      Arrays.fill(this.words(), 0L);
   }

   public void setUntil(int bitIndex) {
      int wordIndex = bitIndex >> 6;
      Arrays.fill(this.words(), 0, wordIndex, -1L);
      if (wordIndex < this.words().length) {
         long mask = ~(-1L << (bitIndex & 63));
         this.words()[wordIndex] |= mask;
      }

   }

   public void clearUntil(int bitIndex) {
      int wordIndex = bitIndex >> 6;
      Arrays.fill(this.words(), 0, wordIndex, 0L);
      if (wordIndex < this.words().length) {
         long mask = -1L << (bitIndex & 63);
         this.words()[wordIndex] &= mask;
      }

   }

   public BitSet $amp(BitSet other) {
      BitSet newBS = new BitSet(package$.MODULE$.max(this.capacity(), other.capacity()));
      int smaller = package$.MODULE$.min(this.numWords(), other.numWords());
      Predef$.MODULE$.assert(newBS.numWords() >= this.numWords());
      Predef$.MODULE$.assert(newBS.numWords() >= other.numWords());

      for(int ind = 0; ind < smaller; ++ind) {
         newBS.words()[ind] = this.words()[ind] & other.words()[ind];
      }

      return newBS;
   }

   public BitSet $bar(BitSet other) {
      BitSet newBS = new BitSet(package$.MODULE$.max(this.capacity(), other.capacity()));
      Predef$.MODULE$.assert(newBS.numWords() >= this.numWords());
      Predef$.MODULE$.assert(newBS.numWords() >= other.numWords());
      int smaller = package$.MODULE$.min(this.numWords(), other.numWords());

      int ind;
      for(ind = 0; ind < smaller; ++ind) {
         newBS.words()[ind] = this.words()[ind] | other.words()[ind];
      }

      while(ind < this.numWords()) {
         newBS.words()[ind] = this.words()[ind];
         ++ind;
      }

      while(ind < other.numWords()) {
         newBS.words()[ind] = other.words()[ind];
         ++ind;
      }

      return newBS;
   }

   public BitSet $up(BitSet other) {
      BitSet newBS = new BitSet(package$.MODULE$.max(this.capacity(), other.capacity()));
      int smaller = package$.MODULE$.min(this.numWords(), other.numWords());

      int ind;
      for(ind = 0; ind < smaller; ++ind) {
         newBS.words()[ind] = this.words()[ind] ^ other.words()[ind];
      }

      if (ind < this.numWords()) {
         Array$.MODULE$.copy(this.words(), ind, newBS.words(), ind, this.numWords() - ind);
      }

      if (ind < other.numWords()) {
         Array$.MODULE$.copy(other.words(), ind, newBS.words(), ind, other.numWords() - ind);
      }

      return newBS;
   }

   public BitSet andNot(BitSet other) {
      BitSet newBS = new BitSet(this.capacity());
      int smaller = package$.MODULE$.min(this.numWords(), other.numWords());

      int ind;
      for(ind = 0; ind < smaller; ++ind) {
         newBS.words()[ind] = this.words()[ind] & ~other.words()[ind];
      }

      if (ind < this.numWords()) {
         Array$.MODULE$.copy(this.words(), ind, newBS.words(), ind, this.numWords() - ind);
      }

      return newBS;
   }

   public void set(int index) {
      long bitmask = 1L << (index & 63);
      int var4 = index >> 6;
      this.words()[var4] |= bitmask;
   }

   public void unset(int index) {
      long bitmask = 1L << (index & 63);
      int var4 = index >> 6;
      this.words()[var4] &= ~bitmask;
   }

   public boolean get(int index) {
      long bitmask = 1L << (index & 63);
      return (this.words()[index >> 6] & bitmask) != 0L;
   }

   public Iterator<Object> iterator() {
      return new Iterator<Object>(this) {
         private int ind;
         // $FF: synthetic field
         private final BitSet $outer;

         public Iterator<Object> seq() {
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

         public Iterator<Object> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Object> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Object> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Object, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Object, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Object> filter(Function1<Object, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Object, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Object> withFilter(Function1<Object, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Object> filterNot(Function1<Object, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Object, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Object, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Object, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Object> takeWhile(Function1<Object, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Object>, Iterator<Object>> partition(Function1<Object, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Object>, Iterator<Object>> span(Function1<Object, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Object> dropWhile(Function1<Object, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Object, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Object, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Object, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Object, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Object, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Object> find(Function1<Object, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Object, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Object> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Object>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Object>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Object>, Iterator<Object>> duplicate() {
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

         public Traversable<Object> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Object> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Object> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Object> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Object, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Object, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Object, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Object, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Object, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Object, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Object, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Object, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Object, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Object, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, Object, B> seqop, Function2<B, B, B> combop) {
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

         public List<Object> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Object> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Object> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Object> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Object> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Object, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Object, Tuple2<T, U>> ev) {
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

         private int ind() {
            return this.ind;
         }

         private void ind_$eq(int x$1) {
            this.ind = x$1;
         }

         public boolean hasNext() {
            return this.ind() >= 0;
         }

         public int next() {
            int tmp = this.ind();
            this.ind_$eq(this.$outer.nextSetBit(this.ind() + 1));
            return tmp;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.ind = $outer.nextSetBit(0);
            }
         }
      };
   }

   public int cardinality() {
      int sum = 0;

      for(int i = 0; i < this.numWords(); ++i) {
         sum += Long.bitCount(this.words()[i]);
      }

      return sum;
   }

   public int nextSetBit(int fromIndex) {
      int wordIndex = fromIndex >> 6;
      if (wordIndex >= this.numWords()) {
         return -1;
      } else {
         int subIndex = fromIndex & 63;
         long word = this.words()[wordIndex] >> subIndex;
         if (word != 0L) {
            return (wordIndex << 6) + subIndex + Long.numberOfTrailingZeros(word);
         } else {
            ++wordIndex;

            while(wordIndex < this.numWords()) {
               word = this.words()[wordIndex];
               if (word != 0L) {
                  return (wordIndex << 6) + Long.numberOfTrailingZeros(word);
               }

               ++wordIndex;
            }

            return -1;
         }
      }
   }

   private int bit2words(int numBits) {
      return (numBits - 1 >> 6) + 1;
   }

   public BitSet(int numBits) {
      this.words = new long[this.bit2words(numBits)];
      this.numWords = this.words().length;
   }
}
