package org.apache.openejb;

public class NoSuchApplicationException extends OpenEJBException {
   public NoSuchApplicationException() {
   }

   public NoSuchApplicationException(String message) {
      super(message);
   }

   public NoSuchApplicationException(String message, Throwable rootCause) {
      super(message, rootCause);
   }

   public NoSuchApplicationException(Throwable rootCause) {
      super(rootCause);
   }
}
