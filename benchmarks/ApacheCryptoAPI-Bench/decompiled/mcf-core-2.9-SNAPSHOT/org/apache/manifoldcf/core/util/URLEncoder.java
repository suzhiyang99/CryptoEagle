package org.apache.manifoldcf.core.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class URLEncoder {
   public static String encode(String s) {
      String str = null;

      try {
         str = java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.name());
         return str;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("UTF-8 not supported " + e.getMessage(), e);
      }
   }
}
