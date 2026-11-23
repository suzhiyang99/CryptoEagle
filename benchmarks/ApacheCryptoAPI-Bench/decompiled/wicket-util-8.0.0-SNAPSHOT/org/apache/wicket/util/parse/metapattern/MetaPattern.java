package org.apache.wicket.util.parse.metapattern;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.io.IClusterable;

public class MetaPattern implements IClusterable {
   private static final long serialVersionUID = 1L;
   private Pattern pattern;
   private List<MetaPattern> patterns;
   private Pattern compiledPattern;
   private static final String _DOUBLE_QUOTED_STRING = "\"[^\"]*?\"";
   private static final String _SINGLE_QUOTED_STRING = "'[^']*?'";
   private static final String _STRING = "(?:[\\w\\-\\.]+|\"[^\"]*?\"|'[^']*?')";
   private static final String _OPTIONAL_STRING = "(?:[\\w\\-\\.]+|\"[^\"]*?\"|'[^']*?')?";
   private static final String _VARIABLE_NAME = "[A-Za-z_][A-Za-z0-9_]*";
   private static final String _XML_NAME = "[A-Za-z_][A-Za-z0-9_.-]*";
   public static final MetaPattern WHITESPACE = new MetaPattern("\\s+");
   public static final MetaPattern OPTIONAL_WHITESPACE = new MetaPattern("\\s*");
   public static final MetaPattern NON_WORD = new MetaPattern("\\W+");
   public static final MetaPattern COMMA = new MetaPattern(",");
   public static final MetaPattern COLON = new MetaPattern(":");
   public static final MetaPattern SEMICOLON = new MetaPattern(";");
   public static final MetaPattern SLASH = new MetaPattern("/");
   public static final MetaPattern BACKSLASH = new MetaPattern("\\\\");
   public static final MetaPattern DOT = new MetaPattern("\\.");
   public static final MetaPattern PLUS = new MetaPattern("\\+");
   public static final MetaPattern MINUS = new MetaPattern("-");
   public static final MetaPattern DASH = new MetaPattern("-");
   public static final MetaPattern UNDERSCORE = new MetaPattern("_");
   public static final MetaPattern AMPERSAND = new MetaPattern("&");
   public static final MetaPattern PERCENT = new MetaPattern("%");
   public static final MetaPattern DOLLAR_SIGN = new MetaPattern("$");
   public static final MetaPattern POUND_SIGN = new MetaPattern("#");
   public static final MetaPattern AT_SIGN = new MetaPattern("@");
   public static final MetaPattern EXCLAMATION_POINT = new MetaPattern("!");
   public static final MetaPattern TILDE = new MetaPattern("~");
   public static final MetaPattern EQUALS = new MetaPattern("=");
   public static final MetaPattern STAR = new MetaPattern("\\*");
   public static final MetaPattern PIPE = new MetaPattern("\\|");
   public static final MetaPattern LEFT_PAREN = new MetaPattern("\\(");
   public static final MetaPattern RIGHT_PAREN = new MetaPattern("\\)");
   public static final MetaPattern LEFT_CURLY = new MetaPattern("\\{");
   public static final MetaPattern RIGHT_CURLY = new MetaPattern("\\}");
   public static final MetaPattern LEFT_SQUARE = new MetaPattern("\\[");
   public static final MetaPattern RIGHT_SQUARE = new MetaPattern("\\]");
   public static final MetaPattern DIGIT = new MetaPattern("\\d");
   public static final MetaPattern DIGITS = new MetaPattern("\\d+");
   public static final MetaPattern INTEGER = new MetaPattern("-?\\d+");
   public static final MetaPattern FLOATING_POINT_NUMBER = new MetaPattern("-?\\d+\\.?\\d*|-?\\.\\d+");
   public static final MetaPattern POSITIVE_INTEGER = new MetaPattern("\\d+");
   public static final MetaPattern HEXADECIMAL_DIGIT = new MetaPattern("[0-9a-fA-F]");
   public static final MetaPattern HEXADECIMAL_DIGITS = new MetaPattern("[0-9a-fA-F]+");
   public static final MetaPattern ANYTHING = new MetaPattern(".*");
   public static final MetaPattern ANYTHING_NON_EMPTY = new MetaPattern(".+");
   public static final MetaPattern WORD = new MetaPattern("\\w+");
   public static final MetaPattern OPTIONAL_WORD = new MetaPattern("\\w*");
   public static final MetaPattern VARIABLE_NAME = new MetaPattern("[A-Za-z_][A-Za-z0-9_]*");
   public static final MetaPattern XML_ELEMENT_NAME = new MetaPattern("[A-Za-z_][A-Za-z0-9_.-]*");
   public static final MetaPattern XML_ATTRIBUTE_NAME = new MetaPattern("[A-Za-z_][A-Za-z0-9_.-]*");
   public static final MetaPattern PERL_INTERPOLATION = new MetaPattern("$\\{[A-Za-z_][A-Za-z0-9_]*\\}");
   public static final MetaPattern DOUBLE_QUOTED_STRING = new MetaPattern("\"[^\"]*?\"");
   public static final MetaPattern STRING = new MetaPattern("(?:[\\w\\-\\.]+|\"[^\"]*?\"|'[^']*?')");
   public static final MetaPattern OPTIONAL_STRING = new MetaPattern("(?:[\\w\\-\\.]+|\"[^\"]*?\"|'[^']*?')?");

   public MetaPattern(String pattern) {
      this.pattern = Pattern.compile(pattern);
   }

   public MetaPattern(MetaPattern pattern) {
      this.pattern = pattern.pattern;
      this.patterns = pattern.patterns;
      this.compiledPattern = pattern.compiledPattern;
   }

   public MetaPattern(MetaPattern... patterns) {
      this(Arrays.asList(patterns));
   }

   public MetaPattern(List<MetaPattern> patterns) {
      this.patterns = patterns;
   }

   public final Matcher matcher(CharSequence input) {
      return this.matcher(input, 0);
   }

   public final Matcher matcher(CharSequence input, int flags) {
      this.compile(flags);
      return this.compiledPattern.matcher(input);
   }

   public final Pattern pattern() {
      return this.pattern(0);
   }

   public final Pattern pattern(int flags) {
      this.compile(flags);
      return this.compiledPattern;
   }

   public String toString() {
      if (this.pattern != null) {
         return this.pattern.pattern();
      } else {
         StringBuilder buffer = new StringBuilder();

         for(MetaPattern metaPattern : this.patterns) {
            buffer.append(metaPattern);
         }

         return buffer.toString();
      }
   }

   private synchronized void compile(int flags) {
      if (this.compiledPattern == null) {
         this.bind(1);
         this.compiledPattern = Pattern.compile(this.toString(), flags);
      }

   }

   private int bind(int group) {
      if (this instanceof Group) {
         ((Group)this).bind(group++);
      }

      if (this.patterns != null) {
         for(MetaPattern metaPattern : this.patterns) {
            group = metaPattern.bind(group);
         }
      }

      return group;
   }
}
