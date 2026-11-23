package org.apache.openejb.classloader;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CompositeClassLoaderConfigurer implements ClassLoaderConfigurer {
   private final ClassLoaderConfigurer[] composites;
   private final URL[] urls;

   public CompositeClassLoaderConfigurer(ClassLoaderConfigurer... configurers) {
      this.composites = configurers;
      Set<URL> urlSet = new HashSet();

      for(ClassLoaderConfigurer configurer : configurers) {
         if (configurer != null) {
            urlSet.addAll(Arrays.asList(configurer.additionalURLs()));
         }
      }

      this.urls = (URL[])urlSet.toArray(new URL[urlSet.size()]);
   }

   public URL[] additionalURLs() {
      return this.urls;
   }

   public boolean accept(URL url) {
      for(ClassLoaderConfigurer configurer : this.composites) {
         if (configurer != null && !configurer.accept(url)) {
            return false;
         }
      }

      return true;
   }
}
