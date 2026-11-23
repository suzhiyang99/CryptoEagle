package org.apache.openejb;

public class ApplicationException extends OpenEJBException {
   public ApplicationException() {
   }

   public ApplicationException(String message) {
      super(message);
   }

   public ApplicationException(Exception e) {
      super((Throwable)e);
   }

   public ApplicationException(Throwable t) {
      super(t);
   }

   public ApplicationException(String message, Exception e) {
      super(message, e);
   }
}
