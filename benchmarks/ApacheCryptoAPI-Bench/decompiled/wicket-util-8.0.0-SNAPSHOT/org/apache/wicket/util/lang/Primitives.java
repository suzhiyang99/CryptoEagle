package org.apache.wicket.util.lang;

public final class Primitives {
   public static int hashCode(long value) {
      return (int)value ^ (int)(value >> 32);
   }
}
