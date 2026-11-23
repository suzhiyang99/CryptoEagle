package org.apache.openejb.config;

public interface ValidationResults {
   ValidationFailure[] getFailures();

   ValidationWarning[] getWarnings();

   ValidationError[] getErrors();

   boolean hasWarnings();

   boolean hasFailures();

   boolean hasErrors();

   String getName();

   String getModuleType();
}
