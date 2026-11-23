package org.apache.openejb.cipher;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.util.Base64;

public class StaticDESPasswordCipher implements PasswordCipher {
   private static final byte[] _3desData = new byte[]{118, 111, -70, 57, 49, 47, 13, 74, -93, -112, 85, -2, 85, 101, 97, 19, 52, -126, 18, 23, -84, 119, 57, 25};
   private static final SecretKeySpec KEY;
   private static final String TRANSFORMATION = "DESede";

   public char[] encrypt(String plainPassword) {
      if (null != plainPassword && plainPassword.length() != 0) {
         byte[] plaintext = plainPassword.getBytes();

         try {
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(1, KEY);
            byte[] cipherText = cipher.doFinal(plaintext);
            return (new String(Base64.encodeBase64(cipherText))).toCharArray();
         } catch (Exception e) {
            throw new OpenEJBRuntimeException(e);
         }
      } else {
         throw new IllegalArgumentException("plainPassword cannot be null nor empty.");
      }
   }

   public String decrypt(char[] encodedPassword) {
      if (null != encodedPassword && encodedPassword.length != 0) {
         try {
            byte[] cipherText = Base64.decodeBase64(String.valueOf(encodedPassword).getBytes());
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(2, KEY);
            return new String(cipher.doFinal(cipherText));
         } catch (Exception e) {
            throw new OpenEJBRuntimeException(e);
         }
      } else {
         throw new IllegalArgumentException("encodedPassword cannot be null nor empty.");
      }
   }

   static {
      KEY = new SecretKeySpec(_3desData, "DESede");
   }
}
