package org.apache.wicket.util.parse.metapattern.parsers;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.MetaPattern;

public class ListParser extends MetaPatternParser {
   private final Group entryGroup;
   private final MetaPattern separatorPattern;
   private final List<String> values = new ArrayList();

   public ListParser(MetaPattern entryPattern, MetaPattern separatorPattern, CharSequence input) {
      super(input);
      this.entryGroup = new Group(entryPattern);
      this.separatorPattern = separatorPattern;
   }

   public final boolean matches() {
      if (!this.advance(this.entryGroup)) {
         return false;
      } else {
         String value = this.entryGroup.get(this.matcher());
         this.values.add(value);

         while(this.advance(this.separatorPattern) && this.advance(this.entryGroup)) {
            this.values.add(this.entryGroup.get(this.matcher()));
         }

         return true;
      }
   }

   public final List<String> getValues() {
      return this.values;
   }
}
