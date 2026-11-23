package org.apache.tika.io;

import java.util.HashSet;
import java.util.Locale;

public class FilenameUtils {
   public static final char[] RESERVED_FILENAME_CHARACTERS = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '?', ':', '*', '<', '>', '|'};
   private static final HashSet<Character> RESERVED = new HashSet(38);

   public static String normalize(String name) {
      if (name == null) {
         throw new IllegalArgumentException("name cannot be null");
      } else {
         StringBuilder sb = new StringBuilder();

         for(char c : name.toCharArray()) {
            if (RESERVED.contains(c)) {
               sb.append('%').append(c < 16 ? "0" : "").append(Integer.toHexString(c).toUpperCase(Locale.ROOT));
            } else {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   public static String getName(String path) {
      if (path != null && path.length() != 0) {
         int unix = path.lastIndexOf("/");
         int windows = path.lastIndexOf("\\");
         int colon = path.lastIndexOf(":");
         String cand = path.substring(Math.max(colon, Math.max(unix, windows)) + 1);
         return !cand.equals("..") && !cand.equals(".") ? cand : "";
      } else {
         return "";
      }
   }

   static {
      for(int i = 0; i < RESERVED_FILENAME_CHARACTERS.length; ++i) {
         RESERVED.add(RESERVED_FILENAME_CHARACTERS[i]);
      }

   }
}
