package org.apache.openejb.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.openejb.EnvProps;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;

public class ConfUtils {
   public static URL getConfResource(String name) {
      URL resource = getResource(name);
      if (!EnvProps.extractConfigurationFiles()) {
         return resource;
      } else {
         try {
            File loginConfig = install(resource, name);
            if (loginConfig != null) {
               resource = loginConfig.toURI().toURL();
            }
         } catch (IOException var3) {
         }

         return resource;
      }
   }

   public static URL getResource(String name) {
      Enumeration<URL> resources = null;

      try {
         resources = Thread.currentThread().getContextClassLoader().getResources(name);
      } catch (IOException var3) {
         return Thread.currentThread().getContextClassLoader().getResource(name);
      }

      URL resource = select(resources);
      return resource;
   }

   private static URL select(Enumeration<URL> enumeration) {
      if (enumeration == null) {
         return null;
      } else {
         ArrayList<URL> urls = Collections.list(enumeration);
         if (urls.size() == 0) {
            return null;
         } else if (urls.size() == 1) {
            return (URL)urls.get(0);
         } else {
            Collections.sort(urls, new UrlComparator(SystemInstance.get().getBase().getDirectory()));
            return (URL)urls.get(0);
         }
      }
   }

   public static File install(String source, String name) throws IOException {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      URL resource = cl.getResource(source);
      return install(resource, name, false);
   }

   public static File install(URL resource, String name) throws IOException {
      return install(resource, name, false);
   }

   public static File install(URL resource, String name, boolean overwrite) throws IOException {
      if (resource == null) {
         return null;
      } else {
         SystemInstance system = SystemInstance.get();
         File conf = system.getConf((String)null);
         if (conf != null && !conf.exists()) {
            return null;
         } else {
            File file = new File(conf, name);
            if (file.exists() && !overwrite) {
               return file;
            } else {
               IO.copy(IO.read(resource), file);
               return file;
            }
         }
      }
   }
}
