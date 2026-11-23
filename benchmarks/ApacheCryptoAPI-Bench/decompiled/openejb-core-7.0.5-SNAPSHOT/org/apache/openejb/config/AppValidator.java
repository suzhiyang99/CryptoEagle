package org.apache.openejb.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.spi.DefinitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.cli.SystemExitException;
import org.apache.openejb.config.rules.CheckAnnotations;
import org.apache.openejb.config.rules.CheckAssemblyBindings;
import org.apache.openejb.config.rules.CheckAsynchronous;
import org.apache.openejb.config.rules.CheckCallbacks;
import org.apache.openejb.config.rules.CheckCdiEnabled;
import org.apache.openejb.config.rules.CheckClasses;
import org.apache.openejb.config.rules.CheckDependsOn;
import org.apache.openejb.config.rules.CheckDescriptorLocation;
import org.apache.openejb.config.rules.CheckIncorrectPropertyNames;
import org.apache.openejb.config.rules.CheckInjectionPointUsage;
import org.apache.openejb.config.rules.CheckInjectionTargets;
import org.apache.openejb.config.rules.CheckMethods;
import org.apache.openejb.config.rules.CheckPersistenceRefs;
import org.apache.openejb.config.rules.CheckRestMethodArePublic;
import org.apache.openejb.config.rules.CheckUserTransactionRefs;
import org.apache.openejb.config.rules.ValidationBase;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.OpenEjbVersion;

public class AppValidator {
   protected static final Messages _messages = new Messages("org.apache.openejb.config.rules");
   private int level = 2;
   private boolean printXml;
   private boolean printWarnings = true;
   private boolean printCount;
   private final List<ValidationResults> sets = new ArrayList();
   private ValidationBase[] additionalValidators;

   public AppValidator() throws OpenEJBException {
   }

   public AppValidator(int level, boolean printXml, boolean printWarnings, boolean printCount) {
      this.level = level;
      this.printXml = printXml;
      this.printWarnings = printWarnings;
      this.printCount = printCount;
   }

   public AppValidator(ValidationBase... additionalValidator) {
      this.additionalValidators = additionalValidator;
   }

   public void addValidationResults(ValidationResults set) {
      this.sets.add(set);
   }

   public ValidationResults[] getValidationResultsSets() {
      ValidationResults[] ejbSets = new ValidationResults[this.sets.size()];
      return (ValidationResults[])this.sets.toArray(ejbSets);
   }

   public AppModule validate(AppModule appModule) {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(appModule.getClassLoader());

      try {
         ValidationRule[] rules = this.getValidationRules();

         for(int i = 0; i < rules.length; ++i) {
            rules[i].validate(appModule);
         }
      } catch (DefinitionException de) {
         throw de;
      } catch (Throwable e) {
         e.printStackTrace(System.out);
         ValidationError err = new ValidationError("cannot.validate");
         err.setCause(e);
         err.setDetails(new Object[]{e.getMessage()});
         appModule.getValidation().addError(err);
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }

      return appModule;
   }

   protected ValidationRule[] getValidationRules() {
      ValidationRule[] defaultRules = new ValidationRule[]{new CheckClasses(), new CheckMethods(), new CheckCallbacks(), new CheckAssemblyBindings(), new CheckInjectionTargets(), new CheckInjectionPointUsage(), new CheckPersistenceRefs(), new CheckDependsOn(), new CheckUserTransactionRefs(), new CheckAsynchronous(), new CheckDescriptorLocation(), new CheckAnnotations(), new CheckIncorrectPropertyNames(), new CheckRestMethodArePublic(), new CheckCdiEnabled()};
      if (this.additionalValidators != null && this.additionalValidators.length != 0) {
         ValidationRule[] rules = new ValidationRule[this.additionalValidators.length + defaultRules.length];
         System.arraycopy(this.additionalValidators, 0, rules, 0, this.additionalValidators.length);
         System.arraycopy(defaultRules, 0, rules, this.additionalValidators.length, defaultRules.length);
         return rules;
      } else {
         return defaultRules;
      }
   }

   public void printResults(ValidationResults set) {
      if (set.hasErrors() || set.hasFailures() || this.printWarnings && set.hasWarnings()) {
         System.out.println("------------------------------------------");
         System.out.println("JAR " + set.getName());
         System.out.println("                                          ");
         this.printValidationExceptions(set.getErrors());
         this.printValidationExceptions(set.getFailures());
         if (this.printWarnings) {
            this.printValidationExceptions(set.getWarnings());
         }

      }
   }

