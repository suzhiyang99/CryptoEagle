package org.apache.tika.mime;

public class HexCoDec {
   private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static byte[] decode(String hexValue) {
      return decode(hexValue.toCharArray());
   }

   public static byte[] decode(char[] hexChars) {
      return decode(hexChars, 0, hexChars.length);
   }

   public static byte[] decode(char[] hexChars, int startIndex, int length) {
      if ((length & 1) != 0) {
         throw new IllegalArgumentException("Length must be even");
      } else {
         byte[] result = new byte[length / 2];

         for(int j = 0; j < result.length; ++j) {
            result[j] = (byte)(hexCharToNibble(hexChars[startIndex++]) * 16 + hexCharToNibble(hexChars[startIndex++]));
         }

         return result;
      }
   }

   public static char[] encode(byte[] bites) {
      return encode(bites, 0, bites.length);
   }

   public static char[] encode(byte[] bites, int startIndex, int length) {
      char[] result = new char[length * 2];
      int i = 0;

      for(int j = 0; i < length; ++i) {
         int bite = bites[startIndex++] & 255;
         result[j++] = HEX_CHARS[bite >> 4];
         result[j++] = HEX_CHARS[bite & 15];
      }

      return result;
   }

   private static int hexCharToNibble(char ch) {
      if (ch >= '0' && ch <= '9') {
         return ch - 48;
      } else if (ch >= 'a' && ch <= 'f') {
         return ch - 97 + 10;
      } else if (ch >= 'A' && ch <= 'F') {
         return ch - 65 + 10;
      } else {
         throw new IllegalArgumentException("Not a hex char - '" + ch + "'");
      }
   }
}
