package org.apache.openejb;

public class DuplicateDeploymentIdException extends OpenEJBException {
   public DuplicateDeploymentIdException() {
   }

   public DuplicateDeploymentIdException(String message) {
      super(message);
   }

   public DuplicateDeploymentIdException(String message, Throwable rootCause) {
      super(message, rootCause);
   }

   public DuplicateDeploymentIdException(Throwable rootCause) {
      super(rootCause);
   }
}
