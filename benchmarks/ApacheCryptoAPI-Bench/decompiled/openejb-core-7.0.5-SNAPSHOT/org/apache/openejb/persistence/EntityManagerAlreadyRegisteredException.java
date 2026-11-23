package org.apache.openejb.persistence;

public class EntityManagerAlreadyRegisteredException extends Exception {
   public EntityManagerAlreadyRegisteredException() {
   }

   public EntityManagerAlreadyRegisteredException(String message) {
      super(message);
   }

   public EntityManagerAlreadyRegisteredException(String message, Throwable cause) {
      super(message, cause);
   }

   public EntityManagerAlreadyRegisteredException(Throwable cause) {
      super(cause);
   }
}
