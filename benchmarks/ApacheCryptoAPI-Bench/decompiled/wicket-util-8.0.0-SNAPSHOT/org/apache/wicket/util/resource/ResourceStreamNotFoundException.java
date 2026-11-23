package org.apache.wicket.util.resource;

public final class ResourceStreamNotFoundException extends Exception {
   private static final long serialVersionUID = 1L;

   public ResourceStreamNotFoundException() {
   }

   public ResourceStreamNotFoundException(String message) {
      super(message);
   }

   public ResourceStreamNotFoundException(Throwable cause) {
      super(cause);
   }

   public ResourceStreamNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }
}
