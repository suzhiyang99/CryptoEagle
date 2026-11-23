package org.apache.wicket.util.value;

import java.io.Serializable;
import org.apache.wicket.util.lang.Primitives;

public class IntValue implements Comparable<IntValue>, Serializable {
   private static final long serialVersionUID = 1L;
   protected final int value;

   public IntValue(int value) {
      this.value = value;
   }

   public final int compareTo(IntValue that) {
      if (this.value < that.value) {
         return -1;
      } else {
         return this.value > that.value ? 1 : 0;
      }
   }

   public final boolean equals(Object that) {
      if (that instanceof IntValue) {
         return this.value == ((IntValue)that).value;
      } else {
         return false;
      }
   }

   public final boolean greaterThan(int value) {
      return this.value > value;
   }

   public final boolean greaterThan(IntValue that) {
      return this.value > that.value;
   }

   public final int hashCode() {
      return Primitives.hashCode((long)this.value);
   }

   public final boolean lessThan(int that) {
      return this.value < that;
   }

   public final boolean lessThan(IntValue that) {
      return this.value < that.value;
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
