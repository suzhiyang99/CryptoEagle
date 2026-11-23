package org.apache.openejb.cipher;

public class PasswordCipherException extends RuntimeException {
   public PasswordCipherException() {
   }

   public PasswordCipherException(String message) {
      super(message);
   }

   public PasswordCipherException(String message, Throwable cause) {
      super(message, cause);
   }

   public PasswordCipherException(Throwable cause) {
      super(cause);
   }
}
