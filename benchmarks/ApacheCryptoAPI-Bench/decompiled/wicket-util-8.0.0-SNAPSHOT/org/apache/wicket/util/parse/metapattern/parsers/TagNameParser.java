package org.apache.wicket.util.parse.metapattern.parsers;

import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.parse.metapattern.OptionalMetaPattern;

public final class TagNameParser extends MetaPatternParser {
   private static final Group namespaceGroup;
   private static final Group nameGroup;
   private static final MetaPattern pattern;

   public TagNameParser(CharSequence input) {
      super(pattern, input);
   }

   public String getNamespace() {
      String namespace = namespaceGroup.get(this.matcher());
      return namespace != null ? namespace.toLowerCase() : namespace;
   }

   public String getName() {
      return nameGroup.get(this.matcher());
   }

   static {
      namespaceGroup = new Group(MetaPattern.VARIABLE_NAME);
      nameGroup = new Group(MetaPattern.XML_ELEMENT_NAME);
      pattern = new MetaPattern(new MetaPattern[]{new OptionalMetaPattern(new MetaPattern[]{namespaceGroup, MetaPattern.COLON}), nameGroup});
   }
}
