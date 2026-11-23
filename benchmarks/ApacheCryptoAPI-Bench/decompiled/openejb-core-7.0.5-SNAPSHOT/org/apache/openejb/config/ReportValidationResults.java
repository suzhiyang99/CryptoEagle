package org.apache.openejb.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ReportValidationResults implements DynamicDeployer {
   private static final Logger logger;
   public static final String VALIDATION_LEVEL = "openejb.validation.output.level";

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      Level level = (Level)SystemInstance.get().getOptions().get("openejb.validation.output.level", ReportValidationResults.Level.MEDIUM);
      boolean hasErrors = appModule.hasErrors();
      boolean hasFailures = appModule.hasFailures();
      boolean hasWarnings = appModule.hasWarnings();
      if (!hasErrors && !hasFailures && !hasWarnings) {
         return appModule;
      } else {
         ValidationFailedException validationFailedException = null;
         List<ValidationContext> contexts = appModule.getValidationContexts();

         for(ValidationContext context : contexts) {
            this.logResults(context, level);
         }

         ValidationContext uberContext = new ValidationContext(appModule);

         for(ValidationContext context : contexts) {
            for(ValidationError error : context.getErrors()) {
               uberContext.addError(error);
            }

            for(ValidationFailure failure : context.getFailures()) {
               uberContext.addFailure(failure);
            }

            for(ValidationWarning warning : context.getWarnings()) {
               uberContext.addWarning(warning);
            }
         }

         if (!hasErrors && !hasFailures) {
            return appModule;
         } else {
            if (level != ReportValidationResults.Level.VERBOSE) {
               List<Level> levels = Arrays.asList(ReportValidationResults.Level.values());
               levels = levels.subList(level.ordinal() + 1, levels.size());
               logger.info("Set the 'openejb.validation.output.level' system property to " + Join.join(" or ", (Collection)levels) + " for increased validation details.");
            }

            throw new ValidationFailedException("Module failed validation. " + uberContext.getModuleType() + "(name=" + uberContext.getName() + ")", uberContext, validationFailedException);
         }
      }
   }

   private void logResults(ValidationContext context, Level level) {
      for(ValidationError e : context.getErrors()) {
         logger.error(e.getPrefix() + " ... " + e.getComponentName() + ":\t" + e.getMessage(level.ordinal() + 1));
      }

      for(ValidationFailure e : context.getFailures()) {
         logger.error(e.getPrefix() + " ... " + e.getComponentName() + ":\t" + e.getMessage(level.ordinal() + 1));
      }

      for(ValidationWarning e : context.getWarnings()) {
         logger.warning(e.getPrefix() + " ... " + e.getComponentName() + ":\t" + e.getMessage(level.ordinal() + 1));
      }

      if (!context.hasErrors() && !context.hasFailures()) {
         if (context.hasWarnings()) {
            if (context.getWarnings().length == 1) {
               logger.warning(context.getWarnings().length + " warning for " + context.getModuleType() + "(path=" + context.getName() + ")");
            } else {
               logger.warning(context.getWarnings().length + " warnings for " + context.getModuleType() + "(path=" + context.getName() + ")");
            }
         }
      } else {
         DeploymentModule module = context.getModule();
         logger.error(String.format("Invalid %s(name=%s, path=%s)", context.getModuleType(), module.getModuleId(), module.getFile()));
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_VALIDATION, "org.apache.openejb.config.rules");
   }

   private static enum Level {
      TERSE,
      MEDIUM,
      VERBOSE;
   }
}
