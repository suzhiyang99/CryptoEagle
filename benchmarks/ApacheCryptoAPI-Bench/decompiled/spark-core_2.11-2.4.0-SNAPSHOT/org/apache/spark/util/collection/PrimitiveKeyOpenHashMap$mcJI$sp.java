package org.apache.spark.util.collection;

import scala.Function0;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.package$;
import scala.runtime.ScalaRunTime$;

public class PrimitiveKeyOpenHashMap$mcJI$sp extends PrimitiveKeyOpenHashMap<Object, Object> {
   public OpenHashSet<Object> _keySet$mcJ$sp;
   public int[] _values$mcI$sp;
   public int[] _oldValues$mcI$sp;
   private final int initialCapacity;
   private final ClassTag<Object> evidence$1;
   private final ClassTag<Object> evidence$2;

   public OpenHashSet<Object> _keySet$mcJ$sp() {
      return this._keySet$mcJ$sp;
   }

   public OpenHashSet<Object> _keySet() {
      return this._keySet$mcJ$sp();
   }

   public void _keySet$mcJ$sp_$eq(OpenHashSet<Object> x$1) {
      this._keySet$mcJ$sp = x$1;
   }

   public void _keySet_$eq(OpenHashSet<Object> x$1) {
      this._keySet$mcJ$sp_$eq(x$1);
   }

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

   public boolean contains(long k) {
      return this.contains$mcJ$sp(k);
   }

   public boolean contains$mcJ$sp(long k) {
      return this._keySet().getPos$mcJ$sp(k) != OpenHashSet$.MODULE$.INVALID_POS();
   }

   public int apply(long k) {
      return this.apply$mcJI$sp(k);
   }

   public int apply$mcJI$sp(long k) {
      int pos = this._keySet().getPos$mcJ$sp(k);
      return this._values()[pos];
   }

   public int getOrElse(long k, int elseValue) {
      return this.getOrElse$mcJI$sp(k, elseValue);
   }

   public int getOrElse$mcJI$sp(long k, int elseValue) {
      int pos = this._keySet().getPos$mcJ$sp(k);
      return pos >= 0 ? this._values()[pos] : elseValue;
   }

   public void update(long k, int v) {
      this.update$mcJI$sp(k, v);
   }

   public void update$mcJI$sp(long k, int v) {
      int pos = this._keySet().addWithoutResize$mcJ$sp(k) & OpenHashSet$.MODULE$.POSITION_MASK();
      this._values()[pos] = v;
      this._keySet().rehashIfNeeded$mcJ$sp(k, this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow(), this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move());
      this._oldValues_$eq((int[])null);
   }

   public int changeValue(long k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return this.changeValue$mcJI$sp(k, defaultValue, mergeValue);
   }

   public int changeValue$mcJI$sp(long k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      int pos = this._keySet().addWithoutResize$mcJ$sp(k);
      int var10000;
      if ((pos & OpenHashSet$.MODULE$.NONEXISTENCE_MASK()) != 0) {
         int newValue = defaultValue.apply$mcI$sp();
         this._values()[pos & OpenHashSet$.MODULE$.POSITION_MASK()] = newValue;
         this._keySet().rehashIfNeeded$mcJ$sp(k, this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow(), this.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move());
         var10000 = newValue;
      } else {
         this._values()[pos] = mergeValue.apply$mcII$sp(this._values()[pos]);
         var10000 = this._values()[pos];
      }

      return var10000;
   }

   public boolean specInstance$() {
      return true;
   }

   public PrimitiveKeyOpenHashMap$mcJI$sp(int initialCapacity, ClassTag<Object> evidence$1, ClassTag<Object> evidence$2) {
      boolean var7;
      Predef$ var10000;
      label25: {
         label27: {
            this.initialCapacity = initialCapacity;
            this.evidence$1 = evidence$1;
            this.evidence$2 = evidence$2;
            super(initialCapacity, evidence$1, evidence$2);
            var10000 = Predef$.MODULE$;
            ClassTag var10001 = package$.MODULE$.classTag(evidence$1);
            ClassTag var4 = package$.MODULE$.classTag(ClassTag$.MODULE$.Long());
            if (var10001 == null) {
               if (var4 == null) {
                  break label27;
               }
            } else if (var10001.equals(var4)) {
               break label27;
            }

            var10001 = package$.MODULE$.classTag(evidence$1);
            ClassTag var5 = package$.MODULE$.classTag(ClassTag$.MODULE$.Int());
            if (var10001 == null) {
               if (var5 == null) {
                  break label27;
               }
            } else if (var10001.equals(var5)) {
               break label27;
            }

            var7 = false;
            break label25;
         }

         var7 = true;
      }

      var10000.require(var7);
      this._keySet_$eq(new OpenHashSet(initialCapacity, evidence$1));
      this._values_$eq(evidence$2.newArray(this._keySet().capacity()));
      this._oldValues$mcI$sp = null;
      super.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$grow = new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PrimitiveKeyOpenHashMap $outer;

         public final void apply(int newCapacity) {
            this.apply$mcVI$sp(newCapacity);
         }

         public void apply$mcVI$sp(int newCapacity) {
            this.$outer._oldValues_$eq(this.$outer._values());
            this.$outer._values_$eq(this.$outer.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$evidence$2.newArray(newCapacity));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      super.org$apache$spark$util$collection$PrimitiveKeyOpenHashMap$$move = new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PrimitiveKeyOpenHashMap $outer;

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

   public PrimitiveKeyOpenHashMap$mcJI$sp(ClassTag<Object> evidence$3, ClassTag<Object> evidence$4) {
      this(64, evidence$3, evidence$4);
   }
}
