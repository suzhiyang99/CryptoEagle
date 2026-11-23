package org.apache.openejb;

public class OpenEJBException extends Exception {
   public OpenEJBException() {
   }

   public OpenEJBException(String message) {
      super(message);
   }

   public OpenEJBException(Throwable rootCause) {
      super(rootCause);
   }

   public OpenEJBException(String message, Throwable rootCause) {
      super(message, rootCause);
   }

   public String getMessage() {
      Throwable rootCause = this.getCause();
      return rootCause != null ? super.getMessage() + ": " + rootCause.getMessage() : super.getMessage();
   }

   public Throwable getRootCause() {
      return super.getCause();
   }
}
