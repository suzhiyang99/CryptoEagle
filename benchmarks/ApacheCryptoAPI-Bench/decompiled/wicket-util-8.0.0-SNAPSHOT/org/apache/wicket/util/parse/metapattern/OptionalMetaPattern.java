package org.apache.wicket.util.parse.metapattern;

import java.util.List;

public final class OptionalMetaPattern extends MetaPattern {
   private static final long serialVersionUID = 1L;

   public OptionalMetaPattern(String pattern) {
      super(pattern);
   }

   public OptionalMetaPattern(MetaPattern pattern) {
      super(pattern);
   }

   public OptionalMetaPattern(List<MetaPattern> patterns) {
      super(patterns);
   }

   public OptionalMetaPattern(MetaPattern[] patterns) {
      super(patterns);
   }

   public String toString() {
      return "(?:" + super.toString() + ")?";
   }
}
