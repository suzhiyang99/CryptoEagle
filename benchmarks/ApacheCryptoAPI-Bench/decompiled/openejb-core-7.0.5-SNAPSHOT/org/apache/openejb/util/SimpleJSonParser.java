package org.apache.openejb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class SimpleJSonParser {
   public static Object read(InputStream is) throws IOException {
      Map<String, Object> json = null;
      Collection<Object> array = null;

      int read;
      while((read = is.read()) != -1) {
         char current = (char)read;
         if (current == '{') {
            json = new HashMap();
         } else {
            if (current == '}') {
               return json;
            }

            if (current == '[') {
               array = new ArrayList();
            } else {
               if (current == ']') {
                  return array;
               }

               if (current == '"') {
                  StringBuilder b = new StringBuilder();

                  do {
                     read = is.read();
                     current = (char)read;
                     b.append(current);
                  } while(read != -1 && current != '"');

                  if (read == -1) {
                     throw new IllegalArgumentException("String should be between \"");
                  }

                  String value = PropertyPlaceHolderHelper.simpleValue(b.substring(0, b.length() - 1));
                  if (valueRead(is, json, array, value)) {
                     return value;
                  }
               } else if (current != ':' && current != '=' && current != ',' && !isWhiteSpace(current)) {
                  StringBuilder b = (new StringBuilder()).append(current);

                  do {
                     read = is.read();
                     current = (char)read;
                     b.append(current);
                  } while(read != -1 && !isWhiteSpace(current) && current != ',');

                  String value = PropertyPlaceHolderHelper.simpleValue(b.substring(0, b.length() - 1));
                  if (valueRead(is, json, array, value)) {
                     return value;
                  }
               }
            }
         }
      }

      throw new IllegalArgumentException("Please check input, a } is probably missing");
   }

   private static boolean valueRead(InputStream is, Map<String, Object> json, Collection<Object> array, String value) throws IOException {
      int read;
      char c;
      do {
         read = is.read();
         c = (char)read;
      } while(read != -1 && c != ':' && c != '=' && (c == ',' || c == '\n' || c == '\r'));

      if (json != null) {
         json.put(value, read(is));
      } else {
         if (array == null) {
            return true;
         }

         array.add(value);
      }

      return false;
   }

   private static boolean isWhiteSpace(char current) {
      return current == ' ' || current == '\n' || current == '\r' || current == '\t';
   }

   private SimpleJSonParser() {
   }
}