   protected void printValidationExceptions(ValidationException[] exceptions) {
      for(int i = 0; i < exceptions.length; ++i) {
         System.out.print(" ");
         System.out.print(exceptions[i].getPrefix());
         System.out.print(" ... ");
         if (!(exceptions[i] instanceof ValidationError)) {
            System.out.print(exceptions[i].getComponentName());
            System.out.print(": ");
         }

         if (this.level > 2) {
            System.out.println(exceptions[i].getMessage(1));
            System.out.println();
            System.out.print('\t');
            System.out.println(exceptions[i].getMessage(this.level));
            System.out.println();
         } else {
            System.out.println(exceptions[i].getMessage(this.level));
         }
      }

      if (this.printCount && exceptions.length > 0) {
         System.out.println();
         System.out.print(" " + exceptions.length + " ");
         System.out.println(exceptions[0].getCategory());
         System.out.println();
      }

   }

   public void printResultsXML(ValidationResults set) {
      if (set.hasErrors() || set.hasFailures() || this.printWarnings && set.hasWarnings()) {
         System.out.println("<jar>");
         System.out.print("  <path>");
         System.out.print(set.getName());
         System.out.println("</path>");
         this.printValidationExceptionsXML(set.getErrors());
         this.printValidationExceptionsXML(set.getFailures());
         if (this.printWarnings) {
            this.printValidationExceptionsXML(set.getWarnings());
         }

         System.out.println("</jar>");
      }
   }

   protected void printValidationExceptionsXML(ValidationException[] exceptions) {
      for(int i = 0; i < exceptions.length; ++i) {
         System.out.print("    <");
         System.out.print(exceptions[i].getPrefix());
         System.out.println(">");
         if (!(exceptions[i] instanceof ValidationError)) {
            System.out.print("      <ejb-name>");
            System.out.print(exceptions[i].getComponentName());
            System.out.println("</ejb-name>");
         }

         System.out.print("      <summary>");
         System.out.print(exceptions[i].getMessage(1));
         System.out.println("</summary>");
         System.out.println("      <description><![CDATA[");
         System.out.println(exceptions[i].getMessage(3));
         System.out.println("]]></description>");
         System.out.print("    </");
         System.out.print(exceptions[i].getPrefix());
         System.out.println(">");
      }

   }

   public void displayResults(ValidationResults[] sets) {
      if (this.printXml) {
         System.out.println("<results>");

         for(int i = 0; i < sets.length; ++i) {
            this.printResultsXML(sets[i]);
         }

         System.out.println("</results>");
      } else {
         for(int i = 0; i < sets.length; ++i) {
            this.printResults(sets[i]);
         }

         for(int i = 0; i < sets.length; ++i) {
            if (sets[i].hasErrors() || sets[i].hasFailures()) {
               if (this.level < 3) {
                  System.out.println();
                  System.out.println("For more details, use the -vvv option");
               }

               i = sets.length;
            }
         }
      }

   }

   public static void main(String[] args) throws SystemExitException {
      CommandLineParser parser = new PosixParser();
      Options options = new Options();
      options.addOption(option("v", "version", "cmd.validate.opt.version"));
      options.addOption(option("h", "help", "cmd.validate.opt.help"));
      CommandLine line = null;

      try {
         line = parser.parse(options, args);
      } catch (ParseException var11) {
         help(options);
         throw new SystemExitException(-1);
      }

      if (line.hasOption("help")) {
         help(options);
      } else if (line.hasOption("version")) {
         OpenEjbVersion.get().print(System.out);
      } else {
         if (line.getArgList().size() == 0) {
            System.out.println("Must specify an module id.");
            help(options);
         }

         DeploymentLoader deploymentLoader = new DeploymentLoader();

         try {
            AppValidator validator = new AppValidator();

            for(Object obj : line.getArgList()) {
               String module = (String)obj;
               File file = new File(module);
               AppModule appModule = deploymentLoader.load(file, (DeploymentLoader.ExternalConfiguration)null);
               validator.validate(appModule);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }

   private static void help(Options options) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("validate [options] <file> [<file>...]", "\n" + i18n("cmd.validate.description"), options, "\n");
   }

   private static Option option(String shortOpt, String longOpt, String description) {
      OptionBuilder.withLongOpt(longOpt);
      OptionBuilder.withDescription(i18n(description));
      return OptionBuilder.create(shortOpt);
   }

   private static String i18n(String key) {
      return _messages.format(key);
   }
}
