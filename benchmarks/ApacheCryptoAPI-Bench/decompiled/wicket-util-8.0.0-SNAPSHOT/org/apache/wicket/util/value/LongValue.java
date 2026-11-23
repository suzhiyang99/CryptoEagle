package org.apache.wicket.util.value;

import java.io.Serializable;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Primitives;

public class LongValue implements Comparable<LongValue>, Serializable {
   private static final long serialVersionUID = 1L;
   protected final long value;

   public LongValue(long value) {
      this.value = value;
   }

   public final int compareTo(LongValue that) {
      if (this.value < that.value) {
         return -1;
      } else {
         return this.value > that.value ? 1 : 0;
      }
   }

   public final boolean equals(Object that) {
      if (that instanceof LongValue) {
         return this.value == ((LongValue)that).value;
      } else {
         return false;
      }
   }

   public final boolean greaterThan(long value) {
      return this.value > value;
   }

   public final boolean greaterThanOrEqual(long value) {
      return this.value >= value;
   }

   public final boolean greaterThan(LongValue that) {
      return this.value > that.value;
   }

   public final boolean greaterThanOrEqual(LongValue that) {
      return this.value >= that.value;
   }

   public final int hashCode() {
      return Primitives.hashCode(this.value);
   }

   public final boolean lessThan(long that) {
      return this.value < that;
   }

   public final boolean lessThanOrEqual(long that) {
      return this.value <= that;
   }

   public final boolean lessThan(LongValue that) {
      return this.value < that.value;
   }

   public final boolean lessThanOrEqual(LongValue that) {
      return this.value <= that.value;
   }

   public String toString() {
      return String.valueOf(this.value);
   }

   public static <T extends LongValue> T min(T lhs, T rhs) {
      Args.notNull(lhs, "lhs");
      Args.notNull(rhs, "rhs");
      return (T)(lhs.compareTo(rhs) < 0 ? lhs : rhs);
   }

   public static <T extends LongValue> T max(T lhs, T rhs) {
      Args.notNull(lhs, "lhs");
      Args.notNull(rhs, "rhs");
      return (T)(lhs.compareTo(rhs) > 0 ? lhs : rhs);
   }

   public static <T extends LongValue> T maxNullSafe(T lhs, T rhs) {
      if (lhs == rhs) {
         return lhs;
      } else if (lhs == null) {
         return rhs;
      } else {
         return (T)(rhs == null ? lhs : max(lhs, rhs));
      }
   }
}
