package org.apache.openejb.util.proxy;

public class ProxyGenerationException extends Exception {
   public ProxyGenerationException() {
   }

   public ProxyGenerationException(String message) {
      super(message);
   }

   public ProxyGenerationException(Throwable cause) {
      super(cause);
   }

   public ProxyGenerationException(String message, Throwable cause) {
      super(message, cause);
   }
}
