package org.apache.wicket.util.parse.metapattern;

import java.util.regex.Matcher;

public class Group extends MetaPattern {
   private static final long serialVersionUID = 1L;
   private int group = -1;

   public Group(MetaPattern pattern) {
      super(pattern);
   }

   public final String get(Matcher matcher) {
      if (this.group == -1) {
         throw new GroupNotBoundException();
      } else {
         return matcher.group(this.group);
      }
   }

   public String toString() {
      return "(" + super.toString() + ")";
   }

   final void bind(int bindTo) {
      if (this.group == -1) {
         this.group = bindTo;
      } else {
         throw new GroupAlreadyBoundException();
      }
   }
}
