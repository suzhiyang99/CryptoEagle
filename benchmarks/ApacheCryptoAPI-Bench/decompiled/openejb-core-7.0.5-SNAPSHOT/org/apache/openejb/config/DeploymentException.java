package org.apache.openejb.config;

public class DeploymentException extends Exception {
   public DeploymentException() {
   }

   public DeploymentException(String message) {
      super(message);
   }

   public DeploymentException(String message, Throwable cause) {
      super(message, cause);
   }

   public DeploymentException(Throwable cause) {
      super(cause);
   }
}
