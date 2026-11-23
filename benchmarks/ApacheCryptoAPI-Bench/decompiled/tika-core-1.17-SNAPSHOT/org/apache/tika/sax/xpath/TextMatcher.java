package org.apache.tika.sax.xpath;

public class TextMatcher extends Matcher {
   public static final Matcher INSTANCE = new TextMatcher();

   public boolean matchesText() {
      return true;
   }
}
