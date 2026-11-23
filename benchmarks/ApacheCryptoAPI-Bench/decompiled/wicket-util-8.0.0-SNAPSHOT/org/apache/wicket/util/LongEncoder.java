package org.apache.wicket.util;

import org.apache.wicket.util.string.PrependingStringBuffer;

public class LongEncoder {
   public static final String DEFAULT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

   private LongEncoder() {
   }

   public static String encode(long value) {
      return encode(value, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
   }

   public static long decode(String value) {
      return decode(value, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
   }

   public static String encode(long value, String alphabet) {
      int len = alphabet.length() - 1;
      PrependingStringBuffer buff = new PrependingStringBuffer();
      boolean negative = value < 0L;
      if (negative) {
         value = -value;
      }

      do {
         int mod = (int)(value % (long)len);
         buff.prepend(alphabet.charAt(mod));
         value /= (long)len;
      } while(value > 0L);

      if (negative) {
         buff.prepend(alphabet.charAt(len));
      }

      return buff.toString();
   }

   public static long decode(String value, String alphabet) {
      int factor = alphabet.length() - 1;
      boolean negative = alphabet.charAt(factor) == value.charAt(0);
      long num = 0L;
      int i = negative ? 1 : 0;

      for(int len = value.length(); i < len; ++i) {
         num = num * (long)factor + (long)alphabet.indexOf(value.charAt(i));
      }

      if (negative) {
         num = -num;
      }

      return num;
   }
}
