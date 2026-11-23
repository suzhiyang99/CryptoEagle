package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.openejb.NoSuchApplicationException;
import org.apache.openejb.UndeployException;
import org.apache.openejb.assembler.Deployer;
import org.apache.openejb.cli.SystemExitException;
import org.apache.openejb.util.JarExtractor;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.OpenEjbVersion;

public class Undeploy {
   private static final Messages messages = new Messages(Undeploy.class);
   private static final String defaultServerUrl = "ejbd://localhost:4201";

   public static void main(String[] args) throws SystemExitException {
      CommandLineParser parser = new PosixParser();
      Options options = new Options();
      options.addOption(option("v", "version", "cmd.deploy.opt.version"));
      options.addOption(option("h", "help", "cmd.undeploy.opt.help"));
      options.addOption(option("s", "server-url", "url", "cmd.deploy.opt.server"));
      CommandLine line = null;

      try {
         line = parser.parse(options, args);
      } catch (ParseException var18) {
         help(options);
         throw new SystemExitException(-1);
      }

      if (line.hasOption("help")) {
         help(options);
      } else if (line.hasOption("version")) {
         OpenEjbVersion.get().print(System.out);
      } else if (line.getArgList().size() == 0) {
         System.out.println("Must specify an module id.");
         help(options);
      } else {
         Properties p = new Properties();
         p.put("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
         String serverUrl = line.getOptionValue("server-url", "ejbd://localhost:4201");
         if ("auto".equalsIgnoreCase(serverUrl.trim())) {
            try {
               File sXml = new File(JavaSecurityManagers.getSystemProperty("openejb.base", "conf/server.xml"));
               if (sXml.exists()) {
                  QuickServerXmlParser result = QuickServerXmlParser.parse(sXml);
                  serverUrl = "http://" + result.host() + ":" + result.http() + "/tomee/ejb";
               }
            } catch (Throwable var17) {
            }
         }

         p.put("java.naming.provider.url", serverUrl);
         Deployer deployer = null;

         try {
            InitialContext ctx = new InitialContext(p);
            deployer = (Deployer)ctx.lookup("openejb/DeployerBusinessRemote");
         } catch (ServiceUnavailableException e) {
            System.out.println(e.getCause().getMessage());
            System.out.println(messages.format("cmd.deploy.serverOffline"));
            throw new SystemExitException(-1);
         } catch (NamingException var16) {
            System.out.println("DeployerEjb does not exist in server '" + serverUrl + "', check the server logs to ensure it exists and has not been removed.");
            throw new SystemExitException(-2);
         }

         int exitCode = 0;

         for(Object obj : line.getArgList()) {
            String moduleId = (String)obj;

            try {
               undeploy(moduleId, deployer);
            } catch (DeploymentTerminatedException e) {
               System.out.println(e.getMessage());
               ++exitCode;
            } catch (UndeployException e) {
               System.out.println(messages.format("cmd.undeploy.failed", moduleId));
               e.printStackTrace(System.out);
               ++exitCode;
            } catch (NoSuchApplicationException var14) {
               System.out.println(messages.format("cmd.undeploy.noSuchModule", moduleId));
               ++exitCode;
            }
         }

         if (exitCode != 0) {
            throw new SystemExitException(exitCode);
         }
      }
   }

   public static void undeploy(String moduleId, Deployer deployer) throws UndeployException, NoSuchApplicationException, DeploymentTerminatedException {
      undeploy(moduleId, new File(moduleId), deployer);
   }

   public static void undeploy(String moduleId, File file, Deployer deployer) throws UndeployException, NoSuchApplicationException, DeploymentTerminatedException {
      try {
         file = file.getCanonicalFile();
      } catch (IOException var7) {
      }

      boolean undeployed = false;
      if (file != null) {
         String path = file.getAbsolutePath();

         try {
            deployer.undeploy(path);
            undeployed = true;
            moduleId = path;
            if (!JarExtractor.delete(file)) {
               throw new DeploymentTerminatedException(messages.format("cmd.undeploy.cantDelete", file.getAbsolutePath()));
            }
         } catch (NoSuchApplicationException var6) {
         }
      }

      if (!undeployed) {
         deployer.undeploy(moduleId);
         System.out.println(messages.format("cmd.undeploy.nothingToDelete", moduleId));
      }

      System.out.println(messages.format("cmd.undeploy.successful", moduleId));
   }

   private static void help(Options options) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("undeploy [options] <file> [<file>...]", "\n" + i18n("cmd.undeploy.description"), options, "\n");
   }

   private static Option option(String shortOpt, String longOpt, String description) {
      OptionBuilder.withLongOpt(longOpt);
      OptionBuilder.withDescription(i18n(description));
      return OptionBuilder.create(shortOpt);
   }

   private static Option option(String shortOpt, String longOpt, String argName, String description) {
      OptionBuilder.withLongOpt(longOpt);
      OptionBuilder.withArgName(argName);
      OptionBuilder.hasArg();
      OptionBuilder.withDescription(i18n(description));
      return OptionBuilder.create(shortOpt);
   }

   private static String i18n(String key) {
      return messages.format(key);
   }

   public static class DeploymentTerminatedException extends Deploy.DeploymentTerminatedException {
      private static final long serialVersionUID = 1L;

      public DeploymentTerminatedException(String message) {
         super(message);
      }

      public DeploymentTerminatedException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
