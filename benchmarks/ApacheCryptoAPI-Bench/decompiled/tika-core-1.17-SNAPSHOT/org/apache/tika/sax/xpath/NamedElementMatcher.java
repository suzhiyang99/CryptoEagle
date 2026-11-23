package org.apache.tika.sax.xpath;

public class NamedElementMatcher extends ChildMatcher {
   private final String namespace;
   private final String name;

   protected NamedElementMatcher(String namespace, String name, Matcher then) {
      super(then);
      this.namespace = namespace;
      this.name = name;
   }

   public Matcher descend(String namespace, String name) {
      return equals(namespace, this.namespace) && name.equals(this.name) ? super.descend(namespace, name) : FAIL;
   }

   private static boolean equals(String a, String b) {
      return a == null ? b == null : a.equals(b);
   }
}
