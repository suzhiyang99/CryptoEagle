package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.openejb.EnvProps;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.sys.Deployments;
import org.apache.openejb.config.sys.JaxbOpenejb;
import org.apache.openejb.config.sys.Openejb;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.finder.ResourceFinder;

public class ConfigUtils {
   public static Logger logger;

   public static String searchForConfiguration(String rawPath) throws OpenEJBException {
      if (rawPath != null) {
         for(String path : deducePaths(rawPath)) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
               return file.getAbsolutePath();
            }

            try {
               file = SystemInstance.get().getBase().getFile(path);
               if (file != null && file.exists() && file.isFile()) {
                  return file.getAbsolutePath();
               }
            } catch (IOException var8) {
            }

            try {
               file = SystemInstance.get().getHome().getFile(path);
               if (file != null && file.exists() && file.isFile()) {
                  return file.getAbsolutePath();
               }
            } catch (IOException var7) {
            }

            try {
               new URL(path);
               return path;
            } catch (MalformedURLException var10) {
            }
         }

         logger.warning("Cannot find the configuration file [" + rawPath + "], Trying conf/openejb.xml instead.");
      }

      File file;
      try {
         file = SystemInstance.get().getConf("openejb.xml");
         if (file != null && file.exists() && file.isFile()) {
            return file.getAbsolutePath();
         }

         file = SystemInstance.get().getConf("openejb.conf");
         if (file != null && file.exists() && file.isFile()) {
            return file.getAbsolutePath();
         }

         file = SystemInstance.get().getConf("openejb.json");
         if (file != null && file.exists() && file.isFile()) {
            return file.getAbsolutePath();
         }

         if (EnvProps.extractConfigurationFiles()) {
            File confDir = SystemInstance.get().getConf((String)null);
            if (confDir != null && confDir.exists()) {
               File config = new File(confDir, "openejb.xml");
               logger.info("Cannot find the configuration file [conf/openejb.xml].  Creating one at " + config.getAbsolutePath());
               file = createConfig(config);
            } else {
               logger.info("Cannot find the configuration file [conf/openejb.xml].  Will attempt to create one for the beans deployed.");
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
         throw new OpenEJBException("Could not locate config file: ", e);
      }

      return file != null && file.exists() ? file.getAbsolutePath() : null;
   }

   public static String[] deducePaths(String path) {
      return path.endsWith(".xml") ? new String[]{path, path.substring(0, path.length() - "xml".length()) + "json"} : new String[]{path};
   }

   public static File createConfig(File config) throws IOException {
      ResourceFinder finder = new ResourceFinder("");
      URL defaultConfig = finder.find("default.openejb.conf");
      IO.copy(IO.read(defaultConfig), config);
      return config;
   }

   public static boolean addDeploymentEntryToConfig(String jarLocation, Openejb config) {
      File jar = new File(jarLocation);

      for(Deployments d : config.getDeployments()) {
         if (d.getFile() != null) {
            try {
               File target = SystemInstance.get().getBase().getFile(d.getFile(), false);
               if (jar.equals(target)) {
                  return false;
               }
            } catch (IOException var7) {
            }
         } else if (d.getDir() != null) {
            try {
               File target = SystemInstance.get().getBase().getFile(d.getDir(), false);
               File jarDir = jar.getAbsoluteFile().getParentFile();
               if (jarDir != null && jarDir.equals(target)) {
                  return false;
               }
            } catch (IOException var8) {
            }
         }
      }

      Deployments dep = JaxbOpenejb.createDeployments();
      dep.setFile(jarLocation);
      config.getDeployments().add(dep);
      return true;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }
}
