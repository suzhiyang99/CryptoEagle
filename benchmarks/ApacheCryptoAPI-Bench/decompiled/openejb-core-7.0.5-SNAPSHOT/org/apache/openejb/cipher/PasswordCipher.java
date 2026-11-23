package org.apache.openejb.cipher;

public interface PasswordCipher {
   char[] encrypt(String var1);

   String decrypt(char[] var1);
}
