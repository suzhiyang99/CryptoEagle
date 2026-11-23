package org.apache.openejb.cipher;

public abstract class SafePasswordCipherBase implements SafePasswordCipher {
   public String decrypt(char[] encryptedPassword) {
      throw new UnsupportedOperationException(this.getClass().getName() + " doesn't support String decryption");
   }
}
