package org.apache.wicket.util.parse.metapattern;

import java.util.regex.Matcher;

public final class FloatingPointGroup extends Group {
   private static final long serialVersionUID = 1L;

   public FloatingPointGroup() {
      super(FLOATING_POINT_NUMBER);
   }

   public float getFloat(Matcher matcher) {
      return this.getFloat(matcher, -1.0F);
   }

   public float getFloat(Matcher matcher, float defaultValue) {
      String value = this.get(matcher);
      return value == null ? defaultValue : Float.parseFloat(value);
   }

   public double getDouble(Matcher matcher) {
      return this.getDouble(matcher, (double)-1.0F);
   }

   public double getDouble(Matcher matcher, double defaultValue) {
      String value = this.get(matcher);
      return value == null ? defaultValue : Double.parseDouble(value);
   }
}
