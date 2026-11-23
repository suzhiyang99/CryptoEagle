package org.apache.openejb.classloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.ProvisioningUtil;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PropertyPlaceHolderHelper;
import org.apache.openejb.util.URLs;
import org.apache.xbean.finder.filter.Filter;
import org.apache.xbean.finder.filter.Filters;

public class ProvisioningClassLoaderConfigurer implements ClassLoaderConfigurer {
   private URL[] added = new URL[0];
   private Filter excluded;

   public ProvisioningClassLoaderConfigurer() {
      this.excluded = FalseFilter.INSTANCE;
   }

   public URL[] additionalURLs() {
      return this.added;
   }

   public boolean accept(URL url) {
      try {
         File file = URLs.toFile(url);
         return !this.excluded.accept(file.getName());
      } catch (IllegalArgumentException var3) {
         return true;
      }
   }

   public void setConfiguration(String configFile) {
      Collection<URL> toAdd = new ArrayList();
      Collection<String> toExclude = new ArrayList();
      BufferedReader reader = null;

      try {
         reader = new BufferedReader(new FileReader(configFile));

         String line;
         while((line = reader.readLine()) != null) {
            line = PropertyPlaceHolderHelper.SUBSTITUTOR.replace(line.trim());
            if (!line.startsWith("#") && !line.isEmpty()) {
               if (line.startsWith("-")) {
                  toExclude.add(line);
               } else {
                  if (line.startsWith("+")) {
                     line = line.substring(1);
                  }

                  String location = line;
                  String algo = "MD5";
                  String hash = null;
                  boolean validJar = line.contains("|");
                  if (validJar) {
                     String[] segments = line.split("|");
                     location = segments[0];
                     if (segments.length >= 2) {
                        hash = segments[1];
                     }

                     if (segments.length >= 3) {
                        algo = segments[2].trim();
                     }
                  }

                  Set<URL> repos = toUrls(ProvisioningUtil.realLocation(location));
                  toAdd.addAll(repos);
                  if (validJar) {
                     String computedHash = Files.hash(repos, algo);
                     if (!computedHash.equals(hash)) {
                        throw new IllegalStateException("Hash of " + location + "(" + computedHash + ") doesn't match expected one (" + hash + ")");
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
         Logger.getInstance(LogCategory.OPENEJB, ProvisioningClassLoaderConfigurer.class).error("Can't read " + configFile, (Throwable)e);
      } finally {
         IO.close(reader);
      }

      this.added = (URL[])toAdd.toArray(new URL[toAdd.size()]);
      if (toExclude.size() > 0) {
         this.excluded = Filters.prefixes((String[])toExclude.toArray(new String[toExclude.size()]));
      }

   }

   private static Set<URL> toUrls(Set<String> strings) {
      Set<URL> urls = new HashSet();

      for(String s : strings) {
         try {
            urls.add((new File(s)).toURI().toURL());
         } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
         }
      }

      return urls;
   }
}
