package org.apache.spark.util.collection;

import scala.Function1;
import scala.Function2;
import scala.Serializable;
import scala.runtime.BoxedUnit;

public final class OpenHashSet$ implements Serializable {
   public static final OpenHashSet$ MODULE$;
   private final int MAX_CAPACITY;
   private final int INVALID_POS;
   private final int NONEXISTENCE_MASK;
   private final int POSITION_MASK;
   private final Function1<Object, BoxedUnit> org$apache$spark$util$collection$OpenHashSet$$grow;
   private final Function2<Object, Object, BoxedUnit> org$apache$spark$util$collection$OpenHashSet$$move;

   static {
      new OpenHashSet$();
   }

   public int MAX_CAPACITY() {
      return this.MAX_CAPACITY;
   }

   public int INVALID_POS() {
      return this.INVALID_POS;
   }

   public int NONEXISTENCE_MASK() {
      return this.NONEXISTENCE_MASK;
   }

   public int POSITION_MASK() {
      return this.POSITION_MASK;
   }

   public void org$apache$spark$util$collection$OpenHashSet$$grow1(int newSize) {
   }

   public void org$apache$spark$util$collection$OpenHashSet$$move1(int oldPos, int newPos) {
   }

   public Function1<Object, BoxedUnit> org$apache$spark$util$collection$OpenHashSet$$grow() {
      return this.org$apache$spark$util$collection$OpenHashSet$$grow;
   }

   public Function2<Object, Object, BoxedUnit> org$apache$spark$util$collection$OpenHashSet$$move() {
      return this.org$apache$spark$util$collection$OpenHashSet$$move;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private OpenHashSet$() {
      MODULE$ = this;
      this.MAX_CAPACITY = 1073741824;
      this.INVALID_POS = -1;
      this.NONEXISTENCE_MASK = Integer.MIN_VALUE;
      this.POSITION_MASK = Integer.MAX_VALUE;
      this.org$apache$spark$util$collection$OpenHashSet$$grow = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(int newSize) {
            this.apply$mcVI$sp(newSize);
         }

         public void apply$mcVI$sp(int newSize) {
            OpenHashSet$.MODULE$.org$apache$spark$util$collection$OpenHashSet$$grow1(newSize);
         }
      };
      this.org$apache$spark$util$collection$OpenHashSet$$move = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(int oldPos, int newPos) {
            this.apply$mcVII$sp(oldPos, newPos);
         }

         public void apply$mcVII$sp(int oldPos, int newPos) {
            OpenHashSet$.MODULE$.org$apache$spark$util$collection$OpenHashSet$$move1(oldPos, newPos);
         }
      };
   }
}
