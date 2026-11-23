package org.apache.spark.util.collection;

import org.apache.spark.annotation.Private;
import scala.Function1;
import scala.Function2;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.package$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@Private
public class OpenHashSet$mcJ$sp extends OpenHashSet<Object> {
   public final OpenHashSet.Hasher<Object> hasher$mcJ$sp;
   public long[] _data$mcJ$sp;
   private final int initialCapacity;
   private final double loadFactor;
   private final ClassTag<Object> evidence$1;

   public OpenHashSet.Hasher<Object> hasher$mcJ$sp() {
      return this.hasher$mcJ$sp;
   }

   public OpenHashSet.Hasher<Object> hasher() {
      return this.hasher$mcJ$sp();
   }

   public long[] _data$mcJ$sp() {
      return this._data$mcJ$sp;
   }

   public long[] _data() {
      return this._data$mcJ$sp();
   }

   public void _data$mcJ$sp_$eq(long[] x$1) {
      this._data$mcJ$sp = x$1;
   }

   public void _data_$eq(long[] x$1) {
      this._data$mcJ$sp_$eq(x$1);
   }

   public boolean contains(long k) {
      return this.contains$mcJ$sp(k);
   }

   public boolean contains$mcJ$sp(long k) {
      return this.getPos$mcJ$sp(k) != OpenHashSet$.MODULE$.INVALID_POS();
   }

   public void add(long k) {
      this.add$mcJ$sp(k);
   }

   public void add$mcJ$sp(long k) {
      this.addWithoutResize$mcJ$sp(k);
      this.rehashIfNeeded$mcJ$sp(k, OpenHashSet$.MODULE$.org$apache$spark$util$collection$OpenHashSet$$grow(), OpenHashSet$.MODULE$.org$apache$spark$util$collection$OpenHashSet$$move());
   }

   public OpenHashSet<Object> union(OpenHashSet<Object> other) {
      return this.union$mcJ$sp(other);
   }

   public OpenHashSet<Object> union$mcJ$sp(OpenHashSet<Object> other) {
      Iterator iterator = other.iterator();

      while(iterator.hasNext()) {
         this.add$mcJ$sp(BoxesRunTime.unboxToLong(iterator.next()));
      }

      return this;
   }

   public int addWithoutResize(long k) {
      return this.addWithoutResize$mcJ$sp(k);
   }

   public int addWithoutResize$mcJ$sp(long k) {
      int pos = this.org$apache$spark$util$collection$OpenHashSet$$hashcode(this.hasher().hash$mcJ$sp(k)) & this.org$apache$spark$util$collection$OpenHashSet$$_mask();

      for(int delta = 1; this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(pos); ++delta) {
         if (this._data()[pos] == k) {
            return pos;
         }

         pos = pos + delta & this.org$apache$spark$util$collection$OpenHashSet$$_mask();
      }

      this._data()[pos] = k;
      this.org$apache$spark$util$collection$OpenHashSet$$_bitset().set(pos);
      this.org$apache$spark$util$collection$OpenHashSet$$_size_$eq(this.org$apache$spark$util$collection$OpenHashSet$$_size() + 1);
      return pos | OpenHashSet$.MODULE$.NONEXISTENCE_MASK();
   }

   public void rehashIfNeeded(long k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      this.rehashIfNeeded$mcJ$sp(k, allocateFunc, moveFunc);
   }

   public void rehashIfNeeded$mcJ$sp(long k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      if (this.org$apache$spark$util$collection$OpenHashSet$$_size() > this.org$apache$spark$util$collection$OpenHashSet$$_growThreshold()) {
         this.rehash$mcJ$sp(k, allocateFunc, moveFunc);
      }

   }

   public int getPos(long k) {
      return this.getPos$mcJ$sp(k);
   }

   public int getPos$mcJ$sp(long k) {
      int pos = this.org$apache$spark$util$collection$OpenHashSet$$hashcode(this.hasher().hash$mcJ$sp(k)) & this.org$apache$spark$util$collection$OpenHashSet$$_mask();

      for(int delta = 1; this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(pos); ++delta) {
         if (k == this._data()[pos]) {
            return pos;
         }

         pos = pos + delta & this.org$apache$spark$util$collection$OpenHashSet$$_mask();
      }

      return OpenHashSet$.MODULE$.INVALID_POS();
   }

   public long getValue(int pos) {
      return this.getValue$mcJ$sp(pos);
   }

   public long getValue$mcJ$sp(int pos) {
      return this._data()[pos];
   }

   public long getValueSafe(int pos) {
      return this.getValueSafe$mcJ$sp(pos);
   }

   public long getValueSafe$mcJ$sp(int pos) {
      Predef$.MODULE$.assert(this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(pos));
      return this._data()[pos];
   }

   public void rehash(long k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      this.rehash$mcJ$sp(k, allocateFunc, moveFunc);
   }

