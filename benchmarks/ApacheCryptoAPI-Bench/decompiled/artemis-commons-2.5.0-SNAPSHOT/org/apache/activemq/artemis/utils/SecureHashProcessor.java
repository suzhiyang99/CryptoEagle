package org.apache.activemq.artemis.utils;

public class SecureHashProcessor implements HashProcessor {
   public static final String BEGIN_HASH = "ENC(";
   public static final String END_HASH = ")";
   private DefaultSensitiveStringCodec codec;

   public SecureHashProcessor(DefaultSensitiveStringCodec codec) {
      this.codec = codec;
   }

   public String hash(String plainText) throws Exception {
      return "ENC(" + this.codec.encode(plainText) + ")";
   }

   public boolean compare(char[] inputValue, String storedValue) {
      String storedHash = storedValue.substring(4, storedValue.length() - 2);
      return this.codec.verify(inputValue, storedHash);
   }
}
