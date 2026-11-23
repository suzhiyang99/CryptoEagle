package org.apache.openejb.config;

public class ValidationFailure extends ValidationException {
   public ValidationFailure(String message) {
      super(message);
   }

   public String getPrefix() {
      return "FAIL";
   }

   public String getCategory() {
      return "failures";
   }
}