   public void rehash$mcJ$sp(long k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      int newCapacity = this.org$apache$spark$util$collection$OpenHashSet$$_capacity() * 2;
      Predef$.MODULE$.require(newCapacity > 0 && newCapacity <= OpenHashSet$.MODULE$.MAX_CAPACITY(), new Serializable(this) {
         // $FF: synthetic field
         private final OpenHashSet$mcJ$sp $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't contain more than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger((int)(this.$outer.org$apache$spark$util$collection$OpenHashSet$$loadFactor * (double)OpenHashSet$.MODULE$.MAX_CAPACITY()))}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      allocateFunc.apply$mcVI$sp(newCapacity);
      BitSet newBitset = new BitSet(newCapacity);
      long[] newData = (long[])this.org$apache$spark$util$collection$OpenHashSet$$evidence$1.newArray(newCapacity);
      int newMask = newCapacity - 1;

      for(int oldPos = 0; oldPos < this.capacity(); ++oldPos) {
         if (this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(oldPos)) {
            long key = this._data()[oldPos];
            int newPos = this.org$apache$spark$util$collection$OpenHashSet$$hashcode(this.hasher().hash$mcJ$sp(key)) & newMask;
            int i = 1;
            boolean keepGoing = true;

            while(keepGoing) {
               if (newBitset.get(newPos)) {
                  newPos = newPos + i & newMask;
                  ++i;
               } else {
                  newData[newPos] = key;
                  newBitset.set(newPos);
                  moveFunc.apply$mcVII$sp(oldPos, newPos);
                  keepGoing = false;
               }
            }
         }
      }

      this.org$apache$spark$util$collection$OpenHashSet$$_bitset_$eq(newBitset);
      this._data_$eq(newData);
      this.org$apache$spark$util$collection$OpenHashSet$$_capacity_$eq(newCapacity);
      this.org$apache$spark$util$collection$OpenHashSet$$_mask_$eq(newMask);
      this.org$apache$spark$util$collection$OpenHashSet$$_growThreshold_$eq((int)(this.org$apache$spark$util$collection$OpenHashSet$$loadFactor * (double)newCapacity));
   }

   public boolean specInstance$() {
      return true;
   }

   public OpenHashSet$mcJ$sp(int initialCapacity, double loadFactor, ClassTag<Object> evidence$1) {
      Object var10001;
      label48: {
         label52: {
            this.initialCapacity = initialCapacity;
            this.loadFactor = loadFactor;
            this.evidence$1 = evidence$1;
            super(initialCapacity, loadFactor, evidence$1);
            Predef$.MODULE$.require(initialCapacity <= OpenHashSet$.MODULE$.MAX_CAPACITY(), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't make capacity bigger than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(OpenHashSet$.MODULE$.MAX_CAPACITY())}));
               }
            });
            Predef$.MODULE$.require(initialCapacity >= 0, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Invalid initial capacity";
               }
            });
            Predef$.MODULE$.require(loadFactor < (double)1.0F, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Load factor must be less than 1.0";
               }
            });
            Predef$.MODULE$.require(loadFactor > (double)0.0F, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Load factor must be greater than 0.0";
               }
            });
            ClassTag mt = package$.MODULE$.classTag(this.org$apache$spark$util$collection$OpenHashSet$$evidence$1);
            ClassTag var6 = ClassTag$.MODULE$.Long();
            if (mt == null) {
               if (var6 == null) {
                  break label52;
               }
            } else if (mt.equals(var6)) {
               break label52;
            }

            label40: {
               ClassTag var7 = ClassTag$.MODULE$.Int();
               if (mt == null) {
                  if (var7 == null) {
                     break label40;
                  }
               } else if (mt.equals(var7)) {
                  break label40;
               }

               var10001 = new OpenHashSet$Hasher$mcJ$sp();
               break label48;
            }

            var10001 = new OpenHashSet.IntHasher();
            break label48;
         }

         var10001 = new OpenHashSet.LongHasher();
      }

      this.hasher$mcJ$sp = (OpenHashSet.Hasher<Object>)var10001;
      super.org$apache$spark$util$collection$OpenHashSet$$_capacity = this.org$apache$spark$util$collection$OpenHashSet$$nextPowerOf2(initialCapacity);
      super.org$apache$spark$util$collection$OpenHashSet$$_mask = this.org$apache$spark$util$collection$OpenHashSet$$_capacity() - 1;
      super.org$apache$spark$util$collection$OpenHashSet$$_size = 0;
      super.org$apache$spark$util$collection$OpenHashSet$$_growThreshold = (int)(loadFactor * (double)this.org$apache$spark$util$collection$OpenHashSet$$_capacity());
      super.org$apache$spark$util$collection$OpenHashSet$$_bitset = new BitSet(this.org$apache$spark$util$collection$OpenHashSet$$_capacity());
      this._data_$eq(evidence$1.newArray(this.org$apache$spark$util$collection$OpenHashSet$$_capacity()));
   }

   public OpenHashSet$mcJ$sp(int initialCapacity, ClassTag<Object> evidence$2) {
      this(initialCapacity, 0.7, evidence$2);
   }

   public OpenHashSet$mcJ$sp(ClassTag<Object> evidence$3) {
      this(64, evidence$3);
   }
}
