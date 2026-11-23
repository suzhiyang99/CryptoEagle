package org.apache.openejb;

public class DeploymentNotFoundException extends Exception {
   public DeploymentNotFoundException() {
   }

   public DeploymentNotFoundException(String message) {
      super(message);
   }

   public DeploymentNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }

   public DeploymentNotFoundException(Throwable cause) {
      super(cause);
   }
}
