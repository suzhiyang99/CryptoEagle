package org.apache.wicket.util.parse.metapattern.parsers;

import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.parse.metapattern.OptionalMetaPattern;

public final class VariableAssignmentParser extends MetaPatternParser {
   private static final MetaPattern namespace;
   private final Group key;
   private final Group value;

   public VariableAssignmentParser(CharSequence input) {
      this(input, MetaPattern.STRING);
   }

   public VariableAssignmentParser(CharSequence input, MetaPattern valuePattern) {
      super(input);
      this.key = new Group(new MetaPattern(new MetaPattern[]{namespace, MetaPattern.XML_ATTRIBUTE_NAME}));
      this.value = new Group(valuePattern);
      MetaPattern variableAssignment = new MetaPattern(new MetaPattern[]{MetaPattern.OPTIONAL_WHITESPACE, MetaPattern.EQUALS, MetaPattern.OPTIONAL_WHITESPACE, this.value});
      this.setPattern(new MetaPattern(new MetaPattern[]{MetaPattern.OPTIONAL_WHITESPACE, this.key, new OptionalMetaPattern(variableAssignment), MetaPattern.OPTIONAL_WHITESPACE}));
   }

   public String getKey() {
      return this.key.get(this.matcher());
   }

   public String getValue() {
      return this.value.get(this.matcher());
   }

   static {
      namespace = new OptionalMetaPattern(new MetaPattern[]{MetaPattern.VARIABLE_NAME, MetaPattern.COLON, new OptionalMetaPattern(new MetaPattern[]{MetaPattern.VARIABLE_NAME, MetaPattern.COLON})});
   }
}
