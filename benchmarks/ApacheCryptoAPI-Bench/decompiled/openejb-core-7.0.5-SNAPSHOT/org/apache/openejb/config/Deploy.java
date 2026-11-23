package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarFile;
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
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.UndeployException;
import org.apache.openejb.assembler.Deployer;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.ClientInfo;
import org.apache.openejb.assembler.classic.ConnectorInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.EnterpriseBeanInfo;
import org.apache.openejb.assembler.classic.InterceptorInfo;
import org.apache.openejb.assembler.classic.PersistenceUnitInfo;
import org.apache.openejb.assembler.classic.WebAppInfo;
import org.apache.openejb.cli.SystemExitException;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JarExtractor;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.OpenEjbVersion;

public class Deploy {
   private static final Messages messages = new Messages(Deploy.class);
   private static final String defaultServerUrl = "ejbd://localhost:4201";

   public static void main(String... args) throws SystemExitException {
      CommandLineParser parser = new PosixParser();
      Options options = new Options();
      options.addOption(option("v", "version", "cmd.deploy.opt.version"));
      options.addOption(option("h", "help", "cmd.deploy.opt.help"));
      options.addOption(option("o", "offline", "cmd.deploy.opt.offline"));
      options.addOption(option("s", "server-url", "url", "cmd.deploy.opt.server"));
      options.addOption(option("d", "debug", "cmd.deploy.opt.debug"));
      options.addOption(option("q", "quiet", "cmd.deploy.opt.quiet"));
      options.addOption(option("u", "undeploy", "cmd.deploy.opt.undeploy"));
      options.addOption(option((String)null, "dir", "cmd.deploy.opt.dir"));

      CommandLine line;
      try {
         line = parser.parse(options, args);
      } catch (ParseException var24) {
         help(options);
         throw new SystemExitException(-1);
      }

      if (line.hasOption("help")) {
         help(options);
      } else if (line.hasOption("version")) {
         OpenEjbVersion.get().print(System.out);
      } else if (line.getArgList().size() == 0) {
         System.out.println("Must specify an archive to deploy.");
         help(options);
      } else {
         for(Object module : line.getArgList()) {
            String path = (String)module;
            File file = new File(path);

            try {
               checkSource(file);
            } catch (DeploymentTerminatedException e) {
               System.out.println(e.getMessage());
               throw new SystemExitException(-100);
            }
         }

         boolean offline = line.hasOption("offline");

         File apps;
         try {
            String dir = line.getOptionValue("dir", "apps");
            apps = SystemInstance.get().getBase().getDirectory(dir);
         } catch (IOException var22) {
            throw new SystemExitException(-1);
         }

         if (!apps.exists()) {
            System.out.println("Directory does not exist: " + apps.getAbsolutePath());
         }

         Deployer deployer = null;
         if (!offline) {
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
               } catch (Throwable var21) {
               }
            }

            p.put("java.naming.provider.url", serverUrl);

            try {
               InitialContext ctx = new InitialContext(p);
               deployer = (Deployer)ctx.lookup("openejb/DeployerBusinessRemote");
            } catch (ServiceUnavailableException e) {
               System.out.println(e.getCause().getMessage());
               System.out.println(messages.format("cmd.deploy.serverOffline"));
               throw new SystemExitException(-1);
            } catch (NamingException var20) {
               System.out.println("openejb/DeployerBusinessRemote does not exist in server '" + serverUrl + "', check the server logs to ensure it exists and has not been removed.");
               throw new SystemExitException(-2);
            }
         }

         boolean undeploy = line.hasOption("undeploy");
         int exitCode = 0;

         for(Object obj : line.getArgList()) {
            String path = (String)obj;
            File file = new File(path);
            File destFile = new File(apps, file.getName());

            try {
               if (shouldUnpack(file)) {
                  File unpacked = unpackedLocation(file, apps);
                  if (undeploy) {
                     undeploy(offline, unpacked, path, deployer);
                  }

                  destFile = unpack(file, unpacked);
               } else {
                  if (undeploy) {
                     undeploy(offline, destFile, path, deployer);
                  }

                  checkDest(destFile, file);
                  copyFile(file, destFile);
               }

               if (offline) {
                  System.out.println(messages.format("cmd.deploy.offline", path, apps.getAbsolutePath()));
               } else {
                  String location;
                  try {
                     location = destFile.getCanonicalPath();
                  } catch (IOException var18) {
                     throw new OpenEJBException(messages.format("cmd.deploy.fileNotFound", path));
                  }

                  AppInfo appInfo = deployer.deploy(location);
                  System.out.println(messages.format("cmd.deploy.successful", path, appInfo.path));
                  if (!line.hasOption("quiet")) {
                     print(appInfo);
                  }
               }
            } catch (UndeployException e) {
               System.out.println(messages.format("cmd.undeploy.failed", path));
               e.printStackTrace(System.out);
               ++exitCode;
            } catch (DeploymentTerminatedException e) {
               System.out.println(e.getMessage());
               ++exitCode;
            } catch (ValidationFailedException e) {
               System.out.println(messages.format("cmd.deploy.validationFailed", path));
               int level = 2;
               if (line.hasOption("debug")) {
                  level = 3;
               }

               AppValidator appValidator = new AppValidator(level, false, true, false);
               appValidator.printResults(e);
               ++exitCode;
               if (!JarExtractor.delete(destFile)) {
                  System.out.println(messages.format("cmd.deploy.cantDelete.deploy", destFile.getAbsolutePath()));
               }
            } catch (Throwable e) {
               System.out.println(messages.format("cmd.deploy.failed", path));
               e.printStackTrace(System.out);
               ++exitCode;
               if (!JarExtractor.delete(destFile)) {
                  System.out.println(messages.format("cmd.deploy.cantDelete.deploy", destFile.getAbsolutePath()));
               }
            }
         }

