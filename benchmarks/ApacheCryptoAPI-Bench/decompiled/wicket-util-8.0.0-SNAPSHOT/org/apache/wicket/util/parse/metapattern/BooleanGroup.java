package org.apache.wicket.util.parse.metapattern;

import java.util.regex.Matcher;

public final class BooleanGroup extends Group {
   private static final long serialVersionUID = 1L;

   public BooleanGroup() {
      super(new MetaPattern("true|false"));
   }

   public boolean getBoolean(Matcher matcher) {
      return this.getBoolean(matcher, false);
   }

   public boolean getBoolean(Matcher matcher, boolean defaultValue) {
      String value = this.get(matcher);
      return value == null ? defaultValue : value.equals("true");
   }
}
