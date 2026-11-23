package org.apache.tika.sax.xpath;

public class NodeMatcher extends Matcher {
   public static final Matcher INSTANCE = new NodeMatcher();

   public boolean matchesElement() {
      return true;
   }

   public boolean matchesAttribute(String namespace, String name) {
      return true;
   }

   public boolean matchesText() {
      return true;
   }
}
