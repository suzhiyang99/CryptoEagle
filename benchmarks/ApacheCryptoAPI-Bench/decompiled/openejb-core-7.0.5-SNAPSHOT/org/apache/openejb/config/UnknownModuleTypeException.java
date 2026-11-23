package org.apache.openejb.config;

public class UnknownModuleTypeException extends UnsupportedModuleTypeException {
   public UnknownModuleTypeException(String message) {
      super(message);
   }

   public UnknownModuleTypeException(String message, Throwable rootCause) {
      super(message, rootCause);
   }
}
