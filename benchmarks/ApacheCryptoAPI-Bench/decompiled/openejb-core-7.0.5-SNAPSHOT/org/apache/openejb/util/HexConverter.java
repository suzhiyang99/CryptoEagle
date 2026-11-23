package org.apache.openejb.util;

public final class HexConverter {
   private HexConverter() {
   }

   public static String bytesToHex(byte[] bytes) {
      StringBuilder buf = new StringBuilder(bytes.length * 2);

      for(byte b : bytes) {
         buf.append(String.format("%02X", b));
      }

      return buf.toString();
   }

   public static byte[] hexToBytes(String hexString) {
      if (hexString.length() % 2 != 0) {
         throw new IllegalArgumentException("Invalid number of digits: input must be a string of hexadecimal digit pairs");
      } else {
         byte[] bytes = new byte[hexString.length() / 2];

         for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
         }

         return bytes;
      }
   }
}
