package org.apache.tika.sax.xpath;

public class AttributeMatcher extends Matcher {
   public static final Matcher INSTANCE = new AttributeMatcher();

   public boolean matchesAttribute(String namespace, String name) {
      return true;
   }
}
