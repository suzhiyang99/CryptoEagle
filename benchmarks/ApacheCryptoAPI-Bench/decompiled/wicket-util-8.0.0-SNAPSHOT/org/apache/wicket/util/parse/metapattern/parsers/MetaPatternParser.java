package org.apache.wicket.util.parse.metapattern.parsers;

import java.util.regex.Matcher;
import org.apache.wicket.util.parse.metapattern.MetaPattern;

public abstract class MetaPatternParser {
   private final CharSequence input;
   private final int length;
   private int pos;
   private Matcher matcher;

   public MetaPatternParser(CharSequence input) {
      this.input = input;
      this.length = input.length();
   }

   public MetaPatternParser(MetaPattern pattern, CharSequence input) {
      this(input);
      this.setPattern(pattern);
   }

   public void setPattern(MetaPattern pattern) {
      this.matcher = pattern.matcher(this.input);
   }

   protected final boolean advance(MetaPattern pattern) {
      CharSequence s = this.input.subSequence(this.pos, this.length);
      this.matcher = pattern.matcher(s);
      if (this.matcher.lookingAt()) {
         this.pos += this.matcher.end();
         return true;
      } else {
         return false;
      }
   }

   public boolean matches() {
      return this.matcher.matches();
   }

   public final Matcher matcher() {
      return this.matcher;
   }

   public final boolean atEnd() {
      return this.pos == this.length;
   }
}
