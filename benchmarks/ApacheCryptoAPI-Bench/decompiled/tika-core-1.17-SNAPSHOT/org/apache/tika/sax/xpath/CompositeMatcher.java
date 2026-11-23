package org.apache.tika.sax.xpath;

public class CompositeMatcher extends Matcher {
   private final Matcher a;
   private final Matcher b;

   public CompositeMatcher(Matcher a, Matcher b) {
      this.a = a;
      this.b = b;
   }

   public Matcher descend(String namespace, String name) {
      Matcher a = this.a.descend(namespace, name);
      Matcher b = this.b.descend(namespace, name);
      if (a == FAIL) {
         return b;
      } else if (b == FAIL) {
         return a;
      } else {
         return this.a == a && this.b == b ? this : new CompositeMatcher(a, b);
      }
   }

   public boolean matchesElement() {
      return this.a.matchesElement() || this.b.matchesElement();
   }

   public boolean matchesAttribute(String namespace, String name) {
      return this.a.matchesAttribute(namespace, name) || this.b.matchesAttribute(namespace, name);
   }

   public boolean matchesText() {
      return this.a.matchesText() || this.b.matchesText();
   }
}