         if (exitCode != 0) {
            throw new SystemExitException(exitCode);
         }
      }
   }

   private static void undeploy(boolean offline, File dest, String path, Deployer deployer) throws UndeployException, DeploymentTerminatedException {
      if (offline) {
         if (dest.exists() && !JarExtractor.delete(dest)) {
            throw new DeploymentTerminatedException(messages.format("cmd.deploy.cantDelete.undeploy", dest.getAbsolutePath()));
         }
      } else {
         try {
            Undeploy.undeploy(path, dest, deployer);
         } catch (NoSuchApplicationException var5) {
         }
      }

   }

   private static void print(AppInfo appInfo) {
      System.out.println("App(id=" + appInfo.path + ")");

      for(EjbJarInfo info : appInfo.ejbJars) {
         System.out.println("    EjbJar(id=" + info.moduleName + ", path=" + info.path + ")");

         for(EnterpriseBeanInfo beanInfo : info.enterpriseBeans) {
            System.out.println("        Ejb(ejb-name=" + beanInfo.ejbName + ", id=" + beanInfo.ejbDeploymentId + ")");

            for(String name : beanInfo.jndiNames) {
               System.out.println("            Jndi(name=" + name + ")");
            }

            System.out.println("");
         }

         for(InterceptorInfo interceptorInfo : info.interceptors) {
            System.out.println("        Interceptor(class=" + interceptorInfo.clazz + ")");
         }

         System.out.println("");
      }

      for(ClientInfo clientInfo : appInfo.clients) {
         System.out.println("    Client(main-class=" + clientInfo.mainClass + ", id=" + clientInfo.moduleId + ", path=" + clientInfo.path + ")");
         System.out.println("");
      }

      for(ConnectorInfo connectorInfo : appInfo.connectors) {
         System.out.println("    Connector(id=" + connectorInfo.moduleId + ", path=" + connectorInfo.path + ")");
         System.out.println("");
      }

      for(WebAppInfo webAppInfo : appInfo.webApps) {
         System.out.println("    WebApp(context-root=" + webAppInfo.contextRoot + ", id=" + webAppInfo.moduleId + ", path=" + webAppInfo.path + ")");
         System.out.println("");
      }

      for(PersistenceUnitInfo persistenceUnitInfo : appInfo.persistenceUnits) {
         System.out.println("    PersistenceUnit(name=" + persistenceUnitInfo.name + ", provider=" + persistenceUnitInfo.provider + ")");
         System.out.println("");
      }

   }

   private static void checkSource(File file) throws DeploymentTerminatedException {
      if (!file.exists()) {
         throw new DeploymentTerminatedException(messages.format("cmd.deploy.fileNotFound", file.getAbsolutePath()));
      }
   }

   private static void checkDest(File destFile, File file) throws DeploymentTerminatedException {
      if (destFile.exists()) {
         throw new DeploymentTerminatedException(messages.format("cmd.deploy.destExists", file.getAbsolutePath(), destFile.getAbsolutePath()));
      }
   }

   private static void copyFile(File file, File destFile) throws DeploymentTerminatedException {
      try {
         IO.copy(file, destFile);
      } catch (Exception var3) {
         throw new DeploymentTerminatedException(messages.format("cmd.deploy.cantCopy", file.getAbsolutePath(), destFile.getAbsolutePath()));
      }
   }

   private static boolean shouldUnpack(File file) {
      String name = file.getName();
      if (!name.endsWith(".ear") && !name.endsWith(".rar") && !name.endsWith(".rar")) {
         JarFile jarFile = null;

         boolean var3;
         try {
            jarFile = new JarFile(file);
            if (jarFile.getEntry("META-INF/application.xml") == null) {
               if (jarFile.getEntry("META-INF/ra.xml") != null) {
                  var3 = true;
                  return var3;
               }

               if (jarFile.getEntry("WEB-INF/web.xml") == null) {
                  return false;
               }

               var3 = true;
               return var3;
            }

            var3 = true;
         } catch (IOException var16) {
            return false;
         } finally {
            if (jarFile != null) {
               try {
                  jarFile.close();
               } catch (IOException var15) {
               }
            }

         }

         return var3;
      } else {
         return true;
      }
   }

   private static File unpack(File jarFile, File destinationDir) throws OpenEJBException, DeploymentTerminatedException {
      try {
         checkDest(destinationDir, jarFile);
         JarExtractor.extract(jarFile, destinationDir);
         return destinationDir;
      } catch (IOException e) {
         throw new OpenEJBException("Unable to extract jar. " + e.getMessage(), e);
      }
   }

   private static File unpackedLocation(File jarFile, File destDir) {
      if (jarFile.isDirectory()) {
         return jarFile;
      } else {
         String name = jarFile.getName();
         if (!name.endsWith(".jar") && !name.endsWith(".ear") && !name.endsWith(".zip") && !name.endsWith(".war") && !name.endsWith(".rar")) {
            name = name + ".unpacked";
         } else {
            name = name.replaceFirst("....$", "");
         }

         File destinationDir = new File(destDir, name);
         return destinationDir;
      }
   }

   private static void help(Options options) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("deploy [options] <file> [<file>...]", "\n" + i18n("cmd.deploy.description"), options, "\n");
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

   public static class DeploymentTerminatedException extends Exception {
      public DeploymentTerminatedException(String message) {
         super(message);
      }

      public DeploymentTerminatedException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
