package org.apache.wicket.util.crypt;

public class NoCrypt implements ICrypt {
   public final String decryptUrlSafe(String text) {
      return text;
   }

   public final String encryptUrlSafe(String plainText) {
      return plainText;
   }

   public void setKey(String key) {
   }
}
