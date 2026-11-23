package org.apache.tika.sax.xpath;

public class ElementMatcher extends Matcher {
   public static final Matcher INSTANCE = new ElementMatcher();

   public boolean matchesElement() {
      return true;
   }
}
