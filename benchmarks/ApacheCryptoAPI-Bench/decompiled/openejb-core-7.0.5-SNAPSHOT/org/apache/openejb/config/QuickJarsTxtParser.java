package org.apache.openejb.config;

import java.io.File;
import org.apache.openejb.classloader.ClassLoaderConfigurer;
import org.apache.openejb.classloader.ProvisioningClassLoaderConfigurer;
import org.apache.openejb.loader.SystemInstance;

public final class QuickJarsTxtParser {
   private static final boolean ACTIVATED = SystemInstance.get().getOptions().get("openejb.jarstxt.activated", true);
   public static final String FILE_NAME = "jars.txt";

   private QuickJarsTxtParser() {
   }

   public static ClassLoaderConfigurer parse(File file) {
      if (ACTIVATED && file.exists()) {
         ProvisioningClassLoaderConfigurer configurer = new ProvisioningClassLoaderConfigurer();
         configurer.setConfiguration(file.getAbsolutePath());
         return configurer;
      } else {
         return null;
      }
   }
}
