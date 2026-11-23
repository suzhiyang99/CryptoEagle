package org.apache.tika.sax.xpath;

public class NamedAttributeMatcher extends Matcher {
   private final String namespace;
   private final String name;

   public NamedAttributeMatcher(String namespace, String name) {
      this.namespace = namespace;
      this.name = name;
   }

   public boolean matchesAttribute(String namespace, String name) {
      return equals(namespace, this.namespace) && name.equals(this.name);
   }

   private static boolean equals(String a, String b) {
      return a == null ? b == null : a.equals(b);
   }
}
