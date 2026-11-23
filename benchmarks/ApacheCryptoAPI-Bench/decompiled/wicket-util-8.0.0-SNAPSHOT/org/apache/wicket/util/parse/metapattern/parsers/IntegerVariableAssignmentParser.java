package org.apache.wicket.util.parse.metapattern.parsers;

import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.IntegerGroup;
import org.apache.wicket.util.parse.metapattern.MetaPattern;

public final class IntegerVariableAssignmentParser extends MetaPatternParser {
   private static final Group variable;
   private static final IntegerGroup value;
   private static final MetaPattern pattern;

   public IntegerVariableAssignmentParser(CharSequence input) {
      super(pattern, input);
   }

   public String getVariable() {
      return variable.get(this.matcher());
   }

   public int getIntValue() {
      return value.getInt(this.matcher());
   }

   public long getLongValue() {
      return value.getLong(this.matcher());
   }

   static {
      variable = new Group(MetaPattern.VARIABLE_NAME);
      value = new IntegerGroup();
      pattern = new MetaPattern(new MetaPattern[]{variable, MetaPattern.OPTIONAL_WHITESPACE, MetaPattern.EQUALS, MetaPattern.OPTIONAL_WHITESPACE, value});
   }
}
