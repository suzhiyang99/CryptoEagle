package org.apache.openejb.cipher;

public class PlainTextPasswordCipher implements PasswordCipher {
   public String decrypt(char[] encryptedPassword) {
      if (null == encryptedPassword) {
         throw new IllegalArgumentException("encodedPassword cannot be null.");
      } else {
         return new String(encryptedPassword);
      }
   }

   public char[] encrypt(String plainPassword) {
      if (null == plainPassword) {
         throw new IllegalArgumentException("plainPassword cannot be null.");
      } else {
         return plainPassword.toCharArray();
      }
   }
}
