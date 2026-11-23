package org.apache.tika.sax.xpath;

public class SubtreeMatcher extends Matcher {
   private final Matcher then;

   public SubtreeMatcher(Matcher then) {
      this.then = then;
   }

   public Matcher descend(String namespace, String name) {
      Matcher next = this.then.descend(namespace, name);
      return (Matcher)(next != FAIL && next != this.then ? new CompositeMatcher(next, this) : this);
   }

   public boolean matchesElement() {
      return this.then.matchesElement();
   }

   public boolean matchesAttribute(String namespace, String name) {
      return this.then.matchesAttribute(namespace, name);
   }

   public boolean matchesText() {
      return this.then.matchesText();
   }
}
