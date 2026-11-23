package org.apache.wicket.util.parse.metapattern.parsers;

import org.apache.wicket.util.parse.metapattern.MetaPattern;

public final class CommaSeparatedVariableParser extends ListParser {
   private static final MetaPattern patternEntry;

   public CommaSeparatedVariableParser(CharSequence input) {
      super(patternEntry, MetaPattern.COMMA, input);
   }

   static {
      patternEntry = new MetaPattern(new MetaPattern[]{MetaPattern.OPTIONAL_WHITESPACE, MetaPattern.STRING, MetaPattern.OPTIONAL_WHITESPACE});
   }
}
