package org.apache.openejb.cli;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.StringTokenizer;
import org.apache.openejb.loader.ClassPath;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemClassPath;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.PropertyPlaceHolderHelper;
import org.apache.openejb.util.URLs;

public class Bootstrap {
   private static final String OPENEJB_VERSION_PROPERTIES_FILE_NAME = "openejb-version.properties";
   private static final String OPENEJB_HOME_PROPERTY_NAME = "openejb.home";
   private static final String OPENEJB_BASE_PROPERTY_NAME = "openejb.base";
   private static final String OPENEJB_CLI_MAIN_CLASS_NAME = "org.apache.openejb.cli.MainImpl";

   private static void setupHome(String[] args) {
      for(String arg : args) {
         if (arg.startsWith("-Dopenejb.home")) {
            addProperty(arg);
         } else if (arg.startsWith("-Dopenejb.base")) {
            addProperty(arg);
         }
      }

      String homeProperty = JavaSecurityManagers.getSystemProperty("openejb.home");
      if (homeProperty == null || !(new File(homeProperty)).exists()) {
         try {
            URL classURL = Thread.currentThread().getContextClassLoader().getResource("openejb-version.properties");
            if (classURL != null) {
               String propsString = classURL.getFile();
               propsString = propsString.substring(0, propsString.indexOf("!"));
               URI uri = URLs.uri(propsString);
               File jarFile = new File(uri.getSchemeSpecificPart());
               if (jarFile.getName().contains("openejb-core")) {
                  File lib = jarFile.getParentFile();
                  File home = lib.getParentFile().getCanonicalFile();
                  JavaSecurityManagers.setSystemProperty("openejb.home", home.getAbsolutePath());
               }
            }
         } catch (Exception e) {
            System.err.println("Error setting openejb.home property: " + e.getClass() + ": " + e.getMessage());
         }

      }
   }

   private static void addProperty(String arg) {
      String prop = arg.substring(arg.indexOf("-D") + 2, arg.indexOf("="));
      String val = arg.substring(arg.indexOf("=") + 1);
      JavaSecurityManagers.setSystemProperty(prop, val);
   }

   private static ClassLoader setupClasspath() {
      String base = JavaSecurityManagers.getSystemProperty("openejb.base", "");
      String home = JavaSecurityManagers.getSystemProperty("catalina.home", JavaSecurityManagers.getSystemProperty("openejb.home", base));

      try {
         File lib = new File(home + File.separator + "lib");
         ClassPath systemCP = new SystemClassPath();
         systemCP.getClassLoader();
         File config = new File(base, "conf/catalina.properties");
         if (!config.isFile()) {
            config = new File(home, "conf/catalina.properties");
         }

         if (config.isFile()) {
            String val = IO.readProperties(config).getProperty("common.loader", lib.getAbsolutePath());
            val = PropertyPlaceHolderHelper.simpleValue(val.replace("${catalina.", "${openejb."));
            StringTokenizer tokenizer = new StringTokenizer(val, ",");

            while(tokenizer.hasMoreElements()) {
               String repository = tokenizer.nextToken().trim();
               if (!repository.isEmpty()) {
                  if (repository.startsWith("\"") && repository.endsWith("\"")) {
                     repository = repository.substring(1, repository.length() - 1);
                  }

                  if (repository.endsWith("*.jar")) {
                     File dir = new File(repository.substring(0, repository.length() - "*.jar".length()));
                     if (dir.isDirectory()) {
                        systemCP.addJarsToPath(dir);
                     }
                  } else if (repository.endsWith(".jar")) {
                     File file = new File(repository);
                     if (file.isFile()) {
                        systemCP.addJarToPath(file.toURI().toURL());
                     }
                  } else {
                     File dir = new File(repository);
                     if (dir.isDirectory()) {
                        systemCP.addJarToPath(dir.toURI().toURL());
                     }
                  }
               }
            }
         } else {
            systemCP.addJarsToPath(lib);
            systemCP.addJarToPath(lib.toURI().toURL());
         }

         return systemCP.getClassLoader();
      } catch (Exception e) {
         System.err.println("Error setting up the classpath: " + e.getClass() + ": " + e.getMessage());
         e.printStackTrace();
         return null;
      }
   }

   public static void main(String[] args) throws Exception {
      setupHome(args);
      ClassLoader loader = setupClasspath();
      if (loader != null) {
         Thread.currentThread().setContextClassLoader(loader);
         if (loader != ClassLoader.getSystemClassLoader()) {
            System.setProperty("openejb.classloader.first.disallow-system-loading", "true");
         }
      }

      Class<?> clazz = (loader == null ? Bootstrap.class.getClassLoader() : loader).loadClass("org.apache.openejb.cli.MainImpl");

      try {
         Object main = clazz.getConstructor().newInstance();
         main.getClass().getMethod("main", String[].class).invoke(main, args);
      } catch (InvocationTargetException e) {
         Throwable cause = e.getCause();
         if ("org.apache.openejb.cli.SystemExitException".equals(cause.getClass().getName())) {
            System.exit(((Number)Number.class.cast(cause.getClass().getMethod("getExitCode").invoke(cause))).intValue());
         }

         if (Exception.class.isInstance(cause)) {
            throw (Exception)Exception.class.cast(cause);
         } else if (Error.class.isInstance(cause)) {
            throw (Error)Error.class.cast(cause);
         } else {
            throw new IllegalStateException(cause);
         }
      }
   }
}
