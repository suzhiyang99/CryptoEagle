package org.apache.manifoldcf.core.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class URLDecoder {
   public static String decode(String s) {
      String str = null;

      try {
         str = java.net.URLDecoder.decode(s, StandardCharsets.UTF_8.name());
         return str;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("UTF-8 not supported " + e.getMessage(), e);
      }
   }
}
