package org.apache.wicket.util.parse.metapattern.parsers;

import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.MetaPattern;

public final class WordParser extends MetaPatternParser {
   private static final Group word;
   private static final MetaPattern wordPattern;

   public WordParser(CharSequence input) {
      super(wordPattern, input);
   }

   public String getWord() {
      return word.get(this.matcher());
   }

   static {
      word = new Group(MetaPattern.WORD);
      wordPattern = new MetaPattern(new MetaPattern[]{MetaPattern.OPTIONAL_WHITESPACE, word, MetaPattern.OPTIONAL_WHITESPACE});
   }
}
