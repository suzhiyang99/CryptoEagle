package org.apache.spark.util.collection;

import scala.Function0;
import scala.Function1;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public class OpenHashMap$mcI$sp<K> extends OpenHashMap<K, Object> {
   public int[] _values$mcI$sp;
   public transient int[] _oldValues$mcI$sp;
   public int nullValue$mcI$sp;
   private final int initialCapacity;
   private final ClassTag<K> evidence$1;
   private final ClassTag<Object> evidence$2;

   public int[] _values$mcI$sp() {
      return this._values$mcI$sp;
   }

   public int[] _values() {
      return this._values$mcI$sp();
   }

   public void _values$mcI$sp_$eq(int[] x$1) {
      this._values$mcI$sp = x$1;
   }

   public void _values_$eq(int[] x$1) {
      this._values$mcI$sp_$eq(x$1);
   }

   public int[] _oldValues$mcI$sp() {
      return this._oldValues$mcI$sp;
   }

   public int[] _oldValues() {
      return this._oldValues$mcI$sp();
   }

   public void _oldValues$mcI$sp_$eq(int[] x$1) {
      this._oldValues$mcI$sp = x$1;
   }

   public void _oldValues_$eq(int[] x$1) {
      this._oldValues$mcI$sp_$eq(x$1);
   }

   public int nullValue$mcI$sp() {
      return this.nullValue$mcI$sp;
   }

   public int nullValue() {
      return this.nullValue$mcI$sp();
   }

   public void nullValue$mcI$sp_$eq(int x$1) {
      this.nullValue$mcI$sp = x$1;
   }

   public void nullValue_$eq(int x$1) {
      this.nullValue$mcI$sp_$eq(x$1);
   }

   public int apply(K k) {
      return this.apply$mcI$sp(k);
   }

   public int apply$mcI$sp(K k) {
      int var10000;
      if (k == null) {
         var10000 = this.nullValue();
      } else {
         int pos = this.org$apache$spark$util$collection$OpenHashMap$$_keySet().getPos(k);
         var10000 = pos < 0 ? BoxesRunTime.unboxToInt((Object)null) : this._values()[pos];
      }

      return var10000;
   }

   public void update(K k, int v) {
      this.update$mcI$sp(k, v);
   }

   public void update$mcI$sp(K k, int v) {
      if (k == null) {
         this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue_$eq(true);
         this.nullValue_$eq(v);
      } else {
         int pos = this.org$apache$spark$util$collection$OpenHashMap$$_keySet().addWithoutResize(k) & OpenHashSet$.MODULE$.POSITION_MASK();
         this._values()[pos] = v;
         this.org$apache$spark$util$collection$OpenHashMap$$_keySet().rehashIfNeeded(k, this.org$apache$spark$util$collection$OpenHashMap$$grow(), this.org$apache$spark$util$collection$OpenHashMap$$move());
         this._oldValues_$eq((int[])null);
      }

   }

   public int changeValue(K k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return this.changeValue$mcI$sp(k, defaultValue, mergeValue);
   }

   public int changeValue$mcI$sp(K k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      int var10000;
      if (k == null) {
         if (this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue()) {
            this.nullValue_$eq(mergeValue.apply$mcII$sp(this.nullValue()));
         } else {
            this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue_$eq(true);
            this.nullValue_$eq(defaultValue.apply$mcI$sp());
         }

         var10000 = this.nullValue();
      } else {
         int pos = this.org$apache$spark$util$collection$OpenHashMap$$_keySet().addWithoutResize(k);
         if ((pos & OpenHashSet$.MODULE$.NONEXISTENCE_MASK()) != 0) {
            int newValue = defaultValue.apply$mcI$sp();
            this._values()[pos & OpenHashSet$.MODULE$.POSITION_MASK()] = newValue;
            this.org$apache$spark$util$collection$OpenHashMap$$_keySet().rehashIfNeeded(k, this.org$apache$spark$util$collection$OpenHashMap$$grow(), this.org$apache$spark$util$collection$OpenHashMap$$move());
            var10000 = newValue;
         } else {
            this._values()[pos] = mergeValue.apply$mcII$sp(this._values()[pos]);
            var10000 = this._values()[pos];
         }
      }

      return var10000;
   }

   public boolean specInstance$() {
      return true;
   }

   public OpenHashMap$mcI$sp(int initialCapacity, ClassTag<K> evidence$1, ClassTag<Object> evidence$2) {
      super(initialCapacity, evidence$1, evidence$2);
      this.initialCapacity = initialCapacity;
      this.evidence$1 = evidence$1;
      this.evidence$2 = evidence$2;
      super.org$apache$spark$util$collection$OpenHashMap$$_keySet = new OpenHashSet<K>(initialCapacity, evidence$1);
      this._values_$eq(evidence$2.newArray(this.org$apache$spark$util$collection$OpenHashMap$$_keySet().capacity()));
      this._oldValues$mcI$sp = null;
      super.org$apache$spark$util$collection$OpenHashMap$$haveNullValue = false;
      this.nullValue$mcI$sp = BoxesRunTime.unboxToInt((Object)null);
      super.org$apache$spark$util$collection$OpenHashMap$$grow = new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OpenHashMap $outer;

         public final void apply(int newCapacity) {
            this.apply$mcVI$sp(newCapacity);
         }

         public void apply$mcVI$sp(int newCapacity) {
            this.$outer._oldValues_$eq(this.$outer._values());
            this.$outer._values_$eq(this.$outer.org$apache$spark$util$collection$OpenHashMap$$evidence$2.newArray(newCapacity));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      super.org$apache$spark$util$collection$OpenHashMap$$move = new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OpenHashMap $outer;

         public final void apply(int oldPos, int newPos) {
            this.apply$mcVII$sp(oldPos, newPos);
         }

         public void apply$mcVII$sp(int oldPos, int newPos) {
            ScalaRunTime$.MODULE$.array_update(this.$outer._values(), newPos, ScalaRunTime$.MODULE$.array_apply(this.$outer._oldValues(), oldPos));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }

   public OpenHashMap$mcI$sp(ClassTag<K> evidence$3, ClassTag<Object> evidence$4) {
      this(64, evidence$3, evidence$4);
   }
}
