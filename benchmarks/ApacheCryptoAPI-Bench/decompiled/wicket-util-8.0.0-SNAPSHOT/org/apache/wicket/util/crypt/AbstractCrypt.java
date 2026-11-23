package org.apache.wicket.util.crypt;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCrypt implements ICrypt {
   private static final String CHARACTER_ENCODING = "UTF-8";
   private static final Logger log = LoggerFactory.getLogger(AbstractCrypt.class);
   private String encryptionKey = UUID.randomUUID().toString();

   public final String decryptUrlSafe(String text) {
      try {
         byte[] decoded = java.util.Base64.getUrlDecoder().decode(text);
         return new String(this.decryptByteArray(decoded), "UTF-8");
      } catch (Exception ex) {
         log.debug("Error decoding text: " + text, ex);
         return null;
      }
   }

   public final String encryptUrlSafe(String plainText) {
      try {
         byte[] encrypted = this.encryptStringToByteArray(plainText);
         java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();
         byte[] encoded = encoder.encode(encrypted);
         return new String(encoded, "UTF-8");
      } catch (GeneralSecurityException e) {
         log.error("Unable to encrypt text '" + plainText + "'", e);
         return null;
      } catch (UnsupportedEncodingException e) {
         log.error("Unable to encrypt text '" + plainText + "'", e);
         return null;
      }
   }

   public String getKey() {
      return this.encryptionKey;
   }

   public void setKey(String key) {
      this.encryptionKey = key;
   }

   protected abstract byte[] crypt(byte[] var1, int var2) throws GeneralSecurityException;

   private byte[] decryptByteArray(byte[] encrypted) {
      try {
         return this.crypt(encrypted, 2);
      } catch (GeneralSecurityException e) {
         throw new RuntimeException("Unable to decrypt the text '" + new String(encrypted) + "'", e);
      }
   }

   private byte[] encryptStringToByteArray(String plainText) throws GeneralSecurityException {
      try {
         return this.crypt(plainText.getBytes("UTF-8"), 1);
      } catch (UnsupportedEncodingException ex) {
         throw new RuntimeException(ex.getMessage());
      }
   }
}
