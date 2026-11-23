package org.apache.openejb.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTemplate {
   public static final Pattern PATTERN = Pattern.compile("(\\{)((\\.|\\w)+)(})");
   private final String template;

   public StringTemplate(String template) {
      this.template = template;
   }

   public String apply(Map<String, String> map) {
      Matcher matcher = PATTERN.matcher(this.template);
      StringBuffer buf = new StringBuffer();

      while(matcher.find()) {
         String key = matcher.group(2);
         if (key == null) {
            throw new IllegalStateException("Key is null. Template '" + this.template + "'");
         }

         String value = (String)map.get(key);
         if (key.toLowerCase().endsWith(".lc")) {
            value = ((String)map.get(key.substring(0, key.length() - 3))).toLowerCase();
         } else if (key.toLowerCase().endsWith(".uc")) {
            value = ((String)map.get(key.substring(0, key.length() - 3))).toUpperCase();
         } else if (key.toLowerCase().endsWith(".cc")) {
            value = Strings.camelCase((String)map.get(key.substring(0, key.length() - 3)));
         }

         if (value == null) {
            throw new IllegalStateException("Value is null for key '" + key + "'. Template '" + this.template + "'. Keys: " + Join.join(", ", (Collection)map.keySet()));
         }

         matcher.appendReplacement(buf, value.replace("$", "\\$"));
      }

      matcher.appendTail(buf);
      return buf.toString();
   }
}
