package org.apache.openejb.config;

import java.util.ArrayList;
import java.util.List;

public class ValidationContext implements ValidationResults {
   private final List<ValidationFailure> failures = new ArrayList();
   private final List<ValidationWarning> warnings = new ArrayList();
   private final List<ValidationError> errors = new ArrayList();
   private final String moduleType;
   private final String name;
   private final DeploymentModule module;

   public ValidationContext(Class<? extends DeploymentModule> moduleType, String name) {
      this.moduleType = moduleType.getSimpleName();
      this.name = name;
      this.module = null;
   }

   public ValidationContext(DeploymentModule module) {
      this.moduleType = module.getClass().getSimpleName();
      this.module = module;
      this.name = null;
   }

   public DeploymentModule getModule() {
      return this.module;
   }

   public void fail(String component, String key, Object... details) {
      ValidationFailure failure = new ValidationFailure(key);
      failure.setDetails(details);
      failure.setComponentName(component);
      this.addFailure(failure);
   }

   public void warn(String component, String key, Object... details) {
      ValidationWarning warning = new ValidationWarning(key);
      warning.setDetails(details);
      warning.setComponentName(component);
      this.addWarning(warning);
   }

   public void error(String component, String key, Object... details) {
      ValidationError error = new ValidationError(key);
      error.setDetails(details);
      error.setComponentName(component);
      this.addError(error);
   }

   public void addWarning(ValidationWarning warning) {
      this.warnings.add(warning);
   }

   public void addFailure(ValidationFailure failure) {
      this.failures.add(failure);
   }

   public void addError(ValidationError error) {
      this.errors.add(error);
   }

   public ValidationFailure[] getFailures() {
      return (ValidationFailure[])this.failures.toArray(new ValidationFailure[this.failures.size()]);
   }

   public ValidationWarning[] getWarnings() {
      return (ValidationWarning[])this.warnings.toArray(new ValidationWarning[this.warnings.size()]);
   }

   public ValidationError[] getErrors() {
      return (ValidationError[])this.errors.toArray(new ValidationError[this.errors.size()]);
   }

   public boolean hasWarnings() {
      return this.warnings.size() > 0;
   }

   public boolean hasFailures() {
      return this.failures.size() > 0;
   }

   public boolean hasErrors() {
      return this.errors.size() > 0;
   }

   public String getName() {
      return this.module == null ? this.name : this.module.getModuleId();
   }

   public String getModuleType() {
      return this.moduleType;
   }
}
