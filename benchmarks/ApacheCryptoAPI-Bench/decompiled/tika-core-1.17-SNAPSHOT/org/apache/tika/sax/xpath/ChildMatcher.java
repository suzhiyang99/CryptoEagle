package org.apache.tika.sax.xpath;

public class ChildMatcher extends Matcher {
   private final Matcher then;

   public ChildMatcher(Matcher then) {
      this.then = then;
   }

   public Matcher descend(String namespace, String name) {
      return this.then;
   }
}
