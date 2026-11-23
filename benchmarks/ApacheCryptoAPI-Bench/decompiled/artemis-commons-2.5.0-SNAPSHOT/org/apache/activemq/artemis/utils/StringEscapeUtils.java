package org.apache.activemq.artemis.utils;

public abstract class StringEscapeUtils {
   public static String escapeString(String str) {
      if (str == null) {
         return str;
      } else {
         int sz = str.length();
         StringBuilder stringBuilder = new StringBuilder(str.length());

         for(int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (ch > 4095) {
               stringBuilder.append("\\u").append(hex(ch));
            } else if (ch > 255) {
               stringBuilder.append("\\u0").append(hex(ch));
            } else if (ch > 127) {
               stringBuilder.append("\\u00").append(hex(ch));
            } else if (ch < ' ') {
               switch (ch) {
                  case '\b':
                     stringBuilder.append('\\').append('b');
                     break;
                  case '\t':
                     stringBuilder.append('\\').append('t');
                     break;
                  case '\n':
                     stringBuilder.append('\\').append('n');
                     break;
                  case '\u000b':
                  default:
                     if (ch > 15) {
                        stringBuilder.append("\\u00").append(hex(ch));
                     } else {
                        stringBuilder.append("\\u000").append(hex(ch));
                     }
                     break;
                  case '\f':
                     stringBuilder.append('\\').append('f');
                     break;
                  case '\r':
                     stringBuilder.append('\\').append('r');
               }
            } else {
               switch (ch) {
                  case '"':
                     stringBuilder.append('\\').append('"');
                     break;
                  case '\'':
                     stringBuilder.append('\\').append('\'');
                     break;
                  case '/':
                     stringBuilder.append('\\').append('/');
                     break;
                  case '\\':
                     stringBuilder.append('\\').append('\\');
                     break;
                  default:
                     stringBuilder.append(ch);
               }
            }
         }

         return stringBuilder.toString();
      }
   }

   private static String hex(char ch) {
      return Integer.toHexString(ch).toUpperCase();
   }
}
