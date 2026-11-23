package org.apache.wicket.util.parse.metapattern;

import java.util.regex.Matcher;

public final class IntegerGroup extends Group {
   private static final long serialVersionUID = 1L;
   private final int radix;

   public IntegerGroup() {
      this(INTEGER);
   }

   public IntegerGroup(MetaPattern pattern) {
      this(pattern, 10);
   }

   public IntegerGroup(MetaPattern pattern, int radix) {
      super(pattern);
      this.radix = radix;
   }

   public int getInt(Matcher matcher) {
      return this.getInt(matcher, -1);
   }

   public int getInt(Matcher matcher, int defaultValue) {
      String value = this.get(matcher);
      return value == null ? defaultValue : Integer.parseInt(value, this.radix);
   }

   public long getLong(Matcher matcher) {
      return this.getLong(matcher, -1L);
   }

   public long getLong(Matcher matcher, long defaultValue) {
      String value = this.get(matcher);
      return value == null ? defaultValue : Long.parseLong(value, this.radix);
   }
}
