package org.apache.openejb.cipher;

public interface SafePasswordCipher extends PasswordCipher {
   char[] decryptAsCharArray(char[] var1);
}
