package org.apache.openejb.config;

import org.apache.openejb.OpenEJBException;

public class UnsupportedModuleTypeException extends OpenEJBException {
   public UnsupportedModuleTypeException() {
   }

   public UnsupportedModuleTypeException(String message) {
      super(message);
   }

   public UnsupportedModuleTypeException(String message, Throwable rootCause) {
      super(message, rootCause);
   }

   public UnsupportedModuleTypeException(Throwable rootCause) {
      super(rootCause);
   }
}
