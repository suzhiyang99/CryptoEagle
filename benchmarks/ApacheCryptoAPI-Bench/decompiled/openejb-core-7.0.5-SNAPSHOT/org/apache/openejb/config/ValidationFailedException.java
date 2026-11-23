package org.apache.openejb.config;

import org.apache.openejb.OpenEJBException;

public class ValidationFailedException extends OpenEJBException implements ValidationResults {
   private final ValidationError[] errors;
   private final ValidationFailure[] failures;
   private final ValidationWarning[] warnings;
   private final String name;
   private final String moduleType;

   public ValidationFailedException(String message, ValidationResults set) {
      this(message, set, (ValidationFailedException)null);
   }

   public ValidationFailedException(String message, ValidationResults set, ValidationFailedException e) {
      super(message, e);
      this.name = set.getName();
      this.errors = set.getErrors();
      this.failures = set.getFailures();
      this.warnings = set.getWarnings();
      this.moduleType = set.getModuleType();
   }

   public String getName() {
      return this.name;
   }

   public ValidationError[] getErrors() {
      return this.errors;
   }

   public ValidationFailure[] getFailures() {
      return this.failures;
   }

   public ValidationWarning[] getWarnings() {
      return this.warnings;
   }

   public boolean hasWarnings() {
      return this.warnings.length > 0;
   }

   public boolean hasFailures() {
      return this.failures.length > 0;
   }

   public boolean hasErrors() {
      return this.errors.length > 0;
   }

   public String getModuleType() {
      return this.moduleType;
   }
}
